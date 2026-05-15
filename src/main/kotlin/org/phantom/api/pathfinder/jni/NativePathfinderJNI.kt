package org.phantom.api.pathfinder.jni

import org.phantom.pathfinder.NativeLoader

object NativePathfinderJNI {

    init {
        val path = NativeLoader.extract("natives/windows/phantom_pathfinder.dll")
        System.load(path)
    }

    @JvmStatic external fun initNative(): Boolean

    @JvmStatic external fun setWorld(worldKey: String, minY: Int, maxY: Int)
    @JvmStatic external fun clearWorld()

    /**
     * sectionMask: bit i set = section i has data in sectionFlags.
     * sectionFlags: packed uint16_t voxel flags, 4096 shorts per section,
     *   index within section = ((y & 15) shl 8) or ((z & 15) shl 4) or (x & 15).
     */
    @JvmStatic external fun upsertChunk(
        chunkX: Int, chunkZ: Int,
        minY: Int, maxY: Int,
        sectionMask: Long,
        sectionFlags: ShortArray
    )

    /** updates: flat [x, y, z, flags(uint16 as int)] quads */
    @JvmStatic external fun applyBlockUpdates(updates: IntArray)

    @JvmStatic external fun cancelSearch()

    /**
     * startPoints / endPoints: flat [x,y,z, x,y,z, ...] int triples.
     * avoidMeta: flat [x,y,z,radiusSq,maxYDiff, ...] int quintuples.
     * avoidPenalty: one double per avoid zone.
     * Returns null if no path found.
     */
    @JvmStatic external fun findPath(
        startPoints: IntArray,
        endPoints: IntArray,
        isFly: Boolean,
        maxIterations: Int,
        heuristicWeight: Double,
        nonPrimaryStartPenalty: Double,
        moveOrderOffset: Int,
        avoidMeta: IntArray,
        avoidPenalty: DoubleArray
    ): NativePathResult?

    /**
     * Returns null if no etherwarp path found.
     * angles in result: flat [yaw0, pitch0, yaw1, pitch1, ...] float pairs.
     */
    @JvmStatic external fun findEtherwarpPath(
        goalX: Int, goalY: Int, goalZ: Int,
        startEyeX: Double, startEyeY: Double, startEyeZ: Double,
        maxIterations: Int,
        threadCount: Int,
        yawStep: Double,
        pitchStep: Double,
        newNodeCost: Double,
        heuristicWeight: Double,
        rayLength: Double,
        rewireEpsilon: Double,
        eyeHeight: Double
    ): NativeEtherwarpResult?
}
