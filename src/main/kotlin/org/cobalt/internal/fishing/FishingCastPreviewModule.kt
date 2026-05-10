package org.cobalt.internal.fishing

import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ColorSetting
import org.cobalt.api.util.render.Render3D

object FishingCastPreviewModule : Module("Fishing Cast Preview") {

  override val category = ModuleCategory.CORE

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Highlight the top face of the water block your fishing hook will land on.",
    true,
  )

  private val fillColorSetting = ColorSetting(
    "Fill Color",
    "Fill color for the landing face.",
    0x4400AAFF,
  )

  private val outlineColorSetting = ColorSetting(
    "Outline Color",
    "Outline color for the landing face.",
    0xFF00C8FF.toInt(),
  )

  private val espSetting = CheckboxSetting(
    "Show Through Blocks",
    "Render through walls.",
    true,
  )

  init {
    addSetting(enabledSetting, fillColorSetting, outlineColorSetting, espSetting)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabledSetting.value) return
    val player = mc.player ?: return
    val level = mc.level ?: return

    if (player.fishing != null) return
    if (player.mainHandItem.item !is FishingRodItem && player.offhandItem.item !is FishingRodItem) return

    val eye = player.eyePosition
    val end = eye.add(player.lookAngle.scale(CAST_RAYCAST_RANGE))
    val result = level.clip(ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, player))
    if (result.type != HitResult.Type.BLOCK) return

    renderFace(event, result.blockPos.x, result.blockPos.y, result.blockPos.z)
  }

  internal fun renderFace(event: WorldRenderEvent.Last, bx: Int, by: Int, bz: Int) {
    val topY = by + 1.0
    val face = AABB(bx.toDouble(), topY - FACE_THICKNESS, bz.toDouble(), bx + 1.0, topY, bz + 1.0)
    Render3D.drawStyledBox(
      event.context,
      face,
      Color(outlineColorSetting.value, true),
      Color(fillColorSetting.value, true),
      espSetting.value,
    )
  }

  private const val CAST_RAYCAST_RANGE = 30.0
  internal const val FACE_THICKNESS = 0.015
}
