package com.mgps.database;

import com.mgps.academic.entity.AcademicClass;
import com.mgps.academic.entity.AcademicDepartment;
import com.mgps.academic.entity.AcademicHouse;
import com.mgps.academic.entity.AcademicSection;
import com.mgps.academic.entity.AcademicStream;
import com.mgps.academic.entity.AcademicSubject;
import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.entity.TimetableEntry;
import com.mgps.academic.repository.AcademicClassRepository;
import com.mgps.academic.repository.AcademicDepartmentRepository;
import com.mgps.academic.repository.AcademicHouseRepository;
import com.mgps.academic.repository.AcademicSectionRepository;
import com.mgps.academic.repository.AcademicStreamRepository;
import com.mgps.academic.repository.AcademicSubjectRepository;
import com.mgps.academic.repository.AcademicYearRepository;
import com.mgps.academic.repository.TimetableEntryRepository;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolDomain;
import com.mgps.school.entity.SchoolStatus;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.repository.SchoolDomainRepository;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.repository.SubscriptionPlanRepository;
import com.mgps.staff.entity.StaffAttendanceRecord;
import com.mgps.staff.entity.StaffAttendanceStatus;
import com.mgps.staff.entity.StaffLeaveApplication;
import com.mgps.staff.entity.StaffLeaveStatus;
import com.mgps.staff.entity.StaffLeaveType;
import com.mgps.staff.entity.StaffMember;
import com.mgps.staff.entity.StaffStatus;
import com.mgps.staff.repository.StaffAttendanceRepository;
import com.mgps.staff.repository.StaffLeaveRepository;
import com.mgps.staff.repository.StaffMemberRepository;
import com.mgps.student.entity.AttendanceStatus;
import com.mgps.student.entity.Gender;
import com.mgps.student.entity.Student;
import com.mgps.student.entity.StudentAttendance;
import com.mgps.student.entity.StudentDocument;
import com.mgps.student.entity.StudentStatus;
import com.mgps.student.repository.StudentAttendanceRepository;
import com.mgps.student.repository.StudentDocumentRepository;
import com.mgps.student.repository.StudentRepository;
import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import com.mgps.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "spring.flyway.enabled=false",
    "spring.sql.init.mode=never",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.datasource.url=jdbc:h2:mem:mgps_phase_1_46;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class Phase1To46DatabaseIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolDomainRepository schoolDomainRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private AcademicClassRepository academicClassRepository;

    @Autowired
    private AcademicSectionRepository academicSectionRepository;

    @Autowired
    private AcademicStreamRepository academicStreamRepository;

    @Autowired
    private AcademicSubjectRepository academicSubjectRepository;

    @Autowired
    private AcademicDepartmentRepository academicDepartmentRepository;

    @Autowired
    private AcademicHouseRepository academicHouseRepository;

    @Autowired
    private TimetableEntryRepository timetableEntryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private StudentDocumentRepository studentDocumentRepository;

    @Autowired
    private StaffMemberRepository staffMemberRepository;

    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    private StaffLeaveRepository staffLeaveRepository;

    @Test
    void shouldPersistSchoolOnboardingUserAcademicStudentAndStaffData() {
        UUID schoolId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(planId)
            .planName("Phase1 Plan")
            .description("Database smoke test plan")
            .maxStudents(500)
            .maxStaff(80)
            .maxUsers(120)
            .monthlyPrice(new BigDecimal("1999.00"))
            .features("{\"sms\":true,\"timetable\":true}")
            .isActive(true)
            .build();
        subscriptionPlanRepository.saveAndFlush(plan);

        School school = School.builder()
            .id(schoolId)
            .name("MGPS Central School")
            .adminEmail("admin@mgps.example")
            .adminPhone("9999999999")
            .address("1 School Road")
            .city("Pune")
            .state("Maharashtra")
            .postalCode("411001")
            .logoUrl("https://cdn.example/logo.png")
            .databaseName("mgps_school_001")
            .subscriptionPlan(plan)
            .status(SchoolStatus.ACTIVE)
            .build();
        schoolRepository.saveAndFlush(school);

        SchoolDomain primaryDomain = SchoolDomain.builder()
            .id(UUID.randomUUID())
            .school(school)
            .domainName("mgps-central.example.com")
            .isPrimary(true)
            .isActive(true)
            .build();
        schoolDomainRepository.saveAndFlush(primaryDomain);

        AppUser adminUser = AppUser.builder()
            .id(UUID.randomUUID())
            .schoolId(schoolId)
            .firstName("Asha")
            .lastName("Patel")
            .email("asha.patel@mgps.example")
            .phone("9000000001")
            .passwordHash("$2a$10$databaseSmokeHash")
            .role(UserRole.SCHOOL_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        appUserRepository.saveAndFlush(adminUser);

        AcademicYear academicYear = new AcademicYear(
            UUID.randomUUID(),
            schoolId,
            "2025-2026",
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2026, 3, 31),
            true,
            null,
            null
        );
        academicYearRepository.saveAndFlush(academicYear);

        AcademicClass academicClass = new AcademicClass(
            UUID.randomUUID(),
            schoolId,
            academicYear.getId(),
            "Grade 10",
            "10",
            "Secondary section",
            true,
            null,
            null
        );
        academicClassRepository.saveAndFlush(academicClass);

        AcademicSection section = new AcademicSection(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "A",
            40,
            true,
            null,
            null
        );
        academicSectionRepository.saveAndFlush(section);

        AcademicStream stream = new AcademicStream(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "Science",
            "Science stream",
            true,
            null,
            null
        );
        academicStreamRepository.saveAndFlush(stream);

        AcademicSubject subject = new AcademicSubject(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "Physics",
            "PHY",
            "Physics core subject",
            true,
            null,
            null
        );
        academicSubjectRepository.saveAndFlush(subject);

        AcademicDepartment department = new AcademicDepartment(
            UUID.randomUUID(),
            schoolId,
            "Science Department",
            "SCI",
            "Dr. Rao",
            true,
            null,
            null
        );
        academicDepartmentRepository.saveAndFlush(department);

        AcademicHouse house = new AcademicHouse(
            UUID.randomUUID(),
            schoolId,
            "Red House",
            "Red",
            "Unity and discipline",
            true,
            null,
            null
        );
        academicHouseRepository.saveAndFlush(house);

        TimetableEntry timetableEntry = new TimetableEntry(
            UUID.randomUUID(),
            schoolId,
            academicYear.getId(),
            academicClass.getId(),
            section.getId(),
            subject.getId(),
            adminUser.getId(),
            "Room 101",
            "MONDAY",
            LocalTime.of(9, 0),
            LocalTime.of(9, 45),
            "First period",
            true,
            null,
            null
        );
        timetableEntryRepository.saveAndFlush(timetableEntry);

        Student student = new Student(
            UUID.randomUUID(),
            schoolId,
            "ADM-1001",
            "Kabir",
            "Sharma",
            LocalDate.of(2010, 2, 15),
            Gender.MALE,
            "kabir.sharma@mgps.example",
            "8888888888",
            "45 Green Street",
            "Rakesh Sharma",
            "7777777777",
            "No known allergies",
            "https://cdn.example/students/kabir.png",
            academicYear.getId(),
            academicClass.getId(),
            section.getId(),
            LocalDate.of(2025, 6, 3),
            null,
            StudentStatus.ADMITTED,
            null,
            null
        );
        studentRepository.saveAndFlush(student);

        StudentAttendance attendance = new StudentAttendance(
            UUID.randomUUID(),
            student.getId(),
            LocalDate.of(2025, 6, 4),
            AttendanceStatus.PRESENT,
            "On time",
            null,
            null
        );
        studentAttendanceRepository.saveAndFlush(attendance);

        StudentDocument studentDocument = new StudentDocument(
            UUID.randomUUID(),
            student.getId(),
            schoolId,
            "Birth Certificate",
            "BC-2025-001",
            "birth-certificate.pdf",
            "https://cdn.example/docs/birth-certificate.pdf",
            "Verified at admission",
            LocalDateTime.now(),
            null,
            null
        );
        studentDocumentRepository.saveAndFlush(studentDocument);

        StaffMember staffMember = new StaffMember(
            UUID.randomUUID(),
            schoolId,
            "EMP-1001",
            "Meera",
            "Nair",
            LocalDate.of(1988, 8, 11),
            "Physics Teacher",
            department.getId(),
            department.getName(),
            "meera.nair@mgps.example",
            "8999999999",
            "12 Lake View",
            "M.Sc Physics, B.Ed",
            10,
            LocalDate.of(2024, 4, 1),
            "PAY-001",
            "ACC-001",
            StaffStatus.ACTIVE,
            null,
            null
        );
        staffMemberRepository.saveAndFlush(staffMember);

        StaffAttendanceRecord staffAttendance = new StaffAttendanceRecord(
            UUID.randomUUID(),
            staffMember.getId(),
            LocalDate.of(2025, 6, 4),
            StaffAttendanceStatus.PRESENT,
            "Teaching as scheduled",
            null,
            null
        );
        staffAttendanceRepository.saveAndFlush(staffAttendance);

        StaffLeaveApplication leaveApplication = new StaffLeaveApplication(
            UUID.randomUUID(),
            staffMember.getId(),
            schoolId,
            StaffLeaveType.CASUAL,
            LocalDate.of(2025, 7, 10),
            LocalDate.of(2025, 7, 12),
            "Family function",
            StaffLeaveStatus.PENDING,
            null,
            null,
            null,
            null,
            null
        );
        staffLeaveRepository.saveAndFlush(leaveApplication);

        entityManager.clear();

        assertThat(subscriptionPlanRepository.findByPlanName("Phase1 Plan")).isPresent();
        assertThat(schoolRepository.findByAdminEmail("admin@mgps.example")).isPresent();
        assertThat(schoolDomainRepository.findBySchoolIdAndIsPrimaryTrue(schoolId)).isPresent();
        assertThat(appUserRepository.findByEmail("asha.patel@mgps.example")).isPresent();
        assertThat(academicYearRepository.findBySchoolIdAndIsActiveTrue(schoolId)).isPresent();
        assertThat(academicClassRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYear.getId()))
            .hasSize(1);
        assertThat(academicSectionRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicStreamRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicSubjectRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicDepartmentRepository.findBySchoolId(schoolId)).hasSize(1);
        assertThat(academicHouseRepository.findBySchoolId(schoolId)).hasSize(1);
        assertThat(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(
            schoolId, academicYear.getId(), "MONDAY")).hasSize(1);
        assertThat(studentRepository.findBySchoolIdAndAdmissionNumber(schoolId, "ADM-1001")).isPresent();
        assertThat(studentAttendanceRepository.findByStudentIdAndAttendanceDate(student.getId(), LocalDate.of(2025, 6, 4)))
            .isPresent();
        assertThat(studentDocumentRepository.findByStudentIdOrderByUploadedAtDesc(student.getId()))
            .hasSize(1);
        assertThat(staffMemberRepository.findBySchoolIdAndEmployeeCode(schoolId, "EMP-1001")).isPresent();
        assertThat(staffAttendanceRepository.findByStaffIdAndAttendanceDate(staffMember.getId(), LocalDate.of(2025, 6, 4)))
            .isPresent();
        assertThat(staffLeaveRepository.findBySchoolIdAndStatus(schoolId, StaffLeaveStatus.PENDING))
            .hasSize(1);
    }

    @Test
    void shouldSupportRepositoryQueriesAcrossTheAcademicAndOperationalModules() {
        UUID schoolId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        AcademicYear year = new AcademicYear(
            academicYearId,
            schoolId,
            "2026-2027",
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2027, 3, 31),
            true,
            null,
            null
        );
        academicYearRepository.saveAndFlush(year);

        AcademicClass classA = new AcademicClass(
            classId,
            schoolId,
            academicYearId,
            "Grade 8",
            "8",
            null,
            true,
            null,
            null
        );
        academicClassRepository.saveAndFlush(classA);

        AcademicSubject subject = new AcademicSubject(
            subjectId,
            schoolId,
            classId,
            "Mathematics",
            "MATH",
            null,
            true,
            null,
            null
        );
        academicSubjectRepository.saveAndFlush(subject);

        AppUser teacher = AppUser.builder()
            .id(teacherId)
            .schoolId(schoolId)
            .firstName("Sanjay")
            .lastName("Iyer")
            .email("sanjay.iyer@mgps.example")
            .phone("8111111111")
            .passwordHash("$2a$10$databaseSmokeHash")
            .role(UserRole.TEACHER)
            .status(UserStatus.ACTIVE)
            .build();
        appUserRepository.saveAndFlush(teacher);

        TimetableEntry entry = new TimetableEntry(
            UUID.randomUUID(),
            schoolId,
            academicYearId,
            classId,
            null,
            subjectId,
            teacherId,
            "Lab 3",
            "TUESDAY",
            LocalTime.of(10, 0),
            LocalTime.of(10, 45),
            null,
            true,
            null,
            null
        );
        timetableEntryRepository.saveAndFlush(entry);

        assertThat(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndTeacherIdAndDayOfWeek(
            schoolId, academicYearId, teacherId, "TUESDAY")).hasSize(1);
        assertThat(academicClassRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId)).hasSize(1);
        assertThat(academicSubjectRepository.findBySchoolId(schoolId)).hasSize(1);
        assertThat(appUserRepository.findByRole(UserRole.TEACHER, org.springframework.data.domain.PageRequest.of(0, 10)))
            .hasSize(1);
    }
}
