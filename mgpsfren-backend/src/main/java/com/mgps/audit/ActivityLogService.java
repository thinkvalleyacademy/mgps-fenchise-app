package com.mgps.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgps.common.util.PiiMasking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ActivityLogService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ActivityLogService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void record(UUID schoolId, UUID actorUserId, String actorEmail, String action,
                       String entityType, UUID entityId, Map<String, ?> details) {
        ensureTable();
        jdbcTemplate.update("""
            INSERT INTO app_activity_logs
                (id, school_id, actor_user_id, actor_email, action, entity_type, entity_id, details, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSONB), ?)
            """,
            UUID.randomUUID(), schoolId, actorUserId, PiiMasking.maskEmail(actorEmail), action, entityType,
            entityId != null ? entityId.toString() : null, toJson(details), LocalDateTime.now());
    }

    public List<ActivityLogRecord> findRecent(int limit) {
        ensureTable();
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return jdbcTemplate.query("""
            SELECT id, school_id, actor_user_id, actor_email, action, entity_type, entity_id,
                   details::text, created_at
            FROM app_activity_logs
            ORDER BY created_at DESC
            LIMIT ?
            """, (rs, rowNum) -> new ActivityLogRecord(
                rs.getObject("id", UUID.class),
                rs.getObject("school_id", UUID.class),
                rs.getObject("actor_user_id", UUID.class),
                rs.getString("actor_email"),
                rs.getString("action"),
                rs.getString("entity_type"),
                rs.getString("entity_id"),
                rs.getString("details"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ), safeLimit);
    }

    private void ensureTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS app_activity_logs (
                id UUID PRIMARY KEY,
                school_id UUID,
                actor_user_id UUID,
                actor_email VARCHAR(255),
                action VARCHAR(100) NOT NULL,
                entity_type VARCHAR(100),
                entity_id VARCHAR(255),
                details JSONB,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """);
        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS idx_app_activity_logs_school_created
            ON app_activity_logs(school_id, created_at DESC)
            """);
    }

    private String toJson(Map<String, ?> details) {
        try {
            return objectMapper.writeValueAsString(details == null ? Map.of() : details);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to serialize activity details", ex);
        }
    }

    public record ActivityLogRecord(
        UUID id,
        UUID schoolId,
        UUID actorUserId,
        String actorEmail,
        String action,
        String entityType,
        String entityId,
        String details,
        LocalDateTime createdAt
    ) {
    }
}
