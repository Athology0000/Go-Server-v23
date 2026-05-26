use base64::engine::general_purpose::STANDARD;
use base64::Engine;
use ed25519_dalek::{Signature, Verifier, VerifyingKey};
use reqwest::blocking::Client;
use serde::{Deserialize, Serialize};

// 32-byte Ed25519 public key matching the server's MANIFEST_SIGNING_KEY.
const MANIFEST_PUBLIC_KEY_B64: &str = "LLeG53gx/aRVy/jhuuKFHJqATd9+cN0Jm04RVxq3o34=";

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
    #[serde(default)]
    pub id: String,

    pub build_id: String,
    pub channel: String,

    #[serde(default = "default_minimum_loader_version")]
    pub minimum_loader_version: String,

    #[serde(default)]
    pub module_key: String,

    #[serde(default)]
    pub modules: Vec<ManifestModule>,

    #[serde(default)]
    pub native_components: Vec<ManifestNative>,

    #[serde(default)]
    pub signature: String,
}

#[derive(Debug, Deserialize)]
struct AddonManifest {
    build_id: String,
    channel: String,
    #[allow(dead_code)]
    expires_at: Option<String>,
    addons: Vec<ManifestAddon>,
}

#[derive(Debug, Deserialize)]
struct ManifestAddon {
    id: String,
    url: String,
    sha256: String,
    required: bool,
}

#[derive(Serialize)]
struct SignedPayload<'a> {
    build_id: &'a str,
    channel: &'a str,
    minimum_loader_version: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    module_key: Option<&'a str>,
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

fn default_minimum_loader_version() -> String {
    "0.1.0".to_string()
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

    let status = resp.status();
    let body = resp
        .text()
        .map_err(|e| format!("Manifest response read failed: {e}"))?;

    if !status.is_success() {
        return Err(format!(
            "Manifest fetch returned HTTP {status}. Body: {body}"
        ));
    }

    let mut manifest = parse_manifest_body(&body)?;
    let expected_sig = expected_sig.trim();
    let payload_sig = manifest.signature.trim();

    if !expected_sig.is_empty() && !payload_sig.is_empty() && expected_sig != payload_sig {
        return Err("Manifest signature mismatch between auth response and manifest payload".to_string());
    }

    let sig_to_verify = if !expected_sig.is_empty() {
        expected_sig
    } else {
        payload_sig
    };

    if sig_to_verify.is_empty() {
        if allow_unsigned_manifests() {
            eprintln!(
                "WARNING: unsigned manifest accepted because PHANTOM_ALLOW_UNSIGNED_MANIFESTS=1 is set."
            );
        } else {
            return Err(
                "Unsigned manifests are prohibited in production. Set PHANTOM_ALLOW_UNSIGNED_MANIFESTS=1 only for local development.".to_string(),
            );
        }
    } else {
        verify_signature(&manifest, sig_to_verify)?;
        manifest.signature = sig_to_verify.to_string();
    }

    Ok(manifest)
}

fn allow_unsigned_manifests() -> bool {
    std::env::var("PHANTOM_ALLOW_UNSIGNED_MANIFESTS")
        .map(|val| {
            let normalized = val.trim().to_lowercase();
            normalized == "1" || normalized == "true" || normalized == "yes"
        })
        .unwrap_or(false)
}

fn parse_manifest_body(body: &str) -> Result<ContentManifest, String> {
    let value: serde_json::Value = serde_json::from_str(body)
        .map_err(|e| format!("Manifest parse failed: {e}. Body was:\n{body}"))?;

    // New dev addon manifest shape:
    // {
    //   "build_id": "...",
    //   "channel": "...",
    //   "expires_at": "...",
    //   "addons": [...]
    // }
    if value.get("addons").is_some() {
        let addon_manifest: AddonManifest = serde_json::from_value(value)
            .map_err(|e| format!("Addon manifest parse failed: {e}. Body was:\n{body}"))?;

        return Ok(ContentManifest {
            id: "stable".to_string(),
            build_id: addon_manifest.build_id,
            channel: addon_manifest.channel,
            minimum_loader_version: default_minimum_loader_version(),
            module_key: String::new(),
            modules: addon_manifest
                .addons
                .into_iter()
                .enumerate()
                .map(|(idx, addon)| ManifestModule {
                    name: addon.id,
                    url: addon.url,
                    sha256: addon.sha256,
                    required: addon.required,
                    init_order: idx as i32,
                })
                .collect(),
            native_components: Vec::new(),
            signature: String::new(),
        });
    }

    // Original signed manifest shape.
    serde_json::from_value::<ContentManifest>(value)
        .map_err(|e| format!("Manifest parse failed: {e}. Body was:\n{body}"))
}

fn verify_signature(manifest: &ContentManifest, sig_b64: &str) -> Result<(), String> {
    let public_key_b64 = manifest_public_key_b64();

    if public_key_b64 == "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY" {
        return Err(
            "Manifest public key is still the placeholder. Replace MANIFEST_PUBLIC_KEY_B64 or use unsigned local dev manifests only."
                .to_string(),
        );
    }

    let public_key_bytes: [u8; 32] = STANDARD
        .decode(&public_key_b64)
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
        module_key: Some(manifest.module_key.trim()).filter(|key| !key.is_empty()),
        modules: manifest
            .modules
            .iter()
            .map(|m| SignedModule {
                name: &m.name,
                url: &m.url,
                sha256: &m.sha256,
                required: m.required,
                init_order: m.init_order,
            })
            .collect(),
        native_components: manifest
            .native_components
            .iter()
            .map(|n| SignedNative {
                name: &n.name,
                url: &n.url,
                sha256: &n.sha256,
                required: n.required,
            })
            .collect(),
    };

    let payload_json = serde_json::to_vec(&payload).map_err(|e| e.to_string())?;
    let payload_without_key = SignedPayload {
        module_key: None,
        ..payload
    };
    let payload_without_key_json =
        serde_json::to_vec(&payload_without_key).map_err(|e| e.to_string())?;

    let verifying_key = VerifyingKey::from_bytes(&public_key_bytes)
        .map_err(|e| format!("Invalid public key: {e}"))?;

    let signature = Signature::from_bytes(&sig_bytes);

    if verifying_key.verify(&payload_json, &signature).is_ok()
        || verifying_key
            .verify(&payload_without_key_json, &signature)
            .is_ok()
    {
        return Ok(());
    }

    Err("Manifest signature verification FAILED - manifest may be tampered or signed by a different MANIFEST_SIGNING_KEY".to_string())
}

fn manifest_public_key_b64() -> String {
    std::env::var("PHANTOM_MANIFEST_PUBLIC_KEY_B64")
        .or_else(|_| std::env::var("PHANTOM_MANIFEST_PUBLIC_KEY"))
        .unwrap_or_else(|_| MANIFEST_PUBLIC_KEY_B64.to_string())
        .trim()
        .to_string()
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

    #[test]
    fn parses_dev_addon_manifest() {
        let body = r#"{
            "build_id": "1.156.0-dev",
            "channel": "stable",
            "expires_at": "2026-05-02T03:00:00Z",
            "addons": [
                {
                    "id": "phantom",
                    "url": "/content/module/phantom-starter.jar",
                    "sha256": "abc123",
                    "required": true
                }
            ]
        }"#;

        let manifest = parse_manifest_body(body).expect("should parse");
        assert_eq!(manifest.id, "stable");
        assert_eq!(manifest.modules.len(), 1);
        assert_eq!(manifest.modules[0].name, "phantom");
        assert_eq!(manifest.modules[0].url, "/content/module/phantom-starter.jar");
    }
}
