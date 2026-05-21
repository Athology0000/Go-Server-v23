package org.phantom.loader

import net.fabricmc.api.ClientModInitializer
import org.phantom.loader.bootstrap.BootstrapStarter
import kotlin.concurrent.thread

object PhantomLoaderClient : ClientModInitializer {
    override fun onInitializeClient() {
        RuntimeGuard.verify()
        LoaderLog.info("Phantom loader initialized.")

        thread(name = "Phantom-Bootstrap", isDaemon = true) {
            BootstrapStarter.start()
        }
    }
}
