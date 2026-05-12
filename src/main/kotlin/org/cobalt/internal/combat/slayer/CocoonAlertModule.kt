package org.cobalt.internal.combat.slayer

import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.ui.NVGRenderer

object CocoonAlertModule : Module("Cocoon Alert") {

  override val category = ModuleCategory.COMBAT

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting("Enabled", "Alert when you cocoon your slayer boss.", false)
  private val showAlert = CheckboxSetting("Show Alert", "Show chat, title, and notification alerts.", true)
  private val alertMessage = TextSetting("Alert Message", "Text to show when the boss is cocooned.", "Boss cocooned!")
  private val showTimer = CheckboxSetting("Show Timer", "Show a 6 second cocoon timer HUD.", true)

  private var timerEndMs = 0L

  val cocoonHud = hudElement("cocoon-timer", "Cocoon Timer", "Shows active cocoon time.") {
    anchor = HudAnchor.CENTER
    offsetY = 58f
    scale = 1f

    width { currentTimerText()?.let { NVGRenderer.textWidth(it, 16f) + 24f } ?: 122f }
    height { 30f }

    render { x, y, _ ->
      val text = currentTimerText() ?: return@render
      val w = NVGRenderer.textWidth(text, 16f) + 24f
      val theme = ThemeManager.currentTheme
      NVGRenderer.rect(x, y, w, 30f, theme.overlay, 5f)
      NVGRenderer.text(text, x + 12f, y + 7f, 16f, 0xFFFF7777.toInt())
    }
  }

  init {
    addSetting(enabled, showAlert, alertMessage, showTimer)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val message = ChatFormatting.stripFormatting(event.message ?: "")?.trim().orEmpty()
    if (!message.equals("YOU COCOONED YOUR SLAYER BOSS", ignoreCase = true)) return

    if (showTimer.value) timerEndMs = System.currentTimeMillis() + 6_000L
    if (showAlert.value) {
      val text = alertMessage.value.ifBlank { "Boss cocooned!" }
      ChatUtils.sendMessage(text)
      NotificationManager.queue("Cocoon Alert", text, 2500L)
      mc.gui.setSubtitle(Component.empty())
      mc.gui.setTitle(Component.literal(text).withStyle(ChatFormatting.RED))
    }
  }

  private fun currentTimerText(): String? {
    if (!enabled.value || !showTimer.value || timerEndMs <= 0L || mc.player == null || mc.level == null) return null
    val remaining = timerEndMs - System.currentTimeMillis()
    if (remaining <= 0L) {
      timerEndMs = 0L
      return null
    }
    return String.format(Locale.US, "Cocoon: %.1fs", remaining / 1000.0)
  }
}
