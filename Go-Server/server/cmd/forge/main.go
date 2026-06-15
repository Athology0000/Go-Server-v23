// Command phantom-forge is the server-side CLI that forges a delivered payload (raw .jar +
// locally-built .dll) into a production-ready, watermarked, approval-gated module build.
//
// Usage:
//
//	phantom-forge build  --name <module> --in-jar <path> --in-dll <path> [--by <operator>]
//	phantom-forge status [--id <buildID>] [--status <state>]
//
// `build` records a content_builds row and runs the obfuscation+watermark pipeline, leaving
// the result in 'pending_approval' for a superadmin to approve. Run it detached (e.g. `&`)
// for background operation; progress is tracked in the DB and surfaced on the admin panel.
//
// Config (env): DB_URL, FORGE_OBFUSCATOR_JAR, FORGE_CONFIG, FORGE_SECRET, FORGE_STAGING_DIR,
// and optional FORGE_JAVA (default "java").
package main

import (
	"context"
	"flag"
	"fmt"
	"os"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/forge"
)

func main() {
	if len(os.Args) < 2 {
		usage()
		os.Exit(2)
	}
	switch os.Args[1] {
	case "build":
		cmdBuild(os.Args[2:])
	case "status":
		cmdStatus(os.Args[2:])
	case "-h", "--help", "help":
		usage()
	default:
		fmt.Fprintf(os.Stderr, "unknown command %q\n\n", os.Args[1])
		usage()
		os.Exit(2)
	}
}

func usage() {
	fmt.Fprint(os.Stderr, `phantom-forge — forge a delivered payload into a prod-ready module build

  phantom-forge build  --name <module> --in-jar <path> --in-dll <path> [--by <operator>]
  phantom-forge status [--id <buildID>] [--status <state>]

env: DB_URL, FORGE_OBFUSCATOR_JAR, FORGE_CONFIG, FORGE_SECRET, FORGE_STAGING_DIR, [FORGE_JAVA]
`)
}

func cmdBuild(args []string) {
	fs := flag.NewFlagSet("build", flag.ExitOnError)
	name := fs.String("name", "", "module name (e.g. phantom-autowalk)")
	inJar := fs.String("in-jar", "", "path to the raw payload .jar")
	inDll := fs.String("in-dll", "", "path to the locally-built .dll")
	by := fs.String("by", "", "operator label recorded on the build (optional)")
	_ = fs.Parse(args)

	if *name == "" || *inJar == "" || *inDll == "" {
		fmt.Fprintln(os.Stderr, "build requires --name, --in-jar and --in-dll")
		os.Exit(2)
	}

	f := forgeFromEnv()
	if !f.Enabled() {
		fatal("forge not configured: set FORGE_OBFUSCATOR_JAR, FORGE_CONFIG, FORGE_SECRET, FORGE_STAGING_DIR")
	}

	jarBytes, err := os.ReadFile(*inJar)
	if err != nil {
		fatal("read --in-jar: %v", err)
	}
	dllBytes, err := os.ReadFile(*inDll)
	if err != nil {
		fatal("read --in-dll: %v", err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), 15*time.Minute)
	defer cancel()

	pool := mustPool(ctx)
	defer pool.Close()

	buildID, err := forge.NewBuildID()
	if err != nil {
		fatal("generate build id: %v", err)
	}
	var createdBy *string
	if *by != "" {
		createdBy = by
	}
	if _, err := db.CreateBuild(ctx, pool, buildID, *name, createdBy); err != nil {
		fatal("record build: %v", err)
	}
	// Print the id up front so a backgrounded run is traceable before the obfuscation finishes.
	fmt.Printf("build %s started (module=%s, status=building)\n", buildID, *name)

	res, err := f.Build(ctx, *name, buildID, jarBytes, dllBytes)
	if err != nil {
		_ = db.MarkBuildFailed(ctx, pool, buildID, err.Error())
		fatal("forge build %s failed: %v", buildID, err)
	}

	if _, err := db.MarkBuildPending(ctx, pool, buildID, res.JarPath, res.JarSHA256, res.DLLPath, res.DLLSHA256); err != nil {
		fatal("record pending build: %v", err)
	}

	fmt.Printf("build %s ready for approval\n", buildID)
	fmt.Printf("  module : %s\n", res.Module)
	fmt.Printf("  jar    : %s  sha256=%s\n", res.JarPath, res.JarSHA256)
	fmt.Printf("  dll    : %s  sha256=%s\n", res.DLLPath, res.DLLSHA256)
	fmt.Printf("  status : pending_approval (awaiting superadmin approve/deny)\n")
}

func cmdStatus(args []string) {
	fs := flag.NewFlagSet("status", flag.ExitOnError)
	id := fs.String("id", "", "build id to look up")
	status := fs.String("status", "", "list builds in this status (default pending_approval)")
	_ = fs.Parse(args)

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	pool := mustPool(ctx)
	defer pool.Close()

	if *id != "" {
		b, err := db.GetBuildByBuildID(ctx, pool, *id)
		if err != nil {
			fatal("get build: %v", err)
		}
		printBuild(b)
		return
	}

	state := *status
	if state == "" {
		state = "pending_approval"
	}
	list, err := db.ListBuildsByStatus(ctx, pool, state, 100)
	if err != nil {
		fatal("list builds: %v", err)
	}
	if len(list) == 0 {
		fmt.Printf("no builds in status %q\n", state)
		return
	}
	fmt.Printf("%d build(s) in status %q:\n", len(list), state)
	for _, b := range list {
		printBuild(b)
	}
}

func printBuild(b *db.ContentBuild) {
	fmt.Printf("- %s  module=%s  status=%s  created=%s\n",
		b.BuildID, b.Module, b.Status, b.CreatedAt.Format(time.RFC3339))
	if b.Error != nil && *b.Error != "" {
		fmt.Printf("    error: %s\n", *b.Error)
	}
}

func forgeFromEnv() *forge.Forge {
	return &forge.Forge{
		JavaPath:   envOr("FORGE_JAVA", "java"),
		ObfJar:     os.Getenv("FORGE_OBFUSCATOR_JAR"),
		ConfigPath: os.Getenv("FORGE_CONFIG"),
		Secret:     os.Getenv("FORGE_SECRET"),
		StagingDir: os.Getenv("FORGE_STAGING_DIR"),
	}
}

func mustPool(ctx context.Context) *pgxpool.Pool {
	dbURL := os.Getenv("DB_URL")
	if dbURL == "" {
		fatal("DB_URL is not set")
	}
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		fatal("connect db: %v", err)
	}
	return pool
}

func envOr(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}

func fatal(format string, a ...any) {
	fmt.Fprintf(os.Stderr, format+"\n", a...)
	os.Exit(1)
}
