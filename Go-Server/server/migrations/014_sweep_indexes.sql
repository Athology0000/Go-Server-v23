-- Retention sweep (audit-log + expired-session TTL): the background sweeper now also deletes
-- expired sessions and panel_sessions by expires_at. Those columns had no index, so each sweep
-- did a seqscan. Index them so the periodic DELETEs stay cheap. (idx_audit_log_created_at already
-- exists from 001_initial.sql, and session_activity(session_id) from 008, so no index is needed
-- for the audit or child-delete paths.)
CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_panel_sessions_expires_at ON panel_sessions(expires_at);
