package db

import (
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
	"time"
)

type Device struct {
	ID                    string     `json:"id"`
	AccountID             string     `json:"account_id"`
	BindingStatus         string     `json:"binding_status"`
	HWIDHash              *string    `json:"-"`
	MinecraftUsername     *string    `json:"minecraft_username"`
	EnrollmentIP          *string    `json:"enrollment_ip"`
	DeviceSecretEncrypted []byte     `json:"-"`
	FailedAttempts        int        `json:"failed_attempts"`
	LastSeenIP            *string    `json:"last_seen_ip"`
	LastLoginAt           *time.Time `json:"last_login_at"`
	BindingResetAt        *time.Time `json:"binding_reset_at"`
	BindingResetBy        *string    `json:"binding_reset_by"`
	CreatedAt             time.Time  `json:"created_at"`
	UpdatedAt             time.Time  `json:"updated_at"`
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

// MarkEnrolled moves a device from unbound to hwid_pending without touching hwid_hash.
// Called by enrollment after the device row exists and device_secret is set.
func MarkEnrolled(ctx context.Context, pool *pgxpool.Pool, deviceID string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $1`, deviceID)
	return err
}

// PinHWID records a device's HWID hash (trust-on-first-use) without changing binding_status.
// Used by /auth/verify-session when HWID TOFU is enabled and the device has no HWID yet.
func PinHWID(ctx context.Context, pool *pgxpool.Pool, deviceID, hwidHash string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET hwid_hash = $1, updated_at = now() WHERE id = $2`, hwidHash, deviceID)
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
