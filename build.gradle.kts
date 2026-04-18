import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.GradleException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.loom)
  `maven-publish`
}

val baseGroup: String by project
val modVersion: String by project
val modName: String by project
val gradlePropertiesFile = rootProject.file("gradle.properties")

fun shouldAutoBumpBuildVersion(): Boolean =
  gradle.startParameter.taskNames.any { taskName ->
    taskName.substringAfterLast(':') == "build"
  }

fun incrementMinorVersion(version: String): String {
  val parts = version.split('.')
  if (parts.size !in 2..3 || parts.any { it.toIntOrNull() == null }) {
    throw GradleException("modVersion must use numeric major.minor or major.minor.patch format, found \"$version\".")
  }

  val major = parts[0].toInt()
  val minor = parts[1].toInt() + 1
  return if (parts.size == 3) "$major.$minor.0" else "$major.$minor"
}

fun persistModVersion(nextVersion: String) {
  val content = gradlePropertiesFile.readText()
  val pattern = Regex("""(?m)^modVersion=.*$""")
  if (!pattern.containsMatchIn(content)) {
    throw GradleException("Could not find modVersion in ${gradlePropertiesFile.path}.")
  }
  gradlePropertiesFile.writeText(content.replaceFirst(pattern, "modVersion=$nextVersion"))
}

val resolvedModVersion =
  if (shouldAutoBumpBuildVersion()) {
    incrementMinorVersion(modVersion).also(::persistModVersion)
  } else {
    modVersion
  }

version = resolvedModVersion
group = baseGroup

base {
  archivesName.set(modName)
}

repositories {
  mavenCentral()
  maven("https://maven.meteordev.org/releases")
  maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
  minecraft(libs.minecraft)
  mappings(loom.officialMojangMappings())
  modImplementation(libs.bundles.fabric)

  implementation(libs.nanovg) { include(this) }
  implementation(libs.bundles.included) { include(this) }

  listOf("windows", "linux", "macos", "macos-arm64").forEach {
    implementation(variantOf(libs.nanovg) { classifier("natives-$it") }) {
      include(this)
    }
  }

  runtimeOnly(libs.httpclient)
  modRuntimeOnly(libs.devauth)
}

tasks {
  processResources {
    val fabricKotlinVersion = libs.versions.fabric.kotlin.get()
    val fabricLoaderVersion = libs.versions.fabric.loader.get()
    val minecraftVersion = libs.versions.minecraft.version.get()

    inputs.property("version", project.version)
    inputs.property("fabricKotlinVersion", fabricKotlinVersion)
    inputs.property("fabricLoaderVersion", fabricLoaderVersion)
    inputs.property("minecraftVersion", minecraftVersion)

    filesMatching("fabric.mod.json") {
      expand(
        "version" to project.version,
        "fabricKotlinVersion" to fabricKotlinVersion,
        "fabricLoaderVersion" to fabricLoaderVersion,
        "minecraftVersion" to minecraftVersion,
      )
    }
  }

  compileKotlin {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_21
    }
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

// ── Native DLL build (Windows, requires VS2026 + CMake) ───────────────────────
val nativesDir = file("natives")
val dllReleasePath = file("natives/build/Release/cobalt_pathfinder.dll")
val dllResourceDest = file("src/main/resources/natives/windows")

tasks.register<Exec>("buildNative") {
  group = "build"
  description = "Compiles cobalt_pathfinder.dll via CMake."
  workingDir = nativesDir
  commandLine(
    "cmd", "/c",
    "cmake --build build --config Release"
  )
  onlyIf { nativesDir.resolve("build").exists() }
}

tasks.register<Copy>("copyNativeDll") {
  group = "build"
  description = "Copies the built cobalt_pathfinder.dll into resources."
  dependsOn("buildNative")
  from(dllReleasePath)
  into(dllResourceDest)
  onlyIf { dllReleasePath.exists() }
}

tasks.named("processResources") {
  dependsOn("copyNativeDll")
}

// ── Deploy JAR to Prism mods folder ──────────────────────────────────────────
val modsDir = file("C:/Users/aeare/AppData/Roaming/PrismLauncher/instances/1.21.11(1)/minecraft/mods")

tasks.register("deployMod") {
  group = "build"
  description = "Copies the built JAR to the Prism Launcher mods folder."
  dependsOn("build")
  doLast {
    val sourceJar = layout.buildDirectory.file("libs/${modName}-${version}.jar").get().asFile.toPath()
    val targetJar = modsDir.toPath().resolve("${modName}-${version}.jar")
    val tempJar = modsDir.toPath().resolve("${modName}-${version}.jar.tmp")

    Files.createDirectories(modsDir.toPath())
    Files.copy(sourceJar, tempJar, StandardCopyOption.REPLACE_EXISTING)
    runCatching {
      Files.move(
        tempJar,
        targetJar,
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.ATOMIC_MOVE
      )
    }.getOrElse {
      Files.move(tempJar, targetJar, StandardCopyOption.REPLACE_EXISTING)
    }
  }
}

// ─────────────────────────────────────────────────────────────────────────────

tasks.register<Copy>("collectObfLibs") {
  group = "build"
  description = "Copies compile classpath jars to build/obf-libs for Skidfuscator -li."
  from(configurations["compileClasspath"].resolvedConfiguration.resolvedArtifacts.map { it.file })
  into(layout.buildDirectory.dir("obf-libs"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
