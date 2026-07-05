package com.mgps.user.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.user.dto.UserDtos.LoginRequest;
import com.mgps.user.dto.UserDtos.LogoutRequest;
import com.mgps.user.dto.UserDtos.RegisterUserRequest;
import com.mgps.user.dto.UserDtos.RefreshRequest;
import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.repository.AppUserRepository;
import com.mgps.audit.ActivityLogService;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.tenant.TenantExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private TokenRevocationService tokenRevocationService;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private TenantExecutionService tenantExecutionService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = new JwtService(
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
            86400000L,
            604800000L
        );
        tokenRevocationService = new TokenRevocationService();
        userService = new UserService(appUserRepository, passwordEncoder, jwtService, tokenRevocationService,
            schoolRepository, tenantExecutionService, activityLogService);

        lenient().when(tenantExecutionService.inMaster(any(Supplier.class)))
            .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get());
        lenient().when(tenantExecutionService.inTenant(any(School.class), any(Supplier.class)))
            .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(1)).get());
        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(tenantExecutionService).inMaster(any(Runnable.class));
        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(tenantExecutionService).inTenant(any(School.class), any(Runnable.class));
    }

    @Test
    void shouldRegisterUser() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setRole(UserRole.SUPER_ADMIN);

        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.registerUser(request);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getProfile().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getProfile().getRole()).isEqualTo(UserRole.SUPER_ADMIN);
    }

    @Test
    void shouldRejectDuplicateUser() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@example.com");

        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldCheckSuperAdminInMasterDatabase() {
        when(appUserRepository.existsByRole(UserRole.SUPER_ADMIN)).thenReturn(true);

        assertThat(userService.hasSuperAdmin()).isTrue();

        verify(tenantExecutionService).inMaster(any(Supplier.class));
    }

    @Test
    void shouldRegisterSchoolUserInTenantDatabase() {
        UUID schoolId = UUID.randomUUID();
        SubscriptionPlan subscriptionPlan = SubscriptionPlan.builder()
            .id(UUID.randomUUID())
            .planName("STANDARD")
            .maxStudents(500)
            .maxStaff(50)
            .maxUsers(75)
            .monthlyPrice(BigDecimal.valueOf(999))
            .build();
        School school = School.builder()
            .id(schoolId)
            .name("Tenant School")
            .databaseName("tenant_school")
            .subscriptionPlan(subscriptionPlan)
            .build();
        RegisterUserRequest request = new RegisterUserRequest();
        request.setSchoolId(schoolId);
        request.setFirstName("School");
        request.setLastName("Admin");
        request.setEmail("admin@tenant.test");
        request.setPassword("Password123!");
        request.setRole(UserRole.SCHOOL_ADMIN);

        when(schoolRepository.findByIdWithSubscriptionPlan(schoolId)).thenReturn(Optional.of(school));
        when(appUserRepository.findByEmail("admin@tenant.test")).thenReturn(Optional.empty());
        when(appUserRepository.save(any(AppUser.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.registerUser(request);

        assertThat(response.getProfile().getSchoolId()).isEqualTo(schoolId);
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository, times(2)).save(userCaptor.capture());
        assertThat(userCaptor.getAllValues())
            .extracting(AppUser::getId)
            .containsOnly(userCaptor.getAllValues().get(0).getId());
        assertThat(userCaptor.getAllValues())
            .extracting(AppUser::getPasswordHash)
            .containsOnly(userCaptor.getAllValues().get(0).getPasswordHash());
        ArgumentCaptor<Map<String, ?>> detailsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(activityLogService, times(2)).record(
            any(), any(), any(), any(), any(), any(), detailsCaptor.capture());
        assertThat(detailsCaptor.getAllValues())
            .allSatisfy(details -> assertThat(details.get("subscription")).isEqualTo("STANDARD"));
    }

    @Test
    void shouldLoginUser() {
        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .passwordHash(passwordEncoder.encode("Password123!"))
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("Password123!");

        when(appUserRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.login(request);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getProfile().getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    void shouldLoginPartnerAgainstTenantDatabaseUsingSchoolCode() {
        UUID schoolId = UUID.randomUUID();
        School school = School.builder()
            .id(schoolId)
            .name("Partner School")
            .databaseName("partner_school")
            .build();
        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .schoolId(schoolId)
            .email("partner@example.com")
            .passwordHash(passwordEncoder.encode("Password123!"))
            .role(UserRole.SCHOOL_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        LoginRequest request = new LoginRequest();
        request.setSchoolCode("partner_school");
        request.setEmail("partner@example.com");
        request.setPassword("Password123!");

        when(schoolRepository.findByDatabaseNameIgnoreCase("partner_school"))
            .thenReturn(Optional.of(school));
        when(appUserRepository.findByEmail("partner@example.com")).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.login(request);

        assertThat(response.getProfile().getSchoolId()).isEqualTo(schoolId);
        assertThat(jwtService.extractTenantId(response.getAccessToken())).isEqualTo(schoolId.toString());
        verify(tenantExecutionService).inTenant(any(School.class), any(Supplier.class));
    }

    @Test
    void shouldRefreshToken() {
        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .passwordHash(passwordEncoder.encode("Password123!"))
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken(refreshToken);

        when(appUserRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        var response = userService.refreshToken(request);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }

    @Test
    void shouldLogoutRevokeTokens() {
        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        LogoutRequest request = new LogoutRequest();
        request.setAccessToken(accessToken);
        request.setRefreshToken(refreshToken);

        userService.logout(request);

        assertThat(tokenRevocationService.isRevoked(jwtService.extractTokenId(accessToken))).isTrue();
        assertThat(tokenRevocationService.isRevoked(jwtService.extractTokenId(refreshToken))).isTrue();
    }

    @Test
    void shouldBulkImportUsersFromCsv() {
        String csv = """
            schoolId,firstName,lastName,email,phone,password,role
            ,Alice,Walker,alice@example.com,1111111111,Password123!,TEACHER
            ,Bob,Stone,bob@example.com,2222222222,Password123!,STAFF
            """;

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            csv.getBytes()
        );

        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(appUserRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = userService.bulkImportUsers(file, null);

        assertThat(result.getTotalRows()).isEqualTo(2);
        assertThat(result.getCreatedCount()).isEqualTo(2);
        assertThat(result.getFailedCount()).isEqualTo(0);
        assertThat(result.getRowResults()).hasSize(2);
        assertThat(result.getRowResults()).allMatch(row -> row.isSuccess());
    }

    @Test
    void shouldReportDuplicateUserDuringBulkImport() {
        String csv = """
            schoolId,firstName,lastName,email,phone,password,role
            ,Alice,Walker,alice@example.com,1111111111,Password123!,TEACHER
            """;

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            csv.getBytes()
        );

        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(true);

        var result = userService.bulkImportUsers(file, null);

        assertThat(result.getTotalRows()).isEqualTo(1);
        assertThat(result.getCreatedCount()).isEqualTo(0);
        assertThat(result.getFailedCount()).isEqualTo(1);
        assertThat(result.getRowResults().get(0).isSuccess()).isFalse();
        assertThat(result.getRowResults().get(0).getMessage()).contains("already exists");
    }

    @Test
    void shouldReportMalformedCsvRow() {
        String csv = """
            schoolId,firstName,lastName,email,phone,password,role
            Alice,Walker,alice@example.com,1111111111,Password123!
            """;

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            csv.getBytes()
        );

        var result = userService.bulkImportUsers(file, null);

        assertThat(result.getTotalRows()).isEqualTo(1);
        assertThat(result.getCreatedCount()).isEqualTo(0);
        assertThat(result.getFailedCount()).isEqualTo(1);
        assertThat(result.getRowResults().get(0).isSuccess()).isFalse();
        assertThat(result.getRowResults().get(0).getMessage()).contains("Expected at least 6 columns");
    }
}
