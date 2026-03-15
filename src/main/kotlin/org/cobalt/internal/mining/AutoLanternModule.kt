package org.cobalt.internal.mining

import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting

object AutoLanternModule : Module("Auto Lantern") {

  private val mc: Minecraft = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically use a lantern when the Mining Speed Boost buff is gone or you moved too far.",
    false
  )

  private val info = InfoSetting(
    "Auto Lantern",
    "Scans the tab list for Mining Speed Boost. Uses a lantern from your hotbar when the buff is missing.",
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

  private val buffStatus = TextSetting(
    "Buff Active",
    "Whether Mining Speed Boost is detected in the tab list.",
    "Unknown"
  )

  init {
    addSetting(enabled, info, requireMacro, cooldownTicks, reUseDistance, buffStatus)
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

  private var pendingUseRelease = false
  private var pendingRestoreSlot = -1

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    // Release use key and restore slot from previous use
    if (pendingUseRelease) {
      mc.options.keyUse?.setDown(false)
      pendingUseRelease = false
    }
    if (pendingRestoreSlot >= 0) {
      mc.options.keyHotbarSlots[pendingRestoreSlot].setDown(true)
      mc.options.keyHotbarSlots[pendingRestoreSlot].setDown(false)
      pendingRestoreSlot = -1
    }
    if (!enabled.value) {
      buffStatus.value = "Unknown"
      return
    }

    val player = mc.player ?: return
    val level = mc.level ?: return

    if (mc.screen != null) return
    if (requireMacro.value && !MiningMacroModule.isActive) return

    val nearLastPlacement: Boolean = run {
      val last = lastUsePos ?: return@run false
      val ref = Vec3(last.x + 0.5, player.y, last.z + 0.5)
      player.position().distanceTo(ref) <= reUseDistance.value
    }

    if (nearLastPlacement) {
      buffStatus.value = "Active (nearby)"
      return
    }

    if (level.gameTime - lastUseTick < cooldownTicks.value.toLong()) return

    if (hasLanternBuff()) {
      buffStatus.value = "Active (tab)"
      return
    }

    buffStatus.value = "Inactive"

    val slot = findLanternSlot(player)
    if (slot !in 0..8) return

    useLantern(player, level, slot)
  }

  private fun hasLanternBuff(): Boolean {
    val connection = mc.connection ?: return false
    return try {
      resolveTabEntries(connection).any { entry ->
        val raw = resolveEntryDisplayName(entry) ?: return@any false
        val stripped = ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT) ?: return@any false
        stripped.contains("mining speed")
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
    raw.lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]+"), " ").trim()

  private fun useLantern(player: Player, level: net.minecraft.world.level.Level, slot: Int) {
    val previousSlot = player.inventory.selectedSlot

    // Switch to lantern slot via hotbar keypress
    if (previousSlot != slot) {
      mc.options.keyHotbarSlots[slot].setDown(true)
      mc.options.keyHotbarSlots[slot].setDown(false)
    }

    // Trigger use via keypress — handleKeybinds() processes this in the same tick
    mc.options.keyUse?.setDown(true)

    // Schedule key release and slot restore for next tick
    pendingUseRelease = true
    pendingRestoreSlot = if (previousSlot != slot) previousSlot else -1

    lastUseTick = level.gameTime
    lastUsePos = player.blockPosition()
  }
}
