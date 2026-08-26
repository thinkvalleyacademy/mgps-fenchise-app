-- V5__extend_examination_schema.sql
-- Extends the minimal `exams`/`marks` tables from V1 (never previously used by
-- any application code) into a real examination module, and adds the new
-- `exam_schedules` table for per-subject exam papers.
--
-- Existing columns are kept as-is; new columns are additive (nullable, or
-- NOT NULL with a DEFAULT) so this is safe to run against a tenant database
-- that may already have rows in these tables.

ALTER TABLE exams ADD COLUMN IF NOT EXISTS school_id UUID;
ALTER TABLE exams ADD COLUMN IF NOT EXISTS class_id UUID;
ALTER TABLE exams ADD COLUMN IF NOT EXISTS exam_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL';
ALTER TABLE exams ADD COLUMN IF NOT EXISTS start_date DATE;
ALTER TABLE exams ADD COLUMN IF NOT EXISTS end_date DATE;
ALTER TABLE exams ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED';
ALTER TABLE exams ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_exams_school_id ON exams(school_id);

CREATE TABLE IF NOT EXISTS exam_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id UUID NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    subject_id UUID NOT NULL,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_number VARCHAR(50),
    max_marks NUMERIC(6,2) NOT NULL,
    passing_marks NUMERIC(6,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exam_schedules_exam_id ON exam_schedules(exam_id);
CREATE INDEX IF NOT EXISTS idx_exam_schedules_subject_id ON exam_schedules(subject_id);

ALTER TABLE marks ADD COLUMN IF NOT EXISTS exam_schedule_id UUID REFERENCES exam_schedules(id) ON DELETE CASCADE;
ALTER TABLE marks ADD COLUMN IF NOT EXISTS is_absent BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE marks ADD COLUMN IF NOT EXISTS remarks TEXT;
ALTER TABLE marks ADD COLUMN IF NOT EXISTS entered_by UUID;
ALTER TABLE marks ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_marks_exam_schedule_id ON marks(exam_schedule_id);
