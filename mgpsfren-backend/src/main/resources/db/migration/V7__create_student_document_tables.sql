-- Student document tables

CREATE TABLE IF NOT EXISTS student_documents (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    school_id UUID NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    document_number VARCHAR(100),
    file_name VARCHAR(255),
    file_url VARCHAR(255),
    remarks TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_student_documents_student_id ON student_documents(student_id);
CREATE INDEX IF NOT EXISTS idx_student_documents_school_id ON student_documents(school_id);
