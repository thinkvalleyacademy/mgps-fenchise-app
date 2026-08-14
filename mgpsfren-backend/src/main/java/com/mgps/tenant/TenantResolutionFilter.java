package com.mgps.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
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
 * 3. Clears the context after the response is sent
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

        // Never inherit context from a previous request that ran on this pooled thread.
        TenantContext.clear();

        try {
            String tenantId = tenantIdentifier.resolveTenant(request);

            if (tenantId != null) {
                TenantContext.setTenant(tenantId);
                log.debug("Request from tenant: {} [{}]", tenantId, request.getRequestURI());
            } else {
                log.debug("Request without tenant context: {}", request.getRequestURI());
            }

            filterChain.doFilter(request, response);

        } catch (TenantResolutionException e) {
            // Fail closed: the request cannot be attributed to a tenant, so it
            // must not proceed against any datasource.
            log.warn("Refusing request with unresolvable tenant [{}]: {}", request.getRequestURI(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"success\":false,\"message\":\"Tenant context could not be resolved\"}");
        } catch (Exception e) {
            log.error("Error in tenant resolution filter", e);
            throw new ServletException("Tenant resolution failed", e);
        } finally {
            // ALWAYS clear. Clearing only when a tenant was resolved leaked context
            // set downstream (e.g. by TenantExecutionService) into the next request
            // served by the same container thread.
            TenantContext.clear();
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
