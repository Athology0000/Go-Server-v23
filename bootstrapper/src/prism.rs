use std::env;
use std::path::{Path, PathBuf};
use std::process::{Command, Stdio};

#[derive(Debug, Clone)]
pub struct PrismInstance {
    pub game_dir: PathBuf,
    pub instance_dir: PathBuf,
    pub instances_dir: PathBuf,
    pub prism_root: PathBuf,
    pub instance_id: String,
}

pub fn detect_from_current_dir() -> Result<PrismInstance, String> {
    let game_dir = env::current_dir()
        .map_err(|e| format!("Failed to read current directory: {e}"))?;

    detect_from_game_dir(game_dir)
}

pub fn detect_from_game_dir(game_dir: PathBuf) -> Result<PrismInstance, String> {
    let game_dir_name = game_dir
        .file_name()
        .and_then(|s| s.to_str())
        .unwrap_or("");

    if !game_dir_name.eq_ignore_ascii_case("minecraft") {
        return Err(format!(
            "Bootstrapper must be run from the instance minecraft folder. Current folder is: {}",
            game_dir.display()
        ));
    }

    let instance_dir = game_dir
        .parent()
        .ok_or("Could not find instance folder above minecraft folder")?
        .to_path_buf();

    let instance_id = instance_dir
        .file_name()
        .and_then(|s| s.to_str())
        .ok_or("Could not read Prism instance folder name")?
        .to_string();

    let instances_dir = instance_dir
        .parent()
        .ok_or("Could not find instances folder above instance folder")?
        .to_path_buf();

    let instances_dir_name = instances_dir
        .file_name()
        .and_then(|s| s.to_str())
        .unwrap_or("");

    if !instances_dir_name.eq_ignore_ascii_case("instances") {
        return Err(format!(
            "Expected parent folder to be 'instances', got: {}",
            instances_dir.display()
        ));
    }

    let prism_root = instances_dir
        .parent()
        .ok_or("Could not find Prism root above instances folder")?
        .to_path_buf();

    Ok(PrismInstance {
        game_dir,
        instance_dir,
        instances_dir,
        prism_root,
        instance_id,
    })
}

pub fn find_prism_exe(prism_root: &Path) -> Result<PathBuf, String> {
    let mut candidates = Vec::new();

    candidates.push(prism_root.join("prismlauncher.exe"));
    candidates.push(prism_root.join("PrismLauncher.exe"));

    if let Some(local_app_data) = env::var_os("LOCALAPPDATA") {
        let local = PathBuf::from(local_app_data);

        candidates.push(local.join("Programs").join("PrismLauncher").join("prismlauncher.exe"));
        candidates.push(local.join("Programs").join("PrismLauncher").join("PrismLauncher.exe"));
        candidates.push(local.join("PrismLauncher").join("prismlauncher.exe"));
        candidates.push(local.join("PrismLauncher").join("PrismLauncher.exe"));
    }

    if let Some(program_files) = env::var_os("ProgramFiles") {
        let pf = PathBuf::from(program_files);

        candidates.push(pf.join("PrismLauncher").join("prismlauncher.exe"));
        candidates.push(pf.join("PrismLauncher").join("PrismLauncher.exe"));
    }

    if let Some(program_files_x86) = env::var_os("ProgramFiles(x86)") {
        let pf = PathBuf::from(program_files_x86);

        candidates.push(pf.join("PrismLauncher").join("prismlauncher.exe"));
        candidates.push(pf.join("PrismLauncher").join("PrismLauncher.exe"));
    }

    for path in candidates {
        if path.exists() {
            return Ok(path);
        }
    }

    // Last attempt: rely on PATH.
    Ok(PathBuf::from("prismlauncher.exe"))
}

pub fn find_java_exe() -> Result<String, String> {
    let candidates = vec![
        "java.exe".to_string(),
        "javaw.exe".to_string(),
    ];

    for candidate in candidates {
        if Command::new(&candidate)
            .arg("-version")
            .output()
            .is_ok()
        {
            return Ok(candidate);
        }
    }

    Err("Java executable not found in PATH".to_string())
}

pub fn launch_detected_instance() -> Result<i32, String> {
    let detected = detect_from_current_dir()?;
    let prism_exe = find_prism_exe(&detected.prism_root)?;

    println!("Using Prism root: {}", detected.prism_root.display());
    println!("Using Prism instance: {}", detected.instance_id);
    println!("Using Prism exe: {}", prism_exe.display());

    let status = Command::new(&prism_exe)
        .arg("--launch")
        .arg(&detected.instance_id)
        .stdin(Stdio::null())
        .status()
        .map_err(|e| {
            format!(
                "Failed to launch Prism instance '{}' using '{}': {e}",
                detected.instance_id,
                prism_exe.display()
            )
        })?;

    Ok(status.code().unwrap_or(0))
}