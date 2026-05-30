package com.mgps.school.repository;

import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for School entity
 * Provides database access for school operations
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, UUID> {
    
    /**
     * Check if school exists by admin email
     */
    boolean existsByAdminEmail(String adminEmail);
    
    /**
     * Find school by admin email
     */
    Optional<School> findByAdminEmail(String adminEmail);
    
    /**
     * Find school by database name
     */
    Optional<School> findByDatabaseName(String databaseName);
    
    /**
     * Find schools by status
     */
    Page<School> findByStatus(SchoolStatus status, Pageable pageable);
    
    /**
     * Find active schools
     */
    Page<School> findByStatusAndUpdatedAtIsNotNull(SchoolStatus status, Pageable pageable);
}
