mod auth;
mod client;
mod config;
mod credentials;
mod enrollment;
mod hwid;
mod jar;
mod launch;
mod manifest;
mod session;

fn main() {
    println!("Cobalt Bootstrapper v{}", env!("CARGO_PKG_VERSION"));

    let cfg = config::BootstrapConfig::load();
    let http = client::build(&cfg.server_url);
    let hwid = hwid::collect();

    // ── Enrollment (first run only) ───────────────────────────────────────────
    if !credentials::exists() {
        match enrollment::run(&http, &cfg.server_url, &hwid) {
            Ok(result) => {
                if let Err(e) = credentials::save(&result.username, &result.device_secret) {
                    fatal(&format!("Failed to save credentials: {e}"));
                }
            }
            Err(e) => fatal(&format!("Enrollment failed: {e}")),
        }
    }

    // ── Load credentials ──────────────────────────────────────────────────────
    let creds = credentials::load()
        .unwrap_or_else(|e| fatal(&format!("Failed to load credentials: {e}")));

    // ── Authenticate ──────────────────────────────────────────────────────────
    println!("Authenticating {}...", creds.username);
    let auth_result = auth::authenticate(
        &http,
        &cfg.server_url,
        &creds.username,
        &hwid,
        &creds.device_secret,
        &cfg.client_version,
        &cfg.build_id,
    ).unwrap_or_else(|e| fatal(&format!("Authentication failed: {e}")));

    println!("Authenticated.");

    // ── Fetch + verify manifest ───────────────────────────────────────────────
    let cobalt_manifest = if !auth_result.manifest_url.is_empty() {
        println!("Verifying content manifest...");
        manifest::fetch_and_verify(
            &http,
            &auth_result.manifest_url,
            &auth_result.session_token,
            &auth_result.manifest_signature,
        ).unwrap_or_else(|e| fatal(&format!("Manifest verification failed: {e}")))
    } else {
        fatal("No manifest URL in auth response");
    };

    // ── Ensure cobalt.jar is present and valid ────────────────────────────────
    let cobalt_module = cobalt_manifest.modules.iter()
        .find(|m| m.name == "cobalt")
        .unwrap_or_else(|| fatal("Manifest contains no 'cobalt' module"));

    println!("Verifying cobalt.jar...");
    let jar_path = jar::ensure(
        &http,
        &cfg.server_url,
        &auth_result.session_token,
        &cobalt_module.sha256,
    ).unwrap_or_else(|e| fatal(&format!("JAR verification failed: {e}")));

    // ── Write session file ────────────────────────────────────────────────────
    let session_path = session::write(&auth_result.session_token)
        .unwrap_or_else(|e| fatal(&format!("Failed to write session file: {e}")));

    println!("Launching Minecraft...");

    // ── Launch Minecraft ──────────────────────────────────────────────────────
    let exit_code = launch::run(&launch::LaunchConfig {
        java_exe: &cfg.java_exe,
        game_dir: &cfg.game_dir,
        mods_dir: &cfg.mods_dir,
        session_file: &session_path,
        jar_path: &jar_path,
    });

    // ── Cleanup ───────────────────────────────────────────────────────────────
    session::delete();

    match exit_code {
        Ok(code) => std::process::exit(code),
        Err(e) => fatal(&format!("Launch failed: {e}")),
    }
}

fn fatal(msg: &str) -> ! {
    eprintln!("\n[ERROR] {msg}");
    eprintln!("Press Enter to exit...");
    let mut buf = String::new();
    let _ = std::io::stdin().read_line(&mut buf);
    std::process::exit(1);
}
