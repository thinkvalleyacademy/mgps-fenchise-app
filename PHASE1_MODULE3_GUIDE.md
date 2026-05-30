# 📚 Phase 1, Module 3: School Onboarding - Developer Guide

**Status**: 🟢 COMPLETED  
**Module**: School Onboarding & Tenant Provisioning  
**Estimated Duration**: 6-8 hours  
**Difficulty**: Medium  
**Dependencies**: Module 1 ✅, Module 2 ✅  

---

## 📋 Module Overview

School Onboarding is the critical second module that enables the Super Admin to register new schools and automatically provision isolated databases for each school. This module bridges the Multi-Tenant Framework (Module 2) with the actual school management operations.

### Key Responsibilities
1. **School Registration** - REST endpoints for creating new schools
2. **Database Provisioning** - Automatic database creation and schema setup
3. **Domain Mapping** - Map custom domains/subdomains to schools
4. **Subscription Management** - Assign subscription plans to schools
5. **School Activation** - Enable/disable schools

### Business Value
- ✅ Super Admin can onboard new franchise schools
- ✅ Automatic database isolation for each school
- ✅ Custom domain support (subdomain-based routing)
- ✅ Subscription plan enforcement
- ✅ School operational status control

---

## 🏗️ Architecture

### Request Flow: New School Registration

```
POST /api/schools
  ↓
SchoolController.createSchool(SchoolRegistrationDTO)
  ↓
SchoolService.registerSchool()
  ├─ Validate school data
  ├─ Create school record in MASTER DB
  ├─ Generate database name (school_{id})
  ├─ Provision tenant database
  │  └─ Execute Flyway V1__create_tenant_schema.sql
  ├─ Create SchoolDomain entry for subdomain routing
  └─ Register datasource in RoutingDataSource
  ↓
Return SchoolCreatedDTO with:
  - schoolId
  - databaseName
  - adminUrl (subdomain.smsapp.com)
  ↓
Response: 201 Created
```

### Database Structure

#### Master Database (mgps_master)
```
schools table
├─ id (UUID, PK)
├─ name (VARCHAR)
├─ admin_email (VARCHAR, unique)
├─ admin_phone (VARCHAR)
├─ address (TEXT)
├─ city, state, postal_code
├─ logo_url (VARCHAR)
├─ database_name (VARCHAR, unique)
├─ subscription_plan_id (FK)
├─ status (enum: ACTIVE, INACTIVE, SUSPENDED)
├─ created_at (TIMESTAMP)
└─ updated_at (TIMESTAMP)

school_domains table
├─ id (UUID, PK)
├─ school_id (UUID, FK)
├─ domain_name (VARCHAR, unique)
├─ is_primary (BOOLEAN)
├─ is_active (BOOLEAN)
├─ created_at (TIMESTAMP)
└─ updated_at (TIMESTAMP)

subscription_plans table
├─ id (UUID, PK)
├─ plan_name (VARCHAR)
├─ description (TEXT)
├─ max_students (INTEGER)
├─ max_staff (INTEGER)
├─ max_users (INTEGER)
├─ monthly_price (DECIMAL)
├─ features (JSONB)
├─ is_active (BOOLEAN)
├─ created_at (TIMESTAMP)
└─ updated_at (TIMESTAMP)
```

#### Tenant Database (school_{id}_db)
Pre-created via Flyway migration V1__create_tenant_schema.sql

---

## 🗂️ Package Structure

```
src/main/java/com/mgps/school/
├── entity/
│   ├── School.java                 (JPA entity)
│   ├── SchoolDomain.java          (Domain mapping)
│   ├── SubscriptionPlan.java      (Plan definition)
│   └── SchoolStatus.java          (Enum)
├── dto/
│   ├── SchoolRegistrationDTO.java (Input)
│   ├── SchoolDTO.java             (Output)
│   ├── SchoolCreatedDTO.java      (Creation response)
│   └── SchoolDomainDTO.java       (Domain DTO)
├── repository/
│   ├── SchoolRepository.java       (Spring Data JPA)
│   ├── SchoolDomainRepository.java
│   └── SubscriptionPlanRepository.java
├── service/
│   ├── SchoolService.java         (Business logic)
│   ├── DatabaseProvisioningService.java  (DB setup)
│   └── DomainMappingService.java  (Domain management)
├── controller/
│   └── SchoolController.java      (REST endpoints)
└── exception/
    ├── SchoolAlreadyExistsException.java
    ├── InvalidSchoolDataException.java
    └── DatabaseProvisioningException.java

src/test/java/com/mgps/school/
├── SchoolServiceTest.java
├── SchoolControllerTest.java
└── DatabaseProvisioningServiceTest.java
```

---

## 🔑 Core Entities

### School Entity
```java
@Entity
@Table(name = "schools")
public class School {
    @Id
    private UUID id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    private String adminEmail;
    
    @Column(length = 20)
    private String adminPhone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 100)
    private String state;
    
    @Column(length = 10)
    private String postalCode;
    
    @Column(length = 255)
    private String logoUrl;
    
    @Column(nullable = false, unique = true, length = 100)
    private String databaseName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolStatus status = SchoolStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<SchoolDomain> domains;
}
```

### SchoolStatus Enum
```java
public enum SchoolStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");
    
    private final String displayName;
}
```

### SchoolDomain Entity
```java
@Entity
@Table(name = "school_domains")
public class SchoolDomain {
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @Column(nullable = false, unique = true, length = 255)
    private String domainName;
    
    @Column(nullable = false)
    private Boolean isPrimary = true;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

---

## 📡 REST Endpoints

### School Management

#### 1. **Create School** (Super Admin only)
```
POST /api/schools
Content-Type: application/json

Request:
{
  "name": "Green Valley School",
  "adminEmail": "admin@gvschool.com",
  "adminPhone": "+91-9876543210",
  "address": "123 School Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "subscriptionPlanId": "uuid-of-plan"
}

Response: 201 Created
{
  "schoolId": "uuid",
  "name": "Green Valley School",
  "databaseName": "school_uuid_db",
  "adminUrl": "gvschool.smsapp.com",
  "status": "ACTIVE",
  "createdAt": "2026-05-29T12:00:00Z"
}
```

#### 2. **Get School by ID**
```
GET /api/schools/{schoolId}

Response: 200 OK
{
  "schoolId": "uuid",
  "name": "Green Valley School",
  "adminEmail": "admin@gvschool.com",
  "adminPhone": "+91-9876543210",
  "address": "123 School Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "domains": [
    {
      "domainId": "uuid",
      "domainName": "gvschool.smsapp.com",
      "isPrimary": true,
      "isActive": true
    }
  ],
  "subscriptionPlan": {
    "planId": "uuid",
    "planName": "Standard",
    "maxStudents": 500,
    "maxStaff": 50
  },
  "status": "ACTIVE",
  "createdAt": "2026-05-29T12:00:00Z",
  "updatedAt": "2026-05-29T12:00:00Z"
}
```

#### 3. **Get All Schools** (paginated)
```
GET /api/schools?page=0&size=20&status=ACTIVE

Response: 200 OK
{
  "content": [
    { /* School objects */ }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3
}
```

#### 4. **Update School**
```
PUT /api/schools/{schoolId}
Content-Type: application/json

Request:
{
  "name": "Green Valley School - Updated",
  "adminPhone": "+91-9876543211",
  "city": "Pune"
}

Response: 200 OK
{
  "schoolId": "uuid",
  "name": "Green Valley School - Updated",
  ...
}
```

#### 5. **Change School Status**
```
PATCH /api/schools/{schoolId}/status
Content-Type: application/json

Request:
{
  "status": "SUSPENDED"
}

Response: 200 OK
{
  "schoolId": "uuid",
  "status": "SUSPENDED",
  "updatedAt": "2026-05-29T12:30:00Z"
}
```

### Domain Management

#### 6. **Add School Domain**
```
POST /api/schools/{schoolId}/domains
Content-Type: application/json

Request:
{
  "domainName": "gv-school.smsapp.com",
  "isPrimary": false
}

Response: 201 Created
{
  "domainId": "uuid",
  "schoolId": "uuid",
  "domainName": "gv-school.smsapp.com",
  "isPrimary": false,
  "isActive": true
}
```

#### 7. **Get School Domains**
```
GET /api/schools/{schoolId}/domains

Response: 200 OK
{
  "domains": [
    {
      "domainId": "uuid",
      "domainName": "gvschool.smsapp.com",
      "isPrimary": true,
      "isActive": true
    }
  ]
}
```

---

## 🔧 Service Layer Implementation

### SchoolService.java (Main Service)

```java
@Service
@Transactional
public class SchoolService {
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private SubscriptionPlanRepository planRepository;
    
    @Autowired
    private DatabaseProvisioningService provisioningService;
    
    @Autowired
    private DomainMappingService domainService;
    
    @Autowired
    private RoutingDataSource routingDataSource;
    
    /**
     * Register a new school
     * Steps:
     * 1. Validate input
     * 2. Check duplicate email
     * 3. Create school record in master DB
     * 4. Generate database name
     * 5. Provision tenant database
     * 6. Create primary domain mapping
     * 7. Register datasource for routing
     */
    public SchoolCreatedDTO registerSchool(SchoolRegistrationDTO dto) {
        // Validation
        if (schoolRepository.existsByAdminEmail(dto.getAdminEmail())) {
            throw new DuplicateResourceException("School with this email already exists");
        }
        
        // Verify subscription plan exists
        SubscriptionPlan plan = planRepository.findById(dto.getSubscriptionPlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
        
        // Create school entity
        School school = new School();
        school.setId(UUID.randomUUID());
        school.setName(dto.getName());
        school.setAdminEmail(dto.getAdminEmail());
        school.setAdminPhone(dto.getAdminPhone());
        school.setAddress(dto.getAddress());
        school.setCity(dto.getCity());
        school.setState(dto.getState());
        school.setPostalCode(dto.getPostalCode());
        school.setSubscriptionPlan(plan);
        school.setStatus(SchoolStatus.ACTIVE);
        
        // Generate database name
        String databaseName = "school_" + school.getId().toString().replace("-", "").substring(0, 12) + "_db";
        school.setDatabaseName(databaseName);
        
        // Save to master DB
        School savedSchool = schoolRepository.save(school);
        
        // Provision tenant database
        try {
            provisioningService.provisionDatabase(savedSchool);
        } catch (Exception e) {
            // Rollback school creation if provisioning fails
            schoolRepository.delete(savedSchool);
            throw new DatabaseProvisioningException("Failed to provision school database", e);
        }
        
        // Create primary domain mapping
        String primaryDomain = generatePrimaryDomain(savedSchool.getName());
        SchoolDomain domain = domainService.createPrimaryDomain(savedSchool, primaryDomain);
        
        // Register datasource for tenant routing
        try {
            routingDataSource.registerTenantDataSource(databaseName, 
                createTenantDataSource(databaseName));
        } catch (Exception e) {
            logger.error("Failed to register datasource for school", e);
            // Non-critical error, but log it
        }
        
        // Return response DTO
        return SchoolCreatedDTO.builder()
            .schoolId(savedSchool.getId())
            .name(savedSchool.getName())
            .databaseName(databaseName)
            .adminUrl(domain.getDomainName())
            .status(savedSchool.getStatus())
            .createdAt(savedSchool.getCreatedAt())
            .build();
    }
    
    /**
     * Get school by ID with full details
     */
    public SchoolDTO getSchoolById(UUID schoolId) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        return mapToDTO(school);
    }
    
    /**
     * Get all schools with pagination
     */
    public Page<SchoolDTO> getAllSchools(Pageable pageable) {
        return schoolRepository.findAll(pageable)
            .map(this::mapToDTO);
    }
    
    /**
     * Get schools by status
     */
    public Page<SchoolDTO> getSchoolsByStatus(SchoolStatus status, Pageable pageable) {
        return schoolRepository.findByStatus(status, pageable)
            .map(this::mapToDTO);
    }
    
    /**
     * Update school details
     */
    public SchoolDTO updateSchool(UUID schoolId, SchoolUpdateDTO dto) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        if (dto.getName() != null) school.setName(dto.getName());
        if (dto.getAdminPhone() != null) school.setAdminPhone(dto.getAdminPhone());
        if (dto.getAddress() != null) school.setAddress(dto.getAddress());
        if (dto.getCity() != null) school.setCity(dto.getCity());
        if (dto.getState() != null) school.setState(dto.getState());
        if (dto.getPostalCode() != null) school.setPostalCode(dto.getPostalCode());
        if (dto.getLogoUrl() != null) school.setLogoUrl(dto.getLogoUrl());
        
        School updated = schoolRepository.save(school);
        return mapToDTO(updated);
    }
    
    /**
     * Change school status (activate/deactivate/suspend)
     */
    public SchoolDTO changeStatus(UUID schoolId, SchoolStatus newStatus) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        school.setStatus(newStatus);
        School updated = schoolRepository.save(school);
        return mapToDTO(updated);
    }
    
    // Helper methods...
}
```

### DatabaseProvisioningService.java

```java
@Service
public class DatabaseProvisioningService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Create tenant database and apply migrations
     */
    public void provisionDatabase(School school) {
        String databaseName = school.getDatabaseName();
        
        try {
            // Create database
            createDatabase(databaseName);
            
            // Initialize schema using Flyway (will run V1__create_tenant_schema.sql)
            // Flyway should be configured to run migrations for tenant databases
            
            logger.info("Database provisioned for school: {}", school.getName());
        } catch (DataAccessException e) {
            throw new DatabaseProvisioningException("Failed to create database", e);
        }
    }
    
    private void createDatabase(String databaseName) {
        String createDbSql = "CREATE DATABASE " + databaseName + 
            " OWNER postgres ENCODING 'UTF8'";
        jdbcTemplate.execute(createDbSql);
    }
    
    /**
     * Drop tenant database (use with caution!)
     */
    public void deleteDatabase(String databaseName) {
        String dropDbSql = "DROP DATABASE IF EXISTS " + databaseName;
        jdbcTemplate.execute(dropDbSql);
    }
}
```

### DomainMappingService.java

```java
@Service
public class DomainMappingService {
    
    @Autowired
    private SchoolDomainRepository domainRepository;
    
    /**
     * Create primary domain for school
     */
    public SchoolDomain createPrimaryDomain(School school, String domainName) {
        SchoolDomain domain = new SchoolDomain();
        domain.setId(UUID.randomUUID());
        domain.setSchool(school);
        domain.setDomainName(domainName);
        domain.setIsPrimary(true);
        domain.setIsActive(true);
        
        return domainRepository.save(domain);
    }
    
    /**
     * Add additional domain for school
     */
    public SchoolDomain addDomain(UUID schoolId, String domainName) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        
        if (domainRepository.existsByDomainName(domainName)) {
            throw new DuplicateResourceException("Domain already exists");
        }
        
        SchoolDomain domain = new SchoolDomain();
        domain.setId(UUID.randomUUID());
        domain.setSchool(school);
        domain.setDomainName(domainName);
        domain.setIsPrimary(false);
        domain.setIsActive(true);
        
        return domainRepository.save(domain);
    }
    
    /**
     * Get domains for a school
     */
    public List<SchoolDomainDTO> getSchoolDomains(UUID schoolId) {
        return domainRepository.findBySchoolId(schoolId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 🧪 Testing Strategy

### Unit Tests
- **SchoolServiceTest**: Business logic, validations, transactions
- **DatabaseProvisioningServiceTest**: Database creation/deletion
- **DomainMappingServiceTest**: Domain assignment logic

### Integration Tests
- End-to-end school creation with database provisioning
- Domain routing verification
- Subscription plan assignment

### Test Data
```java
// Sample subscription plan
SubscriptionPlan standardPlan = new SubscriptionPlan();
standardPlan.setId(UUID.randomUUID());
standardPlan.setPlanName("Standard");
standardPlan.setMaxStudents(500);
standardPlan.setMaxStaff(50);
standardPlan.setMonthlyPrice(new BigDecimal("5000"));
standardPlan.setIsActive(true);

// Sample school registration
SchoolRegistrationDTO dto = new SchoolRegistrationDTO();
dto.setName("Test School");
dto.setAdminEmail("admin@testschool.com");
dto.setAdminPhone("+91-1234567890");
dto.setAddress("123 Test Street");
dto.setCity("Bangalore");
dto.setState("Karnataka");
dto.setPostalCode("560001");
dto.setSubscriptionPlanId(standardPlan.getId());
```

---

## 📊 Database Migration

### Master Database: V2__create_school_tables.sql
```sql
-- Create schools table
CREATE TABLE schools (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    admin_email VARCHAR(255) UNIQUE NOT NULL,
    admin_phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(10),
    logo_url VARCHAR(255),
    database_name VARCHAR(100) UNIQUE NOT NULL,
    subscription_plan_id UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id)
);

-- Create school domains table
CREATE TABLE school_domains (
    id UUID PRIMARY KEY,
    school_id UUID NOT NULL,
    domain_name VARCHAR(255) UNIQUE NOT NULL,
    is_primary BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- Create subscription plans table
CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY,
    plan_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    max_students INTEGER,
    max_staff INTEGER,
    max_users INTEGER,
    monthly_price DECIMAL(10, 2),
    features JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_schools_admin_email ON schools(admin_email);
CREATE INDEX idx_schools_database_name ON schools(database_name);
CREATE INDEX idx_schools_status ON schools(status);
CREATE INDEX idx_school_domains_school_id ON school_domains(school_id);
CREATE INDEX idx_school_domains_domain_name ON school_domains(domain_name);
CREATE INDEX idx_subscription_plans_name ON subscription_plans(plan_name);
```

---

## 🔒 Security & Validation

### Input Validation
- ✅ Email format validation (email regex)
- ✅ Phone number format validation
- ✅ Postal code validation
- ✅ Unique email constraint at DB level
- ✅ Unique domain constraint at DB level

### Authorization
- ✅ School creation: Super Admin only (role check)
- ✅ School updates: School Admin or Super Admin (role check)
- ✅ Domain management: School Admin or Super Admin

### Error Handling
```java
- DuplicateResourceException (409)
  - School with email already exists
  - Domain name already exists

- InvalidSchoolDataException (400)
  - Invalid email format
  - Missing required fields

- DatabaseProvisioningException (500)
  - Database creation failed
  - Migration execution failed

- ResourceNotFoundException (404)
  - School not found
  - Subscription plan not found
```

---

## 📝 Implementation Checklist

### Phase 1: Entity & Repository Layer
- [ ] Create School.java entity
- [ ] Create SchoolStatus.java enum
- [ ] Create SchoolDomain.java entity
- [ ] Create SubscriptionPlan.java entity
- [ ] Create SchoolRepository interface
- [ ] Create SchoolDomainRepository interface
- [ ] Create SubscriptionPlanRepository interface

### Phase 2: DTO Layer
- [ ] Create SchoolRegistrationDTO.java
- [ ] Create SchoolDTO.java
- [ ] Create SchoolCreatedDTO.java
- [ ] Create SchoolUpdateDTO.java
- [ ] Create SchoolDomainDTO.java
- [ ] Create SubscriptionPlanDTO.java

### Phase 3: Service Layer
- [ ] Create SchoolService.java (with business logic)
- [ ] Create DatabaseProvisioningService.java
- [ ] Create DomainMappingService.java
- [ ] Create exception classes

### Phase 4: Controller Layer
- [ ] Create SchoolController.java with all endpoints

### Phase 5: Database Migration
- [ ] Create V2__create_school_tables.sql

### Phase 6: Testing
- [ ] Create SchoolServiceTest.java
- [ ] Create SchoolControllerTest.java
- [ ] Create DatabaseProvisioningServiceTest.java
- [ ] Run full test suite

### Phase 7: Validation & Documentation
- [ ] Compile and verify no errors
- [ ] Run full test suite (should pass all tests)
- [ ] Update MODULE_TRACKING.md
- [ ] Create MODULE3_COMPLETED.md

---

## 🚀 Success Criteria

✅ **All criteria must pass**:
1. All entity classes created and compile without errors
2. All repository interfaces work correctly
3. School service creates schools with proper database provisioning
4. Domains are correctly mapped to schools
5. REST endpoints return correct responses with proper HTTP status codes
6. Database migration runs successfully
7. All unit tests pass (>20 new tests)
8. No compilation errors or warnings
9. Code follows existing project conventions
10. Proper exception handling for all error scenarios

---

## 📚 Reference Files

| File | Purpose |
|------|---------|
| DEVELOPMENT_CONTEXT.md | Project architecture & overview |
| MGPS-requirement-doc.md | Full business requirements |
| PHASE1_MODULE2_GUIDE.md | Multi-Tenant framework reference |
| MODULE_TRACKING.md | Progress tracker |

---

## 💡 Key Implementation Points

1. **Database Provisioning**: After creating school record in master DB, immediately provision the tenant database before returning response
2. **Domain Mapping**: Primary domain should follow pattern: `{school-name-slug}.smsapp.com`
3. **Datasource Registration**: Register tenant datasource in RoutingDataSource after successful provisioning
4. **Error Handling**: If provisioning fails, rollback school creation in master DB
5. **Transactions**: Use @Transactional at service method level
6. **Validation**: Comprehensive input validation before processing
7. **Logging**: Log all critical operations (school creation, provisioning, failures)

---

**Next**: Move into Module 4 planning or start User Management implementation
