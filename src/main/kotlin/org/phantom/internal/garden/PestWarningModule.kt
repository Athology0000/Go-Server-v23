package org.phantom.internal.garden

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.notification.NotificationManager
import org.phantom.internal.garden.managers.PestManager
import org.phantom.internal.garden.managers.PestTabListParser
import org.phantom.mixin.client.TabOverlayAccessor

object PestWarningModule : Module("Pest Warning") {

  override val category = ModuleCategory.FARMING

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Warn while farming in the Garden once pest count reaches the configured threshold.",
    false,
  )

  private val thresholdSetting = SliderSetting(
    "Threshold",
    "Minimum alive pest count required before warning.",
    4.0,
    1.0,
    12.0,
    step = 1.0,
  )

  private val warningDelaySecondsSetting = SliderSetting(
    "Warning Delay",
    "Minimum number of seconds between warnings.",
    10.0,
    0.0,
    120.0,
    step = 1.0,
  )

  private val notificationSetting = CheckboxSetting(
    "Notification",
    "Show a Phantom notification when the warning triggers.",
    true,
  )

  private val actionBarSetting = CheckboxSetting(
    "Action Bar",
    "Show the pest warning in the action bar.",
    true,
  )

  private val soundSetting = CheckboxSetting(
    "Sound",
    "Play a warning sound when the pest threshold is reached.",
    true,
  )

  private val ignoreCleaningCooldownSetting = CheckboxSetting(
    "Ignore Cleaning CD",
    "Suppress warnings while the internal pest cleaning cooldown is active.",
    true,
  )

  private var lastWarningAtMs = 0L

  init {
    addSetting(
      enabledSetting,
      thresholdSetting,
      warningDelaySecondsSetting,
      notificationSetting,
      actionBarSetting,
      soundSetting,
      ignoreCleaningCooldownSetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabledSetting.value) return

    val player = mc.player ?: return
    val level = mc.level ?: return

    if (!isInGarden()) {
      lastWarningAtMs = 0L
      return
    }

    if (!mc.options.keyAttack.isDown) {
      return
    }

    val hit = mc.hitResult as? BlockHitResult ?: return
    if (level.getBlockState(hit.blockPos).block !in cropBlocks) return

    val pestData = PestTabListParser.parse()
    val pests = pestData.alivePests
    if (pests < thresholdSetting.value.toInt()) return

    val now = System.currentTimeMillis()
    if (ignoreCleaningCooldownSetting.value && now < PestManager.cleaningCooldownUntil) return

    val delayMs = (warningDelaySecondsSetting.value * 1000.0).toLong().coerceAtLeast(0L)
    if (now - lastWarningAtMs < delayMs) return

    lastWarningAtMs = now
    val title = "$pests pests!"
    val description = "Kill them to get your Farming Fortune back."

    if (notificationSetting.value) {
      NotificationManager.queue("Garden", "$pests pests active. Clear them to restore Farming Fortune.", 3500L)
    }

    if (actionBarSetting.value) {
      player.displayClientMessage(Component.literal(title), true)
    }

    if (soundSetting.value) {
      level.playLocalSound(
        player.x,
        player.y,
        player.z,
        SoundEvents.NOTE_BLOCK_PLING.value(),
        SoundSource.PLAYERS,
        1.0f,
        1.1f,
        false,
      )
    }
  }

  private fun isInGarden(): Boolean {
    val overlay = mc.gui.tabList as? TabOverlayAccessor ?: return false
    val header = overlay.header?.string?.let(::stripFormatting).orEmpty()
    val footer = overlay.footer?.string?.let(::stripFormatting).orEmpty()
    return header.contains("Garden", ignoreCase = true) || footer.contains("Garden", ignoreCase = true)
  }

  private fun stripFormatting(text: String): String {
    return text.replace(Regex("\u00A7[0-9A-FK-ORa-fk-or]"), "")
  }

  private val cropBlocks: Set<Block> = setOf(
    Blocks.WHEAT,
    Blocks.CARROTS,
    Blocks.POTATOES,
    Blocks.BEETROOTS,
    Blocks.NETHER_WART,
    Blocks.SUGAR_CANE,
    Blocks.CACTUS,
    Blocks.MELON,
    Blocks.PUMPKIN,
    Blocks.CARVED_PUMPKIN,
    Blocks.COCOA,
    Blocks.RED_MUSHROOM,
    Blocks.BROWN_MUSHROOM,
  )
}
