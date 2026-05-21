plugins {
    kotlin("jvm")
}

base {
    archivesName.set("sample-module")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(project(":shared-api"))
}
