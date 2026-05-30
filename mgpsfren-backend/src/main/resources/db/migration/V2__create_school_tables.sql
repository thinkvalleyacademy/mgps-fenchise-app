-- V2__create_school_tables.sql
-- Master database tables for school management

-- Create first
CREATE TABLE IF NOT EXISTS subscription_plans (
                                                  id UUID PRIMARY KEY,
                                                  plan_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    max_students INTEGER NOT NULL,
    max_staff INTEGER NOT NULL,
    max_users INTEGER NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    features TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


INSERT INTO subscription_plans (
    id,
    plan_name,
    description,
    max_students,
    max_staff,
    max_users,
    monthly_price,
    features,
    is_active,
    created_at,
    updated_at
)
VALUES (
           gen_random_uuid(),
           'BASIC',
           'Basic Plan',
           500,
           50,
           80,
           4999.00,
           'All core modules',
           true,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       )
    ON CONFLICT (plan_name) DO NOTHING;

-- Schools table
CREATE TABLE IF NOT EXISTS schools (
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

-- School domains table
CREATE TABLE IF NOT EXISTS school_domains (
    id UUID PRIMARY KEY,
    school_id UUID NOT NULL,
    domain_name VARCHAR(255) UNIQUE NOT NULL,
    is_primary BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- Subscription plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY,
    plan_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    max_students INTEGER NOT NULL,
    max_staff INTEGER NOT NULL,
    max_users INTEGER NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    features TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_schools_admin_email ON schools(admin_email);
CREATE INDEX IF NOT EXISTS idx_schools_database_name ON schools(database_name);
CREATE INDEX IF NOT EXISTS idx_schools_status ON schools(status);
CREATE INDEX IF NOT EXISTS idx_school_domains_school_id ON school_domains(school_id);
CREATE INDEX IF NOT EXISTS idx_school_domains_domain_name ON school_domains(domain_name);
CREATE INDEX IF NOT EXISTS idx_school_domains_is_primary ON school_domains(is_primary);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_name ON subscription_plans(plan_name);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_is_active ON subscription_plans(is_active);
