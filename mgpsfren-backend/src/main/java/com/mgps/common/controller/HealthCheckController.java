package com.mgps.common.controller;

import com.mgps.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Check Controller
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {
    
    /**
     * Health check endpoint
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> healthCheck() {
        Map<String, Object> healthStatus = new LinkedHashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now());
        healthStatus.put("service", "MGPS School Management System");
        healthStatus.put("version", "1.0.0");
        
        return ResponseEntity.ok(
            ApiResponse.success(healthStatus, "Service is running")
        );
    }
    
    /**
     * API info endpoint
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "MGPS - School Management System");
        info.put("description", "Multi-tenant SaaS platform for managing schools");
        info.put("version", "1.0.0");
        info.put("baseUrl", "/api");
        
        return ResponseEntity.ok(
            ApiResponse.success(info, "API Information")
        );
    }
}
