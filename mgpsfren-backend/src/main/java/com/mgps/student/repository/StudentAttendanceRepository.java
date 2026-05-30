package com.mgps.student.repository;

import com.mgps.student.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, UUID> {
    Optional<StudentAttendance> findByStudentIdAndAttendanceDate(UUID studentId, LocalDate attendanceDate);
    List<StudentAttendance> findByStudentId(UUID studentId);
    List<StudentAttendance> findByStudentIdAndAttendanceDateBetween(UUID studentId, LocalDate startDate, LocalDate endDate);
}
