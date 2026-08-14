package com.mgps.examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * One subject-paper within an {@link Exam}: date, time slot, room, and marks scale.
 * New table introduced in tenant migration V5.
 */
@Entity
@Table(name = "exam_schedules")
public class ExamSchedule {

    @Id
    private UUID id;

    @Column(name = "exam_id", nullable = false)
    private UUID examId;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "max_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal maxMarks;

    @Column(name = "passing_marks", precision = 6, scale = 2)
    private BigDecimal passingMarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ExamSchedule() {
    }

    public ExamSchedule(UUID id, UUID examId, UUID subjectId, LocalDate examDate, LocalTime startTime,
                        LocalTime endTime, String roomNumber, BigDecimal maxMarks, BigDecimal passingMarks) {
        this.id = id;
        this.examId = examId;
        this.subjectId = subjectId;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.maxMarks = maxMarks;
        this.passingMarks = passingMarks;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
}
