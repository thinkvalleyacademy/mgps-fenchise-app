package com.mgps.examination.repository;

import com.mgps.examination.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findBySchoolIdAndAcademicYearId(UUID schoolId, UUID academicYearId);
    List<Exam> findBySchoolId(UUID schoolId);
}
