package org.cobalt.internal.auth

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory

data class CobaltSession(val sessionToken: String) {
    val isValid: Boolean get() = sessionToken.isNotBlank()

    companion object {
        val INVALID = CobaltSession("")
        private val logger = LoggerFactory.getLogger("Cobalt/CobaltSession")
        private val gson = Gson()

        fun readAndDelete(): CobaltSession {
            val path = System.getProperty("cobalt.session") ?: run {
                logger.warn("cobalt.session system property not set — running without auth")
                return INVALID
            }
            val file = File(path)
            return try {
                val json = file.readText(StandardCharsets.UTF_8)
                file.delete()
                val raw = gson.fromJson(json, RawSession::class.java)
                if (raw?.session_token.isNullOrBlank()) INVALID
                else CobaltSession(raw.session_token)
            } catch (e: Exception) {
                logger.error("Failed to read session file at {}: {}", path, e.message)
                runCatching { file.delete() }
                INVALID
            }
        }

        private data class RawSession(val session_token: String?)
    }
}
