package com.mgps.school.service;

import com.mgps.school.entity.School;
import com.mgps.school.exception.DatabaseProvisioningException;
import com.mgps.tenant.DataSourceRegistry;
import com.mgps.tenant.RoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for Database Provisioning
 * Handles creation and setup of tenant databases
 */
@Service
public class DatabaseProvisioningService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseProvisioningService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private DataSourceRegistry dataSourceRegistry;
    
    @Value("${app.db.host:localhost}")
    private String dbHost;
    
    @Value("${app.db.port:5432}")
    private String dbPort;
    
    @Value("${app.db.username:postgres}")
    private String dbUsername;
    
    @Value("${app.db.password:postgres123}")
    private String dbPassword;
    
    /**
     * Provision a new tenant database
     * Creates the database and initializes schema via Flyway
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void provisionDatabase(School school) {
        String databaseName = school.getDatabaseName();
        log.info("Starting database provisioning for school: {}", school.getName());
        
        try {
            // Create database
            createDatabase(databaseName);
            log.info("Database created: {}", databaseName);
            
            // Note: Flyway migrations for tenant schema are handled separately
            // via @FlywayTest or separate configuration
            
            log.info("Database provisioning completed for school: {}", school.getName());
        } catch (DataAccessException e) {
            log.error("Failed to create database: {}", databaseName, e);
            throw new DatabaseProvisioningException("Failed to provision database", e);
        }
    }
    
    /**
     * Create a new database
     * Annotated with Propagation.NOT_SUPPORTED to ensure it runs outside of any transaction block,
     * which is required by PostgreSQL for CREATE DATABASE commands.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createDatabase(String databaseName) {
        String createDbSql = "CREATE DATABASE \"" + databaseName + "\" " +
            "OWNER postgres " +
            "ENCODING 'UTF8' " +
            "LOCALE 'en_US.UTF-8' " +
            "TEMPLATE template0";
        
        log.debug("Executing: {}", createDbSql);
        jdbcTemplate.execute(createDbSql);
    }
    
    /**
     * Drop tenant database (use with caution!)
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteDatabase(String databaseName) {
        log.warn("Deleting database: {}", databaseName);
        
        try {
            // Terminate all connections to the database
            String terminateSql = "SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                "FROM pg_stat_activity " +
                "WHERE pg_stat_activity.datname = '" + databaseName + "' " +
                "AND pid <> pg_backend_pid()";
            
            jdbcTemplate.execute(terminateSql);
            
            // Drop database
            String dropDbSql = "DROP DATABASE IF EXISTS \"" + databaseName + "\"";
            jdbcTemplate.execute(dropDbSql);
            
            log.info("Database deleted: {}", databaseName);
        } catch (DataAccessException e) {
            log.error("Failed to delete database: {}", databaseName, e);
            throw new DatabaseProvisioningException("Failed to delete database", e);
        }
    }
    
    /**
     * Register datasource in routing datasource for tenant
     */
    public void registerDataSource(RoutingDataSource routingDataSource, UUID schoolId, String databaseName) {
        log.info("Registering datasource for tenant: {}", schoolId);
        
        try {
            DataSource tenantDataSource = createTenantDataSource(databaseName);
            String tenantIdKey = schoolId.toString();
            
            routingDataSource.registerTenantDataSource(tenantIdKey, tenantDataSource);
            
            if (dataSourceRegistry != null) {
                dataSourceRegistry.registerDataSource(tenantIdKey, tenantDataSource);
            }
            
            log.info("Datasource registered successfully for tenant: {}", schoolId);
        } catch (Exception e) {
            log.error("Failed to register datasource", e);
            throw new DatabaseProvisioningException("Failed to register datasource", e);
        }
    }
    
    /**
     * Create HikariCP datasource for tenant database
     */
    private DataSource createTenantDataSource(String databaseName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + databaseName);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        config.setLeakDetectionThreshold(60000);
        
        log.debug("Creating HikariCP datasource for database: {}", databaseName);
        
        return new HikariDataSource(config);
    }
}
