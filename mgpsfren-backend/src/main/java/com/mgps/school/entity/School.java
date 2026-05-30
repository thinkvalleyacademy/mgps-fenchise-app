package com.mgps.school.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * School Entity - Represents a school in the MGPS franchise system.
 */
@Entity
@Table(name = "schools")
public class School {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String adminEmail;

    @Column(length = 20)
    private String adminPhone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String postalCode;

    @Column(length = 255)
    private String logoUrl;

    @Column(nullable = false, unique = true, length = 100)
    private String databaseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private SchoolStatus status = SchoolStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchoolDomain> domains = new ArrayList<>();

    public School() {
    }

    public School(UUID id, String name, String adminEmail, String adminPhone, String address, String city,
                  String state, String postalCode, String logoUrl, String databaseName,
                  SubscriptionPlan subscriptionPlan, SchoolStatus status,
                  LocalDateTime createdAt, LocalDateTime updatedAt, List<SchoolDomain> domains) {
        this.id = id;
        this.name = name;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.logoUrl = logoUrl;
        this.databaseName = databaseName;
        this.subscriptionPlan = subscriptionPlan;
        this.status = status != null ? status : SchoolStatus.ACTIVE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.domains = domains != null ? domains : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public SchoolStatus getStatus() { return status; }
    public void setStatus(SchoolStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<SchoolDomain> getDomains() { return domains; }
    public void setDomains(List<SchoolDomain> domains) { this.domains = domains; }

    public void addDomain(SchoolDomain domain) {
        if (domains == null) {
            domains = new ArrayList<>();
        }
        domains.add(domain);
        domain.setSchool(this);
    }

    public SchoolDomain getPrimaryDomain() {
        if (domains == null) {
            return null;
        }
        return domains.stream()
            .filter(domain -> Boolean.TRUE.equals(domain.getIsPrimary()))
            .findFirst()
            .orElse(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String adminEmail;
        private String adminPhone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String logoUrl;
        private String databaseName;
        private SubscriptionPlan subscriptionPlan;
        private SchoolStatus status = SchoolStatus.ACTIVE;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<SchoolDomain> domains = new ArrayList<>();

        private Builder() {
        }

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder adminEmail(String adminEmail) { this.adminEmail = adminEmail; return this; }
        public Builder adminPhone(String adminPhone) { this.adminPhone = adminPhone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder databaseName(String databaseName) { this.databaseName = databaseName; return this; }
        public Builder subscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; return this; }
        public Builder status(SchoolStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder domains(List<SchoolDomain> domains) { this.domains = domains; return this; }

        public School build() {
            return new School(id, name, adminEmail, adminPhone, address, city, state, postalCode,
                logoUrl, databaseName, subscriptionPlan, status, createdAt, updatedAt, domains);
        }
    }
}
