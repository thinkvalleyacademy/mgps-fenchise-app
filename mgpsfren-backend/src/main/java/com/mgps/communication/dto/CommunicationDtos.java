package com.mgps.communication.dto;

import com.mgps.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CommunicationDtos {
    private CommunicationDtos() {
    }

    public static class AnnouncementRequest {
        private UUID schoolId;
        private String title;
        private String body;
        private UserRole audienceRole;
        private UUID classId;
        private LocalDateTime expiresAt;

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
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }

    public static class AnnouncementResponse {
        private UUID announcementId;
        private UUID schoolId;
        private String title;
        private String body;
        private UserRole audienceRole;
        private UUID classId;
        private UUID createdBy;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getAnnouncementId() { return announcementId; }
        public void setAnnouncementId(UUID announcementId) { this.announcementId = announcementId; }
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
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class NotificationResponse {
        private UUID notificationId;
        private String title;
        private String body;
        private String category;
        private boolean read;
        private LocalDateTime createdAt;

        public UUID getNotificationId() { return notificationId; }
        public void setNotificationId(UUID notificationId) { this.notificationId = notificationId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
