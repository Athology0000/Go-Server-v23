package cache

import (
	"testing"
	"time"
)

func TestStoreSetGetDel(t *testing.T) {
	s := NewStore()
	defer s.Close()

	s.Set("k", []byte("v"), time.Minute)
	got, ok := s.Get("k")
	if !ok || string(got) != "v" {
		t.Fatalf("Get after Set = %q, %v; want \"v\", true", got, ok)
	}

	s.Del("k")
	if _, ok := s.Get("k"); ok {
		t.Fatalf("Get after Del = present; want absent")
	}
}

func TestStoreExpiry(t *testing.T) {
	s := NewStore()
	defer s.Close()

	s.Set("k", []byte("v"), 10*time.Millisecond)
	time.Sleep(20 * time.Millisecond)
	if _, ok := s.Get("k"); ok {
		t.Fatalf("Get after TTL = present; want expired")
	}
}

func TestStoreIncr(t *testing.T) {
	s := NewStore()
	defer s.Close()

	if n := s.Incr("rl:x", time.Minute); n != 1 {
		t.Fatalf("first Incr = %d; want 1", n)
	}
	if n := s.Incr("rl:x", time.Minute); n != 2 {
		t.Fatalf("second Incr = %d; want 2", n)
	}
}

func TestStoreIncrResetsAfterWindow(t *testing.T) {
	s := NewStore()
	defer s.Close()

	s.Incr("rl:x", 10*time.Millisecond)
	time.Sleep(20 * time.Millisecond)
	if n := s.Incr("rl:x", 10*time.Millisecond); n != 1 {
		t.Fatalf("Incr after window = %d; want 1 (reset)", n)
	}
}

func TestChallengeRoundTrip(t *testing.T) {
	s := NewStore()
	defer s.Close()

	in := &Challenge{DeviceID: "dev1", Challenge: "abc", SourceIP: "1.2.3.4"}
	if err := StoreChallenge(s, in); err != nil {
		t.Fatalf("StoreChallenge: %v", err)
	}

	out, err := GetChallenge(s, "dev1")
	if err != nil {
		t.Fatalf("GetChallenge: %v", err)
	}
	if out.Challenge != "abc" || out.SourceIP != "1.2.3.4" {
		t.Fatalf("GetChallenge = %+v; want challenge abc / ip 1.2.3.4", out)
	}

	DeleteChallenge(s, "dev1")
	if _, err := GetChallenge(s, "dev1"); err != ErrChallengeNotFound {
		t.Fatalf("GetChallenge after delete = %v; want ErrChallengeNotFound", err)
	}
}
