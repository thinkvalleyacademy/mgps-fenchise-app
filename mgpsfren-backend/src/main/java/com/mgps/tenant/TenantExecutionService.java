package com.mgps.tenant;

import com.mgps.school.entity.School;
import com.mgps.school.service.DatabaseProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TenantExecutionService {

    private static final Logger log = LoggerFactory.getLogger(TenantExecutionService.class);

    private final RoutingDataSource routingDataSource;
    private final DatabaseProvisioningService databaseProvisioningService;

    public TenantExecutionService(RoutingDataSource routingDataSource,
                                  DatabaseProvisioningService databaseProvisioningService) {
        this.routingDataSource = routingDataSource;
        this.databaseProvisioningService = databaseProvisioningService;
    }

    public <T> T inMaster(Supplier<T> action) {
        return withTenant(null, action);
    }

    public void inMaster(Runnable action) {
        withTenant(null, () -> {
            action.run();
            return null;
        });
    }

    public <T> T inTenant(School school, Supplier<T> action) {
        ensureRegistered(school);
        return withTenant(school.getId().toString(), school.getDatabaseName(), action);
    }

    public void inTenant(School school, Runnable action) {
        inTenant(school, () -> {
            action.run();
            return null;
        });
    }

    private void ensureRegistered(School school) {
        String tenantId = school.getId().toString();
        if (!routingDataSource.hasTenantDataSource(tenantId)) {
            databaseProvisioningService.registerDataSource(routingDataSource, school);
        }
    }

    private <T> T withTenant(String tenantId, Supplier<T> action) {
        return withTenant(tenantId, null, action);
    }

    private <T> T withTenant(String tenantId, String databaseName, Supplier<T> action) {
        String previousTenant = TenantContext.getTenant();
        try {
            TenantContext.clear();
            if (tenantId != null && !tenantId.isBlank()) {
                log.info("TenantExecutionService switching context | targetTenant={} targetDatabase={} previousTenant={} action=tenant",
                    tenantId, databaseName, previousTenant);
                TenantContext.setTenant(tenantId);
            } else {
                log.info("TenantExecutionService switching context | targetTenant=MASTER targetDatabase=MASTER previousTenant={} action=master",
                    previousTenant);
            }
            log.info("TenantExecutionService current context | tenant={} targetDatabase={}", TenantContext.getTenant(),
                databaseName != null ? databaseName : "MASTER");
            return action.get();
        } finally {
            TenantContext.clear();
            if (previousTenant != null && !previousTenant.isBlank()) {
                TenantContext.setTenant(previousTenant);
            }
            log.info("TenantExecutionService restored context | tenant={}", TenantContext.getTenant());
        }
    }
}
