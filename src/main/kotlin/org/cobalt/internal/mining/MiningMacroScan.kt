package org.cobalt.internal.mining

import net.minecraft.ChatFormatting
import kotlin.math.abs
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import org.cobalt.api.util.ChatUtils

internal typealias ScanOffset = MiningMacroModule.Offset
internal typealias ScanTypeSelection = MiningMacroModule.TypeSelection
internal typealias ScanVein = MiningMacroModule.Vein

internal fun MiningMacroModule.resolveTypeSelections(): List<ScanTypeSelection> {
  val result = mutableListOf<ScanTypeSelection>()
  for (rawLabel in getSelectedTypesInOrder()) {
    val label = MiningBlockRegistry.normalizeType(rawLabel)
    val baseIds =
      if (label.equals("Custom", ignoreCase = true)) {
        val automationIds = automationCustomBlockIds
          .asSequence()
          .filter { it.isNotEmpty() && !MiningBlockRegistry.isBlacklisted(it) }
          .toCollection(linkedSetOf())
        if (automationIds.isNotEmpty()) {
          automationIds
        } else {
          val detected = MiningModule.getDetectedBlockId()?.trim().orEmpty()
          if (useDetectedBlock.value && detected.isNotEmpty() && !MiningBlockRegistry.isBlacklisted(detected)) {
            linkedSetOf(detected)
          } else {
            emptySet()
          }
        }
      } else {
        val idsForLabel = MiningBlockRegistry.idsForType(label).toMutableSet()
        // Fold any automation-detected IDs whose type matches this label so the vein
        // flood-fill includes every nearby ore variant even when they aren't in the
        // static registry for this label (e.g. deepslate variants, mixed veins).
        for (id in automationCustomBlockIds) {
          if (id.isEmpty() || MiningBlockRegistry.isBlacklisted(id)) continue
          val mappedType = MiningBlockRegistry.BLOCK_ID_TO_TYPE[id] ?: continue
          if (MiningBlockRegistry.normalizeType(mappedType).equals(label, ignoreCase = true)) {
            idsForLabel.add(id)
          }
        }
        idsForLabel
      }
    val ids = expandSelectionIds(label, baseIds)
    if (ids.isNotEmpty()) {
      result.add(ScanTypeSelection(label, ids))
    }
  }
  return result
}

internal fun MiningMacroModule.expandSelectionIds(label: String, ids: Set<String>): Set<String> {
  if (ids.isEmpty()) return ids
  if (!label.startsWith("Mithril")) return ids

  val expanded = LinkedHashSet(ids)
  expanded.addAll(MiningBlockRegistry.idsForType("Titanium"))
  return expanded
}

internal fun MiningMacroModule.parseSelectedTypes(raw: String): List<String> {
  val result = LinkedHashSet<String>()
  for (part in raw.split(",")) {
    val label = part.trim()
    if (label.isEmpty() || label.equals("none", ignoreCase = true)) continue
    val normalized = MiningBlockRegistry.normalizeType(label)
    if (MiningBlockRegistry.BLOCK_HARDNESS.containsKey(normalized)) {
      result.add(normalized)
    }
  }
  return result.toList()
}

/** Horizontal distance the player must be within from a route mine point to
 *  pass the manual-start precondition. ~12 blocks is loose enough to allow
 *  standing on a platform or one floor up from the actual ore, tight enough
 *  that a vein on the other side of the room won't pass. */
internal const val MANUAL_START_NEAR_MINE_DIST = 12.0

/**
 * Manual macro start gate. Two paths:
 *  1. A route is loaded for [org.cobalt.internal.routes.RouteType.ORE_MINER]:
 *     player must be within [MANUAL_START_NEAR_MINE_DIST] of one of its
 *     MINE/VEIN points. If yes, arm the route and run the immediate scan
 *     so mining starts on whatever ore is in front of you while the route
 *     stays available for downstream automation (RoutesModule).
 *  2. No route: require an immediate vein near the player. Step-to-nearby
 *     handles small in-vein movement during mining; it's not a substitute
 *     for actually being at the vein when you press the toggle.
 *
 * Returns true if the macro is allowed to start.
 */
internal fun MiningMacroModule.validateManualStartPreconditions(
  level: net.minecraft.world.level.Level,
  player: Player,
  selections: List<ScanTypeSelection>
): Boolean {
  val pickedRouteName = oreMinerRoutePicker.value.trim()
  if (pickedRouteName.isNotEmpty()) {
    val route = org.cobalt.internal.routes.RouteStore.loadAll().firstOrNull {
      it.name == pickedRouteName && it.type == org.cobalt.internal.routes.RouteType.ORE_MINER
    }
    if (route != null) {
      val minePoints = (route.loopOrArea + route.travelRoute).filter {
        it.type == org.cobalt.internal.routes.RoutePointType.MINE ||
          it.type == org.cobalt.internal.routes.RoutePointType.VEIN
      }
      if (minePoints.isNotEmpty()) {
        val pp = player.blockPosition()
        val nearestDistSq = minePoints.minOf { pt ->
          val dx = (pt.x - pp.x).toDouble()
          val dy = (pt.y - pp.y).toDouble()
          val dz = (pt.z - pp.z).toDouble()
          dx * dx + dy * dy + dz * dz
        }
        if (nearestDistSq > MANUAL_START_NEAR_MINE_DIST * MANUAL_START_NEAR_MINE_DIST) {
          val dist = kotlin.math.sqrt(nearestDistSq)
          ChatUtils.sendMessage(
            "Mining macro: route '$pickedRouteName' is loaded but you're " +
              "${"%.1f".format(dist)}b from its nearest mine point. " +
              "Move within ${MANUAL_START_NEAR_MINE_DIST.toInt()}b and try again."
          )
          return false
        }
        org.cobalt.internal.routes.RouteStore.setLoaded(route)
        ChatUtils.sendMessage("Mining macro: route '${route.name}' armed at mine point.")
        if (!immediateStartScan(level, player, selections)) {
          ChatUtils.sendMessage("Mining macro: at route mine point, waiting for vein refresh...")
        }
        return true
      }
    }
  }

  // No route configured (or no usable mine points in it). Require a vein here.
  if (immediateStartScan(level, player, selections)) return true
  ChatUtils.sendMessage(
    "Mining macro: not in or near a vein. Stand next to ore (or load an Ore Miner route) and try again."
  )
  return false
}

internal fun MiningMacroModule.immediateStartScan(
  level: net.minecraft.world.level.Level,
  player: Player,
  selections: List<ScanTypeSelection>
): Boolean {
  val merged = mergeSelections(selections)
  val radius = scanRadius.value.toInt()
  val vertical = scanVertical.value.toInt()
  val offsets = buildOffsets(radius, vertical)
  val origin = automationScanAnchor ?: player.blockPosition()
  for (off in offsets) {
    val pos = origin.offset(off.dx, off.dy, off.dz)
    if (skippedSeeds.contains(pos.asLong())) continue
    if (!isMineableTarget(level, player, pos, merged.ids)) continue
    val vein = buildVein(level, player, pos, merged, maxVeinBlocks.value.toInt())
    if (SKIP_OCCUPIED_VEINS && isVeinOccupied(level, vein, player)) {
      skippedSeeds.add(pos.asLong())
      continue
    }
    currentVein = vein
    veinStartAnchor = origin.immutable()
    currentTargetNoLosTicks = 0
    currentDirectionalFlow = null
    lanternPlacedForVein = false
    lastLanternRefreshTick = -1L
    scanActive = false
    scanPriorityIndex = 0
    rememberVeinAnchor(pos)
    ChatUtils.sendMessage("Mining macro: found ${vein.typeLabel} vein with ${vein.blocks.size} blocks.")
    return true
  }
  return false
}

/**
 * Walk the learned vein anchor list, prefer the closest one, return true if a
 * vein got seeded. Each anchor is checked at its exact position and at a tiny
 * 1-block radius around it to absorb minor server respawn variance.
 */
internal fun MiningMacroModule.tryStartFromLearnedAnchors(
  level: net.minecraft.world.level.Level,
  player: Player,
  selections: List<ScanTypeSelection>
): Boolean {
  val merged = mergeSelections(selections)
  val playerPos = player.blockPosition()

  data class AnchorCandidate(val pos: BlockPos, val distSq: Double)
  val candidates = ArrayList<AnchorCandidate>(learnedVeinAnchors.size)
  for (key in learnedVeinAnchors) {
    val pos = BlockPos.of(key)
    if (skippedSeeds.contains(key)) continue
    candidates.add(AnchorCandidate(pos, distanceToBlockSq(player, pos)))
  }
  candidates.sortBy { it.distSq }

  for (cand in candidates) {
    val seed = findMineableNear(level, player, cand.pos, merged.ids) ?: continue
    val vein = buildVein(level, player, seed, merged, maxVeinBlocks.value.toInt())
    if (SKIP_OCCUPIED_VEINS && isVeinOccupied(level, vein, player)) {
      skippedSeeds.add(seed.asLong())
      continue
    }
    currentVein = vein
    veinStartAnchor = playerPos.immutable()
    currentTargetNoLosTicks = 0
    currentDirectionalFlow = null
    lanternPlacedForVein = false
    lastLanternRefreshTick = -1L
    scanActive = false
    scanPriorityIndex = 0
    rememberVeinAnchor(seed)
    ChatUtils.sendMessage(
      "Mining macro: resumed ${vein.typeLabel} vein from learned anchor (${vein.blocks.size} blocks)."
    )
    return true
  }
  return false
}

/** Search the 3×3×3 cube around [anchor] for a mineable seed. */
private fun MiningMacroModule.findMineableNear(
  level: net.minecraft.world.level.Level,
  player: Player,
  anchor: BlockPos,
  allowedIds: Set<String>
): BlockPos? {
  if (isMineableTarget(level, player, anchor, allowedIds)) return anchor
  for (dy in -1..1) for (dx in -1..1) for (dz in -1..1) {
    if (dx == 0 && dy == 0 && dz == 0) continue
    val p = anchor.offset(dx, dy, dz)
    if (isMineableTarget(level, player, p, allowedIds)) return p
  }
  return null
}

/**
 * Persist the vein seed to the correct per-area file. Area is determined by
 * the seed's Y coordinate (Y<189 = Dwarven, Y>=189 = Glacite) so anchors
 * always end up in the right file no matter where the player stands.
 */
internal fun MiningMacroModule.rememberVeinAnchor(pos: BlockPos) {
  if (!rememberVeinSpots.value) return
  MiningAnchorStore.add(pos, MiningMacroModule.LEARNED_ANCHOR_CAP)
}

internal fun MiningMacroModule.startOrContinueScan(
  level: net.minecraft.world.level.Level,
  player: Player,
  selections: List<ScanTypeSelection>
) {
  // Fixed-spot maps (Dwarven, Glacite camp): the same vein anchors respawn at
  // the same coordinates. Walking through the learned list is O(N) cheap, so
  // do it before the radius scan and we usually pick the right spot first try.
  if (rememberVeinSpots.value && learnedVeinAnchors.isNotEmpty()) {
    if (tryStartFromLearnedAnchors(level, player, selections)) {
      return
    }
  }

  val desiredOrigin = automationScanAnchor ?: player.blockPosition()
  if (!scanActive || desiredOrigin.distSqr(scanOrigin) > 0.0) {
    scanOrigin = desiredOrigin
    scanIndex = 0
    scanActive = true
    scanPriorityIndex = 0
  }

  val radius = scanRadius.value.toInt()
  val vertical = scanVertical.value.toInt()
  if (scanOffsets.isEmpty() || scanRadiusCached != radius || scanVerticalCached != vertical) {
    scanOffsets = buildOffsets(radius, vertical)
    scanRadiusCached = radius
    scanVerticalCached = vertical
    scanIndex = 0
  }

  // Merge all selected types into one combined selection so the vein flood-fill
  // crosses type boundaries. This ensures all selected ore types are mined in one pass.
  val merged = mergeSelections(selections)
  val perTick = scanPerTick.value.toInt().coerceAtLeast(1)
  var processed = 0
  while (scanIndex < scanOffsets.size && processed < perTick) {
    val off = scanOffsets[scanIndex++]
    processed++
    val pos = scanOrigin.offset(off.dx, off.dy, off.dz)
    if (skippedSeeds.contains(pos.asLong())) continue
    if (!isMineableTarget(level, player, pos, merged.ids)) continue

    val vein = buildVein(level, player, pos, merged, maxVeinBlocks.value.toInt())
    if (SKIP_OCCUPIED_VEINS && isVeinOccupied(level, vein, player)) {
      skippedSeeds.add(pos.asLong())
      continue
    }
    currentVein = vein
    veinStartAnchor = player.blockPosition().immutable()
    currentTargetNoLosTicks = 0
    currentDirectionalFlow = null
    lanternPlacedForVein = false
    lastLanternRefreshTick = -1L
    scanActive = false
    scanPriorityIndex = 0
    rememberVeinAnchor(pos)
    ChatUtils.sendMessage("Mining macro: found ${vein.typeLabel} vein with ${vein.blocks.size} blocks.")
    return
  }

  if (scanIndex >= scanOffsets.size) {
    resetScanState()
    warnOnce("No veins found in scan radius.")
  }
}

internal fun MiningMacroModule.resetScanState() {
  scanActive = false
  scanIndex = 0
  scanPriorityIndex = 0
}

internal fun MiningMacroModule.mergeSelections(selections: List<ScanTypeSelection>): ScanTypeSelection {
  if (selections.size == 1) return selections[0]
  val ids = selections.flatMapTo(linkedSetOf()) { it.ids }
  val label = selections.joinToString(", ") { it.label }
  return ScanTypeSelection(label, ids)
}

internal fun MiningMacroModule.buildOffsets(radius: Int, vertical: Int): List<ScanOffset> {
  val list = ArrayList<ScanOffset>()
  val rSq = radius * radius
  for (dy in -vertical..vertical) {
    for (dx in -radius..radius) {
      for (dz in -radius..radius) {
        val horizSq = dx * dx + dz * dz
        if (horizSq > rSq) continue
        val distSq = horizSq + dy * dy
        list.add(ScanOffset(dx, dy, dz, distSq))
      }
    }
  }
  // Prefer same-height / visible-nearby seeds before lower blocks. The old dy sort
  // favored negative Y on ties, which made the macro often start on the block under
  // the visible vein instead of the visible face you are looking at.
  list.sortWith(compareBy<ScanOffset>({ it.distSq }, { abs(it.dy) }, { -it.dy }))
  return list
}

internal fun MiningMacroModule.veinNeighborOffsets26(): List<ScanOffset> {
  val list = ArrayList<ScanOffset>(26)
  for (dy in -1..1) {
    for (dx in -1..1) {
      for (dz in -1..1) {
        if (dx == 0 && dy == 0 && dz == 0) continue
        val distSq = dx * dx + dy * dy + dz * dz
        list.add(ScanOffset(dx, dy, dz, distSq))
      }
    }
  }
  // Straight neighbors first, then diagonal/corner neighbors. This still collects
  // diagonal vein pieces, but it keeps the main connected body stable.
  list.sortWith(compareBy<ScanOffset>({ it.distSq }, { abs(it.dy) }, { -it.dy }))
  return list
}

internal fun MiningMacroModule.buildVein(
  level: net.minecraft.world.level.Level,
  player: Player,
  seed: BlockPos,
  selection: ScanTypeSelection,
  maxBlocks: Int
): ScanVein {
  val seedBlockId = blockIdAt(level, seed)
  val blocks = LinkedHashSet<BlockPos>()
  val queue = ArrayDeque<BlockPos>()
  queue.add(seed)
  blocks.add(seed)

  var minX = seed.x
  var minY = seed.y
  var minZ = seed.z
  var maxX = seed.x
  var maxY = seed.y
  var maxZ = seed.z

  val neighborOffsets = veinNeighborOffsets26()
  while (queue.isNotEmpty() && blocks.size < maxBlocks) {
    val pos = queue.removeFirst()
    for (off in neighborOffsets) {
      val next = pos.offset(off.dx, off.dy, off.dz)
      if (blocks.contains(next)) continue
      if (!isMineableTarget(level, player, next, selection.ids)) continue
      blocks.add(next)
      queue.add(next)
      if (next.x < minX) minX = next.x
      if (next.y < minY) minY = next.y
      if (next.z < minZ) minZ = next.z
      if (next.x > maxX) maxX = next.x
      if (next.y > maxY) maxY = next.y
      if (next.z > maxZ) maxZ = next.z
      if (blocks.size >= maxBlocks) break
    }
  }

  val bounds = AABB(
    minX.toDouble(),
    minY.toDouble(),
    minZ.toDouble(),
    maxX + 1.0,
    maxY + 1.0,
    maxZ + 1.0
  )
  return ScanVein(blocks, selection.label, selection.ids, seedBlockId, bounds)
}

internal fun MiningMacroModule.pruneVein(
  level: net.minecraft.world.level.Level,
  player: Player,
  vein: ScanVein
) {
  val iterator = vein.blocks.iterator()
  while (iterator.hasNext()) {
    val pos = iterator.next()
    if (!isMineableTarget(level, player, pos, vein.targetIds)) {
      iterator.remove()
    }
  }
}

internal fun MiningMacroModule.sanitizeActiveTargets(
  level: net.minecraft.world.level.Level,
  player: Player,
  vein: ScanVein
) {
  val activeMining = miningOnTarget
  if (activeMining != null && !isMineableTarget(level, player, activeMining, vein.targetIds)) {
    vein.blocks.remove(activeMining)
    precisionRolls.remove(activeMining.asLong())
    stopMiningKeys()
  }

  val activeTarget = currentTarget
  if (activeTarget != null && !vein.blocks.contains(activeTarget)) {
    precisionRolls.remove(activeTarget.asLong())
    currentTarget = null
    currentTargetNoLosTicks = 0
    if (approachTarget == activeTarget) {
      stopApproachMovement()
    }
  }
}
