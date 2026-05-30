package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicDepartmentRepository extends JpaRepository<AcademicDepartment, UUID> {
    List<AcademicDepartment> findBySchoolId(UUID schoolId);
}
