package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.cobalt.api.module.ModuleDebug
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.TickScheduler
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.PathTeleportConfig
import org.cobalt.internal.pathfinding.DebugLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

object NativePathfinder {

    val isInitialized: Boolean get() = true

    /** Cached dense path nodes from the native V5 search result. */
    var cachedFullPathNodes: List<Vec3> = emptyList()
        private set

    /** Cached key path nodes (integer block positions as Vec3). Updated on EXECUTING transition. */
    var cachedPathNodes: List<Vec3> = emptyList()
        private set

    /** Cached per-node flags from the dense native path. */
    var cachedPathFlags: IntArray = IntArray(0)
        private set

    /** Cached per-node flags from the last search result (parallel to cachedPathNodes). */
    var cachedKeyNodeFlags: IntArray = IntArray(0)
        private set

    var cachedKeyNodeMetrics: IntArray = IntArray(0)
        private set

    var selectedStartIndex: Int = -1
        private set

    var lastPathSignature: String = ""
        private set

    var lastTimeMs: Long = 0L
        private set

    var lastNodesExplored: Int = 0
        private set

    var lastNanosecondsPerNode: Double = 0.0
        private set

    var lastError: String = ""
        private set

    /**
     * Forward-only cursor into [cachedPathNodes].
     * Written by PathCommand.nearestNodeIndex() during applyToPlayer(); never reset backward.
     */
    var pathNodeCursor: Int = 0
        internal set

    var availabilityFlagsOverride: Int? = null

    /**
     * When true, skips the tunnel-centering logic in PathCommand that pulls the movement guide
     * point to the spatial center of adjacent walkable blocks. Set by route-following callers so
     * the player tracks the recorded waypoints rather than drifting to the room's open center.
     */
    var noTunnelCenter: Boolean = false

    private var state: PathStatus = PathStatus.IDLE
    private var lastTickStatus: PathStatus = PathStatus.IDLE

    // Current goal block position
    private var goalX: Int = 0
    private var goalY: Int = 0
    private var goalZ: Int = 0
    private var arrivalRadius: Double = 1.8

    // Route state
    private var routeWaypoints: List<Triple<Int, Int, Int>> = emptyList()
    private var routeLoop: Boolean = false
    private var routeProfile: MovementProfile = MovementProfile.DEFAULT
    private var routeArrivalRadius: Double = 1.8
    private var routeWpIndex: Int = 0

    private var searchVariantSeed: Int = 0

    private data class SearchPoint(val x: Int, val y: Int, val z: Int)

    private data class AvoidPoint(
        val x: Int,
        val y: Int,
        val z: Int,
        val radiusSq: Int,
        val maxYDiff: Int,
        val penalty: Double,
        var ttlSearches: Int
    )

    private val transientAvoidPoints = mutableListOf<AvoidPoint>()

    // Pre-warmed single-thread pool — keeps a daemon thread alive between searches so
    // there is zero thread-creation overhead when a new path request arrives.
    private val searchExecutor: ExecutorService = Executors.newSingleThreadExecutor { r ->
        Thread(r, "cobalt-pathfinder").apply { isDaemon = true }
    }

    // Async search state (written from search thread, read on game thread)
    @Volatile private var searchResult: NativePathResult? = null
    @Volatile private var searchFailed: Boolean = false
    private var searchFuture: Future<*>? = null

    private var prevJump: Boolean = false
    private var teleportFiredNodeKey: Long = -1L
    private var teleportRestoreSlot: Int = -1
    private var teleportCooldownTicks: Int = 0
    private var selectedTeleportIndex: Int = -1
    private var selectedTeleportYaw: Float = 0f
    private var selectedTeleportPitch: Float = 0f
    private var selectedTeleportAction: ActionType = ActionType.WALK

    private var stuckTicks: Int = 0
    private var lastStuckCheckPos: Vec3 = Vec3.ZERO
    private var collisionTicks: Int = 0
    private var lastCollisionCheckPos: Vec3 = Vec3.ZERO
    private var verticalStallTicks: Int = 0
    private var lastVerticalCheckPos: Vec3 = Vec3.ZERO
    private val stuckPositions: ArrayDeque<BlockPos> = ArrayDeque(4)
    private var cleanExecTicks: Int = 0
    private var lastSearchGoalX: Int = Int.MIN_VALUE
    private var lastSearchGoalY: Int = Int.MIN_VALUE
    private var lastSearchGoalZ: Int = Int.MIN_VALUE
    private var sprintSuppressHysteresisTicks: Int = 0

    private const val STUCK_CHECK_INTERVAL = 20
    private const val STUCK_THRESHOLD = 0.5
    private const val COLLISION_REPLAN_TICKS = 12
    private const val COLLISION_STUCK_THRESHOLD = 0.22
    private const val TELEPORT_YAW_THRESHOLD = 8f
    private const val TELEPORT_PITCH_THRESHOLD = 8f
    private const val MAX_ITERATIONS = 100_000
    private const val HEURISTIC_WEIGHT = 1.5
    private const val NON_PRIMARY_START_PENALTY = 18.0
    /** Minimum horizontal node-to-node distance to trigger AOTV (avoids firing on short hops). */
    private const val AOTV_NODE_DISTANCE_MIN = 6.0
    /** Maximum horizontal distance for AOTV — beyond this the shot is out of range. */
    private const val AOTV_NODE_DISTANCE_MAX = 54.0
    private const val AOTV_MIN_TOTAL_PATH_LENGTH = 40.0
    private const val AOTV_FLUID_STRAIGHTNESS_BONUS = 10.0
    private const val AOTV_FLUID_MIN_GAIN_FACTOR = 0.7
    private const val NODE_REACHED_RANGE = 0.75

    // Per-node flag bits — mirrored from natives/src/path_annotations.cpp
    private const val FLAG_LOW_HEADROOM   = 1 shl 2
    private const val FLAG_STEP_UP_NEXT   = 1 shl 5
    private const val FLAG_TIGHT_CORRIDOR = 1 shl 7

    private const val VERTICAL_STALL_CHECK_INTERVAL = 15
    private const val VERTICAL_STALL_MIN_RISE = 0.2
<<<<<<< Updated upstream
    private const val STUCK_POSITIONS_MAX  = 4
    private const val AVOID_PENALTY        = 25.0
    private const val AVOID_RADIUS_SQ      = 4
    private const val AVOID_MAX_Y_DIFF     = 2
    private const val CLEAN_EXEC_TICKS           = 30
    private const val SPRINT_SUPPRESS_CURVATURE  = 0.4
=======
    private const val STUCK_POSITIONS_MAX = 4
    private const val AVOID_PENALTY = 25.0
    private const val AVOID_RADIUS_SQ = 4
    private const val AVOID_MAX_Y_DIFF = 2
    private const val CLEAN_EXEC_TICKS = 30
    private const val SPRINT_SUPPRESS_CURVATURE = 0.4
    private const val SPRINT_SUPPRESS_HYSTERESIS_TICKS = 6
>>>>>>> Stashed changes

    fun init() {
        // NativePathfinderJNI object init loads the DLL on first access
        NativePathfinderJNI.initNative()
        // Warm the pool thread — submitting a no-op brings the thread to RUNNABLE
        // state so the first real search dispatches with no cold-start overhead.
        searchExecutor.submit {}
    }

    fun destroy() {
        cancelSearch()
        searchExecutor.shutdownNow()
        releaseGuidedControl()
    }

    fun setTarget(x: Double, y: Double, z: Double) {
        setTargetWithRadius(x, y, z, 1.8)
    }

    fun setTargetWithRadius(x: Double, y: Double, z: Double, radius: Double) {
        routeWaypoints = emptyList()
        goalX = x.toInt(); goalY = y.toInt(); goalZ = z.toInt()
        arrivalRadius = radius
        startSearch()
    }

    fun setTargetWithStarts(
        starts: List<BlockPos>,
        goals: List<BlockPos>,
        isFly: Boolean = false,
        radius: Double = 1.8
    ): Boolean {
        if (starts.isEmpty() || goals.isEmpty()) {
            lastError = "No start or goal points were provided"
            state = PathStatus.FAILED
            return false
        }
        routeWaypoints = emptyList()
        routeProfile = if (isFly) MovementProfile.FLY else MovementProfile.DEFAULT
        val finalGoal = goals.first()
        goalX = finalGoal.x; goalY = finalGoal.y; goalZ = finalGoal.z
        arrivalRadius = radius
        return startSearch(starts.map { SearchPoint(it.x, if (isFly) it.y else it.y + 1, it.z) }, goals.map { SearchPoint(it.x, if (isFly) it.y else it.y + 1, it.z) }, isFly)
    }

    fun setFlyTarget(x: Double, y: Double, z: Double, radius: Double = 2.5) {
        routeWaypoints = emptyList()
        routeProfile = MovementProfile.FLY
        goalX = x.toInt(); goalY = y.toInt(); goalZ = z.toInt()
        arrivalRadius = radius
        startSearch(isFly = true)
    }

    fun setRoute(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile) {
        setRouteWithRadius(waypoints, loop, profile, 1.8)
    }

    fun setRouteWithRadius(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile, arrivalRadius: Double) {
        routeLoop = loop
        routeProfile = profile
        routeArrivalRadius = arrivalRadius
        routeWpIndex = 0
        routeWaypoints = buildList {
            var i = 0
            while (i + 2 < waypoints.size) {
                add(Triple(waypoints[i].toInt(), waypoints[i + 1].toInt(), waypoints[i + 2].toInt()))
                i += 3
            }
        }
        if (routeWaypoints.isNotEmpty()) {
            val wp = routeWaypoints[0]
            goalX = wp.first; goalY = wp.second; goalZ = wp.third
            this.arrivalRadius = arrivalRadius
            startSearch()
        }
    }

    fun stop() {
        cancelSearch()
<<<<<<< Updated upstream
        state = PathStatus.IDLE
        cachedFullPathNodes = emptyList()
        cachedPathNodes = emptyList()
        cachedPathFlags = IntArray(0)
        cachedKeyNodeFlags = IntArray(0)
        cachedKeyNodeMetrics = IntArray(0)
        selectedStartIndex = -1
        lastPathSignature = ""
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        noTunnelCenter = false
        routeWpIndex = 0
        stuckTicks = 0
        collisionTicks = 0
        lastStuckCheckPos = Vec3.ZERO
        lastCollisionCheckPos = Vec3.ZERO
        verticalStallTicks = 0
        lastVerticalCheckPos = Vec3.ZERO
        stuckPositions.clear()
        transientAvoidPoints.clear()
        cleanExecTicks = 0
        PathExecutorState.reset()
        releaseGuidedControl()
=======
        resetPathState()
        lastSearchGoalX = Int.MIN_VALUE
        lastSearchGoalY = Int.MIN_VALUE
        lastSearchGoalZ = Int.MIN_VALUE
        sprintSuppressHysteresisTicks = 0
>>>>>>> Stashed changes
    }

    fun onLevelChange() {
        cancelSearch()
        ChunkSerializer.invalidate()
        resetPathState()
    }

    private fun resetPathState() {
        state = PathStatus.IDLE
        cachedFullPathNodes = emptyList()
        cachedPathNodes = emptyList()
        cachedPathFlags = IntArray(0)
        cachedKeyNodeFlags = IntArray(0)
        cachedKeyNodeMetrics = IntArray(0)
        selectedStartIndex = -1
        lastPathSignature = ""
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        teleportCooldownTicks = 0
        resetSelectedTeleport()
        availabilityFlagsOverride = null
        noTunnelCenter = false
        routeWpIndex = 0
        stuckTicks = 0
        collisionTicks = 0
        lastStuckCheckPos = Vec3.ZERO
        lastCollisionCheckPos = Vec3.ZERO
        verticalStallTicks = 0
        lastVerticalCheckPos = Vec3.ZERO
        stuckPositions.clear()
        transientAvoidPoints.clear()
        cleanExecTicks = 0
        PathExecutorState.reset()
        releaseGuidedControl()
    }

    val status: PathStatus get() = state

    fun tick(): PathCommand? {
        val mc = Minecraft.getInstance()
<<<<<<< Updated upstream
        val player = mc.player ?: run { releaseGuidedControl(); return null }

        // Consume completed search result on game thread
=======
        val player = mc.player ?: run {
            releaseGuidedControl()
            return null
        }

>>>>>>> Stashed changes
        if (searchFailed) {
            searchFailed = false
            searchFuture = null
            if (state == PathStatus.PLANNING) {
                state = PathStatus.FAILED
                logFailure("findPath returned no result")
            } else {
                // REPLANNING failed — keep executing old path
                state = PathStatus.EXECUTING
            }
        } else {
            val result = searchResult
            if (result != null) {
                searchResult = null
                searchFuture = null
                applySearchResult(result)
            }
        }

        when (state) {
            PathStatus.IDLE, PathStatus.FAILED, PathStatus.ARRIVED -> {
                if (state != lastTickStatus) {
                    prevJump = false
                    cachedFullPathNodes = emptyList()
                    cachedPathNodes = emptyList()
                    pathNodeCursor = 0
                    releaseGuidedControl()
                }
                lastTickStatus = state
                return null
            }
            PathStatus.PLANNING -> {
                lastTickStatus = state
                return null
            }
<<<<<<< Updated upstream
            else -> {}
=======

            PathStatus.RECOVERING -> {
                // Old DUSk/V5 core did not run a separate recovery controller here.
                // Fall back to EXECUTING so the normal replan/stuck logic keeps control.
                state = PathStatus.EXECUTING
            }

            else -> {
                // EXECUTING or REPLANNING.
            }
>>>>>>> Stashed changes
        }

        // EXECUTING or REPLANNING
        val nodes = cachedPathNodes
        if (nodes.isEmpty()) {
            state = PathStatus.FAILED
            releaseGuidedControl()
            lastTickStatus = state
            return null
        }

        val playerPos = Vec3(player.x, player.y, player.z)
        if (lastStuckCheckPos == Vec3.ZERO) {
            lastStuckCheckPos = playerPos
        }

        // Short collision loops are a better signal for corner snags than the coarse 60-tick stall check.
        if (player.horizontalCollision && player.onGround() && state == PathStatus.EXECUTING) {
            if (collisionTicks == 0 || lastCollisionCheckPos == Vec3.ZERO) {
                lastCollisionCheckPos = playerPos
            }
            collisionTicks++
            if (collisionTicks >= COLLISION_REPLAN_TICKS) {
                collisionTicks = 0
                val moved = playerPos.distanceTo(lastCollisionCheckPos)
                if (moved < COLLISION_STUCK_THRESHOLD) {
                    startReplan(playerPos)
                }
                lastCollisionCheckPos = playerPos
            }
        } else {
            collisionTicks = 0
            lastCollisionCheckPos = playerPos
        }

        // Check arrival at current goal
        val dx = goalX - player.x
        val dy = goalY - player.y
        val dz = goalZ - player.z
        val distToGoal = sqrt(dx * dx + dy * dy + dz * dz)

        if (routeProfile == MovementProfile.FLY) {
            val flyUpdate =
                PathFlyMovementController.update(
                    player,
                    cachedFullPathNodes.ifEmpty { nodes },
                    Vec3(goalX + 0.5, goalY.toDouble(), goalZ + 0.5),
                    state,
                    distToGoal.toFloat()
                )
            if (flyUpdate.arrived) {
                advanceRouteOrArrive()
                lastTickStatus = state
                return null
            }
            lastTickStatus = state
            return flyUpdate.command
        }

        if (distToGoal <= arrivalRadius) {
            if (advanceRouteOrArrive()) {
                lastTickStatus = state
                return null
            }
        }

        // Vertical stall — if the upcoming node is a step-up but Y hasn't risen, replan
        if (state == PathStatus.EXECUTING) {
            val upcomingFlags = cachedKeyNodeFlags.getOrElse(pathNodeCursor) { 0 }
            if (upcomingFlags and FLAG_STEP_UP_NEXT != 0) {
                if (lastVerticalCheckPos == Vec3.ZERO) lastVerticalCheckPos = playerPos
                verticalStallTicks++
                if (verticalStallTicks >= VERTICAL_STALL_CHECK_INTERVAL) {
                    verticalStallTicks = 0
                    val rise = playerPos.y - lastVerticalCheckPos.y
                    if (rise < VERTICAL_STALL_MIN_RISE) startReplan(playerPos)
                    lastVerticalCheckPos = playerPos
                }
            } else {
                verticalStallTicks = 0
                lastVerticalCheckPos = Vec3.ZERO
            }
        }

        // Stuck detection — only trigger new replan when EXECUTING (not already REPLANNING)
        stuckTicks++
        if (stuckTicks >= STUCK_CHECK_INTERVAL && state == PathStatus.EXECUTING) {
            stuckTicks = 0
            val moved = playerPos.distanceTo(lastStuckCheckPos)
            if (moved < STUCK_THRESHOLD) startReplan(playerPos)
            lastStuckCheckPos = playerPos
        }

        if (state == PathStatus.EXECUTING && stuckPositions.isNotEmpty()) {
            cleanExecTicks++
            if (cleanExecTicks >= CLEAN_EXEC_TICKS) {
                stuckPositions.clear()
                cleanExecTicks = 0
            }
        }

        // Update V5-style dense path progress for rotations, while keeping the key-node
        // cursor forward-only for flags, AOTV decisions, and jump annotations.
        val rotationNodes = cachedFullPathNodes.ifEmpty { nodes }
        PathExecutorState.tickOverrides()
        PathExecutorState.updateLookPoints(rotationNodes, lastPathSignature)
        PathExecutorState.update(player, rotationNodes)
        advanceKeyCursor(player, nodes)

        if (state == PathStatus.EXECUTING &&
            PathExecutorState.trackNonChangeProgress(PathExecutorState.currentPathPosition)
        ) {
            startRecovery("nonchange", AvoidReason.STUCK, playerPos)
            lastTickStatus = state
            return null
        }

        if (teleportCooldownTicks > 0) {
            teleportCooldownTicks--
        }

        val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
        val computedFlags = if (aotvSlot >= 0) 0x1 or 0x2 else 0
        val availabilityFlags = availabilityFlagsOverride ?: computedFlags

        val flagsAtCursor = cachedKeyNodeFlags.getOrElse(pathNodeCursor) { 0 }
        val lowHeadroom   = flagsAtCursor and FLAG_LOW_HEADROOM   != 0
        val tightCorridor = flagsAtCursor and FLAG_TIGHT_CORRIDOR != 0

        val activeAction = computeActiveAction(nodes, pathNodeCursor, aotvSlot >= 0, flagsAtCursor)
        val directTargetIndex = directRotationTargetIndex(activeAction, pathNodeCursor, nodes.lastIndex)
        val directTargetNode = nodes.getOrNull(directTargetIndex) ?: nodes.last()
<<<<<<< Updated upstream
        val ndx = directTargetNode.x + 0.5 - player.x
        val ndz = directTargetNode.z + 0.5 - player.z
        val targetYaw = Math.toDegrees(atan2(-ndx, ndz)).toFloat()
=======

        val targetYaw = if (activeAction == ActionType.ETHERWARP || activeAction == ActionType.AOTV) {
            selectedTeleportYaw
        } else {
            val ndx = directTargetNode.x + 0.5 - player.x
            val ndz = directTargetNode.z + 0.5 - player.z
            Math.toDegrees(atan2(-ndx, ndz)).toFloat()
        }
        val targetPitch = if (activeAction == ActionType.ETHERWARP || activeAction == ActionType.AOTV) {
            selectedTeleportPitch
        } else {
            0f
        }
>>>>>>> Stashed changes

        // Jump pulse logic uses the current key node, not the direct rotation target.
        val jumpNode = nodes.getOrNull(minOf(pathNodeCursor, nodes.lastIndex)) ?: nodes.last()
        val jdx = jumpNode.x + 0.5 - player.x
        val jdz = jumpNode.z + 0.5 - player.z
        val ndyRaw = jumpNode.y - player.y
        val jumpRaw = player.onGround() && ndyRaw > 0.5 && sqrt(jdx * jdx + jdz * jdz) < 2.5
        if (jumpRaw && player.onGround()) prevJump = false
        val jumpPulse = jumpRaw && !prevJump
        prevJump = jumpRaw

        val highCurvature = PathExecutorState.pathCurvature >= SPRINT_SUPPRESS_CURVATURE
        if (highCurvature) {
            sprintSuppressHysteresisTicks = SPRINT_SUPPRESS_HYSTERESIS_TICKS
        } else if (sprintSuppressHysteresisTicks > 0) {
            sprintSuppressHysteresisTicks--
        }

        val cmd = PathCommand(
            forward = true,
            back = false,
            jump = jumpPulse,
            sneak = false,
<<<<<<< Updated upstream
            sprint = !lowHeadroom && !tightCorridor && PathExecutorState.pathCurvature < SPRINT_SUPPRESS_CURVATURE,
=======
            sprint = !lowHeadroom &&
                !tightCorridor &&
                !highCurvature &&
                sprintSuppressHysteresisTicks == 0,
>>>>>>> Stashed changes
            targetYaw = targetYaw,
            targetPitch = targetPitch,
            status = state,
            activeAction = activeAction,
            distanceToTarget = distToGoal.toFloat()
        )

        // AOTV / Etherwarp fire on rotation convergence
        if (state == PathStatus.EXECUTING &&
            (cmd.activeAction == ActionType.AOTV || cmd.activeAction == ActionType.ETHERWARP)) {
            val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, cmd.targetYaw))
            val pitchErr = abs(player.xRot - cmd.targetPitch)
            val aligned = yawErr < TELEPORT_YAW_THRESHOLD &&
                (cmd.activeAction == ActionType.AOTV || pitchErr < TELEPORT_PITCH_THRESHOLD)
            val nodeKey = cachedPathNodes.getOrNull(directTargetIndex)
                ?.let { n -> (n.x.toLong() and 0x1FFFFF) or ((n.z.toLong() and 0x1FFFFF) shl 21) }
                ?: -1L
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
            if (aligned && nodeKey != teleportFiredNodeKey && aotvSlot >= 0) {
                teleportFiredNodeKey = nodeKey
                fireTeleport(cmd.activeAction == ActionType.ETHERWARP, aotvSlot)
                teleportCooldownTicks = PathTeleportConfig.teleportCooldownTicks.coerceAtLeast(1)
                if (selectedTeleportIndex > pathNodeCursor) {
                    pathNodeCursor = selectedTeleportIndex.coerceIn(0, nodes.lastIndex)
                }
            }
        } else if (cmd.activeAction != ActionType.AOTV && cmd.activeAction != ActionType.ETHERWARP) {
            teleportFiredNodeKey = -1L
        }

        if (state != lastTickStatus && state == PathStatus.EXECUTING) {
            DebugLog.debug(mc, "Pathfinding", "NativePathfinder: executing path, ${nodes.size} key nodes")
        }
        lastTickStatus = state
        return cmd
    }

    fun setSearchVariantSeed(seed: Int) {
        searchVariantSeed = seed.coerceAtLeast(0)
    }

    private fun advanceRouteOrArrive(): Boolean {
        if (routeWaypoints.isNotEmpty()) {
            routeWpIndex++
            val totalWp = routeWaypoints.size
            val nextIndex = if (routeLoop) routeWpIndex % totalWp else routeWpIndex
            if (nextIndex < totalWp) {
                routeWpIndex = nextIndex
                val wp = routeWaypoints[nextIndex]
                goalX = wp.first; goalY = wp.second; goalZ = wp.third
                arrivalRadius = routeArrivalRadius
                startSearch()
                return true
            }
        }
        state = PathStatus.ARRIVED
        cachedFullPathNodes = emptyList()
        cachedPathNodes = emptyList()
        pathNodeCursor = 0
        releaseGuidedControl()
        return true
    }

    fun addTransientAvoidPoint(
        x: Int,
        y: Int,
        z: Int,
        radius: Int = 2,
        penalty: Double = 36.0,
        ttlSearches: Int = 2,
        maxYDiff: Int = 2
    ) {
        transientAvoidPoints += AvoidPoint(
            x = x,
            y = y,
            z = z,
            radiusSq = (radius.coerceAtLeast(1) * radius.coerceAtLeast(1)),
            maxYDiff = maxYDiff.coerceAtLeast(0),
            penalty = penalty,
            ttlSearches = ttlSearches.coerceAtLeast(1)
        )
    }

    fun clearTransientAvoidPoints() {
        transientAvoidPoints.clear()
    }

    private fun buildAvoidInputs(): Pair<IntArray, DoubleArray> {
        val total = stuckPositions.size + transientAvoidPoints.size
        if (total == 0) return intArrayOf() to doubleArrayOf()

        val meta = IntArray(total * 5)
        val penalties = DoubleArray(total)
        var i = 0
        var p = 0
        for (pos in stuckPositions) {
            meta[i++] = pos.x
            meta[i++] = pos.y
            meta[i++] = pos.z
            meta[i++] = AVOID_RADIUS_SQ
            meta[i++] = AVOID_MAX_Y_DIFF
            penalties[p++] = AVOID_PENALTY
        }

        for (point in transientAvoidPoints) {
            meta[i++] = point.x
            meta[i++] = point.y
            meta[i++] = point.z
            meta[i++] = point.radiusSq
            meta[i++] = point.maxYDiff
            penalties[p++] = point.penalty
        }

        transientAvoidPoints.removeAll { point ->
            point.ttlSearches--
            point.ttlSearches <= 0
        }

        return meta to penalties
    }

    private fun submitSearch(starts: List<SearchPoint>, goals: List<SearchPoint>, isFly: Boolean) {
        val (avoidMeta, avoidPenalty) = buildAvoidInputs()
        val moveOrderOffset = searchVariantSeed.coerceAtLeast(0)
        val flatStarts = starts.toFlatIntArray()
        val flatGoals = goals.toFlatIntArray()
        searchFuture = searchExecutor.submit {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = flatStarts,
                    endPoints = flatGoals,
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = NON_PRIMARY_START_PENALTY,
                    moveOrderOffset = moveOrderOffset,
                    avoidMeta = avoidMeta,
                    avoidPenalty = avoidPenalty
                )
                if (result != null && result.keyPath.size >= 3) {
                    searchResult = result
                } else {
                    lastError = "findPath returned no result"
                    searchFailed = true
                }
            } catch (e: Exception) {
                lastError = e.message ?: "Native path search failed"
                searchFailed = true
            }
        }
    }

    private fun startSearch(isFly: Boolean = routeProfile == MovementProfile.FLY): Boolean {
        cancelSearch()
<<<<<<< Updated upstream
        stuckPositions.clear()
        cleanExecTicks = 0
=======

        val sameGoal = goalX == lastSearchGoalX && goalY == lastSearchGoalY && goalZ == lastSearchGoalZ
        val preserveAvoidance = sameGoal && state == PathStatus.FAILED
        lastSearchGoalX = goalX
        lastSearchGoalY = goalY
        lastSearchGoalZ = goalZ

        if (!preserveAvoidance) {
            stuckPositions.clear()
            transientAvoidPoints.clear()
            debugAvoidPoints = emptyList()
        }
        cleanExecTicks = 0
        debugReplanCount = 0
        debugLastRecoveryReason = "none"
        debugLastAvoidPoint = null
        PathRecoveryController.reset()
        TeleportValidationController.reset()

>>>>>>> Stashed changes
        state = PathStatus.PLANNING
        val player = Minecraft.getInstance().player ?: run {
            lastError = "Player is missing"
            state = PathStatus.FAILED
            return false
        }
<<<<<<< Updated upstream
        val startY = if (isFly) player.blockY else player.blockY
        val goalYAdjusted = if (isFly) goalY else goalY
=======

        val startY = player.blockY
        val goalYAdjusted = goalY

>>>>>>> Stashed changes
        submitSearch(
            starts = listOf(SearchPoint(player.blockX, startY, player.blockZ)),
            goals = listOf(SearchPoint(goalX, goalYAdjusted, goalZ)),
            isFly = isFly
        )
        return true
    }

    private fun startSearch(starts: List<SearchPoint>, goals: List<SearchPoint>, isFly: Boolean): Boolean {
        cancelSearch()
        stuckPositions.clear()
        cleanExecTicks = 0
        state = PathStatus.PLANNING
        submitSearch(starts, goals, isFly)
        return true
    }

<<<<<<< Updated upstream
=======
    private fun startRecovery(reason: String, avoidReason: AvoidReason, playerPos: Vec3) {
        // Old DUSk/V5 behavior: do not enter a separate recovery state.
        // Add the avoid point, then immediately replan from the current player position.
        debugLastRecoveryReason = reason
        addDebugAvoidPoint(
            pos = BlockPos.containing(playerPos),
            reason = avoidReason,
            radius = 2,
            penalty = 36.0,
            ttlSearches = 2
        )
        startReplan(playerPos)
    }

>>>>>>> Stashed changes
    private fun startReplan(playerPos: Vec3) {
        if (state == PathStatus.REPLANNING) return
        state = PathStatus.REPLANNING
        NativePathfinderJNI.cancelSearch()
        searchFuture?.cancel(true)
        searchFuture = null
        cleanExecTicks = 0
        if (stuckPositions.size >= STUCK_POSITIONS_MAX) stuckPositions.removeFirst()
        stuckPositions.addLast(BlockPos.containing(playerPos))
        val startPos = BlockPos.containing(playerPos)
        submitSearch(
            starts = listOf(SearchPoint(startPos.x, startPos.y, startPos.z)),
            goals = listOf(SearchPoint(goalX, goalY, goalZ)),
            isFly = routeProfile == MovementProfile.FLY
        )
    }

    private fun applySearchResult(result: NativePathResult) {
        cachedFullPathNodes = result.path.toVec3List()
        cachedPathNodes = result.keyPath.toVec3List()
        cachedPathFlags = result.pathFlags
        cachedKeyNodeFlags = result.keyNodeFlags
        cachedKeyNodeMetrics = result.keyNodeMetrics
        selectedStartIndex = result.selectedStartIndex
        lastPathSignature = result.signature
        lastTimeMs = result.timeMs
        lastNodesExplored = result.nodesExplored
        lastNanosecondsPerNode = result.nanosecondsPerNode
        lastError = ""
        pathNodeCursor = 0
        stuckTicks = 0
        collisionTicks = 0
        PathExecutorState.reset()
        PathFlyMovementController.reset()
        state = PathStatus.EXECUTING
        val mc = Minecraft.getInstance()
        val playerPos = mc.player?.position() ?: Vec3.ZERO
        lastStuckCheckPos = playerPos
        lastCollisionCheckPos = playerPos
        ModuleDebug.log("Pathfinding", "Path built in ${result.timeMs}ms")
        DebugLog.status(mc, "Pathfinding", "Pathfinder: path found: ${cachedPathNodes.size} key nodes, ${cachedFullPathNodes.size} nodes, goal ($goalX,$goalY,$goalZ), ${result.timeMs}ms, ${result.nodesExplored} explored")
    }

    private fun cancelSearch() {
        NativePathfinderJNI.cancelSearch()
        searchFuture?.cancel(true)
        searchFuture = null
        searchResult = null
        searchFailed = false
        state = PathStatus.IDLE
    }

    private fun List<SearchPoint>.toFlatIntArray(): IntArray {
        val out = IntArray(size * 3)
        var i = 0
        for (point in this) {
            out[i++] = point.x
            out[i++] = point.y
            out[i++] = point.z
        }
        return out
    }

    private fun IntArray.toVec3List(): List<Vec3> {
        val out = ArrayList<Vec3>(size / 3)
        var i = 0
        while (i + 2 < size) {
            out.add(Vec3(this[i].toDouble(), this[i + 1].toDouble(), this[i + 2].toDouble()))
            i += 3
        }
        return out
    }

    private fun fireTeleport(isEtherwarp: Boolean, aotvSlot: Int) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
<<<<<<< Updated upstream
=======

        PathExecutorState.onTeleportTriggered(selectedTeleportIndex.toDouble())

>>>>>>> Stashed changes
        teleportRestoreSlot = player.inventory.selectedSlot
        InventoryUtils.holdHotbarSlot(aotvSlot)
        if (isEtherwarp) mc.options.keyShift?.setDown(true)
        mc.options.keyUse?.setDown(true)
        TickScheduler.schedule(1L) {
            mc.options.keyUse?.setDown(false)
            if (isEtherwarp) mc.options.keyShift?.setDown(false)
        }
        TickScheduler.schedule(4L) {
            val restore = teleportRestoreSlot
            if (restore in 0..8) InventoryUtils.holdHotbarSlot(restore)
            teleportRestoreSlot = -1
        }
    }

    private fun advanceKeyCursor(player: net.minecraft.client.player.LocalPlayer, nodes: List<Vec3>) {
        if (nodes.isEmpty()) return
        val playerPos = player.position()

        while (
            pathNodeCursor < nodes.lastIndex &&
            isOnBlock(centerNode(nodes[pathNodeCursor]), playerPos, NODE_REACHED_RANGE)
        ) {
            pathNodeCursor++
        }

        while (
            pathNodeCursor < nodes.lastIndex &&
            isOnBlock(centerNode(nodes[pathNodeCursor + 1]), playerPos, NODE_REACHED_RANGE)
        ) {
            pathNodeCursor++
        }

        val cursor = pathNodeCursor.coerceIn(0, nodes.lastIndex)
        val searchEnd = minOf(nodes.lastIndex, cursor + 16)
        var nearestIdx = cursor
        var nearestDistSq = Double.POSITIVE_INFINITY
        for (i in maxOf(0, cursor - 1)..searchEnd) {
            val n = nodes[i]
            val dx = n.x + 0.5 - player.x
            val dz = n.z + 0.5 - player.z
            val distSq = dx * dx + dz * dz
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq
                nearestIdx = i
            }
        }
        if (nearestIdx > pathNodeCursor) {
            pathNodeCursor = nearestIdx
        }
    }

    private fun isOnBlock(point: Vec3, playerPos: Vec3, range: Double): Boolean {
        return playerPos.x >= point.x - range && playerPos.x < point.x + range &&
            playerPos.z >= point.z - range && playerPos.z < point.z + range
    }

    private fun centerNode(node: Vec3): Vec3 =
        Vec3(node.x + 0.5, node.y, node.z + 0.5)

<<<<<<< Updated upstream
    /**
     * Decides whether the current path segment warrants an AOTV shot.
     * When consecutive key nodes are far apart the path simplifier has left a long straight
     * leg — AOTV can bridge it faster than walking.
     */
    private fun computeActiveAction(nodes: List<Vec3>, cursor: Int, aotvAvailable: Boolean, nodeFlags: Int): ActionType {
        if (aotvAvailable && nodes.size >= 2) {
            val current = nodes.getOrNull(cursor)
            val next = nodes.getOrNull(cursor + 1)
            if (current != null && next != null) {
                val dx = next.x - current.x
                val dz = next.z - current.z
                val horizDist = sqrt(dx * dx + dz * dz)
                if (horizDist in AOTV_NODE_DISTANCE_MIN..AOTV_NODE_DISTANCE_MAX) return ActionType.AOTV
=======
    private fun computeActiveAction(
        nodes: List<Vec3>,
        cursor: Int,
        aotvAvailable: Boolean,
        nodeFlags: Int
    ): ActionType {
        resetSelectedTeleport()

        if (aotvAvailable && nodes.size >= 2 && teleportCooldownTicks <= 0) {
            val teleport = selectV5TeleportCandidate(nodes, cursor)
            if (teleport != null) {
                selectedTeleportIndex = teleport.index
                selectedTeleportYaw = teleport.yaw
                selectedTeleportPitch = teleport.pitch
                selectedTeleportAction = teleport.action
                return teleport.action
>>>>>>> Stashed changes
            }
        }
        if (nodeFlags and FLAG_STEP_UP_NEXT != 0) return ActionType.SPRINT_JUMP
        return ActionType.WALK
    }

<<<<<<< Updated upstream
    private fun directRotationTargetIndex(activeAction: ActionType, cursor: Int, lastIndex: Int): Int =
        when (activeAction) {
            ActionType.AOTV, ActionType.ETHERWARP -> minOf(cursor + 1, lastIndex)
=======
    private data class TeleportCandidate(
        val index: Int,
        val yaw: Float,
        val pitch: Float,
        val action: ActionType
    )

    private fun selectV5TeleportCandidate(nodes: List<Vec3>, cursor: Int): TeleportCandidate? {
        val player = Minecraft.getInstance().player ?: return null
        if (nodes.isEmpty()) return null

        val distanceToFinal = player.position().distanceTo(centerNode(nodes.last()))
        if (distanceToFinal <= PathTeleportConfig.finalNoTeleportRadius) return null

        // V5 gate: total arc length of remaining path must be >= 40 blocks before teleporting.
        val remainingLength = estimatePathGain(nodes, cursor, nodes.lastIndex)
        if (remainingLength < AOTV_MIN_TOTAL_PATH_LENGTH) return null

        // V5 fluid mode: relax constraints when the player is in water or lava.
        val inFluid = player.isInWater || player.isInLava
        val effectiveStraightnessTolerance = if (inFluid)
            PathTeleportConfig.teleportStraightnessTolerance + AOTV_FLUID_STRAIGHTNESS_BONUS
        else
            PathTeleportConfig.teleportStraightnessTolerance
        val effectiveMinGain = if (inFluid)
            PathTeleportConfig.minTeleportGain * AOTV_FLUID_MIN_GAIN_FACTOR
        else
            PathTeleportConfig.minTeleportGain

        val maxRange = EtherwarpLogic.getEtherwarpRange().toDouble().coerceAtLeast(AOTV_NODE_DISTANCE_MAX)
        val maxIndex = minOf(nodes.lastIndex, cursor + PathTeleportConfig.maxLookAheadNodes.coerceAtLeast(1))

        var best: TeleportCandidate? = null
        var bestGain = 0.0

        for (i in maxIndex downTo cursor + 1) {
            val node = nodes[i]
            val aimBlock = BlockPos(
                Math.floor(node.x).toInt(),
                Math.floor(node.y - 1.0).toInt(),
                Math.floor(node.z).toInt()
            )
            val aimPoint = Vec3(aimBlock.x + 0.5, aimBlock.y + 0.92, aimBlock.z + 0.5)
            val gain = estimatePathGain(nodes, cursor, i)
            if (gain < effectiveMinGain) continue

            val dx = aimPoint.x - player.eyePosition.x
            val dy = aimPoint.y - player.eyePosition.y
            val dz = aimPoint.z - player.eyePosition.z
            val dist = sqrt(dx * dx + dy * dy + dz * dz)
            if (dist > maxRange + 0.75) continue

            val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
            val pitch = Math.toDegrees(-atan2(dy, sqrt(dx * dx + dz * dz))).toFloat()

            if (PathTeleportConfig.v5EtherwarpEnabled) {
                val direct = EtherwarpLogic.getEtherwarpResultTo(aimBlock, aimPoint)
                if (direct.succeeded && direct.pos == aimBlock) {
                    best = TeleportCandidate(i, yaw, pitch, ActionType.ETHERWARP)
                    bestGain = gain
                    break
                }
            }

            if (PathTeleportConfig.v5AotvEnabled && gain >= AOTV_NODE_DISTANCE_MIN && gain <= AOTV_NODE_DISTANCE_MAX) {
                // V5: only use AOTV on straight-ish path chunks; fluid mode uses relaxed tolerance.
                if (isTeleportSegmentStraightEnough(nodes, cursor, i, effectiveStraightnessTolerance)) {
                    best = TeleportCandidate(i, yaw, 0f, ActionType.AOTV)
                    bestGain = gain
                    break
                }
            }
        }

        return best
    }

    private fun estimatePathGain(nodes: List<Vec3>, start: Int, end: Int): Double {
        if (start >= end) return 0.0
        var total = 0.0
        var prev = nodes[start]
        for (i in (start + 1)..end) {
            val next = nodes[i]
            val dx = next.x - prev.x
            val dy = next.y - prev.y
            val dz = next.z - prev.z
            total += sqrt(dx * dx + dy * dy + dz * dz)
            prev = next
        }
        return total
    }

    private fun isTeleportSegmentStraightEnough(
        nodes: List<Vec3>,
        start: Int,
        end: Int,
        tolerance: Double = PathTeleportConfig.teleportStraightnessTolerance
    ): Boolean {
        if (end <= start + 1) return true
        val a = nodes[start]
        val b = nodes[end]
        val baseX = b.x - a.x
        val baseZ = b.z - a.z
        val baseLen = sqrt(baseX * baseX + baseZ * baseZ)
        if (baseLen < 0.001) return false

        val nx = -baseZ / baseLen
        val nz = baseX / baseLen
        var maxOffset = 0.0

        for (i in start + 1 until end) {
            val p = nodes[i]
            val ox = p.x - a.x
            val oz = p.z - a.z
            maxOffset = maxOf(maxOffset, abs(ox * nx + oz * nz))
        }

        return maxOffset <= tolerance
    }

    private fun resetSelectedTeleport() {
        selectedTeleportIndex = -1
        selectedTeleportYaw = 0f
        selectedTeleportPitch = 0f
        selectedTeleportAction = ActionType.WALK
    }

    private fun directRotationTargetIndex(
        activeAction: ActionType,
        cursor: Int,
        lastIndex: Int
    ): Int {
        return when (activeAction) {
            ActionType.AOTV,
            ActionType.ETHERWARP -> if (selectedTeleportIndex >= 0) selectedTeleportIndex else minOf(cursor + 1, lastIndex)

>>>>>>> Stashed changes
            else -> minOf(cursor + 1, lastIndex)
        }.coerceIn(0, lastIndex)

    private fun logFailure(reason: String) {
        val mc = Minecraft.getInstance()
        DebugLog.status(mc, "Pathfinding", "Pathfinder: path failed: $reason")
    }

    private fun releaseGuidedControl() {
        PathFlyMovementController.reset()
        MovementManager.setLookLock(false)
        RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
    }
}
