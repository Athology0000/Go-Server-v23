package org.cobalt.internal.mining

import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.rotation.RotationsModule

internal fun MiningMacroModule.nudgeToward(player: Player, target: BlockPos) {
  nudgeToward(player, Vec3(target.x + 0.5, player.y, target.z + 0.5))
}

internal fun MiningMacroModule.nudgeToward(player: Player, targetPoint: Vec3) {
  val dx = targetPoint.x - player.x
  val dz = targetPoint.z - player.z
  val len = sqrt(dx * dx + dz * dz)
  if (len < 0.05) {
    MovementManager.clearForcedMovement()
    return
  }
  val nx = dx / len
  val nz = dz / len
  val yawRad = Math.toRadians(player.yRot.toDouble())
  val sinYaw = sin(yawRad)
  val cosYaw = cos(yawRad)
  // Project world-space direction onto player's local forward (+Z in local) and strafe (+X) axes.
  val fwd = (-nx * sinYaw + nz * cosYaw).toFloat()
  val str = (nx * cosYaw + nz * sinYaw).toFloat()
  val threshold = 0.35f
  MovementManager.setForcedMovement(
    forward = fwd > threshold,
    backward = fwd < -threshold,
    right = str > threshold,
    left = str < -threshold,
    jump = false, shift = false, sprint = false
  )
}

internal fun MiningMacroModule.nudgeTowardApproach(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
  avoidPlayer: Player? = null,
) {
  val shortStepLimit = maxNearbyStepDistance()
  val approach = findApproach(
    level,
    player,
    target,
    avoidPlayer = avoidPlayer,
    preferredStandOff = preferredNearbyStepDistance(),
    maxTravelDistance = shortStepLimit,
  )
  val stepPoint = approach?.let { Vec3(it.x + 0.5, player.y, it.z + 0.5) }
    ?: resolveNearbyStepPoint(player, target, avoidPlayer)
  nudgeToward(player, clampNearbyStepPoint(player, stepPoint, shortStepLimit))
}

internal fun MiningMacroModule.resolveNearbyStepPoint(
  player: Player,
  target: BlockPos,
  avoidPlayer: Player? = null,
): Vec3 {
  val centerX = target.x + 0.5
  val centerZ = target.z + 0.5
  var dirX = player.x - centerX
  var dirZ = player.z - centerZ
  avoidPlayer?.let { blocker ->
    dirX += (player.x - blocker.x) * 0.9
    dirZ += (player.z - blocker.z) * 0.9
  }
  var len = sqrt(dirX * dirX + dirZ * dirZ)
  if (len < 1.0e-4) {
    dirX = 1.0
    dirZ = 0.0
    len = 1.0
  }
  val scale = preferredNearbyStepDistance() / len
  return Vec3(centerX + dirX * scale, player.y, centerZ + dirZ * scale)
}

internal fun MiningMacroModule.clampNearbyStepPoint(player: Player, point: Vec3, maxDistance: Double): Vec3 {
  val dx = point.x - player.x
  val dz = point.z - player.z
  val distSq = dx * dx + dz * dz
  if (distSq <= maxDistance * maxDistance) return point
  val dist = sqrt(distSq)
  if (dist <= 1.0e-4) return point
  val scale = maxDistance / dist
  return Vec3(player.x + dx * scale, player.y, player.z + dz * scale)
}

internal fun MiningMacroModule.trySidestepAroundBlockingPlayer(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
  blocker: Player,
): Boolean {
  if (player.distanceToSqr(blocker) > BLOCKING_PLAYER_REACT_RANGE * BLOCKING_PLAYER_REACT_RANGE) {
    return false
  }

  val sidestepPoint = resolveBlockingPlayerSidestep(level, player, target, blocker) ?: return false
  stopApproachMovement()
  nudgeToward(player, sidestepPoint)
  focusApproachTarget(player, target)
  return true
}

internal fun MiningMacroModule.resolveBlockingPlayerSidestep(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
  blocker: Player,
): Vec3? {
  val targetCenterX = target.x + 0.5
  val targetCenterZ = target.z + 0.5
  val towardTargetX = targetCenterX - player.x
  val towardTargetZ = targetCenterZ - player.z
  val towardTargetLen = sqrt(towardTargetX * towardTargetX + towardTargetZ * towardTargetZ)
  if (towardTargetLen < 1.0e-4) {
    return null
  }

  val nx = towardTargetX / towardTargetLen
  val nz = towardTargetZ / towardTargetLen
  val leftX = -nz
  val leftZ = nx
  val rightX = nz
  val rightZ = -nx
  val candidates = listOf(
    Vec3(
      player.x + leftX * BLOCKING_PLAYER_SIDESTEP_DISTANCE + nx * BLOCKING_PLAYER_SIDESTEP_FORWARD,
      player.y,
      player.z + leftZ * BLOCKING_PLAYER_SIDESTEP_DISTANCE + nz * BLOCKING_PLAYER_SIDESTEP_FORWARD,
    ),
    Vec3(
      player.x + rightX * BLOCKING_PLAYER_SIDESTEP_DISTANCE + nx * BLOCKING_PLAYER_SIDESTEP_FORWARD,
      player.y,
      player.z + rightZ * BLOCKING_PLAYER_SIDESTEP_DISTANCE + nz * BLOCKING_PLAYER_SIDESTEP_FORWARD,
    ),
    Vec3(
      player.x + leftX * BLOCKING_PLAYER_SIDESTEP_DISTANCE,
      player.y,
      player.z + leftZ * BLOCKING_PLAYER_SIDESTEP_DISTANCE,
    ),
    Vec3(
      player.x + rightX * BLOCKING_PLAYER_SIDESTEP_DISTANCE,
      player.y,
      player.z + rightZ * BLOCKING_PLAYER_SIDESTEP_DISTANCE,
    ),
  )

  var best: Vec3? = null
  var bestScore = Double.POSITIVE_INFINITY
  val playerEyeOffset = player.eyeY - player.y
  val maxTargetDist = mineRange.value + NUDGE_RANGE_EXTRA
  val preferredTargetDist = preferredNearbyStepDistance()

  for (candidate in candidates) {
    val candidateBlock = BlockPos.containing(candidate.x, player.y, candidate.z)
    val resolvedBlock =
      if (MinecraftPathingRules.isWalkable(level, candidateBlock)) {
        candidateBlock
      } else {
        findNearestWalkableAround(level, candidateBlock, 1, 1)
      } ?: continue

    val point = Vec3(resolvedBlock.x + 0.5, player.y, resolvedBlock.z + 0.5)
    val eye = Vec3(point.x, point.y + playerEyeOffset, point.z)
    val visiblePoint = findVisibleAimPoint(level, player, eye, target) ?: continue
    val blockerDistSq = horizontalDistSq(point.x, point.z, blocker.x, blocker.z)
    if (blockerDistSq < BLOCKING_PLAYER_MIN_CLEARANCE * BLOCKING_PLAYER_MIN_CLEARANCE) {
      continue
    }
    val targetDist = sqrt(horizontalDistSq(point.x, point.z, targetCenterX, targetCenterZ))
    if (targetDist > maxTargetDist) {
      continue
    }
    val moveDistSq = horizontalDistSq(point.x, point.z, player.x, player.z)
    val targetDistPenalty = abs(targetDist - preferredTargetDist) * 18.0
    val visibleDistSq = eye.distanceToSqr(visiblePoint)
    val blockerBonus = blockerDistSq * 0.45
    val score = moveDistSq + targetDistPenalty * targetDistPenalty + visibleDistSq * 0.03 - blockerBonus
    if (score < bestScore) {
      bestScore = score
      best = point
    }
  }

  return best
}

internal fun MiningMacroModule.horizontalDistSq(x1: Double, z1: Double, x2: Double, z2: Double): Double {
  val dx = x1 - x2
  val dz = z1 - z2
  return dx * dx + dz * dz
}

internal fun MiningMacroModule.canStepToNearbyTarget(player: Player, target: BlockPos): Boolean {
  if (RoutesModule.isRunning && RoutesModule.routeOwnsMining) return true
  val anchor = veinStartAnchor ?: return true
  val r = anchorRadius.value
  if (player.blockPosition().distSqr(anchor) > r * r) return false
  val maxTargetDist = r + mineRange.value
  return anchor.distSqr(target) <= maxTargetDist * maxTargetDist
}

internal fun MiningMacroModule.hasDriftedFromVeinStart(player: Player): Boolean {
  if (RoutesModule.isRunning && RoutesModule.routeOwnsMining) return false
  val anchor = veinStartAnchor ?: return false
  val r = anchorRadius.value
  return player.blockPosition().distSqr(anchor) > r * r
}

internal fun MiningMacroModule.returnToVeinAnchor(
  level: net.minecraft.world.level.Level,
  player: Player,
) {
  val anchor = veinStartAnchor ?: return
  val arrivalSq = RETURN_TO_ANCHOR_ARRIVAL_DIST * RETURN_TO_ANCHOR_ARRIVAL_DIST
  if (player.blockPosition().distSqr(anchor) <= arrivalSq) {
    stopApproachMovement()
    MovementManager.clearForcedMovement()
    return
  }

  val destination =
    if (MinecraftPathingRules.isWalkable(level, anchor)) {
      anchor
    } else {
      findNearestWalkableAround(level, anchor, RETURN_TO_ANCHOR_SCAN_RADIUS, RETURN_TO_ANCHOR_SCAN_VERTICAL)
    } ?: run {
      MovementManager.clearForcedMovement()
      return
    }

  if (!nativeActive() || lastPathTarget == null || lastPathTarget?.distSqr(destination) ?: 0.0 > 1.0) {
    if (level.gameTime - lastPathStartTick < 8L) {
      return
    }
    lastPathStartTick = level.gameTime
    NativePathfinder.setTarget(destination.x + 0.5, destination.y.toDouble(), destination.z + 0.5)
    startedPath = true
    lastPathTarget = destination
  }
  // Ticking is handled by the early path block in onTick to avoid double-ticking.
}

internal fun MiningMacroModule.findNearestWalkableAround(
  level: net.minecraft.world.level.Level,
  origin: BlockPos,
  radius: Int,
  vertical: Int,
): BlockPos? {
  var best: BlockPos? = null
  var bestDistSq = Double.POSITIVE_INFINITY
  for (dy in -vertical..vertical) {
    for (dx in -radius..radius) {
      for (dz in -radius..radius) {
        val pos = origin.offset(dx, dy, dz)
        if (!MinecraftPathingRules.isWalkable(level, pos)) continue
        val distSq = pos.distSqr(origin).toDouble()
        if (distSq < bestDistSq) {
          bestDistSq = distSq
          best = pos
        }
      }
    }
  }
  return best
}

internal fun MiningMacroModule.captureGoldenGoblinReturnSpot(player: Player): Vec3 {
  val level = mc.level ?: return player.position()
  val origin = player.blockPosition()
  val destination =
    if (MinecraftPathingRules.isWalkable(level, origin)) {
      origin
    } else {
      findNearestWalkableAround(level, origin, RETURN_TO_ANCHOR_SCAN_RADIUS, RETURN_TO_ANCHOR_SCAN_VERTICAL)
    } ?: return player.position()
  return Vec3(destination.x + 0.5, destination.y.toDouble(), destination.z + 0.5)
}

internal fun MiningMacroModule.beginGoldenGoblinInterrupt() {
  if (goldenGoblinInterruptActive) return

  goldenGoblinInterruptActive = true
  goldenGoblinInterruptOwnedCombat = true
  goldenGoblinReturnPending = false
  goldenGoblinReturnPos = mc.player?.let { captureGoldenGoblinReturnSpot(it) }
  if (startedPath && nativeActive()) {
    nativeStop()
  } else {
    MovementManager.clearForcedMovement()
  }
  startedPath = false
  lastPathTarget = null
  lastPathStartTick = 0L
  currentTarget = null
  currentTargetNoLosTicks = 0
  resetApproachTracking()
  stopMiningKeys()
  RotationExecutor.stopRotating()
  CombatMacroModule.startForAutomation(GOLDEN_GOBLIN_NAME)
  ChatUtils.sendMessage("Mining macro: Golden Goblin spawned, interrupting vein.")
}

internal fun MiningMacroModule.handleGoldenGoblinInterrupt(
  level: net.minecraft.world.level.Level,
  player: Player,
) {
  stopMiningKeys()
  currentTarget = null
  currentTargetNoLosTicks = 0

  if (!CombatMacroModule.isActive) {
    CombatMacroModule.startForAutomation(GOLDEN_GOBLIN_NAME)
    goldenGoblinInterruptOwnedCombat = true
  }

  val goblin = findGoldenGoblin(player)
  if (goblin != null) {
    goldenGoblinLastSeenTick = level.gameTime
    return
  }

  if (goldenGoblinLastSeenTick >= 0L && level.gameTime - goldenGoblinLastSeenTick >= GOLDEN_GOBLIN_LOST_TICKS) {
    finishGoldenGoblinInterrupt()
  }
}

internal fun MiningMacroModule.finishGoldenGoblinInterrupt() {
  if (goldenGoblinInterruptOwnedCombat && CombatMacroModule.isActive) {
    CombatMacroModule.stopForAutomation()
  }
  if (nativeActive()) {
    nativeStop()
  }
  goldenGoblinInterruptActive = false
  goldenGoblinInterruptOwnedCombat = false
  goldenGoblinLastSeenTick = -1L
  startedPath = false
  lastPathTarget = null
  lastPathStartTick = 0L
  resetApproachTracking()
  MovementManager.clearForcedMovement()
  RotationExecutor.stopRotating()
  goldenGoblinReturnPending = goldenGoblinReturnPos != null
  if (goldenGoblinReturnPending) {
    ChatUtils.sendMessage("Mining macro: Golden Goblin handled, returning to mining spot.")
  } else {
    ChatUtils.sendMessage("Mining macro: Golden Goblin handled, resuming vein.")
  }
}

internal fun MiningMacroModule.findGoldenGoblin(player: Player): LivingEntity? {
  val level = mc.level ?: return null
  val maxDistSq = 64.0 * 64.0
  return level.entitiesForRendering()
    .asSequence()
    .mapNotNull { it as? LivingEntity }
    .filter { it.isAlive && it.health > 0f && it !== player }
    .firstOrNull { entity ->
      player.distanceToSqr(entity) <= maxDistSq &&
        CombatMacroModule.matchesAutomationTarget(entity, GOLDEN_GOBLIN_NAME)
    }
}

internal fun MiningMacroModule.handleGoldenGoblinReturn(
  level: net.minecraft.world.level.Level,
  player: Player,
) {
  stopMiningKeys()
  currentTarget = null
  currentTargetNoLosTicks = 0

  val returnPos = goldenGoblinReturnPos ?: run {
    goldenGoblinReturnPending = false
    return
  }
  val arrivalDistSq = 0.85 * 0.85
  if (player.position().distanceToSqr(returnPos) <= arrivalDistSq) {
    stopApproachMovement()
    MovementManager.clearForcedMovement()
    goldenGoblinReturnPending = false
    goldenGoblinReturnPos = null
    ChatUtils.sendMessage("Mining macro: Back at mining spot, resuming vein.")
    return
  }

  PathfindingModule.ensureEnabledForAutomation("golden goblin return")
  val rawDestination = BlockPos.containing(returnPos.x, returnPos.y, returnPos.z)
  val destination =
    if (MinecraftPathingRules.isWalkable(level, rawDestination)) {
      rawDestination
    } else {
      findNearestWalkableAround(level, rawDestination, RETURN_TO_ANCHOR_SCAN_RADIUS, RETURN_TO_ANCHOR_SCAN_VERTICAL)
    }

  if (destination == null) {
    nudgeToward(player, Vec3(returnPos.x, player.y, returnPos.z))
    return
  }

  if (player.blockPosition().distSqr(destination) <= 1.0) {
    stopApproachMovement()
    MovementManager.clearForcedMovement()
    goldenGoblinReturnPending = false
    goldenGoblinReturnPos = null
    ChatUtils.sendMessage("Mining macro: Back at mining spot, resuming vein.")
    return
  }

  if (!nativeActive() || lastPathTarget == null || lastPathTarget?.distSqr(destination) ?: 0.0 > 1.0) {
    if (level.gameTime - lastPathStartTick < 8L) {
      return
    }
    lastPathStartTick = level.gameTime
    NativePathfinder.setTarget(destination.x + 0.5, destination.y.toDouble(), destination.z + 0.5)
    startedPath = true
    lastPathTarget = destination
  }

  val cmd = NativePathfinder.tick()
  if (cmd != null) {
    cmd.applyToPlayer()
  } else {
    nudgeToward(player, Vec3(destination.x + 0.5, player.y, destination.z + 0.5))
  }
}

internal fun MiningMacroModule.moveToward(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
  forceApproach: Boolean = false,
  avoidPlayer: Player? = null,
  maxTravelDistance: Double = Double.POSITIVE_INFINITY,
) {
  PathfindingModule.ensureEnabledForAutomation("mining macro")
  val currentDistance = sqrt(distanceToBlockSq(player, target))
  val holdDistance = preferredApproachDistance()
  // Only skip stepping when we're already in an ideal spot: close AND with LOS.
  // Without the LOS check, a block that sits within 4 blocks but is occluded (corners,
  // ceiling ores, short walls) would never trigger a step to a visible position.
  if (!forceApproach && currentDistance <= holdDistance &&
    (!REQUIRE_MINE_LOS || hasLineOfSight(level, player, target))
  ) {
    stopApproachMovement()
    focusApproachTarget(player, target)
    return
  }
  if (approachTarget != target) {
    approachTarget = target
    approachStartTick = level.gameTime
    approachStartDistance = currentDistance
  } else if (
    level.gameTime - approachStartTick >= APPROACH_TIMEOUT_TICKS &&
    approachStartDistance - currentDistance < APPROACH_MIN_PROGRESS_BLOCKS
  ) {
    abandonApproachTarget(target)
    warnOnce("Mining macro: approach timed out, skipping block.")
    return
  }

  val approach = lastPathTarget?.takeIf {
    isApproachUsable(
      level,
      player,
      it,
      target,
      avoidPlayer = avoidPlayer,
      preferredStandOff = holdDistance,
      maxTravelDistance = maxTravelDistance,
    )
  } ?: findApproach(
    level,
    player,
    target,
    avoidPlayer = avoidPlayer,
    preferredStandOff = holdDistance,
    maxTravelDistance = maxTravelDistance,
  ) ?: run {
    stopApproachMovement()
    return
  }
  if (currentDistance <= holdDistance + 0.85) {
    focusApproachTarget(player, target)
  }
  if (!nativeActive() || lastPathTarget == null || lastPathTarget?.distSqr(approach) ?: 0.0 > 1.0) {
    if (level.gameTime - lastPathStartTick < 8L) {
      return
    }
    lastPathStartTick = level.gameTime
    NativePathfinder.setTarget(approach.x + 0.5, approach.y.toDouble(), approach.z + 0.5)
    startedPath = true
    lastPathTarget = approach
  }
  // Ticking is handled by the early path block in onTick to avoid double-ticking.
}

internal fun MiningMacroModule.abandonApproachTarget(target: BlockPos) {
  if (startedPath && nativeActive()) {
    nativeStop()
  } else {
    MovementManager.clearForcedMovement()
  }
  startedPath = false
  lastPathTarget = null
  lastPathStartTick = 0L
  resetApproachTracking()
  currentVein?.blocks?.remove(target)
  currentTarget = null
  currentTargetNoLosTicks = 0
  currentDirectionalFlow = null
}

internal fun MiningMacroModule.stopApproachMovement() {
  if (startedPath && nativeActive()) {
    nativeStop()
  } else {
    MovementManager.clearForcedMovement()
  }
  startedPath = false
  lastPathTarget = null
  lastPathStartTick = 0L
  resetApproachTracking()
}

internal fun MiningMacroModule.focusApproachTarget(player: Player, target: BlockPos) {
  val aim = resolveMiningAimPoint(player, target)
  val precisionRotScale =
    if (aim.usesPrecisionPoint) (precisionPointRotationSpeed.value / 100.0).coerceAtLeast(0.1)
    else 1.0
  frameRotTarget = aim.point
  frameRotSnapThreshold = RotationsModule.bezierSnapThreshold.value.toFloat()
  frameRotSpeedScale = (RotationsModule.sample(RotationsModule.miningSpeedScale.value) * precisionRotScale).toFloat()
  frameRotAccelScale = (RotationsModule.sample(RotationsModule.miningAccelScale.value) * precisionRotScale).toFloat()
  frameRotPitchStep = (RotationsModule.sample(RotationsModule.miningPitchStep.value) * precisionRotScale).toFloat()
  frameRotMaxSpeed = (RotationsModule.sample(RotationsModule.miningMaxSpeed.value) * precisionRotScale).toFloat()
  frameRotMaxAccel = (RotationsModule.sample(RotationsModule.miningMaxAccel.value) * precisionRotScale).toFloat()
}

internal fun MiningMacroModule.resetApproachTracking() {
  approachTarget = null
  approachStartTick = 0L
  approachStartDistance = 0.0
}

internal fun MiningMacroModule.findApproach(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
  avoidPlayer: Player? = null,
  preferredStandOff: Double = preferredApproachDistance(),
  maxTravelDistance: Double = Double.POSITIVE_INFINITY,
): BlockPos? {
  var best: BlockPos? = null
  var bestScore = Double.POSITIVE_INFINITY

  val primary = listOf(
    intArrayOf(1, 0, 0), intArrayOf(-1, 0, 0), intArrayOf(0, 0, 1), intArrayOf(0, 0, -1),
    intArrayOf(1, -1, 0), intArrayOf(-1, -1, 0), intArrayOf(0, -1, 1), intArrayOf(0, -1, -1),
    intArrayOf(1, 1, 0), intArrayOf(-1, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 1, -1),
    intArrayOf(1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, 1), intArrayOf(-1, 0, -1),
  )

  for (off in primary) {
    val candidate = target.offset(off[0], off[1], off[2])
    val score = approachScore(level, player, candidate, target, avoidPlayer, preferredStandOff, maxTravelDistance)
    if (score < bestScore) {
      bestScore = score
      best = candidate
    }
  }
  if (best != null) return best

  var dy = -APPROACH_SCAN_VERTICAL
  while (dy <= APPROACH_SCAN_VERTICAL) {
    var dx = -APPROACH_SCAN_RADIUS
    while (dx <= APPROACH_SCAN_RADIUS) {
      var dz = -APPROACH_SCAN_RADIUS
      while (dz <= APPROACH_SCAN_RADIUS) {
        if (dx != 0 || dy != 0 || dz != 0) {
          val candidate = target.offset(dx, dy, dz)
          val score =
            approachScore(level, player, candidate, target, avoidPlayer, preferredStandOff, maxTravelDistance)
          if (score < bestScore) {
            bestScore = score
            best = candidate
          }
        }
        dz++
      }
      dx++
    }
    dy++
  }

  if (best != null) return best
  return null
}

internal fun MiningMacroModule.isApproachUsable(
  level: net.minecraft.world.level.Level,
  player: Player,
  candidate: BlockPos,
  target: BlockPos,
  avoidPlayer: Player? = null,
  preferredStandOff: Double = preferredApproachDistance(),
  maxTravelDistance: Double = Double.POSITIVE_INFINITY,
): Boolean =
  approachScore(level, player, candidate, target, avoidPlayer, preferredStandOff, maxTravelDistance).isFinite()

internal fun MiningMacroModule.approachScore(
  level: net.minecraft.world.level.Level,
  player: Player,
  candidate: BlockPos,
  target: BlockPos,
  avoidPlayer: Player? = null,
  preferredStandOff: Double = preferredApproachDistance(),
  maxTravelDistance: Double = Double.POSITIVE_INFINITY,
): Double {
  if (!MinecraftPathingRules.isWalkable(level, candidate)) {
    return Double.POSITIVE_INFINITY
  }

  val centerDx = (candidate.x + 0.5) - (target.x + 0.5)
  val centerDy = (candidate.y + 0.5) - (target.y + 0.5)
  val centerDz = (candidate.z + 0.5) - (target.z + 0.5)
  val targetDistSq = centerDx * centerDx + centerDy * centerDy + centerDz * centerDz
  val targetDist = sqrt(targetDistSq)
  val standOff = preferredStandOff.coerceAtLeast(0.5)
  val maxMineDist = (mineRange.value - APPROACH_EDGE_MARGIN).coerceAtLeast(0.5)
  val minMineDist = (standOff - APPROACH_EDGE_WINDOW).coerceAtLeast(0.0)
  if (targetDist < minMineDist || targetDist > maxMineDist) {
    return Double.POSITIVE_INFINITY
  }

  if (REQUIRE_MINE_LOS && !hasLineOfSightFrom(level, player, candidate, target)) {
    return Double.POSITIVE_INFINITY
  }

  val playerDistSq = player.blockPosition().distSqr(candidate).toDouble()
  if (playerDistSq > maxTravelDistance * maxTravelDistance) {
    return Double.POSITIVE_INFINITY
  }
  val verticalPenalty = abs(candidate.y - target.y) * 0.75
  val edgePenalty = abs(targetDist - standOff) * 12.0
  var score = edgePenalty * edgePenalty + playerDistSq * 0.35 + verticalPenalty
  avoidPlayer?.let { blocker ->
    val blockerDx = (candidate.x + 0.5) - blocker.x
    val blockerDz = (candidate.z + 0.5) - blocker.z
    val blockerDistSq = blockerDx * blockerDx + blockerDz * blockerDz
    if (blockerDistSq < BLOCKING_PLAYER_MIN_CLEARANCE * BLOCKING_PLAYER_MIN_CLEARANCE) {
      return Double.POSITIVE_INFINITY
    }
    score += BLOCKING_PLAYER_PENALTY / blockerDistSq.coerceAtLeast(0.25)
  }
  return score
}

internal fun MiningMacroModule.preferredApproachDistance(): Double {
  return minOf(APPROACH_HOLD_DISTANCE, (mineRange.value - APPROACH_EDGE_MARGIN).coerceAtLeast(0.5))
}

internal fun MiningMacroModule.preferredNearbyStepDistance(): Double {
  return (mineRange.value - NEARBY_STEP_REACH_BUFFER).coerceAtLeast(0.5)
}

internal fun MiningMacroModule.maxNearbyStepDistance(): Double {
  return minOf(MAX_WALK_BLOCKS, mineRange.value + NEARBY_STEP_RANGE_EXTRA)
}

internal fun MiningMacroModule.angularDistanceTo(player: Player, target: BlockPos): Float =
  angularDistanceTo(player, Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5))

internal fun MiningMacroModule.angularDistanceTo(player: Player, point: Vec3): Float {
  val dx = point.x - player.x
  val dy = point.y - player.eyeY
  val dz = point.z - player.z
  val targetYaw = Math.toDegrees(kotlin.math.atan2(-dx, dz)).toFloat()
  val horizDist = sqrt(dx * dx + dz * dz)
  val targetPitch = Math.toDegrees(kotlin.math.atan2(-dy, horizDist)).toFloat()
  val yawDelta = abs(AngleUtils.getRotationDelta(player.yRot, targetYaw))
  val pitchDelta = abs(targetPitch - player.xRot)
  return sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta)
}

internal fun MiningMacroModule.hasLineOfSightFrom(
  level: net.minecraft.world.level.Level,
  player: Player,
  from: BlockPos,
  target: BlockPos
): Boolean {
  val eyeY = from.y + if (player.isShiftKeyDown) 1.54 else 1.62
  val eye = Vec3(from.x + 0.5, eyeY, from.z + 0.5)
  return losCheck(level, player, eye, target)
}

internal fun MiningMacroModule.losCheck(
  level: net.minecraft.world.level.Level,
  entity: net.minecraft.world.entity.Entity,
  eye: Vec3,
  target: BlockPos
): Boolean {
  return findVisibleAimPoint(level, entity, eye, target) != null
}

internal fun MiningMacroModule.findBlockingPlayer(
  level: net.minecraft.world.level.Level,
  player: Player,
  eye: Vec3,
  point: Vec3,
): Player? {
  val rayBounds = AABB(eye, point).inflate(BLOCKING_PLAYER_BOX_PADDING)
  var best: Player? = null
  var bestDistSq = eye.distanceToSqr(point)
  for (other in level.players()) {
    if (other == player) continue
    if (other.isSpectator || !other.isAlive) continue
    val hitBox = other.boundingBox.inflate(BLOCKING_PLAYER_BOX_PADDING)
    if (!rayBounds.intersects(hitBox)) continue
    val hit = hitBox.clip(eye, point).orElse(null) ?: continue
    val distSq = eye.distanceToSqr(hit)
    if (distSq < bestDistSq) {
      bestDistSq = distSq
      best = other
    }
  }
  return best
}

internal fun MiningMacroModule.findBlockingPlayerForTarget(
  level: net.minecraft.world.level.Level,
  player: Player,
  target: BlockPos,
): Player? {
  val eye = player.eyePosition
  var best: Player? = null
  var bestDistSq = Double.POSITIVE_INFINITY
  for (point in buildAimSamplePoints(eye, target)) {
    if (!canSeeAimPoint(level, player, eye, point, target, ignorePlayerObstructions = true)) continue
    val blocker = findBlockingPlayer(level, player, eye, point) ?: continue
    val distSq = player.distanceToSqr(blocker)
    if (distSq < bestDistSq) {
      bestDistSq = distSq
      best = blocker
    }
  }
  return best
}

internal fun MiningMacroModule.buildAimSamplePoints(eye: Vec3, target: BlockPos): List<Vec3> {
  val cx = target.x + 0.5
  val cy = target.y + 0.5
  val cz = target.z + 0.5
  // Offset toward the eye on each axis, clamped to stay within the block face
  val ox = ((eye.x - cx) * 0.49).coerceIn(-0.49, 0.49)
  val oy = ((eye.y - cy) * 0.49).coerceIn(-0.49, 0.49)
  val oz = ((eye.z - cz) * 0.49).coerceIn(-0.49, 0.49)
  val center = Vec3(cx, cy, cz)
  return listOf(
    center,
    Vec3(cx + ox, cy, cz),
    Vec3(cx, cy + oy, cz),
    Vec3(cx, cy, cz + oz),
    Vec3(cx + ox, cy + oy, cz + oz),
  )
}

internal fun MiningMacroModule.findVisibleAimPoint(
  level: net.minecraft.world.level.Level,
  entity: net.minecraft.world.entity.Entity,
  eye: Vec3,
  target: BlockPos,
  ignorePlayerObstructions: Boolean = false,
): Vec3? {
  for (point in buildAimSamplePoints(eye, target)) {
    if (canSeeAimPoint(level, entity, eye, point, target, ignorePlayerObstructions)) {
      return point
    }
  }
  return null
}

internal fun MiningMacroModule.canSeeAimPoint(
  level: net.minecraft.world.level.Level,
  entity: net.minecraft.world.entity.Entity,
  eye: Vec3,
  point: Vec3,
  target: BlockPos,
  ignorePlayerObstructions: Boolean = false,
): Boolean {
  val hit = level.clip(
    net.minecraft.world.level.ClipContext(
      eye,
      point,
      net.minecraft.world.level.ClipContext.Block.OUTLINE,
      net.minecraft.world.level.ClipContext.Fluid.NONE,
      entity
    )
  )
  if (hit.type != net.minecraft.world.phys.HitResult.Type.BLOCK || hit.blockPos != target) {
    return false
  }
  if (!ignorePlayerObstructions && entity is Player && findBlockingPlayer(level, entity, eye, point) != null) {
    return false
  }
  return true
}
