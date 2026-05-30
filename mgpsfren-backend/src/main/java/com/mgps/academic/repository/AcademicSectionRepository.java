package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicSectionRepository extends JpaRepository<AcademicSection, UUID> {
    List<AcademicSection> findBySchoolId(UUID schoolId);
    List<AcademicSection> findByClassId(UUID classId);
}
