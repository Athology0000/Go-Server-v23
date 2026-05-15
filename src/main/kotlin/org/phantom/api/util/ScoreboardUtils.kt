package org.phantom.api.util

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerScoreEntry
import net.minecraft.world.scores.Scoreboard

/**
 * Centralized sidebar-scoreboard scraping helpers. Several modules
 * (CustomScoreboardModule, CommissionMacroModule, NoFrillsMiningModule,
 * MiningNukerController, BloodCampHelperModule, DungeonsModule,
 * DungeonScanState, CombatMacroModule, HypixelManager) all reproduce the same
 * `getDisplayObjective(SIDEBAR) -> listPlayerScores -> map display name`
 * boilerplate. Centralizing keeps semantics identical across callers.
 *
 * Each helper degrades to an empty result when no player/level/objective is
 * available, so callers can chain on the result without null guards.
 */
object ScoreboardUtils {

  private val mc: Minecraft get() = Minecraft.getInstance()

  /** The current sidebar objective, or null when none is displayed. */
  fun sidebarObjective(): Objective? {
    val scoreboard = mc.level?.scoreboard ?: return null
    return scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR)
  }

  /** Sidebar entries in scoreboard order. Empty when no sidebar is shown. */
  fun sidebarScores(): List<PlayerScoreEntry> {
    val scoreboard = mc.level?.scoreboard ?: return emptyList()
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
    return scoreboard.listPlayerScores(objective).toList()
  }

  /**
   * Returns the display component for [score], applying the owner's team
   * prefix/suffix when the entry has no explicit display name. Matches the
   * canonical vanilla rendering and what every existing caller does inline.
   */
  fun componentFor(scoreboard: Scoreboard, score: PlayerScoreEntry): Component {
    score.display()?.let { return it.copy() }
    val owner = score.owner()
    val team = scoreboard.getPlayersTeam(owner)
    return if (team == null) {
      score.ownerName().copy()
    } else {
      Component.empty()
        .append(team.playerPrefix.copy())
        .append(Component.literal(owner))
        .append(team.playerSuffix.copy())
    }
  }

  /** Sidebar lines as formatted [Component]s in scoreboard order. */
  fun sidebarComponents(): List<Component> {
    val scoreboard = mc.level?.scoreboard ?: return emptyList()
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
    return scoreboard.listPlayerScores(objective).map { componentFor(scoreboard, it) }
  }

  /**
   * Sidebar lines as strip-formatted plain strings in scoreboard order. The
   * most common shape â€” what NoFrillsMiningModule, MiningNukerController,
   * BloodCampHelperModule, etc. all build inline.
   */
  fun sidebarLines(): List<String> =
    sidebarComponents().map { ChatFormatting.stripFormatting(it.string).orEmpty() }
}
