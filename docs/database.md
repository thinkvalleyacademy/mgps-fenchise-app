# Database Guide

## Multi-Tenant Configuration
The project uses `RoutingDataSource` to switch between the Master database and Tenant databases.

### Flyway Migrations
Migrations are stored in `src/main/resources/db/migration/`.
- `V1__...`: Master schema setup.
- `V2__...`: Tenant schema baseline.
- Subsequent versions add features to either Master or Tenant schemas.

## Data Isolation
- **Strict Isolation**: No cross-tenant queries are allowed.
- **Tenant Context**: The `com.mgps.tenant.TenantContext` thread-local variable ensures the correct schema is used.

## Relationships
- Use `UUID` for primary keys to ensure global uniqueness and easier data portability.
- Use Soft Deletes (`is_deleted` column) for critical data like Students and Staff.

## Common Tables
### Master
- `schools`: Tenant registry.
- `subscription_plans`: Plan details.
- `users`: Super admins.

### Tenant
- `academic_years`: Session management.
- `academic_classes`: Grade levels.
- `students`: Enrollment data.
- `staff`: Employee profiles.
- `fee_categories`: Financial configuration.
