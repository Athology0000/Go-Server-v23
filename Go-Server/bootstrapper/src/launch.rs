use std::ffi::OsStr;
use std::path::{Path, PathBuf};
use std::process::{Command, Stdio};
use std::{env, fs, io};

pub struct LaunchConfig<'a> {
    pub java_exe: &'a str,
    pub game_dir: &'a str,
    pub mods_dir: &'a str,
    pub session_file: &'a Path,
    pub jar_path: &'a Path,
}

pub fn run(cfg: &LaunchConfig) -> Result<i32, String> {
    copy_jar(cfg.jar_path, cfg.mods_dir)?;

    let game_dir = resolve_path_no_verbatim(Path::new(cfg.game_dir))?;
    let prism_root = find_prism_root(&game_dir)?;
    let assets_dir = prism_root.join("assets");
    let libraries_dir = prism_root.join("libraries");

    let natives_dir = prepare_natives_dir(&game_dir, &libraries_dir)?;

    println!("Using game dir: {}", display_path(&game_dir));
    println!("Using Prism root: {}", display_path(&prism_root));
    println!("Using libraries dir: {}", display_path(&libraries_dir));
    println!("Using assets dir: {}", display_path(&assets_dir));
    println!("Using natives dir: {}", display_path(&natives_dir));

    let classpath = build_fabric_client_classpath(&libraries_dir)?;
    let args_file = write_java_args_file(
        &game_dir,
        &assets_dir,
        &natives_dir,
        cfg.session_file,
        &classpath,
    )?;

    println!("Using Java args file: {}", display_path(&args_file));

    let status = Command::new(cfg.java_exe)
        .arg(format!("@{}", java_path(&args_file)))
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .spawn()
        .map_err(|e| format!("Failed to spawn Minecraft: {e}"))?
        .wait()
        .map_err(|e| format!("Wait failed: {e}"))?;

    Ok(status.code().unwrap_or(-1))
}

fn write_java_args_file(
    game_dir: &Path,
    assets_dir: &Path,
    natives_dir: &Path,
    session_file: &Path,
    classpath: &str,
) -> Result<PathBuf, String> {
    let args_file = game_dir
        .join("config")
        .join("cobalt")
        .join("cobalt_launch_args.txt");

    if let Some(parent) = args_file.parent() {
        fs::create_dir_all(parent)
            .map_err(|e| format!("Failed to create args file directory {}: {e}", parent.display()))?;
    }

    let session_path = resolve_path_no_verbatim(session_file)?;
    let session_path = java_path(&session_path);
    let game_dir_str = java_path(game_dir);
    let assets_dir_str = java_path(assets_dir);
    let natives_dir_str = java_path(natives_dir);

    let contents = format!(
        "\
-Dcobalt.session={}
-Djava.library.path={}
-Dorg.lwjgl.librarypath={}
-Dorg.lwjgl.system.SharedLibraryExtractPath={}
-Dorg.lwjgl.system.SharedLibraryExtractDirectory={}
-cp
{}
net.fabricmc.loader.impl.launch.knot.KnotClient
--gameDir
{}
--assetsDir
{}
--assetIndex
29
--version
1.21.11
",
        session_path,
        natives_dir_str,
        natives_dir_str,
        natives_dir_str,
        natives_dir_str,
        classpath,
        game_dir_str,
        assets_dir_str,
    );

    fs::write(&args_file, contents)
        .map_err(|e| format!("Failed to write Java args file {}: {e}", args_file.display()))?;

    Ok(args_file)
}

fn build_fabric_client_classpath(libraries_dir: &Path) -> Result<String, String> {
    let required = vec![
        libraries_dir.join("net/fabricmc/fabric-loader/0.18.4/fabric-loader-0.18.4.jar"),
        libraries_dir.join("net/fabricmc/intermediary/1.21.11/intermediary-1.21.11.jar"),
        libraries_dir.join("net/fabricmc/sponge-mixin/0.17.0+mixin.0.8.7/sponge-mixin-0.17.0+mixin.0.8.7.jar"),
        libraries_dir.join("com/mojang/minecraft/1.21.11/minecraft-1.21.11-client.jar"),

        libraries_dir.join("org/ow2/asm/asm/9.9/asm-9.9.jar"),
        libraries_dir.join("org/ow2/asm/asm-analysis/9.9/asm-analysis-9.9.jar"),
        libraries_dir.join("org/ow2/asm/asm-commons/9.9/asm-commons-9.9.jar"),
        libraries_dir.join("org/ow2/asm/asm-tree/9.9/asm-tree-9.9.jar"),
        libraries_dir.join("org/ow2/asm/asm-util/9.9/asm-util-9.9.jar"),

        libraries_dir.join("net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar"),
        libraries_dir.join("com/google/guava/guava/33.5.0-jre/guava-33.5.0-jre.jar"),
        libraries_dir.join("com/google/guava/failureaccess/1.0.3/failureaccess-1.0.3.jar"),
        libraries_dir.join("com/google/code/gson/gson/2.13.2/gson-2.13.2.jar"),

        libraries_dir.join("org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar"),
        libraries_dir.join("org/apache/logging/log4j/log4j-api/2.25.2/log4j-api-2.25.2.jar"),
        libraries_dir.join("org/apache/logging/log4j/log4j-core/2.25.2/log4j-core-2.25.2.jar"),
        libraries_dir.join("org/apache/logging/log4j/log4j-slf4j2-impl/2.25.2/log4j-slf4j2-impl-2.25.2.jar"),

        libraries_dir.join("org/lwjgl/lwjgl/3.3.3/lwjgl-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-glfw/3.3.3/lwjgl-glfw-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-jemalloc/3.3.3/lwjgl-jemalloc-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-openal/3.3.3/lwjgl-openal-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-opengl/3.3.3/lwjgl-opengl-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-stb/3.3.3/lwjgl-stb-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-tinyfd/3.3.3/lwjgl-tinyfd-3.3.3.jar"),
        libraries_dir.join("org/lwjgl/lwjgl-freetype/3.3.3/lwjgl-freetype-3.3.3.jar"),

        libraries_dir.join("it/unimi/dsi/fastutil/8.5.18/fastutil-8.5.18.jar"),
        libraries_dir.join("com/ibm/icu/icu4j/77.1/icu4j-77.1.jar"),
        libraries_dir.join("com/mojang/authlib/7.0.61/authlib-7.0.61.jar"),
        libraries_dir.join("com/mojang/blocklist/1.0.10/blocklist-1.0.10.jar"),
        libraries_dir.join("com/mojang/brigadier/1.3.10/brigadier-1.3.10.jar"),
        libraries_dir.join("com/mojang/datafixerupper/9.0.19/datafixerupper-9.0.19.jar"),
        libraries_dir.join("com/mojang/jtracy/1.0.37/jtracy-1.0.37.jar"),
        libraries_dir.join("com/mojang/jtracy-natives-windows/1.0.37/jtracy-natives-windows-1.0.37.jar"),
        libraries_dir.join("com/mojang/logging/1.6.11/logging-1.6.11.jar"),
        libraries_dir.join("com/mojang/patchy/2.2.10/patchy-2.2.10.jar"),
        libraries_dir.join("com/mojang/text2speech/1.18.11/text2speech-1.18.11.jar"),

        libraries_dir.join("commons-codec/commons-codec/1.19.0/commons-codec-1.19.0.jar"),
        libraries_dir.join("commons-io/commons-io/2.20.0/commons-io-2.20.0.jar"),
        libraries_dir.join("org/apache/commons/commons-compress/1.28.0/commons-compress-1.28.0.jar"),
        libraries_dir.join("org/apache/commons/commons-lang3/3.19.0/commons-lang3-3.19.0.jar"),

        libraries_dir.join("org/jcraft/jorbis/0.0.17/jorbis-0.0.17.jar"),
        libraries_dir.join("org/joml/joml/1.10.8/joml-1.10.8.jar"),
        libraries_dir.join("org/jspecify/jspecify/1.0.0/jspecify-1.0.0.jar"),

        // Netty 4.2.7, needed by Minecraft 1.21.11.
        libraries_dir.join("io/netty/netty-buffer/4.2.7.Final/netty-buffer-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-codec-base/4.2.7.Final/netty-codec-base-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-codec-compression/4.2.7.Final/netty-codec-compression-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-codec-http/4.2.7.Final/netty-codec-http-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-common/4.2.7.Final/netty-common-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-handler/4.2.7.Final/netty-handler-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-resolver/4.2.7.Final/netty-resolver-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-transport/4.2.7.Final/netty-transport-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-transport-classes-epoll/4.2.7.Final/netty-transport-classes-epoll-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-transport-classes-kqueue/4.2.7.Final/netty-transport-classes-kqueue-4.2.7.Final.jar"),
        libraries_dir.join("io/netty/netty-transport-native-unix-common/4.2.7.Final/netty-transport-native-unix-common-4.2.7.Final.jar"),

        // OSHI/JNA for hardware detection.
        libraries_dir.join("com/github/oshi/oshi-core/6.9.0/oshi-core-6.9.0.jar"),
        libraries_dir.join("net/java/dev/jna/jna/5.17.0/jna-5.17.0.jar"),
        libraries_dir.join("net/java/dev/jna/jna-platform/5.17.0/jna-platform-5.17.0.jar"),

        // Azure/MS auth libs present in your Prism 1.21.11 launch.
        libraries_dir.join("com/azure/azure-json/1.4.0/azure-json-1.4.0.jar"),
        libraries_dir.join("com/microsoft/azure/msal4j/1.23.1/msal4j-1.23.1.jar"),
        libraries_dir.join("com/nimbusds/content-type/2.3/content-type-2.3.jar"),
        libraries_dir.join("com/nimbusds/lang-tag/1.7/lang-tag-1.7.jar"),
        libraries_dir.join("com/nimbusds/nimbus-jose-jwt/9.40/nimbus-jose-jwt-9.40.jar"),
        libraries_dir.join("com/nimbusds/oauth2-oidc-sdk/11.18/oauth2-oidc-sdk-11.18.jar"),
        libraries_dir.join("net/minidev/accessors-smart/2.5.1/accessors-smart-2.5.1.jar"),
        libraries_dir.join("net/minidev/json-smart/2.5.1/json-smart-2.5.1.jar"),
    ];

    let mut jars = Vec::new();

    for jar in required {
        if jar.exists() {
            if !jars.contains(&jar) {
                jars.push(jar);
            }
        } else {
            println!("Missing optional/required library from filtered classpath: {}", jar.display());
        }
    }

    let fabric_loader = libraries_dir.join("net/fabricmc/fabric-loader/0.18.4/fabric-loader-0.18.4.jar");

    if !fabric_loader.exists() {
        return Err(format!("Fabric Loader jar missing: {}", fabric_loader.display()));
    }

    if let Some(index) = jars.iter().position(|p| p == &fabric_loader) {
        let loader = jars.remove(index);
        jars.insert(0, loader);
    } else {
        jars.insert(0, fabric_loader);
    }

    let netty_common = libraries_dir.join("io/netty/netty-common/4.2.7.Final/netty-common-4.2.7.Final.jar");
    if !jars.iter().any(|p| p == &netty_common) {
        return Err(format!("Required Netty jar missing from classpath: {}", netty_common.display()));
    }

    let sep = if cfg!(windows) { ";" } else { ":" };

    println!("Filtered classpath jars: {}", jars.len());

    Ok(jars
        .iter()
        .map(|p| java_path(p))
        .collect::<Vec<_>>()
        .join(sep))
}

fn prepare_natives_dir(game_dir: &Path, libraries_dir: &Path) -> Result<PathBuf, String> {
    let natives_dir = game_dir
        .join("config")
        .join("cobalt")
        .join("natives");

    fs::create_dir_all(&natives_dir)
        .map_err(|e| format!("Failed to create natives dir {}: {e}", natives_dir.display()))?;

    let existing_lwjgl = natives_dir.join("lwjgl.dll");
    if existing_lwjgl.exists() {
        return Ok(strip_verbatim_prefix(natives_dir));
    }

    let native_jars = find_windows_native_jars(libraries_dir)?;

    if native_jars.is_empty() {
        return Err(format!(
            "No Windows native LWJGL jars found under {}",
            libraries_dir.display()
        ));
    }

    println!("Extracting {} native jars...", native_jars.len());

    for jar in native_jars {
        extract_dlls_from_jar(&jar, &natives_dir)?;
    }

    if !existing_lwjgl.exists() {
        return Err(format!(
            "Native extraction finished but lwjgl.dll was not found in {}",
            natives_dir.display()
        ));
    }

    Ok(strip_verbatim_prefix(natives_dir))
}

fn find_windows_native_jars(libraries_dir: &Path) -> Result<Vec<PathBuf>, String> {
    let mut all_jars = Vec::new();
    collect_jars(libraries_dir, &mut all_jars).map_err(|e| e.to_string())?;

    let mut native_jars: Vec<PathBuf> = all_jars
        .into_iter()
        .filter(|p| {
            let s = p.to_string_lossy().to_lowercase();

            s.contains("org\\lwjgl")
                && s.contains("natives-windows")
                && s.ends_with(".jar")
                && !s.contains("arm64")
                && !s.contains("x86")
        })
        .collect();

    native_jars.sort();

    Ok(native_jars)
}

fn extract_dlls_from_jar(jar: &Path, dest_dir: &Path) -> Result<(), String> {
    let file = fs::File::open(jar)
        .map_err(|e| format!("Failed to open native jar {}: {e}", jar.display()))?;

    let mut archive = zip::ZipArchive::new(file)
        .map_err(|e| format!("Failed to read native jar {} as zip: {e}", jar.display()))?;

    for i in 0..archive.len() {
        let mut entry = archive
            .by_index(i)
            .map_err(|e| format!("Failed to read zip entry from {}: {e}", jar.display()))?;

        let name = entry.name().to_string();

        if !name.to_lowercase().ends_with(".dll") {
            continue;
        }

        let file_name = Path::new(&name)
            .file_name()
            .and_then(OsStr::to_str)
            .ok_or_else(|| format!("Invalid DLL name in {}: {}", jar.display(), name))?;

        let out_path = dest_dir.join(file_name);

        let mut out = fs::File::create(&out_path)
            .map_err(|e| format!("Failed to create extracted DLL {}: {e}", out_path.display()))?;

        io::copy(&mut entry, &mut out)
            .map_err(|e| format!("Failed to extract DLL {}: {e}", out_path.display()))?;
    }

    Ok(())
}

fn copy_jar(src: &Path, mods_dir: &str) -> Result<(), String> {
    let mods_dir_path = PathBuf::from(mods_dir);

    fs::create_dir_all(&mods_dir_path)
        .map_err(|e| format!("Failed to create mods dir {}: {e}", mods_dir_path.display()))?;

    let dest = mods_dir_path.join("cobalt.jar");

    let src_canon = resolve_path_no_verbatim(src)?;

    let dest_canon = if dest.exists() {
        Some(resolve_path_no_verbatim(&dest)?)
    } else {
        None
    };

    if let Some(dest_canon) = dest_canon {
        if src_canon == dest_canon {
            println!(
                "cobalt.jar is already in the mods folder, skipping copy: {}",
                dest.display()
            );
            return Ok(());
        }

        fs::remove_file(&dest).map_err(|e| {
            format!(
                "Failed to remove old cobalt.jar at {}. Close Minecraft/Prism if it is still open. Error: {e}",
                dest.display()
            )
        })?;
    }

    fs::copy(&src_canon, &dest).map_err(|e| {
        format!(
            "Failed to copy cobalt.jar from {} to {}. Close Minecraft/Prism if it is still open. Error: {e}",
            display_path(&src_canon),
            dest.display()
        )
    })?;

    Ok(())
}

fn find_prism_root(game_dir: &Path) -> Result<PathBuf, String> {
    let mut current = game_dir.to_path_buf();

    for _ in 0..8 {
        if let Some(parent) = current.parent() {
            if parent.join("libraries").exists() && parent.join("assets").exists() {
                return Ok(strip_verbatim_prefix(parent.to_path_buf()));
            }

            current = parent.to_path_buf();
        } else {
            break;
        }
    }

    Err("Could not find PrismLauncher root containing libraries and assets".to_string())
}

fn resolve_path_no_verbatim(path: &Path) -> Result<PathBuf, String> {
    let absolute = if path.is_absolute() {
        path.to_path_buf()
    } else {
        env::current_dir()
            .map_err(|e| format!("Failed to get current dir: {e}"))?
            .join(path)
    };

    let canonical = fs::canonicalize(&absolute)
        .map_err(|e| format!("Failed to resolve path {}: {e}", absolute.display()))?;

    Ok(strip_verbatim_prefix(canonical))
}

fn strip_verbatim_prefix(path: PathBuf) -> PathBuf {
    let s = path.to_string_lossy().to_string();

    if let Some(stripped) = s.strip_prefix(r"\\?\") {
        return PathBuf::from(stripped);
    }

    if let Some(stripped) = s.strip_prefix(r"\??\") {
        return PathBuf::from(stripped);
    }

    path
}

fn display_path(path: &Path) -> String {
    let s = path.to_string_lossy().to_string();

    if let Some(stripped) = s.strip_prefix(r"\\?\") {
        return stripped.to_string();
    }

    if let Some(stripped) = s.strip_prefix(r"\??\") {
        return stripped.to_string();
    }

    s
}

fn java_path(path: &Path) -> String {
    display_path(path).replace('\\', "/")
}

fn collect_jars(dir: &Path, out: &mut Vec<PathBuf>) -> io::Result<()> {
    for entry in fs::read_dir(dir)? {
        let entry = entry?;
        let path = strip_verbatim_prefix(entry.path());

        if path.is_dir() {
            collect_jars(&path, out)?;
        } else if path.extension().map_or(false, |e| e == "jar") {
            out.push(path);
        }
    }

    Ok(())
}