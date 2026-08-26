package com.mgps.fee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fee_settings")
public class FeeSettings {

    @Id
    private UUID id;

    @Column(name = "school_id", nullable = false, unique = true)
    private UUID schoolId;

    @Column(name = "yearly_discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal yearlyDiscountPercent = new BigDecimal("5.00");

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public FeeSettings() {
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

    public BigDecimal getYearlyDiscountPercent() {
        return yearlyDiscountPercent;
    }

    public void setYearlyDiscountPercent(BigDecimal yearlyDiscountPercent) {
        this.yearlyDiscountPercent = yearlyDiscountPercent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
