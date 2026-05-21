package org.cobalt.loader.bootstrap

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.cobalt.loader.config.LoaderConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object BootstrapAuthClient {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    fun authenticate(request: BootstrapAuthRequest): BootstrapAuthResponse {
        val authUri = URI.create(LoaderConfig.authUrl)
        validateAuthUri(authUri)

        val httpRequest = HttpRequest.newBuilder()
            .uri(authUri)
            .timeout(Duration.ofSeconds(12))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json.encodeToString(request)))
            .build()

        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            return BootstrapAuthResponse(
                ok = false,
                reason = "Auth server returned HTTP ${response.statusCode()}"
            )
        }

        return json.decodeFromString(response.body())
    }

    private fun validateAuthUri(uri: URI) {
        val isLocalhost = uri.host == "localhost" || uri.host == "127.0.0.1"
        val valid = uri.scheme == "https" || (LoaderConfig.allowLocalhostHttp && isLocalhost && uri.scheme == "http")
        require(valid) { "Auth URL must use HTTPS. Only localhost HTTP is allowed in dev mode." }
    }
}
