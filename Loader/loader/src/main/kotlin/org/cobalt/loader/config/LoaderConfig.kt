package org.cobalt.loader.config

import java.nio.file.Path

object LoaderConfig {
    val authUrl: String = read("cobalt.auth.url", "COBALT_AUTH_URL")
        ?: "https://valiant-cooperation-production.up.railway.app/api/auth/verify"

    val heartbeatUrl: String = read("cobalt.heartbeat.url", "COBALT_HEARTBEAT_URL")
        ?: "https://valiant-cooperation-production.up.railway.app/api/auth/heartbeat"

    val moduleBaseUrl: String = read("cobalt.module.base.url", "COBALT_MODULE_BASE_URL")
        ?: "https://valiant-cooperation-production.up.railway.app/modules/"

    val username: String = read("cobalt.username", "COBALT_USERNAME")
        ?: "dev-user"

    val clientVersion: String = read("cobalt.client.version", "COBALT_CLIENT_VERSION")
        ?: "0.1.0"

    val manifestPublicKeyBase64: String = read("cobalt.manifest.publicKey", "COBALT_MANIFEST_PUBLIC_KEY")
        ?: "REPLACE_WITH_BASE64_X509_ED25519_PUBLIC_KEY"

    val cacheDir: Path = Path.of(
        read("cobalt.cache.dir", "COBALT_CACHE_DIR") ?: "config/cobalt/runtime-modules"
    )

    val userConfigPath: Path = Path.of(
        read("cobalt.user.config", "COBALT_USER_CONFIG") ?: "config/cobalt/loader.properties"
    )

    val heartbeatIntervalSeconds: Long = read("cobalt.heartbeat.intervalSeconds", "COBALT_HEARTBEAT_INTERVAL_SECONDS")
        ?.toLongOrNull()
        ?.coerceAtLeast(10L)
        ?: 30L

    val heartbeatFailureLimit: Int = read("cobalt.heartbeat.failureLimit", "COBALT_HEARTBEAT_FAILURE_LIMIT")
        ?.toIntOrNull()
        ?.coerceAtLeast(1)
        ?: 2

    val allowLocalhostHttp: Boolean = read("cobalt.allow.localhost", "COBALT_ALLOW_LOCALHOST")
        ?.equals("true", ignoreCase = true) == true

    val allowExternalModuleUrls: Boolean = read("cobalt.allow.external.modules", "COBALT_ALLOW_EXTERNAL_MODULES")
        ?.equals("true", ignoreCase = true) == true

    private fun read(property: String, env: String): String? {
        return System.getProperty(property)?.takeIf { it.isNotBlank() }
            ?: System.getenv(env)?.takeIf { it.isNotBlank() }
    }
}
