package org.cobalt.internal.visual

import kotlin.math.sin
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.GuiRenderEvent
import org.cobalt.api.event.impl.render.NvgEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object HotbarOverlayModule : Module("Liquid Hotbar") {

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Replace the vanilla hotbar with a liquid glass panel.",
    true
  )

  val isEnabled: Boolean get() = enabledSetting.value

  init {
    addSetting(enabledSetting)
    EventBus.register(this)
  }

  /**
   * GuiRenderEvent fires at TAIL of Gui.render().
   * We draw the dark glass fill first so items rendered immediately after appear on top of it.
   * No NVG here — NvgEvent always fires later, so any NVG fill would land on top of items.
   */
  @SubscribeEvent
  fun onGui(event: GuiRenderEvent) {
    if (!isEnabled || mc.screen != null) return
    val player = mc.player ?: return

    val gw = mc.window.guiScaledWidth
    val gh = mc.window.guiScaledHeight
    val gx = gw / 2 - 91   // vanilla hotbar left edge (GUI scaled units)
    val gy = gh - 22         // vanilla hotbar top edge

    // ── Dark glass fill ──────────────────────────────────────────────────────
    // alpha=0x60 (~37%), very dark navy. Covers vanilla grey background cleanly.
    event.graphics.fill(gx, gy, gx + 182, gy + 22, 0x60050E18.toInt())

    // ── Selected slot darker highlight ───────────────────────────────────────
    val sel = player.inventory.selectedSlot
    event.graphics.fill(gx + 1 + sel * 20, gy + 1,
                        gx + 1 + sel * 20 + 20, gy + 21, 0x50102840.toInt())

    // ── Off-hand fill (if occupied) ──────────────────────────────────────────
    val offhand = player.offhandItem
    if (!offhand.isEmpty) {
      event.graphics.fill(gx - 29, gy, gx - 5, gy + 22, 0x60050E18.toInt())
    }

    // ── Items ─ rendered AFTER fills so they sit on top ──────────────────────
    for (slot in 0..8) {
      val stack = player.inventory.getItem(slot)
      if (stack.isEmpty) continue
      val ix = gx + 1 + slot * 20 + 2   // vanilla-exact item position
      val iy = gy + 3
      event.graphics.renderItem(stack, ix, iy)
      event.graphics.renderItemDecorations(mc.font, stack, ix, iy)
    }

    if (!offhand.isEmpty) {
      event.graphics.renderItem(offhand, gx - 29 + 4, gy + 3)
      event.graphics.renderItemDecorations(mc.font, offhand, gx - 29 + 4, gy + 3)
    }
  }

  /**
   * NvgEvent fires after Gui.render() — only thin border/glow decorations live here.
   * No fills so items from onGui are never obscured.
   */
  @SubscribeEvent
  fun onNvg(event: NvgEvent) {
    if (!isEnabled || mc.screen != null) return
    val player = mc.player ?: return

    val sw = mc.window.screenWidth.toFloat()
    val sh = mc.window.screenHeight.toFloat()
    val sc = mc.window.guiScale.toFloat()
    val gw = mc.window.guiScaledWidth.toFloat()
    val gh = mc.window.guiScaledHeight.toFloat()
    val t  = System.currentTimeMillis() / 1000.0

    val hx = (gw / 2f - 91f) * sc
    val hy = (gh - 22f) * sc
    val hw = 182f * sc
    val hh = 22f * sc
    val rad = 4f * sc

    NVGRenderer.beginFrame(sw, sh)

    // ── Animated outer border ─────────────────────────────────────────────────
    // Slow pulse on the base border brightness
    val basePulse = (sin(t * 1.1) * 0.22 + 0.78).toFloat()
    val baseAlpha = (basePulse * 0x72).toInt()  // max 0x72 = 114
    NVGRenderer.hollowRect(hx, hy, hw, hh, sc * 0.9f, (baseAlpha shl 24) or 0x6699CC, rad)

    // Brighter inner border ring for depth
    val innerAlpha = (basePulse * 0x38).toInt()
    NVGRenderer.hollowRect(hx + sc, hy + sc, hw - sc * 2f, hh - sc * 2f,
      sc * 0.6f, (innerAlpha shl 24) or 0x99CCEE, 0f)

    // Sweeping shine that travels left→right along the top edge
    val shineFrac  = ((sin(t * 0.65) + 1.0) / 2.0).toFloat()  // 0..1
    val shineHalfW = 55f * sc
    val shineX     = hx + shineFrac * (hw - shineHalfW * 2f)
    NVGRenderer.gradientRect(shineX, hy - sc * 0.3f, shineHalfW, sc * 2f,
      0x00BBDDFF, 0x65BBDDFF, Gradient.LeftToRight, 0f)
    NVGRenderer.gradientRect(shineX + shineHalfW, hy - sc * 0.3f, shineHalfW, sc * 2f,
      0x65BBDDFF, 0x00BBDDFF, Gradient.LeftToRight, 0f)

    // ── Selected slot animated border ────────────────────────────────────────
    val sel       = player.inventory.selectedSlot
    val selPulse  = (sin(t * 2.0) * 0.30 + 0.70).toFloat()
    val selAlpha  = (selPulse * 210f).toInt()
    val slotX     = hx + (1f + sel.toFloat() * 20f) * sc
    val slotY     = hy + sc
    // Glow halo behind selection
    NVGRenderer.hollowRect(slotX - sc, slotY - sc, 22f * sc, 22f * sc,
      sc * 1.5f, ((selAlpha / 3) shl 24) or 0x88BBFF, rad)
    // Crisp inner selection ring
    NVGRenderer.hollowRect(slotX, slotY, 20f * sc, 20f * sc,
      sc * 0.9f, (selAlpha shl 24) or 0xAADDFF, 3f * sc)

    // ── Off-hand border ──────────────────────────────────────────────────────
    if (!player.offhandItem.isEmpty) {
      val ox = hx - 29f * sc
      val ow = 24f * sc
      NVGRenderer.hollowRect(ox, hy, ow, hh, sc * 0.9f, (baseAlpha shl 24) or 0x6699CC, rad)
    }

    NVGRenderer.endFrame()
  }
}
