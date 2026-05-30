package com.mgps.student.service;

import com.mgps.academic.entity.AcademicClass;
import com.mgps.academic.entity.AcademicSection;
import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.repository.AcademicClassRepository;
import com.mgps.academic.repository.AcademicSectionRepository;
import com.mgps.academic.repository.AcademicYearRepository;
import com.mgps.student.dto.StudentDtos.*;
import com.mgps.student.entity.*;
import com.mgps.student.repository.StudentDocumentRepository;
import com.mgps.student.repository.StudentAttendanceRepository;
import com.mgps.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentDocumentRepository documentRepository;
    @Mock private StudentAttendanceRepository attendanceRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicClassRepository academicClassRepository;
    @Mock private AcademicSectionRepository academicSectionRepository;

    @InjectMocks
    private StudentService studentService;

    private UUID schoolId;
    private UUID academicYearId;
    private UUID classId;
    private UUID sectionId;

    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();
        classId = UUID.randomUUID();
        sectionId = UUID.randomUUID();
    }

    @Test
    void shouldAdmitStudent() {
        StudentAdmissionRequest request = baseAdmissionRequest();
        AcademicYear year = new AcademicYear(academicYearId, schoolId, "2026-2027", LocalDate.of(2026, 6, 1), LocalDate.of(2027, 5, 31), true, null, null);
        AcademicClass clazz = new AcademicClass(classId, schoolId, academicYearId, "Class 5", "5", null, true, null, null);
        AcademicSection section = new AcademicSection(sectionId, schoolId, classId, "A", 40, true, null, null);
        Student saved = new Student(UUID.randomUUID(), schoolId, "ADM-SCH-1234", "Jane", "Doe", null, Gender.FEMALE,
            "jane@example.com", "9999999999", "Address", "Parent", "8888888888", null, null,
            academicYearId, classId, sectionId, LocalDate.now(), null, StudentStatus.ADMITTED, null, null);

        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(year));
        when(academicClassRepository.findById(classId)).thenReturn(Optional.of(clazz));
        when(academicSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(studentRepository.existsByAdmissionNumber(any())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        var response = studentService.admitStudent(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getStatus()).isEqualTo(StudentStatus.ADMITTED);
    }

    @Test
    void shouldMarkAttendanceAndSummarize() {
        Student student = new Student(UUID.randomUUID(), schoolId, "ADM-SCH-5678", "Jane", "Doe", null, Gender.FEMALE,
            "jane@example.com", "9999999999", "Address", "Parent", "8888888888", null, null,
            academicYearId, classId, sectionId, LocalDate.now(), null, StudentStatus.ACTIVE, null, null);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(student.getId(), LocalDate.of(2026, 1, 15)))
            .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(StudentAttendance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(attendanceRepository.findByStudentId(student.getId())).thenReturn(List.of(
            new StudentAttendance(UUID.randomUUID(), student.getId(), LocalDate.of(2026, 1, 15), AttendanceStatus.PRESENT, null, null, null),
            new StudentAttendance(UUID.randomUUID(), student.getId(), LocalDate.of(2026, 1, 16), AttendanceStatus.ABSENT, null, null, null)
        ));

        StudentAttendanceRequest attendanceRequest = new StudentAttendanceRequest();
        attendanceRequest.setAttendanceDate(LocalDate.of(2026, 1, 15));
        attendanceRequest.setStatus(AttendanceStatus.PRESENT);

        var attendance = studentService.markAttendance(student.getId(), attendanceRequest);
        var summary = studentService.getAttendanceSummary(student.getId());

        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(summary.getTotalDays()).isEqualTo(2);
        assertThat(summary.getPresentDays()).isEqualTo(1);
        assertThat(summary.getAbsentDays()).isEqualTo(1);
    }

    @Test
    void shouldUploadDocumentAndGenerateTransferCertificate() {
        UUID studentId = UUID.randomUUID();
        Student student = new Student(studentId, schoolId, "ADM-SCH-9012", "Jane", "Doe", LocalDate.of(2015, 1, 1), Gender.FEMALE,
            "jane@example.com", "9999999999", "Address", "Parent", "8888888888", null, null,
            academicYearId, classId, sectionId, LocalDate.now(), LocalDate.of(2026, 3, 31), StudentStatus.TRANSFERRED, null, null);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(documentRepository.save(any(StudentDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(documentRepository.findByStudentIdOrderByUploadedAtDesc(studentId)).thenReturn(List.of(
            new StudentDocument(UUID.randomUUID(), studentId, schoolId, "Birth Certificate", "DOC-1", "birth.pdf", "https://example.com/birth.pdf", "Original copy", null, null, null)
        ));

        StudentDocumentUploadRequest uploadRequest = new StudentDocumentUploadRequest();
        uploadRequest.setDocumentType("Birth Certificate");
        uploadRequest.setDocumentNumber("DOC-1");
        uploadRequest.setFileName("birth.pdf");
        uploadRequest.setFileUrl("https://example.com/birth.pdf");
        uploadRequest.setRemarks("Original copy");

        StudentDocumentResponse uploaded = studentService.uploadDocument(studentId, uploadRequest);
        var documents = studentService.getDocuments(studentId);
        var certificate = studentService.generateTransferCertificate(studentId);

        assertThat(uploaded.getDocumentType()).isEqualTo("Birth Certificate");
        assertThat(documents).hasSize(1);
        assertThat(certificate.getStudentId()).isEqualTo(studentId);
        assertThat(certificate.getStatus()).isEqualTo(StudentStatus.TRANSFERRED);
        assertThat(certificate.getCertificateNumber()).startsWith("TC-");
    }

    private StudentAdmissionRequest baseAdmissionRequest() {
        StudentAdmissionRequest request = new StudentAdmissionRequest();
        request.setSchoolId(schoolId);
        request.setAcademicYearId(academicYearId);
        request.setClassId(classId);
        request.setSectionId(sectionId);
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setGender(Gender.FEMALE);
        request.setEmail("jane@example.com");
        request.setPhone("9999999999");
        request.setAddress("Address");
        request.setParentName("Parent");
        request.setParentPhone("8888888888");
        return request;
    }
}
