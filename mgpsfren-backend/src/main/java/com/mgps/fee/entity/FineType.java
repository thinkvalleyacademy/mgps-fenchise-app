package com.mgps.fee.entity;

/**
 * How a {@link FeeStructure} charges a late-payment fine once its due date
 * (plus grace period) has passed.
 */
public enum FineType {
    /** No fine is ever charged for this fee structure. */
    NONE,
    /** A single flat amount, charged once the fee becomes overdue. */
    FLAT,
    /** {@code fineAmount} percent of the outstanding principal, per whole month overdue. */
    PERCENTAGE_PER_MONTH
}
