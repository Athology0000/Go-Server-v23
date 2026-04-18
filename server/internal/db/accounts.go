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
