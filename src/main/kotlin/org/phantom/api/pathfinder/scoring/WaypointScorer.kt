package org.phantom.api.pathfinder.scoring

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

object WaypointScorer {
  data class Score(
    val totalCost: Double,
    val distanceCost: Double = totalCost,
    val crowdingCost: Double = 0.0,
    val dangerCost: Double = 0.0,
    val failureCost: Double = 0.0,
  )

  private val failedWaypoints = LinkedHashMap<BlockPos, Int>()

  fun markFailed(pos: BlockPos) {
    failedWaypoints[pos.immutable()] = FAILURE_TTL
    trimFailures()
  }

  fun decayFailures() {
    val decayed = failedWaypoints
      .mapValues { (_, ttl) -> ttl - 1 }
      .filterValues { ttl -> ttl > 0 }

    failedWaypoints.clear()
    failedWaypoints.putAll(decayed)
  }

  fun reset() {
    failedWaypoints.clear()
  }

  fun collectNearbyPlayers(radius: Double = 64.0): List<Player> {
    val mc = Minecraft.getInstance()
    val player = mc.player ?: return emptyList()
    val radiusSq = radius * radius
    return mc.level
      ?.players()
      ?.filter { it.uuid != player.uuid && it.distanceToSqr(player) <= radiusSq }
      ?: emptyList()
  }

  fun collectDangerEntities(radius: Double = 64.0): List<Entity> {
    val mc = Minecraft.getInstance()
    val player = mc.player ?: return emptyList()
    return mc.level
      ?.getEntities(player, player.boundingBox.inflate(radius))
      ?.filter { entity ->
        entity.name.string.contains("sentry", ignoreCase = true) ||
          entity.name.string.contains("boss", ignoreCase = true)
      }
      ?: emptyList()
  }

  fun bestWaypoint(
    waypoints: List<BlockPos>,
    avoidPoints: List<BlockPos> = emptyList(),
    dangerEntities: List<Entity> = emptyList(),
    crowdingPlayers: List<Player> = emptyList(),
  ): BlockPos? {
    val playerPos = Minecraft.getInstance().player?.position() ?: return waypoints.firstOrNull()
    return waypoints
      .filterNot { avoidPoints.contains(it) }
      .minByOrNull { waypoint ->
        scoreWaypoint(
          playerPos = playerPos,
          waypoint = waypoint,
          dangerEntities = dangerEntities,
          crowdingPlayers = crowdingPlayers,
        ).totalCost
      }
  }

  fun scoreWaypoint(
    playerPos: Vec3,
    waypoint: BlockPos,
    dangerEntities: List<Entity> = emptyList(),
    crowdingPlayers: List<Player> = emptyList(),
  ): Score {
    val center = Vec3(waypoint.x + 0.5, waypoint.y.toDouble(), waypoint.z + 0.5)
    val distanceCost = playerPos.distanceTo(center)
    val crowdingCost = crowdingPlayers.sumOf { player ->
      proximityPenalty(player.position(), center, radius = 10.0, weight = 25.0)
    }
    val dangerCost = dangerEntities.sumOf { entity ->
      proximityPenalty(entity.position(), center, radius = 12.0, weight = 40.0)
    }
    val failureCost = (failedWaypoints[waypoint] ?: 0) * 25.0

    return Score(
      totalCost = distanceCost + crowdingCost + dangerCost + failureCost,
      distanceCost = distanceCost,
      crowdingCost = crowdingCost,
      dangerCost = dangerCost,
      failureCost = failureCost,
    )
  }

  private fun proximityPenalty(pos: Vec3, waypoint: Vec3, radius: Double, weight: Double): Double {
    val distance = pos.distanceTo(waypoint)
    if (distance >= radius) return 0.0
    return (1.0 - distance / radius) * weight
  }

  private fun Vec3.distanceTo(other: Vec3): Double {
    val dx = x - other.x
    val dy = y - other.y
    val dz = z - other.z
    return sqrt(dx * dx + dy * dy + dz * dz)
  }

  private fun trimFailures() {
    while (failedWaypoints.size > MAX_FAILURES) {
      val first = failedWaypoints.keys.firstOrNull() ?: break
      failedWaypoints.remove(first)
    }
  }

  private const val FAILURE_TTL = 8
  private const val MAX_FAILURES = 128
}
