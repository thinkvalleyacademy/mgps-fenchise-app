package com.mgps.school.entity;

/**
 * School Status Enum
 * Represents the operational status of a school
 */
public enum SchoolStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");
    
    private final String displayName;
    
    SchoolStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
