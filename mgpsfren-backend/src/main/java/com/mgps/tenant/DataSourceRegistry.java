package com.mgps.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for managing multiple datasources.
 * Creates and caches datasources for different tenants.
 * Implements lazy loading and connection pooling.
 */
@Component
public class DataSourceRegistry {

    private static final Logger log = LoggerFactory.getLogger(DataSourceRegistry.class);
    
    private final Map<String, DataSource> dataSourceCache = new HashMap<>();
    private final Object cacheLock = new Object();
    
    private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%d/%s";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5432;
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres123";
    
    /**
     * Get or create a datasource for a tenant
     * Implements lazy loading with caching
     * 
     * @param tenantId tenant identifier
     * @param databaseName name of the database to connect to
     * @return DataSource for the tenant
     */
    public DataSource getOrCreateDataSource(String tenantId, String databaseName) {
        if (tenantId == null || databaseName == null) {
            throw new IllegalArgumentException("Tenant ID and database name cannot be null");
        }
        
        // Check cache first
        DataSource cached = dataSourceCache.get(tenantId);
        if (cached != null) {
            log.debug("Using cached datasource for tenant: {}", tenantId);
            return cached;
        }
        
        // Create new datasource with synchronization
        synchronized (cacheLock) {
            // Double-check pattern
            cached = dataSourceCache.get(tenantId);
            if (cached != null) {
                return cached;
            }
            
            DataSource newDataSource = createDataSource(tenantId, databaseName);
            dataSourceCache.put(tenantId, newDataSource);
            log.info("Created and cached new datasource for tenant: {}", tenantId);
            return newDataSource;
        }
    }

    /**
     * Register an already created datasource in the cache.
     */
    public void registerDataSource(String tenantId, DataSource dataSource) {
        if (tenantId == null || dataSource == null) {
            throw new IllegalArgumentException("Tenant ID and datasource cannot be null");
        }

        synchronized (cacheLock) {
            dataSourceCache.put(tenantId, dataSource);
            log.info("Registered datasource in cache for tenant: {}", tenantId);
        }
    }
    
    /**
     * Create a new HikariCP datasource
     */
    private DataSource createDataSource(String tenantId, String databaseName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format(JDBC_URL_TEMPLATE, DEFAULT_HOST, DEFAULT_PORT, databaseName));
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        config.setPoolName("HikariPool-" + tenantId);
        
        return new HikariDataSource(config);
    }
    
    /**
     * Get a cached datasource without creating
     * @param tenantId tenant identifier
     * @return DataSource if exists, null otherwise
     */
    public DataSource getDataSource(String tenantId) {
        return dataSourceCache.get(tenantId);
    }
    
    /**
     * Remove a datasource from cache and close it
     * @param tenantId tenant identifier
     */
    public void removeDataSource(String tenantId) {
        synchronized (cacheLock) {
            DataSource dataSource = dataSourceCache.remove(tenantId);
            if (dataSource instanceof HikariDataSource) {
                log.info("Closing datasource for tenant: {}", tenantId);
                ((HikariDataSource) dataSource).close();
            }
        }
    }
    
    /**
     * Check if datasource exists for tenant
     * @param tenantId tenant identifier
     * @return true if datasource is cached
     */
    public boolean containsDataSource(String tenantId) {
        return dataSourceCache.containsKey(tenantId);
    }
    
    /**
     * Get number of cached datasources
     */
    public int size() {
        return dataSourceCache.size();
    }
    
    /**
     * Clear all cached datasources and close them
     */
    public void clear() {
        synchronized (cacheLock) {
            dataSourceCache.values().stream()
                .filter(ds -> ds instanceof HikariDataSource)
                .forEach(ds -> {
                    log.info("Closing datasource");
                    ((HikariDataSource) ds).close();
                });
            dataSourceCache.clear();
        }
    }
}
