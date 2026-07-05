package com.mgps.tenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routing DataSource that routes connections to the correct database
 * based on the current tenant context.
 * 
 * Routes to:
 * - Master datasource if no tenant is set
 * - Master datasource for the fixed superadmin tenant (THINKVALLEY_ACADEMY_FREN)
 * - Tenant-specific datasource if tenant context is set
 * - Dynamically loads tenant datasource from master if not already registered
 */

public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);
    
    private final DataSource masterDataSource;
    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();
    private final JdbcTemplate masterJdbcTemplate;
    private DataSourceRegistry dataSourceRegistry;
    
    public RoutingDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
        this.masterJdbcTemplate = new JdbcTemplate(masterDataSource);
        Map<Object, Object> targets = new HashMap<>();
        targets.put("MASTER", masterDataSource);
        setDefaultTargetDataSource(masterDataSource);
        setTargetDataSources(targets);
        afterPropertiesSet();
    }
    
    public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }
    
    /**
     * Get connection from appropriate datasource based on tenant context
     */
    @Override
    public Connection getConnection() throws SQLException {
        DataSource dataSource = determineDataSource();
        Connection connection = dataSource.getConnection();
        logConnectionTarget(connection);
        return connection;
    }
    
    /**
     * Get connection with username and password
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        DataSource dataSource = determineDataSource();
        Connection connection = dataSource.getConnection(username, password);
        logConnectionTarget(connection);
        return connection;
    }

    private void logConnectionTarget(Connection connection) {
        try {
            if (connection.getMetaData() == null) {
                log.info("RoutingDataSource connected | tenantContext={} actualDatabase=UNKNOWN url=UNKNOWN",
                    TenantContext.getTenant() == null ? "MASTER" : TenantContext.getTenant());
                return;
            }
            log.info("RoutingDataSource connected | tenantContext={} actualDatabase={} url={}",
                TenantContext.getTenant() == null ? "MASTER" : TenantContext.getTenant(),
                connection.getCatalog(),
                connection.getMetaData().getURL());
        } catch (Exception ex) {
            log.warn("RoutingDataSource connected but database name could not be read | tenantContext={}",
                TenantContext.getTenant(), ex);
        }
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenant();
        return tenantId == null || tenantId.isBlank() ? "MASTER" : tenantId;
    }

    private DataSource determineDataSource() {
        String tenantId = TenantContext.getTenant();
        
        if (tenantId == null || tenantId.isBlank()) {
            log.info("RoutingDataSource choose datasource | tenantContext=MASTER using=MASTER");
            return masterDataSource;
        }

        // Fixed tenant for superadmin ALWAYS uses master
        if (TenantNamingUtil.CLIENT_TENANT_ID.equalsIgnoreCase(tenantId)) {
            log.info("RoutingDataSource choose datasource | tenantContext={} using=MASTER reason=superadmin", tenantId);
            return masterDataSource;
        }
        
        // Try to get tenant-specific datasource
        DataSource tenantDataSource = tenantDataSources.get(tenantId);
        if (tenantDataSource != null) {
            log.info("RoutingDataSource choose datasource | tenantContext={} using=CACHED_TENANT datasourceClass={}", tenantId, tenantDataSource.getClass().getName());
            return tenantDataSource;
        }
        
        // Attempt to lazy load from master database if datasource registry is available
        if (dataSourceRegistry != null) {
            DataSource loadedDs = tryLoadingTenantDataSource(tenantId);
            if (loadedDs != null) {
                log.info("RoutingDataSource choose datasource | tenantContext={} using=LOADED_TENANT datasourceClass={}", tenantId, loadedDs.getClass().getName());
                return loadedDs;
            }
        }

        throw new IllegalStateException("No datasource registered for tenant: " + tenantId);
    }

    private DataSource tryLoadingTenantDataSource(String tenantId) {
        synchronized (tenantDataSources) {
            // Double check cache
            DataSource ds = tenantDataSources.get(tenantId);
            if (ds != null) return ds;

            log.info("Attempting to dynamically resolve datasource for tenant: {}", tenantId);
            
            try {
                String databaseName = resolveDatabaseNameFromMaster(tenantId);
                
                if (databaseName != null) {
                    log.info("Resolved database name '{}' for tenant '{}'", databaseName, tenantId);
                    DataSource tenantDataSource = dataSourceRegistry.getOrCreateDataSource(tenantId, databaseName);
                    registerTenantDataSource(tenantId, tenantDataSource);
                    return tenantDataSource;
                }
            } catch (Exception e) {
                log.error("Failed to dynamically resolve datasource for tenant: {}", tenantId, e);
            }
            
            return null;
        }
    }

    private String resolveDatabaseNameFromMaster(String tenantId) {
        try {
            // Normalize tenantId for comparison
            String normalizedId = tenantId.trim().toLowerCase();
            log.debug("Attempting to resolve database name for normalized tenant ID: {}", normalizedId);

            // 1. Try to find by UUID if tenantId is a valid UUID
            try {
                UUID uuid = UUID.fromString(tenantId);
                List<String> results = masterJdbcTemplate.queryForList(
                    "SELECT database_name FROM schools WHERE id = ?", String.class, uuid);
                if (!results.isEmpty()) return results.get(0);
            } catch (IllegalArgumentException ignored) {}

            // 2. Try to find by database_name itself (Case-Insensitive)
            List<String> results = masterJdbcTemplate.queryForList(
                "SELECT database_name FROM schools WHERE LOWER(database_name) = ?", String.class, normalizedId);
            if (!results.isEmpty()) return results.get(0);

            // 3. Try to find via school_domains table (matches subdomain or full domain)
            // This is crucial for subdomain-based routing (e.g., 'mgpsfren' matching 'mgpsfren.smsapp.com')
            results = masterJdbcTemplate.queryForList(
                "SELECT s.database_name FROM schools s " +
                "JOIN school_domains sd ON s.id = sd.school_id " +
                "WHERE LOWER(sd.domain_name) = ? OR LOWER(sd.domain_name) LIKE ?", 
                String.class, normalizedId, normalizedId + ".%");
            if (!results.isEmpty()) return results.get(0);

            // 4. Try to find by school name slug (normalize both sides to ignore spaces and hyphens)
            results = masterJdbcTemplate.queryForList(
                "SELECT database_name FROM schools " +
                "WHERE LOWER(REPLACE(REPLACE(name, ' ', ''), '-', '')) = REPLACE(?, '-', '')", 
                String.class, normalizedId);
            if (!results.isEmpty()) return results.get(0);

            // 5. Search by admin_email
            results = masterJdbcTemplate.queryForList(
                "SELECT database_name FROM schools WHERE LOWER(admin_email) = ?", String.class, normalizedId);
            if (!results.isEmpty()) return results.get(0);

            return null;
        } catch (Exception e) {
            log.warn("Database error while resolving tenant '{}': {}", tenantId, e.getMessage());
            return null;
        }
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
