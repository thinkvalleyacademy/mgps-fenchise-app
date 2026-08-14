package com.mgps.examination.dto;

import com.mgps.examination.entity.ExamStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public final class ExamDtos {
    private ExamDtos() {
    }

    public static class ExamRequest {
        private UUID schoolId;
        private UUID academicYearId;
        private UUID classId;
        private String name;
        private String examType;
        private LocalDate startDate;
        private LocalDate endDate;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getExamType() { return examType; }
        public void setExamType(String examType) { this.examType = examType; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }

    public static class ExamStatusUpdateRequest {
        private ExamStatus status;

        public ExamStatus getStatus() { return status; }
        public void setStatus(ExamStatus status) { this.status = status; }
    }

    public static class ExamResponse {
        private UUID examId;
        private UUID schoolId;
        private UUID academicYearId;
        private UUID classId;
        private String name;
        private String examType;
        private LocalDate startDate;
        private LocalDate endDate;
        private ExamStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getExamId() { return examId; }
        public void setExamId(UUID examId) { this.examId = examId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getExamType() { return examType; }
        public void setExamType(String examType) { this.examType = examType; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public ExamStatus getStatus() { return status; }
        public void setStatus(ExamStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class ExamScheduleRequest {
        private UUID examId;
        private UUID subjectId;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String roomNumber;
        private BigDecimal maxMarks;
        private BigDecimal passingMarks;

        public UUID getExamId() { return examId; }
        public void setExamId(UUID examId) { this.examId = examId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public LocalDate getExamDate() { return examDate; }
        public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public BigDecimal getMaxMarks() { return maxMarks; }
        public void setMaxMarks(BigDecimal maxMarks) { this.maxMarks = maxMarks; }
        public BigDecimal getPassingMarks() { return passingMarks; }
        public void setPassingMarks(BigDecimal passingMarks) { this.passingMarks = passingMarks; }
    }

    public static class ExamScheduleResponse {
        private UUID scheduleId;
        private UUID examId;
        private UUID subjectId;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String roomNumber;
        private BigDecimal maxMarks;
        private BigDecimal passingMarks;
        private LocalDateTime createdAt;

        public UUID getScheduleId() { return scheduleId; }
        public void setScheduleId(UUID scheduleId) { this.scheduleId = scheduleId; }
        public UUID getExamId() { return examId; }
        public void setExamId(UUID examId) { this.examId = examId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public LocalDate getExamDate() { return examDate; }
        public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public BigDecimal getMaxMarks() { return maxMarks; }
        public void setMaxMarks(BigDecimal maxMarks) { this.maxMarks = maxMarks; }
        public BigDecimal getPassingMarks() { return passingMarks; }
        public void setPassingMarks(BigDecimal passingMarks) { this.passingMarks = passingMarks; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class ExamConflictResponse {
        private boolean conflict;
        private String reason;

        public boolean isConflict() { return conflict; }
        public void setConflict(boolean conflict) { this.conflict = conflict; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class MarkEntryItem {
        private UUID studentId;
        private BigDecimal marksObtained;
        private boolean absent;
        private String remarks;

        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public BigDecimal getMarksObtained() { return marksObtained; }
        public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }
        public boolean isAbsent() { return absent; }
        public void setAbsent(boolean absent) { this.absent = absent; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class MarkEntryRequest {
        private UUID examScheduleId;
        private List<MarkEntryItem> entries;

        public UUID getExamScheduleId() { return examScheduleId; }
        public void setExamScheduleId(UUID examScheduleId) { this.examScheduleId = examScheduleId; }
        public List<MarkEntryItem> getEntries() { return entries; }
        public void setEntries(List<MarkEntryItem> entries) { this.entries = entries; }
    }

    public static class ExamMarkResponse {
        private UUID markId;
        private UUID examScheduleId;
        private UUID studentId;
        private UUID subjectId;
        private BigDecimal marksObtained;
        private boolean absent;
        private String remarks;
        private UUID enteredBy;
        private LocalDateTime updatedAt;

        public UUID getMarkId() { return markId; }
        public void setMarkId(UUID markId) { this.markId = markId; }
        public UUID getExamScheduleId() { return examScheduleId; }
        public void setExamScheduleId(UUID examScheduleId) { this.examScheduleId = examScheduleId; }
        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public BigDecimal getMarksObtained() { return marksObtained; }
        public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }
        public boolean isAbsent() { return absent; }
        public void setAbsent(boolean absent) { this.absent = absent; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public UUID getEnteredBy() { return enteredBy; }
        public void setEnteredBy(UUID enteredBy) { this.enteredBy = enteredBy; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class StudentResultResponse {
        private UUID studentId;
        private String studentName;
        private UUID examId;
        private BigDecimal totalMarksObtained;
        private BigDecimal totalMaxMarks;
        private BigDecimal percentage;
        private String grade;
        private Integer rank;
        private int subjectsAppeared;
        private int subjectsAbsent;

        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public UUID getExamId() { return examId; }
        public void setExamId(UUID examId) { this.examId = examId; }
        public BigDecimal getTotalMarksObtained() { return totalMarksObtained; }
        public void setTotalMarksObtained(BigDecimal totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }
        public BigDecimal getTotalMaxMarks() { return totalMaxMarks; }
        public void setTotalMaxMarks(BigDecimal totalMaxMarks) { this.totalMaxMarks = totalMaxMarks; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
        public int getSubjectsAppeared() { return subjectsAppeared; }
        public void setSubjectsAppeared(int subjectsAppeared) { this.subjectsAppeared = subjectsAppeared; }
        public int getSubjectsAbsent() { return subjectsAbsent; }
        public void setSubjectsAbsent(int subjectsAbsent) { this.subjectsAbsent = subjectsAbsent; }
    }

    public static class SubjectAnalysisResponse {
        private UUID examId;
        private UUID subjectId;
        private int studentsAppeared;
        private BigDecimal averageMarks;
        private BigDecimal highestMarks;
        private BigDecimal lowestMarks;
        private int passCount;
        private int failCount;

        public UUID getExamId() { return examId; }
        public void setExamId(UUID examId) { this.examId = examId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public int getStudentsAppeared() { return studentsAppeared; }
        public void setStudentsAppeared(int studentsAppeared) { this.studentsAppeared = studentsAppeared; }
        public BigDecimal getAverageMarks() { return averageMarks; }
        public void setAverageMarks(BigDecimal averageMarks) { this.averageMarks = averageMarks; }
        public BigDecimal getHighestMarks() { return highestMarks; }
        public void setHighestMarks(BigDecimal highestMarks) { this.highestMarks = highestMarks; }
        public BigDecimal getLowestMarks() { return lowestMarks; }
        public void setLowestMarks(BigDecimal lowestMarks) { this.lowestMarks = lowestMarks; }
        public int getPassCount() { return passCount; }
        public void setPassCount(int passCount) { this.passCount = passCount; }
        public int getFailCount() { return failCount; }
        public void setFailCount(int failCount) { this.failCount = failCount; }
    }
}
