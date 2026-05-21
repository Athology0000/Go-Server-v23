package org.cobalt.loader.bootstrap

import kotlinx.serialization.json.Json
import org.cobalt.loader.config.LoaderConfig
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.Base64

object BootstrapManifestVerifier {
    private val json = Json {
        ignoreUnknownKeys = false
        encodeDefaults = true
    }

    fun verifyAndDecode(signed: SignedBootstrapManifest): BootstrapManifest {
        val publicKeyText = LoaderConfig.manifestPublicKeyBase64
        require(!publicKeyText.startsWith("REPLACE_")) {
            "Missing manifest public key. Set -Dcobalt.manifest.publicKey=BASE64_X509_ED25519_PUBLIC_KEY"
        }

        val payloadBytes = Base64.getDecoder().decode(signed.payloadBase64)
        val signatureBytes = Base64.getDecoder().decode(signed.signatureBase64)
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyText)

        val publicKey = KeyFactory.getInstance("Ed25519")
            .generatePublic(X509EncodedKeySpec(publicKeyBytes))

        val verifier = Signature.getInstance("Ed25519")
        verifier.initVerify(publicKey)
        verifier.update(payloadBytes)

        require(verifier.verify(signatureBytes)) {
            "Invalid module manifest signature"
        }

        val manifest = json.decodeFromString<BootstrapManifest>(payloadBytes.toString(Charsets.UTF_8))
        validateManifest(manifest)
        return manifest
    }

    private fun validateManifest(manifest: BootstrapManifest) {
        val now = Instant.now().epochSecond

        require(manifest.expiresAt > now) { "Module manifest expired" }
        require(manifest.issuedAt <= now + 60) { "Module manifest issued in the future" }
        require(manifest.expiresAt - manifest.issuedAt <= 60 * 30) {
            "Manifest lifetime too long. Keep it short, around 5 to 15 minutes."
        }
        require(manifest.modules.size <= 32) { "Too many modules in manifest" }
        require(manifest.userId.isNotBlank()) { "Manifest is missing user id" }
        require(manifest.minecraftUsername.isNotBlank()) { "Manifest is missing Minecraft username" }

        val duplicateModule = manifest.modules
            .groupingBy { it.id.lowercase() }
            .eachCount()
            .entries
            .firstOrNull { it.value > 1 }
        require(duplicateModule == null) { "Duplicate module id in manifest: ${duplicateModule?.key}" }
    }
}
