use reqwest::blocking::Client;
use sha2::{Digest, Sha256};
use std::fs;
use std::io::Read;
use std::path::{Path, PathBuf};

const JAR_CACHE_PATH: &str = "config/cobalt/cache/cobalt.jar";

pub fn ensure(
    client: &Client,
    server_url: &str,
    session_token: &str,
    expected_sha256: &str,
) -> Result<PathBuf, String> {
    let path = PathBuf::from(JAR_CACHE_PATH);

    if path.exists() {
        let actual = sha256_file(&path)?;
        if actual == expected_sha256 {
            println!("cobalt.jar: verified (cached)");
            return Ok(path);
        }
        println!("cobalt.jar: digest mismatch — re-downloading");
    } else {
        println!("cobalt.jar: not found — downloading");
    }

    download(client, server_url, session_token, expected_sha256, &path)
}

fn download(
    client: &Client,
    server_url: &str,
    session_token: &str,
    expected_sha256: &str,
    dest: &Path,
) -> Result<PathBuf, String> {
    let url = format!("{}/content/module/cobalt", server_url);

    let mut resp = client
        .get(&url)
        .header("Authorization", format!("Bearer {session_token}"))
        .send()
        .map_err(|e| format!("JAR download failed: {e}"))?;

    if !resp.status().is_success() {
        return Err(format!("JAR download returned HTTP {}", resp.status()));
    }

    let mut bytes = Vec::new();
    resp.read_to_end(&mut bytes).map_err(|e| format!("JAR read failed: {e}"))?;

    let actual = {
        let mut h = Sha256::new();
        h.update(&bytes);
        hex::encode(h.finalize())
    };

    if actual != expected_sha256 {
        return Err(format!(
            "JAR digest mismatch after download: expected {expected_sha256}, got {actual}"
        ));
    }

    if let Some(parent) = dest.parent() {
        fs::create_dir_all(parent).map_err(|e| e.to_string())?;
    }
    fs::write(dest, &bytes).map_err(|e| format!("JAR write failed: {e}"))?;

    println!("cobalt.jar: downloaded and verified");
    Ok(dest.to_path_buf())
}

fn sha256_file(path: &Path) -> Result<String, String> {
    let mut file = fs::File::open(path).map_err(|e| e.to_string())?;
    let mut h = Sha256::new();
    let mut buf = [0u8; 8192];
    loop {
        let n = file.read(&mut buf).map_err(|e| e.to_string())?;
        if n == 0 { break; }
        h.update(&buf[..n]);
    }
    Ok(hex::encode(h.finalize()))
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::io::Write;
    use tempfile::NamedTempFile;

    #[test]
    fn sha256_file_known_value() {
        let mut tmp = NamedTempFile::new().unwrap();
        tmp.write_all(b"hello world").unwrap();
        let hash = sha256_file(tmp.path()).unwrap();
        assert_eq!(hash, "b94d27b9934d3e08a52e52d7da7dabfac484efe04294e576e537a2035f8c8b99");
    }
}
