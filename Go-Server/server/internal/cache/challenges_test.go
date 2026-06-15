package cache

import (
	"context"
	"os"
	"sync"
	"testing"

	"github.com/redis/go-redis/v9"
)

func testRedis(t *testing.T) *redis.Client {
	url := os.Getenv("TEST_REDIS_URL")
	if url == "" {
		t.Skip("TEST_REDIS_URL not set; skipping redis integration test")
	}
	opt, err := redis.ParseURL(url)
	if err != nil {
		t.Fatalf("parse TEST_REDIS_URL: %v", err)
	}
	return redis.NewClient(opt)
}

func TestConsumeChallengeIsSingleUse(t *testing.T) {
	ctx := context.Background()
	rdb := testRedis(t)
	deviceID := "test-device-consume"
	_ = DeleteChallenge(ctx, rdb, deviceID)

	if err := StoreChallenge(ctx, rdb, &Challenge{DeviceID: deviceID, Challenge: "abc", SourceIP: "1.2.3.4"}); err != nil {
		t.Fatalf("store: %v", err)
	}

	const racers = 8
	var wg sync.WaitGroup
	wins := make(chan *Challenge, racers)
	for i := 0; i < racers; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			if c, err := ConsumeChallenge(ctx, rdb, deviceID); err == nil {
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
	ctx := context.Background()
	rdb := testRedis(t)
	deviceID := "test-device-absent"
	_ = DeleteChallenge(ctx, rdb, deviceID)

	if _, err := ConsumeChallenge(ctx, rdb, deviceID); err != redis.Nil {
		t.Fatalf("expected redis.Nil for absent challenge, got %v", err)
	}
}
