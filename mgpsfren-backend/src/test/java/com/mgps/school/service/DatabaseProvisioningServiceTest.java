package com.mgps.school.service;

import com.mgps.school.entity.School;
import com.mgps.tenant.DataSourceRegistry;
import com.mgps.tenant.RoutingDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseProvisioningService Tests")
class DatabaseProvisioningServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSourceRegistry dataSourceRegistry;

    @Mock
    private RoutingDataSource routingDataSource;

    @InjectMocks
    private DatabaseProvisioningService databaseProvisioningService;

    @Test
    @DisplayName("Should use tenant-only migrations for tenant database bootstrap")
    void shouldUseTenantOnlyMigrationLocation() {
        assertThat(DatabaseProvisioningService.getTenantMigrationLocations())
            .containsExactly("classpath:db/migration/tenant");
    }

    @Test
    @DisplayName("Should register tenant datasource under the school slug and school id")
    void shouldRegisterTenantDatasourceForSchoolSlugAndId() {
        UUID schoolId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        School school = School.builder()
            .id(schoolId)
            .name("Test School")
            .databaseName("postgres")
            .build();

        ReflectionTestUtils.setField(databaseProvisioningService, "dbHost", "localhost");
        ReflectionTestUtils.setField(databaseProvisioningService, "dbPort", "5432");
        ReflectionTestUtils.setField(databaseProvisioningService, "dbUsername", "postgres");
        ReflectionTestUtils.setField(databaseProvisioningService, "dbPassword", "postgres123");

        databaseProvisioningService.registerDataSource(routingDataSource, school);

        verify(routingDataSource).registerTenantDataSource(eq("test-school"), any());
        verify(routingDataSource).registerTenantDataSource(eq(schoolId.toString()), any());
        verify(dataSourceRegistry).registerDataSource(eq(schoolId.toString()), any());
    }
}
