package org.phantom.internal.fishing

import java.awt.Color
import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.util.getSkyblockId
import org.phantom.api.util.render.Render3D

object SoulWhipPreviewModule : Module("Soul Whip Preview") {

  override val category = ModuleCategory.CORE

  private val mc = Minecraft.getInstance()

  private val arcEnabledSetting = CheckboxSetting(
    "Arc",
    "Show a trajectory arc toward the aimed target.",
    true,
  )

  private val colorSetting = ColorSetting(
    "Arc Color",
    "Arc color.",
    0xFFFF00D5.toInt(),
  )

  private val rangeSetting = SliderSetting(
    "Range",
    "Max reach of the arc in blocks.",
    15.0, 5.0, 30.0, 1.0,
  )

  private val thicknessSetting = SliderSetting(
    "Thickness",
    "Line thickness of the arc.",
    2.5, 0.5, 6.0, 0.5,
  )

  private val landingHighlightSetting = CheckboxSetting(
    "Landing Highlight",
    "Highlight the top face of the block the flay will land on.",
    true,
  )

  private val landingFillColorSetting = ColorSetting(
    "Landing Fill",
    "Fill color for the landing face.",
    0x44FF00D5.toInt(),
  )

  private val landingOutlineColorSetting = ColorSetting(
    "Landing Outline",
    "Outline color for the landing face.",
    0xFFFF00D5.toInt(),
  )

  private val itemKeywordsSetting = TextSetting(
    "Item Keywords",
    "Comma-separated SkyBlock IDs or display name fragments to match.",
    DEFAULT_ITEM_KEYWORDS,
  )

  private val espSetting = CheckboxSetting(
    "Show Through Blocks",
    "Render through walls.",
    true,
  )

  init {
    addSetting(
      arcEnabledSetting,
      colorSetting,
      rangeSetting,
      thicknessSetting,
      landingHighlightSetting,
      landingFillColorSetting,
      landingOutlineColorSetting,
      itemKeywordsSetting,
      espSetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    val player = mc.player ?: return
    val level = mc.level ?: return
    if (!isHoldingMatchingItem(player.mainHandItem)) return

    val eye = player.eyePosition
    val reach = player.lookAngle.scale(rangeSetting.value)

    // Landing highlight â€” SOURCE_ONLY so it lands on the water surface like a bobber
    if (landingHighlightSetting.value) {
      val fluidResult = level.clip(ClipContext(eye, eye.add(reach), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, player))
      if (fluidResult.type == HitResult.Type.BLOCK) {
        val pos = fluidResult.blockPos
        FishingCastPreviewModule.renderFace(event, pos.x, pos.y, pos.z)
      }
    }

    // Arc
    if (arcEnabledSetting.value) {
      val blockResult = level.clip(ClipContext(eye, eye.add(reach), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
      val target = if (blockResult.type == HitResult.Type.BLOCK) blockResult.location else eye.add(reach)

      val start = player.position().add(0.0, player.eyeHeight * 0.72, 0.0)
      val distance = start.distanceTo(target)
      val height = (distance * 0.28).coerceIn(1.2, 5.0)
      val midpoint = start.add(target).scale(0.5).add(0.0, height, 0.0)

      val color = Color(colorSetting.value, true)
      val thickness = thicknessSetting.value.toFloat()
      var prev = start
      for (step in 1..ARC_SEGMENTS) {
        val t = step.toDouble() / ARC_SEGMENTS
        val point = quadraticBezier(start, midpoint, target, t)
        Render3D.drawLine(event.context, prev, point, color, espSetting.value, thickness)
        prev = point
      }
    }
  }

  private fun isHoldingMatchingItem(stack: ItemStack): Boolean {
    if (stack.isEmpty) return false
    val keywords = itemKeywordsSetting.value.split(',')
      .map { it.trim().lowercase(Locale.US) }
      .filter { it.isNotBlank() }
    if (keywords.isEmpty()) return false
    val id = stack.getSkyblockId().lowercase(Locale.US)
    val name = (ChatFormatting.stripFormatting(stack.hoverName.string) ?: "").lowercase(Locale.US).trim()
    return keywords.any { id.contains(it) || name.contains(it) }
  }

  private fun quadraticBezier(start: Vec3, control: Vec3, end: Vec3, t: Double): Vec3 {
    val inv = 1.0 - t
    return start.scale(inv * inv).add(control.scale(2.0 * inv * t)).add(end.scale(t * t))
  }

  private const val DEFAULT_ITEM_KEYWORDS = "soul_whip,flay"
  private const val ARC_SEGMENTS = 28
}
