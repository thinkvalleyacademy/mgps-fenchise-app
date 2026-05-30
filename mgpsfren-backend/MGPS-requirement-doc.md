# **School Management System (SMS) – Requirement Document**

## **Project Overview**

The School Management System (SMS) is a cloud-based, multi-tenant SaaS platform designed to manage multiple schools under a franchise model. The platform will allow a central Super Admin to onboard and manage schools from different locations, while each school independently manages its own academic and administrative operations.

The system will support role-based access, academic management, staff and student administration, scheduling, fee handling, reporting, and scalable deployment architecture.

---

# **1\. Business Model**

The application will operate as a Franchise-Based Multi-Tenant SaaS Platform.

## **Stakeholders**

### **1\. Super Admin**

Central platform owner/operator.

Responsibilities:

* Onboard and manage schools  
* Monitor all schools  
* Access any school as administrator  
* Manage subscription/plans  
* Platform-level analytics and reporting  
* Tenant provisioning and activation  
* Global configuration management

### **2\. School Admin**

Administrator of an individual school.

Responsibilities:

* Manage school operations  
* Manage users and permissions  
* Academic configuration  
* Timetable and scheduling  
* Student admissions  
* Fee and finance operations  
* Reports and communication

### **3\. Staff Roles**

Different operational users within a school.

Examples:

* Teachers  
* Principal  
* Fee Manager  
* Marketing Manager  
* Accountant  
* Receptionist  
* Librarian  
* Transport Manager  
* Exam Coordinator

### **4\. Students & Parents**

End users for academic and communication features.

---

# **2\. Application Architecture**

## **Multi-Tenant Architecture**

The system will follow a Database-per-Tenant strategy.

### **Tenant Isolation**

* Each school will have its own dedicated database/schema.  
* Central master database will maintain:  
  * Tenant registry  
  * School metadata  
  * Subscription information  
  * Authentication mapping  
  * Domain mapping

### **Advantages**

* Better data isolation  
* Easier backup and restore  
* Improved security  
* Independent scaling and maintenance  
* Easier franchise management

---

# **3\. Technology Stack**

## **Frontend**

* React.js  
* Tailwind CSS / Material UI  
* Axios  
* React Router  
* Redux / Zustand

## **Backend**

* Java 21  
* Spring Boot 3.x  
* Spring Security \+ JWT  
* Spring Data JPA  
* Hibernate  
* Flyway/Liquibase for DB migrations

## **Database**

* PostgreSQL  
* Multi-tenant support using separate databases/schemas

## **Infrastructure**

* Docker  
* Docker Compose  
* Linux Server  
* NGINX Reverse Proxy

## **DevOps**

* GitHub  
* GitHub Actions 
* Docker Registry  
* Monitoring & Logging

---
# **0\. UI Architecture & System Setup**

## UI Architecture

* React + TypeScript + Material UI
* Single Page App layout with Header, Sidebar, Breadcrumb, Page Content, Footer
* Route-driven screens using React Router
* State management via Redux or Zustand
* Axios for backend API calls

## Initial System Setup Flow

* Hidden bootstrap route: `/setup/superadmin`
* The route is available only when no super admin exists
* Public registration is disabled for production
* Bootstrap flow:
  * First install → create Super Admin → login → create School → create School Admin → school admin creates additional users

## Core UI Screens

* `/setup/superadmin` - Super Admin Bootstrap
* `/login` - Login Screen
* `/forgot-password` - Forgot Password
* `/reset-password` - Reset Password

* `/dashboard` - Main dashboard after login
* `/schools` - School Management list
* `/schools/create` - Create School
* `/schools/:id` - School Details
* `/subscriptions` - Subscription plans and assignments
* `/academic/sessions` - Academic sessions
* `/academic/classes` - Class management
* `/academic/sections` - Section management
* `/academic/subjects` - Subject management
* `/users` - User directory and role management
* `/students` - Student list and admissions
* `/teachers` - Teacher management
* `/timetable` - Timetable builder
* `/attendance` - Attendance entry and reports
* `/fees` - Fee management
* `/exams` - Exam setup and results
* `/communication` - SMS/Email/notifications

---
# **4\. Core Modules**

# **Phase 1 – High Priority Modules**

## **4.1 School Onboarding Module**

Purpose:  
 Create and configure new franchise schools, driven by the Super Admin portal.

UI Screens:

* Create School
* School Details
* Subscription Management

Features:

* School registration  
* School profile management  
* School logo upload  
* Contact details  
* Address and location  
* Subscription/package selection  
* Database/schema creation  
* Domain/subdomain mapping  
* School activation/deactivation
* School admin account creation and assignment

Example:

* school1.smsapp.com  
* school2.smsapp.com

---

## **4.2 User Management Module**

### **User Types**

* Super Admin  
* School Admin  
* Teacher  
* Student  
* Parent  
* Principal  
* Accountant  
* Staff

### **Features**

* User onboarding  
* Role-based access control (RBAC)  
* Permission management  
* Login/logout  
* JWT authentication  
* Password reset  
* User profile management  
* Active/inactive users  
* Bulk user import

---

## **4.3 Academic Structure Module**

Features:

* Academic year/session management  
* Classes  
* Sections  
* Subjects  
* Streams  
* Departments  
* House system

---

## **4.4 Timetable & Scheduling Module**

Features:

* Class timetable creation  
* Teacher scheduling  
* Subject allocation  
* Room allocation  
* Conflict detection  
* Daily/weekly schedule generation

---

## **4.5 Student Management Module**

Features:

* Student admission  
* Student profile  
* Document upload  
* Class assignment  
* Attendance tracking  
* Student promotion  
* Transfer certificate generation

---

## **4.6 Staff Management Module**

Features:

* Staff onboarding  
* Employee records  
* Department assignment  
* Attendance  
* Leave management  
* Payroll integration

---

# **Phase 2 – Medium Priority Modules**

## **4.7 Fee Management Module**

Features:

* Fee structure creation  
* Monthly/quarterly fee setup  
* Online payment integration  
* Due tracking  
* Receipt generation  
* Scholarship handling  
* Fine calculation

---

## **4.8 Examination Module**

Features:

* Exam scheduling  
* Marks entry  
* Result generation  
* Grade calculation  
* Report cards  
* Rank generation

---

## **4.9 Attendance Module**

Features:

* Student attendance  
* Staff attendance  
* Biometric integration support  
* Attendance reports

---

## **4.10 Communication Module**

Features:

* SMS integration  
* Email notifications  
* Push notifications  
* Parent communication  
* Announcement board

---

# **Phase 3 – Advanced Modules**

## **4.11 Transport Management**

* Vehicle tracking  
* Route management  
* Driver management

## **4.12 Library Management**

* Book inventory  
* Issue/return system

## **4.13 Hostel Management**

* Room allocation  
* Hostel fee tracking

## **4.14 Inventory Management**

* Asset tracking  
* Stock management

## **4.15 Analytics & Reporting**

* School performance dashboard  
* Financial reports  
* Attendance analytics

---

# **5\. Authentication & Security**

## **Security Features**

* JWT-based authentication  
* Role-based authorization  
* Password encryption  
* Audit logs  
* API rate limiting  
* Tenant isolation  
* HTTPS/SSL  
* Secure file upload
* Hidden bootstrap route `/setup/superadmin` for initial super admin creation only
* No public registration endpoint in production

---

# **6\. Multi-Tenant Request Flow**

## **Request Lifecycle**

1. User accesses:  
   * school1.smsapp.com  
2. NGINX routes request to backend.  
3. Backend identifies tenant:  
   * via subdomain/header/token  
4. Application loads tenant configuration.  
5. Dynamic datasource selected.  
6. User authenticated within tenant database.

---

# **7\. Suggested Database Design**

## **Master Database**

Contains:

* Schools  
* Subscription plans  
* Tenant mappings  
* Global users  
* Domain configuration

## **Tenant Database (Per School)**

Contains:

* Students  
* Staff  
* Attendance  
* Fees  
* Exams  
* Timetables  
* Reports

---

# **8\. Deployment Architecture**

## **Infrastructure**

### **Reverse Proxy**

* NGINX

### **Containers**

* Frontend Container  
* Backend Container  
* Database Container  
* Redis (optional)  
* RabbitMQ (optional)

### **Deployment**

* Docker Compose initially  
* Kubernetes in future scaling phase

---

# **9\. Scalability Considerations**

## **Future Enhancements**

* Mobile applications  
* AI-based analytics  
* Online classes  
* Video conferencing  
* Multi-language support  
* ERP integrations  
* WhatsApp integration

---

# **10\. Recommended Development Phases**

## **MVP (Phase 1\)**

Focus on:

1. Multi-tenant setup  
2. School onboarding  
3. Authentication  
4. User management  
5. Student management  
6. Timetable management

## **Phase 2**

1. Fee management  
2. Attendance  
3. Examination  
4. Reporting

## **Phase 3**

1. Communication  
2. Mobile apps  
3. Advanced analytics  
4. Integrations

---

# **11\. Recommended Technical Architecture**

## **Backend Structure**

* API Gateway  
* Auth Service  
* Tenant Management Service  
* Academic Service  
* Fee Service  
* Notification Service

Initially:

* Modular Monolith

Future:

* Microservices architecture

---

# **12\. Suggested DevOps Workflow**

## **CI/CD Pipeline**

* GitHub Actions  
* Docker image build  
* Automated testing  
* Deployment to Linux server

## **Monitoring**

* Prometheus  
* Grafana  
* ELK Stack

---

# **13\. Key Non-Functional Requirements**

## **Performance**

* Fast tenant switching  
* Optimized DB connections

## **Security**

* Tenant-level data isolation

## **Reliability**

* Automated backups  
* Disaster recovery

## **Maintainability**

* Clean modular architecture  
* API documentation

## **Availability**

* 99.9% uptime target

---

# **14\. API & Documentation**

## **API Standards**

* REST APIs  
* Swagger/OpenAPI Documentation

## **Naming Standards**

* `/api/v1/students`  
* `/api/v1/classes`

---

# **15\. Conclusion**

The proposed School Management System will be a scalable, secure, franchise-ready multi-tenant SaaS platform capable of supporting multiple schools independently under a centralized administration model.

The architecture is designed to:

* support future scaling,  
* ensure tenant isolation,  
* simplify deployment,  
* and enable rapid feature expansion.

This system will serve as a strong foundation for a modern educational ERP platform.

