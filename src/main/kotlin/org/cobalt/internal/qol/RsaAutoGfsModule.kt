package org.cobalt.internal.qol

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.util.getSkyblockId

object RsaAutoGfsModule : Module("RSA Auto GFS") {

  override val category = ModuleCategory.QOL

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically refill selected items with /gfs after a world change.",
    false,
  )

  private val enderPearl = CheckboxSetting("Ender Pearl", "Refill Ender Pearls up to 16.", false).inGroup(ITEMS_GROUP)
  private val spiritLeap = CheckboxSetting("Spirit Leap", "Refill Spirit Leaps up to 16.", false).inGroup(ITEMS_GROUP)
  private val superBoom = CheckboxSetting("Super Boom", "Refill Superboom TNT up to 64.", false).inGroup(ITEMS_GROUP)
  private val getMissingItems = CheckboxSetting(
    "Get Missing Items",
    "Also request the full stack when the item is not in your inventory.",
    true,
  ).inGroup(ITEMS_GROUP)

  private val worldLoadDelay = SliderSetting(
    "World Load Delay",
    "Ticks to wait after loading a world before refilling.",
    40.0,
    20.0,
    80.0,
    1.0,
  )

  private val getItemDelay = SliderSetting(
    "Get Item Delay",
    "Ticks between /gfs refill commands.",
    40.0,
    20.0,
    80.0,
    1.0,
  )

  private var lastLevel: ClientLevel? = null
  private var loadDelayTicks = 0
  private var commandDelayTicks = 0
  private var readyAfterWorldLoad = false

  init {
    addSetting(
      enabled,
      enderPearl,
      spiritLeap,
      superBoom,
      getMissingItems,
      worldLoadDelay,
      getItemDelay,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    val mc = Minecraft.getInstance()
    val level = mc.level
    val player = mc.player

    if (level == null || player == null) {
      lastLevel = null
      readyAfterWorldLoad = false
      loadDelayTicks = 0
      commandDelayTicks = 0
      return
    }

    if (level !== lastLevel) {
      lastLevel = level
      readyAfterWorldLoad = false
      loadDelayTicks = worldLoadDelay.value.toInt()
      commandDelayTicks = 0
    }

    if (!enabled.value) return

    if (!readyAfterWorldLoad) {
      if (loadDelayTicks > 0) {
        loadDelayTicks--
        return
      }
      readyAfterWorldLoad = true
    }

    if (commandDelayTicks > 0) {
      commandDelayTicks--
      return
    }

    val sent = when {
      enderPearl.value && tryGetItem(player, "ENDER_PEARL", 16) -> true
      spiritLeap.value && tryGetItem(player, "SPIRIT_LEAP", 16) -> true
      superBoom.value && tryGetItem(player, "SUPERBOOM_TNT", 64) -> true
      else -> false
    }

    if (sent) {
      commandDelayTicks = getItemDelay.value.toInt()
    }
  }

  private fun tryGetItem(player: LocalPlayer, skyblockId: String, maxStack: Int): Boolean {
    var matchingCount = 0
    var found = false

    val inventory = player.inventory
    for (slot in 0 until inventory.containerSize) {
      val stack = inventory.getItem(slot)
      if (stack.getSkyblockId() == skyblockId) {
        found = true
        matchingCount += stack.count
      }
    }

    val missing = when {
      found -> maxStack - matchingCount
      getMissingItems.value -> maxStack
      else -> 0
    }

    if (missing <= 0) return false
    player.connection.sendCommand("gfs $skyblockId $missing")
    return true
  }

  private const val ITEMS_GROUP = "Items"
}
