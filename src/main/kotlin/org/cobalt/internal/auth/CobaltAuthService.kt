package org.cobalt.internal.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread
import net.fabricmc.loader.api.FabricLoader
import org.cobalt.api.module.ModuleManager
import org.cobalt.internal.loader.AddonLoader
import org.slf4j.LoggerFactory

object CobaltAuthService {
<<<<<<< Updated upstream

<<<<<<< HEAD
  private const val MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
  private const val SERVER_BASE_URL = "http://localhost:8080/"
=======
    // Replace with: echo "$MANIFEST_SIGNING_KEY" | base64 -d | tail -c 32 | base64
    private const val MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
    private const val SERVER_BASE_URL = "https://your-server-url.com" // replace with actual URL
    private const val TEMP_BYPASS_ALIAS = "Iamaperson2004"
    private const val DEV_MOCK_AUTH_PROPERTY = "cobalt.devMockAuth"
    private const val DEV_MOCK_AUTH_ENV = "COBALT_DEV_MOCK_AUTH"
>>>>>>> be393fffb0f09841a0cc3de6a2250e7b3c3a898a
=======
    private const val MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
    private const val SERVER_BASE_URL = "http://localhost:8080/"
>>>>>>> Stashed changes

    private const val DEV_MOCK_AUTH_PROPERTY = "cobalt.devMockAuth"
    private const val DEV_MOCK_AUTH_ENV = "COBALT_DEV_MOCK_AUTH"

    private val logger = LoggerFactory.getLogger("Cobalt/AuthService")
    private val gson = Gson()

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build()

    private val cacheDir: Path = Paths.get("config/cobalt/cache/payloads")

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

    fun start(session: CobaltSession) {
        // Local/dev build: do not call the remote auth server, do not verify
        // sessions, and do not lock movement/modules while waiting for auth.
        logger.warn("Remote auth disabled - unlocking local modules without network calls")
        DevUnlock.apply("remote auth disabled")
    }

    private fun isBypassAliasAllowed(): Boolean {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            return true
        }

        val prop = parseBooleanFlag(System.getProperty("cobalt.allowBypassAlias"))
        if (prop != null) {
            return prop
        }

        return parseBooleanFlag(System.getenv("COBALT_ALLOW_BYPASS_ALIAS")) == true
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

        // QUICK DEV FIX:
        // Do not lock Minecraft menus/buttons on failed auth.
        // Protected modules can still check Auth.state / entitlements separately.
        ModuleManager.setLocked(false)
    }

    private fun runAuth(session: CobaltSession) {
        try {
            Auth.state = AuthState.VERIFYING
            Auth.statusMessage = "Verifying Cobalt account..."
            ModuleManager.setLocked(true)

            val minecraftUsername = MinecraftIdentity.currentUsername().orEmpty()

            CobaltAuthDebug.info(
                "runAuth alias=${session.alias} token_present=${session.sessionToken.isNotBlank()} minecraft_username=$minecraftUsername"
            )

            val entitlement = verifyCobaltSession(
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

            Auth.entitledModules = entitledModules

            if (entitledModules.isEmpty()) {
                logger.warn("Authorized response had no enabled_modules; no protected modules will be unlocked")
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

            Files.createDirectories(cacheDir)

            for (module in toLoad) {
                Auth.statusMessage = "Loading ${module.name}... (${Auth.modulesLoaded + 1}/${Auth.modulesTotal})"

                val jarPath = ensureModule(session.sessionToken, module)
                    ?: return fail("Failed to load ${module.name}")

                AddonLoader.loadFromPath(jarPath)
                Auth.modulesLoaded++
            }

            markReady("Ready")
        } catch (e: Exception) {
            logger.error("Auth failed with exception", e)
            fail(e.message ?: "unknown_error")
        }
    }

    private fun verifyCobaltSession(sessionToken: String, minecraftUsername: String): EntitlementResponse? {
        val body = gson.toJson(
            VerifySessionRequest(
                sessionToken = sessionToken,
                minecraftUsername = minecraftUsername
            )
        )

        CobaltAuthDebug.info(
            "verify-session request token_present=${sessionToken.isNotBlank()} minecraft_username=$minecraftUsername"
        )

        val req = HttpRequest.newBuilder(
            URI.create("${SERVER_BASE_URL.trimEnd('/')}/auth/verify-session")
        )
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))

            CobaltAuthDebug.info("verify-session HTTP ${resp.statusCode()} body=${resp.body()}")

            if (resp.statusCode() !in 200..299) {
                logger.warn("verify-session failed with HTTP ${resp.statusCode()}: ${resp.body()}")
                return null
            }

            val parsed = gson.fromJson(resp.body(), EntitlementResponse::class.java)

            CobaltAuthDebug.info(
                "verify-session response authorized=${parsed.authorized} reason=${parsed.reason} " +
                    "account=${parsed.accountId} plan=${parsed.planTier} minecraft_bound=${parsed.minecraftBound} " +
                    "minecraft_username=${parsed.minecraftUsername}"
            )

            parsed
        } catch (e: Exception) {
            logger.error("verify-session call failed", e)
            CobaltAuthDebug.error("verify-session call failed", e)
            null
        }
    }

    private fun fetchManifest(sessionToken: String, url: String): ManifestResponse? {
        val req = HttpRequest.newBuilder(URI.create(url))
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

    private fun ensureModule(sessionToken: String, module: ManifestModule): Path? {
        val safeName = module.name.replace("/", "_").replace("\\", "_")
        val dest = cacheDir.resolve("$safeName.jar")

        if (Files.exists(dest) && sha256Hex(dest.toFile()) == module.sha256) {
            return dest
        }

        val downloadUrl = "${SERVER_BASE_URL.trimEnd('/')}/content/module/${module.name}"

        val req = HttpRequest.newBuilder(URI.create(downloadUrl))
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

            Files.createDirectories(cacheDir)
            Files.write(dest, resp.body())

            val actual = sha256Hex(dest.toFile())
            if (actual != module.sha256) {
                logger.error("Module {} digest mismatch: expected {} got {}", module.name, module.sha256, actual)
                Files.deleteIfExists(dest)
                return null
            }

            dest
        } catch (e: Exception) {
            logger.error("Module {} download failed", module.name, e)
            null
        }
    }

    private fun verifyManifestSignature(manifest: ManifestResponse, sigBase64: String): Boolean {
        if (sigBase64.isBlank()) {
            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                logger.warn("Manifest has no signature. Allowing unsigned manifest in development only.")
                return true
            }

            logger.error("Manifest has no signature. Rejecting unsigned manifest.")
            return false
        }

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
            val rawKey = Base64.getDecoder().decode(MANIFEST_PUBLIC_KEY_B64)

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

    private fun sha256Hex(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")

        file.inputStream().use { stream ->
            val buf = ByteArray(8192)
            var n: Int

            while (stream.read(buf).also { n = it } != -1) {
                digest.update(buf, 0, n)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
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
