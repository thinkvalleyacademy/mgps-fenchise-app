-- V4__add_fee_fine_support.sql
-- Late-payment fine/penalty configuration for fee structures, and the
-- computed fine carried on each student's fee record.

ALTER TABLE fee_structures ADD COLUMN fine_type VARCHAR(20) NOT NULL DEFAULT 'NONE';
ALTER TABLE fee_structures ADD COLUMN fine_amount NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE fee_structures ADD COLUMN grace_period_days INT NOT NULL DEFAULT 0;

ALTER TABLE student_fees ADD COLUMN fine_accrued NUMERIC(12,2) NOT NULL DEFAULT 0;
