package com.mgps.academic.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public final class TimetableDtos {
    private TimetableDtos() {
    }

    public static class TimetableRequest {
        private UUID schoolId;
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;
        private UUID subjectId;
        private UUID teacherId;
        private String roomName;
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private String notes;

        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public UUID getTeacherId() { return teacherId; }
        public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class TimetableResponse {
        private UUID timetableId;
        private UUID schoolId;
        private UUID academicYearId;
        private UUID classId;
        private UUID sectionId;
        private UUID subjectId;
        private UUID teacherId;
        private String roomName;
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private String notes;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UUID getTimetableId() { return timetableId; }
        public void setTimetableId(UUID timetableId) { this.timetableId = timetableId; }
        public UUID getSchoolId() { return schoolId; }
        public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }
        public UUID getAcademicYearId() { return academicYearId; }
        public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
        public UUID getClassId() { return classId; }
        public void setClassId(UUID classId) { this.classId = classId; }
        public UUID getSectionId() { return sectionId; }
        public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
        public UUID getSubjectId() { return subjectId; }
        public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
        public UUID getTeacherId() { return teacherId; }
        public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class TimetableConflictResponse {
        private boolean conflict;
        private String reason;

        public boolean isConflict() { return conflict; }
        public void setConflict(boolean conflict) { this.conflict = conflict; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
