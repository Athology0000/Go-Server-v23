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
import org.phantom.api.event.impl.client.ChatEvent
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
import org.phantom.api.pathfinder.jni.PathExecutorState
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
import org.phantom.internal.rotation.PhantomRotation
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
    // Hypixel emits "Defeat all the burrow defenders in order to dig it!" when
    // a defender mob spawns during a dig. The burrow can NOT be dug until that
    // defender is dead. Without this gate, the DIGGING handler's per-tick
    // findDianaMob() would jump straight to COMBAT for any Diana mob roaming
    // within 14 blocks (leftover from a neighbouring burrow, a rare wandering
    // by, etc.) and leave the current burrow undug — the user-reported "goes
    // straight to combat without checking the burrow was mined". This flag is
    // the authoritative signal that defender combat is required; only after
    // it fires (or "You dug out" fires) does DIGGING allow the COMBAT switch.
    // Reset on entering DIGGING fresh.
    private var defendersRequired = false
    // True while we are fighting a defender that interrupted DIGGING. When the
    // defender dies, COMBAT routes back to DIGGING on the SAME burrow instead
    // of WAITING — otherwise the spade-held burrow is abandoned and the next
    // cycle starts holding the weapon (the "wrong tool in hand" symptom).
    private var combatBlockedDig = false
    // Set true once Hypixel confirms the burrow physically broke ("You dug out
    // a Griffin Burrow!"). After this, a small grace window lets a post-dig
    // mob spawn and be detected before moving on to WAITING.
    private var burrowDugConfirmed = false
    private var burrowDugConfirmedAtTick = 0
    private val POST_DUG_MOB_GRACE_TICKS = 10

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
    // Dynamic replan during chain execution: every REPLAN_INTERVAL_MS the
    // executor submits a fresh plan from the player's CURRENT position. When
    // it returns, if the new plan reaches the goal in strictly fewer hops
    // than what's left of the active plan, we switch to it and announce the
    // improvement in chat ("better aotv route: N vs M remaining"). The
    // existing planner instance is reused — there's only one DianaTeleportPlanner.future
    // at a time, but the active plan lives in `hops` independently, so a
    // background replan doesn't disturb execution until we choose to swap.
    private var lastReplanSubmittedMs = 0L
    private var replanInFlight = false
    // Cooldown after a successful plan SWAP. Stops the macro from flipping
    // between two near-equal plans every replan tick — the user-reported
    // "warps to a spot and warps back" oscillation.
    private var lastReplanCommittedMs = 0L
    private var castRestoreSlot = -1
    private var nativeWalkTarget: Vec3? = null
    private var nativeWalkRadius = 0.0
    // Counts ticks spent at a GUESS waiting for particles to confirm the real
    // burrow location nearby. Diana's compass guess is often a few blocks off
    // the actual burrow — mining the guess directly usually swings the spade
    // at empty ground. SkyBlock Overhaul's flow is: arrive at the guess, see
    // if particles tick in, mine the confirmed spot. We replicate that here.
    private var guessConfirmTicks = 0
    // Auto-heal pacing. Last time-millis we right-clicked a heal item, plus
    // a transient "restore weapon slot in N ticks" so the macro doesn't keep
    // holding the wand after the cast.
    private var lastHealUseMs = 0L
    private var healRestoreSlot = -1
    private var healRestoreInTicks = 0
    // Burst-and-lull combat click cadence. A real player doesn't click at a
    // perfect 5 CPS metronome; they spam in short clusters, take a beat,
    // spam again. nextClickIntervalTicks holds the gap before the next
    // click; burstClicksRemaining counts how many fast clicks are left in
    // the current cluster. When the cluster ends, the cadence widens for
    // one or two clicks before a new cluster starts. Re-rolled per click.
    private var burstClicksRemaining = 0
    private var nextClickIntervalTicks = 3
    // Burst-and-lull cadence for AOTV / etherwarp cast cooldown. A real
    // chain looks like 2-4 quick taps then a tiny pause; the old uniform
    // random between min/max didn't have any of that texture. The counter
    // ticks down per cast — while > 0, the next cooldown is near the floor;
    // when it hits 0, one cast gets a long-end cooldown and the counter
    // re-rolls for the next cluster.
    private var castBurstRemaining = 0
    // Hysteresis for the "running into a wall, hop over it" fallback. The
    // native path executor's JumpDetector already handles real obstacles from
    // cached path nodes; this counter exists only to catch live-world cases
    // the planner missed (entity in the way, mid-cast stand-up against a
    // partial block). One tick of horizontalCollision is meaningless —
    // anything brushing a wall trips it — so we require sustained contact
    // before firing, and cool down briefly after each hop.
    private var collisionStuckTicks = 0
    private var lastFallbackJumpMs = 0L
    private var pendingWarp: WarpPoint? = null
    private var warpTarget: Vec3? = null
    private var warpTicksElapsed = 0
    private var warpAttemptedFor: Vec3? = null
    private var preGuessMoveTarget: Vec3? = null
    // Set at warp completion when the landing spot has a ceiling within
    // POST_WARP_CEILING_CHECK_Y blocks. While this is set, PATHFINDING walks
    // toward the target with no fly mode — the macro is required to leave
    // the enclosure (Hub Crypts is the worst case) before climbing, so the
    // planner doesn't keep trying to AOTV through stone walls.
    private var postWarpWalkUntilMs = 0L

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
        "Dig Timeout", "Safety cap on ticks per burrow before forcibly continuing — the macro already advances on the 'You dug out a Griffin Burrow!' chat line, so this only fires when chat is missed (rare).",
        40.0, 20.0, 160.0, step = 5.0
    )
    private val digApproachTimeoutSetting = SliderSetting(
        "Dig Approach Timeout", "Max ticks to walk onto a burrow after travel.", 50.0, 20.0, 160.0, step = 5.0
    )
    private val digRadiusSetting = SliderSetting(
        "Dig Radius", "How close the macro must stand before digging a burrow.", 1.55, 1.0, 2.3, step = 0.05
    )
    private val fastTreasureDigTicksSetting = SliderSetting(
        "Fast Treasure Dig Ticks", "Use ticks before finishing non-mob burrows in Max Burrows/Hour mode.", 4.0, 3.0, 14.0, step = 1.0
    )
    private val treasureSecondDigDelaySetting = SliderSetting(
        "Treasure Second Dig Delay", "Ticks to wait before mining the same treasure burrow spot again.", 12.0, 5.0, 60.0, step = 1.0
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
        "Close Walk Distance", "Walk to any burrow target within this many blocks instead of starting a teleport plan. Raise this to prefer walking over AOTV/etherwarp.", 32.0, 4.0, 80.0, step = 1.0
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
        "Render Path Nodes", "Draw a thin beam through the planned AOTV / etherwarp / walk hops while travelling.", true
    )
    private val showBurrowMarkerSetting = CheckboxSetting(
        "Render Burrow", "Draw a marker on the current target burrow.", true
    )
    private val simplifyNodesSetting = CheckboxSetting(
        "Simplify Nodes", "Clean up duplicate/collinear generated Diana route nodes.", true
    )
    private val plannerGoalRadiusSetting = SliderSetting(
        "Planner Goal Radius", "How close generated nodes need to get to the burrow.", 2.0, 1.0, 4.0, step = 0.25
    )
    private val transmissionRangeSetting = SliderSetting(
        "Transmission Range", "Instant Transmission range per cast (Hypixel: 12 blocks).", 12.0, 6.0, 15.0, step = 0.5
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
        55.0, 0.0, 400.0, step = 10.0
    )
    private val castCdMinSetting = SliderSetting(
        "Cast Cooldown Min (ms)", "Minimum delay between teleport casts.", 165.0, 80.0, 600.0, step = 5.0
    )
    private val castCdMaxSetting = SliderSetting(
        "Cast Cooldown Max (ms)", "Maximum delay between teleport casts (randomised with min).",
        235.0, 80.0, 800.0, step = 5.0
    )
    private val combatMoveStartSetting = SliderSetting(
        "Combat Move Start", "Start walking toward a Diana mob past this distance.", 5.5, 2.0, 14.0, step = 0.25
    )
    private val combatMoveStopSetting = SliderSetting(
        "Combat Move Stop", "Stop walking once back inside this distance.", 4.0, 1.0, 12.0, step = 0.25
    )
    private val combatAttackRangeSetting = SliderSetting(
        "Combat Attack Range", "Attack Diana mobs within this distance. Hypixel resolves hits server-side based on the WEAPON's reach (~6 blocks for melee); a wider setting still spams clicks but they miss the hitbox so damage never lands.", 6.0, 2.0, 14.0, step = 0.25
    )
    private val combatSmoothAimSetting = CheckboxSetting(
        "Combat Smooth Aim", "Use Diana-specific smooth target tracking during combat.", true
    )
    private val combatAimSpeedSetting = SliderSetting(
        "Combat Aim Speed", "Max combat yaw degrees per frame before Bezier easing.", 13.5, 2.0, 22.0, step = 0.25
    )
    private val combatPitchSpeedSetting = SliderSetting(
        "Combat Pitch Speed", "Max combat pitch degrees per frame before Bezier easing.", 10.0, 2.0, 18.0, step = 0.25
    )
    private val combatAimSmoothingSetting = SliderSetting(
        "Combat Aim Smoothing", "How quickly the tracked aim point follows mob movement. Higher = snappier.", 0.62, 0.10, 1.0, step = 0.02
    )
    private val combatPredictTicksSetting = SliderSetting(
        "Combat Prediction", "Ticks of mob velocity to lead while aiming.", 1.25, 0.0, 4.0, step = 0.25
    )
    private val combatAimHeightSetting = SliderSetting(
        "Combat Aim Height", "Fraction of mob height to aim at.", 0.78, 0.35, 0.95, step = 0.01
    )
    private val combatAimToleranceSetting = SliderSetting(
        "Combat Aim Tolerance", "Max yaw/pitch error before the macro attacks.", 3.5, 1.0, 15.0, step = 0.25
    )
    private val combatAttackIntervalSetting = SliderSetting(
        "Combat Attack Interval", "Ticks between attack presses while fighting. 3 ≈ 6.7 CPS, 4 ≈ 5 CPS.", 3.0, 1.0, 10.0, step = 1.0
    )
    private val autoHealSetting = CheckboxSetting(
        "Auto Heal", "Auto-use a healing item (Wand of Atonement / Healing) when HP drops below the threshold during combat.", true
    )
    private val healThresholdPctSetting = SliderSetting(
        "Heal Threshold %", "Use the heal item once effective HP (health + absorption) drops below this percent of max.", 60.0, 10.0, 95.0, step = 5.0
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
    // Healing-item name fragments. The macro scans the hotbar for an item
    // whose display name contains any of these and right-clicks it when HP
    // drops below the threshold during a Diana mob fight.
    private val HEAL_NAME_FRAGMENTS = listOf(
        "wand of atonement",
        "wand of healing",
        "wand of restoration",
        "wand of strength",
        "blood vampire",
        "healing potion",
    )
    // Min gap between auto-heal right-clicks so we don't spam the wand and
    // waste mana. Wand of Atonement's cooldown is ~5 s server-side; this is
    // a client-side soft cap on top of that.
    private const val AUTO_HEAL_COOLDOWN_MS = 1_500L
    // How many combat ticks after a heal cast before we swap back to the
    // weapon slot. ~6 ticks (300 ms) is enough for the use packet to reach
    // the server and for the heal animation to register.
    private const val HEAL_RESTORE_TICKS = 6
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
    // Sustained-collision fallback-hop tuning. 6 ticks ≈ 300 ms of contact
    // before we override the path executor; 450 ms cooldown stops the same
    // obstacle from re-firing on the landing tick.
    private const val STUCK_HOP_TICKS = 6
    private const val FALLBACK_HOP_COOLDOWN_MS = 450L
    // Spade reach: once horizontally within this many blocks of a burrow,
    // start holding USE while approaching so the dig fires the instant the
    // crosshair lands on the block (and on Hypixel's wider hit-resolve).
    private const val DIG_REACH = 5.0
    // Confirmed-burrow particle staleness budget on DIGGING entry: anything
    // older than this is treated as expired and dropped before we waste a
    // dig on it.
    private const val BURROW_STALENESS_MS = 5_000L
    // Post-warp anti-clipping tuning. After a warp lands, if any solid block
    // sits within POST_WARP_CEILING_CHECK_Y blocks above the player's head,
    // we consider the landing "covered" and walk for up to POST_WARP_WALK_MS
    // before any teleport plan is submitted. Tuned to Hub Crypts: ceilings
    // there are ~4 blocks above the warp landing, exits are ~15 blocks of
    // walking away, so 4 s buys plenty of time to clear the enclosure.
    private const val POST_WARP_CEILING_CHECK_Y = 6
    private const val POST_WARP_WALK_MS = 4_000L
    // Guess-confirmation phase tuning. After arriving at a GUESS / SUB_GUESS
    // burrow we hold position and look for confirming particles within this
    // radius for up to GUESS_CONFIRM_TICKS ticks. Particles only emit when
    // the player is in render range of the burrow, so the wait has to be
    // long enough for ~1-2 particle ticks to arrive (Hypixel particle pings
    // are ~10 Hz) but short enough that an actually-wrong guess gets dropped
    // before the macro burns more time than it would have just digging.
    private const val GUESS_CONFIRM_RADIUS = 8.0
    private const val GUESS_CONFIRM_TICKS = 35
    // Fallback Diana mob detection: how close a non-ArmorStand LivingEntity
    // must be to a named Diana ArmorStand for the two to count as "the same
    // mob". 2.5 is loose enough to handle the stand drifting slightly above
    // the mob and tight enough to never glue onto a random other entity.
    private const val MOB_PROXIMITY_BLOCKS = 2.5
    // Approach-aim transition: above this horizontal distance the macro
    // looks forward at eye height while walking in; below it, the aim
    // pitches down onto the burrow block. Smaller = more human (later tilt-
    // down) but increases the risk the spade fires a frame or two too early
    // if rotation lags behind walking — 2.5 keeps the dig accurate while
    // covering the visible "stares at the ground from 5 blocks out" tell.
    private const val BURROW_AIM_DOWN_DIST = 2.5
    // After a successful mid-chain plan swap, lock out further swaps for this
    // long. Without it, two near-equal plans could ping-pong every 1.5 s
    // (each replan briefly satisfies the strict-improvement check from a
    // different player position), producing the warp-forward-warp-back loop.
    private const val REPLAN_SWAP_COOLDOWN_MS = 6_000L

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
        maxYawStep = 9f,
        maxPitchStep = 9f,
        curveIn = 0.38f,
        curveOut = 0.82f,
        minScale = 0.34f,
        // No snap — user wants the camera to ease all the way in instead of
        // closing the last fraction of a degree in a single frame. The fire
        // gate (1.5°) tolerates this; the rotation just glides into target.
        snapThreshold = 0.0f,
    )

    /**
     * Dedicated approach-aim strategy used while walking onto a burrow.
     * Distinct from rotationStrategy / combatRotationStrategy: this one is
     * deliberately slow and never snaps, because the visible un-human tell
     * on the approach was the rotation finishing each correction in a
     * single frame. Slow max step + low minScale + zero snap = the camera
     * eases continuously throughout the whole walk-in instead of locking on
     * and freezing.
     */
    private val approachRotationStrategy = BezierTrackingRotationStrategy(
        maxYawStep = 4.2f,
        maxPitchStep = 3.2f,
        curveIn = 0.32f,
        curveOut = 0.88f,
        minScale = 0.18f,
        snapThreshold = 0.0f,
    )

    private val combatRotationStrategy = BezierTrackingRotationStrategy(
        yawStepSampler = {
            val base = combatAimSpeedSetting.value
            val capped = if (maxBurrowsPerHourSetting.value) base.coerceAtLeast(13.0) else base
            RotationsModule.sample(Pair(capped * 0.88, capped * 1.12)).toFloat()
        },
        pitchStepSampler = {
            val base = combatPitchSpeedSetting.value
            val capped = if (maxBurrowsPerHourSetting.value) base.coerceAtLeast(10.0) else base
            RotationsModule.sample(Pair(capped * 0.88, capped * 1.12)).toFloat()
        },
        // Bezier shape: low curveIn = aggressive snap toward target, high
        // curveOut = soft tail so the camera doesn't overshoot at settle.
        // minScale prevents the easing from collapsing per-frame movement to
        // a crawl when error is small — keeps the rotation responsive even
        // a few degrees off the target instead of asymptoting in slowly.
        // snapThreshold (0.6°) closes the last fraction of a degree in a
        // single frame so the aim lands exactly on the mob's hitbox instead
        // of asymptoting in for several ticks — the visible "almost on
        // target" wobble that was costing hits.
        curveInProvider = { 0.10f },
        curveOutProvider = { 0.92f },
        minScaleProvider = { 0.40f },
        snapThresholdProvider = { 0.6f },
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
            smoothAimSetting, statusHudSetting, showNodeMapSetting, showBurrowMarkerSetting, travelDebugSetting,
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
        postWarpWalkUntilMs = 0L
        guessConfirmTicks = 0
        defendersRequired = false
        combatBlockedDig = false
        burrowDugConfirmed = false
        burrowDugConfirmedAtTick = 0
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
        lastReplanSubmittedMs = 0L
        replanInFlight = false
        // Prime a fresh burst so the very first cast of a new chain fires
        // quickly (engagement reaction), not on a slow opener.
        castBurstRemaining = Random.nextInt(2, 5)
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
        collisionStuckTicks = 0
        // NativePathfinder.stop() doesn't release the block rotation
        // controller — only PathCommand.applyToPlayer does, and applyToPlayer
        // never runs once the macro switches state (combat / dig / wait). The
        // controller keeps writing the LAST path aim into rotation every
        // frame, which fights combatRotationStrategy and prevents the macro
        // from actually looking at the mob.
        if (PathExecutorState.blockRotationOwned) {
            PhantomRotation.blockController.releaseWhenSettled(maxFrames = 12)
            PathExecutorState.blockRotationOwned = false
            PathExecutorState.blockRotationLastTarget = null
        }
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

    /**
     * Straight-line "is walking out viable" probe. Raycasts from the player's
     * eye toward the target position. Returns true if a real wall blocks the
     * line — i.e., the native walker would have to find some non-obvious
     * route around it. Foliage (leaves, tall grass, ferns) has empty
     * collider shapes and does NOT count as blocking here, but tree TRUNKS,
     * fences, and walls do. Used to suppress the walking bias when the
     * planner's teleport hops are the only viable exit.
     */
    private fun isStraightLineBlocked(
        level: net.minecraft.world.level.Level,
        player: net.minecraft.client.player.LocalPlayer,
        target: Vec3,
    ): Boolean {
        val eye = player.eyePosition
        val aim = Vec3(target.x, target.y + 0.5, target.z)
        val hit = level.clip(
            ClipContext(eye, aim, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)
        )
        return hit.type == HitResult.Type.BLOCK && hit.location.distanceTo(aim) > 1.5
    }

    /**
     * Raycast from the player's eye to the burrow block centre, against the
     * COLLIDER shape (solid walls only — foliage has empty colliders and is
     * ignored). Returns true if a real wall stands between the player and
     * the burrow, in which case the macro must walk around / closer before
     * swinging — otherwise the spade attack just chips the wall block and
     * the burrow itself never breaks.
     */
    private fun isSolidWallBlocking(
        level: net.minecraft.world.level.Level,
        player: net.minecraft.client.player.LocalPlayer,
        burrowBlock: Vec3,
    ): Boolean {
        val eye = player.eyePosition
        val hit = level.clip(
            ClipContext(eye, burrowBlock, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)
        )
        if (hit.type != HitResult.Type.BLOCK) return false
        val pos = (hit as net.minecraft.world.phys.BlockHitResult).blockPos
        // Hit landed on the burrow column itself — no wall between.
        return !(pos.x == floor(burrowBlock.x).toInt() &&
            pos.z == floor(burrowBlock.z).toInt() &&
            pos.y == floor(burrowBlock.y).toInt())
    }

    /**
     * Raycast from the player's eye to the burrow block centre. Returns true
     * when something (grass, fern, flower, sapling, vines) sits in the way
     * before the burrow's column — i.e., would steal a right-click. The
     * pick-ray uses OUTLINE because most plants have empty COLLIDER shapes
     * but non-empty OUTLINE / interaction shapes (which is exactly what
     * Hypixel's spade hit-resolve sees).
     */
    private fun isFoliageBlocking(
        level: net.minecraft.world.level.Level,
        player: net.minecraft.client.player.LocalPlayer,
        burrowBlock: Vec3,
    ): Boolean {
        val eye = player.eyePosition
        val hit = level.clip(
            ClipContext(eye, burrowBlock, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)
        )
        if (hit.type != HitResult.Type.BLOCK) return false
        val pos = (hit as net.minecraft.world.phys.BlockHitResult).blockPos
        // If the ray hit the burrow's column at the burrow's Y, we're good.
        if (pos.x == floor(burrowBlock.x).toInt() &&
            pos.z == floor(burrowBlock.z).toInt() &&
            pos.y == floor(burrowBlock.y).toInt()
        ) {
            return false
        }
        // Foliage signature: OUTLINE stopped the ray (we got here) but
        // COLLIDER is empty — i.e., the block is pickable but you can walk
        // through it. Tall grass, ferns, flowers, saplings, vines, bushes
        // all match. Solid blocks (grass_block, dirt, stone) have non-empty
        // colliders and never trigger the clear pass.
        val state = level.getBlockState(pos)
        return state.getCollisionShape(level, pos).isEmpty
    }

    /**
     * True when there's a solid block within POST_WARP_CEILING_CHECK_Y blocks
     * above the player's eye — i.e., we're under a ceiling. Used to decide
     * whether a freshly-landed warp should walk out before the planner is
     * allowed to schedule airborne hops.
     */
    private fun hasCeilingOverhead(player: net.minecraft.client.player.LocalPlayer): Boolean {
        val level = mc.level ?: return false
        val px = floor(player.x).toInt()
        val pz = floor(player.z).toInt()
        val eyeY = floor(player.eyeY).toInt()
        for (dy in 1..POST_WARP_CEILING_CHECK_Y) {
            val pos = BlockPos(px, eyeY + dy, pz)
            val state = level.getBlockState(pos)
            if (!state.getCollisionShape(level, pos).isEmpty) return true
        }
        return false
    }

    /** Native ground approach while the teleport planner has nothing usable. */
    private fun directApproach(player: net.minecraft.client.player.LocalPlayer, target: Vec3) {
        MovementManager.setMovementLock(true)
        nativeWalkTo(player, target)
        maybeFallbackJump(player)
    }

    /**
     * Sustained-collision hop fallback. Only fires after the player has been
     * pressed against geometry on the ground for STUCK_HOP_TICKS in a row,
     * with a cooldown so a single obstacle doesn't trigger a flurry of jumps.
     * Without the hysteresis a glancing wall contact during normal pathing
     * read as "must jump" and the macro hopped at every corner.
     */
    private fun maybeFallbackJump(player: net.minecraft.client.player.LocalPlayer) {
        if (player.horizontalCollision && player.onGround()) {
            collisionStuckTicks++
        } else {
            collisionStuckTicks = 0
            return
        }
        val now = System.currentTimeMillis()
        if (collisionStuckTicks >= STUCK_HOP_TICKS && now - lastFallbackJumpMs > FALLBACK_HOP_COOLDOWN_MS) {
            MovementManager.forcedJump = true
            lastFallbackJumpMs = now
            collisionStuckTicks = 0
        }
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

    /**
     * Wall-clip-aware acceptance gate for planner output. Counts how many
     * AOTV/etherwarp hops in the plan have a line-of-sight broken by a real
     * wall (foliage is not counted — COLLIDER shapes only). If the plan is
     * meaningfully wall-clip-heavy AND the target is within walking range,
     * we return an empty list — the caller will fall back to native walking.
     *
     * Why the asymmetric rule:
     *  • In Hub Crypts / Wizard Tower the player NEEDS wall-clipping AOTV
     *    hops to escape the building. The target burrow is 100+ blocks
     *    away, so the "within walking range" gate keeps that plan intact.
     *  • In open terrain, the user-reported "uses too much instant and not
     *    enough walking" came from the planner generating chains that hop
     *    over short walls instead of going around. Those plans are now
     *    rejected here and replaced by a direct walk.
     */
    private fun sanitizePlannerHops(
        level: net.minecraft.world.level.Level,
        raw: List<DianaTeleportPlanner.Hop>,
    ): List<DianaTeleportPlanner.Hop> {
        if (raw.size <= 2) return raw
        val player = mc.player ?: return raw
        // Total path length: if this is a long-range escape (Crypts → field),
        // through-wall AOTV is the actual mechanism and we leave the plan be.
        val first = raw.first().standPos()
        val last = raw.last().standPos()
        val pathSpan = first.distanceTo(last)
        if (pathSpan > 60.0) return raw

        var wallClipHops = 0
        for (i in 1 until raw.size) {
            val a = raw[i - 1].standPos().add(0.0, 0.9, 0.0)
            val b = raw[i].standPos().add(0.0, 0.9, 0.0)
            val type = raw[i].type
            if (type != DianaTeleportPlanner.HopType.AOTV &&
                type != DianaTeleportPlanner.HopType.ETHERWARP
            ) continue
            val hit = level.clip(
                ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)
            )
            if (hit.type == HitResult.Type.BLOCK && hit.location.distanceTo(b) > 1.0) {
                wallClipHops++
            }
        }
        return if (wallClipHops > 0) emptyList() else raw
    }

    /**
     * Raycast from `from` to `to` against world collision. If a solid block
     * sits between them, returns the hit position (the visible end of the
     * beam segment); otherwise returns `to`. Used by the node-map renderer to
     * clip beams at walls instead of drawing straight through stone.
     */
    private fun firstWallHit(level: net.minecraft.world.level.Level, from: Vec3, to: Vec3): Vec3 {
        val player = mc.player ?: return to
        val hit = level.clip(
            ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)
        )
        return if (hit.type == HitResult.Type.BLOCK) hit.location else to
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
        val box = AABB(center, center).inflate(range)
        val living = level.getEntitiesOfClass(LivingEntity::class.java, box)
        // Primary path: the mob's own name (or its vehicle / passenger
        // ArmorStand label, via dianaMobName's tree walk) matches a Diana
        // species. Skip ArmorStands as combat targets — they're name tags,
        // not the real hitbox.
        val direct = living
            .filter { it.isAlive && it !is net.minecraft.world.entity.decoration.ArmorStand && dianaMobName(it) != null }
            .minWithOrNull(
                compareByDescending<LivingEntity> { if (dianaMobName(it) in RARE_MOB_NAMES) 1 else 0 }
                    .thenBy { it.position().distanceToSqr(center) }
            )
        if (direct != null) return direct
        // Fallback: some Hypixel Diana mobs render the name tag on a
        // standalone ArmorStand floating right next to the mob rather than
        // riding it. The mob entity itself has no Diana name on its tree, so
        // the primary path misses it. Find any named ArmorStand in range
        // whose label matches a Diana species, then pick the nearest non-
        // stand LivingEntity within MOB_PROXIMITY_BLOCKS of it.
        return matchByProximityStand(level, living, box)
    }

    private fun findRareDianaMob(center: Vec3, range: Double): LivingEntity? {
        val level = mc.level ?: return null
        val box = AABB(center, center).inflate(range)
        val living = level.getEntitiesOfClass(LivingEntity::class.java, box)
        val direct = living
            .filter { it.isAlive && it !is net.minecraft.world.entity.decoration.ArmorStand && dianaMobName(it) in RARE_MOB_NAMES }
            .minByOrNull { it.position().distanceToSqr(center) }
        if (direct != null) return direct
        return matchByProximityStand(level, living, box, rareOnly = true)
    }

    private fun matchByProximityStand(
        level: net.minecraft.world.level.Level,
        living: List<LivingEntity>,
        box: AABB,
        rareOnly: Boolean = false,
    ): LivingEntity? {
        val stands = level.getEntitiesOfClass(
            net.minecraft.world.entity.decoration.ArmorStand::class.java, box
        ).filter {
            val name = ChatFormatting.stripFormatting(
                it.customName?.string ?: it.displayName.string
            ).orEmpty()
            val match = DIANA_MOB_NAMES.firstOrNull { n -> name.contains(n, ignoreCase = true) }
            match != null && (!rareOnly || match in RARE_MOB_NAMES)
        }
        if (stands.isEmpty()) return null
        var best: LivingEntity? = null
        var bestDistSq = MOB_PROXIMITY_BLOCKS * MOB_PROXIMITY_BLOCKS
        for (stand in stands) {
            for (e in living) {
                if (e is net.minecraft.world.entity.decoration.ArmorStand) continue
                if (!e.isAlive) continue
                val dSq = e.position().distanceToSqr(stand.position())
                if (dSq < bestDistSq) {
                    bestDistSq = dSq
                    best = e
                }
            }
        }
        return best
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

    /**
     * Called when COMBAT ends (target dead or timed out). If a defender
     * interrupted DIGGING (combatBlockedDig), resume digging the SAME burrow
     * with the spade back in hand — the burrow is still intact and waiting for
     * the player. Otherwise fall through to the normal post-kill WAITING.
     */
    private fun resumeAfterCombat() {
        targetEntityId = -1
        combatIsRareMob = false
        if (combatBlockedDig && burrowPos != null && !burrowDugConfirmed) {
            combatBlockedDig = false
            defendersRequired = false
            holdSpadeSlot()
            // Reset per-burrow dig progress so the dig timeout / treasure
            // stage starts fresh now that the defender is dead.
            digTicksElapsed = 0
            digApproachTicks = 0
            treasureDigStage = 0
            treasureWaitTicks = 0
            state = State.DIGGING
            return
        }
        combatBlockedDig = false
        waitTicksElapsed = 0
        state = State.WAITING
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
        healRestoreInTicks = 0
        healRestoreSlot = -1
        // Seed the cadence so the very first cluster of clicks on a fresh
        // mob is a burst (engagement reaction) rather than a slow opener.
        burstClicksRemaining = Random.nextInt(3, 7)
        nextClickIntervalTicks = burstIntervalTicks()
        RotationExecutor.stopIfUsing(combatRotationStrategy)
    }

    /**
     * Re-roll the next-click interval after a click fires. While
     * burstClicksRemaining > 0 we stay tight (2-3 ticks ≈ 7-10 CPS); when
     * the burst empties we sample a wider gap (5-9 ticks ≈ 2-4 CPS) and
     * queue a fresh burst behind it. Result: short clusters of quick
     * clicks separated by a noticeable beat, which is what a person
     * actually does when mashing.
     */
    private fun rollNextClickInterval() {
        if (burstClicksRemaining > 0) {
            burstClicksRemaining--
            nextClickIntervalTicks = burstIntervalTicks()
        } else {
            // End of cluster: one slower "settle" interval, then prime the
            // next burst (re-roll count).
            nextClickIntervalTicks = Random.nextInt(5, 10)
            burstClicksRemaining = Random.nextInt(3, 7)
        }
    }

    /**
     * Per-click interval inside a burst. Uses combatAttackIntervalSetting as
     * the FLOOR (so a user who explicitly slows the cps still sees their
     * floor respected) and adds a small ±1-tick jitter above it.
     */
    private fun burstIntervalTicks(): Int {
        val floor = combatAttackIntervalSetting.value.toInt().coerceAtLeast(2)
        return floor + Random.nextInt(0, 2)
    }

    private fun combatAimPoint(target: LivingEntity): Vec3 {
        val prediction = combatPredictTicksSetting.value
        val bb = target.bbHeight.toDouble()
        val height = if (bb < 0.35) bb * 0.5
                     else (bb * combatAimHeightSetting.value).coerceIn(0.35, min(1.85, bb))
        // Lead horizontal motion only. The Cretan Bull and a few other
        // Diana mobs hop continuously — their deltaMovement.y swings between
        // +0.4 (jump start) and -1.0+ (falling) every couple of ticks. With
        // the old `.scale(prediction)` that fed straight into the Y aim, the
        // pitch whipped up and down a degree per tick and combat looked like
        // a seizure. Y velocity is short-lived (jumps last <1s) and the
        // height offset already covers the body, so ignore it entirely.
        val dv = target.deltaMovement
        val raw = target.position()
            .add(dv.x * prediction, 0.0, dv.z * prediction)
            .add(0.0, height, 0.0)
        if (!combatSmoothAimSetting.value) {
            smoothedCombatAim = raw
            return raw
        }
        val previous = smoothedCombatAim
        val alpha = combatAimSmoothingSetting.value.coerceIn(0.10, 1.0)
        // Per-axis smoothing. Horizontal (XZ) tracks at the configured alpha
        // so leading a strafing mob still feels responsive. Vertical (Y) is
        // capped at a much slower rate when the target is airborne so a
        // jumping mob's feet-Y bouncing doesn't translate into pitch whip.
        // Once the mob lands the Y alpha goes back to normal and the aim
        // catches up smoothly.
        val airborne = !target.onGround()
        val alphaY = if (airborne) alpha.coerceAtMost(0.12) else alpha
        val smoothed = if (previous == null || previous.distanceToSqr(raw) > 16.0) {
            raw
        } else {
            val dx = (raw.x - previous.x) * alpha
            val dy = (raw.y - previous.y) * alphaY
            val dz = (raw.z - previous.z) * alpha
            Vec3(previous.x + dx, previous.y + dy, previous.z + dz)
        }
        smoothedCombatAim = smoothed
        return smoothed
    }

    private fun combatRotationError(player: net.minecraft.client.player.LocalPlayer, aimPoint: Vec3): Pair<Float, Float> {
        val desired = AngleUtils.getRotation(player.eyePosition, aimPoint)
        return abs(AngleUtils.getRotationDelta(player.yRot, desired.yaw)) to abs(player.xRot - desired.pitch)
    }

    private fun dianaMobName(entity: LivingEntity): String? {
        // Hypixel renders Diana mobs as a vanilla creature (Husk, Skeleton, etc.)
        // with a name-tagged armor stand passenger carrying the "✦ Minos
        // Inquisitor"-style label. The entity's own displayName is the vanilla
        // mob name, so the macro was never recognising any mob as a Diana
        // target and the COMBAT state never fired. Scan the entity, every
        // passenger (recursively), and the vehicle it's riding so we catch
        // the label no matter which slot Hypixel parks it on.
        val candidates = ArrayDeque<net.minecraft.world.entity.Entity>()
        candidates += entity
        entity.vehicle?.let { candidates += it }
        val seen = HashSet<Int>()
        while (candidates.isNotEmpty()) {
            val e = candidates.removeFirst()
            if (!seen.add(e.id)) continue
            val raw = e.customName?.string ?: e.displayName.string
            val name = ChatFormatting.stripFormatting(raw).orEmpty()
            val match = DIANA_MOB_NAMES.firstOrNull { n -> name.contains(n, ignoreCase = true) }
            if (match != null) return match
            for (p in e.passengers) candidates += p
        }
        return null
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

    private fun findHealHotbarSlot(player: net.minecraft.client.player.LocalPlayer): Int {
        for (i in 0..8) {
            val stack = player.inventory.getItem(i)
            if (stack.isEmpty) continue
            val name = ChatFormatting.stripFormatting(stack.hoverName.string).orEmpty().lowercase()
            if (HEAL_NAME_FRAGMENTS.any { it in name }) return i
        }
        return -1
    }

    /**
     * Run once per combat tick. When effective HP (health + absorption)
     * drops below the configured percent of max HP, swap to a heal item,
     * right-click it, and schedule a swap back to the weapon a few ticks
     * later. Gated by AUTO_HEAL_COOLDOWN_MS so a brief HP dip can't burn
     * five wand casts in a row.
     */
    private fun tryAutoHeal(player: net.minecraft.client.player.LocalPlayer) {
        // Cooldown ticks down regardless so the slot restore always fires
        // even if HP came back above the threshold before the heal completed.
        if (healRestoreInTicks > 0) {
            healRestoreInTicks--
            if (healRestoreInTicks == 0 && healRestoreSlot in 0..8) {
                InventoryUtils.holdHotbarSlot(healRestoreSlot)
                healRestoreSlot = -1
                MovementManager.forcedUse = false
            }
            return
        }
        if (!autoHealSetting.value) return
        val effectiveHp = player.health + player.absorptionAmount
        val maxHp = player.maxHealth.coerceAtLeast(1f)
        val pct = effectiveHp / maxHp * 100f
        if (pct >= healThresholdPctSetting.value) return
        val now = System.currentTimeMillis()
        if (now - lastHealUseMs < AUTO_HEAL_COOLDOWN_MS) return
        val healSlot = findHealHotbarSlot(player)
        if (healSlot !in 0..8) return
        val previousSlot = player.inventory.selectedSlot
        if (previousSlot != healSlot) {
            healRestoreSlot = previousSlot
            InventoryUtils.holdHotbarSlot(healSlot)
        }
        MovementManager.forcedActionsEnabled = true
        MovementManager.forcedUse = true
        lastHealUseMs = now
        healRestoreInTicks = HEAL_RESTORE_TICKS
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
        if (hi <= lo) return lo
        val span = hi - lo
        return if (castBurstRemaining > 0) {
            // Inside a burst: sit in the bottom 35 % of the range so the
            // next cast fires quickly. Plus a tiny randomised jitter so
            // back-to-back bursts aren't identical.
            castBurstRemaining--
            val burstHi = lo + (span * 35 / 100).coerceAtLeast(5)
            lo + Random.nextLong(burstHi - lo + 1)
        } else {
            // Cluster ended: one slower cast in the upper 60-100 % of the
            // range, then re-seed a fresh burst behind it.
            castBurstRemaining = Random.nextInt(2, 5)  // next cluster size
            val lullLo = lo + (span * 60 / 100)
            lullLo + Random.nextLong(hi - lullLo + 1)
        }
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
                    guessConfirmTicks = 0
                    defendersRequired = false
                    burrowDugConfirmed = false
                    burrowDugConfirmedAtTick = 0
                    state = State.DIGGING
                    return
                }

                // Walk-first bias: ANY target within Close Walk Distance gets
                // walked directly instead of submitting an AOTV/etherwarp
                // plan — UNLESS walking is plainly not viable from here.
                // Walking is suppressed when:
                //   • the player is under a ceiling (just-warped into a hub
                //     building like Wizard Tower or Hub Crypts — walking
                //     can't navigate out, the planner's through-wall AOTV
                //     hops are the actual exit), OR
                //   • a real wall blocks the line from eye to target (the
                //     walker would just hammer against stone).
                // Without these escapes the walking bias keeps the macro
                // stuck inside Wizard Tower / Crypts when the target burrow
                // happens to be within Close Walk Distance.
                if (hops.isEmpty()) {
                    val dist = player.position().distanceTo(target)
                    val canWalkOut = !hasCeilingOverhead(player) &&
                        !isStraightLineBlocked(level, player, target)
                    if (canWalkOut && dist <= closeGuessWalkDistanceSetting.value) {
                        // Stuck-walking escape: trees, fences, and small
                        // obstructions can pin the native walker against
                        // geometry it can't navigate around. Track approach
                        // progress every tick; if the macro hasn't made any
                        // forward distance for stuckReplanTicks, abandon the
                        // walk and fall through to planning (AOTV/etherwarp
                        // is the next thing the executor tries).
                        val dSq = player.position().distanceToSqr(target)
                        if (dSq < lastProgressDistSq - 1.0) {
                            lastProgressDistSq = dSq
                            noProgressTicks = 0
                        } else {
                            noProgressTicks++
                        }
                        if (noProgressTicks <= stuckReplanTicks()) {
                            debugTravel("close target (${dist.toInt()}m): walking")
                            directApproach(player, target)
                            return
                        }
                        debugTravel("close walk stuck (${noProgressTicks}t) — planning")
                        stopNativeWalk()
                        noProgressTicks = 0
                        lastProgressDistSq = Double.MAX_VALUE
                        // Fall through to planning / warp / aerial fallback.
                    }

                    if (isGuessTarget()) {
                        val warp = bestWarpFor(player.position(), target)
                        if (warp != null && warpAttemptedFor?.distanceToSqr(target)?.let { it < 4.0 } != true) {
                            debugTravel("far guess: warping ${warp.displayName}")
                            startWarpTo(warp, target)
                            return
                        }
                    }

                    // Far targets with no warp now climb-and-fly through the
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

                // Post-warp lockout: just landed under a ceiling. Walk toward
                // the target instead of submitting a teleport plan so the
                // native walker leaves the enclosure first. Releases as soon
                // as the timer elapses OR the player no longer has a ceiling
                // overhead (already exited).
                if (postWarpWalkUntilMs != 0L) {
                    val nowWarp = System.currentTimeMillis()
                    if (nowWarp < postWarpWalkUntilMs && hasCeilingOverhead(player)) {
                        debugTravel("post-warp: walking out before fly")
                        directApproach(player, target)
                        return
                    } else {
                        postWarpWalkUntilMs = 0L
                    }
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
                    val sanitized = sanitizePlannerHops(level, plan.hops)
                    if (sanitized.isEmpty()) {
                        // Plan was wall-clip-heavy in walking range — walk
                        // around instead of teleporting through stone. The
                        // next replan will try again from a closer position.
                        debugTravel("plan rejected: wall-clip in walk range — walking")
                        if (walkFallbackSetting.value) directApproach(player, target)
                        planSubmitted = false
                        return
                    }
                    hops = sanitized
                    hopIndex = 1 // node 0 is the start position
                    noProgressTicks = 0
                    lastProgressDistSq = Double.MAX_VALUE
                    stopNativeWalk()
                    // Visible "plan committed" line so the user sees exactly
                    // how many nodes the macro is about to traverse.
                    debugTravel("plan committed: ${hops.size} nodes (${plan.teleportNodes} aotv / ${plan.etherwarpNodes} ether / ${plan.walkNodes} walk)")
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

                // Dynamic AOTV reoptimization: ask the planner periodically
                // for a fresh route from where we ACTUALLY are. AOTV drift
                // (fixed-11-block travel from a fallen eye) means the active
                // plan's later hops were geometry-optimal for the original
                // eye, not the current one. The replan checks if a shorter
                // chain exists from here; on strict improvement (≥2 fewer
                // hops to goal) we switch over and announce it. Gated on
                // cast cooldown so we never swap mid-cast and double-fire,
                // and on hops.size > 2 / hopIndex < last so we don't replan
                // single-shot direct-etherwarps or the final approach hop.
                val nowReplan = System.currentTimeMillis()
                if (wantsTeleports && aotvSlot >= 0 && hops.size > 2 && hopIndex < hops.size - 1 &&
                    nowReplan - lastCastMs > 200L &&
                    nowReplan - lastReplanCommittedMs > REPLAN_SWAP_COOLDOWN_MS
                ) {
                    if (!replanInFlight && nowReplan - lastReplanSubmittedMs > 1500L) {
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
                        )
                        lastReplanSubmittedMs = nowReplan
                        replanInFlight = true
                    } else if (replanInFlight) {
                        val newPlan = DianaTeleportPlanner.poll()
                        if (newPlan != null) {
                            replanInFlight = false
                            val remaining = hops.size - hopIndex
                            // Stricter swap gate — the old "≥2 fewer hops"
                            // sometimes accepted a shorter plan whose FIRST
                            // hop moved backward, producing the visible
                            // "warps forward, then warps back" loop the user
                            // reported. All four conditions must hold:
                            //   1. New plan has ≥2 hops (real teleport route).
                            //   2. ≥3 fewer hops than what's left of the
                            //      current plan (was 2 — too easy to flip).
                            //   3. New plan's first travel hop is at least
                            //      ~2 blocks CLOSER to the goal than where
                            //      the player stands right now. Prevents a
                            //      shorter loopy plan from winning by
                            //      taking us backward first.
                            //   4. New plan's first hop is roughly toward
                            //      the goal in the horizontal plane (cosine
                            //      of the angle between (first hop - player)
                            //      and (goal - player) > 0). Catches the
                            //      "shorter route in absolute hops but it
                            //      circles back" case.
                            val playerPos = player.position()
                            val curDistToGoal = playerPos.distanceTo(target)
                            val swapOk = run {
                                if (newPlan.hops.size < 2) return@run false
                                if (newPlan.hops.size + 2 >= remaining) return@run false
                                val firstHop = newPlan.hops[1].standPos()
                                val firstDistToGoal = firstHop.distanceTo(target)
                                if (firstDistToGoal > curDistToGoal - 2.0) return@run false
                                val hx = firstHop.x - playerPos.x
                                val hz = firstHop.z - playerPos.z
                                val gx = target.x - playerPos.x
                                val gz = target.z - playerPos.z
                                val hopLen = kotlin.math.hypot(hx, hz)
                                val goalLen = kotlin.math.hypot(gx, gz)
                                if (hopLen < 1e-3 || goalLen < 1e-3) return@run true
                                val cosAngle = (hx * gx + hz * gz) / (hopLen * goalLen)
                                cosAngle > 0.0
                            }
                            if (swapOk) {
                                val sanitizedSwap = sanitizePlannerHops(level, newPlan.hops)
                                if (sanitizedSwap.isEmpty()) {
                                    // Don't swap to a wall-clipping plan — keep
                                    // the current chain executing and let the
                                    // next replan reconsider from a new position.
                                    lastReplanCommittedMs = nowReplan
                                    return
                                }
                                debugTravel("better aotv route: ${newPlan.hops.size} nodes vs $remaining remaining — switching")
                                hops = sanitizedSwap
                                hopIndex = 1
                                noProgressTicks = 0
                                lastProgressDistSq = Double.MAX_VALUE
                                airHopTrackedIndex = -1
                                castedHopIndex = -1
                                lastReplanCommittedMs = nowReplan
                                return
                            }
                        }
                    }
                }

                val hop = hops[hopIndex]
                val standPos = hop.standPos()

                // "Let the player fall onto the burrow" gate. Once the player
                // is airborne and the burrow is within horizontal fall-reach
                // (the player will land at or near it just by falling), the
                // chain has done its job — any further AOTV cast would either
                // teleport the player past the burrow or send them downward
                // unnecessarily. Stop casting, let gravity finish the trip,
                // and let the PATHFINDING arrival check (player.distanceTo(target)
                // <= arrivalRadius) close out the travel. This kills the
                // user-reported "TPs downward while floating above the burrow"
                // when the planner schedules one last descent hop the player
                // doesn't actually need.
                if ((hop.type == DianaTeleportPlanner.HopType.AOTV ||
                        hop.type == DianaTeleportPlanner.HopType.ETHERWARP) &&
                    !player.onGround()
                ) {
                    val horizDist = kotlin.math.hypot(player.x - target.x, player.z - target.z)
                    val aboveTarget = player.y > target.y + 0.5
                    // 6 blocks is the natural fall radius from a typical AOTV
                    // chain altitude (~6-12 blocks above target). Anything in
                    // that arc the player will reach by gravity alone.
                    val withinFallReach = horizDist <= 6.0
                    val descendingHop = standPos.y < player.y - 0.5
                    if (withinFallReach && aboveTarget && descendingHop) {
                        debugTravel("above burrow (~${horizDist.toInt()}m) — letting fall")
                        // End the chain so PATHFINDING's arrival check / native
                        // walk takes over the moment the player touches ground.
                        releaseTravelInputs()
                        resetHopState()
                        return
                    }
                }

                when (hop.type) {
                    DianaTeleportPlanner.HopType.WALK -> {
                        if (nativeWalkTo(player, standPos, 1.15)) {
                            debugTravel("node $hopIndex complete @ (${hop.x},${hop.y},${hop.z}) walk")
                            hopIndex++
                            resetAimSettle()
                        }
                        // Sustained-collision fallback hop. JumpDetector
                        // already handles obstacles in the cached path; this
                        // catches live-world surprises (mob in the way,
                        // partial block off the route) only after several
                        // ticks of real stuckness.
                        maybeFallbackJump(player)
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
                    // Probe the column above the player: if a solid block sits
                    // within POST_WARP_CEILING_CHECK_Y blocks of the eye, we
                    // landed under a ceiling (Crypt warp is the worst case).
                    // Lock the macro into ground/teleport mode for a few
                    // seconds so it walks out of the enclosure before any
                    // climb-and-fly plan gets submitted — otherwise the
                    // planner happily generates AOTV hops that clip stone.
                    postWarpWalkUntilMs = if (hasCeilingOverhead(player)) {
                        System.currentTimeMillis() + POST_WARP_WALK_MS
                    } else {
                        0L
                    }
                    state = State.PATHFINDING
                }
            }

            State.DIGGING -> {
                val bp = burrowPos ?: run { state = State.IDLE; return }
                // Don't re-promote here. Promotion runs once on the
                // PATHFINDING -> DIGGING transition; running it every tick
                // makes the target hop between confirmed burrows in dense
                // burrow chains (multiple records inside the 8-block radius),
                // and each hop restarts the native walker at a slightly
                // different point. That's the "runs in circles around the
                // burrow" symptom — the macro is chasing a target that keeps
                // moving 1-3 blocks per tick. Once we've decided to dig a
                // specific burrow, stay committed to it.
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
                // Compute onBurrow up front so the aim decision can branch on
                // it. The aim MUST be at the burrow block whenever we're
                // committed to digging (onBurrow == true) — otherwise the
                // forward-look humanisation aims at eye height and Hypixel's
                // hit-resolve lands the right-click either on empty air or on
                // the block directly underneath the player, producing the
                // "spams spade but never mines" and "mines block under, not
                // the burrow" symptoms. Forward look only applies while the
                // player is still walking up (not yet committed to dig).
                val approachDistFlat = kotlin.math.hypot(player.x - bp.x, player.z - bp.z)
                val verticalOkAim = abs(player.y - (bp.y + 1.0)) <= 3.0
                val committedToDig = approachDistFlat <= DIG_REACH && verticalOkAim
                val aimTarget = if (committedToDig || approachDistFlat <= BURROW_AIM_DOWN_DIST) {
                    burrowBlock
                } else {
                    Vec3(bp.x, player.eyeY, bp.z)
                }
                val desiredAim = AngleUtils.getRotation(player.eyePosition, aimTarget)
                if (smoothAimSetting.value) {
                    RotationExecutor.rotateTo(desiredAim, approachRotationStrategy)
                } else {
                    player.yRot = desiredAim.yaw
                    player.xRot = desiredAim.pitch
                }

                // Particle freshness check on DIGGING entry. Skyblock
                // Overhaul does this: a real burrow keeps emitting particle
                // packets until you actually break it, so a record that hasn't
                // ticked in >5 s is either already dug, expired, or a stale
                // entry the server stopped echoing. Digging there just burns
                // spade swings and gets the macro stuck. Guesses don't emit
                // particles by definition, so they bypass the check.
                if (digTicksElapsed == 0 &&
                    burrowType != DianaParticleTracker.BurrowType.GUESS &&
                    burrowType != DianaParticleTracker.BurrowType.SUB_GUESS &&
                    burrowType != DianaParticleTracker.BurrowType.UNKNOWN
                ) {
                    val staleness = DianaParticleTracker.msSinceLastSeen(
                        floor(bp.x).toInt(), floor(bp.z).toInt()
                    )
                    if (staleness > BURROW_STALENESS_MS) {
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        ChatUtils.sendMessage("Diana: burrow stale (no particles for ${staleness}ms), skipping.")
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        state = State.IDLE
                        return
                    }
                }

                val flatDist = kotlin.math.hypot(player.x - bp.x, player.z - bp.z)
                val verticalOk = abs(player.y - (bp.y + 1.0)) <= 3.0
                // Spade has long reach. If we're within DIG_REACH horizontal
                // blocks and roughly at the burrow's elevation, treat it as
                // "close enough to dig" — stop walking and commit. The old
                // strict 1.55-block radius made the macro stall whenever the
                // last walk step was blocked by terrain (fences, partial
                // blocks, a tombstone) even though Hypixel would happily
                // resolve a dig from 4-5 blocks away.
                val onBurrow = flatDist <= DIG_REACH && verticalOk
                // Inner radius is used purely as the native walker's arrival
                // target so the player still positions reasonably on top of
                // the burrow when nothing's in the way.
                val walkRadius = digRadiusSetting.value

                if (!onBurrow) {
                    nativeWalkTo(player, Vec3(bp.x, bp.y + 1.0, bp.z), walkRadius)
                    MovementManager.forcedUse = false
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

                // Guess confirmation: arrived at a guessed burrow — hold
                // position and wait for particles to confirm the actual spot
                // before swinging the spade. Diana's compass guess is often a
                // few blocks off, and digging the guess directly usually digs
                // empty ground. promoteNearbyConfirmedBurrow swaps to the
                // particle-confirmed location the instant one arrives, which
                // also flips burrowType off GUESS so this block stops running.
                // If we wait the full window without seeing particles, drop
                // the guess and look for the next target — that's the
                // SkyBlock Overhaul "mine if particles, proceed if not" rule.
                if (isGuessTarget() && digTicksElapsed == 0) {
                    stopNativeWalk()
                    MovementManager.setMovementLock(true)
                    MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                    MovementManager.forcedUse = false
                    setStatus("Confirming guess (${guessConfirmTicks}t)")
                    val confirmed = promoteNearbyConfirmedBurrow(level, bp, radius = GUESS_CONFIRM_RADIUS)
                    if (confirmed != null) {
                        // Promotion mutated burrowPos / burrowType to the real
                        // burrow. Reset the approach timer so the dig walk
                        // restarts toward the new coords cleanly.
                        guessConfirmTicks = 0
                        digApproachTicks = 0
                        ChatUtils.sendMessage("Diana: guess confirmed → particle burrow at (${floor(confirmed.first.x).toInt()}, ${floor(confirmed.first.z).toInt()}).")
                        return
                    }
                    guessConfirmTicks++
                    if (guessConfirmTicks >= GUESS_CONFIRM_TICKS) {
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        ChatUtils.sendMessage("Diana: no particles at guess after ${guessConfirmTicks}t, skipping.")
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        guessConfirmTicks = 0
                        state = State.IDLE
                    }
                    return
                }

                // Positioned on the burrow — stop and dig.
                stopNativeWalk()
                MovementManager.setMovementLock(true)
                MovementManager.setForcedMovement(false, false, false, false, false, false, false)

                // Post-dig scan: the burrow physically broke (onBurrowDugChat
                // fired and set burrowDugConfirmed). Stop swinging the spade
                // — there's nothing left to break — and spend up to
                // POST_DUG_MOB_GRACE_TICKS (10) scanning for a mob that may
                // have spawned. If one appears, engage. If the window expires
                // without a mob, proceed to the next burrow.
                if (burrowDugConfirmed) {
                    MovementManager.forcedUse = false
                    MovementManager.forcedAttack = false
                    val scanTick = digTicksElapsed - burrowDugConfirmedAtTick
                    val remaining = (POST_DUG_MOB_GRACE_TICKS - scanTick).coerceAtLeast(0)
                    setStatus("Scanning for mob (${remaining}t)")
                    val mob = findDianaMob(bp, mobSearchRangeSetting.value)
                    if (mob != null) {
                        DianaProfitTracker.onMacroDug()
                        DianaProfitTracker.onMacroMobFound()
                        startCombat(mob, rare = dianaMobName(mob) in RARE_MOB_NAMES)
                        return
                    }
                    digTicksElapsed++
                    if (scanTick >= POST_DUG_MOB_GRACE_TICKS) {
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        DianaProfitTracker.onMacroDug()
                        waitTicksElapsed = 0
                        state = State.WAITING
                    }
                    return
                }

                // Dig LOS check: before swinging, raycast eye -> burrow. If a
                // SOLID wall blocks the line (foliage is OK; the attack will
                // break it), the player is on the wrong side of the wall and
                // needs to walk closer. Skipping this gate is what caused the
                // user-reported "tries to mine burrows through walls".
                if (isSolidWallBlocking(level, player, burrowBlock)) {
                    MovementManager.forcedUse = false
                    MovementManager.forcedAttack = false
                    setStatus("Walking around wall to burrow")
                    nativeWalkTo(player, Vec3(bp.x, bp.y + 1.0, bp.z), digRadiusSetting.value)
                    digApproachTicks++
                    if (digApproachTicks > digApproachTimeoutSetting.value.toInt()) {
                        stopNativeWalk()
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        ChatUtils.sendMessage("Diana: wall blocks burrow LOS, retrying.")
                        state = State.IDLE
                    }
                    return
                }

                // Diana burrows on Hypixel are MINED with left-click (attack
                // / break-block), not right-click. The spade's right-click is
                // for activating the compass guess; the actual dig is the
                // attack input. Left-click also harmlessly breaks any
                // foliage (tall grass, ferns, flowers) in front of the
                // burrow on the way in — no separate foliage clearing pass
                // is needed. forcedUse stays off so we don't accidentally
                // re-trigger the compass-guess animation mid-dig.
                MovementManager.forcedActionsEnabled = true
                MovementManager.forcedUse = false
                MovementManager.forcedAttack = true
                digTicksElapsed++

                // Defender combat: Hypixel's "Defeat all the burrow defenders
                // in order to dig it!" chat sets defendersRequired. The
                // burrow can't break until the defender is dead, so engage
                // and resume the same burrow afterwards via combatBlockedDig.
                if (defendersRequired) {
                    val mob = findDianaMob(bp, mobSearchRangeSetting.value)
                    if (mob != null) {
                        MovementManager.forcedUse = false
                        DianaProfitTracker.onMacroMobFound()
                        combatBlockedDig = true
                        startCombat(mob, rare = dianaMobName(mob) in RARE_MOB_NAMES)
                        return
                    }
                    // Defender chat fired but the mob hasn't materialised in
                    // entity-tracking range yet — keep digging (the server
                    // still won't let the burrow break), spade is held.
                }

                if (isTreasureTarget()) {
                    when (treasureDigStage) {
                        0 -> {
                            if (digTicksElapsed >= treasureDigTicks()) {
                                MovementManager.forcedAttack = false
                                treasureDigStage = 1
                                treasureWaitTicks = 0
                                digTicksElapsed = 0
                            }
                        }
                        1 -> {
                            MovementManager.forcedAttack = false
                            treasureWaitTicks++
                            if (treasureWaitTicks >= treasureSecondDigDelaySetting.value.toInt()) {
                                treasureDigStage = 2
                                digTicksElapsed = 0
                            }
                        }
                        else -> {
                            if (digTicksElapsed >= treasureDigTicks()) {
                                MovementManager.forcedAttack = false
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
                    MovementManager.forcedAttack = false
                    DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                    DianaProfitTracker.onMacroDug()
                    waitTicksElapsed = 0
                    state = State.WAITING
                    return
                }
                if (digTicksElapsed >= digTimeoutSetting.value.toInt()) {
                    MovementManager.forcedAttack = false
                    DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                    DianaProfitTracker.onMacroDug()
                    ChatUtils.sendMessage("Diana: burrow dug (no mob), continuing.")
                    waitTicksElapsed = 0
                    state = State.WAITING
                }
            }

            State.COMBAT -> {
                castRestoreSlot = -1
                // Hold the weapon by default, unless tryAutoHeal swapped in
                // the wand this tick — its scheduled restore puts the
                // weapon back N ticks later.
                if (healRestoreInTicks == 0) holdWeaponSlot()
                MovementManager.forcedActionsEnabled = true
                MovementManager.forcedUse = false
                MovementManager.forcedAttack = false
                tryAutoHeal(player)
                combatTicksElapsed++
                val protectedRareFight = combatIsRareMob && (rareMobFocusActive() || cocoonHoldActive())
                if (!protectedRareFight && combatTicksElapsed > combatTimeoutSetting.value.toInt()) {
                    MovementManager.clearForcedMovement()
                    MovementManager.setMovementLock(false)
                    RotationExecutor.stopIfUsing(combatRotationStrategy)
                    ChatUtils.sendMessage("Diana: combat timed out, continuing.")
                    resumeAfterCombat()
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
                        resumeAfterCombat()
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
                val canAttackThisTick = combatTicksElapsed - lastAttackTick >= nextClickIntervalTicks
                val shouldAttack = tdist <= combatAttackRangeSetting.value && aimed && canAttackThisTick
                MovementManager.forcedAttack = shouldAttack
                if (shouldAttack) {
                    lastAttackTick = combatTicksElapsed
                    rollNextClickInterval()
                }
            }

            State.WAITING -> {
                if (waitTicksElapsed == 0) MovementManager.setMovementLock(false)
                waitTicksElapsed++
                // Late-spawn catch: some Diana mobs only show up several
                // ticks after the burrow break — under ping or server lag
                // they can miss the 10-tick post-dig scan and the macro
                // would move on without fighting. While we're still hanging
                // around the just-dug burrow in WAITING, keep looking for a
                // mob within mobSearchRange of bp. burrowPos stays set
                // until IDLE clears it, so this scan is anchored to the
                // burrow we just mined — not a speculative "any Diana mob
                // anywhere" check.
                val bp = burrowPos
                if (bp != null) {
                    val mob = findDianaMob(bp, mobSearchRangeSetting.value)
                    if (mob != null) {
                        DianaProfitTracker.onMacroMobFound()
                        startCombat(mob, rare = dianaMobName(mob) in RARE_MOB_NAMES)
                        return
                    }
                }
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

    /**
     * Cut the dig short the moment Hypixel confirms the burrow broke. Without
     * this the macro keeps spamming USE for the full digTimeout window even
     * though the block is gone — wasted seconds per burrow that translate
     * directly into a lower burrows/hour ceiling. Chat is the authoritative
     * "this burrow is done" signal: react on it, drop the burrow from the
     * tracker, and jump straight to the post-loot wait.
     */
    @SubscribeEvent
    fun onBurrowDugChat(event: ChatEvent.Receive) {
        if (state != State.DIGGING && state != State.COMBAT) return
        val msg = ChatFormatting.stripFormatting(event.message ?: return) ?: return

        // Defender chat: a mob must be killed before the burrow can break.
        // Flag the dig as defender-gated so the DIGGING handler is allowed to
        // transition to COMBAT — and so COMBAT routes back to DIGGING (same
        // burrow) instead of WAITING when the defender dies.
        if (msg.startsWith("Defeat all the burrow defenders")) {
            if (state == State.DIGGING) defendersRequired = true
            return
        }

        if (!msg.startsWith("You dug out a Griffin Burrow!") &&
            !msg.startsWith("You finished the Griffin burrow chain!")
        ) return
        val bp = burrowPos
        if (bp != null) DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
        MovementManager.forcedUse = false
        // The burrow physically broke. Defender combat (if any) is finished by
        // definition. Don't jump to WAITING yet — give the DIGGING handler a
        // short grace window to spot a post-dig spawn (gaia construct, etc.).
        // The grace check (POST_DUG_MOB_GRACE_TICKS) in DIGGING will move us
        // to WAITING if no mob appears.
        burrowDugConfirmed = true
        burrowDugConfirmedAtTick = digTicksElapsed
        defendersRequired = false
        combatBlockedDig = false
    }

    @SubscribeEvent
    fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
        if (state != State.PATHFINDING && state != State.DIGGING && state != State.COMBAT) return
        val level = mc.level ?: return
        val bp = burrowPos ?: return

        // Burrow marker: a single subtle yellow box on the target. Toggled
        // by Render Burrow.
        if (showBurrowMarkerSetting.value) {
            OverlayRenderEngine.addBox(
                level,
                bp.x - 0.5, bp.y, bp.z - 0.5,
                bp.x + 0.5, bp.y + 1.0, bp.z + 0.5,
                fill    = OverlayRenderEngine.Color(255, 215, 0, 55),
                outline = OverlayRenderEngine.Color(255, 215, 0, 220),
                durationTicks = 10,
                tag = "diana-macro-burrow"
            )
        }

        // Path nodes: a single thin beam tracing the planned hop chain plus
        // a small dot at each node. No per-hop colours, no full-block
        // beacons, no end-marker — just enough to see the route. Toggled by
        // Render Path Nodes.
        if (showNodeMapSetting.value && state == State.PATHFINDING && hops.isNotEmpty()) {
            val beam = OverlayRenderEngine.Color(120, 220, 255, 220)
            val node = OverlayRenderEngine.Color(120, 220, 255, 200)
            val player = mc.player
            var prev = player?.let { Vec3(it.x, it.y + 0.9, it.z) }
                ?: hops.first().standPos()
            hops.forEach { h ->
                val c = h.standPos()
                val cTop = Vec3(c.x, c.y + 0.9, c.z)
                // Clip the beam at the first solid block along its path so
                // the route never appears to draw straight through stone /
                // walls. If the player is currently inside a block (e.g.,
                // standing in the foot voxel of a step), the clip starts
                // failing — fall back to a non-clipped draw so we don't lose
                // the segment entirely.
                val visibleEnd = firstWallHit(level, prev, cTop)
                OverlayRenderEngine.addLine(
                    level, prev.x, prev.y, prev.z, visibleEnd.x, visibleEnd.y, visibleEnd.z,
                    beam, 1.6f, 10, "diana-node-map", true
                )
                // Only draw the node marker if the node's stand position is
                // not buried in a wall — that's the "node map goes through
                // walls" symptom. sanitizePlannerHops already drops these
                // from the executor list, but a final visual gate makes the
                // route render clean even if a stale plan slipped through.
                val nodeVisible = visibleEnd.distanceToSqr(cTop) < 0.04
                if (nodeVisible) {
                    OverlayRenderEngine.addBox(
                        level,
                        c.x - 0.15, cTop.y - 0.15, c.z - 0.15,
                        c.x + 0.15, cTop.y + 0.15, c.z + 0.15,
                        fill = OverlayRenderEngine.Color(node.r, node.g, node.b, 80),
                        outline = node, lineWidth = 1.2f,
                        durationTicks = 10, tag = "diana-node-map",
                        forceRender = true
                    )
                }
                prev = cTop
            }
        }
    }
}
