package org.phantom.internal.mining

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
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.BlockChangeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.ActionSetting
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.KeyBindSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.util.helper.KeyBind
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.util.AngleUtils
import org.phantom.internal.pathfinding.HeadRotationModule
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.InventoryUtils
import org.phantom.api.pathfinder.minecraft.MinecraftPathingRules
import org.phantom.internal.etherwarp.EtherwarpLogic
import org.phantom.internal.pathfinding.OverlayRenderEngine
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.util.player.MovementManager
import org.phantom.internal.combat.CombatMacroModule
import org.phantom.internal.pathfinding.PathfindingModule
import org.phantom.internal.rotation.BlockRotationRequest
import org.phantom.internal.rotation.PhantomRotation
import org.phantom.internal.rotation.RotationEasingType
import org.phantom.internal.rotation.RotationsModule
import org.phantom.internal.routes.RoutePickerSetting
import org.phantom.internal.routes.RouteType
import org.phantom.internal.skyblock.DwarvenSidebarLocationParser
import org.phantom.internal.skyblock.HypixelManager
import org.phantom.internal.ui.panel.panels.UIModuleList

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
   * which writes to dwarven_data.json or glacite_data.json using the ore type
   * when it is known, with Y coordinate as the fallback.
   */
  internal val learnedVeinAnchors: LinkedHashSet<Long>
    get() {
      return MiningAnchorStore.get(currentMiningArea())
    }

  internal fun currentMiningArea(player: Player? = mc.player): MiningArea {
    if (player != null && player.blockY > 187) return MiningArea.GLACITE
    DwarvenSidebarLocationParser.currentSidebarLocation()?.let { sidebarLocation ->
      if (DwarvenSidebarLocationParser.isGlaciteLocation(sidebarLocation)) return MiningArea.GLACITE
    }
    val snapshot = HypixelManager.snapshot()
    if (
      DwarvenSidebarLocationParser.isGlaciteLocation(snapshot.area) ||
      DwarvenSidebarLocationParser.isGlaciteLocation(snapshot.map) ||
      DwarvenSidebarLocationParser.isGlaciteLocation(snapshot.placeName)
    ) {
      return MiningArea.GLACITE
    }
    return player?.let { MiningAnchorStore.areaForY(it.blockY) } ?: MiningArea.DWARVEN
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

  internal val costModelEnabled = CheckboxSetting(
    "Cost-Based Target Selection",
    "Score in-range blocks by a V5-style cost (look direction + distance + visibility + cluster) instead of pure nearest-distance. Disable for the old strict-nearest behavior.",
    true,
  )

  internal val costLookBias = SliderSetting(
    "Look Direction Bias",
    "Prefer blocks already near your crosshair (V5 dot*50). Higher = fewer/smaller rotations.",
    50.0,
    0.0,
    100.0,
    1.0,
  )

  internal val costDistanceWeight = SliderSetting(
    "Distance Weight",
    "Cost added per block of distance to the aim point (V5 dist*2).",
    2.0,
    0.0,
    10.0,
    0.1,
  )

  internal val costVisibilityPenalty = SliderSetting(
    "Visibility Stability Penalty",
    "Penalty for blocks that lose line-of-sight under small head movement. 0 disables the stability sampling.",
    18.0,
    0.0,
    50.0,
    1.0,
  )

  internal val costBehindPenalty = SliderSetting(
    "Behind-Player Penalty",
    "Extra cost for blocks behind you (avoids ~180 degree spins). 0 disables.",
    0.0,
    0.0,
    200.0,
    1.0,
  )

  internal val costClusterBonus = SliderSetting(
    "Cluster Bonus",
    "Cost reduction per neighbouring vein block. Higher = commit to the dense clump instead of chasing a lone nearer block. 0 disables.",
    4.0,
    0.0,
    20.0,
    0.5,
  )

  internal val costDistanceBand = SliderSetting(
    "Distance Band",
    "Width (blocks) of each distance ring. The nearest ring always wins; look-bias/cluster/visibility only break ties within a ring. Smaller = stricter closest-first.",
    1.0,
    0.25,
    5.0,
    0.25,
  )

  private val highlightAimPoint = CheckboxSetting(
    "Highlight Aim Point",
    "Show the exact point on the block face the macro is rotating toward.",
    true
  )

  private val highlightBedrock = CheckboxSetting(
    "Highlight Bedrock",
    "Outline every block the macro currently treats as bedrock/blacklisted around the player. Use to confirm the macro's view of the world matches reality.",
    false
  )

  private val highlightBedrockRadius = SliderSetting(
    "Bedrock Highlight Radius",
    "Horizontal radius (blocks) scanned for the bedrock highlight.",
    6.0,
    2.0,
    16.0,
    1.0,
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

  internal val glideLagComp = SliderSetting(
    "Glide Lag Compensation",
    "Extra ticks before tick-glide begins (V5 ADDITIONAL_LAG_COMP). Raise if you glide off blocks before they break under lag. 0 = unchanged.",
    0.0,
    0.0,
    5.0,
    1.0,
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

  // â”€â”€â”€ Mining Ability tab â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  internal val useMiningAbility = CheckboxSetting(
    "Use Mining Ability",
    "Automatically activate the pickaxe ability when chat says it's available.",
    false,
  )

  internal val useMiningSpeedBoost = CheckboxSetting(
    "Mining Speed Boost",
    "Activate Mining Speed Boost on \"is now available!\" chat trigger.",
    true,
  )

  internal val usePickobulus = CheckboxSetting(
    "Pickobulus",
    "Activate Pickobulus on \"is now available!\" chat trigger.",
    false,
  )

  internal val useManiacMiner = CheckboxSetting(
    "Maniac Miner",
    "Activate Maniac Miner on \"is now available!\" chat trigger.",
    false,
  )

  internal val useTunnelVision = CheckboxSetting(
    "Tunnel Vision",
    "Activate Tunnel Vision on \"is now available!\" chat trigger.",
    false,
  )

  internal val useSheerForce = CheckboxSetting(
    "Sheer Force",
    "Activate Sheer Force on \"is now available!\" chat trigger.",
    false,
  )

  internal val useGemstoneInfusion = CheckboxSetting(
    "Gemstone Infusion",
    "Activate Gemstone Infusion on \"is now available!\" chat trigger.",
    false,
  )

  internal val abilityActivationDelay = SliderSetting(
    "Ability Delay (ticks)",
    "Ticks to wait after the trigger before right-clicking.",
    4.0,
    0.0,
    40.0,
    1.0,
  )

  // â”€â”€â”€ Rod Swap tab â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  internal val useRodSwap = CheckboxSetting(
    "Use Rod Swap",
    "Swap to a configured hotbar slot on a trigger, then swap back.",
    false,
  )

  internal val rodSwapSlot = SliderSetting(
    "Rod Slot",
    "Hotbar slot (1-9) to swap to.",
    1.0,
    1.0,
    9.0,
    1.0,
  )

  internal val rodSwapReturnSlot = SliderSetting(
    "Return Slot",
    "Hotbar slot to swap back to after the rod action. 0 = remember previous.",
    0.0,
    0.0,
    9.0,
    1.0,
  )

  // Mining ability state (chat-driven).
  internal var pickobulusAvailable = false
  internal var miningSpeedBoostAvailable = false
  internal var miningSpeedBoostActive = false
  internal var maniacMinerAvailable = false
  internal var maniacMinerActive = false
  internal var tunnelVisionAvailable = false
  internal var tunnelVisionActive = false
  internal var sheerForceAvailable = false
  internal var sheerForceActive = false
  internal var gemstoneInfusionAvailable = false
  internal var gemstoneInfusionActive = false
  internal var pendingAbilityActivationTick = -1L
  internal var pendingAbilityName: String? = null
  internal var preAbilitySlot = -1
  // Retry tracking: after firing the right-click we wait up to 2 s (40 ticks)
  // for the "You used your X Pickaxe Ability!" confirmation. Keep retries very
  // conservative; repeated server-side right-clicks can get the player kicked.
  internal var awaitingAbilityResponse = false
  internal var abilityResponseDeadlineTick = -1L
  internal var abilityRetryCount = 0
  internal const val ABILITY_RESPONSE_TIMEOUT_TICKS = 40L
  internal const val ABILITY_MAX_RETRIES = 1

  // Drill class detection. On macro enable we right-click once with whatever
  // pickaxe/drill is held; the resulting "You used your X Pickaxe Ability!"
  // chat tells us which of the three classes (Mining Speed Boost / Pickobulus /
  // any third ability) the held tool belongs to. Stored for ability routing.
  internal var detectedDrillAbility: String? = null
  internal var awaitingDrillProbeResponse = false
  internal var drillProbePendingTick = -1L
  private val PICKAXE_ABILITY_REGEX = Regex("you used your (.+?) pickaxe ability")

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
      costModelEnabled,
      costLookBias,
      costDistanceWeight,
      costVisibilityPenalty,
      costBehindPenalty,
      costClusterBonus,
      costDistanceBand,
      highlightAimPoint,
      highlightBedrock,
      highlightBedrockRadius,
      stepToNearbyBlocks,
      anchorRadius,
      goldenGoblinInterrupt,
      precisionPointRotationSpeed,
      precisionPointChance,
      tickGliding,
      glideLagComp,
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
      useMiningAbility,
      useMiningSpeedBoost,
      usePickobulus,
      useManiacMiner,
      useTunnelVision,
      useSheerForce,
      useGemstoneInfusion,
      abilityActivationDelay,
      useRodSwap,
      rodSwapSlot,
      rodSwapReturnSlot,
    )

    val generalGroup = "General"
    val targetsGroup = "Targets"
    val miningGroup = "Mining"
    val routingGroup = "Routing"
    val rotationGroup = "Rotation"
    val etherwarpGroup = "Etherwarp"
    val safetyGroup = "Safety"
    val abilityGroup = "Mining Ability"
    val rodSwapGroup = "Rod Swap"

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
    val targetCostGroup = "Target Cost"
    costModelEnabled.uiGroup = targetCostGroup
    costLookBias.uiGroup = targetCostGroup
    costDistanceWeight.uiGroup = targetCostGroup
    costVisibilityPenalty.uiGroup = targetCostGroup
    costBehindPenalty.uiGroup = targetCostGroup
    costClusterBonus.uiGroup = targetCostGroup
    costDistanceBand.uiGroup = targetCostGroup
    highlightAimPoint.uiGroup = miningGroup
    highlightBedrock.uiGroup = miningGroup
    highlightBedrockRadius.uiGroup = miningGroup
    stepToNearbyBlocks.uiGroup = miningGroup

    precisionPointRotationSpeed.uiGroup = rotationGroup
    precisionPointChance.uiGroup = rotationGroup
    tickGliding.uiGroup = rotationGroup
    glideLagComp.uiGroup = rotationGroup

    etherwarpHeader.uiGroup = etherwarpGroup
    useInstantTransmission.uiGroup = etherwarpGroup
    warpMinDistance.uiGroup = etherwarpGroup
    warpAimTolerance.uiGroup = etherwarpGroup
    warpCooldownTicks.uiGroup = etherwarpGroup

    goldenGoblinInterrupt.uiGroup = safetyGroup
    occupiedRadius.uiGroup = safetyGroup

    useMiningAbility.uiGroup = abilityGroup
    useMiningSpeedBoost.uiGroup = abilityGroup
    usePickobulus.uiGroup = abilityGroup
    useManiacMiner.uiGroup = abilityGroup
    useTunnelVision.uiGroup = abilityGroup
    useSheerForce.uiGroup = abilityGroup
    useGemstoneInfusion.uiGroup = abilityGroup
    abilityActivationDelay.uiGroup = abilityGroup

    useRodSwap.uiGroup = rodSwapGroup
    rodSwapSlot.uiGroup = rodSwapGroup
    rodSwapReturnSlot.uiGroup = rodSwapGroup

    EventBus.register(this)
    EventBus.register(BlockTypePickerPopup)
  }

  internal data class Offset(val dx: Int, val dy: Int, val dz: Int, val distSq: Int)

  internal data class TypeSelection(
    val label: String,
    val ids: Set<String>,
    // Per-block-id base cost override consumed by the cost-based target scorer
    // (e.g. weight gray mithril vs light-blue, or prioritize titanium). Empty =
    // every id uses DEFAULT_TARGET_BASE_COST. Plumbing for the Commission pass.
    val weights: Map<String, Double> = emptyMap(),
  )

  internal data class Vein(
    val blocks: MutableSet<BlockPos>,
    val typeLabel: String,
    val targetIds: Set<String>,
    val blockId: String,
    val bounds: AABB,
    val weights: Map<String, Double> = emptyMap(),
  )

  /** Base cost for a block id with no explicit weight (V5 cost-table value). */
  internal const val DEFAULT_TARGET_BASE_COST = 4.0
  /** Normalized look-dot below which a block counts as "behind" (V5 -0.05). */
  internal const val BEHIND_DOT_THRESHOLD = -0.05
  /**
   * Per-distance-ring weight in the cost scorer. Must exceed the combined
   * swing of every in-ring tie-break term (base + dist*distW + behind +
   * vertical + visPen, minus lookBias + cluster ~= [-340, +350] at max
   * settings) so the nearest ring strictly dominates. 1000 > ~690 span.
   */
  internal const val COST_BAND_WEIGHT = 1000.0

  // The native pathfinder only serializes a 96x40x96 buffer around the player,
  // so a goal outside it can never be solved and just FAILs on repeat. Past
  // these conservative half-extents we walk straight at the target instead of
  // asking the pather (no frontier A* â€” matches the "fail cleanly" decision).
  internal const val NATIVE_PATH_MAX_HORIZONTAL = 40.0
  internal const val NATIVE_PATH_MAX_VERTICAL = 16.0

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
  // "Stuck stare" recovery: in-range + target selected but the crosshair pick
  // keeps landing somewhere other than the target. Counts how many consecutive
  // ticks we've been in that state for the same target so we can re-aim to the
  // block center (which raycasts cleanly) instead of staring forever at a
  // precision point that grazes a face edge.
  internal var stareTarget: BlockPos? = null
  internal var stareTicks: Int = 0
  // Blacklist fires at stareTicks > STARE_RECOVERY_TICKS * 2. The opening
  // sweep into a fresh vein is an eased rotation up to scaledDuration (capped
  // 18 ticks) plus the exp-smoother settle tail (~a few ticks), during which
  // the crosshair is legitimately not yet on the block. At 6 the blacklist
  // tripped at 12 ticks â€” well inside a normal opening sweep â€” so start-of-
  // vein blocks got 60-tick-skipped and never mined. 14 -> blacklist at 28
  // ticks, clear of the worst-case ~24-tick sweep, while still catching a
  // genuinely unmineable target.
  internal const val STARE_RECOVERY_TICKS = 14

  // Block keys (BlockPos.asLong) â†’ gameTime tick when the skip expires. A
  // target gets added here when stuck-stare recovery gives up on it, so the
  // selector picks a different (next-closest) block instead of immediately
  // re-locking on the same unmineable one. Cleared on vein change / stop.
  internal val stuckMiningTargets = HashMap<Long, Long>()
  internal const val STUCK_TARGET_SKIP_TICKS = 60L

  // Aim quality tracker. Updated each render frame by the rotation debug
  // renderer with whether the crosshair is currently on the target block and
  // the distance between the rendered aim dot and the crosshair hit. Used to
  // surface a "how well are my settings tracking the precision point?" stat.
  internal var aimQualitySamples: Int = 0
  internal var aimQualityOnTargetSamples: Int = 0
  internal var aimQualityDistanceSum: Double = 0.0
  internal var aimQualityLastTarget: BlockPos? = null
  internal var aimQualityLastReportTick: Long = 0L
  internal const val AIM_QUALITY_REPORT_INTERVAL_TICKS = 200L
  internal var cachedPreviewTarget: BlockPos? = null
  internal var previewCacheTick = -1L
  internal var lastPruneTick = 0L
  // Last tick we force-refreshed the vein (prune+grow) on-demand because the
  // selector was about to path away / give up. Throttled so genuine long
  // travel doesn't re-flood the vein every tick.
  internal var lastForcedRefreshTick = -1L
  internal var miningUsesPrecisionPoint = false
  internal val precisionRolls = HashMap<Long, Boolean>()
  internal var precisionPointChanceSnapshot = precisionPointChance.value
  internal var startedPath = false
  internal var lastPathStartTick = 0L
  internal var approachTarget: BlockPos? = null
  // Sticky "push mode" target: once a short out-of-range block is being closed
  // by the deterministic nudge, keep nudging it (instead of flip-flopping back
  // to the native pather every tick at the push/native range boundary).
  internal var pushModeTarget: BlockPos? = null
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
  internal const val PUSH_EXTRA = 3.0           // within mineRange+this, close the gap with the deterministic nudge, not the native pather
  internal const val REFRESH_BEFORE_PATH_TICKS = 4L  // min ticks between on-demand vein refreshes before a path/stop decision
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

    tickAbilityActivation()
    tickDrillProbe()
    tickAimQualityReport()

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
      // Probe the held drill class only when auto abilities are enabled. This
      // avoids an unexpected right-click on manual macro start.
      detectedDrillAbility = null
      if (useMiningAbility.value) {
        awaitingDrillProbeResponse = true
        drillProbePendingTick = level.gameTime + 2L
      } else {
        awaitingDrillProbeResponse = false
        drillProbePendingTick = -1L
      }
      val selections = resolveTypeSelections()
      if (selections.isEmpty()) {
        ChatUtils.sendMessage("Mining macro: no block types selected, cannot start.")
        enabled.value = false
        wasEnabled = false
        return
      }
      // Automation start (RoutesModule, etc.) sets automationScanAnchor and
      // is trusted â€” skip manual preconditions and run the same scan flow as
      // before. Manual starts validate: either you have a route loaded and
      // you're standing near one of its mine points, or you have no route
      // and you're standing in/next to a vein. Anything else refuses to
      // start with a clear message rather than silently spinning.
      if (automationScanAnchor != null) {
        if (!immediateStartScan(level, player, selections)) {
          ChatUtils.sendMessage("Mining macro: scanning for vein...")
        }
      } else if (!validateManualStartPreconditions(level, player, selections)) {
        enabled.value = false
        wasEnabled = false
        return
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
        if (cmd != null) {
          cmd.applyToPlayer()
        } else {
          // Transient null (PLANNING / ARRIVED-but-still-out-of-range). Do NOT
          // zero inputs and freeze. If we still owe a *short* approach to an
          // out-of-range block, nudge the remaining gap; only clear when
          // genuinely idle. Bounded to the push zone so long-range PLANNING
          // keeps its original clear-and-wait behavior (no straight-line
          // wall-bonk before a long path solves).
          val at = approachTarget
          val d2 = if (at != null) mineReachDistanceSq(player, at) else 0.0
          val mr = mineRange.value
          val pr = mr + PUSH_EXTRA
          if (at != null && d2 > mr * mr && d2 <= pr * pr) {
            nudgeToward(player, at)
          } else {
            MovementManager.clearForcedMovement()
          }
        }
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
      // Re-flood after pruning: the mined shell exposes the enclosed core
      // (interior Hot Mithril etc.) which buildVein could not capture at scan
      // time. Bounded by maxVeinBlocks + anchor radius so it can't run away.
      growVein(level, player, vein)
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
        PhantomRotation.blockController.cancel()
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

    var target = selectMineTarget(level, player, vein)
    // Prefer-close-before-path / no-stale-path: prune+grow only run every 20t,
    // so when the cheap pick (an in-range block) fails, the vein set is stale
    // â€” a block we just mined may still be in it (target gone) and close ore
    // the mine just exposed isn't in it yet. Deciding to path away or give up
    // on that stale set is exactly what makes the macro abandon nearby ore,
    // stall, and stand still. So the moment the pick is null or out of range,
    // refresh ore state on-demand and re-select once before committing to a
    // path/stop. Throttled so genuine long travel doesn't re-flood per tick.
    if ((target == null ||
        mineReachDistanceSq(player, target) > mineRange.value * mineRange.value) &&
      gameTime - lastForcedRefreshTick >= REFRESH_BEFORE_PATH_TICKS
    ) {
      lastForcedRefreshTick = gameTime
      pruneVein(level, player, vein)
      growVein(level, player, vein)
      lastPruneTick = gameTime
      sanitizeActiveTargets(level, player, vein)
      target = selectMineTarget(level, player, vein)
    }
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
    val distSq = mineReachDistanceSq(player, target)
    val inRange = distSq <= mineRange.value * mineRange.value

    if (inRange) {
      // Crosshair-first mining: mine whatever valid vein block the crosshair is on.
      // This prevents dig packets firing at bedrock while the camera is still rotating.
      // Use a fresh raycast off the *current* player rotation rather than mc.hitResult
      // â€” mc.hitResult is computed once per frame against last-frame rotation, so the
      // tick after the rotation controller's smoother converges it can still report
      // the pre-rotation hit (bedrock/adjacent block), making the macro stare at a
      // perfectly-aimed vein block without ever pressing attack.
      val reach = (mineRange.value + 0.5).coerceAtLeast(4.5)
      val viewVec = player.getViewVector(1.0f)
      val from = player.eyePosition
      val to = from.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach)
      val freshHit = level.clip(
        net.minecraft.world.level.ClipContext(
          from,
          to,
          net.minecraft.world.level.ClipContext.Block.OUTLINE,
          net.minecraft.world.level.ClipContext.Fluid.NONE,
          player,
        )
      )
      val hitPos = if (freshHit.type == HitResult.Type.BLOCK) freshHit.blockPos else null
      val crosshairVeinBlock = hitPos?.takeIf { pos ->
        vein.blocks.contains(pos) &&
          isMineableTarget(level, player, pos, vein.targetIds) &&
          mineReachDistanceSq(player, pos) <= mineRange.value * mineRange.value
      }

      if (crosshairVeinBlock != null) {
        currentTarget = crosshairVeinBlock
        stareTarget = null
        stareTicks = 0
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
        // Not on a vein block yet Ã¢â‚¬â€ release attack and keep rotating toward selected target.
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
        // Track stuck-stare: in-range + selected target but crosshair never
        // lands on a vein block. After enough ticks, fall back from precision
        // point to block center so the crosshair pick can resolve cleanly.
        // If center still has no real LOS from this eye position, the block
        // is occluded â€” drop the target so the selector can re-run (and the
        // out-of-range branch can move us to a spot with a clear LOS).
        if (stareTarget == target) {
          stareTicks++
        } else {
          stareTarget = target
          stareTicks = 1
        }
        // Hard-give-up: if we've been aimed at the same target this long
        // without the crosshair ever landing on a vein block, the chosen
        // visible aim point is not actually mineable from this position
        // (smoothing residual lands the crosshair on adjacent geometry).
        // Drop the target AND blacklist it briefly so the next tick's
        // selector picks a different (next-closest) block â€” without the
        // skip the macro would just re-lock on the same unmineable target.
        if (stareTicks > STARE_RECOVERY_TICKS * 2) {
          stuckMiningTargets[target.asLong()] = level.gameTime + STUCK_TARGET_SKIP_TICKS
          currentTarget = null
          currentTargetNoLosTicks = 0
          stareTarget = null
          stareTicks = 0
          frameRotTarget = null
          setAimRenderTarget(null, null)
          return
        }
        val forceCenter = stareTicks > STARE_RECOVERY_TICKS
        val visibleFacePoint: Vec3? = if (forceCenter) {
          findVisibleAimPoint(level, player, player.eyePosition, target)
        } else null
        if (forceCenter && visibleFacePoint == null) {
          // Truly occluded from this eye position â€” abandon. Next tick the
          // selector picks a visible block or the out-of-range branch fires
          // movement to reposition. Crucially this no longer falls back to
          // the geometric block center, which itself could be inside a wall.
          PhantomRotation.blockController.setPrecisionPoint(null)
          PhantomRotation.blockController.cancel()
          frameRotTarget = null
          frameRotPrevBlock = null
          currentTarget = null
          currentTargetNoLosTicks = 0
          stareTarget = null
          stareTicks = 0
          setAimRenderTarget(null, null)
          return
        }
        val resolvedAim = if (forceCenter && visibleFacePoint != null) {
          // Recovery: drop precision and aim at the actual visible-face point
          // findVisibleAimPoint chose. This is guaranteed to be a face the
          // raycast can hit, unlike the block's geometric center.
          AimTarget(visibleFacePoint, false)
        } else {
          resolveMiningAimPoint(player, target)
        }
        // resolveMiningAimPoint can return the block's geometric center as a
        // last-resort fallback (Warp.kt:191) when no visible face is found,
        // or a precision/crosshair point that is no longer reachable from the
        // current eye position. Re-probe with findVisibleAimPoint instead of
        // bailing out â€” bailing here cancels the in-progress rotation and the
        // same target gets reselected next tick, producing the "selects target
        // but freezes" symptom.
        val aim = if (canSeeAimPoint(level, player, player.eyePosition, resolvedAim.point, target)) {
          resolvedAim
        } else {
          val recoveryPoint = findVisibleAimPoint(level, player, player.eyePosition, target)
          if (recoveryPoint != null) {
            AimTarget(recoveryPoint, false)
          } else {
            // Truly occluded from current eye position. Drop the target so the
            // next tick's selector picks a visible block (and the out-of-range
            // branch can move us if needed). Don't cancel the controller â€”
            // killing it mid-sweep was the freeze-causing behavior.
            frameRotTarget = null
            currentTarget = null
            currentTargetNoLosTicks = 0
            stareTarget = null
            stareTicks = 0
            setAimRenderTarget(null, null)
            return
          }
        }
        val precisionRotScale =
          if (aim.usesPrecisionPoint) (precisionPointRotationSpeed.value / 100.0).coerceAtLeast(0.1)
          else 1.0
        // Drive the block-to-block controller on every new target. Duration is scaled
        // by angular distance: short hops finish in ~5 ticks, long hops up to ~16.
        // Also re-fire when forceCenter is true so the stare recovery actually
        // re-aims to the center of the same target.
        if (target != frameRotPrevBlock || forceCenter) {
          val initRot = AngleUtils.getRotation(aim.point)
          val initYaw = abs(AngleUtils.getRotationDelta(player.yRot, initRot.yaw))
          val initPitch = abs(initRot.pitch - player.xRot)
          val initialDist = maxOf(initYaw, initPitch).coerceAtLeast(1f)
          frameRotInitialDist = initialDist
          // Longer base duration + sine in/out gives the camera a clear
          // acceleration in, sweep, and decel out â€” the "deliberate, not
          // robotic" feel the spec asks for.
          val baseDuration = (initialDist / 30f * 3f).toInt().coerceIn(3, 12)
          val scaledDuration = (baseDuration / precisionRotScale.coerceAtLeast(0.1))
            .toInt().coerceIn(3, 18)
          if (forceCenter) {
            // Stare-recovery re-aim at the *same* block: a deliberate cold
            // ease-in is correct here, not a momentum-carrying chain.
            val fromBlock = frameRotPrevBlock ?: target
            PhantomRotation.blockController.rotate(
              BlockRotationRequest(
                fromBlock = fromBlock,
                toBlock = target,
                durationTicks = scaledDuration,
                useFromBlockAsStartRotation = false,
                maxDegreesPerTick = 12f,
                easing = RotationEasingType.EASE_IN_OUT_SINE,
                toAimPoint = aim.point,
              )
            )
          } else {
            // New vein block: chain off the in-flight segment so the camera
            // carries momentum into it (no inter-block microstop).
            PhantomRotation.blockController.chainTo(target, aim.point, scaledDuration)
          }
          frameRotPrevBlock = target
          frameRotLastNs = 0L
        }
        if (aim.usesPrecisionPoint) {
          PhantomRotation.blockController.setPrecisionPoint(aim.point)
        } else {
          PhantomRotation.blockController.setPrecisionPoint(null)
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
      // Push zone: target is only a few blocks out of mining range. The native
      // pather is unreliable here â€” it flaps PLANNING/ARRIVED, returns a null
      // command, and the early path-tick then clearForcedMovement()s, so the
      // path is "generated" but the player never moves and never mines. Drive
      // the deterministic nudge instead. Gated ONLY on the anchor bound + no
      // blocking player â€” explicitly NOT on stepToNearbyBlocks: that toggle
      // decides whether to chase nearby blocks at all, not how to close a tiny
      // committed gap. Sticky via pushModeTarget so a block hovering at the
      // edge can't flip-flop between nudge and native every tick.
      val pushRange = mineRange.value + PUSH_EXTRA
      val inPushZone = distSq <= pushRange * pushRange || pushModeTarget == target
      if (blockingPlayer == null && canStepToNearbyTarget(player, target) && inPushZone) {
        if (startedPath && nativeActive()) {
          nativeStop()
        }
        startedPath = false
        lastPathTarget = null
        resetApproachTracking()
        pushModeTarget = target
        nudgeTowardApproach(level, player, target)
        focusApproachTarget(player, target)
        return
      }
      // Not closing a short gap this tick â€” drop any stale push stickiness so a
      // later far target doesn't get pinned to the nudge.
      pushModeTarget = null
        // V5 rotates toward the approach target while moving toward it. Drive
        // the rotation controller for every movement arm (the pathfinding arms
        // used to call only moveToward, so the camera never turned to the ore
        // â€” "chooses blocks and doesn't turn"). frameRotPrevBlock-guarded
        // inside focusApproachTarget, so re-calling it is cheap.
        focusApproachTarget(player, target)
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
          }
        usePathfinding.value -> {
          moveToward(level, player, target, avoidPlayer = blockingPlayer)
        }
        else -> {
          // V5 always walks toward out-of-range ore (handleVeinMovement). When
          // neither the native pathfinder nor the short-step nudge is driving
          // us, fall back to a direct approach nudge instead of standing still
          // (the old clearForcedMovement no-op was the "won't walk to the ore"
          // symptom). canStepToNearbyTarget already bounds the target to the
          // vein anchor radius, so this can't wander off. (Rotation is driven
          // by the hoisted focusApproachTarget above.)
          nudgeTowardApproach(level, player, target, blockingPlayer)
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

    handleAbilityChat(message)
  }

  private fun handleAbilityChat(message: String) {
    // Drill-class probe response always runs (independent of useMiningAbility),
    // since detecting the class is useful even with auto-activation disabled.
    PICKAXE_ABILITY_REGEX.find(message)?.let { match ->
      val ability = match.groupValues[1].trim().split(' ').joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
      if (awaitingDrillProbeResponse || enabled.value) {
        if (detectedDrillAbility != ability) {
          detectedDrillAbility = ability
          if (awaitingDrillProbeResponse) {
            ChatUtils.sendMessage("Mining macro: detected drill class â†’ $ability")
          }
        }
        awaitingDrillProbeResponse = false
      }
      // Activation confirm: clear available flag for whichever ability fired.
      when (ability.lowercase()) {
        "pickobulus" -> pickobulusAvailable = false
        "mining speed boost" -> {
          miningSpeedBoostAvailable = false
          miningSpeedBoostActive = true
        }
        "maniac miner" -> {
          maniacMinerAvailable = false
          maniacMinerActive = true
        }
        "tunnel vision" -> {
          tunnelVisionAvailable = false
          tunnelVisionActive = true
        }
        "sheer force" -> {
          sheerForceAvailable = false
          sheerForceActive = true
        }
        "gemstone infusion" -> {
          gemstoneInfusionAvailable = false
          gemstoneInfusionActive = true
        }
      }
      // Server confirmed an activation â€” close out any in-flight attempt so
      // the retry loop doesn't fire again. Matches by name when one is in
      // flight; clears unconditionally otherwise (manual user activations).
      if (pendingAbilityName != null) {
        ChatUtils.sendDebug("Mining Ability", "Mining ability successful â€” $ability.")
      }
      clearAbilityAttempt()
    }

    // Buff expiry â€” runs regardless of useMiningAbility so state stays correct.
    if (message.contains("mining speed boost has expired")) miningSpeedBoostActive = false
    if (message.contains("maniac miner has expired")) maniacMinerActive = false
    if (message.contains("tunnel vision has expired")) tunnelVisionActive = false
    if (message.contains("sheer force has expired")) sheerForceActive = false
    if (message.contains("gemstone infusion has expired")) gemstoneInfusionActive = false

    if (!enabled.value || !useMiningAbility.value) return

    // "X is now available!" â€” ability came off cooldown.
    when {
      message.contains("pickobulus is now available") -> {
        pickobulusAvailable = true
        if (usePickobulus.value) queueAbilityActivation("Pickobulus")
      }
      message.contains("mining speed boost is now available") -> {
        miningSpeedBoostAvailable = true
        if (useMiningSpeedBoost.value) queueAbilityActivation("Mining Speed Boost")
      }
      message.contains("maniac miner is now available") -> {
        maniacMinerAvailable = true
        if (useManiacMiner.value) queueAbilityActivation("Maniac Miner")
      }
      message.contains("tunnel vision is now available") -> {
        tunnelVisionAvailable = true
        if (useTunnelVision.value) queueAbilityActivation("Tunnel Vision")
      }
      message.contains("sheer force is now available") -> {
        sheerForceAvailable = true
        if (useSheerForce.value) queueAbilityActivation("Sheer Force")
      }
      message.contains("gemstone infusion is now available") -> {
        gemstoneInfusionAvailable = true
        if (useGemstoneInfusion.value) queueAbilityActivation("Gemstone Infusion")
      }
    }
  }

  private fun queueAbilityActivation(name: String) {
    val level = mc.level ?: return
    val delay = abilityActivationDelay.value.toLong().coerceAtLeast(0L)
    pendingAbilityName = name
    pendingAbilityActivationTick = level.gameTime + delay
    awaitingAbilityResponse = false
    abilityResponseDeadlineTick = -1L
    abilityRetryCount = 0
  }

  /**
   * Fires the one-shot right-click probe used on macro start to detect the
   * held drill class via its "You used your X Pickaxe Ability!" reply. Pending
   * tick is set in onTick when wasEnabled flips.
   */
  internal fun tickDrillProbe() {
    if (drillProbePendingTick < 0L) return
    val level = mc.level ?: return
    if (level.gameTime < drillProbePendingTick) return
    drillProbePendingTick = -1L
    if (!enabled.value || !useMiningAbility.value || mc.screen != null || !holdingMiningTool()) {
      awaitingDrillProbeResponse = false
      return
    }
    mc.options.keyUse?.setDown(true)
    mc.execute { mc.options.keyUse?.setDown(false) }
  }

  /**
   * Fires the right-click for a queued ability and tracks the response. If the
   * server doesn't reply with "You used your X Pickaxe Ability!" inside 2 s
   * (40 ticks), the click is repeated up to [ABILITY_MAX_RETRIES] times. The
   * gradient debug chat keeps the operator informed without spamming.
   */
  internal fun tickAbilityActivation() {
    val level = mc.level ?: return
    val pending = pendingAbilityName ?: return

    if (!enabled.value || !useMiningAbility.value || mc.screen != null) {
      clearAbilityAttempt()
      return
    }

    if (!holdingMiningTool()) {
      ChatUtils.sendDebug("Mining Ability", "Skipped $pending - no mining tool is selected.")
      clearAbilityAttempt()
      return
    }

    if (currentVein == null && currentTarget == null && miningOnTarget == null) {
      ChatUtils.sendDebug("Mining Ability", "Skipped $pending - macro is not mining a vein.")
      clearAbilityAttempt()
      return
    }

    // Initial fire â€” scheduled delay has elapsed and we're not yet awaiting.
    if (!awaitingAbilityResponse && level.gameTime >= pendingAbilityActivationTick) {
      fireAbilityRightClick(pending)
      awaitingAbilityResponse = true
      abilityResponseDeadlineTick = level.gameTime + ABILITY_RESPONSE_TIMEOUT_TICKS
      return
    }

    // Retry â€” fired earlier but never saw the confirmation chat.
    if (awaitingAbilityResponse && level.gameTime >= abilityResponseDeadlineTick) {
      if (abilityRetryCount >= ABILITY_MAX_RETRIES) {
        ChatUtils.sendDebug("Mining Ability", "Mining ability failed â€” $pending after ${abilityRetryCount + 1} attempts.")
        clearAbilityAttempt()
        return
      }
      abilityRetryCount++
      ChatUtils.sendDebug("Mining Ability", "No response - retrying $pending (attempt ${abilityRetryCount + 1}).")
      fireAbilityRightClick(pending)
      abilityResponseDeadlineTick = level.gameTime + ABILITY_RESPONSE_TIMEOUT_TICKS
    }
  }

  private fun fireAbilityRightClick(name: String) {
    ChatUtils.sendDebug("Mining Ability", "Attempting to use $name...")
    mc.options.keyUse?.setDown(true)
    mc.execute { mc.options.keyUse?.setDown(false) }
  }

  private fun holdingMiningTool(): Boolean {
    val player = mc.player ?: return false
    val slot = player.inventory.selectedSlot
    if (slot !in 0..8) return false
    val stack = player.inventory.getItem(slot)
    if (stack.isEmpty) return false
    val name = stack.hoverName.string
    return name.contains("Drill", ignoreCase = true) ||
      name.contains("Gauntlet", ignoreCase = true) ||
      name.contains("Pickaxe", ignoreCase = true)
  }

  private fun clearAbilityAttempt() {
    pendingAbilityName = null
    pendingAbilityActivationTick = -1L
    awaitingAbilityResponse = false
    abilityResponseDeadlineTick = -1L
    abilityRetryCount = 0
  }

  /**
   * Called from the rotation debug renderer once per frame. Accumulates a
   * rolling sample of "is the crosshair on the target block?" and the distance
   * between the rendered aim dot and the crosshair hit. Cleared when the
   * target changes so the stats describe the *current* block, not the session.
   */
  fun recordAimQualitySample(
    targetBlock: BlockPos?,
    cursorOnTarget: Boolean,
    cursorPoint: Vec3,
    aimPoint: Vec3,
  ) {
    if (targetBlock == null) return
    if (targetBlock != aimQualityLastTarget) {
      aimQualityLastTarget = targetBlock
      aimQualitySamples = 0
      aimQualityOnTargetSamples = 0
      aimQualityDistanceSum = 0.0
    }
    aimQualitySamples++
    if (cursorOnTarget) aimQualityOnTargetSamples++
    val dx = cursorPoint.x - aimPoint.x
    val dy = cursorPoint.y - aimPoint.y
    val dz = cursorPoint.z - aimPoint.z
    aimQualityDistanceSum += kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
  }

  /** Returns the on-target hit rate of the current target as a 0..1 fraction. */
  fun aimQualityOnTargetRate(): Double {
    if (aimQualitySamples == 0) return 0.0
    return aimQualityOnTargetSamples.toDouble() / aimQualitySamples
  }

  /** Returns the average aim-dot-to-cursor distance (blocks) for the current target. */
  fun aimQualityAverageOffset(): Double {
    if (aimQualitySamples == 0) return 0.0
    return aimQualityDistanceSum / aimQualitySamples
  }

  /** One-line gradient debug readout â€” fires every 10 s while mining is active. */
  internal fun tickAimQualityReport() {
    if (!enabled.value) return
    val level = mc.level ?: return
    if (miningOnTarget == null) return
    if (level.gameTime - aimQualityLastReportTick < AIM_QUALITY_REPORT_INTERVAL_TICKS) return
    if (aimQualitySamples < 20) return
    aimQualityLastReportTick = level.gameTime
    val pct = (aimQualityOnTargetRate() * 100.0).toInt()
    val avgPx = (aimQualityAverageOffset() * 100.0).toInt() / 100.0
    ChatUtils.sendDebug("Aim Quality", "On-target: $pct%   Avg offset: ${avgPx}b   Samples: $aimQualitySamples")
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
        val fallbackWithAim = if (playerNow != null && levelNow != null && vein.blocks.isNotEmpty()) {
          val eye = playerNow.eyePosition
          vein.blocks
            .asSequence()
            .filter { isMineableTarget(levelNow, playerNow, it, vein.targetIds) }
            .mapNotNull { pos ->
              val aim = findVisibleAimPoint(levelNow, playerNow, eye, pos) ?: return@mapNotNull null
              pos to aim
            }
            .minByOrNull { (pos, _) -> miningTargetScore(playerNow, pos) }
        } else null
        PhantomRotation.blockController.setFallbackBlock(fallbackWithAim?.first)
        if (fallbackWithAim != null) {
          val (fallback, fallbackAim) = fallbackWithAim
          PhantomRotation.blockController.rotate(
            BlockRotationRequest(
              fromBlock = event.pos,
              toBlock = fallback,
              durationTicks = 5,
              useFromBlockAsStartRotation = false,
              maxDegreesPerTick = 12f,
              easing = RotationEasingType.EASE_IN_OUT_SINE,
              toAimPoint = fallbackAim,
            )
          )
          frameRotPrevBlock = fallback
          frameRotTarget = null
        } else {
          PhantomRotation.blockController.cancel()
          frameRotPrevBlock = null
          frameRotTarget = null
        }
      }
    } else if (!vein.blocks.contains(event.pos)) {
      // Block refreshed/respawned back to a target ore - re-add it to the active vein.
      vein.blocks.add(event.pos)
    }
  }

  // Rotation is now driven by PhantomRotation.blockController on TickEvent.End.
  // This handler just clears the frame-rotation bookkeeping when there is no
  // target, so frameRotPrevBlock resets correctly between veins.
  @SubscribeEvent
  fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    if (frameRotTarget == null) {
      frameRotLastNs = 0L
      frameRotPrevBlock = null
    }
  }

  /**
   * Drive the rotation controller toward a freshly (re)selected mining target.
   * startMining only set the legacy frameRotTarget, which no renderer consumes,
   * so the crosshair-first path pressed attack but never turned onto the block
   * ("swings but doesn't turn"). Mirrors the acquisition-path rotate request
   * and is frameRotPrevBlock-gated so it isn't re-issued every mining tick.
   */
  internal fun driveMiningRotation(
    player: Player,
    target: BlockPos,
    aimPoint: Vec3,
    precisionRotScale: Double,
  ) {
    if (target == frameRotPrevBlock) return
    val initRot = AngleUtils.getRotation(aimPoint)
    val initYaw = abs(AngleUtils.getRotationDelta(player.yRot, initRot.yaw))
    val initPitch = abs(initRot.pitch - player.xRot)
    val initialDist = maxOf(initYaw, initPitch).coerceAtLeast(1f)
    frameRotInitialDist = initialDist
    val baseDuration = (initialDist / 30f * 3f).toInt().coerceIn(3, 12)
    val scaledDuration = (baseDuration / precisionRotScale.coerceAtLeast(0.1))
      .toInt().coerceIn(3, 18)
    // Chain off the in-flight segment so the camera carries momentum block to
    // block (no decel-to-stop + re-accel "microstop"). chainTo cold-starts
    // with accel-in easing on the first block of a vein automatically.
    PhantomRotation.blockController.chainTo(target, aimPoint, scaledDuration)
    frameRotPrevBlock = target
    frameRotLastNs = 0L
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
    val highlightBlocks = collectHighlightBlocks(level, player, active, preview)
    val vein = currentVein
    OverlayRenderEngine.clearTag(OVERLAY_TAG)
    val nothingToRender =
      active == null && preview == null && returnPos == null && highlightBlocks.isEmpty() && vein == null
    if (nothingToRender && !highlightBedrock.value) return

    val activeFill = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0x44)
    val activeOutline = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0xFF)
    val previewOutline = OverlayRenderEngine.Color(0xFF, 0xD8, 0x4C, 0xFF)
    val inRangeOutline = OverlayRenderEngine.Color(0x2D, 0xE2, 0xFF, 0xB8)
    val pathTargetOutline = OverlayRenderEngine.Color(0xFF, 0xA8, 0x34, 0xB8)
    val returnOutline = OverlayRenderEngine.Color(0x6E, 0xEA, 0x92, 0xFF)
    renderBlockOutlines(level, highlightBlocks.pathTargets, pathTargetOutline, 1.15f)
    renderBlockOutlines(level, highlightBlocks.inRangeTargets, inRangeOutline, 1.45f)

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
    if (highlightBedrock.value) {
      renderBedrockHighlight(level, player)
    }
  }

  /**
   * Outlines every block within [highlightBedrockRadius] of the player whose
   * current world state is in [MiningBlockRegistry.PERMANENT_LOS_OCCLUDERS] or
   * is flagged blacklisted by the registry. Reads block state live from
   * [level.getBlockState] each render frame â€” no cache â€” so a block that's
   * actually been mined will stop being highlighted the same frame the world
   * sees the update. If a highlighted block looks like it should be air or
   * ore, the world packet for that change hasn't arrived yet (or the macro is
   * holding a stale reference somewhere else worth chasing down).
   */
  private fun renderBedrockHighlight(
    level: net.minecraft.world.level.Level,
    player: Player,
  ) {
    val radius = highlightBedrockRadius.value.toInt().coerceIn(2, 24)
    val baseX = player.blockX
    val baseY = player.blockY
    val baseZ = player.blockZ
    val outline = OverlayRenderEngine.Color(0xFF, 0x55, 0x44, 0xD8)
    val fill = OverlayRenderEngine.Color(0xFF, 0x55, 0x44, 0x3A)
    val cursor = BlockPos.MutableBlockPos()
    val vertical = (radius / 2).coerceAtLeast(2)
    for (dy in -vertical..vertical) {
      for (dx in -radius..radius) {
        for (dz in -radius..radius) {
          cursor.set(baseX + dx, baseY + dy, baseZ + dz)
          val state = level.getBlockState(cursor)
          if (state.isAir) continue
          val id = BuiltInRegistries.BLOCK.getKey(state.block).toString()
          val flagged = id in MiningBlockRegistry.PERMANENT_LOS_OCCLUDERS ||
            MiningBlockRegistry.isBlacklisted(id)
          if (!flagged) continue
          for (direction in Direction.values()) {
            if (isBedrockFaceExposed(level, cursor, direction)) {
              renderBedrockFace(level, cursor, direction, fill, outline)
            }
          }
        }
      }
    }
  }

  private fun isBedrockFaceExposed(
    level: net.minecraft.world.level.Level,
    pos: BlockPos,
    direction: Direction,
  ): Boolean {
    val neighbor = pos.relative(direction)
    val neighborState = level.getBlockState(neighbor)
    return neighborState.isAir || neighborState.getCollisionShape(level, neighbor).isEmpty
  }

  private fun renderBedrockFace(
    level: net.minecraft.world.level.Level,
    pos: BlockPos,
    direction: Direction,
    fill: OverlayRenderEngine.Color,
    outline: OverlayRenderEngine.Color,
  ) {
    val pad = 0.003
    val thickness = 0.012
    val x0 = pos.x.toDouble()
    val y0 = pos.y.toDouble()
    val z0 = pos.z.toDouble()
    val x1 = x0 + 1.0
    val y1 = y0 + 1.0
    val z1 = z0 + 1.0

    val faceBox = when (direction) {
      Direction.DOWN -> doubleArrayOf(x0, y0 - thickness, z0, x1, y0, z1)
      Direction.UP -> doubleArrayOf(x0, y1, z0, x1, y1 + thickness, z1)
      Direction.NORTH -> doubleArrayOf(x0, y0, z0 - thickness, x1, y1, z0)
      Direction.SOUTH -> doubleArrayOf(x0, y0, z1, x1, y1, z1 + thickness)
      Direction.WEST -> doubleArrayOf(x0 - thickness, y0, z0, x0, y1, z1)
      Direction.EAST -> doubleArrayOf(x1, y0, z0, x1 + thickness, y1, z1)
    }

    OverlayRenderEngine.addBox(
      level,
      faceBox[0],
      faceBox[1],
      faceBox[2],
      faceBox[3],
      faceBox[4],
      faceBox[5],
      fill,
      null,
      1.0f,
      1,
      OVERLAY_TAG,
      forceRender = true,
    )

    val corners = when (direction) {
      Direction.DOWN -> listOf(
        Vec3(x0, y0 - pad, z0),
        Vec3(x1, y0 - pad, z0),
        Vec3(x1, y0 - pad, z1),
        Vec3(x0, y0 - pad, z1),
      )
      Direction.UP -> listOf(
        Vec3(x0, y1 + pad, z0),
        Vec3(x1, y1 + pad, z0),
        Vec3(x1, y1 + pad, z1),
        Vec3(x0, y1 + pad, z1),
      )
      Direction.NORTH -> listOf(
        Vec3(x0, y0, z0 - pad),
        Vec3(x1, y0, z0 - pad),
        Vec3(x1, y1, z0 - pad),
        Vec3(x0, y1, z0 - pad),
      )
      Direction.SOUTH -> listOf(
        Vec3(x0, y0, z1 + pad),
        Vec3(x1, y0, z1 + pad),
        Vec3(x1, y1, z1 + pad),
        Vec3(x0, y1, z1 + pad),
      )
      Direction.WEST -> listOf(
        Vec3(x0 - pad, y0, z0),
        Vec3(x0 - pad, y0, z1),
        Vec3(x0 - pad, y1, z1),
        Vec3(x0 - pad, y1, z0),
      )
      Direction.EAST -> listOf(
        Vec3(x1 + pad, y0, z0),
        Vec3(x1 + pad, y0, z1),
        Vec3(x1 + pad, y1, z1),
        Vec3(x1 + pad, y1, z0),
      )
    }

    for (index in corners.indices) {
      val start = corners[index]
      val end = corners[(index + 1) % corners.size]
      OverlayRenderEngine.addLine(
        level,
        start.x,
        start.y,
        start.z,
        end.x,
        end.y,
        end.z,
        outline,
        1.6f,
        1,
        OVERLAY_TAG,
        forceRender = true,
      )
    }
  }


  internal fun setAimRenderTarget(block: BlockPos?, point: Vec3?) {
    aimRenderBlock = block
    aimRenderPoint = point
  }

  private data class HighlightBlocks(
    val inRangeTargets: Set<BlockPos>,
    val pathTargets: Set<BlockPos>,
  ) {
    fun isEmpty(): Boolean = inRangeTargets.isEmpty() && pathTargets.isEmpty()
  }

  private fun collectHighlightBlocks(
    level: net.minecraft.world.level.Level,
    player: Player,
    active: BlockPos?,
    preview: BlockPos?,
  ): HighlightBlocks {
    if (!highlightPossibleBlocks.value) return HighlightBlocks(emptySet(), emptySet())
    val vein = currentVein ?: return HighlightBlocks(emptySet(), emptySet())
    val limit = highlightPossibleLimit.value.toInt().coerceAtLeast(1)
    val inRange = LinkedHashSet<BlockPos>(minOf(limit, vein.blocks.size))
    val pathTargets = LinkedHashSet<BlockPos>(minOf(limit, vein.blocks.size))
    val rangeSq = mineRange.value * mineRange.value
    val now = level.gameTime

    fun canHighlight(pos: BlockPos): Boolean {
      if (!vein.blocks.contains(pos)) return false
      if (!isMineableTarget(level, player, pos, vein.targetIds)) return false
      val skipUntil = stuckMiningTargets[pos.asLong()]
      return skipUntil == null || skipUntil <= now
    }

    fun addCandidate(pos: BlockPos) {
      if (!canHighlight(pos)) return
      val targetSet = if (mineReachDistanceSq(player, pos) <= rangeSq) inRange else pathTargets
      if (inRange.size + pathTargets.size < limit || targetSet.contains(pos)) {
        targetSet.add(pos)
      }
    }

    // Active and preview first: these mirror the selector's current answer even
    // when the sorted debug list would otherwise be truncated by the limit.
    active?.let(::addCandidate)
    preview?.let(::addCandidate)

    val sorted = vein.blocks
      .asSequence()
      .filter { it != active && it != preview }
      .filter(::canHighlight)
      .sortedWith(compareBy<BlockPos> { mineReachDistanceSq(player, it) > rangeSq }
        .thenBy { highlightSortScore(player, it) })

    for (pos in sorted) {
      if (inRange.size + pathTargets.size >= limit) break
      addCandidate(pos)
    }

    return HighlightBlocks(inRange, pathTargets)
  }

  private fun highlightSortScore(player: Player, pos: BlockPos): Double {
    val dx = (pos.x + 0.5) - player.eyePosition.x
    val dy = (pos.y + 0.5) - player.eyePosition.y
    val dz = (pos.z + 0.5) - player.eyePosition.z
    val belowPenalty = if (pos.y < player.blockY - 1) 16.0 else 0.0
    return dx * dx + dy * dy + dz * dz + belowPenalty
  }

  private fun renderBlockOutlines(
    level: net.minecraft.world.level.Level,
    blocks: Set<BlockPos>,
    color: OverlayRenderEngine.Color,
    lineWidth: Float,
  ) {
    if (blocks.isEmpty()) return
    val pad = 0.006
    for (pos in blocks) {
      OverlayRenderEngine.addBox(
        level,
        pos.x - pad,
        pos.y - pad,
        pos.z - pad,
        pos.x + 1.0 + pad,
        pos.y + 1.0 + pad,
        pos.z + 1.0 + pad,
        null,
        color,
        lineWidth = lineWidth,
        durationTicks = 2,
        tag = OVERLAY_TAG,
        forceRender = true,
      )
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
    PhantomRotation.blockController.cancel()
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
    stuckMiningTargets.clear()
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
