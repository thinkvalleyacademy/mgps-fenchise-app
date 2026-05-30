package com.mgps.school.repository;

import com.mgps.school.entity.SchoolDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SchoolDomain entity
 * Provides database access for domain mapping operations
 */
@Repository
public interface SchoolDomainRepository extends JpaRepository<SchoolDomain, UUID> {
    
    /**
     * Check if domain exists
     */
    boolean existsByDomainName(String domainName);
    
    /**
     * Find domain by name
     */
    Optional<SchoolDomain> findByDomainName(String domainName);
    
    /**
     * Find all domains for a school
     */
    List<SchoolDomain> findBySchoolId(UUID schoolId);
    
    /**
     * Find primary domain for a school
     */
    Optional<SchoolDomain> findBySchoolIdAndIsPrimaryTrue(UUID schoolId);
    
    /**
     * Find active domains for a school
     */
    List<SchoolDomain> findBySchoolIdAndIsActiveTrue(UUID schoolId);
}
