pluginManagement {
    val loom_version: String by settings
    val kotlin_version: String by settings

    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("fabric-loom") version loom_version
        kotlin("jvm") version kotlin_version
        kotlin("plugin.serialization") version kotlin_version
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "PhantomLoaderProject"
include("loader")
