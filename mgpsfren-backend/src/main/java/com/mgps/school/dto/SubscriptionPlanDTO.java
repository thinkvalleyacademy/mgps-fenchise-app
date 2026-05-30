package com.mgps.school.dto;

import java.util.UUID;

/**
 * DTO for Subscription Plan Response.
 */
public class SubscriptionPlanDTO {

    private UUID planId;
    private String planName;
    private String description;
    private Integer maxStudents;
    private Integer maxStaff;
    private Integer maxUsers;
    private String monthlyPrice;
    private Boolean isActive;

    public SubscriptionPlanDTO() {
    }

    public SubscriptionPlanDTO(UUID planId, String planName, String description, Integer maxStudents,
                               Integer maxStaff, Integer maxUsers, String monthlyPrice, Boolean isActive) {
        this.planId = planId;
        this.planName = planName;
        this.description = description;
        this.maxStudents = maxStudents;
        this.maxStaff = maxStaff;
        this.maxUsers = maxUsers;
        this.monthlyPrice = monthlyPrice;
        this.isActive = isActive;
    }

    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }
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

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID planId;
        private String planName;
        private String description;
        private Integer maxStudents;
        private Integer maxStaff;
        private Integer maxUsers;
        private String monthlyPrice;
        private Boolean isActive;

        private Builder() {
        }

        public Builder planId(UUID planId) { this.planId = planId; return this; }
        public Builder planName(String planName) { this.planName = planName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder maxStudents(Integer maxStudents) { this.maxStudents = maxStudents; return this; }
        public Builder maxStaff(Integer maxStaff) { this.maxStaff = maxStaff; return this; }
        public Builder maxUsers(Integer maxUsers) { this.maxUsers = maxUsers; return this; }
        public Builder monthlyPrice(String monthlyPrice) { this.monthlyPrice = monthlyPrice; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public SubscriptionPlanDTO build() {
            return new SubscriptionPlanDTO(planId, planName, description, maxStudents, maxStaff, maxUsers, monthlyPrice, isActive);
        }
    }
}
