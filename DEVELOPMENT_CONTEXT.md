# MGPS Franchise School Management System - Development Context

## 📋 Project Overview
**MGPS** is a multi-tenant SaaS platform for managing multiple schools under a franchise model. Each school operates independently with its own database, while a central Super Admin manages all franchises.

**Tech Stack:**
- **Backend**: Java 21, Spring Boot 3.x, Spring Security, JPA/Hibernate
- **Frontend**: React.js, Tailwind CSS, React Router, Zustand/Redux
- **Database**: PostgreSQL (Database-per-Tenant strategy)
- **Infrastructure**: Docker Compose, NGINX, GitHub Actions

---

## 🏗️ Architecture Overview

### Multi-Tenant Model
- **Master Database**: Central management of schools, subscriptions, users, domain mappings
- **Tenant Database**: Each school has isolated database/schema containing students, staff, fees, exams, etc.
- **Request Flow**: NGINX → Backend (identifies tenant via subdomain/header) → Routes to tenant database

### Key Stakeholders
1. **Super Admin** - Platform operator, onboard schools, global analytics
2. **School Admin** - Manage school operations, users, academic config
3. **Staff Roles** - Teachers, Principal, Accountant, Receptionist, etc.
4. **Students & Parents** - End users for academic features

---

## 📦 Folder Structure

```
mgps-fenchise-app/
├── DEVELOPMENT_CONTEXT.md          (This file - AI coder reference)
├── MODULE_TRACKING.md               (Development progress tracker)
├── docker-files/postgres/docker-compose.yml               (PostgreSQL setup)
├── mgpsfren-backend/                (Spring Boot application)
│   ├── MGPS-requirement-doc.md      (Full requirements)
│   └── src/
│       ├── main/java/com/mgps/
│       │   ├── auth/                (JWT, authentication)
│       │   ├── tenant/              (Multi-tenant logic)
│       │   ├── school/              (School onboarding)
│       │   ├── user/                (User management)
│       │   ├── academic/            (Classes, subjects, streams)
│       │   ├── timetable/           (Scheduling)
│       │   ├── student/             (Student management)
│       │   ├── staff/               (Staff management)
│       │   ├── fee/                 (Fee management)
│       │   ├── examination/         (Exams, marks, results)
│       │   ├── attendance/          (Attendance tracking)
│       │   ├── communication/       (Notifications, messaging)
│       │   └── common/              (Shared utilities, exceptions)
│       └── resources/
│           ├── application.yml      (Spring config)
│           └── db/migration/        (Flyway migrations)
└── mgpsfren-frontend/               (React application)
    └── src/
        ├── pages/
        ├── components/
        └── services/
```

---

## 🎯 Development Phases

### Phase 1 - MVP (HIGH PRIORITY)
- [x] Project initialization
- [ ] Multi-tenant setup & routing
- [ ] School onboarding module
- [ ] Authentication & JWT
- [ ] User management & RBAC
- [ ] Student management
- [ ] Timetable management
- [ ] Basic academic structure

### Phase 2 - MEDIUM PRIORITY
- [ ] Fee management
- [ ] Attendance module
- [ ] Examination module
- [ ] Communication & notifications
- [ ] Reporting

### Phase 3 - ADVANCED
- [ ] Transport management
- [ ] Library management
- [ ] Hostel management
- [ ] Inventory management
- [ ] Mobile apps
- [ ] AI analytics

---

## 🗄️ Database Setup

### PostgreSQL Configuration
- **Host**: localhost
- **Port**: 5432
- **Admin User**: postgres
- **Admin Password**: postgres123
- **Master Database**: mgps_master

**Starting the database:**
```bash
docker-compose up -d postgres
```

### Master Database Schema
```sql
-- Schools registry
CREATE TABLE schools (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  domain VARCHAR(255) UNIQUE,
  database_name VARCHAR(255),
  database_schema VARCHAR(255),
  subscription_plan VARCHAR(50),
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Subscription plans
CREATE TABLE subscription_plans (
  id UUID PRIMARY KEY,
  name VARCHAR(100),
  features JSONB,
  price DECIMAL(10, 2)
);

-- Domain mappings
CREATE TABLE domain_mappings (
  id UUID PRIMARY KEY,
  domain VARCHAR(255) UNIQUE,
  school_id UUID REFERENCES schools(id),
  tenant_database VARCHAR(255)
);
```

### Tenant Database Schema
Each school database will contain:
- students, staff, users, attendance, fees, exams, timetables, etc.

---

## 🔐 Authentication Flow

1. User logs in with school domain + credentials
2. Backend identifies school via subdomain/header
3. JWT token generated with tenant context
4. Token includes: userId, schoolId, roles, permissions
5. Subsequent requests use token to route to correct tenant database

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose
- Node.js 18+
- PostgreSQL 15+ (via Docker)

### Backend Setup
```bash
cd mgpsfren-backend
# Configure application.yml with master DB connection
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd mgpsfren-frontend
npm install
npm start
```

### Database
```bash
docker-compose up -d postgres
# Run Flyway migrations automatically on app startup
```

---

## 📝 Coding Standards

- **Java**: Follow Spring Boot best practices, use DTOs, service layer pattern
- **Database**: Use Flyway for migrations, optimize queries for multi-tenant
- **API**: RESTful with versioning (/api/v1/...)
- **Error Handling**: Consistent error responses with error codes
- **Logging**: Use SLF4J with contextual tenant info
- **Testing**: Unit and integration tests for critical modules

---

## 🔗 Important References

- **Requirements**: `mgpsfren-backend/MGPS-requirement-doc.md`
- **Progress**: `MODULE_TRACKING.md` (update after each module)
- **Docker**: `docker-compose.yml`

---

## 💡 Next Steps for AI Coder

1. Check `MODULE_TRACKING.md` for current development status
2. Read the relevant requirement section in `MGPS-requirement-doc.md`
3. Check existing code in the module folder
4. Implement feature following the module specification
5. Update `MODULE_TRACKING.md` when done
6. Run tests: `./mvnw test`

---

**Last Updated**: 28 May 2026
**Status**: Project Initialization Phase
