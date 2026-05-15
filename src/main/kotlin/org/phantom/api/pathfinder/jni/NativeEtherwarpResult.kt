package org.phantom.api.pathfinder.jni

data class NativeEtherwarpResult(
    val path: IntArray,
    val angles: FloatArray,
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativeEtherwarpResult) return false
        return path.contentEquals(other.path) &&
            angles.contentEquals(other.angles) &&
            timeMs == other.timeMs &&
            nodesExplored == other.nodesExplored &&
            nanosecondsPerNode == other.nanosecondsPerNode
    }

    override fun hashCode(): Int {
        var result = path.contentHashCode()
        result = 31 * result + angles.contentHashCode()
        result = 31 * result + timeMs.hashCode()
        result = 31 * result + nodesExplored
        result = 31 * result + nanosecondsPerNode.hashCode()
        return result
    }
}
