package com.mgps.staff.repository;

import com.mgps.staff.entity.StaffAttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendanceRecord, UUID> {
    Optional<StaffAttendanceRecord> findByStaffIdAndAttendanceDate(UUID staffId, LocalDate attendanceDate);
    List<StaffAttendanceRecord> findByStaffId(UUID staffId);
}
