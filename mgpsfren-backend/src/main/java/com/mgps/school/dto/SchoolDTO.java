package com.mgps.school.dto;

import com.mgps.school.entity.SchoolStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * DTO for School Response (full details).
 */
public class SchoolDTO {

    private UUID schoolId;
    private String name;
    private String adminEmail;
    private String adminPhone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String logoUrl;
    private String databaseName;
    private SchoolStatus status;
    private List<SchoolDomainDTO> domains = new ArrayList<>();
    private SubscriptionPlanDTO subscriptionPlan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SchoolDTO() {
    }

    public SchoolDTO(UUID schoolId, String name, String adminEmail, String adminPhone, String address, String city,
                     String state, String postalCode, String logoUrl, String databaseName, SchoolStatus status,
                     List<SchoolDomainDTO> domains, SubscriptionPlanDTO subscriptionPlan,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.schoolId = schoolId;
        this.name = name;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.logoUrl = logoUrl;
        this.databaseName = databaseName;
        this.status = status;
        this.domains = domains != null ? domains : new ArrayList<>();
        this.subscriptionPlan = subscriptionPlan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getSchoolId() { return schoolId; }
    public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    public SchoolStatus getStatus() { return status; }
    public void setStatus(SchoolStatus status) { this.status = status; }
    public List<SchoolDomainDTO> getDomains() { return domains; }
    public void setDomains(List<SchoolDomainDTO> domains) { this.domains = domains; }
    public SubscriptionPlanDTO getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlanDTO subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID schoolId;
        private String name;
        private String adminEmail;
        private String adminPhone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String logoUrl;
        private String databaseName;
        private SchoolStatus status;
        private List<SchoolDomainDTO> domains = new ArrayList<>();
        private SubscriptionPlanDTO subscriptionPlan;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder schoolId(UUID schoolId) { this.schoolId = schoolId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder adminEmail(String adminEmail) { this.adminEmail = adminEmail; return this; }
        public Builder adminPhone(String adminPhone) { this.adminPhone = adminPhone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder databaseName(String databaseName) { this.databaseName = databaseName; return this; }
        public Builder status(SchoolStatus status) { this.status = status; return this; }
        public Builder domains(List<SchoolDomainDTO> domains) { this.domains = domains; return this; }
        public Builder subscriptionPlan(SubscriptionPlanDTO subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SchoolDTO build() {
            return new SchoolDTO(schoolId, name, adminEmail, adminPhone, address, city, state, postalCode,
                logoUrl, databaseName, status, domains, subscriptionPlan, createdAt, updatedAt);
        }
    }
}
