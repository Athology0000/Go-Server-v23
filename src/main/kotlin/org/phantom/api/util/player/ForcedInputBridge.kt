package org.phantom.api.util.player

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

/**
 * Mirrors MovementManager's forced state into actual KeyMapping#setDown states.
 *
 * Important: this bridge must only release keys that it previously forced.
 * If it calls setDown(false) while the player is physically holding WASD,
 * Minecraft sees the key as released and the player cannot move.
 */
object ForcedInputBridge {
  private var registered = false
  private var idleReleaseTicks = 0

  /** True after this bridge has written forced key states into Minecraft options. */
  private var ownsPhysicalKeys = false

  fun register() {
    if (registered) return
    registered = true

    ClientTickEvents.END_CLIENT_TICK.register {
      sync()
    }
  }

  fun sync() {
    val mc = Minecraft.getInstance() ?: return
    val options = mc.options ?: return

    // No forced movement active: do not touch normal physical keyboard input.
    // Only release once if this bridge previously forced those keys down.
    if (!MovementManager.hasForcedMovement) {
      idleReleaseTicks++
      if (ownsPhysicalKeys) {
        releasePhysicalKeys()
      }
      if (MovementManager.isMovementLocked && idleReleaseTicks >= 2) {
        MovementManager.setMovementLock(false)
      }
      return
    }

    idleReleaseTicks = 0

    // If movement is not locked, the bridge should not override player input.
    if (!MovementManager.isMovementLocked) {
      if (ownsPhysicalKeys) {
        releasePhysicalKeys()
      }
      return
    }

    ownsPhysicalKeys = true

    options.keyUp.setDown(MovementManager.forcedForward)
    options.keyDown.setDown(MovementManager.forcedBackward)
    options.keyLeft.setDown(MovementManager.forcedLeft)
    options.keyRight.setDown(MovementManager.forcedRight)
    options.keyJump.setDown(MovementManager.forcedJump)
    options.keyShift.setDown(MovementManager.forcedShift)
    options.keySprint.setDown(MovementManager.forcedSprint)
  }

  fun releasePhysicalKeys() {
    // Do not clear player input unless this bridge previously owned the keys.
    if (!ownsPhysicalKeys) return

    val mc = Minecraft.getInstance() ?: run {
      ownsPhysicalKeys = false
      return
    }
    val options = mc.options ?: run {
      ownsPhysicalKeys = false
      return
    }

    options.keyUp.setDown(false)
    options.keyDown.setDown(false)
    options.keyLeft.setDown(false)
    options.keyRight.setDown(false)
    options.keyJump.setDown(false)
    options.keySprint.setDown(false)
    options.keyShift.setDown(false)

    ownsPhysicalKeys = false
  }
}
