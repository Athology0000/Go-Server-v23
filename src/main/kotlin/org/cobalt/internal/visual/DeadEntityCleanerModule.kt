package org.cobalt.internal.visual

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting

object DeadEntityCleanerModule : Module("Dead Entity Cleaner") {

  override val category = ModuleCategory.VISUAL

  private val enabled = CheckboxSetting(
    "Enabled",
    "Hide or remove dying entities client-side for better performance.",
    false,
  )

  private val action = ModeSetting(
    "Action",
    "Hide only denies rendering; remove also discards matching entities client-side.",
    1,
    arrayOf("Hide", "Remove"),
  )

  private val deathTicks = SliderSetting(
    "Death Ticks",
    "Minimum death animation ticks before an entity is hidden or removed.",
    1.0,
    0.0,
    20.0,
    1.0,
  )

  private val removeMobs = CheckboxSetting(
    "Mobs",
    "Clean dead mobs.",
    true,
  )

  private val removePlayers = CheckboxSetting(
    "Players",
    "Clean dead player entities except yourself.",
    false,
  )

  private val removeArmorStands = CheckboxSetting(
    "Armor Stands",
    "Clean dead armor stands. Keep off on Hypixel if armor stands are used for displays.",
    false,
  )

  private val requireHealthZero = CheckboxSetting(
    "Require 0 Health",
    "Only clean living entities whose health is zero or below.",
    false,
  )

  init {
    addSetting(
      enabled,
      action,
      deathTicks,
      removeMobs,
      removePlayers,
      removeArmorStands,
      requireHealthZero,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value || action.value != ACTION_REMOVE) return
    val level = Minecraft.getInstance().level ?: return

    level.entitiesForRendering()
      .asSequence()
      .filter(::shouldCleanEntity)
      .toList()
      .forEach { entity ->
        if (!entity.isRemoved) {
          entity.discard()
        }
      }
  }

  @JvmStatic
  fun shouldHideEntity(entity: Entity?): Boolean {
    if (!enabled.value || entity == null) return false
    return shouldCleanEntity(entity)
  }

  private fun shouldCleanEntity(entity: Entity): Boolean {
    val mc = Minecraft.getInstance()
    val player = mc.player
    if (entity === player) return false
    if (entity.isRemoved) return false

    val living = entity as? LivingEntity ?: return false

    if (living is Player && !removePlayers.value) return false
    if (living is ArmorStand && !removeArmorStands.value) return false
    if (living !is Player && living !is ArmorStand && !removeMobs.value) return false

    if (requireHealthZero.value && living.health > 0.0f) return false

    val threshold = deathTicks.value.toInt().coerceIn(0, 20)
    return living.deathTime >= threshold && threshold > 0 ||
      threshold == 0 && living.deathTime > 0 ||
      living.health <= 0.0f ||
      living.isDeadOrDying ||
      !living.isAlive
  }

  private const val ACTION_REMOVE = 1
}
