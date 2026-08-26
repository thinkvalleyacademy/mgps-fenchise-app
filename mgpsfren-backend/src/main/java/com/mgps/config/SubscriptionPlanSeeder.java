package com.mgps.config;

import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.repository.SubscriptionPlanRepository;
import com.mgps.tenant.TenantExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ensures the master database always has the default FREE/BASIC subscription
 * plans, since master Flyway is disabled and the seed rows in
 * V2__create_school_tables.sql never execute.
 */
@Component
public class SubscriptionPlanSeeder {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanSeeder.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final TenantExecutionService tenantExecutionService;

    public SubscriptionPlanSeeder(SubscriptionPlanRepository subscriptionPlanRepository,
                                   TenantExecutionService tenantExecutionService) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.tenantExecutionService = tenantExecutionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultPlans() {
        tenantExecutionService.inMaster(() -> {
            upsertPlan("FREE", "Free onboarding plan", 100, 10, 20,
                new BigDecimal("0.00"), "Free onboarding, core modules, basic reports");
            upsertPlan("BASIC", "Basic Plan", 500, 50, 80,
                new BigDecimal("4999.00"), "All core modules");
        });
        log.info("Verified default subscription plans (FREE, BASIC) exist in master database");
    }

    private void upsertPlan(String planName, String description, int maxStudents, int maxStaff, int maxUsers,
                             BigDecimal monthlyPrice, String features) {
        if (subscriptionPlanRepository.existsByPlanName(planName)) {
            return;
        }
        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(UUID.randomUUID())
            .planName(planName)
            .description(description)
            .maxStudents(maxStudents)
            .maxStaff(maxStaff)
            .maxUsers(maxUsers)
            .monthlyPrice(monthlyPrice)
            .features(features)
            .isActive(true)
            .build();
        subscriptionPlanRepository.save(plan);
        log.info("Seeded default subscription plan: {}", planName);
    }
}
