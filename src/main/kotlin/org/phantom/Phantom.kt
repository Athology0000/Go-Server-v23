package org.phantom

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.phantom.internal.BuiltinModules
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
    if (shouldRegisterEmbeddedModules()) {
      BuiltinModules.register()
    }
    AddonLoader.activateLoadedAddons()

    println("Phantom Client Initialized")
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
