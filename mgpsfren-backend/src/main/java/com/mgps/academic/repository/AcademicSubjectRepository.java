package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicSubjectRepository extends JpaRepository<AcademicSubject, UUID> {
    List<AcademicSubject> findBySchoolId(UUID schoolId);
    List<AcademicSubject> findByClassId(UUID classId);
}
