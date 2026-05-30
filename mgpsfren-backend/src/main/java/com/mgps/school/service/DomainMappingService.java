package com.mgps.school.service;

import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolDomain;
import com.mgps.school.repository.SchoolDomainRepository;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.school.dto.SchoolDomainDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Domain Mapping Operations
 * Manages school domains and subdomain routing
 */
@Service
@Transactional
public class DomainMappingService {

    private static final Logger log = LoggerFactory.getLogger(DomainMappingService.class);
    
    @Autowired
    private SchoolDomainRepository schoolDomainRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    /**
     * Create primary domain for school
     */
    public SchoolDomain createPrimaryDomain(School school, String domainName) {
        log.info("Creating primary domain for school: {}", school.getId());
        
        SchoolDomain domain = SchoolDomain.builder()
            .id(UUID.randomUUID())
            .school(school)
            .domainName(domainName)
            .isPrimary(true)
            .isActive(true)
            .build();
        
        SchoolDomain saved = schoolDomainRepository.save(domain);
        log.info("Primary domain created: {}", domainName);
        
        return saved;
    }
    
    /**
     * Add additional domain for school
     */
    public SchoolDomain addDomain(UUID schoolId, String domainName) {
        log.info("Adding domain to school: {} -> {}", schoolId, domainName);
        
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        if (schoolDomainRepository.existsByDomainName(domainName)) {
            log.warn("Attempt to create duplicate domain: {}", domainName);
            throw new DuplicateResourceException("Domain already exists");
        }
        
        SchoolDomain domain = SchoolDomain.builder()
            .id(UUID.randomUUID())
            .school(school)
            .domainName(domainName)
            .isPrimary(false)
            .isActive(true)
            .build();
        
        SchoolDomain saved = schoolDomainRepository.save(domain);
        log.info("Domain added successfully: {}", domainName);
        
        return saved;
    }
    
    /**
     * Get domains for a school
     */
    @Transactional(readOnly = true)
    public List<SchoolDomainDTO> getSchoolDomains(UUID schoolId) {
        return schoolDomainRepository.findBySchoolId(schoolId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get primary domain for school
     */
    @Transactional(readOnly = true)
    public SchoolDomain getPrimaryDomain(UUID schoolId) {
        return schoolDomainRepository.findBySchoolIdAndIsPrimaryTrue(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("Primary domain not found"));
    }
    
    /**
     * Deactivate domain
     */
    public SchoolDomain deactivateDomain(UUID domainId) {
        log.info("Deactivating domain: {}", domainId);
        
        SchoolDomain domain = schoolDomainRepository.findById(domainId)
            .orElseThrow(() -> new ResourceNotFoundException("Domain not found"));
        
        domain.setIsActive(false);
        SchoolDomain updated = schoolDomainRepository.save(domain);
        
        log.info("Domain deactivated: {}", domainId);
        
        return updated;
    }
    
    /**
     * Activate domain
     */
    public SchoolDomain activateDomain(UUID domainId) {
        log.info("Activating domain: {}", domainId);
        
        SchoolDomain domain = schoolDomainRepository.findById(domainId)
            .orElseThrow(() -> new ResourceNotFoundException("Domain not found"));
        
        domain.setIsActive(true);
        SchoolDomain updated = schoolDomainRepository.save(domain);
        
        log.info("Domain activated: {}", domainId);
        
        return updated;
    }
    
    /**
     * Map SchoolDomain to DTO
     */
    private SchoolDomainDTO mapToDTO(SchoolDomain domain) {
        return SchoolDomainDTO.builder()
            .domainId(domain.getId())
            .domainName(domain.getDomainName())
            .isPrimary(domain.getIsPrimary())
            .isActive(domain.getIsActive())
            .createdAt(domain.getCreatedAt())
            .build();
    }
}
