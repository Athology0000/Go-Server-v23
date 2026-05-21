package org.cobalt.loader.config

import org.cobalt.loader.util.LoaderLog
import java.nio.file.Files
import java.util.Properties
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

object SavedLoginConfig {
    private const val USERNAME_KEY = "username"

    fun loadUsername(): String {
        val configured = LoaderConfig.username.takeUnless { it == "dev-user" }
        if (configured != null) {
            return configured
        }

        return runCatching {
            if (!Files.isRegularFile(LoaderConfig.userConfigPath)) {
                return@runCatching ""
            }

            val properties = Properties()
            LoaderConfig.userConfigPath.inputStream().use(properties::load)
            properties.getProperty(USERNAME_KEY, "").trim()
        }.getOrElse {
            LoaderLog.error("Failed to read saved Cobalt login config: ${it.message}", it)
            ""
        }
    }

    fun saveUsername(username: String) {
        runCatching {
            Files.createDirectories(LoaderConfig.userConfigPath.parent)
            val properties = Properties()
            properties.setProperty(USERNAME_KEY, username.trim())
            LoaderConfig.userConfigPath.outputStream().use { output ->
                properties.store(output, "Cobalt loader login")
            }
        }.onFailure {
            LoaderLog.error("Failed to save Cobalt login config: ${it.message}", it)
        }
    }
}
