package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.api.rotation.EasingType
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.rotation.strategy.TimedEaseStrategy
import org.phantom.api.util.helper.Rotation
import org.phantom.internal.garden.GardenConfig
import org.phantom.mixin.client.MinecraftAccessor

object PestAotvManager {

    @Volatile var isActive = false

    fun reset() { isActive = false }

    fun teleportToRoof() {
        val mc = Minecraft.getInstance()
        isActive = true
        try {
            val pitch = GardenConfig.roofPitch.toFloat()
            mc.execute {
                RotationExecutor.rotateTo(
                    Rotation(mc.player?.yRot ?: 0f, pitch),
                    TimedEaseStrategy(EasingType.LINEAR, EasingType.LINEAR, 400L)
                )
            }
            Thread.sleep(500)
            mc.execute {
                val player = mc.player ?: return@execute
                val aotv = (0..8).firstOrNull { slot ->
                    player.inventory.getItem(slot).hoverName.string
                        .contains("Aspect of the Void", ignoreCase = true)
                }
                if (aotv != null) player.inventory.selectedSlot = aotv
                (mc as? MinecraftAccessor)?.rightClick()
            }
            Thread.sleep(800)
        } finally {
            isActive = false
        }
    }
}
