package com.mgps.staff.service;

import com.mgps.academic.entity.AcademicDepartment;
import com.mgps.academic.repository.AcademicDepartmentRepository;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.staff.dto.StaffDtos.*;
import com.mgps.staff.entity.*;
import com.mgps.staff.repository.StaffAttendanceRepository;
import com.mgps.staff.repository.StaffLeaveRepository;
import com.mgps.staff.repository.StaffMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class StaffService {

    private final StaffMemberRepository staffMemberRepository;
    private final StaffAttendanceRepository attendanceRepository;
    private final StaffLeaveRepository leaveRepository;
    private final AcademicDepartmentRepository departmentRepository;

    public StaffService(StaffMemberRepository staffMemberRepository,
                        StaffAttendanceRepository attendanceRepository,
                        StaffLeaveRepository leaveRepository,
                        AcademicDepartmentRepository departmentRepository) {
        this.staffMemberRepository = staffMemberRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.departmentRepository = departmentRepository;
    }

    public StaffResponse onboardStaff(StaffAdmissionRequest request) {
        AcademicDepartment department = validateDepartment(request.getSchoolId(), request.getDepartmentId());
        String employeeCode = generateEmployeeCode(request.getSchoolId());
        while (staffMemberRepository.existsByEmployeeCode(employeeCode)) {
            employeeCode = generateEmployeeCode(request.getSchoolId());
        }

        StaffMember member = new StaffMember(
            UUID.randomUUID(),
            request.getSchoolId(),
            employeeCode,
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getDesignation(),
            department != null ? department.getId() : null,
            department != null ? department.getName() : null,
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getQualification(),
            request.getExperienceYears(),
            request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now(),
            request.getPayrollEmployeeId(),
            request.getPayrollAccountReference(),
            StaffStatus.ACTIVE,
            null,
            null
        );
        return map(staffMemberRepository.save(member));
    }

    public StaffResponse updateStaff(UUID staffId, StaffUpdateRequest request) {
        StaffMember member = getStaffEntity(staffId);
        if (request.getFirstName() != null) member.setFirstName(request.getFirstName());
        if (request.getLastName() != null) member.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) member.setDateOfBirth(request.getDateOfBirth());
        if (request.getDesignation() != null) member.setDesignation(request.getDesignation());
        if (request.getDepartmentId() != null) {
            AcademicDepartment department = validateDepartment(member.getSchoolId(), request.getDepartmentId());
            member.setDepartmentId(department.getId());
            member.setDepartmentName(department.getName());
        }
        if (request.getEmail() != null) member.setEmail(request.getEmail());
        if (request.getPhone() != null) member.setPhone(request.getPhone());
        if (request.getAddress() != null) member.setAddress(request.getAddress());
        if (request.getQualification() != null) member.setQualification(request.getQualification());
        if (request.getExperienceYears() != null) member.setExperienceYears(request.getExperienceYears());
        if (request.getPayrollEmployeeId() != null) member.setPayrollEmployeeId(request.getPayrollEmployeeId());
        if (request.getPayrollAccountReference() != null) member.setPayrollAccountReference(request.getPayrollAccountReference());
        if (request.getStatus() != null) member.setStatus(request.getStatus());
        return map(staffMemberRepository.save(member));
    }

    public StaffResponse getStaff(UUID staffId) {
        return map(getStaffEntity(staffId));
    }

    public Page<StaffResponse> getStaffBySchool(UUID schoolId, Pageable pageable) {
        return staffMemberRepository.findBySchoolId(schoolId, pageable).map(this::map);
    }

    public List<StaffResponse> getStaffByDepartment(UUID schoolId, UUID departmentId) {
        return staffMemberRepository.findBySchoolIdAndDepartmentId(schoolId, departmentId).stream()
            .map(this::map)
            .collect(Collectors.toList());
    }

    public StaffResponse assignDepartment(UUID staffId, StaffDepartmentRequest request) {
        StaffMember member = getStaffEntity(staffId);
        AcademicDepartment department = validateDepartment(member.getSchoolId(), request.getDepartmentId());
        member.setDepartmentId(department.getId());
        member.setDepartmentName(department.getName());
        if (request.getDesignation() != null) {
            member.setDesignation(request.getDesignation());
        }
        return map(staffMemberRepository.save(member));
    }

    public StaffAttendanceResponse markAttendance(UUID staffId, StaffAttendanceRequest request) {
        StaffMember member = getStaffEntity(staffId);
        LocalDate attendanceDate = request.getAttendanceDate() != null ? request.getAttendanceDate() : LocalDate.now();
        StaffAttendanceRecord record = attendanceRepository.findByStaffIdAndAttendanceDate(staffId, attendanceDate)
            .orElseGet(() -> new StaffAttendanceRecord(UUID.randomUUID(), staffId, attendanceDate, request.getStatus(), request.getRemarks(), null, null));
        record.setStatus(request.getStatus());
        record.setRemarks(request.getRemarks());
        if (request.getStatus() == StaffAttendanceStatus.ON_LEAVE) {
            member.setStatus(StaffStatus.ON_LEAVE);
        } else if (request.getStatus() == StaffAttendanceStatus.PRESENT || request.getStatus() == StaffAttendanceStatus.HALF_DAY) {
            member.setStatus(StaffStatus.ACTIVE);
        } else if (request.getStatus() == StaffAttendanceStatus.ABSENT) {
            member.setStatus(StaffStatus.ACTIVE);
        }
        staffMemberRepository.save(member);
        return mapAttendance(attendanceRepository.save(record));
    }

    public AttendanceSummaryResponse getAttendanceSummary(UUID staffId) {
        getStaffEntity(staffId);
        List<StaffAttendanceRecord> records = attendanceRepository.findByStaffId(staffId);
        AttendanceSummaryResponse summary = new AttendanceSummaryResponse();
        summary.setStaffId(staffId);
        summary.setTotalDays(records.size());
        summary.setPresentDays((int) records.stream().filter(r -> r.getStatus() == StaffAttendanceStatus.PRESENT).count());
        summary.setAbsentDays((int) records.stream().filter(r -> r.getStatus() == StaffAttendanceStatus.ABSENT).count());
        summary.setHalfDays((int) records.stream().filter(r -> r.getStatus() == StaffAttendanceStatus.HALF_DAY).count());
        summary.setLeaveDays((int) records.stream().filter(r -> r.getStatus() == StaffAttendanceStatus.ON_LEAVE).count());
        summary.setAttendancePercentage(summary.getTotalDays() == 0 ? 0.0 :
            (summary.getPresentDays() * 100.0) / summary.getTotalDays());
        return summary;
    }

    public StaffLeaveResponse applyLeave(UUID staffId, StaffLeaveRequest request) {
        StaffMember member = getStaffEntity(staffId);
        StaffLeaveApplication application = new StaffLeaveApplication(
            UUID.randomUUID(),
            staffId,
            request.getSchoolId() != null ? request.getSchoolId() : member.getSchoolId(),
            request.getLeaveType(),
            request.getStartDate(),
            request.getEndDate(),
            request.getReason(),
            StaffLeaveStatus.PENDING,
            null,
            null,
            request.getRemarks(),
            null,
            null
        );
        member.setStatus(StaffStatus.ON_LEAVE);
        staffMemberRepository.save(member);
        return mapLeave(leaveRepository.save(application));
    }

    public List<StaffLeaveResponse> getLeaves(UUID staffId) {
        getStaffEntity(staffId);
        return leaveRepository.findByStaffIdOrderByCreatedAtDesc(staffId).stream().map(this::mapLeave).collect(Collectors.toList());
    }

    public StaffLeaveResponse approveLeave(UUID leaveId, StaffLeaveDecisionRequest request) {
        StaffLeaveApplication application = getLeaveEntity(leaveId);
        application.setStatus(StaffLeaveStatus.APPROVED);
        application.setApprovedBy(request.getApprovedBy());
        application.setApprovedAt(LocalDateTime.now());
        application.setRemarks(request.getRemarks());
        StaffLeaveApplication saved = leaveRepository.save(application);
        updateStaffStatusAfterLeaveDecision(saved.getStaffId(), StaffStatus.ON_LEAVE);
        return mapLeave(saved);
    }

    public StaffLeaveResponse rejectLeave(UUID leaveId, StaffLeaveDecisionRequest request) {
        StaffLeaveApplication application = getLeaveEntity(leaveId);
        application.setStatus(StaffLeaveStatus.REJECTED);
        application.setApprovedBy(request.getApprovedBy());
        application.setApprovedAt(LocalDateTime.now());
        application.setRemarks(request.getRemarks());
        StaffLeaveApplication saved = leaveRepository.save(application);
        updateStaffStatusAfterLeaveDecision(saved.getStaffId(), StaffStatus.ACTIVE);
        return mapLeave(saved);
    }

    private void updateStaffStatusAfterLeaveDecision(UUID staffId, StaffStatus status) {
        StaffMember member = getStaffEntity(staffId);
        member.setStatus(status);
        staffMemberRepository.save(member);
    }

    private AcademicDepartment validateDepartment(UUID schoolId, UUID departmentId) {
        if (departmentId == null) {
            return null;
        }
        AcademicDepartment department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        if (!department.getSchoolId().equals(schoolId)) {
            throw new ResourceNotFoundException("Department does not belong to this school");
        }
        return department;
    }

    private StaffMember getStaffEntity(UUID staffId) {
        return staffMemberRepository.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff member not found"));
    }

    private StaffLeaveApplication getLeaveEntity(UUID leaveId) {
        return leaveRepository.findById(leaveId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff leave application not found"));
    }

    private String generateEmployeeCode(UUID schoolId) {
        String schoolSeed = schoolId != null ? schoolId.toString().replace("-", "").substring(0, 6).toUpperCase() : "SCH";
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "EMP-" + schoolSeed + "-" + random;
    }

    private StaffResponse map(StaffMember member) {
        StaffResponse response = new StaffResponse();
        response.setStaffId(member.getId());
        response.setSchoolId(member.getSchoolId());
        response.setEmployeeCode(member.getEmployeeCode());
        response.setFirstName(member.getFirstName());
        response.setLastName(member.getLastName());
        response.setDateOfBirth(member.getDateOfBirth());
        response.setDesignation(member.getDesignation());
        response.setDepartmentId(member.getDepartmentId());
        response.setDepartmentName(member.getDepartmentName());
        response.setEmail(member.getEmail());
        response.setPhone(member.getPhone());
        response.setAddress(member.getAddress());
        response.setQualification(member.getQualification());
        response.setExperienceYears(member.getExperienceYears());
        response.setJoiningDate(member.getJoiningDate());
        response.setPayrollEmployeeId(member.getPayrollEmployeeId());
        response.setPayrollAccountReference(member.getPayrollAccountReference());
        response.setStatus(member.getStatus());
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());
        return response;
    }

    private StaffAttendanceResponse mapAttendance(StaffAttendanceRecord record) {
        StaffAttendanceResponse response = new StaffAttendanceResponse();
        response.setAttendanceId(record.getId());
        response.setStaffId(record.getStaffId());
        response.setAttendanceDate(record.getAttendanceDate());
        response.setStatus(record.getStatus());
        response.setRemarks(record.getRemarks());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }

    private StaffLeaveResponse mapLeave(StaffLeaveApplication application) {
        StaffLeaveResponse response = new StaffLeaveResponse();
        response.setLeaveId(application.getId());
        response.setStaffId(application.getStaffId());
        response.setSchoolId(application.getSchoolId());
        response.setLeaveType(application.getLeaveType());
        response.setStartDate(application.getStartDate());
        response.setEndDate(application.getEndDate());
        response.setReason(application.getReason());
        response.setStatus(application.getStatus());
        response.setApprovedBy(application.getApprovedBy());
        response.setApprovedAt(application.getApprovedAt());
        response.setRemarks(application.getRemarks());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }

    public static class StaffAttendanceResponse {
        private UUID attendanceId;
        private UUID staffId;
        private LocalDate attendanceDate;
        private StaffAttendanceStatus status;
        private String remarks;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getAttendanceId() { return attendanceId; }
        public void setAttendanceId(UUID attendanceId) { this.attendanceId = attendanceId; }
        public UUID getStaffId() { return staffId; }
        public void setStaffId(UUID staffId) { this.staffId = staffId; }
        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
        public StaffAttendanceStatus getStatus() { return status; }
        public void setStatus(StaffAttendanceStatus status) { this.status = status; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
