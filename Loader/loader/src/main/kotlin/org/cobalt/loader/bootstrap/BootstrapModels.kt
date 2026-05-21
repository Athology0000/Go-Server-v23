package org.cobalt.loader.bootstrap

import kotlinx.serialization.Serializable

@Serializable
data class BootstrapAuthRequest(
    val username: String,
    val minecraftUsername: String,
    val hwid: String,
    val clientVersion: String
)

@Serializable
data class BootstrapAuthResponse(
    val ok: Boolean,
    val reason: String? = null,
    val sessionToken: String? = null,
    val manifest: SignedBootstrapManifest? = null
)

@Serializable
data class BootstrapHeartbeatRequest(
    val username: String,
    val minecraftUsername: String,
    val hwid: String,
    val clientVersion: String
)

@Serializable
data class BootstrapHeartbeatResponse(
    val ok: Boolean = true,
    val reason: String? = null
)

@Serializable
data class SignedBootstrapManifest(
    val payloadBase64: String,
    val signatureBase64: String
)

@Serializable
data class BootstrapManifest(
    val issuedAt: Long,
    val expiresAt: Long,
    val userId: String,
    val minecraftUsername: String,
    val modules: List<BootstrapModule>
)

@Serializable
data class BootstrapModule(
    val id: String,
    val version: String,
    val delivery: String = "jar",
    val url: String = "",
    val sha256: String,
    val entrypoint: String,
    val requiredRole: String
)

sealed interface DownloadedBootstrapModule {
    val module: BootstrapModule

    data class Jar(
        override val module: BootstrapModule,
        val path: java.nio.file.Path
    ) : DownloadedBootstrapModule

    data class Bytecode(
        override val module: BootstrapModule,
        val className: String,
        val bytes: ByteArray
    ) : DownloadedBootstrapModule {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Bytecode) return false
            return module == other.module && className == other.className && bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            var result = module.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }
}
