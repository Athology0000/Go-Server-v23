plugins {
    id("fabric-loom") apply false
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
}

allprojects {
    group = property("group") as String
    version = property("mod_version") as String
}
