package db

import (
	"context"
	"encoding/json"

	"github.com/jackc/pgx/v5/pgxpool"
)

// InsertSessionActivity appends one heartbeat activity record. activity is stored
// verbatim as JSONB (pgx's jsonb codec marshals the value), so whatever shape the
// loader reports — today a string array, later macro/duration/event objects — is
// preserved without a schema change. Callers pass json.RawMessage("[]") when empty.
func InsertSessionActivity(
	ctx context.Context,
	pool *pgxpool.Pool,
	sessionID string,
	accountID string,
	deviceID string,
	activity json.RawMessage,
) error {
	_, err := pool.Exec(ctx, `
		INSERT INTO session_activity (session_id, account_id, device_id, activity)
		VALUES ($1, $2, $3, $4)
	`, sessionID, accountID, deviceID, activity)

	return err
}
