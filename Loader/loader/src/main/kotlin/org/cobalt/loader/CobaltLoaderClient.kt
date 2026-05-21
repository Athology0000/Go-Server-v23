package org.cobalt.loader

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import org.cobalt.loader.config.SavedLoginConfig
import org.cobalt.loader.ui.CobaltLoginScreen
import org.cobalt.loader.util.LoaderLog
import kotlin.concurrent.thread

object CobaltLoaderClient : ClientModInitializer {
    override fun onInitializeClient() {
        LoaderLog.info("Cobalt Loader initialized. Waiting for login screen...")

        thread(name = "Cobalt-Login-Screen", isDaemon = true) {
            val client = MinecraftClient.getInstance()
            while (client.currentScreen == null) {
                Thread.sleep(50L)
            }

            client.execute {
                client.setScreen(CobaltLoginScreen(client.currentScreen, SavedLoginConfig.loadUsername()))
            }
        }
    }
}
