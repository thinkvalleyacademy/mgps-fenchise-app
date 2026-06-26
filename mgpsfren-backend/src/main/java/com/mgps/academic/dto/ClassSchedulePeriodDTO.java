package com.mgps.academic.dto;

import java.time.LocalTime;
import java.util.UUID;

public class ClassSchedulePeriodDTO {
    private UUID id;
    private String className;
    private String academicSession;
    private String periodName;
    private Integer displayOrder;
    private LocalTime startTime;
    private LocalTime endTime;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
