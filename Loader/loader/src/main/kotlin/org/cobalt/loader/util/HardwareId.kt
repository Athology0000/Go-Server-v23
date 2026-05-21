package org.cobalt.loader.util

import java.security.MessageDigest

object HardwareId {
    fun current(): String {
        val raw = buildString {
            append(System.getProperty("os.name"))
            append('|')
            append(System.getProperty("os.arch"))
            append('|')
            append(System.getenv("PROCESSOR_IDENTIFIER") ?: "")
            append('|')
            append(System.getenv("COMPUTERNAME") ?: "")
            append('|')
            append(System.getenv("HOSTNAME") ?: "")
        }
        return sha256(raw)
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
