package com.mgps.tenant;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for tenant identification strategies.
 * Determines which tenant a request belongs to.
 */
public interface TenantIdentifier {
    
    /**
     * Extract tenant ID from the current request
     * @param request HTTP request
     * @return tenant ID, or null if unable to determine
     */
    String resolveTenant(HttpServletRequest request);
}
