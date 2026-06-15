package cache

import (
	"context"
	"errors"
	"os"
	"sync"
	"testing"

	"github.com/jackc/pgx/v5/pgxpool"
)

// testPool connects to the integration Postgres (TEST_DB_URL) and ensures the
// auth_challenges table exists. It skips cleanly when the DB is unset/unreachable.
func testPool(t *testing.T) (context.Context, *pgxpool.Pool) {
	t.Helper()
	url := os.Getenv("TEST_DB_URL")
	if url == "" {
		t.Skip("TEST_DB_URL not set; skipping postgres integration test")
	}
	ctx := context.Background()
	pool, err := pgxpool.New(ctx, url)
	if err != nil {
		t.Fatalf("connect postgres: %v", err)
	}
	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		t.Skipf("postgres not reachable (%v); skipping", err)
	}
	t.Cleanup(pool.Close)

	// Mirror migration 011 so the test is self-contained against a fresh DB.
	if _, err := pool.Exec(ctx, `
		CREATE TABLE IF NOT EXISTS auth_challenges (
			device_id  TEXT PRIMARY KEY,
			challenge  TEXT NOT NULL,
			source_ip  TEXT NOT NULL,
			expires_at TIMESTAMPTZ NOT NULL
		)`); err != nil {
		t.Fatalf("ensure auth_challenges: %v", err)
	}
	return ctx, pool
}

func TestConsumeChallengeIsSingleUse(t *testing.T) {
	ctx, pool := testPool(t)
	deviceID := "test-device-consume"
	_ = DeleteChallenge(ctx, pool, deviceID)

	if err := StoreChallenge(ctx, pool, &Challenge{DeviceID: deviceID, Challenge: "abc", SourceIP: "1.2.3.4"}); err != nil {
		t.Fatalf("store: %v", err)
	}

	const racers = 8
	var wg sync.WaitGroup
	wins := make(chan *Challenge, racers)
	for i := 0; i < racers; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			if c, err := ConsumeChallenge(ctx, pool, deviceID); err == nil {
				wins <- c
			}
		}()
	}
	wg.Wait()
	close(wins)

	if got := len(wins); got != 1 {
		t.Fatalf("expected exactly 1 winner, got %d", got)
	}
}

func TestConsumeChallengeMissingReturnsError(t *testing.T) {
	ctx, pool := testPool(t)
	deviceID := "test-device-absent"
	_ = DeleteChallenge(ctx, pool, deviceID)

	if _, err := ConsumeChallenge(ctx, pool, deviceID); !errors.Is(err, ErrChallengeNotFound) {
		t.Fatalf("expected ErrChallengeNotFound for absent challenge, got %v", err)
	}
}
