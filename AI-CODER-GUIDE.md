# AI Coder Quick Reference

This is a quick reference guide for AI coders starting work on this project.

## 🎯 First Time Here?

### 5-Minute Orientation
1. Read: [DEVELOPMENT_CONTEXT.md](DEVELOPMENT_CONTEXT.md) (project architecture)
2. Check: [MODULE_TRACKING.md](MODULE_TRACKING.md) (what's done, what's next)
3. Run: `docker-compose up -d postgres` (start database)
4. Pick: A module from MODULE_TRACKING.md that's marked ⬜
5. Code: Follow the module specification in DEVELOPMENT_CONTEXT.md

### Key Files
| File | Purpose |
|------|---------|
| `DEVELOPMENT_CONTEXT.md` | **START HERE** - Full architecture, stack, database design |
| `MODULE_TRACKING.md` | Progress - see which modules are done/in-progress |
| `README.md` | Setup guide - how to run everything locally |
| `docker-compose.yml` | PostgreSQL + PGAdmin configuration |
| `mgpsfren-backend/MGPS-requirement-doc.md` | Detailed feature requirements |

## 📦 Current Status

- **Date**: 28 May 2026
- **Phase**: Project Initialization
- **Database**: ✅ Docker setup ready
- **Backend**: ⬜ Not started
- **Frontend**: ⬜ Not started

## 🗄️ Database Connection

```
Host: localhost
Port: 5432
Master Database: mgps_master
Username: postgres
Password: postgres123
```

**Start it**: `docker-compose up -d postgres`

## 🏗️ Architecture

- **Multi-tenant**: Each school has isolated database
- **Routing**: Identify tenant via subdomain (school1.smsapp.com) or header
- **Master DB**: Stores schools registry, domains, subscriptions, master users
- **Tenant DB**: Created per school on onboarding

## 📝 Coding Standards

- **Java**: Spring Boot 3.x, use service layer & DTOs
- **Database**: Flyway migrations (versioned SQL files)
- **API**: RESTful, `/api/v1/...` prefix, consistent error responses
- **Testing**: Unit tests in `src/test/`, target 80% coverage
- **Logging**: Always include tenant context

## 🚀 Quick Start

```bash
# 1. Start database
cd docker-files/postgres
docker-compose up -d postgres

# 2. Build backend
cd mgpsfren-backend
./mvnw clean install

# 3. Run backend
./mvnw spring-boot:run

# Backend runs on http://localhost:8080
```

## 📋 Before You Code

1. **Which module?** → Check MODULE_TRACKING.md for what's next
2. **What to build?** → Read the module section in DEVELOPMENT_CONTEXT.md
3. **How to start?** → Create module folder under `src/main/java/com/mgps/[module]`
4. **Database changes?** → Add SQL migration in `src/main/resources/db/migration/`
5. **Testing?** → Write tests in `src/test/java/com/mgps/[module]`

## ✅ After You Code

1. Update MODULE_TRACKING.md (change ⬜ to 🟢)
2. Run tests: `./mvnw test`
3. Commit: `git commit -m "[DONE] Module Name"`

## 🔗 Module Dependencies (Do in Order)

1. **Backend Setup** - Initialize project
2. **Multi-tenant Framework** - TenantContext, routing
3. **School Onboarding** - Database provisioning
4. **Authentication** - JWT, Login/Logout
5. **User Management** - RBAC, User CRUD
6. **Academic Structure** - Classes, Subjects
7. **Student Management** - Admission, profiles
8. **Timetable** - Scheduling

## 💾 Common Database Queries

```sql
-- List all schools
SELECT id, name, domain, is_active FROM schools;

-- Get subscription plans
SELECT * FROM subscription_plans;

-- Check domain mappings
SELECT domain, tenant_database FROM domain_mappings;
```

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| `docker-compose: command not found` | Install Docker Desktop |
| `Connection refused on 5432` | Run `docker-compose up -d postgres` |
| `PGAdmin won't open` | Check `docker-compose ps`, wait 30s for startup |
| `Port 8080 already in use` | Change server.port in application.yml |
| `Tests failing` | Check logs, ensure database is running |

## 📞 Need Help?

1. **Architecture questions** → DEVELOPMENT_CONTEXT.md
2. **Module specifications** → MGPS-requirement-doc.md
3. **Database schema** → init-db.sql
4. **Setup issues** → README.md

## 🎓 Learning Resources

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Multi-tenancy**: Section in DEVELOPMENT_CONTEXT.md
- **JWT Auth**: Common pattern in Spring Security
- **PostgreSQL**: Official docs

---

**Remember**: Read the context files first, they save time! 💡
