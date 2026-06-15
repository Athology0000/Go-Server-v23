package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

type Challenge struct {
	DeviceID  string `json:"device_id"`
	Challenge string `json:"challenge"`
	SourceIP  string `json:"source_ip"`
}

const challengeTTL = 30 * time.Second

func challengeKey(deviceID string) string {
	return fmt.Sprintf("auth:challenge:%s", deviceID)
}

func StoreChallenge(ctx context.Context, rdb *redis.Client, c *Challenge) error {
	data, err := json.Marshal(c)
	if err != nil {
		return err
	}
	return rdb.Set(ctx, challengeKey(c.DeviceID), data, challengeTTL).Err()
}

func GetChallenge(ctx context.Context, rdb *redis.Client, deviceID string) (*Challenge, error) {
	data, err := rdb.Get(ctx, challengeKey(deviceID)).Bytes()
	if err != nil {
		return nil, err
	}
	c := &Challenge{}
	return c, json.Unmarshal(data, c)
}

func DeleteChallenge(ctx context.Context, rdb *redis.Client, deviceID string) error {
	return rdb.Del(ctx, challengeKey(deviceID)).Err()
}

// ConsumeChallenge atomically fetches and deletes the challenge for deviceID via
// Redis GETDEL. Concurrent /auth/finish calls cannot double-consume: exactly one
// caller receives the value, the rest get redis.Nil. Returns redis.Nil if absent.
func ConsumeChallenge(ctx context.Context, rdb *redis.Client, deviceID string) (*Challenge, error) {
	data, err := rdb.GetDel(ctx, challengeKey(deviceID)).Bytes()
	if err != nil {
		return nil, err
	}
	c := &Challenge{}
	return c, json.Unmarshal(data, c)
}
