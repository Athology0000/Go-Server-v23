package org.cobalt.internal.combat

import java.awt.Color
import net.minecraft.ChatFormatting

internal data class SlayerHighlightState(
  val type: SlayerHighlightType,
  val attunement: BlazeAttunement?,
)

internal enum class SlayerHighlightType {
  BOSS,
  MINIBOSS,
  HIGH_TIER_MINIBOSS
}

internal enum class BlazeAttunement(val argb: Int) {
  ASHEN(0xFFFF6B6B.toInt()),
  SPIRIT(0xFF5AE6FF.toInt()),
  AURIC(0xFFFFD54A.toInt()),
  CRYSTAL(0xFFC88CFF.toInt()),
}

internal data class SlayerEspPalette(
  val stroke: Color,
  val fill: Color,
  val outerStroke: Color,
  val outerFill: Color,
)

internal val CHAT_FORMATTING_COLORS = listOf(
  ChatFormatting.BLACK to Color(0x000000),
  ChatFormatting.DARK_BLUE to Color(0x0000AA),
  ChatFormatting.DARK_GREEN to Color(0x00AA00),
  ChatFormatting.DARK_AQUA to Color(0x00AAAA),
  ChatFormatting.DARK_RED to Color(0xAA0000),
  ChatFormatting.DARK_PURPLE to Color(0xAA00AA),
  ChatFormatting.GOLD to Color(0xFFAA00),
  ChatFormatting.GRAY to Color(0xAAAAAA),
  ChatFormatting.DARK_GRAY to Color(0x555555),
  ChatFormatting.BLUE to Color(0x5555FF),
  ChatFormatting.GREEN to Color(0x55FF55),
  ChatFormatting.AQUA to Color(0x55FFFF),
  ChatFormatting.RED to Color(0xFF5555),
  ChatFormatting.LIGHT_PURPLE to Color(0xFF55FF),
  ChatFormatting.YELLOW to Color(0xFFFF55),
  ChatFormatting.WHITE to Color(0xFFFFFF),
)

internal const val SLAYER_ESP_STYLE_GLOW = 0
internal const val SLAYER_ESP_STYLE_BOX = 1
internal const val SLAYER_GLOW_TEAM_PREFIX = "cbsl_"
internal const val SLAYER_GLOW_TEAM_BOSS = "cbsl_boss"
internal const val SLAYER_GLOW_TEAM_MINI = "cbsl_mini"
internal const val SLAYER_GLOW_TEAM_HIGH = "cbsl_high"
internal const val SLAYER_GLOW_TEAM_ASHEN = "cbsl_ash"
internal const val SLAYER_GLOW_TEAM_SPIRIT = "cbsl_spi"
internal const val SLAYER_GLOW_TEAM_AURIC = "cbsl_aur"
internal const val SLAYER_GLOW_TEAM_CRYSTAL = "cbsl_crys"
