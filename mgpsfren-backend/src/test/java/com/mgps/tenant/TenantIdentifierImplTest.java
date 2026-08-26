package com.mgps.tenant;

import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TenantIdentifierImpl
 */
@DisplayName("TenantIdentifier Tests")
class TenantIdentifierImplTest {

    private static final String SECRET = "test-secret-key-for-jwt-generation-1234567890-abcde";

    private JwtService jwtService;
    private TenantIdentifierImpl tenantIdentifier;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3600000L, 7200000L);
        tenantIdentifier = new TenantIdentifierImpl(jwtService);
    }

    private AppUser tenantUser(UUID schoolId) {
        return AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .schoolId(schoolId)
            .role(UserRole.PRINCIPAL)
            .status(UserStatus.ACTIVE)
            .build();
    }

    private AppUser superAdmin() {
        return AppUser.builder()
            .id(UUID.randomUUID())
            .email("superadmin@example.com")
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
    }

    // --- Anonymous requests -------------------------------------------------

    @Test
    @DisplayName("Should resolve tenant from X-Tenant-Id header on allow-listed anonymous endpoints")
    void testResolveTenantFromHeaderOnAnonymousEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.addHeader("X-Tenant-Id", "school1");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo("school1");
    }

    @Test
    @DisplayName("Should resolve tenant from subdomain on allow-listed anonymous endpoints")
    void testResolveTenantFromSubdomain() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.setServerName("school1.smsapp.com");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo("school1");
    }

    @Test
    @DisplayName("Should ignore the header on anonymous endpoints that are not allow-listed")
    void testAnonymousHeaderIgnoredOnOtherEndpoints() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("X-Tenant-Id", "victim_school");
        request.setServerName("attacker.smsapp.com");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should ignore localhost")
    void testIgnoreLocalhost() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.setServerName("localhost");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should ignore IP addresses")
    void testIgnoreIpAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.setServerName("192.168.1.1");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should prioritize header over subdomain")
    void testHeaderPriority() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.addHeader("X-Tenant-Id", "header_school");
        request.setServerName("subdomain.smsapp.com");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo("header_school");
    }

    @Test
    @DisplayName("Should convert tenant ID to lowercase")
    void testTenantIdLowercase() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/enquiries");
        request.addHeader("X-Tenant-Id", "SCHOOL1");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo("school1");
    }

    @Test
    @DisplayName("Should return null when tenant cannot be resolved")
    void testReturnNullWhenCannotResolve() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    // --- Authenticated requests ---------------------------------------------

    @Test
    @DisplayName("Should resolve tenant from JWT schoolId claim")
    void testResolveTenantFromJwtSchoolIdClaim() {
        UUID schoolId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer " + jwtService.generateAccessToken(tenantUser(schoolId)));

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo(schoolId.toString());
    }

    @Test
    @DisplayName("Should ignore X-Tenant-Id for a tenant-scoped token")
    void testTokenWinsOverHeaderForTenantUser() {
        UUID schoolId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer " + jwtService.generateAccessToken(tenantUser(schoolId)));
        request.addHeader("X-Tenant-Id", "22222222-2222-2222-2222-222222222222");
        request.setServerName("victim.smsapp.com");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo(schoolId.toString());
    }

    @Test
    @DisplayName("Should keep superadmin requests on the master datasource")
    void testSuperAdminJwtUsesMasterDatasource() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization",
            "Bearer " + jwtService.generateAccessToken(superAdmin(), "thinkvalley_academy_fren"));

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should let a superadmin target a tenant explicitly via header")
    void testSuperAdminMayTargetTenantViaHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer " + jwtService.generateAccessToken(superAdmin()));
        request.addHeader("X-Tenant-Id", "school1");

        assertThat(tenantIdentifier.resolveTenant(request)).isEqualTo("school1");
    }

    @Test
    @DisplayName("Should not fall back to header when the bearer token is invalid")
    void testInvalidTokenDoesNotFallBackToHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer not-a-real-token");
        request.addHeader("X-Tenant-Id", "victim_school");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should not accept a refresh token as tenant context")
    void testRefreshTokenIsNotAcceptedAsContext() {
        UUID schoolId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer "
            + jwtService.generateRefreshToken(tenantUser(schoolId), schoolId.toString()));

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should fail closed when a non-superadmin token carries no tenant")
    void testNonSuperAdminWithoutTenantFailsClosed() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/students");
        request.addHeader("Authorization", "Bearer " + jwtService.generateAccessToken(tenantUser(null)));

        assertThatThrownBy(() -> tenantIdentifier.resolveTenant(request))
            .isInstanceOf(TenantResolutionException.class);
    }

    // --- Control-plane endpoints --------------------------------------------

    @Test
    @DisplayName("Should leave login tenant resolution to the request school code")
    void testLoginDoesNotUseDeploymentSubdomain() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        request.setServerName("mgpsfren.thinkvalleysoftwares.in");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should keep token refresh on the master datasource")
    void testRefreshEndpointStaysOnMaster() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/refresh");
        request.addHeader("X-Tenant-Id", "mgpsfren");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should keep superadmin setup on the master datasource")
    void testSuperAdminSetupDoesNotUseDeploymentSubdomain() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/setup/superadmin");
        request.setServerName("mgpsfren.thinkvalleysoftwares.in");
        request.addHeader("X-Tenant-Id", "mgpsfren");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should keep school management endpoints on the master datasource")
    void testSchoolManagementDoesNotUseDeploymentSubdomainOrHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/schools");
        request.setServerName("mgpsfren.thinkvalleysoftwares.in");
        request.addHeader("X-Tenant-Id", "mgpsfren");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }

    @Test
    @DisplayName("Should keep subscription plan endpoints on the master datasource")
    void testSubscriptionPlansDoNotUseDeploymentSubdomainOrHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/subscription-plans");
        request.setServerName("mgpsfren.thinkvalleysoftwares.in");
        request.addHeader("X-Tenant-Id", "mgpsfren");

        assertThat(tenantIdentifier.resolveTenant(request)).isNull();
    }
}
