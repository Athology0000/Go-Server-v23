package org.cobalt.loader.bootstrap

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.cobalt.loader.config.LoaderConfig
import org.cobalt.loader.util.LoaderLog
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.concurrent.thread

object BootstrapHeartbeatClient {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    @Volatile
    private var worker: Thread? = null

    fun start(sessionToken: String, request: BootstrapHeartbeatRequest) {
        worker?.interrupt()
        worker = thread(name = "Cobalt-Heartbeat", isDaemon = true) {
            var consecutiveFailures = 0
            while (!Thread.currentThread().isInterrupted) {
                val accepted = send(sessionToken, request)
                if (accepted) {
                    consecutiveFailures = 0
                } else {
                    consecutiveFailures++
                    if (consecutiveFailures >= LoaderConfig.heartbeatFailureLimit) {
                        LoaderLog.error("Heartbeat trust lost. Unloading protected modules.")
                        BootstrapModuleLoader.unloadAll()
                        Thread.currentThread().interrupt()
                    }
                }

                try {
                    Thread.sleep(Duration.ofSeconds(LoaderConfig.heartbeatIntervalSeconds).toMillis())
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    private fun send(sessionToken: String, request: BootstrapHeartbeatRequest): Boolean {
        return runCatching {
            val heartbeatUri = URI.create(LoaderConfig.heartbeatUrl)
            validateHeartbeatUri(heartbeatUri)

            val httpRequest = HttpRequest.newBuilder()
                .uri(heartbeatUri)
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer $sessionToken")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.encodeToString(request)))
                .build()

            val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                LoaderLog.error("Heartbeat failed with HTTP ${response.statusCode()}")
                return@runCatching false
            }

            val heartbeatResponse = json.decodeFromString<BootstrapHeartbeatResponse>(response.body())
            if (!heartbeatResponse.ok) {
                LoaderLog.error("Heartbeat rejected: ${heartbeatResponse.reason ?: "unknown reason"}")
                return@runCatching false
            }
            true
        }.onFailure {
            LoaderLog.error("Heartbeat failed: ${it.message}", it)
        }.getOrDefault(false)
    }

    private fun validateHeartbeatUri(uri: URI) {
        val isLocalhost = uri.host == "localhost" || uri.host == "127.0.0.1"
        val valid = uri.scheme == "https" || (LoaderConfig.allowLocalhostHttp && isLocalhost && uri.scheme == "http")
        require(valid) { "Heartbeat URL must use HTTPS. Only localhost HTTP is allowed in dev mode." }
    }
}
