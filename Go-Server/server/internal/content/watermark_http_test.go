package content

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"io"
	"net/http/httptest"
	"os"
	"path/filepath"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
	"github.com/phantom/server/internal/middleware"
)

// TestWatermarkModuleDownloadHTTP proves the real downloads-page path: an authorized user hits
// GET /content/module/:name and gets back a .jar stamped with *their* account id. It drives the
// genuine chain — SessionAuth (real DB session lookup) -> handleModule -> ModuleBytes ->
// entitlement.Resolve (real license/entitlement) -> Watermarker.Apply -> HTTP response bytes —
// then extracts the mark from the response body and asserts it equals the downloader's account id.
//
// Requires a live Postgres with migrations applied. Point WATERMARK_IT_DB_URL at it
// (e.g. the docker-compose stack: postgres://phantom:phantom@localhost:5432/phantom?sslmode=disable).
// Skips cleanly when the DB url is unset or the obfuscator artifacts are missing.
func TestWatermarkModuleDownloadHTTP(t *testing.T) {
	dbURL := os.Getenv("WATERMARK_IT_DB_URL")
	if dbURL == "" {
		t.Skip("WATERMARK_IT_DB_URL unset; skipping HTTP downloads-page integration test")
	}

	wm, jarBytes := newTestWatermarker(t) // skips if java / obfuscator jar / sample jar are missing

	ctx, cancel := context.WithTimeout(context.Background(), 4*time.Minute)
	defer cancel()

	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		t.Fatalf("connect postgres: %v", err)
	}
	defer pool.Close()
	if err := pool.Ping(ctx); err != nil {
		t.Skipf("postgres not reachable at WATERMARK_IT_DB_URL (%v); skipping", err)
	}

	// --- seed an authorized user: account -> device -> license(pro) -> session ---
	suffix := randSuffix(t)
	username := "wm-it-" + suffix

	account, err := db.CreateAccount(ctx, pool, username, "x-not-a-real-hash", nil)
	if err != nil {
		t.Fatalf("create account: %v", err)
	}
	t.Cleanup(func() { cleanupAccount(pool, account.ID) })

	device, err := db.CreateDevice(ctx, pool, account.ID, "127.0.0.1", []byte{0})
	if err != nil {
		t.Fatalf("create device: %v", err)
	}

	expiry := time.Now().Add(24 * time.Hour)
	if _, err := db.CreateLicense(ctx, pool, account.ID, "pro", time.Now(), &expiry); err != nil {
		t.Fatalf("create license: %v", err)
	}

	rawToken, tokenHash, err := crypto.GenerateToken()
	if err != nil {
		t.Fatalf("generate token: %v", err)
	}
	sessExpiry := time.Now().Add(1 * time.Hour)
	if _, err := db.CreateSession(ctx, pool, tokenHash, device.ID, account.ID,
		"pro", []string{"*"}, []string{"*"}, &expiry, sessExpiry, "127.0.0.1"); err != nil {
		t.Fatalf("create session: %v", err)
	}

	// --- stage a downloadable .jar module the user is entitled to ---
	contentDir := t.TempDir()
	modDir := filepath.Join(contentDir, "modules")
	if err := os.MkdirAll(modDir, 0o755); err != nil {
		t.Fatal(err)
	}
	const moduleName = "phantom-wmtest"
	if err := os.WriteFile(filepath.Join(modDir, moduleName+".jar"), jarBytes, 0o644); err != nil {
		t.Fatal(err)
	}

	// --- build the real content service + watermarker, mounted on the real route+middleware ---
	entSvc := entitlement.New(pool)
	svc := New(pool, entSvc, contentDir, []byte("sign-key-unused-here"), []byte("module-key-unused-here"), "http://localhost:8080")
	svc.SetWatermarker(wm)

	app := fiber.New()
	app.Get("/content/module/:name", middleware.SessionAuth(pool, false), handleModule(svc))

	req := httptest.NewRequest("GET", "/content/module/"+moduleName, nil)
	req.Header.Set("Authorization", "Bearer "+rawToken)

	// Generous timeout: the handler cold-starts a JVM to embed the watermark.
	resp, err := app.Test(req, 240000)
	if err != nil {
		t.Fatalf("app.Test: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != fiber.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		t.Fatalf("GET /content/module/%s: status=%d body=%s", moduleName, resp.StatusCode, string(body))
	}
	if ct := resp.Header.Get("Content-Type"); ct != "application/java-archive" {
		t.Errorf("unexpected Content-Type %q, want application/java-archive", ct)
	}

	served, err := io.ReadAll(resp.Body)
	if err != nil {
		t.Fatalf("read response body: %v", err)
	}
	if len(served) == 0 {
		t.Fatal("server returned an empty jar")
	}

	// The served jar must NOT be byte-identical to the source: it carries this user's mark.
	if len(served) == len(jarBytes) {
		identical := true
		for i := range served {
			if served[i] != jarBytes[i] {
				identical = false
				break
			}
		}
		if identical {
			t.Fatal("served jar is byte-identical to the source — watermark was not applied")
		}
	}

	// The mark recovered from the downloaded bytes must be exactly this account's id.
	got, err := wm.ExtractWatermark(ctx, served)
	if err != nil {
		t.Fatalf("extract watermark from downloaded jar: %v", err)
	}
	if got != account.ID {
		t.Fatalf("downloaded jar watermark = %q, want account id %q", got, account.ID)
	}
	t.Logf("downloads-page proof OK: account %s received a jar watermarked with its own id", account.ID)
}

func randSuffix(t *testing.T) string {
	t.Helper()
	b := make([]byte, 6)
	if _, err := rand.Read(b); err != nil {
		t.Fatalf("rand: %v", err)
	}
	return hex.EncodeToString(b)
}

// cleanupAccount removes the seeded rows in FK-safe order so repeated runs stay clean.
func cleanupAccount(pool *pgxpool.Pool, accountID string) {
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	for _, q := range []string{
		`DELETE FROM sessions WHERE account_id = $1`,
		`DELETE FROM plan_overrides WHERE account_id = $1`,
		`DELETE FROM licenses WHERE account_id = $1`,
		`DELETE FROM devices WHERE account_id = $1`,
		`DELETE FROM accounts WHERE id = $1`,
	} {
		_, _ = pool.Exec(ctx, q, accountID)
	}
}
