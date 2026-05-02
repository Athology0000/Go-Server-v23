package org.cobalt.internal.auth

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
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

    private const val TEMP_BYPASS_ALIAS = "Iamaperson2004"
    private const val CREDS_PATH = "config/cobalt/creds.json"
    private const val SESSION_TOKEN_PATH = "config/cobalt/session.token"

    fun readAndDelete(): CobaltSession {
      CobaltAuthDebug.info("session load start")

      val fallbackAlias = readLaunchAlias().ifBlank { readStoredAlias() }
      CobaltAuthDebug.info("fallback alias present=${fallbackAlias.isNotBlank()} alias=$fallbackAlias")

      val propValue = System.getProperty("cobalt.session")?.trim()
      CobaltAuthDebug.info("system property cobalt.session present=${!propValue.isNullOrBlank()}")

      if (!propValue.isNullOrBlank()) {
        val propSession = readFromProperty(propValue, fallbackAlias)
        if (propSession.isValid || propSession.alias.isNotBlank()) {
          CobaltAuthDebug.info(
            "loaded session from cobalt.session property valid=${propSession.isValid} alias=${propSession.alias}"
          )
          return propSession
        }
      }

      val fallbackFile = File(SESSION_TOKEN_PATH)
      CobaltAuthDebug.info(
        "checking fallback session token file=${fallbackFile.absolutePath} exists=${fallbackFile.exists()}"
      )

      if (fallbackFile.exists()) {
        val token = runCatching {
          fallbackFile.readText(StandardCharsets.UTF_8).trim()
        }.getOrDefault("")

        CobaltAuthDebug.info("fallback token present=${token.isNotBlank()} length=${token.length}")

        if (token.isNotBlank()) {
          return CobaltSession(token, fallbackAlias)
        }
      }

      CobaltAuthDebug.warn("no Cobalt session token found")

      return if (fallbackAlias.isNotBlank()) {
        CobaltSession("", fallbackAlias)
      } else {
        INVALID
      }
    }

    private fun readFromProperty(propValue: String, fallbackAlias: String): CobaltSession {
      val possibleFile = File(propValue)

      if (!possibleFile.exists()) {
        CobaltAuthDebug.info("cobalt.session property is not a file path, treating as raw token")

        return if (propValue.isNotBlank()) {
          CobaltSession(propValue, fallbackAlias)
        } else {
          INVALID
        }
      }

      return try {
        CobaltAuthDebug.info("reading session JSON file from property path=${possibleFile.absolutePath}")

        val json = possibleFile.readText(StandardCharsets.UTF_8)
        val raw = gson.fromJson(json, RawSession::class.java)

        if (raw == null) {
          if (fallbackAlias.isNotBlank()) CobaltSession("", fallbackAlias) else INVALID
        } else {
          val alias = raw.alias ?: raw.cobalt_alias ?: raw.username ?: fallbackAlias
          val token = raw.session_token.orEmpty()

          CobaltAuthDebug.info("session JSON parsed token_present=${token.isNotBlank()} alias=$alias")

          if (token.isBlank() && alias.isBlank()) {
            INVALID
          } else {
            CobaltSession(token, alias)
          }
        }
      } catch (e: Exception) {
        logger.error("Failed to read session file at {}: {}", possibleFile.absolutePath, e.message)
        CobaltAuthDebug.error("failed to read cobalt.session property file", e)
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

      CobaltAuthDebug.info("checking stored creds file=${file.absolutePath} exists=${file.exists()}")

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
