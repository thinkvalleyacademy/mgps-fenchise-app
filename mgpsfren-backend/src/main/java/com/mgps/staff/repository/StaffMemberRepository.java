package com.mgps.staff.repository;

import com.mgps.staff.entity.StaffMember;
import com.mgps.staff.entity.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {
    boolean existsByEmployeeCode(String employeeCode);
    Optional<StaffMember> findBySchoolIdAndEmployeeCode(UUID schoolId, String employeeCode);
    Page<StaffMember> findBySchoolId(UUID schoolId, Pageable pageable);
    List<StaffMember> findBySchoolIdAndDepartmentId(UUID schoolId, UUID departmentId);
    Page<StaffMember> findByStatus(StaffStatus status, Pageable pageable);
}
