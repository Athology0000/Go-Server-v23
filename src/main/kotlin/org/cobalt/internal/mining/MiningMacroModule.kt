package org.cobalt.internal.mining

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.random.Random
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.BlockChangeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.internal.pathfinding.HeadRotationModule
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.OverlayRenderEngine
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.rotation.BlockRotationRequest
import org.cobalt.internal.rotation.CobaltRotation
import org.cobalt.internal.rotation.RotationEasingType
import org.cobalt.internal.rotation.RotationsModule
import org.cobalt.internal.routes.RoutePickerSetting
import org.cobalt.internal.routes.RouteType
import org.cobalt.internal.ui.panel.panels.UIModuleList

object MiningMacroModule : Module("Mining Macro") {

  override val category = ModuleCategory.MINING

  internal val mc: Minecraft = Minecraft.getInstance()
  private const val OVERLAY_TAG = "mining-macro-targets"

  // Frame-based rotation - target set in onTick, applied every render frame via onFrame.
  internal var frameRotTarget:     Vec3?  = null
  internal var aimRenderPoint:     Vec3?  = null
  internal var aimRenderBlock:     BlockPos? = null
  internal var frameRotSpeedScale: Float  = 1f
  internal var frameRotAccelScale: Float  = 1f
  internal var frameRotPitchStep:  Float  = 3.5f
  internal var frameRotMaxSpeed:   Float  = 100f
  internal var frameRotMaxAccel:   Float  = 220f
  internal var frameRotSnapThreshold: Float = 0f
  // Constant-speed rotation: initial angular distance captured when block changes.
  internal var frameRotInitialDist: Float = 0f
  internal var frameRotPrevBlock:   BlockPos? = null
  internal var frameRotLastNs:      Long = 0L

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically scan, move to, and mine veins.",
    false
  )

  private val toggleKeybind = KeyBindSetting(
    "Toggle Keybind",
    "Key to start/stop the mining macro.",
    KeyBind(-1)
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

  internal val blockTypes = TextSetting(
    "Block Types",
    "Active types (edit via + button).",
    "None"
  )

  private val blockTypesPicker = ActionSetting(
    "Pick Types",
    "Open multi-select block type picker.",
    "+"
  ) { BlockTypePickerPopup.open() }

  internal val useDetectedBlock = CheckboxSetting(
    "Use Detected Block",
    "When type is Custom, use Mining module detected block id.",
    true
  )

  internal val scanRadius = SliderSetting(
    "Scan Radius",
    "Horizontal scan radius for veins.",
    32.0,
    8.0,
    96.0
  )

  internal val scanVertical = SliderSetting(
    "Scan Vertical",
    "Vertical scan range above/below you.",
    8.0,
    2.0,
    40.0
  )

  internal val scanPerTick = SliderSetting(
    "Scan Per Tick",
    "Blocks scanned per tick when searching.",
    400.0,
    50.0,
    2000.0
  )

  internal val maxVeinBlocks = SliderSetting(
    "Max Vein Blocks",
    "Maximum blocks collected per vein.",
    512.0,
    32.0,
    2048.0,
    1.0
  )

  internal val rememberVeinSpots = CheckboxSetting(
    "Remember Vein Spots",
    "Cache vein seed positions and check them first before scanning. Dwarven/Glacite veins respawn in fixed locations.",
    true
  )

  internal val clearLearnedSpots = ActionSetting(
    "Clear Learned Spots",
    "Forget cached vein anchors for the current area (dwarven_data.json / glacite_data.json).",
    "Clear",
  ) {
    MiningAnchorStore.clearAll()
    ChatUtils.sendMessage("Mining macro: cleared dwarven_data.json + glacite_data.json.")
  }

  internal val returnToStartAfterVein = CheckboxSetting(
    "Return To Start After Vein",
    "After fully mining a vein, walk back to where the macro was started before scanning for the next one.",
    true,
  )

  /** Per-area anchor cap. */
  internal const val LEARNED_ANCHOR_CAP = 256

  /**
   * Anchors of the area the player is currently in. Backed by [MiningAnchorStore]
   * which writes to dwarven_data.json or glacite_data.json based on the player's
   * Y coordinate (Y<189 = Dwarven, Y>=189 = Glacite).
   */
  internal val learnedVeinAnchors: LinkedHashSet<Long>
    get() {
      val y = mc.player?.blockY ?: return MiningAnchorStore.get(MiningArea.DWARVEN)
      return MiningAnchorStore.get(MiningAnchorStore.areaForY(y))
    }

  private val highlightPossibleBlocks = CheckboxSetting(
    "Highlight Possible Blocks",
    "Outline every currently valid block the mining macro can choose from.",
    true
  )

  private val highlightPossibleLimit = SliderSetting(
    "Highlight Limit",
    "Maximum possible blocks to outline at once. Lower this if FPS drops.",
    384.0,
    16.0,
    2048.0,
    1.0
  )

  internal val mineRange = SliderSetting(
    "Mine Range",
    "Distance to mine blocks.",
    4.5,
    2.0,
    6.0
  )

  private val highlightAimPoint = CheckboxSetting(
    "Highlight Aim Point",
    "Show the exact point on the block face the macro is rotating toward.",
    true
  )

  internal val stepToNearbyBlocks = CheckboxSetting(
    "Step To Nearby Blocks",
    "Walk a short distance to reach blocks that are just outside mining range while staying near the vein start.",
    true
  )

  internal val anchorRadius = SliderSetting(
    "Anchor Radius",
    "Maximum distance from the vein start position before returning to anchor. Disabled when running via Routes.",
    14.0,
    4.0,
    48.0
  )

  internal val goldenGoblinInterrupt = CheckboxSetting(
    "Golden Goblin Interrupt",
    "Pause mining and kill Golden Goblins as soon as the spawn message appears, then resume mining.",
    true
  )

  internal val precisionPointRotationSpeed = SliderSetting(
    "Precision Point Rotation Speed %",
    "Rotation speed multiplier used when aiming at a precision point.",
    100.0,
    10.0,
    200.0,
    1.0
  )

  internal val precisionPointChance = SliderSetting(
    "Chance to Look at Precision Point",
    "Percent chance to aim at a block's precision point while mining or tick gliding.",
    100.0,
    0.0,
    100.0,
    1.0
  )

  val tickGliding = CheckboxSetting(
    "Tick Gliding",
    "Start rotating to the next block during the ping-delay window.",
    false
  )

  private val etherwarpHeader = InfoSetting(
    "Etherwarp Settings",
    "Warp movement and aiming options.",
    InfoType.INFO
  )

  internal val useInstantTransmission = CheckboxSetting(
    "Instant Transmission",
    "Use Etherwarp (Shift + Right Click) when possible.",
    true
  )

  internal val warpMinDistance = SliderSetting(
    "Warp Min Dist",
    "Only warp if target is farther than this.",
    10.0,
    0.0,
    30.0
  )

  internal val warpAimTolerance = SliderSetting(
    "Warp Aim Tol",
    "Yaw/pitch error before warping.",
    6.0,
    2.0,
    15.0
  )

  internal val warpCooldownTicks = SliderSetting(
    "Warp Cooldown",
    "Ticks to wait between warp attempts.",
    24.0,
    6.0,
    80.0
  )

  internal val usePathfinding = CheckboxSetting(
    "Use Pathfinding",
    "Walk to the target when warping is not possible.",
    true
  )

  val useVeinDirection = CheckboxSetting(
    "Vein Direction",
    "Use Vein Direction Setter coordinate pairs to control mining flow.",
    false
  )

  internal val occupiedRadius = SliderSetting(
    "Occupied Radius",
    "Radius around a vein considered occupied.",
    6.0,
    1.0,
    10.0
  )

  val oreMinerRoutePicker = RoutePickerSetting(
    "Ore Miner Route",
    "ORE_MINER route to follow during the mining macro.",
    RouteType.ORE_MINER,
    "mining:ore",
  )

  init {
    MiningPrecisionTracker.ensureInitialized()
    MiningProfitTracker.ensureInitialized()

    addSetting(
      enabled,
      toggleKeybind,
      info,
      oreMinerRoutePicker,
      useMiningModuleType,
      blockTypes,
      blockTypesPicker,
      useDetectedBlock,
      scanRadius,
      scanVertical,
      scanPerTick,
      maxVeinBlocks,
      highlightPossibleBlocks,
      highlightPossibleLimit,
      mineRange,
      highlightAimPoint,
      stepToNearbyBlocks,
      anchorRadius,
      goldenGoblinInterrupt,
      precisionPointRotationSpeed,
      precisionPointChance,
      tickGliding,
      etherwarpHeader,
      useInstantTransmission,
      warpMinDistance,
      warpAimTolerance,
      warpCooldownTicks,
      usePathfinding,
      useVeinDirection,
      occupiedRadius,
      rememberVeinSpots,
      clearLearnedSpots,
      returnToStartAfterVein,
    )

    val generalGroup = "General"
    val targetsGroup = "Targets"
    val miningGroup = "Mining"
    val routingGroup = "Routing"
    val rotationGroup = "Rotation"
    val etherwarpGroup = "Etherwarp"
    val safetyGroup = "Safety"

    enabled.uiGroup = generalGroup
    toggleKeybind.uiGroup = generalGroup
    info.uiGroup = generalGroup

    useMiningModuleType.uiGroup = targetsGroup
    blockTypes.uiGroup = targetsGroup
    blockTypesPicker.uiGroup = targetsGroup
    useDetectedBlock.uiGroup = targetsGroup
    scanRadius.uiGroup = targetsGroup
    scanVertical.uiGroup = targetsGroup
    scanPerTick.uiGroup = targetsGroup
    maxVeinBlocks.uiGroup = targetsGroup
    highlightPossibleBlocks.uiGroup = targetsGroup
    highlightPossibleLimit.uiGroup = targetsGroup

    oreMinerRoutePicker.uiGroup = routingGroup
    anchorRadius.uiGroup = routingGroup
    usePathfinding.uiGroup = routingGroup
    useVeinDirection.uiGroup = routingGroup

    mineRange.uiGroup = miningGroup
    highlightAimPoint.uiGroup = miningGroup
    stepToNearbyBlocks.uiGroup = miningGroup

    precisionPointRotationSpeed.uiGroup = rotationGroup
    precisionPointChance.uiGroup = rotationGroup
    tickGliding.uiGroup = rotationGroup

    etherwarpHeader.uiGroup = etherwarpGroup
    useInstantTransmission.uiGroup = etherwarpGroup
    warpMinDistance.uiGroup = etherwarpGroup
    warpAimTolerance.uiGroup = etherwarpGroup
    warpCooldownTicks.uiGroup = etherwarpGroup

    goldenGoblinInterrupt.uiGroup = safetyGroup
    occupiedRadius.uiGroup = safetyGroup

    EventBus.register(this)
    EventBus.register(BlockTypePickerPopup)
  }

  internal data class Offset(val dx: Int, val dy: Int, val dz: Int, val distSq: Int)

  internal data class TypeSelection(
    val label: String,
    val ids: Set<String>,
  )

  internal data class Vein(
    val blocks: MutableSet<BlockPos>,
    val typeLabel: String,
    val targetIds: Set<String>,
    val blockId: String,
    val bounds: AABB
  )

  internal data class AimTarget(
    val point: Vec3,
    val usesPrecisionPoint: Boolean,
  )

  internal var scanOffsets: List<Offset> = emptyList()
  internal var scanIndex = 0
  internal var scanOrigin: BlockPos = BlockPos.ZERO
  internal var scanActive = false
  internal var scanRadiusCached = 0
  internal var scanVerticalCached = 0
  internal var scanPriorityIndex = 0

  internal var currentVein: Vein? = null
  internal var veinStartAnchor: BlockPos? = null
  internal var automationScanAnchor: BlockPos? = null
  internal var automationCustomBlockIds: Set<String> = emptySet()
  internal var currentTarget: BlockPos? = null
  internal var currentTargetNoLosTicks = 0
  internal var currentDirectionalFlow: VeinDirectionModule.VeinFlow? = null
  internal var lastPathTarget: BlockPos? = null
  internal var lastWarnTick = 0L

  internal var warpStage = 0
  internal var warpTarget: BlockPos? = null
  internal var warpUseEtherwarp = false
  internal var warpStageTicks = 0
  internal var warpCooldownUntil = 0L
  internal var warpRestoreSlot = -1

  internal var miningActive = false
  internal var miningOnTargetTicks = 0
  internal var miningLockedTicks = 0
  internal var miningOnTarget: BlockPos? = null
  internal var cachedPreviewTarget: BlockPos? = null
  internal var previewCacheTick = -1L
  internal var lastPruneTick = 0L
  internal var miningUsesPrecisionPoint = false
  internal val precisionRolls = HashMap<Long, Boolean>()
  internal var precisionPointChanceSnapshot = precisionPointChance.value
  internal var startedPath = false
  internal var lastPathStartTick = 0L
  internal var approachTarget: BlockPos? = null
  internal var approachStartTick = 0L
  internal var approachStartDistance = 0.0
  private var wasEnabled = false

  /** Position the player was standing in when the macro was last enabled. */
  internal var macroStartAnchor: BlockPos? = null

  /** True while pathing back to [macroStartAnchor] after a vein completes. */
  internal var returningToStart: Boolean = false

  internal var lanternPlacedForVein = false
  internal var lastLanternPlaceTick = 0L
  internal var lastLanternRefreshTick = -1L
  internal var goldenGoblinInterruptActive = false
  internal var goldenGoblinInterruptOwnedCombat = false
  internal var goldenGoblinLastSeenTick = -1L
  internal var goldenGoblinReturnPending = false
  internal var goldenGoblinReturnPos: Vec3? = null

  val isActive: Boolean get() = enabled.value

  fun startForAutomation(
    blockTypeNames: String,
    anchor: BlockPos? = null,
    customBlockId: String? = null,
    customBlockIds: Set<String> = emptySet(),
  ) {
    blockTypes.value = blockTypeNames
    automationScanAnchor = anchor?.immutable()
    val combined = linkedSetOf<String>()
    customBlockId
      ?.trim()
      ?.takeIf { it.isNotEmpty() && !MiningBlockRegistry.isBlacklisted(it) }
      ?.let { combined.add(it) }
    for (id in customBlockIds) {
      val trimmed = id.trim()
      if (trimmed.isNotEmpty() && !MiningBlockRegistry.isBlacklisted(trimmed)) combined.add(trimmed)
    }
    automationCustomBlockIds = combined
    val firstName = blockTypeNames.split(",").firstOrNull()?.trim().orEmpty()
    val idx = MiningBlockRegistry.BLOCK_TYPES.indexOf(firstName)
    if (idx >= 0) MiningModule.blockType.value = idx
    enabled.value = true
  }

  fun stopForAutomation() {
    automationScanAnchor = null
    automationCustomBlockIds = emptySet()
    enabled.value = false
  }

  internal fun onLevelChange() {
    if (!enabled.value && !wasEnabled) return
    enabled.value = false
    stopMacro("World change.")
    wasEnabled = false
  }

  fun hasCurrentVein(): Boolean = enabled.value && currentVein != null

  fun getSelectedTypesInOrder(): List<String> = parseSelectedTypes(blockTypes.value)

  fun getTrackedTargetBlock(): BlockPos? = if (enabled.value) miningOnTarget ?: currentTarget else null

  fun getTrackedTargetTicks(useGliding: Boolean = false): Int =
    if (enabled.value && miningOnTarget != null) {
      if (useGliding) miningOnTargetTicks else miningLockedTicks
    } else {
      0
    }

  fun getPrecisionTargetBlock(): BlockPos? = if (enabled.value) miningOnTarget ?: currentTarget else null

  fun isUsingPrecisionPoint(): Boolean =
    enabled.value && miningOnTarget != null && miningUsesPrecisionPoint

  fun shouldRenderRotationDebug(): Boolean =
    enabled.value && highlightAimPoint.value

  internal val skippedSeeds = HashSet<Long>()
  internal const val FLOW_MATCH_MAX_DIST_SQ = 24.0 * 24.0
  internal const val DIRECTIONAL_STEP_MAX_DIST_SQ = 64.0
  internal const val DIRECTIONAL_MIN_PROJECTION = 0.05
  internal const val DIRECTIONAL_MAX_LATERAL_SQ = 25.0
  internal const val TARGET_LOS_GRACE_TICKS = 8
  internal const val TARGET_STICKY_RANGE_EXTRA = 1.4
  internal const val APPROACH_SCAN_RADIUS = 3
  internal const val APPROACH_SCAN_VERTICAL = 2
  internal const val APPROACH_SCAN_RADIUS_WIDE = 6
  internal const val APPROACH_SCAN_VERTICAL_WIDE = 4
  internal const val APPROACH_TIMEOUT_TICKS = 30L
  internal const val APPROACH_MIN_PROGRESS_BLOCKS = 1.0
  internal const val APPROACH_HOLD_DISTANCE = 4.0
  internal const val APPROACH_EDGE_MARGIN = 0.15
  internal const val APPROACH_EDGE_WINDOW = 0.35
  internal const val WARP_ALIGN_TICKS = 20
  internal const val WARP_SNEAK_TICKS = 2
  internal const val WARP_POST_TICKS = 6
  internal const val LANTERN_PLACE_COOLDOWN_TICKS = 12L
  internal const val LANTERN_REFRESH_TICKS = 20L * 60L
  internal const val MIN_MINING_TICKS_PER_BLOCK = 40
  internal const val MINING_TIMEOUT_SCALE = 1.6
  internal const val MINING_TIMEOUT_EXTRA_TICKS = 8
  internal const val FALLBACK_TIMEOUT_MINING_SPEED = 1000.0
  internal const val WILL_O_WISP_NAME = "will o wisp"
  internal const val REQUIRE_MINE_LOS = true
  internal const val SKIP_OCCUPIED_VEINS = true
  internal const val NUDGE_RANGE_EXTRA = 2.5   // blocks beyond mineRange to attempt a gentle nudge
  internal const val MAX_WALK_BLOCKS = 5.0      // never walk more than this many blocks to reach a target
  internal const val NEARBY_STEP_RANGE_EXTRA = 0.85
  internal const val NEARBY_STEP_REACH_BUFFER = 0.65
  internal const val BLOCKING_PLAYER_MIN_CLEARANCE = 1.35
  internal const val BLOCKING_PLAYER_BOX_PADDING = 0.18
  internal const val BLOCKING_PLAYER_PENALTY = 9.0
  internal const val BLOCKING_PLAYER_REACT_RANGE = 3.25
  internal const val BLOCKING_PLAYER_SIDESTEP_DISTANCE = 1.1
  internal const val BLOCKING_PLAYER_SIDESTEP_FORWARD = 0.4
  internal const val RETURN_TO_ANCHOR_ARRIVAL_DIST = 1.25
  internal const val RETURN_TO_ANCHOR_SCAN_RADIUS = 2
  internal const val RETURN_TO_ANCHOR_SCAN_VERTICAL = 2
  internal const val GOLDEN_GOBLIN_NAME = "golden goblin"
  internal const val GOLDEN_GOBLIN_LOST_TICKS = 20L

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (toggleKeybind.value.isPressed()) {
      enabled.value = !enabled.value
    }

    if (!enabled.value) {
      if (wasEnabled) {
        stopMacro("Disabled.")
        wasEnabled = false
      }
      return
    }

    val player = mc.player ?: return
    val level = mc.level ?: return

    // First tick after enable - try to seed the vein immediately from the player's position.
    if (!wasEnabled) {
      wasEnabled = true
      macroStartAnchor = player.blockPosition().immutable()
      returningToStart = false
      MiningProfitTracker.resetSession()
      val selections = resolveTypeSelections()
      if (selections.isEmpty()) {
        ChatUtils.sendMessage("Mining macro: no block types selected, cannot start.")
        enabled.value = false
        wasEnabled = false
        return
      }
      // Attempt an immediate synchronous vein scan. If it succeeds the vein is ready on the
      // very next tick. If it fails (wrong type selected or no ore immediately nearby) just
      // allow the normal progressive scan to run - don't block the user from starting.
      if (!immediateStartScan(level, player, selections)) {
        ChatUtils.sendMessage("Mining macro: scanning for vein...")
      }
      return
    }

    if (AutoLanternModule.isPlacementInProgress()) {
      stopMiningKeys()
      if (startedPath && nativeActive()) {
        nativeStop()
      }
      startedPath = false
      lastPathTarget = null
      resetApproachTracking()
      MovementManager.clearForcedMovement()
      return
    }

    if (usePathfinding.value) {
      PathfindingModule.ensureEnabledForAutomation("mining macro")
    }

    if (!goldenGoblinInterruptActive && !goldenGoblinReturnPending) {
      if (startedPath && !nativeActive()) {
        startedPath = false
        lastPathTarget = null
        resetApproachTracking()
        MovementManager.clearForcedMovement()
      }
      if (startedPath && nativeActive()) {
        val cmd = NativePathfinder.tick()
        if (cmd != null) cmd.applyToPlayer()
        else MovementManager.clearForcedMovement()
      }
    }

    if (mc.screen != null) {
      stopMiningKeys()
      RotationExecutor.stopRotating()
      return
    }

    if (warpTarget != null) {
      handleWarp(player, level)
      return
    }

    // Return-to-start: drive the path back to macroStartAnchor before scanning
    // for the next vein. Cleared once we're within arrival distance.
    if (returningToStart) {
      val anchor = macroStartAnchor
      if (anchor == null) {
        returningToStart = false
      } else {
        val arrival = RETURN_TO_ANCHOR_ARRIVAL_DIST * RETURN_TO_ANCHOR_ARRIVAL_DIST
        if (player.blockPosition().distSqr(anchor) <= arrival) {
          returningToStart = false
          stopApproachMovement()
          MovementManager.clearForcedMovement()
        } else {
          returnToMacroStart(level, player)
          return
        }
      }
    }

    if (goldenGoblinReturnPending) {
      handleGoldenGoblinReturn(level, player)
      return
    }

    if (goldenGoblinInterruptActive) {
      handleGoldenGoblinInterrupt(level, player)
      return
    }

    val selections = resolveTypeSelections()
    if (selections.isEmpty()) {
      resetScanState()
      warnOnce("No target block ids for selected type.")
      stopMiningKeys()
      return
    }

    val vein = currentVein
    if (vein == null) {
      currentVein = null
      veinStartAnchor = null
      currentTarget = null
      currentTargetNoLosTicks = 0
      currentDirectionalFlow = null
      resetApproachTracking()
      lanternPlacedForVein = false
      lastLanternRefreshTick = -1L
      startOrContinueScan(level, player, selections)
      return
    }

    val gameTime = level.gameTime
    if (gameTime - lastPruneTick >= 20L) {
      pruneVein(level, player, vein)
      lastPruneTick = gameTime
    }
    sanitizeActiveTargets(level, player, vein)
    if (vein.blocks.isEmpty()) {
      currentVein = null
      veinStartAnchor = null
      currentTarget = null
      currentTargetNoLosTicks = 0
      currentDirectionalFlow = null
      resetApproachTracking()
      resetScanState()
      lanternPlacedForVein = false
      lastLanternRefreshTick = -1L
      // Begin return-to-start; the path is driven by the block at the top of
      // onTick once `returningToStart` is true.
      if (returnToStartAfterVein.value && macroStartAnchor != null) {
        returningToStart = true
        stopMiningKeys()
        CobaltRotation.blockController.cancel()
      }
      return
    }

    if (hasDriftedFromVeinStart(player)) {
      stopMiningKeys()
      RotationExecutor.stopRotating()
      currentTarget = null
      currentTargetNoLosTicks = 0
      returnToVeinAnchor(level, player)
      return
    }

    val target = selectMineTarget(level, player, vein)
    if (target == null) {
      currentTarget = null
      currentTargetNoLosTicks = 0
      val nearest = selectNearestBlock(level, player, vein.blocks, vein.targetIds)
      if (nearest != null) {
        val nearestDistSq = distanceToBlockSq(player, nearest)
        val shortStepLimit = maxNearbyStepDistance()
        val shortStepAllowed =
          canStepToNearbyTarget(player, nearest) && nearestDistSq <= shortStepLimit * shortStepLimit
        if (shortStepAllowed) {
          val blockingPlayer = findBlockingPlayerForTarget(level, player, nearest)
          if (useInstantTransmission.value && tryStartWarp(player, level, nearest)) {
            return
          }
          if (blockingPlayer != null && trySidestepAroundBlockingPlayer(level, player, nearest, blockingPlayer)) {
            return
          }
          when {
            blockingPlayer != null && usePathfinding.value -> {
              moveToward(
                level,
                player,
                nearest,
                forceApproach = true,
                avoidPlayer = blockingPlayer,
                maxTravelDistance = shortStepLimit,
              )
            }
            blockingPlayer != null -> {
              nudgeTowardApproach(level, player, nearest, blockingPlayer)
              focusApproachTarget(player, nearest)
            }
            stepToNearbyBlocks.value -> {
              if (usePathfinding.value) {
                moveToward(level, player, nearest, maxTravelDistance = shortStepLimit)
              } else {
                nudgeTowardApproach(level, player, nearest)
                focusApproachTarget(player, nearest)
              }
            }
            usePathfinding.value -> {
              moveToward(level, player, nearest, maxTravelDistance = shortStepLimit)
            }
            else -> {
              MovementManager.clearForcedMovement()
            }
          }
        } else {
          if (usePathfinding.value && canStepToNearbyTarget(player, nearest)) {
            moveToward(level, player, nearest)
          } else {
            MovementManager.clearForcedMovement()
          }
        }
      }
      stopMiningKeys()
      RotationExecutor.stopRotating()
      return
    }

    currentTarget = target
    val distSq = distanceToBlockSq(player, target)
    val inRange = distSq <= mineRange.value * mineRange.value

    if (inRange) {
      // Crosshair-first mining: mine whatever valid vein block the crosshair is on.
      // This prevents dig packets firing at bedrock while the camera is still rotating.
      val hitPos = (mc.hitResult as? BlockHitResult)
        ?.takeIf { it.type == HitResult.Type.BLOCK }?.blockPos
      val crosshairVeinBlock = hitPos?.takeIf { pos ->
        vein.blocks.contains(pos) &&
          isMineableTarget(level, player, pos, vein.targetIds) &&
          distanceToBlockSq(player, pos) <= mineRange.value * mineRange.value
      }

      if (crosshairVeinBlock != null) {
        currentTarget = crosshairVeinBlock
        if (maybeRefreshLantern(level, player)) {
          stopMiningKeys()
          if (startedPath && nativeActive()) {
            nativeStop()
          }
          startedPath = false
          lastPathTarget = null
          resetApproachTracking()
          MovementManager.clearForcedMovement()
          return
        }
        if (startedPath && nativeActive()) {
          nativeStop()
        }
        startedPath = false
        lastPathTarget = null
        resetApproachTracking()
        MovementManager.clearForcedMovement()
        startMining(player, crosshairVeinBlock)
      } else {
        // Not on a vein block yet â€” release attack and keep rotating toward selected target.
        if (miningActive) {
          setMiningAttackDown(false)
          miningActive = false
        }
        val blockingPlayer = findBlockingPlayerForTarget(level, player, target)
        if (blockingPlayer != null) {
          if (trySidestepAroundBlockingPlayer(level, player, target, blockingPlayer)) {
            return
          }
          if (usePathfinding.value) {
            moveToward(level, player, target, forceApproach = true, avoidPlayer = blockingPlayer)
          } else {
            nudgeTowardApproach(level, player, target, blockingPlayer)
            focusApproachTarget(player, target)
          }
          return
        }
        val aim = resolveMiningAimPoint(player, target)
        val precisionRotScale =
          if (aim.usesPrecisionPoint) (precisionPointRotationSpeed.value / 100.0).coerceAtLeast(0.1)
          else 1.0
        // Drive the block-to-block controller on every new target. Duration is scaled
        // by angular distance: short hops finish in ~5 ticks, long hops up to ~16.
        if (target != frameRotPrevBlock) {
          val initRot = AngleUtils.getRotation(aim.point)
          val initYaw = abs(AngleUtils.getRotationDelta(player.yRot, initRot.yaw))
          val initPitch = abs(initRot.pitch - player.xRot)
          val initialDist = maxOf(initYaw, initPitch).coerceAtLeast(1f)
          frameRotInitialDist = initialDist
          // Longer base duration + sine in/out gives the camera a clear
          // acceleration in, sweep, and decel out — the "deliberate, not
          // robotic" feel the spec asks for.
          val baseDuration = (initialDist / 30f * 3f).toInt().coerceIn(3, 12)
          val scaledDuration = (baseDuration / precisionRotScale.coerceAtLeast(0.1))
            .toInt().coerceIn(3, 18)
          val fromBlock = frameRotPrevBlock ?: target
          CobaltRotation.blockController.rotate(
            BlockRotationRequest(
              fromBlock = fromBlock,
              toBlock = target,
              durationTicks = scaledDuration,
              useFromBlockAsStartRotation = false,
              maxDegreesPerTick = 12f,
              easing = RotationEasingType.EASE_IN_OUT_SINE,
            )
          )
          frameRotPrevBlock = target
          frameRotLastNs = 0L
        }
        if (aim.usesPrecisionPoint) {
          CobaltRotation.blockController.setPrecisionPoint(aim.point)
        } else {
          CobaltRotation.blockController.setPrecisionPoint(null)
        }
        frameRotTarget = aim.point
        setAimRenderTarget(target, aim.point)
      }
    } else {
      stopMiningKeys()
      RotationExecutor.stopRotating()
      if (useInstantTransmission.value && tryStartWarp(player, level, target)) {
        return
      }
      val blockingPlayer = findBlockingPlayerForTarget(level, player, target)
      if (blockingPlayer != null && trySidestepAroundBlockingPlayer(level, player, target, blockingPlayer)) {
        return
      }
      val nudgeThresh = mineRange.value + NUDGE_RANGE_EXTRA
      val shortStepLimit = maxNearbyStepDistance()
      val canShortStep =
        stepToNearbyBlocks.value &&
          canStepToNearbyTarget(player, target) &&
          distSq <= nudgeThresh * nudgeThresh
      // Fast path: target is just barely out of mining range (≤ mineRange + 2).
      // The native pathfinder is unreliable at these tiny distances — it
      // oscillates between PLANNING and ARRIVED, and during those states the
      // early-tick block calls clearForcedMovement, killing any step. Drive a
      // direct nudge in that window so the player actually walks the half-block
      // they need to put the block in range.
      val closeNudgeRange = mineRange.value + 2.0
      if (canShortStep && blockingPlayer == null && distSq <= closeNudgeRange * closeNudgeRange) {
        if (startedPath && nativeActive()) {
          nativeStop()
        }
        startedPath = false
        lastPathTarget = null
        resetApproachTracking()
        nudgeTowardApproach(level, player, target)
        focusApproachTarget(player, target)
        return
      }
        when {
          blockingPlayer != null && usePathfinding.value && canShortStep -> {
            moveToward(
              level,
              player,
            target,
            forceApproach = true,
            avoidPlayer = blockingPlayer,
            maxTravelDistance = shortStepLimit,
          )
        }
          blockingPlayer != null && usePathfinding.value -> {
            moveToward(level, player, target, forceApproach = true, avoidPlayer = blockingPlayer)
          }
          canShortStep && usePathfinding.value -> {
            moveToward(level, player, target, maxTravelDistance = shortStepLimit)
          }
          canShortStep -> {
            nudgeTowardApproach(level, player, target, blockingPlayer)
            focusApproachTarget(player, target)
          }
        usePathfinding.value -> {
          moveToward(level, player, target, avoidPlayer = blockingPlayer)
        }
        else -> {
          MovementManager.clearForcedMovement()
        }
      }
    }
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    val raw = event.message ?: return
    val message = ChatFormatting.stripFormatting(raw)?.lowercase() ?: raw.lowercase()

    if (enabled.value && goldenGoblinInterrupt.value && message.contains("a golden goblin has spawned")) {
      beginGoldenGoblinInterrupt()
      mc.level?.let { level -> goldenGoblinLastSeenTick = level.gameTime }
      return
    }

    if (goldenGoblinInterruptActive) {
      if (message.contains("from killing a golden goblin") || message.contains("golden goblin slayer commission complete")) {
        finishGoldenGoblinInterrupt()
      }
    }
  }

  @SubscribeEvent
  fun onBlockChange(event: BlockChangeEvent) {
    val vein = currentVein ?: return
    val newId = BuiltInRegistries.BLOCK.getKey(event.newBlock.block).toString()
    val level = mc.level
    val player = mc.player
    val stillTarget =
      vein.targetIds.contains(newId)
        && !MiningBlockRegistry.isBlacklisted(newId)
        && !event.newBlock.isAir
        && (level == null || player == null || event.newBlock.getDestroyProgress(player, level, event.pos) > 0f)

    if (!stillTarget) {
      vein.blocks.remove(event.pos)
      precisionRolls.remove(event.pos.asLong())
      val wasRotationTarget = currentTarget == event.pos || miningOnTarget == event.pos ||
        frameRotPrevBlock == event.pos
      if (currentTarget == event.pos) {
        currentTarget = null
        currentTargetNoLosTicks = 0
      }
      if (approachTarget == event.pos || lastPathTarget == event.pos) {
        stopApproachMovement()
      }
      if (miningOnTarget == event.pos) {
        stopMiningKeys()
      }
      // Block under the rotation controller just turned to bedrock/air. Immediately
      // retarget the controller to the next-nearest mineable vein block (or cancel)
      // so the camera doesn't linger on the now-exposed bedrock until the next
      // onTick happens to pick an in-range target.
      if (wasRotationTarget) {
        val playerNow = mc.player
        val levelNow = mc.level
        val fallback = if (playerNow != null && levelNow != null && vein.blocks.isNotEmpty()) {
          val playerPos = playerNow.blockPosition()
          vein.blocks
            .asSequence()
            .filter { isMineableTarget(levelNow, playerNow, it, vein.targetIds) }
            .minByOrNull { it.distSqr(playerPos) }
        } else null
        CobaltRotation.blockController.setFallbackBlock(fallback)
        if (fallback != null) {
          CobaltRotation.blockController.rotate(
            BlockRotationRequest(
              fromBlock = event.pos,
              toBlock = fallback,
              durationTicks = 5,
              useFromBlockAsStartRotation = false,
              maxDegreesPerTick = 12f,
              easing = RotationEasingType.EASE_IN_OUT_SINE,
            )
          )
          frameRotPrevBlock = fallback
          frameRotTarget = null
        } else {
          CobaltRotation.blockController.cancel()
          frameRotPrevBlock = null
          frameRotTarget = null
        }
      }
    } else if (!vein.blocks.contains(event.pos)) {
      // Block refreshed/respawned back to a target ore - re-add it to the active vein.
      vein.blocks.add(event.pos)
    }
  }

  // Rotation is now driven by CobaltRotation.blockController on TickEvent.End.
  // This handler just clears the frame-rotation bookkeeping when there is no
  // target, so frameRotPrevBlock resets correctly between veins.
  @SubscribeEvent
  fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    if (frameRotTarget == null) {
      frameRotLastNs = 0L
      frameRotPrevBlock = null
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    val level = mc.level ?: run {
      OverlayRenderEngine.clearTag(OVERLAY_TAG)
      return
    }
    if (!enabled.value) {
      OverlayRenderEngine.clearTag(OVERLAY_TAG)
      return
    }

    val active = miningOnTarget
    val player = mc.player ?: run {
      OverlayRenderEngine.clearTag(OVERLAY_TAG)
      return
    }
    val gameTime = level.gameTime
    if (gameTime != previewCacheTick) {
      cachedPreviewTarget = resolvePreviewTarget(level, player)
      previewCacheTick = gameTime
    }
    val preview = cachedPreviewTarget
    val returnPos = goldenGoblinReturnPos
    val connectedBlocks = collectConnectedHighlightBlocks(level, player, active, preview)
    val vein = currentVein
    OverlayRenderEngine.clearTag(OVERLAY_TAG)
    if (active == null && preview == null && returnPos == null && connectedBlocks.isEmpty() && vein == null) {
      return
    }

    val activeFill = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0x44)
    val activeOutline = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0xFF)
    val previewOutline = OverlayRenderEngine.Color(0xFF, 0xD8, 0x4C, 0xFF)
    val possibleOutline = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0xA0)
    val returnOutline = OverlayRenderEngine.Color(0x6E, 0xEA, 0x92, 0xFF)
    renderSingleVeinOutline(level, vein, connectedBlocks, possibleOutline)

    if (active != null) {
      val pad = 0.002
      OverlayRenderEngine.addBox(
        level,
        active.x - pad,
        active.y - pad,
        active.z - pad,
        active.x + 1.0 + pad,
        active.y + 1.0 + pad,
        active.z + 1.0 + pad,
        activeFill,
        activeOutline,
        2.0f,
        2,
        OVERLAY_TAG,
      )
    }
    if (preview != null && preview != active) {
      OverlayRenderEngine.outlineBlockColor(level, preview, previewOutline, 2, OVERLAY_TAG, 2.0f)
    }
    if (returnPos != null) {
      renderReturnSpotMarker(level, returnPos, returnOutline)
    }
  }


  internal fun setAimRenderTarget(block: BlockPos?, point: Vec3?) {
    aimRenderBlock = block
    aimRenderPoint = point
  }

  private fun renderSingleVeinOutline(
    level: net.minecraft.world.level.Level,
    vein: Vein?,
    fallbackBlocks: Set<BlockPos>,
    color: OverlayRenderEngine.Color,
  ) {
    if (!highlightPossibleBlocks.value) return

    val bounds = when {
      vein != null && vein.blocks.isNotEmpty() -> boundsForBlocks(vein.blocks)
      fallbackBlocks.isNotEmpty() -> boundsForBlocks(fallbackBlocks)
      else -> return
    }

    // One big outline for the full vein. This intentionally does NOT draw one box
    // per block, so the scan preview looks like one connected vein shell instead
    // of a pile of separate cubes.
    val pad = 0.025
    OverlayRenderEngine.addBox(
      level,
      bounds.minX - pad,
      bounds.minY - pad,
      bounds.minZ - pad,
      bounds.maxX + pad,
      bounds.maxY + pad,
      bounds.maxZ + pad,
      null,
      color,
      2.6f,
      2,
      OVERLAY_TAG,
      forceRender = true,
    )
  }

  private fun boundsForBlocks(blocks: Collection<BlockPos>): AABB {
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var minZ = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE
    var maxZ = Int.MIN_VALUE

    for (pos in blocks) {
      if (pos.x < minX) minX = pos.x
      if (pos.y < minY) minY = pos.y
      if (pos.z < minZ) minZ = pos.z
      if (pos.x > maxX) maxX = pos.x
      if (pos.y > maxY) maxY = pos.y
      if (pos.z > maxZ) maxZ = pos.z
    }

    return AABB(
      minX.toDouble(),
      minY.toDouble(),
      minZ.toDouble(),
      maxX + 1.0,
      maxY + 1.0,
      maxZ + 1.0,
    )
  }

  private data class RenderEdge(
    val x1: Int,
    val y1: Int,
    val z1: Int,
    val x2: Int,
    val y2: Int,
    val z2: Int,
  )

  private fun collectConnectedHighlightBlocks(
    level: net.minecraft.world.level.Level,
    player: Player,
    active: BlockPos?,
    preview: BlockPos?,
  ): Set<BlockPos> {
    if (!highlightPossibleBlocks.value) return emptySet()
    val vein = currentVein ?: return emptySet()
    val limit = highlightPossibleLimit.value.toInt().coerceAtLeast(1)
    val result = LinkedHashSet<BlockPos>(minOf(limit, vein.blocks.size))

    // Always include the active and preview blocks first so the connected shell does not
    // draw a fake seam between the block being mined and the rest of the vein.
    if (active != null && vein.blocks.contains(active) && isMineableTarget(level, player, active, vein.targetIds)) {
      result.add(active)
    }
    if (preview != null && vein.blocks.contains(preview) && isMineableTarget(level, player, preview, vein.targetIds)) {
      result.add(preview)
    }

    val sorted = vein.blocks
      .asSequence()
      .filter { it != active && it != preview }
      .filter { isMineableTarget(level, player, it, vein.targetIds) }
      .sortedBy { highlightSortScore(player, it) }

    for (pos in sorted) {
      result.add(pos)
      if (result.size >= limit) break
    }

    return result
  }

  private fun highlightSortScore(player: Player, pos: BlockPos): Double {
    val dx = (pos.x + 0.5) - player.eyePosition.x
    val dy = (pos.y + 0.5) - player.eyePosition.y
    val dz = (pos.z + 0.5) - player.eyePosition.z
    val belowPenalty = if (pos.y < player.blockY - 1) 16.0 else 0.0
    return dx * dx + dy * dy + dz * dz + belowPenalty
  }

  private fun renderConnectedBlockShell(
    level: net.minecraft.world.level.Level,
    blocks: Set<BlockPos>,
    color: OverlayRenderEngine.Color,
  ) {
    if (blocks.isEmpty()) return
    val edges = LinkedHashSet<RenderEdge>()
    for (pos in blocks) {
      for (dir in Direction.values()) {
        if (blocks.contains(pos.relative(dir))) continue
        addFaceEdges(edges, pos, dir)
      }
    }
    for (edge in edges) {
      OverlayRenderEngine.addLine(
        level,
        edge.x1.toDouble(), edge.y1.toDouble(), edge.z1.toDouble(),
        edge.x2.toDouble(), edge.y2.toDouble(), edge.z2.toDouble(),
        color,
        1.35f,
        2,
        OVERLAY_TAG,
      )
    }
  }

  private fun addFaceEdges(edges: MutableSet<RenderEdge>, pos: BlockPos, dir: Direction) {
    val x0 = pos.x
    val y0 = pos.y
    val z0 = pos.z
    val x1 = pos.x + 1
    val y1 = pos.y + 1
    val z1 = pos.z + 1

    when (dir) {
      Direction.DOWN -> {
        addEdge(edges, x0, y0, z0, x1, y0, z0)
        addEdge(edges, x1, y0, z0, x1, y0, z1)
        addEdge(edges, x1, y0, z1, x0, y0, z1)
        addEdge(edges, x0, y0, z1, x0, y0, z0)
      }
      Direction.UP -> {
        addEdge(edges, x0, y1, z0, x1, y1, z0)
        addEdge(edges, x1, y1, z0, x1, y1, z1)
        addEdge(edges, x1, y1, z1, x0, y1, z1)
        addEdge(edges, x0, y1, z1, x0, y1, z0)
      }
      Direction.NORTH -> {
        addEdge(edges, x0, y0, z0, x1, y0, z0)
        addEdge(edges, x1, y0, z0, x1, y1, z0)
        addEdge(edges, x1, y1, z0, x0, y1, z0)
        addEdge(edges, x0, y1, z0, x0, y0, z0)
      }
      Direction.SOUTH -> {
        addEdge(edges, x0, y0, z1, x1, y0, z1)
        addEdge(edges, x1, y0, z1, x1, y1, z1)
        addEdge(edges, x1, y1, z1, x0, y1, z1)
        addEdge(edges, x0, y1, z1, x0, y0, z1)
      }
      Direction.WEST -> {
        addEdge(edges, x0, y0, z0, x0, y0, z1)
        addEdge(edges, x0, y0, z1, x0, y1, z1)
        addEdge(edges, x0, y1, z1, x0, y1, z0)
        addEdge(edges, x0, y1, z0, x0, y0, z0)
      }
      Direction.EAST -> {
        addEdge(edges, x1, y0, z0, x1, y0, z1)
        addEdge(edges, x1, y0, z1, x1, y1, z1)
        addEdge(edges, x1, y1, z1, x1, y1, z0)
        addEdge(edges, x1, y1, z0, x1, y0, z0)
      }
    }
  }

  private fun addEdge(
    edges: MutableSet<RenderEdge>,
    ax: Int,
    ay: Int,
    az: Int,
    bx: Int,
    by: Int,
    bz: Int,
  ) {
    val forward =
      ax < bx ||
        (ax == bx && ay < by) ||
        (ax == bx && ay == by && az <= bz)
    if (forward) {
      edges.add(RenderEdge(ax, ay, az, bx, by, bz))
    } else {
      edges.add(RenderEdge(bx, by, bz, ax, ay, az))
    }
  }

  private fun stopMacro(reason: String) {
    if (goldenGoblinInterruptOwnedCombat && CombatMacroModule.isActive) {
      CombatMacroModule.stopForAutomation()
    }
    if (startedPath && nativeActive()) {
      nativeStop()
    }
    startedPath = false
    lastPathStartTick = 0L
    stopMiningKeys()
    RotationExecutor.stopRotating()
    CobaltRotation.blockController.cancel()
    frameRotPrevBlock = null
    frameRotTarget = null
    macroStartAnchor = null
    returningToStart = false
    restoreEtherwarpSlot()
    resetWarp()
    currentVein = null
    veinStartAnchor = null
    currentTarget = null
    currentTargetNoLosTicks = 0
    currentDirectionalFlow = null
    lastPathTarget = null
    cachedPreviewTarget = null
    previewCacheTick = -1L
    lastPruneTick = 0L
    resetApproachTracking()
    lanternPlacedForVein = false
    lastLanternRefreshTick = -1L
    resetScanState()
    skippedSeeds.clear()
    miningOnTarget = null
    miningOnTargetTicks = 0
    frameRotSnapThreshold = 0f
    precisionRolls.clear()
    goldenGoblinInterruptActive = false
    goldenGoblinInterruptOwnedCombat = false
    goldenGoblinLastSeenTick = -1L
    goldenGoblinReturnPending = false
    goldenGoblinReturnPos = null
    automationScanAnchor = null
    automationCustomBlockIds = emptySet()
  }
}
