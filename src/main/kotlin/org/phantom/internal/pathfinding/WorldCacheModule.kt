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
import org.phantom.api.util.ScoreboardUtils
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
        val map = snapshot.map.takeIf(::isKnown)
        val basePlace = map
            ?: snapshot.placeName.takeIf(::isKnown)
            ?: CachedWorld.getWorldDisplayName().takeIf(::isKnown)
        val isDwarven = listOfNotNull(map, snapshot.area, snapshot.mode, snapshot.placeName, basePlace)
            .any { it.equals("Dwarven Mines", ignoreCase = true) }
        val sublocation = if (isDwarven) {
            detectDwarvenSidebarSublocation()
        } else {
            null
        }
        val place = when {
            isDwarven && sublocation != null -> "Dwarven Mines - $sublocation"
            isDwarven -> "Dwarven Mines"
            basePlace != null -> basePlace
            else -> snapshot.placeName.takeIf(::isKnown)
        } ?: CachedWorld.getWorldDisplayName().takeIf(::isKnown)
            ?: "Unknown"

        val locationParts = mutableListOf<String>()
        snapshot.serverType.takeIf(::isKnown)?.let { locationParts += it }
        snapshot.mode.takeIf { isKnown(it) && !matchesDisplayPlace(it, place, map, sublocation) }?.let { locationParts += it }
        snapshot.serverName.takeIf(::isKnown)?.let { locationParts += it }

        val detailPrefix = locationParts.joinToString(" - ").takeIf { it.isNotBlank() }
        val cacheStats = CachedWorld.getCacheStats()
        val detail = if (detailPrefix != null) "$detailPrefix - $cacheStats" else cacheStats
        return place to detail
    }

    private fun isKnown(value: String): Boolean =
        value.isNotBlank() && !value.equals("Unknown", ignoreCase = true)

    private fun matchesDisplayPlace(value: String, place: String, map: String?, sublocation: String?): Boolean =
        value.equals(place, ignoreCase = true) ||
            map?.let { value.equals(it, ignoreCase = true) } == true ||
            sublocation?.let { value.equals(it, ignoreCase = true) } == true

    private fun detectDwarvenSidebarSublocation(): String? {
        val lines = buildList {
            ScoreboardUtils.sidebarObjective()?.displayName?.string?.let { add(it) }
            addAll(ScoreboardUtils.sidebarLines())
        }
        for (line in lines) {
            val normalized = normalizeLocationKey(line)
            for ((needle, display) in DWARVEN_SIDEBAR_LOCATIONS) {
                if (normalized.contains(needle)) return display
            }
        }
        return null
    }

    private fun normalizeLocationKey(raw: String): String =
        raw.lowercase()
            .replace("'", "")
            .replace(Regex("""[^a-z0-9]+"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

    private val DWARVEN_SIDEBAR_LOCATIONS = linkedMapOf(
        "royal mines" to "Royal Mines",
        "cliffside veins" to "Cliffside Veins",
        "upper mines" to "Upper Mines",
        "ramparts quarry" to "Rampart's Quarry",
        "rampart quarry" to "Rampart's Quarry",
        "lava springs" to "Lava Springs",
        "the forge" to "The Forge",
        "forge" to "The Forge",
        "aristocrat passage" to "Aristocrat Passage",
        "palace bridge" to "Palace Bridge",
        "hanging court" to "Hanging Court",
        "great ice wall" to "Great Ice Wall",
        "goblin burrows" to "Goblin Burrows",
        "far reserve" to "Far Reserve",
        "divans gateway" to "Divan's Gateway",
        "miners guild" to "Miner's Guild",
        "dwarven village" to "Dwarven Village",
        // Glacite subspaces are reported under the Dwarven Mines parent by the
        // SkyBlock location API, so the scoreboard area is the only signal that
        // we're in Glacite Tunnels et al. Without these the HUD just showed
        // "Dwarven Mines" with no "- Glacite Tunnels".
        "glacite tunnels" to "Glacite Tunnels",
        "glacite mineshafts" to "Glacite Mineshafts",
        "glacite mineshaft" to "Glacite Mineshafts",
        "fossil research center" to "Fossil Research Center",
        "dwarven base camp" to "Dwarven Base Camp",
        "great glacite lake" to "Great Glacite Lake",
    )
}
