package com.mgps.user.entity;

/**
 * Permissions used by the user management module.
 */
public enum UserPermission {
    USER_VIEW_SELF,
    USER_LIST,
    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_UPDATE_STATUS,
    USER_DELETE,
    USER_BULK_IMPORT,
    USER_MANAGE_PERMISSIONS,
    ACADEMIC_MANAGE,
    STUDENT_MANAGE,
    STAFF_MANAGE,
    AUTH_REFRESH_TOKEN,
    AUTH_LOGOUT,
    PERMISSION_VIEW
}
