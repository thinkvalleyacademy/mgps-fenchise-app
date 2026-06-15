package com.mgps.tenant;

import com.mgps.school.entity.School;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.service.DatabaseProvisioningService;
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

    @Test
    void shouldRestoreExistingSchoolDatasourcesAtStartup() {
        School school = School.builder()
            .id(UUID.randomUUID())
            .name("Existing School")
            .databaseName("existing_school")
            .build();
        when(schoolRepository.findAll()).thenReturn(List.of(school));

        TenantDataSourceInitializer initializer = new TenantDataSourceInitializer(
            schoolRepository, databaseProvisioningService, routingDataSource);

        initializer.run(null);

        verify(databaseProvisioningService).registerDataSource(routingDataSource, school);
    }
}
