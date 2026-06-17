package com.mgps.tenant;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class RoutingDataSource extends AbstractDataSource {

    private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);
    
    private final DataSource masterDataSource;
    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();
    private final JdbcTemplate masterJdbcTemplate;
    private DataSourceRegistry dataSourceRegistry;
    
    public RoutingDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
        this.masterJdbcTemplate = new JdbcTemplate(masterDataSource);
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
        ensureTenantAuthTable(dataSource);
        return dataSource.getConnection();
    }
    
    /**
     * Get connection with username and password
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        DataSource dataSource = determineDataSource();
        ensureTenantAuthTable(dataSource);
        return dataSource.getConnection(username, password);
    }
    
    /**
     * Determine which datasource to use based on tenant context
     */
    private void ensureTenantAuthTable(DataSource dataSource) {
        if (dataSource == null || dataSource == masterDataSource) {
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            boolean tableExists = false;

            try (ResultSet resultSet = metaData.getTables(null, "public", "app_users", new String[]{"TABLE"})) {
                tableExists = resultSet.next();
            }

            if (!tableExists) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS app_users (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        school_id UUID,
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        phone VARCHAR(20),
                        password_hash VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                        last_login_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    CREATE INDEX IF NOT EXISTS idx_app_users_school_id ON app_users(school_id);
                    CREATE INDEX IF NOT EXISTS idx_app_users_email ON app_users(email);
                    CREATE INDEX IF NOT EXISTS idx_app_users_role ON app_users(role);
                    CREATE INDEX IF NOT EXISTS idx_app_users_status ON app_users(status);
                    """;

                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }

                log.info("Created missing app_users table in tenant schema");
            }
        } catch (SQLException ex) {
            log.warn("Unable to ensure tenant auth table exists for datasource", ex);
        }
    }

    private DataSource determineDataSource() {
        String tenantId = TenantContext.getTenant();
        
        if (tenantId == null || tenantId.isBlank()) {
            log.debug("No tenant context set, using master datasource");
            return masterDataSource;
        }

        // Fixed tenant for superadmin ALWAYS uses master
        if (TenantNamingUtil.CLIENT_TENANT_ID.equalsIgnoreCase(tenantId)) {
            log.debug("Using master datasource for fixed superadmin tenant: {}", tenantId);
            return masterDataSource;
        }
        
        // Try to get tenant-specific datasource
        DataSource tenantDataSource = tenantDataSources.get(tenantId);
        if (tenantDataSource != null) {
            log.debug("Using cached tenant datasource for: {}", tenantId);
            return tenantDataSource;
        }
        
        // Attempt to lazy load from master database if datasource registry is available
        if (dataSourceRegistry != null) {
            DataSource loadedDs = tryLoadingTenantDataSource(tenantId);
            if (loadedDs != null) {
                return loadedDs;
            }
        }

        // If tenant datasource not found, log warning and use master
        log.warn("Tenant datasource not found for tenant: {}, using master as fallback", tenantId);
        return masterDataSource;
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
            // 1. Try to find by UUID if tenantId is a valid UUID
            try {
                UUID uuid = UUID.fromString(tenantId);
                List<String> results = masterJdbcTemplate.queryForList(
                    "SELECT database_name FROM schools WHERE id = ?", String.class, uuid);
                if (!results.isEmpty()) return results.get(0);
            } catch (IllegalArgumentException ignored) {}

            // 2. Try to find by database_name itself
            List<String> results = masterJdbcTemplate.queryForList(
                "SELECT database_name FROM schools WHERE database_name = ?", String.class, tenantId);
            if (!results.isEmpty()) return results.get(0);

            // 3. If still not found, search by admin_email which is sometimes used as an identifier
            results = masterJdbcTemplate.queryForList(
                "SELECT database_name FROM schools WHERE admin_email = ?", String.class, tenantId);
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
