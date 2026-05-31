package com.mgps.student.service;

import com.mgps.academic.entity.AcademicClass;
import com.mgps.academic.entity.AcademicSection;
import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.repository.AcademicClassRepository;
import com.mgps.academic.repository.AcademicSectionRepository;
import com.mgps.academic.repository.AcademicYearRepository;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.fee.entity.FeeStructure;
import com.mgps.fee.service.FeeService;
import com.mgps.student.dto.StudentDtos.*;
import com.mgps.student.entity.*;
import com.mgps.student.repository.StudentDocumentRepository;
import com.mgps.student.repository.StudentAttendanceRepository;
import com.mgps.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDocumentRepository documentRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AcademicClassRepository academicClassRepository;
    private final AcademicSectionRepository academicSectionRepository;
    private final FeeService feeService;

    public StudentService(StudentRepository studentRepository,
                          StudentDocumentRepository documentRepository,
                          StudentAttendanceRepository attendanceRepository,
                          AcademicYearRepository academicYearRepository,
                          AcademicClassRepository academicClassRepository,
                          AcademicSectionRepository academicSectionRepository,
                          FeeService feeService) {
        this.studentRepository = studentRepository;
        this.documentRepository = documentRepository;
        this.attendanceRepository = attendanceRepository;
        this.academicYearRepository = academicYearRepository;
        this.academicClassRepository = academicClassRepository;
        this.academicSectionRepository = academicSectionRepository;
        this.feeService = feeService;
    }

    public StudentResponse admitStudent(StudentAdmissionRequest request) {
        UUID yearId = request.getAcademicYearId();
        if (yearId == null) {
            yearId = academicYearRepository.findBySchoolIdAndIsActiveTrue(request.getSchoolId())
                .map(AcademicYear::getId)
                .orElseThrow(() -> new ResourceNotFoundException("No active academic year found. Please activate one first."));
        }

        validateAcademicRefs(yearId, request.getClassId(), request.getSectionId(), request.getSchoolId());

        String admissionNumber = generateAdmissionNumber(request.getSchoolId());
        while (studentRepository.existsByAdmissionNumber(admissionNumber)) {
            admissionNumber = generateAdmissionNumber(request.getSchoolId());
        }

        Student student = new Student(
            UUID.randomUUID(),
            request.getSchoolId(),
            admissionNumber,
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getGender(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getParentName(),
            request.getParentPhone(),
            request.getMedicalInfo(),
            request.getPhotoUrl(),
            yearId,
            request.getClassId(),
            request.getSectionId(),
            LocalDate.now(),
            null,
            StudentStatus.ADMITTED,
            null,
            null
        );

        Student saved = studentRepository.save(student);

        // --- Automatic Fee Assignment ---
        if (request.getFeeStructureIds() != null && !request.getFeeStructureIds().isEmpty()) {
            for (UUID structureId : request.getFeeStructureIds()) {
                feeService.assignFeeToStudent(saved.getId(), structureId);
            }
        } else {
            List<FeeStructure> defaults = feeService.getDefaultStructures(request.getSchoolId(), yearId, request.getClassId());
            for (FeeStructure fs : defaults) {
                feeService.assignFeeToStudent(saved.getId(), fs.getId());
            }
        }

        return map(saved);
    }

    public StudentResponse updateStudent(UUID studentId, StudentUpdateRequest request) {
        Student student = getStudentEntity(studentId);
        applyUpdate(student, request);
        return map(studentRepository.save(student));
    }

    public StudentResponse getStudent(UUID studentId) {
        return map(getStudentEntity(studentId));
    }

    public Page<StudentResponse> getStudentsBySchool(UUID schoolId, Pageable pageable) {
        return studentRepository.findBySchoolId(schoolId, pageable).map(this::map);
    }

    public List<StudentResponse> getStudentsByClass(UUID schoolId, UUID classId) {
        return studentRepository.findBySchoolIdAndClassId(schoolId, classId).stream().map(this::map).collect(Collectors.toList());
    }

    public StudentResponse assignClass(UUID studentId, StudentAssignmentRequest request) {
        validateAcademicRefs(request.getAcademicYearId(), request.getClassId(), request.getSectionId(), null);
        Student student = getStudentEntity(studentId);
        student.setAcademicYearId(request.getAcademicYearId());
        student.setClassId(request.getClassId());
        student.setSectionId(request.getSectionId());
        student.setStatus(StudentStatus.ACTIVE);
        return map(studentRepository.save(student));
    }

    public StudentResponse promoteStudent(UUID studentId, StudentPromotionRequest request) {
        validateAcademicRefs(request.getAcademicYearId(), request.getClassId(), request.getSectionId(), null);
        Student student = getStudentEntity(studentId);
        student.setAcademicYearId(request.getAcademicYearId());
        student.setClassId(request.getClassId());
        student.setSectionId(request.getSectionId());
        student.setStatus(StudentStatus.ACTIVE);
        return map(studentRepository.save(student));
    }

    public StudentResponse transferStudent(UUID studentId, StudentTransferRequest request) {
        Student student = getStudentEntity(studentId);
        student.setStatus(request.getStatus() != null ? StudentStatus.valueOf(request.getStatus().toUpperCase()) : StudentStatus.TRANSFERRED);
        student.setTransferDate(request.getTransferDate() != null ? request.getTransferDate() : LocalDate.now());
        return map(studentRepository.save(student));
    }

    public StudentAttendanceResponse markAttendance(UUID studentId, StudentAttendanceRequest request) {
        Student student = getStudentEntity(studentId);
        LocalDate attendanceDate = request.getAttendanceDate() != null ? request.getAttendanceDate() : LocalDate.now();
        StudentAttendance attendance = attendanceRepository.findByStudentIdAndAttendanceDate(studentId, attendanceDate)
            .orElseGet(() -> new StudentAttendance(UUID.randomUUID(), studentId, attendanceDate, request.getStatus(), request.getRemarks(), null, null));
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());
        return mapAttendance(attendanceRepository.save(attendance));
    }

    public AttendanceSummaryResponse getAttendanceSummary(UUID studentId) {
        getStudentEntity(studentId);
        List<StudentAttendance> records = attendanceRepository.findByStudentId(studentId);
        AttendanceSummaryResponse summary = new AttendanceSummaryResponse();
        summary.setStudentId(studentId);
        summary.setTotalDays(records.size());
        summary.setPresentDays((int) records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count());
        summary.setAbsentDays((int) records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count());
        summary.setLeaveDays((int) records.stream().filter(r -> r.getStatus() == AttendanceStatus.LEAVE).count());
        summary.setAttendancePercentage(summary.getTotalDays() == 0 ? 0.0 :
            (summary.getPresentDays() * 100.0) / summary.getTotalDays());
        return summary;
    }

    public StudentDocumentResponse uploadDocument(UUID studentId, StudentDocumentUploadRequest request) {
        Student student = getStudentEntity(studentId);
        StudentDocument document = new StudentDocument(
            UUID.randomUUID(),
            studentId,
            student.getSchoolId(),
            request.getDocumentType(),
            request.getDocumentNumber(),
            request.getFileName(),
            request.getFileUrl(),
            request.getRemarks(),
            java.time.LocalDateTime.now(),
            null,
            null
        );
        return mapDocument(documentRepository.save(document));
    }

    public List<StudentDocumentResponse> getDocuments(UUID studentId) {
        getStudentEntity(studentId);
        return documentRepository.findByStudentIdOrderByUploadedAtDesc(studentId).stream()
            .map(this::mapDocument)
            .collect(Collectors.toList());
    }

    public TransferCertificateResponse generateTransferCertificate(UUID studentId) {
        Student student = getStudentEntity(studentId);
        TransferCertificateResponse response = new TransferCertificateResponse();
        response.setCertificateNumber(generateCertificateNumber(student));
        response.setStudentId(student.getId());
        response.setSchoolId(student.getSchoolId());
        response.setStudentName(student.getFirstName() + " " + student.getLastName());
        response.setAdmissionNumber(student.getAdmissionNumber());
        response.setAcademicYearId(student.getAcademicYearId());
        response.setClassId(student.getClassId());
        response.setSectionId(student.getSectionId());
        response.setIssueDate(LocalDate.now());
        response.setTransferDate(student.getTransferDate() != null ? student.getTransferDate() : LocalDate.now());
        response.setStatus(student.getStatus());
        response.setParentName(student.getParentName());
        response.setDateOfBirth(student.getDateOfBirth());
        return response;
    }

    private void validateAcademicRefs(UUID academicYearId, UUID classId, UUID sectionId, UUID schoolId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        AcademicClass academicClass = academicClassRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        if (sectionId != null) {
            academicSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        }
        if (schoolId != null && !academicYear.getSchoolId().equals(schoolId) && !academicClass.getSchoolId().equals(schoolId)) {
            throw new ResourceNotFoundException("Academic references do not belong to this school");
        }
    }

    private Student getStudentEntity(UUID studentId) {
        return studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    private String generateAdmissionNumber(UUID schoolId) {
        String schoolSeed = schoolId != null ? schoolId.toString().replace("-", "").substring(0, 6).toUpperCase() : "SCH";
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ADM-" + schoolSeed + "-" + random;
    }

    private void applyUpdate(Student student, StudentUpdateRequest request) {
        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) student.setGender(request.getGender());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getParentName() != null) student.setParentName(request.getParentName());
        if (request.getParentPhone() != null) student.setParentPhone(request.getParentPhone());
        if (request.getMedicalInfo() != null) student.setMedicalInfo(request.getMedicalInfo());
        if (request.getPhotoUrl() != null) student.setPhotoUrl(request.getPhotoUrl());
    }

    private StudentResponse map(Student student) {
        StudentResponse response = new StudentResponse();
        response.setStudentId(student.getId());
        response.setSchoolId(student.getSchoolId());
        response.setAdmissionNumber(student.getAdmissionNumber());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setGender(student.getGender());
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setAddress(student.getAddress());
        response.setParentName(student.getParentName());
        response.setParentPhone(student.getParentPhone());
        response.setMedicalInfo(student.getMedicalInfo());
        response.setPhotoUrl(student.getPhotoUrl());
        response.setAcademicYearId(student.getAcademicYearId());
        response.setClassId(student.getClassId());
        response.setSectionId(student.getSectionId());
        response.setAdmissionDate(student.getAdmissionDate());
        response.setTransferDate(student.getTransferDate());
        response.setStatus(student.getStatus());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());
        return response;
    }

    private StudentAttendanceResponse mapAttendance(StudentAttendance attendance) {
        StudentAttendanceResponse response = new StudentAttendanceResponse();
        response.setAttendanceId(attendance.getId());
        response.setStudentId(attendance.getStudentId());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setStatus(attendance.getStatus());
        response.setRemarks(attendance.getRemarks());
        response.setCreatedAt(attendance.getCreatedAt());
        response.setUpdatedAt(attendance.getUpdatedAt());
        return response;
    }

    private StudentDocumentResponse mapDocument(StudentDocument document) {
        StudentDocumentResponse response = new StudentDocumentResponse();
        response.setDocumentId(document.getId());
        response.setStudentId(document.getStudentId());
        response.setSchoolId(document.getSchoolId());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentNumber(document.getDocumentNumber());
        response.setFileName(document.getFileName());
        response.setFileUrl(document.getFileUrl());
        response.setRemarks(document.getRemarks());
        response.setUploadedAt(document.getUploadedAt());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }

    private String generateCertificateNumber(Student student) {
        String seed = student.getAdmissionNumber() != null ? student.getAdmissionNumber().replace("-", "") : student.getId().toString().replace("-", "");
        String shortSeed = seed.substring(0, Math.min(8, seed.length())).toUpperCase();
        return "TC-" + shortSeed + "-" + LocalDate.now().toString().replace("-", "");
    }

    public static class StudentAttendanceResponse {
        private UUID attendanceId;
        private UUID studentId;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private String remarks;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        public UUID getAttendanceId() { return attendanceId; }
        public void setAttendanceId(UUID attendanceId) { this.attendanceId = attendanceId; }
        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
