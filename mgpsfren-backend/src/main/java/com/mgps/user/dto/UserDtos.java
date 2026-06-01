package com.mgps.user.dto;

import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserDtos {
    private UserDtos() {
    }

    public static class RegisterUserRequest {
        private UUID schoolId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;
        private UserRole role;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class LogoutRequest {
        private String accessToken;
        private String refreshToken;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class UserStatusRequest {
        private UserStatus status;

        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
    }

    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private UserRole role;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }

    public static class BulkImportResult {
        private int totalRows;
        private int createdCount;
        private int failedCount;
        private List<RowResult> rowResults;

        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        public int getCreatedCount() { return createdCount; }
        public void setCreatedCount(int createdCount) { this.createdCount = createdCount; }
        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
        public List<RowResult> getRowResults() { return rowResults; }
        public void setRowResults(List<RowResult> rowResults) { this.rowResults = rowResults; }
    }

    public static class RowResult {
        private int rowNumber;
        private boolean success;
        private String message;
        private String email;

        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserProfile profile;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
        public UserProfile getProfile() { return profile; }
        public void setProfile(UserProfile profile) { this.profile = profile; }
    }

    public static class UserProfile {
        private UUID userId;
        private UUID schoolId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private UserRole role;
        private UserStatus status;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
