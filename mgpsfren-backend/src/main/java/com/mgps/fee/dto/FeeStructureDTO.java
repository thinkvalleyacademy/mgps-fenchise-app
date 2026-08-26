package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FeeStructureDTO {
    private UUID id;
    private UUID schoolId;
    private UUID academicYearId;
    private UUID classId;
    private List<UUID> classIds; // request-only: create the same structure for each of these classes
    private Boolean applyToAllClasses; // request-only: create a single school-wide structure (classId = null)
    private UUID feeCategoryId;
    private String feeCategoryName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private Boolean isDefault;
    private Boolean isActive;
    private String recurrenceType; // ONE_TIME, MONTHLY
    private String fineType; // NONE, FLAT, PERCENTAGE_PER_MONTH
    private BigDecimal fineAmount;
    private Integer gracePeriodDays;

    public FeeStructureDTO() {
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

    public UUID getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(UUID academicYearId) {
        this.academicYearId = academicYearId;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public List<UUID> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<UUID> classIds) {
        this.classIds = classIds;
    }

    public Boolean getApplyToAllClasses() {
        return applyToAllClasses;
    }

    public void setApplyToAllClasses(Boolean applyToAllClasses) {
        this.applyToAllClasses = applyToAllClasses;
    }

    public UUID getFeeCategoryId() {
        return feeCategoryId;
    }

    public void setFeeCategoryId(UUID feeCategoryId) {
        this.feeCategoryId = feeCategoryId;
    }

    public String getFeeCategoryName() {
        return feeCategoryName;
    }

    public void setFeeCategoryName(String feeCategoryName) {
        this.feeCategoryName = feeCategoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public String getFineType() {
        return fineType;
    }

    public void setFineType(String fineType) {
        this.fineType = fineType;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }
}
