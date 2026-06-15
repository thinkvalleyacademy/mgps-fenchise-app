package com.mgps.tenant;

import com.mgps.school.entity.School;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.service.DatabaseProvisioningService;
import com.mgps.school.service.TenantSchoolDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantDataSourceInitializerTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private DatabaseProvisioningService databaseProvisioningService;

    @Mock
    private RoutingDataSource routingDataSource;

    @Mock
    private TenantSchoolDataService tenantSchoolDataService;

    @Test
    void shouldRestoreExistingSchoolDatasourcesAtStartup() {
        School school = School.builder()
            .id(UUID.randomUUID())
            .name("Existing School")
            .databaseName("existing_school")
            .build();
        when(schoolRepository.findAllWithSubscriptionPlan()).thenReturn(List.of(school));

        TenantDataSourceInitializer initializer = new TenantDataSourceInitializer(
            schoolRepository, databaseProvisioningService, routingDataSource, tenantSchoolDataService);

        initializer.run(null);

        verify(databaseProvisioningService).migrateTenantSchema("existing_school");
        verify(databaseProvisioningService).registerDataSource(routingDataSource, school);
        verify(tenantSchoolDataService).synchronizeSnapshot(school);
    }
}
