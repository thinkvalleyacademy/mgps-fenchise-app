package com.mgps.school.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.school.dto.*;
import com.mgps.school.entity.SchoolStatus;
import com.mgps.school.service.DomainMappingService;
import com.mgps.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for School Management
 * Provides endpoints for school registration, management, and domain mapping
 * 
 * Base Path: /api/schools
 */
@RestController
@RequestMapping("/schools")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class SchoolController {

    private static final Logger log = LoggerFactory.getLogger(SchoolController.class);
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private DomainMappingService domainMappingService;
    
    /**
     * Register a new school
     * POST /api/schools
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createSchool(@RequestBody SchoolRegistrationDTO dto) {
        log.info("Received request to create school: {}", dto.getName());
        
        SchoolCreatedDTO createdSchool = schoolService.registerSchool(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(createdSchool, "School registered successfully"));
    }
    
    /**
     * Get school by ID
     * GET /api/schools/{schoolId}
     */
    @GetMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<?>> getSchool(@PathVariable UUID schoolId) {
        log.info("Fetching school: {}", schoolId);
        
        SchoolDTO school = schoolService.getSchoolById(schoolId);
        
        return ResponseEntity.ok(ApiResponse.success(school, "School retrieved successfully"));
    }
    
    /**
     * Get all schools with pagination
     * GET /api/schools?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllSchools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) SchoolStatus status) {
        
        log.info("Fetching schools - page: {}, size: {}, status: {}", page, size, status);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolDTO> schools;
        
        if (status != null) {
            schools = schoolService.getSchoolsByStatus(status, pageable);
        } else {
            schools = schoolService.getAllSchools(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(schools, "Schools retrieved successfully"));
    }
    
    /**
     * Update school details
     * PUT /api/schools/{schoolId}
     */
    @PutMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<?>> updateSchool(
            @PathVariable UUID schoolId,
            @RequestBody SchoolUpdateDTO dto) {
        
        log.info("Updating school: {}", schoolId);
        
        SchoolDTO updatedSchool = schoolService.updateSchool(schoolId, dto);
        
        return ResponseEntity.ok(ApiResponse.success(updatedSchool, "School updated successfully"));
    }
    
    /**
     * Change school status
     * PATCH /api/schools/{schoolId}/status
     */
    @PatchMapping("/{schoolId}/status")
    public ResponseEntity<ApiResponse<?>> changeSchoolStatus(
            @PathVariable UUID schoolId,
            @RequestBody SchoolStatusDTO dto) {
        
        log.info("Changing school status to {}: {}", dto.getStatus(), schoolId);
        
        SchoolDTO updatedSchool = schoolService.changeStatus(schoolId, dto.getStatus());
        
        return ResponseEntity.ok(ApiResponse.success(updatedSchool, "School status changed successfully"));
    }
    
    /**
     * Get school domains
     * GET /api/schools/{schoolId}/domains
     */
    @GetMapping("/{schoolId}/domains")
    public ResponseEntity<ApiResponse<?>> getSchoolDomains(@PathVariable UUID schoolId) {
        log.info("Fetching domains for school: {}", schoolId);
        
        List<SchoolDomainDTO> domains = domainMappingService.getSchoolDomains(schoolId);
        
        return ResponseEntity.ok(ApiResponse.success(domains, "School domains retrieved successfully"));
    }
    
    /**
     * Add domain to school
     * POST /api/schools/{schoolId}/domains
     */
    @PostMapping("/{schoolId}/domains")
    public ResponseEntity<ApiResponse<?>> addDomain(
            @PathVariable UUID schoolId,
            @RequestBody SchoolDomainRequestDTO dto) {
        
        log.info("Adding domain to school {}: {}", schoolId, dto.getDomainName());
        
        domainMappingService.addDomain(schoolId, dto.getDomainName());
        
        List<SchoolDomainDTO> domains = domainMappingService.getSchoolDomains(schoolId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(domains, "Domain added successfully"));
    }
}
