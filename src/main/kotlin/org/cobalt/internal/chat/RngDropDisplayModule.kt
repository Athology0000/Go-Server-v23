package org.cobalt.internal.chat

import java.util.ArrayDeque
import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.HudModuleManager
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer

object RngDropDisplayModule : Module("RNG Drop Display") {

  private data class DropEntry(
    val text: String,
    val severity: DropSeverity,
    val timestampMs: Long,
  )

  private enum class DropSeverity(
    val color: Int,
    val sound: SoundEvent,
    val pitch: Float,
  ) {
    PET(0xFFFFD166.toInt(), SoundEvents.ANVIL_LAND, 1.7f),
    INSANE(0xFFFF8A5B.toInt(), SoundEvents.ANVIL_LAND, 1.4f),
    CRAZY_RARE(0xFFF98CFF.toInt(), SoundEvents.NOTE_BLOCK_PLING.value(), 1.8f),
    VERY_RARE(0xFF7EDBFF.toInt(), SoundEvents.NOTE_BLOCK_PLING.value(), 1.55f),
    RNG(0xFFB2FF6A.toInt(), SoundEvents.NOTE_BLOCK_PLING.value(), 1.35f),
    RARE(0xFFFFFFFF.toInt(), SoundEvents.NOTE_BLOCK_PLING.value(), 1.15f),
  }

  private val mc = Minecraft.getInstance()
  private val entries = ArrayDeque<DropEntry>()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Track rare drop chat messages and show them in a HUD feed.",
    false,
  )

  private val notificationSetting = CheckboxSetting(
    "Notification",
    "Show a Dutt notification when a tracked drop appears.",
    true,
  ).inGroup("Feed")

  private val soundSetting = CheckboxSetting(
    "Sound",
    "Play a sound when a tracked drop appears.",
    true,
  ).inGroup("Feed")

  private val lifetimeSecondsSetting = SliderSetting(
    "Lifetime",
    "How long each drop stays visible in the HUD feed.",
    45.0,
    5.0,
    300.0,
    step = 5.0,
  ).inGroup("Feed")

  private val maxEntriesSetting = SliderSetting(
    "Max Entries",
    "Maximum number of recent drops to keep in the HUD feed.",
    5.0,
    1.0,
    10.0,
    step = 1.0,
  ).inGroup("Feed")

  init {
    addSetting(
      enabledSetting,
      notificationSetting,
      soundSetting,
      lifetimeSecondsSetting,
      maxEntriesSetting,
    )
    EventBus.register(this)
  }

  val rngHud = hudElement("rng-drop-feed", "RNG Drop Feed", "Shows recent rare drops from chat.") {
    anchor = HudAnchor.TOP_RIGHT
    offsetX = 16f
    offsetY = 48f

    width { computeWidth() }
    height { computeHeight() }

    render { _, _, _ ->
      val lines = visibleEntries()
      if (lines.isEmpty()) return@render

      val theme = ThemeManager.currentTheme
      val width = computeWidth()
      val height = computeHeight()
      val rowWidth = width - PADDING * 2f

      NVGRenderer.rect(0f, 0f, width, height, withAlpha(theme.panel, 212), 10f)
      NVGRenderer.hollowRect(0f, 0f, width, height, 1.1f, withAlpha(theme.accent, 88), 10f)
      NVGRenderer.text("RNG Drops", PADDING, PADDING - 1f, HEADER_SIZE, theme.textPrimary)
      NVGRenderer.rect(PADDING, HEADER_Y + 14f, rowWidth, 1f, withAlpha(theme.accentSecondary, 72))

      var y = HEADER_Y + 22f
      lines.forEach { entry ->
        val age = formatAge(entry.timestampMs)
        val ageWidth = NVGRenderer.textWidth(age, AGE_SIZE)
        val severityWidth = NVGRenderer.textWidth(severityLabel(entry.severity), ROW_SIZE)
        val availableWidth = max(40f, rowWidth - severityWidth - ageWidth - 16f)
        val message = ellipsize(entry.text, availableWidth, ROW_SIZE)

        NVGRenderer.text(severityLabel(entry.severity), PADDING, y, ROW_SIZE, entry.severity.color)
        NVGRenderer.text(
          message,
          PADDING + severityWidth + 8f,
          y,
          ROW_SIZE,
          theme.text,
        )
        NVGRenderer.text(
          age,
          width - PADDING - ageWidth,
          y,
          AGE_SIZE,
          theme.textSecondary,
        )
        y += ROW_HEIGHT
      }
    }
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    val raw = event.message ?: return
    val severity = detectSeverity(raw) ?: return

    pruneExpired()
    val normalized = normalizeMessage(raw)
    if (!enabledSetting.value) return

    val entry = DropEntry(normalized, severity, System.currentTimeMillis())
    entries.addFirst(entry)
    trimToLimit()

    if (notificationSetting.value) {
      NotificationManager.queue("RNG Drop", normalized, 4500L)
    }

    if (soundSetting.value) {
      val player = mc.player ?: return
      mc.level?.playLocalSound(
        player.x,
        player.y,
        player.z,
        severity.sound,
        SoundSource.PLAYERS,
        1.0f,
        severity.pitch,
        false,
      )
    }
  }

  private fun visibleEntries(): List<DropEntry> {
    pruneExpired()
    if (entries.isEmpty()) {
      return if (HudModuleManager.isEditorOpen) sampleEntries() else emptyList()
    }
    return entries.toList()
  }

  private fun sampleEntries(): List<DropEntry> {
    val now = System.currentTimeMillis()
    return listOf(
      DropEntry("PET DROP! Baby Yeti", DropSeverity.PET, now - 11_000L),
      DropEntry("CRAZY RARE DROP! Giant's Sword", DropSeverity.CRAZY_RARE, now - 28_000L),
    )
  }

  private fun computeWidth(): Float {
    val lines = visibleEntries()
    val headerWidth = NVGRenderer.textWidth("RNG Drops", HEADER_SIZE)
    val rowWidth = lines.maxOfOrNull(::rowWidth) ?: NVGRenderer.textWidth("No recent drops", ROW_SIZE)
    return max(220f, max(headerWidth, rowWidth) + PADDING * 2f).coerceAtMost(420f)
  }

  private fun computeHeight(): Float {
    val lines = visibleEntries()
    if (lines.isEmpty()) return 0f
    return HEADER_Y + 18f + lines.size * ROW_HEIGHT + PADDING
  }

  private fun rowWidth(entry: DropEntry): Float {
    val severityWidth = NVGRenderer.textWidth(severityLabel(entry.severity), ROW_SIZE)
    val ageWidth = NVGRenderer.textWidth(formatAge(entry.timestampMs), AGE_SIZE)
    val messageWidth = NVGRenderer.textWidth(entry.text, ROW_SIZE)
    return severityWidth + messageWidth + ageWidth + 24f
  }

  private fun severityLabel(severity: DropSeverity): String =
    when (severity) {
      DropSeverity.PET -> "PET"
      DropSeverity.INSANE -> "INSANE"
      DropSeverity.CRAZY_RARE -> "CRAZY"
      DropSeverity.VERY_RARE -> "V.RARE"
      DropSeverity.RNG -> "RNG"
      DropSeverity.RARE -> "RARE"
    }

  private fun detectSeverity(message: String): DropSeverity? {
    val normalized = normalizeMessage(message).uppercase()
    return when {
      normalized.contains("PET DROP!") -> DropSeverity.PET
      normalized.contains("INSANE DROP!") -> DropSeverity.INSANE
      normalized.contains("CRAZY RARE DROP!") -> DropSeverity.CRAZY_RARE
      normalized.contains("VERY RARE DROP!") -> DropSeverity.VERY_RARE
      normalized.contains("RNG DROP!") -> DropSeverity.RNG
      normalized.contains("RARE DROP!") -> DropSeverity.RARE
      else -> null
    }
  }

  private fun normalizeMessage(message: String): String {
    val stripped = ChatFormatting.stripFormatting(message) ?: message
    return stripped.replace(Regex("\\s+"), " ").trim()
  }

  private fun formatAge(timestampMs: Long): String {
    val seconds = ((System.currentTimeMillis() - timestampMs) / 1000L).coerceAtLeast(0L)
    return "${seconds}s"
  }

  private fun pruneExpired() {
    val maxAgeMs = (lifetimeSecondsSetting.value * 1000.0).toLong()
    val now = System.currentTimeMillis()
    while (entries.isNotEmpty() && now - entries.last().timestampMs > maxAgeMs) {
      entries.removeLast()
    }
    trimToLimit()
  }

  private fun trimToLimit() {
    val maxEntries = maxEntriesSetting.value.toInt().coerceAtLeast(1)
    while (entries.size > maxEntries) {
      entries.removeLast()
    }
  }

  private fun ellipsize(text: String, maxWidth: Float, size: Float): String {
    if (NVGRenderer.textWidth(text, size) <= maxWidth) return text
    for (length in text.length - 1 downTo 1) {
      val candidate = text.substring(0, length).trimEnd() + "..."
      if (NVGRenderer.textWidth(candidate, size) <= maxWidth) return candidate
    }
    return "..."
  }

  private fun withAlpha(color: Int, alpha: Int): Int {
    return (alpha.coerceIn(0, 255) shl 24) or (color and 0x00FFFFFF)
  }

  private const val PADDING = 10f
  private const val HEADER_Y = 11f
  private const val HEADER_SIZE = 13f
  private const val ROW_SIZE = 10.5f
  private const val AGE_SIZE = 9.5f
  private const val ROW_HEIGHT = 16f
}
