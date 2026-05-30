package com.mgps.school.dto;

import com.mgps.school.entity.SchoolStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for School Creation Response.
 */
public class SchoolCreatedDTO {

    private UUID schoolId;
    private String name;
    private String databaseName;
    private String adminUrl;
    private SchoolStatus status;
    private LocalDateTime createdAt;

    public SchoolCreatedDTO() {
    }

    public SchoolCreatedDTO(UUID schoolId, String name, String databaseName, String adminUrl,
                            SchoolStatus status, LocalDateTime createdAt) {
        this.schoolId = schoolId;
        this.name = name;
        this.databaseName = databaseName;
        this.adminUrl = adminUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getSchoolId() { return schoolId; }
    public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    public String getAdminUrl() { return adminUrl; }
    public void setAdminUrl(String adminUrl) { this.adminUrl = adminUrl; }
    public SchoolStatus getStatus() { return status; }
    public void setStatus(SchoolStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UUID schoolId;
        private String name;
        private String databaseName;
        private String adminUrl;
        private SchoolStatus status;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder schoolId(UUID schoolId) { this.schoolId = schoolId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder databaseName(String databaseName) { this.databaseName = databaseName; return this; }
        public Builder adminUrl(String adminUrl) { this.adminUrl = adminUrl; return this; }
        public Builder status(SchoolStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SchoolCreatedDTO build() {
            return new SchoolCreatedDTO(schoolId, name, databaseName, adminUrl, status, createdAt);
        }
    }
}
