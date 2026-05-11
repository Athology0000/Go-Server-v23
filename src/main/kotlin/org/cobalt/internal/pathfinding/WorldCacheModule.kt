package org.cobalt.internal.pathfinding

import kotlin.math.max
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.pathfinder.cache.CachedWorld
import org.cobalt.api.ui.theme.ThemeGradient
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.ui.theme.ThemeSurface
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient
import org.cobalt.internal.skyblock.HypixelManager

object WorldCacheModule : Module("World Cache HUD") {

    override val category = ModuleCategory.VISUAL

    private val textSize    = 13f
    private val lineHeight  = textSize + 5f
    private val padding     = 10f
    private val barHeight   = 10f
    private val barTopGap   = 6f
    private val corner      = 8f
    private val minWidth    = 140f

    val worldCacheHud = hudElement("world-cache-hud", "World Cache HUD", "Shows current map and pathfinder chunk cache fill") {
        anchor  = HudAnchor.TOP_LEFT
        offsetX = 10f
        offsetY = 220f

        val onlyInSkyblock = setting(CheckboxSetting("Only In SkyBlock", "Hide when not on a SkyBlock island", false))

        width {
            max(NVGRenderer.textWidth(HypixelManager.currentPlaceName(), textSize), minWidth) + padding * 2
        }

        height {
            lineHeight + barTopGap + barHeight + padding * 2
        }

        render { x, y, _ ->
            val map = HypixelManager.currentPlaceName()
            if (onlyInSkyblock.value && map == "Unknown") return@render

            val ready  = CachedWorld.readyChunkCount
            val total  = CachedWorld.MAXIMUM_CACHED_CHUNKS
            val ratio  = (ready.toFloat() / total).coerceIn(0f, 1f)
            val pct    = (ratio * 100).toInt()

            val panelW = max(NVGRenderer.textWidth(map, textSize), minWidth) + padding * 2
            val panelH = lineHeight + barTopGap + barHeight + padding * 2
            val (gradientStart, gradientEnd) = ThemeGradient.colors()

            NVGRenderer.rect(x, y, panelW, panelH, ThemeSurface.panelSolid(), corner)
            NVGRenderer.gradientRect(x, y, panelW, panelH * 0.5f, ThemeSurface.overlay(), 0x00000000, Gradient.TopToBottom, corner)
            NVGRenderer.hollowRect(x, y, panelW, panelH, 1.5f,
                ThemeGradient.withAlpha(gradientStart, 0x55), corner)

            NVGRenderer.text(map, x + padding, y + padding, textSize, ThemeManager.currentTheme.accent)

            val barX  = x + padding
            val barY  = y + padding + lineHeight + barTopGap
            val barW  = panelW - padding * 2
            val fillW = (barW * ratio).coerceAtLeast(if (ratio > 0f) barHeight else 0f)

            NVGRenderer.rect(barX, barY, barW, barHeight, ThemeSurface.track(), barHeight / 2f)
            NVGRenderer.hollowRect(barX, barY, barW, barHeight, 1f, 0x22FFFFFF, barHeight / 2f)

            if (fillW > 0f) {
                NVGRenderer.gradientRect(
                    barX, barY, fillW, barHeight,
                    gradientStart,
                    gradientEnd,
                    Gradient.LeftToRight,
                    barHeight / 2f
                )
            }

            val pctStr  = "$pct%"
            val pctSize = 10f
            val pctW    = NVGRenderer.textWidth(pctStr, pctSize)
            NVGRenderer.text(pctStr, barX + (barW - pctW) / 2f, barY - 0.5f, pctSize, 0xFFF4F7FB.toInt())
        }
    }
}
