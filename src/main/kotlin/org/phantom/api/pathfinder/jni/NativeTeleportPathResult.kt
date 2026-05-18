package org.phantom.api.pathfinder.jni

/**
 * Result of the native teleport-first hybrid planner.
 *
 * [path] is a flat [x,y,z,...] int triple list, one node per hop landing.
 * [hopTypes] is parallel to the node list: 0 = WALK, 1 = AOTV, 2 = ETHERWARP.
 * [yaw]/[pitch] are the cast angles for each node (only meaningful for teleport
 * nodes). Constructed from JNI; ctor signature is `([I[I[F[FZJID)V`.
 */
data class NativeTeleportPathResult(
    val path: IntArray,
    val hopTypes: IntArray,
    val yaw: FloatArray,
    val pitch: FloatArray,
    val reachedGoal: Boolean,
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativeTeleportPathResult) return false
        return path.contentEquals(other.path) &&
            hopTypes.contentEquals(other.hopTypes) &&
            yaw.contentEquals(other.yaw) &&
            pitch.contentEquals(other.pitch) &&
            reachedGoal == other.reachedGoal &&
            timeMs == other.timeMs &&
            nodesExplored == other.nodesExplored &&
            nanosecondsPerNode == other.nanosecondsPerNode
    }

    override fun hashCode(): Int {
        var result = path.contentHashCode()
        result = 31 * result + hopTypes.contentHashCode()
        result = 31 * result + yaw.contentHashCode()
        result = 31 * result + pitch.contentHashCode()
        result = 31 * result + reachedGoal.hashCode()
        result = 31 * result + timeMs.hashCode()
        result = 31 * result + nodesExplored
        result = 31 * result + nanosecondsPerNode.hashCode()
        return result
    }
}
