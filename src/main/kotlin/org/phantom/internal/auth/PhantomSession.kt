package org.phantom.internal.auth

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import org.slf4j.LoggerFactory

data class PhantomSession(
  val sessionToken: String,
  val alias: String = "",
) {
  val isValid: Boolean
    get() = sessionToken.isNotBlank()

  val normalizedAlias: String
    get() = alias.trim().lowercase(Locale.US)

  fun isTempAliasBypass(): Boolean {
    return normalizedAlias == TEMP_BYPASS_ALIAS.lowercase(Locale.US)
  }

  companion object {
    val INVALID = PhantomSession("")

    private val logger = LoggerFactory.getLogger("Phantom/PhantomSession")
    private val gson = Gson()

    private const val TEMP_BYPASS_ALIAS = "Iamaperson2004"
    private const val CREDS_PATH = "config/phantom/creds.json"
    private const val SESSION_TOKEN_PATH = "config/phantom/session.token"

    fun readAndDelete(): PhantomSession {
      PhantomAuthDebug.info("session load start")

      val fallbackAlias = readLaunchAlias().ifBlank { readStoredAlias() }

      PhantomAuthDebug.info(
        "fallback alias present=${fallbackAlias.isNotBlank()} alias=$fallbackAlias"
      )

      if (fallbackAlias.equals(TEMP_BYPASS_ALIAS, ignoreCase = true)) {
        PhantomAuthDebug.warn("dev bypass alias detected, returning alias-only session")
        return PhantomSession("", fallbackAlias)
      }

      val propValue = System.getProperty("phantom.session")?.trim()

      PhantomAuthDebug.info(
        "system property phantom.session present=${!propValue.isNullOrBlank()}"
      )

      if (!propValue.isNullOrBlank()) {
        val propSession = readFromProperty(propValue, fallbackAlias)

        if (propSession.isValid || propSession.alias.isNotBlank()) {
          PhantomAuthDebug.info(
            "loaded session from phantom.session property valid=${propSession.isValid} alias=${propSession.alias}"
          )

          return propSession
        }
      }

      val fallbackFile = File(SESSION_TOKEN_PATH)

      PhantomAuthDebug.info(
        "checking fallback session token file=${fallbackFile.absolutePath} exists=${fallbackFile.exists()}"
      )

      if (fallbackFile.exists()) {
        val token = runCatching {
          fallbackFile.readText(StandardCharsets.UTF_8).trim()
        }.getOrDefault("")

        PhantomAuthDebug.info("fallback token present=${token.isNotBlank()} length=${token.length}")

        if (token.isNotBlank()) {
          return PhantomSession(token, fallbackAlias)
        }
      }

      PhantomAuthDebug.warn("no Phantom session token found")

      return if (fallbackAlias.isNotBlank()) {
        PhantomSession("", fallbackAlias)
      } else {
        INVALID
      }
    }

    private fun readFromProperty(propValue: String, fallbackAlias: String): PhantomSession {
      val possibleFile = File(propValue)

      if (!possibleFile.exists()) {
        PhantomAuthDebug.info("phantom.session property is not a file path, treating as raw token")

        return if (propValue.isNotBlank()) {
          PhantomSession(propValue, fallbackAlias)
        } else {
          INVALID
        }
      }

      return try {
        PhantomAuthDebug.info("reading session JSON file from property path=${possibleFile.absolutePath}")

        val json = possibleFile.readText(StandardCharsets.UTF_8)
        val raw = gson.fromJson(json, RawSession::class.java)

        if (raw == null) {
          if (fallbackAlias.isNotBlank()) PhantomSession("", fallbackAlias) else INVALID
        } else {
          val alias = raw.alias ?: raw.phantom_alias ?: raw.username ?: fallbackAlias
          val token = raw.session_token.orEmpty()

          PhantomAuthDebug.info("session JSON parsed token_present=${token.isNotBlank()} alias=$alias")

          if (token.isBlank() && alias.isBlank()) {
            INVALID
          } else {
            PhantomSession(token, alias)
          }
        }
      } catch (e: Exception) {
        logger.error("Failed to read session file at {}: {}", possibleFile.absolutePath, e.message)
        PhantomAuthDebug.error("failed to read phantom.session property file", e)
        INVALID
      }
    }

    private data class RawSession(
      val session_token: String?,
      val alias: String? = null,
      val phantom_alias: String? = null,
      val username: String? = null,
    )

    private fun readLaunchAlias(): String {
      return System.getProperty("phantom.alias").orEmpty().trim()
    }

    private fun readStoredAlias(): String {
      val file = File(CREDS_PATH)

      PhantomAuthDebug.info("checking stored creds file=${file.absolutePath} exists=${file.exists()}")

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
