package org.cobalt.api.util

import kotlin.math.roundToInt
import kotlin.math.floor
import net.minecraft.ChatFormatting
import net.minecraft.client.GuiMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.util.FormattedCharSequence
import org.cobalt.mixin.client.ChatComponentAccessor
import org.cobalt.api.ui.theme.ThemeGradient

object ChatUtils {

  private val mc: Minecraft =
    Minecraft.getInstance()

  // used for sendDebug to prevent resending same message multiple times in a row
  private var lastDebugMessageKey: String = ""
  private var lastAnimatedRefreshMs: Long = 0L

  @JvmStatic
  fun sendDebug(message: String) {
    sendDebug(source = null, message = message)
  }

  @JvmStatic
  fun sendDebug(source: String?, message: String) {
    if (mc.player == null || mc.level == null) return

    val trimmedSource = source?.trim().orEmpty()
    val debugKey = if (trimmedSource.isEmpty()) message else "$trimmedSource|$message"
    if (debugKey == lastDebugMessageKey) return

    val body = Component.empty()
    if (trimmedSource.isNotEmpty()) {
      body.append(
        glowingGradient("[$trimmedSource] ", 0x44EBA4, 0xB9FFF1)
      )
    }
    body.append(glowingGradient(message, 0xEFFFFC, 0x78FFD2))

    mc.gui.chat.addMessage(
      Component.empty().append(debugPrefix())
        .append(body)
    )

    lastDebugMessageKey = debugKey
  }

  @JvmStatic
  fun sendMessage(message: String) {
    if (mc.player == null || mc.level == null) return

    mc.gui.chat.addMessage(
      Component.empty().append(prefix())
        .append(glowingGradient(message, 0xF7FCFF, 0x73DFFF))
    )
  }

  @JvmStatic
  fun buildGradient(text: String, startRgb: Int, endRgb: Int): MutableComponent {
    val result = Component.empty()
    val length = text.length

    if (length <= 1) {
      return Component.literal(text).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startRgb)))
    }

    val sr = (startRgb shr 16) and 0xFF
    val sg = (startRgb shr 8) and 0xFF
    val sb = startRgb and 0xFF

    val er = (endRgb shr 16) and 0xFF
    val eg = (endRgb shr 8) and 0xFF
    val eb = endRgb and 0xFF

    for (i in text.indices) {
      val ratio = i.toDouble() / (length - 1)

      val r = (sr + ratio * (er - sr)).roundToInt()
      val g = (sg + ratio * (eg - sg)).roundToInt()
      val b = (sb + ratio * (eb - sb)).roundToInt()

      val rgb = (r shl 16) or (g shl 8) or b

      val charText = Component.literal(text[i].toString())
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)))

      result.append(charText)
    }

    return result
  }

  @JvmStatic
  fun refreshAnimatedChat(chat: ChatComponent) {
    if (mc.player == null || mc.level == null) return

    val now = System.currentTimeMillis()
    if (now - lastAnimatedRefreshMs < 33L) return
    lastAnimatedRefreshMs = now

    val accessor = chat as? ChatComponentAccessor ?: return
    val messages = accessor.getCobaltAllMessages()
    var changed = false

    for (index in messages.indices) {
      val message = messages[index]
      val replacement = animatedCobaltLine(message.content().string) ?: continue
      messages[index] = GuiMessage(message.addedTime(), replacement, message.signature(), message.tag())
      changed = true
    }

    if (changed) {
      accessor.callCobaltRefreshTrimmedMessages()
    }
  }

  @JvmStatic
  fun animatedChatLineContent(original: FormattedCharSequence): FormattedCharSequence? {
    val raw = formattedSequenceString(original)
    val replacement = animatedCobaltLine(raw) ?: return null
    return replacement.visualOrderText
  }

  private fun animatedCobaltLine(raw: String): MutableComponent? {
    val clean = stripFormatting(raw)
    return when {
      clean.startsWith("[Cobalt Debug] ") -> Component.empty()
        .append(debugPrefix())
        .append(glowingGradient(clean.removePrefix("[Cobalt Debug] "), 0xEFFFFC, 0x78FFD2))

      clean.startsWith("[Cobalt] ") -> Component.empty()
        .append(prefix())
        .append(glowingGradient(clean.removePrefix("[Cobalt] "), 0xF7FCFF, 0x73DFFF))

      else -> null
    }
  }

  private fun glowingGradient(text: String, startRgb: Int, endRgb: Int): MutableComponent {
    val result = Component.empty()
    if (text.isEmpty()) return result

    val customStops = chatGradientStops()
    if (customStops != null) {
      return animatedStopGradient(text, customStops)
    }

    val phase = currentGradientPhase()
    val length = (text.length - 1).coerceAtLeast(1)
    val baseHue = readableHue(startRgb, endRgb)

    for (i in text.indices) {
      val position = i.toDouble() / length
      val hue = positiveModulo(baseHue + phase + position * 0.28, 1.0)
      val glowWave = (kotlin.math.sin((position * 2.0 - phase * 2.0) * Math.PI * 2.0) + 1.0) * 0.5
      val color = hsvToRgb(
        hue,
        saturation = 0.70 + glowWave * 0.20,
        value = 0.86 + glowWave * 0.14
      )
      result.append(
        Component.literal(text[i].toString())
          .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)))
      )
    }
    return result
  }

  private fun animatedStopGradient(text: String, stops: List<Int>): MutableComponent {
    val result = Component.empty()
    val phase = currentGradientPhase()
    val length = (text.length - 1).coerceAtLeast(1)

    for (i in text.indices) {
      val position = i.toDouble() / length
      val color = sampleLoopingGradient(stops, position + phase)
      result.append(
        Component.literal(text[i].toString())
          .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)))
      )
    }

    return result
  }

  private fun chatGradientStops(): List<Int>? {
    return ThemeGradient.configuredStops()
      ?.map { color -> color and 0x00FFFFFF }
  }

  private fun sampleLoopingGradient(stops: List<Int>, position: Double): Int {
    val wrapped = positiveModulo(position, 1.0)
    val scaled = wrapped * stops.size
    val startIndex = floor(scaled).toInt().coerceIn(0, stops.lastIndex)
    val endIndex = (startIndex + 1) % stops.size
    val ratio = scaled - floor(scaled)
    return lerpRgb(stops[startIndex], stops[endIndex], ratio)
  }

  private fun prefix(): MutableComponent = Component.literal("[")
    .withStyle(ChatFormatting.DARK_GRAY)
    .append(glowingGradient("Cobalt", 0x3A9ED8, 0xB9F9FF))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private fun debugPrefix(): MutableComponent = Component.literal("[")
    .withStyle(ChatFormatting.DARK_GRAY)
    .append(glowingGradient("Cobalt Debug", 0x2ECF8A, 0xB9FFF1))
    .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))

  private fun lerpRgb(startRgb: Int, endRgb: Int, ratio: Double): Int {
    val t = ratio.coerceIn(0.0, 1.0)
    val sr = (startRgb shr 16) and 0xFF
    val sg = (startRgb shr 8) and 0xFF
    val sb = startRgb and 0xFF
    val er = (endRgb shr 16) and 0xFF
    val eg = (endRgb shr 8) and 0xFF
    val eb = endRgb and 0xFF
    val r = (sr + (er - sr) * t).roundToInt()
    val g = (sg + (eg - sg) * t).roundToInt()
    val b = (sb + (eb - sb) * t).roundToInt()
    return (r shl 16) or (g shl 8) or b
  }

  private fun lighten(rgb: Int, amount: Double): Int {
    val t = amount.coerceIn(0.0, 1.0)
    val r = (rgb shr 16) and 0xFF
    val g = (rgb shr 8) and 0xFF
    val b = rgb and 0xFF
    val nr = (r + (255 - r) * t).roundToInt()
    val ng = (g + (255 - g) * t).roundToInt()
    val nb = (b + (255 - b) * t).roundToInt()
    return (nr shl 16) or (ng shl 8) or nb
  }

  private fun currentGradientPhase(): Double =
    (System.currentTimeMillis() % 4200L).toDouble() / 4200.0

  private fun positiveModulo(value: Double, mod: Double): Double {
    val raw = value % mod
    return if (raw < 0.0) raw + mod else raw
  }

  private fun stripFormatting(text: String): String =
    text.replace(Regex("\u00A7."), "")

  private fun formattedSequenceString(sequence: FormattedCharSequence): String {
    val out = StringBuilder()
    sequence.accept { _, _, codePoint ->
      out.appendCodePoint(codePoint)
      true
    }
    return out.toString()
  }

  private fun readableHue(startRgb: Int, endRgb: Int): Double {
    val startSaturation = rgbSaturation(startRgb)
    return rgbHue(if (startSaturation < 0.25) endRgb else startRgb)
  }

  private fun rgbHue(rgb: Int): Double {
    val r = ((rgb shr 16) and 0xFF) / 255.0
    val g = ((rgb shr 8) and 0xFF) / 255.0
    val b = (rgb and 0xFF) / 255.0
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    if (delta <= 0.0001) return 0.52

    val hue = when (max) {
      r -> ((g - b) / delta) % 6.0
      g -> ((b - r) / delta) + 2.0
      else -> ((r - g) / delta) + 4.0
    }
    return positiveModulo(hue / 6.0, 1.0)
  }

  private fun rgbSaturation(rgb: Int): Double {
    val r = ((rgb shr 16) and 0xFF) / 255.0
    val g = ((rgb shr 8) and 0xFF) / 255.0
    val b = (rgb and 0xFF) / 255.0
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    return if (max <= 0.0001) 0.0 else (max - min) / max
  }

  private fun hsvToRgb(hue: Double, saturation: Double, value: Double): Int {
    val h = positiveModulo(hue, 1.0) * 6.0
    val s = saturation.coerceIn(0.0, 1.0)
    val v = value.coerceIn(0.0, 1.0)
    val c = v * s
    val x = c * (1.0 - kotlin.math.abs((h % 2.0) - 1.0))
    val m = v - c

    val sector = floor(h).toInt().coerceIn(0, 5)
    val (rp, gp, bp) = when (sector) {
      0 -> Triple(c, x, 0.0)
      1 -> Triple(x, c, 0.0)
      2 -> Triple(0.0, c, x)
      3 -> Triple(0.0, x, c)
      4 -> Triple(x, 0.0, c)
      else -> Triple(c, 0.0, x)
    }

    val r = ((rp + m) * 255.0).roundToInt().coerceIn(0, 255)
    val g = ((gp + m) * 255.0).roundToInt().coerceIn(0, 255)
    val b = ((bp + m) * 255.0).roundToInt().coerceIn(0, 255)
    return (r shl 16) or (g shl 8) or b
  }

}
