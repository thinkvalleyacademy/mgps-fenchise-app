# Phase 1, Module 2: Multi-Tenant Architecture Framework

## ✅ Completed: Module 1 - Backend Project Setup

**Status**: 🟢 COMPLETED  
**Date**: 29 May 2026

### What Was Done
- ✅ Maven project structure created (pom.xml)
- ✅ All Spring Boot 3.x dependencies configured
- ✅ Java 21 LTS setup with proper compiler configuration
- ✅ Main application class created
- ✅ Application.yml with comprehensive configuration
- ✅ Exception handling framework (4 custom exceptions)
- ✅ Global exception handler with proper HTTP status codes
- ✅ Health check endpoints created
- ✅ CORS configuration for frontend
- ✅ Web configuration setup
- ✅ API response wrapper DTO for consistent responses
- ✅ Testing dependencies configured (JUnit 5, Mockito, TestContainers)
- ✅ Code coverage (JaCoCo) configured
- ✅ Project successfully builds ✅

### Project Structure Created
```
mgpsfren-backend/
├── pom.xml                          [✅ Parent POM with all dependencies]
├── src/main/java/com/mgps/
│   ├── MgpsApplication.java         [✅ Main Spring Boot class]
│   ├── config/
│   │   ├── CorsConfig.java         [✅ CORS configuration]
│   │   └── WebConfig.java          [✅ Web configuration]
│   ├── common/
│   │   ├── exception/              [✅ Custom exceptions]
│   │   │   ├── MgpsException.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BusinessLogicException.java
│   │   │   ├── DuplicateResourceException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── controller/
│   │   │   └── HealthCheckController.java [✅ Health & Info endpoints]
│   │   └── dto/
│   │       └── ApiResponse.java    [✅ Standard response wrapper]
│   ├── tenant/                     [⬜ To be implemented]
│   ├── auth/                       [⬜ To be implemented]
│   ├── user/                       [⬜ To be implemented]
│   └── school/                     [⬜ To be implemented]
├── src/main/resources/
│   ├── application.yml             [✅ Configuration file]
│   └── db/migration/               [✅ Flyway migrations directory]
└── src/test/java/com/mgps/
    └── MgpsApplicationTests.java   [✅ Context load test]
```

### Build Verification
```
✅ mvn clean compile - SUCCESS
✅ Project compiles without errors
✅ All dependencies resolved
✅ Ready for module development
```

---

## 🚀 Next Module: Phase 1, Module 2 - Multi-Tenant Architecture

### 🎯 Objectives
Implement the core multi-tenant framework that will:
1. Identify which tenant (school) the request belongs to
2. Route requests to the correct database
3. Maintain tenant context throughout the request lifecycle
4. Support dynamic datasource switching

### 📋 Tasks to Complete

#### Task 1: Tenant Context Management
- [ ] Create `TenantContext` holder class
  - Thread-local storage for current tenant ID
  - Get/Set current tenant methods
  - Clear tenant after request
  
- [ ] Create `TenantContextHolder` utility
  - Static methods for access
  - Request lifecycle management
  
#### Task 2: Tenant Identification
- [ ] Create tenant identifier from:
  - Subdomain extraction (school1.smsapp.com → school1)
  - JWT token (tenantId in claims)
  - HTTP header (X-Tenant-Id)
  - Priority: Header > Token > Subdomain
  
- [ ] Create `TenantIdentifier` interface/implementation

#### Task 3: Dynamic DataSource Routing
- [ ] Implement `AbstractRoutingDataSource` extension
  - Override `determineCurrentLookupKey()`
  - Route based on TenantContext
  
- [ ] Create datasource registry
  - Master datasource (always present)
  - Cached tenant datasources
  - Dynamic creation for new tenants
  
#### Task 4: Tenant Request Filter
- [ ] Create `TenantResolutionFilter` (Spring Filter)
  - Extract tenant identifier from request
  - Set TenantContext
  - Clear context after request
  - Handle multi-tenant routing

#### Task 5: Configuration
- [ ] Update `DataSourceConfig`
  - Configure master datasource (hardcoded)
  - Setup routing datasource
  - Connection pool settings per tenant
  
- [ ] Create `MultiTenantConfig`
  - Tenant detection strategy
  - Datasource creation strategy

#### Task 6: Testing
- [ ] Unit tests for TenantContext
  - Thread safety verification
  - Get/Set operations
  
- [ ] Integration tests for routing
  - Verify correct datasource is used
  - Test with multiple tenants
  - Test tenant switching

### 📁 Files to Create

```
src/main/java/com/mgps/tenant/
├── TenantContext.java              [ThreadLocal tenant holder]
├── TenantContextHolder.java        [Utility class for access]
├── TenantIdentifier.java           [Interface for identification]
├── TenantIdentifierImpl.java        [Implementation]
├── RoutingDataSource.java          [AbstractRoutingDataSource extension]
├── DataSourceRegistry.java         [Manage multiple datasources]
├── TenantResolutionFilter.java     [Spring Filter]
├── TenantDatasourceService.java    [Service for datasource operations]
└── config/
    ├── DataSourceConfig.java       [Datasource configuration]
    └── MultiTenantConfig.java      [Multi-tenant configuration]

src/test/java/com/mgps/tenant/
├── TenantContextTest.java
├── RoutingDataSourceTest.java
└── TenantResolutionFilterIntegrationTest.java
```

### 🔄 Request Flow (What We're Building)

```
1. HTTP Request arrives at /api/students (from school1.smsapp.com)
   ↓
2. TenantResolutionFilter intercepts
   ↓
3. Identifies tenant: "school1" (from subdomain)
   ↓
4. Looks up in domain_mappings: school1 → school1_db
   ↓
5. Sets TenantContext.setTenant("school1")
   ↓
6. RoutingDataSource.determineCurrentLookupKey() → returns "school1"
   ↓
7. Switches to school1_db datasource
   ↓
8. REST Controller executes (gets students from school1_db)
   ↓
9. Response sent back
   ↓
10. TenantContext cleared by filter
```

### 🗄️ Database Schema Required

The master database already has:
- ✅ `domain_mappings` table (domain → database mapping)
- ✅ `schools` table (schools registry)
- ✅ `subscription_plans` table

We need to use these to:
1. Extract tenant from request
2. Look up database name from domain_mappings
3. Create/get datasource for that database
4. Route query to correct database

### 💻 Code Examples (Guidance)

**TenantContext.java**
```java
public class TenantContext {
    private static final ThreadLocal<String> tenantId = new ThreadLocal<>();
    
    public static void setTenant(String id) {
        tenantId.set(id);
    }
    
    public static String getTenant() {
        return tenantId.get();
    }
    
    public static void clear() {
        tenantId.remove();
    }
}
```

**TenantResolutionFilter.java**
```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantResolutionFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
        try {
            String tenantId = extractTenant(request);
            TenantContext.setTenant(tenantId);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    
    private String extractTenant(HttpServletRequest request) {
        // Logic to extract from header, token, or subdomain
    }
}
```

**RoutingDataSource.java**
```java
public class RoutingDataSource extends AbstractRoutingDataSource {
    
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenant();
    }
}
```

### 🧪 Testing Strategy

1. **Unit Tests**
   - Test TenantContext get/set/clear
   - Test thread isolation
   - Test tenant identifier extraction

2. **Integration Tests**
   - Test with real database
   - Test datasource routing
   - Test multiple concurrent requests

3. **E2E Tests**
   - Test full request flow
   - Verify correct database is queried
   - Test tenant isolation

### 📚 Dependencies Already Configured
- ✅ Spring Web
- ✅ Spring Data JPA
- ✅ PostgreSQL Driver
- ✅ HikariCP (connection pooling)
- ✅ Testing libraries

### ⏱️ Estimated Timeline
- **Tenant Context**: 30 min
- **Datasource Routing**: 1 hour
- **Filter & Identification**: 1 hour
- **Configuration**: 30 min
- **Tests**: 1 hour
- **Integration & Debugging**: 1 hour

**Total**: ~5 hours

### ✅ Definition of Done
- [ ] All classes created and implemented
- [ ] Unit tests pass (80%+ coverage)
- [ ] Integration tests pass
- [ ] Request properly routes to tenant database
- [ ] Thread safety verified
- [ ] Code review passed
- [ ] Documentation updated
- [ ] All tests passing: `mvn clean test`

### 📝 Notes
- Use ThreadLocal carefully (clear after request)
- Connection pooling is important for performance
- Test with multiple databases
- Verify isolation between tenants
- Handle edge cases (null tenant, invalid database)

---

## 📞 Questions During Development?

1. **How to identify tenant?** → See `extractTenant()` in filter
2. **How to manage datasources?** → Use DataSourceRegistry with caching
3. **Thread safety?** → ThreadLocal handles this, ensure clear() is called
4. **Testing with multiple DBs?** → Use TestContainers or H2
5. **Performance?** → Cache datasources, use connection pooling

---

**Status**: Ready to start development  
**Next**: Complete this module, then move to Phase 1 Module 3: School Onboarding
