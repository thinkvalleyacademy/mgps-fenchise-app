package com.mgps.student.repository;

import com.mgps.student.entity.Student;
import com.mgps.student.entity.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByAdmissionNumber(String admissionNumber);
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    Optional<Student> findBySchoolIdAndAdmissionNumber(UUID schoolId, String admissionNumber);
    Page<Student> findBySchoolId(UUID schoolId, Pageable pageable);
    List<Student> findBySchoolIdAndClassId(UUID schoolId, UUID classId);
    List<Student> findBySchoolIdAndAcademicYearId(UUID schoolId, UUID academicYearId);
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
}
