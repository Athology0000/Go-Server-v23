package org.cobalt.loader.bootstrap

import net.minecraft.client.MinecraftClient
import org.cobalt.loader.config.LoaderConfig
import org.cobalt.loader.util.HardwareId
import org.cobalt.loader.util.LoaderLog

object BootstrapStarter {
    fun start(username: String = LoaderConfig.username): BootstrapStartResult {
        try {
            val minecraftUsername = MinecraftClient.getInstance().session.username
            LoaderLog.info("Authenticating $username as Minecraft user $minecraftUsername")
            val hwid = HardwareId.current()

            val authResponse = BootstrapAuthClient.authenticate(
                BootstrapAuthRequest(
                    username = username,
                    minecraftUsername = minecraftUsername,
                    hwid = hwid,
                    clientVersion = LoaderConfig.clientVersion
                )
            )

            if (!authResponse.ok) {
                return fail("Auth failed: ${authResponse.reason ?: "unknown reason"}")
            }

            val sessionToken = authResponse.sessionToken
            val signedManifest = authResponse.manifest

            if (sessionToken.isNullOrBlank() || signedManifest == null) {
                return fail("Auth server did not return a session token or manifest")
            }

            val manifest = BootstrapManifestVerifier.verifyAndDecode(signedManifest)
            if (!manifest.minecraftUsername.equals(minecraftUsername, ignoreCase = true)) {
                return fail("Manifest Minecraft user ${manifest.minecraftUsername} does not match current session $minecraftUsername")
            }

            val downloadedModules = BootstrapModuleDownloader.downloadAll(manifest.modules, sessionToken)
            BootstrapModuleLoader.loadModules(manifest, downloadedModules)
            BootstrapHeartbeatClient.start(
                sessionToken,
                BootstrapHeartbeatRequest(
                    username = username,
                    minecraftUsername = minecraftUsername,
                    hwid = hwid,
                    clientVersion = LoaderConfig.clientVersion
                )
            )

            LoaderLog.info("Bootstrap complete. Loaded ${downloadedModules.size} runtime module(s).")
            return BootstrapStartResult.Success
        } catch (t: Throwable) {
            return fail("Bootstrap failed: ${t.message}", t)
        }
    }

    private fun fail(message: String, throwable: Throwable? = null): BootstrapStartResult.Failure {
        LoaderLog.error(message, throwable)
        // Do not register protected modules on failure.
        return BootstrapStartResult.Failure(message)
    }
}

sealed interface BootstrapStartResult {
    data object Success : BootstrapStartResult
    data class Failure(val message: String) : BootstrapStartResult
}
