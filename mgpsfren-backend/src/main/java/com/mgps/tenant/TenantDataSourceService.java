package com.mgps.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing tenant datasources.
 * Handles loading tenant database information and creating datasources.
 */
@Service
public class TenantDataSourceService {

    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceService.class);
    
    @Autowired
    private DataSourceRegistry dataSourceRegistry;
    
    /**
     * Get datasource for a tenant.
     * Creates datasource if it doesn't exist (lazy loading).
     * 
     * @param tenantId tenant identifier
     * @param databaseName database name for the tenant
     * @return configured datasource
     */
    public javax.sql.DataSource getTenantDataSource(String tenantId, String databaseName) {
        if (tenantId == null || databaseName == null) {
            throw new IllegalArgumentException("Tenant ID and database name cannot be null");
        }
        
        return dataSourceRegistry.getOrCreateDataSource(tenantId, databaseName);
    }
    
    /**
     * Register a tenant datasource in the routing datasource
     * 
     * @param routingDataSource the routing datasource to register in
     * @param tenantId tenant identifier
     * @param databaseName database name for the tenant
     */
    public void registerTenantDataSource(RoutingDataSource routingDataSource, 
                                         String tenantId, 
                                         String databaseName) {
        javax.sql.DataSource dataSource = getTenantDataSource(tenantId, databaseName);
        routingDataSource.registerTenantDataSource(tenantId, dataSource);
        log.info("Registered tenant datasource: tenant={}, database={}", tenantId, databaseName);
    }
    
    /**
     * Check if tenant datasource exists
     * @param tenantId tenant identifier
     * @return true if datasource is cached
     */
    public boolean hasTenantDataSource(String tenantId) {
        return dataSourceRegistry.containsDataSource(tenantId);
    }
}
