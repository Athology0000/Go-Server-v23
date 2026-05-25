use crate::client::api_url;
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
    pub plan_tier: String,
    pub entitlement_expires_at: Option<String>,
}

#[derive(Serialize)]
struct StartRequest<'a> {
    username: &'a str,
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
    authenticated: Option<bool>,
    authorized: Option<bool>,
    reason: Option<String>,
    account_id: Option<String>,
    username: Option<String>,
    session_token: Option<String>,
    expires_in: Option<i32>,
    manifest_url: Option<String>,
    manifest_signature: Option<String>,
    plan_tier: Option<String>,
    entitlement_expires_at: Option<String>,
    error: Option<String>,
}

pub fn authenticate(
    client: &Client,
    server_url: &str,
    username: &str,
    device_secret: &[u8],
    client_version: &str,
    build_id: &str,
) -> Result<AuthResult, String> {
    let start_resp = client
        .post(api_url(server_url, "/auth/start"))
        .json(&StartRequest {
            username,
            minecraft_username: "",
            client_version,
            bootstrap_build_id: build_id,
        })
        .send()
        .map_err(|e| format!("auth/start request failed: {e}"))?;

    let start_status = start_resp.status();
    let start_body = start_resp
        .text()
        .map_err(|e| format!("auth/start response read failed: {e}"))?;

    if !start_status.is_success() {
        return Err(format!(
            "auth/start rejected: HTTP {start_status}. Body: {start_body}"
        ));
    }

    let start: StartResponse = serde_json::from_str(&start_body)
        .map_err(|e| format!("auth/start response parse failed: {e}. Body: {start_body}"))?;

    if let Some(err) = &start.error {
        return Err(format!("auth/start error: {err}"));
    }

    let challenge = start.challenge.ok_or("auth/start: missing challenge")?;
    let proof = compute_proof(device_secret, &challenge);

    let finish_resp = client
        .post(api_url(server_url, "/auth/finish"))
        .json(&FinishRequest {
            username,
            proof,
            minecraft_username: "",
        })
        .send()
        .map_err(|e| format!("auth/finish request failed: {e}"))?;

    let finish_status = finish_resp.status();
    let finish_body = finish_resp
        .text()
        .map_err(|e| format!("auth/finish response read failed: {e}"))?;

    if !finish_status.is_success() {
        return Err(format!(
            "auth/finish rejected: HTTP {finish_status}. Body: {finish_body}"
        ));
    }

    let finish: FinishResponse = serde_json::from_str(&finish_body)
        .map_err(|e| format!("auth/finish response parse failed: {e}. Body: {finish_body}"))?;

    if let Some(err) = &finish.error {
        return Err(format!("auth/finish error: {err}"));
    }

    if finish.authorized != Some(true) {
        let reason = finish.reason.unwrap_or_else(|| "unauthorized".to_string());

        let hint = match reason.as_str() {
            "no_entitlement" => {
                "\n  Hint: the server has no entitlement configured for your plan tier."
            }
            "no_license" => "\n  Hint: no license is associated with this account.",
            "license_expired" => "\n  Hint: your license has expired.",
            "license_revoked" | "license_suspended" => {
                "\n  Hint: your license has been revoked or suspended."
            }
            _ => "",
        };

        return Err(format!("Not authorized: {reason}{hint}"));
    }

    let manifest_url = finish
        .manifest_url
        .filter(|s| !s.trim().is_empty())
        .ok_or("No manifest URL in auth response")?;

    let session_token = finish.session_token.ok_or("missing session_token")?;

    let plan_tier = finish
        .plan_tier
        .filter(|s| !s.trim().is_empty())
        .unwrap_or_else(|| "unknown".to_string());

    Ok(AuthResult {
        session_token,
        manifest_url,
        manifest_signature: finish.manifest_signature.unwrap_or_default(),
        plan_tier,
        entitlement_expires_at: finish.entitlement_expires_at,
    })
}

fn compute_proof(device_secret: &[u8], challenge: &str) -> String {
    let mut mac = HmacSha256::new_from_slice(device_secret).expect("HMAC accepts any key size");
    mac.update(challenge.as_bytes());
    hex::encode(mac.finalize().into_bytes())
}
