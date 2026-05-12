package org.cobalt.api.pathfinder.jni.executor

import net.minecraft.world.phys.Vec3

internal data class SplineProjection(
    val distance: Double,
    val point: Vec3,
    val distSq: Double,
)

internal class SplinePath(
    val controlPoints: List<Vec3>,
    val samples: List<Vec3>,
    val cumulative: DoubleArray,
) {
    val totalLength: Double = cumulative.lastOrNull() ?: 0.0

    fun sample(distance: Double): Vec3 {
        if (samples.isEmpty()) return Vec3.ZERO
        if (samples.size == 1 || totalLength <= 0.0) return samples.first()
        val d = distance.coerceIn(0.0, totalLength)
        val rawIndex = cumulative.binarySearch(d)
        val upper = if (rawIndex >= 0) rawIndex else (-rawIndex - 1).coerceIn(1, cumulative.lastIndex)
        val lower = (upper - 1).coerceAtLeast(0)
        val span = cumulative[upper] - cumulative[lower]
        if (span <= 1e-6) return samples[upper]
        val t = (d - cumulative[lower]) / span
        return lerp(samples[lower], samples[upper], t)
    }

    fun project(
        point: Vec3,
        hintDistance: Double,
        searchBack: Double,
        searchForward: Double,
        horizontalOnly: Boolean,
    ): SplineProjection {
        if (samples.size < 2) return SplineProjection(0.0, samples.firstOrNull() ?: Vec3.ZERO, Double.POSITIVE_INFINITY)
        val startDistance = (hintDistance - searchBack).coerceAtLeast(0.0)
        val endDistance = (hintDistance + searchForward).coerceAtMost(totalLength)
        var bestDistance = hintDistance.coerceIn(0.0, totalLength)
        var bestPoint = sample(bestDistance)
        var bestDistSq = Double.POSITIVE_INFINITY

        // Binary-search the first segment whose end >= startDistance, then iterate
        // forward only until segStart exceeds endDistance. Was O(N) over every
        // spline sample per project() call — at typical paths (300+ samples,
        // multiple project() calls per tick) the linear skip-loop dominated the
        // tick cost. cumulative[] is sorted by construction so this is O(log N + window).
        val rawIdx = cumulative.binarySearch(startDistance)
        val firstSeg = ((if (rawIdx >= 0) rawIdx else -rawIdx - 1) - 1).coerceAtLeast(0)

        for (i in firstSeg until samples.lastIndex) {
            val segStart = cumulative[i]
            if (segStart > endDistance) break
            val segEnd = cumulative[i + 1]

            val a = samples[i]
            val b = samples[i + 1]
            val frac = if (horizontalOnly) closestHorizontalFraction(point, a, b) else closestFraction(point, a, b)
            val projected = lerp(a, b, frac)
            val distance = (segStart + (segEnd - segStart) * frac).coerceIn(startDistance, endDistance)
            val distSq = if (horizontalOnly) {
                val dx = point.x - projected.x
                val dz = point.z - projected.z
                dx * dx + dz * dz
            } else {
                point.distanceToSqr(projected)
            }

            if (distSq < bestDistSq) {
                bestDistSq = distSq
                bestDistance = distance
                bestPoint = projected
            }
        }

        return SplineProjection(bestDistance, bestPoint, bestDistSq)
    }

    fun distanceAtControlIndex(indexFloat: Double): Double {
        if (controlPoints.isEmpty() || samples.isEmpty()) return indexFloat
        val control = sampleControl(indexFloat)
        return project(
            point = control,
            hintDistance = indexFloat.coerceAtLeast(0.0),
            searchBack = 12.0,
            searchForward = 18.0,
            horizontalOnly = false,
        ).distance
    }

    private fun sampleControl(indexFloat: Double): Vec3 {
        val safe = indexFloat.coerceIn(0.0, controlPoints.lastIndex.toDouble())
        val index = safe.toInt().coerceIn(0, controlPoints.lastIndex)
        val frac = safe - index
        val a = controlPoints[index]
        val b = controlPoints[minOf(index + 1, controlPoints.lastIndex)]
        return lerp(a, b, frac)
    }
}

private const val SPLINE_STEPS = 10

internal fun buildSplinePath(points: List<Vec3>): SplinePath {
    if (points.size < 2) return SplinePath(points, points, DoubleArray(points.size) { 0.0 })

    val samples = ArrayList<Vec3>((points.size - 1) * SPLINE_STEPS + 1)
    for (i in 0 until points.lastIndex) {
        val p0 = points[(i - 1).coerceAtLeast(0)]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[(i + 2).coerceAtMost(points.lastIndex)]
        for (step in 0 until SPLINE_STEPS) {
            samples.add(catmullRom(p0, p1, p2, p3, step.toDouble() / SPLINE_STEPS))
        }
    }
    samples.add(points.last())

    val cumulative = DoubleArray(samples.size)
    for (i in 1 until samples.size) {
        cumulative[i] = cumulative[i - 1] + samples[i - 1].distanceTo(samples[i])
    }
    return SplinePath(points, samples, cumulative)
}

private fun catmullRom(p0: Vec3, p1: Vec3, p2: Vec3, p3: Vec3, t: Double): Vec3 {
    val t2 = t * t
    val t3 = t2 * t
    return Vec3(
        0.5 * (2 * p1.x + (-p0.x + p2.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 + (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3),
        0.5 * (2 * p1.y + (-p0.y + p2.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 + (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3),
        0.5 * (2 * p1.z + (-p0.z + p2.z) * t + (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 + (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3),
    )
}

internal fun lerp(a: Vec3, b: Vec3, t: Double): Vec3 =
    Vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t)

internal fun closestFraction(p: Vec3, a: Vec3, b: Vec3): Double {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dz = b.z - a.z
    val lenSq = dx * dx + dy * dy + dz * dz
    if (lenSq <= 1e-8) return 0.0
    return (((p.x - a.x) * dx + (p.y - a.y) * dy + (p.z - a.z) * dz) / lenSq).coerceIn(0.0, 1.0)
}

internal fun closestHorizontalFraction(p: Vec3, a: Vec3, b: Vec3): Double {
    val dx = b.x - a.x
    val dz = b.z - a.z
    val lenSq = dx * dx + dz * dz
    if (lenSq <= 1e-8) return 0.0
    return (((p.x - a.x) * dx + (p.z - a.z) * dz) / lenSq).coerceIn(0.0, 1.0)
}
