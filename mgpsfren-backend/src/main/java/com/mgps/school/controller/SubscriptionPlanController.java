package com.mgps.school.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Subscription Plan Management
 */
@RestController
@RequestMapping("/subscription-plans")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
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
}
