package org.phantom.api.util.player

object MovementManager {

  @JvmField
  @Volatile
  var isLookLocked = false

  @JvmField
  @Volatile
  var isMovementLocked = false

  @JvmField
  @Volatile
  var hasForcedMovement = false

  @JvmField
  @Volatile
  var forcedForward = false

  @JvmField
  @Volatile
  var forcedBackward = false

  @JvmField
  @Volatile
  var forcedLeft = false

  @JvmField
  @Volatile
  var forcedRight = false

  @JvmField
  @Volatile
  var forcedJump = false

  @JvmField
  @Volatile
  var forcedShift = false

  @JvmField
  @Volatile
  var forcedSprint = false

  @JvmField
  @Volatile
  var forcedAttack = false

  @JvmField
  @Volatile
  var forcedUse = false

  @JvmField
  @Volatile
  var forcedActionsEnabled = false

  @Volatile
  var movementOwner: MovementOwner = MovementOwner.NONE
    private set

  @Volatile
  var lookOwner: MovementOwner = MovementOwner.NONE
    private set

  @JvmStatic
  fun canTakeMovement(owner: MovementOwner): Boolean {
    val current = movementOwner
    return current == MovementOwner.NONE ||
      current == owner ||
      owner.priority >= current.priority
  }

  @JvmStatic
  fun canTakeLook(owner: MovementOwner): Boolean {
    val current = lookOwner
    return current == MovementOwner.NONE ||
      current == owner ||
      owner.priority >= current.priority
  }

  @JvmStatic
  fun requestMovement(owner: MovementOwner): Boolean {
    if (!canTakeMovement(owner)) return false
    movementOwner = owner
    isMovementLocked = true
    return true
  }

  @JvmStatic
  fun requestLook(owner: MovementOwner): Boolean {
    if (!canTakeLook(owner)) return false
    lookOwner = owner
    isLookLocked = true
    return true
  }

  @JvmStatic
  fun releaseMovement(owner: MovementOwner) {
    if (owner != MovementOwner.PATHFINDER && owner != MovementOwner.FAILSAFE && movementOwner != owner) return
    movementOwner = MovementOwner.NONE
    isMovementLocked = false
    clearForcedMovement()
  }

  @JvmStatic
  fun releaseLook(owner: MovementOwner) {
    if (owner != MovementOwner.PATHFINDER && owner != MovementOwner.FAILSAFE && lookOwner != owner) return
    lookOwner = MovementOwner.NONE
    isLookLocked = false
  }

  @JvmStatic
  fun setLookLock(state: Boolean = true) {
    isLookLocked = state
  }

  @JvmStatic
  fun setMovementLock(state: Boolean = true) {
    isMovementLocked = state
    if (!state) {
      clearForcedMovement()
    }
  }

  @JvmStatic
  fun setForcedMovement(
    forward: Boolean,
    backward: Boolean,
    @Suppress("UNUSED_PARAMETER")
    left: Boolean,
    @Suppress("UNUSED_PARAMETER")
    right: Boolean,
    jump: Boolean,
    shift: Boolean,
    sprint: Boolean
  ) {
    movementOwner = MovementOwner.PATHFINDER
    isMovementLocked = true

    forcedForward = forward
    forcedBackward = backward
    forcedLeft = left
    forcedRight = right
    forcedJump = jump
    forcedShift = shift
    forcedSprint = sprint

    hasForcedMovement = true
  }

  @JvmStatic
  fun setForcedMovement(
    owner: MovementOwner,
    forward: Boolean,
    backward: Boolean,
    left: Boolean,
    right: Boolean,
    jump: Boolean,
    shift: Boolean,
    sprint: Boolean
  ): Boolean {
    if (!requestMovement(owner)) return false

    forcedForward = forward
    forcedBackward = backward
    forcedLeft = left
    forcedRight = right
    forcedJump = jump
    forcedShift = shift
    forcedSprint = sprint

    hasForcedMovement = true
    return true
  }

  @JvmStatic
  fun setForcedActions(
    owner: MovementOwner,
    attack: Boolean = false,
    use: Boolean = false
  ): Boolean {
    if (!requestMovement(owner)) return false

    forcedActionsEnabled = true
    forcedAttack = attack
    forcedUse = use

    return true
  }

  @JvmStatic
  fun clearForcedMovement() {
    ForcedInputBridge.releasePhysicalKeys()
    hasForcedMovement = false
    forcedForward = false
    forcedBackward = false
    forcedLeft = false
    forcedRight = false
    forcedJump = false
    forcedShift = false
    forcedSprint = false
    forcedAttack = false
    forcedUse = false
    forcedActionsEnabled = false
  }

  @JvmStatic
  fun clearForcedMovement(owner: MovementOwner) {
    if (owner != MovementOwner.PATHFINDER && owner != MovementOwner.FAILSAFE && movementOwner != owner) return
    clearForcedMovement()
  }

  @JvmStatic
  fun clearAll(owner: MovementOwner) {
    releaseMovement(owner)
    releaseLook(owner)
  }

  @JvmStatic
  fun forceClearAll() {
    movementOwner = MovementOwner.NONE
    lookOwner = MovementOwner.NONE
    isMovementLocked = false
    isLookLocked = false
    clearForcedMovement()
  }

  @JvmStatic
  fun currentMovementOwnerName(): String = movementOwner.name

  @JvmStatic
  fun currentLookOwnerName(): String = lookOwner.name

  @JvmStatic
  fun forceRecoveryJump(): Boolean = setForcedMovement(
    owner = MovementOwner.RECOVERY,
    forward = true, backward = false, left = false, right = false,
    jump = true, shift = false, sprint = false
  )

  @JvmStatic
  fun forceRecoveryBackup(): Boolean = setForcedMovement(
    owner = MovementOwner.RECOVERY,
    forward = false, backward = true, left = false, right = false,
    jump = false, shift = false, sprint = false
  )

  @JvmStatic
  fun forceRecoveryStrafeLeft(): Boolean = setForcedMovement(
    owner = MovementOwner.RECOVERY,
    forward = false, backward = false, left = true, right = false,
    jump = false, shift = false, sprint = false
  )

  @JvmStatic
  fun forceRecoveryStrafeRight(): Boolean = setForcedMovement(
    owner = MovementOwner.RECOVERY,
    forward = false, backward = false, left = false, right = true,
    jump = false, shift = false, sprint = false
  )
}
