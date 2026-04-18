package audit

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/db"
)

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

func (s *Service) Log(eventType string, accountID, deviceID, adminName, ip *string, details map[string]any) {
	go db.WriteAudit(context.Background(), s.pool, db.AuditEvent{
		EventType: eventType,
		AccountID: accountID,
		DeviceID:  deviceID,
		AdminName: adminName,
		IP:        ip,
		Details:   details,
	})
}
