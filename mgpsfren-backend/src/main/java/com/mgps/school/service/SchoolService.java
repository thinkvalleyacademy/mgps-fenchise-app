package com.mgps.school.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.school.dto.*;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolDomain;
import com.mgps.school.entity.SchoolStatus;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.exception.DatabaseProvisioningException;
import com.mgps.school.repository.SchoolDomainRepository;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.repository.SubscriptionPlanRepository;
import com.mgps.tenant.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for School Management
 * Handles school registration, provisioning, and domain management
 */
@Service
@Transactional
public class SchoolService {

    private static final Logger log = LoggerFactory.getLogger(SchoolService.class);
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private SchoolDomainRepository schoolDomainRepository;
    
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Autowired
    private DatabaseProvisioningService databaseProvisioningService;
    
    @Autowired
    private DomainMappingService domainMappingService;
    
    @Autowired
    private RoutingDataSource routingDataSource;
    
    /**
     * Register a new school
     * Steps:
     * 1. Validate input
     * 2. Check duplicate email
     * 3. Create school record in master DB
     * 4. Generate database name
     * 5. Provision tenant database
     * 6. Create primary domain mapping
     * 7. Register datasource for routing
     */
    public SchoolCreatedDTO registerSchool(SchoolRegistrationDTO dto) {
        log.info("Registering new school: {}", dto.getName());
        
        // Validation
        if (schoolRepository.existsByAdminEmail(dto.getAdminEmail())) {
            log.warn("Attempt to register school with existing email: {}", dto.getAdminEmail());
            throw new DuplicateResourceException("School with this email already exists");
        }
        
        // Verify subscription plan exists
        UUID planId = dto.getSubscriptionPlanId();
        SubscriptionPlan plan;

        if (planId == null) {
            log.warn("No subscription plan ID provided, attempting to find default 'BASIC' plan");
            plan = subscriptionPlanRepository.findByPlanName("BASIC")
                .or(() -> subscriptionPlanRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new ResourceNotFoundException("No subscription plans available in the system"));
        } else {
            plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));
        }

        // Create school entity
        UUID schoolId = UUID.randomUUID();
        School school = School.builder()
            .id(schoolId)
            .name(dto.getName())
            .adminEmail(dto.getAdminEmail())
            .adminPhone(dto.getAdminPhone())
            .address(dto.getAddress())
            .city(dto.getCity())
            .state(dto.getState())
            .postalCode(dto.getPostalCode())
            .subscriptionPlan(plan)
            .status(SchoolStatus.ACTIVE)
            .build();
        
        // Generate database name
        String databaseName = generateDatabaseName(schoolId);
        school.setDatabaseName(databaseName);
        
        // Save to master DB
        School savedSchool = schoolRepository.save(school);
        log.info("School created in master database: {} (ID: {})", savedSchool.getName(), savedSchool.getId());
        
        // Provision tenant database
        try {
            databaseProvisioningService.provisionDatabase(savedSchool);
            log.info("Database provisioned for school: {}", savedSchool.getName());
        } catch (Exception e) {
            log.error("Failed to provision database for school", e);
            // Rollback school creation if provisioning fails
            schoolRepository.delete(savedSchool);
            throw new DatabaseProvisioningException("Failed to provision school database", e);
        }
        
        // Create primary domain mapping
        String primaryDomain = generatePrimaryDomain(savedSchool.getName());
        SchoolDomain domain = domainMappingService.createPrimaryDomain(savedSchool, primaryDomain);
        log.info("Primary domain created for school: {}", primaryDomain);
        
        // Register datasource for tenant routing
        try {
            databaseProvisioningService.registerDataSource(routingDataSource, schoolId, databaseName);
            log.info("Datasource registered for school: {}", schoolId);
        } catch (Exception e) {
            log.error("Failed to register datasource for school", e);
            // Non-critical error, but log it
        }
        
        // Return response DTO
        return SchoolCreatedDTO.builder()
            .schoolId(savedSchool.getId())
            .name(savedSchool.getName())
            .databaseName(databaseName)
            .adminUrl(domain.getDomainName())
            .status(savedSchool.getStatus())
            .createdAt(savedSchool.getCreatedAt())
            .build();
    }
    
    /**
     * Get school by ID with full details
     */
    @Transactional(readOnly = true)
    public SchoolDTO getSchoolById(UUID schoolId) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        return mapToDTO(school);
    }
    
    /**
     * Get all schools with pagination
     */
    @Transactional(readOnly = true)
    public Page<SchoolDTO> getAllSchools(Pageable pageable) {
        return schoolRepository.findAll(pageable)
            .map(this::mapToDTO);
    }
    
    /**
     * Get schools by status
     */
    @Transactional(readOnly = true)
    public Page<SchoolDTO> getSchoolsByStatus(SchoolStatus status, Pageable pageable) {
        return schoolRepository.findByStatus(status, pageable)
            .map(this::mapToDTO);
    }
    
    /**
     * Update school details
     */
    public SchoolDTO updateSchool(UUID schoolId, SchoolUpdateDTO dto) {
        log.info("Updating school: {}", schoolId);
        
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        if (dto.getName() != null) school.setName(dto.getName());
        if (dto.getAdminPhone() != null) school.setAdminPhone(dto.getAdminPhone());
        if (dto.getAddress() != null) school.setAddress(dto.getAddress());
        if (dto.getCity() != null) school.setCity(dto.getCity());
        if (dto.getState() != null) school.setState(dto.getState());
        if (dto.getPostalCode() != null) school.setPostalCode(dto.getPostalCode());
        if (dto.getLogoUrl() != null) school.setLogoUrl(dto.getLogoUrl());
        
        School updated = schoolRepository.save(school);
        log.info("School updated successfully: {}", schoolId);
        
        return mapToDTO(updated);
    }
    
    /**
     * Change school status (activate/deactivate/suspend)
     */
    public SchoolDTO changeStatus(UUID schoolId, SchoolStatus newStatus) {
        log.info("Changing school status to {}: {}", newStatus, schoolId);
        
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        school.setStatus(newStatus);
        School updated = schoolRepository.save(school);
        
        log.info("School status changed: {} -> {}", schoolId, newStatus);
        
        return mapToDTO(updated);
    }
    
    /**
     * Get school by database name (internal use)
     */
    @Transactional(readOnly = true)
    public School getSchoolByDatabaseName(String databaseName) {
        return schoolRepository.findByDatabaseName(databaseName)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
    }
    
    /**
     * Map School entity to SchoolDTO
     */
    private SchoolDTO mapToDTO(School school) {
        List<SchoolDomainDTO> domains = school.getDomains() == null
            ? new ArrayList<>()
            : school.getDomains().stream()
                .map(this::mapDomainToDTO)
                .collect(Collectors.toList());
        
        SubscriptionPlanDTO planDTO = null;
        if (school.getSubscriptionPlan() != null) {
            planDTO = mapPlanToDTO(school.getSubscriptionPlan());
        }
        
        return SchoolDTO.builder()
            .schoolId(school.getId())
            .name(school.getName())
            .adminEmail(school.getAdminEmail())
            .adminPhone(school.getAdminPhone())
            .address(school.getAddress())
            .city(school.getCity())
            .state(school.getState())
            .postalCode(school.getPostalCode())
            .logoUrl(school.getLogoUrl())
            .databaseName(school.getDatabaseName())
            .status(school.getStatus())
            .domains(domains)
            .subscriptionPlan(planDTO)
            .createdAt(school.getCreatedAt())
            .updatedAt(school.getUpdatedAt())
            .build();
    }
    
    /**
     * Map SchoolDomain to SchoolDomainDTO
     */
    private SchoolDomainDTO mapDomainToDTO(SchoolDomain domain) {
        return SchoolDomainDTO.builder()
            .domainId(domain.getId())
            .domainName(domain.getDomainName())
            .isPrimary(domain.getIsPrimary())
            .isActive(domain.getIsActive())
            .createdAt(domain.getCreatedAt())
            .build();
    }
    
    /**
     * Map SubscriptionPlan to SubscriptionPlanDTO
     */
    private SubscriptionPlanDTO mapPlanToDTO(SubscriptionPlan plan) {
        return SubscriptionPlanDTO.builder()
            .planId(plan.getId())
            .planName(plan.getPlanName())
            .description(plan.getDescription())
            .maxStudents(plan.getMaxStudents())
            .maxStaff(plan.getMaxStaff())
            .maxUsers(plan.getMaxUsers())
            .monthlyPrice(plan.getMonthlyPrice() != null ? plan.getMonthlyPrice().toPlainString() : null)
            .isActive(plan.getIsActive())
            .build();
    }
    
    /**
     * Generate database name for school
     */
    private String generateDatabaseName(UUID schoolId) {
        // Format: school_<first 12 chars of uuid>_db
        String uuidStr = schoolId.toString().replace("-", "");
        return "school_" + uuidStr.substring(0, 12) + "_db";
    }
    
    /**
     * Generate primary domain for school
     * Format: {school-name-slug}.smsapp.com
     */
    private String generatePrimaryDomain(String schoolName) {
        // Normalize to ASCII, convert to lowercase, replace spaces with hyphens
        String normalized = Normalizer
            .normalize(schoolName, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .toLowerCase()
            .replaceAll("\\s+", "-")
            .replaceAll("[^a-z0-9-]", "");
        
        return normalized + ".smsapp.com";
    }
}
