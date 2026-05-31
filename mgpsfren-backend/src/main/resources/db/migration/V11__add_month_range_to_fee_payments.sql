-- V11__add_month_range_to_fee_payments.sql
-- Add month range support for fee collection

ALTER TABLE fee_payments ADD COLUMN month_from INTEGER;
ALTER TABLE fee_payments ADD COLUMN month_to INTEGER;
