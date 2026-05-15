package org.phantom.internal.fishing

import net.minecraft.client.player.LocalPlayer
import org.phantom.api.rotation.IRotationStrategy
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.util.helper.Rotation

object FishingRotationLock {

  private val holdStrategy = HoldRotationStrategy()

  var lockedYaw: Float = 0f
    private set
  var lockedPitch: Float = 0f
    private set
  var isLocked: Boolean = false
    private set

  fun lock(yaw: Float, pitch: Float) {
    lockedYaw = yaw
    lockedPitch = pitch
    isLocked = true
  }

  fun unlock() {
    isLocked = false
    RotationExecutor.stopIfUsing(holdStrategy)
  }

  fun applyLock() {
    if (!isLocked) return
    RotationExecutor.rotateTo(Rotation(lockedYaw, lockedPitch), holdStrategy)
  }

  private class HoldRotationStrategy : IRotationStrategy {
    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
      return Rotation(targetYaw, targetPitch)
    }
  }
}
