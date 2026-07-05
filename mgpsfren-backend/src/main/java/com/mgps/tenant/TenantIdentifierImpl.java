package com.mgps.tenant;

import com.mgps.user.service.JwtService;
import com.mgps.user.entity.UserRole;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of TenantIdentifier.
 * Resolves tenant from (in priority order):
 * 1. JWT token (Superadmins always use master, others use their assigned tenant)
 * 2. X-Tenant-Id HTTP header (for public/anonymous requests or context switching)
 * 3. Subdomain extraction (school1.smsapp.com -> school1)
 */
@Component
public class TenantIdentifierImpl implements TenantIdentifier {

    private static final Logger log = LoggerFactory.getLogger(TenantIdentifierImpl.class);

    private final JwtService jwtService;

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public TenantIdentifierImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    public String resolveTenant(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        // Global public endpoints must never inherit tenant context from host/header.
        if (requestUri.endsWith("/auth/login") || requestUri.contains("/setup/")) {
            return null;
        }

        // Priority 1: Check JWT token
        // If a valid token is present, it dictates the context.
        // Superadmins are global and always stay in master.
        String tenantFromToken = resolveFromToken(request);
        if (tenantFromToken != null) {
            if ("MASTER".equals(tenantFromToken)) {
                log.debug("Superadmin request resolved to master context");
                return null;
            }
            log.debug("Tenant resolved from token: {}", tenantFromToken);
            return tenantFromToken;
        }
        
        // Priority 2: Check X-Tenant-Id header
        // Useful for anonymous requests or when a superadmin wants to target a specific tenant via header
        // Note: For superadmins, we might still want to force master here to protect the security check
        String tenantFromHeader = resolveFromHeader(request);
        if (tenantFromHeader != null) {
            log.debug("Tenant resolved from header: {}", tenantFromHeader);
            return tenantFromHeader;
        }
        
        // Priority 3: Check subdomain
        String tenantFromSubdomain = resolveFromSubdomain(request);
        if (tenantFromSubdomain != null) {
            log.debug("Tenant resolved from subdomain: {}", tenantFromSubdomain);
            return tenantFromSubdomain;
        }
        
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
     * Extract tenant or 'MASTER' from JWT token
     */
    private String resolveFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            return null;
        }

        try {
            // Check for Superadmin role first
            String role = jwtService.extractRole(token);
            if (UserRole.SUPER_ADMIN.name().equals(role)) {
                return "MASTER"; 
            }

            // Extract assigned tenant for other roles
            String tenantId = jwtService.extractTenantId(token);
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId.toLowerCase().trim();
            }

            String schoolId = jwtService.extractSchoolIdAsString(token);
            if (schoolId != null && !schoolId.isBlank()) {
                return schoolId;
            }
        } catch (Exception ex) {
            log.debug("Could not resolve tenant from JWT", ex);
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
