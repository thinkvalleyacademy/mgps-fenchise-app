package com.mgps.config;

import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multi-tenant configuration.
 * 
 * Note: TenantResolutionFilter is auto-registered as @Component
 * and auto-configured by Spring Boot, so no additional bean configuration needed.
 */
@Configuration
public class MultiTenantConfig {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantConfig.class);
    
    // Configuration bean - no additional setup required
    // TenantResolutionFilter is auto-registered as @Component
}
