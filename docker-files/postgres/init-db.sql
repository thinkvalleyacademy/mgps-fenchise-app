-- PostgreSQL initialization script for MGPS Master Database

-- Create master database (if needed)
-- Note: Master database is typically created during postgres init, but schema tables go here

-- ============================================================================
-- MASTER DATABASE SCHEMA
-- These tables manage the multi-tenant system
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Subscription Plans Table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    max_students INTEGER,
    max_staff INTEGER,
    features JSONB,
    price DECIMAL(10, 2),
    currency VARCHAR(3) DEFAULT 'INR',
    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Schools Registry Table
CREATE TABLE IF NOT EXISTS schools (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(50),
    email VARCHAR(255),
    phone VARCHAR(20),
    website VARCHAR(255),
    logo_url TEXT,
    subscription_plan_id UUID REFERENCES subscription_plans(id),
    database_name VARCHAR(255) NOT NULL UNIQUE,
    database_schema VARCHAR(255) DEFAULT 'public',
    domain VARCHAR(255) UNIQUE,
    subdomain VARCHAR(255) UNIQUE,
    principal_name VARCHAR(255),
    principal_email VARCHAR(255),
    principal_phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(10),
    country VARCHAR(100),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activation_date TIMESTAMP,
    is_active BOOLEAN DEFAULT false,
    is_verified BOOLEAN DEFAULT false,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Domain Mappings Table
CREATE TABLE IF NOT EXISTS domain_mappings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    domain VARCHAR(255) UNIQUE NOT NULL,
    school_id UUID NOT NULL REFERENCES schools(id) ON DELETE CASCADE,
    subdomain VARCHAR(255),
    tenant_database VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Master Users Table (Super Admin and Franchise Admins)
CREATE TABLE IF NOT EXISTS master_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'SCHOOL_ADMIN',
    school_id UUID REFERENCES schools(id),
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    phone_verified BOOLEAN DEFAULT false,
    last_login TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Keys for Integration
CREATE TABLE IF NOT EXISTS api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID REFERENCES schools(id) ON DELETE CASCADE,
    key_name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    secret_key VARCHAR(255),
    permissions JSONB,
    is_active BOOLEAN DEFAULT true,
    last_used TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

-- Audit Log Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID REFERENCES schools(id),
    user_id UUID,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100),
    entity_id VARCHAR(255),
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_schools_domain ON schools(domain);
CREATE INDEX IF NOT EXISTS idx_schools_subdomain ON schools(subdomain);
CREATE INDEX IF NOT EXISTS idx_schools_is_active ON schools(is_active);
CREATE INDEX IF NOT EXISTS idx_domain_mappings_domain ON domain_mappings(domain);
CREATE INDEX IF NOT EXISTS idx_domain_mappings_school_id ON domain_mappings(school_id);
CREATE INDEX IF NOT EXISTS idx_master_users_email ON master_users(email);
CREATE INDEX IF NOT EXISTS idx_master_users_school_id ON master_users(school_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_school_id ON api_keys(school_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_school_id ON audit_logs(school_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);

-- ============================================================================
-- SAMPLE DATA (Optional - Remove in production)
-- ============================================================================

-- Insert default subscription plans
INSERT INTO subscription_plans (name, description, max_students, max_staff, price, features)
VALUES 
    ('BASIC', 'Basic school management features', 500, 50, 5000.00, 
     '{"modules": ["student_management", "staff_management", "timetable"], "support": "email"}'),
    ('PROFESSIONAL', 'Professional features with advanced tools', 2000, 200, 15000.00,
     '{"modules": ["student_management", "staff_management", "timetable", "fees", "examination"], "support": "email+phone"}'),
    ('ENTERPRISE', 'Full-featured enterprise solution', 5000, 500, 50000.00,
     '{"modules": ["all"], "support": "24/7"}')
ON CONFLICT (name) DO NOTHING;
