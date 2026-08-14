package com.mgps.examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Extends the legacy {@code exams} table (originally: id, academic_year_id,
 * exam_name, exam_date, max_marks, is_active) already provisioned in every
 * tenant database. New columns were added additively in V5 rather than
 * recreating the table.
 */
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    private UUID id;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "exam_name", nullable = false, length = 100)
    private String name;

    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType = "GENERAL";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamStatus status = ExamStatus.SCHEDULED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Exam() {
    }

    public Exam(UUID id, UUID schoolId, UUID academicYearId, UUID classId, String name, String examType,
               LocalDate startDate, LocalDate endDate, ExamStatus status) {
        this.id = id;
        this.schoolId = schoolId;
        this.academicYearId = academicYearId;
        this.classId = classId;
        this.name = name;
        this.examType = examType != null ? examType : "GENERAL";
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : ExamStatus.SCHEDULED;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
