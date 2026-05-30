-- Staff management tables

CREATE TABLE IF NOT EXISTS staff_members (
    id UUID PRIMARY KEY,
    school_id UUID NOT NULL,
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    designation VARCHAR(150),
    department_id UUID,
    department_name VARCHAR(150),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    qualification TEXT,
    experience_years INTEGER,
    joining_date DATE,
    payroll_employee_id VARCHAR(100),
    payroll_account_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES academic_departments(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS staff_attendance_records (
    id UUID PRIMARY KEY,
    staff_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff_members(id) ON DELETE CASCADE,
    UNIQUE (staff_id, attendance_date)
);

CREATE TABLE IF NOT EXISTS staff_leave_applications (
    id UUID PRIMARY KEY,
    staff_id UUID NOT NULL,
    school_id UUID NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(150),
    approved_at TIMESTAMP,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff_members(id) ON DELETE CASCADE,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_staff_members_school_id ON staff_members(school_id);
CREATE INDEX IF NOT EXISTS idx_staff_members_department_id ON staff_members(department_id);
CREATE INDEX IF NOT EXISTS idx_staff_attendance_staff_id ON staff_attendance_records(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_attendance_date ON staff_attendance_records(attendance_date);
CREATE INDEX IF NOT EXISTS idx_staff_leave_staff_id ON staff_leave_applications(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_leave_school_id ON staff_leave_applications(school_id);
