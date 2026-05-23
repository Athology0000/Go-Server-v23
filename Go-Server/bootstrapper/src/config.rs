use serde::{Deserialize, Serialize};
use std::fs;
use std::path::{Path, PathBuf};

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

fn default_server_url() -> String {
    "https://valiant-cooperation-production.up.railway.app".to_string()
}

fn default_mods_dir() -> String {
    detect_game_dir()
        .join("mods")
        .to_string_lossy()
        .to_string()
}

fn default_java_exe() -> String {
    "java".to_string()
}

fn default_game_dir() -> String {
    detect_game_dir().to_string_lossy().to_string()
}

fn default_build_id() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

fn default_client_version() -> String {
    "1.0.0".to_string()
}

fn detect_game_dir() -> PathBuf {
    // Prefer current working directory if it is already the Minecraft game folder.
    if let Ok(cwd) = std::env::current_dir() {
        if is_minecraft_game_dir(&cwd) {
            return cwd;
        }

        // If launched from a child folder like config/phantom/cache, walk upward.
        if let Some(found) = find_minecraft_dir_upwards(&cwd) {
            return found;
        }
    }

    // Fallback to the executable location.
    if let Ok(exe) = std::env::current_exe() {
        if let Some(exe_dir) = exe.parent() {
            if is_minecraft_game_dir(exe_dir) {
                return exe_dir.to_path_buf();
            }

            if let Some(found) = find_minecraft_dir_upwards(exe_dir) {
                return found;
            }
        }
    }

    // Last fallback.
    PathBuf::from(".")
}

fn find_minecraft_dir_upwards(start: &Path) -> Option<PathBuf> {
    for dir in start.ancestors() {
        if is_minecraft_game_dir(dir) {
            return Some(dir.to_path_buf());
        }
    }

    None
}

fn is_minecraft_game_dir(path: &Path) -> bool {
    let folder_name_ok = path
        .file_name()
        .and_then(|s| s.to_str())
        .map(|s| s.eq_ignore_ascii_case("minecraft"))
        .unwrap_or(false);

    // Prism instance minecraft folder should usually have config and/or mods.
    folder_name_ok && (path.join("config").exists() || path.join("mods").exists())
}

impl BootstrapConfig {
    pub fn load() -> Self {
        let path = detect_game_dir().join("config/phantom/bootstrap.json");

        let mut cfg: Self = if path.exists() {
            fs::read_to_string(&path)
                .ok()
                .and_then(|s| serde_json::from_str(&s).ok())
                .unwrap_or_default()
        } else {
            Self::default()
        };

        // Trim trailing slashes so endpoint concatenation never produces "//path".
        let trimmed = cfg.server_url.trim_end_matches('/');
        if trimmed.len() != cfg.server_url.len() {
            cfg.server_url = trimmed.to_string();
        }

        cfg
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