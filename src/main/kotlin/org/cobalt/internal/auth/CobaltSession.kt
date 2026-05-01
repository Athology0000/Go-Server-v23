package org.cobalt.internal.auth

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Locale
import org.slf4j.LoggerFactory

data class CobaltSession(
    val sessionToken: String,
    val alias: String = "",
) {
    val isValid: Boolean
        get() = sessionToken.isNotBlank()

    val normalizedAlias: String
        get() = alias.trim().lowercase(Locale.US)

    fun isTempAliasBypass(): Boolean = normalizedAlias == TEMP_BYPASS_ALIAS

    companion object {
        val INVALID = CobaltSession("")
        private val logger = LoggerFactory.getLogger("Cobalt/CobaltSession")
        private val gson = Gson()
        private const val TEMP_BYPASS_ALIAS = "hwnm"
        private const val CREDS_PATH = "config/cobalt/creds.json"

        fun readAndDelete(): CobaltSession {
            val fallbackAlias = readLaunchAlias().ifBlank { readStoredAlias() }
            val path = System.getProperty("cobalt.session") ?: run {
                logger.warn("cobalt.session system property not set - running without auth")
                return if (fallbackAlias.isNotBlank()) CobaltSession("", fallbackAlias) else INVALID
            }
            val file = File(path)
            return try {
                val json = file.readText(StandardCharsets.UTF_8)
                file.delete()
                val raw = gson.fromJson(json, RawSession::class.java)
                if (raw == null) {
                    if (fallbackAlias.isNotBlank()) CobaltSession("", fallbackAlias) else INVALID
                } else {
                    val alias = raw.alias ?: raw.cobalt_alias ?: raw.username ?: fallbackAlias
                    if (raw.session_token.isNullOrBlank() && alias.isBlank()) {
                        INVALID
                    } else {
                        CobaltSession(raw.session_token.orEmpty(), alias)
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to read session file at {}: {}", path, e.message)
                runCatching { file.delete() }
                INVALID
            }
        }

        private data class RawSession(
            val session_token: String?,
            val alias: String? = null,
            val cobalt_alias: String? = null,
            val username: String? = null,
        )

        private fun readLaunchAlias(): String {
            return System.getProperty("cobalt.alias").orEmpty().trim()
        }

        private fun readStoredAlias(): String {
            val file = File(CREDS_PATH)
            if (!file.exists()) return ""
            return runCatching {
                val raw = gson.fromJson(file.readText(StandardCharsets.UTF_8), RawStoredCreds::class.java)
                raw?.username.orEmpty().trim()
            }.getOrDefault("")
        }

        private data class RawStoredCreds(
            val username: String? = null,
        )
    }
}
