package com.mgps.staff.dto;

import com.mgps.staff.entity.StaffAttendanceStatus;
import com.mgps.staff.entity.StaffLeaveStatus;
import com.mgps.staff.entity.StaffLeaveType;
import com.mgps.staff.entity.StaffStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class StaffDtos {
    private StaffDtos() {
    }

    public static class StaffAdmissionRequest {
        private UUID schoolId;
        private UUID departmentId;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String designation;
        private String email;
        private String phone;
        private String address;
        private String qualification;
        private Integer experienceYears;
        private LocalDate joiningDate;
        private String payrollEmployeeId;
        private String payrollAccountReference;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getDepartmentId() { return departmentId; }
        public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getQualification() { return qualification; }
        public void setQualification(String qualification) { this.qualification = qualification; }
        public Integer getExperienceYears() { return experienceYears; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public LocalDate getJoiningDate() { return joiningDate; }
        public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
        public String getPayrollEmployeeId() { return payrollEmployeeId; }
        public void setPayrollEmployeeId(String payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }
        public String getPayrollAccountReference() { return payrollAccountReference; }
        public void setPayrollAccountReference(String payrollAccountReference) { this.payrollAccountReference = payrollAccountReference; }
    }

    public static class StaffUpdateRequest {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String designation;
        private UUID departmentId;
        private String email;
        private String phone;
        private String address;
        private String qualification;
        private Integer experienceYears;
        private String payrollEmployeeId;
        private String payrollAccountReference;
        private StaffStatus status;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public UUID getDepartmentId() { return departmentId; }
        public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getQualification() { return qualification; }
        public void setQualification(String qualification) { this.qualification = qualification; }
        public Integer getExperienceYears() { return experienceYears; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public String getPayrollEmployeeId() { return payrollEmployeeId; }
        public void setPayrollEmployeeId(String payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }
        public String getPayrollAccountReference() { return payrollAccountReference; }
        public void setPayrollAccountReference(String payrollAccountReference) { this.payrollAccountReference = payrollAccountReference; }
        public StaffStatus getStatus() { return status; }
        public void setStatus(StaffStatus status) { this.status = status; }
    }

    public static class StaffDepartmentRequest {
        private UUID departmentId;
        private String designation;

        public UUID getDepartmentId() { return departmentId; }
        public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
    }

    public static class StaffAttendanceRequest {
        private LocalDate attendanceDate;
        private StaffAttendanceStatus status;
        private String remarks;

        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
        public StaffAttendanceStatus getStatus() { return status; }
        public void setStatus(StaffAttendanceStatus status) { this.status = status; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class StaffLeaveRequest {
        private UUID schoolId;
        private StaffLeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private String remarks;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public StaffLeaveType getLeaveType() { return leaveType; }
        public void setLeaveType(StaffLeaveType leaveType) { this.leaveType = leaveType; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class StaffLeaveDecisionRequest {
        private String approvedBy;
        private String remarks;

        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class AttendanceSummaryResponse {
        private UUID staffId;
        private int totalDays;
        private int presentDays;
        private int absentDays;
        private int halfDays;
        private int leaveDays;
        private double attendancePercentage;

        public UUID getStaffId() { return staffId; }
        public void setStaffId(UUID staffId) { this.staffId = staffId; }
        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
        public int getPresentDays() { return presentDays; }
        public void setPresentDays(int presentDays) { this.presentDays = presentDays; }
        public int getAbsentDays() { return absentDays; }
        public void setAbsentDays(int absentDays) { this.absentDays = absentDays; }
        public int getHalfDays() { return halfDays; }
        public void setHalfDays(int halfDays) { this.halfDays = halfDays; }
        public int getLeaveDays() { return leaveDays; }
        public void setLeaveDays(int leaveDays) { this.leaveDays = leaveDays; }
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }

    public static class StaffResponse {
        private UUID staffId;
        private UUID schoolId;
        private String employeeCode;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String designation;
        private UUID departmentId;
        private String departmentName;
        private String email;
        private String phone;
        private String address;
        private String qualification;
        private Integer experienceYears;
        private LocalDate joiningDate;
        private String payrollEmployeeId;
        private String payrollAccountReference;
        private StaffStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getStaffId() { return staffId; }
        public void setStaffId(UUID staffId) { this.staffId = staffId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public UUID getDepartmentId() { return departmentId; }
        public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getQualification() { return qualification; }
        public void setQualification(String qualification) { this.qualification = qualification; }
        public Integer getExperienceYears() { return experienceYears; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public LocalDate getJoiningDate() { return joiningDate; }
        public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
        public String getPayrollEmployeeId() { return payrollEmployeeId; }
        public void setPayrollEmployeeId(String payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }
        public String getPayrollAccountReference() { return payrollAccountReference; }
        public void setPayrollAccountReference(String payrollAccountReference) { this.payrollAccountReference = payrollAccountReference; }
        public StaffStatus getStatus() { return status; }
        public void setStatus(StaffStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class StaffLeaveResponse {
        private UUID leaveId;
        private UUID staffId;
        private UUID schoolId;
        private StaffLeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private StaffLeaveStatus status;
        private String approvedBy;
        private LocalDateTime approvedAt;
        private String remarks;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getLeaveId() { return leaveId; }
        public void setLeaveId(UUID leaveId) { this.leaveId = leaveId; }
        public UUID getStaffId() { return staffId; }
        public void setStaffId(UUID staffId) { this.staffId = staffId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public StaffLeaveType getLeaveType() { return leaveType; }
        public void setLeaveType(StaffLeaveType leaveType) { this.leaveType = leaveType; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public StaffLeaveStatus getStatus() { return status; }
        public void setStatus(StaffLeaveStatus status) { this.status = status; }
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
        public LocalDateTime getApprovedAt() { return approvedAt; }
        public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
