# MGPS Backend - AI Instructions

## 🛠️ Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL with Hibernate/JPA
- **Migration**: Flyway

## 🏗️ Multi-tenancy Mandates
1. **Tenant ID**: Every entity should ideally belong to a tenant. Global entities go into `mgps_master`.
2. **Context Propagation**: Ensure `TenantContext` is correctly set and cleared in every request.
3. **Dynamic Routing**: Use `AbstractRoutingDataSource` for runtime database switching.

## 📝 Patterns
1. **REST Controllers**: Return `ResponseEntity<ApiResponse<T>>`.
2. **Service Layer**: Contain all business logic. No logic in Controllers.
3. **Repositiories**: Use Spring Data JPA. Use specifications for complex filtering.
4. **DTOs**: Map Entities to DTOs using explicit mapper classes or constructors.

## 🧪 Testing
- Write integration tests using `@SpringBootTest` and Testcontainers if possible.
- Mock external services using `@MockBean`.
