package com.mgps.enquiry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Enquiry Entity - Represents an inquiry submitted through the public website.
 * Stored in mgps_master database.
 */
@Entity
@Table(name = "enquiries")
public class Enquiry {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(name = "student_class", length = 50)
    private String studentClass;

    @Column(columnDefinition = "TEXT")
    private String query;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Enquiry() {
        this.id = UUID.randomUUID();
    }

    public Enquiry(UUID id, String fullName, String email, String mobileNumber, String studentClass, String query) {
        this.id = id != null ? id : UUID.randomUUID();
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.studentClass = studentClass;
        this.query = query;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private String fullName;
        private String email;
        private String mobileNumber;
        private String studentClass;
        private String query;

        private Builder() {}

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder mobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; return this; }
        public Builder studentClass(String studentClass) { this.studentClass = studentClass; return this; }
        public Builder query(String query) { this.query = query; return this; }

        public Enquiry build() {
            return new Enquiry(id, fullName, email, mobileNumber, studentClass, query);
        }
    }
}
