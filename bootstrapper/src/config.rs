use serde::{Deserialize, Serialize};
use std::fs;
use std::path::PathBuf;

#[derive(Debug, Serialize, Deserialize)]
pub struct BootstrapConfig {
    #[serde(default = "default_server_url")]
    pub server_url: String,
    #[serde(default = "default_mods_dir")]
    pub mods_dir: String,
    #[serde(default = "default_java_exe")]
    pub java_exe: String,
    #[serde(default = "default_game_dir")]
    pub game_dir: String,
    #[serde(default = "default_build_id")]
    pub build_id: String,
    #[serde(default = "default_client_version")]
    pub client_version: String,
}

fn default_server_url() -> String { "https://your-server-url.com".to_string() }
fn default_mods_dir() -> String { "mods".to_string() }
fn default_java_exe() -> String { "java".to_string() }
fn default_game_dir() -> String { ".minecraft".to_string() }
fn default_build_id() -> String { env!("CARGO_PKG_VERSION").to_string() }
fn default_client_version() -> String { "1.0.0".to_string() }

impl BootstrapConfig {
    pub fn load() -> Self {
        let path = PathBuf::from("config/cobalt/bootstrap.json");
        if path.exists() {
            fs::read_to_string(&path)
                .ok()
                .and_then(|s| serde_json::from_str(&s).ok())
                .unwrap_or_default()
        } else {
            Self::default()
        }
    }
}

impl Default for BootstrapConfig {
    fn default() -> Self {
        Self {
            server_url: default_server_url(),
            mods_dir: default_mods_dir(),
            java_exe: default_java_exe(),
            game_dir: default_game_dir(),
            build_id: default_build_id(),
            client_version: default_client_version(),
        }
    }
}
