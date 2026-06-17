package enrollment

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/crypto"
)

// redemption.go holds the Redemption module: the one owner of the multi-step license
// redemption transaction.
//
// Before this module the redemption sequence — account lookup, key FOR UPDATE lock,
// device ensure/create, secret encrypt/decrypt, hwid_pending advance, license
// create/extend (the lifetime rule), and the double-spend-guarded key mark — lived as a
// 160-line method hanging off the enrollment Service, reachable only through a live
// *pgxpool.Pool. That made it a hypothetical seam: the handler called it but nothing
// could vary across the call, so the redemption rules had no test and the handler had to
// know the (username, secret, planTier, expiresAt, err) return tuple by position.
//
// This module pulls the whole transaction behind one small interface — Redeem(ctx, req)
// (Result, error) — and a typed request/result. The handler now depends on the Redeemer
// seam, which a test fake satisfies (the second adapter that makes the seam real), so the
// handler's accept/reject/persist-shape contract is asserted without a database, and the
// redemption's pure lifetime rule (computeNewExpiry) is asserted directly. The atomic tx
// stays inside the module: it owns the redemption, not storage primitives.

// RedeemRequest is the typed input to a redemption. Naming the fields here is what lets the
// handler stop threading positional arguments into the transaction.
type RedeemRequest struct {
	RawKey    string
	AccountID string
	SourceIP  string
}

// RedeemResult is the typed outcome of a successful redemption — exactly the values the
// handler marshals back to the loader. DeviceSecret is base64 of the decrypted secret.
type RedeemResult struct {
	Username     string
	DeviceSecret string
	PlanTier     string
	ExpiresAt    *time.Time
}

// Redeemer is the seam the handler depends on. *Redemption is the live adapter; a fake
// stands in for it in handler tests. One small surface in front of the whole transaction.
type Redeemer interface {
	Redeem(ctx context.Context, req RedeemRequest) (RedeemResult, error)
}

// Redemption owns the redemption transaction. It is deliberately deep: a one-method surface
// hiding the account/key/device/license sequence, with the atomic tx held inside.
type Redemption struct {
	pool      *pgxpool.Pool
	auditSvc  *audit.Service
	masterKey []byte
}

// NewRedemption wires the module to the live pool and crypto material.
func NewRedemption(pool *pgxpool.Pool, auditSvc *audit.Service, masterKey []byte) *Redemption {
	return &Redemption{pool: pool, auditSvc: auditSvc, masterKey: masterKey}
}

// computeNewExpiry is the redemption's lifetime rule, kept pure so it can be asserted
// directly: lifetime stays lifetime, duration_days<=0 means lifetime, otherwise extend from
// max(now, existing).
func computeNewExpiry(hasExisting bool, existing *time.Time, durationDays int) *time.Time {
	now := time.Now()

	// Existing lifetime stays lifetime.
	if hasExisting && existing == nil {
		return nil
	}

	// duration_days <= 0 means "never expires" (lifetime).
	if durationDays <= 0 {
		return nil
	}

	base := now
	if existing != nil && existing.After(now) {
		base = *existing
	}
	t := base.AddDate(0, 0, durationDays)
	return &t
}

// Redeem performs full enrollment in one atomic transaction:
// - Redeem the license key for the given account
// - Ensure a device secret exists
// - Move device to hwid_pending (HWID is pinned later by the loader at /auth/verify-session)
// - Create/extend the account license (so auth works right away)
// - Return the username + decrypted device_secret (base64) + plan_tier + expires_at
func (r *Redemption) Redeem(ctx context.Context, req RedeemRequest) (RedeemResult, error) {
	accountID := req.AccountID
	sourceIP := req.SourceIP
	keyHash := crypto.HashLicenseKey(req.RawKey)

	tx, err := r.pool.Begin(ctx)
	if err != nil {
		return RedeemResult{}, err
	}
	defer tx.Rollback(ctx)

	// Account must exist and be active.
	var username string
	var accountStatus string
	if err := tx.QueryRow(ctx, `SELECT username, status FROM accounts WHERE id = $1`, accountID).
		Scan(&username, &accountStatus); err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			r.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found"})
			return RedeemResult{}, ErrBadCredentials
		}
		return RedeemResult{}, err
	}
	if accountStatus != "active" {
		r.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return RedeemResult{}, ErrBadCredentials
	}

	// Lock the key row so concurrent redeems can't double-spend it.
	var planTier string
	var durationDays int
	var status string
	if err := tx.QueryRow(ctx,
		`SELECT plan_tier, duration_days, status
		 FROM license_keys
		 WHERE key_hash = $1
		 FOR UPDATE`,
		keyHash,
	).Scan(&planTier, &durationDays, &status); err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			r.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_found"})
			return RedeemResult{}, ErrKeyNotFound
		}
		return RedeemResult{}, err
	}
	if status != "available" {
		r.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_available", "status": status})
		return RedeemResult{}, ErrKeyNotAvailable
	}

	// Ensure device exists and is unbound.
	var deviceID string
	var bindingStatus string
	var secretEncrypted []byte
	deviceErr := tx.QueryRow(ctx,
		`SELECT id, binding_status, device_secret_encrypted
		 FROM devices
		 WHERE account_id = $1
		 LIMIT 1`,
		accountID,
	).Scan(&deviceID, &bindingStatus, &secretEncrypted)

	var secretPlain []byte
	if deviceErr != nil {
		if !errors.Is(deviceErr, pgx.ErrNoRows) {
			return RedeemResult{}, deviceErr
		}

		secretPlain = make([]byte, 32)
		if _, err := rand.Read(secretPlain); err != nil {
			return RedeemResult{}, err
		}
		enc, err := crypto.EncryptAESGCM(r.masterKey, secretPlain)
		if err != nil {
			return RedeemResult{}, err
		}
		if err := tx.QueryRow(ctx,
			`INSERT INTO devices (account_id, enrollment_ip, device_secret_encrypted)
			 VALUES ($1, $2, $3)
			 RETURNING id, binding_status, device_secret_encrypted`,
			accountID, sourceIP, enc,
		).Scan(&deviceID, &bindingStatus, &secretEncrypted); err != nil {
			return RedeemResult{}, err
		}
	} else {
		if bindingStatus != "unbound" {
			r.auditSvc.Log("enroll.redeem.fail", &accountID, &deviceID, nil, &sourceIP, map[string]any{"reason": "already_enrolled", "status": bindingStatus})
			return RedeemResult{}, ErrAlreadyEnrolled
		}
		// Align enrollment_ip to the current request so auth/start won't IP-mismatch.
		if _, err := tx.Exec(ctx, `UPDATE devices SET enrollment_ip = $1, updated_at = now() WHERE id = $2`, sourceIP, deviceID); err != nil {
			return RedeemResult{}, err
		}
		plain, err := crypto.DecryptAESGCM(r.masterKey, secretEncrypted)
		if err != nil {
			return RedeemResult{}, err
		}
		secretPlain = plain
	}

	// Mark device enrolled (eligible for auth/start). HWID is pinned later by the loader at /auth/verify-session.
	if _, err := tx.Exec(ctx,
		`UPDATE devices SET binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $1`,
		deviceID); err != nil {
		return RedeemResult{}, err
	}

	// Create or extend the license on the account.
	now := time.Now()
	var existingExpires *time.Time
	licenseErr := tx.QueryRow(ctx,
		`SELECT expires_at
		 FROM licenses
		 WHERE account_id = $1
		 FOR UPDATE`,
		accountID,
	).Scan(&existingExpires)
	if licenseErr != nil && !errors.Is(licenseErr, pgx.ErrNoRows) {
		return RedeemResult{}, licenseErr
	}

	newExpires := computeNewExpiry(!errors.Is(licenseErr, pgx.ErrNoRows), existingExpires, durationDays)
	if errors.Is(licenseErr, pgx.ErrNoRows) {
		if _, err := tx.Exec(ctx,
			`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
			 VALUES ($1, $2, $3, $4)`,
			accountID, planTier, now, newExpires,
		); err != nil {
			return RedeemResult{}, err
		}
	} else {
		if _, err := tx.Exec(ctx,
			`UPDATE licenses
			 SET plan_tier = $1, status = 'active', expires_at = $2, updated_at = now()
			 WHERE account_id = $3`,
			planTier, newExpires, accountID,
		); err != nil {
			return RedeemResult{}, err
		}
	}

	// Mark the key as redeemed.
	tag, err := tx.Exec(ctx,
		`UPDATE license_keys
		 SET status = 'redeemed', redeemed_by = $1, redeemed_at = now(), enrollment_ip = $2
		 WHERE key_hash = $3 AND status = 'available'`,
		accountID, sourceIP, keyHash,
	)
	if err != nil {
		return RedeemResult{}, err
	}
	if tag.RowsAffected() != 1 {
		return RedeemResult{}, ErrKeyNotAvailable
	}

	if err := tx.Commit(ctx); err != nil {
		return RedeemResult{}, err
	}

	r.auditSvc.Log("enroll.redeem.success", &accountID, &deviceID, nil, &sourceIP, map[string]any{"plan_tier": planTier, "duration_days": durationDays, "expires_at": newExpires})
	return RedeemResult{
		Username:     username,
		DeviceSecret: base64.StdEncoding.EncodeToString(secretPlain),
		PlanTier:     planTier,
		ExpiresAt:    newExpires,
	}, nil
}
