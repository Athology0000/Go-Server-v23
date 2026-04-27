use std::path::{Path, PathBuf};
use std::process::Command;
use std::{fs, io};

pub struct LaunchConfig<'a> {
    pub java_exe: &'a str,
    pub game_dir: &'a str,
    pub mods_dir: &'a str,
    pub session_file: &'a Path,
    pub jar_path: &'a Path,
}

pub fn run(cfg: &LaunchConfig) -> Result<i32, String> {
    copy_jar(cfg.jar_path, cfg.mods_dir)?;

    let session_flag = format!(
        "-Dcobalt.session={}",
        cfg.session_file.to_str().ok_or("Invalid session path")?
    );

    // Fabric launch: classpath + KnotClient main class.
    // The asset index and MC version must match the installed Fabric instance.
    let status = Command::new(cfg.java_exe)
        .arg(&session_flag)
        .arg("-cp")
        .arg(fabric_classpath(cfg.game_dir)?)
        .arg("net.fabricmc.loader.launch.knot.KnotClient")
        .arg("--gameDir").arg(cfg.game_dir)
        .arg("--assetsDir").arg(format!("{}/assets", cfg.game_dir))
        .arg("--assetIndex").arg("17")
        .arg("--version").arg("1.21.1")
        .spawn()
        .map_err(|e| format!("Failed to spawn Minecraft: {e}"))?
        .wait()
        .map_err(|e| format!("Wait failed: {e}"))?;

    Ok(status.code().unwrap_or(-1))
}

fn copy_jar(src: &Path, mods_dir: &str) -> Result<(), String> {
    let dest = PathBuf::from(mods_dir).join("cobalt.jar");
    fs::create_dir_all(mods_dir).map_err(|e| e.to_string())?;
    fs::copy(src, &dest).map_err(|e| format!("Failed to copy cobalt.jar to mods dir: {e}"))?;
    Ok(())
}

fn fabric_classpath(game_dir: &str) -> Result<String, String> {
    let libs = PathBuf::from(game_dir).join("libraries");
    if !libs.exists() {
        return Err(format!("Libraries dir not found: {}", libs.display()));
    }
    let mut jars = Vec::new();
    collect_jars(&libs, &mut jars).map_err(|e| e.to_string())?;
    let sep = if cfg!(windows) { ";" } else { ":" };
    Ok(jars.iter().map(|p| p.to_str().unwrap_or("")).collect::<Vec<_>>().join(sep))
}

fn collect_jars(dir: &Path, out: &mut Vec<PathBuf>) -> io::Result<()> {
    for entry in fs::read_dir(dir)? {
        let entry = entry?;
        let path = entry.path();
        if path.is_dir() {
            collect_jars(&path, out)?;
        } else if path.extension().map_or(false, |e| e == "jar") {
            out.push(path);
        }
    }
    Ok(())
}
