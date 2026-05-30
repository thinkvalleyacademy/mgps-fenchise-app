package com.mgps.staff.service;

import com.mgps.academic.entity.AcademicDepartment;
import com.mgps.academic.repository.AcademicDepartmentRepository;
import com.mgps.staff.dto.StaffDtos.*;
import com.mgps.staff.entity.*;
import com.mgps.staff.repository.StaffAttendanceRepository;
import com.mgps.staff.repository.StaffLeaveRepository;
import com.mgps.staff.repository.StaffMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private StaffAttendanceRepository attendanceRepository;
    @Mock private StaffLeaveRepository leaveRepository;
    @Mock private AcademicDepartmentRepository departmentRepository;

    @InjectMocks
    private StaffService staffService;

    private UUID schoolId;
    private UUID departmentId;

    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        departmentId = UUID.randomUUID();
    }

    @Test
    void shouldOnboardStaffAndAssignDepartment() {
        AcademicDepartment department = new AcademicDepartment(departmentId, schoolId, "Science", "SCI", "Head", true, null, null);
        StaffMember saved = new StaffMember(UUID.randomUUID(), schoolId, "EMP-SCH-1234", "John", "Doe", null, "Teacher", departmentId, "Science",
            "john@example.com", "9999999999", "Address", "MSc", 5, LocalDate.of(2025, 6, 1), "PAY-1", "ACC-1", StaffStatus.ACTIVE, null, null);

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(staffMemberRepository.existsByEmployeeCode(any())).thenReturn(false);
        when(staffMemberRepository.save(any(StaffMember.class))).thenReturn(saved);

        StaffAdmissionRequest request = new StaffAdmissionRequest();
        request.setSchoolId(schoolId);
        request.setDepartmentId(departmentId);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDesignation("Teacher");
        request.setEmail("john@example.com");
        request.setPhone("9999999999");
        request.setQualification("MSc");
        request.setExperienceYears(5);

        StaffResponse response = staffService.onboardStaff(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getDepartmentName()).isEqualTo("Science");
        assertThat(response.getStatus()).isEqualTo(StaffStatus.ACTIVE);
    }

    @Test
    void shouldMarkAttendanceApplyAndResolveLeave() {
        UUID staffId = UUID.randomUUID();
        StaffMember staff = new StaffMember(staffId, schoolId, "EMP-SCH-5678", "John", "Doe", null, "Teacher", departmentId, "Science",
            "john@example.com", "9999999999", "Address", "MSc", 5, LocalDate.now(), "PAY-1", "ACC-1", StaffStatus.ACTIVE, null, null);
        when(staffMemberRepository.findById(staffId)).thenReturn(Optional.of(staff));
        when(attendanceRepository.findByStaffIdAndAttendanceDate(staffId, LocalDate.of(2026, 1, 15))).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(StaffAttendanceRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(attendanceRepository.findByStaffId(staffId)).thenReturn(List.of(
            new StaffAttendanceRecord(UUID.randomUUID(), staffId, LocalDate.of(2026, 1, 15), StaffAttendanceStatus.PRESENT, null, null, null),
            new StaffAttendanceRecord(UUID.randomUUID(), staffId, LocalDate.of(2026, 1, 16), StaffAttendanceStatus.ABSENT, null, null, null)
        ));
        when(leaveRepository.save(any(StaffLeaveApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(leaveRepository.findById(any())).thenAnswer(invocation -> Optional.of(
            new StaffLeaveApplication((UUID) invocation.getArgument(0), staffId, schoolId, StaffLeaveType.CASUAL, LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 2), "Reason", StaffLeaveStatus.PENDING, null, null, "Remarks", null, null)
        ));
        when(staffMemberRepository.save(any(StaffMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StaffAttendanceRequest attendanceRequest = new StaffAttendanceRequest();
        attendanceRequest.setAttendanceDate(LocalDate.of(2026, 1, 15));
        attendanceRequest.setStatus(StaffAttendanceStatus.PRESENT);
        attendanceRequest.setRemarks("On time");

        StaffService.StaffAttendanceResponse attendance = staffService.markAttendance(staffId, attendanceRequest);
        AttendanceSummaryResponse summary = staffService.getAttendanceSummary(staffId);

        StaffLeaveRequest leaveRequest = new StaffLeaveRequest();
        leaveRequest.setSchoolId(schoolId);
        leaveRequest.setLeaveType(StaffLeaveType.CASUAL);
        leaveRequest.setStartDate(LocalDate.of(2026, 2, 1));
        leaveRequest.setEndDate(LocalDate.of(2026, 2, 2));
        leaveRequest.setReason("Personal work");
        leaveRequest.setRemarks("Need leave");
        StaffLeaveResponse applied = staffService.applyLeave(staffId, leaveRequest);
        StaffLeaveResponse approved = staffService.approveLeave(applied.getLeaveId(), decision("Principal"));

        assertThat(attendance.getStatus()).isEqualTo(StaffAttendanceStatus.PRESENT);
        assertThat(summary.getTotalDays()).isEqualTo(2);
        assertThat(summary.getPresentDays()).isEqualTo(1);
        assertThat(applied.getStatus()).isEqualTo(StaffLeaveStatus.PENDING);
        assertThat(approved.getStatus()).isEqualTo(StaffLeaveStatus.APPROVED);
    }

    @Test
    void shouldListStaffBySchool() {
        StaffMember staff = new StaffMember(UUID.randomUUID(), schoolId, "EMP-SCH-9999", "John", "Doe", null, "Teacher", null, null,
            "john@example.com", "9999999999", "Address", "MSc", 5, LocalDate.now(), null, null, StaffStatus.ACTIVE, null, null);
        when(staffMemberRepository.findBySchoolId(schoolId, PageRequest.of(0, 20))).thenReturn(new PageImpl<>(List.of(staff)));

        var page = staffService.getStaffBySchool(schoolId, PageRequest.of(0, 20));

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    private StaffLeaveDecisionRequest decision(String approvedBy) {
        StaffLeaveDecisionRequest request = new StaffLeaveDecisionRequest();
        request.setApprovedBy(approvedBy);
        request.setRemarks("Approved");
        return request;
    }
}
