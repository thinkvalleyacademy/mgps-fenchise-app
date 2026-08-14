package com.mgps.user.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.audit.ActivityLogService;
import com.mgps.school.entity.School;
import com.mgps.tenant.TenantContext;
import com.mgps.user.dto.UserDtos.AuthResponse;
import com.mgps.user.dto.UserDtos.BulkImportResult;
import com.mgps.user.dto.UserDtos.LoginRequest;
import com.mgps.user.dto.UserDtos.LogoutRequest;
import com.mgps.user.dto.UserDtos.RegisterUserRequest;
import com.mgps.user.dto.UserDtos.RefreshRequest;
import com.mgps.user.dto.UserDtos.RowResult;
import com.mgps.user.dto.UserDtos.UserProfile;
import com.mgps.user.dto.UserDtos.UserStatusRequest;
import com.mgps.user.dto.UserDtos.UserUpdateRequest;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.tenant.TenantExecutionService;
import com.mgps.tenant.TenantGuard;
import com.mgps.tenant.TenantNamingUtil;
import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;
    private final SchoolRepository schoolRepository;
    private final TenantExecutionService tenantExecutionService;
    private final ActivityLogService activityLogService;
    private final TenantGuard tenantGuard;

    public UserService() {
        this(null, null, null, null, null, null, null, null);
    }

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       TokenRevocationService tokenRevocationService) {
        this(appUserRepository, passwordEncoder, jwtService, tokenRevocationService, null, null, null, null);
    }

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       TokenRevocationService tokenRevocationService, SchoolRepository schoolRepository,
                       TenantExecutionService tenantExecutionService,
                       ActivityLogService activityLogService,
                       TenantGuard tenantGuard) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
        this.schoolRepository = schoolRepository;
        this.tenantExecutionService = tenantExecutionService;
        this.activityLogService = activityLogService;
        // TenantGuard is stateless; never leave it null or the cross-tenant checks
        // below would silently become no-ops.
        this.tenantGuard = tenantGuard != null ? tenantGuard : new TenantGuard();
    }

    public AuthResponse registerUser(RegisterUserRequest request) {
        AppUser saved;
        log.info("registerUser request | schoolId={} email={} role={} passwordLength={}",
            request.getSchoolId(), request.getEmail(), request.getRole(),
            request.getPassword() == null ? 0 : request.getPassword().length());
        if (request.getSchoolId() == null) {
            saved = createUser(request);
        } else {
            // Without this, any principal holding USER_CREATE could provision a
            // user (of any role) inside another school's database.
            tenantGuard.assertSchoolAccessible(request.getSchoolId());
            saved = createUserInTenant(request);
        }

        return buildAuthResponse(saved);
    }

    private AppUser createUserInTenant(RegisterUserRequest request) {
        School school = tenantExecutionService.inMaster(() -> schoolRepository.findByIdWithSubscriptionPlan(request.getSchoolId()))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        String email = normalizeEmail(request.getEmail());
        UUID userId = UUID.randomUUID();
        String passwordHash = passwordEncoder.encode(request.getPassword());

        log.info("createUserInTenant start | schoolId={} schoolName={} database={} email={} currentTenant={}",
            school.getId(), school.getName(), school.getDatabaseName(), email, TenantContext.getTenant());

        AppUser savedMaster = null;
        AppUser savedTenant = null;

        // Duplicate checks must happen before any save to avoid partial insertions.
        boolean masterExists = tenantExecutionService.inMaster(() -> {
            log.info("createUserInTenant master-duplicate-check | email={} currentTenant={}", email, TenantContext.getTenant());
            var existing = appUserRepository.findByEmail(email);
            log.info("createUserInTenant master-duplicate-check result | email={} found={}", email, existing.isPresent());
            return existing.isPresent();
        });
        if (masterExists) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        boolean tenantExists = tenantExecutionService.inTenant(school, () -> {
            log.info("createUserInTenant tenant-duplicate-check | email={} currentTenant={}", email, TenantContext.getTenant());
            var existing = appUserRepository.findByEmail(email);
            log.info("createUserInTenant tenant-duplicate-check result | email={} found={}", email, existing.isPresent());
            return existing.isPresent();
        });
        if (tenantExists) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        try {
            savedMaster = tenantExecutionService.inMaster(() -> {
                AppUser masterUser = buildUser(request, userId, email, passwordHash);
                AppUser saved = appUserRepository.save(masterUser);
                log.info("createUserInTenant master-saved | userId={} email={} schoolId={}",
                    saved.getId(), saved.getEmail(), saved.getSchoolId());
                return saved;
            });

            savedTenant = tenantExecutionService.inTenant(school, () -> {
                AppUser tenantUser = buildUser(request, userId, email, passwordHash);
                log.info("createUserInTenant tenant-save-target | email={} expectedDatabase={}",
                    email, school.getDatabaseName());
                AppUser saved = appUserRepository.save(tenantUser);
                log.info("createUserInTenant tenant-saved | userId={} email={} schoolId={} expectedDatabase={}",
                    saved.getId(), saved.getEmail(), saved.getSchoolId(), school.getDatabaseName());
                return saved;
            });

            recordUserCreated(school, savedMaster, savedTenant);
            return savedTenant;
        } catch (RuntimeException ex) {
            if (savedTenant != null) {
                AppUser tenantUserToDelete = savedTenant;
                try {
                    tenantExecutionService.inTenant(school, () -> appUserRepository.deleteById(tenantUserToDelete.getId()));
                } catch (Exception cleanupEx) {
                    log.warn("Failed to rollback tenant user {} after registration failure", tenantUserToDelete.getId(), cleanupEx);
                }
            }
            if (savedMaster != null) {
                AppUser masterUserToDelete = savedMaster;
                try {
                    tenantExecutionService.inMaster(() -> appUserRepository.deleteById(masterUserToDelete.getId()));
                } catch (Exception cleanupEx) {
                    log.warn("Failed to rollback master user {} after registration failure", masterUserToDelete.getId(), cleanupEx);
                }
            }
            throw ex;
        }
    }

    private AppUser createUser(RegisterUserRequest request) {
        if (tenantExecutionService != null) {
            return tenantExecutionService.inMaster(() -> createUserInCurrentDataSource(request));
        }
        return createUserInCurrentDataSource(request);
    }

    private AppUser createUserInCurrentDataSource(RegisterUserRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (appUserRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        AppUser user = buildUser(
            request, UUID.randomUUID(), email, passwordEncoder.encode(request.getPassword()));
        return appUserRepository.save(user);
    }

    private AppUser buildUser(RegisterUserRequest request, UUID userId, String email, String passwordHash) {
        return AppUser.builder()
            .id(userId)
            .schoolId(request.getSchoolId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(email)
            .phone(request.getPhone())
            .passwordHash(passwordHash)
            .role(request.getRole())
            .status(UserStatus.ACTIVE)
            .build();
    }

    public AuthResponse login(LoginRequest request) {
        if (isPartnerLogin(request)) {
            School school = resolveSchool(request.getSchoolCode());
            return tenantExecutionService.inTenant(school, () -> loginAgainstCurrentDataSource(request));
        }
        return tenantExecutionService.inMaster(() -> loginAgainstCurrentDataSource(request));
    }

    private AuthResponse loginAgainstCurrentDataSource(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(normalizeEmail(request.getEmail()))
            .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResourceNotFoundException("User account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        appUserRepository.save(user);
        return buildAuthResponse(user);
    }

    private boolean isPartnerLogin(LoginRequest request) {
        return request.getSchoolCode() != null
            && !request.getSchoolCode().isBlank()
            && !TenantNamingUtil.CLIENT_TENANT_ID.equalsIgnoreCase(request.getSchoolCode().trim());
    }

    private School resolveSchool(String schoolCode) {
        String normalizedCode = schoolCode.trim();
        return tenantExecutionService.inMaster(() -> {
            try {
                return schoolRepository.findById(UUID.fromString(normalizedCode));
            } catch (IllegalArgumentException ignored) {
                return schoolRepository.findByDatabaseNameIgnoreCase(normalizedCode)
                    .or(() -> schoolRepository.findAll().stream()
                        .filter(school -> TenantNamingUtil.generateTenantId(
                            school.getName(), school.getCity(), school.getPostalCode())
                            .equalsIgnoreCase(normalizedCode))
                        .findFirst());
            }
        }).orElseThrow(() -> new ResourceNotFoundException("School code not found"));
    }

    public AuthResponse refreshToken(RefreshRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new ResourceNotFoundException("Refresh token is required");
        }

        String refreshToken = request.getRefreshToken().trim();
        if (!jwtService.isTokenValid(refreshToken) || !"refresh".equals(jwtService.extractTokenType(refreshToken))) {
            throw new ResourceNotFoundException("Invalid refresh token");
        }

        String tokenId = jwtService.extractTokenId(refreshToken);
        if (tokenRevocationService.isRevoked(tokenId)) {
            throw new ResourceNotFoundException("Refresh token has been revoked");
        }

        String email = jwtService.extractEmail(refreshToken);
        String tenantId = jwtService.extractTenantId(refreshToken);

        // The refresh token itself names the tenant it was issued for. Resolving
        // the user against the ambient request context instead would let a token
        // from school A be exchanged for a session in school B whenever the same
        // email exists in both databases.
        if (tenantId == null || tenantId.isBlank()) {
            return tenantExecutionService.inMaster(() -> buildAuthResponse(loadUserByEmail(email)));
        }

        School school = tenantExecutionService.inMaster(() -> schoolRepository.findById(UUID.fromString(tenantId)))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        return tenantExecutionService.inTenant(school, () -> buildAuthResponse(loadUserByEmail(email)));
    }

    private AppUser loadUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void logout(LogoutRequest request) {
        if (request == null) {
            return;
        }

        revokeTokenIfPresent(request.getAccessToken());
        revokeTokenIfPresent(request.getRefreshToken());
    }

    public boolean hasSuperAdmin() {
        if (tenantExecutionService != null) {
            return tenantExecutionService.inMaster(() -> appUserRepository.existsByRole(UserRole.SUPER_ADMIN));
        }
        return appUserRepository.existsByRole(UserRole.SUPER_ADMIN);
    }

    public UserProfile getCurrentProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return toProfile(appUserRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public UserProfile getUserProfile(UUID userId) {
        return toProfile(appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public Page<UserProfile> getAllUsers(Pageable pageable) {
        return appUserRepository.findAll(pageable).map(this::toProfile);
    }

    public Page<UserProfile> getUsersBySchool(UUID schoolId, Pageable pageable) {
        tenantGuard.assertSchoolAccessible(schoolId);
        School school = tenantExecutionService.inMaster(() -> schoolRepository.findById(schoolId))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        return tenantExecutionService.inTenant(
            school, () -> appUserRepository.findBySchoolId(schoolId, pageable).map(this::toProfile));
    }

    private void recordUserCreated(School school, AppUser masterUser, AppUser tenantUser) {
        String actorEmail = currentActorEmail();
        Map<String, Object> details = Map.of(
            "email", tenantUser.getEmail(),
            "role", tenantUser.getRole().name(),
            "schoolName", school.getName(),
            "subscription", school.getSubscriptionPlan() != null
                ? school.getSubscriptionPlan().getPlanName()
                : "NONE"
        );
        tenantExecutionService.inMaster(() -> activityLogService.record(
            school.getId(), null, actorEmail, "USER_CREATED", "APP_USER", masterUser.getId(), details));
        tenantExecutionService.inTenant(school, () -> activityLogService.record(
            school.getId(), null, actorEmail, "USER_CREATED", "APP_USER", tenantUser.getId(), details));
    }

    private String currentActorEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    public UserProfile updateStatus(UUID userId, UserStatusRequest request) {
        if (request.getSchoolId() == null) {
            return updateCurrentDataSourceStatus(userId, request);
        }

        tenantGuard.assertSchoolAccessible(request.getSchoolId());
        School school = tenantExecutionService.inMaster(() -> schoolRepository.findById(request.getSchoolId()))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        tenantExecutionService.inMaster(() -> updateCurrentDataSourceStatus(userId, request));
        return tenantExecutionService.inTenant(school, () -> updateCurrentDataSourceStatus(userId, request));
    }

    private UserProfile updateCurrentDataSourceStatus(UUID userId, UserStatusRequest request) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(request.getStatus());
        return toProfile(appUserRepository.save(user));
    }

    public UserProfile updateUser(UUID userId, UserUpdateRequest request) {
        if (request.getSchoolId() == null) {
            return updateCurrentDataSourceUser(userId, request);
        }

        tenantGuard.assertSchoolAccessible(request.getSchoolId());
        School school = tenantExecutionService.inMaster(() -> schoolRepository.findById(request.getSchoolId()))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        tenantExecutionService.inMaster(() -> updateCurrentDataSourceUser(userId, request));
        return tenantExecutionService.inTenant(school, () -> updateCurrentDataSourceUser(userId, request));
    }

    private UserProfile updateCurrentDataSourceUser(UUID userId, UserUpdateRequest request) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail().toLowerCase().trim());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(request.getRole());
        return toProfile(appUserRepository.save(user));
    }

    public void deleteUser(UUID userId) {
        deleteUser(userId, null);
    }

    public void deleteUser(UUID userId, UUID schoolId) {
        if (schoolId == null) {
            deleteCurrentDataSourceUser(userId);
            return;
        }

        tenantGuard.assertSchoolAccessible(schoolId);
        School school = tenantExecutionService.inMaster(() -> schoolRepository.findById(schoolId))
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        tenantExecutionService.inTenant(school, () -> deleteCurrentDataSourceUser(userId));
        tenantExecutionService.inMaster(() -> deleteCurrentDataSourceUser(userId));
    }

    private void deleteCurrentDataSourceUser(UUID userId) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        appUserRepository.deleteById(userId);
    }

    public BulkImportResult bulkImportUsers(MultipartFile file, UUID defaultSchoolId) {
        if (file == null || file.isEmpty()) {
            throw new ResourceNotFoundException("CSV file is required");
        }

        List<RowResult> rowResults = new ArrayList<>();
        int createdCount = 0;
        int totalRows = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new ResourceNotFoundException("CSV file is empty");
            }

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) {
                    continue;
                }

                totalRows++;
                RowResult rowResult = processCsvRow(line, rowNumber, defaultSchoolId);
                rowResults.add(rowResult);
                if (rowResult.isSuccess()) {
                    createdCount++;
                }
            }
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Failed to read CSV file");
        }

        BulkImportResult result = new BulkImportResult();
        result.setTotalRows(totalRows);
        result.setCreatedCount(createdCount);
        result.setFailedCount(totalRows - createdCount);
        result.setRowResults(rowResults);
        return result;
    }

    private AuthResponse buildAuthResponse(AppUser user) {
        String tenantId = resolveTenantId(user);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtService.generateAccessToken(user, tenantId));
        response.setRefreshToken(jwtService.generateRefreshToken(user, tenantId));
        response.setExpiresIn(jwtService.getExpirationMs());
        response.setProfile(toProfile(user));
        return response;
    }

    private String resolveTenantId(AppUser user) {
        if (user == null) {
            return null;
        }

        if (user.getRole() == UserRole.SUPER_ADMIN) {
            return null;
        }

        if (user.getSchoolId() == null) {
            return null;
        }

        return user.getSchoolId().toString();
    }

    private void revokeTokenIfPresent(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String tokenId = jwtService.extractTokenId(token.trim());
        if (tokenId != null) {
            java.time.Instant expiration = jwtService.extractExpiration(token.trim());
            tokenRevocationService.revoke(tokenId, expiration != null ? expiration : java.time.Instant.now());
        }
    }

    private RowResult processCsvRow(String line, int rowNumber, UUID defaultSchoolId) {
        RowResult rowResult = new RowResult();
        rowResult.setRowNumber(rowNumber);

        try {
            String[] columns = parseCsvLine(line);
            if (columns.length < 6) {
                rowResult.setSuccess(false);
                rowResult.setMessage("Expected at least 6 columns: schoolId,firstName,lastName,email,phone,password,role");
                return rowResult;
            }

            UUID schoolId = columns[0].isBlank()
                ? defaultSchoolId
                : UUID.fromString(columns[0].trim());
            // A CSV row must not be able to stamp a foreign school id onto a user.
            tenantGuard.assertSchoolAccessible(schoolId);
            String firstName = columns[1].trim();
            String lastName = columns[2].trim();
            String email = columns[3].trim().toLowerCase();
            String phone = columns[4].trim();
            String password = columns[5].trim();
            String roleValue = columns.length > 6 ? columns[6].trim() : "STAFF";

            if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
                rowResult.setSuccess(false);
                rowResult.setMessage("First name, last name, email, and password are required");
                rowResult.setEmail(email);
                return rowResult;
            }

            if (appUserRepository.existsByEmail(email)) {
                rowResult.setSuccess(false);
                rowResult.setMessage("User already exists");
                rowResult.setEmail(email);
                return rowResult;
            }

            AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .schoolId(schoolId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone.isBlank() ? null : phone)
                .passwordHash(passwordEncoder.encode(password))
                .role(com.mgps.user.entity.UserRole.valueOf(roleValue.toUpperCase()))
                .status(UserStatus.ACTIVE)
                .build();

            appUserRepository.save(user);
            rowResult.setSuccess(true);
            rowResult.setMessage("Imported successfully");
            rowResult.setEmail(email);
            return rowResult;
        } catch (Exception ex) {
            rowResult.setSuccess(false);
            rowResult.setMessage("Failed to import row: " + ex.getMessage());
            return rowResult;
        }
    }

    private String[] parseCsvLine(String line) {
        return line.split(",", -1);
    }

    private UserProfile toProfile(AppUser user) {
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setSchoolId(user.getSchoolId());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setPhone(user.getPhone());
        profile.setRole(user.getRole());
        profile.setStatus(user.getStatus());
        profile.setLastLoginAt(user.getLastLoginAt());
        profile.setCreatedAt(user.getCreatedAt());
        profile.setUpdatedAt(user.getUpdatedAt());
        return profile;
    }
}
