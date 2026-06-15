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
);

CREATE INDEX IF NOT EXISTS idx_app_activity_logs_school_created
    ON app_activity_logs(school_id, created_at DESC);
