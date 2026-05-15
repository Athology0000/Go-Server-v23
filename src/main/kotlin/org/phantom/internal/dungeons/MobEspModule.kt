package org.phantom.internal.dungeons

import java.awt.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Giant
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.inGroup
import org.phantom.api.util.render.Render3D

object MobEspModule : Module("Mob ESP") {

  override val category = ModuleCategory.COMBAT

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting("Enabled", "Highlight dungeon mobs with colored boxes.", false)

  private val renderModeSetting = ModeSetting(
    "Mode",
    "Box rendering style.",
    0,
    arrayOf("Filled Outline", "Filled", "Outline"),
  )

  private val showStarredSetting = CheckboxSetting("Starred Mobs", "Highlight starred dungeon mobs.", true)
  private val showBloodSetting = CheckboxSetting("Blood Mobs", "Highlight blood dungeon mobs.", false)
  private val showWitherSetting = CheckboxSetting("Withers", "Highlight the closest non-shield wither.", true)
  private val showBatsSetting = CheckboxSetting("Bats", "Highlight bats.", false)
  private val depthSetting = CheckboxSetting(
    "Depth Test",
    "Only render when the entity is visible. Disable to see through walls.",
    false,
  )

  private val starredFillSetting = ColorSetting("Star Fill", "Fill color for starred mobs.", 0x1A00FF00).inGroup(COLORS_GROUP)
  private val starredOutlineSetting = ColorSetting("Star Outline", "Outline color for starred mobs.", 0xFF00DCDC.toInt()).inGroup(COLORS_GROUP)
  private val bloodFillSetting = ColorSetting("Blood Fill", "Fill color for blood mobs.", 0x1AFF0000).inGroup(COLORS_GROUP)
  private val bloodOutlineSetting = ColorSetting("Blood Outline", "Outline color for blood mobs.", 0xFFFF0000.toInt()).inGroup(COLORS_GROUP)
  private val witherFillSetting = ColorSetting("Wither Fill", "Fill color for wither.", 0x1A1A1A1A).inGroup(COLORS_GROUP)
  private val witherOutlineSetting = ColorSetting("Wither Outline", "Outline color for wither.", 0xFF0055FF.toInt()).inGroup(COLORS_GROUP)
  private val batFillSetting = ColorSetting("Bat Fill", "Fill color for bats.", 0x5AAD5CAD).inGroup(COLORS_GROUP)
  private val batOutlineSetting = ColorSetting("Bat Outline", "Outline color for bats.", 0xFFAD5CAD.toInt()).inGroup(COLORS_GROUP)

  private val starredMobs = HashSet<Int>()
  private val bloodMobs = HashSet<Int>()
  private val batMobs = HashSet<Int>()
  private var witherEntityId = -1
  private var tickCount = 0

  private val bloodMobNames = setOf(
    "Revoker", "Psycho", "Reaper", "Cannibal", "Mute", "Ooze", "Putrid", "Freak",
    "Leech", "Tear", "Parasite", "Flamer", "Skull", "Mr. Dead", "Vader", "Frost",
    "Walker", "Wandering Soul", "Bonzo", "Scarf", "Livid", "Spirit Bear",
  )

  init {
    addSetting(
      enabledSetting,
      renderModeSetting,
      showStarredSetting,
      showBloodSetting,
      showWitherSetting,
      showBatsSetting,
      depthSetting,
      starredFillSetting,
      starredOutlineSetting,
      bloodFillSetting,
      bloodOutlineSetting,
      witherFillSetting,
      witherOutlineSetting,
      batFillSetting,
      batOutlineSetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabledSetting.value) return
    tickCount++
    if (tickCount % UPDATE_INTERVAL_TICKS != 0) return
    updateTrackedEntities()
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabledSetting.value) return
    val esp = !depthSetting.value
    if (showStarredSetting.value) renderGroup(event, starredMobs, starredFillSetting.value, starredOutlineSetting.value, esp)
    if (showBloodSetting.value) renderGroup(event, bloodMobs, bloodFillSetting.value, bloodOutlineSetting.value, esp)
    if (showBatsSetting.value) renderGroup(event, batMobs, batFillSetting.value, batOutlineSetting.value, esp)
    if (showWitherSetting.value && witherEntityId != -1) renderWitherIfPresent(event, esp)
  }

  private fun updateTrackedEntities() {
    val level = mc.level ?: return
    val player = mc.player ?: return
    starredMobs.clear()
    bloodMobs.clear()
    batMobs.clear()
    witherEntityId = -1
    var closestWitherDistSq = Double.MAX_VALUE

    for (entity in level.entitiesForRendering()) {
      when {
        showStarredSetting.value && entity is ArmorStand -> trackStarredMob(entity)
        showBloodSetting.value && entity is Player && entity.uuid != player.uuid -> trackBloodMob(entity)
        showBloodSetting.value && entity is Giant -> bloodMobs.add(entity.id)
        showBatsSetting.value && entity is Bat && !entity.isInvisible -> batMobs.add(entity.id)
        showWitherSetting.value && entity is WitherBoss && !entity.isInvisible -> {
          if (entity.maxHealth != WITHER_SHIELD_MAX_HEALTH) {
            val distSq = entity.distanceToSqr(player)
            if (distSq < closestWitherDistSq) {
              closestWitherDistSq = distSq
              witherEntityId = entity.id
            }
          }
        }
      }
    }
  }

  private fun trackStarredMob(stand: ArmorStand) {
    if (!isStarredStand(stand)) return
    getMobNearStand(stand)?.let { starredMobs.add(it.id) }
  }

  private fun trackBloodMob(entity: Player) {
    val name = ChatFormatting.stripFormatting(entity.name.string)?.trim() ?: return
    if (name in bloodMobNames) bloodMobs.add(entity.id)
  }

  private fun isStarredStand(stand: ArmorStand): Boolean {
    val raw = stand.customName?.string ?: return false
    val name = ChatFormatting.stripFormatting(raw)?.trim() ?: return false
    return name.contains("âœ¯ ") && name.endsWith("â¤")
  }

  private fun getMobNearStand(stand: ArmorStand): LivingEntity? {
    val level = mc.level ?: return null
    val searchBox = stand.boundingBox.move(0.0, -1.0, 0.0)
    return level.entitiesForRendering()
      .asSequence()
      .filter { it !== stand && it is LivingEntity && it !is ArmorStand && it !is LocalPlayer && it.boundingBox.intersects(searchBox) }
      .mapNotNull { it as? LivingEntity }
      .minByOrNull { it.distanceToSqr(stand.position()) }
  }

  private fun renderGroup(event: WorldRenderEvent.Last, ids: Set<Int>, fillArgb: Int, outlineArgb: Int, esp: Boolean) {
    val level = mc.level ?: return
    val dead = mutableListOf<Int>()
    for (id in ids) {
      val entity = level.getEntity(id)
      if (entity is LivingEntity && !entity.isDeadOrDying) {
        renderEntityBox(event, entity, fillArgb, outlineArgb, esp)
      } else {
        dead.add(id)
      }
    }
    (ids as HashSet).removeAll(dead.toSet())
  }

  private fun renderWitherIfPresent(event: WorldRenderEvent.Last, esp: Boolean) {
    val entity = mc.level?.getEntity(witherEntityId) ?: run { witherEntityId = -1; return }
    renderEntityBox(event, entity, witherFillSetting.value, witherOutlineSetting.value, esp)
  }

  private fun renderEntityBox(event: WorldRenderEvent.Last, entity: Entity, fillArgb: Int, outlineArgb: Int, esp: Boolean) {
    val pos = entity.position()
    val hw = entity.bbWidth / 2.0
    val h = entity.bbHeight.toDouble()
    val box = AABB(pos.x - hw, pos.y, pos.z - hw, pos.x + hw, pos.y + h, pos.z + hw)
    val fill = Color(fillArgb, true)
    val outline = Color(outlineArgb, true)
    when (renderModeSetting.value) {
      0 -> Render3D.drawStyledBox(event.context, box, outline, fill, esp)
      1 -> Render3D.drawStyledBox(event.context, box, Color(0, 0, 0, 0), fill, esp)
      else -> Render3D.drawStyledBox(event.context, box, outline, null, esp)
    }
  }

  private const val COLORS_GROUP = "Colours"
  private const val UPDATE_INTERVAL_TICKS = 10
  private const val WITHER_SHIELD_MAX_HEALTH = 300f
}
