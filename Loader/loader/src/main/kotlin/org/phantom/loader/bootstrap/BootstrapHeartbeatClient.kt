package org.phantom.loader.bootstrap

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.phantom.internal.auth.Auth
import org.phantom.internal.auth.AuthState
import org.phantom.internal.loader.AddonLoader
import org.phantom.loader.LoaderConfig
import org.phantom.loader.LoaderLog
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

    fun start(sessionToken: String) {
        worker?.interrupt()
        worker = thread(name = "Phantom-Heartbeat", isDaemon = true) {
            var failures = 0
            while (!Thread.currentThread().isInterrupted) {
                if (send(sessionToken)) {
                    failures = 0
                } else if (++failures >= LoaderConfig.heartbeatFailureLimit) {
                    LoaderLog.error("Heartbeat trust lost. Unloading protected modules.")
                    Auth.state = AuthState.FAILED
                    Auth.failureReason = "Heartbeat trust lost"
                    Auth.statusMessage = "Phantom session revoked - heartbeat trust lost."
                    AddonLoader.unloadLoadedAddons()
                    Thread.currentThread().interrupt()
                }

                try {
                    Thread.sleep(Duration.ofSeconds(LoaderConfig.heartbeatIntervalSeconds).toMillis())
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    private fun send(sessionToken: String): Boolean =
        runCatching {
            val request = HttpRequest.newBuilder()
                .uri(trustedUri("${LoaderConfig.serverBaseUrl.trimEnd('/')}/auth/heartbeat"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.encodeToString(HeartbeatRequest(sessionToken))))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.statusCode() == 200
        }.onFailure {
            LoaderLog.error("Heartbeat failed: ${it.message}", it)
        }.getOrDefault(false)
}
