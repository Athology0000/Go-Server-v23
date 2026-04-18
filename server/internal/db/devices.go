package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Device struct {
	ID                    string
	AccountID             string
	BindingStatus         string
	HWIDHash              *string
	MinecraftUsername     *string
	EnrollmentIP          *string
	DeviceSecretEncrypted []byte
	FailedAttempts        int
	LastSeenIP            *string
	LastLoginAt           *time.Time
	BindingResetAt        *time.Time
	BindingResetBy        *string
	CreatedAt             time.Time
	UpdatedAt             time.Time
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
