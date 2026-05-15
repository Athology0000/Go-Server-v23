package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenConfig
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge

object PestReturnManager {

    @Volatile var isReturning = false

    fun reset() { isReturning = false }

    fun startReturn(onComplete: () -> Unit) {
        if (isReturning) return
        isReturning = true
        GardenWorkerThread.submit("pest-return") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { ScriptBridge.startReturnScript(GardenConfig.returnScript) }
                Thread.sleep(500)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isReturning = false
                mc.execute { onComplete() }
            }
        }
    }
}
