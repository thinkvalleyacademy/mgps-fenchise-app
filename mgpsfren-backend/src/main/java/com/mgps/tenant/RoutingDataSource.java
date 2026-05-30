package com.mgps.tenant;

import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routing DataSource that routes connections to the correct database
 * based on the current tenant context.
 * 
 * Routes to:
 * - Master datasource if no tenant is set
 * - Tenant-specific datasource if tenant context is set
 */
public class RoutingDataSource extends AbstractDataSource {

    private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);
    
    private DataSource masterDataSource;
    private final Map<String, DataSource> tenantDataSources = new HashMap<>();
    
    public RoutingDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
    }
    
    /**
     * Get connection from appropriate datasource based on tenant context
     */
    @Override
    public Connection getConnection() throws SQLException {
        DataSource dataSource = determineDataSource();
        return dataSource.getConnection();
    }
    
    /**
     * Get connection with username and password
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        DataSource dataSource = determineDataSource();
        return dataSource.getConnection(username, password);
    }
    
    /**
     * Determine which datasource to use based on tenant context
     */
    private DataSource determineDataSource() {
        String tenantId = TenantContext.getTenant();
        
        if (tenantId == null || tenantId.isBlank()) {
            log.debug("No tenant context set, using master datasource");
            return masterDataSource;
        }
        
        // Try to get tenant-specific datasource
        DataSource tenantDataSource = tenantDataSources.get(tenantId);
        if (tenantDataSource != null) {
            log.debug("Using tenant datasource for: {}", tenantId);
            return tenantDataSource;
        }
        
        // If tenant datasource not found, log warning and use master
        log.warn("Tenant datasource not found for tenant: {}, using master", tenantId);
        return masterDataSource;
    }
    
    /**
     * Register a datasource for a tenant
     * @param tenantId tenant identifier
     * @param dataSource the datasource for this tenant
     */
    public void registerTenantDataSource(String tenantId, DataSource dataSource) {
        if (tenantId != null && dataSource != null) {
            log.info("Registering datasource for tenant: {}", tenantId);
            tenantDataSources.put(tenantId, dataSource);
        }
    }
    
    /**
     * Remove a tenant datasource
     * @param tenantId tenant identifier
     */
    public void removeTenantDataSource(String tenantId) {
        if (tenantId != null) {
            log.info("Removing datasource for tenant: {}", tenantId);
            tenantDataSources.remove(tenantId);
        }
    }
    
    /**
     * Check if a tenant datasource is registered
     * @param tenantId tenant identifier
     * @return true if datasource exists for tenant
     */
    public boolean hasTenantDataSource(String tenantId) {
        return tenantDataSources.containsKey(tenantId);
    }
    
    /**
     * Get the number of registered tenant datasources
     */
    public int getTenantDataSourceCount() {
        return tenantDataSources.size();
    }
    
    /**
     * Clear all tenant datasources (keep master)
     */
    public void clearTenantDataSources() {
        log.info("Clearing all tenant datasources");
        tenantDataSources.clear();
    }
}
