use std::fs::{self, File, OpenOptions};
use std::io::Write;
use std::path::{Path, PathBuf};

#[cfg(unix)]
use std::os::unix::fs::OpenOptionsExt;

pub fn write(session_token: &str, game_dir: &str) -> Result<PathBuf, String> {
    let dir = Path::new(game_dir).join("config").join("cobalt");

    fs::create_dir_all(&dir)
        .map_err(|e| format!("Failed to create session dir {}: {e}", dir.display()))?;

    let path = dir.join("session.token");

    let mut file = {
        #[cfg(unix)]
        {
            OpenOptions::new()
                .write(true)
                .create(true)
                .truncate(true)
                .mode(0o600)
                .open(&path)
                .map_err(|e| format!("Failed to open session file {}: {e}", path.display()))?
        }
        #[cfg(not(unix))]
        {
            OpenOptions::new()
                .write(true)
                .create(true)
                .truncate(true)
                .open(&path)
                .map_err(|e| format!("Failed to open session file {}: {e}", path.display()))?
        }
    };

    file.write_all(session_token.trim().as_bytes())
        .map_err(|e| format!("Failed to write session file {}: {e}", path.display()))?;

    #[cfg(unix)]
    {
        fs::set_permissions(&path, fs::Permissions::from_mode(0o600))
            .map_err(|e| format!("Failed to set permissions on session file {}: {e}", path.display()))?;
    }

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