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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private TokenRevocationService tokenRevocationService;

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
        userService = new UserService(appUserRepository, passwordEncoder, jwtService, tokenRevocationService);
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
