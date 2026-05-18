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
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.rotation.strategy.BezierTrackingRotationStrategy
import org.phantom.api.util.AngleUtils
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.InventoryUtils
import org.phantom.api.util.TickScheduler
import org.phantom.api.util.helper.Rotation
import org.phantom.api.util.player.MovementManager
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

    private var activatingTicksElapsed = 0
    private var collectTicksElapsed     = 0
    private var pathfindingTicksElapsed = 0
    private var digTicksElapsed         = 0
    private var digApproachTicks        = 0
    private var combatMoving            = false
    private var lastTravelDebugMs       = 0L
    private var waitTicksElapsed        = 0

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
    private val smoothAimSetting = CheckboxSetting(
        "Smooth Aim", "Human-like Bezier rotation toward each hop (off = snap, for testing).", true
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
    private const val COMBAT_ROTATION_STEP_SCALE = 0.62
    private const val AIM_ERROR_DEG = 3.5f
    // Combat positioning: start walking only when the mob drifts past
    // COMBAT_MOVE_START, stop once back within COMBAT_MOVE_STOP, and swing
    // whenever it is within COMBAT_ATTACK_RANGE.
    private const val COMBAT_MOVE_START = 5.0
    private const val COMBAT_MOVE_STOP = 3.0
    private const val COMBAT_ATTACK_RANGE = 4.0

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
            enabledSetting, spadeSlotSetting, weaponSlotSetting, aotvSlotSetting,
            collectDurationSetting, postKillWaitSetting, minParticlesSetting,
            useEtherwarpSetting, useAotvSetting, smoothAimSetting,
            showNodeMapSetting, travelDebugSetting, aimSettleSetting,
            castCdMinSetting, castCdMaxSetting,
        )
        EventBus.register(this)
    }

    // -- Cleanup ---------------------------------------------------------------

    private fun cleanup() {
        NativePathfinder.stop()
        MovementManager.setMovementLock(false)
        RotationExecutor.stopRotating()
        state = State.IDLE
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
        MovementManager.setForcedMovement(true, false, false, false, false, false, true)
    }

    private fun debugTravel(msg: String) {
        if (!travelDebugSetting.value) return
        val now = System.currentTimeMillis()
        if (now - lastTravelDebugMs < 1500L) return
        lastTravelDebugMs = now
        ChatUtils.sendMessage("Diana: $msg")
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
        if (!enabledSetting.value) {
            if (wasEnabled) stop()
            wasEnabled = false
            return
        }
        wasEnabled = true

        val player = mc.player ?: run { stop(); wasEnabled = false; return }
        val level  = mc.level  ?: run { stop(); wasEnabled = false; return }

        when (state) {

            State.IDLE -> {
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
                if (pathfindingTicksElapsed > 600) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: travel timed out, retrying.")
                    state = State.IDLE
                    return
                }

                val aotvSlot = resolveAotvSlot()
                if (aotvSlot < 0) {
                    endTravel()
                    ChatUtils.sendMessage("Diana: no AOTV in hotbar (set AOTV Slot).")
                    state = State.IDLE
                    return
                }
                // Hold AOTV for travelling (unless mid cast-restore).
                if (castRestoreSlot < 0 && player.inventory.selectedSlot != aotvSlot) {
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
                            debugTravel("planner: no path, walking + retrying")
                            directApproach(player, target)
                            planSubmitted = false
                        }
                        return
                    }
                    debugTravel(
                        "plan: ${plan.hops.size} hops, reached=${plan.reachedGoal}, " +
                            "${plan.timeMs}ms, ${plan.nodesExplored} nodes"
                    )
                    if (plan.hops.size < 2) {
                        directApproach(player, target)
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
                    if (noProgressTicks > 80) {
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
                        MovementManager.setForcedMovement(true, false, false, false, false, false, true)
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
                        aimAt(Rotation(hop.yaw, hop.pitch))

                        if (player.position().distanceTo(standPos) <= 2.0) {
                            hopIndex++
                            resetAimSettle()
                            return
                        }
                        // Cast fired and we're now falling toward the landing.
                        if (!player.onGround() && player.deltaMovement.y < -0.08 &&
                            player.y < standPos.y - 1.1
                        ) {
                            hopIndex++
                            resetAimSettle()
                            return
                        }

                        val now = System.currentTimeMillis()
                        if (now - lastCastMs < castCooldownMs) return

                        val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, hop.yaw))
                        val pitchErr = abs(player.xRot - hop.pitch)
                        if (yawErr > AIM_ERROR_DEG || (isEther && pitchErr > AIM_ERROR_DEG)) {
                            aimStableSinceMs = now
                            return
                        }
                        if (now - aimStableSinceMs < aimSettleSetting.value.toLong()) return

                        fireCast(isEther, aotvSlot)
                        lastCastMs = now
                        castCooldownMs = randomCastCooldown()
                    }
                }
            }

            State.DIGGING -> {
                val bp = burrowPos ?: run { state = State.IDLE; return }
                val burrowBlock = Vec3(bp.x, bp.y, bp.z)

                MovementManager.forcedActionsEnabled = true
                // Always look down at the burrow block so the use actually digs it.
                aimAt(AngleUtils.getRotation(player.eyePosition, burrowBlock))

                val flatDist = kotlin.math.hypot(player.x - bp.x, player.z - bp.z)
                val onBurrow = flatDist <= 1.3 && abs(player.y - (bp.y + 1.0)) <= 2.0

                if (!onBurrow) {
                    // Teleport landings are up to ~2 blocks off — walk onto the
                    // burrow before digging instead of using into thin air.
                    MovementManager.setMovementLock(true)
                    MovementManager.setForcedMovement(true, false, false, false, false, false, false)
                    MovementManager.forcedUse = false
                    digApproachTicks++
                    if (digApproachTicks > 70) {
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
                val mob = level.getEntitiesOfClass(
                    LivingEntity::class.java,
                    AABB(bp, bp).inflate(12.0)
                ).firstOrNull { e ->
                    val name = ChatFormatting.stripFormatting(e.displayName.string).orEmpty()
                    DIANA_MOB_NAMES.any { n -> name.contains(n, ignoreCase = true) }
                }
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
                if (digTicksElapsed >= 70) {
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

                val target = level.getEntity(targetEntityId)
                if (target == null || (target as? LivingEntity)?.isAlive != true) {
                    MovementManager.clearForcedMovement()
                    MovementManager.setMovementLock(false)
                    DianaProfitTracker.onMacroKill()
                    waitTicksElapsed = 0
                    state = State.WAITING
                    return
                }

                RotationExecutor.rotateTo(AngleUtils.getRotation(target), rotationStrategy)
                MovementManager.setMovementLock(true)
                val tdist = player.position().distanceTo(target.position())

                // Stand still and only move when the mob is genuinely out of
                // reach. Hysteresis (start vs stop) prevents step/stop jitter.
                if (combatMoving) {
                    if (tdist <= COMBAT_MOVE_STOP) combatMoving = false
                } else if (tdist > COMBAT_MOVE_START) {
                    combatMoving = true
                }

                MovementManager.setForcedMovement(
                    combatMoving, false, false, false, false, false, false
                )
                MovementManager.forcedAttack = tdist <= COMBAT_ATTACK_RANGE
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

        // Node map: draw the planned hop path so it's visible before/while
        // the macro travels (walk = grey, AOTV = cyan, etherwarp = magenta).
        if (showNodeMapSetting.value && state == State.PATHFINDING && hops.isNotEmpty()) {
            val player = mc.player
            var prev = player?.let { Vec3(it.x, it.y, it.z) } ?: hops.first().standPos()
            for (h in hops) {
                val c = h.standPos()
                val col = when (h.type) {
                    DianaTeleportPlanner.HopType.WALK ->
                        OverlayRenderEngine.Color(170, 170, 170, 255)
                    DianaTeleportPlanner.HopType.AOTV ->
                        OverlayRenderEngine.Color(0, 220, 255, 255)
                    DianaTeleportPlanner.HopType.ETHERWARP ->
                        OverlayRenderEngine.Color(255, 0, 220, 255)
                }
                OverlayRenderEngine.addLine(
                    level,
                    prev.x, prev.y + 0.5, prev.z,
                    c.x, c.y + 0.5, c.z,
                    col, 2.5f, 10, "diana-node-map"
                )
                OverlayRenderEngine.addBox(
                    level,
                    c.x - 0.25, c.y, c.z - 0.25,
                    c.x + 0.25, c.y + 0.5, c.z + 0.25,
                    fill = OverlayRenderEngine.Color(col.r, col.g, col.b, 70),
                    outline = col,
                    durationTicks = 10,
                    tag = "diana-node-map"
                )
                prev = c
            }
        }
    }
}
