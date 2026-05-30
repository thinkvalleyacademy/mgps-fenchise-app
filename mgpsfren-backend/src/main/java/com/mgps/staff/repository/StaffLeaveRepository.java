package com.mgps.staff.repository;

import com.mgps.staff.entity.StaffLeaveApplication;
import com.mgps.staff.entity.StaffLeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StaffLeaveRepository extends JpaRepository<StaffLeaveApplication, UUID> {
    List<StaffLeaveApplication> findByStaffIdOrderByCreatedAtDesc(UUID staffId);
    List<StaffLeaveApplication> findBySchoolIdAndStatus(UUID schoolId, StaffLeaveStatus status);
}
