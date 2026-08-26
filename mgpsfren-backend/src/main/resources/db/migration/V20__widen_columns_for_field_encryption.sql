-- V20__widen_columns_for_field_encryption.sql
-- Encrypted values (base64(iv + ciphertext)) are much longer than the plain
-- text they replace, so widen every column that now goes through
-- EncryptedStringConverter.

ALTER TABLE app_users ALTER COLUMN phone TYPE TEXT;

ALTER TABLE students ALTER COLUMN email TYPE TEXT;
ALTER TABLE students ALTER COLUMN phone TYPE TEXT;
ALTER TABLE students ALTER COLUMN parent_name TYPE TEXT;
ALTER TABLE students ALTER COLUMN parent_phone TYPE TEXT;

ALTER TABLE staff_members ALTER COLUMN email TYPE TEXT;
ALTER TABLE staff_members ALTER COLUMN phone TYPE TEXT;
ALTER TABLE staff_members ALTER COLUMN payroll_account_reference TYPE TEXT;
