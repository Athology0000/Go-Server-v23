package org.phantom.internal.visual

import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.util.render.Render3D

object ArrowHitboxesModule : Module("Arrow Hitboxes") {

  override val category = ModuleCategory.VISUAL

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Show hitboxes for arrows in the world.",
    false,
  )

  private val color = ColorSetting(
    "Color",
    "Arrow hitbox outline color.",
    0xFF74E38B.toInt(),
  )

  private val thickness = SliderSetting(
    "Thickness",
    "Arrow hitbox outline thickness.",
    2.0,
    1.0,
    10.0,
    0.5,
  )

  init {
    addSetting(enabled, color, thickness)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onWorldRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    val level = mc.level ?: return
    val stroke = Color(color.value, true)
    for (arrow in level.entitiesForRendering().filterIsInstance<AbstractArrow>()) {
      Render3D.drawStyledBox(
        context = event.context,
        box = arrow.boundingBox,
        strokeColor = stroke,
        fillColor = null,
        esp = true,
        lineWidth = thickness.value.toFloat(),
      )
    }
  }
}
