use base64::{engine::general_purpose::STANDARD, Engine};
use serde::{Deserialize, Serialize};
use std::fs;
use std::path::PathBuf;
use winapi::um::{
    dpapi::{CryptProtectData, CryptUnprotectData},
    wincrypt::DATA_BLOB,
    winbase::LocalFree,
};

#[derive(Debug)]
pub struct Credentials {
    pub username: String,
    pub device_secret: Vec<u8>,
}

#[derive(Serialize, Deserialize)]
struct StoredCreds {
    username: String,
    device_secret_b64: String,
}

const CREDS_PATH: &str = "config/cobalt/creds.json";

pub fn exists() -> bool {
    PathBuf::from(CREDS_PATH).exists()
}

pub fn load() -> Result<Credentials, String> {
    let raw = fs::read_to_string(CREDS_PATH).map_err(|e| e.to_string())?;
    let stored: StoredCreds = serde_json::from_str(&raw).map_err(|e| e.to_string())?;
    let encrypted = STANDARD.decode(&stored.device_secret_b64).map_err(|e| e.to_string())?;
    let decrypted = dpapi_unprotect(&encrypted)?;
    Ok(Credentials {
        username: stored.username,
        device_secret: decrypted,
    })
}

pub fn save(username: &str, device_secret: &[u8]) -> Result<(), String> {
    let encrypted = dpapi_protect(device_secret)?;
    let stored = StoredCreds {
        username: username.to_string(),
        device_secret_b64: STANDARD.encode(&encrypted),
    };
    fs::create_dir_all("config/cobalt").map_err(|e| e.to_string())?;
    let json = serde_json::to_string_pretty(&stored).map_err(|e| e.to_string())?;
    fs::write(CREDS_PATH, json).map_err(|e| e.to_string())?;
    Ok(())
}

fn dpapi_protect(data: &[u8]) -> Result<Vec<u8>, String> {
    unsafe {
        let mut input = DATA_BLOB { cbData: data.len() as u32, pbData: data.as_ptr() as *mut u8 };
        let mut output = DATA_BLOB { cbData: 0, pbData: std::ptr::null_mut() };
        if CryptProtectData(&mut input, std::ptr::null(), std::ptr::null_mut(),
                            std::ptr::null_mut(), std::ptr::null_mut(), 0, &mut output) == 0 {
            return Err(format!("CryptProtectData failed: {}", std::io::Error::last_os_error()));
        }
        let vec = std::slice::from_raw_parts(output.pbData, output.cbData as usize).to_vec();
        LocalFree(output.pbData as *mut _);
        Ok(vec)
    }
}

fn dpapi_unprotect(data: &[u8]) -> Result<Vec<u8>, String> {
    unsafe {
        let mut input = DATA_BLOB { cbData: data.len() as u32, pbData: data.as_ptr() as *mut u8 };
        let mut output = DATA_BLOB { cbData: 0, pbData: std::ptr::null_mut() };
        if CryptUnprotectData(&mut input, std::ptr::null_mut(), std::ptr::null_mut(),
                              std::ptr::null_mut(), std::ptr::null_mut(), 0, &mut output) == 0 {
            return Err(format!("CryptUnprotectData failed: {}", std::io::Error::last_os_error()));
        }
        let vec = std::slice::from_raw_parts(output.pbData, output.cbData as usize).to_vec();
        LocalFree(output.pbData as *mut _);
        Ok(vec)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn dpapi_round_trip() {
        let original = b"test_device_secret_bytes_32_bytes!!";
        let encrypted = dpapi_protect(original).expect("protect failed");
        let decrypted = dpapi_unprotect(&encrypted).expect("unprotect failed");
        assert_eq!(decrypted, original);
    }
}
