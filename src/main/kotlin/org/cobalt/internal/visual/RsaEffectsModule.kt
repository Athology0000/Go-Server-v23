package org.cobalt.internal.visual

import net.minecraft.client.Minecraft
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.inGroup

object RsaEffectsModule : Module("RSA Effects") {

  override val category = ModuleCategory.VISUAL

  private val enabled = CheckboxSetting(
    "Enabled",
    "Remove selected visual or movement status effects.",
    false,
  )

  private val nausea = CheckboxSetting("Nausea", "Remove nausea.", true).inGroup(EFFECTS_GROUP)
  private val blindness = CheckboxSetting("Blindness", "Remove blindness.", true).inGroup(EFFECTS_GROUP)
  private val slowness = CheckboxSetting("Slowness", "Remove slowness.", false).inGroup(EFFECTS_GROUP)
  private val haste = CheckboxSetting("Haste", "Remove haste.", false).inGroup(EFFECTS_GROUP)
  private val speed = CheckboxSetting("Speed", "Remove speed.", false).inGroup(EFFECTS_GROUP)
  private val darkness = CheckboxSetting("Darkness", "Remove darkness.", true).inGroup(EFFECTS_GROUP)
  private val miningFatigue = CheckboxSetting("Mining Fatigue", "Remove mining fatigue.", false).inGroup(EFFECTS_GROUP)

  init {
    addSetting(
      enabled,
      nausea,
      blindness,
      slowness,
      haste,
      speed,
      darkness,
      miningFatigue,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) return
    val player = Minecraft.getInstance().player ?: return

    if (nausea.value) player.removeIfPresent(MobEffects.NAUSEA)
    if (blindness.value) player.removeIfPresent(MobEffects.BLINDNESS)
    if (slowness.value) player.removeIfPresent(MobEffects.SLOWNESS)
    if (haste.value) player.removeIfPresent(MobEffects.HASTE)
    if (speed.value) player.removeIfPresent(MobEffects.SPEED)
    if (darkness.value) player.removeIfPresent(MobEffects.DARKNESS)
    if (miningFatigue.value) player.removeIfPresent(MobEffects.MINING_FATIGUE)
  }

  private fun net.minecraft.world.entity.player.Player.removeIfPresent(effect: Holder<MobEffect>) {
    if (hasEffect(effect)) {
      removeEffect(effect)
    }
  }

  private const val EFFECTS_GROUP = "Status Effects"
}
