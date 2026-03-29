package org.cobalt.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import kotlin.math.exp

/**
 * Frame-based exponential-decay rotation strategy for pathfinding.
 * Smooths the C++-provided target angles using a dt-aware lerp so that
 * the camera reaches the target quickly but never snaps, regardless of FPS.
 */
object PathfinderRotationStrategy : IRotationStrategy {

    private var smoothYaw: Float = 0f
    private var smoothPitch: Float = 0f
    private var initialized: Boolean = false
    private var lastNs: Long = 0L

    // Higher = faster convergence. 14 ≈ ~80% of the way there within 1/10 s.
    private const val SMOOTH_SPEED = 14.0

    override fun onStart() {
        initialized = false
    }

    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
        val now = System.nanoTime()
        if (!initialized) {
            smoothYaw = player.yRot
            smoothPitch = player.xRot
            initialized = true
            lastNs = now
            return Rotation(smoothYaw, smoothPitch)
        }
        val dt = ((now - lastNs).toDouble() / 1_000_000_000.0).toFloat().coerceIn(0f, 0.1f)
        lastNs = now
        val t = (1.0 - exp(-SMOOTH_SPEED * dt)).toFloat()
        smoothYaw += AngleUtils.getRotationDelta(smoothYaw, targetYaw) * t
        smoothPitch += (targetPitch - smoothPitch) * t
        return Rotation(smoothYaw, smoothPitch)
    }
}
