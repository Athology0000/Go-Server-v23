package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.floor
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
        IDLE, ACTIVATING_SPADE, COLLECTING_PARTICLES, PATHFINDING, DIGGING, COMBAT, WAITING
    }

    private var state = State.IDLE
    private var wasEnabled = false
    private var paused = false

    private var activatingTicksElapsed = 0
    private var collectTicksElapsed     = 0
    private var pathfindingTicksElapsed = 0
    private var digTicksElapsed         = 0
    private var digApproachTicks        = 0
    private var combatMoving            = false
    private var lastTravelDebugMs       = 0L
    private var waitTicksElapsed        = 0
    private var loopsCompleted          = 0
    private var lastStatus              = "Idle"

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
    private var castRestoreSlot = -1

    // -- Settings --------------------------------------------------------------

    private val enabledSetting = CheckboxSetting("Enabled", "Start or stop the Diana macro.", false)
    private val toggleKeySetting = KeyBindSetting("Toggle Key", "Start or stop the Diana macro.", KeyBind(-1))
    private val pauseKeySetting = KeyBindSetting("Pause Key", "Pause or resume the Diana macro without disabling it.", KeyBind(-1))
    private val autoEnableHelperSetting = CheckboxSetting(
        "Auto Enable Helper", "Turn on Diana Helper while the macro is running.", true
    )
    private val useKnownBurrowSetting = CheckboxSetting(
        "Use Known Burrow", "Travel to an already detected helper burrow before using the spade again.", true
    )

    private val spadeSlotSetting = SliderSetting(
        "Spade Slot", "Hotbar slot of the Ancestral Spade (1-9).", 1.0, 1.0, 9.0, step = 1.0
    )
    private val weaponSlotSetting = SliderSetting(
        "Weapon Slot", "Hotbar slot of your main weapon (1-9).", 2.0, 1.0, 9.0, step = 1.0
    )
    private val collectDurationSetting = SliderSetting(
        "Collect Duration", "Max ticks to wait for burrow particles before retrying.", 40.0, 5.0, 60.0, step = 1.0
    )
    private val postKillWaitSetting = SliderSetting(
        "Post-Kill Wait", "Ticks to wait after kill before looping.", 80.0, 20.0, 200.0, step = 1.0
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
    private val stuckReplanTicksSetting = SliderSetting(
        "Stuck Replan Ticks", "Ticks without travel progress before replanning.", 80.0, 20.0, 200.0, step = 5.0
    )
    private val minParticlesSetting = SliderSetting(
        "Min Particles", "Minimum CRIT packets required before pathfinding to burrow.", 2.0, 1.0, 10.0, step = 1.0
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
    private val smoothAimSetting = CheckboxSetting(
        "Smooth Aim", "Human-like Bezier rotation toward each hop (off = snap, for testing).", true
    )
    private val statusHudSetting = CheckboxSetting(
        "Status HUD", "Show the Diana macro state HUD.", true
    )
    private val showNodeMapSetting = CheckboxSetting(
        "Show Node Map", "Render the planned hop path (walk/AOTV/etherwarp) while travelling.", true
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
    private val mobSearchRangeSetting = SliderSetting(
        "Mob Search Range", "Search radius for spawned Diana mobs around the burrow.", 14.0, 6.0, 24.0, step = 1.0
    )

    private val spadeSlot  get() = spadeSlotSetting.value.toInt() - 1
    private val weaponSlot get() = weaponSlotSetting.value.toInt() - 1
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
    private const val COMBAT_ROTATION_STEP_SCALE = 0.62
    private const val AIM_ERROR_DEG = 3.5f

    private val rotationStrategy = BezierTrackingRotationStrategy(
        yawStepSampler   = { (RotationsModule.sample(RotationsModule.combatYawStep.value)   * COMBAT_ROTATION_STEP_SCALE).toFloat() },
        pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
        curveInProvider  = { RotationsModule.bezierCurveIn.value.toFloat() },
        curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
        minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
    )

    // -- Init ------------------------------------------------------------------

    init {
        addSetting(
            enabledSetting, toggleKeySetting, pauseKeySetting, autoEnableHelperSetting,
            useKnownBurrowSetting, spadeSlotSetting, weaponSlotSetting, aotvSlotSetting,
            collectDurationSetting, postKillWaitSetting, travelTimeoutSetting,
            digTimeoutSetting, digApproachTimeoutSetting, stuckReplanTicksSetting,
            minParticlesSetting, useEtherwarpSetting, useAotvSetting, walkFallbackSetting,
            smoothAimSetting, statusHudSetting, showNodeMapSetting, travelDebugSetting,
            aimSettleSetting, castCdMinSetting, castCdMaxSetting, combatMoveStartSetting,
            combatMoveStopSetting, combatAttackRangeSetting, mobSearchRangeSetting,
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
        burrowPos = null
        burrowType = DianaParticleTracker.BurrowType.UNKNOWN
        targetEntityId = -1
        activatingTicksElapsed = 0
        collectTicksElapsed = 0
        pathfindingTicksElapsed = 0
        digTicksElapsed = 0
        digApproachTicks = 0
        waitTicksElapsed = 0
        combatMoving = false
        releaseTravelInputs()
        DianaTeleportPlanner.cancel()
        resetHopState()
    }

    private fun stop() { cleanup() }

    // -- Teleport-first hop executor helpers -----------------------------------

    private fun resetHopState() {
        planSubmitted = false
        hops = emptyList()
        hopIndex = 0
        noProgressTicks = 0
        lastProgressDistSq = Double.MAX_VALUE
        resetAimSettle()
    }

    private fun resetAimSettle() {
        aimStableSinceMs = 0L
        lastAimYaw = Float.NaN
        lastAimPitch = Float.NaN
    }

    private fun releaseTravelInputs() {
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

    /** Walk straight at the burrow while the planner has nothing usable. */
    private fun directApproach(player: net.minecraft.client.player.LocalPlayer, target: Vec3) {
        aimAt(AngleUtils.getRotation(player.eyePosition, Vec3(target.x, target.y + 0.3, target.z)))
        MovementManager.setMovementLock(true)
        val jump = player.horizontalCollision || target.y > player.y + 0.5
        MovementManager.setForcedMovement(true, false, false, false, jump, false, true)
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

    private fun pauseMacro() {
        paused = true
        setStatus("Paused")
        releaseTravelInputs()
        MovementManager.clearForcedMovement()
        MovementManager.setMovementLock(false)
        MovementManager.forcedActionsEnabled = false
        MovementManager.forcedUse = false
        MovementManager.forcedAttack = false
        RotationExecutor.stopIfUsing(rotationStrategy)
    }

    private fun knownBurrow(level: net.minecraft.world.level.Level): Pair<Vec3, DianaParticleTracker.BurrowType>? {
        DianaParticleTracker.getBurrowRecord(level)?.let { return it }
        val helperPos = DianaHelperModule.burrowPos ?: return null
        return helperPos to DianaParticleTracker.BurrowType.GUESS
    }

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

    private fun dianaMobName(entity: LivingEntity): String? {
        val name = ChatFormatting.stripFormatting(entity.displayName.string).orEmpty()
        return DIANA_MOB_NAMES.firstOrNull { n -> name.contains(n, ignoreCase = true) }
    }

    private fun randomCastCooldown(): Long {
        val lo = castCdMinSetting.value.toLong()
        val hi = castCdMaxSetting.value.toLong().coerceAtLeast(lo)
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
        setStatus(state.name.lowercase().replaceFirstChar { it.uppercase() })

        when (state) {

            State.IDLE -> {
                if (useKnownBurrowSetting.value) {
                    val known = knownBurrow(level)
                    if (known != null) {
                        burrowPos = known.first
                        burrowType = known.second
                        DianaProfitTracker.onMacroTarget(burrowType)
                        pathfindingTicksElapsed = 0
                        setStatus("Using known burrow")
                        state = State.PATHFINDING
                        return
                    }
                }
                // Reset particle tracker before a fresh activation
                DianaParticleTracker.reset()
                player.inventory.selectedSlot = spadeSlot
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
                        state = State.PATHFINDING
                        return
                    }
                }
                // Timeout - no burrow detected, retry
                if (collectTicksElapsed >= collectDurationSetting.value.toInt()) {
                    state = State.IDLE
                }
            }

            State.PATHFINDING -> {
                val bp = burrowPos ?: run { state = State.IDLE; return }
                val target = Vec3(bp.x, bp.y + 1.0, bp.z)

                pathfindingTicksElapsed++
                if (pathfindingTicksElapsed > travelTimeoutSetting.value.toInt()) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: travel timed out, retrying.")
                    state = State.IDLE
                    return
                }

                val aotvSlot = resolveAotvSlot()
                val wantsTeleports = useAotvSetting.value || useEtherwarpSetting.value
                if (aotvSlot < 0 && wantsTeleports && !walkFallbackSetting.value) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: no AOTV in hotbar (set AOTV Slot).")
                    state = State.IDLE
                    return
                }
                // Hold AOTV for travelling (unless mid cast-restore).
                if (aotvSlot >= 0 && castRestoreSlot < 0 && player.inventory.selectedSlot != aotvSlot) {
                    player.inventory.selectedSlot = aotvSlot
                }

                // Arrived at the burrow.
                if (player.position().distanceTo(target) <= 2.5) {
                    endTravel()
                    DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                    DianaProfitTracker.onMacroArrived()
                    player.inventory.selectedSlot = spadeSlot
                    digTicksElapsed = 0
                    digApproachTicks = 0
                    state = State.DIGGING
                    return
                }

                // Request a hybrid teleport-first plan and wait for it.
                if (!wantsTeleports || aotvSlot < 0) {
                    debugTravel("walking fallback")
                    directApproach(player, target)
                    return
                }

                if (!planSubmitted) {
                    DianaTeleportPlanner.submit(
                        start = player.position(),
                        goal = target,
                        goalReachedRadius = 2.0,
                        transmissionRange = 11.0,
                        etherwarpRange = EtherwarpLogic.getEtherwarpRange().toDouble(),
                        availableMana = -1,
                        aotvEnabled = useAotvSetting.value,
                        etherwarpEnabled = useEtherwarpSetting.value,
                    )
                    planSubmitted = true
                    hops = emptyList()
                    hopIndex = 0
                    MovementManager.setMovementLock(true)
                    MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                    return
                }
                if (hops.isEmpty()) {
                    val plan = DianaTeleportPlanner.poll()
                    if (plan == null) {
                        if (!DianaTeleportPlanner.isPlanning) {
                            // No plan yet — keep approaching directly so far
                            // chunks stream in, then resubmit. Never just stand.
                            debugTravel("planner: no path, ${if (walkFallbackSetting.value) "walking + " else ""}retrying")
                            if (walkFallbackSetting.value) directApproach(player, target)
                            planSubmitted = false
                        }
                        return
                    }
                    debugTravel(
                        "plan: ${plan.hops.size} hops, reached=${plan.reachedGoal}, " +
                            "${plan.timeMs}ms, ${plan.nodesExplored} nodes"
                    )
                    if (plan.hops.size < 2) {
                        if (walkFallbackSetting.value) directApproach(player, target)
                        planSubmitted = false
                        return
                    }
                    hops = plan.hops
                    hopIndex = 1 // node 0 is the start position
                    noProgressTicks = 0
                    lastProgressDistSq = Double.MAX_VALUE
                }

                // No-progress watchdog -> replan from current position.
                val dSq = player.position().distanceToSqr(target)
                if (dSq < lastProgressDistSq - 1.0) {
                    lastProgressDistSq = dSq
                    noProgressTicks = 0
                } else {
                    noProgressTicks++
                    if (noProgressTicks > stuckReplanTicksSetting.value.toInt()) {
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
                        aimAt(
                            AngleUtils.getRotation(
                                player.eyePosition,
                                Vec3(standPos.x, standPos.y + 0.5, standPos.z)
                            )
                        )
                        MovementManager.setMovementLock(true)
                        val jump = player.horizontalCollision ||
                            standPos.y > player.y + 0.5
                        MovementManager.setForcedMovement(true, false, false, false, jump, false, true)
                        if (player.position().distanceTo(standPos) <= 1.3) {
                            hopIndex++
                            resetAimSettle()
                        }
                    }

                    DianaTeleportPlanner.HopType.AOTV,
                    DianaTeleportPlanner.HopType.ETHERWARP -> {
                        MovementManager.setMovementLock(true)
                        MovementManager.setForcedMovement(false, false, false, false, false, false, false)
                        val isEther = hop.type == DianaTeleportPlanner.HopType.ETHERWARP
                        // Instant transmission needs no target block and can be
                        // chained mid-air; etherwarp needs a block + crouch
                        // (fireCast handles the sneak). Air chains must fire
                        // fast and tolerate pitch since they travel vertically.
                        val air = hop.airborne
                        aimAt(Rotation(hop.yaw, hop.pitch))

                        if (player.position().distanceTo(standPos) <= 2.0) {
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
                            hopIndex++
                            resetAimSettle()
                            return
                        }

                        val now = System.currentTimeMillis()
                        val cd = if (air) 90L else castCooldownMs
                        if (now - lastCastMs < cd) return

                        val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, hop.yaw))
                        val pitchErr = abs(player.xRot - hop.pitch)
                        if (yawErr > AIM_ERROR_DEG || ((isEther || air) && pitchErr > AIM_ERROR_DEG)) {
                            aimStableSinceMs = now
                            return
                        }
                        val settleMs = if (air) 0L else aimSettleSetting.value.toLong()
                        if (now - aimStableSinceMs < settleMs) return

                        fireCast(isEther, aotvSlot)
                        lastCastMs = now
                        castCooldownMs = if (air) 90L else randomCastCooldown()
                    }
                }
            }

            State.DIGGING -> {
                val bp = burrowPos ?: run { state = State.IDLE; return }
                // bp.y is the burrow block's coordinate (it spans world Y
                // bp.y..bp.y+1). Aim at its CENTRE, not its bottom face, or the
                // look-ray passes into the block below it (the off-by-one).
                val burrowBlock = Vec3(bp.x, bp.y + 0.5, bp.z)

                MovementManager.forcedActionsEnabled = true
                // Never dig with the AOTV. Force the spade every tick and
                // cancel any pending post-cast slot restore that would swap
                // the AOTV back in mid-dig.
                castRestoreSlot = -1
                if (player.inventory.selectedSlot != spadeSlot) {
                    player.inventory.selectedSlot = spadeSlot
                }
                // Always look down at the burrow block so the use actually digs it.
                aimAt(AngleUtils.getRotation(player.eyePosition, burrowBlock))

                val flatDist = kotlin.math.hypot(player.x - bp.x, player.z - bp.z)
                val onBurrow = flatDist <= 1.3 && abs(player.y - (bp.y + 1.0)) <= 2.0

                if (!onBurrow) {
                    // Teleport landings are up to ~2 blocks off — walk onto the
                    // burrow before digging instead of using into thin air.
                    MovementManager.setMovementLock(true)
                    val jump = player.horizontalCollision ||
                        (bp.y + 1.0) > player.y + 0.5
                    MovementManager.setForcedMovement(true, false, false, false, jump, false, false)
                    MovementManager.forcedUse = false
                    digApproachTicks++
                    if (digApproachTicks > digApproachTimeoutSetting.value.toInt()) {
                        MovementManager.clearForcedMovement()
                        MovementManager.setMovementLock(false)
                        DianaParticleTracker.removeBurrow(floor(bp.x).toInt(), floor(bp.z).toInt())
                        ChatUtils.sendMessage("Diana: couldn't reach burrow, retrying.")
                        state = State.IDLE
                    }
                    return
                }

                // Positioned on the burrow — stop and dig.
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
                    targetEntityId = mob.id
                    player.inventory.selectedSlot = weaponSlot
                    combatMoving = false
                    state = State.COMBAT
                    return
                }

                // GUESS / MOB burrows keep digging and watching for a mob; other
                // types finish after a short dig. Guesses are now dug like any
                // real burrow rather than skipped.
                val mobLikely = burrowType == DianaParticleTracker.BurrowType.MOB ||
                    burrowType == DianaParticleTracker.BurrowType.GUESS
                if (!mobLikely && digTicksElapsed >= 14) {
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
                MovementManager.forcedActionsEnabled = true
                MovementManager.forcedUse = false
                MovementManager.forcedAttack = false

                val target = (level.getEntity(targetEntityId) as? LivingEntity)
                    ?.takeIf { it.isAlive }
                    ?: burrowPos?.let { findDianaMob(it, mobSearchRangeSetting.value) }
                if (target == null) {
                    MovementManager.clearForcedMovement()
                    MovementManager.setMovementLock(false)
                    DianaProfitTracker.onMacroKill()
                    loopsCompleted++
                    waitTicksElapsed = 0
                    state = State.WAITING
                    return
                }
                targetEntityId = target.id

                RotationExecutor.rotateTo(AngleUtils.getRotation(target), rotationStrategy)
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
                MovementManager.forcedAttack = tdist <= combatAttackRangeSetting.value
            }

            State.WAITING -> {
                if (waitTicksElapsed == 0) MovementManager.setMovementLock(false)
                waitTicksElapsed++
                if (waitTicksElapsed >= postKillWaitSetting.value.toInt()) {
                    state = State.IDLE
                }
            }
        }
    }

    val statusHud = hudElement("diana-macro-status", "Diana Macro Status") {
        anchor = HudAnchor.TOP_LEFT
        offsetX = 12f
        offsetY = 96f

        width { 150f }
        height { 58f }

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
            NVGRenderer.rect(x, y, 150f, 58f, theme.panel, 8f)
            NVGRenderer.hollowRect(x, y, 150f, 58f, 1f, theme.controlBorder, 8f)
            NVGRenderer.text("Diana Macro", x + 9f, y + 10f, 11f, theme.text)
            NVGRenderer.text(status, x + 9f, y + 25f, 9f, if (paused) 0xFFFFAA00.toInt() else theme.accent)
            NVGRenderer.text("Target: $distance", x + 9f, y + 38f, 9f, theme.textSecondary)
            NVGRenderer.text("Loops: $loopsCompleted", x + 82f, y + 38f, 9f, theme.textSecondary)
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
