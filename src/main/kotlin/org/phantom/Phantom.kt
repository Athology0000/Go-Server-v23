package org.phantom

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.phantom.api.module.ModuleManager
import org.phantom.internal.BuiltinModules
import org.phantom.internal.auth.Auth
import org.phantom.internal.auth.AuthState
import org.phantom.internal.loader.AddonLoader

/**
 * Dev-only client entrypoint. In production the loader project's
 * [org.phantom.loader.PhantomLoaderClient] owns initialization and module
 * registration; this entrypoint exists so `runClient` works without a server.
 */
@Suppress("UNUSED")
object Phantom : ClientModInitializer {

  override fun onInitializeClient() {
    PhantomPublicInit.init()
    applyDevUnlockIfEnabled()
    if (shouldRegisterEmbeddedModules()) {
      BuiltinModules.register()
    }
    AddonLoader.activateLoadedAddons()

    println("Phantom Client Initialized")
  }

  /**
   * Dev-only auth escape hatch. Sets [Auth] to a fully-ready state so that
   * `runClient` works without a Phantom server. Only fires when BOTH of the
   * following are true:
   *   1. Fabric reports this as a development environment.
   *   2. The `phantom.devMockAuth` system property or `PHANTOM_DEV_MOCK_AUTH`
   *      env var is truthy ("1", "true", "yes", "on" — case-insensitive).
   *
   * The `runClient` Gradle task sets `phantom.devMockAuth=true` by default
   * (see build.gradle.kts), so no extra flags are needed during local dev.
   */
  private fun applyDevUnlockIfEnabled() {
    if (!FabricLoader.getInstance().isDevelopmentEnvironment) return

    val raw = System.getProperty("phantom.devMockAuth")
      ?: System.getenv("PHANTOM_DEV_MOCK_AUTH")
      ?: return
    if (raw.trim().lowercase() !in setOf("1", "true", "yes", "on")) return

    Auth.state = AuthState.READY
    Auth.failureReason = ""
    Auth.statusMessage = "Ready (dev mock auth)"
    Auth.entitledModules = setOf("*")
    if (Auth.alias.isBlank()) Auth.alias = "dev"
    ModuleManager.setLocked(false)

    println("[Phantom] Dev mock auth applied — all modules entitled, auth gate bypassed.")
  }

  private fun shouldRegisterEmbeddedModules(): Boolean {
    // Production module registration is owned by org.phantom.loader.PhantomLoaderClient.
    if (FabricLoader.getInstance().isDevelopmentEnvironment) return true

    return (System.getProperty("phantom.embeddedModules")
      ?.trim()
      ?.lowercase()
      in setOf("1", "true", "yes", "on")
      )
  }
}
