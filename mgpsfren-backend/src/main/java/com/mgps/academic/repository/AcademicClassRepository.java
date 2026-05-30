package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicClassRepository extends JpaRepository<AcademicClass, UUID> {
    List<AcademicClass> findBySchoolId(UUID schoolId);
    List<AcademicClass> findBySchoolIdAndAcademicYearId(UUID schoolId, UUID academicYearId);
}
