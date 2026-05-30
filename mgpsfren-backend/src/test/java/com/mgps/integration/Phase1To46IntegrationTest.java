package com.mgps.integration;

import com.mgps.academic.dto.AcademicDtos.*;
import com.mgps.academic.dto.TimetableDtos.*;
import com.mgps.academic.entity.AcademicDepartment;
import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.entity.TimetableEntry;
import com.mgps.academic.repository.*;
import com.mgps.academic.service.AcademicStructureService;
import com.mgps.academic.service.TimetableService;
import com.mgps.staff.dto.StaffDtos.*;
import com.mgps.staff.entity.*;
import com.mgps.staff.repository.*;
import com.mgps.staff.service.StaffService;
import com.mgps.student.dto.StudentDtos.*;
import com.mgps.student.entity.*;
import com.mgps.student.repository.*;
import com.mgps.student.service.StudentService;
import com.mgps.tenant.RoutingDataSource;
import com.mgps.user.entity.UserPermission;
import com.mgps.user.entity.UserRole;
import com.mgps.user.service.RolePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Phase1To46IntegrationTest.TestConfig.class)
class Phase1To46IntegrationTest {

    @Autowired private RolePermissionService rolePermissionService;
    @Autowired private AcademicStructureService academicStructureService;
    @Autowired private TimetableService timetableService;
    @Autowired private StudentService studentService;
    @Autowired private StaffService staffService;

    @Autowired private AcademicYearRepository academicYearRepository;
    @Autowired private AcademicClassRepository academicClassRepository;
    @Autowired private AcademicSectionRepository academicSectionRepository;
    @Autowired private AcademicStreamRepository academicStreamRepository;
    @Autowired private AcademicSubjectRepository academicSubjectRepository;
    @Autowired private AcademicDepartmentRepository academicDepartmentRepository;
    @Autowired private AcademicHouseRepository academicHouseRepository;
    @Autowired private TimetableEntryRepository timetableEntryRepository;

    @Autowired private StudentRepository studentRepository;
    @Autowired private StudentDocumentRepository studentDocumentRepository;
    @Autowired private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired private StaffMemberRepository staffMemberRepository;
    @Autowired private StaffAttendanceRepository staffAttendanceRepository;
    @Autowired private StaffLeaveRepository staffLeaveRepository;

    @Test
    void shouldResolveCorePermissions() {
        assertThat(rolePermissionService.getPermissionsForRole(UserRole.SUPER_ADMIN))
            .contains(UserPermission.STUDENT_MANAGE, UserPermission.STAFF_MANAGE);
        assertThat(rolePermissionService.getPermissionsForRole(UserRole.TEACHER))
            .contains(UserPermission.STUDENT_MANAGE, UserPermission.STAFF_MANAGE);
    }

    @Test
    void shouldCreateAcademicAndTimetableFlow() {
        UUID schoolId = UUID.randomUUID();

        when(academicYearRepository.save(any(AcademicYear.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicClassRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicSectionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicStreamRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicSubjectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicDepartmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(academicHouseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AcademicYearResponse year = academicStructureService.createAcademicYear(academicYearRequest(schoolId));
        AcademicClassResponse academicClass = academicStructureService.createAcademicClass(academicClassRequest(schoolId, year.getYearId()));
        AcademicSimpleResponse department = academicStructureService.createDepartment(academicDepartmentRequest(schoolId));
        AcademicSimpleResponse subject = academicStructureService.createSubject(academicSubjectRequest(schoolId, academicClass.getClassId()));
        AcademicSimpleResponse section = academicStructureService.createSection(academicSectionRequest(schoolId, academicClass.getClassId()));

        when(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(schoolId, year.getYearId(), "MONDAY"))
            .thenReturn(List.of());
        when(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndClassId(schoolId, year.getYearId(), academicClass.getClassId()))
            .thenReturn(List.of());
        when(timetableEntryRepository.findBySchoolIdAndAcademicYearId(schoolId, year.getYearId()))
            .thenReturn(List.of());
        when(timetableEntryRepository.save(any(TimetableEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TimetableRequest timetableRequest = new TimetableRequest();
        timetableRequest.setSchoolId(schoolId);
        timetableRequest.setAcademicYearId(year.getYearId());
        timetableRequest.setClassId(academicClass.getClassId());
        timetableRequest.setSectionId(section.getId());
        timetableRequest.setSubjectId(subject.getId());
        timetableRequest.setTeacherId(UUID.randomUUID());
        timetableRequest.setRoomName("Room A");
        timetableRequest.setDayOfWeek("MONDAY");
        timetableRequest.setStartTime(LocalTime.of(9, 0));
        timetableRequest.setEndTime(LocalTime.of(9, 45));
        timetableRequest.setNotes("Integration test slot");

        TimetableResponse timetable = timetableService.createTimetableEntry(timetableRequest);

        assertThat(year.getSchoolId()).isEqualTo(schoolId);
        assertThat(academicClass.getAcademicYearId()).isEqualTo(year.getYearId());
        assertThat(department.getSchoolId()).isEqualTo(schoolId);
        assertThat(timetable.getRoomName()).isEqualTo("Room A");
    }

    @Test
    void shouldAdmitStudentAndProcessStaffFlows() {
        UUID schoolId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        AcademicYear year = new AcademicYear(academicYearId, schoolId, "2026-2027", LocalDate.of(2026, 6, 1), LocalDate.of(2027, 5, 31), true, null, null);
        com.mgps.academic.entity.AcademicClass academicClass = new com.mgps.academic.entity.AcademicClass(classId, schoolId, academicYearId, "Class 5", "5", null, true, null, null);
        com.mgps.academic.entity.AcademicSection section = new com.mgps.academic.entity.AcademicSection(sectionId, schoolId, classId, "A", 40, true, null, null);
        AcademicDepartment department = new AcademicDepartment(departmentId, schoolId, "Science", "SCI", "Head", true, null, null);

        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(year));
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(academicClass));
        when(academicSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(academicDepartmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        AtomicReference<Student> savedStudent = new AtomicReference<>();
        when(studentRepository.existsByAdmissionNumber(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            savedStudent.set(saved);
            return saved;
        });
        when(studentAttendanceRepository.findByStudentIdAndAttendanceDate(any(), any())).thenReturn(Optional.empty());
        when(studentAttendanceRepository.save(any(StudentAttendance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentAttendanceRepository.findByStudentId(any())).thenReturn(List.of(
            new StudentAttendance(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 1, 15), AttendanceStatus.PRESENT, null, null, null),
            new StudentAttendance(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 1, 16), AttendanceStatus.ABSENT, null, null, null)
        ));

        AtomicReference<StaffMember> savedStaff = new AtomicReference<>();
        when(staffMemberRepository.existsByEmployeeCode(anyString())).thenReturn(false);
        when(staffMemberRepository.save(any(StaffMember.class))).thenAnswer(invocation -> {
            StaffMember saved = invocation.getArgument(0);
            savedStaff.set(saved);
            return saved;
        });
        when(staffAttendanceRepository.findByStaffIdAndAttendanceDate(any(), any())).thenReturn(Optional.empty());
        when(staffAttendanceRepository.save(any(StaffAttendanceRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(staffAttendanceRepository.findByStaffId(any())).thenReturn(List.of(
            new StaffAttendanceRecord(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 1, 15), StaffAttendanceStatus.PRESENT, null, null, null),
            new StaffAttendanceRecord(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, 1, 16), StaffAttendanceStatus.ABSENT, null, null, null)
        ));
        when(staffLeaveRepository.save(any(StaffLeaveApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(staffLeaveRepository.findByStaffIdOrderByCreatedAtDesc(any())).thenReturn(List.of());

        StudentAdmissionRequest studentRequest = new StudentAdmissionRequest();
        studentRequest.setSchoolId(schoolId);
        studentRequest.setAcademicYearId(academicYearId);
        studentRequest.setClassId(classId);
        studentRequest.setSectionId(sectionId);
        studentRequest.setFirstName("Jane");
        studentRequest.setLastName("Doe");
        studentRequest.setGender(Gender.FEMALE);
        studentRequest.setParentName("Parent");
        studentRequest.setParentPhone("8888888888");

        StudentResponse admittedStudent = studentService.admitStudent(studentRequest);
        when(studentRepository.findById(admittedStudent.getStudentId())).thenReturn(Optional.of(savedStudent.get()));

        StudentAttendanceRequest attendanceRequest = new StudentAttendanceRequest();
        attendanceRequest.setAttendanceDate(LocalDate.of(2026, 1, 15));
        attendanceRequest.setStatus(AttendanceStatus.PRESENT);
        attendanceRequest.setRemarks("On time");

        StudentService.StudentAttendanceResponse studentAttendance = studentService.markAttendance(admittedStudent.getStudentId(), attendanceRequest);
        com.mgps.student.dto.StudentDtos.AttendanceSummaryResponse studentSummary =
            studentService.getAttendanceSummary(admittedStudent.getStudentId());

        StaffAdmissionRequest staffRequest = new StaffAdmissionRequest();
        staffRequest.setSchoolId(schoolId);
        staffRequest.setDepartmentId(departmentId);
        staffRequest.setFirstName("John");
        staffRequest.setLastName("Doe");
        staffRequest.setDesignation("Teacher");
        staffRequest.setQualification("MSc");
        staffRequest.setExperienceYears(4);

        StaffResponse onboardedStaff = staffService.onboardStaff(staffRequest);
        when(staffMemberRepository.findById(onboardedStaff.getStaffId())).thenReturn(Optional.of(savedStaff.get()));

        StaffAttendanceRequest staffAttendanceRequest = new StaffAttendanceRequest();
        staffAttendanceRequest.setAttendanceDate(LocalDate.of(2026, 1, 15));
        staffAttendanceRequest.setStatus(StaffAttendanceStatus.PRESENT);
        staffAttendanceRequest.setRemarks("On time");
        StaffService.StaffAttendanceResponse staffAttendance = staffService.markAttendance(onboardedStaff.getStaffId(), staffAttendanceRequest);
        com.mgps.staff.dto.StaffDtos.AttendanceSummaryResponse staffSummary =
            staffService.getAttendanceSummary(onboardedStaff.getStaffId());

        StaffLeaveRequest leaveRequest = new StaffLeaveRequest();
        leaveRequest.setSchoolId(schoolId);
        leaveRequest.setLeaveType(StaffLeaveType.CASUAL);
        leaveRequest.setStartDate(LocalDate.of(2026, 2, 1));
        leaveRequest.setEndDate(LocalDate.of(2026, 2, 2));
        leaveRequest.setReason("Personal work");
        leaveRequest.setRemarks("Integration leave");
        StaffLeaveResponse leave = staffService.applyLeave(onboardedStaff.getStaffId(), leaveRequest);

        assertThat(admittedStudent.getSchoolId()).isEqualTo(schoolId);
        assertThat(studentAttendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(studentSummary.getTotalDays()).isEqualTo(2);
        assertThat(onboardedStaff.getSchoolId()).isEqualTo(schoolId);
        assertThat(staffAttendance.getStatus()).isEqualTo(StaffAttendanceStatus.PRESENT);
        assertThat(staffSummary.getTotalDays()).isEqualTo(2);
        assertThat(leave.getStatus()).isEqualTo(StaffLeaveStatus.PENDING);
    }

    private AcademicYearRequest academicYearRequest(UUID schoolId) {
        AcademicYearRequest request = new AcademicYearRequest();
        request.setSchoolId(schoolId);
        request.setName("2026-2027");
        request.setStartDate(LocalDate.of(2026, 6, 1));
        request.setEndDate(LocalDate.of(2027, 5, 31));
        return request;
    }

    private AcademicClassRequest academicClassRequest(UUID schoolId, UUID yearId) {
        AcademicClassRequest request = new AcademicClassRequest();
        request.setSchoolId(schoolId);
        request.setAcademicYearId(yearId);
        request.setName("Class 5");
        request.setGradeLevel("5");
        request.setDescription("Primary");
        return request;
    }

    private AcademicDepartmentRequest academicDepartmentRequest(UUID schoolId) {
        AcademicDepartmentRequest request = new AcademicDepartmentRequest();
        request.setSchoolId(schoolId);
        request.setName("Science");
        request.setCode("SCI");
        request.setHeadName("Head");
        return request;
    }

    private AcademicSubjectRequest academicSubjectRequest(UUID schoolId, UUID classId) {
        AcademicSubjectRequest request = new AcademicSubjectRequest();
        request.setSchoolId(schoolId);
        request.setClassId(classId);
        request.setName("Math");
        request.setCode("MAT");
        request.setDescription("Mathematics");
        return request;
    }

    private AcademicSectionRequest academicSectionRequest(UUID schoolId, UUID classId) {
        AcademicSectionRequest request = new AcademicSectionRequest();
        request.setSchoolId(schoolId);
        request.setClassId(classId);
        request.setName("A");
        request.setCapacity(40);
        return request;
    }

    @Configuration
    @Import({
        RolePermissionService.class,
        AcademicStructureService.class,
        TimetableService.class,
        StudentService.class,
        StaffService.class
    })
    static class TestConfig {
        @Bean AcademicYearRepository academicYearRepository() { return mock(AcademicYearRepository.class); }
        @Bean AcademicClassRepository academicClassRepository() { return mock(AcademicClassRepository.class); }
        @Bean AcademicSectionRepository academicSectionRepository() { return mock(AcademicSectionRepository.class); }
        @Bean AcademicStreamRepository academicStreamRepository() { return mock(AcademicStreamRepository.class); }
        @Bean AcademicSubjectRepository academicSubjectRepository() { return mock(AcademicSubjectRepository.class); }
        @Bean AcademicDepartmentRepository academicDepartmentRepository() { return mock(AcademicDepartmentRepository.class); }
        @Bean AcademicHouseRepository academicHouseRepository() { return mock(AcademicHouseRepository.class); }
        @Bean TimetableEntryRepository timetableEntryRepository() { return mock(TimetableEntryRepository.class); }

        @Bean StudentRepository studentRepository() { return mock(StudentRepository.class); }
        @Bean StudentDocumentRepository studentDocumentRepository() { return mock(StudentDocumentRepository.class); }
        @Bean StudentAttendanceRepository studentAttendanceRepository() { return mock(StudentAttendanceRepository.class); }

        @Bean StaffMemberRepository staffMemberRepository() { return mock(StaffMemberRepository.class); }
        @Bean StaffAttendanceRepository staffAttendanceRepository() { return mock(StaffAttendanceRepository.class); }
        @Bean StaffLeaveRepository staffLeaveRepository() { return mock(StaffLeaveRepository.class); }
    }
}
