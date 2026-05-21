package org.phantom.loader.bootstrap

import org.phantom.pathfinder.NativeLoader
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object BootstrapContentClient {
    private const val MAX_MODULE_BYTES = 25L * 1024L * 1024L
    private const val MAX_NATIVE_BYTES = 10L * 1024L * 1024L

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    fun downloadModule(module: ManifestModule, sessionToken: String): ByteArray {
        validateModule(module)
        val bytes = downloadBytes(module.url, sessionToken, MAX_MODULE_BYTES)
        require(bytes.isNotEmpty()) { "Module ${module.name} was empty." }
        return bytes
    }

    fun downloadAndInstallNative(native: ManifestNative, sessionToken: String) {
        val bytes = downloadBytes(native.url, sessionToken, MAX_NATIVE_BYTES)
        try {
            val actualHash = sha256Hex(bytes)
            require(actualHash.equals(native.sha256, ignoreCase = true)) {
                "Native ${native.name} SHA-256 mismatch."
            }
            NativeLoader.installDownloaded(native.name, bytes)
        } finally {
            bytes.fill(0)
        }
    }

    private fun downloadBytes(url: String, sessionToken: String, maxBytes: Long): ByteArray {
        val request = HttpRequest.newBuilder()
            .uri(trustedUri(url))
            .timeout(Duration.ofSeconds(25))
            .header("Authorization", "Bearer $sessionToken")
            .header("Accept", "application/octet-stream, application/java-archive")
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        require(response.statusCode() == 200) {
            "Content download returned HTTP ${response.statusCode()} for $url"
        }

        val bytes = response.body()
        require(bytes.size.toLong() in 1..maxBytes) {
            "Content download has invalid size ${bytes.size} for $url"
        }
        return bytes
    }

    private fun validateModule(module: ManifestModule) {
        require(Regex("^[a-zA-Z0-9_.-]{1,96}$").matches(module.name)) {
            "Unsafe module name: ${module.name}"
        }
        require(Regex("^[a-fA-F0-9]{64}$").matches(module.sha256)) {
            "Invalid SHA-256 for ${module.name}"
        }
    }
}
