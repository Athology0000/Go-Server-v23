// Package cache provides the server's in-process challenge store and rate
// limiter, backed by an in-memory TTL map (see store.go). It previously wrapped
// a Redis client; that external dependency has been removed.
package cache
