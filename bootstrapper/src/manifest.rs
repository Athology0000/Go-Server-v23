use base64::engine::general_purpose::STANDARD;
use base64::Engine;
use ed25519_dalek::{Signature, VerifyingKey};
use reqwest::blocking::Client;
use serde::{Deserialize, Serialize};

// 32-byte Ed25519 public key (last 32 bytes of MANIFEST_SIGNING_KEY).
// Compute with: echo "$MANIFEST_SIGNING_KEY" | base64 -d | tail -c 32 | base64
const MANIFEST_PUBLIC_KEY_B64: &str = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY";

#[derive(Debug, Deserialize, Clone)]
pub struct ManifestModule {
    pub name: String,
    pub url: String,
    pub sha256: String,
    pub required: bool,
    pub init_order: i32,
}

#[derive(Debug, Deserialize, Clone)]
pub struct ManifestNative {
    pub name: String,
    pub url: String,
    pub sha256: String,
    pub required: bool,
}

#[derive(Debug, Deserialize, Clone)]
pub struct ContentManifest {
    pub id: String,
    pub build_id: String,
    pub channel: String,
    pub minimum_loader_version: String,
    pub modules: Vec<ManifestModule>,
    pub native_components: Vec<ManifestNative>,
    pub signature: String,
}

#[derive(Serialize)]
struct SignedPayload<'a> {
    build_id: &'a str,
    channel: &'a str,
    minimum_loader_version: &'a str,
    modules: Vec<SignedModule<'a>>,
    native_components: Vec<SignedNative<'a>>,
}

#[derive(Serialize)]
struct SignedModule<'a> {
    name: &'a str,
    url: &'a str,
    sha256: &'a str,
    required: bool,
    init_order: i32,
}

#[derive(Serialize)]
struct SignedNative<'a> {
    name: &'a str,
    url: &'a str,
    sha256: &'a str,
    required: bool,
}

pub fn fetch_and_verify(
    client: &Client,
    manifest_url: &str,
    session_token: &str,
    expected_sig: &str,
) -> Result<ContentManifest, String> {
    let resp = client
        .get(manifest_url)
        .header("Authorization", format!("Bearer {session_token}"))
        .send()
        .map_err(|e| format!("Manifest fetch failed: {e}"))?;

    if !resp.status().is_success() {
        return Err(format!("Manifest fetch returned HTTP {}", resp.status()));
    }

    let manifest: ContentManifest = resp.json().map_err(|e| format!("Manifest parse failed: {e}"))?;

    let sig_to_verify = if !expected_sig.is_empty() { expected_sig } else { &manifest.signature };
    verify_signature(&manifest, sig_to_verify)?;

    Ok(manifest)
}

fn verify_signature(manifest: &ContentManifest, sig_b64: &str) -> Result<(), String> {
    let public_key_bytes: [u8; 32] = STANDARD
        .decode(MANIFEST_PUBLIC_KEY_B64)
        .map_err(|_| "Invalid MANIFEST_PUBLIC_KEY_B64")?
        .try_into()
        .map_err(|_| "Public key must be exactly 32 bytes")?;

    let sig_bytes: [u8; 64] = STANDARD
        .decode(sig_b64)
        .map_err(|e| format!("Invalid signature base64: {e}"))?
        .try_into()
        .map_err(|_| "Signature must be exactly 64 bytes")?;

    let payload = SignedPayload {
        build_id: &manifest.build_id,
        channel: &manifest.channel,
        minimum_loader_version: &manifest.minimum_loader_version,
        modules: manifest.modules.iter().map(|m| SignedModule {
            name: &m.name,
            url: &m.url,
            sha256: &m.sha256,
            required: m.required,
            init_order: m.init_order,
        }).collect(),
        native_components: manifest.native_components.iter().map(|n| SignedNative {
            name: &n.name,
            url: &n.url,
            sha256: &n.sha256,
            required: n.required,
        }).collect(),
    };

    let payload_json = serde_json::to_vec(&payload).map_err(|e| e.to_string())?;

    let verifying_key = VerifyingKey::from_bytes(&public_key_bytes)
        .map_err(|e| format!("Invalid public key: {e}"))?;
    let signature = Signature::from_bytes(&sig_bytes);

    verifying_key.verify_strict(&payload_json, &signature)
        .map_err(|_| "Manifest signature verification FAILED — manifest may be tampered".to_string())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn public_key_b64_is_placeholder_or_valid() {
        if MANIFEST_PUBLIC_KEY_B64 == "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY" {
            return;
        }
        let bytes = STANDARD.decode(MANIFEST_PUBLIC_KEY_B64).expect("should decode");
        assert_eq!(bytes.len(), 32, "Ed25519 public key must be 32 bytes");
    }
}
