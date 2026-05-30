package com.mgps.common.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tenant Information Controller
 * Demonstrates multi-tenant routing by showing current tenant context
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {
    
    /**
     * Get current tenant information
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getTenantInfo() {
        String tenantId = TenantContext.getTenant();
        
        Map<String, Object> tenantInfo = new LinkedHashMap<>();
        tenantInfo.put("tenantId", tenantId);
        tenantInfo.put("isTenantSet", tenantId != null);
        tenantInfo.put("timestamp", LocalDateTime.now());
        
        if (tenantId != null) {
            return ResponseEntity.ok(
                ApiResponse.success(tenantInfo, "Tenant information retrieved successfully")
            );
        } else {
            return ResponseEntity.ok(
                ApiResponse.success(tenantInfo, "No tenant context set (using master database)")
            );
        }
    }
    
    /**
     * Verify multi-tenant context
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyTenantContext() {
        boolean isSet = TenantContext.isSet();
        String tenantId = TenantContext.getTenant();
        
        Map<String, Object> verification = new LinkedHashMap<>();
        verification.put("contextSet", isSet);
        verification.put("tenantId", tenantId);
        verification.put("routing", isSet ? "To tenant database" : "To master database");
        
        return ResponseEntity.ok(
            ApiResponse.success(verification, "Tenant context verified")
        );
    }
}
