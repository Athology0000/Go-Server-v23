package enrollment

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"strings"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
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
	// deviceSecret is the DeviceSecret module: it owns the master key for sealing/opening a
	// device secret, so Handshake asks to seal/open instead of calling AES-GCM with the key.
	deviceSecret *crypto.DeviceSecret
}

func New(pool *pgxpool.Pool, auditSvc *audit.Service, masterKey, pepper []byte) *Service {
	return &Service{
		pool:         pool,
		auditSvc:     auditSvc,
		masterKey:    masterKey,
		pepper:       pepper,
		deviceSecret: crypto.NewDeviceSecret(masterKey),
	}
}

// Redeem now lives in the Redemption module (redemption.go): the multi-step redemption
// transaction is deep enough to own its own module behind the Redeemer seam, instead of
// hanging off Service where nothing could vary across the call. Service retains Handshake,
// the username/password self-enrollment path.

func (s *Service) Handshake(ctx context.Context, username, password, sourceIP string) (string, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if err != nil {
		// Match the argon2 cost of a real verify so a missing account isn't distinguishable by
		// response time (user-enumeration oracle); same for the blocked branch below.
		crypto.DummyVerifyPassword()
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, nil, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found", "username": username})
		return "", ErrBadCredentials
	}
	if account.Status != "active" {
		crypto.DummyVerifyPassword()
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return "", ErrBadCredentials
	}
	ok, err := crypto.VerifyPassword(password, account.PasswordHash)
	if err != nil || !ok {
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "bad_credentials"})
		return "", ErrBadCredentials
	}
	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		// First enrollment for this account: no device row yet. Auto-provision an unbound
		// device bound to the current IP, then enroll it below. A valid account self-enrolls
		// its machine — the same device state the license-key redeem path produces, but keyed
		// on the verified username/password instead of a key. HWID pinning at
		// /auth/verify-session and the one-device-per-account model still constrain it.
		secretPlain := make([]byte, 32)
		if _, randErr := rand.Read(secretPlain); randErr != nil {
			return "", randErr
		}
		enc, encErr := s.deviceSecret.Seal(secretPlain)
		if encErr != nil {
			return "", encErr
		}
		device, err = db.CreateDevice(ctx, s.pool, account.ID, sourceIP, enc)
		if err != nil {
			return "", err
		}
		s.auditSvc.Log(audit.EventEnrollHandshakeDeviceCreated, &account.ID, &device.ID, nil, &sourceIP, nil)
	} else if err != nil {
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "device_lookup_error"})
		return "", err
	}
	if device.BindingStatus != "unbound" {
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "device_already_bound", "status": device.BindingStatus})
		return "", ErrBadCredentials
	}
	if device.EnrollmentIP == nil || *device.EnrollmentIP != sourceIP {
		s.auditSvc.Log(audit.EventEnrollHandshakeFail, &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "ip_mismatch"})
		return "", ErrIPMismatch
	}
	if err := db.MarkEnrolled(ctx, s.pool, device.ID); err != nil {
		return "", err
	}
	plain, err := s.deviceSecret.Open(device.DeviceSecretEncrypted)
	if err != nil {
		return "", err
	}
	s.auditSvc.Log(audit.EventEnrollHandshakeSuccess, &account.ID, &device.ID, nil, &sourceIP, nil)
	return base64.StdEncoding.EncodeToString(plain), nil
}

func normalize(s string) string { return strings.ToLower(strings.TrimSpace(s)) }
