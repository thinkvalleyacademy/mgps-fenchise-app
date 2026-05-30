package com.mgps.user.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.user.entity.UserPermission;
import com.mgps.user.entity.UserRole;
import com.mgps.user.service.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final RolePermissionService rolePermissionService;

    public PermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/roles/{role}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<?>> getPermissionsForRole(@PathVariable UserRole role) {
        List<String> permissions = rolePermissionService.getPermissionNamesForRole(role);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("role", role);
        payload.put("permissions", permissions);
        return ResponseEntity.ok(ApiResponse.success(payload, "Role permissions retrieved successfully"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getMyPermissions(Authentication authentication) {
        List<String> permissions = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> !authority.startsWith("ROLE_"))
            .sorted()
            .collect(Collectors.toList());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("authorities", permissions);
        return ResponseEntity.ok(ApiResponse.success(payload, "Current permissions retrieved successfully"));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<?>> listRoles() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("roles", List.of(UserRole.values()));
        payload.put("permissions", List.of(UserPermission.values()));
        return ResponseEntity.ok(ApiResponse.success(payload, "Permission catalog retrieved successfully"));
    }
}
