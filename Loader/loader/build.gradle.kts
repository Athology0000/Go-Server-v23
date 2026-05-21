plugins {
    id("fabric-loom")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

base {
    archivesName.set("cobalt-loader")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    implementation(project(":shared-api"))
    include(project(":shared-api"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("serialization_version")}")
    include("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("serialization_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
