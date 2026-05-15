package org.phantom.api.util

import kotlin.math.*

data class Vector3(val x: Double, val y: Double, val z: Double) {

    constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    companion object {
        val ZERO = Vector3(0.0, 0.0, 0.0)

        fun fromCoords(x0: Double, y0: Double, z0: Double, x1: Double, y1: Double, z1: Double) =
            Vector3(x1 - x0, y1 - y0, z1 - z0)

        fun fromPitchYaw(pitch: Double, yaw: Double): Vector3 {
            val f = cos(-yaw * 0.017453292 - Math.PI)
            val f1 = sin(-yaw * 0.017453292 - Math.PI)
            val f2 = -cos(-pitch * 0.017453292)
            val f3 = sin(-pitch * 0.017453292)
            return Vector3(f1 * f2, f3, f * f2).normalize()
        }
    }

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(factor: Double) = Vector3(x * factor, y * factor, z * factor)
    operator fun times(factor: Float) = times(factor.toDouble())
    operator fun times(factor: Int) = times(factor.toDouble())
    operator fun unaryMinus() = Vector3(-x, -y, -z)

    fun add(other: Vector3) = this + other
    fun subtract(other: Vector3) = this - other
    fun multiply(factor: Double) = this * factor

    fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z
    fun dotProduct(other: Vector3) = dot(other)

    fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
    fun crossProduct(other: Vector3) = cross(other)

    fun length() = sqrt(x * x + y * y + z * z)
    fun getLength() = length()
    fun lengthSq() = x * x + y * y + z * z

    fun normalize(): Vector3 {
        val len = length()
        return if (len == 0.0) ZERO else Vector3(x / len, y / len, z / len)
    }

    fun distanceTo(other: Vector3) = (this - other).length()
    fun distanceSqTo(other: Vector3) = (this - other).lengthSq()

    fun getAngleRad(other: Vector3) = acos((dot(other) / (length() * other.length())).coerceIn(-1.0, 1.0))
    fun getAngleDeg(other: Vector3) = Math.toDegrees(getAngleRad(other))

    fun getYaw(): Double {
        val n = normalize()
        return Math.toDegrees(-atan2(n.x, n.z))
    }

    fun getPitch(): Double {
        val n = normalize()
        return Math.toDegrees(-asin(n.y.coerceIn(-1.0, 1.0)))
    }

    fun rotate(degrees: Int, reverse: Boolean = false): Vector3 {
        val d = if (reverse) (360 - degrees) % 360 else degrees
        return when (d) {
            90 -> Vector3(z, y, -x)
            180 -> Vector3(-x, y, -z)
            270 -> Vector3(-z, y, x)
            else -> this
        }
    }

    fun getPlaneEquation(p1: Vector3, p2: Vector3, p3: Vector3): DoubleArray {
        val d1 = p2 - p1
        val d2 = p3 - p1
        val normal = d1.cross(d2)
        return doubleArrayOf(normal.x, normal.y, normal.z, -p1.dot(normal))
    }

    fun getComponents() = doubleArrayOf(x, y, z)
    fun copy() = this

    override fun toString() = "Vector3(x=$x, y=$y, z=$z)"
}
