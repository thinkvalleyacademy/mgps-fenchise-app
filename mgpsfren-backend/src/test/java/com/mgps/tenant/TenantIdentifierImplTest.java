package com.mgps.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TenantIdentifierImpl
 */
@DisplayName("TenantIdentifier Tests")
class TenantIdentifierImplTest {
    
    private TenantIdentifierImpl tenantIdentifier;
    
    @BeforeEach
    void setUp() {
        tenantIdentifier = new TenantIdentifierImpl();
    }
    
    @Test
    @DisplayName("Should resolve tenant from X-Tenant-Id header")
    void testResolveTenantFromHeader() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-Id", "school1");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isEqualTo("school1");
    }
    
    @Test
    @DisplayName("Should resolve tenant from subdomain")
    void testResolveTenantFromSubdomain() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("school1.smsapp.com");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isEqualTo("school1");
    }
    
    @Test
    @DisplayName("Should handle multiple subdomain levels")
    void testResolveFromMultipleLevelSubdomain() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("school1.smsapp.co.uk");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isEqualTo("school1");
    }
    
    @Test
    @DisplayName("Should ignore localhost")
    void testIgnoreLocalhost() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isNull();
    }
    
    @Test
    @DisplayName("Should ignore IP addresses")
    void testIgnoreIpAddress() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("192.168.1.1");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isNull();
    }
    
    @Test
    @DisplayName("Should prioritize header over subdomain")
    void testHeaderPriority() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-Id", "header_school");
        request.setServerName("subdomain.smsapp.com");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isEqualTo("header_school");
    }
    
    @Test
    @DisplayName("Should convert tenant ID to lowercase")
    void testTenantIdLowercase() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-Id", "SCHOOL1");
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isEqualTo("school1");
    }
    
    @Test
    @DisplayName("Should return null when tenant cannot be resolved")
    void testReturnNullWhenCannotResolve() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        // Act
        String tenantId = tenantIdentifier.resolveTenant(request);
        
        // Assert
        assertThat(tenantId).isNull();
    }
}
