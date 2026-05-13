package org.cobalt.api.util

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerInfo

/**
 * Centralized tab-list (player-list overlay) scraping helpers. Replaces the
 * `connection.onlinePlayers.mapNotNull { info.tabListDisplayName?.string
 *  ?: info.profile.name }` boilerplate duplicated across HypixelManager,
 * CustomScoreboardModule, PetTabListParser, PestTabListParser, PathManager,
 * NoFrillsMiningModule, CommissionMacroModule, GlaciteCommissionMacroModule,
 * CombatMacroModule and AutoLanternModule.
 *
 * Each helper degrades to an empty result when the network connection is not
 * available, so callers can chain without null guards.
 */
object TabListUtils {

  private val mc: Minecraft get() = Minecraft.getInstance()

  /** All [PlayerInfo] entries currently in the tab list. */
  fun onlinePlayers(): Collection<PlayerInfo> =
    mc.connection?.onlinePlayers ?: emptyList()

  /**
   * Raw tab-list display strings (with chat formatting codes still embedded).
   * Order matches `connection.onlinePlayers` iteration. Falls back to the
   * profile name when an entry has no tab display name (legacy/offline mode).
   */
  fun rawDisplayNames(): List<String> =
    onlinePlayers().mapNotNull { info ->
      info.tabListDisplayName?.string ?: info.profile.name
    }

  /**
   * Strip-formatted display strings, no whitespace normalization. The most
   * common shape callers want — what CombatMacroModule, CustomScoreboardModule
   * and the Pet/Pest parsers all build inline.
   */
  fun displayNames(): List<String> =
    rawDisplayNames().map { ChatFormatting.stripFormatting(it).orEmpty() }

  /**
   * Returns true when any tab-list line contains [needle] (case-insensitive).
   * Useful for "are we in dungeon?" / "is the goblin still up?" probes that
   * are otherwise expressed as `displayNames().any { it.contains(...) }`.
   */
  fun anyLineContains(needle: String): Boolean {
    if (needle.isEmpty()) return false
    return displayNames().any { it.contains(needle, ignoreCase = true) }
  }
}
