package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.KeyBindSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.rotation.strategy.BezierTrackingRotationStrategy
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.AngleUtils
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.InventoryUtils
import org.phantom.api.util.TickScheduler
import org.phantom.api.util.getSkyblockId
import org.phantom.api.util.helper.KeyBind
import org.phantom.api.util.helper.Rotation
import org.phantom.api.util.player.MovementManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.etherwarp.EtherwarpLogic
import org.phantom.internal.pathfinding.OverlayRenderEngine
import org.phantom.internal.rotation.RotationsModule

object DianaMacroModule : Module("Diana Macro") {

    override val category = ModuleCategory.DIANA

    private val mc = Minecraft.getInstance()
    val isActive: Boolean get() = enabledSetting.value

    // -- State -----------------------------------------------------------------

    private enum class State {
        IDLE, ACTIVATING_SPADE, COLLECTING_PARTICLES, PATHFINDING, WARPING, DIGGING, COMBAT, WAITING
    }

    private var state = State.IDLE
    private var wasEnabled = false
    private var paused = false

    private var activatingTicksElapsed = 0
    private var collectTicksElapsed     = 0
    private var pathfindingTicksElapsed = 0
    private var digTicksElapsed         = 0
    private var digApproachTicks        = 0
    private var treasureDigStage        = 0
    private var treasureWaitTicks       = 0
    private var combatMoving            = false
    private var combatTicksElapsed      = 0
    private var targetLostTicks         = 0
    private var lastAttackTick          = 0
    private var smoothedCombatAim: Vec3? = null
    private var rareMobFocusPos: Vec3? = null
    private var rareMobFocusUntilMs = 0L
    private var combatIsRareMob = false
    private var ironEscapeTarget: Vec3? = null
    private var ironEscapeTicks = 0
    private var lastTravelDebugMs       = 0L
    private var waitTicksElapsed        = 0
    private var loopsCompleted          = 0
    private var lastStatus              = "Idle"
    private var lastPlanInfo            = "No plan"

    private var burrowPos: Vec3?   = null
    private var burrowType: DianaParticleTracker.BurrowType = DianaParticleTracker.BurrowType.UNKNOWN
    private var targetEntityId: Int = -1

    // -- Teleport-first hop executor state -------------------------------------
    private var planSubmitted = false
    private var hops: List<DianaTeleportPlanner.Hop> = emptyList()
    private var hopIndex = 0
    private var lastCastMs = 0L
    private var castCooldownMs = 280L
    private var aimStableSinceMs = 0L
    private var lastAimYaw = Float.NaN
    private var lastAimPitch = Float.NaN
    private var noProgressTicks = 0
    private var lastProgressDistSq = Double.MAX_VALUE
    // Fast per-airborne-hop watchdog. AOTV travels a fixed ~11 blocks along
    // the look vector, so each sky-chain hop has ~0.3–2 blocks of drift from
    // the planner's idealised landing once the player has fallen between casts;
    // a few hops of accumulated drift make `player.position().distanceTo(standPos) <= 2.0`
    // unreachable and the chain stalls. The slow no-progress watchdog (~1.75 s)
    // is far too late when you're falling 4+ blocks/tick. These fields detect
    // a single airborne hop failing to complete within ~300 ms and trigger an
    // immediate resetHopState() so the planner replans from the current
    // position before the chain drifts further.
    private var airHopTrackedIndex = -1
    private var airHopStartMs = 0L
    // Track which hopIndex we last cast for. AOTV's fixed-11-block travel can
    // land the player 2-4 blocks from the planner's standPos when the eye has
    // fallen between casts — the `<=2.0` proximity advance silently misses it,
    // the chain looks like it "didn't pass the node", and the off-course
    // watchdog only catches it 500 ms later. After firing, we advance on the
    // very next tick (the teleport is essentially instant) instead of waiting
    // for a proximity check that may never fire.
    private var castedHopIndex = -1
    private var castedAtMs = 0L
    private var castRestoreSlot = -1
    private var nativeWalkTarget: Vec3? = null
    private var nativeWalkRadius = 0.0
    private var pendingWarp: WarpPoint? = null
    private var warpTarget: Vec3? = null
    private var warpTicksElapsed = 0
    private var warpAttemptedFor: Vec3? = null
    private var preGuessMoveTarget: Vec3? = null

    // -- Settings --------------------------------------------------------------

    private val enabledSetting = CheckboxSetting("Enabled", "Start or stop the Diana macro.", false)
    private val toggleKeySetting = KeyBindSetting("Toggle Key", "Start or stop the Diana macro.", KeyBind(-1))
    private val pauseKeySetting = KeyBindSetting("Pause Key", "Pause or resume the Diana macro without disabling it.", KeyBind(-1))
    private val autoEnableHelperSetting = CheckboxSetting(
        "Auto Enable Helper", "Turn on Diana Helper while the macro is running.", true
    )
    private val pauseInMenusSetting = CheckboxSetting(
        "Pause In Menus", "Release movement and attacks while any screen is open.", true
    )
    private val stopOnDeathSetting = CheckboxSetting(
        "Stop On Death", "Disable the macro if you die or enter a death state.", true
    )
    private val maxLoopsSetting = SliderSetting(
        "Max Loops", "Stop after this many completed mob kills. 0 = unlimited.", 0.0, 0.0, 500.0, step = 1.0
    )
    private val maxBurrowsPerHourSetting = CheckboxSetting(
        "Max Burrows/Hour", "Prefer lower downtime and earlier transitions for faster Diana cycles.", true
    )
    private val useKnownBurrowSetting = CheckboxSetting(
        "Use Known Burrow", "Travel to an already detected helper burrow before using the spade again.", true
    )
    private val avoidIronBlocksSetting = CheckboxSetting(
        "Avoid Iron Blocks", "Step off iron blocks under your feet before continuing Diana actions.", true
    )

    private val autoSpadeSlotSetting = CheckboxSetting(
        "Auto Spade Slot", "Find a Diana spade in the hotbar automatically.", true
    )
    private val spadeSlotSetting = SliderSetting(
        "Spade Slot", "Hotbar slot of the Ancestral Spade (1-9).", 1.0, 1.0, 9.0, step = 1.0
    )
    private val autoWeaponSlotSetting = CheckboxSetting(
        "Auto Weapon Slot", "Find a likely weapon in the hotbar after a mob spawns.", false
    )
    private val weaponSlotSetting = SliderSetting(
        "Weapon Slot", "Hotbar slot of your main weapon (1-9).", 2.0, 1.0, 9.0, step = 1.0
    )
    private val collectDurationSetting = SliderSetting(
        "Collect Duration", "Max ticks to wait for burrow particles before retrying.", 40.0, 5.0, 60.0, step = 1.0
    )
    private val fastCollectDurationSetting = SliderSetting(
        "Fast Collect Ticks", "Particle wait cap while Max Burrows/Hour is enabled.", 18.0, 5.0, 40.0, step = 1.0
    )
    private val postKillWaitSetting = SliderSetting(
        "Post-Kill Wait", "Ticks to wait after kill before looping.", 80.0, 20.0, 200.0, step = 1.0
    )
    private val fastLoopWaitSetting = SliderSetting(
        "Fast Loop Wait", "Ticks to wait between burrows while Max Burrows/Hour is enabled.", 2.0, 0.0, 40.0, step = 1.0
    )
    private val travelTimeoutSetting = SliderSetting(
        "Travel Timeout", "Max ticks to travel to a burrow before retrying.", 600.0, 120.0, 1200.0, step = 20.0
    )
    private val digTimeoutSetting = SliderSetting(
        "Dig Timeout", "Max ticks to dig a mob/guess burrow before continuing.", 70.0, 20.0, 160.0, step = 5.0
    )
    private val digApproachTimeoutSetting = SliderSetting(
        "Dig Approach Timeout", "Max ticks to walk onto a burrow after travel.", 70.0, 20.0, 160.0, step = 5.0
    )
    private val digRadiusSetting = SliderSetting(
        "Dig Radius", "How close the macro must stand before digging a burrow.", 1.55, 1.0, 2.3, step = 0.05
    )
    private val fastTreasureDigTicksSetting = SliderSetting(
        "Fast Treasure Dig Ticks", "Use ticks before finishing non-mob burrows in Max Burrows/Hour mode.", 5.0, 3.0, 14.0, step = 1.0
    )
    private val treasureSecondDigDelaySetting = SliderSetting(
        "Treasure Second Dig Delay", "Ticks to wait before mining the same treasure burrow spot again.", 20.0, 5.0, 60.0, step = 1.0
    )
    private val stuckReplanTicksSetting = SliderSetting(
        "Stuck Replan Ticks", "Ticks without travel progress before replanning.", 80.0, 20.0, 200.0, step = 5.0
    )
    private val minParticlesSetting = SliderSetting(
        "Min Particles", "Minimum CRIT packets required before pathfinding to burrow.", 2.0, 1.0, 10.0, step = 1.0
    )
    private val preGuessMoveSetting = CheckboxSetting(
        "Pre-Guess Move", "After using the spade, start moving along the shovel beam direction while waiting for the guess.", true
    )
    private val preGuessMoveRangeSetting = SliderSetting(
        "Pre-Guess Move Range", "How far the shovel-beam movement target is projected before a real guess is available.", 28.0, 8.0, 70.0, step = 1.0
    )
    private val preGuessStopDistanceSetting = SliderSetting(
        "Pre-Guess Stop Distance", "Stop the early shovel-direction movement this far from the projected point.", 3.0, 1.0, 8.0, step = 0.5
    )
    private val aotvSlotSetting = SliderSetting(
        "AOTV Slot", "Hotbar slot of the Aspect of the Void used for travel (1-9). 0 = auto-detect.",
        0.0, 0.0, 9.0, step = 1.0
    )
    private val useEtherwarpSetting = CheckboxSetting(
        "Use Etherwarp", "Allow long sneak-teleport (etherwarp) hops while travelling.", true
    )
    private val useAotvSetting = CheckboxSetting(
        "Use Instant Transmission", "Allow short AOTV transmission hops while travelling.", true
    )
    private val walkFallbackSetting = CheckboxSetting(
        "Walk Fallback", "Walk directly to the burrow when AOTV is missing or the planner has no route.", true
    )
    private val closeGuessWalkDistanceSetting = SliderSetting(
        "Close Guess Walk Distance", "Walk to guesses within this many blocks instead of starting a teleport plan.", 18.0, 4.0, 60.0, step = 1.0
    )
    private val autoHubWarpSetting = CheckboxSetting(
        "Auto Hub Warp", "Use nearby hub warp spots before walking/AOTV when a far guess is much closer to a warp.", true
    )
    private val aerialChainSetting = CheckboxSetting(
        "Aerial AOTV Chain", "For far guesses with no good warp, chain upward Instant Transmission nodes toward the guess.", true
    )
    private val aerialChainMinDistanceSetting = SliderSetting(
        "Aerial Chain Min Distance", "Minimum guess distance before using the upward AOTV chain.", 55.0, 20.0, 180.0, step = 5.0
    )
    private val aerialChainNodesSetting = SliderSetting(
        "Aerial Chain Nodes", "Number of rapid Instant Transmission air nodes to generate toward a far guess.", 10.0, 3.0, 14.0, step = 1.0
    )
    private val aerialChainPitchSetting = SliderSetting(
        "Aerial Chain Pitch", "Upward pitch used for rapid Instant Transmission air chaining.", -18.0, -35.0, -4.0, step = 1.0
    )
    private val aerialCastCooldownSetting = SliderSetting(
        "Aerial Cast Cooldown", "Milliseconds between AOTV casts while air chaining.", 35.0, 0.0, 120.0, step = 5.0
    )
    private val smoothAimSetting = CheckboxSetting(
        "Smooth Aim", "Human-like Bezier rotation toward each hop (off = snap, for testing).", true
    )
    private val statusHudSetting = CheckboxSetting(
        "Status HUD", "Show the Diana macro state HUD.", true
    )
    private val showNodeMapSetting = CheckboxSetting(
        "Show Node Map", "Render the planned hop path (walk/AOTV/etherwarp) while travelling.", true
    )
    private val simplifyNodesSetting = CheckboxSetting(
        "Simplify Nodes", "Clean up duplicate/collinear generated Diana route nodes.", true
    )
    private val plannerGoalRadiusSetting = SliderSetting(
        "Planner Goal Radius", "How close generated nodes need to get to the burrow.", 2.0, 1.0, 4.0, step = 0.25
    )
    private val transmissionRangeSetting = SliderSetting(
        "Transmission Range", "Instant Transmission range used for generated Diana nodes.", 11.0, 6.0, 15.0, step = 0.5
    )
    private val plannerIterationsSetting = SliderSetting(
        "Planner Iterations", "Maximum native planner iterations for generated Diana nodes.", 16_000.0, 4_000.0, 18_000.0, step = 500.0
    )
    private val plannerNodesSetting = SliderSetting(
        "Planner Nodes", "Maximum native planner nodes for generated Diana routes.", 8_000.0, 2_000.0, 12_000.0, step = 500.0
    )
    private val travelDebugSetting = CheckboxSetting(
        "Travel Debug Chat", "Print planner results / fallbacks to chat.", true
    )
    private val aimSettleSetting = SliderSetting(
        "Aim Settle (ms)", "How long the aim must stay on-target before a teleport fires.",
        110.0, 0.0, 400.0, step = 10.0
    )
    private val castCdMinSetting = SliderSetting(
        "Cast Cooldown Min (ms)", "Minimum delay between teleport casts.", 240.0, 80.0, 600.0, step = 10.0
    )
    private val castCdMaxSetting = SliderSetting(
        "Cast Cooldown Max (ms)", "Maximum delay between teleport casts (randomised with min).",
        320.0, 80.0, 800.0, step = 10.0
    )
    private val combatMoveStartSetting = SliderSetting(
        "Combat Move Start", "Start walking toward a Diana mob past this distance.", 5.0, 2.0, 10.0, step = 0.25
    )
    private val combatMoveStopSetting = SliderSetting(
        "Combat Move Stop", "Stop walking once back inside this distance.", 3.0, 1.0, 8.0, step = 0.25
    )
    private val combatAttackRangeSetting = SliderSetting(
        "Combat Attack Range", "Attack Diana mobs within this distance.", 4.0, 2.0, 6.0, step = 0.25
    )
    private val combatSmoothAimSetting = CheckboxSetting(
        "Combat Smooth Aim", "Use Diana-specific smooth target tracking during combat.", true
    )
    private val combatAimSpeedSetting = SliderSetting(
        "Combat Aim Speed", "Max combat yaw degrees per frame before Bezier easing.", 8.5, 2.0, 22.0, step = 0.25
    )
    private val combatPitchSpeedSetting = SliderSetting(
        "Combat Pitch Speed", "Max combat pitch degrees per frame before Bezier easing.", 6.5, 2.0, 18.0, step = 0.25
    )
    private val combatAimSmoothingSetting = SliderSetting(
        "Combat Aim Smoothing", "How quickly the tracked aim point follows mob movement. Higher = snappier.", 0.46, 0.10, 1.0, step = 0.02
    )
    private val combatPredictTicksSetting = SliderSetting(
        "Combat Prediction", "Ticks of mob velocity to lead while aiming.", 1.25, 0.0, 4.0, step = 0.25
    )
    private val combatAimHeightSetting = SliderSetting(
        "Combat Aim Height", "Fraction of mob height to aim at.", 0.78, 0.35, 0.95, step = 0.01
    )
    private val combatAimToleranceSetting = SliderSetting(
        "Combat Aim Tolerance", "Max yaw/pitch error before the macro attacks.", 5.0, 1.0, 15.0, step = 0.25
    )
    private val combatAttackIntervalSetting = SliderSetting(
        "Combat Attack Interval", "Ticks between attack presses while fighting.", 2.0, 1.0, 10.0, step = 1.0
    )
    private val combatReacquireTicksSetting = SliderSetting(
        "Combat Reacquire Ticks", "Ticks to keep searching before treating a missing mob as killed.", 12.0, 1.0, 60.0, step = 1.0
    )
    private val combatTimeoutSetting = SliderSetting(
        "Combat Timeout", "Max ticks to spend on one Diana mob before continuing.", 300.0, 80.0, 900.0, step = 10.0
    )
    private val rareMobPrioritySetting = CheckboxSetting(
        "Rare Mob Priority", "Interrupt burrow loops to fight rare Diana mobs and shared rare mob waypoints.", true
    )
    private val cocoonHoldSetting = CheckboxSetting(
        "Cocoon Hold", "Stay anchored on rare mobs during cocoon/shield warnings instead of timing out.", true
    )
    private val nearbyBurrowRadiusSetting = SliderSetting(
        "Rare Nearby Burrows", "While waiting on a rare mob/cocoon, only do burrows within this many blocks.", 35.0, 10.0, 80.0, step = 1.0
    )
    private val rareMobFocusSecondsSetting = SliderSetting(
        "Rare Focus Seconds", "How long to keep the rare-mob burrow radius after seeing a rare mob.", 45.0, 10.0, 180.0, step = 5.0
    )
    private val mobSearchRangeSetting = SliderSetting(
        "Mob Search Range", "Search radius for spawned Diana mobs around the burrow.", 14.0, 6.0, 24.0, step = 1.0
    )

    private val spadeSlot get() = resolveSpadeSlot()
    private val weaponSlot get() = resolveWeaponSlot()

    private fun resolveSpadeSlot(): Int {
        val configured = spadeSlotSetting.value.toInt() - 1
        if (!autoSpadeSlotSetting.value) return configured
        val auto = InventoryUtils.findHotbarSlotMatching { isDianaSpade(it) }
        return if (auto in 0..8) auto else configured
    }

    private fun resolveWeaponSlot(): Int {
        val configured = weaponSlotSetting.value.toInt() - 1
        if (!autoWeaponSlotSetting.value) return configured
        val auto = InventoryUtils.findHotbarSlotMatching { isLikelyWeapon(it) }
        return if (auto in 0..8) auto else configured
    }

    private fun resolveAotvSlot(): Int {
        val configured = aotvSlotSetting.value.toInt()
        if (configured in 1..9) return configured - 1
        val auto = EtherwarpLogic.findEtherwarpHotbarSlot()
        return if (auto in 0..8) auto else -1
    }

    // -- Constants -------------------------------------------------------------

    private val DIANA_MOB_NAMES = listOf(
        "Siamese Lynxes",
        "Minos Hunter",
        "Minotaur",
        "Gaia Construct",
        "Minos Champion",
        "Minos Inquisitor",
        "Cretan Bull",
        "Harpy",
        "Sphinx",
        "King Minos",
        "Manticore",
    )
    private val RARE_MOB_NAMES = setOf("Minos Inquisitor", "Sphinx", "King Minos", "Manticore")
    private val DIANA_SPADE_IDS = setOf("ANCESTRAL_SPADE", "ANCESTRAL_SPADE_2", "DEIFIC_SPADE", "DWARVEN_METAL_DETECTOR")
    private val WEAPON_ID_HINTS = listOf(
        "SWORD", "DAGGER", "KATANA", "CLAYMORE", "BLADE", "AXE", "STAFF", "WAND", "SCYTHE", "BOW", "SHORTBOW"
    )
    private val WEAPON_NAME_HINTS = listOf(
        "sword", "dagger", "katana", "claymore", "blade", "axe", "staff", "wand", "scythe", "bow", "shortbow"
    )
    private data class WarpPoint(val command: String, val displayName: String, val pos: Vec3, val extraBlocks: Int)

    private val HUB_WARPS = listOf(
        WarpPoint("hub", "Hub", Vec3(-3.0, 70.0, -70.0), 0),
        WarpPoint("castle", "Castle", Vec3(-250.0, 130.0, 45.0), 15),
        WarpPoint("crypt", "Crypt", Vec3(-160.0, 61.0, -100.0), 20),
        WarpPoint("da", "Dark Auction", Vec3(91.0, 75.0, 173.0), 15),
        WarpPoint("museum", "Museum", Vec3(-75.0, 76.0, 80.0), 15),
        WarpPoint("wizard", "Wizard", Vec3(42.0, 122.0, 69.0), 20),
        WarpPoint("stonks", "Stonks", Vec3(51.0, 72.0, -52.0), 10),
        WarpPoint("taylor", "Taylor", Vec3(-24.0, 71.0, -39.0), 10),
    )
    private const val COMBAT_ROTATION_STEP_SCALE = 0.62
    private const val AIM_ERROR_DEG = 3.5f

    private val rotationStrategy = BezierTrackingRotationStrategy(
        yawStepSampler   = { (RotationsModule.sample(RotationsModule.combatYawStep.value)   * COMBAT_ROTATION_STEP_SCALE).toFloat() },
        pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
        curveInProvider  = { RotationsModule.bezierCurveIn.value.toFloat() },
        curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
        minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
    )

    // Sky-chain rotation: must keep up with a live target whose required pitch
    // shifts ~2-3°/tick while the player falls between airborne AOTV hops, yet
    // still look human (Bezier ease). The default rotationStrategy is combat
    // tuned (small step, minScale ~0.18) and slows to a crawl near the target,
    // so the gate never closes before the next fall tick moves it again.
    // Here: moderate per-frame cap and a soft eased curve so the camera swing
    // is visibly smooth, with a minScale floor that's high enough the ease-out
    // doesn't lose the moving target. A small snap closes the final fraction
    // of a degree without leaving precision-killing drift behind.
    private val airborneRotationStrategy = BezierTrackingRotationStrategy(
        maxYawStep = 14f,
        maxPitchStep = 14f,
        curveIn = 0.35f,
        curveOut = 0.78f,
        minScale = 0.42f,
        snapThreshold = 0.6f,
    )

    private val combatRotationStrategy = BezierTrackingRotationStrategy(
        yawStepSampler = {
            val base = combatAimSpeedSetting.value
            val capped = if (maxBurrowsPerHourSetting.value) base.coerceAtLeast(10.0) else base
            RotationsModule.sample(Pair(capped * 0.82, capped * 1.14)).toFloat()
        },
        pitchStepSampler = {
            val base = combatPitchSpeedSetting.value
            val capped = if (maxBurrowsPerHourSetting.value) base.coerceAtLeast(8.0) else base
            RotationsModule.sample(Pair(capped * 0.82, capped * 1.14)).toFloat()
        },
        curveInProvider = { 0.18f },
        curveOutProvider = { 0.92f },
        minScaleProvider = { 0.24f },
        snapThresholdProvider = { 0.0f },
    )

    // -- Init ------------------------------------------------------------------

    init {
        addSetting(
            enabledSetting, toggleKeySetting, pauseKeySetting, autoEnableHelperSetting,
            pauseInMenusSetting, stopOnDeathSetting, maxLoopsSetting, maxBurrowsPerHourSetting, useKnownBurrowSetting,
            avoidIronBlocksSetting,
            autoSpadeSlotSetting, spadeSlotSetting, autoWeaponSlotSetting, weaponSlotSetting, aotvSlotSetting,
            collectDurationSetting, fastCollectDurationSetting, postKillWaitSetting, fastLoopWaitSetting,
            travelTimeoutSetting, digTimeoutSetting, digApproachTimeoutSetting, digRadiusSetting,
            fastTreasureDigTicksSetting, treasureSecondDigDelaySetting, stuckReplanTicksSetting,
            minParticlesSetting, preGuessMoveSetting, preGuessMoveRangeSetting, preGuessStopDistanceSetting,
            useEtherwarpSetting, useAotvSetting, walkFallbackSetting,
            closeGuessWalkDistanceSetting, autoHubWarpSetting, aerialChainSetting,
            aerialChainMinDistanceSetting, aerialChainNodesSetting, aerialChainPitchSetting, aerialCastCooldownSetting,
            smoothAimSetting, statusHudSetting, showNodeMapSetting, travelDebugSetting,
            simplifyNodesSetting, plannerGoalRadiusSetting, transmissionRangeSetting,
            plannerIterationsSetting, plannerNodesSetting, aimSettleSetting,
            castCdMinSetting, castCdMaxSetting, combatMoveStartSetting,
            combatMoveStopSetting, combatAttackRangeSetting, combatSmoothAimSetting,
            combatAimSpeedSetting, combatPitchSpeedSetting, combatAimSmoothingSetting,
            combatPredictTicksSetting, combatAimHeightSetting, combatAimToleranceSetting,
            combatAttackIntervalSetting, combatReacquireTicksSetting, combatTimeoutSetting,
            rareMobPrioritySetting, cocoonHoldSetting, nearbyBurrowRadiusSetting, rareMobFocusSecondsSetting,
            mobSearchRangeSetting,
        )
        EventBus.register(this)
    }

    // -- Cleanup ---------------------------------------------------------------

    private fun cleanup() {
        NativePathfinder.stop()
        MovementManager.setMovementLock(false)
        RotationExecutor.stopRotating()
        state = State.IDLE
        paused = false
        lastStatus = "Idle"
        lastPlanInfo = "No plan"
        loopsCompleted = 0
        burrowPos = null
        burrowType = DianaParticleTracker.BurrowType.UNKNOWN
        targetEntityId = -1
        activatingTicksElapsed = 0
        collectTicksElapsed = 0
        pathfindingTicksElapsed = 0
        digTicksElapsed = 0
        digApproachTicks = 0
        treasureDigStage = 0
        treasureWaitTicks = 0
        waitTicksElapsed = 0
        combatMoving = false
        combatTicksElapsed = 0
        targetLostTicks = 0
        lastAttackTick = 0
        smoothedCombatAim = null
        rareMobFocusPos = null
        rareMobFocusUntilMs = 0L
        combatIsRareMob = false
        ironEscapeTarget = null
        ironEscapeTicks = 0
        releaseTravelInputs()
        DianaTeleportPlanner.cancel()
        resetHopState()
        pendingWarp = null
        warpTarget = null
        warpTicksElapsed = 0
        warpAttemptedFor = null
        preGuessMoveTarget = null
    }

    private fun stop() { cleanup() }

    // -- Teleport-first hop executor helpers -----------------------------------

    private fun resetHopState() {
        planSubmitted = false
        hops = emptyList()
        hopIndex = 0
        noProgressTicks = 0
        lastProgressDistSq = Double.MAX_VALUE
        airHopTrackedIndex = -1
        airHopStartMs = 0L
        castedHopIndex = -1
        castedAtMs = 0L
        resetAimSettle()
    }

    /**
     * For the direct-etherwarp shortcut: pick a SOLID block ~2 blocks away
     * from the burrow with clear stand on top, so the player lands on it
     * and is then a short walk from the burrow. Prefers the side closest to
     * the player so the etherwarp aim doesn't sweep across the burrow itself.
     * Returns null if no valid stand block exists or the player is out of
     * etherwarp reach of every candidate (fall through to planner).
     */
    private fun findEtherwarpStandBlock(
        level: net.minecraft.world.level.Level,
        burrow: Vec3,
        playerEye: Vec3,
        maxReach: Double,
    ): net.minecraft.core.BlockPos? {
        val bx = floor(burrow.x).toInt()
        val by = floor(burrow.y).toInt()
        val bz = floor(burrow.z).toInt()

        // Order candidates by which side of the burrow the player is on, so
        // the etherwarp picks the side facing the player (shorter aim swing,
        // less chance of the look-ray clipping the burrow column).
        val pdx = playerEye.x - burrow.x
        val pdz = playerEye.z - burrow.z
        val primary: Pair<Int, Int>
        val secondary: Pair<Int, Int>
        if (abs(pdx) >= abs(pdz)) {
            primary = (if (pdx > 0) 2 else -2) to 0
            secondary = 0 to (if (pdz > 0) 2 else -2)
        } else {
            primary = 0 to (if (pdz > 0) 2 else -2)
            secondary = (if (pdx > 0) 2 else -2) to 0
        }
        val candidates = listOf(primary, secondary, -primary.first to -primary.second, -secondary.first to -secondary.second)

        for ((dx, dz) in candidates) {
            val cx = bx + dx
            val cy = by
            val cz = bz + dz
            val standState = level.getBlockState(net.minecraft.core.BlockPos(cx, cy, cz))
            if (standState.isAir) continue
            // Head + body clearance for the player to stand here.
            if (!level.getBlockState(net.minecraft.core.BlockPos(cx, cy + 1, cz)).isAir) continue
            if (!level.getBlockState(net.minecraft.core.BlockPos(cx, cy + 2, cz)).isAir) continue
            // Etherwarp aims at the block's top face — that's where the
            // server's ray-trace will hit and what we measure distance to.
            val standTop = Vec3(cx + 0.5, cy + 1.0, cz + 0.5)
            if (playerEye.distanceTo(standTop) > maxReach) continue
            return net.minecraft.core.BlockPos(cx, cy, cz)
        }
        return null
    }

    private fun nativeWalkActive(): Boolean =
        NativePathfinder.status.let { it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED }

    private fun stopNativeWalk() {
        NativePathfinder.stop()
        NativePathfinder.availabilityFlagsOverride = null
        nativeWalkTarget = null
        nativeWalkRadius = 0.0
    }

    private fun resetAimSettle() {
        aimStableSinceMs = 0L
        lastAimYaw = Float.NaN
        lastAimPitch = Float.NaN
    }

    private fun releaseTravelInputs() {
        stopNativeWalk()
        MovementManager.setMovementLock(false)
        MovementManager.clearForcedMovement()
        RotationExecutor.stopIfUsing(rotationStrategy)
        mc.options.keyShift?.setDown(false)
        mc.options.keyUse?.setDown(false)
    }

    private fun endTravel() {
        releaseTravelInputs()
        DianaTeleportPlanner.cancel()
        resetHopState()
    }

    private fun aimAt(rot: Rotation) {
        if (smoothAimSetting.value) {
            RotationExecutor.rotateTo(rot, rotationStrategy)
        } else {
            val p = mc.player ?: return
            p.yRot = rot.yaw
            p.xRot = rot.pitch
        }
    }

    /**
     * Ground-only native walking. Diana's own teleport planner still owns AOTV
     * and etherwarp hops; this disables native teleport actions so walking and
     * jump timing come from the native path executor only.
     */
    private fun nativeWalkTo(
        player: net.minecraft.client.player.LocalPlayer,
        target: Vec3,
        radius: Double = 1.35,
    ): Boolean {
        val targetChanged = nativeWalkTarget?.distanceToSqr(target)?.let { it > 0.04 } ?: true
        val radiusChanged = abs(nativeWalkRadius - radius) > 0.01
        if (targetChanged || radiusChanged || !nativeWalkActive()) {
            NativePathfinder.stop()
            NativePathfinder.availabilityFlagsOverride = 0
            NativePathfinder.setTargetWithRadius(target.x, target.y, target.z, radius)
            nativeWalkTarget = target
            nativeWalkRadius = radius
        } else {
            NativePathfinder.availabilityFlagsOverride = 0
        }

        if (player.position().distanceTo(target) <= radius || NativePathfinder.status == PathStatus.ARRIVED) {
            stopNativeWalk()
            return true
        }

        val cmd = NativePathfinder.tick()
        if (cmd != null) {
            cmd.applyToPlayer()
        } else if (NativePathfinder.status == PathStatus.FAILED) {
            stopNativeWalk()
            return false
        }
        return player.position().distanceTo(target) <= radius
    }

    /** Native ground approach while the teleport planner has nothing usable. */
    private fun directApproach(player: net.minecraft.client.player.LocalPlayer, target: Vec3) {
        MovementManager.setMovementLock(true)
        nativeWalkTo(player, target)
    }

    private fun debugTravel(msg: String) {
        if (!travelDebugSetting.value) return
        val now = System.currentTimeMillis()
        if (now - lastTravelDebugMs < 1500L) return
        lastTravelDebugMs = now
        ChatUtils.sendMessage("Diana: $msg")
    }

    private fun setStatus(text: String) {
        lastStatus = text
    }

    private fun collectTimeoutTicks(): Int =
        if (maxBurrowsPerHourSetting.value) {
            fastCollectDurationSetting.value.toInt().coerceAtMost(collectDurationSetting.value.toInt())
        } else {
            collectDurationSetting.value.toInt()
        }

    private fun loopWaitTicks(): Int =
        if (maxBurrowsPerHourSetting.value) {
            fastLoopWaitSetting.value.toInt()
        } else {
            postKillWaitSetting.value.toInt()
        }

    private fun treasureDigTicks(): Int =
        if (maxBurrowsPerHourSetting.value) {
            fastTreasureDigTicksSetting.value.toInt()
        } else {
            14
        }

    private fun isGuessTarget(): Boolean =
        burrowType == DianaParticleTracker.BurrowType.GUESS ||
            burrowType == DianaParticleTracker.BurrowType.SUB_GUESS

    private fun isTreasureTarget(): Boolean =
        burrowType == DianaParticleTracker.BurrowType.TREASURE ||
            burrowType == DianaParticleTracker.BurrowType.START ||
            burrowType == DianaParticleTracker.BurrowType.UNKNOWN

    private fun promoteNearbyConfirmedBurrow(
        level: net.minecraft.world.level.Level,
        center: Vec3,
        radius: Double = 6.0,
    ): Pair<Vec3, DianaParticleTracker.BurrowType>? {
        val radiusSq = radius * radius
        val confirmed = DianaParticleTracker.getBurrowRecords(level)
            .filter { (_, type) ->
                type != DianaParticleTracker.BurrowType.GUESS &&
                    type != DianaParticleTracker.BurrowType.SUB_GUESS &&
                    type != DianaParticleTracker.BurrowType.UNKNOWN
            }
            .filter { (pos, _) -> pos.distanceToSqr(center) <= radiusSq }
            .minByOrNull { (pos, _) -> pos.distanceToSqr(center) }
            ?: return null

        burrowPos = confirmed.first
        burrowType = confirmed.second
        if (floor(center.x).toInt() != floor(confirmed.first.x).toInt() ||
            floor(center.z).toInt() != floor(confirmed.first.z).toInt()
        ) {
            DianaParticleTracker.removeBurrow(floor(center.x).toInt(), floor(center.z).toInt())
        }
        return confirmed
    }

    private fun allowedWarpCommands(): Set<String> {
        val configured = DianaHelperModule.allowedWarps.value.split(',', ';', ' ')
            .map { normalizeWarpName(it) }
            .filter { it.isNotBlank() }
            .toSet()
        return configured.ifEmpty { setOf("wizard", "da", "castle", "crypt", "stonks") }
    }

    private fun normalizeWarpName(raw: String): String {
        val text = raw.trim().lowercase()
        return when (text) {
            "darkauction", "dark_auction", "dark-auction" -> "da"
            else -> text
        }
    }

    private fun flatDistance(a: Vec3, b: Vec3): Double =
        kotlin.math.hypot(a.x - b.x, a.z - b.z)

    private fun bestWarpFor(playerPos: Vec3, target: Vec3): WarpPoint? {
        if (!autoHubWarpSetting.value) return null
        val allowed = allowedWarpCommands()
        val best = HUB_WARPS
            .filter { it.command in allowed }
            .minByOrNull { flatDistance(it.pos, target) + it.extraBlocks } ?: return null
        val playerDistance = flatDistance(playerPos, target)
        val warpDistance = flatDistance(best.pos, target) + best.extraBlocks
        val closeCutoff = DianaHelperModule.closeWarpDistance.value
        val saving = DianaHelperModule.warpDistanceDifference.value.coerceAtLeast(18.0)
        val meaningfullyCloser = warpDistance <= playerDistance * 0.82
        return if (
            playerDistance > closeCutoff &&
            warpDistance < playerDistance &&
            playerDistance - warpDistance >= saving &&
            meaningfullyCloser
        ) best else null
    }

    private fun startWarpTo(warp: WarpPoint, target: Vec3) {
        endTravel()
        pendingWarp = warp
        warpTarget = target
        warpTicksElapsed = 0
        warpAttemptedFor = target
        mc.player?.connection?.sendCommand("warp ${warp.command}")
        setStatus("Warping ${warp.displayName}")
        state = State.WARPING
    }

    /**
     * Last-resort blind aerial chain, used only when the native climb-and-fly
     * planner returns no route for a far guess. Returns true if it engaged.
     */
    private fun tryAerialChainFallback(
        player: net.minecraft.client.player.LocalPlayer,
        target: Vec3,
    ): Boolean {
        // Reached only after the aotvSlot >= 0 / wantsTeleports guard, so the
        // AOTV slot is already held for pathfinding here.
        if (!aerialChainSetting.value || !useAotvSetting.value) return false
        if (!isGuessTarget()) return false
        if (player.position().distanceTo(target) < aerialChainMinDistanceSetting.value) return false
        hops = buildAerialAotvChain(player, target)
        hopIndex = 1
        planSubmitted = true
        noProgressTicks = 0
        lastProgressDistSq = Double.MAX_VALUE
        lastPlanInfo = "air chain ${hops.count { it.airborne }} nodes (fallback)"
        stopNativeWalk()
        return true
    }

    private fun buildAerialAotvChain(player: net.minecraft.client.player.LocalPlayer, target: Vec3): List<DianaTeleportPlanner.Hop> {
        val count = aerialChainNodesSetting.value.toInt().coerceIn(3, 14)
        val start = player.position()
        val yaw = AngleUtils.getRotation(player.eyePosition, Vec3(target.x, target.y + 1.5, target.z)).yaw
        val pitch = aerialChainPitchSetting.value.toFloat()
        val result = ArrayList<DianaTeleportPlanner.Hop>(count + 2)
        result += DianaTeleportPlanner.Hop(floor(start.x).toInt(), floor(start.y).toInt(), floor(start.z).toInt(), DianaTeleportPlanner.HopType.WALK, yaw, pitch)
        for (i in 1..count) {
            val t = i.toDouble() / (count + 1).toDouble()
            val x = start.x + (target.x - start.x) * t
            val z = start.z + (target.z - start.z) * t
            val y = start.y + i * 1.18
            result += DianaTeleportPlanner.Hop(
                floor(x).toInt(),
                floor(y).toInt(),
                floor(z).toInt(),
                DianaTeleportPlanner.HopType.AOTV,
                yaw,
                pitch,
                airborne = true,
            )
        }
        result += DianaTeleportPlanner.Hop(floor(target.x).toInt(), floor(target.y).toInt(), floor(target.z).toInt(), DianaTeleportPlanner.HopType.WALK, yaw, 0f)
        return result
    }

    private fun computeSpadeBeamMoveTarget(player: net.minecraft.client.player.LocalPlayer, level: net.minecraft.world.level.Level): Vec3 {
        val eye = player.eyePosition
        val direction = player.lookAngle
        val end = eye.add(direction.scale(preGuessMoveRangeSetting.value))
        val hit = level.clip(ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
        val raw = if (hit.type == HitResult.Type.BLOCK) hit.location else end
        val surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, floor(raw.x).toInt(), floor(raw.z).toInt())
        val y = if (surfaceY > level.minY) surfaceY.toDouble() else player.y
        return Vec3(raw.x, y, raw.z)
    }

    private fun handlePreGuessMove(
        player: net.minecraft.client.player.LocalPlayer,
        level: net.minecraft.world.level.Level,
    ) {
        if (!preGuessMoveSetting.value) return
        val target = preGuessMoveTarget ?: computeSpadeBeamMoveTarget(player, level).also { preGuessMoveTarget = it }
        if (player.position().distanceTo(target) <= preGuessStopDistanceSetting.value) {
            stopNativeWalk()
            MovementManager.setMovementLock(true)
            MovementManager.setForcedMovement(false, false, false, false, false, false, false)
            return
        }
        setStatus("Following spade beam")
        nativeWalkTo(player, target, preGuessStopDistanceSetting.value)
    }

    private fun arrivalRadius(): Double =
        if (maxBurrowsPerHourSetting.value) 3.0 else 2.5

    private fun aimSettleMs(): Long =
        if (maxBurrowsPerHourSetting.value) {
            aimSettleSetting.value.toLong().coerceAtMost(45L)
        } else {
            aimSettleSetting.value.toLong()
        }

    private fun stuckReplanTicks(): Int =
        if (maxBurrowsPerHourSetting.value) {
            stuckReplanTicksSetting.value.toInt().coerceAtMost(35)
        } else {
            stuckReplanTicksSetting.value.toInt()
        }

    private fun pauseMacro() {
        paused = true
        setStatus("Paused")
        pauseMacroInputsOnly()
    }

    private fun pauseMacroInputsOnly() {
        releaseTravelInputs()
        MovementManager.clearForcedMovement()
        MovementManager.setMovementLock(false)
        MovementManager.forcedActionsEnabled = false
        MovementManager.forcedUse = false
        MovementManager.forcedAttack = false
        RotationExecutor.stopIfUsing(rotationStrategy)
        RotationExecutor.stopIfUsing(combatRotationStrategy)
    }

    private fun updateRareMobFocus(player: net.minecraft.client.player.LocalPlayer) {
        if (!rareMobPrioritySetting.value) return
        val anchor = DianaHelperModule.rareMobAnchorPos
            ?: findRareDianaMob(player.position(), mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value)?.position()
            ?: return
        rareMobFocusPos = anchor
        rareMobFocusUntilMs = System.currentTimeMillis() + rareMobFocusSecondsSetting.value.toLong() * 1000L
    }

    private fun rareMobFocusActive(): Boolean =
        rareMobPrioritySetting.value &&
            rareMobFocusPos != null &&
            System.currentTimeMillis() <= rareMobFocusUntilMs

    private fun cocoonHoldActive(): Boolean =
        cocoonHoldSetting.value && DianaHelperModule.isCocoonActive()

    private fun rareNearbyBurrow(level: net.minecraft.world.level.Level): Pair<Vec3, DianaParticleTracker.BurrowType>? {
        val anchor = rareMobFocusPos ?: return null
        val radius = nearbyBurrowRadiusSetting.value
        return DianaParticleTracker.getBurrowRecords(level, expireSecondsMs())
            .filter { (_, type) -> type != DianaParticleTracker.BurrowType.SUB_GUESS }
            .filter { (_, type) -> type != DianaParticleTracker.BurrowType.GUESS || useKnownBurrowSetting.value }
            .filter { (pos, _) -> pos.distanceTo(anchor) <= radius }
            .minByOrNull { (pos, _) -> pos.distanceToSqr(anchor) }
    }

    private fun knownBurrow(level: net.minecraft.world.level.Level): Pair<Vec3, DianaParticleTracker.BurrowType>? {
        if (rareMobFocusActive() || cocoonHoldActive()) {
            rareNearbyBurrow(level)?.let { return it }
            if (cocoonHoldActive()) return null
        }
        DianaParticleTracker.getBurrowRecords(level).firstOrNull { it.second != DianaParticleTracker.BurrowType.SUB_GUESS }?.let { return it }
        val helperPos = DianaHelperModule.burrowPos ?: return null
        return helperPos to DianaParticleTracker.BurrowType.GUESS
    }

    private fun expireSecondsMs(): Long = DianaHelperModule.expireSeconds.value.toLong() * 1000L

    private fun findDianaMob(center: Vec3, range: Double): LivingEntity? {
        val level = mc.level ?: return null
        return level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(center, center).inflate(range)
        )
            .filter { it.isAlive && dianaMobName(it) != null }
            .minWithOrNull(
                compareByDescending<LivingEntity> { if (dianaMobName(it) in RARE_MOB_NAMES) 1 else 0 }
                    .thenBy { it.position().distanceToSqr(center) }
            )
    }

    private fun findRareDianaMob(center: Vec3, range: Double): LivingEntity? {
        val level = mc.level ?: return null
        return level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(center, center).inflate(range)
        )
            .filter { it.isAlive && dianaMobName(it) in RARE_MOB_NAMES }
            .minByOrNull { it.position().distanceToSqr(center) }
    }

    private fun startCombat(target: LivingEntity, rare: Boolean) {
        stopNativeWalk()
        MovementManager.forcedUse = false
        targetEntityId = target.id
        holdWeaponSlot()
        combatIsRareMob = rare
        if (rare) {
            rareMobFocusPos = target.position()
            rareMobFocusUntilMs = System.currentTimeMillis() + rareMobFocusSecondsSetting.value.toLong() * 1000L
        }
        resetCombatTracking()
        combatIsRareMob = rare
        state = State.COMBAT
    }

    private fun waitAtRareAnchor(player: net.minecraft.client.player.LocalPlayer): Boolean {
        val anchor = rareMobFocusPos ?: DianaHelperModule.rareMobAnchorPos ?: return false
        rareMobFocusPos = anchor
        setStatus(if (cocoonHoldActive()) "Holding cocoon" else "Rare mob wait")
        MovementManager.forcedActionsEnabled = false
        MovementManager.forcedUse = false
        MovementManager.forcedAttack = false
        if (player.position().distanceTo(anchor) > 4.5) {
            directApproach(player, anchor)
        } else {
            stopNativeWalk()
            MovementManager.setMovementLock(true)
            MovementManager.setForcedMovement(false, false, false, false, false, false, false)
            aimAt(AngleUtils.getRotation(player.eyePosition, Vec3(anchor.x, anchor.y + 1.0, anchor.z)))
        }
        return true
    }

    private fun handleIronBlockEscape(player: net.minecraft.client.player.LocalPlayer, level: net.minecraft.world.level.Level): Boolean {
        if (!avoidIronBlocksSetting.value || state == State.PATHFINDING || state == State.ACTIVATING_SPADE || state == State.COLLECTING_PARTICLES) {
            if (ironEscapeTarget != null) stopNativeWalk()
            ironEscapeTarget = null
            ironEscapeTicks = 0
            return false
        }
        val feet = BlockPos.containing(player.x, player.y - 0.05, player.z)
        val below = feet.below()
        val standingOnIron = level.getBlockState(below).block == Blocks.IRON_BLOCK ||
            level.getBlockState(feet).block == Blocks.IRON_BLOCK
        if (!standingOnIron) {
            if (ironEscapeTarget != null) stopNativeWalk()
            ironEscapeTarget = null
            ironEscapeTicks = 0
            return false
        }

        val target = ironEscapeTarget
            ?.takeIf { player.position().distanceTo(it) > 0.65 && ironEscapeTicks < 40 }
            ?: findIronEscapeTarget(level, feet, player.position())
            ?: player.position().add(player.lookAngle.multiply(-2.0, 0.0, -2.0))
        ironEscapeTarget = target
        ironEscapeTicks++
        setStatus("Moving off iron")
        MovementManager.forcedActionsEnabled = false
        MovementManager.forcedUse = false
        MovementManager.forcedAttack = false
        directApproach(player, target)
        return true
    }

    private fun findIronEscapeTarget(level: net.minecraft.world.level.Level, feet: BlockPos, playerPos: Vec3): Vec3? {
        var best: Vec3? = null
        var bestScore = Double.MAX_VALUE
        for (dx in -3..3) {
            for (dz in -3..3) {
                if (dx == 0 && dz == 0) continue
                val candidateFeet = feet.offset(dx, 0, dz)
                if (!canStandOffIron(level, candidateFeet)) continue
                val center = Vec3(candidateFeet.x + 0.5, candidateFeet.y.toDouble(), candidateFeet.z + 0.5)
                val score = center.distanceToSqr(playerPos) + kotlin.math.abs(dx).coerceAtLeast(kotlin.math.abs(dz)) * 0.15
                if (score < bestScore) {
                    bestScore = score
                    best = center
                }
            }
        }
        return best
    }

    private fun canStandOffIron(level: net.minecraft.world.level.Level, feet: BlockPos): Boolean {
        val support = feet.below()
        val supportState = level.getBlockState(support)
        if (supportState.block == Blocks.IRON_BLOCK || supportState.isAir) return false
        val feetState = level.getBlockState(feet)
        val headState = level.getBlockState(feet.above())
        return !feetState.blocksMotion() && !headState.blocksMotion()
    }

    private fun resetCombatTracking() {
        combatMoving = false
        combatTicksElapsed = 0
        targetLostTicks = 0
        lastAttackTick = 0
        smoothedCombatAim = null
        RotationExecutor.stopIfUsing(combatRotationStrategy)
    }

    private fun combatAimPoint(target: LivingEntity): Vec3 {
        val prediction = combatPredictTicksSetting.value
        val height = (target.bbHeight * combatAimHeightSetting.value).coerceIn(0.35, min(1.85, target.bbHeight.toDouble()))
        val raw = target.position()
            .add(target.deltaMovement.scale(prediction))
            .add(0.0, height, 0.0)
        if (!combatSmoothAimSetting.value) {
            smoothedCombatAim = raw
            return raw
        }
        val previous = smoothedCombatAim
        val alpha = combatAimSmoothingSetting.value.coerceIn(0.10, 1.0)
        val smoothed = if (previous == null || previous.distanceToSqr(raw) > 16.0) {
            raw
        } else {
            previous.add(raw.subtract(previous).scale(alpha))
        }
        smoothedCombatAim = smoothed
        return smoothed
    }

    private fun combatRotationError(player: net.minecraft.client.player.LocalPlayer, aimPoint: Vec3): Pair<Float, Float> {
        val desired = AngleUtils.getRotation(player.eyePosition, aimPoint)
        return abs(AngleUtils.getRotationDelta(player.yRot, desired.yaw)) to abs(player.xRot - desired.pitch)
    }

    private fun dianaMobName(entity: LivingEntity): String? {
        val name = ChatFormatting.stripFormatting(entity.displayName.string).orEmpty()
        return DIANA_MOB_NAMES.firstOrNull { n -> name.contains(n, ignoreCase = true) }
    }

    private fun isDianaSpade(stack: net.minecraft.world.item.ItemStack): Boolean {
        if (stack.isEmpty) return false
        val id = stack.getSkyblockId().uppercase()
        if (id in DIANA_SPADE_IDS) return true
        return ChatFormatting.stripFormatting(stack.hoverName.string)
            ?.contains("spade", ignoreCase = true) == true
    }

    private fun isLikelyWeapon(stack: net.minecraft.world.item.ItemStack): Boolean {
        if (stack.isEmpty || isDianaSpade(stack) || EtherwarpLogic.isEtherwarpStack(stack)) return false
        val id = stack.getSkyblockId().uppercase()
        if (WEAPON_ID_HINTS.any { id.contains(it) }) return true
        val name = ChatFormatting.stripFormatting(stack.hoverName.string)?.lowercase().orEmpty()
        return WEAPON_NAME_HINTS.any { name.contains(it) }
    }

    private fun holdSlot(slot: Int): Boolean {
        val player = mc.player ?: return false
        if (slot !in 0..8) return false
        if (player.inventory.selectedSlot != slot) InventoryUtils.holdHotbarSlot(slot)
        return true
    }

    private fun holdSpadeSlot(): Boolean {
        val player = mc.player ?: return false
        val slot = spadeSlot
        if (slot !in 0..8 || !isDianaSpade(player.inventory.getItem(slot))) return false
        return holdSlot(slot)
    }

    private fun holdAotvSlotForPathfinding(): Int {
        val slot = resolveAotvSlot()
        if (slot >= 0 && castRestoreSlot < 0) holdSlot(slot)
        return slot
    }

    private fun holdWeaponSlot(): Int {
        val slot = weaponSlot.coerceIn(0, 8)
        holdSlot(slot)
        return slot
    }

    private fun randomCastCooldown(): Long {
        val lo = if (maxBurrowsPerHourSetting.value) {
            castCdMinSetting.value.toLong().coerceAtMost(150L)
        } else {
            castCdMinSetting.value.toLong()
        }
        val configuredHi = if (maxBurrowsPerHourSetting.value) {
            castCdMaxSetting.value.toLong().coerceAtMost(220L)
        } else {
            castCdMaxSetting.value.toLong()
        }
        val hi = configuredHi.coerceAtLeast(lo)
        return if (hi <= lo) lo else lo + Random.nextLong(hi - lo + 1)
    }

    private fun fireCast(isEtherwarp: Boolean, aotvSlot: Int) {
        val player = mc.player ?: return
        castRestoreSlot = player.inventory.selectedSlot
        InventoryUtils.holdHotbarSlot(aotvSlot)
        if (isEtherwarp) mc.options.keyShift?.setDown(true)
        mc.options.keyUse?.setDown(true)
        TickScheduler.schedule(1L) {
            mc.options.keyUse?.setDown(false)
            if (isEtherwarp) mc.options.keyShift?.setDown(false)
        }
        TickScheduler.schedule(4L) {
            val r = castRestoreSlot
            if (r in 0..8) InventoryUtils.holdHotbarSlot(r)
            castRestoreSlot = -1
        }
    }

    // -- Tick ------------------------------------------------------------------

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (toggleKeySetting.value.isPressed()) {
            enabledSetting.value = !enabledSetting.value
            paused = false
            if (enabledSetting.value) ChatUtils.sendMessage("Diana: macro enabled.") else ChatUtils.sendMessage("Diana: macro disabled.")
        }
        if (pauseKeySetting.value.isPressed() && enabledSetting.value) {
            if (paused) {
                paused = false
                ChatUtils.sendMessage("Diana: macro resumed.")
            } else {
                pauseMacro()
                ChatUtils.sendMessage("Diana: macro paused.")
            }
        }
        if (!enabledSetting.value) {
            if (wasEnabled) stop()
            wasEnabled = false
            return
        }
        wasEnabled = true
        if (autoEnableHelperSetting.value) DianaHelperModule.enabled.value = true
        if (paused) return

        val player = mc.player ?: run { stop(); wasEnabled = false; return }
        val level  = mc.level  ?: run { stop(); wasEnabled = false; return }
        if (pauseInMenusSetting.value && mc.screen != null) {
            setStatus("Menu pause")
            pauseMacroInputsOnly()
            return
        }
        if (stopOnDeathSetting.value && (!player.isAlive || player.health <= 0.0f)) {
            enabledSetting.value = false
            stop()
            ChatUtils.sendMessage("Diana: macro stopped because you died.")
            return
        }
        if (maxLoopsSetting.value.toInt() > 0 && loopsCompleted >= maxLoopsSetting.value.toInt()) {
            enabledSetting.value = false
            stop()
            ChatUtils.sendMessage("Diana: max loops reached.")
            return
        }
        if (handleIronBlockEscape(player, level)) return
        updateRareMobFocus(player)
        setStatus(state.name.lowercase().replaceFirstChar { it.uppercase() })

        when (state) {

            State.IDLE -> {
                if (rareMobPrioritySetting.value) {
                    val rare = findRareDianaMob(
                        rareMobFocusPos ?: player.position(),
                        (mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value).coerceAtLeast(24.0)
                    )
                    if (rare != null) {
                        DianaProfitTracker.onMacroMobFound()
                        startCombat(rare, rare = true)
                        return
                    }
                }
                if (useKnownBurrowSetting.value) {
                    val known = knownBurrow(level)
                    if (known != null) {
                        burrowPos = known.first
                        burrowType = known.second
                        DianaProfitTracker.onMacroTarget(burrowType)
                        pathfindingTicksElapsed = 0
                        digTicksElapsed = 0
                        digApproachTicks = 0
                        treasureDigStage = 0
                        treasureWaitTicks = 0
                        warpAttemptedFor = null
                        preGuessMoveTarget = null
                        setStatus("Using known burrow")
                        state = State.PATHFINDING
                        return
                    }
                }
                if ((rareMobFocusActive() || cocoonHoldActive()) && waitAtRareAnchor(player)) return
                // Reset particle tracker before a fresh activation
                DianaParticleTracker.reset()
                val resolvedSpade = spadeSlot
                if (resolvedSpade !in 0..8 || !isDianaSpade(player.inventory.getItem(resolvedSpade))) {
                    ChatUtils.sendMessage("Diana: no Diana spade found in hotbar.")
                    state = State.WAITING
                    waitTicksElapsed = 0
                    return
                }
                holdSpadeSlot()
                MovementManager.setMovementLock(true)
                MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                MovementManager.forcedActionsEnabled = true
                MovementManager.forcedUse = true
                activatingTicksElapsed = 0
                state = State.ACTIVATING_SPADE
            }

            State.ACTIVATING_SPADE -> {
                activatingTicksElapsed++
                // Hold the use key for 3 ticks so the server reliably registers the activation
                if (activatingTicksElapsed < 3) {
                    MovementManager.forcedUse = true
                    return
                }
                MovementManager.forcedUse = false
                collectTicksElapsed = 0
                preGuessMoveTarget = computeSpadeBeamMoveTarget(player, level)
                state = State.COLLECTING_PARTICLES
            }

            State.COLLECTING_PARTICLES -> {
                collectTicksElapsed++
                // Transition as soon as we have enough packets - don't wait the full duration
                val record = DianaParticleTracker.getBurrowRecord(level)
                if (record != null || DianaParticleTracker.count() >= minParticlesSetting.value.toInt()) {
                    val resolved = record ?: DianaParticleTracker.getBurrowRecord(level)
                    if (resolved != null) {
                        burrowPos = resolved.first
                        burrowType = resolved.second
                        DianaProfitTracker.onMacroTarget(burrowType)
                        pathfindingTicksElapsed = 0
                        digTicksElapsed = 0
                        digApproachTicks = 0
                        treasureDigStage = 0
                        treasureWaitTicks = 0
                        warpAttemptedFor = null
                        preGuessMoveTarget = null
                        state = State.PATHFINDING
                        return
                    }
                }
                handlePreGuessMove(player, level)
                // Timeout - no burrow detected, retry
                if (collectTicksElapsed >= collectTimeoutTicks()) {
                    preGuessMoveTarget = null
                    state = State.IDLE
                }
            }

            State.PATHFINDING -> {
                val bp = burrowPos ?: run { state = State.IDLE; return }
                val target = Vec3(bp.x, bp.y + 1.0, bp.z)

                if (rareMobPrioritySetting.value) {
                    val rare = findRareDianaMob(rareMobFocusPos ?: player.position(), mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value)
                    if (rare != null) {
                        endTravel()
                        DianaProfitTracker.onMacroMobFound()
                        startCombat(rare, rare = true)
                        return
                    }
                }

                pathfindingTicksElapsed++
                if (pathfindingTicksElapsed > travelTimeoutSetting.value.toInt()) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: travel timed out, retrying.")
                    state = State.IDLE
                    return
                }

                val aotvSlot = holdAotvSlotForPathfinding()
                val wantsTeleports = useAotvSetting.value || useEtherwarpSetting.value
                if (aotvSlot < 0 && wantsTeleports && !walkFallbackSetting.value) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: no AOTV in hotbar (set AOTV Slot).")
                    state = State.IDLE
                    return
                }

                // Arrived at the burrow.
                if (player.position().distanceTo(target) <= arrivalRadius()) {
                    endTravel()
                    promoteNearbyConfirmedBurrow(level, bp, radius = 8.0)
                    DianaProfitTracker.onMacroArrived()
                    holdSpadeSlot()
                    digTicksElapsed = 0
                    digApproachTicks = 0
                    treasureDigStage = 0
                    treasureWaitTicks = 0
                    state = State.DIGGING
                    return
                }

                if (isGuessTarget() && hops.isEmpty()) {
                    val dist = player.position().distanceTo(target)
                    if (dist <= closeGuessWalkDistanceSetting.value) {
                        debugTravel("close guess: walking")
                        directApproach(player, target)
                        return
                    }

                    val warp = bestWarpFor(player.position(), target)
                    if (warp != null && warpAttemptedFor?.distanceToSqr(target)?.let { it < 4.0 } != true) {
                        debugTravel("far guess: warping ${warp.displayName}")
                        startWarpTo(warp, target)
                        return
                    }

                    // Far guesses with no warp now climb-and-fly through the
                    // native planner (terrain-aware), driven by the existing
                    // Aerial Chain Min Distance trigger below. The old blind
                    // Kotlin chain (buildAerialAotvChain) is kept only as a
                    // last-resort fallback when the native planner finds no
                    // route at all.
                }

                // Direct-etherwarp fast-path: if the burrow is within direct
                // etherwarp reach AND a clean stand block exists ~2 blocks
                // from it, inject a single-hop "plan" that lands the player
                // on that stand block in one shot. Bypasses the planner
                // entirely — both faster and free of the planner thrash that
                // produces the "back and forth between two spots" oscillation
                // near the goal. Gate on dist > 4 so we don't re-fire after
                // landing (the arrival check / walk handles the final 2-4 m).
                if (!planSubmitted && hops.isEmpty() && useEtherwarpSetting.value && aotvSlot >= 0) {
                    val ewRange = EtherwarpLogic.getEtherwarpRange().toDouble()
                    val distToBurrow = player.eyePosition.distanceTo(target)
                    if (distToBurrow in 4.0..ewRange) {
                        val standBlock = findEtherwarpStandBlock(level, Vec3(bp.x, bp.y, bp.z), player.eyePosition, ewRange)
                        if (standBlock != null) {
                            val standCenter = Vec3(standBlock.x + 0.5, standBlock.y + 0.5, standBlock.z + 0.5)
                            val rot = AngleUtils.getRotation(player.eyePosition, standCenter)
                            val startHop = DianaTeleportPlanner.Hop(
                                floor(player.x).toInt(), floor(player.y).toInt(), floor(player.z).toInt(),
                                DianaTeleportPlanner.HopType.WALK, 0f, 0f, false
                            )
                            val ewHop = DianaTeleportPlanner.Hop(
                                standBlock.x, standBlock.y + 1, standBlock.z,
                                DianaTeleportPlanner.HopType.ETHERWARP, rot.yaw, rot.pitch, false
                            )
                            hops = listOf(startHop, ewHop)
                            hopIndex = 1
                            planSubmitted = true
                            noProgressTicks = 0
                            lastProgressDistSq = Double.MAX_VALUE
                            debugTravel("direct etherwarp: stand @ (${standBlock.x},${standBlock.y},${standBlock.z}), ${distToBurrow.toInt()}m to burrow, plan has 1 cast")
                            MovementManager.setMovementLock(true)
                            MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                            return
                        }
                    }
                }

                // Request a hybrid teleport-first plan and wait for it.
                if (!wantsTeleports || aotvSlot < 0) {
                    debugTravel("walking fallback")
                    directApproach(player, target)
                    return
                }

                if (!planSubmitted) {
                    // Climb-and-fly only for far guesses with the aerial chain
                    // enabled — reuses the existing Aerial Chain Min Distance
                    // setting as the native fly trigger (0 = plain blend).
                    val flyTrigger = if (
                        isGuessTarget() &&
                        aerialChainSetting.value &&
                        useAotvSetting.value
                    ) aerialChainMinDistanceSetting.value else 0.0
                    DianaTeleportPlanner.submit(
                        start = player.position(),
                        goal = target,
                        goalReachedRadius = plannerGoalRadiusSetting.value,
                        transmissionRange = transmissionRangeSetting.value,
                        etherwarpRange = EtherwarpLogic.getEtherwarpRange().toDouble(),
                        availableMana = -1,
                        aotvEnabled = useAotvSetting.value,
                        etherwarpEnabled = useEtherwarpSetting.value,
                        maxIterations = plannerIterationsSetting.value.toInt(),
                        maxNodes = plannerNodesSetting.value.toInt(),
                        simplify = simplifyNodesSetting.value,
                        flyTriggerDistance = flyTrigger,
                    )
                    planSubmitted = true
                    hops = emptyList()
                    hopIndex = 0
                    MovementManager.setMovementLock(true)
                    MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                    if (maxBurrowsPerHourSetting.value && walkFallbackSetting.value) directApproach(player, target)
                    return
                }
                if (hops.isEmpty()) {
                    val plan = DianaTeleportPlanner.poll()
                    if (plan == null) {
                        if (!DianaTeleportPlanner.isPlanning) {
                            if (tryAerialChainFallback(player, target)) {
                                debugTravel("planner: no path, blind air-chain fallback")
                                return
                            }
                            // No plan yet — keep approaching directly so far
                            // chunks stream in, then resubmit. Never just stand.
                            if (walkFallbackSetting.value) directApproach(player, target)
                            debugTravel("planner: no path, ${if (walkFallbackSetting.value) "walking + " else ""}retrying")
                            planSubmitted = false
                        } else if (walkFallbackSetting.value) {
                            directApproach(player, target)
                        }
                        return
                    }
                    debugTravel(
                        "plan: ${plan.hops.size}/${plan.rawHopCount} hops, " +
                            "tp=${plan.teleportNodes}, ew=${plan.etherwarpNodes}, walk=${plan.walkNodes}, " +
                            "reached=${plan.reachedGoal}, ${plan.timeMs}ms, ${plan.nodesExplored} nodes"
                    )
                    lastPlanInfo = "${plan.hops.size}/${plan.rawHopCount} nodes  ${plan.totalDistance.toInt()}m"
                    if (plan.hops.size < 2) {
                        if (tryAerialChainFallback(player, target)) {
                            debugTravel("planner: route too short, blind air-chain fallback")
                            return
                        }
                        if (walkFallbackSetting.value) directApproach(player, target)
                        planSubmitted = false
                        return
                    }
                    hops = plan.hops
                    hopIndex = 1 // node 0 is the start position
                    noProgressTicks = 0
                    lastProgressDistSq = Double.MAX_VALUE
                    stopNativeWalk()
                    // Visible "plan committed" line so the user sees exactly
                    // how many nodes the macro is about to traverse.
                    debugTravel("plan committed: ${plan.hops.size} nodes (${plan.teleportNodes} aotv / ${plan.etherwarpNodes} ether / ${plan.walkNodes} walk)")
                }

                // No-progress watchdog -> replan from current position.
                val dSq = player.position().distanceToSqr(target)
                if (dSq < lastProgressDistSq - 1.0) {
                    lastProgressDistSq = dSq
                    noProgressTicks = 0
                } else {
                    noProgressTicks++
                    if (noProgressTicks > stuckReplanTicks()) {
                        releaseTravelInputs()
                        resetHopState()
                        return
                    }
                }

                if (hopIndex >= hops.size) {
                    resetHopState()
                    return
                }

                val hop = hops[hopIndex]
                val standPos = hop.standPos()

                when (hop.type) {
                    DianaTeleportPlanner.HopType.WALK -> {
                        if (nativeWalkTo(player, standPos, 1.15)) {
                            debugTravel("node $hopIndex complete @ (${hop.x},${hop.y},${hop.z}) walk")
                            hopIndex++
                            resetAimSettle()
                        }
                    }

                    DianaTeleportPlanner.HopType.AOTV,
                    DianaTeleportPlanner.HopType.ETHERWARP -> {
                        stopNativeWalk()
                        MovementManager.setMovementLock(true)
                        MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                        val isEther = hop.type == DianaTeleportPlanner.HopType.ETHERWARP

                        // Post-cast quick advance: AOTV/etherwarp teleport is
                        // essentially instant on the server, so once we've
                        // fired for THIS hop, the next tick the player is at
                        // (or near) the landing. The slow proximity check
                        // (<=2.0) can miss the landing when AOTV's fixed
                        // 11-block travel from a fallen eye drops the player
                        // a few blocks short of standPos — the chain looks
                        // like "didn't pass the node" and the watchdog has
                        // to clean up 500 ms later. Trust the cast: advance
                        // ~60 ms after firing regardless of proximity, and
                        // let the next hop's live aim self-correct from the
                        // actual landing position.
                        val nowEarly = System.currentTimeMillis()
                        if (castedHopIndex == hopIndex && nowEarly - castedAtMs > 60L) {
                            debugTravel("node $hopIndex complete @ (${player.x.toInt()},${player.y.toInt()},${player.z.toInt()}) ${if (isEther) "etherwarp" else "aotv"}")
                            hopIndex++
                            castedHopIndex = -1
                            resetAimSettle()
                            return
                        }
                        // Instant transmission needs no target block and can be
                        // chained mid-air; etherwarp needs a block + crouch
                        // (fireCast handles the sneak). Air chains must fire
                        // fast and tolerate pitch since they travel vertically.
                        val air = hop.airborne
                        // Sky-chain hops: the planner stored yaw/pitch computed
                        // against an idealised eye at (from.x+0.5, from.y+1.62,
                        // from.z+0.5). The instant the previous AOTV lands you
                        // airborne you start falling, so by the time aerialCastCooldown
                        // elapses + the rotation strategy settles, your eye is
                        // several blocks below where the planner assumed — and
                        // the same look angles from a lower origin send AOTV in
                        // a different absolute direction, missing the intended
                        // landing and breaking the chain. Recompute the aim
                        // every tick from the player's CURRENT eye toward the
                        // planner's aim point (the same point anglesBetween()
                        // used in the planner: standPos centre, +1y top face),
                        // and use those live angles for both the rotation
                        // target and the aim-error gate. Ground hops keep the
                        // stored angles (no fall between cast attempts).
                        val castYaw: Float
                        val castPitch: Float
                        if (air) {
                            // Live re-aim each tick: the planner stored angles
                            // for an idealised eye at the airborne node's block
                            // centre, but by the time we cast the player has
                            // fallen — recompute from the current eye toward
                            // the planner's aim point (same point anglesBetween
                            // used in the planner) so AOTV's ~11-block fixed
                            // reach along the look vector lands close to plan.
                            val aimTarget = Vec3(hop.x + 0.5, hop.y + 1.0, hop.z + 0.5)
                            val live = AngleUtils.getRotation(player.eyePosition, aimTarget)
                            castYaw = live.yaw
                            castPitch = live.pitch
                            // Human-like fast Bezier (NOT snap): airborneRotationStrategy
                            // is tuned with a brisk per-frame cap and a high
                            // minScale floor so it eases naturally yet still
                            // keeps up with the live target's pitch drift as
                            // gravity pulls the player. The gate below holds
                            // fireCast until the swing has actually landed
                            // within 1.5° so the cast packet uses an accurate
                            // angle. Stop any prior strategy first so it
                            // doesn't fight this one.
                            if (!RotationExecutor.isUsing(airborneRotationStrategy)) {
                                RotationExecutor.stopIfUsing(rotationStrategy)
                            }
                            RotationExecutor.rotateTo(Rotation(castYaw, castPitch), airborneRotationStrategy)
                        } else {
                            castYaw = hop.yaw
                            castPitch = hop.pitch
                            aimAt(Rotation(castYaw, castPitch))
                        }

                        if (player.position().distanceTo(standPos) <= 2.0) {
                            debugTravel("node $hopIndex complete @ (${hop.x},${hop.y},${hop.z}) proximity")
                            hopIndex++
                            resetAimSettle()
                            return
                        }
                        // Ground teleports: once we're falling past the landing
                        // the cast already happened, advance. Skip this for air
                        // hops — you're always falling between sky chains.
                        if (!air && !player.onGround() && player.deltaMovement.y < -0.08 &&
                            player.y < standPos.y - 1.1
                        ) {
                            debugTravel("node $hopIndex complete @ (${hop.x},${hop.y},${hop.z}) falling-past")
                            hopIndex++
                            resetAimSettle()
                            return
                        }

                        val now = System.currentTimeMillis()

                        // Fast off-course watchdog for airborne hops. AOTV
                        // travels a fixed ~11 blocks along the look vector, so
                        // each sky hop carries ~0.3-2 blocks of drift from the
                        // planner's ideal landing once the player has fallen
                        // between casts; past ~2 blocks the `<=2.0` proximity
                        // advance never fires and the slow no-progress
                        // watchdog (~1.75 s) is far too late mid-fall. 500 ms
                        // is enough for the smooth airborne rotation to land
                        // within the 1.5° gate, the cast to fire, AOTV to
                        // teleport, and proximity to advance the hop — about
                        // 8-10 ticks — and short enough that a stuck hop
                        // triggers a fresh plan before the player has fallen
                        // more than 4-5 blocks.
                        if (air) {
                            if (airHopTrackedIndex != hopIndex) {
                                airHopTrackedIndex = hopIndex
                                airHopStartMs = now
                            } else if (now - airHopStartMs > 500L) {
                                debugTravel("air hop $hopIndex off-course (>500ms) — replanning")
                                releaseTravelInputs()
                                resetHopState()
                                return
                            }
                        }

                        val cd = if (air) aerialCastCooldownSetting.value.toLong() else castCooldownMs
                        if (now - lastCastMs < cd) return

                        // Precision gate. Airborne hops use a tight 1.5°
                        // tolerance — at AOTV's ~11-block reach, 1.5° of
                        // angular error is ~0.3 blocks of landing error, which
                        // keeps the chain on the planner's intended track.
                        // Ground hops keep the 3.5° tolerance + smooth settle
                        // so human-like aim has time to land on stored angles.
                        // Settle is 0 for airborne — once the smooth swing has
                        // converged within 1.5°, fire immediately; any extra
                        // wait costs altitude as the player keeps falling.
                        val airTol = 1.5f
                        val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, castYaw))
                        val pitchErr = abs(player.xRot - castPitch)
                        val tol = if (air) airTol else AIM_ERROR_DEG
                        if (yawErr > tol || ((isEther || air) && pitchErr > tol)) {
                            aimStableSinceMs = now
                            return
                        }
                        if (!air && now - aimStableSinceMs < aimSettleMs()) return

                        fireCast(isEther, aotvSlot)
                        lastCastMs = now
                        castCooldownMs = if (air) aerialCastCooldownSetting.value.toLong() else randomCastCooldown()
                        // Mark THIS hop as already-cast so the post-cast quick
                        // advance fires on the next tick instead of waiting on
                        // the proximity check (which AOTV's fixed-11-block
                        // travel from a fallen eye routinely misses by 2-4
                        // blocks).
                        castedHopIndex = hopIndex
                        castedAtMs = now
                    }
                }
            }

            State.WARPING -> {
                val target = warpTarget ?: burrowPos ?: run {
                    pendingWarp = null
                    state = State.IDLE
                    return
                }
                val warp = pendingWarp
                warpTicksElapsed++
                setStatus(if (warp != null) "Warping ${warp.displayName}" else "Warping")
                MovementManager.setMovementLock(true)
                MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                MovementManager.forcedActionsEnabled = false
                MovementManager.forcedUse = false
                MovementManager.forcedAttack = false

                val landedNearWarp = warp?.let { player.position().distanceTo(it.pos) <= 40.0 } == true
                if (landedNearWarp || warpTicksElapsed >= 80) {
                    pendingWarp = null
                    warpTarget = null
                    warpTicksElapsed = 0
                    pathfindingTicksElapsed = 0
                    releaseTravelInputs()
                    resetHopState()
                    burrowPos = target
                    state = State.PATHFINDING
                }
            }

            State.DIGGING -> {
                var bp = burrowPos ?: run { state = State.IDLE; return }
                promoteNearbyConfirmedBurrow(level, bp, radius = 8.0)?.let { bp = it.first }
                // bp.y is the burrow block's coordinate (it spans world Y
                // bp.y..bp.y+1). Aim at its CENTRE, not its bottom face, or the
                // look-ray passes into the block below it (the off-by-one).
                val burrowBlock = Vec3(bp.x, bp.y + 0.5, bp.z)

                MovementManager.forcedActionsEnabled = true
                // Never dig with the AOTV. Force the spade every tick and
                // cancel any pending post-cast slot restore that would swap
                // the AOTV back in mid-dig.
                castRestoreSlot = -1
                val resolvedSpade = spadeSlot
                if (resolvedSpade !in 0..8 || !isDianaSpade(player.inventory.getItem(resolvedSpade))) {
                    MovementManager.forcedUse = false
                    ChatUtils.sendMessage("Diana: lost spade, retrying.")
                    state = State.IDLE
                    return
                }
                holdSpadeSlot()
                // Always look down at the burrow block so the use actually digs it.
                aimAt(AngleUtils.getRotation(player.eyePosition, burrowBlock))

                val flatDist = kotlin.math.hypot(player.x - bp.x, player.z - bp.z)
                val onBurrow = flatDist <= digRadiusSetting.value && abs(player.y - (bp.y + 1.0)) <= 2.0

                if (!onBurrow) {
                    // Teleport landings are up to ~2 blocks off — walk onto the
                    // burrow before digging instead of using into thin air.
                    MovementManager.forcedUse = false
                    nativeWalkTo(player, Vec3(bp.x, bp.y + 1.0, bp.z), digRadiusSetting.value)
                    digApproachTicks++
                    if (digApproachTicks > digApproachTimeoutSetting.value.toInt()) {
                        stopNativeWalk()
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        ChatUtils.sendMessage("Diana: couldn't reach burrow, retrying.")
                        state = State.IDLE
                    }
                    return
                }

                // Positioned on the burrow — stop and dig.
                stopNativeWalk()
                MovementManager.setMovementLock(true)
                MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                MovementManager.forcedUse = true
                digTicksElapsed++

                // A mob can spawn from ANY burrow, including guesses/inquisitors.
                val mob = findDianaMob(bp, mobSearchRangeSetting.value)
                if (mob != null) {
                    MovementManager.forcedUse = false
                    DianaProfitTracker.onMacroDug()
                    DianaProfitTracker.onMacroMobFound()
                    startCombat(mob, rare = dianaMobName(mob) in RARE_MOB_NAMES)
                    return
                }

                if (isTreasureTarget()) {
                    when (treasureDigStage) {
                        0 -> {
                            if (digTicksElapsed >= treasureDigTicks()) {
                                MovementManager.forcedUse = false
                                treasureDigStage = 1
                                treasureWaitTicks = 0
                                digTicksElapsed = 0
                            }
                        }
                        1 -> {
                            MovementManager.forcedUse = false
                            treasureWaitTicks++
                            if (treasureWaitTicks >= treasureSecondDigDelaySetting.value.toInt()) {
                                treasureDigStage = 2
                                digTicksElapsed = 0
                            }
                        }
                        else -> {
                            if (digTicksElapsed >= treasureDigTicks()) {
                                MovementManager.forcedUse = false
                                DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                                DianaProfitTracker.onMacroDug()
                                waitTicksElapsed = 0
                                state = State.WAITING
                            }
                        }
                    }
                    return
                }

                // GUESS / MOB burrows keep digging and watching for a mob; other
                // types finish after a short dig. Guesses are now dug like any
                // real burrow rather than skipped.
                val mobLikely = burrowType == DianaParticleTracker.BurrowType.MOB ||
                    burrowType == DianaParticleTracker.BurrowType.GUESS
                if (!mobLikely && digTicksElapsed >= treasureDigTicks()) {
                    MovementManager.forcedUse = false
                    DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                    DianaProfitTracker.onMacroDug()
                    waitTicksElapsed = 0
                    state = State.WAITING
                    return
                }
                if (digTicksElapsed >= digTimeoutSetting.value.toInt()) {
                    MovementManager.forcedUse = false
                    DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                    DianaProfitTracker.onMacroDug()
                    ChatUtils.sendMessage("Diana: burrow dug (no mob), continuing.")
                    waitTicksElapsed = 0
                    state = State.WAITING
                }
            }

            State.COMBAT -> {
                castRestoreSlot = -1
                holdWeaponSlot()
                MovementManager.forcedActionsEnabled = true
                MovementManager.forcedUse = false
                MovementManager.forcedAttack = false
                combatTicksElapsed++
                val protectedRareFight = combatIsRareMob && (rareMobFocusActive() || cocoonHoldActive())
                if (!protectedRareFight && combatTicksElapsed > combatTimeoutSetting.value.toInt()) {
                    MovementManager.clearForcedMovement()
                    MovementManager.setMovementLock(false)
                    RotationExecutor.stopIfUsing(combatRotationStrategy)
                    ChatUtils.sendMessage("Diana: combat timed out, continuing.")
                    waitTicksElapsed = 0
                    state = State.WAITING
                    return
                }

                val target = (level.getEntity(targetEntityId) as? LivingEntity)
                    ?.takeIf { it.isAlive }
                    ?: if (combatIsRareMob) {
                        findRareDianaMob(rareMobFocusPos ?: player.position(), mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value)
                    } else {
                        burrowPos?.let { findDianaMob(it, mobSearchRangeSetting.value) }
                    }
                if (target == null) {
                    targetLostTicks++
                    setStatus("Reacquiring mob")
                    if (protectedRareFight && waitAtRareAnchor(player)) return
                    MovementManager.setMovementLock(true)
                    MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                    val reacquireTicks = if (combatIsRareMob) {
                        combatReacquireTicksSetting.value.toInt().coerceAtLeast(40)
                    } else {
                        combatReacquireTicksSetting.value.toInt()
                    }
                    if (targetLostTicks >= reacquireTicks) {
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        RotationExecutor.stopIfUsing(combatRotationStrategy)
                        DianaProfitTracker.onMacroKill()
                        loopsCompleted++
                        waitTicksElapsed = 0
                        state = State.WAITING
                    }
                    return
                }
                targetLostTicks = 0
                targetEntityId = target.id
                val rareTarget = dianaMobName(target) in RARE_MOB_NAMES
                if (rareTarget) {
                    combatIsRareMob = true
                    rareMobFocusPos = target.position()
                    rareMobFocusUntilMs = System.currentTimeMillis() + rareMobFocusSecondsSetting.value.toLong() * 1000L
                }

                val aimPoint = combatAimPoint(target)
                val desiredRotation = AngleUtils.getRotation(player.eyePosition, aimPoint)
                if (combatSmoothAimSetting.value) {
                    RotationExecutor.rotateTo(desiredRotation, combatRotationStrategy)
                } else {
                    RotationExecutor.rotateTo(desiredRotation, rotationStrategy)
                }
                stopNativeWalk()
                MovementManager.setMovementLock(true)
                val tdist = player.position().distanceTo(target.position())

                // Stand still and only move when the mob is genuinely out of
                // reach. Hysteresis (start vs stop) prevents step/stop jitter.
                if (combatMoving) {
                    if (tdist <= combatMoveStopSetting.value) combatMoving = false
                } else if (tdist > combatMoveStartSetting.value) {
                    combatMoving = true
                }

                MovementManager.setForcedMovement(
                    combatMoving, false, false, false,
                    combatMoving && player.horizontalCollision, false, false
                )
                val (yawErr, pitchErr) = combatRotationError(player, aimPoint)
                val aimed = yawErr <= combatAimToleranceSetting.value && pitchErr <= combatAimToleranceSetting.value
                val attackInterval = combatAttackIntervalSetting.value.toInt().coerceAtLeast(1)
                val canAttackThisTick = combatTicksElapsed - lastAttackTick >= attackInterval
                val shouldAttack = tdist <= combatAttackRangeSetting.value && aimed && canAttackThisTick
                MovementManager.forcedAttack = shouldAttack
                if (shouldAttack) lastAttackTick = combatTicksElapsed
            }

            State.WAITING -> {
                if (waitTicksElapsed == 0) MovementManager.setMovementLock(false)
                waitTicksElapsed++
                if (waitTicksElapsed >= loopWaitTicks()) {
                    state = State.IDLE
                }
            }
        }
    }

    val statusHud = hudElement("diana-macro-status", "Diana Macro Status") {
        anchor = HudAnchor.TOP_LEFT
        offsetX = 12f
        offsetY = 96f

        width { 164f }
        height { 82f }

        render { x, y, _ ->
            if (!statusHudSetting.value || !enabledSetting.value) return@render
            val theme = ThemeManager.currentTheme
            val status = if (paused) "Paused" else lastStatus
            val target = burrowPos
            val player = mc.player
            val distance = if (target != null && player != null) {
                "${player.position().distanceTo(target).toInt()}m"
            } else {
                "--"
            }
            val bph = DianaProfitTracker.snapshot().burrowsPerHour.toInt()
            NVGRenderer.rect(x, y, 164f, 82f, theme.panel, 8f)
            NVGRenderer.hollowRect(x, y, 164f, 82f, 1f, theme.controlBorder, 8f)
            NVGRenderer.text("Diana Macro", x + 9f, y + 10f, 11f, theme.text)
            NVGRenderer.text(status, x + 9f, y + 25f, 9f, if (paused) 0xFFFFAA00.toInt() else theme.accent)
            NVGRenderer.text("Target: $distance", x + 9f, y + 38f, 9f, theme.textSecondary)
            NVGRenderer.text("Loops: $loopsCompleted", x + 82f, y + 38f, 9f, theme.textSecondary)
            NVGRenderer.text("Plan: $lastPlanInfo", x + 9f, y + 51f, 9f, theme.textSecondary)
            NVGRenderer.text("BPH: $bph", x + 9f, y + 64f, 9f, theme.textSecondary)
            NVGRenderer.text(if (maxBurrowsPerHourSetting.value) "Fast" else "Normal", x + 82f, y + 64f, 9f, theme.textSecondary)
        }
    }

    @SubscribeEvent
    fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
        if (state != State.PATHFINDING && state != State.DIGGING && state != State.COMBAT) return
        val level = mc.level ?: return
        val bp = burrowPos ?: return
        OverlayRenderEngine.addBox(
            level,
            bp.x - 0.5, bp.y, bp.z - 0.5,
            bp.x + 0.5, bp.y + 1.0, bp.z + 0.5,
            fill    = OverlayRenderEngine.Color(255, 220, 0, 60),
            outline = OverlayRenderEngine.Color(255, 220, 0, 255),
            durationTicks = 10,
            tag = "diana-macro-burrow"
        )

        // Node map: planned hop path, visible through walls while travelling.
        //   cyan  = instant transmission   (lighter = mid-air sky chain)
        //   magenta = etherwarp (near-burrow finisher)
        //   grey  = walk bridge
        if (showNodeMapSetting.value && state == State.PATHFINDING && hops.isNotEmpty()) {
            val player = mc.player
            var prev = player?.let { Vec3(it.x, it.y + 0.9, it.z) }
                ?: hops.first().standPos()
            hops.forEachIndexed { i, h ->
                val c = h.standPos()
                val col = when (h.type) {
                    DianaTeleportPlanner.HopType.WALK ->
                        OverlayRenderEngine.Color(160, 160, 170, 255)
                    DianaTeleportPlanner.HopType.AOTV ->
                        if (h.airborne) OverlayRenderEngine.Color(120, 235, 255, 255)
                        else OverlayRenderEngine.Color(0, 200, 255, 255)
                    DianaTeleportPlanner.HopType.ETHERWARP ->
                        OverlayRenderEngine.Color(255, 60, 230, 255)
                }
                // Connecting beam between hop points (cast trajectory).
                OverlayRenderEngine.addLine(
                    level,
                    prev.x, prev.y, prev.z,
                    c.x, c.y + 0.9, c.z,
                    col, 3.0f, 10, "diana-node-map", true
                )
                if (h.airborne) {
                    // Air node: small marker at the sky point.
                    OverlayRenderEngine.addBox(
                        level,
                        c.x - 0.2, c.y - 0.2, c.z - 0.2,
                        c.x + 0.2, c.y + 0.2, c.z + 0.2,
                        fill = OverlayRenderEngine.Color(col.r, col.g, col.b, 90),
                        outline = col, lineWidth = 1.5f,
                        durationTicks = 10, tag = "diana-node-map",
                        forceRender = true
                    )
                } else {
                    // Ground node: full standing block + a beacon so it's easy
                    // to spot which blocks the route lands on.
                    OverlayRenderEngine.addBox(
                        level,
                        c.x - 0.5, c.y, c.z - 0.5,
                        c.x + 0.5, c.y + 1.0, c.z + 0.5,
                        fill = OverlayRenderEngine.Color(col.r, col.g, col.b, 55),
                        outline = col, lineWidth = 2.0f,
                        durationTicks = 10, tag = "diana-node-map",
                        forceRender = true
                    )
                    OverlayRenderEngine.addLine(
                        level,
                        c.x, c.y, c.z, c.x, c.y + 2.5, c.z,
                        col, 2.0f, 10, "diana-node-map", true
                    )
                }
                if (i == hops.lastIndex) {
                    // Final hop: bright marker on the burrow approach.
                    OverlayRenderEngine.addBox(
                        level,
                        c.x - 0.5, c.y, c.z - 0.5,
                        c.x + 0.5, c.y + 1.0, c.z + 0.5,
                        fill = OverlayRenderEngine.Color(255, 255, 255, 70),
                        outline = OverlayRenderEngine.Color(255, 255, 255, 255),
                        lineWidth = 2.5f, durationTicks = 10,
                        tag = "diana-node-map", forceRender = true
                    )
                }
                prev = Vec3(c.x, c.y + 0.9, c.z)
            }
        }
    }
}
