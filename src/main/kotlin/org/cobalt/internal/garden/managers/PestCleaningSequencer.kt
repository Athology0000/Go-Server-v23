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

                if (GardenConfig.autoWardrobeEnabled && GardenConfig.autoWardrobePest) {
                    GearManager.swapForPest()
                    Thread.sleep(2000)
                }

                if (GardenConfig.aotvEnabled) {
                    PestAotvManager.teleportToRoof()
                    Thread.sleep(1500)
                }

                mc.execute { ScriptBridge.startPestScript(GardenConfig.pestScript) }

                // Wait for pests to actually clear before returning to farming
                val deadline = System.currentTimeMillis() + 180_000L
                while (System.currentTimeMillis() < deadline) {
                    Thread.sleep(2000)
                    val alive = PestTabListParser.parse().alivePests
                    if (alive < GardenConfig.pestThreshold) break
                }

                // Swap back to farming gear
                if (GardenConfig.autoWardrobeEnabled && GardenConfig.autoWardrobePest) {
                    GearManager.swapForFarming()
                    Thread.sleep(1500)
                }

                // Give Taunahi a moment to stop its script
                Thread.sleep(GardenConfig.gardenWarpDelayMs.coerceAtLeast(500L))

            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isRunning = false
                PestManager.startCooldown(10_000L)
                mc.execute { onComplete() }
            }
        }
    }
}
