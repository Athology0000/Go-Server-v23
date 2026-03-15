package org.cobalt.internal.visual

import kotlin.math.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.TitleScreen
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.NvgEvent
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object TitleScreenRenderer {

  private val mc = Minecraft.getInstance()
  private var mouseGuiX = 0
  private var mouseGuiY = 0

  init { EventBus.register(this) }

  fun setMousePos(x: Int, y: Int) { mouseGuiX = x; mouseGuiY = y }

  @SubscribeEvent
  fun onNvg(event: NvgEvent) {
    if (mc.screen !is TitleScreen) return
    val sw = mc.window.screenWidth.toFloat()
    val sh = mc.window.screenHeight.toFloat()
    val sc = mc.window.guiScale.toFloat()
    val gw = mc.window.guiScaledWidth.toFloat()
    val gh = mc.window.guiScaledHeight.toFloat()
    val t  = System.currentTimeMillis() / 1000.0

    NVGRenderer.beginFrame(sw, sh)
    drawBackground(sw, sh, t)
    drawStars(sw, sh, t)
    drawLightningNodes(sw, sh, t)
    drawTitle(sw, sh, sc, t)
    drawButtons(gw, gh, sc, t)
    NVGRenderer.endFrame()
  }

  // ---- Noise helpers ----

  /**
   * Domain-warped FBM using 4 octaves of sin harmonics.
   * Domain warping distorts the input coords with a lower-frequency noise field first,
   * producing organic non-repeating cloud shapes. The t offsets make the field drift over time.
   */
  private fun fbm(px: Float, py: Float, t: Double): Float {
    val tx = t * 7.0
    val ty = t * 4.5
    // Domain warp: shift coords by another noise pass
    val wx = (sin(px * 0.0030 + tx * 0.34) * 88.0 + cos(py * 0.0038 + ty * 0.27) * 62.0).toFloat()
    val wy = (sin(py * 0.0034 - tx * 0.27) * 72.0 + cos(px * 0.0027 - ty * 0.31) * 55.0).toFloat()
    val x  = px + wx
    val y  = py + wy
    // FBM
    var v = sin(x * 0.0052 + tx * 0.07) * sin(y * 0.0060 + ty * 0.09) * 0.5000
    v    += sin(x * 0.0107 - tx * 0.05 + 1.57) * sin(y * 0.0114 + ty * 0.11 + 0.79) * 0.2500
    v    += sin(x * 0.0218 + tx * 0.09 + 2.36) * sin(y * 0.0230 - ty * 0.07 + 1.18) * 0.1250
    v    += sin(x * 0.0441 - tx * 0.07 + 4.12) * sin(y * 0.0457 + ty * 0.10 + 3.24) * 0.0625
    return ((v / 0.9375) * 0.5 + 0.5).toFloat().coerceIn(0f, 1f)
  }

  /** Radial vignette weight — bright near the upper-centre, dark at edges. */
  private fun cloudVignette(fx: Float, fy: Float): Float {
    val cx = fx - 0.50f
    val cy = fy - 0.38f
    return (1f - (cx * cx * 2.8f + cy * cy * 2.2f)).coerceIn(0f, 1f)
  }

  // ---- Background (noise cloud field) ----

  private fun drawBackground(sw: Float, sh: Float, t: Double) {
    NVGRenderer.rect(0f, 0f, sw, sh, 0xFF050810.toInt(), 0f)

    val cols = 42; val rows = 26
    val cw = sw / cols; val ch = sh / rows

    // Pass 1 — wide soft glow halo (large radius, very low alpha)
    // Gives the "lit-from-within" bloom around every cloud mass.
    for (row in 0 until rows) {
      for (col in 0 until cols) {
        val px = (col + 0.5f) * cw
        val py = (row + 0.5f) * ch
        val n  = fbm(px, py, t)
        if (n < 0.28f) continue
        val intensity = ((n - 0.28f) / 0.72f).coerceIn(0f, 1f)
        val vign  = cloudVignette(px / sw, py / sh)
        val alpha = (intensity * vign * 22).toInt().coerceIn(0, 22)
        if (alpha < 2) continue
        NVGRenderer.circle(px, py, cw * 3.8f, (alpha shl 24) or 0x102888)
      }
    }

    // Pass 2 — dense cloud body (tighter radius, moderate alpha)
    for (row in 0 until rows) {
      for (col in 0 until cols) {
        val px = (col + 0.5f) * cw
        val py = (row + 0.5f) * ch
        val n  = fbm(px, py, t)
        if (n < 0.46f) continue
        val intensity = ((n - 0.46f) / 0.54f).coerceIn(0f, 1f)
        val vign  = cloudVignette(px / sw, py / sh)
        val alpha = (intensity.pow(1.3f) * vign * 52).toInt().coerceIn(0, 52)
        if (alpha < 2) continue
        val rgb = when {
          intensity > 0.65f -> 0x1840B0
          intensity > 0.35f -> 0x102E80
          else              -> 0x0B2262
        }
        NVGRenderer.circle(px, py, cw * 1.5f, (alpha shl 24) or rgb)
      }
    }

    // Pass 3 — bright electric core at density peaks
    for (row in 0 until rows) {
      for (col in 0 until cols) {
        val px = (col + 0.5f) * cw
        val py = (row + 0.5f) * ch
        val n  = fbm(px, py, t)
        if (n < 0.68f) continue
        val intensity = ((n - 0.68f) / 0.32f).coerceIn(0f, 1f)
        val vign  = cloudVignette(px / sw, py / sh)
        val alpha = (intensity.pow(1.6f) * vign * 38).toInt().coerceIn(0, 38)
        if (alpha < 2) continue
        NVGRenderer.circle(px, py, cw * 0.8f, (alpha shl 24) or 0x2858CC)
      }
    }

    // Aurora-like horizontal bands
    for (i in 0..2) {
      val bandY  = sh * (0.18f + i * 0.20f)
      val bandH  = sh * 0.14f
      val drift  = (sin(t * 0.15 + i * 2.1) * sh * 0.03).toFloat()
      val aAlpha = ((sin(t * 0.28 + i * 1.7) * 0.4 + 0.4) * 14).toInt()
      NVGRenderer.gradientRect(0f, bandY + drift, sw, bandH, 0x00000000, (aAlpha shl 24) or 0x1840A0, Gradient.TopToBottom, 0f)
      NVGRenderer.gradientRect(0f, bandY + drift + bandH, sw, bandH, (aAlpha shl 24) or 0x1840A0, 0x00000000, Gradient.TopToBottom, 0f)
    }

    // Edge vignette
    NVGRenderer.gradientRect(0f, 0f, sw * 0.22f, sh, 0x88000000.toInt(), 0x00000000, Gradient.LeftToRight, 0f)
    NVGRenderer.gradientRect(sw * 0.78f, 0f, sw * 0.22f, sh, 0x00000000, 0x88000000.toInt(), Gradient.LeftToRight, 0f)
    NVGRenderer.gradientRect(0f, sh * 0.65f, sw, sh * 0.35f, 0x00000000, 0xCC000000.toInt(), Gradient.TopToBottom, 0f)
    NVGRenderer.gradientRect(0f, 0f, sw, sh * 0.10f, 0x66000000.toInt(), 0x00000000, Gradient.TopToBottom, 0f)
  }

  /** Soft gaussian blob — used only for orb halos and title glow. */
  private fun drawNebulaBlob(cx: Float, cy: Float, maxR: Float, rgb: Int, peakAlpha: Int, layers: Int = 30) {
    for (i in 0..layers) {
      val frac  = i.toFloat() / layers
      val r     = maxR * (1f - frac * 0.90f)
      val gauss = exp(-4.2 * (1.0 - frac) * (1.0 - frac)).toFloat()
      val alpha = (peakAlpha * gauss).toInt().coerceIn(0, peakAlpha)
      NVGRenderer.circle(cx, cy, r, (alpha shl 24) or rgb)
    }
  }

  // ---- Stars ----

  private fun drawStars(sw: Float, sh: Float, t: Double) {
    val rng = java.util.Random(99887L)
    repeat(180) { i ->
      val sx = rng.nextFloat() * sw
      val sy = rng.nextFloat() * sh
      val b  = rng.nextFloat()
      val twinkle = (sin(t * (0.3 + b * 1.8) + i) * 0.35 + 0.65).toFloat()
      val r = (0.3f + b * 1.6f) * (sw / 1920f).coerceAtLeast(1f)
      NVGRenderer.circle(sx, sy, r, ((b * twinkle * 200).toInt() shl 24) or 0xCCD8F0)
    }
    val rng2 = java.util.Random(55443L)
    repeat(18) { i ->
      val sx    = rng2.nextFloat() * sw
      val sy    = rng2.nextFloat() * sh
      val pulse = (sin(t * 0.6 + i * 1.1) * 0.4 + 0.6).toFloat()
      val r = (1.5f + rng2.nextFloat() * 1.5f) * (sw / 1920f).coerceAtLeast(1f)
      NVGRenderer.circle(sx, sy, r, ((pulse * 200).toInt() shl 24) or 0xEEF4FF)
    }
  }

  // ---- Lightning nodes + bolts ----

  private data class OrbDef(val fx: Float, val fy: Float, val phase: Double)

  private val orbDefs = listOf(
    OrbDef(0.15f, 0.22f, 0.0),
    OrbDef(0.73f, 0.16f, 2.3),
    OrbDef(0.55f, 0.45f, 4.8),
    OrbDef(0.88f, 0.35f, 1.6),
    OrbDef(0.32f, 0.52f, 3.5),
    OrbDef(0.62f, 0.28f, 6.1),
  )

  private val connections = listOf(0 to 1, 1 to 5, 5 to 2, 2 to 4, 4 to 0, 1 to 2, 3 to 5, 0 to 3)

  private fun drawLightningNodes(sw: Float, sh: Float, t: Double) {
    val pos = orbDefs.map { o ->
      val x = o.fx * sw + (sin(t * 0.2 + o.phase) * sw * 0.04).toFloat()
      val y = o.fy * sh + (cos(t * 0.15 + o.phase) * sh * 0.03).toFloat()
      Triple(x, y, o.phase)
    }

    // One bolt at a time — each connection holds for BOLT_SLOT_SEC then fades out.
    val slotProgress = (t % BOLT_SLOT_SEC) / BOLT_SLOT_SEC  // 0..1 within current slot
    val slotIdx      = (t / BOLT_SLOT_SEC).toLong()
    val (a, b)       = connections[(slotIdx % connections.size).toInt()]
    val boltSeed     = slotIdx * 131L + a * 31L + b * 17L

    // Quick fade-in (first 12%), hold, slow fade-out (last 25%)
    val alpha = when {
      slotProgress < 0.12 -> (slotProgress / 0.12 * 85).toInt()
      slotProgress > 0.75 -> ((1.0 - (slotProgress - 0.75) / 0.25) * 85).toInt()
      else                -> 85
    }.coerceIn(0, 85)

    if (alpha > 4) {
      drawLightningBolt(pos[a].first, pos[a].second,
                        pos[b].first, pos[b].second,
                        alpha, boltSeed, sw)
    }

    for ((x, y, phase) in pos) {
      val pulse = (sin(t * 0.5 + phase) * 0.35 + 0.65).toFloat()
      drawNebulaBlob(x, y, sw * 0.016f, 0x1E50C0, (pulse * 55).toInt(), 18)
      NVGRenderer.circle(x, y, sw * 0.003f * pulse, ((pulse * 230).toInt() shl 24) or 0xAADDFF)
    }
  }

  private const val BOLT_SLOT_SEC = 3.5

  /**
   * Jagged lightning bolt via midpoint displacement.
   * Seed is quantized so the bolt snaps to a new random shape ~8× per second (flickering).
   */
  private fun drawLightningBolt(
    x1: Float, y1: Float, x2: Float, y2: Float,
    alpha: Int, seed: Long, sw: Float
  ) {
    val rng = java.util.Random(seed)
    val pts = arrayListOf(floatArrayOf(x1, y1), floatArrayOf(x2, y2))
    repeat(4) {
      val next = arrayListOf<FloatArray>()
      for (i in 0 until pts.size - 1) {
        next.add(pts[i])
        val mx  = (pts[i][0] + pts[i + 1][0]) / 2f
        val my  = (pts[i][1] + pts[i + 1][1]) / 2f
        val dx  = pts[i + 1][0] - pts[i][0]
        val dy  = pts[i + 1][1] - pts[i][1]
        val len = sqrt(dx * dx + dy * dy)
        if (len > 0f) {
          val jitter = (rng.nextFloat() - 0.5f) * len * 0.90f
          next.add(floatArrayOf(mx + (-dy / len) * jitter, my + (dx / len) * jitter))
        } else {
          next.add(floatArrayOf(mx, my))
        }
      }
      next.add(pts.last())
      pts.clear(); pts.addAll(next)
    }

    val glowW  = sw * 0.0025f
    val coreW  = sw * 0.0007f
    val gAlpha = (alpha * 0.22f).toInt()

    for (i in 0 until pts.size - 1) {
      val ax = pts[i][0]; val ay = pts[i][1]
      val bx = pts[i + 1][0]; val by = pts[i + 1][1]
      val dx = bx - ax; val dy = by - ay
      val len = sqrt(dx * dx + dy * dy)
      if (len < 0.5f) continue
      val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
      NVGRenderer.push()
      NVGRenderer.translate(ax, ay)
      NVGRenderer.rotate(angle)
      NVGRenderer.rect(0f, -glowW, len, glowW * 2f, (gAlpha shl 24) or 0x5588CC, 0f)
      NVGRenderer.rect(0f, -coreW, len, coreW * 2f, (alpha shl 24) or 0xCCEEFF, 0f)
      NVGRenderer.pop()
    }
  }

  // ---- Title ----

  private fun drawTitle(sw: Float, sh: Float, sc: Float, t: Double) {
    val title   = "Dutt Client"
    val titleSz = 32f * sc
    val titleW  = NVGRenderer.textWidth(title, titleSz)
    val titleX  = sw / 2f - titleW / 2f
    val titleY  = sh * 0.23f
    val pulse   = (sin(t * 0.8) * 0.08 + 0.92).toFloat()
    drawNebulaBlob(sw / 2f, titleY + titleSz * 0.5f, titleSz * 2.5f, 0x1840A0, (pulse * 40).toInt(), 24)
    val titleAlpha = (pulse * 255).toInt().coerceIn(180, 255)
    NVGRenderer.text(title, titleX, titleY, titleSz, (titleAlpha shl 24) or 0xEEF2FF)
  }

  // ---- Buttons ----

  private data class Btn(val label: String, val gx: Float, val gy: Float, val gw: Float, val gh: Float = 20f)

  private fun drawButtons(gw: Float, gh: Float, sc: Float, t: Double) {
    val cx = gw / 2f
    val buttons = listOf(
      Btn("Singleplayer", cx - 100f, gh / 4f + 48f,  200f),
      Btn("Multiplayer",  cx - 100f, gh / 4f + 72f,  200f),
      Btn("Options",      cx - 100f, gh / 4f + 132f,  98f),
      Btn("Quit",         cx + 2f,   gh / 4f + 132f,  98f),
    )

    for ((label, gx, gy, btnGw, btnGh) in buttons) {
      val hovered = mouseGuiX >= gx && mouseGuiX <= gx + btnGw &&
                    mouseGuiY >= gy && mouseGuiY <= gy + btnGh
      val sx  = gx * sc;  val sy  = gy * sc
      val bwS = btnGw * sc; val bhS = btnGh * sc
      val rad = 6f * sc

      val fillAlpha   = if (hovered) 0x55 else 0x28
      val borderAlpha = if (hovered) 0xA0 else 0x44
      NVGRenderer.rect(sx, sy, bwS, bhS, (fillAlpha shl 24) or 0x7A90B8, rad)
      NVGRenderer.gradientRect(sx + rad, sy, bwS - rad * 2f, bhS * 0.38f,
        0x1EAACCEE.toInt(), 0x00AACCEE, Gradient.TopToBottom, rad)
      NVGRenderer.hollowRect(sx, sy, bwS, bhS, sc * 0.85f, (borderAlpha shl 24) or 0x99BBDD, rad)

      if (label == "Options") {
        // Gear icon + text side by side
        val textSz  = 9f * sc
        val gearR   = bhS * 0.30f
        val tw      = NVGRenderer.textWidth("Options", textSz)
        val gap     = 4f * sc
        val totalW  = gearR * 2f + gap + tw
        val startX  = sx + (bwS - totalW) / 2f
        val tcol    = if (hovered) 0xFFEEF4FF.toInt() else 0xAABBCCFF.toInt()
        drawGearIcon(startX + gearR, sy + bhS / 2f, gearR, t, hovered)
        NVGRenderer.text("Options", startX + gearR * 2f + gap,
          sy + (bhS - textSz) / 2f + sc * 0.5f, textSz, tcol)
      } else {
        val textSz  = 9f * sc
        val tw      = NVGRenderer.textWidth(label, textSz)
        val tcol    = if (hovered) 0xFFEEF4FF.toInt() else 0xAABBCCFF.toInt()
        NVGRenderer.text(label, sx + (bwS - tw) / 2f,
          sy + (bhS - textSz) / 2f + sc * 0.5f, textSz, tcol)
      }
    }
  }

  private fun drawGearIcon(cx: Float, cy: Float, outerR: Float, t: Double, hovered: Boolean) {
    val numTeeth   = 8
    val rot        = (t * 0.55).toFloat()
    val discR      = outerR * 0.66f
    val toothW     = outerR * 0.32f
    val toothStart = discR - outerR * 0.08f
    val toothLen   = outerR - toothStart
    val col        = if (hovered) 0xFFEEF4FF.toInt() else 0xAABBCCFF.toInt()

    repeat(numTeeth) { i ->
      val angle = rot + i * (2f * PI.toFloat() / numTeeth)
      NVGRenderer.push()
      NVGRenderer.translate(cx, cy)
      NVGRenderer.rotate(angle)
      NVGRenderer.rect(-toothW / 2f, toothStart, toothW, toothLen, col, toothW * 0.22f)
      NVGRenderer.pop()
    }
    NVGRenderer.circle(cx, cy, discR, col)
    NVGRenderer.circle(cx, cy, discR * 0.40f, 0xEE050810.toInt())
  }
}
