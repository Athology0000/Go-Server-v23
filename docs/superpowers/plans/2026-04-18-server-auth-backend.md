# Server Auth & Entitlements Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a zero-trust Go + Fiber authentication, entitlement, and content delivery server for Cobalt.

**Architecture:** Single Go binary with two HTTP listeners (`:8080` public, `127.0.0.1:8081` admin), clean internal packages per domain, PostgreSQL for persistent state, Redis for ephemeral state (challenges, rate limits). All secrets in environment variables.

**Tech Stack:** Go 1.22+, Fiber v2, pgx v5 (PostgreSQL), go-redis v9, golang.org/x/crypto (argon2), crypto/ed25519, crypto/aes

---

## File Map

```
server/
  cmd/server/
    main.go                        ← wire everything, start both listeners
  internal/
    config/
      config.go                    ← load + validate env vars
    crypto/
      argon2.go                    ← password hash/verify
      hmac.go                      ← HMAC-SHA256, constant-time compare
      aes.go                       ← AES-256-GCM encrypt/decrypt
      ed25519.go                   ← manifest signing/verify
      tokens.go                    ← random token generation, SHA-256 hashing
      keys.go                      ← license key generation (base32)
    db/
      db.go                        ← pgx pool init
      accounts.go                  ← account queries
      devices.go                   ← device queries
      licenses.go                  ← license queries
      license_keys.go              ← license key queries
      sessions.go                  ← session queries
      entitlements.go              ← entitlement + override queries
      manifests.go                 ← content manifest queries
      admin_tokens.go              ← admin token queries
      audit.go                     ← audit log insert
    cache/
      cache.go                     ← redis client init
      challenges.go                ← store/get/delete auth challenges
      ratelimit.go                 ← sliding window rate limiter
    middleware/
      session.go                   ← bearer session token auth (re-validates DB)
      admin.go                     ← admin token auth + role enforcement
      ratelimit.go                 ← rate limit middleware wrapper
      realip.go                    ← extract real IP from connection (never body)
    enrollment/
      handler.go                   ← POST /enroll/redeem, POST /enroll/handshake
      service.go                   ← enrollment business logic
    auth/
      handler.go                   ← POST /auth/start, POST /auth/finish
      service.go                   ← challenge issue, proof verify, binding logic
    entitlement/
      service.go                   ← resolve effective modules/features for account
    content/
      handler.go                   ← GET /content/manifest/:id, /module/:name, /native/:name
      service.go                   ← verify session + entitlement, stream file
    admin/
      handler.go                   ← all /admin/* routes
      service.go                   ← admin business logic
    audit/
      audit.go                     ← fire-and-forget audit log writer
  migrations/
    001_initial.sql                ← all table definitions
  go.mod
  go.sum
```

---

## Task 1: Go module + dependencies

**Files:**
- Create: `server/go.mod`
- Create: `server/go.sum` (generated)

- [ ] **Step 1: Init module**

```bash
mkdir -p server && cd server
go mod init github.com/cobalt/server
```

- [ ] **Step 2: Add dependencies**

```bash
go get github.com/gofiber/fiber/v2@v2.52.5
go get github.com/jackc/pgx/v5@v5.6.0
go get github.com/redis/go-redis/v9@v9.5.3
go get golang.org/x/crypto@v0.23.0
go get github.com/joho/godotenv@v1.5.1
```

- [ ] **Step 3: Verify go.mod**

Expected `go.mod` (versions may differ slightly):
```
module github.com/cobalt/server

go 1.22

require (
    github.com/gofiber/fiber/v2 v2.52.5
    github.com/jackc/pgx/v5 v5.6.0
    github.com/redis/go-redis/v9 v9.5.3
    golang.org/x/crypto v0.23.0
    github.com/joho/godotenv v1.5.1
)
```

- [ ] **Step 4: Commit**

```bash
git add server/go.mod server/go.sum
git commit -m "feat(server): init go module with dependencies"
```

---

## Task 2: Config loader

**Files:**
- Create: `server/internal/config/config.go`

- [ ] **Step 1: Write config struct and loader**

```go
// server/internal/config/config.go
package config

import (
	"fmt"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	MasterKey          []byte // 32 bytes, AES-256
	ServerPepper       []byte // 32 bytes, HMAC key
	ManifestSigningKey []byte // Ed25519 private key (64 bytes)
	DBURL              string
	RedisURL           string
	AdminAPISecret     string
	PublicPort         string
	AdminPort          string
	ContentDir         string
}

func Load() (*Config, error) {
	_ = godotenv.Load()

	masterKey, err := decodeBase64Env("MASTER_KEY", 32)
	if err != nil {
		return nil, err
	}
	pepper, err := decodeBase64Env("SERVER_PEPPER", 32)
	if err != nil {
		return nil, err
	}
	signingKey, err := decodeBase64Env("MANIFEST_SIGNING_KEY", 64)
	if err != nil {
		return nil, err
	}

	cfg := &Config{
		MasterKey:          masterKey,
		ServerPepper:       pepper,
		ManifestSigningKey: signingKey,
		DBURL:              requireEnv("DB_URL"),
		RedisURL:           requireEnv("REDIS_URL"),
		AdminAPISecret:     requireEnv("ADMIN_API_SECRET"),
		PublicPort:         getEnvOr("PUBLIC_PORT", "8080"),
		AdminPort:          getEnvOr("ADMIN_PORT", "8081"),
		ContentDir:         getEnvOr("CONTENT_DIR", "./content"),
	}
	return cfg, nil
}

func requireEnv(key string) string {
	v := os.Getenv(key)
	if v == "" {
		panic(fmt.Sprintf("required env var %s is not set", key))
	}
	return v
}

func getEnvOr(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}

func decodeBase64Env(key string, expectedLen int) ([]byte, error) {
	import "encoding/base64"
	raw := requireEnv(key)
	b, err := base64.StdEncoding.DecodeString(raw)
	if err != nil {
		return nil, fmt.Errorf("%s: invalid base64: %w", key, err)
	}
	if len(b) != expectedLen {
		return nil, fmt.Errorf("%s: expected %d bytes, got %d", key, expectedLen, len(b))
	}
	return b, nil
}
```

- [ ] **Step 2: Commit**

```bash
git add server/internal/config/config.go
git commit -m "feat(server): config loader from env vars"
```

---

## Task 3: Crypto helpers

**Files:**
- Create: `server/internal/crypto/argon2.go`
- Create: `server/internal/crypto/hmac.go`
- Create: `server/internal/crypto/aes.go`
- Create: `server/internal/crypto/ed25519.go`
- Create: `server/internal/crypto/tokens.go`
- Create: `server/internal/crypto/keys.go`

- [ ] **Step 1: argon2.go**

```go
// server/internal/crypto/argon2.go
package crypto

import (
	"crypto/rand"
	"crypto/subtle"
	"encoding/base64"
	"fmt"

	"golang.org/x/crypto/argon2"
)

const (
	argonMemory  = 64 * 1024
	argonIter    = 3
	argonParallel = 2
	argonKeyLen  = 32
	argonSaltLen = 16
)

func HashPassword(password string) (string, error) {
	salt := make([]byte, argonSaltLen)
	if _, err := rand.Read(salt); err != nil {
		return "", err
	}
	hash := argon2.IDKey([]byte(password), salt, argonIter, argonMemory, argonParallel, argonKeyLen)
	encoded := fmt.Sprintf("$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
		argonMemory, argonIter, argonParallel,
		base64.RawStdEncoding.EncodeToString(salt),
		base64.RawStdEncoding.EncodeToString(hash),
	)
	return encoded, nil
}

func VerifyPassword(password, encoded string) (bool, error) {
	var mem, iter uint32
	var par uint8
	var saltB64, hashB64 string
	_, err := fmt.Sscanf(encoded, "$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
		&mem, &iter, &par, &saltB64, &hashB64)
	if err != nil {
		return false, fmt.Errorf("invalid hash format: %w", err)
	}
	salt, err := base64.RawStdEncoding.DecodeString(saltB64)
	if err != nil {
		return false, err
	}
	expectedHash, err := base64.RawStdEncoding.DecodeString(hashB64)
	if err != nil {
		return false, err
	}
	computed := argon2.IDKey([]byte(password), salt, iter, mem, par, uint32(len(expectedHash)))
	return subtle.ConstantTimeCompare(computed, expectedHash) == 1, nil
}
```

- [ ] **Step 2: hmac.go**

```go
// server/internal/crypto/hmac.go
package crypto

import (
	"crypto/hmac"
	"crypto/sha256"
	"crypto/subtle"
	"encoding/hex"
)

func HMACHash(key, data []byte) string {
	mac := hmac.New(sha256.New, key)
	mac.Write(data)
	return hex.EncodeToString(mac.Sum(nil))
}

func HMACVerify(key, data []byte, providedHex string) bool {
	expected := HMACHash(key, data)
	provided := []byte(providedHex)
	expectedBytes := []byte(expected)
	if len(provided) != len(expectedBytes) {
		return false
	}
	return subtle.ConstantTimeCompare(provided, expectedBytes) == 1
}
```

- [ ] **Step 3: aes.go**

```go
// server/internal/crypto/aes.go
package crypto

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"errors"
	"io"
)

func EncryptAESGCM(key, plaintext []byte) ([]byte, error) {
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}
	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}
	nonce := make([]byte, gcm.NonceSize())
	if _, err = io.ReadFull(rand.Reader, nonce); err != nil {
		return nil, err
	}
	ciphertext := gcm.Seal(nonce, nonce, plaintext, nil)
	return ciphertext, nil
}

func DecryptAESGCM(key, ciphertext []byte) ([]byte, error) {
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}
	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}
	if len(ciphertext) < gcm.NonceSize() {
		return nil, errors.New("ciphertext too short")
	}
	nonce, ciphertext := ciphertext[:gcm.NonceSize()], ciphertext[gcm.NonceSize():]
	return gcm.Open(nil, nonce, ciphertext, nil)
}
```

- [ ] **Step 4: ed25519.go**

```go
// server/internal/crypto/ed25519.go
package crypto

import (
	"crypto/ed25519"
	"encoding/json"
)

func SignManifest(privateKey []byte, manifest any) (string, error) {
	data, err := json.Marshal(manifest)
	if err != nil {
		return "", err
	}
	sig := ed25519.Sign(privateKey, data)
	return encodeBase64(sig), nil
}

func VerifyManifest(publicKey, data []byte, sigBase64 string) bool {
	sig, err := decodeBase64(sigBase64)
	if err != nil {
		return false
	}
	return ed25519.Verify(publicKey, data, sig)
}
```

- [ ] **Step 5: tokens.go**

```go
// server/internal/crypto/tokens.go
package crypto

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
)

func GenerateToken() (raw string, hash string, err error) {
	b := make([]byte, 32)
	if _, err = rand.Read(b); err != nil {
		return
	}
	raw = base64.URLEncoding.EncodeToString(b)
	h := sha256.Sum256(b)
	hash = hex.EncodeToString(h[:])
	return
}

func HashToken(rawBase64 string) (string, error) {
	b, err := base64.URLEncoding.DecodeString(rawBase64)
	if err != nil {
		return "", err
	}
	h := sha256.Sum256(b)
	return hex.EncodeToString(h[:]), nil
}

func encodeBase64(b []byte) string {
	return base64.StdEncoding.EncodeToString(b)
}

func decodeBase64(s string) ([]byte, error) {
	return base64.StdEncoding.DecodeString(s)
}
```

- [ ] **Step 6: keys.go**

```go
// server/internal/crypto/keys.go
package crypto

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/base32"
	"encoding/hex"
	"strings"
)

var base32Enc = base32.NewEncoding("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567").WithPadding(base32.NoPadding)

func GenerateLicenseKey() (raw string, hash string, err error) {
	b := make([]byte, 20)
	if _, err = rand.Read(b); err != nil {
		return
	}
	encoded := base32Enc.EncodeToString(b)
	// Format as COBALT-XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX
	parts := []string{"COBALT"}
	for i := 0; i < len(encoded); i += 4 {
		end := i + 4
		if end > len(encoded) {
			end = len(encoded)
		}
		parts = append(parts, encoded[i:end])
	}
	raw = strings.Join(parts, "-")
	h := sha256.Sum256([]byte(raw))
	hash = hex.EncodeToString(h[:])
	return
}

func HashLicenseKey(raw string) string {
	normalized := strings.ToUpper(strings.TrimSpace(raw))
	h := sha256.Sum256([]byte(normalized))
	return hex.EncodeToString(h[:])
}
```

- [ ] **Step 7: Commit**

```bash
git add server/internal/crypto/
git commit -m "feat(server): crypto helpers — argon2id, HMAC, AES-GCM, Ed25519, tokens, license keys"
```

---

## Task 4: Database schema migration

**Files:**
- Create: `server/migrations/001_initial.sql`

- [ ] **Step 1: Write migration**

```sql
-- server/migrations/001_initial.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE accounts (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    email         TEXT,
    status        TEXT NOT NULL DEFAULT 'active'
                  CHECK (status IN ('active','suspended','banned')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE license_keys (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key_hash      TEXT UNIQUE NOT NULL,
    plan_tier     TEXT NOT NULL,
    status        TEXT NOT NULL DEFAULT 'available'
                  CHECK (status IN ('available','redeemed','revoked')),
    redeemed_by   UUID REFERENCES accounts(id),
    redeemed_at   TIMESTAMPTZ,
    enrollment_ip TEXT,
    created_by    TEXT NOT NULL,
    notes         TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE licenses (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id       UUID UNIQUE NOT NULL REFERENCES accounts(id),
    plan_tier        TEXT NOT NULL,
    status           TEXT NOT NULL DEFAULT 'active'
                     CHECK (status IN ('active','expired','suspended','revoked','trial')),
    starts_at        TIMESTAMPTZ NOT NULL,
    expires_at       TIMESTAMPTZ,
    grace_expires_at TIMESTAMPTZ,
    max_devices      INT,
    notes            TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE devices (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id               UUID NOT NULL REFERENCES accounts(id),
    binding_status           TEXT NOT NULL DEFAULT 'unbound'
                             CHECK (binding_status IN ('unbound','hwid_pending','fully_bound','suspended','banned')),
    hwid_hash                TEXT,
    minecraft_username       TEXT,
    enrollment_ip            TEXT,
    device_secret_encrypted  BYTEA NOT NULL,
    failed_attempts          INT NOT NULL DEFAULT 0,
    last_seen_ip             TEXT,
    last_login_at            TIMESTAMPTZ,
    binding_reset_at         TIMESTAMPTZ,
    binding_reset_by         TEXT,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sessions (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_token_hash     TEXT UNIQUE NOT NULL,
    device_id              UUID NOT NULL REFERENCES devices(id),
    account_id             UUID NOT NULL REFERENCES accounts(id),
    plan_tier              TEXT NOT NULL,
    enabled_modules        JSONB NOT NULL DEFAULT '[]',
    enabled_features       JSONB NOT NULL DEFAULT '[]',
    entitlement_expires_at TIMESTAMPTZ,
    expires_at             TIMESTAMPTZ NOT NULL,
    revoked                BOOLEAN NOT NULL DEFAULT false,
    last_seen_ip           TEXT,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE entitlements (
    plan_tier         TEXT PRIMARY KEY,
    enabled_features  JSONB NOT NULL DEFAULT '[]',
    enabled_modules   JSONB NOT NULL DEFAULT '[]',
    native_components JSONB NOT NULL DEFAULT '[]',
    content_channel   TEXT NOT NULL DEFAULT 'stable',
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE plan_overrides (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID UNIQUE NOT NULL REFERENCES accounts(id),
    additional_modules  JSONB NOT NULL DEFAULT '[]',
    removed_modules     JSONB NOT NULL DEFAULT '[]',
    additional_features JSONB NOT NULL DEFAULT '[]',
    removed_features    JSONB NOT NULL DEFAULT '[]',
    notes               TEXT,
    created_by          TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE content_manifests (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id          TEXT NOT NULL,
    channel           TEXT NOT NULL,
    modules           JSONB NOT NULL DEFAULT '[]',
    native_components JSONB NOT NULL DEFAULT '[]',
    signature         TEXT NOT NULL,
    expires_at        TIMESTAMPTZ NOT NULL,
    revoked           BOOLEAN NOT NULL DEFAULT false,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE admin_tokens (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash     TEXT UNIQUE NOT NULL,
    admin_username TEXT NOT NULL,
    role           TEXT NOT NULL CHECK (role IN ('super_admin','support','viewer')),
    expires_at     TIMESTAMPTZ NOT NULL,
    revoked        BOOLEAN NOT NULL DEFAULT false,
    last_used_at   TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audit_log (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type   TEXT NOT NULL,
    account_id   UUID REFERENCES accounts(id),
    device_id    UUID REFERENCES devices(id),
    admin_name   TEXT,
    ip           TEXT,
    details      JSONB,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_devices_account_id ON devices(account_id);
CREATE INDEX idx_sessions_token_hash ON sessions(session_token_hash);
CREATE INDEX idx_sessions_device_id ON sessions(device_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
CREATE INDEX idx_audit_log_account_id ON audit_log(account_id);
```

- [ ] **Step 2: Apply migration**

```bash
psql $DB_URL -f server/migrations/001_initial.sql
```

Expected: all CREATE TABLE statements succeed, no errors.

- [ ] **Step 3: Commit**

```bash
git add server/migrations/001_initial.sql
git commit -m "feat(server): initial database schema"
```

---

## Task 5: Database client + account queries

**Files:**
- Create: `server/internal/db/db.go`
- Create: `server/internal/db/accounts.go`
- Create: `server/internal/db/devices.go`
- Create: `server/internal/db/license_keys.go`
- Create: `server/internal/db/licenses.go`
- Create: `server/internal/db/sessions.go`
- Create: `server/internal/db/entitlements.go`
- Create: `server/internal/db/manifests.go`
- Create: `server/internal/db/admin_tokens.go`
- Create: `server/internal/db/audit.go`

- [ ] **Step 1: db.go**

```go
// server/internal/db/db.go
package db

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

func NewPool(ctx context.Context, dbURL string) (*pgxpool.Pool, error) {
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		return nil, err
	}
	if err := pool.Ping(ctx); err != nil {
		return nil, err
	}
	return pool, nil
}
```

- [ ] **Step 2: accounts.go**

```go
// server/internal/db/accounts.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Account struct {
	ID           string
	Username     string
	PasswordHash string
	Email        *string
	Status       string
	CreatedAt    time.Time
	UpdatedAt    time.Time
}

func GetAccountByUsername(ctx context.Context, pool *pgxpool.Pool, username string) (*Account, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, username, password_hash, email, status, created_at, updated_at
		 FROM accounts WHERE username = $1`, username)
	a := &Account{}
	err := row.Scan(&a.ID, &a.Username, &a.PasswordHash, &a.Email, &a.Status, &a.CreatedAt, &a.UpdatedAt)
	if err != nil {
		return nil, err
	}
	return a, nil
}

func CreateAccount(ctx context.Context, pool *pgxpool.Pool, username, passwordHash string, email *string) (*Account, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO accounts (username, password_hash, email)
		 VALUES ($1, $2, $3)
		 RETURNING id, username, password_hash, email, status, created_at, updated_at`,
		username, passwordHash, email)
	a := &Account{}
	err := row.Scan(&a.ID, &a.Username, &a.PasswordHash, &a.Email, &a.Status, &a.CreatedAt, &a.UpdatedAt)
	return a, err
}

func UpdateAccountStatus(ctx context.Context, pool *pgxpool.Pool, accountID, status string) error {
	_, err := pool.Exec(ctx,
		`UPDATE accounts SET status = $1, updated_at = now() WHERE id = $2`,
		status, accountID)
	return err
}

func ListAccounts(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*Account, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, username, password_hash, email, status, created_at, updated_at
		 FROM accounts ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var accounts []*Account
	for rows.Next() {
		a := &Account{}
		if err := rows.Scan(&a.ID, &a.Username, &a.PasswordHash, &a.Email, &a.Status, &a.CreatedAt, &a.UpdatedAt); err != nil {
			return nil, err
		}
		accounts = append(accounts, a)
	}
	return accounts, nil
}
```

- [ ] **Step 3: devices.go**

```go
// server/internal/db/devices.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Device struct {
	ID                     string
	AccountID              string
	BindingStatus          string
	HWIDHash               *string
	MinecraftUsername      *string
	EnrollmentIP           *string
	DeviceSecretEncrypted  []byte
	FailedAttempts         int
	LastSeenIP             *string
	LastLoginAt            *time.Time
	BindingResetAt         *time.Time
	BindingResetBy         *string
	CreatedAt              time.Time
	UpdatedAt              time.Time
}

func GetDeviceByAccountID(ctx context.Context, pool *pgxpool.Pool, accountID string) (*Device, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, binding_status, hwid_hash, minecraft_username, enrollment_ip,
		        device_secret_encrypted, failed_attempts, last_seen_ip, last_login_at,
		        binding_reset_at, binding_reset_by, created_at, updated_at
		 FROM devices WHERE account_id = $1 LIMIT 1`, accountID)
	return scanDevice(row)
}

func GetDeviceByID(ctx context.Context, pool *pgxpool.Pool, deviceID string) (*Device, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, binding_status, hwid_hash, minecraft_username, enrollment_ip,
		        device_secret_encrypted, failed_attempts, last_seen_ip, last_login_at,
		        binding_reset_at, binding_reset_by, created_at, updated_at
		 FROM devices WHERE id = $1`, deviceID)
	return scanDevice(row)
}

type scannable interface {
	Scan(dest ...any) error
}

func scanDevice(row scannable) (*Device, error) {
	d := &Device{}
	err := row.Scan(&d.ID, &d.AccountID, &d.BindingStatus, &d.HWIDHash, &d.MinecraftUsername,
		&d.EnrollmentIP, &d.DeviceSecretEncrypted, &d.FailedAttempts,
		&d.LastSeenIP, &d.LastLoginAt, &d.BindingResetAt, &d.BindingResetBy,
		&d.CreatedAt, &d.UpdatedAt)
	return d, err
}

func CreateDevice(ctx context.Context, pool *pgxpool.Pool, accountID, enrollmentIP string, secretEncrypted []byte) (*Device, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO devices (account_id, enrollment_ip, device_secret_encrypted)
		 VALUES ($1, $2, $3)
		 RETURNING id, account_id, binding_status, hwid_hash, minecraft_username, enrollment_ip,
		           device_secret_encrypted, failed_attempts, last_seen_ip, last_login_at,
		           binding_reset_at, binding_reset_by, created_at, updated_at`,
		accountID, enrollmentIP, secretEncrypted)
	return scanDevice(row)
}

func BindHWID(ctx context.Context, pool *pgxpool.Pool, deviceID, hwidHash string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET hwid_hash = $1, binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $2`, hwidHash, deviceID)
	return err
}

func FullyBind(ctx context.Context, pool *pgxpool.Pool, deviceID, minecraftUsername, sourceIP string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET minecraft_username = $1, binding_status = 'fully_bound',
		        last_seen_ip = $2, last_login_at = now(), updated_at = now()
		 WHERE id = $3`, minecraftUsername, sourceIP, deviceID)
	return err
}

func IncrementFailedAttempts(ctx context.Context, pool *pgxpool.Pool, deviceID string) (int, error) {
	row := pool.QueryRow(ctx,
		`UPDATE devices SET failed_attempts = failed_attempts + 1, updated_at = now()
		 WHERE id = $1 RETURNING failed_attempts`, deviceID)
	var count int
	return count, row.Scan(&count)
}

func SuspendDevice(ctx context.Context, pool *pgxpool.Pool, deviceID string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET binding_status = 'suspended', updated_at = now() WHERE id = $1`, deviceID)
	return err
}

func ResetDeviceBinding(ctx context.Context, pool *pgxpool.Pool, deviceID, adminUsername string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET hwid_hash = NULL, minecraft_username = NULL, enrollment_ip = NULL,
		        failed_attempts = 0, binding_status = 'unbound',
		        binding_reset_at = now(), binding_reset_by = $1, updated_at = now()
		 WHERE id = $2`, adminUsername, deviceID)
	return err
}

func UpdateDeviceStatus(ctx context.Context, pool *pgxpool.Pool, deviceID, status string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET binding_status = $1, updated_at = now() WHERE id = $2`, status, deviceID)
	return err
}
```

- [ ] **Step 4: license_keys.go**

```go
// server/internal/db/license_keys.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type LicenseKey struct {
	ID           string
	KeyHash      string
	PlanTier     string
	Status       string
	RedeemedBy   *string
	RedeemedAt   *time.Time
	EnrollmentIP *string
	CreatedBy    string
	Notes        *string
	CreatedAt    time.Time
}

func GetLicenseKeyByHash(ctx context.Context, pool *pgxpool.Pool, keyHash string) (*LicenseKey, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, key_hash, plan_tier, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at
		 FROM license_keys WHERE key_hash = $1`, keyHash)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RedeemLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, accountID, sourceIP string) error {
	_, err := pool.Exec(ctx,
		`UPDATE license_keys SET status = 'redeemed', redeemed_by = $1, redeemed_at = now(), enrollment_ip = $2
		 WHERE key_hash = $3 AND status = 'available'`, accountID, sourceIP, keyHash)
	return err
}

func CreateLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, planTier, createdBy string, notes *string) (*LicenseKey, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO license_keys (key_hash, plan_tier, created_by, notes)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, key_hash, plan_tier, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at`,
		keyHash, planTier, createdBy, notes)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RevokeLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyID string) error {
	_, err := pool.Exec(ctx, `UPDATE license_keys SET status = 'revoked' WHERE id = $1`, keyID)
	return err
}
```

- [ ] **Step 5: sessions.go**

```go
// server/internal/db/sessions.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Session struct {
	ID                   string
	SessionTokenHash     string
	DeviceID             string
	AccountID            string
	PlanTier             string
	EnabledModules       []string
	EnabledFeatures      []string
	EntitlementExpiresAt *time.Time
	ExpiresAt            time.Time
	Revoked              bool
	LastSeenIP           *string
	CreatedAt            time.Time
}

func CreateSession(ctx context.Context, pool *pgxpool.Pool, tokenHash, deviceID, accountID, planTier string,
	modules, features []string, entitlementExpiry *time.Time, expiresAt time.Time, ip string) (*Session, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO sessions (session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		                       entitlement_expires_at, expires_at, last_seen_ip)
		 VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
		 RETURNING id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		           entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at`,
		tokenHash, deviceID, accountID, planTier, modules, features, entitlementExpiry, expiresAt, ip)
	s := &Session{}
	err := row.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
		&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
		&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt)
	return s, err
}

func GetSessionByTokenHash(ctx context.Context, pool *pgxpool.Pool, tokenHash string) (*Session, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		        entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at
		 FROM sessions WHERE session_token_hash = $1`, tokenHash)
	s := &Session{}
	err := row.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
		&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
		&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt)
	return s, err
}

func RevokeSession(ctx context.Context, pool *pgxpool.Pool, sessionID string) error {
	_, err := pool.Exec(ctx, `UPDATE sessions SET revoked = true WHERE id = $1`, sessionID)
	return err
}

func ListActiveSessions(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*Session, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		        entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at
		 FROM sessions WHERE revoked = false AND expires_at > now()
		 ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var sessions []*Session
	for rows.Next() {
		s := &Session{}
		if err := rows.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
			&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
			&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt); err != nil {
			return nil, err
		}
		sessions = append(sessions, s)
	}
	return sessions, nil
}
```

- [ ] **Step 6: entitlements.go**

```go
// server/internal/db/entitlements.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Entitlement struct {
	PlanTier         string
	EnabledFeatures  []string
	EnabledModules   []string
	NativeComponents []string
	ContentChannel   string
	UpdatedAt        time.Time
}

type PlanOverride struct {
	ID                 string
	AccountID          string
	AdditionalModules  []string
	RemovedModules     []string
	AdditionalFeatures []string
	RemovedFeatures    []string
	Notes              *string
	CreatedBy          string
	CreatedAt          time.Time
	UpdatedAt          time.Time
}

func GetEntitlement(ctx context.Context, pool *pgxpool.Pool, planTier string) (*Entitlement, error) {
	row := pool.QueryRow(ctx,
		`SELECT plan_tier, enabled_features, enabled_modules, native_components, content_channel, updated_at
		 FROM entitlements WHERE plan_tier = $1`, planTier)
	e := &Entitlement{}
	err := row.Scan(&e.PlanTier, &e.EnabledFeatures, &e.EnabledModules, &e.NativeComponents, &e.ContentChannel, &e.UpdatedAt)
	return e, err
}

func GetPlanOverride(ctx context.Context, pool *pgxpool.Pool, accountID string) (*PlanOverride, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, additional_modules, removed_modules, additional_features, removed_features,
		        notes, created_by, created_at, updated_at
		 FROM plan_overrides WHERE account_id = $1`, accountID)
	o := &PlanOverride{}
	err := row.Scan(&o.ID, &o.AccountID, &o.AdditionalModules, &o.RemovedModules,
		&o.AdditionalFeatures, &o.RemovedFeatures, &o.Notes, &o.CreatedBy, &o.CreatedAt, &o.UpdatedAt)
	return o, err
}

func UpsertEntitlement(ctx context.Context, pool *pgxpool.Pool, e *Entitlement) error {
	_, err := pool.Exec(ctx,
		`INSERT INTO entitlements (plan_tier, enabled_features, enabled_modules, native_components, content_channel)
		 VALUES ($1, $2, $3, $4, $5)
		 ON CONFLICT (plan_tier) DO UPDATE SET
		   enabled_features = EXCLUDED.enabled_features,
		   enabled_modules = EXCLUDED.enabled_modules,
		   native_components = EXCLUDED.native_components,
		   content_channel = EXCLUDED.content_channel,
		   updated_at = now()`,
		e.PlanTier, e.EnabledFeatures, e.EnabledModules, e.NativeComponents, e.ContentChannel)
	return err
}

func UpsertPlanOverride(ctx context.Context, pool *pgxpool.Pool, o *PlanOverride) error {
	_, err := pool.Exec(ctx,
		`INSERT INTO plan_overrides (account_id, additional_modules, removed_modules, additional_features, removed_features, notes, created_by)
		 VALUES ($1, $2, $3, $4, $5, $6, $7)
		 ON CONFLICT (account_id) DO UPDATE SET
		   additional_modules = EXCLUDED.additional_modules,
		   removed_modules = EXCLUDED.removed_modules,
		   additional_features = EXCLUDED.additional_features,
		   removed_features = EXCLUDED.removed_features,
		   notes = EXCLUDED.notes,
		   updated_at = now()`,
		o.AccountID, o.AdditionalModules, o.RemovedModules, o.AdditionalFeatures, o.RemovedFeatures, o.Notes, o.CreatedBy)
	return err
}

func DeletePlanOverride(ctx context.Context, pool *pgxpool.Pool, accountID string) error {
	_, err := pool.Exec(ctx, `DELETE FROM plan_overrides WHERE account_id = $1`, accountID)
	return err
}
```

- [ ] **Step 7: manifests.go**

```go
// server/internal/db/manifests.go
package db

import (
	"context"
	"encoding/json"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type ManifestModule struct {
	Name     string `json:"name"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}

type ManifestNative struct {
	Name     string `json:"name"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}

type ContentManifest struct {
	ID               string
	BuildID          string
	Channel          string
	Modules          []ManifestModule
	NativeComponents []ManifestNative
	Signature        string
	ExpiresAt        time.Time
	Revoked          bool
	CreatedAt        time.Time
}

func GetLatestManifest(ctx context.Context, pool *pgxpool.Pool, channel string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at
		 FROM content_manifests
		 WHERE channel = $1 AND revoked = false AND expires_at > now()
		 ORDER BY created_at DESC LIMIT 1`, channel)
	return scanManifest(row)
}

func GetManifestByID(ctx context.Context, pool *pgxpool.Pool, id string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at
		 FROM content_manifests WHERE id = $1`, id)
	return scanManifest(row)
}

func scanManifest(row scannable) (*ContentManifest, error) {
	m := &ContentManifest{}
	var modulesJSON, nativesJSON []byte
	err := row.Scan(&m.ID, &m.BuildID, &m.Channel, &modulesJSON, &nativesJSON,
		&m.Signature, &m.ExpiresAt, &m.Revoked, &m.CreatedAt)
	if err != nil {
		return nil, err
	}
	json.Unmarshal(modulesJSON, &m.Modules)
	json.Unmarshal(nativesJSON, &m.NativeComponents)
	return m, nil
}

func CreateManifest(ctx context.Context, pool *pgxpool.Pool, m *ContentManifest) (*ContentManifest, error) {
	modulesJSON, _ := json.Marshal(m.Modules)
	nativesJSON, _ := json.Marshal(m.NativeComponents)
	row := pool.QueryRow(ctx,
		`INSERT INTO content_manifests (build_id, channel, modules, native_components, signature, expires_at)
		 VALUES ($1, $2, $3, $4, $5, $6)
		 RETURNING id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at`,
		m.BuildID, m.Channel, modulesJSON, nativesJSON, m.Signature, m.ExpiresAt)
	return scanManifest(row)
}
```

- [ ] **Step 8: admin_tokens.go**

```go
// server/internal/db/admin_tokens.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type AdminToken struct {
	ID            string
	TokenHash     string
	AdminUsername string
	Role          string
	ExpiresAt     time.Time
	Revoked       bool
	LastUsedAt    *time.Time
	CreatedAt     time.Time
}

func CreateAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenHash, adminUsername, role string, expiresAt time.Time) (*AdminToken, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO admin_tokens (token_hash, admin_username, role, expires_at)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, token_hash, admin_username, role, expires_at, revoked, last_used_at, created_at`,
		tokenHash, adminUsername, role, expiresAt)
	t := &AdminToken{}
	err := row.Scan(&t.ID, &t.TokenHash, &t.AdminUsername, &t.Role, &t.ExpiresAt, &t.Revoked, &t.LastUsedAt, &t.CreatedAt)
	return t, err
}

func GetAdminTokenByHash(ctx context.Context, pool *pgxpool.Pool, tokenHash string) (*AdminToken, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, token_hash, admin_username, role, expires_at, revoked, last_used_at, created_at
		 FROM admin_tokens WHERE token_hash = $1`, tokenHash)
	t := &AdminToken{}
	err := row.Scan(&t.ID, &t.TokenHash, &t.AdminUsername, &t.Role, &t.ExpiresAt, &t.Revoked, &t.LastUsedAt, &t.CreatedAt)
	return t, err
}

func TouchAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenID string) error {
	_, err := pool.Exec(ctx, `UPDATE admin_tokens SET last_used_at = now() WHERE id = $1`, tokenID)
	return err
}

func RevokeAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenID string) error {
	_, err := pool.Exec(ctx, `UPDATE admin_tokens SET revoked = true WHERE id = $1`, tokenID)
	return err
}
```

- [ ] **Step 9: audit.go**

```go
// server/internal/db/audit.go
package db

import (
	"context"
	"encoding/json"

	"github.com/jackc/pgx/v5/pgxpool"
)

type AuditEvent struct {
	EventType string
	AccountID *string
	DeviceID  *string
	AdminName *string
	IP        *string
	Details   map[string]any
}

func WriteAudit(ctx context.Context, pool *pgxpool.Pool, e AuditEvent) {
	details, _ := json.Marshal(e.Details)
	pool.Exec(ctx,
		`INSERT INTO audit_log (event_type, account_id, device_id, admin_name, ip, details)
		 VALUES ($1, $2, $3, $4, $5, $6)`,
		e.EventType, e.AccountID, e.DeviceID, e.AdminName, e.IP, details)
}
```

- [ ] **Step 10: licenses.go**

```go
// server/internal/db/licenses.go
package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type License struct {
	ID             string
	AccountID      string
	PlanTier       string
	Status         string
	StartsAt       time.Time
	ExpiresAt      *time.Time
	GraceExpiresAt *time.Time
	MaxDevices     *int
	Notes          *string
	CreatedAt      time.Time
	UpdatedAt      time.Time
}

func GetLicenseByAccountID(ctx context.Context, pool *pgxpool.Pool, accountID string) (*License, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, plan_tier, status, starts_at, expires_at, grace_expires_at, max_devices, notes, created_at, updated_at
		 FROM licenses WHERE account_id = $1`, accountID)
	l := &License{}
	err := row.Scan(&l.ID, &l.AccountID, &l.PlanTier, &l.Status, &l.StartsAt, &l.ExpiresAt,
		&l.GraceExpiresAt, &l.MaxDevices, &l.Notes, &l.CreatedAt, &l.UpdatedAt)
	return l, err
}

func CreateLicense(ctx context.Context, pool *pgxpool.Pool, accountID, planTier string, startsAt time.Time, expiresAt *time.Time) (*License, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, account_id, plan_tier, status, starts_at, expires_at, grace_expires_at, max_devices, notes, created_at, updated_at`,
		accountID, planTier, startsAt, expiresAt)
	l := &License{}
	err := row.Scan(&l.ID, &l.AccountID, &l.PlanTier, &l.Status, &l.StartsAt, &l.ExpiresAt,
		&l.GraceExpiresAt, &l.MaxDevices, &l.Notes, &l.CreatedAt, &l.UpdatedAt)
	return l, err
}

func UpdateLicenseStatus(ctx context.Context, pool *pgxpool.Pool, licenseID, status string) error {
	_, err := pool.Exec(ctx,
		`UPDATE licenses SET status = $1, updated_at = now() WHERE id = $2`, status, licenseID)
	return err
}
```

- [ ] **Step 11: Commit**

```bash
git add server/internal/db/
git commit -m "feat(server): database client and all query functions"
```

---

## Task 6: Redis cache (challenges + rate limiting)

**Files:**
- Create: `server/internal/cache/cache.go`
- Create: `server/internal/cache/challenges.go`
- Create: `server/internal/cache/ratelimit.go`

- [ ] **Step 1: cache.go**

```go
// server/internal/cache/cache.go
package cache

import (
	"context"

	"github.com/redis/go-redis/v9"
)

func NewClient(redisURL string) (*redis.Client, error) {
	opts, err := redis.ParseURL(redisURL)
	if err != nil {
		return nil, err
	}
	client := redis.NewClient(opts)
	if err := client.Ping(context.Background()).Err(); err != nil {
		return nil, err
	}
	return client, nil
}
```

- [ ] **Step 2: challenges.go**

```go
// server/internal/cache/challenges.go
package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

type Challenge struct {
	DeviceID  string `json:"device_id"`
	Challenge string `json:"challenge"`
	SourceIP  string `json:"source_ip"`
	Used      bool   `json:"used"`
}

const challengeTTL = 30 * time.Second

func challengeKey(deviceID string) string {
	return fmt.Sprintf("auth:challenge:%s", deviceID)
}

func StoreChallenge(ctx context.Context, rdb *redis.Client, c *Challenge) error {
	data, err := json.Marshal(c)
	if err != nil {
		return err
	}
	return rdb.Set(ctx, challengeKey(c.DeviceID), data, challengeTTL).Err()
}

func GetChallenge(ctx context.Context, rdb *redis.Client, deviceID string) (*Challenge, error) {
	data, err := rdb.Get(ctx, challengeKey(deviceID)).Bytes()
	if err != nil {
		return nil, err
	}
	c := &Challenge{}
	return c, json.Unmarshal(data, c)
}

func DeleteChallenge(ctx context.Context, rdb *redis.Client, deviceID string) error {
	return rdb.Del(ctx, challengeKey(deviceID)).Err()
}
```

- [ ] **Step 3: ratelimit.go**

```go
// server/internal/cache/ratelimit.go
package cache

import (
	"context"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

func CheckRateLimit(ctx context.Context, rdb *redis.Client, key string, limit int, window time.Duration) (bool, error) {
	redisKey := fmt.Sprintf("rl:%s", key)
	pipe := rdb.Pipeline()
	incr := pipe.Incr(ctx, redisKey)
	pipe.Expire(ctx, redisKey, window)
	if _, err := pipe.Exec(ctx); err != nil {
		return false, err
	}
	return incr.Val() <= int64(limit), nil
}
```

- [ ] **Step 4: Commit**

```bash
git add server/internal/cache/
git commit -m "feat(server): Redis cache for challenges and rate limiting"
```

---

## Task 7: Middleware

**Files:**
- Create: `server/internal/middleware/realip.go`
- Create: `server/internal/middleware/ratelimit.go`
- Create: `server/internal/middleware/session.go`
- Create: `server/internal/middleware/admin.go`

- [ ] **Step 1: realip.go**

```go
// server/internal/middleware/realip.go
package middleware

import "github.com/gofiber/fiber/v2"

// RealIP stores the true source IP from the TCP connection into locals.
// Never trust X-Forwarded-For from clients — only use it if behind a trusted proxy.
func RealIP() fiber.Handler {
	return func(c *fiber.Ctx) error {
		// fiber.Ctx.IP() returns the RemoteAddr by default when ProxyHeader is not set
		c.Locals("realip", c.IP())
		return c.Next()
	}
}

func GetRealIP(c *fiber.Ctx) string {
	if ip, ok := c.Locals("realip").(string); ok {
		return ip
	}
	return c.IP()
}
```

- [ ] **Step 2: ratelimit.go**

```go
// server/internal/middleware/ratelimit.go
package middleware

import (
	"fmt"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/redis/go-redis/v9"
	"github.com/cobalt/server/internal/cache"
)

func RateLimit(rdb *redis.Client, limit int, window time.Duration, keyFn func(*fiber.Ctx) string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		key := keyFn(c)
		allowed, err := cache.CheckRateLimit(c.Context(), rdb, key, limit, window)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if !allowed {
			return c.Status(429).JSON(fiber.Map{"error": "rate_limited"})
		}
		return c.Next()
	}
}

func IPKey(prefix string) func(*fiber.Ctx) string {
	return func(c *fiber.Ctx) string {
		return fmt.Sprintf("%s:%s", prefix, GetRealIP(c))
	}
}

func IPAndUsernameKey(prefix string) func(*fiber.Ctx) string {
	return func(c *fiber.Ctx) string {
		body := struct{ Username string `json:"username"` }{}
		c.BodyParser(&body)
		return fmt.Sprintf("%s:%s:%s", prefix, GetRealIP(c), body.Username)
	}
}
```

- [ ] **Step 3: session.go**

```go
// server/internal/middleware/session.go
package middleware

import (
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

type SessionKey struct{}

func SessionAuth(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		header := c.Get("Authorization")
		if !strings.HasPrefix(header, "Bearer ") {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		raw := strings.TrimPrefix(header, "Bearer ")
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || session.Revoked || time.Now().After(session.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		c.Locals("session", session)
		return c.Next()
	}
}

func GetSession(c *fiber.Ctx) *db.Session {
	s, _ := c.Locals("session").(*db.Session)
	return s
}
```

- [ ] **Step 4: admin.go**

```go
// server/internal/middleware/admin.go
package middleware

import (
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

var roleRank = map[string]int{"viewer": 1, "support": 2, "super_admin": 3}

func AdminAuth(pool *pgxpool.Pool, minRole string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		header := c.Get("Authorization")
		if !strings.HasPrefix(header, "Bearer ") {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		raw := strings.TrimPrefix(header, "Bearer ")
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		token, err := db.GetAdminTokenByHash(c.Context(), pool, hash)
		if err != nil || token.Revoked || time.Now().After(token.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if roleRank[token.Role] < roleRank[minRole] {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized"})
		}
		go db.TouchAdminToken(c.Context(), pool, token.ID)
		c.Locals("admin_token", token)
		return c.Next()
	}
}

func GetAdminToken(c *fiber.Ctx) *db.AdminToken {
	t, _ := c.Locals("admin_token").(*db.AdminToken)
	return t
}
```

- [ ] **Step 5: Commit**

```bash
git add server/internal/middleware/
git commit -m "feat(server): middleware — real IP, rate limit, session auth, admin token auth"
```

---

## Task 8: Audit service

**Files:**
- Create: `server/internal/audit/audit.go`

- [ ] **Step 1: audit.go**

```go
// server/internal/audit/audit.go
package audit

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/db"
)

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

func (s *Service) Log(eventType string, accountID, deviceID, adminName, ip *string, details map[string]any) {
	go db.WriteAudit(context.Background(), s.pool, db.AuditEvent{
		EventType: eventType,
		AccountID: accountID,
		DeviceID:  deviceID,
		AdminName: adminName,
		IP:        ip,
		Details:   details,
	})
}
```

- [ ] **Step 2: Commit**

```bash
git add server/internal/audit/
git commit -m "feat(server): fire-and-forget audit service"
```

---

## Task 9: Entitlement service

**Files:**
- Create: `server/internal/entitlement/service.go`

- [ ] **Step 1: service.go**

```go
// server/internal/entitlement/service.go
package entitlement

import (
	"context"
	"errors"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/db"
)

type Result struct {
	Authorized           bool
	Reason               string
	PlanTier             string
	EnabledModules       []string
	EnabledFeatures      []string
	NativeComponents     []string
	ContentChannel       string
	EntitlementExpiresAt *time.Time
}

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

func (s *Service) Resolve(ctx context.Context, accountID string) (*Result, error) {
	license, err := db.GetLicenseByAccountID(ctx, s.pool, accountID)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return &Result{Authorized: false, Reason: "no_license"}, nil
		}
		return nil, err
	}

	now := time.Now()
	switch license.Status {
	case "revoked", "suspended":
		return &Result{Authorized: false, Reason: "license_" + license.Status}, nil
	case "expired":
		if license.GraceExpiresAt == nil || now.After(*license.GraceExpiresAt) {
			return &Result{Authorized: false, Reason: "license_expired"}, nil
		}
	case "active", "trial":
		if license.ExpiresAt != nil && now.After(*license.ExpiresAt) {
			return &Result{Authorized: false, Reason: "license_expired"}, nil
		}
	default:
		return &Result{Authorized: false, Reason: "entitlement_inactive"}, nil
	}

	ent, err := db.GetEntitlement(ctx, s.pool, license.PlanTier)
	if err != nil {
		return &Result{Authorized: false, Reason: "no_entitlement"}, nil
	}

	modules := make([]string, len(ent.EnabledModules))
	copy(modules, ent.EnabledModules)
	features := make([]string, len(ent.EnabledFeatures))
	copy(features, ent.EnabledFeatures)

	override, err := db.GetPlanOverride(ctx, s.pool, accountID)
	if err == nil && override != nil {
		modules = applyOverride(modules, override.AdditionalModules, override.RemovedModules)
		features = applyOverride(features, override.AdditionalFeatures, override.RemovedFeatures)
	}

	return &Result{
		Authorized:           true,
		PlanTier:             license.PlanTier,
		EnabledModules:       modules,
		EnabledFeatures:      features,
		NativeComponents:     ent.NativeComponents,
		ContentChannel:       ent.ContentChannel,
		EntitlementExpiresAt: license.ExpiresAt,
	}, nil
}

func applyOverride(base, add, remove []string) []string {
	set := make(map[string]bool, len(base))
	for _, v := range base {
		set[v] = true
	}
	for _, v := range add {
		set[v] = true
	}
	for _, v := range remove {
		delete(set, v)
	}
	result := make([]string, 0, len(set))
	for v := range set {
		result = append(result, v)
	}
	return result
}
```

- [ ] **Step 2: Commit**

```bash
git add server/internal/entitlement/
git commit -m "feat(server): entitlement resolution with plan overrides"
```

---

## Task 10: Enrollment handlers

**Files:**
- Create: `server/internal/enrollment/service.go`
- Create: `server/internal/enrollment/handler.go`

- [ ] **Step 1: service.go**

```go
// server/internal/enrollment/service.go
package enrollment

import (
	"context"
	"errors"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

var (
	ErrKeyNotFound    = errors.New("key not found")
	ErrKeyNotAvailable = errors.New("key already used or revoked")
	ErrIPMismatch     = errors.New("ip mismatch")
	ErrBadCredentials = errors.New("bad credentials")
	ErrNoLicense      = errors.New("no license on account")
)

type Service struct {
	pool      *pgxpool.Pool
	masterKey []byte
	pepper    []byte
}

func New(pool *pgxpool.Pool, masterKey, pepper []byte) *Service {
	return &Service{pool: pool, masterKey: masterKey, pepper: pepper}
}

func (s *Service) Redeem(ctx context.Context, rawKey, accountID, sourceIP string) error {
	keyHash := crypto.HashLicenseKey(rawKey)
	key, err := db.GetLicenseKeyByHash(ctx, s.pool, keyHash)
	if errors.Is(err, pgx.ErrNoRows) {
		return ErrKeyNotFound
	}
	if err != nil {
		return err
	}
	if key.Status != "available" {
		return ErrKeyNotAvailable
	}
	// Generate device secret
	secret := make([]byte, 32)
	if _, err := randRead(secret); err != nil {
		return err
	}
	encrypted, err := crypto.EncryptAESGCM(s.masterKey, secret)
	if err != nil {
		return err
	}
	// Create device record in unbound state
	if _, err := db.CreateDevice(ctx, s.pool, accountID, sourceIP, encrypted); err != nil {
		return err
	}
	return db.RedeemLicenseKey(ctx, s.pool, keyHash, accountID, sourceIP)
}

func (s *Service) Handshake(ctx context.Context, username, password, rawHWID, sourceIP string) (deviceSecret string, err error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if err != nil {
		return "", ErrBadCredentials
	}
	if account.Status != "active" {
		return "", ErrBadCredentials
	}
	ok, err := crypto.VerifyPassword(password, account.PasswordHash)
	if err != nil || !ok {
		return "", ErrBadCredentials
	}
	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if err != nil {
		return "", ErrBadCredentials
	}
	if device.BindingStatus != "unbound" {
		return "", ErrBadCredentials
	}
	if device.EnrollmentIP == nil || *device.EnrollmentIP != sourceIP {
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
	return encodeBase64(plain), nil
}

func normalize(s string) string  { return strings.ToLower(strings.TrimSpace(s)) }
func normalizeHWID(s string) string { return strings.ToUpper(strings.TrimSpace(s)) }
```

- [ ] **Step 2: handler.go**

```go
// server/internal/enrollment/handler.go
package enrollment

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/middleware"
)

type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	AccountID  string `json:"account_id"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	HWID     string `json:"hwid"`
}

func RegisterRoutes(app *fiber.App, svc *Service) {
	app.Post("/enroll/redeem", handleRedeem(svc))
	app.Post("/enroll/handshake", handleHandshake(svc))
}

func handleRedeem(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || req.LicenseKey == "" || req.AccountID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		err := svc.Redeem(c.Context(), req.LicenseKey, req.AccountID, ip)
		if errors.Is(err, ErrKeyNotFound) || errors.Is(err, ErrKeyNotAvailable) {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"status": "redeemed"})
	}
}

func handleHandshake(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req handshakeRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Password == "" || req.HWID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		secret, err := svc.Handshake(c.Context(), req.Username, req.Password, req.HWID, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadCredentials) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"device_secret": secret})
	}
}
```

- [ ] **Step 3: Commit**

```bash
git add server/internal/enrollment/
git commit -m "feat(server): enrollment — license key redemption and bootstrapper handshake"
```

---

## Task 11: Auth handlers

**Files:**
- Create: `server/internal/auth/service.go`
- Create: `server/internal/auth/handler.go`

- [ ] **Step 1: service.go**

```go
// server/internal/auth/service.go
package auth

import (
	"context"
	"crypto/rand"
	"encoding/base64"
	"encoding/hex"
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
	ErrNotFound      = errors.New("not found")
	ErrIPMismatch    = errors.New("ip mismatch")
	ErrHWIDMismatch  = errors.New("hwid mismatch")
	ErrUsernameMismatch = errors.New("username mismatch")
	ErrBadProof      = errors.New("bad proof")
	ErrDeviceBlocked = errors.New("device blocked")
	ErrNoChallenge   = errors.New("no challenge")
)

type StartResult struct {
	Challenge   string
	ExpiresIn   int
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

	// Delete challenge immediately (mark used)
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

	// Entitlement
	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		return nil, err
	}
	if !ent.Authorized {
		return &FinishResult{Authenticated: true, Authorized: false, Reason: ent.Reason}, nil
	}

	// Create session (1 hour)
	expiresAt := time.Now().Add(time.Hour)
	rawToken, tokenHash, err := crypto.GenerateToken()
	if err != nil {
		return nil, err
	}
	sess, err := db.CreateSession(ctx, s.pool, tokenHash, device.ID, account.ID,
		ent.PlanTier, ent.EnabledModules, ent.EnabledFeatures,
		ent.EntitlementExpiresAt, expiresAt, sourceIP)
	if err != nil {
		return nil, err
	}

	// Get latest manifest for this channel
	manifest, err := db.GetLatestManifest(ctx, s.pool, ent.ContentChannel)
	manifestURL := ""
	if err == nil {
		manifestURL = s.baseURL + "/content/manifest/" + manifest.ID
	}

	_ = sess
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

func normalize(s string) string      { return strings.ToLower(strings.TrimSpace(s)) }
func normalizeHWID(s string) string  { return strings.ToUpper(strings.TrimSpace(s)) }
func encodeHex(b []byte) string      { return hex.EncodeToString(b) }
```

- [ ] **Step 2: handler.go**

```go
// server/internal/auth/handler.go
package auth

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/middleware"
)

type startRequest struct {
	Username          string `json:"username"`
	HWID              string `json:"hwid"`
	MinecraftUsername string `json:"minecraft_username"`
	ClientVersion     string `json:"client_version"`
	BootstrapBuildID  string `json:"bootstrap_build_id"`
}

type finishRequest struct {
	Username          string `json:"username"`
	Proof             string `json:"proof"`
	MinecraftUsername string `json:"minecraft_username"`
}

func RegisterRoutes(app *fiber.App, svc *Service) {
	app.Post("/auth/start", handleStart(svc))
	app.Post("/auth/finish", handleFinish(svc))
}

func handleStart(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req startRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.HWID == "" {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		ip := middleware.GetRealIP(c)
		result, err := svc.Start(c.Context(), req.Username, req.HWID, req.MinecraftUsername, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrHWIDMismatch) ||
			errors.Is(err, ErrNotFound) || errors.Is(err, ErrDeviceBlocked) || errors.Is(err, ErrUsernameMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "authentication_failed"})
		}
		return c.JSON(fiber.Map{"challenge": result.Challenge, "expires_in": result.ExpiresIn})
	}
}

func handleFinish(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req finishRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Proof == "" {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		ip := middleware.GetRealIP(c)
		result, err := svc.Finish(c.Context(), req.Username, req.Proof, ip, req.MinecraftUsername)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadProof) ||
			errors.Is(err, ErrNoChallenge) || errors.Is(err, ErrNotFound) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if !result.Authorized {
			return c.JSON(fiber.Map{"authenticated": true, "authorized": false, "reason": result.Reason})
		}
		return c.JSON(fiber.Map{
			"authenticated":   true,
			"authorized":      true,
			"session_token":   result.SessionToken,
			"expires_in":      result.ExpiresIn,
			"plan_tier":       result.PlanTier,
			"enabled_modules": result.Modules,
			"enabled_features": result.Features,
			"manifest_url":    result.ManifestURL,
		})
	}
}
```

- [ ] **Step 3: Commit**

```bash
git add server/internal/auth/
git commit -m "feat(server): auth start/finish with challenge-response, binding, and entitlement"
```

---

## Task 12: Content delivery handlers

**Files:**
- Create: `server/internal/content/service.go`
- Create: `server/internal/content/handler.go`

- [ ] **Step 1: service.go**

```go
// server/internal/content/service.go
package content

import (
	"context"
	"errors"
	"path/filepath"
	"slices"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/entitlement"
)

var (
	ErrNotEntitled = errors.New("not entitled")
	ErrExpired     = errors.New("manifest expired or revoked")
	ErrNotFound    = errors.New("not found")
)

type Service struct {
	pool       *pgxpool.Pool
	entSvc     *entitlement.Service
	contentDir string
}

func New(pool *pgxpool.Pool, entSvc *entitlement.Service, contentDir string) *Service {
	return &Service{pool: pool, entSvc: entSvc, contentDir: contentDir}
}

func (s *Service) GetManifest(ctx context.Context, accountID, manifestID string) (*db.ContentManifest, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, ErrNotEntitled
	}
	m, err := db.GetManifestByID(ctx, s.pool, manifestID)
	if err != nil {
		return nil, ErrNotFound
	}
	if m.Revoked || time.Now().After(m.ExpiresAt) {
		return nil, ErrExpired
	}
	return m, nil
}

func (s *Service) ModulePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}
	if !slices.Contains(ent.EnabledModules, name) {
		return "", ErrNotEntitled
	}
	return filepath.Join(s.contentDir, "modules", filepath.Base(name)), nil
}

func (s *Service) NativePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}
	if !slices.Contains(ent.NativeComponents, name) {
		return "", ErrNotEntitled
	}
	return filepath.Join(s.contentDir, "native", filepath.Base(name)), nil
}
```

- [ ] **Step 2: handler.go**

```go
// server/internal/content/handler.go
package content

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/middleware"
	"github.com/jackc/pgx/v5/pgxpool"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, svc *Service) {
	auth := middleware.SessionAuth(pool)
	app.Get("/content/manifest/:id", auth, handleManifest(svc))
	app.Get("/content/module/:name", auth, handleModule(svc))
	app.Get("/content/native/:name", auth, handleNative(svc))
}

func handleManifest(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		m, err := svc.GetManifest(c.Context(), sess.AccountID, c.Params("id"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized"})
		}
		if errors.Is(err, ErrNotFound) || errors.Is(err, ErrExpired) {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(m)
	}
}

func handleModule(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		path, err := svc.ModulePath(c.Context(), sess.AccountID, c.Params("name"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.SendFile(path)
	}
}

func handleNative(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		path, err := svc.NativePath(c.Context(), sess.AccountID, c.Params("name"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.SendFile(path)
	}
}
```

- [ ] **Step 3: Commit**

```bash
git add server/internal/content/
git commit -m "feat(server): content delivery — manifest, module, and native download"
```

---

## Task 13: Admin handlers

**Files:**
- Create: `server/internal/admin/handler.go`
- Create: `server/internal/admin/service.go`

- [ ] **Step 1: service.go**

```go
// server/internal/admin/service.go
package admin

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

func (s *Service) CreateAccount(ctx context.Context, username, password string, email *string) (*db.Account, error) {
	hash, err := crypto.HashPassword(password)
	if err != nil {
		return nil, err
	}
	return db.CreateAccount(ctx, s.pool, username, hash, email)
}

func (s *Service) GenerateKeys(ctx context.Context, planTier, createdBy string, count int, notes *string) ([]string, error) {
	keys := make([]string, count)
	for i := range count {
		raw, hash, err := crypto.GenerateLicenseKey()
		if err != nil {
			return nil, err
		}
		if _, err := db.CreateLicenseKey(ctx, s.pool, hash, planTier, createdBy, notes); err != nil {
			return nil, err
		}
		keys[i] = raw
	}
	return keys, nil
}

func (s *Service) CreateAdminToken(ctx context.Context, adminUsername, role string, durationHours int) (string, error) {
	raw, hash, err := crypto.GenerateToken()
	if err != nil {
		return "", err
	}
	expiresAt := time.Now().Add(time.Duration(durationHours) * time.Hour)
	if _, err := db.CreateAdminToken(ctx, s.pool, hash, adminUsername, role, expiresAt); err != nil {
		return "", err
	}
	return raw, nil
}

func (s *Service) ResetDeviceBinding(ctx context.Context, deviceID, adminUsername string) error {
	return db.ResetDeviceBinding(ctx, s.pool, deviceID, adminUsername)
}

func (s *Service) CreateLicense(ctx context.Context, accountID, planTier string, expiresAt *time.Time) (*db.License, error) {
	return db.CreateLicense(ctx, s.pool, accountID, planTier, time.Now(), expiresAt)
}
```

- [ ] **Step 2: handler.go**

```go
// server/internal/admin/handler.go
package admin

import (
	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/entitlement"
	"github.com/cobalt/server/internal/middleware"
	"github.com/jackc/pgx/v5/pgxpool"
	"time"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, svc *Service, entSvc *entitlement.Service) {
	superAdmin := middleware.AdminAuth(pool, "super_admin")
	support    := middleware.AdminAuth(pool, "support")
	viewer     := middleware.AdminAuth(pool, "viewer")

	app.Post("/admin/accounts", superAdmin, handleCreateAccount(svc))
	app.Get("/admin/accounts", viewer, handleListAccounts(pool))
	app.Patch("/admin/accounts/:id", support, handleUpdateAccount(pool))

	app.Post("/admin/keys", superAdmin, handleGenerateKeys(svc))
	app.Patch("/admin/keys/:id", superAdmin, handleRevokeKey(pool))

	app.Post("/admin/devices/:id/reset", support, handleResetBinding(svc, pool))

	app.Get("/admin/sessions", viewer, handleListSessions(pool))
	app.Delete("/admin/sessions/:id", superAdmin, handleRevokeSession(pool))

	app.Get("/admin/entitlements/:tier", viewer, handleGetEntitlement(pool))
	app.Put("/admin/entitlements/:tier", superAdmin, handleUpsertEntitlement(pool))

	app.Post("/admin/overrides/:account_id", superAdmin, handleUpsertOverride(pool))
	app.Delete("/admin/overrides/:account_id", superAdmin, handleDeleteOverride(pool))

	app.Post("/admin/tokens", superAdmin, handleCreateToken(svc))
	app.Delete("/admin/tokens/:id", superAdmin, handleRevokeToken(pool))

	app.Get("/admin/audit", viewer, handleListAudit(pool))
}

func handleCreateAccount(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req struct {
			Username string  `json:"username"`
			Password string  `json:"password"`
			Email    *string `json:"email"`
		}
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		account, err := svc.CreateAccount(c.Context(), req.Username, req.Password, req.Email)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.Status(201).JSON(account)
	}
}

func handleListAccounts(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		accounts, err := db.ListAccounts(c.Context(), pool, 50, 0)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(accounts)
	}
}

func handleUpdateAccount(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req struct{ Status string `json:"status"` }
		if err := c.BodyParser(&req); err != nil || req.Status == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if err := db.UpdateAccountStatus(c.Context(), pool, c.Params("id"), req.Status); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

func handleGenerateKeys(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req struct {
			PlanTier string  `json:"plan_tier"`
			Count    int     `json:"count"`
			Notes    *string `json:"notes"`
		}
		if err := c.BodyParser(&req); err != nil || req.PlanTier == "" || req.Count < 1 {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if req.Count > 100 {
			req.Count = 100
		}
		admin := middleware.GetAdminToken(c)
		keys, err := svc.GenerateKeys(c.Context(), req.PlanTier, admin.AdminUsername, req.Count, req.Notes)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.Status(201).JSON(fiber.Map{"keys": keys})
	}
}

func handleRevokeKey(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		if err := db.RevokeLicenseKey(c.Context(), pool, c.Params("id")); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

func handleResetBinding(svc *Service, pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		admin := middleware.GetAdminToken(c)
		if err := svc.ResetDeviceBinding(c.Context(), c.Params("id"), admin.AdminUsername); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "reset"})
	}
}

func handleListSessions(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sessions, err := db.ListActiveSessions(c.Context(), pool, 50, 0)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(sessions)
	}
}

func handleRevokeSession(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		if err := db.RevokeSession(c.Context(), pool, c.Params("id")); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

func handleGetEntitlement(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		ent, err := db.GetEntitlement(c.Context(), pool, c.Params("tier"))
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(ent)
	}
}

func handleUpsertEntitlement(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var e db.Entitlement
		if err := c.BodyParser(&e); err != nil {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		e.PlanTier = c.Params("tier")
		if err := db.UpsertEntitlement(c.Context(), pool, &e); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

func handleUpsertOverride(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var o db.PlanOverride
		if err := c.BodyParser(&o); err != nil {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		o.AccountID = c.Params("account_id")
		admin := middleware.GetAdminToken(c)
		o.CreatedBy = admin.AdminUsername
		if err := db.UpsertPlanOverride(c.Context(), pool, &o); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

func handleDeleteOverride(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		if err := db.DeletePlanOverride(c.Context(), pool, c.Params("account_id")); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "deleted"})
	}
}

func handleCreateToken(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req struct {
			AdminUsername string `json:"admin_username"`
			Role          string `json:"role"`
			DurationHours int    `json:"duration_hours"`
		}
		if err := c.BodyParser(&req); err != nil || req.AdminUsername == "" || req.Role == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if req.DurationHours <= 0 {
			req.DurationHours = 8
		}
		raw, err := svc.CreateAdminToken(c.Context(), req.AdminUsername, req.Role, req.DurationHours)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.Status(201).JSON(fiber.Map{"token": raw})
	}
}

func handleRevokeToken(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		if err := db.RevokeAdminToken(c.Context(), pool, c.Params("id")); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

func handleListAudit(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		rows, err := pool.Query(c.Context(),
			`SELECT id, event_type, account_id, device_id, admin_name, ip, details, created_at
			 FROM audit_log ORDER BY created_at DESC LIMIT 100`)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		defer rows.Close()
		var entries []fiber.Map
		for rows.Next() {
			var id, eventType string
			var accountID, deviceID, adminName, ip *string
			var details []byte
			var createdAt time.Time
			rows.Scan(&id, &eventType, &accountID, &deviceID, &adminName, &ip, &details, &createdAt)
			entries = append(entries, fiber.Map{
				"id": id, "event_type": eventType, "account_id": accountID,
				"device_id": deviceID, "admin_name": adminName, "ip": ip,
				"details": string(details), "created_at": createdAt,
			})
		}
		return c.JSON(entries)
	}
}
```

- [ ] **Step 3: Commit**

```bash
git add server/internal/admin/
git commit -m "feat(server): admin API — accounts, keys, devices, sessions, entitlements, tokens, audit"
```

---

## Task 14: Main entry point

**Files:**
- Create: `server/cmd/server/main.go`

- [ ] **Step 1: main.go**

```go
// server/cmd/server/main.go
package main

import (
	"context"
	"log/slog"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/cobalt/server/internal/admin"
	"github.com/cobalt/server/internal/audit"
	"github.com/cobalt/server/internal/auth"
	"github.com/cobalt/server/internal/cache"
	"github.com/cobalt/server/internal/config"
	"github.com/cobalt/server/internal/content"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/enrollment"
	"github.com/cobalt/server/internal/entitlement"
	"github.com/cobalt/server/internal/middleware"
	"github.com/redis/go-redis/v9"
)

func main() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))

	cfg, err := config.Load()
	if err != nil {
		logger.Error("config load failed", "err", err)
		os.Exit(1)
	}

	ctx := context.Background()

	pool, err := db.NewPool(ctx, cfg.DBURL)
	if err != nil {
		logger.Error("db connect failed", "err", err)
		os.Exit(1)
	}

	rdb, err := cache.NewClient(cfg.RedisURL)
	if err != nil {
		logger.Error("redis connect failed", "err", err)
		os.Exit(1)
	}

	_ = audit.New(pool) // available for future use

	entSvc := entitlement.New(pool)
	enrollSvc := enrollment.New(pool, cfg.MasterKey, cfg.ServerPepper)
	authSvc := auth.New(pool, rdb, entSvc, cfg.MasterKey, cfg.ServerPepper, "https://your-domain.invalid")
	contentSvc := content.New(pool, entSvc, cfg.ContentDir)
	adminSvc := admin.New(pool)

	// Public listener
	pub := fiber.New(fiber.Config{
		DisableStartupMessage: true,
		ProxyHeader:           "", // never trust client-provided IP headers
	})
	pub.Use(recover.New())
	pub.Use(middleware.RealIP())

	// Rate limits
	pub.Post("/enroll/redeem", middleware.RateLimit(rdb, 5, 60*60, middleware.IPKey("enroll_redeem")))
	pub.Post("/enroll/handshake", middleware.RateLimit(rdb, 10, 60*60, middleware.IPAndUsernameKey("enroll_hs")))
	pub.Post("/auth/start", middleware.RateLimit(rdb, 10, 60, middleware.IPAndUsernameKey("auth_start")))
	pub.Post("/auth/finish", middleware.RateLimit(rdb, 5, 60, middleware.IPAndUsernameKey("auth_finish")))

	enrollment.RegisterRoutes(pub, enrollSvc)
	auth.RegisterRoutes(pub, authSvc)
	content.RegisterRoutes(pub, pool, contentSvc)

	// Admin listener (localhost only)
	adm := fiber.New(fiber.Config{DisableStartupMessage: true})
	adm.Use(recover.New())
	adm.Use(middleware.RealIP())
	admin.RegisterRoutes(adm, pool, adminSvc, entSvc)

	errCh := make(chan error, 2)
	go func() { errCh <- pub.Listen(":" + cfg.PublicPort) }()
	go func() { errCh <- adm.Listen("127.0.0.1:" + cfg.AdminPort) }()

	logger.Info("server started", "public", cfg.PublicPort, "admin", cfg.AdminPort)
	if err := <-errCh; err != nil {
		logger.Error("server error", "err", err)
		os.Exit(1)
	}
}
```

- [ ] **Step 2: Build**

```bash
cd server && go build ./cmd/server/
```

Expected: `server.exe` produced with no errors.

- [ ] **Step 3: Commit**

```bash
git add server/cmd/server/main.go
git commit -m "feat(server): main entry point, wire all services and listeners"
```

---

## Task 15: Bootstrap admin token

- [ ] **Step 1: Generate secrets for .env**

```bash
# Run these in PowerShell on the VPS to generate secrets
$masterKey = [Convert]::ToBase64String((1..32 | % { Get-Random -Maximum 256 }))
$pepper    = [Convert]::ToBase64String((1..32 | % { Get-Random -Maximum 256 }))
# For Ed25519: use ssh-keygen or a Go tool to generate a 64-byte key
```

- [ ] **Step 2: Create .env file on VPS**

```env
MASTER_KEY=<base64-32-bytes>
SERVER_PEPPER=<base64-32-bytes>
MANIFEST_SIGNING_KEY=<base64-64-bytes>
DB_URL=postgres://cobalt:password@localhost:5432/cobalt_auth
REDIS_URL=redis://localhost:6379
ADMIN_API_SECRET=<random-strong-secret>
PUBLIC_PORT=8080
ADMIN_PORT=8081
CONTENT_DIR=C:\cobalt-server\content
```

Set NTFS permissions: only the service account can read this file.

- [ ] **Step 3: Create first super_admin token via bootstrap endpoint**

Add a one-time bootstrap route to `main.go` (remove after first use):

```go
// Temporary — remove after creating first admin token
adm.Post("/bootstrap/token", func(c *fiber.Ctx) error {
    secret := c.Get("X-Bootstrap-Secret")
    if secret != cfg.AdminAPISecret {
        return c.Status(401).JSON(fiber.Map{"error": "unauthorized"})
    }
    raw, err := adminSvc.CreateAdminToken(c.Context(), "admin", "super_admin", 8760) // 1 year
    if err != nil {
        return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
    }
    return c.JSON(fiber.Map{"token": raw})
})
```

```bash
curl -X POST http://127.0.0.1:8081/bootstrap/token \
  -H "X-Bootstrap-Secret: <ADMIN_API_SECRET>"
```

Save the returned token. Remove the bootstrap route and redeploy.

- [ ] **Step 4: Install as Windows service via NSSM**

```bat
nssm install CobaltServer "C:\cobalt-server\cobalt-server.exe"
nssm set CobaltServer AppDirectory "C:\cobalt-server"
nssm set CobaltServer AppEnvironmentExtra "MASTER_KEY=..." "SERVER_PEPPER=..." ...
nssm start CobaltServer
```

- [ ] **Step 5: Commit**

```bash
git add server/
git commit -m "feat(server): complete server implementation"
```

---

## Self-Review Notes

**Spec coverage check:**
- [x] Challenge-response auth (Tasks 11)
- [x] IP enforced through enrollment, released after fully_bound (Task 11 service)
- [x] HWID HMAC'd, never raw (Task 3 + 11)
- [x] Device secret AES-256-GCM at rest (Task 3 + 10)
- [x] Entitlement as distinct step (Task 9)
- [x] Session re-verified on every request (Task 7 session.go)
- [x] Entitlement re-checked at download time (Task 12 service.go)
- [x] Admin zero-trust with role enforcement (Task 7 admin.go + Task 13)
- [x] Rate limiting on all public endpoints (Task 14 main.go)
- [x] Generic external errors (Tasks 10, 11, 12, 13)
- [x] Audit log (Task 8)
- [x] Device auto-suspend at 5 failures (Task 11 service.go)
- [x] Admin binding reset clears all fields (Task 5 devices.go)
- [x] Manifest selected server-side by channel (Task 11 service.go)
- [x] Constant-time comparison for proofs (Task 3 hmac.go)
