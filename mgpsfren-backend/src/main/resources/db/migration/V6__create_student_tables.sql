-- V6__create_student_tables.sql
-- Student management tables

CREATE TABLE IF NOT EXISTS students (
    id UUID PRIMARY KEY,
    school_id UUID NOT NULL,
    admission_number VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    parent_name VARCHAR(150),
    parent_phone VARCHAR(20),
    medical_info TEXT,
    photo_url VARCHAR(255),
    academic_year_id UUID,
    class_id UUID,
    section_id UUID,
    admission_date DATE NOT NULL,
    transfer_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ADMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id) ON DELETE SET NULL,
    FOREIGN KEY (class_id) REFERENCES academic_classes(id) ON DELETE SET NULL,
    FOREIGN KEY (section_id) REFERENCES academic_sections(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS student_attendance_records (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE (student_id, attendance_date)
);

CREATE INDEX IF NOT EXISTS idx_students_school_id ON students(school_id);
CREATE INDEX IF NOT EXISTS idx_students_admission_number ON students(admission_number);
CREATE INDEX IF NOT EXISTS idx_students_class_id ON students(class_id);
CREATE INDEX IF NOT EXISTS idx_students_section_id ON students(section_id);
CREATE INDEX IF NOT EXISTS idx_student_attendance_student_id ON student_attendance_records(student_id);
CREATE INDEX IF NOT EXISTS idx_student_attendance_date ON student_attendance_records(attendance_date);
