package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class FeeStructureDTO {
    private UUID id;
    private UUID schoolId;
    private UUID academicYearId;
    private UUID classId;
    private UUID feeCategoryId;
    private String feeCategoryName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private Boolean isActive;

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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
