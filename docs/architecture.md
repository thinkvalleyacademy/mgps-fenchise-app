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
1. **Frontend** sends a request with a `Authorization: Bearer <access token>` header.
2. **`TenantResolutionFilter`** runs first and asks `TenantIdentifier` to resolve the tenant.
3. **TenantContext** stores the identifier for the current thread, and is always cleared
   when the request completes so nothing leaks to the next request on that thread.
4. **RoutingDataSource** selects the correct database connection based on `TenantContext`,
   and refuses to route to a school that is not `ACTIVE`.
5. **Business Logic** executes against the isolated tenant data.

### Tenant resolution rules
Tenant identity is derived from the authenticated principal, never from
client-controlled routing hints. Precisely:

| Request | Tenant source |
| --- | --- |
| Control-plane paths (`/auth/login`, `/auth/refresh`, `/setup/*`, `/schools*`, `/subscription-plans*`) | Always master |
| Valid access token, normal role | The signed `tenantId` claim **only** |
| Valid access token, `SUPER_ADMIN` | Master, or one tenant if explicitly named via `X-Tenant-Id` |
| Invalid / expired / refresh token | No tenant (request is rejected by the security chain) |
| Anonymous, allow-listed endpoint (`/enquiries`) | `X-Tenant-Id`, else subdomain |
| Anything else | No tenant |

`X-Tenant-Id` and the subdomain are **not** trusted for authenticated requests.
A non-superadmin token carrying no tenant claim fails closed with HTTP 403
rather than falling back to the master database.

### Authorising a caller-supplied `schoolId`
Datasource routing is not by itself an authorisation check. Any code path that
resolves a school from master and then switches context (`TenantExecutionService.inTenant`)
must first call `TenantGuard.assertSchoolAccessible(schoolId)`, which permits
SUPER_ADMIN platform-wide and every other principal only its own school.
