# MGPS Franchise School Management System - Quick Start Guide

## 📌 What's Been Set Up

You now have a complete development foundation with:

1. **DEVELOPMENT_CONTEXT.md** - Comprehensive project overview (START HERE!)
2. **MODULE_TRACKING.md** - Track progress on each module as you develop
3. **docker-compose.yml** - PostgreSQL + PGAdmin setup (ready to run)
4. **init-db.sql** - Master database schema with sample data
5. **Memory files** - AI-coder context for future sessions

---

## 🚀 Getting Started

### Step 1: Start PostgreSQL Database
```bash
cd /Users/mukeshkumar/Desktop/TVA-application-devlopment/mgps-fenchise-app
docker-compose up -d postgres
```

**Verify it's running:**
```bash
docker-compose logs postgres
# Look for: "database system is ready to accept connections"
```

**Check containers:**
```bash
docker-compose ps
```

---

### Step 2: Access Database Admin Interface (Optional)
- **URL**: http://localhost:5050
- **Email**: admin@mgps.local
- **Password**: admin123

Add PostgreSQL server in PGAdmin:
- Host: `postgres` (or `localhost` from host machine)
- Port: `5432`
- Username: `postgres`
- Password: `postgres123`

---

### Step 3: Backend Project Setup

```bash
cd mgpsfren-backend

# Create Spring Boot project structure (if not already done)
# You'll need to initialize Maven project with:
# - pom.xml with Spring Boot 3.x, Spring Security, JPA, PostgreSQL driver
# - application.yml with master DB connection
# - Maven modules for different components

# Install dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

**Backend will run on**: http://localhost:8080

---

### Step 4: Frontend Setup (When Ready)

```bash
cd mgpsfren-frontend

# Install dependencies
npm install

# Start development server
npm start
```

**Frontend will run on**: http://localhost:3000

---

## 📁 Project Structure Created

```
mgps-fenchise-app/
├── README.md                          ← This file
├── DEVELOPMENT_CONTEXT.md             ← Full architecture & module overview
├── MODULE_TRACKING.md                 ← Progress tracker (update as you code)
├── docker-compose.yml                 ← PostgreSQL + PGAdmin
├── init-db.sql                        ← Database schema initialization
│
├── mgpsfren-backend/                  ← Spring Boot application
│   ├── pom.xml                        ← Maven configuration (create)
│   ├── src/main/
│   │   ├── java/com/mgps/            ← Java source code
│   │   │   ├── config/               ← Configuration classes
│   │   │   ├── auth/                 ← Authentication & JWT
│   │   │   ├── tenant/               ← Multi-tenant routing
│   │   │   ├── school/               ← School onboarding
│   │   │   ├── user/                 ← User management & RBAC
│   │   │   ├── academic/             ← Academic structure
│   │   │   ├── student/              ← Student management
│   │   │   └── common/               ← Shared utilities
│   │   └── resources/
│   │       ├── application.yml        ← Spring Boot config (create)
│   │       └── db/migration/          ← Flyway migrations
│   └── src/test/
│
└── mgpsfren-frontend/                 ← React application
    ├── package.json                   ← Create with: npm create react-app
    ├── src/
    │   ├── pages/
    │   ├── components/
    │   ├── services/
    │   └── App.js
    └── public/
```

---

## 🔑 Database Credentials

| Component | Username | Password | Host | Port |
|-----------|----------|----------|------|------|
| PostgreSQL | postgres | postgres123 | localhost | 5432 |
| PGAdmin | admin@mgps.local | admin123 | localhost | 5050 |

---

## 🗄️ Database Overview

### Master Database: `mgps_master`
Manages the multi-tenant system:
- **schools** - Registered schools
- **subscription_plans** - Available plans
- **domain_mappings** - School domain → database mapping
- **master_users** - Super admins and school admins
- **audit_logs** - System audit trail

### Tenant Databases (Per School)
Each school gets its own database with tables:
- students, staff, users, attendance, fees, exams, timetables, etc.

---

## 📋 For AI Coders: Before You Start Coding

### 1. Read the Context
```
👉 DEVELOPMENT_CONTEXT.md - 5 min read, full project overview
```

### 2. Check Progress
```
👉 MODULE_TRACKING.md - See what's done, what's next
```

### 3. Check Repo Memory
Your workspace memory files are at:
- `/memories/repo/MGPS_PROJECT_STRUCTURE.md`
- `/memories/repo/DEVELOPMENT_PHASE.md`

### 4. Before Coding a Module
- Read the module section in `MGPS-requirement-doc.md`
- Check `MODULE_TRACKING.md` for dependencies
- Create the module folder in appropriate location
- Follow coding standards mentioned in DEVELOPMENT_CONTEXT.md

### 5. After Completing a Module
- Update `MODULE_TRACKING.md` (change ⬜ to 🟢)
- Write unit tests (target 80% coverage)
- Run: `./mvnw clean test`
- Commit with message: `[DONE] Module Name`

---

## 🛠️ Common Commands

### Docker
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f postgres

# Access PostgreSQL
docker exec -it mgps-postgres psql -U postgres -d mgps_master

# Remove volumes (CAREFUL!)
docker-compose down -v
```

### Backend
```bash
# Clean build
./mvnw clean install

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=ClassName

# Skip tests during build
./mvnw clean install -DskipTests
```

### Database
```bash
# Connect to master database
docker exec -it mgps-postgres psql -U postgres -d mgps_master

# View all tables
\dt

# View specific table structure
\d table_name

# Exit psql
\q
```

---

## 📊 Development Phases

### ✅ Phase 1 - MVP (High Priority)
- [ ] Backend Project Setup
- [ ] Multi-tenant Architecture
- [x] School Onboarding Module
- [ ] Authentication & User Management
- [ ] Student Management
- [ ] Timetable Management

### 🟡 Phase 2 - Medium Priority
- [ ] Fee Management
- [ ] Attendance Module
- [ ] Examination Module
- [ ] Communication Module

### 🔵 Phase 3 - Advanced
- [ ] Transport Management
- [ ] Library Management
- [ ] Analytics & Reporting

---

## 🔗 Important Links

| Document | Purpose |
|----------|---------|
| [DEVELOPMENT_CONTEXT.md](DEVELOPMENT_CONTEXT.md) | Full project architecture & overview |
| [MODULE_TRACKING.md](MODULE_TRACKING.md) | Development progress tracker |
| [mgpsfren-backend/MGPS-requirement-doc.md](mgpsfren-backend/MGPS-requirement-doc.md) | Detailed requirements |

---

## 💡 Tips for Development

1. **Multi-tenant routing**: Every API request must identify the tenant (via subdomain, header, or JWT token)
2. **Database migrations**: Use Flyway with versioned SQL files in `src/main/resources/db/migration/`
3. **Testing**: Write tests alongside implementation, not after
4. **Logging**: Always include tenant context in logs for easier debugging
5. **API versioning**: Use `/api/v1/` prefix in all endpoints
6. **Error handling**: Return consistent error responses with error codes

---

## ⚠️ Before Production

- [ ] Enable HTTPS/SSL
- [ ] Configure proper JWT secret (use environment variables)
- [ ] Set up CI/CD pipeline (GitHub Actions)
- [ ] Enable database backups
- [ ] Configure logging and monitoring
- [ ] Security audit (CORS, rate limiting, input validation)
- [ ] Load testing for multi-tenant performance

---

## 📞 Support

**For questions about:**
- **Architecture** → Read DEVELOPMENT_CONTEXT.md
- **Progress** → Check MODULE_TRACKING.md
- **Requirements** → See MGPS-requirement-doc.md
- **Database** → Check docker-compose.yml and init-db.sql
- **Setup** → Follow "Getting Started" section above

---

## 🎯 Next Steps

1. ✅ **Database running** → Run `docker-compose up -d postgres`
2. ⬜ **Backend initialized** → Create pom.xml and project structure
3. ⬜ **Multi-tenant framework** → Implement TenantContext & routing
4. ⬜ **School Onboarding API** → Create endpoints for school registration
5. ⬜ **Authentication** → Implement JWT-based auth

---

**Last Updated**: 28 May 2026  
**Status**: Ready for Development  
**Next Phase**: Backend Project Initialization
