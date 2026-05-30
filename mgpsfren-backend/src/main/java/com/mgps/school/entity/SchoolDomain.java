package com.mgps.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SchoolDomain Entity - Maps domains/subdomains to schools.
 */
@Entity
@Table(name = "school_domains")
public class SchoolDomain {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(nullable = false, unique = true, length = 255)
    private String domainName;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isPrimary = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SchoolDomain() {
    }

    public SchoolDomain(UUID id, School school, String domainName, Boolean isPrimary, Boolean isActive,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.school = school;
        this.domainName = domainName;
        this.isPrimary = isPrimary != null ? isPrimary : Boolean.TRUE;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean primary) { isPrimary = primary; }
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
        private School school;
        private String domainName;
        private Boolean isPrimary = Boolean.TRUE;
        private Boolean isActive = Boolean.TRUE;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder school(School school) { this.school = school; return this; }
        public Builder domainName(String domainName) { this.domainName = domainName; return this; }
        public Builder isPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SchoolDomain build() {
            return new SchoolDomain(id, school, domainName, isPrimary, isActive, createdAt, updatedAt);
        }
    }
}
