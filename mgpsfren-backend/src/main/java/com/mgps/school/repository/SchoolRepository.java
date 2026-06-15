package com.mgps.school.repository;

import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for School entity
 * Provides database access for school operations
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, UUID> {

    @EntityGraph(attributePaths = "subscriptionPlan")
    @Query("select s from School s where s.id = :id")
    Optional<School> findByIdWithSubscriptionPlan(@Param("id") UUID id);

    @EntityGraph(attributePaths = "subscriptionPlan")
    @Query("select s from School s")
    List<School> findAllWithSubscriptionPlan();

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

    Optional<School> findByDatabaseNameIgnoreCase(String databaseName);
    
    /**
     * Find schools by status
     */
    Page<School> findByStatus(SchoolStatus status, Pageable pageable);
    
    /**
     * Find active schools
     */
    Page<School> findByStatusAndUpdatedAtIsNotNull(SchoolStatus status, Pageable pageable);
}
