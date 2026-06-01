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
@Table(name = "academic_subjects")
public class AcademicSubject {
    @Id
    private UUID id;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(name = "subject_type", length = 50)
    private String subjectType;

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

    public AcademicSubject() {
    }

    public AcademicSubject(UUID id, UUID schoolId, UUID classId, String name, String code, String description,
                           Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, schoolId, classId, name, code, null, description, isActive, createdAt, updatedAt);
    }

    public AcademicSubject(UUID id, UUID schoolId, UUID classId, String name, String code, String subjectType, String description,
                           Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.schoolId = schoolId;
        this.classId = classId;
        this.name = name;
        this.code = code;
        this.subjectType = subjectType;
        this.description = description;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
