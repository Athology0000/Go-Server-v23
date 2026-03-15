package org.cobalt.internal.mining

import kotlin.math.abs
import kotlin.math.sqrt
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.internal.pathfinding.HeadRotationModule
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.DuskPathfinder
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.rotation.RotationsModule

object MiningMacroModule : Module("Mining Macro") {

  private val mc: Minecraft = Minecraft.getInstance()

  // Frame-based rotation — target set in onTick, applied every render frame via onFrame.
  private var frameRotTarget:     Vec3?  = null
  private var frameRotSpeedScale: Float  = 1f
  private var frameRotAccelScale: Float  = 1f
  private var frameRotPitchStep:  Float  = 3.5f
  private var frameRotMaxSpeed:   Float  = 100f
  private var frameRotMaxAccel:   Float  = 220f

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically scan, move to, and mine veins.",
    false
  )

  private val info = InfoSetting(
    "Macro",
    "Scans for the closest vein and mines it until empty.",
    InfoType.INFO
  )

  private val useMiningModuleType = CheckboxSetting(
    "Use Mining Type",
    "Use the Mining module block type selection.",
    true
  )

  private val blockType = ModeSetting(
    "Block Type",
    "Type used when not synced from Mining module.",
    0,
    MiningBlockRegistry.BLOCK_TYPES
  )

  private val useDetectedBlock = CheckboxSetting(
    "Use Detected Block",
    "When type is Custom, use Mining module detected block id.",
    true
  )

  private val scanRadius = SliderSetting(
    "Scan Radius",
    "Horizontal scan radius for veins.",
    32.0,
    8.0,
    96.0
  )

  private val scanVertical = SliderSetting(
    "Scan Vertical",
    "Vertical scan range above/below you.",
    8.0,
    2.0,
    40.0
  )

  private val scanPerTick = SliderSetting(
    "Scan Per Tick",
    "Blocks scanned per tick when searching.",
    400.0,
    50.0,
    2000.0
  )

  private val maxVeinBlocks = SliderSetting(
    "Max Vein Blocks",
    "Maximum blocks collected per vein.",
    256.0,
    32.0,
    512.0
  )

  private val mineRange = SliderSetting(
    "Mine Range",
    "Distance to mine blocks.",
    4.5,
    2.0,
    6.0
  )

  private val requireMineLos = CheckboxSetting(
    "Require LOS",
    "Only mine blocks in line of sight.",
    true
  )

  private val useInstantTransmission = CheckboxSetting(
    "Instant Transmission",
    "Use Etherwarp (Shift + Right Click) when possible.",
    true
  )

  private val warpMinDistance = SliderSetting(
    "Warp Min Dist",
    "Only warp if target is farther than this.",
    10.0,
    0.0,
    30.0
  )

  private val warpAimTolerance = SliderSetting(
    "Warp Aim Tol",
    "Yaw/pitch error before warping.",
    6.0,
    2.0,
    15.0
  )

  private val warpCooldownTicks = SliderSetting(
    "Warp Cooldown",
    "Ticks to wait between warp attempts.",
    24.0,
    6.0,
    80.0
  )

  private val usePathfinding = CheckboxSetting(
    "Use Pathfinding",
    "Walk to the target when warping is not possible.",
    true
  )

  val useVeinDirection = CheckboxSetting(
    "Vein Direction",
    "Use Vein Direction Setter coordinate pairs to control mining flow.",
    false
  )

  private val skipOccupiedVeins = CheckboxSetting(
    "Skip Occupied Veins",
    "Ignore veins with other players nearby.",
    true
  )

  private val occupiedRadius = SliderSetting(
    "Occupied Radius",
    "Radius around a vein considered occupied.",
    6.0,
    1.0,
    16.0
  )

  init {
    addSetting(
      enabled,
      info,
      useMiningModuleType,
      blockType,
      useDetectedBlock,
      scanRadius,
      scanVertical,
      scanPerTick,
      maxVeinBlocks,
      mineRange,
      requireMineLos,
      useInstantTransmission,
      warpMinDistance,
      warpAimTolerance,
      warpCooldownTicks,
      usePathfinding,
      useVeinDirection,
      skipOccupiedVeins,
      occupiedRadius,
    )

    EventBus.register(this)
  }

  private data class Offset(val dx: Int, val dy: Int, val dz: Int, val distSq: Int)

  private data class Vein(
    val blocks: MutableSet<BlockPos>,
    val blockId: String,
    val bounds: AABB
  )

  private var scanOffsets: List<Offset> = emptyList()
  private var scanIndex = 0
  private var scanOrigin: BlockPos = BlockPos.ZERO
  private var scanActive = false
  private var scanRadiusCached = 0
  private var scanVerticalCached = 0

  private var currentVein: Vein? = null
  private var currentTarget: BlockPos? = null
  private var currentTargetNoLosTicks = 0
  private var currentDirectionalFlow: VeinDirectionModule.VeinFlow? = null
  private var lastPathTarget: BlockPos? = null
  private var lastWarnTick = 0L

  private var warpStage = 0
  private var warpTarget: BlockPos? = null
  private var warpStageTicks = 0
  private var warpCooldownUntil = 0L
  private var warpRestoreSlot = -1

  private var miningActive = false
  private var startedPath = false
  private var lastPathStartTick = 0L
  private var wasEnabled = false
  private var lanternPlacedForVein = false
  private var lastLanternPlaceTick = 0L
  private var pendingLanternRelease = false
  private var pendingLanternRestoreSlot = -1

  val isActive: Boolean get() = enabled.value

  fun startForAutomation(blockTypeName: String) {
    val idx = MiningBlockRegistry.BLOCK_TYPES.indexOf(blockTypeName)
    if (idx >= 0) MiningModule.blockType.value = idx
    enabled.value = true
  }

  fun stopForAutomation() {
    enabled.value = false
  }

  private val skippedSeeds = HashSet<Long>()
  private const val FLOW_MATCH_MAX_DIST_SQ = 24.0 * 24.0
  private const val DIRECTIONAL_STEP_MAX_DIST_SQ = 64.0
  private const val DIRECTIONAL_MIN_PROJECTION = 0.05
  private const val DIRECTIONAL_MAX_LATERAL_SQ = 25.0
  private const val TARGET_LOS_GRACE_TICKS = 8
  private const val TARGET_STICKY_RANGE_EXTRA = 1.4
  private const val APPROACH_SCAN_RADIUS = 3
  private const val APPROACH_SCAN_VERTICAL = 2
  private const val WARP_ALIGN_TICKS = 20
  private const val WARP_SNEAK_TICKS = 2
  private const val WARP_POST_TICKS = 6
  private const val LANTERN_PLACE_COOLDOWN_TICKS = 12L
  private const val WILL_O_WISP_NAME = "will o wisp"

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (pendingLanternRelease) {
      mc.options.keyUse?.setDown(false)
      pendingLanternRelease = false
    }
    if (pendingLanternRestoreSlot >= 0) {
      mc.options.keyHotbarSlots[pendingLanternRestoreSlot].setDown(true)
      mc.options.keyHotbarSlots[pendingLanternRestoreSlot].setDown(false)
      pendingLanternRestoreSlot = -1
    }

    if (!enabled.value) {
      if (wasEnabled) {
        stopMacro("Disabled.")
        wasEnabled = false
      }
      return
    }
    wasEnabled = true

    val player = mc.player ?: return
    val level = mc.level ?: return

    if (usePathfinding.value) {
      PathfindingModule.ensureEnabledForAutomation("mining macro")
    }

    if (startedPath && !DuskPathfinder.isActive()) {
      startedPath = false
      lastPathTarget = null
    }

    if (mc.screen != null) {
      stopMiningKeys()
      RotationExecutor.stopRotating()
      return
    }

    if (warpStage > 0) {
      handleWarp(player, level)
      return
    }

    val targetIds = resolveTargetIds()
    if (targetIds.isEmpty()) {
      warnOnce("No target block ids for selected type.")
      stopMiningKeys()
      return
    }

    val vein = currentVein
    if (vein == null || vein.blocks.isEmpty()) {
      currentVein = null
      currentTarget = null
      currentTargetNoLosTicks = 0
      currentDirectionalFlow = null
      lanternPlacedForVein = false
      startOrContinueScan(level, player, targetIds)
      return
    }

    pruneVein(level, vein, targetIds)
    if (vein.blocks.isEmpty()) {
      currentVein = null
      currentTarget = null
      currentTargetNoLosTicks = 0
      currentDirectionalFlow = null
      lanternPlacedForVein = false
      return
    }

    val target = selectMineTarget(level, player, vein)
    if (target == null) {
      currentTarget = null
      currentTargetNoLosTicks = 0
      val nearest = selectNearestBlock(player, vein.blocks)
      if (nearest != null) {
        if (useInstantTransmission.value && tryStartWarp(player, level, nearest)) {
          return
        }
        if (usePathfinding.value) {
          moveToward(level, player, nearest)
        }
      }
      stopMiningKeys()
      RotationExecutor.stopRotating()
      return
    }

    currentTarget = target
    val distSq = distanceToBlockSq(player, target)
    val inRange = distSq <= mineRange.value * mineRange.value

    if (inRange && (!requireMineLos.value || hasLineOfSight(level, player, target))) {
      if (!lanternPlacedForVein) {
        lanternPlacedForVein = tryPlaceLantern(level, player)
      }
      if (startedPath && DuskPathfinder.isActive()) {
        DuskPathfinder.stop(mc, "Mining.")
      }
      startedPath = false
      lastPathTarget = null
      startMining(player, target)
    } else {
      stopMiningKeys()
      RotationExecutor.stopRotating()
      if (useInstantTransmission.value && tryStartWarp(player, level, target)) {
        return
      }
      if (usePathfinding.value) {
        moveToward(level, player, target)
      }
    }
  }

  private fun resolveTargetIds(): Set<String> {
    val label = resolveSelectedType()
    val ids = MiningBlockRegistry.idsForType(label)
    if (ids.isNotEmpty()) {
      return ids
    }
    if (useDetectedBlock.value && label.equals("Custom", ignoreCase = true)) {
      val detected = MiningModule.detectedBlockText.value.trim()
      if (detected.contains(":")) {
        return setOf(detected)
      }
    }
    return emptySet()
  }

  private fun resolveSelectedType(): String {
    return if (useMiningModuleType.value) {
      MiningModule.blockType.options.getOrNull(MiningModule.blockType.value) ?: "Custom"
    } else {
      blockType.options.getOrNull(blockType.value) ?: "Custom"
    }
  }

  private fun startOrContinueScan(
    level: net.minecraft.world.level.Level,
    player: Player,
    targetIds: Set<String>
  ) {
    if (!scanActive || player.blockPosition().distSqr(scanOrigin) > 4.0) {
      scanOrigin = player.blockPosition()
      scanIndex = 0
      scanActive = true
    }

    val radius = scanRadius.value.toInt()
    val vertical = scanVertical.value.toInt()
    if (scanOffsets.isEmpty() || scanRadiusCached != radius || scanVerticalCached != vertical) {
      scanOffsets = buildOffsets(radius, vertical)
      scanRadiusCached = radius
      scanVerticalCached = vertical
      scanIndex = 0
    }

    val perTick = scanPerTick.value.toInt().coerceAtLeast(1)
    var processed = 0
    while (scanIndex < scanOffsets.size && processed < perTick) {
      val off = scanOffsets[scanIndex++]
      processed++
      val pos = scanOrigin.offset(off.dx, off.dy, off.dz)
      val id = blockIdAt(level, pos)
      if (!targetIds.contains(id)) continue
      if (skippedSeeds.contains(pos.asLong())) continue

      val vein = buildVein(level, pos, id, maxVeinBlocks.value.toInt())
      if (skipOccupiedVeins.value && isVeinOccupied(level, vein, player)) {
        skippedSeeds.add(pos.asLong())
        continue
      }
      currentVein = vein
      currentTargetNoLosTicks = 0
      currentDirectionalFlow = null
      lanternPlacedForVein = false
      scanActive = false
      ChatUtils.sendMessage("Mining macro: found vein with ${vein.blocks.size} blocks.")
      return
    }

    if (scanIndex >= scanOffsets.size) {
      scanActive = false
      warnOnce("No veins found in scan radius.")
    }
  }

  private fun buildOffsets(radius: Int, vertical: Int): List<Offset> {
    val list = ArrayList<Offset>()
    val rSq = radius * radius
    for (dy in -vertical..vertical) {
      for (dx in -radius..radius) {
        for (dz in -radius..radius) {
          val horizSq = dx * dx + dz * dz
          if (horizSq > rSq) continue
          val distSq = horizSq + dy * dy
          list.add(Offset(dx, dy, dz, distSq))
        }
      }
    }
    list.sortWith(compareBy({ it.distSq }, { it.dy }))
    return list
  }

  private fun buildVein(
    level: net.minecraft.world.level.Level,
    seed: BlockPos,
    blockId: String,
    maxBlocks: Int
  ): Vein {
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

    while (queue.isNotEmpty() && blocks.size < maxBlocks) {
      val pos = queue.removeFirst()
      for (dir in Direction.values()) {
        val next = pos.relative(dir)
        if (blocks.contains(next)) continue
        if (blockIdAt(level, next) != blockId) continue
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
    return Vein(blocks, blockId, bounds)
  }

  private fun pruneVein(
    level: net.minecraft.world.level.Level,
    vein: Vein,
    targetIds: Set<String>
  ) {
    val iterator = vein.blocks.iterator()
    while (iterator.hasNext()) {
      val pos = iterator.next()
      if (!targetIds.contains(blockIdAt(level, pos))) {
        iterator.remove()
      }
    }
  }

  private fun selectMineTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    vein: Vein
  ): BlockPos? {
    val sticky = currentTarget?.takeIf { vein.blocks.contains(it) }
    if (sticky != null) {
      val distSq = distanceToBlockSq(player, sticky)
      val inAttackRange = distSq <= mineRange.value * mineRange.value
      if (!inAttackRange) {
        currentTargetNoLosTicks = 0
        return sticky
      }

      val hasLos = !requireMineLos.value || hasLineOfSight(level, player, sticky)
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
    currentTargetNoLosTicks = 0

    if (useVeinDirection.value) {
      val directional = selectDirectionalMineTarget(level, player, vein)
      if (directional != null) {
        return directional
      }
    }

    var best: BlockPos? = null
    var bestDist = Double.POSITIVE_INFINITY
    for (pos in vein.blocks) {
      if (requireMineLos.value && !hasLineOfSight(level, player, pos)) continue
      val distSq = distanceToBlockSq(player, pos)
      if (distSq < bestDist) {
        bestDist = distSq
        best = pos
      }
    }
    return best ?: selectNearestBlock(player, vein.blocks)
  }

  private fun selectDirectionalMineTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    vein: Vein
  ): BlockPos? {
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
      ?: selectClosestBlockToReference(level, player, vein.blocks, flow.start)
      ?: selectNearestBlock(player, vein.blocks)
      ?: return null

    if (activeAnchor == null) {
      return anchor
    }

    val forward = selectForwardDirectionalTarget(level, player, vein.blocks, anchor, nx, ny, nz)
    if (forward != null) {
      return forward
    }

    if (!requireMineLos.value || hasLineOfSight(level, player, anchor)) {
      return anchor
    }
    return selectNearestBlock(player, vein.blocks)
  }

  private fun resolveDirectionalFlow(
    vein: Vein,
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

  private fun flowDistanceSqToVein(flow: VeinDirectionModule.VeinFlow, vein: Vein): Double {
    val startDistSq = pointDistanceSqToBounds(flow.start, vein.bounds)
    val endDistSq = pointDistanceSqToBounds(flow.end, vein.bounds)
    return minOf(startDistSq, endDistSq)
  }

  private fun pointDistanceSqToBounds(pos: BlockPos, bounds: AABB): Double {
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

  private fun selectClosestBlockToReference(
    level: net.minecraft.world.level.Level,
    player: Player,
    blocks: Set<BlockPos>,
    reference: BlockPos
  ): BlockPos? {
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    for (pos in blocks) {
      if (requireMineLos.value && !hasLineOfSight(level, player, pos)) continue
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

  private fun selectForwardDirectionalTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    blocks: Set<BlockPos>,
    anchor: BlockPos,
    nx: Double,
    ny: Double,
    nz: Double
  ): BlockPos? {
    var best: BlockPos? = null
    var bestScore = Double.NEGATIVE_INFINITY
    for (pos in blocks) {
      if (pos == anchor) continue
      if (requireMineLos.value && !hasLineOfSight(level, player, pos)) continue

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

  private fun selectNearestBlock(player: Player, blocks: Set<BlockPos>): BlockPos? {
    var best: BlockPos? = null
    var bestDist = Double.POSITIVE_INFINITY
    for (pos in blocks) {
      val distSq = distanceToBlockSq(player, pos)
      if (distSq < bestDist) {
        bestDist = distSq
        best = pos
      }
    }
    return best
  }

  private fun startMining(player: Player, target: BlockPos) {
    frameRotTarget     = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    frameRotSpeedScale = RotationsModule.sample(RotationsModule.miningSpeedScale.value).toFloat()
    frameRotAccelScale = RotationsModule.sample(RotationsModule.miningAccelScale.value).toFloat()
    frameRotPitchStep  = RotationsModule.sample(RotationsModule.miningPitchStep.value).toFloat()
    frameRotMaxSpeed   = RotationsModule.sample(RotationsModule.miningMaxSpeed.value).toFloat()
    frameRotMaxAccel   = RotationsModule.sample(RotationsModule.miningMaxAccel.value).toFloat()
    mc.options.keyAttack?.setDown(true)
    miningActive = true
  }

  private fun stopMiningKeys() {
    if (miningActive) {
      mc.options.keyAttack?.setDown(false)
      miningActive = false
    }
    frameRotTarget = null
  }

  private fun tryPlaceLantern(
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

    lastLanternPlaceTick = level.gameTime
    val previousSlot = player.inventory.selectedSlot

    if (previousSlot != lanternSlot) {
      mc.options.keyHotbarSlots[lanternSlot].setDown(true)
      mc.options.keyHotbarSlots[lanternSlot].setDown(false)
    }

    mc.options.keyUse?.setDown(true)
    pendingLanternRelease = true
    pendingLanternRestoreSlot = if (previousSlot != lanternSlot) previousSlot else -1
    return true
  }

  private fun findLanternHotbarSlot(player: Player): Int {
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

  private fun normalizeHotbarItemName(rawName: String): String {
    return rawName
      .lowercase()
      .replace(Regex("[^a-z0-9]+"), " ")
      .trim()
  }

  private fun moveToward(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ) {
    PathfindingModule.ensureEnabledForAutomation("mining macro")
    val approach = findApproach(level, player, target) ?: return
    if (!DuskPathfinder.isActive() || lastPathTarget == null || lastPathTarget?.distSqr(approach) ?: 0.0 > 1.0) {
      if (level.gameTime - lastPathStartTick < 8L) {
        return
      }
      lastPathStartTick = level.gameTime
      val started = DuskPathfinder.start(mc, approach)
      if (started) {
        startedPath = true
        lastPathTarget = approach
      } else if (!DuskPathfinder.isActive()) {
        startedPath = false
        lastPathTarget = null
      }
    }
  }

  private fun findApproach(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
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
      val score = approachScore(level, player, candidate, target)
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
            val score = approachScore(level, player, candidate, target)
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
    return MinecraftPathingRules.resolveTarget(level, target)
  }

  private fun approachScore(
    level: net.minecraft.world.level.Level,
    player: Player,
    candidate: BlockPos,
    target: BlockPos
  ): Double {
    if (!MinecraftPathingRules.isWalkable(level, candidate)) {
      return Double.POSITIVE_INFINITY
    }

    val centerDx = (candidate.x + 0.5) - (target.x + 0.5)
    val centerDy = (candidate.y + 0.5) - (target.y + 0.5)
    val centerDz = (candidate.z + 0.5) - (target.z + 0.5)
    val targetDistSq = centerDx * centerDx + centerDy * centerDy + centerDz * centerDz
    val maxMineDist = mineRange.value + 1.0
    if (targetDistSq > maxMineDist * maxMineDist) {
      return Double.POSITIVE_INFINITY
    }

    if (requireMineLos.value && !hasLineOfSightFrom(level, player, candidate, target)) {
      return Double.POSITIVE_INFINITY
    }

    val playerDistSq = player.blockPosition().distSqr(candidate).toDouble()
    val verticalPenalty = abs(candidate.y - target.y) * 0.75
    return playerDistSq + targetDistSq * 0.35 + verticalPenalty
  }

  private fun hasLineOfSightFrom(
    level: net.minecraft.world.level.Level,
    player: Player,
    from: BlockPos,
    target: BlockPos
  ): Boolean {
    val eyeY = from.y + if (player.isShiftKeyDown) 1.54 else 1.62
    val eye = Vec3(from.x + 0.5, eyeY, from.z + 0.5)
    return losCheck(level, player, eye, target)
  }

  /**
   * Checks line of sight from [eye] to [target] by sampling the block face
   * closest to the eye. This handles wall-embedded ores where a ray to the
   * block center is occluded by the stone above the ore.
   */
  private fun losCheck(
    level: net.minecraft.world.level.Level,
    entity: net.minecraft.world.entity.Entity,
    eye: Vec3,
    target: BlockPos
  ): Boolean {
    val cx = target.x + 0.5
    val cy = target.y + 0.5
    val cz = target.z + 0.5
    // Offset toward the eye on each axis, clamped to stay within the block face
    val ox = ((eye.x - cx) * 0.49).coerceIn(-0.49, 0.49)
    val oy = ((eye.y - cy) * 0.49).coerceIn(-0.49, 0.49)
    val oz = ((eye.z - cz) * 0.49).coerceIn(-0.49, 0.49)
    val center = Vec3(cx, cy, cz)
    val points = listOf(
      center,
      Vec3(cx + ox, cy, cz),
      Vec3(cx, cy + oy, cz),
      Vec3(cx, cy, cz + oz),
      Vec3(cx + ox, cy + oy, cz + oz),
    )
    for (point in points) {
      val hit = level.clip(
        net.minecraft.world.level.ClipContext(
          eye, point,
          net.minecraft.world.level.ClipContext.Block.OUTLINE,
          net.minecraft.world.level.ClipContext.Fluid.NONE,
          entity
        )
      )
      if (hit.type == net.minecraft.world.phys.HitResult.Type.BLOCK && hit.blockPos == target) {
        return true
      }
    }
    return false
  }

  private fun tryStartWarp(
    player: Player,
    level: net.minecraft.world.level.Level,
    target: BlockPos
  ): Boolean {
    if (!useInstantTransmission.value) return false
    if (level.gameTime < warpCooldownUntil) return false
    if (!EtherwarpLogic.holdingEtherwarpItem()) return false
    if (!EtherwarpLogic.canEtherwarp()) return false

    val eye = player.eyePosition
    val targetCenter = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    val distSq = eye.distanceToSqr(targetCenter)
    val minDistSq = warpMinDistance.value * warpMinDistance.value
    if (distSq < minDistSq) return false

    val range = EtherwarpLogic.getEtherwarpRange().toDouble()
    if (distSq > range * range) return false

    if (!hasLineOfSight(level, player, target)) return false
    if (!ensureEtherwarpHotbarSelected()) return false

    if (startedPath && DuskPathfinder.isActive()) {
      DuskPathfinder.stop(mc, "Warping.")
    }
    startedPath = false
    lastPathTarget = null
    stopMiningKeys()
    RotationExecutor.stopRotating()
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)

    warpTarget = target
    warpStage = 0
    warpStageTicks = 0
    return true
  }

  private fun handleWarp(
    player: Player,
    level: net.minecraft.world.level.Level
  ) {
    val target = warpTarget ?: run {
      resetWarp()
      return
    }

    val targetCenter = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    frameRotTarget     = targetCenter
    frameRotSpeedScale = RotationsModule.sample(RotationsModule.warpSpeedScale.value).toFloat()
    frameRotAccelScale = RotationsModule.sample(RotationsModule.warpAccelScale.value).toFloat()
    frameRotPitchStep  = RotationsModule.sample(RotationsModule.miningPitchStep.value).toFloat()
    frameRotMaxSpeed   = RotationsModule.sample(RotationsModule.miningMaxSpeed.value).toFloat()
    frameRotMaxAccel   = RotationsModule.sample(RotationsModule.miningMaxAccel.value).toFloat()
    val targetRotation = AngleUtils.getRotation(targetCenter)
    val yawError   = abs(AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw)).toDouble()
    val pitchError = abs(targetRotation.pitch - player.xRot).toDouble()

    when (warpStage) {
      0 -> {
        val tol = warpAimTolerance.value
        if ((yawError <= tol && pitchError <= tol) || warpStageTicks >= WARP_ALIGN_TICKS) {
          mc.options.keyShift?.setDown(true)
          warpStage = 1
          warpStageTicks = 0
          return
        }
        warpStageTicks++
      }
      1 -> {
        mc.options.keyShift?.setDown(true)
        if (warpStageTicks >= WARP_SNEAK_TICKS) {
          mc.options.keyUse?.setDown(true)
          warpStage = 2
          warpStageTicks = 0
          return
        }
        warpStageTicks++
      }
      else -> {
        mc.options.keyShift?.setDown(true)
        if (warpStageTicks >= 1) {
          mc.options.keyUse?.setDown(false)
        }
        if (warpStageTicks >= WARP_POST_TICKS) {
          mc.options.keyUse?.setDown(false)
          mc.options.keyShift?.setDown(false)
          warpCooldownUntil = level.gameTime + warpCooldownTicks.value.toLong()
          restoreEtherwarpSlot()
          resetWarp()
          return
        }
        warpStageTicks++
      }
    }
  }

  @SubscribeEvent
  fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    val target = frameRotTarget ?: return
    val player = mc.player ?: return
    applyHeadRotation(
      player, target,
      maxSpeedScale = frameRotSpeedScale,
      accelScale    = frameRotAccelScale,
      maxPitchStep  = frameRotPitchStep,
      maxTurnSpeed  = frameRotMaxSpeed,
      maxTurnAccel  = frameRotMaxAccel,
    )
  }

  private fun applyHeadRotation(
    player: Player,
    target: Vec3,
    maxSpeedScale: Float = 1f,
    accelScale: Float    = 1f,
    maxPitchStep: Float  = 6f,
    maxTurnSpeed: Float  = 100f,
    maxTurnAccel: Float  = 220f,
  ): Pair<Double, Double> {
    val targetRotation = AngleUtils.getRotation(target)
    val yawDelta = AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw)
    val yawStep  = HeadRotationModule.computeTurnDelta(
      yawDelta,
      maxSpeedScale = maxSpeedScale,
      accelScale    = accelScale,
      maxTurnSpeed  = maxTurnSpeed,
      maxTurnAccel  = maxTurnAccel,
    )
    player.yRot     = AngleUtils.normalizeAngle(player.yRot + yawStep)
    player.yHeadRot = player.yRot
    player.yBodyRot = player.yRot

    val pitchDelta = targetRotation.pitch - player.xRot
    val pitchStep  = HeadRotationModule.computePitchDelta(
      pitchDelta,
      maxSpeedScale = maxSpeedScale,
      accelScale    = accelScale,
      maxPitchSpeed = maxPitchStep * 20f,
      maxPitchAccel = maxPitchStep * 60f,
    )
    player.xRot = (player.xRot + pitchStep).coerceIn(-89.9f, 89.9f)

    val yawError   = abs(AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw)).toDouble()
    val pitchError = abs(targetRotation.pitch - player.xRot).toDouble()
    return yawError to pitchError
  }

  private fun resetWarp() {
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)
    warpStage = 0
    warpTarget = null
    warpStageTicks = 0
    frameRotTarget = null
  }

  private fun ensureEtherwarpHotbarSelected(): Boolean {
    val player = mc.player ?: return false
    val currentSlot = player.inventory.selectedSlot
    val currentStack = player.inventory.getItem(currentSlot)
    if (EtherwarpLogic.isEtherwarpStack(currentStack)) {
      return true
    }
    if (EtherwarpLogic.isEtherwarpStack(player.offhandItem)) {
      return true
    }
    val slot = EtherwarpLogic.findEtherwarpHotbarSlot()
    if (slot in 0..8) {
      if (warpRestoreSlot == -1) {
        warpRestoreSlot = currentSlot
      }
      InventoryUtils.holdHotbarSlot(slot)
      return true
    }
    return false
  }

  private fun restoreEtherwarpSlot() {
    if (warpRestoreSlot in 0..8) {
      InventoryUtils.holdHotbarSlot(warpRestoreSlot)
    }
    warpRestoreSlot = -1
  }

  private fun hasLineOfSight(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Boolean {
    val eye = player.eyePosition
    return losCheck(level, player, eye, target)
  }

  private fun blockIdAt(level: net.minecraft.world.level.Level, pos: BlockPos): String {
    val state = level.getBlockState(pos)
    return BuiltInRegistries.BLOCK.getKey(state.block).toString()
  }

  private fun distanceToBlockSq(player: Player, pos: BlockPos): Double {
    val dx = (pos.x + 0.5) - player.x
    val dy = (pos.y + 0.5) - player.y
    val dz = (pos.z + 0.5) - player.z
    return dx * dx + dy * dy + dz * dz
  }

  private fun isVeinOccupied(
    level: net.minecraft.world.level.Level,
    vein: Vein,
    player: Player
  ): Boolean {
    val radius = occupiedRadius.value
    val bounds = vein.bounds
    val aabb = AABB(
      bounds.minX - radius,
      bounds.minY - radius,
      bounds.minZ - radius,
      bounds.maxX + radius,
      bounds.maxY + radius,
      bounds.maxZ + radius
    )
    for (other in level.players()) {
      if (other == player) continue
      if (other.isSpectator) continue
      if (aabb.intersects(other.boundingBox)) {
        return true
      }
    }
    return false
  }

  private fun warnOnce(message: String) {
    val level = mc.level ?: return
    if (level.gameTime - lastWarnTick < 60L) return
    lastWarnTick = level.gameTime
    ChatUtils.sendMessage(message)
  }

  private fun stopMacro(reason: String) {
    if (startedPath && DuskPathfinder.isActive()) {
      DuskPathfinder.stop(mc, reason)
    }
    startedPath = false
    lastPathStartTick = 0L
    stopMiningKeys()
    RotationExecutor.stopRotating()
    restoreEtherwarpSlot()
    resetWarp()
    currentVein = null
    currentTarget = null
    currentTargetNoLosTicks = 0
    currentDirectionalFlow = null
    lastPathTarget = null
    lanternPlacedForVein = false
    scanActive = false
  }
}
