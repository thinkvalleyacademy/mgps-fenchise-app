-- V19__create_fee_settings_table.sql
-- Per-school configurable fee business rules (currently just the yearly-payment discount %)

CREATE TABLE IF NOT EXISTS fee_settings (
    id UUID PRIMARY KEY,
    school_id UUID UNIQUE NOT NULL,
    yearly_discount_percent DECIMAL(5, 2) NOT NULL DEFAULT 5.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
