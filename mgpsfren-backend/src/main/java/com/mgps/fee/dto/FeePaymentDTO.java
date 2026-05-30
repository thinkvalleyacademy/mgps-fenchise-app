package com.mgps.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FeePaymentDTO {
    private UUID id;
    private UUID schoolId;
    private UUID studentFeeId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String transactionId;
    private String receiptNumber;
    private String remarks;
    private UUID processedBy;

    public FeePaymentDTO() {
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

    public UUID getStudentFeeId() {
        return studentFeeId;
    }

    public void setStudentFeeId(UUID studentFeeId) {
        this.studentFeeId = studentFeeId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public UUID getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(UUID processedBy) {
        this.processedBy = processedBy;
    }
}
