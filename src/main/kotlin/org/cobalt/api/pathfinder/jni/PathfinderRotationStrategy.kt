package org.cobalt.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.helper.Rotation

/**
 * Pass-through rotation strategy for C++-driven pathfinding.
 * The C++ RotationController has already applied bezier easing and micro-noise.
 * This strategy returns the target angles unchanged so that RotationExecutor
 * applies GCD exactly once — no double-smoothing.
 */
object PathfinderRotationStrategy : IRotationStrategy {
    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation =
        Rotation(targetYaw, targetPitch)
}
