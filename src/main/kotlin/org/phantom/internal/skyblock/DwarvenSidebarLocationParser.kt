package org.phantom.internal.skyblock

import java.util.Locale
import net.minecraft.ChatFormatting
import org.phantom.api.util.ScoreboardUtils

/**
 * Scoreboard-only Dwarven/Glacite location parser.
 *
 * Hypixel's location snapshot usually reports the parent map as Dwarven Mines.
 * The live sublocation is shown in the sidebar, so HUD text and mining-mode
 * switches should read this directly instead of depending on the snapshot.
 */
object DwarvenSidebarLocationParser {

    fun currentSidebarLocation(): String? {
        val lines = buildList {
            ScoreboardUtils.sidebarObjective()?.displayName?.string?.let { add(it) }
            addAll(ScoreboardUtils.sidebarLines())
        }
        return detect(lines)
    }

    fun detect(lines: Iterable<String>): String? {
        for (line in lines) {
            detect(line)?.let { return it }
        }
        return null
    }

    fun detect(raw: String): String? {
        val normalized = normalize(raw)
        if (normalized.isBlank()) return null
        return LOCATION_ALIASES
            .firstOrNull { (alias, _) -> normalized.contains(alias) }
            ?.second
    }

    fun isGlaciteLocation(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        val detected = detect(value) ?: value
        return normalize(detected) in GLACITE_LOCATION_KEYS
    }

    private fun normalize(raw: String): String =
        ChatFormatting.stripFormatting(raw).orEmpty()
            .lowercase(Locale.ROOT)
            .replace("'", "")
            .replace(Regex("""[^a-z0-9]+"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

    private val LOCATION_ALIASES = listOf(
        "fossil research center" to "Fossil Research Center",
        "grandpa wolfs cave" to "Grandpa Wolf's Cave",
        "grandpa wolf cave" to "Grandpa Wolf's Cave",
        "great glacite lake" to "Great Glacite Lake",
        "glacite mineshafts" to "Glacite Mineshafts",
        "glacite mineshaft" to "Glacite Mineshafts",
        "mineshafts" to "Glacite Mineshafts",
        "mineshaft" to "Glacite Mineshafts",
        "dwarven base camp" to "Dwarven Base Camp",
        "dwarven basecamp" to "Dwarven Base Camp",
        "base camp" to "Dwarven Base Camp",
        "the glacite tunnels" to "Glacite Tunnels",
        "glacite tunnels" to "Glacite Tunnels",
        "glacite tunnel" to "Glacite Tunnels",
        "glacite lake" to "Great Glacite Lake",
        "ramparts quarry" to "Rampart's Quarry",
        "rampart quarry" to "Rampart's Quarry",
        "royal mines" to "Royal Mines",
        "royal mine" to "Royal Mines",
        "cliffside veins" to "Cliffside Veins",
        "cliffside vein" to "Cliffside Veins",
        "upper mines" to "Upper Mines",
        "upper mine" to "Upper Mines",
        "lava springs" to "Lava Springs",
        "lava spring" to "Lava Springs",
        "aristocrat passage" to "Aristocrat Passage",
        "palace bridge" to "Palace Bridge",
        "hanging court" to "Hanging Court",
        "great ice wall" to "Great Ice Wall",
        "goblin burrows" to "Goblin Burrows",
        "goblin burrow" to "Goblin Burrows",
        "far reserve" to "Far Reserve",
        "divans gateway" to "Divan's Gateway",
        "divan gateway" to "Divan's Gateway",
        "miners guild" to "Miner's Guild",
        "miner guild" to "Miner's Guild",
        "dwarven village" to "Dwarven Village",
        "the mist" to "The Mist",
        "mist" to "The Mist",
        "gates to the mines" to "Gates to the Mines",
        "gate to the mines" to "Gates to the Mines",
        "the forge" to "The Forge",
        "forge" to "The Forge",
        "dwarven mines" to "Dwarven Mines",
    ).sortedByDescending { it.first.length }

    private val GLACITE_LOCATION_KEYS = setOf(
        "dwarven base camp",
        "glacite tunnels",
        "great glacite lake",
        "grandpa wolfs cave",
        "glacite mineshafts",
        "fossil research center",
    )
}
