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
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object PetDisplayModule : Module("Pet Display") {

    private val mc = Minecraft.getInstance()

    // ── Settings ──────────────────────────────────────────────────────────────

    private val enabledSetting    = CheckboxSetting("Enabled",       "Show the pet info HUD.",              true)
    private val glowSetting       = CheckboxSetting("Glow",          "Animated glow border.",               true)
    private val showHeldItemSetting = CheckboxSetting("Show Held Item", "Show the pet's held item line.",   true)

    // ── Layout constants (mirrors Spotify HUD) ────────────────────────────────

    private const val W      = 240f
    private const val ICON   = 52f   // fixed icon square, like Spotify's ART=60
    private const val CORNER = 10f
    private const val PAD    = 8f
    private const val TEXT_X = PAD + ICON + PAD   // 68f — text column starts here

    // ── HUD ───────────────────────────────────────────────────────────────────

    val petHud = hudElement("pet-display", "Pet Display", "Animated pet info HUD") {
        anchor  = HudAnchor.BOTTOM_RIGHT
        offsetX = 10f
        offsetY = 10f

        fun hudHeight(): Float {
            val hasItem = showHeldItemSetting.value &&
                PetTabListParser.current?.heldItem?.isNotEmpty() == true
            return if (hasItem) 84f else 70f
        }

        width  { W }
        height { hudHeight() }

        render { x, y, _ ->
            if (!enabledSetting.value) return@render

            val now  = System.currentTimeMillis()
            val twoPi = (Math.PI * 2.0).toFloat()
            val h    = hudHeight()
            val c1   = ThemeManager.currentTheme.accent
            val c2   = ThemeManager.currentTheme.accentSecondary
            val data = PetTabListParser.current

            // ── Glow (identical to Spotify's glowSetting block) ───────────────
            if (glowSetting.value) {
                val pulse = 0.4f + 0.6f * cos((now % 4000L).toFloat() / 4000f * twoPi)
                val a2 = (0x18 * pulse).toInt().coerceIn(0, 0x28)
                val a1 = (0x2A * pulse).toInt().coerceIn(0, 0x40)
                NVGRenderer.hollowRect(x - 3f,   y - 3f,   W + 6f, h + 6f, 2.5f, (a2 shl 24) or (c1 and 0x00FFFFFF), CORNER + 3f)
                NVGRenderer.hollowRect(x - 1.5f, y - 1.5f, W + 3f, h + 3f, 1.5f, (a1 shl 24) or (c1 and 0x00FFFFFF), CORNER + 1.5f)
            }

            // ── Background (identical to Spotify's drawBackground) ────────────
            NVGRenderer.rect(x, y, W, h, 0xFF0A0E1A.toInt(), CORNER)
            NVGRenderer.gradientRect(x, y, W, h * 0.5f, 0x14FFFFFF, 0x00000000, Gradient.TopToBottom, CORNER)
            val shiftX = cos((now % 10000L).toFloat() / 10000f * twoPi) * (W * 0.42f)
            NVGRenderer.hollowGradientRectShifted(x, y, W, h, 1.5f, c1, c2, Gradient.LeftToRight, CORNER, shiftX, 0f)

            // ── Icon area (like Spotify's album art slot) ─────────────────────
            val iconX = x + PAD
            val iconY = y + PAD
            NVGRenderer.rect(iconX, iconY, ICON, ICON, 0xFF101521.toInt(), 6f)
            NVGRenderer.hollowRect(iconX, iconY, ICON, ICON, 1f, 0x22FFFFFF, 6f)

            if (data == null) {
                // Diamond symbol centered in icon area (like Spotify's musical note)
                val sym  = "✦"
                val symW = NVGRenderer.textWidth(sym, 20f)
                NVGRenderer.text(sym, iconX + ICON / 2f - symW / 2f, iconY + ICON / 2f - 10f, 20f, 0x33FFFFFF)
                NVGRenderer.text("No pet active", x + TEXT_X, y + h / 2f + 5f, 10f, 0x80FFFFFF.toInt())
                return@render
            }

            // Colored symbol when pet is active
            val sym  = "✦"
            val symW = NVGRenderer.textWidth(sym, 20f)
            NVGRenderer.text(sym, iconX + ICON / 2f - symW / 2f, iconY + ICON / 2f - 10f, 20f, c1)

            val textColor = 0xFFFFFFFF.toInt()
            val dimColor  = 0xBBB0C8FF.toInt()

            // ── Pet name + level (like Spotify's title row) ───────────────────
            val lvStr = "Lv ${data.level}"
            val lvW   = NVGRenderer.textWidth(lvStr, 10f)
            NVGRenderer.textShadow(data.name, x + TEXT_X, y + 18f, 12f, textColor)
            NVGRenderer.text(lvStr, x + W - PAD - lvW, y + 18f, 10f, 0xFF7EC8FF.toInt())

            // ── Held item (like Spotify's artist row) ─────────────────────────
            if (showHeldItemSetting.value && data.heldItem.isNotEmpty()) {
                NVGRenderer.text(data.heldItem, x + TEXT_X, y + 33f, 10f, dimColor)
            }

            // ── XP progress bar (like Spotify's drawProgressBar) ─────────────
            val barX = x + TEXT_X
            val barY = y + h - PAD - 6f
            val barW = W - TEXT_X - PAD
            val barH = 6f
            val barR = barH / 2f

            NVGRenderer.rect(barX, barY, barW, barH, 0xFF1A2040.toInt(), barR)

            if (data.isMaxLevel) {
                NVGRenderer.gradientRect(barX, barY, barW, barH, 0xFFFFD700.toInt(), 0xFFFFA500.toInt(), Gradient.LeftToRight, barR)
                val maxW = NVGRenderer.textWidth("MAX", 8f)
                NVGRenderer.text("MAX", barX + barW / 2f - maxW / 2f, barY - 1f, 8f, 0xFFFFD700.toInt())
            } else if (data.xpRequired > 0) {
                val ratio = (data.xpCurrent.toFloat() / data.xpRequired.toFloat()).coerceIn(0f, 1f)
                if (ratio > 0f) {
                    val fillW = (barW * ratio).coerceAtLeast(0f)
                    if (fillW > barR * 2f) {
                        NVGRenderer.pushScissor(barX, barY, fillW, barH)
                        NVGRenderer.gradientRect(barX, barY, barW, barH, c1, c2, Gradient.LeftToRight, barR)
                        NVGRenderer.popScissor()
                        NVGRenderer.circle(barX + fillW, barY + barH / 2f, barH * 0.7f,  (0x88 shl 24) or (c1 and 0x00FFFFFF))
                        NVGRenderer.circle(barX + fillW, barY + barH / 2f, barH * 0.38f, 0xCCFFFFFF.toInt())
                    }
                }
                val pct  = "${(ratio * 100).toInt()}%"
                val pctW = NVGRenderer.textWidth(pct, 8f)
                NVGRenderer.text(pct, barX + barW / 2f - pctW / 2f, barY - 1f, 8f, dimColor)
            }
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        addSetting(enabledSetting, glowSetting, showHeldItemSetting)
        EventBus.register(this)
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
        if (!enabledSetting.value || !petHud.enabled) return
        PetTabListParser.update()
    }
}
