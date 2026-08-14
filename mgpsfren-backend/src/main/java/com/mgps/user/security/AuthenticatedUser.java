package com.mgps.user.security;

import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * Authenticated principal that carries the tenant the user belongs to.
 *
 * The plain Spring {@link User} only carries the email, which forced
 * authorisation checks to fall back on {@code TenantContext}. That does not work
 * on control-plane endpoints (for example {@code /schools/**}), which run against
 * master and therefore have no tenant context even for a school user.
 */
public class AuthenticatedUser extends User {

    private final UUID userId;
    private final UUID schoolId;
    private final UserRole role;

    public AuthenticatedUser(AppUser user, Collection<? extends GrantedAuthority> authorities) {
        super(
            user.getEmail(),
            user.getPasswordHash(),
            user.getStatus() == UserStatus.ACTIVE,
            true,
            true,
            user.getStatus() == UserStatus.ACTIVE,
            authorities
        );
        this.userId = user.getId();
        this.schoolId = user.getSchoolId();
        this.role = user.getRole();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getSchoolId() {
        return schoolId;
    }

    public UserRole getRole() {
        return role;
    }
}
