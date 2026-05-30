-- V5__create_timetable_tables.sql
-- Timetable and scheduling tables

CREATE TABLE IF NOT EXISTS timetable_entries (
    id UUID PRIMARY KEY,
    school_id UUID NOT NULL,
    academic_year_id UUID NOT NULL,
    class_id UUID NOT NULL,
    section_id UUID,
    subject_id UUID NOT NULL,
    teacher_id UUID NOT NULL,
    room_name VARCHAR(100) NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_timetable_school_id ON timetable_entries(school_id);
CREATE INDEX IF NOT EXISTS idx_timetable_academic_year_id ON timetable_entries(academic_year_id);
CREATE INDEX IF NOT EXISTS idx_timetable_class_id ON timetable_entries(class_id);
CREATE INDEX IF NOT EXISTS idx_timetable_teacher_id ON timetable_entries(teacher_id);
CREATE INDEX IF NOT EXISTS idx_timetable_subject_id ON timetable_entries(subject_id);
CREATE INDEX IF NOT EXISTS idx_timetable_day_of_week ON timetable_entries(day_of_week);
