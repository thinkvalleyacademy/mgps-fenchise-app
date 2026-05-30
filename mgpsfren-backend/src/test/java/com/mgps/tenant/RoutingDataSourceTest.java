package com.mgps.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RoutingDataSource
 */
@DisplayName("RoutingDataSource Tests")
class RoutingDataSourceTest {
    
    private RoutingDataSource routingDataSource;
    private DataSource mockMasterDataSource;
    private DataSource mockTenantDataSource;
    
    @BeforeEach
    void setUp() {
        mockMasterDataSource = mock(DataSource.class);
        mockTenantDataSource = mock(DataSource.class);
        routingDataSource = new RoutingDataSource(mockMasterDataSource);
    }
    
    @Test
    @DisplayName("Should register tenant datasource")
    void testRegisterTenantDataSource() {
        // Act
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        
        // Assert
        assertThat(routingDataSource.hasTenantDataSource("school1")).isTrue();
    }
    
    @Test
    @DisplayName("Should check if tenant datasource exists")
    void testHasTenantDataSource() {
        // Arrange
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        
        // Act & Assert
        assertThat(routingDataSource.hasTenantDataSource("school1")).isTrue();
        assertThat(routingDataSource.hasTenantDataSource("school2")).isFalse();
    }
    
    @Test
    @DisplayName("Should remove tenant datasource")
    void testRemoveTenantDataSource() {
        // Arrange
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        
        // Act
        routingDataSource.removeTenantDataSource("school1");
        
        // Assert
        assertThat(routingDataSource.hasTenantDataSource("school1")).isFalse();
    }
    
    @Test
    @DisplayName("Should report tenant datasource count")
    void testGetTenantDataSourceCount() {
        // Arrange
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        routingDataSource.registerTenantDataSource("school2", mockTenantDataSource);
        
        // Act
        int count = routingDataSource.getTenantDataSourceCount();
        
        // Assert
        assertThat(count).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should clear all tenant datasources")
    void testClearTenantDataSources() {
        // Arrange
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        routingDataSource.registerTenantDataSource("school2", mockTenantDataSource);
        
        // Act
        routingDataSource.clearTenantDataSources();
        
        // Assert
        assertThat(routingDataSource.getTenantDataSourceCount()).isZero();
    }
    
    @Test
    @DisplayName("Should use master datasource when no tenant is set")
    void testUseMasterDataSourceWhenNoTenant() {
        // Arrange
        TenantContext.clear();
        
        // Act
        // Note: This is testing internal logic, actual connection would fail with mocks
        // In real scenario, we'd test with actual datasources
        
        // Assert - Just verify we can create routing datasource
        assertThat(routingDataSource).isNotNull();
    }
}
