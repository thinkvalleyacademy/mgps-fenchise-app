package com.mgps.school.repository;

import com.mgps.school.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SubscriptionPlan entity
 * Provides database access for subscription plan operations
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    
    /**
     * Find plan by name
     */
    Optional<SubscriptionPlan> findByPlanName(String planName);
    
    /**
     * Check if plan exists by name
     */
    boolean existsByPlanName(String planName);
}
