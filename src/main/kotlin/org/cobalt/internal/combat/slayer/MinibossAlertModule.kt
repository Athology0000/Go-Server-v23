package org.cobalt.internal.combat.slayer

import java.util.UUID
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.util.ChatUtils

object MinibossAlertModule : Module("Miniboss Alert") {

  override val category = ModuleCategory.COMBAT

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting("Enabled", "Alert when a nearby slayer miniboss spawns.", false)
  private val detection = ModeSetting("Detection", "How minibosses are detected.", 2, arrayOf("Chat", "Entity Scan", "Both"))
  private val sendMessage = CheckboxSetting("Send Message", "Send a Cobalt chat message and notification.", true)
  private val showTitle = CheckboxSetting("Show Title", "Show the alert as a title.", true)
  private val maxDistance = SliderSetting("Maximum Distance", "Maximum entity scan distance in blocks.", 10.0, 1.0, 20.0, 1.0)
  private val alertText = TextSetting("Alert Text", "Text used for normal miniboss alerts.", "Miniboss spawned!")
  private val bigText = TextSetting("Big Boi Text", "Text used for high-tier miniboss alerts.", "Big boi spawned!")

  private val alertedEntities = linkedSetOf<UUID>()

  init {
    addSetting(enabled, detection, sendMessage, showTitle, maxDistance, alertText, bigText)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value || detection.value == 1) return
    val stripped = ChatFormatting.stripFormatting(event.message ?: "")?.trim().orEmpty()
    val match = CHAT_PATTERN.find(stripped) ?: return
    val name = match.groupValues[1]
    alert(if (isBigMiniboss(name)) bigText.value else alertText.value)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value || detection.value == 0) return
    val level = mc.level ?: return
    val player = mc.player ?: return

    alertedEntities.removeIf { uuid ->
      level.getEntity(uuid) == null
    }

    val radius = maxDistance.value
    for (entity in level.entitiesForRendering().filterIsInstance<LivingEntity>()) {
      if (entity.uuid == player.uuid || entity.uuid in alertedEntities) continue
      if (!entity.isAlive || entity.distanceTo(player) > radius || entity.tickCount > 40) continue
      val name = entity.customName?.string ?: entity.name.string
      if (!isKnownMiniboss(name)) continue
      alertedEntities += entity.uuid
      alert(if (isBigMiniboss(name)) bigText.value else alertText.value)
    }
  }

  private fun alert(rawText: String) {
    val text = rawText.ifBlank { "Miniboss spawned!" }
    if (showTitle.value) {
      mc.gui.setSubtitle(Component.empty())
      mc.gui.setTitle(Component.literal(text).withStyle(ChatFormatting.AQUA))
    }
    if (sendMessage.value) {
      ChatUtils.sendMessage(text)
      NotificationManager.queue("Slayer Miniboss", text, 2500L)
    }
  }

  private fun isKnownMiniboss(name: String): Boolean {
    val clean = ChatFormatting.stripFormatting(name)?.lowercase().orEmpty()
    return MINIBOSS_NAMES.any { clean.contains(it) }
  }

  private fun isBigMiniboss(name: String): Boolean {
    val clean = ChatFormatting.stripFormatting(name)?.lowercase().orEmpty()
    return BIG_MINIBOSS_NAMES.any { clean.contains(it) }
  }

  private val CHAT_PATTERN = Regex("""^SLAYER MINI-BOSS (.+?) has spawned!$""")

  private val MINIBOSS_NAMES = setOf(
    "revenant sycophant",
    "revenant champion",
    "deformed revenant",
    "atoned champion",
    "atoned revenant",
    "sven follower",
    "sven alpha",
    "pack enforcer",
    "tarantula vermin",
    "tarantula beast",
    "mutant tarantula",
    "voidling devot",
    "voidling radical",
    "voidcrazed maniac",
    "flaming spider",
    "mutated blaze",
    "kindleheart demon",
    "bloodfiend",
    "clotgoyle",
    "blood ichor",
  )

  private val BIG_MINIBOSS_NAMES = setOf(
    "deformed revenant",
    "atoned revenant",
    "sven alpha",
    "mutant tarantula",
    "voidcrazed maniac",
    "kindleheart demon",
    "blood ichor",
  )
}
