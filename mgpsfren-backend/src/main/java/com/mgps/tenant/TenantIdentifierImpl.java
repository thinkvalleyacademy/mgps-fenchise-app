package com.mgps.tenant;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of TenantIdentifier.
 * Resolves tenant from (in priority order):
 * 1. X-Tenant-Id HTTP header
 * 2. JWT token tenant claim
 * 3. Subdomain extraction (school1.smsapp.com -> school1)
 */
@Component
public class TenantIdentifierImpl implements TenantIdentifier {

    private static final Logger log = LoggerFactory.getLogger(TenantIdentifierImpl.class);
    
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    public String resolveTenant(HttpServletRequest request) {
        // Priority 1: Check X-Tenant-Id header
        String tenantFromHeader = resolveFromHeader(request);
        if (tenantFromHeader != null) {
            log.debug("Tenant resolved from header: {}", tenantFromHeader);
            return tenantFromHeader;
        }
        
        // Priority 2: Check JWT token
        String tenantFromToken = resolveFromToken(request);
        if (tenantFromToken != null) {
            log.debug("Tenant resolved from token: {}", tenantFromToken);
            return tenantFromToken;
        }
        
        // Priority 3: Check subdomain
        String tenantFromSubdomain = resolveFromSubdomain(request);
        if (tenantFromSubdomain != null) {
            log.debug("Tenant resolved from subdomain: {}", tenantFromSubdomain);
            return tenantFromSubdomain;
        }
        
        log.warn("Could not resolve tenant from request");
        return null;
    }
    
    /**
     * Extract tenant from X-Tenant-Id header
     */
    private String resolveFromHeader(HttpServletRequest request) {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId.toLowerCase().trim();
        }
        return null;
    }
    
    /**
     * Extract tenant from JWT token (claims)
     * Note: Full implementation would decode JWT, this is a placeholder
     */
    private String resolveFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            // In a real implementation, decode JWT and extract 'tenantId' claim
            // For now, return null as this requires proper JWT configuration
            return null;
        }
        return null;
    }
    
    /**
     * Extract tenant from subdomain (school1.smsapp.com -> school1)
     */
    private String resolveFromSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        
        // Exclude localhost and direct IP addresses
        if (serverName.contains("localhost") || isIpAddress(serverName)) {
            return null;
        }
        
        // Extract subdomain
        String[] parts = serverName.split("\\.");
        if (parts.length >= 3) {
            // Format: school1.smsapp.com -> take first part
            return parts[0].toLowerCase();
        }
        
        return null;
    }
    
    /**
     * Check if the server name is an IP address
     */
    private boolean isIpAddress(String serverName) {
        return serverName.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }
}
