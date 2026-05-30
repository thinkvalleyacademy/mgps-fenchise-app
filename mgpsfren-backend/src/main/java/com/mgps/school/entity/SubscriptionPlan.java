package com.mgps.school.entity;

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
 * SubscriptionPlan Entity - Represents subscription plans for schools.
 */
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer maxStudents;

    @Column(nullable = false)
    private Integer maxStaff;

    @Column(nullable = false)
    private Integer maxUsers;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(UUID id, String planName, String description, Integer maxStudents, Integer maxStaff,
                            Integer maxUsers, BigDecimal monthlyPrice, String features, Boolean isActive,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.planName = planName;
        this.description = description;
        this.maxStudents = maxStudents;
        this.maxStaff = maxStaff;
        this.maxUsers = maxUsers;
        this.monthlyPrice = monthlyPrice;
        this.features = features;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private String planName;
        private String description;
        private Integer maxStudents;
        private Integer maxStaff;
        private Integer maxUsers;
        private BigDecimal monthlyPrice;
        private String features;
        private Boolean isActive = Boolean.TRUE;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder planName(String planName) { this.planName = planName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder maxStudents(Integer maxStudents) { this.maxStudents = maxStudents; return this; }
        public Builder maxStaff(Integer maxStaff) { this.maxStaff = maxStaff; return this; }
        public Builder maxUsers(Integer maxUsers) { this.maxUsers = maxUsers; return this; }
        public Builder monthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; return this; }
        public Builder features(String features) { this.features = features; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SubscriptionPlan build() {
            return new SubscriptionPlan(id, planName, description, maxStudents, maxStaff, maxUsers, monthlyPrice,
                features, isActive, createdAt, updatedAt);
        }
    }
}
