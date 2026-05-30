package com.mgps.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tenant context holder using ThreadLocal for thread-safe tenant storage.
 * Stores the current tenant ID for the executing request.
 * 
 * Usage:
 *   TenantContext.setTenant("school1");
 *   String tenantId = TenantContext.getTenant();  // "school1"
 *   TenantContext.clear();
 */
public class TenantContext {

    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    
    private static final ThreadLocal<String> tenantHolder = new ThreadLocal<>();
    
    /**
     * Set the current tenant ID
     * @param tenantId the tenant identifier
     */
    public static void setTenant(String tenantId) {
        if (tenantId != null && !tenantId.isBlank()) {
            log.debug("Setting tenant context: {}", tenantId);
            tenantHolder.set(tenantId);
        } else {
            log.warn("Attempt to set null or blank tenant ID");
        }
    }
    
    /**
     * Get the current tenant ID
     * @return tenant ID or null if not set
     */
    public static String getTenant() {
        return tenantHolder.get();
    }
    
    /**
     * Check if a tenant is currently set
     * @return true if tenant context is set, false otherwise
     */
    public static boolean isSet() {
        return tenantHolder.get() != null;
    }
    
    /**
     * Clear the tenant context (should be called after request processing)
     */
    public static void clear() {
        String tenant = tenantHolder.get();
        if (tenant != null) {
            log.debug("Clearing tenant context: {}", tenant);
        }
        tenantHolder.remove();
    }
    
    /**
     * Get the current tenant ID or throw exception if not set
     * @return tenant ID
     * @throws IllegalStateException if tenant is not set
     */
    public static String getTenantOrThrow() {
        String tenantId = getTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is not set");
        }
        return tenantId;
    }
}
