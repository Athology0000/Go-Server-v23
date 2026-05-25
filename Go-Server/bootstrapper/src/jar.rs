use crate::client::api_url;
use reqwest::blocking::Client;
use sha2::{Digest, Sha256};
use std::fs::{self, File};
use std::io::{Read, Write};
use std::path::{Path, PathBuf};

pub fn ensure(
    client: &Client,
    server_url: &str,
    session_token: &str,
    expected_sha256: &str,
    mods_dir: &str,
) -> Result<PathBuf, String> {
    let mods_dir = PathBuf::from(mods_dir);

    fs::create_dir_all(&mods_dir)
        .map_err(|e| format!("Failed to create mods folder {}: {e}", mods_dir.display()))?;

    let jar_path = mods_dir.join("phantom.jar");
    let tmp_path = mods_dir.join("phantom.jar.tmp");

    if jar_path.exists() {
        println!("phantom.jar: found in mods folder, verifying");

        let existing_hash = file_sha256(&jar_path)?;
        if existing_hash.eq_ignore_ascii_case(expected_sha256) {
            println!("phantom.jar: verified");
            return Ok(jar_path);
        }

        println!("phantom.jar: hash mismatch, redownloading");
        fs::remove_file(&jar_path).map_err(|e| format!("Failed to remove old phantom.jar: {e}"))?;
    } else {
        println!("phantom.jar: not found in mods folder — downloading");
    }

    if tmp_path.exists() {
        fs::remove_file(&tmp_path).map_err(|e| format!("Failed to remove old temp jar: {e}"))?;
    }

    let url = api_url(server_url, "/content/module/phantom");

    println!("phantom.jar: downloading from {}", url);
    println!("phantom.jar: saving to {}", jar_path.display());

    let mut resp = client
        .get(&url)
        .header("Authorization", format!("Bearer {session_token}"))
        .send()
        .map_err(|e| format!("JAR download request failed: {e}"))?;

    let status = resp.status();

    if !status.is_success() {
        let body = resp.text().unwrap_or_default();
        return Err(format!("JAR download returned HTTP {status}. Body: {body}"));
    }

    let mut out = File::create(&tmp_path)
        .map_err(|e| format!("Failed to create temp jar file {}: {e}", tmp_path.display()))?;

    let mut downloaded: u64 = 0;
    let mut buffer = [0u8; 64 * 1024];

    loop {
        let n = resp
            .read(&mut buffer)
            .map_err(|e| format!("Failed while reading jar download: {e}"))?;

        if n == 0 {
            break;
        }

        out.write_all(&buffer[..n])
            .map_err(|e| format!("Failed while writing jar file: {e}"))?;

        downloaded += n as u64;
    }

    out.flush()
        .map_err(|e| format!("Failed to flush jar file: {e}"))?;

    drop(out);

    if downloaded == 0 {
        let _ = fs::remove_file(&tmp_path);
        return Err("JAR download completed but file was empty".to_string());
    }

    println!("phantom.jar: downloaded {} bytes", downloaded);
    println!("phantom.jar: verifying sha256");

    let downloaded_hash = file_sha256(&tmp_path)?;

    if !downloaded_hash.eq_ignore_ascii_case(expected_sha256) {
        let _ = fs::remove_file(&tmp_path);

        return Err(format!(
            "JAR hash mismatch. Expected {}, got {}",
            expected_sha256, downloaded_hash
        ));
    }

    fs::rename(&tmp_path, &jar_path)
        .map_err(|e| format!("Failed to finalize phantom.jar into mods folder: {e}"))?;

    println!("phantom.jar: downloaded and verified in mods folder");

    Ok(jar_path)
}

fn file_sha256(path: &Path) -> Result<String, String> {
    let mut file =
        File::open(path).map_err(|e| format!("Failed to open {}: {e}", path.display()))?;

    let mut hasher = Sha256::new();
    let mut buffer = [0u8; 64 * 1024];

    loop {
        let n = file
            .read(&mut buffer)
            .map_err(|e| format!("Failed to read {}: {e}", path.display()))?;

        if n == 0 {
            break;
        }

        hasher.update(&buffer[..n]);
    }

    Ok(format!("{:x}", hasher.finalize()))
}
