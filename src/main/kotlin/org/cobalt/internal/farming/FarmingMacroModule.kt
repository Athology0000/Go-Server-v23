package org.cobalt.internal.farming

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.player.MovementManager

object FarmingMacroModule : Module("Farming Macro") {

  private val mc = Minecraft.getInstance()
  private val laneKeyOptions = arrayOf("A", "D", "S")

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Lock camera, hold movement, and farm in alternating lanes.",
    false
  )

  private val infoSetting = InfoSetting(
    "Macro",
    "Simple lane-based farming macro with camera lock and automatic lane switching.",
    InfoType.INFO
  )

  private val yawSetting = SliderSetting(
    "Yaw",
    "Locked camera yaw.",
    0.0,
    -180.0,
    180.0,
    step = 1.0
  )

  private val pitchSetting = SliderSetting(
    "Pitch",
    "Locked camera pitch.",
    70.0,
    -90.0,
    90.0,
    step = 1.0
  )

  private val captureRotationSetting = ActionSetting(
    "Capture Rotation",
    "Copy your current yaw and pitch into the lock settings.",
    "Capture"
  ) {
    val player = mc.player ?: return@ActionSetting
    yawSetting.value = normalizeYaw(player.yRot.toDouble())
    pitchSetting.value = player.xRot.toDouble().coerceIn(-89.0, 89.0)
  }

  private val laneKeyOneSetting = ModeSetting(
    "Lane Key 1",
    "First lane key. Defaults to A because W+S cancels forward movement.",
    0,
    laneKeyOptions
  )

  private val laneKeyTwoSetting = ModeSetting(
    "Lane Key 2",
    "Second lane key. Defaults to D. You can switch this to S if your setup needs it.",
    1,
    laneKeyOptions
  )

  private val collisionTicksSetting = SliderSetting(
    "Collision Ticks",
    "Ticks with no progress in the active lane direction before swapping lanes.",
    4.0,
    1.0,
    20.0,
    step = 1.0
  )

  private val switchCooldownTicksSetting = SliderSetting(
    "Switch Cooldown",
    "Ticks to wait after a lane swap before another swap is allowed.",
    10.0,
    1.0,
    30.0,
    step = 1.0
  )

  private val minTravelPerTickSetting = SliderSetting(
    "Min Travel",
    "Minimum travel in the active lane direction per tick before it counts as blocked.",
    0.02,
    0.0,
    0.20
  )

  private val sprintSetting = CheckboxSetting(
    "Sprint",
    "Hold sprint while farming.",
    false
  )

  private val autoSelectToolSetting = CheckboxSetting(
    "Auto Select Tool",
    "Select the first hotbar item whose name contains the tool text.",
    true
  )

  private val toolNameSetting = TextSetting(
    "Tool Name",
    "Hotbar item name fragment to select before farming.",
    "hoe"
  )

  private val pauseInScreensSetting = CheckboxSetting(
    "Pause In Screens",
    "Release movement and attack while a screen is open.",
    true
  )

  private var wasEnabled = false
  private var activeLaneIndex = 0
  private var stuckTicks = 0
  private var switchCooldownTicks = 0
  private var activeTicks = 0
  private var lastX = 0.0
  private var lastZ = 0.0
  private var hasLastPos = false

  init {
    addSetting(
      enabledSetting,
      infoSetting,
      yawSetting,
      pitchSetting,
      captureRotationSetting,
      laneKeyOneSetting,
      laneKeyTwoSetting,
      collisionTicksSetting,
      switchCooldownTicksSetting,
      minTravelPerTickSetting,
      sprintSetting,
      autoSelectToolSetting,
      toolNameSetting,
      pauseInScreensSetting,
    )

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabledSetting.value) {
      if (wasEnabled) {
        stopMacro()
      }
      wasEnabled = false
      return
    }

    val player = mc.player
    if (player == null) {
      stopMacro()
      wasEnabled = false
      return
    }

    if (!wasEnabled) {
      startMacro(player)
    }
    wasEnabled = true

    if (pauseInScreensSetting.value && mc.screen != null) {
      releaseMovementAndAttack()
      return
    }

    activeTicks++
    MovementManager.setLookLock(true)
    lockRotation(player)
    maybeSelectTool()
    updateLaneState(player)
    applyMovementAndAttack()
  }

  private fun startMacro(player: LocalPlayer) {
    activeLaneIndex = 0
    stuckTicks = 0
    switchCooldownTicks = 0
    activeTicks = 0
    hasLastPos = true
    lastX = player.x
    lastZ = player.z
    MovementManager.setLookLock(true)
    releaseMovementAndAttack()
  }

  private fun stopMacro() {
    enabledSetting.value = false
    wasEnabled = false
    stuckTicks = 0
    switchCooldownTicks = 0
    activeTicks = 0
    hasLastPos = false
    MovementManager.setLookLock(false)
    releaseMovementAndAttack()
  }

  private fun lockRotation(player: LocalPlayer) {
    val yaw = normalizeYaw(yawSetting.value).toFloat()
    val pitch = pitchSetting.value.toFloat().coerceIn(-89f, 89f)

    player.setYRot(yaw)
    player.setXRot(pitch)
    player.yHeadRot = yaw
    player.yBodyRot = yaw
  }

  private fun maybeSelectTool() {
    if (!autoSelectToolSetting.value) return

    val toolName = toolNameSetting.value.trim()
    if (toolName.isEmpty()) return

    val slot = InventoryUtils.findItemInHotbar(toolName)
    if (slot in 0..8) {
      InventoryUtils.holdHotbarSlot(slot)
    }
  }

  private fun updateLaneState(player: LocalPlayer) {
    val collisionLimit = collisionTicksSetting.value.toInt().coerceAtLeast(1)
    val minTravel = minTravelPerTickSetting.value.coerceAtLeast(0.0)
    val laneKey = currentLaneKey()

    var blockedNow = player.horizontalCollision
    if (hasLastPos) {
      val dx = player.x - lastX
      val dz = player.z - lastZ
      val laneProgress = movementAlongLane(dx, dz, laneKey, yawSetting.value)
      if (activeTicks > 5 && laneProgress <= minTravel && player.onGround()) {
        blockedNow = true
      }
    }

    lastX = player.x
    lastZ = player.z
    hasLastPos = true

    if (switchCooldownTicks > 0) {
      switchCooldownTicks--
      if (!blockedNow) {
        stuckTicks = 0
      }
      return
    }

    stuckTicks = if (blockedNow) stuckTicks + 1 else 0
    if (stuckTicks >= collisionLimit) {
      activeLaneIndex = 1 - activeLaneIndex
      stuckTicks = 0
      switchCooldownTicks = switchCooldownTicksSetting.value.toInt().coerceAtLeast(1)
    }
  }

  private fun applyMovementAndAttack() {
    val laneKey = currentLaneKey()
    mc.options.keyUp.setDown(true)
    mc.options.keyLeft.setDown(laneKey == LaneKey.LEFT)
    mc.options.keyRight.setDown(laneKey == LaneKey.RIGHT)
    mc.options.keyDown.setDown(laneKey == LaneKey.BACK)
    mc.options.keySprint.setDown(sprintSetting.value)
    mc.options.keyAttack.setDown(true)
  }

  private fun releaseMovementAndAttack() {
    mc.options.keyUp.setDown(false)
    mc.options.keyLeft.setDown(false)
    mc.options.keyRight.setDown(false)
    mc.options.keyDown.setDown(false)
    mc.options.keySprint.setDown(false)
    mc.options.keyAttack.setDown(false)
  }

  private fun currentLaneKey(): LaneKey {
    val setting = if (activeLaneIndex == 0) laneKeyOneSetting else laneKeyTwoSetting
    return laneKeyFromSetting(setting.value)
  }

  private fun laneKeyFromSetting(value: Int): LaneKey {
    return when (value.coerceIn(0, laneKeyOptions.lastIndex)) {
      0 -> LaneKey.LEFT
      1 -> LaneKey.RIGHT
      2 -> LaneKey.BACK
      else -> LaneKey.LEFT
    }
  }

  private fun normalizeYaw(yaw: Double): Double {
    var result = yaw
    while (result > 180.0) result -= 360.0
    while (result <= -180.0) result += 360.0
    return if (abs(result) < 1.0e-6) 0.0 else result
  }

  private fun movementAlongLane(dx: Double, dz: Double, laneKey: LaneKey, yawDegrees: Double): Double {
    val yawRad = Math.toRadians(normalizeYaw(yawDegrees))
    val forwardX = -sin(yawRad)
    val forwardZ = cos(yawRad)
    val rightX = forwardZ
    val rightZ = -forwardX

    val laneVector = when (laneKey) {
      LaneKey.LEFT -> Pair(-rightX, -rightZ)
      LaneKey.RIGHT -> Pair(rightX, rightZ)
      LaneKey.BACK -> Pair(-forwardX, -forwardZ)
    }

    return dx * laneVector.first + dz * laneVector.second
  }

  private enum class LaneKey {
    LEFT,
    RIGHT,
    BACK,
  }
}
