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
  internal var frameRotSpeedScale: Float  = 1f
  internal var frameRotAccelScale: Float  = 1f
  internal var frameRotPitchStep:  Float  = 3.5f
  internal var frameRotMaxSpeed:   Float  = 100f
  internal var frameRotMaxAccel:   Float  = 220f
  internal var frameRotSnapThreshold: Float = 0f

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
    256.0,
    32.0,
    512.0
  )

  internal val mineRange = SliderSetting(
    "Mine Range",
    "Distance to mine blocks.",
    4.5,
    2.0,
    6.0
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
      mineRange,
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

    oreMinerRoutePicker.uiGroup = routingGroup
    anchorRadius.uiGroup = routingGroup
    usePathfinding.uiGroup = routingGroup
    useVeinDirection.uiGroup = routingGroup

    mineRange.uiGroup = miningGroup
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

  internal val skippedSeeds = HashSet<Long>()
  internal const val FLOW_MATCH_MAX_DIST_SQ = 24.0 * 24.0
  internal const val DIRECTIONAL_STEP_MAX_DIST_SQ = 64.0
  internal const val DIRECTIONAL_MIN_PROJECTION = 0.05
  internal const val DIRECTIONAL_MAX_LATERAL_SQ = 25.0
  internal const val TARGET_LOS_GRACE_TICKS = 8
  internal const val TARGET_STICKY_RANGE_EXTRA = 1.4
  internal const val APPROACH_SCAN_RADIUS = 3
  internal const val APPROACH_SCAN_VERTICAL = 2
  internal const val APPROACH_TIMEOUT_TICKS = 18L
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
              moveToward(level, player, nearest)
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
        frameRotSnapThreshold = RotationsModule.bezierSnapThreshold.value.toFloat()
        frameRotTarget     = aim.point
        frameRotSpeedScale = (RotationsModule.sample(RotationsModule.miningSpeedScale.value) * precisionRotScale).toFloat()
        frameRotAccelScale = (RotationsModule.sample(RotationsModule.miningAccelScale.value) * precisionRotScale).toFloat()
        frameRotPitchStep  = (RotationsModule.sample(RotationsModule.miningPitchStep.value) * precisionRotScale).toFloat()
        frameRotMaxSpeed   = (RotationsModule.sample(RotationsModule.miningMaxSpeed.value) * precisionRotScale).toFloat()
        frameRotMaxAccel   = (RotationsModule.sample(RotationsModule.miningMaxAccel.value) * precisionRotScale).toFloat()
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
    } else if (!vein.blocks.contains(event.pos)) {
      // Block refreshed/respawned back to a target ore - re-add it to the active vein.
      vein.blocks.add(event.pos)
    }
  }

  @SubscribeEvent
  fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    val target = frameRotTarget ?: return
    val player = mc.player ?: return

    // Inversely scale speed with angular distance: small deltas get the largest boost so that
    // adjacent-block rotations (typically 5-20 deg) are not glacially slow.
    val targetRot  = AngleUtils.getRotation(target)
    val yawDelta   = abs(AngleUtils.getRotationDelta(player.yRot, targetRot.yaw).toFloat())
    val pitchDelta = abs(targetRot.pitch - player.xRot)
    val maxDelta   = maxOf(yawDelta, pitchDelta).coerceAtLeast(1f)
    val distScale  = (60f / maxDelta).coerceIn(1f, 6f)

    applyHeadRotation(
      player, target,
      maxSpeedScale = frameRotSpeedScale * distScale,
      accelScale    = frameRotAccelScale * distScale,
      maxPitchStep  = frameRotPitchStep,
      maxTurnSpeed  = frameRotMaxSpeed,
      maxTurnAccel  = frameRotMaxAccel,
      snapThreshold = frameRotSnapThreshold,
    )
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

    OverlayRenderEngine.clearTag(OVERLAY_TAG)
    if (active == null && preview == null && returnPos == null) {
      return
    }

    val activeFill = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0x44)
    val activeOutline = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0xFF)
    val previewOutline = OverlayRenderEngine.Color(0xFF, 0xD8, 0x4C, 0xFF)
    val returnOutline = OverlayRenderEngine.Color(0x6E, 0xEA, 0x92, 0xFF)

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
