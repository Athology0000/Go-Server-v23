package cache

import (
	"fmt"
	"time"
)

// CheckRateLimit increments the counter for key within the rolling window and
// reports whether the request is still under limit, the remaining allowance, and
// when the window resets. The error return is retained for call-site
// compatibility and is always nil with the in-memory store.
func CheckRateLimit(store *Store, key string, limit int, window time.Duration) (bool, int, time.Time, error) {
	count := store.Incr(fmt.Sprintf("rl:%s", key), window)
	remaining := limit - count
	if remaining < 0 {
		remaining = 0
	}
	return count <= limit, remaining, time.Now().Add(window), nil
}
