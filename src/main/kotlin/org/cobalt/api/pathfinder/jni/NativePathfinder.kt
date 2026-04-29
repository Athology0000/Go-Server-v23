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
import org.cobalt.internal.pathfinding.DebugLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

object NativePathfinder {

    val isInitialized: Boolean get() = true

    /** Cached key path nodes (integer block positions as Vec3). Updated on EXECUTING transition. */
    var cachedPathNodes: List<Vec3> = emptyList()
        private set

    /** Cached per-node flags from the last search result (parallel to cachedPathNodes). */
    var cachedKeyNodeFlags: IntArray = IntArray(0)
        private set

    /**
     * Forward-only cursor into [cachedPathNodes].
     * Written by PathCommand.nearestNodeIndex() during applyToPlayer(); never reset backward.
     */
    var pathNodeCursor: Int = 0
        internal set

    var availabilityFlagsOverride: Int? = null

    /** When true, suppresses strafe inputs — only W key and camera are used for movement. */
    var noStrafe: Boolean = false

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

    private var stuckTicks: Int = 0
    private var lastStuckCheckPos: Vec3 = Vec3.ZERO
    private var collisionTicks: Int = 0
    private var lastCollisionCheckPos: Vec3 = Vec3.ZERO
    private var verticalStallTicks: Int = 0
    private var lastVerticalCheckPos: Vec3 = Vec3.ZERO

    private const val STUCK_CHECK_INTERVAL = 20
    private const val STUCK_THRESHOLD = 0.5
    private const val COLLISION_REPLAN_TICKS = 12
    private const val COLLISION_STUCK_THRESHOLD = 0.22
    private const val TELEPORT_YAW_THRESHOLD = 8f
    private const val TELEPORT_PITCH_THRESHOLD = 8f
    private const val MAX_ITERATIONS = 100_000
    private const val HEURISTIC_WEIGHT = 1.5
    /** Minimum horizontal node-to-node distance to trigger AOTV (avoids firing on short hops). */
    private const val AOTV_NODE_DISTANCE_MIN = 6.0
    /** Maximum horizontal distance for AOTV — beyond this the shot is out of range. */
    private const val AOTV_NODE_DISTANCE_MAX = 54.0

    // Per-node flag bits — mirrored from natives/src/path_annotations.cpp
    private const val FLAG_LOW_HEADROOM   = 1 shl 2
    private const val FLAG_STEP_UP_NEXT   = 1 shl 5
    private const val FLAG_TIGHT_CORRIDOR = 1 shl 7

    private const val VERTICAL_STALL_CHECK_INTERVAL = 15
    private const val VERTICAL_STALL_MIN_RISE = 0.2

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
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        cachedKeyNodeFlags = IntArray(0)
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        noStrafe = false
        noTunnelCenter = false
        routeWpIndex = 0
        stuckTicks = 0
        collisionTicks = 0
        lastStuckCheckPos = Vec3.ZERO
        lastCollisionCheckPos = Vec3.ZERO
        verticalStallTicks = 0
        lastVerticalCheckPos = Vec3.ZERO
        releaseGuidedControl()
    }

    fun onLevelChange() {
        cancelSearch()
        ChunkSerializer.invalidate()
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        cachedKeyNodeFlags = IntArray(0)
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        noStrafe = false
        noTunnelCenter = false
        routeWpIndex = 0
        stuckTicks = 0
        collisionTicks = 0
        lastStuckCheckPos = Vec3.ZERO
        lastCollisionCheckPos = Vec3.ZERO
        verticalStallTicks = 0
        lastVerticalCheckPos = Vec3.ZERO
        releaseGuidedControl()
    }

    val status: PathStatus get() = state

    fun tick(): PathCommand? {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: run { releaseGuidedControl(); return null }

        // Consume completed search result on game thread
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
            else -> {}
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

        if (distToGoal <= arrivalRadius) {
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
                    lastTickStatus = state
                    return null
                }
            }
            state = PathStatus.ARRIVED
            cachedPathNodes = emptyList()
            pathNodeCursor = 0
            releaseGuidedControl()
            lastTickStatus = state
            return null
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

        // Compute targetYaw toward next path node (PathCommand.resolveGuidedRotation overrides this)
        val curNode = nodes.getOrNull(minOf(pathNodeCursor, nodes.lastIndex)) ?: nodes.last()
        val ndx = curNode.x + 0.5 - player.x
        val ndz = curNode.z + 0.5 - player.z
        val targetYaw = Math.toDegrees(atan2(-ndx, ndz)).toFloat()

        // Jump pulse logic
        val ndyRaw = curNode.y - player.y
        val jumpRaw = player.onGround() && ndyRaw > 0.5 && sqrt(ndx * ndx + ndz * ndz) < 2.5
        if (jumpRaw && player.onGround()) prevJump = false
        val jumpPulse = jumpRaw && !prevJump
        prevJump = jumpRaw

        val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
        val computedFlags = if (aotvSlot >= 0) 0x1 or 0x2 else 0
        val availabilityFlags = availabilityFlagsOverride ?: computedFlags

        val flagsAtCursor = cachedKeyNodeFlags.getOrElse(pathNodeCursor) { 0 }
        val lowHeadroom   = flagsAtCursor and FLAG_LOW_HEADROOM   != 0
        val tightCorridor = flagsAtCursor and FLAG_TIGHT_CORRIDOR != 0

        val activeAction = computeActiveAction(nodes, pathNodeCursor, aotvSlot >= 0, flagsAtCursor)

        val cmd = PathCommand(
            forward = true,
            back = false,
            jump = jumpPulse,
            sneak = false,
            sprint = !lowHeadroom && !tightCorridor,
            targetYaw = targetYaw,
            targetPitch = 0f,
            status = state,
            activeAction = activeAction,
            distanceToTarget = distToGoal.toFloat(),
            forwardOnly = noStrafe || tightCorridor
        )

        // AOTV / Etherwarp fire on rotation convergence
        if (state == PathStatus.EXECUTING &&
            (cmd.activeAction == ActionType.AOTV || cmd.activeAction == ActionType.ETHERWARP)) {
            val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, cmd.targetYaw))
            val pitchErr = abs(player.xRot - cmd.targetPitch)
            val aligned = yawErr < TELEPORT_YAW_THRESHOLD &&
                (cmd.activeAction == ActionType.AOTV || pitchErr < TELEPORT_PITCH_THRESHOLD)
            val nodeKey = cachedPathNodes.getOrNull(pathNodeCursor)
                ?.let { n -> (n.x.toLong() and 0x1FFFFF) or ((n.z.toLong() and 0x1FFFFF) shl 21) }
                ?: -1L
            if (aligned && nodeKey != teleportFiredNodeKey && aotvSlot >= 0) {
                teleportFiredNodeKey = nodeKey
                fireTeleport(cmd.activeAction == ActionType.ETHERWARP, aotvSlot)
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

    private fun submitSearch(sx: Int, sy: Int, sz: Int, gx: Int, gy: Int, gz: Int, isFly: Boolean) {
        searchFuture = searchExecutor.submit {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = intArrayOf(sx, sy, sz),
                    endPoints = intArrayOf(gx, gy, gz),
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = 0.0,
                    moveOrderOffset = 0,
                    avoidMeta = intArrayOf(),
                    avoidPenalty = doubleArrayOf()
                )
                if (result != null && result.keyPath.size >= 3) {
                    searchResult = result
                } else {
                    searchFailed = true
                }
            } catch (_: Exception) {
                searchFailed = true
            }
        }
    }

    private fun startSearch() {
        cancelSearch()
        state = PathStatus.PLANNING
        val player = Minecraft.getInstance().player ?: return
        submitSearch(
            player.blockX, player.blockY, player.blockZ,
            goalX, goalY, goalZ,
            routeProfile == MovementProfile.FLY
        )
    }

    private fun startReplan(playerPos: Vec3) {
        if (state == PathStatus.REPLANNING) return
        state = PathStatus.REPLANNING
        NativePathfinderJNI.cancelSearch()
        searchFuture?.cancel(true)
        searchFuture = null
        val startPos = BlockPos.containing(playerPos)
        submitSearch(
            startPos.x, startPos.y, startPos.z,
            goalX, goalY, goalZ,
            routeProfile == MovementProfile.FLY
        )
    }

    private fun applySearchResult(result: NativePathResult) {
        val flat = result.keyPath
        val nodes = ArrayList<Vec3>(flat.size / 3)
        var i = 0
        while (i + 2 < flat.size) {
            // Store as integer block coords; PathCommand adds +0.5 for centering
            nodes.add(Vec3(flat[i].toDouble(), flat[i + 1].toDouble(), flat[i + 2].toDouble()))
            i += 3
        }
        cachedPathNodes = nodes
        cachedKeyNodeFlags = result.keyNodeFlags
        pathNodeCursor = 0
        stuckTicks = 0
        collisionTicks = 0
        state = PathStatus.EXECUTING
        val mc = Minecraft.getInstance()
        val playerPos = mc.player?.position() ?: Vec3.ZERO
        lastStuckCheckPos = playerPos
        lastCollisionCheckPos = playerPos
        ModuleDebug.log("Pathfinding", "Path built in ${result.timeMs}ms")
        DebugLog.status(mc, "Pathfinding", "Pathfinder: path found: ${nodes.size} nodes, goal ($goalX,$goalY,$goalZ), ${result.timeMs}ms, ${result.nodesExplored} explored")
    }

    private fun cancelSearch() {
        NativePathfinderJNI.cancelSearch()
        searchFuture?.cancel(true)
        searchFuture = null
        searchResult = null
        searchFailed = false
        state = PathStatus.IDLE
    }

    private fun fireTeleport(isEtherwarp: Boolean, aotvSlot: Int) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
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
            }
        }
        if (nodeFlags and FLAG_STEP_UP_NEXT != 0) return ActionType.SPRINT_JUMP
        return ActionType.WALK
    }

    private fun logFailure(reason: String) {
        val mc = Minecraft.getInstance()
        DebugLog.status(mc, "Pathfinding", "Pathfinder: path failed: $reason")
    }

    private fun releaseGuidedControl() {
        MovementManager.setLookLock(false)
        RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
    }
}
