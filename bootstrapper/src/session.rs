use serde::Serialize;
use std::fs;
use std::path::PathBuf;

const SESSION_PATH: &str = "config/cobalt/.session";

#[derive(Serialize)]
struct SessionFile<'a> {
    session_token: &'a str,
}

pub fn write(session_token: &str) -> Result<PathBuf, String> {
    let json = serde_json::to_string(&SessionFile { session_token })
        .map_err(|e| e.to_string())?;
    fs::create_dir_all("config/cobalt").map_err(|e| e.to_string())?;
    fs::write(SESSION_PATH, json).map_err(|e| format!("Failed to write session file: {e}"))?;
    std::fs::canonicalize(SESSION_PATH).map_err(|e| e.to_string())
}

pub fn delete() {
    let _ = fs::remove_file(SESSION_PATH);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn write_and_delete() {
        let path = write("test_token_abc").unwrap();
        assert!(path.exists());
        let contents = fs::read_to_string(&path).unwrap();
        assert!(contents.contains("test_token_abc"));
        delete();
        assert!(!path.exists());
    }
}
