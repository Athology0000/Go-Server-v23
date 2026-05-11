package enrollment

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/audit"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

var (
	ErrKeyNotFound     = errors.New("key not found")
	ErrKeyNotAvailable = errors.New("key already used or revoked")
	ErrIPMismatch      = errors.New("ip mismatch")
	ErrBadCredentials  = errors.New("bad credentials")
	ErrNoLicense       = errors.New("no license on account")
	ErrAlreadyEnrolled = errors.New("already enrolled")
)

type Service struct {
	pool      *pgxpool.Pool
	auditSvc  *audit.Service
	masterKey []byte
	pepper    []byte
}

func New(pool *pgxpool.Pool, auditSvc *audit.Service, masterKey, pepper []byte) *Service {
	return &Service{pool: pool, auditSvc: auditSvc, masterKey: masterKey, pepper: pepper}
}

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

func (s *Service) Redeem(ctx context.Context, rawKey, accountID, sourceIP string) error {
	keyHash := crypto.HashLicenseKey(rawKey)
	key, err := db.GetLicenseKeyByHash(ctx, s.pool, keyHash)
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_found"})
		return ErrKeyNotFound
	}
	if err != nil {
		return err
	}
	if key.Status != "available" {
		s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_available", "status": key.Status})
		return ErrKeyNotAvailable
	}

	// Ensure the account has an enrollment device secret.
	// If a device already exists, keep it (single-device model).
	if _, devErr := db.GetDeviceByAccountID(ctx, s.pool, accountID); devErr != nil {
		if !errors.Is(devErr, pgx.ErrNoRows) {
			return devErr
		}
		secret := make([]byte, 32)
		if _, err := rand.Read(secret); err != nil {
			return err
		}
		encrypted, err := crypto.EncryptAESGCM(s.masterKey, secret)
		if err != nil {
			return err
		}
		if _, err := db.CreateDevice(ctx, s.pool, accountID, sourceIP, encrypted); err != nil {
			return err
		}
	}

	// Create or extend the license on the account based on the key.
	license, licErr := db.GetLicenseByAccountID(ctx, s.pool, accountID)
	if licErr != nil && !errors.Is(licErr, pgx.ErrNoRows) {
		return licErr
	}
	var existingExpires *time.Time
	hasExisting := licErr == nil
	if hasExisting {
		existingExpires = license.ExpiresAt
	}
	newExpires := computeNewExpiry(hasExisting, existingExpires, key.DurationDays)

	if errors.Is(licErr, pgx.ErrNoRows) {
		if _, err := db.CreateLicense(ctx, s.pool, accountID, key.PlanTier, time.Now(), newExpires); err != nil {
			return err
		}
	} else {
		// NOTE: mirrors panel redeem behavior; updates plan + expiry and re-activates.
		_, err := s.pool.Exec(ctx,
			`UPDATE licenses
			 SET plan_tier = $1, status = 'active', expires_at = $2, updated_at = now()
			 WHERE account_id = $3`,
			key.PlanTier, newExpires, accountID,
		)
		if err != nil {
			return err
		}
	}

	if err := db.RedeemLicenseKey(ctx, s.pool, keyHash, accountID, sourceIP); err != nil {
		return err
	}
	s.auditSvc.Log("enroll.redeem.success", &accountID, nil, nil, &sourceIP, map[string]any{"plan_tier": key.PlanTier})
	return nil
}

// RedeemWithHWID performs "bootstrapper mode 2":
// - Redeem the license key for the given account
// - Ensure a device secret exists
// - Bind the HWID immediately (binding_status -> hwid_pending)
// - Create/extend the account license (so auth works right away)
// - Return the username + decrypted device_secret (base64)
func (s *Service) RedeemWithHWID(ctx context.Context, rawKey, accountID, rawHWID, sourceIP string) (string, string, string, *time.Time, error) {
	keyHash := crypto.HashLicenseKey(rawKey)

	tx, err := s.pool.Begin(ctx)
	if err != nil {
		return "", "", "", nil, err
	}
	defer tx.Rollback(ctx)

	// Account must exist and be active.
	var username string
	var accountStatus string
	if err := tx.QueryRow(ctx, `SELECT username, status FROM accounts WHERE id = $1`, accountID).
		Scan(&username, &accountStatus); err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found"})
			return "", "", "", nil, ErrBadCredentials
		}
		return "", "", "", nil, err
	}
	if accountStatus != "active" {
		s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return "", "", "", nil, ErrBadCredentials
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
			s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_found"})
			return "", "", "", nil, ErrKeyNotFound
		}
		return "", "", "", nil, err
	}
	if status != "available" {
		s.auditSvc.Log("enroll.redeem.fail", &accountID, nil, nil, &sourceIP, map[string]any{"reason": "key_not_available", "status": status})
		return "", "", "", nil, ErrKeyNotAvailable
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
			return "", "", "", nil, deviceErr
		}

		secretPlain = make([]byte, 32)
		if _, err := rand.Read(secretPlain); err != nil {
			return "", "", "", nil, err
		}
		enc, err := crypto.EncryptAESGCM(s.masterKey, secretPlain)
		if err != nil {
			return "", "", "", nil, err
		}
		if err := tx.QueryRow(ctx,
			`INSERT INTO devices (account_id, enrollment_ip, device_secret_encrypted)
			 VALUES ($1, $2, $3)
			 RETURNING id, binding_status, device_secret_encrypted`,
			accountID, sourceIP, enc,
		).Scan(&deviceID, &bindingStatus, &secretEncrypted); err != nil {
			return "", "", "", nil, err
		}
	} else {
		if bindingStatus != "unbound" {
			s.auditSvc.Log("enroll.redeem.fail", &accountID, &deviceID, nil, &sourceIP, map[string]any{"reason": "already_enrolled", "status": bindingStatus})
			return "", "", "", nil, ErrAlreadyEnrolled
		}
		// Align enrollment_ip to the current request so auth/start won't IP-mismatch.
		if _, err := tx.Exec(ctx, `UPDATE devices SET enrollment_ip = $1, updated_at = now() WHERE id = $2`, sourceIP, deviceID); err != nil {
			return "", "", "", nil, err
		}
		plain, err := crypto.DecryptAESGCM(s.masterKey, secretEncrypted)
		if err != nil {
			return "", "", "", nil, err
		}
		secretPlain = plain
	}

	// Bind HWID (device becomes eligible for auth/start).
	hwidHash := crypto.HMACHash(s.pepper, []byte(normalizeHWID(rawHWID)))
	if _, err := tx.Exec(ctx,
		`UPDATE devices SET hwid_hash = $1, binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $2`,
		hwidHash, deviceID); err != nil {
		return "", "", "", nil, err
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
		return "", "", "", nil, licenseErr
	}

	newExpires := computeNewExpiry(!errors.Is(licenseErr, pgx.ErrNoRows), existingExpires, durationDays)
	if errors.Is(licenseErr, pgx.ErrNoRows) {
		if _, err := tx.Exec(ctx,
			`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
			 VALUES ($1, $2, $3, $4)`,
			accountID, planTier, now, newExpires,
		); err != nil {
			return "", "", "", nil, err
		}
	} else {
		if _, err := tx.Exec(ctx,
			`UPDATE licenses
			 SET plan_tier = $1, status = 'active', expires_at = $2, updated_at = now()
			 WHERE account_id = $3`,
			planTier, newExpires, accountID,
		); err != nil {
			return "", "", "", nil, err
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
		return "", "", "", nil, err
	}
	if tag.RowsAffected() != 1 {
		return "", "", "", nil, ErrKeyNotAvailable
	}

	if err := tx.Commit(ctx); err != nil {
		return "", "", "", nil, err
	}

	s.auditSvc.Log("enroll.redeem.success", &accountID, &deviceID, nil, &sourceIP, map[string]any{"plan_tier": planTier, "duration_days": durationDays, "expires_at": newExpires})
	return username, base64.StdEncoding.EncodeToString(secretPlain), planTier, newExpires, nil
}

func (s *Service) Handshake(ctx context.Context, username, password, rawHWID, sourceIP string) (string, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if err != nil {
		s.auditSvc.Log("enroll.handshake.fail", nil, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found", "username": username})
		return "", ErrBadCredentials
	}
	if account.Status != "active" {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return "", ErrBadCredentials
	}
	ok, err := crypto.VerifyPassword(password, account.PasswordHash)
	if err != nil || !ok {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "bad_credentials"})
		return "", ErrBadCredentials
	}
	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if err != nil {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "device_not_found"})
		return "", ErrBadCredentials
	}
	if device.BindingStatus != "unbound" {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "device_already_bound", "status": device.BindingStatus})
		return "", ErrBadCredentials
	}
	if device.EnrollmentIP == nil || *device.EnrollmentIP != sourceIP {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "ip_mismatch"})
		return "", ErrIPMismatch
	}
	hwidHash := crypto.HMACHash(s.pepper, []byte(normalizeHWID(rawHWID)))
	if err := db.BindHWID(ctx, s.pool, device.ID, hwidHash); err != nil {
		return "", err
	}
	plain, err := crypto.DecryptAESGCM(s.masterKey, device.DeviceSecretEncrypted)
	if err != nil {
		return "", err
	}
	s.auditSvc.Log("enroll.handshake.success", &account.ID, &device.ID, nil, &sourceIP, nil)
	return base64.StdEncoding.EncodeToString(plain), nil
}

func normalize(s string) string     { return strings.ToLower(strings.TrimSpace(s)) }
func normalizeHWID(s string) string { return strings.ToUpper(strings.TrimSpace(s)) }
