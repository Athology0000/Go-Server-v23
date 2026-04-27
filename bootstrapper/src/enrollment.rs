use base64::engine::general_purpose::STANDARD;
use base64::Engine;
use reqwest::blocking::Client;
use serde::{Deserialize, Serialize};
use std::io::{self, Write};

#[derive(Serialize)]
struct RedeemRequest<'a> {
    license_key: &'a str,
    account_id: &'a str,
}

#[derive(Serialize)]
struct HandshakeRequest<'a> {
    username: &'a str,
    password: &'a str,
    hwid: &'a str,
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

pub fn run(client: &Client, server_url: &str, hwid: &str) -> Result<EnrollmentResult, String> {
    println!("\n=== Cobalt First-Time Setup ===\n");

    let account_id = prompt("Enter your Cobalt account ID (from dashboard): ")?;
    let license_key = prompt("Enter your license key: ")?;
    let username = prompt("Enter your Cobalt username: ")?;
    let password = prompt_hidden("Enter your Cobalt password: ")?;

    println!("\nRedeeming license key...");

    let redeem_resp = client
        .post(format!("{}/enroll/redeem", server_url))
        .json(&RedeemRequest { license_key: &license_key, account_id: &account_id })
        .send()
        .map_err(|e| format!("enroll/redeem request failed: {e}"))?;

    if !redeem_resp.status().is_success() {
        return Err(format!("License key redemption failed: HTTP {}", redeem_resp.status()));
    }

    println!("Binding device...");

    let hs_resp = client
        .post(format!("{}/enroll/handshake", server_url))
        .json(&HandshakeRequest { username: &username, password: &password, hwid })
        .send()
        .map_err(|e| format!("enroll/handshake request failed: {e}"))?;

    if !hs_resp.status().is_success() {
        return Err(format!(
            "Device handshake failed: HTTP {} — check username and password",
            hs_resp.status()
        ));
    }

    let hs: HandshakeResponse = hs_resp.json().map_err(|e| e.to_string())?;

    if let Some(err) = &hs.error {
        return Err(format!("Enrollment error: {err}"));
    }

    let secret_b64 = hs.device_secret.ok_or("Missing device_secret in response")?;
    let secret_bytes = STANDARD
        .decode(&secret_b64)
        .map_err(|e| format!("Invalid device_secret encoding: {e}"))?;

    println!("Enrollment complete. Welcome, {}!\n", username);

    Ok(EnrollmentResult { username, device_secret: secret_bytes })
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
