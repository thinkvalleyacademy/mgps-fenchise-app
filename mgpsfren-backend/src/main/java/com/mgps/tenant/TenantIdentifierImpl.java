package com.mgps.tenant;

import com.mgps.user.service.JwtService;
import com.mgps.user.entity.UserRole;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves the tenant that owns the current request.
 *
 * Security model:
 * <ol>
 *   <li><b>Control-plane endpoints</b> always run against master and never
 *       inherit tenant context from client-controlled input.</li>
 *   <li><b>Authenticated requests</b> take their tenant exclusively from the
 *       signed JWT. The {@code X-Tenant-Id} header and the subdomain are
 *       ignored, because both are attacker-controlled. The single exception is
 *       a SUPER_ADMIN, who may explicitly target one tenant via the header.</li>
 *   <li><b>Anonymous requests</b> may only use the header/subdomain on the small
 *       allow-list of endpoints that genuinely need tenant context before a
 *       token exists.</li>
 * </ol>
 *
 * Anything else fails closed with {@link TenantResolutionException} rather than
 * falling back to the master datasource.
 */
@Component
public class TenantIdentifierImpl implements TenantIdentifier {

    private static final Logger log = LoggerFactory.getLogger(TenantIdentifierImpl.class);

    private final JwtService jwtService;

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /** Sentinel meaning "this principal is global, stay on master". */
    private static final String MASTER = "MASTER";

    /**
     * Endpoints reachable without a token that still need tenant context.
     * Everything not listed here gets no tenant when unauthenticated.
     */
    private static final Set<String> ANONYMOUS_TENANT_ENDPOINTS = Set.of(
        "/enquiries"
    );

    public TenantIdentifierImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String resolveTenant(HttpServletRequest request) {
        String path = normalizePath(request.getRequestURI());

        // Global control-plane endpoints must never inherit tenant context.
        if (isMasterEndpoint(path)) {
            return null;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        boolean hasBearer = authHeader != null && authHeader.startsWith(BEARER_PREFIX);

        if (hasBearer) {
            return resolveForAuthenticatedRequest(request, authHeader.substring(BEARER_PREFIX.length()).trim(), path);
        }

        // Anonymous request: only a narrow allow-list may pick a tenant.
        if (!isAnonymousTenantEndpoint(path)) {
            log.debug("Anonymous request to non-tenant endpoint, staying on master: {}", path);
            return null;
        }

        String fromHeader = resolveFromHeader(request);
        if (fromHeader != null) {
            log.debug("Anonymous tenant resolved from header: {}", fromHeader);
            return fromHeader;
        }

        String fromSubdomain = resolveFromSubdomain(request);
        if (fromSubdomain != null) {
            log.debug("Anonymous tenant resolved from subdomain: {}", fromSubdomain);
            return fromSubdomain;
        }

        return null;
    }

    /**
     * For a request carrying a bearer token the token is the only trusted
     * source of tenant identity.
     */
    private String resolveForAuthenticatedRequest(HttpServletRequest request, String token, String path) {
        String tenantFromToken = resolveFromToken(token);

        if (tenantFromToken == null) {
            // Token is absent/expired/forged/not an access token. Do NOT fall back
            // to the header or subdomain: that was the cross-tenant escape hatch.
            // Stay on master with no tenant; the security filter chain rejects the
            // request before it can touch any data.
            log.debug("Bearer token present but unusable for tenant resolution: {}", path);
            return null;
        }

        if (MASTER.equals(tenantFromToken)) {
            // Superadmins are global. They may explicitly target a tenant, and
            // only they may do so, because only they are authorised platform-wide.
            String impersonated = resolveFromHeader(request);
            if (impersonated != null) {
                log.info("Superadmin targeting tenant via header: {} [{}]", impersonated, path);
                return impersonated;
            }
            return null;
        }

        return tenantFromToken;
    }

    private String normalizePath(String requestUri) {
        if (requestUri == null) {
            return "";
        }
        return requestUri.startsWith("/api/")
            ? requestUri.substring("/api".length())
            : requestUri;
    }

    private boolean isMasterEndpoint(String path) {
        return path.endsWith("/auth/login")
            || path.endsWith("/auth/refresh")
            || path.contains("/setup/")
            || path.equals("/schools")
            || path.startsWith("/schools/")
            || path.equals("/subscription-plans")
            || path.startsWith("/subscription-plans/");
    }

    private boolean isAnonymousTenantEndpoint(String path) {
        return ANONYMOUS_TENANT_ENDPOINTS.stream()
            .anyMatch(allowed -> path.equals(allowed) || path.startsWith(allowed + "/"));
    }

    /**
     * Extract tenant from X-Tenant-Id header.
     */
    private String resolveFromHeader(HttpServletRequest request) {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId.toLowerCase().trim();
        }
        return null;
    }

    /**
     * Extract the tenant, or the {@code MASTER} sentinel, from a signed JWT.
     *
     * @return the tenant id, {@code MASTER} for superadmins, or {@code null}
     *         when the token cannot be trusted to carry tenant identity.
     */
    private String resolveFromToken(String token) {
        if (token.isBlank()) {
            return null;
        }

        try {
            // Only access tokens grant context. A refresh token replayed as a
            // bearer credential must not select a datasource.
            if (!jwtService.isTokenValid(token) || !"access".equals(jwtService.extractTokenType(token))) {
                return null;
            }

            String role = jwtService.extractRole(token);
            if (UserRole.SUPER_ADMIN.name().equals(role)) {
                return MASTER;
            }

            String tenantId = jwtService.extractTenantId(token);
            if (tenantId != null && !tenantId.isBlank()) {
                return tenantId.toLowerCase().trim();
            }

            String schoolId = jwtService.extractSchoolIdAsString(token);
            if (schoolId != null && !schoolId.isBlank()) {
                return schoolId.toLowerCase().trim();
            }

            // A non-superadmin with no tenant claim has nowhere legitimate to go.
            // Previously this fell through to the header and then to master,
            // handing tenant users the control-plane database.
            throw new TenantResolutionException(
                "Authenticated principal has no tenant assigned; refusing to fall back to master");
        } catch (TenantResolutionException ex) {
            throw ex;
        } catch (Exception ex) {
            log.debug("Could not resolve tenant from JWT", ex);
            return null;
        }
    }

    /**
     * Extract tenant from subdomain (school1.smsapp.com -> school1).
     */
    private String resolveFromSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();

        if (serverName == null || serverName.contains("localhost") || isIpAddress(serverName)) {
            return null;
        }

        String[] parts = serverName.split("\\.");
        if (parts.length >= 3) {
            return parts[0].toLowerCase();
        }

        return null;
    }

    private boolean isIpAddress(String serverName) {
        return serverName.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }
}
