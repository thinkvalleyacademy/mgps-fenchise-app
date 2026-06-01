package com.mgps.student.dto;

import com.mgps.student.entity.AttendanceStatus;
import com.mgps.student.entity.Gender;
import com.mgps.student.entity.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class StudentDtos {
    private StudentDtos() {
    }

    public static class StudentAdmissionRequest {
        private UUID schoolId;
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String email;
        private String phone;
        private String address;
        private String parentName;
        private String parentPhone;
        private String medicalInfo;
        private String photoUrl;
        private List<UUID> feeStructureIds;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public String getParentPhone() { return parentPhone; }
        public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
        public String getMedicalInfo() { return medicalInfo; }
        public void setMedicalInfo(String medicalInfo) { this.medicalInfo = medicalInfo; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public List<UUID> getFeeStructureIds() { return feeStructureIds; }
        public void setFeeStructureIds(List<UUID> feeStructureIds) { this.feeStructureIds = feeStructureIds; }
    }

    public static class StudentUpdateRequest {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String email;
        private String phone;
        private String address;
        private String parentName;
        private String parentPhone;
        private String medicalInfo;
        private String photoUrl;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public String getParentPhone() { return parentPhone; }
        public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
        public String getMedicalInfo() { return medicalInfo; }
        public void setMedicalInfo(String medicalInfo) { this.medicalInfo = medicalInfo; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    }

    public static class StudentAssignmentRequest {
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;

        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
    }

    public static class StudentPromotionRequest {
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;

        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
    }

    public static class StudentTransferRequest {
        private String status;
        private LocalDate transferDate;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDate getTransferDate() { return transferDate; }
        public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }
    }

    public static class StudentAttendanceRequest {
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private String remarks;

        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class AttendanceSummaryResponse {
        private UUID studentId;
        private int totalDays;
        private int presentDays;
        private int absentDays;
        private int leaveDays;
        private double attendancePercentage;

        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
        public int getPresentDays() { return presentDays; }
        public void setPresentDays(int presentDays) { this.presentDays = presentDays; }
        public int getAbsentDays() { return absentDays; }
        public void setAbsentDays(int absentDays) { this.absentDays = absentDays; }
        public int getLeaveDays() { return leaveDays; }
        public void setLeaveDays(int leaveDays) { this.leaveDays = leaveDays; }
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }

    public static class StudentDocumentUploadRequest {
        private String documentType;
        private String documentNumber;
        private String fileName;
        private String fileUrl;
        private String remarks;

        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class StudentDocumentResponse {
        private UUID documentId;
        private UUID studentId;
        private UUID schoolId;
        private String documentType;
        private String documentNumber;
        private String fileName;
        private String fileUrl;
        private String remarks;
        private LocalDateTime uploadedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getDocumentId() { return documentId; }
        public void setDocumentId(UUID documentId) { this.documentId = documentId; }
        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class TransferCertificateResponse {
        private String certificateNumber;
        private UUID studentId;
        private UUID schoolId;
        private String studentName;
        private String admissionNumber;
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;
        private LocalDate issueDate;
        private LocalDate transferDate;
        private StudentStatus status;
        private String parentName;
        private LocalDate dateOfBirth;

        public String getCertificateNumber() { return certificateNumber; }
        public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getAdmissionNumber() { return admissionNumber; }
        public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
        public LocalDate getIssueDate() { return issueDate; }
        public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
        public LocalDate getTransferDate() { return transferDate; }
        public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }
        public StudentStatus getStatus() { return status; }
        public void setStatus(StudentStatus status) { this.status = status; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    }

    public static class StudentResponse {
        private UUID studentId;
        private UUID schoolId;
        private String admissionNumber;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String email;
        private String phone;
        private String address;
        private String parentName;
        private String parentPhone;
        private String medicalInfo;
        private String photoUrl;
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;
        private String className;
        private String sectionName;
        private LocalDate admissionDate;
        private LocalDate transferDate;
        private StudentStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getAdmissionNumber() { return admissionNumber; }
        public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public String getParentPhone() { return parentPhone; }
        public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
        public String getMedicalInfo() { return medicalInfo; }
        public void setMedicalInfo(String medicalInfo) { this.medicalInfo = medicalInfo; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public LocalDate getAdmissionDate() { return admissionDate; }
        public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
        public LocalDate getTransferDate() { return transferDate; }
        public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }
        public StudentStatus getStatus() { return status; }
        public void setStatus(StudentStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
