package com.mgps.tenant;

import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.service.DatabaseProvisioningService;
import com.mgps.school.service.TenantSchoolDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TenantDataSourceInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceInitializer.class);

    private final SchoolRepository schoolRepository;
    private final DatabaseProvisioningService databaseProvisioningService;
    private final RoutingDataSource routingDataSource;
    private final TenantSchoolDataService tenantSchoolDataService;

    public TenantDataSourceInitializer(SchoolRepository schoolRepository,
                                       DatabaseProvisioningService databaseProvisioningService,
                                       RoutingDataSource routingDataSource,
                                       TenantSchoolDataService tenantSchoolDataService) {
        this.schoolRepository = schoolRepository;
        this.databaseProvisioningService = databaseProvisioningService;
        this.routingDataSource = routingDataSource;
        this.tenantSchoolDataService = tenantSchoolDataService;
    }

    @Override
    public void run(ApplicationArguments args) {
        TenantContext.clear();
        schoolRepository.findAllWithSubscriptionPlan().forEach(school -> {
            try {
                databaseProvisioningService.migrateTenantSchema(school.getDatabaseName());
                databaseProvisioningService.registerDataSource(routingDataSource, school);
                tenantSchoolDataService.synchronizeSnapshot(school);
                tenantSchoolDataService.seedDefaultAcademicSetup(school);
            } catch (RuntimeException ex) {
                log.error("Failed to restore datasource for school {} ({})",
                    school.getName(), school.getId(), ex);
            }
        });
        log.info("Restored {} tenant datasource aliases", routingDataSource.getTenantDataSourceCount());
    }
}
