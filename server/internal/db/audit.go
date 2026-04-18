package db

import (
	"context"
	"encoding/json"
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

func WriteAudit(ctx context.Context, pool *pgxpool.Pool, e AuditEvent) {
	details, _ := json.Marshal(e.Details)
	pool.Exec(ctx,
		`INSERT INTO audit_log (event_type, account_id, device_id, admin_name, ip, details)
		 VALUES ($1, $2, $3, $4, $5, $6)`,
		e.EventType, e.AccountID, e.DeviceID, e.AdminName, e.IP, details)
}
