package org.phantom.internal.garden

import net.minecraft.client.Minecraft
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.garden.managers.PestManager
import org.phantom.internal.garden.managers.PestTabListParser
import org.phantom.mixin.client.TabOverlayAccessor

object GardenHudModule : Module("Garden HUD") {

    override val category = ModuleCategory.FARMING

    private val mc = Minecraft.getInstance()

    // â”€â”€ State updated every tick â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private var alivePests       = 0
    private var infestedPlots    = emptyList<String>()
    private var bonusActive      = false
    private var cooldownDeadline = 0L   // epoch ms when cooldown expires
    private var lastSpawnedAt    = 0L   // epoch ms of last pest spawn
    private var prevAliveCount   = 0

    // â”€â”€ Layout constants â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private const val ROW_H   = 15f
    private const val FONT_SZ = 10f
    private const val PAD     = 8f
    private const val W       = 190f

    init {
        EventBus.register(this)
    }

    // â”€â”€ hudElement â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    val pestHud = hudElement("garden-pest-hud", "Garden Pest HUD", "Shows pest cooldown and status in the Garden") {
        anchor  = HudAnchor.TOP_LEFT
        offsetX = 10f
        offsetY = 10f

        width  { W + PAD * 2 }
        height { PAD * 2 + ROW_H * visibleRowCount() }

        render { x, y, _ ->
            if (!isInGarden()) return@render

            val now = System.currentTimeMillis()
            val cdRemaining = ((cooldownDeadline - now) / 1000f).coerceAtLeast(0f)
            val internalCdRemaining = ((PestManager.cleaningCooldownUntil - now) / 1000f).coerceAtLeast(0f)
            val lastSpawnedSec = if (lastSpawnedAt > 0) ((now - lastSpawnedAt) / 1000f) else -1f

            var rowY = y + PAD

            fun row(label: String, value: String, valueColor: Int) {
                val theme = ThemeManager.currentTheme
                NVGRenderer.text(label, x + PAD, rowY, FONT_SZ, theme.text)
                NVGRenderer.text(value, x + PAD + 110f, rowY, FONT_SZ, valueColor)
                rowY += ROW_H
            }

            val green  = 0xFF55FF55.toInt()
            val yellow = 0xFFFFFF55.toInt()
            val red    = 0xFFFF5555.toInt()
            val gray   = 0xFFAAAAAA.toInt()

            row("Curr Pest:",
                "$alivePests",
                if (alivePests > 0) red else green)

            row("Pest Cooldown:",
                if (cdRemaining > 0) "%.0fs".format(cdRemaining) else "Ready",
                if (cdRemaining > 0) yellow else green)

            if (infestedPlots.isNotEmpty()) {
                row("Infested Plots:", infestedPlots.size.toString(), red)
            }

            if (lastSpawnedSec >= 0) {
                row("Last Spawned:", "%.0fs ago".format(lastSpawnedSec), gray)
            }

            row("Farming Bonus:",
                if (bonusActive) "Active" else "Inactive",
                if (bonusActive) green else gray)

            if (internalCdRemaining > 0) {
                row("Internal CD:", "%.0fs".format(internalCdRemaining), yellow)
            }
        }
    }

    // â”€â”€ Tick-driven state update â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @SubscribeEvent
    fun onTick(event: TickEvent.Start) {
        if (!pestHud.enabled) return
        if (!isInGarden()) return

        val data = PestTabListParser.parse()

        if (data.alivePests > prevAliveCount) {
            lastSpawnedAt = System.currentTimeMillis()
        }
        prevAliveCount = data.alivePests
        alivePests     = data.alivePests
        infestedPlots  = data.infestedPlots
        bonusActive    = data.bonusActive

        if (data.cooldownSeconds > 0) {
            val newDeadline = System.currentTimeMillis() + data.cooldownSeconds * 1000L
            if (newDeadline > cooldownDeadline) cooldownDeadline = newDeadline
        } else {
            cooldownDeadline = 0L
        }
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun isInGarden(): Boolean {
        val overlay = mc.gui.tabList as? TabOverlayAccessor ?: return false
        val header  = overlay.header?.string?.replace(Regex("\u00A7[0-9a-fk-or]"), "") ?: ""
        val footer  = overlay.footer?.string?.replace(Regex("\u00A7[0-9a-fk-or]"), "") ?: ""
        return header.contains("Garden", ignoreCase = true) ||
               footer.contains("Garden", ignoreCase = true)
    }

    private fun visibleRowCount(): Float {
        val now = System.currentTimeMillis()
        var count = 3f  // Curr Pest + Pest Cooldown + Farming Bonus always shown
        if (infestedPlots.isNotEmpty()) count++
        if (lastSpawnedAt > 0) count++
        if (PestManager.cleaningCooldownUntil > now) count++
        return count
    }
}
