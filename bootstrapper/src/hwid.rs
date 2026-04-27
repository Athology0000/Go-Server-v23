use winreg::{enums::HKEY_LOCAL_MACHINE, RegKey};

pub fn collect() -> String {
    let guid = machine_guid().unwrap_or_else(|| "UNKNOWN_GUID".to_string());
    let serial = volume_serial().map(|s| s.to_string()).unwrap_or_else(|| "0".to_string());
    format!("{}:{}", guid, serial).to_uppercase()
}

fn machine_guid() -> Option<String> {
    let hklm = RegKey::predef(HKEY_LOCAL_MACHINE);
    let key = hklm.open_subkey(r"SOFTWARE\Microsoft\Cryptography").ok()?;
    key.get_value::<String, _>("MachineGuid").ok()
}

fn volume_serial() -> Option<u32> {
    use winapi::um::fileapi::GetVolumeInformationW;
    let root: Vec<u16> = "C:\\\0".encode_utf16().collect();
    let mut serial: u32 = 0;
    let ok = unsafe {
        GetVolumeInformationW(
            root.as_ptr(),
            std::ptr::null_mut(), 0,
            &mut serial,
            std::ptr::null_mut(),
            std::ptr::null_mut(),
            std::ptr::null_mut(), 0,
        )
    };
    if ok != 0 { Some(serial) } else { None }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn hwid_is_non_empty_and_uppercase() {
        let hwid = collect();
        assert!(!hwid.is_empty());
        assert_eq!(hwid, hwid.to_uppercase());
        assert!(hwid.contains(':'), "expected colon separator, got: {hwid}");
    }
}
