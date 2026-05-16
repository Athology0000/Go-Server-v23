package org.phantom.internal.mining

import kotlin.math.ceil
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.phantom.api.util.player.MovementManager
import org.phantom.internal.rotation.RotationsModule

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
      if (!canStepToNearbyTarget(player, pos)) continue
      if (blockIdAt(level, pos) !in titaniumIds) continue
      if (mineReachDistanceSq(player, pos) > rangeSq) continue
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
    val distSq = mineReachDistanceSq(player, sticky)
    val inAttackRange = distSq <= mineRange.value * mineRange.value

    // Only keep sticky when the block is already in attack range (stable mid-mine targeting).
    // When out of range, fall through and re-select the nearest block every tick so the
    // macro always approaches the closest available block first.
    if (inAttackRange && canStepToNearbyTarget(player, sticky)) {
      // Use the same aim-point LOS standard the rotation actually needs â€” a
      // center-to-center hasLineOfSight can pass while every usable face is
      // occluded, leaving the macro to stare through a block. If no visible
      // aim point exists from the current eye position, abandon sticky so the
      // selector either picks a different block or triggers movement.
      val visible = !REQUIRE_MINE_LOS || findVisibleAimPoint(level, player, player.eyePosition, sticky) != null
      if (visible) {
        currentTargetNoLosTicks = 0
        return sticky
      }
    }
  }
  currentTargetNoLosTicks = 0

  // Vein flow (directional targeting) unwired: target selection is driven purely
  // by the cost/distance scorer below. VeinDirectionModule remains but no longer
  // influences which block is mined.

  val rangeSq = mineRange.value * mineRange.value
  val eye = player.eyePosition

  // Strict closest-first: distance to the visible aim point is the only
  // criterion. Angle/pitch penalties were causing the macro to skip the
  // obviously-closest block in favor of slightly-better-aimed but farther
  // blocks, forcing the user to manually nudge the camera. Sticky targeting
  // (above) keeps the current pick stable across ticks, so the selector only
  // matters when the previous target is gone or invalid.
  val now = level.gameTime
  // Cost-based selection (V5 port): when enabled, score = base + dist*distW
  // - dot*lookBias + (1-visibilityStability)*visPen + behindPenalty. When
  // disabled, falls back to the original strict closest-aim-point distance.
  val costMode = costModelEnabled.value
  val lookVec = if (costMode) player.getViewVector(1.0f) else null
  val distW = costDistanceWeight.value
  val lookBias = costLookBias.value
  val visPen = costVisibilityPenalty.value
  val behindPen = costBehindPenalty.value
  val clusterBonus = costClusterBonus.value
  var bestInRange: net.minecraft.core.BlockPos? = null
  var bestScore = Double.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (!canStepToNearbyTarget(player, pos)) continue
    if (mineReachDistanceSq(player, pos) > rangeSq) continue
    val skipUntil = MiningMacroModule.stuckMiningTargets[pos.asLong()]
    if (skipUntil != null && skipUntil > now) continue
    val visiblePoint: Vec3? = if (REQUIRE_MINE_LOS) findVisibleAimPoint(level, player, eye, pos) else null
    if (REQUIRE_MINE_LOS && visiblePoint == null) continue
    val aimPoint = visiblePoint ?: Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    val dx = aimPoint.x - eye.x
    val dy = aimPoint.y - eye.y
    val dz = aimPoint.z - eye.z
    val score: Double
    if (!costMode) {
      score = dx * dx + dy * dy + dz * dz
    } else {
      val dist = sqrt(dx * dx + dy * dy + dz * dz)
      val dot = if (dist > 1e-6 && lookVec != null)
        (dx * lookVec.x + dy * lookVec.y + dz * lookVec.z) / dist
      else 1.0
      var s = baseCostFor(level, vein, pos) + dist * distW - dot * lookBias
      if (behindPen > 0.0 && dot < MiningMacroModule.BEHIND_DOT_THRESHOLD) {
        s += behindPen
      }
      // Below-player penalty (parity with the legacy miningTargetScore and the
      // approach branch). Without it the cost model happily picks a close,
      // well-aimed block *under* the player, pitching the camera straight down
      // ("looks down randomly"). Non-negative, so the prune bound stays valid.
      if (pos.y < player.blockY - 1) {
        s += 16.0
      }
      // Cluster bonus: a block surrounded by more vein blocks is cheaper, so
      // the macro commits to the dense clump instead of chasing a lone nearer
      // block. Deterministic and non-positive, so `s` stays an admissible
      // lower bound for the visibility prune below.
      if (clusterBonus > 0.0) {
        s -= clusterBonus * veinNeighborCount(vein, pos)
      }
      // Visibility-stability is the expensive term (5 raycasts). Its penalty is
      // non-negative, so `s` here is an admissible lower bound: if it already
      // can't beat the running best, skip the sampling entirely.
      if (visPen > 0.0 && s < bestScore) {
        s += (1.0 - visibilityStability(level, player, pos)) * visPen
      }
      score = s
    }
    if (score < bestScore) {
      bestScore = score
      bestInRange = pos
    }
  }
  if (bestInRange != null) return bestInRange

  // No in-range block - find best LOS block to walk/warp toward. With the cost
  // model on, rank by V5 approach cost (base + dist*distW, no look term) so the
  // chosen block is the cheapest to travel to, not just the geometrically
  // nearest; otherwise keep the original distance score.
  var best: net.minecraft.core.BlockPos? = null
  var bestDist = Double.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (!canStepToNearbyTarget(player, pos)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue
    val distSq = if (!costMode) {
      miningTargetScore(player, pos)
    } else {
      val ddx = (pos.x + 0.5) - eye.x
      val ddy = (pos.y + 0.5) - eye.y
      val ddz = (pos.z + 0.5) - eye.z
      val d = sqrt(ddx * ddx + ddy * ddy + ddz * ddz)
      val belowPenalty = if (pos.y < player.blockY - 1) 16.0 else 0.0
      baseCostFor(level, vein, pos) + d * distW + belowPenalty
    }
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
      if (!canStepToNearbyTarget(player, pos)) continue
      val distSq = mineReachDistanceSq(player, pos)
      if (distSq > rangeSq) continue
      if (!isCrosshairOnTarget(pos)) continue
      if (distSq < bestDist) {
        bestDist = distSq
        best = pos
      }
    }
  }
  if (best != null) return best

  // Last resort: nothing in range is visible and the crosshair isn't on a vein
  // block (the cold-start case where the macro must still rotate to face the
  // vein). Returning null makes onTick stop rotating, so the player has to look
  // manually. We must return something so the rotation controller acquires it,
  // BUT preferring the geometrically nearest block lets it lock onto a buried
  // block and stare. So prefer, in order: nearest with a visible aim point,
  // then nearest with center LOS, then nearest overall. The crosshair-first
  // gate in onTick still blocks any dig packet until a real face is hit.
  return selectAcquisitionFallback(level, player, vein)
}

/**
 * Cold-start / no-LOS acquisition target. Prefers a block the macro can
 * actually mine soon (visible aim point > center LOS) over the merely-closest
 * one, so it rotates toward a mineable block instead of a buried one.
 */
internal fun MiningMacroModule.selectAcquisitionFallback(
  level: net.minecraft.world.level.Level,
  player: Player,
  vein: TargetVein,
): net.minecraft.core.BlockPos? {
  val eye = player.eyePosition
  var visBest: net.minecraft.core.BlockPos? = null
  var visScore = Double.POSITIVE_INFINITY
  var losBest: net.minecraft.core.BlockPos? = null
  var losScore = Double.POSITIVE_INFINITY
  for (pos in vein.blocks) {
    if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
    if (!canStepToNearbyTarget(player, pos)) continue
    val score = miningTargetScore(player, pos)
    if (findVisibleAimPoint(level, player, eye, pos) != null) {
      if (score < visScore) { visScore = score; visBest = pos }
    } else if (hasLineOfSight(level, player, pos)) {
      if (score < losScore) { losScore = score; losBest = pos }
    }
  }
  return visBest
    ?: losBest
    ?: selectNearestBlock(level, player, vein.blocks, vein.targetIds)
}

/**
 * V5 visibility-stability: sample the eye position at small lateral offsets and
 * return the fraction (0..1) of samples from which a visible aim point on
 * [pos] still exists. Blocks that flicker out of line-of-sight under tiny head
 * movement score low and get penalized by the cost scorer, which avoids
 * picking targets that lose LOS mid-mine as the rotation smoother moves.
 */
private val VIS_STAB_OFFSETS = doubleArrayOf(
  0.0, 0.0,
  0.18, 0.0,
  -0.18, 0.0,
  0.0, 0.18,
  0.0, -0.18,
)

/**
 * Number of the 26 surrounding positions that are also blocks in this vein,
 * capped at [CLUSTER_NEIGHBOR_CAP] so the bonus stays bounded. Pure set
 * lookups (no world reads) so it's cheap to call per candidate.
 */
private const val CLUSTER_NEIGHBOR_CAP = 12

internal fun MiningMacroModule.veinNeighborCount(
  vein: TargetVein,
  pos: net.minecraft.core.BlockPos,
): Int {
  val blocks = vein.blocks
  var count = 0
  for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
    if (dx == 0 && dy == 0 && dz == 0) continue
    if (blocks.contains(pos.offset(dx, dy, dz))) {
      count++
      if (count >= CLUSTER_NEIGHBOR_CAP) return count
    }
  }
  return count
}

/** Port-6 base cost for [pos]: the vein's per-id weight, or the default. */
internal fun MiningMacroModule.baseCostFor(
  level: net.minecraft.world.level.Level,
  vein: TargetVein,
  pos: net.minecraft.core.BlockPos,
): Double = vein.weights[blockIdAt(level, pos)] ?: MiningMacroModule.DEFAULT_TARGET_BASE_COST

internal fun MiningMacroModule.visibilityStability(
  level: net.minecraft.world.level.Level,
  player: Player,
  pos: net.minecraft.core.BlockPos,
): Double {
  val eye = player.eyePosition
  var visible = 0
  var i = 0
  while (i < VIS_STAB_OFFSETS.size) {
    val sampleEye = Vec3(eye.x + VIS_STAB_OFFSETS[i], eye.y, eye.z + VIS_STAB_OFFSETS[i + 1])
    if (findVisibleAimPoint(level, player, sampleEye, pos) != null) visible++
    i += 2
  }
  return visible / (VIS_STAB_OFFSETS.size / 2.0)
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

internal fun MiningMacroModule.miningTargetScore(
  player: Player,
  pos: net.minecraft.core.BlockPos,
): Double {
  val eye = player.eyePosition
  val dx = (pos.x + 0.5) - eye.x
  val dy = (pos.y + 0.5) - eye.y
  val dz = (pos.z + 0.5) - eye.z

  // Do not let the selector over-prefer blocks below the visible vein just because
  // they are close to the player's feet. If they are truly the only remaining valid
  // blocks, they can still be selected after visible blocks are gone.
  val belowPenalty = if (pos.y < player.blockY - 1) 16.0 else 0.0
  return dx * dx + dy * dy + dz * dz + belowPenalty
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
    if (!canStepToNearbyTarget(player, pos)) continue
    val distSq = miningTargetScore(player, pos)
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
    if (!canStepToNearbyTarget(player, pos)) continue
    if (REQUIRE_MINE_LOS && !hasLineOfSight(level, player, pos)) continue
    val distSq = miningTargetScore(player, pos)
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
  // Actually turn onto the block. Without this the controller is never told to
  // rotate for the mining target, so the macro swings without turning.
  driveMiningRotation(player, target, aim.point, precisionRotScale)
  frameRotSnapThreshold = RotationsModule.bezierSnapThreshold.value.toFloat()
  frameRotTarget = aim.point
  setAimRenderTarget(target, aim.point)
  // Route the precision aim point into the rotation controller. Without this,
  // a precision point that only becomes available *after* mining starts (the
  // common case â€” the precision tracker resolves the sub-block aim during the
  // first few mining ticks) never reaches the controller and the chance % has
  // no visible effect.
  if (aim.usesPrecisionPoint) {
    org.phantom.internal.rotation.PhantomRotation.blockController.setPrecisionPoint(aim.point)
  } else {
    org.phantom.internal.rotation.PhantomRotation.blockController.setPrecisionPoint(null)
  }
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
  setAimRenderTarget(null, null)
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
