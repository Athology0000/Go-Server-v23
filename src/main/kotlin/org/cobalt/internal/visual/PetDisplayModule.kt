package org.cobalt.internal.visual

import kotlin.math.cos
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object PetDisplayModule : Module("Pet Display") {

    private val mc = Minecraft.getInstance()

    private val enabled = CheckboxSetting("Enabled", "Show the pet display HUD.", true)

    val petHud = hudElement("pet-display", "Pet Display", "Animated pet info HUD") {
        anchor  = HudAnchor.BOTTOM_RIGHT
        offsetX = -10f
        offsetY = -60f

        val showHeldItemSetting = setting(CheckboxSetting("Show Held Item", "Show held pet item.", true))
        val glowPulseSetting    = setting(CheckboxSetting("Glow Pulse",     "Animate border glow.", true))

        fun hudHeight(): Float {
            val hasItem = showHeldItemSetting.value && (PetTabListParser.current?.heldItem?.isNotEmpty() == true)
            return if (hasItem) 72f else 58f
        }

        width  { 180f }
        height { hudHeight() }

        render { x, y, _ ->
            if (!enabled.value) return@render

            val now   = System.currentTimeMillis()
            val twoPi = (Math.PI * 2.0).toFloat()
            val w = 180f
            val h = hudHeight()
            val radius = h / 2f
            val pad = 14f
            val data = PetTabListParser.current

            // Glow layers
            if (glowPulseSetting.value) {
                val t = (now % 3000L).toFloat() / 3000f
                val pulse = 0.6f + 0.4f * cos(t * twoPi)
                for (i in 3 downTo 1) {
                    val alpha = ((0x44.toFloat() * pulse) * i / 3).toInt().coerceIn(0, 0x44)
                    NVGRenderer.hollowRect(
                        x - i * 1.5f, y - i * 1.5f,
                        w + i * 3f, h + i * 3f,
                        2f + i.toFloat(),
                        (alpha shl 24) or 0x7EC8FF,
                        radius + i * 1.5f
                    )
                }
            }

            // Background pill
            NVGRenderer.rect(x, y, w, h, 0xFF0A0F2E.toInt(), radius)

            // Animated border
            val angle  = (now % 8000L).toFloat() / 8000f * twoPi
            val shiftX = cos(angle) * (w * 0.4f)
            NVGRenderer.hollowGradientRectShifted(
                x, y, w, h, 1.5f,
                0xFFADD8FF.toInt(), 0xFF4FA8E8.toInt(),
                Gradient.LeftToRight, radius, shiftX, 0f
            )

            if (data == null) {
                NVGRenderer.text("No pet detected", x + pad, y + h / 2f + 4f, 11f, 0x80FFFFFF.toInt())
                return@render
            }

            val textColor = 0xFFFFFFFF.toInt()
            val dimColor  = 0xBBB0C8FF.toInt()

            // Pet name + level
            val lvStr  = "Lv ${data.level}"
            val lvWidth = NVGRenderer.textWidth(lvStr, 10f)
            NVGRenderer.textShadow(data.name, x + pad, y + 16f, 13f, textColor)
            NVGRenderer.text(lvStr, x + w - pad - lvWidth, y + 16f, 10f, 0xFF7EC8FF.toInt())

            var nextY = 30f

            // Held item
            if (showHeldItemSetting.value && data.heldItem.isNotEmpty()) {
                NVGRenderer.text(data.heldItem, x + pad, y + nextY, 10f, dimColor)
                nextY += 14f
            }

            // XP bar
            val barX = x + pad
            val barY = y + nextY
            val barW = w - pad * 2f
            val barH = 7f
            val barR = barH / 2f

            NVGRenderer.rect(barX, barY, barW, barH, 0xFF1A1F4E.toInt(), barR)

            if (data.isMaxLevel) {
                NVGRenderer.gradientRect(barX, barY, barW, barH, 0xFFFFD700.toInt(), 0xFFFFA500.toInt(), Gradient.LeftToRight, barR)
                val maxW = NVGRenderer.textWidth("MAX", 9f)
                NVGRenderer.text("MAX", barX + barW / 2f - maxW / 2f, barY - 1f, 9f, 0xFFFFD700.toInt())
            } else if (data.xpRequired > 0) {
                val ratio = (data.xpCurrent.toFloat() / data.xpRequired.toFloat()).coerceIn(0f, 1f)
                if (ratio > 0f) {
                    val fillW = (barW * ratio).coerceAtLeast(barR * 2f)
                    val shimT  = (now % 2000L).toFloat() / 2000f
                    val shimShift = cos(shimT * twoPi) * fillW * 0.3f

                    NVGRenderer.pushScissor(barX, barY, fillW, barH)
                    NVGRenderer.gradientRect(
                        barX + shimShift, barY,
                        fillW, barH,
                        0xFF00C6FF.toInt(), 0xFF0055FF.toInt(),
                        Gradient.LeftToRight, barR
                    )
                    NVGRenderer.popScissor()
                }

                val pct  = "${(ratio * 100).toInt()}%"
                val pctW = NVGRenderer.textWidth(pct, 8f)
                NVGRenderer.text(pct, barX + barW / 2f - pctW / 2f, barY - 1f, 8f, dimColor)
            }
        }
    }

    init {
        addSetting(enabled)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.End) {
        if (!enabled.value) return
        PetTabListParser.update()
    }
}
