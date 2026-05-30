package com.mgps.user.service;

import com.mgps.user.entity.UserPermission;
import com.mgps.user.entity.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RolePermissionServiceTest {

    @Test
    void superAdminShouldHaveAllPermissions() {
        RolePermissionService service = new RolePermissionService();

        assertThat(service.getPermissionsForRole(UserRole.SUPER_ADMIN))
            .contains(UserPermission.USER_CREATE, UserPermission.USER_MANAGE_PERMISSIONS, UserPermission.PERMISSION_VIEW);
    }

    @Test
    void studentShouldOnlyHaveSelfAndAuthPermissions() {
        RolePermissionService service = new RolePermissionService();

        assertThat(service.getPermissionsForRole(UserRole.STUDENT))
            .contains(UserPermission.USER_VIEW_SELF, UserPermission.AUTH_LOGOUT, UserPermission.AUTH_REFRESH_TOKEN)
            .doesNotContain(UserPermission.USER_LIST, UserPermission.USER_UPDATE_STATUS);
    }
}
