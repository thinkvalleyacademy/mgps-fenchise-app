-- V9__create_fee_management_tables.sql
-- Fee Management module tables for tenant database

-- Fee Categories (e.g., Tuition Fee, Admission Fee, Library Fee)
CREATE TABLE IF NOT EXISTS fee_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fee Structures (Definitions for how much to charge for each category per academic year/class)
CREATE TABLE IF NOT EXISTS fee_structures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL,
    academic_year_id UUID NOT NULL,
    class_id UUID, -- Optional: Can be for all classes if NULL
    fee_category_id UUID REFERENCES fee_categories(id),
    amount DECIMAL(12, 2) NOT NULL,
    due_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Student Fee Assignments (Actual fees assigned to specific students)
CREATE TABLE IF NOT EXISTS student_fees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL,
    student_id UUID REFERENCES students(id) ON DELETE CASCADE,
    fee_structure_id UUID REFERENCES fee_structures(id),
    amount_due DECIMAL(12, 2) NOT NULL,
    amount_paid DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PARTIAL, PAID
    due_date DATE,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    discount_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fee Payments (Transaction records)
CREATE TABLE IF NOT EXISTS fee_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL,
    student_fee_id UUID REFERENCES student_fees(id),
    amount_paid DECIMAL(12, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_mode VARCHAR(50), -- CASH, ONLINE, CHEQUE, BANK_TRANSFER
    transaction_id VARCHAR(100), -- For online payments
    receipt_number VARCHAR(50) UNIQUE,
    remarks TEXT,
    processed_by UUID, -- Link to staff/user id
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_fee_categories_school ON fee_categories(school_id);
CREATE INDEX IF NOT EXISTS idx_fee_structures_school ON fee_structures(school_id);
CREATE INDEX IF NOT EXISTS idx_student_fees_school ON student_fees(school_id);
CREATE INDEX IF NOT EXISTS idx_fee_payments_school ON fee_payments(school_id);
