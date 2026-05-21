
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.SecureRandom
import java.util.Base64
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.loom)
  `maven-publish`
}

val baseGroup: String by project
val modVersion: String by project
val modName: String by project
val gradlePropertiesFile = rootProject.file("gradle.properties")
val requestedTaskNames = gradle.startParameter.taskNames.map { it.substringAfterLast(':') }.toSet()
val buildChannel =
  providers.gradleProperty("phantomBuildChannel").orNull?.trim()?.lowercase()
    ?: when {
      requestedTaskNames.any { it in setOf("buildDev", "deployDev", "deploydev") } -> "dev"
      else -> "release"
    }

if (buildChannel !in setOf("dev", "release")) {
  throw GradleException("phantomBuildChannel must be 'dev' or 'release', found '$buildChannel'.")
}

val isDevBuild = buildChannel == "dev"
val artifactDisplayName = if (isDevBuild) "Phantom Client Dev" else "Phantom Client"
val artifactBaseName = if (isDevBuild) "${modName}-dev" else modName

fun shouldAutoBumpBuildVersion(): Boolean =
  !isDevBuild && requestedTaskNames.any { taskName ->
    taskName == "build" || taskName == "buildRelease"
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

version = if (isDevBuild) "$resolvedModVersion-dev" else resolvedModVersion
group = baseGroup

base {
  archivesName.set(artifactBaseName)
}

repositories {
  mavenCentral()
  maven("https://maven.meteordev.org/releases")
  maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
  maven("https://repo.hypixel.net/repository/Hypixel/")
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

  modImplementation(libs.hypixel.api) { include(this) }

  runtimeOnly(libs.httpclient)
  modRuntimeOnly(libs.devauth)
}

tasks {
  named<JavaExec>("runClient") {
    val devMockAuth =
      System.getProperty("phantom.devMockAuth")
        ?: System.getenv("PHANTOM_DEV_MOCK_AUTH")
        ?: "true"
    systemProperty("phantom.devMockAuth", devMockAuth)
  }

  processResources {
    val fabricKotlinVersion = libs.versions.fabric.kotlin.get()
    val fabricLoaderVersion = libs.versions.fabric.loader.get()
    val minecraftVersion = libs.versions.minecraft.version.get()

    inputs.property("version", project.version)
    inputs.property("displayName", artifactDisplayName)
    inputs.property("fabricKotlinVersion", fabricKotlinVersion)
    inputs.property("fabricLoaderVersion", fabricLoaderVersion)
    inputs.property("minecraftVersion", minecraftVersion)

    filesMatching("fabric.mod.json") {
      expand(
        "version" to project.version,
        "displayName" to artifactDisplayName,
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

  named<JavaCompile>("compileJava") {
    dependsOn("compileKotlin")
    doFirst {
      classpath += files(layout.buildDirectory.dir("classes/kotlin/main"))
    }
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

// â”€â”€ Native DLL build (Windows, requires VS2026 + CMake) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
val phantomRemoteAddonId = "phantom"
val phantomRemoteAddonEntrypoint = "org.phantom.internal.remote.PhantomRemoteAddon"
val phantomCoreAddonId = "phantom-core"
val phantomCoreAddonEntrypoint = "org.phantom.internal.remote.PhantomCoreAddon"
val phantomMiningAddonId = "phantom-mining"
val phantomMiningAddonEntrypoint = "org.phantom.internal.remote.PhantomMiningAddon"
val phantomSlayerAddonId = "phantom-slayer"
val phantomSlayerAddonEntrypoint = "org.phantom.internal.remote.PhantomSlayerAddon"
val phantomDianaAddonId = "phantom-diana"
val phantomDianaAddonEntrypoint = "org.phantom.internal.remote.PhantomDianaAddon"
val phantomProtectedIncludes = listOf(
  "org/phantom/internal/BuiltinModules.class",
  "org/phantom/internal/remote/**",
  "org/phantom/internal/chat/**",
  "org/phantom/internal/combat/**",
  "org/phantom/internal/crimson/**",
  "org/phantom/internal/diana/**",
  "org/phantom/internal/dungeons/**",
  "org/phantom/internal/etherwarp/**",
  "org/phantom/internal/farming/**",
  "org/phantom/internal/fishing/**",
  "org/phantom/internal/garden/**",
  "org/phantom/internal/grotto/**",
  "org/phantom/internal/mining/**",
  "org/cobalt/internal/mining/**",
  "org/phantom/internal/pig/**",
  "org/phantom/internal/qol/**",
  "org/phantom/internal/seal/**",
  "org/phantom/internal/spotify/**",
  "org/phantom/internal/wardrobe/**",
)
val phantomCoreIncludes = listOf(
  "org/phantom/internal/remote/PhantomCoreAddon.class",
  "org/phantom/internal/account/**",
  "org/phantom/internal/debug/**",
  "org/phantom/internal/helper/**",
  "org/phantom/internal/pathfinding/**",
  "org/phantom/internal/performance/**",
  "org/phantom/internal/qol/**",
  "org/phantom/internal/rotation/**",
  "org/phantom/internal/routes/**",
  "org/phantom/internal/scheduler/**",
  "org/phantom/internal/skyblock/**",
  "org/phantom/internal/stats/**",
  "org/phantom/internal/ui/**",
  "org/phantom/internal/visual/**",
  "org/phantom/internal/wardrobe/**",
)
val phantomMiningIncludes = listOf(
  "org/phantom/internal/remote/PhantomMiningAddon.class",
  "org/phantom/internal/combat/*.class",
  "org/phantom/internal/etherwarp/**",
  "org/phantom/internal/grotto/**",
  "org/phantom/internal/mining/**",
  "org/cobalt/internal/mining/**",
)
val phantomSlayerIncludes = listOf(
  "org/phantom/internal/remote/PhantomSlayerAddon.class",
  "org/phantom/internal/combat/*.class",
  "org/phantom/internal/combat/slayer/**",
)
val phantomDianaIncludes = listOf(
  "org/phantom/internal/remote/PhantomDianaAddon.class",
  "org/phantom/internal/diana/**",
  "org/phantom/internal/etherwarp/**",
  "org/phantom/internal/qol/**",
)

val splitProtectedModules =
  providers.gradleProperty("phantomSplitModules")
    .map { it.equals("true", ignoreCase = true) }
    .orElse(!isDevBuild)

tasks.named<Jar>("jar") {
  if (splitProtectedModules.get()) {
    exclude(phantomProtectedIncludes)
  }
}

fun isPhantomProtectedEntry(entryName: String): Boolean =
  phantomProtectedIncludes.any { pattern ->
    if (pattern.endsWith("/**")) {
      entryName.startsWith(pattern.removeSuffix("**"))
    } else {
      entryName == pattern
    }
  }

val stripProtectedClassesFromRuntimeJar = tasks.register("stripProtectedClassesFromRuntimeJar") {
  group = "build"
  description = "Removes protected module bytecode from the runtime starter JAR."
  onlyIf { splitProtectedModules.get() }

  val runtimeJar = layout.buildDirectory.file("libs/${artifactBaseName}-${project.version}.jar")
  inputs.file(runtimeJar)
  outputs.file(runtimeJar)
  outputs.upToDateWhen { false }

  doLast {
    val jarFile = runtimeJar.get().asFile
    if (!jarFile.exists()) {
      throw GradleException("Runtime JAR does not exist: ${jarFile.path}")
    }

    val tempFile = jarFile.resolveSibling("${jarFile.name}.stripped")
    JarFile(jarFile).use { input ->
      JarOutputStream(tempFile.outputStream()).use { output ->
        input.entries().asSequence().forEach { entry ->
          if (isPhantomProtectedEntry(entry.name)) return@forEach

          val copy = JarEntry(entry.name)
          copy.time = entry.time
          output.putNextEntry(copy)
          if (!entry.isDirectory) {
            input.getInputStream(entry).use { it.copyTo(output) }
          }
          output.closeEntry()
        }
      }
    }

    Files.move(tempFile.toPath(), jarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
  }
}

tasks.named("remapJar") {
  finalizedBy(stripProtectedClassesFromRuntimeJar)
}

val generatePhantomRemoteManifest = tasks.register("generatePhantomRemoteManifest") {
  val outputDir = layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomRemoteAddonId")
  outputs.dir(outputDir)

  doLast {
    val dir = outputDir.get().asFile
    dir.mkdirs()
    dir.resolve("phantom.addon.json").writeText(
      """
      {
        "id": "$phantomRemoteAddonId",
        "name": "Phantom",
        "version": "${project.version}",
        "entrypoints": ["$phantomRemoteAddonEntrypoint"],
        "mixins": []
      }
      """.trimIndent()
    )
  }
}

val generatePhantomMiningRemoteManifest = tasks.register("generatePhantomMiningRemoteManifest") {
  val outputDir = layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomMiningAddonId")
  outputs.dir(outputDir)

  doLast {
    val dir = outputDir.get().asFile
    dir.mkdirs()
    dir.resolve("phantom.addon.json").writeText(
      """
      {
        "id": "$phantomMiningAddonId",
        "name": "Phantom Mining",
        "version": "${project.version}",
        "entrypoints": ["$phantomMiningAddonEntrypoint"],
        "mixins": []
      }
      """.trimIndent()
    )
  }
}

val generatePhantomCoreRemoteManifest = tasks.register("generatePhantomCoreRemoteManifest") {
  val outputDir = layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomCoreAddonId")
  outputs.dir(outputDir)

  doLast {
    val dir = outputDir.get().asFile
    dir.mkdirs()
    dir.resolve("phantom.addon.json").writeText(
      """
      {
        "id": "$phantomCoreAddonId",
        "name": "Phantom Core",
        "version": "${project.version}",
        "entrypoints": ["$phantomCoreAddonEntrypoint"],
        "mixins": []
      }
      """.trimIndent()
    )
  }
}

val generatePhantomSlayerRemoteManifest = tasks.register("generatePhantomSlayerRemoteManifest") {
  val outputDir = layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomSlayerAddonId")
  outputs.dir(outputDir)

  doLast {
    val dir = outputDir.get().asFile
    dir.mkdirs()
    dir.resolve("phantom.addon.json").writeText(
      """
      {
        "id": "$phantomSlayerAddonId",
        "name": "Phantom Slayer",
        "version": "${project.version}",
        "entrypoints": ["$phantomSlayerAddonEntrypoint"],
        "mixins": []
      }
      """.trimIndent()
    )
  }
}

val generatePhantomDianaRemoteManifest = tasks.register("generatePhantomDianaRemoteManifest") {
  val outputDir = layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomDianaAddonId")
  outputs.dir(outputDir)

  doLast {
    val dir = outputDir.get().asFile
    dir.mkdirs()
    dir.resolve("phantom.addon.json").writeText(
      """
      {
        "id": "$phantomDianaAddonId",
        "name": "Phantom Diana",
        "version": "${project.version}",
        "entrypoints": ["$phantomDianaAddonEntrypoint"],
        "mixins": []
      }
      """.trimIndent()
    )
  }
}

tasks.register<Jar>("packagePhantomCoreModuleJar") {
  group = "build"
  description = "Packages Phantom's protected core helper bytecode as a remote addon JAR."
  dependsOn("classes", generatePhantomCoreRemoteManifest)

  archiveFileName.set("$phantomCoreAddonId.jar")
  destinationDirectory.set(layout.buildDirectory.dir("phantom-modules/plain"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(sourceSets.main.get().output) {
    phantomCoreIncludes.forEach { include(it) }
  }
  from(layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomCoreAddonId"))
}

tasks.register<Jar>("packagePhantomModuleJar") {
  group = "build"
  description = "Packages Phantom's protected module bytecode as a remote addon JAR."
  dependsOn("classes", generatePhantomRemoteManifest)

  archiveFileName.set("$phantomRemoteAddonId.jar")
  destinationDirectory.set(layout.buildDirectory.dir("phantom-modules/plain"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(sourceSets.main.get().output) {
    phantomProtectedIncludes.forEach { include(it) }
  }
  from(layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomRemoteAddonId"))
}

tasks.register<Jar>("packagePhantomMiningModuleJar") {
  group = "build"
  description = "Packages Phantom's protected mining module bytecode as a remote addon JAR."
  dependsOn("classes", generatePhantomMiningRemoteManifest)

  archiveFileName.set("$phantomMiningAddonId.jar")
  destinationDirectory.set(layout.buildDirectory.dir("phantom-modules/plain"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(sourceSets.main.get().output) {
    phantomMiningIncludes.forEach { include(it) }
  }
  from(layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomMiningAddonId"))
}

tasks.register<Jar>("packagePhantomSlayerModuleJar") {
  group = "build"
  description = "Packages Phantom's protected Slayer module bytecode as a remote addon JAR."
  dependsOn("classes", generatePhantomSlayerRemoteManifest)

  archiveFileName.set("$phantomSlayerAddonId.jar")
  destinationDirectory.set(layout.buildDirectory.dir("phantom-modules/plain"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(sourceSets.main.get().output) {
    phantomSlayerIncludes.forEach { include(it) }
  }
  from(layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomSlayerAddonId"))
}

tasks.register<Jar>("packagePhantomDianaModuleJar") {
  group = "build"
  description = "Packages Phantom's protected Diana module bytecode as a remote addon JAR."
  dependsOn("classes", generatePhantomDianaRemoteManifest)

  archiveFileName.set("$phantomDianaAddonId.jar")
  destinationDirectory.set(layout.buildDirectory.dir("phantom-modules/plain"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  from(sourceSets.main.get().output) {
    phantomDianaIncludes.forEach { include(it) }
  }
  from(layout.buildDirectory.dir("generated/phantom-remote-manifest/$phantomDianaAddonId"))
}

fun moduleEncryptionKey(): ByteArray {
  val raw = System.getenv("MODULE_ENCRYPTION_KEY")
    ?: System.getenv("MASTER_KEY")
    ?: throw GradleException("Set MODULE_ENCRYPTION_KEY or MASTER_KEY to base64-encoded 32 bytes before encrypting modules.")

  val decoded = Base64.getDecoder().decode(raw)
  if (decoded.size != 32) {
    throw GradleException("Module encryption key must decode to 32 bytes, got ${decoded.size}.")
  }
  return decoded
}

fun encryptAesGcm(key: ByteArray, plaintext: ByteArray): ByteArray {
  val nonce = ByteArray(12)
  SecureRandom().nextBytes(nonce)

  val cipher = Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, nonce))

  return nonce + cipher.doFinal(plaintext)
}

tasks.register("encryptPhantomModule") {
  group = "build"
  description = "Encrypts the protected Phantom module bundle into phantom.enc."
  dependsOn("packagePhantomModuleJar")

  val plainJar = layout.buildDirectory.file("phantom-modules/plain/$phantomRemoteAddonId.jar")
  val encryptedFile = layout.buildDirectory.file("phantom-modules/encrypted/$phantomRemoteAddonId.enc")

  inputs.file(plainJar)
  outputs.file(encryptedFile)

  doLast {
    val output = encryptedFile.get().asFile
    output.parentFile.mkdirs()
    output.writeBytes(encryptAesGcm(moduleEncryptionKey(), plainJar.get().asFile.readBytes()))
  }
}

tasks.register("encryptPhantomCoreModule") {
  group = "build"
  description = "Encrypts the protected Phantom core bundle into phantom-core.enc."
  dependsOn("packagePhantomCoreModuleJar")

  val plainJar = layout.buildDirectory.file("phantom-modules/plain/$phantomCoreAddonId.jar")
  val encryptedFile = layout.buildDirectory.file("phantom-modules/encrypted/$phantomCoreAddonId.enc")

  inputs.file(plainJar)
  outputs.file(encryptedFile)

  doLast {
    val output = encryptedFile.get().asFile
    output.parentFile.mkdirs()
    output.writeBytes(encryptAesGcm(moduleEncryptionKey(), plainJar.get().asFile.readBytes()))
  }
}

tasks.register("encryptPhantomMiningModule") {
  group = "build"
  description = "Encrypts the protected Phantom mining module bundle into phantom-mining.enc."
  dependsOn("packagePhantomMiningModuleJar")

  val plainJar = layout.buildDirectory.file("phantom-modules/plain/$phantomMiningAddonId.jar")
  val encryptedFile = layout.buildDirectory.file("phantom-modules/encrypted/$phantomMiningAddonId.enc")

  inputs.file(plainJar)
  outputs.file(encryptedFile)

  doLast {
    val output = encryptedFile.get().asFile
    output.parentFile.mkdirs()
    output.writeBytes(encryptAesGcm(moduleEncryptionKey(), plainJar.get().asFile.readBytes()))
  }
}

tasks.register("encryptPhantomSlayerModule") {
  group = "build"
  description = "Encrypts the protected Phantom Slayer module bundle into phantom-slayer.enc."
  dependsOn("packagePhantomSlayerModuleJar")

  val plainJar = layout.buildDirectory.file("phantom-modules/plain/$phantomSlayerAddonId.jar")
  val encryptedFile = layout.buildDirectory.file("phantom-modules/encrypted/$phantomSlayerAddonId.enc")

  inputs.file(plainJar)
  outputs.file(encryptedFile)

  doLast {
    val output = encryptedFile.get().asFile
    output.parentFile.mkdirs()
    output.writeBytes(encryptAesGcm(moduleEncryptionKey(), plainJar.get().asFile.readBytes()))
  }
}

tasks.register("encryptPhantomDianaModule") {
  group = "build"
  description = "Encrypts the protected Phantom Diana module bundle into phantom-diana.enc."
  dependsOn("packagePhantomDianaModuleJar")

  val plainJar = layout.buildDirectory.file("phantom-modules/plain/$phantomDianaAddonId.jar")
  val encryptedFile = layout.buildDirectory.file("phantom-modules/encrypted/$phantomDianaAddonId.enc")

  inputs.file(plainJar)
  outputs.file(encryptedFile)

  doLast {
    val output = encryptedFile.get().asFile
    output.parentFile.mkdirs()
    output.writeBytes(encryptAesGcm(moduleEncryptionKey(), plainJar.get().asFile.readBytes()))
  }
}

tasks.register("packagePhantomModules") {
  group = "build"
  description = "Packages Phantom's protected module bundles."
  dependsOn(
    "packagePhantomCoreModuleJar",
    "packagePhantomMiningModuleJar",
    "packagePhantomSlayerModuleJar",
    "packagePhantomDianaModuleJar"
  )
}

tasks.register("encryptPhantomModules") {
  group = "build"
  description = "Packages and encrypts Phantom's protected module bundles."
  dependsOn(
    "encryptPhantomCoreModule",
    "encryptPhantomMiningModule",
    "encryptPhantomSlayerModule",
    "encryptPhantomDianaModule"
  )
}

tasks.register<Copy>("syncPhantomNativeToServer") {
  group = "build"
  description = "Copies Phantom native components into the Go server content/native directory."
  from(layout.projectDirectory.file("src/main/resources/natives/windows/phantom_pathfinder.dll"))
  into(layout.projectDirectory.dir("Go-Server/server/content/native"))
}

tasks.register<Copy>("syncPhantomModulesToServer") {
  group = "build"
  description = "Copies encrypted Phantom module bundles into the Go server content/modules directory."
  dependsOn("encryptPhantomModules", "syncPhantomNativeToServer")
  from(layout.buildDirectory.dir("phantom-modules/encrypted")) {
    include("$phantomCoreAddonId.enc")
    include("$phantomMiningAddonId.enc")
    include("$phantomSlayerAddonId.enc")
    include("$phantomDianaAddonId.enc")
  }
  into(layout.projectDirectory.dir("Go-Server/server/content/modules"))
}

val nativesDir = file("natives")
val dllReleasePath = file("natives/build/Release/phantom_pathfinder.dll")
val dllResourceDest = file("src/main/resources/natives/windows")

tasks.register<Exec>("buildNative") {
  group = "build"
  description = "Compiles phantom_pathfinder.dll via CMake."
  workingDir = nativesDir
  commandLine(
    "cmd", "/c",
    "cmake --build build --config Release"
  )
  onlyIf { nativesDir.resolve("build").exists() }
}

tasks.register<Copy>("copyNativeDll") {
  group = "build"
  description = "Copies the built phantom_pathfinder.dll into resources."
  dependsOn("buildNative")
  from(dllReleasePath)
  into(dllResourceDest)
  onlyIf { dllReleasePath.exists() }
}

tasks.named("processResources") {
  dependsOn("copyNativeDll")
}

// â”€â”€ Deploy JAR to Prism mods folder â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
val modsDir = file("C:/Users/aeare/AppData/Roaming/PrismLauncher/instances/1.21.11(1)/minecraft/mods")
val releaseBranchName = "release"

tasks.register("buildDev") {
  group = "build"
  description = "Builds the dev artifact."
  dependsOn("build")
}

tasks.register("buildRelease") {
  group = "build"
  description = "Builds the release artifact."
  dependsOn("build")
}

tasks.register("verifyReleaseBranch") {
  group = "build"
  description = "Fails unless the current git branch is the release branch."
  doLast {
    val process = ProcessBuilder("git", "branch", "--show-current")
      .directory(rootDir)
      .redirectErrorStream(true)
      .start()
    val currentBranch = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    if (currentBranch != releaseBranchName) {
      val branchLabel = if (currentBranch.isBlank()) "unknown" else currentBranch
      throw GradleException("deploy must be run from '$releaseBranchName' branch. Current branch: '$branchLabel'.")
    }
  }
}

tasks.register("copyBuiltMod") {
  group = "build"
  description = "Copies the built JAR to the Prism Launcher mods folder."
  dependsOn("build")
  doLast {
    val jarName = "${base.archivesName.get()}-${project.version}.jar"
    val sourceJar = layout.buildDirectory.file("libs/$jarName").get().asFile.toPath()
    val targetJar = modsDir.toPath().resolve(jarName)
    val tempJar = modsDir.toPath().resolve("$jarName.tmp")

    Files.createDirectories(modsDir.toPath())
    Files.newDirectoryStream(modsDir.toPath(), "${modName}*.jar").use { existingJars ->
      existingJars
        .filter { it.fileName.toString() != jarName }
        .forEach {
          runCatching { Files.deleteIfExists(it) }
            .onFailure { error ->
              logger.warn("Could not delete old mod jar ${it.fileName}: ${error.message}")
            }
        }
    }
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

tasks.register("deployDev") {
  group = "build"
  description = "Builds and copies the dev artifact to the Prism Launcher mods folder."
  dependsOn("copyBuiltMod")
}

tasks.register("deploydev") {
  group = "build"
  description = "Alias for deployDev."
  dependsOn("deployDev")
}

tasks.register("deploy") {
  group = "build"
  description = "Builds and copies the release artifact to the Prism Launcher mods folder. Only allowed on the release branch."
  dependsOn("verifyReleaseBranch", "copyBuiltMod")
}

// â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

tasks.register<Copy>("collectObfLibs") {
  group = "build"
  description = "Copies compile classpath jars to build/obf-libs for Skidfuscator -li."
  from(configurations["compileClasspath"].resolvedConfiguration.resolvedArtifacts.map { it.file })
  into(layout.buildDirectory.dir("obf-libs"))
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
