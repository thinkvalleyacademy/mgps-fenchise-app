package com.mgps.school.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.school.dto.SubscriptionPlanRequestDTO;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Subscription Plan Management
 */
@RestController
@RequestMapping("/subscription-plans")
public class SubscriptionPlanController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanController.class);

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    /**
     * Get all active subscription plans
     * GET /api/subscription-plans
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPlans() {
        log.info("Fetching all active subscription plans");
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll().stream()
                .filter(SubscriptionPlan::getIsActive)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(plans, "Subscription plans retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPlan(@RequestBody SubscriptionPlanRequestDTO dto) {
        log.info("Creating subscription plan: {}", dto.getPlanName());
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setMaxStudents(dto.getMaxStudents());
        plan.setMaxStaff(dto.getMaxStaff());
        plan.setMaxUsers(dto.getMaxUsers());
        plan.setMonthlyPrice(new BigDecimal(dto.getMonthlyPrice()));
        plan.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        plan.setId(UUID.randomUUID());
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedPlan, "Subscription plan created successfully"));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ApiResponse<?>> updatePlan(@PathVariable UUID planId, @RequestBody SubscriptionPlanRequestDTO dto) {
        log.info("Updating subscription plan: {}", planId);
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
            .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
        if (dto.getPlanName() != null) plan.setPlanName(dto.getPlanName());
        if (dto.getDescription() != null) plan.setDescription(dto.getDescription());
        if (dto.getMaxStudents() != null) plan.setMaxStudents(dto.getMaxStudents());
        if (dto.getMaxStaff() != null) plan.setMaxStaff(dto.getMaxStaff());
        if (dto.getMaxUsers() != null) plan.setMaxUsers(dto.getMaxUsers());
        if (dto.getMonthlyPrice() != null) plan.setMonthlyPrice(new BigDecimal(dto.getMonthlyPrice()));
        if (dto.getIsActive() != null) plan.setIsActive(dto.getIsActive());
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return ResponseEntity.ok(ApiResponse.success(updatedPlan, "Subscription plan updated successfully"));
    }
}
