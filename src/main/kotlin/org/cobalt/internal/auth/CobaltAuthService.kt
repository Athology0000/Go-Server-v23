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
import java.util.Base64
import kotlin.concurrent.thread
import net.minecraft.client.Minecraft
import org.cobalt.internal.loader.AddonLoader
import org.slf4j.LoggerFactory

object CobaltAuthService {

    // Replace with: echo "$MANIFEST_SIGNING_KEY" | base64 -d | tail -c 32 | base64
    private const val MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
    private const val SERVER_BASE_URL = "https://your-server-url.com" // replace with actual URL

    private val logger = LoggerFactory.getLogger("Cobalt/AuthService")
    private val gson = Gson()
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build()
    private val cacheDir: Path = Paths.get("config/cobalt/cache/payloads")

    // ASN.1 header for Ed25519 public key (OID 1.3.101.112)
    private val ED25519_ASN1_HEADER = byteArrayOf(
        0x30, 0x2A, 0x30, 0x05, 0x06, 0x03, 0x2B, 0x65, 0x70, 0x03, 0x21, 0x00
    )

    fun start(session: CobaltSession) {
        if (!session.isValid) {
            Auth.state = AuthState.FAILED
            Auth.statusMessage = "No session — run the bootstrapper first"
            Auth.failureReason = "no_session"
            return
        }
        thread(name = "Cobalt-Auth", isDaemon = true) { runAuth(session) }
    }

    private fun runAuth(session: CobaltSession) {
        try {
            Auth.state = AuthState.VERIFYING
            Auth.statusMessage = "Verifying account…"

            val mc = Minecraft.getInstance()
            val mcUsername = mc.user.name

            val entitlement = verifyMinecraft(session.sessionToken, mcUsername)
                ?: return fail("Server auth failed")

            if (!entitlement.authorized) {
                return fail(entitlement.reason ?: "not_authorized")
            }

            if (entitlement.manifestUrl.isNullOrBlank()) {
                Auth.state = AuthState.READY
                Auth.statusMessage = "Ready"
                return
            }

            Auth.state = AuthState.LOADING
            Auth.statusMessage = "Fetching module list…"

            val manifest = fetchManifest(session.sessionToken, entitlement.manifestUrl)
                ?: return fail("Failed to fetch manifest")

            if (!verifyManifestSignature(manifest, entitlement.manifestSignature ?: "")) {
                return fail("Manifest signature invalid")
            }

            val entitled = (entitlement.enabledModules ?: emptyList()).toSet()
            val toLoad = manifest.modules.filter { it.name in entitled }
            Auth.modulesTotal = toLoad.size
            Auth.modulesLoaded = 0

            Files.createDirectories(cacheDir)

            for (module in toLoad) {
                Auth.statusMessage = "Loading ${module.name}… (${Auth.modulesLoaded + 1}/${Auth.modulesTotal})"
                val jarPath = ensureModule(session.sessionToken, module) ?: return fail("Failed to load ${module.name}")
                AddonLoader.loadFromPath(jarPath)
                Auth.modulesLoaded++
            }

            Auth.state = AuthState.READY
            Auth.statusMessage = "Ready"

        } catch (e: Exception) {
            logger.error("Auth failed with exception", e)
            fail(e.message ?: "unknown_error")
        }
    }

    private fun fail(reason: String) {
        logger.warn("Auth gate failed: {}", reason)
        Auth.failureReason = reason
        Auth.statusMessage = "Auth failed: $reason"
        Auth.state = AuthState.FAILED
    }

    private fun verifyMinecraft(sessionToken: String, mcUsername: String): EntitlementResponse? {
        val body = gson.toJson(mapOf("session_token" to sessionToken, "minecraft_username" to mcUsername))
        val req = HttpRequest.newBuilder(URI.create("$SERVER_BASE_URL/auth/verify-minecraft"))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            gson.fromJson(resp.body(), EntitlementResponse::class.java)
        } catch (e: Exception) {
            logger.error("verify-minecraft call failed", e)
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
            if (resp.statusCode() != 200) return null
            gson.fromJson(resp.body(), ManifestResponse::class.java)
        } catch (e: Exception) {
            logger.error("Manifest fetch failed", e)
            null
        }
    }

    private fun ensureModule(sessionToken: String, module: ManifestModule): Path? {
        val dest = cacheDir.resolve("${module.name}.jar")
        if (Files.exists(dest) && sha256Hex(dest.toFile()) == module.sha256) {
            return dest
        }
        val downloadUrl = "$SERVER_BASE_URL/content/module/${module.name}"
        val req = HttpRequest.newBuilder(URI.create(downloadUrl))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer $sessionToken")
            .GET()
            .build()
        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray())
            if (resp.statusCode() != 200) {
                logger.error("Module {} download returned {}", module.name, resp.statusCode())
                return null
            }
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
            while (stream.read(buf).also { n = it } != -1) digest.update(buf, 0, n)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private data class EntitlementResponse(
        val authorized: Boolean = false,
        val reason: String? = null,
        @SerializedName("plan_tier") val planTier: String? = null,
        @SerializedName("enabled_modules") val enabledModules: List<String>? = null,
        @SerializedName("enabled_features") val enabledFeatures: List<String>? = null,
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
        val name: String, val url: String, val sha256: String,
        val required: Boolean, val init_order: Int
    )
    private data class SignedNative(
        val name: String, val url: String, val sha256: String, val required: Boolean
    )
    private data class SignedPayload(
        val build_id: String, val channel: String, val minimum_loader_version: String,
        val modules: List<SignedModule>, val native_components: List<SignedNative>
    )
}
