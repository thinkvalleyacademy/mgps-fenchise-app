package com.mgps.school.service;

import com.mgps.school.entity.School;
import com.mgps.school.exception.DatabaseProvisioningException;
import com.mgps.tenant.DataSourceRegistry;
import com.mgps.tenant.RoutingDataSource;
import com.mgps.tenant.TenantNamingUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Set;
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
    private static final String TENANT_MIGRATION_LOCATION = "classpath:db/migration/tenant";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private DataSourceRegistry dataSourceRegistry;
    
    @Value("${DB_HOST:postgres}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private String dbPort;

    @Value("${DB_USERNAME:${spring.datasource.username:${app.db.username:postgres}}}")
    private String dbUsername;

    @Value("${DB_PASSWORD:${spring.datasource.password:${app.db.password:postgres123}}}")
    private String dbPassword;
    
    public static String[] getTenantMigrationLocations() {
        return new String[]{TENANT_MIGRATION_LOCATION};
    }

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

            initializeTenantSchema(databaseName);

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
        registerDataSource(routingDataSource, null, schoolId, databaseName);
    }

    public void registerDataSource(RoutingDataSource routingDataSource, School school) {
        if (school == null) {
            throw new IllegalArgumentException("School cannot be null when registering datasource");
        }

        registerDataSource(routingDataSource, school, school.getId(), school.getDatabaseName());
    }

    private void registerDataSource(RoutingDataSource routingDataSource,
                                    School school,
                                    UUID schoolId,
                                    String databaseName) {
        log.info("Registering datasource for tenant: {}", schoolId);

        try {
            DataSource tenantDataSource = createTenantDataSource(databaseName);

            Set<String> tenantKeys = new LinkedHashSet<>();
            tenantKeys.add(schoolId.toString());

            if (school != null) {
                String tenantId = TenantNamingUtil.generateTenantId(school.getName(), school.getCity(), school.getPostalCode());
                if (!tenantId.isBlank()) {
                    tenantKeys.add(tenantId);
                }

                String tenantSlug = normalizeTenantSlug(school.getName());
                if (!tenantSlug.isBlank()) {
                    tenantKeys.add(tenantSlug);
                }

                if (school.getDatabaseName() != null && !school.getDatabaseName().isBlank()) {
                    tenantKeys.add(school.getDatabaseName());
                }
            }

            for (String tenantKey : tenantKeys) {
                routingDataSource.registerTenantDataSource(tenantKey, tenantDataSource);

                if (dataSourceRegistry != null) {
                    dataSourceRegistry.registerDataSource(tenantKey, tenantDataSource);
                }

                log.debug("Registered tenant datasource alias: {}", tenantKey);
            }

            log.info("Datasource registered successfully for tenant: {}", schoolId);
        } catch (Exception e) {
            log.error("Failed to register datasource", e);
            throw new DatabaseProvisioningException("Failed to register datasource", e);
        }
    }

    private String normalizeTenantSlug(String schoolName) {
        if (schoolName == null || schoolName.isBlank()) {
            return "";
        }

        return Normalizer.normalize(schoolName, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .toLowerCase()
            .replaceAll("\\s+", "-")
            .replaceAll("[^a-z0-9-]", "")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
    
    private void initializeTenantSchema(String databaseName) {
        HikariDataSource tenantDataSource = null;

        try {
            tenantDataSource = (HikariDataSource) createTenantDataSource(databaseName);

            Flyway flyway = Flyway.configure()
                .dataSource(tenantDataSource)
                .locations(getTenantMigrationLocations())
                .baselineOnMigrate(true)
                .load();

            int appliedMigrations = flyway.migrate().migrationsExecuted;
            log.info("Applied {} Flyway migrations to tenant database: {}", appliedMigrations, databaseName);
        } catch (Exception e) {
            log.error("Failed to initialize tenant schema for database: {}", databaseName, e);
            throw new DatabaseProvisioningException("Failed to initialize tenant schema", e);
        } finally {
            if (tenantDataSource != null) {
                tenantDataSource.close();
            }
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
