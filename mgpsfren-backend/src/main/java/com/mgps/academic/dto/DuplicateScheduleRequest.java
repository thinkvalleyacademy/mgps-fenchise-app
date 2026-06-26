package com.mgps.academic.dto;

public class DuplicateScheduleRequest {
    private String sourceClassName;
    private String sourceSession;
    private Integer sourceWeekNumber;
    private String targetClassName;
    private String targetSession;
    private Integer targetWeekNumber;

    // Getters and Setters
    public String getSourceClassName() { return sourceClassName; }
    public void setSourceClassName(String sourceClassName) { this.sourceClassName = sourceClassName; }
    public String getSourceSession() { return sourceSession; }
    public void setSourceSession(String sourceSession) { this.sourceSession = sourceSession; }
    public Integer getSourceWeekNumber() { return sourceWeekNumber; }
    public void setSourceWeekNumber(Integer sourceWeekNumber) { this.sourceWeekNumber = sourceWeekNumber; }
    public String getTargetClassName() { return targetClassName; }
    public void setTargetClassName(String targetClassName) { this.targetClassName = targetClassName; }
    public String getTargetSession() { return targetSession; }
    public void setTargetSession(String targetSession) { this.targetSession = targetSession; }
    public Integer getTargetWeekNumber() { return targetWeekNumber; }
    public void setTargetWeekNumber(Integer targetWeekNumber) { this.targetWeekNumber = targetWeekNumber; }
}
