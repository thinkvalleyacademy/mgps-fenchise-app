package com.mgps.academic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "academic_classes")
public class AcademicClass {
    @Id
    private UUID id;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "grade_level", length = 50)
    private String gradeLevel;

    @Column(length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AcademicClass() {
    }

    public AcademicClass(UUID id, UUID schoolId, UUID academicYearId, String name, String gradeLevel,
                         String description, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, schoolId, academicYearId, name, gradeLevel, null, description, isActive, createdAt, updatedAt);
    }

    public AcademicClass(UUID id, UUID schoolId, UUID academicYearId, String name, String gradeLevel,
                         String code, String description, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.schoolId = schoolId;
        this.academicYearId = academicYearId;
        this.name = name;
        this.gradeLevel = gradeLevel;
        this.code = code;
        this.description = description;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
