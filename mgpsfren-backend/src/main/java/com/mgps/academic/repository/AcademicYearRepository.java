package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    List<AcademicYear> findBySchoolId(UUID schoolId);
    Optional<AcademicYear> findBySchoolIdAndIsActiveTrue(UUID schoolId);
}
