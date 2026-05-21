package org.phantom.loader.bootstrap

import org.phantom.loader.LoaderConfig
import java.net.URI
import java.security.MessageDigest

internal fun trustedUri(value: String): URI {
    val uri = URI.create(value)
    val isLocalhost = uri.host == "localhost" || uri.host == "127.0.0.1"
    val validScheme = uri.scheme == "https" || (LoaderConfig.allowLocalhostHttp && isLocalhost && uri.scheme == "http")

    require(validScheme) { "Phantom network endpoints must use HTTPS outside local development." }
    require(uri.userInfo == null) { "Phantom network endpoints must not contain user info." }
    require(uri.query == null && uri.fragment == null) { "Phantom network endpoints must not contain query or fragment." }

    return uri
}

internal fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
        .digest(bytes)
        .joinToString("") { "%02x".format(it) }

internal fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"
