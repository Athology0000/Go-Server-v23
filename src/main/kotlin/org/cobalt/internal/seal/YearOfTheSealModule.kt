package org.cobalt.internal.seal

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.pathfinder.jni.PathCommand
import org.cobalt.api.pathfinder.jni.PathfinderRotationStrategy
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.util.player.MovementManager
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

object YearOfTheSealModule : Module("Year of the Seal") {

    private val mc = Minecraft.getInstance()

    private val enabledSetting = CheckboxSetting(
        "Enabled",
        "Automatically pathfinds to the predicted beach ball landing position.",
        false
    )

    private val toggleKeybind = KeyBindSetting(
        "Toggle Key",
        "Keybind to enable/disable the Year of the Seal macro.",
        KeyBind(-1)
    )

    private val minBounces = SliderSetting(
        "Min Bounce Quality",
        "Minimum observed bounces before pathfinding starts. Higher = more data, more accurate prediction.",
        2.0, 1.0, 40.0, 1.0
    )

    private val statusSetting = TextSetting(
        "Status",
        "Current Year of the Seal macro state.",
        "Idle"
    )

    /** Tracks position history and predicts the landing position for one beach ball entity. */
    private class BallPredictor {
        val data = mutableListOf<Vec3>()    // all positions recorded since spawn
        var startIndex = 0                  // slice start within data (reset each bounce)
        var minY = Double.MAX_VALUE         // lowest Y seen (ground level)
        var bounceCounter = 0
        var lastBounceMs = 0L
        var positiveYDelta = false          // whether last Y delta was upward
        var lastPos = Vec3(0.0, 0.0, 0.0)
        var predTick = 0                    // counter mod 3 for throttled prediction updates
        var landingPos: Vec3? = null        // current best landing prediction
        private val models = listOf(
            PolynomialModel.SmallPoly,
            PolynomialModel.AveragePoly,
            PolynomialModel.SpreadPoly
        )

        fun update(pos: Vec3) {
            data.add(pos)
            if (pos.y < minY) minY = pos.y

            // Skip bounce logic on first tick — lastPos is uninitialized
            if (data.size == 1) { lastPos = pos; return }

            // Bounce detection — skip if movement too small or debounce active
            val dist = pos.distanceTo(lastPos)
            if (dist >= 0.3) {
                val dy = pos.y - lastPos.y
                val nowMs = System.currentTimeMillis()
                if (dy > 0.0 && !positiveYDelta && (nowMs - lastBounceMs) > 800L) {
                    // Ball just started going up — it bounced
                    bounceCounter++
                    lastBounceMs = nowMs
                    startIndex = data.lastIndex   // reset prediction slice to this bounce
                }
                positiveYDelta = dy > 0.0
                lastPos = pos
            }

            // Throttle prediction to every 3 ticks
            predTick++
            if (predTick >= 3) {
                predTick = 0
                landingPos = runPrediction()
            }
        }

        private fun runPrediction(): Vec3? {
            val slice = data.subList(startIndex, data.size).toList()
            val candidates = models.mapNotNull { it.predict(this, slice) }
                .filter { abs(it.y - minY) <= 1.0 }
            if (candidates.isEmpty()) return null
            // Average X and Z of all valid model outputs
            val avgX = candidates.sumOf { it.x } / candidates.size
            val avgZ = candidates.sumOf { it.z } / candidates.size
            return Vec3(avgX, minY, avgZ)
        }

        // Fit y = a*t^2 + b*t + c through three (t, y) points.
        // Returns Triple(a, b, c) or null if inputs are degenerate.
        private fun fitQuadratic(
            t1: Int, y1: Double,
            t2: Int, y2: Double,
            t3: Int, y3: Double
        ): Triple<Double, Double, Double>? {
            val d1 = (t2 - t1).toDouble()
            val d2 = (t3 - t1).toDouble()
            if (d1 == 0.0 || d2 == 0.0 || d1 == d2) return null
            val sq1 = t1.toDouble() * t1
            val sq2 = t2.toDouble() * t2
            val sq3 = t3.toDouble() * t3
            val denom = (sq2 - sq1) * d2 - (sq3 - sq1) * d1
            if (denom == 0.0) return null
            val a = ((y2 - y1) * d2 - (y3 - y1) * d1) / denom
            val b = ((y2 - y1) - a * (sq2 - sq1)) / d1
            val c = y1 - b * t1 - a * sq1
            return Triple(a, b, c)
        }

        // Extrapolate from t=fromT forward until poly(t) <= targetY, up to maxSteps ticks.
        // Returns the landing Vec3 (XZ from linear drift + Y at targetY), or null if never reached.
        private fun extrapolate(
            fromT: Int,
            startXZ: Vec3,
            dx: Double, dz: Double,
            a: Double, b: Double, c: Double,
            targetY: Double,
            maxSteps: Int = 300
        ): Vec3? {
            var x = startXZ.x
            var z = startXZ.z
            for (t in (fromT + 1)..(fromT + maxSteps)) {
                x += dx
                z += dz
                val y = a * t * t + b * t + c
                if (y <= targetY) return Vec3(x, targetY, z)
            }
            return null
        }

        // Average XZ drift per tick across the current bounce segment.
        private fun segmentDrift(slice: List<Vec3>): Pair<Double, Double> {
            if (slice.size < 2) return 0.0 to 0.0
            val n = (slice.size - 1).toDouble()
            return (slice.last().x - slice.first().x) / n to
                   (slice.last().z - slice.first().z) / n
        }

        private sealed class PolynomialModel {
            abstract fun predict(predictor: BallPredictor, slice: List<Vec3>): Vec3?

            object SmallPoly : PolynomialModel() {
                override fun predict(predictor: BallPredictor, slice: List<Vec3>): Vec3? =
                    predictor.smallPoly(slice)
            }

            object AveragePoly : PolynomialModel() {
                override fun predict(predictor: BallPredictor, slice: List<Vec3>): Vec3? =
                    predictor.averagePoly(slice)
            }

            object SpreadPoly : PolynomialModel() {
                override fun predict(predictor: BallPredictor, slice: List<Vec3>): Vec3? =
                    predictor.spreadPoly(slice)
            }
        }

        // SmallPoly: 3 most recent points. Min 3 points in slice.
        private fun smallPoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 3) return null
            val n = slice.size - 1
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(
                n,     slice[n].y,
                n - 1, slice[n - 1].y,
                n - 2, slice[n - 2].y
            ) ?: return null
            return extrapolate(n, slice[n], dx, dz, a, b, c, minY)
        }

        // AveragePoly: 2-point averaged windows at t-1, t-3, t-5. Min 7 points in slice.
        private fun averagePoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 7) return null
            val n = slice.size - 1
            val y1 = (slice[n - 1].y + slice[n - 2].y) / 2.0
            val y2 = (slice[n - 3].y + slice[n - 4].y) / 2.0
            val y3 = (slice[n - 5].y + slice[n - 6].y) / 2.0
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(n - 1, y1, n - 3, y2, n - 5, y3) ?: return null
            return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
        }

        // SpreadPoly: spread across full bounce segment. Min 5 points in slice.
        private fun spreadPoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 5) return null
            val n = slice.size - 1
            val mid = (n + 1) / 2
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(
                n - 1,  slice[n - 1].y,
                mid,    slice[mid].y,
                1,      slice[1].y
            ) ?: return null
            return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
        }
    }

    // Ball trackers keyed by entity ID
    private val predictors = mutableMapOf<Int, BallPredictor>()
    // Entity ID of the ball locked onto when the macro was enabled — only this ball is tracked
    private var lockedEntityId: Int? = null
    // Last target issued to NativePathfinder (to avoid replanning on every tick)
    private var lastIssuedTarget: Vec3? = null
    private var pathActive = false
    private var wasEnabled = false
    private var lastStatus = ""
    private var lastTrackedCount = 0
    private var announcedTarget = false

    init {
        addSetting(enabledSetting, toggleKeybind, minBounces, statusSetting)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (toggleKeybind.value.isPressed()) {
            enabledSetting.value = !enabledSetting.value
        }
        handleEnabledTransition()
        if (!enabledSetting.value) {
            if (predictors.isNotEmpty() || lastIssuedTarget != null) stopAll()
            return
        }
        val level = mc.level ?: return

        var slimeCount = 0
        var candidateCount = 0

        // Lock onto the first beach ball seen after enabling — never switch to a different ball.
        level.entitiesForRendering().forEach { entity ->
            if (entity !is Slime) return@forEach
            slimeCount++
            if (isBeachBallCandidate(entity)) {
                candidateCount++
                if (lockedEntityId == null) {
                    lockedEntityId = entity.id
                    ChatUtils.sendMessage("Year of the Seal: locked onto ball #${entity.id}.")
                }
                if (entity.id == lockedEntityId) {
                    predictors.putIfAbsent(entity.id, BallPredictor())
                }
            }
        }

        // Update existing predictors, prune removed entities
        val iter = predictors.iterator()
        while (iter.hasNext()) {
            val (id, predictor) = iter.next()
            val entity = level.getEntity(id)
            if (entity == null || entity.isRemoved) {
                iter.remove()
                continue
            }
            predictor.update(Vec3(entity.x, entity.y, entity.z))
        }

        // Find the first predictor with enough bounces and a valid prediction
        val readyPredictor = predictors.values
            .filter { it.bounceCounter >= minBounces.value.toInt() && it.landingPos != null }
            .maxByOrNull { it.bounceCounter }
        val target = readyPredictor?.landingPos

        if (target == null) {
            // No valid prediction yet — release pathfinder if we held it
            if (lastIssuedTarget != null) {
                NativePathfinder.stop()
                MovementManager.clearForcedMovement()
                lastIssuedTarget = null
                pathActive = false
                announcedTarget = false
            }
            updateTrackingStatus(slimeCount, candidateCount)
            return
        }

        // Only replan if target shifted by more than 0.5 blocks
        val prev = lastIssuedTarget
        if (prev == null || target.distanceTo(prev) > 0.5) {
            NativePathfinder.availabilityFlagsOverride = 0
            NativePathfinder.setTarget(target.x, target.y, target.z)
            lastIssuedTarget = target
            pathActive = true
            if (!announcedTarget) {
                ChatUtils.sendMessage(
                    "Year of the Seal target acquired after ${readyPredictor.bounceCounter} bounces."
                )
                announcedTarget = true
            }
        }
        setStatus(
            "Pathing: ${formatVec(target)} | bounces=${readyPredictor.bounceCounter} | ${NativePathfinder.status.name}"
        )

        // Tick the pathfinder and apply movement
        if (pathActive) {
            val cmd = NativePathfinder.tick()
            if (cmd != null) {
                applyWalkingCommand(cmd, target)
            } else {
                when (NativePathfinder.status) {
                    PathStatus.IDLE, PathStatus.ARRIVED, PathStatus.FAILED -> {
                        MovementManager.clearForcedMovement()
                        pathActive = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun applyWalkingCommand(cmd: PathCommand, target: Vec3) {
        val player = mc.player ?: return
        val guide = resolveWalkGuide(player, target)
        val dx = guide.x - player.x
        val dz = guide.z - player.z
        val distance = sqrt(dx * dx + dz * dz)
        if (distance < WALK_STOP_DISTANCE) {
            MovementManager.clearForcedMovement()
            return
        }

        MovementManager.setLookLock(false)
        RotationExecutor.stopIfUsing(PathfinderRotationStrategy)

        val nx = dx / distance
        val nz = dz / distance
        val yawRad = Math.toRadians(player.yRot.toDouble())
        val sinYaw = sin(yawRad)
        val cosYaw = cos(yawRad)
        val localForward = -nx * sinYaw + nz * cosYaw
        val localStrafe = nx * cosYaw + nz * sinYaw

        val forward = localForward > WALK_INPUT_THRESHOLD
        val backward = localForward < -WALK_INPUT_THRESHOLD
        val right = localStrafe > WALK_INPUT_THRESHOLD
        val left = localStrafe < -WALK_INPUT_THRESHOLD
        val sprint = cmd.sprint && forward && !backward && abs(localStrafe) < SPRINT_STRAFE_LIMIT

        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            forward = forward,
            backward = backward,
            left = left,
            right = right,
            jump = cmd.jump,
            shift = cmd.sneak,
            sprint = sprint
        )
        player.setSprinting(sprint)
    }

    private fun resolveWalkGuide(player: net.minecraft.client.player.LocalPlayer, target: Vec3): Vec3 {
        val nodes = NativePathfinder.cachedPathNodes
        if (nodes.isEmpty()) return target

        val nearestIndex = nearestPathNodeIndex(player, nodes)
        val guideIndex = min(nodes.lastIndex, nearestIndex + WALK_NODE_LOOKAHEAD)
        val node = nodes[guideIndex]
        return Vec3(node.x + 0.5, node.y, node.z + 0.5)
    }

    private fun nearestPathNodeIndex(
        player: net.minecraft.client.player.LocalPlayer,
        nodes: List<Vec3>
    ): Int {
        val cursor = NativePathfinder.pathNodeCursor.coerceIn(0, nodes.lastIndex)
        val start = max(0, cursor - 1)
        val end = min(nodes.lastIndex, cursor + WALK_NODE_SEARCH_WINDOW)
        var nearestIndex = start
        var nearestDistanceSq = Double.POSITIVE_INFINITY

        for (index in start..end) {
            val node = nodes[index]
            val dx = node.x + 0.5 - player.x
            val dz = node.z + 0.5 - player.z
            val distanceSq = dx * dx + dz * dz
            if (distanceSq < nearestDistanceSq) {
                nearestDistanceSq = distanceSq
                nearestIndex = index
            }
        }

        if (nearestIndex > NativePathfinder.pathNodeCursor) {
            NativePathfinder.pathNodeCursor = nearestIndex
        }
        return nearestIndex
    }

    private fun isBeachBallCandidate(entity: Slime): Boolean {
        return entity.maxHealth >= BEACH_BALL_HEALTH_THRESHOLD ||
            entity.health >= BEACH_BALL_HEALTH_THRESHOLD
    }

    private fun updateTrackingStatus(slimeCount: Int, candidateCount: Int) {
        if (predictors.isEmpty()) {
            setStatus("Scanning: no beach ball | slimes=$slimeCount candidates=$candidateCount")
            lastTrackedCount = 0
            return
        }

        val best = predictors.values.maxByOrNull { it.bounceCounter }
        val bestBounces = best?.bounceCounter ?: 0
        val hasPrediction = best?.landingPos != null
        setStatus(
            "Tracking ${predictors.size}: bounces=$bestBounces/${minBounces.value.toInt()} prediction=$hasPrediction"
        )

        if (lastTrackedCount == 0) {
            ChatUtils.sendMessage("Year of the Seal is tracking ${predictors.size} beach ball candidate(s).")
        }
        lastTrackedCount = predictors.size
    }

    private fun handleEnabledTransition() {
        val enabled = enabledSetting.value
        if (enabled == wasEnabled) return
        wasEnabled = enabled
        if (enabled) {
            setStatus("Scanning: enabled")
            ChatUtils.sendMessage("Year of the Seal enabled. Scanning for the beach ball.")
        } else {
            setStatus("Idle")
            ChatUtils.sendMessage("Year of the Seal disabled.")
        }
    }

    private fun setStatus(status: String) {
        if (status == lastStatus) return
        lastStatus = status
        statusSetting.value = status
    }

    private fun formatVec(vec: Vec3): String {
        return "${vec.x.toInt()}, ${vec.y.toInt()}, ${vec.z.toInt()}"
    }

    private fun stopAll() {
        NativePathfinder.stop()
        MovementManager.clearForcedMovement()
        predictors.clear()
        lockedEntityId = null
        lastIssuedTarget = null
        pathActive = false
        lastTrackedCount = 0
        announcedTarget = false
        setStatus(if (enabledSetting.value) "Scanning: reset" else "Idle")
    }

    internal fun onLevelChange() = stopAll()

    private const val BEACH_BALL_HEALTH_THRESHOLD = 1000f
    private const val WALK_INPUT_THRESHOLD = 0.22
    private const val WALK_STOP_DISTANCE = 0.18
    private const val SPRINT_STRAFE_LIMIT = 0.35
    private const val WALK_NODE_LOOKAHEAD = 2
    private const val WALK_NODE_SEARCH_WINDOW = 16
}
