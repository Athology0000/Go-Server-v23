package enrollment

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"strings"

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
	if err := db.RedeemLicenseKey(ctx, s.pool, keyHash, accountID, sourceIP); err != nil {
		return err
	}
	s.auditSvc.Log("enroll.redeem.success", &accountID, nil, nil, &sourceIP, map[string]any{"plan_tier": key.PlanTier})
	return nil
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
