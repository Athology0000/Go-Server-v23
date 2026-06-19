package enrollment

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
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
//
// Redemption is bound to a credential proof: the account to redeem onto is derived from a
// verified Username/Password, never trusted from the caller. AccountID is intentionally NOT a
// field — accepting a caller-supplied account id is the account-takeover vulnerability this
// seam was hardened against (issue #1). The panel's own redeem path derives the account from an
// authenticated panel session for the same reason.
type RedeemRequest struct {
	RawKey   string
	Username string
	Password string
	SourceIP string
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
	// deviceSecret is the DeviceSecret module: it owns the master key for sealing/opening a
	// device secret, so the transaction asks to seal/open instead of calling AES-GCM raw.
	deviceSecret *crypto.DeviceSecret
}

// NewRedemption wires the module to the live pool and crypto material.
func NewRedemption(pool *pgxpool.Pool, auditSvc *audit.Service, masterKey []byte) *Redemption {
	return &Redemption{
		pool:         pool,
		auditSvc:     auditSvc,
		masterKey:    masterKey,
		deviceSecret: crypto.NewDeviceSecret(masterKey),
	}
}

// redeemTx is the minimal subset of pgx.Tx the redemption core touches: a row query and a
// command exec. Naming it as an interface is what makes RedeemOnTx unit-testable — a fake that
// scripts QueryRow/Exec satisfies it, so the device/key/license rules can be asserted with no
// Postgres. *pgxpool.Tx (and pgx.Tx) satisfy it directly, so the live callers pass their real
// tx unchanged.
type redeemTx interface {
	QueryRow(ctx context.Context, sql string, args ...any) pgx.Row
	Exec(ctx context.Context, sql string, args ...any) (pgconn.CommandTag, error)
}

// CoreOptions captures the two enrollment-only behavioural knobs the panel path does not want.
// Both default to false, which is exactly the panel's behaviour: it does not advance the device
// to hwid_pending and never opens/returns the device secret. Enrollment sets both true.
type CoreOptions struct {
	// AdvanceToHWIDPending moves the device to binding_status='hwid_pending' (and re-aligns an
	// existing device's enrollment_ip to the request IP) so the loader can pin HWID later.
	AdvanceToHWIDPending bool
	// OpenDeviceSecret decrypts the device secret and returns it (base64) in the result.
	OpenDeviceSecret bool
}

// CoreResult is the typed outcome of RedeemOnTx, carrying everything either adapter needs to
// build its own response/audit. DeviceSecret is only populated when CoreOptions.OpenDeviceSecret
// is set.
type CoreResult struct {
	DeviceID     string
	PlanTier     string
	DurationDays int
	ExpiresAt    *time.Time
	DeviceSecret string
}

// RedeemOnTx is the deep, shared redemption core. It runs on an ALREADY-OPEN tx with an
// ALREADY-RESOLVED accountID — the two adapter-specific concerns (how the account is proven, who
// owns the tx) stay with the caller, while the redemption rules themselves live here once:
//
//   - lock + validate the key (FOR UPDATE; ErrKeyNotFound / ErrKeyNotAvailable),
//   - ensure the device exists and is unbound (ErrAlreadyEnrolled otherwise) — the same
//     unbound-guard the panel path enforces inline,
//   - create + seal a fresh device secret when there is none,
//   - optionally advance the device to hwid_pending and open its secret (enrollment-only knobs),
//   - create or extend the account license via the pure computeNewExpiry rule,
//   - mark the key redeemed under a double-spend guard (rowsAffected != 1 → ErrKeyNotAvailable).
//
// It does NOT begin/commit the tx, resolve the account, or emit audit events — those belong to
// the caller. The DeviceSecret crypto module seals/opens secrets so callers don't touch AES-GCM.
func RedeemOnTx(ctx context.Context, tx redeemTx, ds *crypto.DeviceSecret, accountID, rawKey, sourceIP string, opts CoreOptions) (CoreResult, error) {
	keyHash := crypto.HashLicenseKey(rawKey)

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
			return CoreResult{}, ErrKeyNotFound
		}
		return CoreResult{}, err
	}
	if status != "available" {
		return CoreResult{}, ErrKeyNotAvailable
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
			return CoreResult{}, deviceErr
		}

		secretPlain = make([]byte, 32)
		if _, err := rand.Read(secretPlain); err != nil {
			return CoreResult{}, err
		}
		enc, err := ds.Seal(secretPlain)
		if err != nil {
			return CoreResult{}, err
		}
		if err := tx.QueryRow(ctx,
			`INSERT INTO devices (account_id, enrollment_ip, device_secret_encrypted)
			 VALUES ($1, $2, $3)
			 RETURNING id, binding_status, device_secret_encrypted`,
			accountID, sourceIP, enc,
		).Scan(&deviceID, &bindingStatus, &secretEncrypted); err != nil {
			return CoreResult{}, err
		}
	} else {
		if bindingStatus != "unbound" {
			return CoreResult{}, ErrAlreadyEnrolled
		}
		if opts.AdvanceToHWIDPending {
			// Align enrollment_ip to the current request so auth/start won't IP-mismatch.
			if _, err := tx.Exec(ctx, `UPDATE devices SET enrollment_ip = $1, updated_at = now() WHERE id = $2`, sourceIP, deviceID); err != nil {
				return CoreResult{}, err
			}
		}
		if opts.OpenDeviceSecret {
			plain, err := ds.Open(secretEncrypted)
			if err != nil {
				return CoreResult{}, err
			}
			secretPlain = plain
		}
	}

	// Mark device enrolled (eligible for auth/start). HWID is pinned later by the loader at /auth/verify-session.
	if opts.AdvanceToHWIDPending {
		if _, err := tx.Exec(ctx,
			`UPDATE devices SET binding_status = 'hwid_pending', updated_at = now()
			 WHERE id = $1`,
			deviceID); err != nil {
			return CoreResult{}, err
		}
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
		return CoreResult{}, licenseErr
	}

	newExpires := computeNewExpiry(!errors.Is(licenseErr, pgx.ErrNoRows), existingExpires, durationDays)
	if errors.Is(licenseErr, pgx.ErrNoRows) {
		if _, err := tx.Exec(ctx,
			`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
			 VALUES ($1, $2, $3, $4)`,
			accountID, planTier, now, newExpires,
		); err != nil {
			return CoreResult{}, err
		}
	} else {
		if _, err := tx.Exec(ctx,
			`UPDATE licenses
			 SET plan_tier = $1, status = 'active', expires_at = $2, updated_at = now()
			 WHERE account_id = $3`,
			planTier, newExpires, accountID,
		); err != nil {
			return CoreResult{}, err
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
		return CoreResult{}, err
	}
	if tag.RowsAffected() != 1 {
		return CoreResult{}, ErrKeyNotAvailable
	}

	res := CoreResult{
		DeviceID:     deviceID,
		PlanTier:     planTier,
		DurationDays: durationDays,
		ExpiresAt:    newExpires,
	}
	if opts.OpenDeviceSecret {
		res.DeviceSecret = base64.StdEncoding.EncodeToString(secretPlain)
	}
	return res, nil
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
	sourceIP := req.SourceIP

	tx, err := r.pool.Begin(ctx)
	if err != nil {
		return RedeemResult{}, err
	}
	defer tx.Rollback(ctx)

	// Resolve the account from a proven credential, not a caller-supplied id. The account the
	// key redeems onto (and whose device_secret the response returns) is whoever the verified
	// username/password identifies — mirroring enrollment Handshake and the panel redeem path.
	// A wrong username, wrong password, or blocked account all collapse to ErrBadCredentials so
	// the endpoint is not a username-enumeration / account-existence oracle.
	var accountID string
	var username string
	var passwordHash string
	var accountStatus string
	if err := tx.QueryRow(ctx,
		`SELECT id, username, password_hash, status FROM accounts WHERE username = $1`,
		normalize(req.Username),
	).Scan(&accountID, &username, &passwordHash, &accountStatus); err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			// Spend the same argon2 work as a real verify so a missing account isn't
			// distinguishable by response time (user-enumeration oracle); same for blocked below.
			crypto.DummyVerifyPassword()
			r.auditSvc.Log(audit.EventEnrollRedeemFail, nil, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found", "username": req.Username})
			return RedeemResult{}, ErrBadCredentials
		}
		return RedeemResult{}, err
	}
	if accountStatus != "active" {
		crypto.DummyVerifyPassword()
		r.auditSvc.Log(audit.EventEnrollRedeemFail, &accountID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return RedeemResult{}, ErrBadCredentials
	}
	if ok, err := crypto.VerifyPassword(req.Password, passwordHash); err != nil || !ok {
		r.auditSvc.Log(audit.EventEnrollRedeemFail, &accountID, nil, nil, &sourceIP, map[string]any{"reason": "bad_credentials"})
		return RedeemResult{}, ErrBadCredentials
	}

	// Run the shared redemption core on this tx, against the credential-resolved account. The two
	// enrollment-only knobs (advance to hwid_pending, open + return the device secret) are set
	// here; the panel adapter leaves both off. The core returns the package's typed errors, which
	// we map to fine-grained audit reasons below.
	res, err := RedeemOnTx(ctx, tx, r.deviceSecret, accountID, req.RawKey, sourceIP, CoreOptions{
		AdvanceToHWIDPending: true,
		OpenDeviceSecret:     true,
	})
	if err != nil {
		switch {
		case errors.Is(err, ErrKeyNotFound):
			r.auditSvc.Log(audit.EventEnrollRedeemFail, &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_found"})
		case errors.Is(err, ErrKeyNotAvailable):
			r.auditSvc.Log(audit.EventEnrollRedeemFail, &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_available"})
		case errors.Is(err, ErrAlreadyEnrolled):
			r.auditSvc.Log(audit.EventEnrollRedeemFail, &accountID, nil, nil, &sourceIP, map[string]any{"reason": "already_enrolled"})
		}
		return RedeemResult{}, err
	}

	if err := tx.Commit(ctx); err != nil {
		return RedeemResult{}, err
	}

	deviceID := res.DeviceID
	r.auditSvc.Log(audit.EventEnrollRedeemSuccess, &accountID, &deviceID, nil, &sourceIP, map[string]any{"plan_tier": res.PlanTier, "duration_days": res.DurationDays, "expires_at": res.ExpiresAt})
	return RedeemResult{
		Username:     username,
		DeviceSecret: res.DeviceSecret,
		PlanTier:     res.PlanTier,
		ExpiresAt:    res.ExpiresAt,
	}, nil
}
