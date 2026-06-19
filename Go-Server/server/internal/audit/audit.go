package audit

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

// Log records an audit event. Fire-and-forget via a goroutine (best-effort);
// that behavior is intentionally unchanged in this pass.
//
// eventType is the typed EventType taxonomy (see events.go). Untyped string
// literals at not-yet-migrated call sites still satisfy this parameter, so the
// migration can proceed incrementally without breaking emit sites.
func (s *Service) Log(eventType EventType, accountID, deviceID, adminName, ip *string, details map[string]any) {
	go db.WriteAudit(context.Background(), s.pool, db.AuditEvent{
		EventType: string(eventType),
		AccountID: accountID,
		DeviceID:  deviceID,
		AdminName: adminName,
		IP:        ip,
		Details:   details,
	})
}
