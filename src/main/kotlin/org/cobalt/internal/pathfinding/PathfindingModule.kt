package org.cobalt.internal.pathfinding

import java.util.Locale
import net.minecraft.client.Minecraft
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import net.minecraft.world.InteractionHand
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.pathfinder.jni.ActionType
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.player.MovementManager
import org.cobalt.api.util.ui.NVGRenderer

object PathfindingModule : Module("Pathfinding") {

  private val mc: Minecraft = Minecraft.getInstance()
  private var moduleOwnsPath = false

  val enabled = CheckboxSetting(
    "Enabled",
    "Enable pathfinding target selection and commands.",
    false
  )

  private val info = InfoSetting(
    "Target",
    "Use /cobalt setpos or /cobalt setposhere to set the target.",
    InfoType.INFO
  )

  val targetX = TextSetting("Target X", "Target X coordinate.", "0")
  val targetY = TextSetting("Target Y", "Target Y coordinate.", "0")
  val targetZ = TextSetting("Target Z", "Target Z coordinate.", "0")
  val targetBlock = TextSetting("Target Block", "Filled from right-click (informational).", "")

  val cacheHudEnabled = CheckboxSetting(
    "Cache HUD",
    "Show cached chunk map HUD.",
    false
  )

  val cacheHudRadius = SliderSetting(
    "Cache Radius",
    "Chunk radius shown in the cache HUD.",
    4.0,
    1.0,
    12.0
  )

  val cacheHudCellSize = SliderSetting(
    "Cache Cell Size",
    "Cell size for the cache HUD (pixels).",
    8.0,
    4.0,
    16.0
  )

  val cacheHudShowGrid = CheckboxSetting(
    "Cache Grid",
    "Show grid lines on the cache HUD.",
    true
  )

  private val debugFileLogging = CheckboxSetting(
    "Debug File Logs",
    "Write path/rotation debug logs to file.",
    false
  )

  private val aotvSlot = ModeSetting(
    "AOTV Slot",
    "Hotbar slot (1-9) holding your AOTV item.",
    0,
    arrayOf("1","2","3","4","5","6","7","8","9")
  )

  private val startAction = ActionSetting(
    "Start",
    "Start pathfinding to the target coordinates.",
    "Start"
  ) {
    startFromSettings()
  }

  private val stopAction = ActionSetting(
    "Stop",
    "Stop the current path.",
    "Stop"
  ) {
    stopPath()
  }

  val cacheHud = hudElement(
    "path-cache-hud",
    "Cache Map",
    "Shows cached chunks around you."
  ) {
    anchor = HudAnchor.TOP_RIGHT
    offsetX = -12f
    offsetY = 12f

    width {
      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      (radius * 2 + 1) * cell + 8f
    }
    height {
      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      (radius * 2 + 1) * cell + 8f
    }

    render { screenX, screenY, _ ->
      if (!cacheHudEnabled.value) return@render
      val player = mc.player ?: return@render
      val level = mc.level ?: return@render

      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      val size = radius * 2 + 1
      val mapW = size * cell
      val mapH = size * cell

      NVGRenderer.rect(screenX, screenY, mapW + 8f, mapH + 8f, ThemeManager.currentTheme.panel, 6f)

      val originX = screenX + 4f
      val originY = screenY + 4f
      val centerChunkX = player.blockX shr 4
      val centerChunkZ = player.blockZ shr 4

      for (dz in -radius..radius) {
        for (dx in -radius..radius) {
          val chunkX = centerChunkX + dx
          val chunkZ = centerChunkZ + dz
          val cached = MinecraftPathingRules.isChunkCached(level, chunkX, chunkZ)
          val color =
            when {
              dx == 0 && dz == 0 -> ThemeManager.currentTheme.accentSecondary
              cached -> ThemeManager.currentTheme.accent
              else -> ThemeManager.currentTheme.overlay
            }
          val x = originX + (dx + radius) * cell
          val y = originY + (dz + radius) * cell
          NVGRenderer.rect(x, y, cell - 1f, cell - 1f, color)
        }
      }

      if (cacheHudShowGrid.value) {
        NVGRenderer.hollowRect(
          originX - 0.5f,
          originY - 0.5f,
          mapW + 1f,
          mapH + 1f,
          1f,
          ThemeManager.currentTheme.moduleDivider,
          4f
        )
      }
    }
  }

  init {
    addSetting(
      enabled,
      info,
      targetX,
      targetY,
      targetZ,
      targetBlock,
      cacheHudEnabled,
      cacheHudRadius,
      cacheHudCellSize,
      cacheHudShowGrid,
      debugFileLogging,
      aotvSlot,
      startAction,
      stopAction,
    )

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRightClick(event: MouseEvent.RightClick) {
    // No-op: target is set via commands.
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    org.cobalt.internal.pathfinding.DebugLog.debugFileEnabled = debugFileLogging.value
    // PathfindingModule only drives NativePathfinder when the user explicitly
    // started a path via the UI (startFromSettings / startTo). Macros tick it themselves.
    if (!enabled.value || !moduleOwnsPath) return
    val cmd = NativePathfinder.tick()
    if (cmd != null) {
      cmd.applyToPlayer()
      if (cmd.activeAction == ActionType.AOTV) {
        val player = mc.player
        if (player != null) {
          val prevSlot = player.inventory.selectedSlot
          player.inventory.selectedSlot = aotvSlot.value
          mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
          player.inventory.selectedSlot = prevSlot
        }
      }
    } else {
      moduleOwnsPath = false
      MovementManager.setMovementLock(false)
    }
  }

  @SubscribeEvent
  fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    // Path overlay rendering handled by NativePathfinder's C++ state machine
  }

  fun ensureEnabledForAutomation(source: String) {
    if (enabled.value) return
    enabled.value = true
    ChatUtils.sendMessage("Pathfinding auto-enabled for $source.")
  }

  fun startFromSettings() {
    if (!enabled.value) {
      ensureEnabledForAutomation("pathfinding")
    }

    val x = parseCoordinate(targetX.value) ?: return invalidTarget("X", targetX.value)
    val y = parseCoordinate(targetY.value) ?: return invalidTarget("Y", targetY.value)
    val z = parseCoordinate(targetZ.value) ?: return invalidTarget("Z", targetZ.value)

    NativePathfinder.setTarget(x, y, z)
    moduleOwnsPath = true
  }

  fun setTargetOnly(x: Double, y: Double, z: Double) {
    setTarget(x, y, z, null)
    ChatUtils.sendMessage("Target set to $x, $y, $z.")
  }

  fun startTo(x: Double, y: Double, z: Double) {
    setTarget(x, y, z, null)
    if (!enabled.value) {
      ensureEnabledForAutomation("pathfinding")
    }
    NativePathfinder.setTarget(x, y, z)
    moduleOwnsPath = true
  }

  fun stopPath() {
    NativePathfinder.stop()
    moduleOwnsPath = false
    MovementManager.setMovementLock(false)
  }

  private fun nativeActive(): Boolean {
    val s = NativePathfinder.status
    return s != PathStatus.IDLE &&
           s != PathStatus.ARRIVED &&
           s != PathStatus.FAILED
  }

  fun setTargetAtPlayer() {
    val player = mc.player ?: return
    setTarget(player.x, player.y, player.z, "player")
    ChatUtils.sendMessage("Target set to your position.")
  }

  private fun setTarget(x: Double, y: Double, z: Double, blockName: String?) {
    targetX.value = formatCoord(x)
    targetY.value = formatCoord(y)
    targetZ.value = formatCoord(z)
    if (blockName != null) {
      targetBlock.value = blockName
    }
  }

  private fun formatCoord(value: Double): String {
    val intVal = value.toInt()
    return if (value == intVal.toDouble()) {
      intVal.toString()
    } else {
      String.format(Locale.US, "%.3f", value)
    }
  }

  private fun parseCoordinate(value: String): Double? {
    return value.trim().toDoubleOrNull()
  }

  private fun invalidTarget(axis: String, value: String) {
    ChatUtils.sendMessage("Invalid $axis coordinate: \"$value\"")
  }
}
