-- V13__add_academic_class_code_and_subject_type.sql

ALTER TABLE academic_classes
    ADD COLUMN IF NOT EXISTS code VARCHAR(50);

ALTER TABLE academic_subjects
    ADD COLUMN IF NOT EXISTS subject_type VARCHAR(50);
