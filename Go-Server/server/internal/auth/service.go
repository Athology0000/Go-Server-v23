package auth

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"regexp"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/cache"
	"github.com/phantom/server/internal/config"
	"github.com/phantom/server/internal/content"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
)

// offlineUsernamePattern matches the placeholder Minecraft usernames Fabric and
// PrismLauncher generate for offline-mode launches (Player followed by digits).
// These rotate per launch, so a device bound to one such name will perma-mismatch
// on every subsequent launch. When either the bound name or the incoming name
// looks like an offline placeholder we silently rebind instead of returning 403.
var offlineUsernamePattern = regexp.MustCompile(`^Player\d+$`)

func isOfflinePlaceholderName(name string) bool {
	return offlineUsernamePattern.MatchString(strings.TrimSpace(name))
}

var (
	ErrNotFound            = errors.New("not found")
	ErrIPMismatch          = errors.New("ip mismatch")
	ErrHWIDMismatch        = errors.New("hwid mismatch")
	ErrUsernameMismatch    = errors.New("username mismatch")
	ErrBadProof            = errors.New("bad proof")
	ErrDeviceBlocked       = errors.New("device blocked")
	ErrNoChallenge         = errors.New("no challenge")
	ErrSessionInvalid      = errors.New("session invalid")
	ErrManifestUnavailable = errors.New("manifest unavailable")
)

type StartResult struct {
	Challenge string
	ExpiresIn int
}

type FinishResult struct {
	Authenticated        bool
	Authorized           bool
	Reason               string
	AccountID            string
	Username             string
	SessionToken         string
	ExpiresIn            int
	PlanTier             string
	Modules              []string
	Features             []string
	ManifestURL          string
	ManifestSignature    string
	EntitlementExpiresAt *time.Time
}

type Service struct {
	pool      *pgxpool.Pool
	entSvc    *entitlement.Service
	auditSvc  *audit.Service
	masterKey []byte
	pepper    []byte
	baseURL   string
	cfg       *config.Config
}

func New(
	pool *pgxpool.Pool,
	entSvc *entitlement.Service,
	auditSvc *audit.Service,
	masterKey []byte,
	pepper []byte,
	baseURL string,
	cfg *config.Config,
) *Service {
	return &Service{
		pool:      pool,
		entSvc:    entSvc,
		auditSvc:  auditSvc,
		masterKey: masterKey,
		pepper:    pepper,
		baseURL:   strings.TrimRight(baseURL, "/"),
		cfg:       cfg,
	}
}

func (s *Service) Start(ctx context.Context, username, minecraftUsername, sourceIP string) (*StartResult, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("auth.start.fail", nil, nil, nil, &sourceIP, map[string]any{
			"reason":   "account_not_found",
			"username": username,
		})
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	if account.Status != "active" {
		s.auditSvc.Log("auth.start.fail", &account.ID, nil, nil, &sourceIP, map[string]any{
			"reason": "account_blocked",
		})
		return nil, ErrDeviceBlocked
	}

	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("auth.start.fail", &account.ID, nil, nil, &sourceIP, map[string]any{
			"reason": "device_not_found",
		})
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	if device.BindingStatus == "suspended" || device.BindingStatus == "banned" {
		s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_blocked",
			"status": device.BindingStatus,
		})
		return nil, ErrDeviceBlocked
	}

	if device.BindingStatus != "hwid_pending" && device.BindingStatus != "fully_bound" {
		s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_not_enrolled",
		})
		return nil, ErrNotFound
	}

	if device.BindingStatus == "fully_bound" && minecraftUsername != "" {
		if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
			s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"reason": "username_mismatch",
			})
			return nil, ErrUsernameMismatch
		}
	}

	challenge := make([]byte, 32)
	if _, err := rand.Read(challenge); err != nil {
		return nil, err
	}

	challengeB64 := base64.StdEncoding.EncodeToString(challenge)

	if err := cache.StoreChallenge(ctx, s.pool, &cache.Challenge{
		DeviceID:  device.ID,
		Challenge: challengeB64,
		SourceIP:  sourceIP,
	}); err != nil {
		return nil, err
	}

	s.auditSvc.Log("auth.start.success", &account.ID, &device.ID, nil, &sourceIP, nil)

	return &StartResult{
		Challenge: challengeB64,
		ExpiresIn: 30,
	}, nil
}

func (s *Service) Finish(ctx context.Context, username, proofHex, sourceIP, minecraftUsername string) (*FinishResult, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("auth.finish.fail", nil, nil, nil, &sourceIP, map[string]any{
			"reason":   "account_not_found",
			"username": username,
		})
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if err != nil {
		s.auditSvc.Log("auth.finish.fail", &account.ID, nil, nil, &sourceIP, map[string]any{
			"reason": "device_not_found",
		})
		return nil, ErrNotFound
	}

	ch, err := cache.ConsumeChallenge(ctx, s.pool, device.ID)
	if err != nil {
		s.auditSvc.Log("auth.finish.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "no_challenge_or_expired",
		})
		return nil, ErrNoChallenge
	}

	if ch.SourceIP != sourceIP {
		s.auditSvc.Log("auth.finish.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason":   "ip_mismatch",
			"expected": ch.SourceIP,
		})
		return nil, ErrIPMismatch
	}

	plain, err := crypto.DecryptAESGCM(s.masterKey, device.DeviceSecretEncrypted)
	if err != nil {
		return nil, err
	}

	if !crypto.HMACVerify(plain, []byte(ch.Challenge), proofHex) {
		count, _ := db.IncrementFailedAttempts(ctx, s.pool, device.ID)
		if count >= 5 {
			db.SuspendDevice(ctx, s.pool, device.ID)
			s.auditSvc.Log("auth.device.suspended", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"reason": "too_many_failed_attempts",
			})
		}

		s.auditSvc.Log("auth.finish.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason":          "bad_proof",
			"failed_attempts": count,
		})

		return nil, ErrBadProof
	}

	if device.BindingStatus == "hwid_pending" {
		if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsername, sourceIP); err != nil {
			return nil, err
		}
	}

	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		return nil, err
	}

	if !ent.Authorized {
		s.auditSvc.Log("auth.finish.entitlement_denied", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": ent.Reason,
		})

		return &FinishResult{
			Authenticated: true,
			Authorized:    false,
			AccountID:     account.ID,
			Username:      account.Username,
			Reason:        ent.Reason,
		}, nil
	}

	expiresAt := time.Now().Add(time.Duration(s.cfg.SessionTTLHours) * time.Hour)

	rawToken, tokenHash, err := crypto.GenerateToken()
	if err != nil {
		return nil, err
	}

	planTier := strings.TrimSpace(ent.PlanTier)
	if planTier == "" {
		planTier = "unknown"
	}

	modules := ent.EnabledModules
	if modules == nil {
		modules = []string{}
	}

	features := ent.EnabledFeatures
	if features == nil {
		features = []string{}
	}

	if _, err := db.CreateSession(
		ctx,
		s.pool,
		tokenHash,
		device.ID,
		account.ID,
		planTier,
		modules,
		features,
		ent.EntitlementExpiresAt,
		expiresAt,
		sourceIP,
	); err != nil {
		return nil, err
	}

	manifestURL, manifestSig, err := s.resolveManifest(ctx, ent.LicenseID, ent.ContentChannel, ent.EnabledModules)
	if err != nil {
		log.Printf("[auth.finish] no manifest found for channel %s: %v", ent.ContentChannel, err)
		return nil, fmt.Errorf("%w: %v", ErrManifestUnavailable, err)
	}

	s.auditSvc.Log("auth.finish.success", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
		"plan_tier": planTier,
	})

	log.Printf(
		"[auth.finish] result username=%s account_id=%s authorized=true resolved_plan=%q expires_at=%v modules=%v features=%v manifest_url=%q",
		account.Username,
		account.ID,
		planTier,
		ent.EntitlementExpiresAt,
		modules,
		features,
		manifestURL,
	)

	return &FinishResult{
		Authenticated:        true,
		Authorized:           true,
		AccountID:            account.ID,
		Username:             account.Username,
		SessionToken:         rawToken,
		ExpiresIn:            3600,
		PlanTier:             planTier,
		Modules:              modules,
		Features:             features,
		ManifestURL:          manifestURL,
		ManifestSignature:    manifestSig,
		EntitlementExpiresAt: ent.EntitlementExpiresAt,
	}, nil
}

type HeartbeatResult struct {
	PlanTier string
	Modules  []string
	Features []string
}

func (s *Service) Heartbeat(ctx context.Context, sessionToken, sourceIP string, activity json.RawMessage) (*HeartbeatResult, error) {
	tokenHash, err := crypto.HashToken(sessionToken)
	if err != nil {
		return nil, ErrSessionInvalid
	}

	session, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return nil, ErrSessionInvalid
		}
		return nil, err
	}

	if !db.SessionLive(session, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		return nil, ErrSessionInvalid
	}

	// Re-resolve entitlements every beat: a revoked/expired license takes
	// effect mid-session instead of surviving until restart.
	ent, err := s.entSvc.Resolve(ctx, session.AccountID)
	if err != nil {
		return nil, err
	}

	if !ent.Authorized {
		if revokeErr := db.RevokeSession(ctx, s.pool, session.ID); revokeErr != nil {
			log.Printf("[auth.heartbeat] revoke failed session_id=%s err=%v", session.ID, revokeErr)
		}
		s.auditSvc.Log("auth.heartbeat.entitlement_revoked", &session.AccountID, &session.DeviceID, nil, &sourceIP, map[string]any{
			"session_id": session.ID,
			"reason":     ent.Reason,
		})
		return nil, ErrSessionInvalid
	}

	planTier := strings.TrimSpace(ent.PlanTier)
	if planTier == "" {
		planTier = "unknown"
	}

	// Refresh liveness (last_heartbeat_at) and the stored entitlement view, but do
	// NOT extend expires_at — the session stays open only while heartbeats arrive.
	if err := db.UpdateSessionHeartbeat(ctx, s.pool, session.ID, planTier, ent.EnabledModules, ent.EnabledFeatures); err != nil {
		return nil, err
	}

	// Persist what the loader reported for the stats page. Default to an empty
	// JSON array when the client sent nothing parseable.
	if len(activity) == 0 {
		activity = json.RawMessage("[]")
	}
	if err := db.InsertSessionActivity(ctx, s.pool, session.ID, session.AccountID, session.DeviceID, activity); err != nil {
		log.Printf("[auth.heartbeat] activity insert failed session_id=%s err=%v", session.ID, err)
	}

	s.auditSvc.Log("auth.heartbeat", &session.AccountID, &session.DeviceID, nil, &sourceIP, map[string]any{
		"session_id": session.ID,
	})

	return &HeartbeatResult{
		PlanTier: planTier,
		Modules:  ent.EnabledModules,
		Features: ent.EnabledFeatures,
	}, nil
}

type VerifyMinecraftResult struct {
	Authorized           bool
	Reason               string
	PlanTier             string
	Modules              []string
	Features             []string
	ManifestURL          string
	ManifestSignature    string
	EntitlementExpiresAt *time.Time
}

type VerifySessionResult struct {
	Authorized           bool
	Reason               string
	AccountID            string
	Username             string
	MinecraftUsername    string
	PlanTier             string
	Modules              []string
	Features             []string
	ManifestURL          string
	ManifestSignature    string
	EntitlementExpiresAt *time.Time
}

func (s *Service) VerifyMinecraft(ctx context.Context, rawToken, minecraftUsername, sourceIP string) (*VerifyMinecraftResult, error) {
	tokenHash, err := crypto.HashToken(rawToken)
	if err != nil {
		return nil, ErrSessionInvalid
	}

	sess, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil || !db.SessionLive(sess, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		return nil, ErrSessionInvalid
	}

	device, err := db.GetDeviceByID(ctx, s.pool, sess.DeviceID)
	if err != nil {
		return nil, err
	}

	account, err := db.GetAccountByID(ctx, s.pool, sess.AccountID)
	if err != nil {
		return nil, err
	}

	if account.Status != "active" {
		s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "account_blocked",
		})
		return &VerifyMinecraftResult{
			Authorized: false,
			Reason:     "account_blocked",
		}, nil
	}

	switch device.BindingStatus {
	case "hwid_pending":
		if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsername, sourceIP); err != nil {
			return nil, err
		}
	case "fully_bound":
		if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
			s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"reason": "minecraft_username_mismatch",
			})
			return &VerifyMinecraftResult{
				Authorized: false,
				Reason:     "minecraft_username_mismatch",
			}, nil
		}
	default:
		s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_not_eligible",
			"status": device.BindingStatus,
		})
		return &VerifyMinecraftResult{
			Authorized: false,
			Reason:     "device_not_eligible",
		}, nil
	}

	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		return nil, err
	}

	if !ent.Authorized {
		s.auditSvc.Log("auth.verify_mc.entitlement_denied", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": ent.Reason,
		})
		return &VerifyMinecraftResult{
			Authorized: false,
			Reason:     ent.Reason,
		}, nil
	}

	manifestURL, manifestSig, _ := s.resolveManifest(ctx, ent.LicenseID, ent.ContentChannel, ent.EnabledModules)

	s.auditSvc.Log("auth.verify_mc.success", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
		"plan_tier": ent.PlanTier,
	})

	return &VerifyMinecraftResult{
		Authorized:           true,
		PlanTier:             ent.PlanTier,
		Modules:              ent.EnabledModules,
		Features:             ent.EnabledFeatures,
		ManifestURL:          manifestURL,
		ManifestSignature:    manifestSig,
		EntitlementExpiresAt: ent.EntitlementExpiresAt,
	}, nil
}

func (s *Service) VerifySession(ctx context.Context, rawToken, sourceIP, minecraftUsernameInput, hwidHash string) (*VerifySessionResult, error) {
	started := time.Now()
	minecraftUsernameInput = strings.TrimSpace(minecraftUsernameInput)

	log.Printf("[auth.verify_session] start ip=%s token_present=%t token=%s minecraft_input=%q",
		sourceIP,
		strings.TrimSpace(rawToken) != "",
		shortToken(rawToken),
		minecraftUsernameInput,
	)

	tokenHash, err := crypto.HashToken(rawToken)
	if err != nil {
		log.Printf("[auth.verify_session] token_hash_failed ip=%s err=%v", sourceIP, err)
		return nil, ErrSessionInvalid
	}

	log.Printf("[auth.verify_session] token_hash_ok ip=%s token_hash=%s",
		sourceIP,
		shortHash(tokenHash),
	)

	sess, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil {
		log.Printf("[auth.verify_session] session_lookup_failed ip=%s token_hash=%s err=%v",
			sourceIP,
			shortHash(tokenHash),
			err,
		)
		return nil, ErrSessionInvalid
	}

	log.Printf("[auth.verify_session] session_found ip=%s session_id=%s account_id=%s device_id=%s revoked=%t expires_at=%s now=%s",
		sourceIP,
		sess.ID,
		sess.AccountID,
		sess.DeviceID,
		sess.Revoked,
		sess.ExpiresAt.Format(time.RFC3339),
		time.Now().Format(time.RFC3339),
	)

	if sess.Revoked {
		log.Printf("[auth.verify_session] session_revoked ip=%s session_id=%s", sourceIP, sess.ID)
		return nil, ErrSessionInvalid
	}

	if time.Now().After(sess.ExpiresAt) {
		log.Printf("[auth.verify_session] session_expired ip=%s session_id=%s expires_at=%s",
			sourceIP,
			sess.ID,
			sess.ExpiresAt.Format(time.RFC3339),
		)
		return nil, ErrSessionInvalid
	}

	if !db.SessionLive(sess, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		log.Printf("[auth.verify_session] session_stale ip=%s session_id=%s last_heartbeat=%s",
			sourceIP, sess.ID, sess.LastHeartbeatAt.Format(time.RFC3339))
		return nil, ErrSessionInvalid
	}

	device, err := db.GetDeviceByID(ctx, s.pool, sess.DeviceID)
	if err != nil {
		log.Printf("[auth.verify_session] device_lookup_failed ip=%s device_id=%s err=%v",
			sourceIP,
			sess.DeviceID,
			err,
		)
		return nil, err
	}

	log.Printf("[auth.verify_session] device_found ip=%s device_id=%s binding_status=%s minecraft_username=%q",
		sourceIP,
		device.ID,
		device.BindingStatus,
		ptrStringValue(device.MinecraftUsername),
	)

	if minecraftUsernameInput != "" {
		switch device.BindingStatus {
		case "hwid_pending":
			if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsernameInput, sourceIP); err != nil {
				log.Printf("[auth.verify_session] minecraft_bind_failed ip=%s device_id=%s err=%v",
					sourceIP,
					device.ID,
					err,
				)
				return nil, err
			}

			device.MinecraftUsername = &minecraftUsernameInput
			device.BindingStatus = "fully_bound"

			log.Printf("[auth.verify_session] minecraft_bound ip=%s device_id=%s minecraft_username=%q",
				sourceIP,
				device.ID,
				minecraftUsernameInput,
			)

		case "fully_bound":
			if device.MinecraftUsername == nil || strings.TrimSpace(*device.MinecraftUsername) == "" {
				if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsernameInput, sourceIP); err != nil {
					log.Printf("[auth.verify_session] minecraft_rebind_empty_failed ip=%s device_id=%s err=%v",
						sourceIP,
						device.ID,
						err,
					)
					return nil, err
				}

				device.MinecraftUsername = &minecraftUsernameInput

				log.Printf("[auth.verify_session] minecraft_bound_empty ip=%s device_id=%s minecraft_username=%q",
					sourceIP,
					device.ID,
					minecraftUsernameInput,
				)
			} else if !strings.EqualFold(*device.MinecraftUsername, minecraftUsernameInput) {
				if isOfflinePlaceholderName(minecraftUsernameInput) || isOfflinePlaceholderName(*device.MinecraftUsername) {
					log.Printf(
						"[auth.verify_session] minecraft_offline_rebind ip=%s device_id=%s old=%q new=%q",
						sourceIP,
						device.ID,
						*device.MinecraftUsername,
						minecraftUsernameInput,
					)

					if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsernameInput, sourceIP); err != nil {
						log.Printf("[auth.verify_session] minecraft_offline_rebind_failed ip=%s device_id=%s err=%v",
							sourceIP,
							device.ID,
							err,
						)
						return nil, err
					}

					device.MinecraftUsername = &minecraftUsernameInput
				} else {
					log.Printf(
						"[auth.verify_session] minecraft_username_mismatch ip=%s device_id=%s expected=%q got=%q",
						sourceIP,
						device.ID,
						*device.MinecraftUsername,
						minecraftUsernameInput,
					)

					s.auditSvc.Log("auth.verify_session.fail", &sess.AccountID, &device.ID, nil, &sourceIP, map[string]any{
						"reason":   "minecraft_username_mismatch",
						"expected": *device.MinecraftUsername,
						"got":      minecraftUsernameInput,
					})

					return &VerifySessionResult{
						Authorized: false,
						Reason:     "minecraft_username_mismatch",
					}, nil
				}
			}
		default:
			log.Printf("[auth.verify_session] minecraft_bind_skipped ip=%s device_id=%s status=%s",
				sourceIP,
				device.ID,
				device.BindingStatus,
			)
		}
	}

	account, err := db.GetAccountByID(ctx, s.pool, sess.AccountID)
	if err != nil {
		log.Printf("[auth.verify_session] account_lookup_failed ip=%s account_id=%s err=%v",
			sourceIP,
			sess.AccountID,
			err,
		)
		return nil, err
	}

	log.Printf("[auth.verify_session] account_found ip=%s account_id=%s username=%s status=%s",
		sourceIP,
		account.ID,
		account.Username,
		account.Status,
	)

	if account.Status != "active" {
		log.Printf("[auth.verify_session] account_blocked ip=%s account_id=%s status=%s",
			sourceIP,
			account.ID,
			account.Status,
		)

		s.auditSvc.Log("auth.verify_session.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "account_blocked",
			"status": account.Status,
		})

		return &VerifySessionResult{
			Authorized: false,
			Reason:     "account_blocked",
		}, nil
	}

	if device.BindingStatus == "suspended" || device.BindingStatus == "banned" {
		log.Printf("[auth.verify_session] device_blocked ip=%s account_id=%s device_id=%s status=%s",
			sourceIP,
			account.ID,
			device.ID,
			device.BindingStatus,
		)

		s.auditSvc.Log("auth.verify_session.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_blocked",
			"status": device.BindingStatus,
		})

		return &VerifySessionResult{
			Authorized: false,
			Reason:     "device_blocked",
		}, nil
	}

	// HWID trust-on-first-use (default OFF; gated by HWID_TOFU_ENABLED). Pin the device's HWID the
	// first time one is seen, then require it to match on subsequent sessions. When disabled, the
	// hwid_hash the loader sends is ignored, preserving the current no-HWID behaviour.
	if s.cfg.HwidTofuEnabled && strings.TrimSpace(hwidHash) != "" {
		if device.HWIDHash == nil || strings.TrimSpace(*device.HWIDHash) == "" {
			if err := db.PinHWID(ctx, s.pool, device.ID, hwidHash); err != nil {
				return nil, err
			}
			device.HWIDHash = &hwidHash
			s.auditSvc.Log("auth.verify_session.hwid_pinned", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"hwid_prefix": shortHash(hwidHash),
			})
		} else if !strings.EqualFold(strings.TrimSpace(*device.HWIDHash), strings.TrimSpace(hwidHash)) {
			// Sanitized prefixes (not the full hashes) so operators can distinguish a genuine
			// hardware change from a spoofing attempt. Recovery = admin device reset + re-enroll.
			log.Printf("[auth.verify_session] hwid_mismatch ip=%s device_id=%s stored=%s got=%s",
				sourceIP, device.ID, shortHash(strings.TrimSpace(*device.HWIDHash)), shortHash(strings.TrimSpace(hwidHash)))
			s.auditSvc.Log("auth.verify_session.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"reason":        "hwid_mismatch",
				"stored_prefix": shortHash(strings.TrimSpace(*device.HWIDHash)),
				"got_prefix":    shortHash(strings.TrimSpace(hwidHash)),
			})
			return &VerifySessionResult{
				Authorized: false,
				Reason:     "hwid_mismatch",
			}, nil
		}
	}

	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		log.Printf("[auth.verify_session] entitlement_lookup_failed ip=%s account_id=%s err=%v",
			sourceIP,
			account.ID,
			err,
		)
		return nil, err
	}

	log.Printf("[auth.verify_session] entitlement_result ip=%s account_id=%s authorized=%t reason=%q plan=%s modules=%v features=%v channel=%s expires_at=%v",
		sourceIP,
		account.ID,
		ent.Authorized,
		ent.Reason,
		ent.PlanTier,
		ent.EnabledModules,
		ent.EnabledFeatures,
		ent.ContentChannel,
		ent.EntitlementExpiresAt,
	)

	if !ent.Authorized {
		log.Printf("[auth.verify_session] entitlement_denied ip=%s account_id=%s reason=%s",
			sourceIP,
			account.ID,
			ent.Reason,
		)

		s.auditSvc.Log("auth.verify_session.entitlement_denied", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": ent.Reason,
		})

		return &VerifySessionResult{
			Authorized: false,
			Reason:     ent.Reason,
		}, nil
	}

	manifestURL, manifestSig, manifestErr := s.resolveManifest(ctx, ent.LicenseID, ent.ContentChannel, ent.EnabledModules)
	if manifestErr == nil {

		log.Printf("[auth.verify_session] manifest_found ip=%s account_id=%s channel=%s manifest_id=%s manifest_url=%s signature_present=%t",
			sourceIP,
			account.ID,
			ent.ContentChannel,
			manifestIDFromURL(manifestURL),
			manifestURL,
			manifestSig != "",
		)
	} else {
		log.Printf("[auth.verify_session] manifest_missing_or_disabled ip=%s account_id=%s channel=%s err=%v",
			sourceIP,
			account.ID,
			ent.ContentChannel,
			manifestErr,
		)
	}

	minecraftUsername := ""
	if device.MinecraftUsername != nil {
		minecraftUsername = strings.TrimSpace(*device.MinecraftUsername)
	}

	modules := ent.EnabledModules
	if modules == nil {
		modules = []string{}
	}

	features := ent.EnabledFeatures
	if features == nil {
		features = []string{}
	}

	// Refresh last_seen_ip so subsequent SessionAuth-protected content fetches
	// from the same bootstrap (manifest, module, native) pass strictIP. Without
	// this, clients behind carrier-grade NAT or rotating egress IPs (where the
	// content fetch IP differs from the session-creation IP) get permanently
	// 401'd on every content endpoint despite holding a valid session.
	if err := db.UpdateSessionLastSeenIP(ctx, s.pool, sess.ID, sourceIP); err != nil {
		log.Printf("[auth.verify_session] last_seen_ip_update_failed ip=%s session_id=%s err=%v",
			sourceIP,
			sess.ID,
			err,
		)
	}

	s.auditSvc.Log("auth.verify_session.success", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
		"plan_tier":          ent.PlanTier,
		"minecraft_username": minecraftUsername,
	})

	log.Printf("[auth.verify_session] success ip=%s account_id=%s username=%s minecraft_bound=%t minecraft_username=%q modules=%v manifest_present=%t duration_ms=%d",
		sourceIP,
		account.ID,
		account.Username,
		minecraftUsername != "",
		minecraftUsername,
		modules,
		manifestURL != "",
		time.Since(started).Milliseconds(),
	)

	return &VerifySessionResult{
		Authorized:           true,
		AccountID:            account.ID,
		Username:             account.Username,
		MinecraftUsername:    minecraftUsername,
		PlanTier:             ent.PlanTier,
		Modules:              modules,
		Features:             features,
		ManifestURL:          manifestURL,
		ManifestSignature:    manifestSig,
		EntitlementExpiresAt: ent.EntitlementExpiresAt,
	}, nil
}

func normalize(s string) string {
	return strings.ToLower(strings.TrimSpace(s))
}

func (s *Service) resolveManifest(ctx context.Context, licenseID, channel string, enabledModules []string) (string, string, error) {
	dbManifest, dbErr := db.GetLatestManifest(ctx, s.pool, channel)

	// An operator-pinned DB manifest wins ONLY when it actually delivers modules. An empty manifest
	// (historically inserted just to clear a 503) must not shadow the per-account filesystem build,
	// or unlock succeeds but no modules ever stream. It is still honored as a last-resort safety net
	// below, so a content-build failure can never re-introduce a fleet lockout.
	if dbErr == nil && len(dbManifest.Modules) > 0 {
		return s.baseURL + "/content/manifest/" + dbManifest.ID, dbManifest.Signature, nil
	}

	stableURL, stableSig, stableErr := s.resolveStableManifest(ctx, licenseID, channel, enabledModules)
	if stableErr == nil {
		return stableURL, stableSig, nil
	}

	// Fail-open, not fail-closed: if the filesystem build fails (e.g. content not yet deployed) but
	// any DB manifest exists (even an empty one), serve it so authentication still completes and
	// unlock stays alive, rather than 503-locking the whole fleet.
	if dbErr == nil {
		return s.baseURL + "/content/manifest/" + dbManifest.ID, dbManifest.Signature, nil
	}
	return "", "", fmt.Errorf("manifest lookup failed: %w; stable manifest build failed: %v", dbErr, stableErr)
}

// resolveStableManifest materializes this license's bundles (no-op once cached) and builds the
// per-account filesystem manifest. Split out so resolveManifest can treat any failure here uniformly
// and fall back to a DB manifest.
func (s *Service) resolveStableManifest(ctx context.Context, licenseID, channel string, enabledModules []string) (string, string, error) {
	if genErr := content.EnsureLicenseBundles(s.cfg.ContentDir, licenseID, s.cfg.ModuleEncryptionKey, s.cfg.ModuleWmSecret, s.cfg.ModuleWmPepper); genErr != nil {
		return "", "", fmt.Errorf("generate license bundles: %w", genErr)
	}
	stableManifest, err := content.BuildStableManifest(
		ctx,
		s.cfg.ContentDir,
		licenseID,
		s.baseURL,
		channel,
		s.cfg.ManifestSigningKey,
		s.cfg.ModuleEncryptionKey,
		enabledModules,
	)
	if err != nil {
		return "", "", err
	}
	return s.baseURL + "/content/manifest/stable", stableManifest.Signature, nil
}

func manifestIDFromURL(value string) string {
	value = strings.TrimRight(value, "/")
	if idx := strings.LastIndex(value, "/"); idx >= 0 {
		return value[idx+1:]
	}
	return value
}

func shortToken(raw string) string {
	raw = strings.TrimSpace(raw)
	if len(raw) <= 12 {
		return raw
	}
	return raw[:6] + "..." + raw[len(raw)-6:]
}

func shortHash(raw string) string {
	raw = strings.TrimSpace(raw)
	if len(raw) <= 16 {
		return raw
	}
	return raw[:8] + "..." + raw[len(raw)-8:]
}

func ptrStringValue(value *string) string {
	if value == nil {
		return ""
	}
	return *value
}
