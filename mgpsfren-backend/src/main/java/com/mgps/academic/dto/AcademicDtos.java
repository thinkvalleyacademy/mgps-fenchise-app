package com.mgps.academic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class AcademicDtos {
    private AcademicDtos() {
    }

    public static class AcademicYearRequest {
        private UUID schoolId;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }

    public static class AcademicYearResponse {
        private UUID yearId;
        private UUID schoolId;
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getYearId() { return yearId; }
        public void setYearId(UUID yearId) { this.yearId = yearId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class AcademicClassRequest {
        private UUID schoolId;
        private UUID academicYearId;
        private String name;
        private String gradeLevel;
        private String code;
        private String description;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class AcademicClassResponse {
        private UUID classId;
        private UUID schoolId;
        private UUID academicYearId;
        private String name;
        private String gradeLevel;
        private String code;
        private String description;
        private Integer sectionCount;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getSectionCount() { return sectionCount; }
        public void setSectionCount(Integer sectionCount) { this.sectionCount = sectionCount; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class AcademicSectionRequest {
        private UUID schoolId;
        private UUID classId;
        private String name;
        private Integer capacity;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
    }

    public static class AcademicSimpleResponse {
        private UUID id;
        private UUID schoolId;
        private String name;
        private String code;
        private String subjectType;
        private Integer capacity;
        private Boolean isActive;
        private LocalDateTime createdAt;

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getSubjectType() { return subjectType; }
        public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class AcademicStreamRequest {
        private UUID schoolId;
        private UUID classId;
        private String name;
        private String description;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class AcademicSubjectRequest {
        private UUID schoolId;
        private UUID classId;
        private String name;
        private String code;
        private String subjectType;
        private String description;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getSubjectType() { return subjectType; }
        public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class AcademicDepartmentRequest {
        private UUID schoolId;
        private String name;
        private String code;
        private String headName;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getHeadName() { return headName; }
        public void setHeadName(String headName) { this.headName = headName; }
    }

    public static class AcademicHouseRequest {
        private UUID schoolId;
        private String name;
        private String color;
        private String motto;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getMotto() { return motto; }
        public void setMotto(String motto) { this.motto = motto; }
    }
}
