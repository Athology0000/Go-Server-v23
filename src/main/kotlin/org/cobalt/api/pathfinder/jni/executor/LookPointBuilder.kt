package org.cobalt.api.pathfinder.jni.executor

import net.minecraft.world.phys.Vec3
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt

// Player eye height above feet, used as the Y offset for look points.
private const val LOOK_EYE_OFFSET = 1.62
// Sideways "outward offset" applied to look points on curves so the player's aim
// leads through corners instead of cutting them.
private const val OUTWARD_OFFSET_STRENGTH = 0.6
// Window (in nodes) for curvature estimation around each candidate look point.
private const val LOOK_WINDOW = 4
// Minimum horizontal squared spacing between consecutive look points before we
// merge (replace) instead of appending — keeps the look list from clustering.
private const val MIN_LOOK_SPACING_SQ = 0.64
private const val LOOK_MIN_INTERVAL = 1.2
private const val LOOK_MAX_INTERVAL = 8.0

/**
 * Build a list of eye-level "look points" from raw path nodes. Spacing is
 * curvature-adaptive (tighter through turns) and curved sections get a small
 * outward offset so the aim leads through corners.
 */
internal fun buildLookPoints(nodes: List<Vec3>): List<Vec3> {
    val result = mutableListOf<Vec3>()
    val first = nodes[0]
    result.add(Vec3(first.x + 0.5, first.y + LOOK_EYE_OFFSET, first.z + 0.5))
    var lastPlaced = nodes[0]

    for (i in 1 until nodes.lastIndex) {
        val curr = nodes[i]
        val dist = dist3d(curr, lastPlaced)

        val prev = nodes[maxOf(0, i - LOOK_WINDOW)]
        val next = nodes[minOf(nodes.lastIndex, i + LOOK_WINDOW)]
        val v1x = curr.x - prev.x; val v1z = curr.z - prev.z
        val v2x = next.x - curr.x; val v2z = next.z - curr.z
        val m1 = sqrt(v1x * v1x + v1z * v1z)
        val m2 = sqrt(v2x * v2x + v2z * v2z)

        var curvature = 0.0
        var offsetX = 0.0
        var offsetZ = 0.0

        if (m1 > 0.05 && m2 > 0.05) {
            val dot = ((v1x * v2x + v1z * v2z) / (m1 * m2)).coerceIn(-1.0, 1.0)
            val angle = acos(dot)
            curvature = (angle / (PI / 2.5)).coerceAtMost(1.0)
            val cross = v1x * v2z - v1z * v2x
            val dir = if (cross > 0) 1.0 else -1.0
            val fwdX = v1x / m1 + v2x / m2
            val fwdZ = v1z / m1 + v2z / m2
            val fMag = sqrt(fwdX * fwdX + fwdZ * fwdZ)
            if (fMag > 0.01) {
                offsetX = -(fwdZ / fMag) * dir * curvature * OUTWARD_OFFSET_STRENGTH
                offsetZ = (fwdX / fMag) * dir * curvature * OUTWARD_OFFSET_STRENGTH
            }
        }

        val dynamicInterval = LOOK_MAX_INTERVAL - curvature * (LOOK_MAX_INTERVAL - LOOK_MIN_INTERVAL)
        if (dist < dynamicInterval) continue

        val raw = Vec3(curr.x + 0.5 + offsetX, curr.y + LOOK_EYE_OFFSET, curr.z + 0.5 + offsetZ)
        val last = result.last()
        val sdx = raw.x - last.x; val sdz = raw.z - last.z
        if (sdx * sdx + sdz * sdz >= MIN_LOOK_SPACING_SQ) {
            result.add(raw)
        } else {
            result[result.lastIndex] = raw
        }
        lastPlaced = curr
    }

    val last = nodes.last()
    result.add(Vec3(last.x + 0.5, last.y + LOOK_EYE_OFFSET, last.z + 0.5))
    return result
}

private fun dist3d(a: Vec3, b: Vec3): Double {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}
