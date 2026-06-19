package db

import (
	"context"
	"encoding/json"
	"log"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type AuditEvent struct {
	EventType string
	AccountID *string
	DeviceID  *string
	AdminName *string
	IP        *string
	Details   map[string]any
}

type AuditRecord struct {
	ID        string         `json:"id"`
	EventType string         `json:"event_type"`
	AccountID *string        `json:"account_id"`
	DeviceID  *string        `json:"device_id"`
	AdminName *string        `json:"admin_name"`
	IP        *string        `json:"ip"`
	Details   map[string]any `json:"details"`
	CreatedAt time.Time      `json:"created_at"`
}

func WriteAudit(ctx context.Context, pool *pgxpool.Pool, e AuditEvent) {
	details, _ := json.Marshal(e.Details)
	// Audit writes are best-effort, but a silent failure leaves forensic gaps with no signal.
	// Log the error so dropped events are at least visible (and alertable) rather than invisible.
	if _, err := pool.Exec(ctx,
		`INSERT INTO audit_log (event_type, account_id, device_id, admin_name, ip, details)
		 VALUES ($1, $2, $3, $4, $5, $6)`,
		e.EventType, e.AccountID, e.DeviceID, e.AdminName, e.IP, details); err != nil {
		log.Printf("[audit] failed to write event %q: %v", e.EventType, err)
	}
}

// ListActivityLog returns the activity-feed subset of the audit log. The set of
// event types is no longer hardcoded here; it is supplied by the caller from the
// audit-package taxonomy (audit.ActivityEventTypes), so the feed filter cannot
// drift from the emit sites. Behavior is unchanged: passing today's taxonomy
// yields the exact same rows as the former hardcoded IN(...) list.
func ListActivityLog(ctx context.Context, pool *pgxpool.Pool, eventTypes []string, limit, offset int) ([]*AuditRecord, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, event_type, account_id, device_id, admin_name, ip, details, created_at
		 FROM audit_log
		 WHERE event_type = ANY($1)
		 ORDER BY created_at DESC LIMIT $2 OFFSET $3`, eventTypes, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var records []*AuditRecord
	for rows.Next() {
		r := &AuditRecord{}
		var detailsJSON []byte
		if err := rows.Scan(&r.ID, &r.EventType, &r.AccountID, &r.DeviceID,
			&r.AdminName, &r.IP, &detailsJSON, &r.CreatedAt); err != nil {
			return nil, err
		}
		json.Unmarshal(detailsJSON, &r.Details)
		records = append(records, r)
	}
	return records, nil
}

func ListAuditLog(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*AuditRecord, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, event_type, account_id, device_id, admin_name, ip, details, created_at
		 FROM audit_log ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var records []*AuditRecord
	for rows.Next() {
		r := &AuditRecord{}
		var detailsJSON []byte
		if err := rows.Scan(&r.ID, &r.EventType, &r.AccountID, &r.DeviceID,
			&r.AdminName, &r.IP, &detailsJSON, &r.CreatedAt); err != nil {
			return nil, err
		}
		json.Unmarshal(detailsJSON, &r.Details)
		records = append(records, r)
	}
	return records, nil
}
