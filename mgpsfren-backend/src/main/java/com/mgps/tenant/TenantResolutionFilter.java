package com.mgps.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter for tenant resolution and context setting.
 * 
 * This filter:
 * 1. Identifies the tenant from the incoming request
 * 2. Sets the TenantContext for the request lifecycle
 * 3. Clears the context after response is sent
 * 
 * Must be registered with high precedence to ensure it runs before other filters.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantResolutionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantResolutionFilter.class);
    
    @Autowired
    private TenantIdentifier tenantIdentifier;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
        
        String tenantId = null;
        
        try {
            // Resolve tenant from request
            tenantId = tenantIdentifier.resolveTenant(request);
            
            if (tenantId != null) {
                // Set tenant context for this request
                TenantContext.setTenant(tenantId);
                log.debug("Request from tenant: {} [{}]", tenantId, request.getRequestURI());
            } else {
                log.debug("Request without tenant context: {}", request.getRequestURI());
            }
            
            // Continue request processing
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Error in tenant resolution filter", e);
            throw new ServletException("Tenant resolution failed", e);
        } finally {
            // Always clear tenant context after request processing
            if (tenantId != null) {
                TenantContext.clear();
                log.debug("Cleared tenant context: {}", tenantId);
            }
        }
    }
    
    /**
     * This filter should apply to all requests
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Apply filter to all requests
        return false;
    }
}
