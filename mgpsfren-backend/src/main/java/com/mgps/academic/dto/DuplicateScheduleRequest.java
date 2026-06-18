package com.mgps.academic.dto;

public class DuplicateScheduleRequest {
    private String sourceClassName;
    private String sourceSession;
    private String targetClassName;
    private String targetSession;

    // Getters and Setters
    public String getSourceClassName() { return sourceClassName; }
    public void setSourceClassName(String sourceClassName) { this.sourceClassName = sourceClassName; }
    public String getSourceSession() { return sourceSession; }
    public void setSourceSession(String sourceSession) { this.sourceSession = sourceSession; }
    public String getTargetClassName() { return targetClassName; }
    public void setTargetClassName(String targetClassName) { this.targetClassName = targetClassName; }
    public String getTargetSession() { return targetSession; }
    public void setTargetSession(String targetSession) { this.targetSession = targetSession; }
}
