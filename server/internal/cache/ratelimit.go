package cache

import (
	"context"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

func CheckRateLimit(ctx context.Context, rdb *redis.Client, key string, limit int, window time.Duration) (bool, error) {
	redisKey := fmt.Sprintf("rl:%s", key)
	pipe := rdb.Pipeline()
	incr := pipe.Incr(ctx, redisKey)
	pipe.Expire(ctx, redisKey, window)
	if _, err := pipe.Exec(ctx); err != nil {
		return false, err
	}
	return incr.Val() <= int64(limit), nil
}
