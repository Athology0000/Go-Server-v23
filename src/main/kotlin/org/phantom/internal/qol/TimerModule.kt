package org.phantom.internal.qol

import java.util.Locale
import net.minecraft.client.Minecraft
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.HudModuleManager
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.ui.theme.ThemeSurface
import org.phantom.api.util.ui.NVGRenderer

object TimerModule : Module("Timer") {

  override val category = ModuleCategory.QOL

  private val mc = Minecraft.getInstance()

  private val showLabel = CheckboxSetting(
    "Show Label",
    "Show the active timer label above the countdown.",
    true,
  )

  private val useTheme = CheckboxSetting(
    "Use Theme",
    "Use the active Phantom theme for the timer HUD colors.",
    true,
  )

  private val accentColor = ColorSetting(
    "Accent Color",
    "Progress bar color when theme colors are disabled.",
    0xFF55AAFF.toInt(),
  )

  private val timers = LinkedHashMap<String, TimerState>()

  val timerHud = hudElement("timer", "Timer", "Shows active countdown timers.") {
    anchor = HudAnchor.TOP_CENTER
    offsetY = 104f
    scale = 1f

    setting(showLabel)
    setting(useTheme)
    setting(accentColor)

    width { currentTimer()?.let(::hudWidth) ?: HUD_WIDTH }
    height { if (showLabel.value) 48f else 34f }

    render { x, y, _ ->
      val timer = currentTimer() ?: return@render
      val w = hudWidth(timer)
      val h = if (showLabel.value) 48f else 34f
      val theme = ThemeManager.currentTheme
      val accent = if (useTheme.value) theme.accent else accentColor.value
      val panel = if (useTheme.value) ThemeSurface.withAlpha(theme.panel, 0xE8) else 0xDD101014.toInt()
      val border = if (useTheme.value) theme.controlBorder else 0x6633333A
      val track = if (useTheme.value) ThemeSurface.withAlpha(theme.inset, 0xCC) else 0x88202026.toInt()
      val progress = timer.remainingMs().toFloat() / timer.durationMs.toFloat()
      val timerText = formatRemaining(timer.remainingMs())

      NVGRenderer.rect(x, y, w, h, panel, 6f)
      NVGRenderer.hollowRect(x, y, w, h, 1f, border, 6f)
      NVGRenderer.rect(x, y, 3f, h, accent, 3f)

      if (showLabel.value) {
        NVGRenderer.text(timer.label, x + 12f, y + 7f, 12f, theme.textSecondary)
        NVGRenderer.text(timerText, x + 12f, y + 22f, 16f, if (useTheme.value) theme.textPrimary else 0xFFFFFFFF.toInt())
        NVGRenderer.rect(x + 12f, y + 39f, w - 24f, 4f, track, 2f)
        NVGRenderer.rect(x + 12f, y + 39f, (w - 24f) * progress.coerceIn(0f, 1f), 4f, accent, 2f)
      } else {
        NVGRenderer.text(timerText, x + 12f, y + 7f, 16f, if (useTheme.value) theme.textPrimary else 0xFFFFFFFF.toInt())
        NVGRenderer.rect(x + 12f, y + 25f, w - 24f, 4f, track, 2f)
        NVGRenderer.rect(x + 12f, y + 25f, (w - 24f) * progress.coerceIn(0f, 1f), 4f, accent, 2f)
      }
    }
  }

  init {
    addSetting(showLabel, useTheme, accentColor)
  }

  override fun isVisibleInUi(): Boolean = false

  fun startTimer(id: String, label: String, durationMs: Long) {
    if (id.isBlank() || durationMs <= 0L) return
    val now = System.currentTimeMillis()
    synchronized(timers) {
      timers[id] = TimerState(
        id = id,
        label = label.ifBlank { "Timer" },
        startedAtMs = now,
        durationMs = durationMs,
      )
    }
  }

  fun startTimer(id: String, label: String, durationSeconds: Double) {
    startTimer(id, label, (durationSeconds * 1000.0).toLong())
  }

  fun cancelTimer(id: String) {
    synchronized(timers) {
      timers.remove(id)
    }
  }

  fun clearTimers() {
    synchronized(timers) {
      timers.clear()
    }
  }

  private fun currentTimer(): TimerState? {
    if (!HudModuleManager.isEditorOpen && (mc.player == null || mc.level == null)) return null
    val now = System.currentTimeMillis()
    synchronized(timers) {
      val expired = timers.filterValues { it.endAtMs <= now }.keys
      expired.forEach(timers::remove)
      val active = timers.values.minByOrNull { it.endAtMs }
      if (active != null) return active
    }
    return if (HudModuleManager.isEditorOpen) {
      TimerState("preview", "Timer Preview", now - 2_850L, 5_000L)
    } else {
      null
    }
  }

  private fun hudWidth(timer: TimerState): Float {
    val labelWidth = if (showLabel.value) NVGRenderer.textWidth(timer.label, 12f) else 0f
    val timeWidth = NVGRenderer.textWidth(formatRemaining(timer.remainingMs()), 16f)
    return maxOf(HUD_WIDTH, labelWidth + 24f, timeWidth + 24f).coerceAtMost(260f)
  }

  private fun formatRemaining(remainingMs: Long): String {
    val clamped = remainingMs.coerceAtLeast(0L)
    val seconds = clamped / 1000L
    val millis = clamped % 1000L
    return String.format(Locale.US, "%ds %03dms", seconds, millis)
  }

  private data class TimerState(
    val id: String,
    val label: String,
    val startedAtMs: Long,
    val durationMs: Long,
  ) {
    val endAtMs: Long = startedAtMs + durationMs

    fun remainingMs(): Long = endAtMs - System.currentTimeMillis()
  }

  private const val HUD_WIDTH = 154f
}
