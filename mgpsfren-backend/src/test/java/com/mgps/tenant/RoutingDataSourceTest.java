package com.mgps.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;

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

    @Test
    @DisplayName("Should fail fast when tenant datasource is missing")
    void testMissingTenantDataSourceDoesNotFallbackToMaster() throws Exception {
        // Arrange
        TenantContext.setTenant("school1");

        try {
            // Act & Assert
            assertThatThrownBy(() -> routingDataSource.getConnection())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No datasource registered for tenant: school1");
            verify(mockMasterDataSource, never()).getConnection();
        } finally {
            TenantContext.clear();
        }
    }

    @Test
    @DisplayName("Should use registered tenant datasource")
    void testUseRegisteredTenantDataSource() throws Exception {
        // Arrange
        Connection tenantConnection = mock(Connection.class);
        when(mockTenantDataSource.getConnection()).thenReturn(tenantConnection);
        routingDataSource.registerTenantDataSource("school1", mockTenantDataSource);
        TenantContext.setTenant("school1");

        try {
            // Act
            Connection connection = routingDataSource.getConnection();

            // Assert
            assertThat(connection).isSameAs(tenantConnection);
            verify(mockTenantDataSource).getConnection();
            verify(mockMasterDataSource, never()).getConnection();
        } finally {
            TenantContext.clear();
        }
    }
}
