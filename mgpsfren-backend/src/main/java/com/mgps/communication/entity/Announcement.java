package com.mgps.communication.entity;

import com.mgps.user.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    private UUID id;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Null means visible to every role in the school. */
    @Enumerated(EnumType.STRING)
    @Column(name = "audience_role", length = 30)
    private UserRole audienceRole;

    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Announcement() {
    }

    public Announcement(UUID id, UUID schoolId, String title, String body, UserRole audienceRole, UUID classId,
                        UUID createdBy, LocalDateTime expiresAt) {
        this.id = id;
        this.schoolId = schoolId;
        this.title = title;
        this.body = body;
        this.audienceRole = audienceRole;
        this.classId = classId;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSchoolId() { return schoolId; }
    public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public UserRole getAudienceRole() { return audienceRole; }
    public void setAudienceRole(UserRole audienceRole) { this.audienceRole = audienceRole; }
    public UUID getClassId() { return classId; }
    public void setClassId(UUID classId) { this.classId = classId; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
