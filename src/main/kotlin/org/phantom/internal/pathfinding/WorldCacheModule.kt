package org.phantom.internal.pathfinding

import kotlin.math.max
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.pathfinder.cache.CachedWorld
import org.phantom.api.ui.theme.ThemeGradient
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.ui.theme.ThemeSurface
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.api.util.ui.helper.Gradient
import org.phantom.internal.skyblock.HypixelManager

object WorldCacheModule : Module("World Cache HUD") {

    override val category = ModuleCategory.VISUAL

    private val textSize    = 13f
    private val detailSize  = 10.5f
    private val lineHeight  = textSize + 5f
    private val detailHeight = detailSize + 4f
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
            val lines = displayLines(HypixelManager.snapshot())
            max(
                max(NVGRenderer.textWidth(lines.first, textSize), NVGRenderer.textWidth(lines.second, detailSize)),
                minWidth
            ) + padding * 2
        }

        height {
            lineHeight + detailHeight + barTopGap + barHeight + padding * 2
        }

        render { x, y, _ ->
            val snapshot = HypixelManager.snapshot()
            if (onlyInSkyblock.value && !snapshot.serverType.equals("SKYBLOCK", ignoreCase = true)) return@render
            val lines = displayLines(snapshot)

            val ready  = CachedWorld.readyChunkCount
            val total  = CachedWorld.MAXIMUM_CACHED_CHUNKS
            val ratio  = (ready.toFloat() / total).coerceIn(0f, 1f)
            val pct    = (ratio * 100).toInt()

            val panelW = max(
                max(NVGRenderer.textWidth(lines.first, textSize), NVGRenderer.textWidth(lines.second, detailSize)),
                minWidth
            ) + padding * 2
            val panelH = lineHeight + detailHeight + barTopGap + barHeight + padding * 2
            val (gradientStart, gradientEnd) = ThemeGradient.colors()

            NVGRenderer.rect(x, y, panelW, panelH, ThemeSurface.panelSolid(), corner)
            NVGRenderer.gradientRect(x, y, panelW, panelH * 0.5f, ThemeSurface.overlay(), 0x00000000, Gradient.TopToBottom, corner)
            NVGRenderer.hollowRect(x, y, panelW, panelH, 1.5f,
                ThemeGradient.withAlpha(gradientStart, 0x55), corner)

            NVGRenderer.text(lines.first, x + padding, y + padding, textSize, ThemeManager.currentTheme.accent)
            NVGRenderer.text(lines.second, x + padding, y + padding + lineHeight, detailSize, ThemeManager.currentTheme.textSecondary)

            val barX  = x + padding
            val barY  = y + padding + lineHeight + detailHeight + barTopGap
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

    private fun displayLines(snapshot: HypixelManager.LocationSnapshot): Pair<String, String> {
        val place = snapshot.placeName.takeIf(::isKnown)
            ?: CachedWorld.getWorldDisplayName().takeIf(::isKnown)
            ?: "Unknown"

        val locationParts = mutableListOf<String>()
        snapshot.serverType.takeIf(::isKnown)?.let { locationParts += it }
        snapshot.mode.takeIf { isKnown(it) && !it.equals(place, ignoreCase = true) }?.let { locationParts += it }
        snapshot.serverName.takeIf(::isKnown)?.let { locationParts += it }

        val detailPrefix = locationParts.joinToString(" - ").takeIf { it.isNotBlank() }
        val cacheStats = CachedWorld.getCacheStats()
        val detail = if (detailPrefix != null) "$detailPrefix - $cacheStats" else cacheStats
        return place to detail
    }

    private fun isKnown(value: String): Boolean =
        value.isNotBlank() && !value.equals("Unknown", ignoreCase = true)
}
