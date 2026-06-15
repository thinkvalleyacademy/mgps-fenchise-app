-- Align tenant databases with the current JPA entities.
-- Tenant databases do not contain the master schools table, so school_id is kept
-- as a tenant-scoping column without a foreign key to schools(id).

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Academic structure
ALTER TABLE academic_years ADD COLUMN IF NOT EXISTS school_id UUID;
ALTER TABLE academic_years ADD COLUMN IF NOT EXISTS name VARCHAR(100);
UPDATE academic_years SET name = year_name WHERE name IS NULL AND year_name IS NOT NULL;
ALTER TABLE academic_years ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'academic_years'
          AND column_name = 'year_name'
    ) THEN
        ALTER TABLE academic_years ALTER COLUMN year_name DROP NOT NULL;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS academic_classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    academic_year_id UUID,
    name VARCHAR(100),
    grade_level VARCHAR(50),
    code VARCHAR(50),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id) ON DELETE CASCADE
);

INSERT INTO academic_classes (id, academic_year_id, name, grade_level, created_at)
SELECT id, academic_year_id, class_name, class_level::VARCHAR, created_at
FROM classes
WHERE EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE table_schema = 'public'
      AND table_name = 'classes'
)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS academic_subjects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    class_id UUID,
    name VARCHAR(150),
    code VARCHAR(50),
    subject_type VARCHAR(50),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS academic_sections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    class_id UUID,
    name VARCHAR(100),
    capacity INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS academic_streams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    class_id UUID,
    name VARCHAR(100),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS academic_departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    name VARCHAR(150),
    code VARCHAR(50),
    head_name VARCHAR(150),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS academic_houses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    name VARCHAR(150),
    color VARCHAR(50),
    motto VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students
ALTER TABLE students ADD COLUMN IF NOT EXISTS school_id UUID;
ALTER TABLE students ADD COLUMN IF NOT EXISTS admission_number VARCHAR(50);
UPDATE students SET admission_number = enrollment_number WHERE admission_number IS NULL AND enrollment_number IS NOT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE students ADD COLUMN IF NOT EXISTS parent_name VARCHAR(150);
UPDATE students SET parent_name = guardian_name WHERE parent_name IS NULL AND guardian_name IS NOT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS parent_phone VARCHAR(20);
UPDATE students SET parent_phone = guardian_contact WHERE parent_phone IS NULL AND guardian_contact IS NOT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS medical_info TEXT;
ALTER TABLE students ADD COLUMN IF NOT EXISTS photo_url VARCHAR(255);
ALTER TABLE students ADD COLUMN IF NOT EXISTS academic_year_id UUID;
ALTER TABLE students ADD COLUMN IF NOT EXISTS section_id UUID;
ALTER TABLE students ADD COLUMN IF NOT EXISTS admission_date DATE;
UPDATE students SET admission_date = enrollment_date WHERE admission_date IS NULL AND enrollment_date IS NOT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS transfer_date DATE;
ALTER TABLE students ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ADMITTED';
UPDATE students SET status = CASE WHEN is_active IS FALSE THEN 'INACTIVE' ELSE 'ADMITTED' END WHERE status IS NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'students'
          AND column_name = 'enrollment_date'
    ) THEN
        ALTER TABLE students ALTER COLUMN enrollment_date DROP NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'students'
          AND column_name = 'class_id'
          AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE students
            ALTER COLUMN class_id TYPE UUID
            USING CASE
                WHEN class_id ~* '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
                THEN class_id::UUID
                ELSE NULL
            END;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS student_attendance_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, attendance_date)
);

CREATE TABLE IF NOT EXISTS student_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    school_id UUID,
    document_type VARCHAR(100) NOT NULL,
    document_number VARCHAR(100),
    file_name VARCHAR(255),
    file_url VARCHAR(255),
    remarks TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Staff
CREATE TABLE IF NOT EXISTS staff_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    employee_code VARCHAR(50) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    designation VARCHAR(150),
    department_id UUID REFERENCES academic_departments(id) ON DELETE SET NULL,
    department_name VARCHAR(150),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    qualification TEXT,
    experience_years INTEGER,
    joining_date DATE,
    payroll_employee_id VARCHAR(100),
    payroll_account_reference VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO staff_members (
    id, first_name, last_name, email, phone, employee_code, designation,
    department_name, date_of_birth, address, qualification, joining_date,
    status, created_at, updated_at
)
SELECT id, first_name, last_name, email, phone, employee_id, designation,
       department, date_of_birth, address, qualification, date_of_joining,
       CASE WHEN is_active IS FALSE THEN 'INACTIVE' ELSE 'ACTIVE' END,
       created_at, updated_at
FROM staff
WHERE EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE table_schema = 'public'
      AND table_name = 'staff'
)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS staff_attendance_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff_members(id) ON DELETE CASCADE,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (staff_id, attendance_date)
);

CREATE TABLE IF NOT EXISTS staff_leave_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff_members(id) ON DELETE CASCADE,
    school_id UUID,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    approved_by VARCHAR(150),
    approved_at TIMESTAMP,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Timetable
CREATE TABLE IF NOT EXISTS timetable_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    academic_year_id UUID REFERENCES academic_years(id) ON DELETE CASCADE,
    class_id UUID REFERENCES academic_classes(id) ON DELETE CASCADE,
    section_id UUID,
    subject_id UUID,
    teacher_id UUID,
    room_name VARCHAR(100),
    day_of_week VARCHAR(20),
    start_time TIME,
    end_time TIME,
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fees
CREATE TABLE IF NOT EXISTS fee_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    name VARCHAR(100),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fee_structures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    academic_year_id UUID,
    class_id UUID,
    fee_category_id UUID REFERENCES fee_categories(id),
    amount DECIMAL(12, 2),
    due_date DATE,
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    recurrence_type VARCHAR(20) DEFAULT 'ONE_TIME',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student_fees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    student_id UUID REFERENCES students(id) ON DELETE CASCADE,
    fee_structure_id UUID REFERENCES fee_structures(id),
    amount_due DECIMAL(12, 2),
    amount_paid DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UNPAID',
    due_date DATE,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    discount_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fee_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID,
    student_fee_id UUID REFERENCES student_fees(id),
    amount_paid DECIMAL(12, 2),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_mode VARCHAR(50),
    transaction_id VARCHAR(100),
    receipt_number VARCHAR(50) UNIQUE,
    remarks TEXT,
    processed_by UUID,
    month_from INTEGER,
    month_to INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_academic_years_school_id ON academic_years(school_id);
CREATE INDEX IF NOT EXISTS idx_academic_classes_school_id ON academic_classes(school_id);
CREATE INDEX IF NOT EXISTS idx_academic_classes_year_id ON academic_classes(academic_year_id);
CREATE INDEX IF NOT EXISTS idx_academic_subjects_class_id ON academic_subjects(class_id);
CREATE INDEX IF NOT EXISTS idx_academic_sections_class_id ON academic_sections(class_id);
CREATE INDEX IF NOT EXISTS idx_students_school_id ON students(school_id);
CREATE INDEX IF NOT EXISTS idx_students_admission_number ON students(admission_number);
CREATE INDEX IF NOT EXISTS idx_students_class_id ON students(class_id);
CREATE INDEX IF NOT EXISTS idx_student_documents_student_id ON student_documents(student_id);
CREATE INDEX IF NOT EXISTS idx_staff_members_school_id ON staff_members(school_id);
CREATE INDEX IF NOT EXISTS idx_staff_attendance_staff_id ON staff_attendance_records(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_leave_staff_id ON staff_leave_applications(staff_id);
CREATE INDEX IF NOT EXISTS idx_timetable_school_id ON timetable_entries(school_id);
CREATE INDEX IF NOT EXISTS idx_timetable_academic_year_id ON timetable_entries(academic_year_id);
CREATE INDEX IF NOT EXISTS idx_fee_categories_school ON fee_categories(school_id);
CREATE INDEX IF NOT EXISTS idx_fee_structures_school ON fee_structures(school_id);
CREATE INDEX IF NOT EXISTS idx_student_fees_school ON student_fees(school_id);
CREATE INDEX IF NOT EXISTS idx_fee_payments_school ON fee_payments(school_id);
