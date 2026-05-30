package com.mgps.user.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.user.dto.UserDtos.AuthResponse;
import com.mgps.user.dto.UserDtos.BulkImportResult;
import com.mgps.user.dto.UserDtos.LoginRequest;
import com.mgps.user.dto.UserDtos.LogoutRequest;
import com.mgps.user.dto.UserDtos.RegisterUserRequest;
import com.mgps.user.dto.UserDtos.RefreshRequest;
import com.mgps.user.dto.UserDtos.RowResult;
import com.mgps.user.dto.UserDtos.UserProfile;
import com.mgps.user.dto.UserDtos.UserStatusRequest;
import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.repository.AppUserRepository;
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
import java.util.UUID;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       TokenRevocationService tokenRevocationService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
    }

    public AuthResponse registerUser(RegisterUserRequest request) {
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .schoolId(request.getSchoolId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail().toLowerCase().trim())
            .phone(request.getPhone())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .status(UserStatus.ACTIVE)
            .build();

        AppUser saved = appUserRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.getEmail().toLowerCase().trim())
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
        AppUser user = appUserRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    public void logout(LogoutRequest request) {
        if (request == null) {
            return;
        }

        revokeTokenIfPresent(request.getAccessToken());
        revokeTokenIfPresent(request.getRefreshToken());
    }

    public boolean hasSuperAdmin() {
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
        return appUserRepository.findBySchoolId(schoolId, pageable).map(this::toProfile);
    }

    public UserProfile updateStatus(UUID userId, UserStatusRequest request) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(request.getStatus());
        return toProfile(appUserRepository.save(user));
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
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtService.generateAccessToken(user));
        response.setRefreshToken(jwtService.generateRefreshToken(user));
        response.setExpiresIn(jwtService.getExpirationMs());
        response.setProfile(toProfile(user));
        return response;
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
