package org.cobalt.api.pathfinder

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import org.cobalt.api.pathfinder.cache.CachedWorld
import org.cobalt.api.pathfinder.jni.NativePathfinderBridge
import org.cobalt.api.pathfinder.jni.NativePathResult
import org.cobalt.api.pathfinder.jni.NativeStateEncoder
import org.cobalt.api.pathfinder.jni.NativeVoxelFlags
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor
import kotlin.math.max

object PathManager {

    private const val NON_PRIMARY_START_PENALTY = 250.0
    private const val HEURISTIC_WEIGHT = 1.05
    private const val ETHERWARP_DEFAULT_MAX_ITERATIONS = 100_000
    private const val ETHERWARP_AUTO_THREAD_COUNT = 0
    private const val ETHERWARP_STANDING_EYE_HEIGHT = 1.62
    private const val ETHERWARP_LEGACY_SNEAK_OFFSET = 0.08
    private const val ETHERWARP_MODERN_SNEAK_OFFSET = 0.35

    private val MODERN_ETHERWARP_AREAS = setOf(
        "Hub", "Dwarven Mines", "Gold Mine", "The Park", "Park",
        "Spider's Den", "Spider Den", "The End", "End",
        "The Farming Islands", "The Barn", "Galatea"
    )

    const val FLAG_FLUID_FEET = 1 shl 0
    const val FLAG_FLUID_HEAD = 1 shl 1
    const val FLAG_LOW_HEADROOM = 1 shl 2
    const val FLAG_NEAR_EDGE = 1 shl 3
    const val FLAG_NEAR_WALL = 1 shl 4
    const val FLAG_STEP_UP_NEXT = 1 shl 5
    const val FLAG_DROP_NEXT = 1 shl 6
    const val FLAG_TIGHT_CORRIDOR = 1 shl 7

    private data class PathAnnotations(
        val pathFlags: IntArray,
        val keyNodeFlags: IntArray,
        val keyNodeMetrics: IntArray,
        val signatureHex: String
    )

    private data class ManagedAvoidEntry(
        val x: Int, val y: Int, val z: Int,
        val radius: Int,
        var penalty: Double,
        var ttlSearches: Int
    )

    private data class NativeAvoidZone(
        val x: Int, val y: Int, val z: Int,
        val radiusSq: Int,
        val penalty: Double,
        val maxYDiff: Int
    )

    private data class PathSnapshot(
        val points: List<BlockPos>,
        val keyNodes: List<BlockPos>,
        val isFly: Boolean,
        val timeMs: Long,
        val nodesExplored: Int,
        val nanosecondsPerNode: Double,
        val selectedStartIndex: Int
    )

    private data class EtherwarpSnapshot(
        val points: List<BlockPos>,
        val angles: FloatArray,
        val timeMs: Long,
        val nodesExplored: Int,
        val nanosecondsPerNode: Double
    )

    private data class EtherwarpLandingCandidate(
        val x: Int, val y: Int, val z: Int,
        val centerX: Double, val centerY: Double, val centerZ: Double,
        val originDistanceSq: Double,
        val anchorDistanceSq: Double
    )

    @Volatile private var currentPath: PathSnapshot? = null
    @Volatile private var currentAnnotations: PathAnnotations? = null
    @Volatile private var currentEtherwarpPath: EtherwarpSnapshot? = null
    @Volatile private var lastError: String? = null
    @Volatile private var isSearching: Boolean = false
    @Volatile private var searchVariantSeed: Int = 0

    private val avoidLock = Any()
    private val transientAvoidEntries = ArrayList<ManagedAvoidEntry>(8)
    private var currentTask: Future<*>? = null
    private val searchId = AtomicInteger(0)

    // =========================================================================
    // Public path-finding entry points (findPath / findEtherwarpPath / variants)
    // =========================================================================

    private fun resolveEtherwarpThreadCount(threadCount: Int): Int {
        val maxThreads = max(1, Runtime.getRuntime().availableProcessors())
        return if (threadCount == ETHERWARP_AUTO_THREAD_COUNT) maxThreads
        else threadCount.coerceIn(1, maxThreads)
    }

    @JvmStatic
    @JvmOverloads
    fun findPath(
        startX: Int, startY: Int, startZ: Int,
        endX: Int, endY: Int, endZ: Int,
        maxIterations: Int = 2_000_000,
        isFly: Boolean = false
    ): Boolean = findPath(
        arrayOf(intArrayOf(startX, startY, startZ)),
        arrayOf(intArrayOf(endX, endY, endZ)),
        maxIterations, isFly
    )

    @JvmStatic
    @JvmOverloads
    fun findPathMultipleGoals(
        startX: Int, startY: Int, startZ: Int,
        endGoals: IntArray,
        maxIterations: Int = 2_000_000,
        isFly: Boolean = false
    ): Boolean {
        val endPoints = unpackFlatPoints("End goals", endGoals) ?: return false
        return findPath(arrayOf(intArrayOf(startX, startY, startZ)), endPoints, maxIterations, isFly)
    }

    @JvmStatic
    @JvmOverloads
    fun findPath(
        startPoints: Array<IntArray>,
        endPoints: Array<IntArray>,
        maxIterations: Int = 2_000_000,
        isFly: Boolean = false
    ): Boolean {
        cancelSearch()
        lastError = null
        currentPath = null
        currentAnnotations = null
        currentEtherwarpPath = null

        if (maxIterations <= 0) { lastError = "maxIterations must be > 0"; return false }
        validatePoints("Start points", startPoints)?.let { lastError = it; return false }
        validatePoints("End points", endPoints)?.let { lastError = it; return false }
        if (isFly && (startPoints.size != 1 || endPoints.size != 1)) {
            lastError = "Fly pathfinder only supports one start/end point"; return false
        }
        validateHeights(startPoints, endPoints, isFly)?.let { lastError = it; return false }

        val avoidZones = consumeTransientAvoidZones()
        val (avoidMeta, avoidPenalty) = encodeAvoidZones(avoidZones)
        val currentId = searchId.incrementAndGet()
        isSearching = true
        val startFlat = flattenPoints(startPoints)
        val endFlat = flattenPoints(endPoints)

        return try {
            currentTask = CachedWorld.executor.submit {
                try {
                    val result = NativePathfinderBridge.findPath(
                        NativePathfinderBridge.NativePathSearchRequest(
                            startFlat, endFlat, isFly, maxIterations,
                            HEURISTIC_WEIGHT,
                            if (isFly) 0.0 else NON_PRIMARY_START_PENALTY,
                            if (isFly) 0 else searchVariantSeed,
                            avoidMeta, avoidPenalty
                        )
                    )
                    if (searchId.get() != currentId) return@submit
                    if (result != null && result.path.isNotEmpty()) {
                        currentPath = PathSnapshot(
                            points = toBlockPosList(result.path),
                            keyNodes = toBlockPosList(if (result.keyPath.isNotEmpty()) result.keyPath else result.path),
                            isFly = isFly,
                            timeMs = result.timeMs,
                            nodesExplored = result.nodesExplored,
                            nanosecondsPerNode = result.nanosecondsPerNode,
                            selectedStartIndex = result.selectedStartIndex
                        )
                        currentAnnotations = PathAnnotations(result.pathFlags, result.keyNodeFlags, result.keyNodeMetrics, result.signature)
                        lastError = null
                    } else {
                        currentPath = null
                        currentAnnotations = null
                        lastError = NativePathfinderBridge.getLastError() ?: "No path found to destination"
                    }
                } catch (e: InterruptedException) {
                    if (searchId.get() == currentId) lastError = "Pathfinding was cancelled"
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    if (searchId.get() == currentId) lastError = e.message ?: "Unknown error during pathfinding"
                } finally {
                    if (searchId.get() == currentId) { isSearching = false; currentTask = null }
                }
            }
            true
        } catch (e: Exception) {
            isSearching = false
            lastError = e.message ?: "Failed to submit pathfinding task"
            false
        }
    }

    @JvmStatic
    @JvmOverloads
    fun findEtherwarpPath(
        goalX: Int, goalY: Int, goalZ: Int,
        maxIterations: Int = ETHERWARP_DEFAULT_MAX_ITERATIONS,
        threadCount: Int = ETHERWARP_AUTO_THREAD_COUNT,
        yawStep: Double = 5.0,
        pitchStep: Double = 5.0,
        newNodeCost: Double = 1.0,
        heuristicWeight: Double = 1.0,
        rayLength: Double = 61.0,
        rewireEpsilon: Double = 1.0,
        eyeHeight: Double = Double.NaN
    ): Boolean {
        cancelSearch()
        lastError = null
        currentPath = null
        currentAnnotations = null
        currentEtherwarpPath = null

        if (maxIterations <= 0) { lastError = "maxIterations must be > 0"; return false }
        if (!yawStep.isFinite() || yawStep <= 0.0) { lastError = "yawStep must be > 0"; return false }
        if (!pitchStep.isFinite() || pitchStep <= 0.0) { lastError = "pitchStep must be > 0"; return false }
        if (!newNodeCost.isFinite() || newNodeCost <= 0.0) { lastError = "newNodeCost must be > 0"; return false }
        if (!heuristicWeight.isFinite() || heuristicWeight <= 0.0) { lastError = "heuristicWeight must be > 0"; return false }
        if (!rayLength.isFinite() || rayLength <= 0.0) { lastError = "rayLength must be > 0"; return false }
        if (!rewireEpsilon.isFinite() || rewireEpsilon < 0.0) { lastError = "rewireEpsilon must be >= 0"; return false }

        val resolvedEyeHeight = when {
            eyeHeight.isNaN() -> getCurrentEtherwarpEyeHeight()
            eyeHeight.isFinite() && eyeHeight > 0.0 -> eyeHeight
            else -> Double.NaN
        }
        if (!resolvedEyeHeight.isFinite() || resolvedEyeHeight <= 0.0) {
            lastError = "eyeHeight must be > 0 or NaN for auto"; return false
        }

        val client = Minecraft.getInstance()
        val level = client.level ?: run { lastError = "World is not loaded"; return false }
        val player = client.player ?: run { lastError = "Player is not loaded"; return false }

        val originX = player.x
        val originY = player.y + resolvedEyeHeight
        val originZ = player.z

        val minSupportY = level.minY
        val maxSupportY = level.maxY - 1
        if (goalY !in minSupportY..maxSupportY) {
            lastError = "Etherwarp goal Y must be between $minSupportY and $maxSupportY"
            return false
        }
        validateEtherwarpLanding("Goal block", goalX, goalY, goalZ)?.let { lastError = it; return false }

        val currentId = searchId.incrementAndGet()
        val resolvedThreadCount = resolveEtherwarpThreadCount(threadCount)
        isSearching = true

        return try {
            currentTask = CachedWorld.executor.submit {
                try {
                    val result = NativePathfinderBridge.findEtherwarpPath(
                        NativePathfinderBridge.NativeEtherwarpSearchRequest(
                            goalX, goalY, goalZ,
                            originX, originY, originZ,
                            maxIterations, resolvedThreadCount,
                            yawStep, pitchStep, newNodeCost,
                            heuristicWeight, rayLength, rewireEpsilon,
                            resolvedEyeHeight + 1.0
                        )
                    )
                    if (searchId.get() != currentId) return@submit
                    if (result != null && result.path.isNotEmpty()) {
                        currentEtherwarpPath = EtherwarpSnapshot(
                            toBlockPosList(result.path), result.angles.copyOf(),
                            result.timeMs, result.nodesExplored, result.nanosecondsPerNode
                        )
                        lastError = null
                    } else {
                        currentEtherwarpPath = null
                        lastError = NativePathfinderBridge.getLastError() ?: "No etherwarp path found"
                    }
                } catch (e: InterruptedException) {
                    if (searchId.get() == currentId) lastError = "Pathfinding was cancelled"
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    if (searchId.get() == currentId) lastError = e.message ?: "Unknown error"
                } finally {
                    if (searchId.get() == currentId) { isSearching = false; currentTask = null }
                }
            }
            true
        } catch (e: Exception) {
            isSearching = false
            lastError = e.message ?: "Failed to submit etherwarp task"
            false
        }
    }

    // =========================================================================
    // Native call argument encoding / result decoding
    // =========================================================================

    private fun flattenPoints(points: Array<IntArray>): IntArray {
        val out = IntArray(points.size * 3)
        var idx = 0
        for (p in points) { out[idx++] = p[0]; out[idx++] = p[1]; out[idx++] = p[2] }
        return out
    }

    private fun toBlockPosList(flatPath: IntArray): List<BlockPos> {
        if (flatPath.isEmpty() || flatPath.size % 3 != 0) return emptyList()
        val out = ArrayList<BlockPos>(flatPath.size / 3)
        var idx = 0
        while (idx + 2 < flatPath.size) {
            out.add(BlockPos(flatPath[idx], flatPath[idx + 1], flatPath[idx + 2]))
            idx += 3
        }
        return out
    }

    private fun encodeAvoidZones(zones: Array<NativeAvoidZone>): Pair<IntArray, DoubleArray> {
        if (zones.isEmpty()) return IntArray(0) to DoubleArray(0)
        val meta = IntArray(zones.size * 5)
        val penalties = DoubleArray(zones.size)
        var idx = 0
        for (i in zones.indices) {
            val z = zones[i]
            meta[idx++] = z.x; meta[idx++] = z.y; meta[idx++] = z.z
            meta[idx++] = z.radiusSq; meta[idx++] = z.maxYDiff
            penalties[i] = z.penalty
        }
        return meta to penalties
    }

    private fun validatePoints(label: String, points: Array<IntArray>): String? {
        if (points.isEmpty()) return "$label are required"
        if (points.any { it.size != 3 }) return "$label must contain [x, y, z] points"
        return null
    }

    private fun validateHeights(startPoints: Array<IntArray>, endPoints: Array<IntArray>, isFly: Boolean): String? {
        val level = Minecraft.getInstance().level ?: return "World is not loaded"
        val minY = if (isFly) level.minY else level.minY + 1
        val maxY = level.maxY
        if (startPoints.any { it[1] !in minY..maxY }) return "Start Y must be between $minY and $maxY"
        if (endPoints.any { it[1] !in minY..maxY }) return "End Y must be between $minY and $maxY"
        return null
    }

    private fun unpackFlatPoints(label: String, points: IntArray): Array<IntArray>? {
        if (points.isEmpty()) { lastError = "$label are required"; return null }
        if (points.size % 3 != 0) { lastError = "$label must be x,y,z triples"; return null }
        val result = Array(points.size / 3) { IntArray(3) }
        var idx = 0
        while (idx < points.size) {
            val out = idx / 3
            result[out][0] = points[idx]; result[out][1] = points[idx + 1]; result[out][2] = points[idx + 2]
            idx += 3
        }
        return result
    }

    // =========================================================================
    // Etherwarp landing validation + landing candidate enumeration
    // =========================================================================

    private fun validateEtherwarpLanding(label: String, x: Int, y: Int, z: Int): String? {
        val level = Minecraft.getInstance().level ?: return "World is not loaded"
        val supportFlags = resolveEtherwarpFlags(level, x, y, z) ?: return null
        if (!isEtherwarpStandable(supportFlags)) return "$label must be a solid etherwarp landing block"
        val standOffset = etherwarpStandOffset(supportFlags)
        val feetFlags = resolveEtherwarpFlags(level, x, y + standOffset, z) ?: return null
        val headFlags = resolveEtherwarpFlags(level, x, y + standOffset + 1, z) ?: return null
        if (!isEtherwarpTeleportSpaceClear(feetFlags)) return "$label must have passable space above it"
        if (!isEtherwarpTeleportSpaceClear(headFlags)) return "$label must have two passable blocks above it"
        return null
    }

    private fun resolveEtherwarpFlags(level: net.minecraft.client.multiplayer.ClientLevel, x: Int, y: Int, z: Int): Int? {
        val chunkX = x shr 4
        val chunkZ = z shr 4
        if (level.chunkSource.getChunk(chunkX, chunkZ, false) != null) {
            return NativeStateEncoder.flagsForState(level.getBlockState(BlockPos(x, y, z)))
        }
        return CachedWorld.getBlockFlags(x, y, z)?.toInt()?.and(0xFFFF)
    }

    private fun isEtherwarpStandable(flags: Int) = (flags and NativeVoxelFlags.SOLID) != 0
    private fun isFenceLikeEtherwarpSupport(flags: Int) = (flags and NativeVoxelFlags.FENCE_LIKE) != 0
    private fun etherwarpStandOffset(flags: Int) = if (isFenceLikeEtherwarpSupport(flags)) 2 else 1
    private fun isEtherwarpTeleportSpaceClear(flags: Int) =
        (flags and NativeVoxelFlags.ETHER_TELEPORT_CLEAR) != 0 && (flags and NativeVoxelFlags.ETHER_FEET_BLOCKER) == 0

    private fun isValidEtherwarpLanding(level: net.minecraft.client.multiplayer.ClientLevel, x: Int, y: Int, z: Int): Boolean {
        val supportFlags = resolveEtherwarpFlags(level, x, y, z) ?: return false
        if (!isEtherwarpStandable(supportFlags)) return false
        val standOffset = etherwarpStandOffset(supportFlags)
        val feetFlags = resolveEtherwarpFlags(level, x, y + standOffset, z) ?: return false
        val headFlags = resolveEtherwarpFlags(level, x, y + standOffset + 1, z) ?: return false
        return isEtherwarpTeleportSpaceClear(feetFlags) && isEtherwarpTeleportSpaceClear(headFlags)
    }

    private fun getEtherwarpLandingCenter(level: net.minecraft.client.multiplayer.ClientLevel, x: Int, y: Int, z: Int): DoubleArray {
        val supportFlags = resolveEtherwarpFlags(level, x, y, z)
        return doubleArrayOf(x + 0.5, y + (if (supportFlags != null) etherwarpStandOffset(supportFlags) else 1).toDouble(), z + 0.5)
    }

    @JvmStatic
    fun isValidEtherwarpLanding(x: Int, y: Int, z: Int): Boolean {
        val level = Minecraft.getInstance().level ?: return false
        return isValidEtherwarpLanding(level, x, y, z)
    }

    @JvmStatic
    fun getEtherwarpLandingCenter(x: Int, y: Int, z: Int): DoubleArray? {
        val level = Minecraft.getInstance().level ?: return null
        return getEtherwarpLandingCenter(level, x, y, z)
    }

    @JvmStatic
    fun getEtherwarpLandingCandidates(
        anchorX: Double, anchorY: Double, anchorZ: Double,
        radius: Int, maxDistance: Double,
        sortOriginX: Double, sortOriginY: Double, sortOriginZ: Double
    ): EtherwarpLandingCandidatesResult? {
        val level = Minecraft.getInstance().level ?: return null
        if (!anchorX.isFinite() || !anchorY.isFinite() || !anchorZ.isFinite()) return null
        if (!sortOriginX.isFinite() || !sortOriginY.isFinite() || !sortOriginZ.isFinite()) return null

        val clampedRadius = radius.coerceAtLeast(0)
        val distanceLimitSq = when {
            !maxDistance.isFinite() -> Double.POSITIVE_INFINITY
            maxDistance < 0.0 -> return EtherwarpLandingCandidatesResult(IntArray(0), DoubleArray(0))
            else -> maxDistance * maxDistance
        }

        val baseX = floor(anchorX).toInt()
        val baseY = floor(anchorY).toInt()
        val baseZ = floor(anchorZ).toInt()
        val candidates = ArrayList<EtherwarpLandingCandidate>()

        for (dx in -clampedRadius..clampedRadius) {
            for (dy in -clampedRadius..clampedRadius) {
                for (dz in -clampedRadius..clampedRadius) {
                    val goalX = baseX + dx
                    val goalY = baseY + dy
                    val goalZ = baseZ + dz
                    if (!isValidEtherwarpLanding(level, goalX, goalY, goalZ)) continue

                    val center = getEtherwarpLandingCenter(level, goalX, goalY, goalZ)
                    val dxA = center[0] - anchorX; val dyA = center[1] - anchorY; val dzA = center[2] - anchorZ
                    val anchorDistSq = dxA * dxA + dyA * dyA + dzA * dzA
                    if (anchorDistSq > distanceLimitSq) continue

                    val dxO = goalX - sortOriginX; val dyO = goalY - sortOriginY; val dzO = goalZ - sortOriginZ
                    candidates.add(EtherwarpLandingCandidate(
                        goalX, goalY, goalZ, center[0], center[1], center[2],
                        dxO * dxO + dyO * dyO + dzO * dzO, anchorDistSq
                    ))
                }
            }
        }

        candidates.sortWith(compareBy({ it.originDistanceSq }, { it.anchorDistanceSq }))

        val goals = IntArray(candidates.size * 3)
        val centers = DoubleArray(candidates.size * 3)
        var gi = 0; var ci = 0
        for (c in candidates) {
            goals[gi++] = c.x; goals[gi++] = c.y; goals[gi++] = c.z
            centers[ci++] = c.centerX; centers[ci++] = c.centerY; centers[ci++] = c.centerZ
        }
        return EtherwarpLandingCandidatesResult(goals, centers)
    }

    // =========================================================================
    // Hypixel area + per-area etherwarp eye/sneak offsets
    // =========================================================================

    @JvmStatic
    fun getCurrentEtherwarpEyeHeight(): Double = ETHERWARP_STANDING_EYE_HEIGHT - getCurrentEtherwarpSneakOffset()

    @JvmStatic
    fun getCurrentEtherwarpSneakOffset(): Double =
        if (isModernEtherwarpArea(getCurrentHypixelArea())) ETHERWARP_MODERN_SNEAK_OFFSET
        else ETHERWARP_LEGACY_SNEAK_OFFSET

    private fun getCurrentHypixelArea(): String? {
        for (name in org.cobalt.api.util.TabListUtils.rawDisplayNames()) {
            if (!name.contains("Area:")) continue
            val value = name.substringAfter("Area:", "").trim()
            if (value.isNotEmpty()) return value
        }
        return null
    }

    private fun isModernEtherwarpArea(area: String?): Boolean = area != null && area in MODERN_ETHERWARP_AREAS

    // =========================================================================
    // Transient avoid zones + accessors used by JNI / Kotlin callers
    // =========================================================================

    private fun consumeTransientAvoidZones(): Array<NativeAvoidZone> {
        synchronized(avoidLock) {
            if (transientAvoidEntries.isEmpty()) return emptyArray()
            val zones = ArrayList<NativeAvoidZone>(transientAvoidEntries.size)
            transientAvoidEntries.removeIf { it.ttlSearches <= 0 }
            for (entry in transientAvoidEntries) {
                val r = entry.radius.coerceAtLeast(1)
                zones.add(NativeAvoidZone(entry.x, entry.y, entry.z, r * r, entry.penalty.coerceAtLeast(0.0), 2))
                entry.ttlSearches--
            }
            transientAvoidEntries.removeIf { it.ttlSearches <= 0 }
            return zones.toTypedArray()
        }
    }

    @JvmStatic
    fun isSearching(): Boolean = isSearching

    @JvmStatic
    fun cancelSearch() {
        currentTask?.cancel(true)
        currentTask = null
        searchId.incrementAndGet()
        isSearching = false
        NativePathfinderBridge.cancelSearch()
    }

    @JvmStatic
    fun getPathArray(): IntArray {
        val s = currentPath ?: return IntArray(0)
        val yOff = if (s.isFly) 0 else -1
        val out = IntArray(s.points.size * 3)
        var i = 0
        for (p in s.points) { out[i++] = p.x; out[i++] = p.y + yOff; out[i++] = p.z }
        return out
    }

    @JvmStatic
    fun getKeyNodesArray(): IntArray {
        val s = currentPath ?: return IntArray(0)
        val yOff = if (s.isFly) 0 else -1
        val out = IntArray(s.keyNodes.size * 3)
        var i = 0
        for (p in s.keyNodes) { out[i++] = p.x; out[i++] = p.y + yOff; out[i++] = p.z }
        return out
    }

    @JvmStatic
    fun getEtherwarpPathArray(): IntArray {
        val s = currentEtherwarpPath ?: return IntArray(0)
        val out = IntArray(s.points.size * 3)
        var i = 0
        for (p in s.points) { out[i++] = p.x; out[i++] = p.y; out[i++] = p.z }
        return out
    }

    @JvmStatic fun getEtherwarpAnglesArray(): FloatArray = currentEtherwarpPath?.angles?.copyOf() ?: FloatArray(0)
    @JvmStatic fun getPathFlagsArray(): IntArray = currentAnnotations?.pathFlags ?: IntArray(0)
    @JvmStatic fun getKeyNodeFlagsArray(): IntArray = currentAnnotations?.keyNodeFlags ?: IntArray(0)
    @JvmStatic fun getKeyNodeMetricsArray(): IntArray = currentAnnotations?.keyNodeMetrics ?: IntArray(0)
    @JvmStatic fun getPathSignature(): String = currentAnnotations?.signatureHex ?: ""

    @JvmStatic
    @JvmOverloads
    fun addTransientAvoidPoint(x: Int, y: Int, z: Int, radius: Int = 2, penalty: Double = 36.0, ttlSearches: Int = 2) {
        val r = radius.coerceIn(1, 6)
        val p = penalty.coerceIn(5.0, 120.0)
        val ttl = ttlSearches.coerceIn(1, 8)
        synchronized(avoidLock) {
            val existing = transientAvoidEntries.find { it.x == x && it.y == y && it.z == z && it.radius == r }
            if (existing != null) {
                existing.ttlSearches = max(existing.ttlSearches, ttl)
                existing.penalty = max(existing.penalty, p)
            } else {
                transientAvoidEntries.add(ManagedAvoidEntry(x, y, z, r, p, ttl))
            }
        }
    }

    @JvmStatic
    fun clearTransientAvoidPoints() {
        synchronized(avoidLock) { transientAvoidEntries.clear() }
    }

    @JvmStatic fun setSearchVariantSeed(seed: Int) { searchVariantSeed = seed }
    @JvmStatic fun getLastError(): String? = lastError
    @JvmStatic fun hasPath(): Boolean = currentPath != null
    @JvmStatic fun hasEtherwarpPath(): Boolean = currentEtherwarpPath != null
    @JvmStatic fun getPathSize(): Int = currentPath?.points?.size ?: 0
    @JvmStatic fun getEtherwarpPathSize(): Int = currentEtherwarpPath?.points?.size ?: 0
    @JvmStatic fun getKeyNodeCount(): Int = currentPath?.keyNodes?.size ?: 0
    @JvmStatic fun getLastTimeMs(): Long = currentPath?.timeMs ?: -1L
    @JvmStatic fun getEtherwarpLastTimeMs(): Long = currentEtherwarpPath?.timeMs ?: -1L
    @JvmStatic fun getNodesExplored(): Int = currentPath?.nodesExplored ?: 0
    @JvmStatic fun getSelectedStartIndex(): Int = currentPath?.selectedStartIndex ?: -1
    @JvmStatic fun getEtherwarpVoxelFlagsAt(x: Int, y: Int, z: Int): Int {
        val level = Minecraft.getInstance().level ?: return 0
        return resolveEtherwarpFlags(level, x, y, z) ?: 0
    }
    @JvmStatic fun isEtherwarpSupportSolid(flags: Int): Boolean = isEtherwarpStandable(flags)
    @JvmStatic fun getEtherwarpStandOffsetForFlags(flags: Int): Int = etherwarpStandOffset(flags)
    @JvmStatic fun isEtherwarpTeleportSpaceClearFlags(flags: Int): Boolean = isEtherwarpTeleportSpaceClear(flags)

    @JvmStatic
    fun clear() {
        cancelSearch()
        currentPath = null
        currentAnnotations = null
        currentEtherwarpPath = null
        lastError = null
    }
}
