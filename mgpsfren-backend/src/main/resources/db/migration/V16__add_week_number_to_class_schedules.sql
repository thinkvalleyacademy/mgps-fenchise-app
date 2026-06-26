ALTER TABLE class_schedules
    ADD COLUMN IF NOT EXISTS week_number INTEGER NOT NULL DEFAULT 1;

CREATE INDEX IF NOT EXISTS idx_class_schedules_class_session_week
    ON class_schedules(class_name, academic_session, week_number);
