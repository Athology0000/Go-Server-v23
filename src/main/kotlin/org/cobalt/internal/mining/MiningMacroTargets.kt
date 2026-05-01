package org.cobalt.internal.mining

import kotlin.math.ceil
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.rotation.RotationsModule

internal typealias TargetVein = MiningMacroModule.Vein

internal fun MiningMacroModule.selectMineTarget(
  level: net.minecraft.world.level.Level,
  player: Player,
  vein: TargetVein
): net.minecraft.core.BlockPos? {
  // Priority: in-range titanium blocks beat everything else (including sticky non-titanium targets).
  // This ensures titanium that appears next to a mithril vein is mined immediately.
  val titaniumIds = MiningBlockRegistry.idsForType("Titanium")
  if (titaniumIds.any { it in vein.targetIds }) {
    val rangeSq = mineRange.value * mineRange.value
    val eye = player.eyePosition
    var bestTi: net.minecraft.core.BlockPos? = null
    var bestTiAngle = Float.POSITIVE_INFINITY
    for (pos in vein.blocks) {
      if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
      if (blockIdAt(level, pos) !in titaniumIds) continue
      if (distanceToBlockSq(player, pos) > rangeSq) continue
      val visiblePoint = if (REQUIRE_MINE_LOS) findVisibleAimPoint(level, player, eye, pos) else null
      if (REQUIRE_MINE_LOS && visiblePoint == null) continue
      val aimPoint = visiblePoint ?: Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
      val ang = angularDistanceTo(player, aimPoint)
      if (ang < bestTiAngle) {
        bestTiAngle = ang
        bestTi = pos
      }
    }
    if (bestTi != null) return bestTi
  }

  val sticky = currentTarget?.takeIf { vein.blocks.contains(it) && isMineableTarget(level, player, it, vein.targetIds) }
  if (sticky != null) {
    val distSq = distanceToBlockSq(player, sticky)
    val inAttackRange = distSq <= mineRange.value * mineRange.value

    // Only keep sticky when the block is already in attack range (stable mid-mine targeting).
    // When out of range, fall through and re-select the nearest block every tick so the
    // macro always approaches the closest available block first.
    if (inAttackRange) {
      val hasLos = !REQUIRE_MINE_LOS || hasLineOfSight(level, player, sticky)
      if (hasLos) {
        currentTargetNoLosTicks = 0
        return sticky
      }

      val stickyRange = mineRange.value + TARGET_STICKY_RANGE_EXTRA
      if (distSq <= stickyRange * stickyRange && currentTargetNoLosTicks < TARGET_LOS_GRACE_TICKS) {
        currentTargetNoLosTicks++
        return sticky
      }
    }
  }
  currentTargetNoLosTicks = 0

  if (useVeinDirection.value) {
    val directional = selectDirectionalMineTarget(level, player, vein)
    if (directional != null) {
      return directional
    }
  }

  val rangeSq = mineRange.value * mineRange.value
  val eye = player.eyePosition

  // For in-range blocks, prefer the one whose visible face is closest to the
  // player's current aim. Using the actual face point (not block center) gives
  // the real rotation cost and avoids picking blocks that require more turn than
  // their center angle implies.
  var bestInRange: net.minecraft.core.BlockPos? = null
  var bestAngle = Float.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (distanceToBlockSq(player, pos) > rangeSq) continue
    val visiblePoint: Vec3? = if (REQUIRE_MINE_LOS) findVisibleAimPoint(level, player, eye, pos) else null
    if (REQUIRE_MINE_LOS && visiblePoint == null) continue
    val aimPoint = visiblePoint ?: Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    val ang = angularDistanceTo(player, aimPoint)
    if (ang < bestAngle) {
      bestAngle = ang
      bestInRange = pos
    }
  }
  if (bestInRange != null) return bestInRange

  // No in-range block - find closest LOS block to walk/warp toward.
  var best: net.minecraft.core.BlockPos? = null
  var bestDist = Double.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue
    val distSq = distanceToBlockSq(player, pos)
    if (distSq < bestDist) {
      bestDist = distSq
      best = pos
    }
  }
  // If no block has LOS, fall back only to in-range blocks where the crosshair is already
  // on the block. This lets us click without rotating (avoiding aiming through bedrock).
  if (best == null) {
    for (pos in vein.blocks) {
      if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
      val distSq = distanceToBlockSq(player, pos)
      if (distSq > rangeSq) continue
      if (!isCrosshairOnTarget(pos)) continue
      if (distSq < bestDist) {
        bestDist = distSq
        best = pos
      }
    }
  }
  return best
}

internal fun MiningMacroModule.selectDirectionalMineTarget(
  level: net.minecraft.world.level.Level,
  player: Player,
  vein: TargetVein
): net.minecraft.core.BlockPos? {
  val flows = VeinDirectionModule.getFlowsForVein(vein.blockId)
  if (flows.isEmpty()) {
    return null
  }

  val flow = resolveDirectionalFlow(vein, flows) ?: return null
  val vecX = (flow.end.x - flow.start.x).toDouble()
  val vecY = (flow.end.y - flow.start.y).toDouble()
  val vecZ = (flow.end.z - flow.start.z).toDouble()
  val len = sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ)
  if (len < 0.001) return null

  val nx = vecX / len
  val ny = vecY / len
  val nz = vecZ / len

  val activeAnchor = currentTarget?.takeIf { vein.blocks.contains(it) }
  val anchor = activeAnchor
    ?: selectClosestBlockToReference(level, player, vein.blocks, flow.start, vein.targetIds)
    ?: selectNearestBlock(level, player, vein.blocks, vein.targetIds)
    ?: return null

  if (activeAnchor == null) {
    return anchor
  }

  val forward = selectForwardDirectionalTarget(level, player, vein.blocks, anchor, nx, ny, nz, vein.targetIds)
  if (forward != null) {
    return forward
  }

  if (!REQUIRE_MINE_LOS || hasLineOfSight(level, player, anchor)) {
    return anchor
  }
  return selectNearestBlock(level, player, vein.blocks, vein.targetIds)
}

internal fun MiningMacroModule.resolveDirectionalFlow(
  vein: TargetVein,
  flows: List<VeinDirectionModule.VeinFlow>
): VeinDirectionModule.VeinFlow? {
  currentDirectionalFlow?.let { cached ->
    if (flows.contains(cached) && flowDistanceSqToVein(cached, vein) <= FLOW_MATCH_MAX_DIST_SQ) {
      return cached
    }
  }

  var best: VeinDirectionModule.VeinFlow? = null
  var bestDistSq = Double.POSITIVE_INFINITY
  for (flow in flows) {
    val distSq = flowDistanceSqToVein(flow, vein)
    if (distSq < bestDistSq) {
      bestDistSq = distSq
      best = flow
    }
  }

  if (best == null || bestDistSq > FLOW_MATCH_MAX_DIST_SQ) {
    currentDirectionalFlow = null
    return null
  }

  currentDirectionalFlow = best
  return best
}

internal fun MiningMacroModule.flowDistanceSqToVein(flow: VeinDirectionModule.VeinFlow, vein: TargetVein): Double {
  val startDistSq = pointDistanceSqToBounds(flow.start, vein.bounds)
  val endDistSq = pointDistanceSqToBounds(flow.end, vein.bounds)
  return minOf(startDistSq, endDistSq)
}

internal fun MiningMacroModule.pointDistanceSqToBounds(pos: net.minecraft.core.BlockPos, bounds: net.minecraft.world.phys.AABB): Double {
  val px = pos.x + 0.5
  val py = pos.y + 0.5
  val pz = pos.z + 0.5

  val dx = when {
    px < bounds.minX -> bounds.minX - px
    px > bounds.maxX -> px - bounds.maxX
    else -> 0.0
  }
  val dy = when {
    py < bounds.minY -> bounds.minY - py
    py > bounds.maxY -> py - bounds.maxY
    else -> 0.0
  }
  val dz = when {
    pz < bounds.minZ -> bounds.minZ - pz
    pz > bounds.maxZ -> pz - bounds.maxZ
    else -> 0.0
  }

  return dx * dx + dy * dy + dz * dz
}

internal fun MiningMacroModule.selectClosestBlockToReference(
  level: net.minecraft.world.level.Level,
  player: Player,
  blocks: Set<net.minecraft.core.BlockPos>,
  reference: net.minecraft.core.BlockPos,
  allowedIds: Set<String>
): net.minecraft.core.BlockPos? {
  var best: net.minecraft.core.BlockPos? = null
  var bestDistSq = Double.POSITIVE_INFINITY
  for (pos in blocks) {
    if (!isMineableTarget(level, player, pos, allowedIds)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue
    val dx = (pos.x - reference.x).toDouble()
    val dy = (pos.y - reference.y).toDouble()
    val dz = (pos.z - reference.z).toDouble()
    val distSq = dx * dx + dy * dy + dz * dz
    if (distSq < bestDistSq) {
      bestDistSq = distSq
      best = pos
    }
  }
  return best
}

internal fun MiningMacroModule.selectForwardDirectionalTarget(
  level: net.minecraft.world.level.Level,
  player: Player,
  blocks: Set<net.minecraft.core.BlockPos>,
  anchor: net.minecraft.core.BlockPos,
  nx: Double,
  ny: Double,
  nz: Double,
  allowedIds: Set<String>
): net.minecraft.core.BlockPos? {
  var best: net.minecraft.core.BlockPos? = null
  var bestScore = Double.NEGATIVE_INFINITY
  for (pos in blocks) {
    if (pos == anchor) continue
    if (!isMineableTarget(level, player, pos, allowedIds)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue

    val relX = (pos.x - anchor.x).toDouble()
    val relY = (pos.y - anchor.y).toDouble()
    val relZ = (pos.z - anchor.z).toDouble()
    val distSq = relX * relX + relY * relY + relZ * relZ
    if (distSq <= 0.25 || distSq > DIRECTIONAL_STEP_MAX_DIST_SQ) continue

    val projection = relX * nx + relY * ny + relZ * nz
    if (projection <= DIRECTIONAL_MIN_PROJECTION) continue

    val lateralSq = (distSq - projection * projection).coerceAtLeast(0.0)
    if (lateralSq > DIRECTIONAL_MAX_LATERAL_SQ) continue

    val score = projection - sqrt(lateralSq) * 0.45 - sqrt(distSq) * 0.12
    if (score > bestScore) {
      bestScore = score
      best = pos
    }
  }
  return best
}

internal fun MiningMacroModule.selectNearestBlock(
  level: net.minecraft.world.level.Level,
  player: Player,
  blocks: Set<net.minecraft.core.BlockPos>,
  allowedIds: Set<String>? = null,
): net.minecraft.core.BlockPos? {
  var best: net.minecraft.core.BlockPos? = null
  var bestDist = Double.POSITIVE_INFINITY
  for (pos in blocks) {
    if (!isMineableTarget(level, player, pos, allowedIds)) continue
    val distSq = distanceToBlockSq(player, pos)
    if (distSq < bestDist) {
      bestDist = distSq
      best = pos
    }
  }
  return best
}

internal fun MiningMacroModule.resolvePreviewTarget(
  level: net.minecraft.world.level.Level,
  player: Player
): net.minecraft.core.BlockPos? {
  val vein = currentVein ?: return currentTarget
  val active = miningOnTarget

  currentTarget
    ?.takeIf { it != active && vein.blocks.contains(it) && isMineableTarget(level, player, it, vein.targetIds) }
    ?.let { return it }

  var best: net.minecraft.core.BlockPos? = null
  var bestDist = Double.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (pos == active) continue
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue
    val distSq = distanceToBlockSq(player, pos)
    if (distSq < bestDist) {
      bestDist = distSq
      best = pos
    }
  }
  return best ?: vein.blocks.firstOrNull { it != active && isMineableTarget(level, player, it, vein.targetIds) }
}

internal fun MiningMacroModule.startMining(player: Player, target: net.minecraft.core.BlockPos) {
  if (target != miningOnTarget) {
    miningOnTarget = target
    miningOnTargetTicks = 0
    miningLockedTicks = 0
  }
  miningOnTargetTicks++
  val currentAim = resolveMiningAimPoint(player, target)
  miningUsesPrecisionPoint = currentAim.usesPrecisionPoint
  val maxTicksForBlock = maxMiningTicksForTarget(target)
  if (miningOnTargetTicks > maxTicksForBlock) {
    currentVein?.blocks?.remove(target)
    miningOnTarget = null
    miningOnTargetTicks = 0
    stopMiningKeys()
    return
  }
  if (isCrosshairOnTarget(target)) {
    miningLockedTicks++
  }
  val aim = resolveGlideAimTarget(player, target, currentAim)
  val precisionRotScale =
    if (aim.usesPrecisionPoint) (precisionPointRotationSpeed.value / 100.0).coerceAtLeast(0.1)
    else 1.0
  frameRotSnapThreshold = RotationsModule.bezierSnapThreshold.value.toFloat()
  frameRotTarget = aim.point
  frameRotSpeedScale = (RotationsModule.sample(RotationsModule.miningSpeedScale.value) * precisionRotScale).toFloat()
  frameRotAccelScale = (RotationsModule.sample(RotationsModule.miningAccelScale.value) * precisionRotScale).toFloat()
  frameRotPitchStep = (RotationsModule.sample(RotationsModule.miningPitchStep.value) * precisionRotScale).toFloat()
  frameRotMaxSpeed = (RotationsModule.sample(RotationsModule.miningMaxSpeed.value) * precisionRotScale).toFloat()
  frameRotMaxAccel = (RotationsModule.sample(RotationsModule.miningMaxAccel.value) * precisionRotScale).toFloat()
  setMiningAttackDown(true)
  miningActive = true
}

internal fun MiningMacroModule.setMiningAttackDown(attack: Boolean) {
  mc.options.keyAttack?.setDown(attack)
  mc.options.keyUse?.setDown(false)
  MovementManager.forcedActionsEnabled = attack
  MovementManager.forcedAttack = attack
  MovementManager.forcedUse = false
}

internal fun MiningMacroModule.maxMiningTicksForTarget(target: net.minecraft.core.BlockPos): Int {
  val calculatedTicks = MiningModule.getCalculatedLookTicks(includePingDelay = true)
  if (calculatedTicks > 0.0) {
    return ceil(calculatedTicks * MINING_TIMEOUT_SCALE + MINING_TIMEOUT_EXTRA_TICKS)
      .toInt()
      .coerceAtLeast(MIN_MINING_TICKS_PER_BLOCK)
  }

  val level = mc.level
  val blockId = level?.let { blockIdAt(it, target) }
  val blockType = blockId?.let { MiningBlockRegistry.BLOCK_ID_TO_TYPE[it] }
  val hardness = blockType?.let { MiningBlockRegistry.BLOCK_HARDNESS[it] }
  val fallbackTicks = MiningBreakThresholds.getOptimalTicks(blockType, FALLBACK_TIMEOUT_MINING_SPEED, hardness)
  return ceil(fallbackTicks * MINING_TIMEOUT_SCALE + MINING_TIMEOUT_EXTRA_TICKS)
    .toInt()
    .coerceAtLeast(MIN_MINING_TICKS_PER_BLOCK)
}

internal fun MiningMacroModule.stopMiningKeys() {
  if (miningActive) {
    setMiningAttackDown(false)
    miningActive = false
  } else {
    setMiningAttackDown(false)
  }
  frameRotTarget = null
  frameRotSnapThreshold = 0f
  miningOnTarget = null
  miningOnTargetTicks = 0
  miningLockedTicks = 0
  miningUsesPrecisionPoint = false
}

internal fun MiningMacroModule.maybeRefreshLantern(
  level: net.minecraft.world.level.Level,
  player: Player
): Boolean {
  val needsRefresh =
    !lanternPlacedForVein ||
      !AutoLanternModule.isLanternBuffActive() ||
      lastLanternRefreshTick < 0L ||
      level.gameTime - lastLanternRefreshTick >= LANTERN_REFRESH_TICKS
  if (!needsRefresh) {
    return false
  }
  if (tryPlaceLantern(level, player)) {
    lanternPlacedForVein = true
    lastLanternRefreshTick = level.gameTime
    return true
  }
  return false
}

internal fun MiningMacroModule.tryPlaceLantern(
  level: net.minecraft.world.level.Level,
  player: Player
): Boolean {
  if (level.gameTime - lastLanternPlaceTick < LANTERN_PLACE_COOLDOWN_TICKS) {
    return false
  }

  val lanternSlot = findLanternHotbarSlot(player)
  if (lanternSlot !in 0..8) {
    return false
  }
  if (AutoLanternModule.isPlacementInProgress()) {
    return false
  }

  if (!AutoLanternModule.tryStartLanternPlacement(lanternSlot)) {
    return false
  }
  lastLanternPlaceTick = level.gameTime
  return true
}

internal fun MiningMacroModule.findLanternHotbarSlot(player: Player): Int {
  val inventory = player.inventory
  for (i in 0..8) {
    val stack = inventory.getItem(i)
    if (stack.isEmpty) continue
    val normalized = normalizeHotbarItemName(stack.hoverName.string)
    if (
      normalized.contains("mithril lantern") ||
      normalized.contains("titanium lantern") ||
      normalized.contains("glacite lantern") ||
      normalized.contains(WILL_O_WISP_NAME)
    ) {
      return i
    }
  }
  return -1
}

internal fun MiningMacroModule.normalizeHotbarItemName(rawName: String): String {
  return (ChatFormatting.stripFormatting(rawName) ?: rawName)
    .lowercase()
    .replace(Regex("[^a-z0-9]+"), " ")
    .trim()
}
