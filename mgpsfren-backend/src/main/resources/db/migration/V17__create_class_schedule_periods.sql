ALTER TABLE class_schedules
    ADD COLUMN IF NOT EXISTS period_name VARCHAR(20) NOT NULL DEFAULT 'P1';

CREATE TABLE IF NOT EXISTS class_schedule_periods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_name VARCHAR(50) NOT NULL,
    academic_session VARCHAR(50) NOT NULL,
    period_name VARCHAR(20) NOT NULL,
    display_order INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (class_name, academic_session, period_name)
);

CREATE INDEX IF NOT EXISTS idx_class_schedule_periods_class_session
    ON class_schedule_periods(class_name, academic_session);
