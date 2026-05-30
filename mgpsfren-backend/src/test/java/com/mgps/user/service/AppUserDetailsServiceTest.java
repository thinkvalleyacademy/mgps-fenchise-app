package com.mgps.user.service;

import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void shouldBuildAuthoritiesFromRolePermissions() {
        RolePermissionService rolePermissionService = new RolePermissionService();
        AppUserDetailsService service = new AppUserDetailsService(appUserRepository, rolePermissionService);

        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .passwordHash("hashed")
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

        when(appUserRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin@example.com");

        assertThat(details.getAuthorities())
            .extracting("authority")
            .contains("ROLE_SUPER_ADMIN", "USER_MANAGE_PERMISSIONS", "USER_LIST", "USER_READ");
    }
}
