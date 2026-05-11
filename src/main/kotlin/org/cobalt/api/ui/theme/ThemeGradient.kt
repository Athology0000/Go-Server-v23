package org.cobalt.api.ui.theme

import kotlin.math.floor
import kotlin.math.roundToInt

object ThemeGradient {

  private val hexColorPattern = Regex("#?([0-9a-fA-F]{8}|[0-9a-fA-F]{6})")

  fun colors(theme: Theme = ThemeManager.currentTheme): Pair<Int, Int> {
    val stops = stops(theme)
    return stops[0] to stops[1]
  }

  fun stops(theme: Theme = ThemeManager.currentTheme): List<Int> {
    val configured = configuredStops(theme)
    if (configured != null) return configured

    return listOf(
      theme.accent.withFullAlpha(),
      theme.accentSecondary.withFullAlpha()
    )
  }

  fun configuredStops(theme: Theme = ThemeManager.currentTheme): List<Int>? {
    if (theme.chatGradient.isBlank()) return null

    val parsed = hexColorPattern.findAll(theme.chatGradient)
      .map { match ->
        val hex = match.groupValues[1]
        val rgbHex = if (hex.length == 8) hex.takeLast(6) else hex
        0xFF000000.toInt() or rgbHex.toInt(16)
      }
      .take(16)
      .toList()

    return parsed.takeIf { it.size >= 2 }
  }

  fun sample(theme: Theme = ThemeManager.currentTheme, position: Float): Int =
    sample(stops(theme), position)

  fun sample(stops: List<Int>, position: Float): Int {
    if (stops.isEmpty()) return 0xFFFFFFFF.toInt()
    if (stops.size == 1) return stops[0].withFullAlpha()

    val wrapped = ((position % 1f) + 1f) % 1f
    val scaled = wrapped * stops.size
    val startIndex = floor(scaled).toInt().coerceIn(0, stops.lastIndex)
    val endIndex = (startIndex + 1) % stops.size
    val ratio = scaled - floor(scaled)
    return lerpRgb(stops[startIndex], stops[endIndex], ratio)
  }

  fun phase(periodMs: Long = 4200L): Float {
    val period = periodMs.coerceAtLeast(1L)
    return (System.currentTimeMillis() % period).toFloat() / period.toFloat()
  }

  fun withAlpha(color: Int, alpha: Int): Int =
    (alpha.coerceIn(0, 255) shl 24) or (color and 0x00FFFFFF)

  private fun lerpRgb(start: Int, end: Int, ratio: Float): Int {
    val t = ratio.coerceIn(0f, 1f)
    val sr = (start ushr 16) and 0xFF
    val sg = (start ushr 8) and 0xFF
    val sb = start and 0xFF
    val er = (end ushr 16) and 0xFF
    val eg = (end ushr 8) and 0xFF
    val eb = end and 0xFF
    val r = (sr + (er - sr) * t).roundToInt()
    val g = (sg + (eg - sg) * t).roundToInt()
    val b = (sb + (eb - sb) * t).roundToInt()
    return 0xFF000000.toInt() or (r shl 16) or (g shl 8) or b
  }

  private fun Int.withFullAlpha(): Int =
    0xFF000000.toInt() or (this and 0x00FFFFFF)
}
