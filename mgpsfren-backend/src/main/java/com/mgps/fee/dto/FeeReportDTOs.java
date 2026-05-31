package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.util.UUID;

public final class FeeReportDTOs {
    private FeeReportDTOs() {}

    public static class ClassFeeReport {
        private UUID classId;
        private String className;
        private BigDecimal totalExpected;
        private BigDecimal totalCollected;
        private BigDecimal totalDiscounted;
        private BigDecimal totalOutstanding;
        private int studentCount;

        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public BigDecimal getTotalExpected() { return totalExpected; }
        public void setTotalExpected(BigDecimal totalExpected) { this.totalExpected = totalExpected; }
        public BigDecimal getTotalCollected() { return totalCollected; }
        public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
        public BigDecimal getTotalDiscounted() { return totalDiscounted; }
        public void setTotalDiscounted(BigDecimal totalDiscounted) { this.totalDiscounted = totalDiscounted; }
        public BigDecimal getTotalOutstanding() { return totalOutstanding; }
        public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }
        public int getStudentCount() { return studentCount; }
        public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
    }

    public static class StudentFeeReport {
        private UUID studentId;
        private String studentName;
        private String admissionNumber;
        private BigDecimal totalExpected;
        private BigDecimal totalCollected;
        private BigDecimal totalDiscounted;
        private BigDecimal totalOutstanding;
        private String status;

        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getAdmissionNumber() { return admissionNumber; }
        public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
        public BigDecimal getTotalExpected() { return totalExpected; }
        public void setTotalExpected(BigDecimal totalExpected) { this.totalExpected = totalExpected; }
        public BigDecimal getTotalCollected() { return totalCollected; }
        public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
        public BigDecimal getTotalDiscounted() { return totalDiscounted; }
        public void setTotalDiscounted(BigDecimal totalDiscounted) { this.totalDiscounted = totalDiscounted; }
        public BigDecimal getTotalOutstanding() { return totalOutstanding; }
        public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class SchoolFeeReport {
        private UUID schoolId;
        private BigDecimal overallExpected;
        private BigDecimal overallCollected;
        private BigDecimal overallDiscounted;
        private BigDecimal overallOutstanding;
        private int totalSchoolsCount; // If applicable for super admin, but here it's per school
        private int activeStudentsCount;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public BigDecimal getOverallExpected() { return overallExpected; }
        public void setOverallExpected(BigDecimal overallExpected) { this.overallExpected = overallExpected; }
        public BigDecimal getOverallCollected() { return overallCollected; }
        public void setOverallCollected(BigDecimal overallCollected) { this.overallCollected = overallCollected; }
        public BigDecimal getOverallDiscounted() { return overallDiscounted; }
        public void setOverallDiscounted(BigDecimal overallDiscounted) { this.overallDiscounted = overallDiscounted; }
        public BigDecimal getOverallOutstanding() { return overallOutstanding; }
        public void setOverallOutstanding(BigDecimal overallOutstanding) { this.overallOutstanding = overallOutstanding; }
        public int getActiveStudentsCount() { return activeStudentsCount; }
        public void setActiveStudentsCount(int activeStudentsCount) { this.activeStudentsCount = activeStudentsCount; }
    }
}
