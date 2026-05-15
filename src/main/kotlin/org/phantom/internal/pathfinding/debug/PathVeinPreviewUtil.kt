package org.phantom.internal.pathfinding.debug

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import kotlin.math.abs

object PathVeinPreviewUtil {

  data class StandCandidate(
    val standPos: BlockPos,
    val score: Double
  )

  fun bestStandForVein(vein: BlockPos): StandCandidate? {
    return allStandCandidates(vein).firstOrNull()
  }

  fun allStandCandidates(vein: BlockPos): List<StandCandidate> {
    val mc = Minecraft.getInstance()
    val level = mc.level
    val player = mc.player

    val candidates = mutableListOf<StandCandidate>()

    for (dy in -1..1) {
      for (dx in -3..3) {
        for (dz in -3..3) {
          if (dx == 0 && dz == 0) continue
          if (abs(dx) + abs(dz) > 4) continue

          val pos = BlockPos(vein.x + dx, vein.y + dy, vein.z + dz)
          if (level != null && !level.worldBorder.isWithinBounds(pos)) continue

          val playerDistance = if (player != null) {
            player.blockPosition().distManhattan(pos).toDouble()
          } else {
            0.0
          }

          val veinDistance = abs(dx).toDouble() + abs(dy).toDouble() + abs(dz).toDouble()
          val score = veinDistance * 10.0 + playerDistance
          candidates += StandCandidate(pos, score)
        }
      }
    }

    return candidates.sortedBy { it.score }
  }
}
