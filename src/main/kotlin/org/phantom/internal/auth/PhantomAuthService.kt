package org.phantom.internal.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread
import net.fabricmc.loader.api.FabricLoader
import org.phantom.api.module.ModuleManager
import org.phantom.internal.helper.Config
import org.phantom.internal.loader.AddonLoader
import org.slf4j.LoggerFactory

object PhantomAuthService {

  private const val DEFAULT_MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
  private const val DEFAULT_SERVER_BASE_URL = "http://localhost:8080/"
  private const val AUTH_BASE_URL_PROPERTY = "phantom.authBaseUrl"
  private const val AUTH_BASE_URL_ENV = "PHANTOM_AUTH_BASE_URL"
  private const val MANIFEST_PUBLIC_KEY_PROPERTY = "phantom.manifestPublicKey"
  private const val MANIFEST_PUBLIC_KEY_ENV = "PHANTOM_MANIFEST_PUBLIC_KEY"

  private const val DEV_MOCK_AUTH_PROPERTY = "phantom.devMockAuth"
  private const val DEV_MOCK_AUTH_ENV = "PHANTOM_DEV_MOCK_AUTH"

  private val logger = LoggerFactory.getLogger("Phantom/AuthService")
  private val gson = Gson()

  private val httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(15))
    .build()

  private val ED25519_ASN1_HEADER = byteArrayOf(
    0x30, 0x2A, 0x30, 0x05, 0x06, 0x03, 0x2B, 0x65, 0x70, 0x03, 0x21, 0x00
  )

  private fun normalizeEntitlements(raw: List<String>?): Set<String> {
    return raw
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
      ?.toSet()
      ?: emptySet()
  }

  fun start(session: PhantomSession) {
    if (isDevMockAuthEnabled()) {
      logger.warn("Dev mock auth enabled - skipping remote auth, entitlement, and module download flow")
      Auth.alias = session.alias.ifBlank { "dev" }
      Auth.accountId = ""
      Auth.entitledModules = setOf("*")
      markReady("Ready (dev mock auth)")
      return
    }

    if (!session.isValid) {
      fail("no_session")
      Auth.statusMessage = "No Phantom session - run the bootstrapper first"
      return
    }

    Auth.alias = session.alias
    Auth.accountId = ""

    thread(name = "Phantom-Auth", isDaemon = true) {
      runAuth(session)
    }
  }

  private fun isDevMockAuthEnabled(): Boolean {
    if (!FabricLoader.getInstance().isDevelopmentEnvironment) return false

    val propertyFlag = parseBooleanFlag(System.getProperty(DEV_MOCK_AUTH_PROPERTY))
    if (propertyFlag != null) return propertyFlag

    return parseBooleanFlag(System.getenv(DEV_MOCK_AUTH_ENV)) == true
  }

  private fun parseBooleanFlag(raw: String?): Boolean? {
    return when (raw?.trim()?.lowercase()) {
      null, "" -> null
      "1", "true", "yes", "on" -> true
      "0", "false", "no", "off" -> false
      else -> null
    }
  }

  private fun markReady(statusMessage: String) {
    Auth.failureReason = ""
    Auth.modulesLoaded = 0
    Auth.modulesTotal = 0
    Auth.state = AuthState.READY
    Auth.statusMessage = statusMessage
    ModuleManager.setLocked(false)

    logger.info(
      "Auth ready: status='{}', alias='{}', accountId='{}', entitledModules={}, minecraftBound={}, minecraftUsername='{}', canToggle={}",
      statusMessage,
      Auth.alias,
      Auth.accountId,
      Auth.entitledModules,
      Auth.minecraftBound,
      Auth.minecraftUsername,
      ModuleManager.canToggleModules()
    )
  }

  private fun fail(reason: String) {
    logger.warn("Auth gate failed: {}", reason)
    Auth.failureReason = reason
    Auth.statusMessage = "Auth failed: $reason"
    Auth.state = AuthState.FAILED
    ModuleManager.setLocked(true)
  }

  private fun runAuth(session: PhantomSession) {
    try {
      Auth.state = AuthState.VERIFYING
      Auth.statusMessage = "Verifying Phantom account..."
      ModuleManager.setLocked(true)

      val minecraftUsername = MinecraftIdentity.currentUsername().orEmpty()

      PhantomAuthDebug.info(
        "runAuth alias=${session.alias} token_present=${session.sessionToken.isNotBlank()} minecraft_username=$minecraftUsername"
      )

      val entitlement = verifyPhantomSession(
        sessionToken = session.sessionToken,
        minecraftUsername = minecraftUsername
      ) ?: return fail("Server auth failed")

      if (!entitlement.authorized) {
        return fail(entitlement.reason ?: "not_authorized")
      }

      Auth.alias = entitlement.alias?.takeIf { it.isNotBlank() } ?: session.alias
      Auth.accountId = entitlement.accountId.orEmpty()
      Auth.minecraftBound = entitlement.minecraftBound ?: !entitlement.minecraftUsername.isNullOrBlank()
      Auth.minecraftUsername = entitlement.minecraftUsername?.takeIf { it.isNotBlank() } ?: minecraftUsername
      Auth.minecraftUuid = entitlement.minecraftUuid.orEmpty()

      val entitledModules = normalizeEntitlements(entitlement.enabledModules)

      Auth.entitledModules = entitledModules.ifEmpty {
        logger.warn("Authorized response had no enabled_modules; temporarily granting all modules with '*'")
        setOf("*")
      }

      if (entitlement.manifestUrl.isNullOrBlank()) {
        markReady("Ready")
        return
      }

      Auth.state = AuthState.LOADING
      Auth.statusMessage = "Fetching module list..."

      val manifest = fetchManifest(session.sessionToken, entitlement.manifestUrl)
        ?: return fail("Failed to fetch manifest")

      if (!verifyManifestSignature(manifest, entitlement.manifestSignature ?: "")) {
        return fail("Manifest signature invalid")
      }

      val entitled = Auth.entitledModules ?: emptySet()

      val toLoad = manifest.modules.filter { module ->
        "*" in entitled || module.name in entitled
      }

      Auth.modulesTotal = toLoad.size
      Auth.modulesLoaded = 0

      for (module in toLoad.sortedBy { it.initOrder }) {
        Auth.statusMessage = "Loading ${module.name}... (${Auth.modulesLoaded + 1}/${Auth.modulesTotal})"

        val jarBytes = downloadModuleBytes(session.sessionToken, module)
          ?: return fail("Failed to load ${module.name}")

        val loadedAddons = AddonLoader.loadFromBytes("${module.name}.jar", jarBytes, activate = true)
        if (loadedAddons.isEmpty()) {
          return fail("Failed to initialize ${module.name}")
        }

        Auth.modulesLoaded++
      }

      Config.loadModulesConfig()
      markReady("Ready")
    } catch (e: Exception) {
      logger.error("Auth failed with exception", e)
      fail(e.message ?: "unknown_error")
    }
  }

  private fun verifyPhantomSession(sessionToken: String, minecraftUsername: String): EntitlementResponse? {
    val body = gson.toJson(
      VerifySessionRequest(
        sessionToken = sessionToken,
        minecraftUsername = minecraftUsername
      )
    )

    PhantomAuthDebug.info(
      "verify-session request token_present=${sessionToken.isNotBlank()} minecraft_username=$minecraftUsername"
    )

    val req = HttpRequest.newBuilder(
      trustedUri("${serverBaseUrl().trimEnd('/')}/auth/verify-session")
    )
      .timeout(Duration.ofSeconds(20))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(body))
      .build()

    return try {
      val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))

      PhantomAuthDebug.info("verify-session HTTP ${resp.statusCode()} body=${resp.body()}")

      if (resp.statusCode() !in 200..299) {
        logger.warn("verify-session failed with HTTP ${resp.statusCode()}: ${resp.body()}")
        return null
      }

      val parsed = gson.fromJson(resp.body(), EntitlementResponse::class.java)

      PhantomAuthDebug.info(
        "verify-session response authorized=${parsed.authorized} reason=${parsed.reason} " +
          "account=${parsed.accountId} plan=${parsed.planTier} minecraft_bound=${parsed.minecraftBound} " +
          "minecraft_username=${parsed.minecraftUsername}"
      )

      parsed
    } catch (e: Exception) {
      logger.error("verify-session call failed", e)
      PhantomAuthDebug.error("verify-session call failed", e)
      null
    }
  }

  private fun fetchManifest(sessionToken: String, url: String): ManifestResponse? {
    val uri = trustedUri(url)

    val req = HttpRequest.newBuilder(uri)
      .timeout(Duration.ofSeconds(20))
      .header("Authorization", "Bearer $sessionToken")
      .GET()
      .build()

    return try {
      val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))

      if (resp.statusCode() !in 200..299) {
        logger.warn("Manifest fetch failed with HTTP ${resp.statusCode()}: ${resp.body()}")
        return null
      }

      gson.fromJson(resp.body(), ManifestResponse::class.java)
    } catch (e: Exception) {
      logger.error("Manifest fetch failed", e)
      null
    }
  }

  private fun downloadModuleBytes(sessionToken: String, module: ManifestModule): ByteArray? {
    if (module.name.isBlank()) {
      logger.error("Manifest contained a module with no name")
      return null
    }

    val downloadUrl = module.url.ifBlank {
      val encodedName = URLEncoder.encode(module.name, StandardCharsets.UTF_8)
      "${serverBaseUrl().trimEnd('/')}/content/module/$encodedName"
    }

    val req = HttpRequest.newBuilder(trustedUri(downloadUrl))
      .timeout(Duration.ofSeconds(60))
      .header("Authorization", "Bearer $sessionToken")
      .GET()
      .build()

    return try {
      val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray())

      if (resp.statusCode() !in 200..299) {
        logger.error("Module {} download returned {}", module.name, resp.statusCode())
        return null
      }

      val body = resp.body()

      val actual = sha256Hex(body)
      if (actual != module.sha256) {
        logger.error("Module {} digest mismatch: expected {} got {}", module.name, module.sha256, actual)
        return null
      }

      body
    } catch (e: Exception) {
      logger.error("Module {} download failed", module.name, e)
      null
    }
  }

  private fun verifyManifestSignature(manifest: ManifestResponse, sigBase64: String): Boolean {
    if (sigBase64.isBlank()) return false

    return try {
      val payload = SignedPayload(
        build_id = manifest.buildId,
        channel = manifest.channel,
        minimum_loader_version = manifest.minimumLoaderVersion,
        modules = manifest.modules.map { m ->
          SignedModule(m.name, m.url, m.sha256, m.required, m.initOrder)
        },
        native_components = manifest.nativeComponents.map { n ->
          SignedNative(n.name, n.url, n.sha256, n.required)
        }
      )

      val payloadJson = gson.toJson(payload).toByteArray(StandardCharsets.UTF_8)
      val sigBytes = Base64.getDecoder().decode(sigBase64)
      val keyB64 = manifestPublicKeyB64()
      if (keyB64.isBlank() || keyB64 == DEFAULT_MANIFEST_PUBLIC_KEY_B64) {
        logger.error("Manifest public key is not configured")
        return false
      }

      val rawKey = Base64.getDecoder().decode(keyB64)

      verifyEd25519(rawKey, payloadJson, sigBytes)
    } catch (e: Exception) {
      logger.error("Manifest signature verification failed", e)
      false
    }
  }

  private fun verifyEd25519(rawPublicKey: ByteArray, message: ByteArray, sig: ByteArray): Boolean {
    val keyBytes = ED25519_ASN1_HEADER + rawPublicKey
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("Ed25519")
    val publicKey = keyFactory.generatePublic(keySpec)
    val verifier = Signature.getInstance("Ed25519")

    verifier.initVerify(publicKey)
    verifier.update(message)

    return verifier.verify(sig)
  }

  private fun sha256Hex(bytes: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(bytes).joinToString("") { "%02x".format(it) }
  }

  private fun serverBaseUrl(): String {
    return System.getProperty(AUTH_BASE_URL_PROPERTY)
      ?.takeIf { it.isNotBlank() }
      ?: System.getenv(AUTH_BASE_URL_ENV)
        ?.takeIf { it.isNotBlank() }
      ?: DEFAULT_SERVER_BASE_URL
  }

  private fun manifestPublicKeyB64(): String {
    return System.getProperty(MANIFEST_PUBLIC_KEY_PROPERTY)
      ?.takeIf { it.isNotBlank() }
      ?: System.getenv(MANIFEST_PUBLIC_KEY_ENV)
        ?.takeIf { it.isNotBlank() }
      ?: DEFAULT_MANIFEST_PUBLIC_KEY_B64
  }

  private fun trustedUri(raw: String): URI {
    val uri = URI.create(raw)
    val scheme = uri.scheme?.lowercase(Locale.US)
    val host = uri.host?.lowercase(Locale.US).orEmpty()

    if (scheme == "https") return uri

    val localHttpAllowed = FabricLoader.getInstance().isDevelopmentEnvironment &&
      scheme == "http" &&
      (host == "localhost" || host == "127.0.0.1" || host == "::1")

    require(localHttpAllowed) {
      "Refusing non-HTTPS auth/content URL: $raw"
    }

    return uri
  }

  private data class VerifySessionRequest(
    @SerializedName("session_token") val sessionToken: String,
    @SerializedName("minecraft_username") val minecraftUsername: String = ""
  )

  private data class EntitlementResponse(
    val authorized: Boolean = false,
    val reason: String? = null,

    val alias: String? = null,
    @SerializedName("account_id") val accountId: String? = null,
    @SerializedName("plan_tier") val planTier: String? = null,

    @SerializedName("enabled_modules") val enabledModules: List<String>? = null,
    @SerializedName("enabled_features") val enabledFeatures: List<String>? = null,

    @SerializedName("minecraft_bound") val minecraftBound: Boolean? = null,
    @SerializedName("minecraft_username") val minecraftUsername: String? = null,
    @SerializedName("minecraft_uuid") val minecraftUuid: String? = null,

    @SerializedName("manifest_url") val manifestUrl: String? = null,
    @SerializedName("manifest_signature") val manifestSignature: String? = null
  )

  private data class ManifestModule(
    val name: String = "",
    val url: String = "",
    val sha256: String = "",
    val required: Boolean = false,
    @SerializedName("init_order") val initOrder: Int = 0
  )

  private data class ManifestNative(
    val name: String = "",
    val url: String = "",
    val sha256: String = "",
    val required: Boolean = false
  )

  private data class ManifestResponse(
    val id: String = "",
    @SerializedName("build_id") val buildId: String = "",
    val channel: String = "",
    @SerializedName("minimum_loader_version") val minimumLoaderVersion: String = "",
    val modules: List<ManifestModule> = emptyList(),
    @SerializedName("native_components") val nativeComponents: List<ManifestNative> = emptyList(),
    val signature: String = ""
  )

  private data class SignedModule(
    val name: String,
    val url: String,
    val sha256: String,
    val required: Boolean,
    val init_order: Int
  )

  private data class SignedNative(
    val name: String,
    val url: String,
    val sha256: String,
    val required: Boolean
  )

  private data class SignedPayload(
    val build_id: String,
    val channel: String,
    val minimum_loader_version: String,
    val modules: List<SignedModule>,
    val native_components: List<SignedNative>
  )
}
