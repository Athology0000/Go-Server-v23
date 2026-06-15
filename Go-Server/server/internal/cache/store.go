package cache

import (
	"sync"
	"time"
)

// Store is an in-process replacement for the former Redis cache. It backs the
// auth challenge store and the rate limiter with a TTL-keyed map.
//
// State is per-instance: it is not shared across replicas and is lost on
// restart. That is acceptable for the single-instance deployment this server
// runs as — auth challenges live ~30s and rate-limit windows ~1m, so a restart
// at worst clears a few in-flight challenges and resets rate counters. If this
// service is ever scaled to multiple replicas, rate limiting and challenge
// hand-off would need a shared backing store again.
type Store struct {
	mu    sync.Mutex
	items map[string]*item
	stop  chan struct{}
}

type item struct {
	data    []byte
	count   int
	expires time.Time
}

// NewStore creates an in-memory store and starts a background janitor that
// evicts expired entries once a minute.
func NewStore() *Store {
	s := &Store{
		items: make(map[string]*item),
		stop:  make(chan struct{}),
	}
	go s.janitor()
	return s
}

// Close stops the background janitor. It is safe to call once.
func (s *Store) Close() error {
	close(s.stop)
	return nil
}

func (s *Store) janitor() {
	t := time.NewTicker(time.Minute)
	defer t.Stop()
	for {
		select {
		case <-s.stop:
			return
		case now := <-t.C:
			s.mu.Lock()
			for k, it := range s.items {
				if now.After(it.expires) {
					delete(s.items, k)
				}
			}
			s.mu.Unlock()
		}
	}
}

// Set stores data under key with a time-to-live.
func (s *Store) Set(key string, data []byte, ttl time.Duration) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.items[key] = &item{data: data, expires: time.Now().Add(ttl)}
}

// Get returns the data stored under key and whether it was present and unexpired.
func (s *Store) Get(key string) ([]byte, bool) {
	s.mu.Lock()
	defer s.mu.Unlock()
	it, ok := s.items[key]
	if !ok || time.Now().After(it.expires) {
		if ok {
			delete(s.items, key)
		}
		return nil, false
	}
	return it.data, true
}

// Del removes key.
func (s *Store) Del(key string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	delete(s.items, key)
}

// Incr increments the counter at key, (re)setting its expiry to now+window, and
// returns the new count. This mirrors the previous Redis INCR+EXPIRE pipeline:
// a counter that keeps seeing traffic keeps its window pushed out, and a counter
// idle for longer than window resets to 1 on the next hit.
func (s *Store) Incr(key string, window time.Duration) int {
	s.mu.Lock()
	defer s.mu.Unlock()
	it, ok := s.items[key]
	if !ok || time.Now().After(it.expires) {
		it = &item{}
		s.items[key] = it
	}
	it.count++
	it.expires = time.Now().Add(window)
	return it.count
}
