package org.phantom.loader

import java.io.File
import java.nio.charset.StandardCharsets

/**
 * The short-lived session token produced by the bootstrapper's stage-1 auth.
 * Delivered as `-Dphantom.session` (a file path or a raw token) or, as a
 * fallback, the file `config/phantom/session.token`.
 */
data class PhantomSession(val token: String) {
    val isValid: Boolean get() = token.isNotBlank()

    companion object {
        private const val SESSION_TOKEN_PATH = "config/phantom/session.token"
        val INVALID = PhantomSession("")

        fun read(): PhantomSession {
            val property = System.getProperty("phantom.session")?.trim()
            if (!property.isNullOrBlank()) {
                val asFile = File(property)
                if (asFile.isFile) {
                    readToken(asFile)?.let { return PhantomSession(it) }
                } else {
                    return PhantomSession(property)
                }
            }

            val fallback = File(SESSION_TOKEN_PATH)
            if (fallback.isFile) {
                readToken(fallback)?.let { return PhantomSession(it) }
            }

            return INVALID
        }

        private fun readToken(file: File): String? =
            runCatching { file.readText(StandardCharsets.UTF_8).trim() }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
    }
}
