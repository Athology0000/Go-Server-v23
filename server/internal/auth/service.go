package auth

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/redis/go-redis/v9"
	"github.com/cobalt/server/internal/cache"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/entitlement"
)

var (
	ErrNotFound         = errors.New("not found")
	ErrIPMismatch       = errors.New("ip mismatch")
	ErrHWIDMismatch     = errors.New("hwid mismatch")
	ErrUsernameMismatch = errors.New("username mismatch")
	ErrBadProof         = errors.New("bad proof")
	ErrDeviceBlocked    = errors.New("device blocked")
	ErrNoChallenge      = errors.New("no challenge")
)

type StartResult struct {
	Challenge string
	ExpiresIn int
}

type FinishResult struct {
	Authenticated bool
	Authorized    bool
	Reason        string
	SessionToken  string
	ExpiresIn     int
	PlanTier      string
	Modules       []string
	Features      []string
	ManifestURL   string
}

type Service struct {
	pool      *pgxpool.Pool
	rdb       *redis.Client
	entSvc    *entitlement.Service
	masterKey []byte
	pepper    []byte
	baseURL   string
}

func New(pool *pgxpool.Pool, rdb *redis.Client, entSvc *entitlement.Service, masterKey, pepper []byte, baseURL string) *Service {
	return &Service{pool: pool, rdb: rdb, entSvc: entSvc, masterKey: masterKey, pepper: pepper, baseURL: baseURL}
}

func (s *Service) Start(ctx context.Context, username, rawHWID, minecraftUsername, sourceIP string) (*StartResult, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	if account.Status != "active" {
		return nil, ErrDeviceBlocked
	}

	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	if device.BindingStatus == "suspended" || device.BindingStatus == "banned" {
		return nil, ErrDeviceBlocked
	}
	if device.BindingStatus != "hwid_pending" && device.BindingStatus != "fully_bound" {
		return nil, ErrNotFound
	}

	// IP must match enrollment_ip until fully_bound
	if device.BindingStatus == "hwid_pending" {
		if device.EnrollmentIP == nil || *device.EnrollmentIP != sourceIP {
			return nil, ErrIPMismatch
		}
	}

	// HWID check
	hwidHash := crypto.HMACHash(s.pepper, []byte(normalizeHWID(rawHWID)))
	if device.HWIDHash == nil || *device.HWIDHash != hwidHash {
		return nil, ErrHWIDMismatch
	}

	// Minecraft username check (only after fully bound)
	if device.BindingStatus == "fully_bound" {
		if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
			return nil, ErrUsernameMismatch
		}
	}

	challenge := make([]byte, 32)
	rand.Read(challenge)
	challengeB64 := base64.StdEncoding.EncodeToString(challenge)

	if err := cache.StoreChallenge(ctx, s.rdb, &cache.Challenge{
		DeviceID:  device.ID,
		Challenge: challengeB64,
		SourceIP:  sourceIP,
	}); err != nil {
		return nil, err
	}

	return &StartResult{Challenge: challengeB64, ExpiresIn: 30}, nil
}

func (s *Service) Finish(ctx context.Context, username, proofHex, sourceIP, minecraftUsername string) (*FinishResult, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if err != nil {
		return nil, ErrNotFound
	}

	ch, err := cache.GetChallenge(ctx, s.rdb, device.ID)
	if err != nil || ch.Used {
		return nil, ErrNoChallenge
	}
	if ch.SourceIP != sourceIP {
		return nil, ErrIPMismatch
	}

	// Delete challenge immediately — single use, enforced before proof verification result is returned
	cache.DeleteChallenge(ctx, s.rdb, device.ID)

	plain, err := crypto.DecryptAESGCM(s.masterKey, device.DeviceSecretEncrypted)
	if err != nil {
		return nil, err
	}

	if !crypto.HMACVerify(plain, []byte(ch.Challenge), proofHex) {
		count, _ := db.IncrementFailedAttempts(ctx, s.pool, device.ID)
		if count >= 5 {
			db.SuspendDevice(ctx, s.pool, device.ID)
		}
		return nil, ErrBadProof
	}

	// First auth — fully bind
	if device.BindingStatus == "hwid_pending" {
		if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsername, sourceIP); err != nil {
			return nil, err
		}
	}

	// Entitlement check
	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		return nil, err
	}
	if !ent.Authorized {
		return &FinishResult{Authenticated: true, Authorized: false, Reason: ent.Reason}, nil
	}

	// Create 1-hour session
	expiresAt := time.Now().Add(time.Hour)
	rawToken, tokenHash, err := crypto.GenerateToken()
	if err != nil {
		return nil, err
	}
	if _, err := db.CreateSession(ctx, s.pool, tokenHash, device.ID, account.ID,
		ent.PlanTier, ent.EnabledModules, ent.EnabledFeatures,
		ent.EntitlementExpiresAt, expiresAt, sourceIP); err != nil {
		return nil, err
	}

	// Get latest manifest for this channel
	manifestURL := ""
	manifest, err := db.GetLatestManifest(ctx, s.pool, ent.ContentChannel)
	if err == nil {
		manifestURL = s.baseURL + "/content/manifest/" + manifest.ID
	}

	return &FinishResult{
		Authenticated: true,
		Authorized:    true,
		SessionToken:  rawToken,
		ExpiresIn:     3600,
		PlanTier:      ent.PlanTier,
		Modules:       ent.EnabledModules,
		Features:      ent.EnabledFeatures,
		ManifestURL:   manifestURL,
	}, nil
}

func normalize(s string) string     { return strings.ToLower(strings.TrimSpace(s)) }
func normalizeHWID(s string) string { return strings.ToUpper(strings.TrimSpace(s)) }
