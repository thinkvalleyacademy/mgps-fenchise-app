-- V12__add_recurrence_type_to_fee_structures.sql
-- Support for One-time vs Monthly fees

ALTER TABLE fee_structures ADD COLUMN recurrence_type VARCHAR(20) DEFAULT 'ONE_TIME';
