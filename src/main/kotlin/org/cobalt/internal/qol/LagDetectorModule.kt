package org.cobalt.internal.qol

import java.util.Locale
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.ui.theme.ThemeSurface
import org.cobalt.api.util.ui.NVGRenderer

object LagDetectorModule : Module("Lag Detector") {

  override val category = ModuleCategory.QOL

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Display a HUD timer when server tick packets stop arriving.",
    false,
  )

  private val thresholdMs = SliderSetting(
    "Threshold",
    "Milliseconds since the last server tick before the HUD appears.",
    750.0,
    100.0,
    5000.0,
    50.0,
  )

  private val displayUnit = ModeSetting(
    "Display Unit",
    "Choose whether the lag timer is shown in milliseconds or seconds.",
    0,
    arrayOf("Milliseconds", "Seconds"),
  )

  private val useTheme = CheckboxSetting(
    "Use Theme",
    "Use the active Cobalt theme for the lag detector HUD colors.",
    true,
  )

  private var lastServerTickMs = 0L

  val lagHud = hudElement("lag-detector", "Lag Detector", "Shows server tick delay.") {
    anchor = HudAnchor.TOP_CENTER
    offsetY = 72f
    scale = 1f

    setting(enabled)
    setting(thresholdMs)
    setting(displayUnit)
    setting(useTheme)

    width { currentText()?.let { NVGRenderer.textWidth(it, 15f) + 22f } ?: 76f }
    height { 27f }

    render { x, y, _ ->
      val text = currentText() ?: return@render
      val w = NVGRenderer.textWidth(text, 15f) + 22f
      val theme = ThemeManager.currentTheme
      val backgroundColor = if (useTheme.value) ThemeSurface.withAlpha(theme.panel, 0xE6) else 0xCC000000.toInt()
      val accentColor = if (useTheme.value) theme.errorIcon else 0xFFFF5555.toInt()
      val textColor = if (useTheme.value) theme.errorIcon else 0xFFFF7777.toInt()

      NVGRenderer.rect(x, y, w, 27f, backgroundColor, 5f)
      if (useTheme.value) {
        NVGRenderer.hollowRect(x, y, w, 27f, 1f, theme.errorBorder, 5f)
      }
      NVGRenderer.rect(x, y, 3f, 27f, accentColor, 2f)
      NVGRenderer.text(text, x + 11f, y + 6f, 15f, textColor)
    }
  }

  init {
    addSetting(enabled, thresholdMs, displayUnit, useTheme)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onPacket(event: PacketEvent.Incoming) {
    if (event.packet is ClientboundSetTimePacket) {
      lastServerTickMs = System.currentTimeMillis()
    }
  }

  private fun currentText(): String? {
    if (!enabled.value || mc.player == null || mc.level == null || lastServerTickMs == 0L) return null
    val elapsed = System.currentTimeMillis() - lastServerTickMs
    if (elapsed <= thresholdMs.value.toLong()) return null
    return if (displayUnit.value == 1) {
      String.format(Locale.US, "%.2fs", elapsed / 1000.0)
    } else {
      String.format(Locale.US, "%dms", elapsed)
    }
  }
}
