-- V15__create_enquiry_table.sql
-- Create enquiries table in mgps_master database

CREATE TABLE IF NOT EXISTS enquiries (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    student_class VARCHAR(50),
    query TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index for email and mobile number for faster searching
CREATE INDEX idx_enquiries_email ON enquiries(email);
CREATE INDEX idx_enquiries_mobile_number ON enquiries(mobile_number);
