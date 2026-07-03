package com.mgps.school.dto;

/**
 * DTO for creating or updating subscription plans.
 */
public class SubscriptionPlanRequestDTO {

    private String planName;
    private String description;
    private Integer maxStudents;
    private Integer maxStaff;
    private Integer maxUsers;
    private String monthlyPrice;
    private Boolean isActive = true;

    public SubscriptionPlanRequestDTO() {
    }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
    public Integer getMaxStaff() { return maxStaff; }
    public void setMaxStaff(Integer maxStaff) { this.maxStaff = maxStaff; }
    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
    public String getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(String monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
