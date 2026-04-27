use hmac::{Hmac, Mac};
use reqwest::blocking::Client;
use serde::{Deserialize, Serialize};
use sha2::Sha256;

type HmacSha256 = Hmac<Sha256>;

#[derive(Debug)]
pub struct AuthResult {
    pub session_token: String,
    pub manifest_url: String,
    pub manifest_signature: String,
}

#[derive(Serialize)]
struct StartRequest<'a> {
    username: &'a str,
    hwid: &'a str,
    minecraft_username: &'a str,
    client_version: &'a str,
    bootstrap_build_id: &'a str,
}

#[derive(Deserialize)]
struct StartResponse {
    challenge: Option<String>,
    error: Option<String>,
}

#[derive(Serialize)]
struct FinishRequest<'a> {
    username: &'a str,
    proof: String,
    minecraft_username: &'a str,
}

#[derive(Deserialize)]
struct FinishResponse {
    authorized: Option<bool>,
    reason: Option<String>,
    session_token: Option<String>,
    manifest_url: Option<String>,
    manifest_signature: Option<String>,
    error: Option<String>,
}

pub fn authenticate(
    client: &Client,
    server_url: &str,
    username: &str,
    hwid: &str,
    device_secret: &[u8],
    client_version: &str,
    build_id: &str,
) -> Result<AuthResult, String> {
    let start_resp = client
        .post(format!("{}/auth/start", server_url))
        .json(&StartRequest {
            username,
            hwid,
            minecraft_username: "",
            client_version,
            bootstrap_build_id: build_id,
        })
        .send()
        .map_err(|e| format!("auth/start request failed: {e}"))?;

    if !start_resp.status().is_success() {
        return Err(format!("auth/start rejected: HTTP {}", start_resp.status()));
    }

    let start: StartResponse = start_resp.json().map_err(|e| e.to_string())?;

    if let Some(err) = &start.error {
        return Err(format!("auth/start error: {err}"));
    }

    let challenge = start.challenge.ok_or("auth/start: missing challenge")?;
    let proof = compute_proof(device_secret, &challenge);

    let finish_resp = client
        .post(format!("{}/auth/finish", server_url))
        .json(&FinishRequest {
            username,
            proof,
            minecraft_username: "",
        })
        .send()
        .map_err(|e| format!("auth/finish request failed: {e}"))?;

    if !finish_resp.status().is_success() {
        return Err(format!("auth/finish rejected: HTTP {}", finish_resp.status()));
    }

    let finish: FinishResponse = finish_resp.json().map_err(|e| e.to_string())?;

    if let Some(err) = &finish.error {
        return Err(format!("auth/finish error: {err}"));
    }

    if finish.authorized != Some(true) {
        let reason = finish.reason.unwrap_or_else(|| "unauthorized".to_string());
        return Err(format!("Not authorized: {reason}"));
    }

    Ok(AuthResult {
        session_token: finish.session_token.ok_or("missing session_token")?,
        manifest_url: finish.manifest_url.unwrap_or_default(),
        manifest_signature: finish.manifest_signature.unwrap_or_default(),
    })
}

fn compute_proof(device_secret: &[u8], challenge: &str) -> String {
    let mut mac = HmacSha256::new_from_slice(device_secret).expect("HMAC accepts any key size");
    mac.update(challenge.as_bytes());
    hex::encode(mac.finalize().into_bytes())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn compute_proof_known_value() {
        let key = b"secret";
        let challenge = "challenge";
        let proof = compute_proof(key, challenge);
        assert_eq!(proof.len(), 64);
        assert!(proof.chars().all(|c| c.is_ascii_hexdigit()));
    }
}
