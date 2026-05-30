package com.mgps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application for MGPS (Multi-tenant School Management System)
 * 
 * Features:
 * - Multi-tenant architecture (database per tenant)
 * - JWT-based authentication
 * - Role-based access control (RBAC)
 * - School franchise management
 * - Academic management
 */
@SpringBootApplication
@EnableScheduling
public class MgpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgpsApplication.class, args);
    }

}
