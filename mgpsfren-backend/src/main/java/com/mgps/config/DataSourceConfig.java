package com.mgps.config;

import com.mgps.tenant.RoutingDataSource;
import com.mgps.tenant.TenantDataSourceService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataSource configuration for multi-tenant setup.
 * Configures:
 * 1. Master datasource (for admin/tenant registry)
 * 2. Routing datasource (routes queries to correct tenant database)
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);
    
    @Value("${spring.datasource.url}")
    private String masterUrl;
    
    @Value("${spring.datasource.username}")
    private String masterUsername;
    
    @Value("${spring.datasource.password}")
    private String masterPassword;
    
    /**
     * Create master datasource for tenant registry and global data
     */
    @Bean
    public DataSource masterDataSource() {
        log.info("Creating master datasource: {}", masterUrl);
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(masterUrl);
        config.setUsername(masterUsername);
        config.setPassword(masterPassword);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        config.setPoolName("HikariPool-Master");
        config.setDriverClassName("org.postgresql.Driver");
        
        return new HikariDataSource(config);
    }
    
    /**
     * Create routing datasource
     * Routes queries to master or tenant database based on TenantContext
     */
    @Bean
    @Primary
    public DataSource routingDataSource(DataSource masterDataSource) {
        log.info("Creating routing datasource");
        return new RoutingDataSource(masterDataSource);
    }
}
