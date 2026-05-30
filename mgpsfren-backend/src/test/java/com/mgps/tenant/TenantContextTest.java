package com.mgps.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TenantContext
 */
@DisplayName("TenantContext Tests")
class TenantContextTest {
    
    @BeforeEach
    void setUp() {
        TenantContext.clear();
    }
    
    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }
    
    @Test
    @DisplayName("Should set and get tenant ID")
    void testSetAndGetTenant() {
        // Arrange
        String tenantId = "school1";
        
        // Act
        TenantContext.setTenant(tenantId);
        String retrieved = TenantContext.getTenant();
        
        // Assert
        assertThat(retrieved).isEqualTo(tenantId);
    }
    
    @Test
    @DisplayName("Should return null when tenant is not set")
    void testGetTenantWhenNotSet() {
        // Act
        String tenant = TenantContext.getTenant();
        
        // Assert
        assertThat(tenant).isNull();
    }
    
    @Test
    @DisplayName("Should clear tenant context")
    void testClearTenant() {
        // Arrange
        TenantContext.setTenant("school1");
        
        // Act
        TenantContext.clear();
        String tenant = TenantContext.getTenant();
        
        // Assert
        assertThat(tenant).isNull();
    }
    
    @Test
    @DisplayName("Should handle blank tenant ID")
    void testBlankTenantId() {
        // Act
        TenantContext.setTenant("");
        String tenant = TenantContext.getTenant();
        
        // Assert
        assertThat(tenant).isNull();
    }
    
    @Test
    @DisplayName("Should handle null tenant ID")
    void testNullTenantId() {
        // Act
        TenantContext.setTenant(null);
        String tenant = TenantContext.getTenant();
        
        // Assert
        assertThat(tenant).isNull();
    }
    
    @Test
    @DisplayName("Should check if tenant is set")
    void testIsSet() {
        // Arrange
        assertThat(TenantContext.isSet()).isFalse();
        
        // Act
        TenantContext.setTenant("school1");
        
        // Assert
        assertThat(TenantContext.isSet()).isTrue();
    }
    
    @Test
    @DisplayName("Should throw exception when getting tenant without setting")
    void testGetTenantOrThrow() {
        // Act & Assert
        assertThatThrownBy(TenantContext::getTenantOrThrow)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Tenant context is not set");
    }
    
    @Test
    @DisplayName("Should return tenant ID when getTenantOrThrow is called with set tenant")
    void testGetTenantOrThrowWithSetTenant() {
        // Arrange
        String tenantId = "school1";
        TenantContext.setTenant(tenantId);
        
        // Act
        String retrieved = TenantContext.getTenantOrThrow();
        
        // Assert
        assertThat(retrieved).isEqualTo(tenantId);
    }
    
    @Test
    @DisplayName("Should be thread-safe (ThreadLocal isolation)")
    void testThreadSafety() throws InterruptedException {
        // Arrange
        String[] result1 = new String[1];
        String[] result2 = new String[1];
        
        // Act & Assert
        Thread thread1 = new Thread(() -> {
            TenantContext.setTenant("school1");
            try { Thread.sleep(100); } catch (Exception e) {}
            result1[0] = TenantContext.getTenant();
        });
        
        Thread thread2 = new Thread(() -> {
            try { Thread.sleep(50); } catch (Exception e) {}
            TenantContext.setTenant("school2");
            result2[0] = TenantContext.getTenant();
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        
        assertThat(result1[0]).isEqualTo("school1");
        assertThat(result2[0]).isEqualTo("school2");
    }
}
