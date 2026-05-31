# MGPS Franchise School Management System - AI Instructions

Welcome to the MGPS Project. This file provides foundational mandates and architectural guidance for all AI interactions in this repository.

## 🏗️ Project Structure
- **mgpsfren-backend/**: Spring Boot 3.x application (Java 21). Multi-tenant architecture (Database-per-Tenant).
- **mgpsfren-frontend/**: React application with TypeScript and Vite.
- **docker-files/**: Docker configurations for PostgreSQL and other services.
- **docs/**: Comprehensive project documentation.
- **.ai/**: Project memory and decision logs.

## 📜 Documentation Index
- [Architecture Overview](./docs/architecture.md)
- [API Guidelines](./docs/api-guidelines.md)
- [Coding Standards](./docs/coding-standards.md)
- [Database Schema & Multi-tenancy](./docs/database.md)
- [Deployment Guide](./docs/deployment.md)

## 🧠 AI Context & Memory
- [Project Memory](./.ai/project-memory.md): Current state and summary.
- [Decisions Log](./.ai/decisions.md): Architectural and technical choices.
- [Known Issues](./.ai/known-issues.md): Tracked bugs and technical debt.

## 🛠️ Global Mandates
1. **Multi-tenancy**: Every backend change must respect the multi-tenant routing logic. Never bypass the `TenantContext`.
2. **Type Safety**: Use TypeScript in the frontend and strong typing (DTOs, Entities) in the backend. No `any` types.
3. **Surgical Edits**: Use the `replace` tool for code changes whenever possible to minimize context bloat.
4. **Validation**: Always verify changes by running relevant tests (`./mvnw test` or frontend tests).
5. **Memory Management**: Keep `.ai/project-memory.md` updated with significant progress.

Refer to module-specific `GEMINI.md` files in `mgpsfren-backend/` and `mgpsfren-frontend/` for specialized instructions.
