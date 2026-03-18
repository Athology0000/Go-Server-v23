package org.cobalt.internal.garden.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.ClickType
import org.cobalt.internal.garden.GardenConfig

object EquipmentManager {

    @Volatile var isSwapping = false

    fun reset() { isSwapping = false }

    fun swapTo(armorSetName: String) {
        if (armorSetName.isBlank()) return
        isSwapping = true
        try {
            val mc = Minecraft.getInstance()
            mc.execute { mc.player?.connection?.sendCommand("equipment") }
            Thread.sleep(1200)
            mc.execute {
                val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                val slot = screen.menu.slots.firstOrNull { s ->
                    s.item.hoverName.string.contains(armorSetName, ignoreCase = true)
                } ?: return@execute
                mc.gameMode?.handleInventoryMouseClick(
                    screen.menu.containerId, slot.index, 0, ClickType.PICKUP, mc.player!!
                )
            }
            Thread.sleep(GardenConfig.swapDelayMs)
            mc.execute { mc.player?.closeContainer() }
            Thread.sleep(300)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            isSwapping = false
        }
    }
}
