package org.cobalt.internal.garden.managers

import net.minecraft.client.Minecraft
import org.cobalt.internal.garden.GardenConfig
import org.cobalt.internal.garden.GardenWorkerThread
import org.cobalt.internal.garden.ScriptBridge

object PestCleaningSequencer {

    @Volatile var isRunning = false

    fun reset() { isRunning = false }

    fun startSequence(onComplete: () -> Unit) {
        if (isRunning) return
        isRunning = true
        GardenWorkerThread.submit("pest-clean") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { ScriptBridge.stopScript() }
                Thread.sleep(500)
                mc.execute { ScriptBridge.setSpawn() }
                Thread.sleep(300)

                if (GardenConfig.autoWardrobeEnabled) {
                    GearManager.swapForPest()
                    Thread.sleep(2000)
                }

                if (GardenConfig.aotvEnabled) {
                    PestAotvManager.teleportToRoof()
                    Thread.sleep(1500)
                }

                mc.execute { ScriptBridge.startPestScript(GardenConfig.pestScript) }
                Thread.sleep(500)

            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isRunning = false
                mc.execute { onComplete() }
            }
        }
    }
}
