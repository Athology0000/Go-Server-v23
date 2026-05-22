#!/usr/bin/env bash
set -euo pipefail

: "${DATABASE_URL:?DATABASE_URL is required}"
: "${MIGRATIONS_DIR:=/migrations}"

echo "Waiting for postgres..."
until psql "$DATABASE_URL" -c '\q' 2>/dev/null; do
  sleep 1
done
echo "Postgres is ready."

psql "$DATABASE_URL" <<'SQL'
CREATE TABLE IF NOT EXISTS schema_migrations (
    filename TEXT PRIMARY KEY,
    applied_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
SQL

for f in $(ls "$MIGRATIONS_DIR"/*.sql | sort); do
  name=$(basename "$f")
  exists=$(psql "$DATABASE_URL" -t -c "SELECT 1 FROM schema_migrations WHERE filename = '$name'" | tr -d '[:space:]')
  if [ "$exists" = "1" ]; then
    echo "Skipping $name (already applied)"
  else
    echo "Applying $name..."
    psql "$DATABASE_URL" -f "$f"
    psql "$DATABASE_URL" -c "INSERT INTO schema_migrations (filename) VALUES ('$name')"
    echo "Applied $name"
  fi
done

echo "Migrations complete."
