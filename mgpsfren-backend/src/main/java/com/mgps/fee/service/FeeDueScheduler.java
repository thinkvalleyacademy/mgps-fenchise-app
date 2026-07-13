package com.mgps.fee.service;

import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolStatus;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.tenant.TenantExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FeeDueScheduler {

    private static final Logger log = LoggerFactory.getLogger(FeeDueScheduler.class);

    private final SchoolRepository schoolRepository;
    private final TenantExecutionService tenantExecutionService;
    private final FeeService feeService;

    public FeeDueScheduler(SchoolRepository schoolRepository,
                           TenantExecutionService tenantExecutionService,
                           FeeService feeService) {
        this.schoolRepository = schoolRepository;
        this.tenantExecutionService = tenantExecutionService;
        this.feeService = feeService;
    }

    @Scheduled(cron = "${fees.due-refresh.cron:0 5 0 * * *}")
    public void refreshMonthlyDueStatuses() {
        tenantExecutionService.inMaster(() -> schoolRepository.findAllWithSubscriptionPlan().stream()
                .filter(school -> school.getStatus() == SchoolStatus.ACTIVE)
                .forEach(this::refreshSchoolFees));
    }

    private void refreshSchoolFees(School school) {
        try {
            Integer updated = tenantExecutionService.inTenant(school, feeService::refreshDueStatuses);
            log.info("Refreshed fee due statuses for school {} ({}) updated={}",
                    school.getName(), school.getId(), updated);
        } catch (RuntimeException ex) {
            log.error("Failed to refresh fee due statuses for school {} ({})",
                    school.getName(), school.getId(), ex);
        }
    }
}
