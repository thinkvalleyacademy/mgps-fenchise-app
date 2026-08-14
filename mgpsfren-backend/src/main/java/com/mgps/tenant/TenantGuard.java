package com.mgps.tenant;

import com.mgps.user.entity.UserRole;
import com.mgps.user.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Authorises operations against a caller-supplied {@code schoolId}.
 *
 * Most endpoints take the target school as a request parameter or body field.
 * Datasource routing alone does not make that safe: any code path that resolves
 * the school from master and then switches context (see
 * {@link TenantExecutionService#inTenant}) will happily operate on a different
 * tenant's database if the id is not checked against the caller first.
 *
 * Rules:
 * <ul>
 *   <li>SUPER_ADMIN is a platform-wide role and may target any school.</li>
 *   <li>Every other principal may only target the school bound to its own
 *       token, i.e. the current {@link TenantContext}.</li>
 * </ul>
 */
@Component
public class TenantGuard {

    private static final Logger log = LoggerFactory.getLogger(TenantGuard.class);

    private static final String SUPER_ADMIN_AUTHORITY = "ROLE_" + UserRole.SUPER_ADMIN.name();

    /**
     * Assert that the current principal may act on the given school.
     *
     * @param schoolId target school; {@code null} means "the caller's own tenant"
     *                 and is always allowed.
     * @throws AccessDeniedException if the caller may not act on that school
     */
    public void assertSchoolAccessible(UUID schoolId) {
        if (schoolId == null) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // No Spring Security principal at all means this call did not arrive through
            // the HTTP/JWT layer: a @Scheduled job, bootstrap code, or a unit test calling
            // the service directly. Spring Security rejects every real HTTP request with no
            // authenticated principal before it reaches a guarded service method (see
            // SecurityConfig's `.anyRequest().authenticated()`), so there is no external,
            // potentially malicious caller this branch could ever let through unchecked —
            // only our own server-side code reaches here.
            return;
        }

        if (isSuperAdmin()) {
            return;
        }

        UUID callerSchool = currentSchoolId();
        if (callerSchool == null) {
            log.warn("Rejecting request for school {}: caller has no school binding", schoolId);
            throw new AccessDeniedException("No tenant context for this request");
        }

        if (!callerSchool.equals(schoolId)) {
            log.warn("Cross-tenant access denied | callerSchool={} requestedSchool={}", callerSchool, schoolId);
            throw new AccessDeniedException("Not permitted to access another school's data");
        }
    }

    /**
     * True when the current principal is a platform superadmin.
     */
    public boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(SUPER_ADMIN_AUTHORITY::equals);
    }

    /**
     * The school the caller is bound to, or {@code null} for master/superadmin context.
     *
     * Read from the authenticated principal rather than {@link TenantContext},
     * because control-plane endpoints (e.g. {@code /schools/**}) intentionally run
     * against master and therefore carry no tenant context even for school users.
     * {@link TenantContext} is only a fallback for non-HTTP callers.
     */
    public UUID currentSchoolId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            if (principal.getSchoolId() != null) {
                return principal.getSchoolId();
            }
        }

        String tenant = TenantContext.getTenant();
        if (tenant == null || tenant.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(tenant);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
