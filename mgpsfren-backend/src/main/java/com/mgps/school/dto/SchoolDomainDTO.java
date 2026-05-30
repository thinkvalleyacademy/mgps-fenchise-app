package com.mgps.school.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Domain Response.
 */
public class SchoolDomainDTO {

    private UUID domainId;
    private String domainName;
    private Boolean isPrimary;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public SchoolDomainDTO() {
    }

    public SchoolDomainDTO(UUID domainId, String domainName, Boolean isPrimary, Boolean isActive, LocalDateTime createdAt) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.isPrimary = isPrimary;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public UUID getDomainId() { return domainId; }
    public void setDomainId(UUID domainId) { this.domainId = domainId; }
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean primary) { isPrimary = primary; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID domainId;
        private String domainName;
        private Boolean isPrimary;
        private Boolean isActive;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder domainId(UUID domainId) { this.domainId = domainId; return this; }
        public Builder domainName(String domainName) { this.domainName = domainName; return this; }
        public Builder isPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SchoolDomainDTO build() {
            return new SchoolDomainDTO(domainId, domainName, isPrimary, isActive, createdAt);
        }
    }
}
