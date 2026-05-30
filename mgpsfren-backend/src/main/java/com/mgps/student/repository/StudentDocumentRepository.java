package com.mgps.student.repository;

import com.mgps.student.entity.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, UUID> {
    List<StudentDocument> findByStudentIdOrderByUploadedAtDesc(UUID studentId);
}
