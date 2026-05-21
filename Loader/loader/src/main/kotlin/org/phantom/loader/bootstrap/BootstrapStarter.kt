package org.phantom.loader.bootstrap

import net.minecraft.client.Minecraft
import org.phantom.PhantomPublicInit
import org.phantom.internal.auth.Auth
import org.phantom.internal.auth.AuthState
import org.phantom.internal.loader.AddonLoader
import org.phantom.loader.LoaderLog
import org.phantom.loader.PhantomSession

object BootstrapStarter {
    fun start(): BootstrapStartResult {
        val session = PhantomSession.read()
        if (!session.isValid) {
            return fail("Missing Phantom session token.")
        }

        Auth.state = AuthState.VERIFYING
        Auth.statusMessage = "Verifying Phantom session..."

        return try {
            val minecraftUsername = Minecraft.getInstance().user.name.trim()
            val auth = BootstrapAuthClient.verifySession(session.token, minecraftUsername)
            if (!auth.authorized) {
                return fail("Auth failed: ${auth.reason.ifBlank { "not authorized" }}")
            }

            Auth.alias = auth.username.ifBlank { auth.alias }
            Auth.accountId = auth.accountId
            Auth.minecraftBound = auth.minecraftBound
            Auth.minecraftUsername = auth.minecraftUsername?.ifBlank { minecraftUsername } ?: minecraftUsername
            Auth.minecraftUuid = auth.minecraftUuid.orEmpty()
            Auth.entitledModules = auth.enabledModules.toSet()

            require(auth.manifestUrl.isNotBlank()) { "Auth response did not include a manifest URL." }

            Auth.state = AuthState.LOADING
            Auth.statusMessage = "Loading protected Phantom modules..."

            val manifest = BootstrapManifestVerifier.fetchAndVerify(
                manifestUrl = auth.manifestUrl,
                sessionToken = session.token,
                expectedSignature = auth.manifestSignature,
            )

            manifest.nativeComponents
                .filter { it.required }
                .forEach { BootstrapContentClient.downloadAndInstallNative(it, session.token) }

            PhantomPublicInit.init()

            val modules = manifest.modules
                .sortedWith(compareBy<ManifestModule> { it.initOrder }.thenBy { it.name })
                .filter { module ->
                    module.required ||
                        "*" in auth.enabledModules ||
                        module.name in auth.enabledModules ||
                        module.name.removePrefix("phantom-") in auth.enabledModules
                }

            Auth.modulesTotal = modules.size
            modules.forEachIndexed { index, module ->
                loadModule(module, manifest, session.token)
                Auth.modulesLoaded = index + 1
            }

            AddonLoader.activateLoadedAddons()
            BootstrapHeartbeatClient.start(session.token)

            Auth.state = AuthState.READY
            Auth.statusMessage = "Phantom ready."
            LoaderLog.info("Bootstrap complete. Loaded ${modules.size} protected module bundle(s).")
            BootstrapStartResult.Success
        } catch (t: Throwable) {
            fail("Bootstrap failed: ${t.message}", t)
        }
    }

    private fun loadModule(module: ManifestModule, manifest: ContentManifest, sessionToken: String) {
        val encrypted = BootstrapContentClient.downloadModule(module, sessionToken)
        var decrypted: ByteArray? = null
        try {
            decrypted = ModuleCrypto.decryptAesGcm(manifest.moduleKey, encrypted)
            val actualHash = sha256Hex(decrypted)
            require(actualHash.equals(module.sha256, ignoreCase = true)) {
                "Module ${module.name} SHA-256 mismatch."
            }
            AddonLoader.loadFromBytes("${module.name}.jar", decrypted, activate = false)
        } finally {
            encrypted.fill(0)
            decrypted?.fill(0)
        }
    }

    private fun fail(message: String, throwable: Throwable? = null): BootstrapStartResult.Failure {
        Auth.state = AuthState.FAILED
        Auth.failureReason = message
        Auth.statusMessage = message
        LoaderLog.error(message, throwable)
        return BootstrapStartResult.Failure(message)
    }
}

sealed interface BootstrapStartResult {
    data object Success : BootstrapStartResult
    data class Failure(val message: String) : BootstrapStartResult
}
