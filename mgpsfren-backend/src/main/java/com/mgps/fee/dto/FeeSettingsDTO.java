package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class FeeSettingsDTO {
    private UUID schoolId;
    private BigDecimal yearlyDiscountPercent;

    public FeeSettingsDTO() {
    }

    public FeeSettingsDTO(UUID schoolId, BigDecimal yearlyDiscountPercent) {
        this.schoolId = schoolId;
        this.yearlyDiscountPercent = yearlyDiscountPercent;
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
}
