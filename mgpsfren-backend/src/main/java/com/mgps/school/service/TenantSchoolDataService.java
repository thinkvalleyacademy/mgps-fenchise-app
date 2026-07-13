package com.mgps.school.service;

import com.mgps.audit.ActivityLogService;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.tenant.TenantExecutionService;
import com.mgps.user.dto.UserDtos.UserProfile;
import com.mgps.user.repository.AppUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TenantSchoolDataService {

    private final TenantExecutionService tenantExecutionService;
    private final JdbcTemplate jdbcTemplate;
    private final AppUserRepository appUserRepository;
    private final ActivityLogService activityLogService;

    public TenantSchoolDataService(TenantExecutionService tenantExecutionService,
                                   JdbcTemplate jdbcTemplate,
                                   AppUserRepository appUserRepository,
                                   ActivityLogService activityLogService) {
        this.tenantExecutionService = tenantExecutionService;
        this.jdbcTemplate = jdbcTemplate;
        this.appUserRepository = appUserRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void synchronizeSnapshot(School school) {
        tenantExecutionService.inTenant(school, () -> {
            ensureSnapshotTable();
            SubscriptionPlan plan = school.getSubscriptionPlan();
            jdbcTemplate.update("""
                INSERT INTO tenant_school_snapshot (
                    school_id, school_name, admin_email, admin_phone, city, state, postal_code,
                    status, database_name, subscription_plan_id, subscription_plan_name,
                    max_students, max_staff, max_users, monthly_price, logo_url, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (school_id) DO UPDATE SET
                    school_name = EXCLUDED.school_name,
                    admin_email = EXCLUDED.admin_email,
                    admin_phone = EXCLUDED.admin_phone,
                    city = EXCLUDED.city,
                    state = EXCLUDED.state,
                    postal_code = EXCLUDED.postal_code,
                    status = EXCLUDED.status,
                    database_name = EXCLUDED.database_name,
                    subscription_plan_id = EXCLUDED.subscription_plan_id,
                    subscription_plan_name = EXCLUDED.subscription_plan_name,
                    max_students = EXCLUDED.max_students,
                    max_staff = EXCLUDED.max_staff,
                    max_users = EXCLUDED.max_users,
                    monthly_price = EXCLUDED.monthly_price,
                    logo_url = EXCLUDED.logo_url,
                    updated_at = CURRENT_TIMESTAMP
                """,
                school.getId(), school.getName(), school.getAdminEmail(), school.getAdminPhone(),
                school.getCity(), school.getState(), school.getPostalCode(), school.getStatus().name(),
                school.getDatabaseName(), plan != null ? plan.getId() : null,
                plan != null ? plan.getPlanName() : null, plan != null ? plan.getMaxStudents() : null,
                plan != null ? plan.getMaxStaff() : null, plan != null ? plan.getMaxUsers() : null,
                plan != null ? plan.getMonthlyPrice() : null, school.getLogoUrl());
        });
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TenantSchoolOverview getOverview(School school) {
        synchronizeSnapshot(school);
        return tenantExecutionService.inTenant(school, () -> {
            TenantSchoolSnapshot snapshot = jdbcTemplate.queryForObject("""
                SELECT school_id, school_name, admin_email, admin_phone, city, state, postal_code,
                       status, database_name, subscription_plan_id, subscription_plan_name,
                       max_students, max_staff, max_users, monthly_price, logo_url
                FROM tenant_school_snapshot
                WHERE school_id = ?
                """, (rs, rowNum) -> new TenantSchoolSnapshot(
                    rs.getObject("school_id", UUID.class),
                    rs.getString("school_name"),
                    rs.getString("admin_email"),
                    rs.getString("admin_phone"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("postal_code"),
                    rs.getString("status"),
                    rs.getString("database_name"),
                    rs.getObject("subscription_plan_id", UUID.class),
                    rs.getString("subscription_plan_name"),
                    (Integer) rs.getObject("max_students"),
                    (Integer) rs.getObject("max_staff"),
                    (Integer) rs.getObject("max_users"),
                    rs.getBigDecimal("monthly_price"),
                    rs.getString("logo_url")
                ), school.getId());

            List<UserProfile> users = appUserRepository.findBySchoolId(school.getId(), PageRequest.of(0, 200))
                .map(user -> {
                    UserProfile profile = new UserProfile();
                    profile.setUserId(user.getId());
                    profile.setSchoolId(user.getSchoolId());
                    profile.setFirstName(user.getFirstName());
                    profile.setLastName(user.getLastName());
                    profile.setEmail(user.getEmail());
                    profile.setPhone(user.getPhone());
                    profile.setRole(user.getRole());
                    profile.setStatus(user.getStatus());
                    profile.setLastLoginAt(user.getLastLoginAt());
                    profile.setCreatedAt(user.getCreatedAt());
                    profile.setUpdatedAt(user.getUpdatedAt());
                    return profile;
                })
                .getContent();
            return new TenantSchoolOverview(snapshot, users, activityLogService.findRecent(100));
        });
    }

    private void ensureSnapshotTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tenant_school_snapshot (
                school_id UUID PRIMARY KEY,
                school_name VARCHAR(255) NOT NULL,
                admin_email VARCHAR(255),
                admin_phone VARCHAR(20),
                city VARCHAR(100),
                state VARCHAR(100),
                postal_code VARCHAR(20),
                status VARCHAR(50),
                database_name VARCHAR(100),
                subscription_plan_id UUID,
                subscription_plan_name VARCHAR(100),
                max_students INTEGER,
                max_staff INTEGER,
                max_users INTEGER,
                monthly_price DECIMAL(10, 2),
                logo_url TEXT,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """);
    }

    public record TenantSchoolOverview(
        TenantSchoolSnapshot school,
        List<UserProfile> users,
        List<ActivityLogService.ActivityLogRecord> activity
    ) {
    }

    public record TenantSchoolSnapshot(
        UUID schoolId,
        String schoolName,
        String adminEmail,
        String adminPhone,
        String city,
        String state,
        String postalCode,
        String status,
        String databaseName,
        UUID subscriptionPlanId,
        String subscriptionPlanName,
        Integer maxStudents,
        Integer maxStaff,
        Integer maxUsers,
        java.math.BigDecimal monthlyPrice,
        String logoUrl
    ) {
    }
}
