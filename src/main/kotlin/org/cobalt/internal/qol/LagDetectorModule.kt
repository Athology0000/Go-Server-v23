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
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.ui.theme.ThemeManager
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

  private var lastServerTickMs = 0L

  val lagHud = hudElement("lag-detector", "Lag Detector", "Shows server tick delay.") {
    anchor = HudAnchor.TOP_CENTER
    offsetY = 72f
    scale = 1f

    width { currentText()?.let { NVGRenderer.textWidth(it, 15f) + 22f } ?: 76f }
    height { 27f }

    render { x, y, _ ->
      val text = currentText() ?: return@render
      val w = NVGRenderer.textWidth(text, 15f) + 22f
      val theme = ThemeManager.currentTheme
      NVGRenderer.rect(x, y, w, 27f, theme.overlay, 5f)
      NVGRenderer.rect(x, y, 3f, 27f, 0xFFFF5555.toInt(), 2f)
      NVGRenderer.text(text, x + 11f, y + 6f, 15f, 0xFFFF7777.toInt())
    }
  }

  init {
    addSetting(enabled, thresholdMs)
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
    return String.format(Locale.US, "%dms", elapsed)
  }
}
