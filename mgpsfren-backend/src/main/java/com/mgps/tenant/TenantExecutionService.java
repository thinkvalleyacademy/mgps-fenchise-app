package com.mgps.tenant;

import com.mgps.school.entity.School;
import com.mgps.school.service.DatabaseProvisioningService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TenantExecutionService {

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
        return withTenant(school.getId().toString(), action);
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
        String previousTenant = TenantContext.getTenant();
        try {
            TenantContext.clear();
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setTenant(tenantId);
            }
            return action.get();
        } finally {
            TenantContext.clear();
            if (previousTenant != null && !previousTenant.isBlank()) {
                TenantContext.setTenant(previousTenant);
            }
        }
    }
}
