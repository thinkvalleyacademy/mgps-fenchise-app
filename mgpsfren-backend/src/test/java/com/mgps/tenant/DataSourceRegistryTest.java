package com.mgps.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for DataSourceRegistry
 * Note: Tests that would require actual DB connections are excluded
 */
@DisplayName("DataSourceRegistry Tests")
class DataSourceRegistryTest {
    
    private DataSourceRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new DataSourceRegistry();
    }
    
    @Test
    @DisplayName("Should throw exception for null tenant ID")
    void testNullTenantIdThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> registry.getOrCreateDataSource(null, "school1_db"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant ID and database name cannot be null");
    }
    
    @Test
    @DisplayName("Should throw exception for null database name")
    void testNullDatabaseNameThrowsException() {
        // Act & Assert
        assertThatThrownBy(() -> registry.getOrCreateDataSource("school1", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tenant ID and database name cannot be null");
    }
    
    @Test
    @DisplayName("Should return null for non-existent datasource")
    void testGetNonExistentDataSource() {
        // Act
        DataSource ds = registry.getDataSource("school1");
        
        // Assert
        assertThat(ds).isNull();
    }
    
    @Test
    @DisplayName("Should report size as zero initially")
    void testSizeInitially() {
        // Act
        int size = registry.size();
        
        // Assert
        assertThat(size).isZero();
    }
    
    @Test
    @DisplayName("Should not contain non-existent datasource")
    void testContainsNonExistent() {
        // Act & Assert
        assertThat(registry.containsDataSource("school1")).isFalse();
    }
}
