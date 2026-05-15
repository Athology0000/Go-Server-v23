package org.phantom.internal.combat.slayer

import java.util.Locale
import java.util.UUID
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.notification.NotificationManager
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.render.Render3D
import org.phantom.api.util.ui.NVGRenderer

object CocoonAlertModule : Module("Cocoon Alert") {

  override val category = ModuleCategory.COMBAT

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting("Enabled", "Alert when you cocoon your slayer boss.", false)
  private val showAlert = CheckboxSetting("Show Alert", "Show chat, title, and notification alerts.", true)
  private val alertMessage = TextSetting("Alert Message", "Text to show when the boss is cocooned.", "Boss cocooned!")
  private val showTimer = CheckboxSetting("Show Timer HUD", "Show the currently cocooned mob and break timer.", true)
  private val highlightCocoon = CheckboxSetting("Highlight Cocoon", "Draw an ESP box on the currently cocooned mob.", true)
  private val traceLine = CheckboxSetting("Trace Line", "Draw a line from your crosshair to the cocooned mob.", true)
  private val highlightColor = ColorSetting("Highlight Color", "Color for the cocoon ESP and trace line.", 0xFFFF7777.toInt())

  private var timerEndMs = 0L
  private var cocoonedEntityId: UUID? = null
  private var cocoonedEntityName = "Slayer Boss"

  val cocoonHud = hudElement("cocoon-timer", "Cocoon Timer", "Shows the cocooned mob and break timer.") {
    anchor = HudAnchor.CENTER
    offsetY = 58f
    scale = 1f

    width { currentCocoonState()?.let { state -> hudWidth(state) } ?: 190f }
    height { 54f }

    render { x, y, _ ->
      val state = currentCocoonState() ?: return@render
      val w = hudWidth(state)
      val theme = ThemeManager.currentTheme
      val accent = highlightColor.value
      val progress = state.remainingMs.toFloat() / COCOON_DURATION_MS.toFloat()
      NVGRenderer.rect(x, y, w, 54f, theme.overlay, 5f)
      NVGRenderer.rect(x, y, 4f, 54f, accent, 5f)
      NVGRenderer.text("Cocooned", x + 14f, y + 7f, 12f, theme.textSecondary)
      NVGRenderer.text(state.mobName, x + 14f, y + 22f, 15f, theme.textPrimary)
      NVGRenderer.text(String.format(Locale.US, "%.1fs", state.remainingMs / 1000.0), x + w - 54f, y + 22f, 15f, accent)
      NVGRenderer.rect(x + 14f, y + 43f, w - 28f, 4f, theme.panel, 2f)
      NVGRenderer.rect(x + 14f, y + 43f, (w - 28f) * progress.coerceIn(0f, 1f), 4f, accent, 2f)
    }
  }

  init {
    addSetting(enabled, showAlert, alertMessage, showTimer, highlightCocoon, traceLine, highlightColor)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val message = ChatFormatting.stripFormatting(event.message ?: "")?.trim().orEmpty()
    if (!message.equals("YOU COCOONED YOUR SLAYER BOSS", ignoreCase = true)) return

    captureCocoonTarget()
    timerEndMs = System.currentTimeMillis() + COCOON_DURATION_MS
    if (showAlert.value) {
      val text = alertMessage.value.ifBlank { "Boss cocooned!" }
      ChatUtils.sendMessage(text)
      NotificationManager.queue("Cocoon Alert", text, 2500L)
      mc.gui.setSubtitle(Component.empty())
      mc.gui.setTitle(Component.literal(text).withStyle(ChatFormatting.RED))
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value || timerEndMs <= 0L) return
    val player = mc.player ?: return
    val target = currentCocoonTarget() ?: return
    val color = colorFromSetting(highlightColor.value)

    if (highlightCocoon.value) {
      Render3D.drawStyledBox(
        event.context,
        target.boundingBox.inflate(0.12, 0.08, 0.12),
        color,
        withAlpha(color, 70),
        esp = true,
        lineWidth = 2.8f,
      )
      Render3D.drawWorldLabel(
        event.context,
        target.boundingBox.center.add(0.0, target.bbHeight * 0.58 + 0.55, 0.0),
        String.format(Locale.US, "%.1fs", (timerEndMs - System.currentTimeMillis()).coerceAtLeast(0L) / 1000.0),
        color,
        textScale = 1.2f,
      )
    }

    if (traceLine.value) {
      Render3D.drawLine(
        event.context,
        player.eyePosition,
        target.boundingBox.center,
        color,
        esp = true,
        thickness = 2.0f,
      )
    }
  }

  private fun currentCocoonState(): CocoonHudState? {
    if (!enabled.value || !showTimer.value || timerEndMs <= 0L || mc.player == null || mc.level == null) return null
    val remaining = timerEndMs - System.currentTimeMillis()
    if (remaining <= 0L) {
      clearCocoon()
      return null
    }
    val target = currentCocoonTarget()
    val name = target?.let(::displayName) ?: cocoonedEntityName
    return CocoonHudState(name.ifBlank { "Slayer Boss" }, remaining)
  }

  private fun captureCocoonTarget() {
    val target = resolveLikelyCocoonTarget()
    cocoonedEntityId = target?.uuid
    cocoonedEntityName = target?.let(::displayName)?.ifBlank { null } ?: "Slayer Boss"
  }

  private fun currentCocoonTarget(): LivingEntity? {
    val level = mc.level ?: return null
    val id = cocoonedEntityId ?: return null
    val target = level.entitiesForRendering().firstOrNull { it.uuid == id } as? LivingEntity ?: return null
    if (!target.isAlive || target.health <= 0f) return null
    return target
  }

  private fun resolveLikelyCocoonTarget(): LivingEntity? {
    val player = mc.player ?: return null
    val level = mc.level ?: return null

    val candidates = level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<LivingEntity>()
      .filter { it !== player }
      .filter { it !is ArmorStand }
      .filter { it !is Player }
      .filter { it.isAlive && it.health > 0f }
      .filter { it.distanceToSqr(player) <= TARGET_CAPTURE_RANGE_SQ }
      .toList()

    return candidates.maxByOrNull { targetScore(player, it) }
  }

  private fun targetScore(player: Player, target: LivingEntity): Double {
    val names = targetNames(target)
    var score = 0.0
    if (names.any(::isSpiderBossName)) score += 10_000.0
    if (names.any { it.contains("slayer") || it.contains("boss") }) score += 1_200.0
    if (names.any { it.contains("tarantula") || it.contains("brood") || it.contains("spider") }) score += 900.0
    score += target.health.coerceAtLeast(0f).toDouble().coerceAtMost(2_000.0) * 0.02
    score -= target.distanceToSqr(player) * 3.0
    return score
  }

  private fun targetNames(target: LivingEntity): List<String> {
    val names = LinkedHashSet<String>()
    normalizeName(target.name.string).takeIf { it.isNotBlank() }?.let(names::add)
    findAttachedArmorStandNames(target).map(::normalizeName).filter { it.isNotBlank() }.forEach(names::add)
    return names.toList()
  }

  private fun displayName(target: LivingEntity): String {
    val attached = findAttachedArmorStandNames(target)
      .filter { normalizeName(it).let { name -> name.isNotBlank() && !isTransientName(name) } }
      .maxByOrNull { nameScore(normalizeName(it)) }
    return attached ?: sanitizeName(target.name.string).ifBlank { target.type.description.string }
  }

  private fun findAttachedArmorStandNames(target: LivingEntity): List<String> {
    val level = mc.level ?: return emptyList()
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<ArmorStand>()
      .filter { it.isAlive }
      .filter { stand ->
        stand.y >= target.y - 0.5 &&
          stand.y <= target.y + 3.6 &&
          horizontalDistSq(stand.x, stand.z, target.x, target.z) <= ATTACHED_NAME_RANGE_SQ
      }
      .sortedBy { stand -> horizontalDistSq(stand.x, stand.z, target.x, target.z) + kotlin.math.abs(stand.y - target.y) }
      .map { sanitizeName(it.name.string) }
      .filter { it.isNotBlank() }
      .toList()
  }

  private fun nameScore(normalizedName: String): Int {
    var score = 0
    if (isSpiderBossName(normalizedName)) score += 2000
    if (normalizedName.contains("tarantula") || normalizedName.contains("brood")) score += 800
    if (normalizedName.any(Char::isLetter)) score += 100
    if (isTransientName(normalizedName)) score -= 1000
    return score
  }

  private fun isSpiderBossName(normalizedName: String): Boolean =
    SPIDER_BOSS_KEYWORDS.any { normalizedName.contains(it) }

  private fun isTransientName(normalizedName: String): Boolean =
    TRANSIENT_NAME_KEYWORDS.any { normalizedName.contains(it) }

  private fun sanitizeName(raw: String): String {
    return ChatFormatting.stripFormatting(raw).orEmpty()
      .replace(SANITIZE_LEVEL_PREFIX, "")
      .replace(SANITIZE_SYMBOL_PREFIX, "")
      .replace(SANITIZE_HP_SUFFIX, "")
      .replace(SANITIZE_WHITESPACE, " ")
      .trim()
  }

  private fun normalizeName(raw: String): String =
    sanitizeName(raw).lowercase(Locale.ROOT)

  private fun horizontalDistSq(ax: Double, az: Double, bx: Double, bz: Double): Double {
    val dx = ax - bx
    val dz = az - bz
    return dx * dx + dz * dz
  }

  private fun hudWidth(state: CocoonHudState): Float {
    val labelWidth = NVGRenderer.textWidth(state.mobName, 15f)
    return (labelWidth + 88f).coerceIn(190f, 320f)
  }

  private fun clearCocoon() {
    timerEndMs = 0L
    cocoonedEntityId = null
    cocoonedEntityName = "Slayer Boss"
  }

  private fun colorFromSetting(argb: Int): java.awt.Color =
    java.awt.Color(argb, true)

  private fun withAlpha(color: java.awt.Color, alpha: Int): java.awt.Color =
    java.awt.Color(color.red, color.green, color.blue, alpha.coerceIn(0, 255))

  private data class CocoonHudState(
    val mobName: String,
    val remainingMs: Long,
  )

  private const val COCOON_DURATION_MS = 6_000L
  private const val TARGET_CAPTURE_RANGE_SQ = 22.0 * 22.0
  private const val ATTACHED_NAME_RANGE_SQ = 2.7 * 2.7
  private val SPIDER_BOSS_KEYWORDS = arrayOf("tarantula broodfather", "conjoined brood")
  private val TRANSIENT_NAME_KEYWORDS = arrayOf("hits", "immune", "shield", "cocoon")
  private val SANITIZE_LEVEL_PREFIX = Regex("^\\[[^\\]]+\\]\\s*")
  private val SANITIZE_SYMBOL_PREFIX = Regex("^[^A-Za-z0-9]+")
  private val SANITIZE_HP_SUFFIX = Regex("\\s+[0-9.,]+(?:/[0-9.,]+)?(?:[kKmMbB])?\\s*[â¤]?$")
  private val SANITIZE_WHITESPACE = Regex("\\s+")
}
