package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class BulkFeePaymentResultDTO {
    private UUID studentId;
    private String collectionType;
    private List<FeePaymentDTO> payments;
    private BigDecimal totalCollected;
    private BigDecimal discountPercentApplied;
    private BigDecimal totalDiscountApplied;

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public List<FeePaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<FeePaymentDTO> payments) {
        this.payments = payments;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getDiscountPercentApplied() {
        return discountPercentApplied;
    }

    public void setDiscountPercentApplied(BigDecimal discountPercentApplied) {
        this.discountPercentApplied = discountPercentApplied;
    }

    public BigDecimal getTotalDiscountApplied() {
        return totalDiscountApplied;
    }

    public void setTotalDiscountApplied(BigDecimal totalDiscountApplied) {
        this.totalDiscountApplied = totalDiscountApplied;
    }
}
