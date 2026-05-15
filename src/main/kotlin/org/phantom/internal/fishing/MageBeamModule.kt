package org.phantom.internal.fishing

import java.awt.Color
import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
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
import org.phantom.api.util.render.WorldGlowEngine

object MageBeamModule : Module("Mage Beam") {

  override val category = ModuleCategory.CORE

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Show a glowing beam in your look direction when holding a mage staff.",
    true,
  )

  private val colorSetting = ColorSetting(
    "Color",
    "Beam core color.",
    0xFF7B00FF.toInt(),
  )

  private val rangeSetting = SliderSetting(
    "Range",
    "Max beam length in blocks.",
    25.0, 5.0, 60.0, 1.0,
  )

  private val itemKeywordsSetting = TextSetting(
    "Item Keywords",
    "Comma-separated SkyBlock IDs or display name fragments to match.",
    DEFAULT_ITEM_KEYWORDS,
  )

  init {
    addSetting(enabledSetting, colorSetting, rangeSetting, itemKeywordsSetting)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabledSetting.value) return
    val player = mc.player ?: return
    val level = mc.level ?: return
    if (!isHoldingMatchingItem(player.mainHandItem)) return

    val eye = player.eyePosition
    val end = eye.add(player.lookAngle.scale(rangeSetting.value))
    val result = level.clip(ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
    val target = if (result.type == HitResult.Type.BLOCK) result.location else end

    val cam = event.context.camera.position()
    val base = Color(colorSetting.value, true)
    val r = base.red / 255f
    val g = base.green / 255f
    val b = base.blue / 255f
    val a = base.alpha / 255f
    WorldGlowEngine.addLine(eye, target, r, g, b, a, cam)
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

  private const val DEFAULT_ITEM_KEYWORDS = "wand,staff,beam"
}
