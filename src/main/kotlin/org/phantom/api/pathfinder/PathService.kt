package org.phantom.api.pathfinder

import net.minecraft.client.Minecraft
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.player.MovementManager
import org.phantom.api.util.player.MovementOwner

object PathService {

  private val mc: Minecraft
    get() = Minecraft.getInstance()

  private var currentRequest: PathRequest? = null
  private var currentOwner: PathOwner = PathOwner.NONE
  private var requestTicks: Int = 0
  private var lastStatus: PathStatus = PathStatus.IDLE

  val owner: PathOwner
    get() = currentOwner

  val activeRequest: PathRequest?
    get() = currentRequest

  val isActive: Boolean
    get() = currentRequest != null &&
      NativePathfinder.status != PathStatus.IDLE &&
      NativePathfinder.status != PathStatus.ARRIVED &&
      NativePathfinder.status != PathStatus.FAILED

  fun requestPath(request: PathRequest): Boolean {
    val existing = currentRequest

    if (existing != null && currentOwner != PathOwner.NONE) {
      if (request.owner.priority < currentOwner.priority) {
        return false
      }

      failCurrent(PathFailReason.OWNER_PREEMPTED, silent = true)
    }

    val player = mc.player
    val level = mc.level

    if (player == null) {
      request.onFail(PathFailReason.NO_PLAYER)
      return false
    }

    if (level == null) {
      request.onFail(PathFailReason.NO_WORLD)
      return false
    }

    currentRequest = request
    currentOwner = request.owner
    requestTicks = 0
    lastStatus = PathStatus.IDLE

    NativePathfinder.setTargetWithRadius(
      request.x,
      request.y,
      request.z,
      request.arrivalRadius
    )

    return true
  }

  fun tick(): Boolean {
    val request = currentRequest ?: return false

    requestTicks++

    if (requestTicks > request.timeoutTicks) {
      failCurrent(PathFailReason.TIMEOUT)
      return true
    }

    val cmd = NativePathfinder.tick()

    if (cmd != null) {
      cmd.applyToPlayer()
    } else {
      val status = NativePathfinder.status

      if (
        status == PathStatus.PLANNING ||
        status == PathStatus.REPLANNING
      ) {
        MovementManager.clearForcedMovement()
      }

      if (status == PathStatus.ARRIVED) {
        arriveCurrent()
        return true
      }

      if (status == PathStatus.FAILED) {
        failCurrent(PathFailReason.PATHFINDER_FAILED)
        return true
      }

      if (status == PathStatus.IDLE && lastStatus != PathStatus.IDLE) {
        failCurrent(PathFailReason.UNKNOWN)
        return true
      }

      lastStatus = status
    }

    return true
  }

  fun cancel(owner: PathOwner, reason: PathFailReason = PathFailReason.USER_CANCELLED) {
    if (currentOwner != owner && owner != PathOwner.FAILSAFE) return
    failCurrent(reason)
  }

  fun forceCancel(reason: PathFailReason = PathFailReason.USER_CANCELLED) {
    failCurrent(reason)
  }

  fun clearAfterWorldChange() {
    NativePathfinder.onLevelChange()
    val req = currentRequest
    currentRequest = null
    currentOwner = PathOwner.NONE
    requestTicks = 0
    lastStatus = PathStatus.IDLE
    MovementManager.forceClearAll()
    req?.onFail(PathFailReason.WORLD_CHANGED)
  }

  private fun arriveCurrent() {
    val request = currentRequest ?: return

    currentRequest = null
    currentOwner = PathOwner.NONE
    requestTicks = 0
    lastStatus = PathStatus.ARRIVED

    MovementManager.setMovementLock(false)
    MovementManager.setLookLock(false)

    request.onArrive()
  }

  private fun failCurrent(reason: PathFailReason, silent: Boolean = false) {
    val request = currentRequest

    NativePathfinder.stop()
    currentRequest = null
    currentOwner = PathOwner.NONE
    requestTicks = 0
    lastStatus = PathStatus.FAILED

    MovementManager.setMovementLock(false)
    MovementManager.setLookLock(false)

    if (request != null) {
      if (!silent) {
        ChatUtils.sendMessage("Path failed: $reason")
      }
      request.onFail(reason)
    }
  }
}