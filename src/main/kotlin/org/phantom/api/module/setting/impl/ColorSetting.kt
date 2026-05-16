package org.phantom.api.module.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.phantom.api.module.setting.Setting
import org.phantom.api.ui.theme.ThemeManager
import java.awt.Color

/** ARGB color picker setting. Value is an ARGB integer (e.g. `0xFFFF0000.toInt()` for red). */
class ColorSetting(
  name: String,
  description: String,
  defaultValue: Int,
  val supportsGradient: Boolean = true,
  val supportsAnimatedGradient: Boolean = true,
) : Setting<Int>(name, description, defaultValue) {

  override val defaultValue: Int = defaultValue

  /** Current color mode (static, rainbow, theme, etc.). */
  var mode: ColorMode = ColorMode.Static(defaultValue)

  /** Instance creation timestamp for per-instance rainbow phase offset. */
  private val instanceStartTime = System.currentTimeMillis()

  /** Dynamically resolved color based on current mode. */
  override var value: Int
    get() = resolveColor()
    set(newValue) {
      mode = ColorMode.Static(newValue)
      super.value = newValue
    }

  /**
   * Resolves the current color based on the active mode.
   * Called on every read of `value` to support dynamic modes (rainbow, theme).
   */
  private fun resolveColor(): Int {
    return when (val m = mode) {
      is ColorMode.Static -> m.argb

      is ColorMode.Rainbow -> {
        // Per-instance rainbow: use instanceStartTime for phase offset
        val elapsed = (System.currentTimeMillis() - instanceStartTime) / 1000.0
        val hue = ((elapsed * m.speed) % 1.0 + 1.0).toFloat() % 1f
        val rgb = Color.HSBtoRGB(hue, m.saturation, m.brightness)
        val alpha = (m.opacity * 255).toInt().coerceIn(0, 255)
        (alpha shl 24) or (rgb and 0x00FFFFFF)
      }

      is ColorMode.SyncedRainbow -> {
        val theme = ThemeManager.currentTheme
        val hue = ThemeManager.getRainbowHue()
        val sat = theme.rainbowSaturation
        val bri = theme.rainbowBrightness
        val rgb = Color.HSBtoRGB(hue, sat, bri)
        val alpha = (m.opacity * 255).toInt().coerceIn(0, 255)
        (alpha shl 24) or (rgb and 0x00FFFFFF)
      }

      is ColorMode.Gradient -> {
        if (m.animated) sampleGradient(m.startArgb, m.endArgb, gradientPhase()) else m.startArgb
      }

      is ColorMode.ThemeColor -> {
        ThemeColorResolver.resolve(m.propertyName)
      }

      is ColorMode.TweakedTheme -> {
        val baseColor = ThemeColorResolver.resolve(m.propertyName)
        tweakColor(baseColor, m.hueOffset, m.saturationMultiplier, m.brightnessMultiplier, m.opacityMultiplier)
      }
    }
  }

  /**
   * Applies HSB/opacity adjustments to a base ARGB color.
   * Used by TweakedTheme mode.
   */
  private fun tweakColor(
    argb: Int,
    hueShift: Float,
    saturationMult: Float,
    brightnessMult: Float,
    opacityMult: Float
  ): Int {
    val color = Color(argb, true)
    val hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)

    val newHue = (hsb[0] + hueShift + 1f) % 1f
    val newSat = (hsb[1] * saturationMult).coerceIn(0f, 1f)
    val newBri = (hsb[2] * brightnessMult).coerceIn(0f, 1f)
    val newAlpha = ((color.alpha / 255f) * opacityMult).coerceIn(0f, 1f)

    val rgb = Color.HSBtoRGB(newHue, newSat, newBri)
    val alpha = (newAlpha * 255).toInt().coerceIn(0, 255)
    return (alpha shl 24) or (rgb and 0x00FFFFFF)
  }

  override fun read(element: JsonElement) {
    if (element.isJsonPrimitive) {
      // Legacy format: plain ARGB int -> Static mode
      val argb = element.asInt
      mode = ColorMode.Static(argb)
      super.value = argb
    } else if (element.isJsonObject) {
      // New format: JSON object with mode discriminator
      val obj = element.asJsonObject
      val modeType = obj.get("mode")?.asString ?: "static"

      mode = when (modeType) {
        "static" -> {
          val argb = obj.get("argb")?.asInt ?: super.value
          super.value = argb
          ColorMode.Static(argb)
        }

        "rainbow" -> {
          ColorMode.Rainbow(
            speed = obj.get("speed")?.asFloat ?: 1f,
            saturation = obj.get("saturation")?.asFloat ?: 1f,
            brightness = obj.get("brightness")?.asFloat ?: 1f,
            opacity = obj.get("opacity")?.asFloat ?: 1f
          )
        }

        "synced_rainbow" -> {
          ColorMode.SyncedRainbow(
            speed = obj.get("speed")?.asFloat ?: 1f,
            saturation = obj.get("saturation")?.asFloat ?: 1f,
            brightness = obj.get("brightness")?.asFloat ?: 1f,
            opacity = obj.get("opacity")?.asFloat ?: 1f
          )
        }

        "theme" -> {
          ColorMode.ThemeColor(
            propertyName = obj.get("propertyName")?.asString ?: "accent"
          )
        }

        "gradient" -> {
          val startArgb = obj.get("startArgb")?.asInt ?: super.value
          val endArgb = obj.get("endArgb")?.asInt ?: startArgb
          super.value = startArgb
          if (supportsGradient) {
            ColorMode.Gradient(
              startArgb = startArgb,
              endArgb = endArgb,
              direction = obj.get("direction")?.asString ?: ColorMode.DIRECTION_LEFT_TO_RIGHT,
              animated = supportsAnimatedGradient && (obj.get("animated")?.asBoolean ?: false),
            )
          } else {
            ColorMode.Static(startArgb)
          }
        }

        "tweaked_theme" -> {
          ColorMode.TweakedTheme(
            propertyName = obj.get("propertyName")?.asString ?: "accent",
            hueOffset = obj.get("hueOffset")?.asFloat ?: 0f,
            saturationMultiplier = obj.get("saturationMultiplier")?.asFloat ?: 1f,
            brightnessMultiplier = obj.get("brightnessMultiplier")?.asFloat ?: 1f,
            opacityMultiplier = obj.get("opacityMultiplier")?.asFloat ?: 1f
          )
        }

        else -> {
          // Unknown mode: fallback to static
          val argb = super.value
          ColorMode.Static(argb)
        }
      }
    }
  }

  override fun write(): JsonElement {
    return when (val m = mode) {
      is ColorMode.Static -> {
        // Backward compatible: write as plain int
        JsonPrimitive(m.argb)
      }

      is ColorMode.Rainbow -> {
        JsonObject().apply {
          addProperty("mode", "rainbow")
          addProperty("speed", m.speed)
          addProperty("saturation", m.saturation)
          addProperty("brightness", m.brightness)
          addProperty("opacity", m.opacity)
        }
      }

      is ColorMode.SyncedRainbow -> {
        JsonObject().apply {
          addProperty("mode", "synced_rainbow")
          addProperty("speed", m.speed)
          addProperty("saturation", m.saturation)
          addProperty("brightness", m.brightness)
          addProperty("opacity", m.opacity)
        }
      }

      is ColorMode.ThemeColor -> {
        JsonObject().apply {
          addProperty("mode", "theme")
          addProperty("propertyName", m.propertyName)
        }
      }

      is ColorMode.Gradient -> {
        JsonObject().apply {
          addProperty("mode", "gradient")
          addProperty("startArgb", m.startArgb)
          addProperty("endArgb", m.endArgb)
          addProperty("direction", m.direction)
          addProperty("animated", m.animated)
        }
      }

      is ColorMode.TweakedTheme -> {
        JsonObject().apply {
          addProperty("mode", "tweaked_theme")
          addProperty("propertyName", m.propertyName)
          addProperty("hueOffset", m.hueOffset)
          addProperty("saturationMultiplier", m.saturationMultiplier)
          addProperty("brightnessMultiplier", m.brightnessMultiplier)
          addProperty("opacityMultiplier", m.opacityMultiplier)
        }
      }
    }
  }

  private fun gradientPhase(): Float {
    val phase = (System.currentTimeMillis() % 4200L).toFloat() / 4200f
    return ((kotlin.math.cos(phase * Math.PI * 2.0) + 1.0) * 0.5).toFloat()
  }

  private fun sampleGradient(startArgb: Int, endArgb: Int, t: Float): Int {
    val clamped = t.coerceIn(0f, 1f)
    val a = lerpChannel(startArgb ushr 24, endArgb ushr 24, clamped)
    val r = lerpChannel(startArgb ushr 16, endArgb ushr 16, clamped)
    val g = lerpChannel(startArgb ushr 8, endArgb ushr 8, clamped)
    val b = lerpChannel(startArgb, endArgb, clamped)
    return (a shl 24) or (r shl 16) or (g shl 8) or b
  }

  private fun lerpChannel(start: Int, end: Int, t: Float): Int {
    return ((start and 0xFF) + (((end and 0xFF) - (start and 0xFF)) * t)).toInt().coerceIn(0, 255)
  }

}
