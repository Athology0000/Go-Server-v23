package cache

import (
	"encoding/json"
	"errors"
	"fmt"
	"time"
)

type Challenge struct {
	DeviceID  string `json:"device_id"`
	Challenge string `json:"challenge"`
	SourceIP  string `json:"source_ip"`
	Used      bool   `json:"used"`
}

const challengeTTL = 30 * time.Second

// ErrChallengeNotFound is returned by GetChallenge when no live challenge exists
// for the device (never stored, already consumed, or expired).
var ErrChallengeNotFound = errors.New("challenge not found")

func challengeKey(deviceID string) string {
	return fmt.Sprintf("auth:challenge:%s", deviceID)
}

func StoreChallenge(store *Store, c *Challenge) error {
	data, err := json.Marshal(c)
	if err != nil {
		return err
	}
	store.Set(challengeKey(c.DeviceID), data, challengeTTL)
	return nil
}

func GetChallenge(store *Store, deviceID string) (*Challenge, error) {
	data, ok := store.Get(challengeKey(deviceID))
	if !ok {
		return nil, ErrChallengeNotFound
	}
	c := &Challenge{}
	return c, json.Unmarshal(data, c)
}

func DeleteChallenge(store *Store, deviceID string) {
	store.Del(challengeKey(deviceID))
}
