package db

import (
	"context"
	"encoding/json"
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
	pool.Exec(ctx,
		`INSERT INTO audit_log (event_type, account_id, device_id, admin_name, ip, details)
		 VALUES ($1, $2, $3, $4, $5, $6)`,
		e.EventType, e.AccountID, e.DeviceID, e.AdminName, e.IP, details)
}

func ListActivityLog(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*AuditRecord, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, event_type, account_id, device_id, admin_name, ip, details, created_at
		 FROM audit_log
		 WHERE event_type IN (
		   'panel.login.success','panel.login.fail',
		   'auth.start.success','auth.start.fail',
		   'auth.finish.success','auth.finish.fail',
		   'auth.device.suspended',
		   'panel.key.redeem.success'
		 )
		 ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
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
