mod auth;
mod client;
mod config;
mod credentials;
mod enrollment;
mod heartbeat;
mod jar;
mod launch;
mod manifest;
mod prism;
mod session;

use chrono::{DateTime, Utc};
use std::env;
use std::process;

// Colors
const RESET: &str = "\x1b[0m";
const BOLD: &str = "\x1b[1m";
const DIM: &str = "\x1b[2m";

const PURPLE: &str = "\x1b[38;5;135m";
const PURPLE_BRIGHT: &str = "\x1b[38;5;177m";
const PURPLE_DARK: &str = "\x1b[38;5;54m";

const WHITE: &str = "\x1b[97m";
const GREEN: &str = "\x1b[38;5;82m";
const RED: &str = "\x1b[38;5;196m";
const YELLOW: &str = "\x1b[38;5;220m";

fn main() {
    #[cfg(windows)]
    {
        let _ = enable_ansi_support();
    }

    // Basic integrity check
    if let Err(e) = verify_integrity() {
        eprintln!("{}INTEGRITY CHECK FAILED: {}{}", RED, e, RESET);
        eprintln!("This executable may have been tampered with.");
        process::exit(1);
    }

    banner();

    let cfg = config::BootstrapConfig::load();

    info(&format!("server: {}", cfg.server_url));
    info(&format!("game dir: {}", cfg.game_dir));
    info(&format!("mods dir: {}", cfg.mods_dir));

    let http = client::build(&cfg.server_url)
        .unwrap_or_else(|e| fatal(&format!("Failed to create HTTP client: {e}")));

    let first_time_use = !credentials::exists();

    if first_time_use {
        divider();
        println!("  {}{}FIRST-TIME SETUP{}", BOLD, PURPLE_BRIGHT, RESET);
        println!("  {}This will bind this loader to your Phantom account.{}", DIM, RESET);
        println!("  {}After auth, the starter jar will be downloaded.{}", DIM, RESET);
        divider();
        println!();

        match enrollment::run(&http, &cfg.server_url) {
            Ok(result) => {
                if let Err(e) = credentials::save(&result.username, &result.device_secret) {
                    fatal(&format!("Failed to save credentials: {e}"));
                }

                println!();
                ok("Enrollment complete, credentials saved");
            }
            Err(e) => fatal(&format!("Enrollment failed: {e}")),
        }
    }

    divider();

    let creds = credentials::load()
        .unwrap_or_else(|e| fatal(&format!("Failed to load credentials: {e}")));

    println!();
    step(1, "Authenticating");
    info(&format!("user: {}{}{}", BOLD, creds.username, RESET));

    let auth_result = auth::authenticate(
        &http,
        &cfg.server_url,
        &creds.username,
        &creds.device_secret,
        &cfg.client_version,
        &cfg.build_id,
    )
    .unwrap_or_else(|e| fatal(&format!("Authentication failed: {e}")));

    ok("Authenticated");
    info(&format!("tier: {}{}{}", BOLD, auth_result.plan_tier, RESET));

    match format_key_time_left(auth_result.entitlement_expires_at.as_deref()) {
        Some(text) => info(&format!("key time left: {text}")),
        None => info("key time left: lifetime / no expiry"),
    }

    println!();
    step(2, "Verifying starter manifest");

    let phantom_manifest = if !auth_result.manifest_url.is_empty() {
        let m = manifest::fetch_and_verify(
            &http,
            &auth_result.manifest_url,
            &auth_result.session_token,
            &auth_result.manifest_signature,
        )
        .unwrap_or_else(|e| fatal(&format!("Manifest verification failed: {e}")));

        ok(&format!(
            "Manifest verified  id: {}  build: {}",
            m.id, m.build_id
        ));

        m
    } else {
        fatal("No manifest URL in auth response");
    };

    let phantom_module = phantom_manifest
        .modules
        .iter()
        .find(|m| m.name == "phantom")
        .unwrap_or_else(|| fatal("Manifest contains no 'phantom' module"));

    println!();

    if first_time_use {
        step(3, "Installing starter jar");
        info("first-time use detected");
        info("downloading only the starter jar");
        info("pro jar downloads are blocked by the bootstrapper");
    } else {
        step(3, "Verifying starter jar");
    }

    let jar_path = jar::ensure(
        &http,
        &cfg.server_url,
        &auth_result.session_token,
        &phantom_module.sha256,
        &cfg.mods_dir,
    )
    .unwrap_or_else(|e| fatal(&format!("Starter jar verification failed: {e}")));

    ok(&format!("Starter jar ready: {}", jar_path.display()));

    println!();
    step(4, "Writing Phantom session");

    let session_path = session::write(&auth_result.session_token, &cfg.game_dir)
        .unwrap_or_else(|e| fatal(&format!("Failed to write session file: {e}")));

    ok(&format!("Session file written: {}", session_path.display()));

    // The session file lives just long enough for phantom.jar to read it on
    // Minecraft startup. After the JVM exits we wipe it from disk so it can't be
    // copy-replayed offline. Set PHANTOM_KEEP_SESSION_TOKEN=1 to skip cleanup
    // (debugging only — the token is the gate to /content/* downloads).
    let keep_session = env::var("PHANTOM_KEEP_SESSION_TOKEN")
        .map(|v| !v.is_empty() && v != "0")
        .unwrap_or(false);

    if keep_session {
        warn("PHANTOM_KEEP_SESSION_TOKEN set; session.token will NOT be cleaned up after exit.");
    }

    println!();
    step(5, "Starting heartbeat monitor");

    heartbeat::start_heartbeat(&http, &cfg.server_url, &auth_result.session_token, &cfg.game_dir);

    ok("Heartbeat monitor started");

    println!();
    divider();
    println!("  {}{}> Launching Minecraft directly...{}", BOLD, PURPLE_BRIGHT, RESET);
    divider();
    println!();

    let java_exe = prism::find_java_exe()
        .unwrap_or_else(|e| fatal(&format!("Failed to find Java: {e}")));

    println!("Using Java: {}", java_exe);

    let launch_config = launch::LaunchConfig {
        java_exe: &java_exe,
        game_dir: &cfg.game_dir,
        mods_dir: &cfg.mods_dir,
        session_file: &session_path,
        jar_path: &jar_path,
    };

    let exit_code = launch::run(&launch_config);

    // Always wipe the session token from disk after Minecraft exits. By this point
    // phantom.jar has already read it during startup and stage-2 auth is complete;
    // the on-disk copy is no longer needed and is a replay-attack surface.
    if !keep_session {
        session::delete(&cfg.game_dir);
    }

    match exit_code {
        Ok(code) => {
            if code != 0 {
                pause_before_exit(&format!("Minecraft exited with code {code}"));
                std::process::exit(code);
            }

            pause_before_exit("Minecraft finished. Phantom authentication should be complete.");
            std::process::exit(0);
        }
        Err(e) => fatal(&format!("Launch failed: {e}")),
    }
}

// Banner / theme

fn banner() {
    println!();
    purple_line();

    println!("{}{}  ██████╗ ██╗  ██╗ █████╗ ███╗   ██╗████████╗ ██████╗ ███╗   ███╗{}", BOLD, PURPLE_BRIGHT, RESET);
    println!("{}{}  ██╔══██╗██║  ██║██╔══██╗████╗  ██║╚══██╔══╝██╔═══██╗████╗ ████║{}", BOLD, PURPLE_BRIGHT, RESET);
    println!("{}{}  ██████╔╝███████║███████║██╔██╗ ██║   ██║   ██║   ██║██╔████╔██║{}", BOLD, PURPLE, RESET);
    println!("{}{}  ██╔═══╝ ██╔══██║██╔══██║██║╚██╗██║   ██║   ██║   ██║██║╚██╔╝██║{}", BOLD, PURPLE, RESET);
    println!("{}{}  ██║     ██║  ██║██║  ██║██║ ╚████║   ██║   ╚██████╔╝██║ ╚═╝ ██║{}", BOLD, PURPLE_DARK, RESET);
    println!("{}{}  ╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝     ╚═╝{}", BOLD, PURPLE_DARK, RESET);

    println!();
    println!(
        "  {}{}Bootstrapper v{}{}  {}secure starter loader{}",
        BOLD,
        WHITE,
        env!("CARGO_PKG_VERSION"),
        RESET,
        DIM,
        RESET
    );

    purple_line();
    println!();
}

fn purple_line() {
    println!(
        "  {}{}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━{}",
        DIM, PURPLE, RESET
    );
}

fn divider() {
    purple_line();
}

fn step(n: u8, label: &str) {
    println!(
        "  {}[{}{}{}{}]{} {}{}{}",
        DIM, RESET, PURPLE_BRIGHT, n, DIM, RESET, BOLD, label, RESET
    );
}

fn ok(msg: &str) {
    println!("  {}[OK]{} {}{}{}", GREEN, RESET, WHITE, msg, RESET);
}

fn info(msg: &str) {
    println!("  {}-{} {}{}", PURPLE_BRIGHT, RESET, msg, RESET);
}

fn warn(msg: &str) {
    println!("  {}[WARN]{} {}{}", YELLOW, RESET, msg, RESET);
}

fn fatal(msg: &str) -> ! {
    println!();
    println!("  {}{}[ERROR]{}", BOLD, RED, RESET);
    println!("  {}{}{}", RED, msg, RESET);
    println!();

    pause_before_exit("Bootstrapper stopped because of an error.");
    std::process::exit(1);
}

fn pause_before_exit(msg: &str) {
    println!();
    println!("  {}{}{}", DIM, msg, RESET);
    println!("  {}Press Enter to exit...{}", DIM, RESET);

    let mut buf = String::new();
    let _ = std::io::stdin().read_line(&mut buf);
}

// Time formatting

fn format_key_time_left(expires_at: Option<&str>) -> Option<String> {
    let raw = expires_at?.trim();

    if raw.is_empty() {
        return None;
    }

    let expires = DateTime::parse_from_rfc3339(raw).ok()?.with_timezone(&Utc);
    let now = Utc::now();

    let seconds_left = (expires - now).num_seconds().max(0);

    if seconds_left == 0 {
        return Some("expired".to_string());
    }

    let days = seconds_left / 86_400;
    let hours = (seconds_left % 86_400) / 3_600;
    let minutes = (seconds_left % 3_600) / 60;
    let total_hours = seconds_left as f64 / 3_600.0;

    if days > 0 {
        Some(format!("{days}d {hours}h {minutes}m ({total_hours:.1} hrs)"))
    } else if hours > 0 {
        Some(format!("{hours}h {minutes}m ({total_hours:.1} hrs)"))
    } else {
        Some(format!("{minutes}m ({total_hours:.2} hrs)"))
    }
}

// Integrity verification

fn verify_integrity() -> Result<(), String> {
    use sha2::{Digest, Sha256};
    use std::fs;

    let exe_path = env::current_exe()
        .map_err(|e| format!("Failed to get executable path: {e}"))?;

    let exe_data = fs::read(&exe_path)
        .map_err(|e| format!("Failed to read executable: {e}"))?;

    let mut hasher = Sha256::new();
    hasher.update(&exe_data);
    let hash = hasher.finalize();
    let hash_hex = hex::encode(hash);

    let expected = env::var("PHANTOM_BOOTSTRAPPER_SHA256")
        .ok()
        .or_else(|| option_env!("PHANTOM_BOOTSTRAPPER_SHA256").map(str::to_string))
        .map(|s| s.trim().to_ascii_lowercase())
        .filter(|s| !s.is_empty());

    let Some(expected) = expected else {
        if cfg!(debug_assertions) {
            return Ok(());
        }

        return Err(
            "No expected bootstrapper SHA-256 configured. Set PHANTOM_BOOTSTRAPPER_SHA256 for release builds."
                .to_string(),
        );
    };

    if expected.len() != 64 || !expected.chars().all(|ch| ch.is_ascii_hexdigit()) {
        return Err("Configured bootstrapper SHA-256 is not a 64-character hex digest".to_string());
    }

    if hash_hex != expected {
        return Err(format!(
            "Executable hash mismatch. Expected {}, got {}",
            expected, hash_hex
        ));
    }

    Ok(())
}

// Windows ANSI support

#[cfg(windows)]
fn enable_ansi_support() -> Result<(), ()> {
    use std::os::windows::io::AsRawHandle;

    extern "system" {
        fn GetConsoleMode(handle: *mut std::ffi::c_void, mode: *mut u32) -> i32;
        fn SetConsoleMode(handle: *mut std::ffi::c_void, mode: u32) -> i32;
    }

    let stdout = std::io::stdout();
    let handle = stdout.as_raw_handle();
    let mut mode: u32 = 0;

    unsafe {
        if GetConsoleMode(handle as _, &mut mode) == 0 {
            return Err(());
        }

        SetConsoleMode(handle as _, mode | 0x0004);
    }

    Ok(())
}
