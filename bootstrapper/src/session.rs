use std::fs;
use std::path::{Path, PathBuf};

pub fn write(session_token: &str, game_dir: &str) -> Result<PathBuf, String> {
    let dir = Path::new(game_dir).join("config").join("cobalt");

    fs::create_dir_all(&dir)
        .map_err(|e| format!("Failed to create session dir {}: {e}", dir.display()))?;

    let path = dir.join("session.token");

    fs::write(&path, session_token.trim())
        .map_err(|e| format!("Failed to write session file {}: {e}", path.display()))?;

    println!("session: wrote {}", path.display());

    if !path.exists() {
        return Err(format!("Session file was not created: {}", path.display()));
    }

    Ok(path)
}

pub fn delete(game_dir: &str) {
    let path = Path::new(game_dir)
        .join("config")
        .join("cobalt")
        .join("session.token");

    let _ = fs::remove_file(&path);
    println!("session: deleted {}", path.display());
}