package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicHouseRepository extends JpaRepository<AcademicHouse, UUID> {
    List<AcademicHouse> findBySchoolId(UUID schoolId);
}
