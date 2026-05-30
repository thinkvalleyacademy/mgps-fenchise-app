package com.mgps.fee.repository;

import com.mgps.fee.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, UUID> {
    List<FeeStructure> findBySchoolId(UUID schoolId);
    List<FeeStructure> findBySchoolIdAndAcademicYearId(UUID schoolId, UUID academicYearId);
}
