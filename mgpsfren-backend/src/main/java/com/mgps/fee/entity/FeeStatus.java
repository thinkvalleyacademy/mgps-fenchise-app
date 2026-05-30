package com.mgps.fee.entity;

public enum FeeStatus {
    UNPAID("Unpaid"),
    PARTIAL("Partial"),
    PAID("Paid");

    private final String displayName;

    FeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
