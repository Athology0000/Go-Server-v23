package org.phantom.api.pathfinder.jni

object NativePathfinderBridge {

    data class NativePathSearchRequest(
        val startPoints: IntArray,
        val endPoints: IntArray,
        val isFly: Boolean,
        val maxIterations: Int,
        val heuristicWeight: Double,
        val nonPrimaryStartPenalty: Double,
        val moveOrderOffset: Int,
        val avoidMeta: IntArray,
        val avoidPenalty: DoubleArray
    )

    data class NativeEtherwarpSearchRequest(
        val goalX: Int,
        val goalY: Int,
        val goalZ: Int,
        val startEyeX: Double,
        val startEyeY: Double,
        val startEyeZ: Double,
        val maxIterations: Int,
        val threadCount: Int,
        val yawStep: Double,
        val pitchStep: Double,
        val newNodeCost: Double,
        val heuristicWeight: Double,
        val rayLength: Double,
        val rewireEpsilon: Double,
        val eyeHeight: Double
    )

    @Volatile private var lastError: String? = null

    @JvmStatic
    fun isAvailable(): Boolean = true

    @JvmStatic
    fun getLastError(): String? = lastError

    @JvmStatic
    fun setWorld(worldKey: String, minY: Int, maxY: Int) {
        try {
            NativePathfinderJNI.setWorld(worldKey, minY, maxY)
            lastError = null
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
        }
    }

    @JvmStatic
    fun clearWorld() {
        try {
            NativePathfinderJNI.clearWorld()
            lastError = null
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
        }
    }

    @JvmStatic
    fun upsertChunk(chunkX: Int, chunkZ: Int, minY: Int, maxY: Int, sectionMask: Long, sectionFlags: ShortArray) {
        try {
            NativePathfinderJNI.upsertChunk(chunkX, chunkZ, minY, maxY, sectionMask, sectionFlags)
            lastError = null
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
        }
    }

    @JvmStatic
    fun applyBlockUpdates(updates: IntArray) {
        if (updates.isEmpty()) return
        try {
            NativePathfinderJNI.applyBlockUpdates(updates)
            lastError = null
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
        }
    }

    @JvmStatic
    fun findPath(request: NativePathSearchRequest): NativePathResult? {
        return try {
            val result = NativePathfinderJNI.findPath(
                request.startPoints, request.endPoints, request.isFly,
                request.maxIterations, request.heuristicWeight,
                request.nonPrimaryStartPenalty, request.moveOrderOffset,
                request.avoidMeta, request.avoidPenalty
            )
            if (result == null) lastError = "Native pathfinder returned no path"
            else lastError = null
            result
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
            null
        }
    }

    @JvmStatic
    fun findEtherwarpPath(request: NativeEtherwarpSearchRequest): NativeEtherwarpResult? {
        return try {
            val result = NativePathfinderJNI.findEtherwarpPath(
                request.goalX, request.goalY, request.goalZ,
                request.startEyeX, request.startEyeY, request.startEyeZ,
                request.maxIterations, request.threadCount,
                request.yawStep, request.pitchStep, request.newNodeCost,
                request.heuristicWeight, request.rayLength, request.rewireEpsilon,
                request.eyeHeight
            )
            if (result == null) lastError = "Native etherwarp pathfinder returned no path"
            else lastError = null
            result
        } catch (t: Throwable) {
            lastError = t.message ?: t.javaClass.simpleName
            null
        }
    }

    @JvmStatic
    fun cancelSearch() {
        try {
            NativePathfinderJNI.cancelSearch()
        } catch (_: Throwable) {}
    }
}
