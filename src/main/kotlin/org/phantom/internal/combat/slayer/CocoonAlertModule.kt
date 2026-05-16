package org.phantom.internal.combat.slayer

import java.util.Locale
import java.util.UUID
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
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
  private val onlyAlertSlayer = CheckboxSetting("Only Alert Slayer", "Only show alerts when the cocooned mob is a slayer boss or miniboss.", false)
  private val alertMessage = TextSetting("Boss Alert Message", "Text to show when a slayer boss is cocooned.", "Slayer boss cocooned!")
  private val minibossAlertMessage = TextSetting("Miniboss Alert Message", "Text to show when a slayer miniboss is cocooned.", "Slayer miniboss cocooned!")
  private val showTimer = CheckboxSetting("Show Timer HUD", "Show the currently cocooned mob and break timer.", true)
  private val highlightCocoon = CheckboxSetting("Highlight Cocoon", "Draw ESP boxes on the cocoon armor stands.", true)
  private val onlyHighlightSlayer = CheckboxSetting("Only Highlight Slayer", "Only draw cocoon ESP and trace lines for slayer bosses or minibosses.", false)
  private val traceLine = CheckboxSetting("Trace Line", "Draw a line from your crosshair to the cocoon.", true)
  private val highlightColor = ColorSetting("Highlight Color", "Color for the cocoon ESP and trace line.", 0xFFFF7777.toInt())

  private var timerEndMs = 0L
  private var cocoonedEntityId: UUID? = null
  private var cocoonedEntityName = "Slayer Boss"
  private var cocoonedEntityNormalizedName = ""
  private var cocoonedIsSlayerBoss = false
  private var cocoonedIsSlayerMiniboss = false

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
    addSetting(
      enabled,
      showAlert,
      onlyAlertSlayer,
      alertMessage,
      minibossAlertMessage,
      showTimer,
      highlightCocoon,
      onlyHighlightSlayer,
      traceLine,
      highlightColor,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val message = ChatFormatting.stripFormatting(event.message ?: "")?.trim().orEmpty()
    val cocoon = parseCocoonMessage(message) ?: return
    val normalizedMobName = normalizeName(cocoon.mobName)
    val isSlayerBoss = cocoon.isSlayerBoss || isSlayerBossName(normalizedMobName)
    val isSlayerMiniboss = !isSlayerBoss && isSlayerMinibossName(normalizedMobName)
    val isSlayerCocoon = isSlayerBoss || isSlayerMiniboss
    cocoonedIsSlayerBoss = isSlayerBoss
    cocoonedIsSlayerMiniboss = isSlayerMiniboss

    captureCocoonTarget(cocoon.mobName)
    timerEndMs = System.currentTimeMillis() + COCOON_DURATION_MS
    if (showAlert.value && (!onlyAlertSlayer.value || isSlayerCocoon)) {
      val text =
        when {
          isSlayerBoss -> alertMessage.value.ifBlank { "Slayer boss cocooned!" }
          isSlayerMiniboss -> minibossAlertMessage.value.ifBlank { "Slayer miniboss cocooned!" }
          else -> "${cocoon.mobName.ifBlank { "Mob" }} cocooned!"
        }
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
    val target = currentCocoonTarget()
    val cocoonAnchors = currentCocoonAnchors(target)
    if (cocoonAnchors.isEmpty()) return
    val color = colorFromSetting(highlightColor.value)
    val shouldHighlight = !onlyHighlightSlayer.value || cocoonedIsSlayerBoss || cocoonedIsSlayerMiniboss

    if (highlightCocoon.value && shouldHighlight) {
      for (anchor in cocoonAnchors) {
        Render3D.drawStyledBox(
          event.context,
          anchor.boundingBox.inflate(0.12, 0.12, 0.12),
          color,
          withAlpha(color, 70),
          esp = true,
          lineWidth = 2.8f,
        )
      }
      val center = cocoonCenter(cocoonAnchors)
      Render3D.drawWorldLabel(
        event.context,
        center.add(0.0, 0.85, 0.0),
        String.format(Locale.US, "%.1fs", (timerEndMs - System.currentTimeMillis()).coerceAtLeast(0L) / 1000.0),
        color,
        textScale = 1.2f,
      )
    }

    if (traceLine.value && shouldHighlight) {
      Render3D.drawLine(
        event.context,
        player.eyePosition,
        cocoonCenter(cocoonAnchors),
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
    val targetName = target?.let(::displayName)?.takeIf { isUsableDisplayName(it) }
    val name = targetName ?: cocoonedEntityName
    return CocoonHudState(name.ifBlank { "Slayer Boss" }, remaining)
  }

  private fun captureCocoonTarget(chatMobName: String) {
    cocoonedEntityNormalizedName = normalizeName(chatMobName)
    val target = resolveLikelyCocoonTarget()
    cocoonedEntityId = target?.uuid
    cocoonedEntityName = chatMobName.ifBlank {
      target?.let(::displayName)?.ifBlank { null } ?: "Slayer Boss"
    }
  }

  private fun currentCocoonTarget(): LivingEntity? {
    val level = mc.level ?: return null
    val id = cocoonedEntityId ?: return resolveLikelyCocoonTarget()?.also { cocoonedEntityId = it.uuid }
    val target = level.entitiesForRendering().firstOrNull { it.uuid == id } as? LivingEntity
    if (target != null && target.isAlive && target.health > 0f) return target

    return resolveLikelyCocoonTarget()?.also { cocoonedEntityId = it.uuid }
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
    val cocoonAnchors = findLikelyCocoonArmorStands()

    return candidates
      .map { it to targetScore(player, it, cocoonAnchors) }
      .filter { it.second > MIN_TARGET_SCORE }
      .maxByOrNull { it.second }
      ?.first
  }

  private fun targetScore(player: Player, target: LivingEntity, cocoonAnchors: List<ArmorStand>): Double {
    val names = targetNames(target)
    var score = 0.0
    score += cocoonAnchorScore(target, cocoonAnchors)
    if (cocoonedEntityNormalizedName.isNotBlank() && cocoonedEntityNormalizedName != "slayer boss") {
      if (names.any { it.contains(cocoonedEntityNormalizedName) || cocoonedEntityNormalizedName.contains(it) }) score += 12_000.0
    }
    if (cocoonedIsSlayerBoss && names.any(::isSlayerBossName)) score += 15_000.0
    if (cocoonedIsSlayerMiniboss && names.any(::isSlayerMinibossName)) score += 15_000.0
    if (names.any(::isSlayerBossName)) score += 10_000.0
    if (names.any(::isSlayerMinibossName)) score += 8_000.0
    if (names.any { it.contains("slayer") || it.contains("boss") }) score += 1_200.0
    if (names.any { it.contains("tarantula") || it.contains("brood") || it.contains("spider") }) score += 900.0
    score += facingScore(player, target)
    score += target.health.coerceAtLeast(0f).toDouble().coerceAtMost(2_000.0) * 0.02
    score -= target.distanceToSqr(player) * 3.0
    return score
  }

  private fun findLikelyCocoonArmorStands(): List<ArmorStand> {
    val player = mc.player ?: return emptyList()
    val level = mc.level ?: return emptyList()
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<ArmorStand>()
      .filter { it.isAlive }
      .filter { it.distanceToSqr(player) <= TARGET_CAPTURE_RANGE_SQ }
      .filter { isGenericArmorStand(it) }
      .toList()
  }

  private fun currentCocoonAnchors(target: LivingEntity?): List<ArmorStand> {
    val anchors = findLikelyCocoonArmorStands()
    if (target == null) return anchors
    return anchors
      .filter { stand ->
        stand.y >= target.y - 0.7 &&
          stand.y <= target.y + 5.0 &&
          horizontalDistSq(stand.x, stand.z, target.x, target.z) <= COCOON_ANCHOR_MOB_RANGE_SQ
      }
      .ifEmpty { anchors }
  }

  private fun cocoonCenter(anchors: List<ArmorStand>): Vec3 {
    if (anchors.isEmpty()) return mc.player?.eyePosition ?: Vec3.ZERO
    val sum = anchors.fold(Vec3.ZERO) { acc, stand -> acc.add(stand.boundingBox.center) }
    return sum.scale(1.0 / anchors.size.toDouble())
  }

  private fun cocoonAnchorScore(target: LivingEntity, anchors: List<ArmorStand>): Double {
    if (anchors.isEmpty()) return 0.0
    val bestAnchorDistanceSq = anchors
      .asSequence()
      .filter { stand -> stand.y >= target.y - 0.7 && stand.y <= target.y + 5.0 }
      .map { stand -> horizontalDistSq(stand.x, stand.z, target.x, target.z) }
      .minOrNull()
      ?: return 0.0

    if (bestAnchorDistanceSq > COCOON_ANCHOR_MOB_RANGE_SQ) return 0.0
    return 18_000.0 - bestAnchorDistanceSq * 1_400.0
  }

  private fun facingScore(player: Player, target: LivingEntity): Double {
    val toTarget = target.boundingBox.center.subtract(player.eyePosition)
    val distance = toTarget.length()
    if (distance <= 0.0001) return 0.0
    val dot = player.getViewVector(1.0f).normalize().dot(toTarget.normalize()).coerceIn(-1.0, 1.0)
    return dot * 1_000.0 - distance * 8.0
  }

  private fun targetNames(target: LivingEntity): List<String> {
    val names = LinkedHashSet<String>()
    normalizeName(target.name.string).takeIf { it.isNotBlank() }?.let(names::add)
    findAttachedArmorStandNames(target).map(::normalizeName).filter { it.isNotBlank() }.forEach(names::add)
    return names.toList()
  }

  private fun displayName(target: LivingEntity): String {
    val attached = findAttachedArmorStandNames(target)
      .filter { isUsableDisplayName(it) }
      .maxByOrNull { nameScore(normalizeName(it)) }
    val entityName = sanitizeName(target.name.string)
    return attached ?: entityName.takeIf(::isUsableDisplayName) ?: target.type.description.string.takeIf(::isUsableDisplayName).orEmpty()
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
      .mapNotNull { it.customName?.string }
      .map(::sanitizeName)
      .filter { it.isNotBlank() }
      .toList()
  }

  private fun nameScore(normalizedName: String): Int {
    var score = 0
    if (isSlayerBossName(normalizedName)) score += 2000
    if (isSlayerMinibossName(normalizedName)) score += 1600
    if (normalizedName.contains("tarantula") || normalizedName.contains("brood")) score += 800
    if (normalizedName.any(Char::isLetter)) score += 100
    if (isTransientName(normalizedName)) score -= 1000
    return score
  }

  private fun isSlayerBossName(normalizedName: String): Boolean =
    SLAYER_BOSS_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  private fun isSlayerMinibossName(normalizedName: String): Boolean =
    SLAYER_MINIBOSS_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  private fun isUsableDisplayName(name: String): Boolean {
    val normalizedName = normalizeName(name)
    return normalizedName.isNotBlank() &&
      normalizedName != "armor stand" &&
      !isTransientName(normalizedName)
  }

  private fun isGenericArmorStand(stand: ArmorStand): Boolean {
    val custom = stand.customName?.string?.let(::normalizeName).orEmpty()
    val entityName = normalizeName(stand.name.string)
    return custom.isBlank() || custom == "armor stand" || entityName == "armor stand"
  }

  private fun parseCocoonMessage(message: String): CocoonChatMessage? {
    if (message.equals(SLAYER_BOSS_COCOON_CHAT, ignoreCase = true)) {
      return CocoonChatMessage("Slayer Boss", true)
    }

    val match = COCOON_CHAT_PATTERN.matchEntire(message) ?: return null
    val mobName = sanitizeName(match.groupValues[1].removeSuffix("!"))
    return CocoonChatMessage(mobName, false)
  }

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
    cocoonedEntityNormalizedName = ""
    cocoonedIsSlayerBoss = false
    cocoonedIsSlayerMiniboss = false
  }

  private fun colorFromSetting(argb: Int): java.awt.Color =
    java.awt.Color(argb, true)

  private fun withAlpha(color: java.awt.Color, alpha: Int): java.awt.Color =
    java.awt.Color(color.red, color.green, color.blue, alpha.coerceIn(0, 255))

  private data class CocoonHudState(
    val mobName: String,
    val remainingMs: Long,
  )

  private data class CocoonChatMessage(
    val mobName: String,
    val isSlayerBoss: Boolean,
  )

  private const val COCOON_DURATION_MS = 6_000L
  private const val TARGET_CAPTURE_RANGE_SQ = 22.0 * 22.0
  private const val ATTACHED_NAME_RANGE_SQ = 2.7 * 2.7
  private const val COCOON_ANCHOR_MOB_RANGE_SQ = 3.8 * 3.8
  private const val MIN_TARGET_SCORE = -900.0
  private const val SLAYER_BOSS_COCOON_CHAT = "YOU COCOONED YOUR SLAYER BOSS"
  private val SLAYER_BOSS_KEYWORDS = SlayerDefinitions.all.flatMap { it.bossKeywords.asIterable() }.toTypedArray()
  private val EXTRA_SLAYER_MINIBOSS_KEYWORDS = arrayOf(
    "revenant sycophant",
    "revenant champion",
    "deformed revenant",
    "atoned champion",
    "atoned revenant",
    "sven follower",
    "sven alpha",
    "pack enforcer",
    "tarantula vermin",
    "tarantula beast",
    "mutant tarantula",
    "primordial jockey",
    "primordial viscount",
    "voidling devotee",
    "voidling radical",
    "voidcrazed maniac",
    "flare demon",
    "kindleheart demon",
    "burningsoul demon",
    "bloodfiend",
    "clotgoyle",
    "blood ichor",
  )
  private val SLAYER_MINIBOSS_KEYWORDS = (
    SlayerDefinitions.all.flatMap { definition ->
      definition.priorityKeywordsByMinTier.values.flatMap { it.asIterable() } +
        definition.highTierPriorityKeywordsByMinTier.values.flatMap { it.asIterable() }
    } + EXTRA_SLAYER_MINIBOSS_KEYWORDS
  ).distinct().toTypedArray()
  private val TRANSIENT_NAME_KEYWORDS = arrayOf("hits", "immune", "shield", "cocoon")
  private val COCOON_CHAT_PATTERN = Regex("^CAUGHT!\\s+You cocooned an?\\s+(.+)$", RegexOption.IGNORE_CASE)
  private val SANITIZE_LEVEL_PREFIX = Regex("^\\[[^\\]]+\\]\\s*")
  private val SANITIZE_SYMBOL_PREFIX = Regex("^[^A-Za-z0-9]+")
  private val SANITIZE_HP_SUFFIX = Regex("\\s+[0-9.,]+(?:/[0-9.,]+)?(?:[kKmMbB])?\\s*[â¤]?$")
  private val SANITIZE_WHITESPACE = Regex("\\s+")
}
