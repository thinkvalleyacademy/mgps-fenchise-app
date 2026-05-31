-- V10__add_is_default_to_fee_structure.sql
-- Add is_default column to fee_structures table

ALTER TABLE fee_structures ADD COLUMN is_default BOOLEAN DEFAULT false;
