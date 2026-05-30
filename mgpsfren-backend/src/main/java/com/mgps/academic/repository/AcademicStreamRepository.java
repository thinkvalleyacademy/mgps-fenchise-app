package com.mgps.academic.repository;

import com.mgps.academic.entity.AcademicStream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicStreamRepository extends JpaRepository<AcademicStream, UUID> {
    List<AcademicStream> findBySchoolId(UUID schoolId);
    List<AcademicStream> findByClassId(UUID classId);
}
