package org.phantom.internal.combat.slayer

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks

internal object EndermanSlayerPhase {
  const val ATTACHED_TEXT_RANGE_SQ = 9.0
  const val BEACON_SCAN_RADIUS = 16

  fun isHitShieldText(normalizedName: String): Boolean =
    HIT_SHIELD_PATTERNS.any { pattern -> pattern.containsMatchIn(normalizedName) } ||
      HIT_SHIELD_TEXT_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  fun isLaserText(normalizedName: String): Boolean =
    LASER_TEXT_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  fun nearestBeacon(level: ClientLevel, player: Player): BlockPos? {
    val origin = player.blockPosition()
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY

    for (dx in -BEACON_SCAN_RADIUS..BEACON_SCAN_RADIUS) {
      for (dy in -BEACON_SCAN_RADIUS..BEACON_SCAN_RADIUS) {
        for (dz in -BEACON_SCAN_RADIUS..BEACON_SCAN_RADIUS) {
          val pos = origin.offset(dx, dy, dz)
          if (!level.getBlockState(pos).`is`(Blocks.BEACON)) continue
          val distSq = player.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
          if (distSq >= bestDistSq) continue
          best = pos
          bestDistSq = distSq
        }
      }
    }

    return best
  }

  private val HIT_SHIELD_PATTERNS = arrayOf(
    Regex("\\b\\d+[,.]?\\d*\\s+hits?\\b"),
    Regex("\\bhits?\\s*[:x-]?\\s*\\d+[,.]?\\d*\\b"),
    Regex("\\b\\d+[,.]?\\d*\\s+hit shield\\b"),
  )
  private val HIT_SHIELD_TEXT_KEYWORDS = arrayOf("hits", "shield", "shielded", "immune")
  private val LASER_TEXT_KEYWORDS = arrayOf("aligning", "lasers", "laser", "align")
}
