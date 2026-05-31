package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class StudentFeeDTO {
    private UUID id;
    private UUID schoolId;
    private UUID studentId;
    private String studentName;
    private UUID feeStructureId;
    private String feeCategoryName;
    private BigDecimal amountDue; // Monthly rate or One-time total
    private BigDecimal amountPaid;
    private String status;
    private LocalDate dueDate;
    private BigDecimal discountAmount;
    private String discountReason;
    private String recurrenceType; // ONE_TIME, MONTHLY
    private BigDecimal totalDueTillDate; // Calculated based on elapsed months for monthly

    public StudentFeeDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(UUID schoolId) {
        this.schoolId = schoolId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public UUID getFeeStructureId() {
        return feeStructureId;
    }

    public void setFeeStructureId(UUID feeStructureId) {
        this.feeStructureId = feeStructureId;
    }

    public String getFeeCategoryName() {
        return feeCategoryName;
    }

    public void setFeeCategoryName(String feeCategoryName) {
        this.feeCategoryName = feeCategoryName;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public BigDecimal getTotalDueTillDate() {
        return totalDueTillDate;
    }

    public void setTotalDueTillDate(BigDecimal totalDueTillDate) {
        this.totalDueTillDate = totalDueTillDate;
    }
}
