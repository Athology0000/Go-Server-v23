package org.phantom.loader.bootstrap

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.phantom.loader.LoaderConfig
import java.math.BigInteger
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyFactory
import java.security.Signature
import java.security.interfaces.EdECPublicKey
import java.security.spec.EdECPoint
import java.security.spec.EdECPublicKeySpec
import java.security.spec.NamedParameterSpec
import java.time.Duration
import java.util.Base64

object BootstrapManifestVerifier {
    // encodeDefaults MUST be true: the signed payload is re-serialized here and
    // compared byte-for-byte against the server's Ed25519 signature. Go's
    // db.ManifestModule / db.ManifestNative have no `omitempty`, so the server
    // always emits `required` and `init_order`. Omitting them (encodeDefaults =
    // false) changes the bytes and fails verification for every module.
    // explicitNulls stays false so a null module_key is omitted, matching the
    // server's `omitempty` on ModuleKey (the with-key/without-key dual attempt
    // in verifySignature covers both states).
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    fun fetchAndVerify(manifestUrl: String, sessionToken: String, expectedSignature: String): ContentManifest {
        val request = HttpRequest.newBuilder()
            .uri(trustedUri(manifestUrl))
            .timeout(Duration.ofSeconds(12))
            .header("Authorization", "Bearer $sessionToken")
            .header("Accept", "application/json")
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        require(response.statusCode() == 200) {
            "Manifest fetch returned HTTP ${response.statusCode()}"
        }

        val manifest = json.decodeFromString<ContentManifest>(response.body())
        val signature = expectedSignature.ifBlank { manifest.signature }
        require(signature.isNotBlank()) { "Manifest is unsigned." }
        verifySignature(manifest, signature)
        return manifest
    }

    private fun verifySignature(manifest: ContentManifest, signatureBase64: String) {
        val publicKeyText = LoaderConfig.manifestPublicKeyB64
        require(!publicKeyText.startsWith("REPLACE_WITH_")) {
            "Missing pinned Phantom manifest public key."
        }

        val signatureBytes = Base64.getDecoder().decode(signatureBase64)
        val publicKey = decodeRawEd25519PublicKey(Base64.getDecoder().decode(publicKeyText))

        val payloadWithKey = SignedManifestPayload(
            buildId = manifest.buildId,
            channel = manifest.channel,
            minimumLoaderVersion = manifest.minimumLoaderVersion,
            moduleKey = manifest.moduleKey,
            modules = manifest.modules,
            nativeComponents = manifest.nativeComponents,
        )

        val payloadWithoutKey = payloadWithKey.copy(moduleKey = null)
        val verified = verify(publicKey, json.encodeToString(payloadWithKey).toByteArray(), signatureBytes) ||
            verify(publicKey, json.encodeToString(payloadWithoutKey).toByteArray(), signatureBytes)

        require(verified) { "Manifest signature verification failed." }
    }

    private fun verify(publicKey: EdECPublicKey, payload: ByteArray, signatureBytes: ByteArray): Boolean {
        val verifier = Signature.getInstance("Ed25519")
        verifier.initVerify(publicKey)
        verifier.update(payload)
        return verifier.verify(signatureBytes)
    }

    private fun decodeRawEd25519PublicKey(raw: ByteArray): EdECPublicKey {
        require(raw.size == 32) { "Ed25519 public key must be exactly 32 bytes." }

        val yBytes = raw.copyOf()
        val xOdd = (yBytes[31].toInt() and 0x80) != 0
        yBytes[31] = (yBytes[31].toInt() and 0x7f).toByte()
        yBytes.reverse()

        val point = EdECPoint(xOdd, BigInteger(1, yBytes))
        val spec = EdECPublicKeySpec(NamedParameterSpec("Ed25519"), point)
        return KeyFactory.getInstance("Ed25519").generatePublic(spec) as EdECPublicKey
    }
}
