package org.cobalt.internal.mining

import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.rotation.EasingType
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.TimedEaseStrategy
import org.cobalt.api.util.helper.Rotation
import org.cobalt.internal.pathfinding.OverlayRenderEngine
import org.cobalt.internal.visual.DeployableHudModule

object AutoLanternModule : Module("Auto Lantern") {

  private val mc: Minecraft = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically use a lantern when the lantern buff is gone or you moved too far.",
    false
  )

  private val info = InfoSetting(
    "Auto Lantern",
    "Uses deployable lantern status first, with tab list fallback, then places a lantern from your hotbar when needed.",
    InfoType.INFO
  )

  private val requireMacro = CheckboxSetting(
    "Require Macro",
    "Only activate when Mining Macro is enabled.",
    true
  )

  private val cooldownTicks = SliderSetting(
    "Cooldown",
    "Minimum ticks to wait between lantern uses.",
    80.0,
    20.0,
    600.0
  )

  private val reUseDistance = SliderSetting(
    "Reuse Distance",
    "Distance from last use spot before reusing regardless of buff status.",
    14.0,
    4.0,
    48.0
  )

  init {
    addSetting(enabled, info, requireMacro, cooldownTicks, reUseDistance)
    EventBus.register(this)
  }

  private val LANTERN_NAMES = setOf(
    "mithril lantern",
    "titanium lantern",
    "glacite lantern",
    "will o wisp"
  )

  private var lastUseTick = -1L
  private var lastUsePos: BlockPos? = null
  private var lanternBuffActive = false

  private var pendingUseRelease = false
  private var pendingRestoreSlot = -1
  private var pendingLanternSlot = -1
  private var lookDownTicks = 0
  private val lanternLookDownStrategy = TimedEaseStrategy(EasingType.LINEAR, EasingType.LINEAR, 300L)

  fun isLanternBuffActive(): Boolean = lanternBuffActive

  fun getLanternBuffStatus(): String =
    DeployableHudModule.getLanternStatusLabel()
      ?: if (lanternBuffActive) "Active" else "Inactive"

  fun noteLanternUse(levelTick: Long, pos: BlockPos) {
    lastUseTick = levelTick
    lastUsePos = pos.immutable()
    lanternBuffActive = true
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (pendingUseRelease) {
      mc.options.keyUse?.setDown(false)
      pendingUseRelease = false
    }
    if (pendingRestoreSlot >= 0) {
      mc.options.keyHotbarSlots[pendingRestoreSlot].setDown(true)
      mc.options.keyHotbarSlots[pendingRestoreSlot].setDown(false)
      pendingRestoreSlot = -1
    }

    // Handle look-down -> use lantern sequence.
    if (pendingLanternSlot >= 0) {
      val pendingPlayer = mc.player
      val pendingLevel = mc.level
      if (pendingPlayer == null || pendingLevel == null) {
        pendingLanternSlot = -1
        lookDownTicks = 0
        RotationExecutor.stopRotating()
      } else if (lookDownTicks > 0) {
        lookDownTicks--
        val behindYaw = pendingPlayer.yRot + 180f
        RotationExecutor.rotateTo(Rotation(behindYaw, 80f), lanternLookDownStrategy)
      } else {
        val slot = pendingLanternSlot
        pendingLanternSlot = -1
        RotationExecutor.stopRotating()
        useLantern(pendingPlayer, pendingLevel, slot)
      }
      return
    }

    val player = mc.player
    if (player == null) {
      lanternBuffActive = false
      return
    }
    lanternBuffActive = resolveLanternBuffState(player)

    if (!enabled.value) {
      return
    }

    val level = mc.level ?: return
    if (mc.screen != null) return
    if (requireMacro.value && !MiningMacroModule.isActive) return
    if (lanternBuffActive) return
    if (level.gameTime - lastUseTick < cooldownTicks.value.toLong()) return

    val slot = findLanternSlot(player)
    if (slot !in 0..8) return

    // Look down before placing so the lantern lands at the player's feet.
    pendingLanternSlot = slot
    lookDownTicks = 4
  }

  private fun resolveLanternBuffState(player: Player): Boolean {
    if (DeployableHudModule.isLanternActive()) {
      return true
    }
    if (isNearLastPlacement(player)) {
      return true
    }
    return hasLanternBuffInTabList()
  }

  private fun isNearLastPlacement(player: Player): Boolean {
    val last = lastUsePos ?: return false
    val ref = Vec3(last.x + 0.5, player.y, last.z + 0.5)
    return player.position().distanceTo(ref) <= reUseDistance.value
  }

  private fun hasLanternBuffInTabList(): Boolean {
    val connection = mc.connection ?: return false
    return try {
      resolveTabEntries(connection).any { entry ->
        val raw = resolveEntryDisplayName(entry) ?: return@any false
        val stripped = ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT) ?: return@any false
        stripped.contains("mining speed") && !stripped.contains("speed boost")
      }
    } catch (_: Exception) {
      false
    }
  }

  private fun resolveTabEntries(connection: Any): List<Any> {
    val methodNames = listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")
    for (name in methodNames) {
      val method =
        connection.javaClass.methods.firstOrNull { m ->
          m.name == name && m.parameterCount == 0
        } ?: continue
      val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
      when (result) {
        is Collection<*> -> return result.filterNotNull()
        is Iterable<*> -> return result.filterNotNull()
      }
    }
    return emptyList()
  }

  private fun resolveEntryDisplayName(entry: Any): String? {
    val displayMethodNames = listOf("getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName")
    for (name in displayMethodNames) {
      val method =
        entry.javaClass.methods.firstOrNull { m ->
          m.name == name && m.parameterCount == 0
        } ?: continue
      val value = runCatching { method.invoke(entry) }.getOrNull()
      val text = coerceText(value)
      if (!text.isNullOrBlank()) {
        return text
      }
    }

    val profileMethodNames = listOf("getProfile", "getGameProfile", "profile")
    for (name in profileMethodNames) {
      val method =
        entry.javaClass.methods.firstOrNull { m ->
          m.name == name && m.parameterCount == 0
        } ?: continue
      val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
      val nameMethod =
        profile.javaClass.methods.firstOrNull { m ->
          m.name == "getName" && m.parameterCount == 0
        } ?: continue
      val profileName = runCatching { nameMethod.invoke(profile) as? String }.getOrNull()
      if (!profileName.isNullOrBlank()) {
        return profileName
      }
    }
    return null
  }

  private fun coerceText(value: Any?): String? {
    if (value == null) return null
    if (value is String) return value
    val textMethod =
      value.javaClass.methods.firstOrNull { m ->
        m.name == "getString" && m.parameterCount == 0
      }
    if (textMethod != null) {
      val raw = runCatching { textMethod.invoke(value) }.getOrNull()
      if (raw is String) {
        return raw
      }
    }
    return value.toString()
  }

  private fun findLanternSlot(player: Player): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue
      val name = normalizeName(stack.hoverName.string)
      if (LANTERN_NAMES.any { name.contains(it) }) return i
    }
    return -1
  }

  private fun normalizeName(raw: String): String =
    (ChatFormatting.stripFormatting(raw) ?: raw).lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]+"), " ").trim()

  private fun useLantern(player: Player, level: net.minecraft.world.level.Level, slot: Int) {
    val previousSlot = player.inventory.selectedSlot

    if (previousSlot != slot) {
      mc.options.keyHotbarSlots[slot].setDown(true)
      mc.options.keyHotbarSlots[slot].setDown(false)
    }

    mc.options.keyUse?.setDown(true)

    pendingUseRelease = true
    pendingRestoreSlot = if (previousSlot != slot) previousSlot else -1

    noteLanternUse(level.gameTime, player.blockPosition())
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    if (!lanternBuffActive) return
    val pos = lastUsePos ?: return
    val level = mc.level ?: return
    val player = mc.player ?: return
    if (!isNearLastPlacement(player)) return

    val cx = pos.x + 0.5
    val cy = pos.y + 0.5
    val cz = pos.z + 0.5
    val radius = 15.0
    val segments = 48

    val now = System.currentTimeMillis()
    // Pulse: alpha oscillates between 80 and 200 over 2 s
    val pulse = (sin(now / 1000.0 * Math.PI) * 0.5 + 0.5).toFloat()
    val baseAlpha = (80 + (pulse * 120).toInt()).coerceIn(0, 255)
    // Rotating bright arc: 1/6 of the circle, one full spin every 3 s
    val rotAngle = (now % 3000L).toDouble() / 3000.0 * Math.PI * 2.0
    val arcHalfSpan = Math.PI / 6.0  // +/-30 deg around the highlight centre

    var prevX = cx + cos(0.0) * radius
    var prevZ = cz + sin(0.0) * radius
    for (i in 1..segments) {
      val angle = (i.toDouble() / segments) * Math.PI * 2.0
      val nextX = cx + cos(angle) * radius
      val nextZ = cz + sin(angle) * radius

      val segMid = ((i - 0.5) / segments) * Math.PI * 2.0
      val diff = abs(((segMid - rotAngle + Math.PI * 3.0) % (Math.PI * 2.0)) - Math.PI)
      val inArc = diff < arcHalfSpan
      val alpha = if (inArc) 255 else baseAlpha
      val r = if (inArc) 160 else 80
      val g = if (inArc) 235 else 200
      val width = if (inArc) 2.5f else 1.5f
      OverlayRenderEngine.addLine(
        level, prevX, cy, prevZ, nextX, cy, nextZ,
        OverlayRenderEngine.Color(r, g, 255, alpha), width, 2, "lantern-radius", true
      )
      prevX = nextX
      prevZ = nextZ
    }

    OverlayRenderEngine.render(event.context)
  }
}
