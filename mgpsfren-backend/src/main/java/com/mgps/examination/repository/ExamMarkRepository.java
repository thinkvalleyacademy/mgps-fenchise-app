package com.mgps.examination.repository;

import com.mgps.examination.entity.ExamMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamMarkRepository extends JpaRepository<ExamMark, UUID> {
    List<ExamMark> findByExamScheduleId(UUID examScheduleId);
    Optional<ExamMark> findByExamScheduleIdAndStudentId(UUID examScheduleId, UUID studentId);
    List<ExamMark> findByExamIdAndStudentId(UUID examId, UUID studentId);
    List<ExamMark> findByExamId(UUID examId);
    List<ExamMark> findByExamIdAndSubjectId(UUID examId, UUID subjectId);
}
