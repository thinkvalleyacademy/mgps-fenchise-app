package com.mgps.postgres;

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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/mgps_master",
    "spring.datasource.username=postgres",
    "spring.datasource.password=postgres123",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
    "spring.flyway.enabled=false",
    "spring.sql.init.mode=never",
    "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class Phase1To46PostgresIntegrationTest {

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
    void shouldPersistAndReadAcrossPhase1To46OnPostgres() {
        UUID schoolId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        SubscriptionPlan plan = SubscriptionPlan.builder()
            .id(planId)
            .planName("PG Smoke Plan")
            .description("Postgres integration test plan")
            .maxStudents(800)
            .maxStaff(100)
            .maxUsers(160)
            .monthlyPrice(new BigDecimal("2999.00"))
            .features("{\"modules\":[\"student_management\",\"staff_management\"]}")
            .isActive(true)
            .build();
        subscriptionPlanRepository.saveAndFlush(plan);

        School school = School.builder()
            .id(schoolId)
            .name("PG Integration School")
            .adminEmail("pg.admin@mgps.example")
            .adminPhone("9000000002")
            .address("2 River Road")
            .city("Mumbai")
            .state("Maharashtra")
            .postalCode("400001")
            .logoUrl("https://cdn.example/pg-logo.png")
            .databaseName("pg_integration_school")
            .subscriptionPlan(plan)
            .status(SchoolStatus.ACTIVE)
            .build();
        schoolRepository.saveAndFlush(school);

        SchoolDomain domain = SchoolDomain.builder()
            .id(UUID.randomUUID())
            .school(school)
            .domainName("pg-integration.example.com")
            .isPrimary(true)
            .isActive(true)
            .build();
        schoolDomainRepository.saveAndFlush(domain);

        AppUser admin = AppUser.builder()
            .id(UUID.randomUUID())
            .schoolId(schoolId)
            .firstName("Priya")
            .lastName("Shah")
            .email("priya.shah@mgps.example")
            .phone("9555555555")
            .passwordHash("$2a$10$postgresSmokeHash")
            .role(UserRole.SCHOOL_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        appUserRepository.saveAndFlush(admin);

        AcademicYear year = new AcademicYear(
            UUID.randomUUID(),
            schoolId,
            "2026-2027",
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2027, 3, 31),
            true,
            null,
            null
        );
        academicYearRepository.saveAndFlush(year);

        AcademicClass academicClass = new AcademicClass(
            UUID.randomUUID(),
            schoolId,
            year.getId(),
            "Grade 9",
            "9",
            "Senior secondary",
            true,
            null,
            null
        );
        academicClassRepository.saveAndFlush(academicClass);

        AcademicSection section = new AcademicSection(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "B",
            42,
            true,
            null,
            null
        );
        academicSectionRepository.saveAndFlush(section);

        AcademicStream stream = new AcademicStream(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "Commerce",
            "Commerce stream",
            true,
            null,
            null
        );
        academicStreamRepository.saveAndFlush(stream);

        AcademicSubject subject = new AcademicSubject(
            UUID.randomUUID(),
            schoolId,
            academicClass.getId(),
            "Accountancy",
            "ACC",
            "Core commerce subject",
            true,
            null,
            null
        );
        academicSubjectRepository.saveAndFlush(subject);

        AcademicDepartment department = new AcademicDepartment(
            UUID.randomUUID(),
            schoolId,
            "Commerce Department",
            "COM",
            "Ms. Iyer",
            true,
            null,
            null
        );
        academicDepartmentRepository.saveAndFlush(department);

        AcademicHouse house = new AcademicHouse(
            UUID.randomUUID(),
            schoolId,
            "Blue House",
            "Blue",
            "Excellence and integrity",
            true,
            null,
            null
        );
        academicHouseRepository.saveAndFlush(house);

        TimetableEntry timetableEntry = new TimetableEntry(
            UUID.randomUUID(),
            schoolId,
            year.getId(),
            academicClass.getId(),
            section.getId(),
            subject.getId(),
            admin.getId(),
            "Room 201",
            "WEDNESDAY",
            LocalTime.of(11, 0),
            LocalTime.of(11, 45),
            "Commerce period",
            true,
            null,
            null
        );
        timetableEntryRepository.saveAndFlush(timetableEntry);

        Student student = new Student(
            UUID.randomUUID(),
            schoolId,
            "PG-ADM-1001",
            "Nikhil",
            "Verma",
            LocalDate.of(2011, 5, 20),
            Gender.MALE,
            "nikhil.verma@mgps.example",
            "9666666666",
            "8 Coastal Lane",
            "Renu Verma",
            "9777777777",
            "Asthma",
            "https://cdn.example/students/nikhil.png",
            year.getId(),
            academicClass.getId(),
            section.getId(),
            LocalDate.of(2026, 6, 5),
            null,
            StudentStatus.ADMITTED,
            null,
            null
        );
        studentRepository.saveAndFlush(student);

        StudentAttendance attendance = new StudentAttendance(
            UUID.randomUUID(),
            student.getId(),
            LocalDate.of(2026, 6, 6),
            AttendanceStatus.PRESENT,
            "Present for all sessions",
            null,
            null
        );
        studentAttendanceRepository.saveAndFlush(attendance);

        StudentDocument document = new StudentDocument(
            UUID.randomUUID(),
            student.getId(),
            schoolId,
            "Transfer Certificate",
            "TC-2026-001",
            "transfer-certificate.pdf",
            "https://cdn.example/docs/tc.pdf",
            "Submitted on admission",
            LocalDateTime.now(),
            null,
            null
        );
        studentDocumentRepository.saveAndFlush(document);

        StaffMember staff = new StaffMember(
            UUID.randomUUID(),
            schoolId,
            "EMP-PG-1001",
            "Ananya",
            "Kulkarni",
            LocalDate.of(1990, 9, 9),
            "Accountant",
            department.getId(),
            department.getName(),
            "ananya.kulkarni@mgps.example",
            "9444444444",
            "16 Hill Road",
            "MBA Finance",
            12,
            LocalDate.of(2025, 4, 1),
            "PAY-PG-001",
            "ACC-PG-001",
            StaffStatus.ACTIVE,
            null,
            null
        );
        staffMemberRepository.saveAndFlush(staff);

        StaffAttendanceRecord staffAttendance = new StaffAttendanceRecord(
            UUID.randomUUID(),
            staff.getId(),
            LocalDate.of(2026, 6, 6),
            StaffAttendanceStatus.PRESENT,
            "Worked morning shift",
            null,
            null
        );
        staffAttendanceRepository.saveAndFlush(staffAttendance);

        StaffLeaveApplication leaveApplication = new StaffLeaveApplication(
            UUID.randomUUID(),
            staff.getId(),
            schoolId,
            StaffLeaveType.CASUAL,
            LocalDate.of(2026, 7, 15),
            LocalDate.of(2026, 7, 16),
            "Personal work",
            StaffLeaveStatus.PENDING,
            null,
            null,
            null,
            null,
            null
        );
        staffLeaveRepository.saveAndFlush(leaveApplication);

        assertThat(subscriptionPlanRepository.findByPlanName("PG Smoke Plan")).isPresent();
        assertThat(schoolRepository.findByAdminEmail("pg.admin@mgps.example")).isPresent();
        assertThat(schoolDomainRepository.findBySchoolIdAndIsPrimaryTrue(schoolId)).isPresent();
        assertThat(appUserRepository.findByEmail("priya.shah@mgps.example")).isPresent();
        assertThat(academicYearRepository.findBySchoolIdAndIsActiveTrue(schoolId)).isPresent();
        assertThat(academicClassRepository.findBySchoolIdAndAcademicYearId(schoolId, year.getId())).hasSize(1);
        assertThat(academicSectionRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicStreamRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicSubjectRepository.findByClassId(academicClass.getId())).hasSize(1);
        assertThat(academicDepartmentRepository.findBySchoolId(schoolId)).hasSize(1);
        assertThat(academicHouseRepository.findBySchoolId(schoolId)).hasSize(1);
        assertThat(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(
            schoolId, year.getId(), "WEDNESDAY")).hasSize(1);
        assertThat(studentRepository.findBySchoolIdAndAdmissionNumber(schoolId, "PG-ADM-1001")).isPresent();
        assertThat(studentAttendanceRepository.findByStudentIdAndAttendanceDate(student.getId(), LocalDate.of(2026, 6, 6)))
            .isPresent();
        assertThat(studentDocumentRepository.findByStudentIdOrderByUploadedAtDesc(student.getId())).hasSize(1);
        assertThat(staffMemberRepository.findBySchoolIdAndEmployeeCode(schoolId, "EMP-PG-1001")).isPresent();
        assertThat(staffAttendanceRepository.findByStaffIdAndAttendanceDate(staff.getId(), LocalDate.of(2026, 6, 6)))
            .isPresent();
        assertThat(staffLeaveRepository.findBySchoolIdAndStatus(schoolId, StaffLeaveStatus.PENDING)).hasSize(1);
    }
}
