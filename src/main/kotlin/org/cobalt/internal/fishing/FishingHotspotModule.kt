package org.cobalt.internal.fishing

import java.awt.Color
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.render.Render3D
import org.joml.Vector3f

object FishingHotspotModule : Module("Fishing Hotspot") {

  override val category = ModuleCategory.FARMING

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Track and render fishing hotspots exactly like the SkyOcean feature.",
    false,
  )

  private val warningSetting = CheckboxSetting(
    "Warning",
    "Warn when a recently-fished hotspot despawns nearby.",
    false,
  )

  private val circleSurfaceSetting = CheckboxSetting(
    "Circle Surface",
    "Render the hotspot surface circle.",
    true,
  ).inGroup("Highlight")

  private val circleOutlineSetting = CheckboxSetting(
    "Circle Outline",
    "Render the hotspot outline cylinder.",
    true,
  ).inGroup("Highlight")

  private val surfaceTransparencySetting = SliderSetting(
    "Surface Transparency",
    "Alpha used for the hotspot surface circle.",
    50.0,
    0.0,
    255.0,
    step = 1.0,
  ).inGroup("Highlight")

  private val outlineTransparencySetting = SliderSetting(
    "Outline Transparency",
    "Alpha used for the hotspot outline cylinder.",
    100.0,
    0.0,
    255.0,
    step = 1.0,
  ).inGroup("Highlight")

  private val hotspotsById = LinkedHashMap<Int, HotspotData>()

  private var lastHotspotFishAtMs = 0L
  private var lastBiteAtMs = 0L
  private var lastBiteHookPos: Vec3? = null

  init {
    addSetting(
      enabledSetting,
      warningSetting,
      circleSurfaceSetting,
      circleOutlineSetting,
      surfaceTransparencySetting,
      outlineTransparencySetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabledSetting.value) return

    val level = mc.level ?: run {
      clearState()
      return
    }

    val visibleHotspots = HashSet<Int>()
    for (entity in level.entitiesForRendering()) {
      val stand = entity as? ArmorStand ?: continue
      if (!stand.isAlive) continue

      val rawName = stand.customName?.string ?: stand.name.string
      val type = HotspotType.match(rawName) ?: continue
      visibleHotspots += stand.id

      val hotspot = hotspotsById.getOrPut(stand.id) { HotspotData(id = stand.id, type = type) }
      hotspot.type = type
      hotspot.pos = resolveHotspotSurface(stand.position()) ?: hotspot.pos ?: stand.position()
    }

    val iterator = hotspotsById.entries.iterator()
    while (iterator.hasNext()) {
      val (_, hotspot) = iterator.next()
      if (hotspot.id in visibleHotspots) continue
      iterator.remove()
      handleHotspotDespawn(hotspot)
    }
  }

  @SubscribeEvent
  fun onIncomingPacket(event: PacketEvent.Incoming) {
    if (event.packet is ClientboundRespawnPacket) {
      clearState()
      return
    }

    if (!enabledSetting.value) return

    when (val packet = event.packet) {
      is ClientboundLevelParticlesPacket -> handleParticlePacket(event, packet)
      is ClientboundSoundPacket -> handleSplashPacket(packet)
    }
  }

  @SubscribeEvent
  fun onOutgoingPacket(event: PacketEvent.Outgoing) {
    if (!enabledSetting.value) return

    val packet = event.packet as? ServerboundUseItemPacket ?: return
    val player = mc.player ?: return
    val hook = resolveOwnedHook() ?: return
    if (System.currentTimeMillis() - lastBiteAtMs > CATCH_REEL_WINDOW_MS) return
    if (player.getItemInHand(packet.hand).item !is FishingRodItem) return

    val bitePos = lastBiteHookPos
    if (bitePos != null && hook.position().distanceToSqr(bitePos) > MAX_REEL_HOOK_DRIFT_SQ) return

    markNearbyHotspotFished(hook.position())
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabledSetting.value || !shouldRenderHotspots()) return

    for (hotspot in hotspotsById.values) {
      val pos = hotspot.pos ?: continue
      val radius = hotspot.radius ?: DEFAULT_HOTSPOT_RADIUS

      if (circleOutlineSetting.value) {
        Render3D.drawCircleOutline(
          event.context,
          pos,
          radius.toFloat(),
          hotspot.type.outlineColor(outlineTransparencySetting.value.toInt()),
        )
      }

      if (circleSurfaceSetting.value) {
        Render3D.drawCircleSurface(
          event.context,
          pos,
          radius.toFloat(),
          hotspot.type.surfaceColor(surfaceTransparencySetting.value.toInt()),
        )
      }
    }
  }

  private fun handleParticlePacket(event: PacketEvent.Incoming, packet: ClientboundLevelParticlesPacket) {
    if (!packet.isHotspotParticle()) return

    val maxHotspotRadiusSq =
      if (packet.particle.type == ParticleTypes.SMOKE) {
        25.5
      } else {
        9.5
      }

    val match =
      hotspotsById.values
        .asSequence()
        .mapNotNull { hotspot ->
          val pos = hotspot.pos ?: return@mapNotNull null
          val distanceSq = (packet.x - pos.x).pow(2) + (packet.z - pos.z).pow(2)
          if (distanceSq > maxHotspotRadiusSq) return@mapNotNull null
          hotspot to distanceSq
        }
        .minByOrNull { it.second } ?: return

    match.first.radius = roundToHalf(sqrt(match.second))
    if (shouldRenderHotspots()) {
      event.setCancelled(true)
    }
  }

  private fun handleSplashPacket(packet: ClientboundSoundPacket) {
    if (packet.sound.value() != SoundEvents.FISHING_BOBBER_SPLASH) return

    val hook = resolveOwnedHook() ?: return
    if (hook.tickCount < MIN_SPLASH_HOOK_AGE_TICKS) return

    val dx = packet.x - hook.x
    val dy = packet.y - hook.y
    val dz = packet.z - hook.z
    if ((dx * dx) + (dy * dy) + (dz * dz) > SPLASH_TRACK_RADIUS_SQ) return

    lastBiteAtMs = System.currentTimeMillis()
    lastBiteHookPos = hook.position()
  }

  private fun handleHotspotDespawn(hotspot: HotspotData) {
    if (!warningSetting.value) return
    if (!hotspot.fishedIn) return
    if (System.currentTimeMillis() - lastHotspotFishAtMs > HOTSPOT_WARNING_WINDOW_MS) return

    val player = mc.player ?: return
    val pos = hotspot.pos ?: return
    if (player.position().distanceToSqr(pos) > HOTSPOT_WARNING_DISTANCE_SQ) return

    ChatUtils.sendMessage("${hotspot.type.displayName} Hotspot despawned!")
    mc.gui.setTimes(10, 60, 10)
    mc.gui.setSubtitle(Component.empty())
    mc.gui.setTitle(Component.literal("Hotspot despawned!").withStyle(ChatFormatting.GOLD))
  }

  private fun markNearbyHotspotFished(hookPos: Vec3) {
    val hotspot =
      hotspotsById.values
        .asSequence()
        .filter { hotspot ->
          val pos = hotspot.pos ?: return@filter false
          abs(hookPos.y - pos.y) < 3.0
        }
        .minByOrNull { hotspot ->
          val pos = hotspot.pos ?: return@minByOrNull Double.MAX_VALUE
          hookPos.distanceToSqr(pos)
        } ?: return

    hotspot.fishedIn = true
    lastHotspotFishAtMs = System.currentTimeMillis()
  }

  private fun resolveHotspotSurface(origin: Vec3): Vec3? {
    val level = mc.level ?: return null
    val blockPos = BlockPos.containing(origin)

    for (offset in 0..HOTSPOT_SURFACE_SEARCH_DEPTH) {
      val candidate = blockPos.below(offset)
      val fluid = level.getFluidState(candidate)
      if (fluid.isEmpty) continue
      return Vec3(origin.x, candidate.y + fluid.getHeight(level, candidate).toDouble(), origin.z)
    }

    return null
  }

  private fun resolveOwnedHook(): FishingHook? {
    val player = mc.player ?: return null
    val hook = player.fishing ?: return null
    if (!hook.isAlive) return null
    return if (hook.playerOwner?.uuid == player.uuid) hook else null
  }

  private fun shouldRenderHotspots(): Boolean {
    return circleSurfaceSetting.value || circleOutlineSetting.value
  }

  fun nearestHotspotPos(from: Vec3): Vec3? =
    hotspotsById.values
      .asSequence()
      .mapNotNull { it.pos }
      .minByOrNull { it.distanceToSqr(from) }

  private fun clearState() {
    hotspotsById.clear()
    lastHotspotFishAtMs = 0L
    lastBiteAtMs = 0L
    lastBiteHookPos = null
  }

  private fun stripFormatting(text: String): String {
    return (ChatFormatting.stripFormatting(text) ?: text).trim()
  }

  private fun normalizeHotspotName(text: String): String {
    return buildString(text.length) {
      for (char in stripFormatting(text).lowercase(Locale.US)) {
        append(if (char.isLetterOrDigit()) char else ' ')
      }
    }.replace(WHITESPACE_REGEX, " ").trim()
  }

  private fun roundToHalf(value: Double): Double {
    return (value * 2.0).roundToInt() / 2.0
  }

  private fun ClientboundLevelParticlesPacket.isHotspotParticle(): Boolean {
    if (particle.type == ParticleTypes.SMOKE) {
      return count in 1..5
    }

    val options = particle as? DustParticleOptions ?: return false
    return sameColor(options.color, HOTSPOT_PARTICLE_COLOR)
  }

  private fun sameColor(left: Vector3f, right: Vector3f): Boolean {
    return abs(left.x - right.x) <= COLOR_EPSILON &&
      abs(left.y - right.y) <= COLOR_EPSILON &&
      abs(left.z - right.z) <= COLOR_EPSILON
  }

  private data class HotspotData(
    val id: Int,
    var type: HotspotType,
    var pos: Vec3? = null,
    var radius: Double? = null,
    var fishedIn: Boolean = false,
  )

  private enum class HotspotType(
    val displayName: String,
    val colorRgb: Int,
    vararg val markers: String,
  ) {
    SEA_CREATURE("Sea Creature", 0x00AAAA, "sea creature chance"),
    FISHING_SPEED("Fishing Speed", 0x55FFFF, "fishing speed"),
    DOUBLE_HOOK("Double Hook", 0x5555FF, "double hook chance"),
    TREASURE("Treasure", 0xFFAA00, "treasure chance"),
    TROPHY_FISH("Trophy Fish", 0xFFAA00, "trophy fish chance"),
    ;

    fun surfaceColor(alpha: Int): Color = colorWithAlpha(colorRgb, alpha)
    fun outlineColor(alpha: Int): Color = colorWithAlpha(colorRgb, alpha)

    companion object {
      fun match(input: String): HotspotType? {
        val normalized = FishingHotspotModule.normalizeHotspotName(input)
        return entries.firstOrNull { type -> type.markers.any { normalized.contains(it) } }
      }
    }
  }

  private val HOTSPOT_PARTICLE_COLOR = Vector3f(1.0f, 0.4117647f, 0.7058824f)
  private val WHITESPACE_REGEX = Regex("\\s+")

  private const val MIN_SPLASH_HOOK_AGE_TICKS = 8
  private const val DEFAULT_HOTSPOT_RADIUS = 3.0
  private const val HOTSPOT_WARNING_WINDOW_MS = 30_000L
  private const val HOTSPOT_WARNING_DISTANCE_SQ = 40.0 * 40.0
  private const val CATCH_REEL_WINDOW_MS = 3_000L
  private const val SPLASH_TRACK_RADIUS_SQ = 2.2 * 2.2
  private const val MAX_REEL_HOOK_DRIFT_SQ = 9.0
  private const val HOTSPOT_OUTLINE_HEIGHT = 0.1f
  private const val HOTSPOT_SURFACE_SEARCH_DEPTH = 8
  private const val COLOR_EPSILON = 0.02f

  private fun colorWithAlpha(rgb: Int, alpha: Int): Color {
    return Color((alpha.coerceIn(0, 255) shl 24) or rgb, true)
  }
}
