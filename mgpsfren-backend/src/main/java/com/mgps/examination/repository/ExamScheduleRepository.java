package com.mgps.examination.repository;

import com.mgps.examination.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, UUID> {
    List<ExamSchedule> findByExamId(UUID examId);
    List<ExamSchedule> findByExamIdAndSubjectId(UUID examId, UUID subjectId);
}
