use crate::client::api_url;
use base64::engine::general_purpose::STANDARD;
use base64::Engine;
use reqwest::blocking::Client;
use reqwest::header::CONTENT_TYPE;
use serde::{Deserialize, Serialize};
use std::io::{self, Write};

#[derive(Serialize)]
struct RedeemRequest<'a> {
    license_key: &'a str,
    account_id: &'a str,
}

#[derive(Deserialize)]
struct RedeemResponse {
    status: Option<String>,
    username: Option<String>,
    device_secret: Option<String>,
    error: Option<String>,
    reason: Option<String>,
}

#[derive(Serialize)]
struct HandshakeRequest<'a> {
    username: &'a str,
    password: &'a str,
}

#[derive(Deserialize)]
struct HandshakeResponse {
    device_secret: Option<String>,
    error: Option<String>,
}

pub struct EnrollmentResult {
    pub username: String,
    pub device_secret: Vec<u8>,
}

pub fn run(client: &Client, server_url: &str) -> Result<EnrollmentResult, String> {
    println!("\n=== Phantom First-Time Setup ===\n");

    println!("Select setup method:");
    println!("  1) Username + password");
    println!("  2) Account ID + license key");
    let mode = prompt("Enter 1 or 2: ")?;

    match mode.trim() {
        "1" => {
            let username = prompt("Enter your Phantom username: ")?;
            let password = prompt_hidden("Enter your Phantom password: ")?;

            println!("\nBinding device...");

            let hs_resp = client
                .post(api_url(server_url, "/enroll/handshake"))
                .json(&HandshakeRequest {
                    username: &username,
                    password: &password,
                })
                .send()
                .map_err(|e| format!("enroll/handshake request failed: {e}"))?;

            if !hs_resp.status().is_success() {
                let status = hs_resp.status();
                let ct = hs_resp
                    .headers()
                    .get(CONTENT_TYPE)
                    .and_then(|v| v.to_str().ok())
                    .unwrap_or("")
                    .to_string();
                let body = hs_resp.text().unwrap_or_else(|_| "".to_string());
                return Err(format!(
                    "Device handshake failed: HTTP {status} (content-type: {ct})\n{body}\n\nTip: if you haven't redeemed a key in the dashboard yet, choose option 2."
                ));
            }

            let status = hs_resp.status();
            let ct = hs_resp
                .headers()
                .get(CONTENT_TYPE)
                .and_then(|v| v.to_str().ok())
                .unwrap_or("")
                .to_string();
            let body = hs_resp
                .text()
                .map_err(|e| format!("Failed reading handshake response body: {e}"))?;

            let hs: HandshakeResponse = serde_json::from_str(&body).map_err(|e| {
                format!(
                    "Handshake response was not valid JSON: {e}\nHTTP {status} (content-type: {ct})\n{body}"
                )
            })?;

            if let Some(err) = &hs.error {
                return Err(format!("Enrollment error: {err}"));
            }

            let secret_b64 = hs
                .device_secret
                .ok_or("Missing device_secret in response")?;
            let secret_bytes = STANDARD
                .decode(&secret_b64)
                .map_err(|e| format!("Invalid device_secret encoding: {e}"))?;

            println!("Enrollment complete. Welcome, {}!\n", username);
            Ok(EnrollmentResult {
                username,
                device_secret: secret_bytes,
            })
        }
        "2" => {
            let account_id = prompt("Enter your Phantom account ID (from dashboard): ")?;
            let license_key = prompt("Enter your license key: ")?;

            println!("\nRedeeming license key...");

            let redeem_resp = client
                .post(api_url(server_url, "/enroll/redeem"))
                .json(&RedeemRequest {
                    license_key: &license_key,
                    account_id: &account_id,
                })
                .send()
                .map_err(|e| format!("enroll/redeem request failed: {e}"))?;

            let status = redeem_resp.status();
            let ct = redeem_resp
                .headers()
                .get(CONTENT_TYPE)
                .and_then(|v| v.to_str().ok())
                .unwrap_or("")
                .to_string();
            let body = redeem_resp.text().unwrap_or_else(|_| "".to_string());

            if !status.is_success() {
                let key_hint = if !license_key.trim().to_uppercase().starts_with("PHANTOM-") {
                    "\n\nHint: paste the FULL key (starts with PHANTOM-...), not the key ID/hash prefix."
                } else {
                    ""
                };
                return Err(format!(
                    "License key redemption failed: HTTP {status} (content-type: {ct})\n{body}{key_hint}\n\nHint: server_url should be the API base (e.g. http://localhost:8080), not the panel/admin URL (3001/3002)."
                ));
            }

            let redeem: RedeemResponse = serde_json::from_str(&body).map_err(|e| {
                format!(
                    "Redeem response was not valid JSON: {e}\nHTTP {status} (content-type: {ct})\n{body}"
                )
            })?;

            if let Some(err) = &redeem.error {
                let reason = redeem.reason.unwrap_or_default();
                if reason.is_empty() {
                    return Err(format!("Enrollment error: {err}"));
                }
                return Err(format!("Enrollment error: {err} ({reason})"));
            }

            let username = redeem
                .username
                .ok_or("Missing username in redeem response")?;
            let secret_b64 = redeem
                .device_secret
                .ok_or("Missing device_secret in redeem response")?;
            let secret_bytes = STANDARD
                .decode(&secret_b64)
                .map_err(|e| format!("Invalid device_secret encoding: {e}"))?;

            println!("Enrollment complete. Welcome, {}!\n", username);
            Ok(EnrollmentResult {
                username,
                device_secret: secret_bytes,
            })
        }
        _ => Err("Invalid selection (enter 1 or 2)".to_string()),
    }
}

fn prompt(label: &str) -> Result<String, String> {
    print!("{label}");
    io::stdout().flush().map_err(|e| e.to_string())?;
    let mut buf = String::new();
    io::stdin().read_line(&mut buf).map_err(|e| e.to_string())?;
    Ok(buf.trim().to_string())
}

// TODO: replace with rpassword crate for production (add rpassword = "7" to Cargo.toml)
fn prompt_hidden(label: &str) -> Result<String, String> {
    prompt(label)
}
