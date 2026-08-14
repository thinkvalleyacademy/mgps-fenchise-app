package com.mgps.examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Extends the legacy {@code marks} table (originally: id, exam_id, student_id,
 * subject_id, marks_obtained) with a link to {@link ExamSchedule} and absence
 * tracking. {@code examId}/{@code subjectId} are kept populated from the linked
 * schedule for backward-compatible reporting, even though {@code examScheduleId}
 * is the authoritative reference going forward.
 */
@Entity
@Table(name = "marks")
public class ExamMark {

    @Id
    private UUID id;

    @Column(name = "exam_schedule_id", nullable = false)
    private UUID examScheduleId;

    @Column(name = "exam_id", nullable = false)
    private UUID examId;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "marks_obtained", precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "is_absent", nullable = false)
    private boolean absent = false;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "entered_by")
    private UUID enteredBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ExamMark() {
    }

    public ExamMark(UUID id, UUID examScheduleId, UUID examId, UUID subjectId, UUID studentId,
                    BigDecimal marksObtained, boolean absent, String remarks, UUID enteredBy) {
        this.id = id;
        this.examScheduleId = examScheduleId;
        this.examId = examId;
        this.subjectId = subjectId;
        this.studentId = studentId;
        this.marksObtained = marksObtained;
        this.absent = absent;
        this.remarks = remarks;
        this.enteredBy = enteredBy;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getExamScheduleId() { return examScheduleId; }
    public void setExamScheduleId(UUID examScheduleId) { this.examScheduleId = examScheduleId; }
    public UUID getExamId() { return examId; }
    public void setExamId(UUID examId) { this.examId = examId; }
    public UUID getSubjectId() { return subjectId; }
    public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public BigDecimal getMarksObtained() { return marksObtained; }
    public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }
    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public UUID getEnteredBy() { return enteredBy; }
    public void setEnteredBy(UUID enteredBy) { this.enteredBy = enteredBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
