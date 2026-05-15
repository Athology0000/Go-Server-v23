package org.phantom.api.pathfinder.jni

data class NativePathResult(
    val path: IntArray,
    val keyPath: IntArray,
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double,
    val selectedStartIndex: Int,
    val pathFlags: IntArray,
    val keyNodeFlags: IntArray,
    val keyNodeMetrics: IntArray,
    val signature: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativePathResult) return false
        return path.contentEquals(other.path) &&
            keyPath.contentEquals(other.keyPath) &&
            timeMs == other.timeMs &&
            nodesExplored == other.nodesExplored &&
            nanosecondsPerNode == other.nanosecondsPerNode &&
            selectedStartIndex == other.selectedStartIndex &&
            pathFlags.contentEquals(other.pathFlags) &&
            keyNodeFlags.contentEquals(other.keyNodeFlags) &&
            keyNodeMetrics.contentEquals(other.keyNodeMetrics) &&
            signature == other.signature
    }

    override fun hashCode(): Int {
        var result = path.contentHashCode()
        result = 31 * result + keyPath.contentHashCode()
        result = 31 * result + timeMs.hashCode()
        result = 31 * result + nodesExplored
        result = 31 * result + nanosecondsPerNode.hashCode()
        result = 31 * result + selectedStartIndex
        result = 31 * result + pathFlags.contentHashCode()
        result = 31 * result + keyNodeFlags.contentHashCode()
        result = 31 * result + keyNodeMetrics.contentHashCode()
        result = 31 * result + signature.hashCode()
        return result
    }
}
