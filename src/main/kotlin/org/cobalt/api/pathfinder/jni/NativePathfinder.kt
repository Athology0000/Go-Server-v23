package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.TickScheduler
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.DebugLog
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

object NativePathfinder {

    val isInitialized: Boolean get() = true

    /** Cached key path nodes (integer block positions as Vec3). Updated on EXECUTING transition. */
    var cachedPathNodes: List<Vec3> = emptyList()
        private set

    /**
     * Forward-only cursor into [cachedPathNodes].
     * Written by PathCommand.nearestNodeIndex() during applyToPlayer(); never reset backward.
     */
    var pathNodeCursor: Int = 0
        internal set

    var availabilityFlagsOverride: Int? = null

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

    // Async search state (written from search thread, read on game thread)
    @Volatile private var searchResult: NativePathResult? = null
    @Volatile private var searchFailed: Boolean = false
    private var searchThread: Thread? = null

    private var prevJump: Boolean = false
    private var teleportFiredNodeKey: Long = -1L
    private var teleportRestoreSlot: Int = -1

    private var stuckTicks: Int = 0
    private var lastStuckCheckPos: Vec3 = Vec3.ZERO

    private const val STUCK_CHECK_INTERVAL = 60
    private const val STUCK_THRESHOLD = 0.5
    private const val TELEPORT_YAW_THRESHOLD = 8f
    private const val TELEPORT_PITCH_THRESHOLD = 8f
    private const val MAX_ITERATIONS = 500_000
    private const val HEURISTIC_WEIGHT = 1.05

    fun init() {
        // NativePathfinderJNI object init loads the DLL on first access
        NativePathfinderJNI.initNative()
    }

    fun destroy() {
        cancelSearch()
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

    fun setRouteWithRadius(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile, radius: Double) {
        routeLoop = loop
        routeProfile = profile
        routeArrivalRadius = radius
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
            arrivalRadius = radius
            startSearch()
        }
    }

    fun stop() {
        cancelSearch()
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        routeWpIndex = 0
        stuckTicks = 0
        releaseGuidedControl()
    }

    fun onLevelChange() {
        cancelSearch()
        ChunkSerializer.invalidate()
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        routeWpIndex = 0
        stuckTicks = 0
        releaseGuidedControl()
    }

    val status: PathStatus get() = state

    fun tick(): PathCommand? {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: run { releaseGuidedControl(); return null }

        // Consume completed search result on game thread
        if (searchFailed) {
            searchFailed = false
            searchThread = null
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
                searchThread = null
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

        val cmd = PathCommand(
            forward = true,
            back = false,
            jump = jumpPulse,
            sneak = false,
            sprint = true,
            targetYaw = targetYaw,
            targetPitch = 0f,
            status = state,
            activeAction = ActionType.WALK,
            distanceToTarget = distToGoal.toFloat()
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
            DebugLog.debug(mc, "NativePathfinder", "executing path, ${nodes.size} key nodes")
        }
        lastTickStatus = state
        return cmd
    }

    private fun startSearch() {
        cancelSearch()
        state = PathStatus.PLANNING
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val sx = player.blockX; val sy = player.blockY; val sz = player.blockZ
        val gx = goalX; val gy = goalY; val gz = goalZ
        val isFly = routeProfile == MovementProfile.FLY

        searchThread = Thread {
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
        }.also { it.isDaemon = true; it.name = "cobalt-pathfinder"; it.start() }
    }

    private fun startReplan(playerPos: Vec3) {
        if (state == PathStatus.REPLANNING) return
        state = PathStatus.REPLANNING
        NativePathfinderJNI.cancelSearch()
        searchThread = null
        val gx = goalX; val gy = goalY; val gz = goalZ
        val sx = playerPos.x.toInt(); val sy = playerPos.y.toInt(); val sz = playerPos.z.toInt()
        val isFly = routeProfile == MovementProfile.FLY

        searchThread = Thread {
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
        }.also { it.isDaemon = true; it.name = "cobalt-pathfinder-replan"; it.start() }
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
        pathNodeCursor = 0
        stuckTicks = 0
        lastStuckCheckPos = Vec3.ZERO
        state = PathStatus.EXECUTING
    }

    private fun cancelSearch() {
        NativePathfinderJNI.cancelSearch()
        searchThread = null
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

    private fun logFailure(reason: String) {
        val mc = Minecraft.getInstance()
        DebugLog.debug(mc, "NativePathfinder", "path failed: $reason")
    }

    private fun releaseGuidedControl() {
        MovementManager.setLookLock(false)
        RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
    }
}
