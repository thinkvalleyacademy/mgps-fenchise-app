# Architecture Overview

## Multi-Tenant Strategy
MGPS uses a **Database-per-Tenant** (or Schema-per-Tenant) strategy to ensure maximum data isolation between different franchise schools.

### Master Database (`mgps_master`)
The master database serves as the control plane for the entire SaaS platform. It contains:
- **Schools Registry**: List of all schools, their status, and configuration.
- **Subscription Plans**: Definitions of available plans and their limits.
- **Domain Mappings**: Mapping of subdomains/domains to specific tenant databases.
- **Global Users**: Super Admins and high-level platform operators.

### Tenant Databases
Each school is provisioned with its own isolated database or schema. This database contains all school-specific data:
- Students, Staff, and Parents
- Academic Years, Classes, Sections, and Subjects
- Attendance, Fees, Exams, and Results
- Timetables and Communications

## Tech Stack
- **Backend**: Spring Boot 3.x, Java 21, Spring Security (JWT), Spring Data JPA.
- **Frontend**: React 18, TypeScript, Vite, Vanilla CSS (with some Tailwind utility patterns).
- **Persistence**: PostgreSQL 15+.
- **Communication**: REST APIs (JSON).

## Request Flow
1. **Frontend** sends a request with a `X-Tenant-ID` header or based on the subdomain.
2. **Backend Interceptor** (`TenantResolutionFilter`) extracts the tenant identifier.
3. **TenantContext** stores the identifier for the current thread.
4. **RoutingDataSource** (Hibernate/JPA) selects the correct database connection based on `TenantContext`.
5. **Business Logic** executes against the isolated tenant data.
