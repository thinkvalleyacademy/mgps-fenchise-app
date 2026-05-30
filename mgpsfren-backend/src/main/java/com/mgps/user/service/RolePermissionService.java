package com.mgps.user.service;

import com.mgps.user.entity.UserPermission;
import com.mgps.user.entity.UserRole;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central role-to-permission mapping for the MVP.
 */
@Service
public class RolePermissionService {

    private final EnumMap<UserRole, Set<UserPermission>> permissionMap = new EnumMap<>(UserRole.class);

    public RolePermissionService() {
        permissionMap.put(UserRole.SUPER_ADMIN, EnumSet.allOf(UserPermission.class));
        permissionMap.put(UserRole.SCHOOL_ADMIN, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.USER_LIST,
            UserPermission.USER_READ,
            UserPermission.USER_CREATE,
            UserPermission.USER_UPDATE,
            UserPermission.USER_UPDATE_STATUS,
            UserPermission.USER_DELETE,
            UserPermission.USER_BULK_IMPORT,
            UserPermission.ACADEMIC_MANAGE,
            UserPermission.STUDENT_MANAGE,
            UserPermission.STAFF_MANAGE,
            UserPermission.PERMISSION_VIEW,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.PRINCIPAL, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.USER_LIST,
            UserPermission.USER_READ,
            UserPermission.USER_BULK_IMPORT,
            UserPermission.ACADEMIC_MANAGE,
            UserPermission.STUDENT_MANAGE,
            UserPermission.STAFF_MANAGE,
            UserPermission.PERMISSION_VIEW,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.TEACHER, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.ACADEMIC_MANAGE,
            UserPermission.STUDENT_MANAGE,
            UserPermission.STAFF_MANAGE,
            UserPermission.PERMISSION_VIEW,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.STUDENT, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.PARENT, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.ACCOUNTANT, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.USER_LIST,
            UserPermission.USER_READ,
            UserPermission.ACADEMIC_MANAGE,
            UserPermission.STAFF_MANAGE,
            UserPermission.PERMISSION_VIEW,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
        permissionMap.put(UserRole.STAFF, EnumSet.of(
            UserPermission.USER_VIEW_SELF,
            UserPermission.ACADEMIC_MANAGE,
            UserPermission.STUDENT_MANAGE,
            UserPermission.STAFF_MANAGE,
            UserPermission.AUTH_REFRESH_TOKEN,
            UserPermission.AUTH_LOGOUT
        ));
    }

    public Set<UserPermission> getPermissionsForRole(UserRole role) {
        if (role == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(permissionMap.getOrDefault(role, Collections.emptySet()));
    }

    public List<String> getPermissionNamesForRole(UserRole role) {
        return getPermissionsForRole(role).stream().map(Enum::name).sorted().collect(Collectors.toList());
    }

    public Set<String> getAuthorityNamesForRole(UserRole role) {
        return getPermissionsForRole(role).stream()
            .map(Enum::name)
            .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
