package org.phantom.api.ui.theme.impl

import java.awt.Color
import org.phantom.api.ui.theme.Theme

internal open class PresetTheme(
  override val name: String,
  private val baseBackground: Int,
  private val basePanel: Int,
  private val baseInset: Int,
  private val baseText: Int,
  private val baseTextSecondary: Int,
  private val baseAccent: Int,
  private val baseAccentSecondary: Int,
  override val chatGradient: String,
) : Theme {

  override val rainbowEnabled = false
  override val rainbowSpeed = 1f
  override val rainbowSaturation = 1f
  override val rainbowBrightness = 1f

  override val background = baseBackground
  override val panel = basePanel
  override val inset = baseInset
  override val overlay = baseBackground.alpha(230)

  override val text = baseText
  override val textPrimary = baseText.adjust(1.08f)
  override val textSecondary = baseTextSecondary
  override val textDisabled = baseText.alpha(95)
  override val textPlaceholder = baseText.alpha(120)
  override val textOnAccent = readableOnAccent(baseAccent)

  override val accent = baseAccent
  override val accentPrimary = baseAccent.adjust(0.86f)
  override val accentSecondary = baseAccentSecondary
  override val selection = baseAccent.alpha(95)

  override val controlBg = baseInset.alpha(210)
  override val controlBorder = baseAccent.alpha(95)
  override val inputBg = baseInset.adjust(1.08f).alpha(220)
  override val inputBorder = baseAccent.alpha(125)

  override val success = Color(0x2B, 0xC4, 0x70).rgb
  override val warning = Color(0xE4, 0xAF, 0x35).rgb
  override val error = Color(0xE2, 0x4B, 0x5F).rgb
  override val info = baseAccentSecondary

  override val scrollbarThumb = baseAccent
  override val scrollbarTrack = baseInset
  override val sliderTrack = baseAccent.alpha(60)
  override val sliderFill = baseAccent
  override val sliderThumb = baseAccentSecondary

  override val tooltipBackground = baseBackground.alpha(244)
  override val tooltipBorder = baseAccent.alpha(120)
  override val tooltipText = baseText

  override val notificationBackground = basePanel
  override val notificationBorder = baseAccent
  override val notificationText = baseText
  override val notificationTextSecondary = baseTextSecondary

  override val infoBackground = info.alpha(30)
  override val infoBorder = info.alpha(150)
  override val infoIcon = info
  override val warningBackground = warning.alpha(30)
  override val warningBorder = warning.alpha(150)
  override val warningIcon = warning
  override val successBackground = success.alpha(30)
  override val successBorder = success.alpha(150)
  override val successIcon = success
  override val errorBackground = error.alpha(30)
  override val errorBorder = error.alpha(150)
  override val errorIcon = error

  override val selectionText = textPrimary
  override val searchPlaceholderText = baseTextSecondary.alpha(165)
  override val moduleDivider = baseAccent.alpha(55)
  override val selectedOverlay = baseAccent.alpha(45)

  override val white = Color.WHITE.rgb
  override val black = Color.BLACK.rgb
  override val transparent = Color(0, 0, 0, 0).rgb

  private fun Int.adjust(factor: Float): Int {
    val c = Color(this, true)
    return Color(
      (c.red * factor).coerceIn(0f, 255f).toInt(),
      (c.green * factor).coerceIn(0f, 255f).toInt(),
      (c.blue * factor).coerceIn(0f, 255f).toInt(),
      c.alpha
    ).rgb
  }

  private fun Int.alpha(alpha: Int): Int {
    val c = Color(this, true)
    return Color(c.red, c.green, c.blue, alpha.coerceIn(0, 255)).rgb
  }

  private fun readableOnAccent(rgb: Int): Int {
    val c = Color(rgb)
    val luminance = (0.2126 * c.red + 0.7152 * c.green + 0.0722 * c.blue) / 255.0
    return if (luminance > 0.62) Color(18, 18, 18).rgb else Color.WHITE.rgb
  }
}

internal class OnyxTheme : PresetTheme(
  name = "Onyx",
  baseBackground = Color(4, 4, 5).rgb,
  basePanel = Color(12, 12, 14).rgb,
  baseInset = Color(22, 22, 25).rgb,
  baseText = Color(244, 244, 244).rgb,
  baseTextSecondary = Color(166, 166, 166).rgb,
  baseAccent = Color(230, 230, 230).rgb,
  baseAccentSecondary = Color(130, 130, 135).rgb,
  chatGradient = "#FFFFFF,#B8B8B8,#0D0D0F,#DCDCDC"
)

internal class AuroraTheme : PresetTheme(
  name = "Aurora",
  baseBackground = Color(8, 14, 18).rgb,
  basePanel = Color(13, 24, 29).rgb,
  baseInset = Color(18, 36, 42).rgb,
  baseText = Color(226, 252, 244).rgb,
  baseTextSecondary = Color(136, 188, 181).rgb,
  baseAccent = Color(76, 244, 180).rgb,
  baseAccentSecondary = Color(109, 177, 255).rgb,
  chatGradient = "#4CF4B4,#6DB1FF,#C8FFEF"
)

internal class CrimsonTheme : PresetTheme(
  name = "Crimson",
  baseBackground = Color(18, 6, 8).rgb,
  basePanel = Color(31, 10, 13).rgb,
  baseInset = Color(47, 15, 19).rgb,
  baseText = Color(255, 232, 232).rgb,
  baseTextSecondary = Color(211, 143, 150).rgb,
  baseAccent = Color(255, 70, 95).rgb,
  baseAccentSecondary = Color(255, 170, 95).rgb,
  chatGradient = "#FF465F,#FFAA5F,#FFE8E8"
)

internal class GroveTheme : PresetTheme(
  name = "Grove",
  baseBackground = Color(5, 15, 11).rgb,
  basePanel = Color(10, 27, 20).rgb,
  baseInset = Color(17, 42, 31).rgb,
  baseText = Color(225, 250, 231).rgb,
  baseTextSecondary = Color(139, 188, 153).rgb,
  baseAccent = Color(63, 214, 116).rgb,
  baseAccentSecondary = Color(180, 230, 102).rgb,
  chatGradient = "#3FD674,#B4E666,#E1FAE7"
)

internal class GlacierTheme : PresetTheme(
  name = "Glacier",
  baseBackground = Color(7, 13, 23).rgb,
  basePanel = Color(12, 25, 39).rgb,
  baseInset = Color(18, 38, 58).rgb,
  baseText = Color(229, 244, 255).rgb,
  baseTextSecondary = Color(137, 179, 204).rgb,
  baseAccent = Color(83, 188, 255).rgb,
  baseAccentSecondary = Color(177, 235, 255).rgb,
  chatGradient = "#53BCFF,#B1EBFF,#E5F4FF"
)

internal class SolarTheme : PresetTheme(
  name = "Solar",
  baseBackground = Color(18, 13, 5).rgb,
  basePanel = Color(34, 24, 9).rgb,
  baseInset = Color(51, 35, 13).rgb,
  baseText = Color(255, 242, 218).rgb,
  baseTextSecondary = Color(214, 174, 112).rgb,
  baseAccent = Color(255, 196, 67).rgb,
  baseAccentSecondary = Color(255, 116, 72).rgb,
  chatGradient = "#FFC443,#FF7448,#FFF2DA"
)

internal class SakuraTheme : PresetTheme(
  name = "Sakura",
  baseBackground = Color(19, 9, 17).rgb,
  basePanel = Color(34, 17, 31).rgb,
  baseInset = Color(51, 25, 46).rgb,
  baseText = Color(255, 232, 247).rgb,
  baseTextSecondary = Color(215, 150, 194).rgb,
  baseAccent = Color(255, 117, 190).rgb,
  baseAccentSecondary = Color(183, 143, 255).rgb,
  chatGradient = "#FF75BE,#B78FFF,#FFE8F7"
)

internal class CircuitTheme : PresetTheme(
  name = "Circuit",
  baseBackground = Color(3, 10, 12).rgb,
  basePanel = Color(7, 20, 23).rgb,
  baseInset = Color(10, 34, 38).rgb,
  baseText = Color(220, 255, 248).rgb,
  baseTextSecondary = Color(113, 184, 177).rgb,
  baseAccent = Color(0, 255, 194).rgb,
  baseAccentSecondary = Color(255, 232, 76).rgb,
  chatGradient = "#00FFC2,#FFE84C,#73FFF0"
)

internal class NordTheme : PresetTheme(
  name = "Nord",
  baseBackground = Color(0x2E, 0x34, 0x40).rgb,
  basePanel = Color(0x3B, 0x42, 0x52).rgb,
  baseInset = Color(0x43, 0x4C, 0x5E).rgb,
  baseText = Color(0xEC, 0xEF, 0xF4).rgb,
  baseTextSecondary = Color(0xD8, 0xDE, 0xE9).rgb,
  baseAccent = Color(0x88, 0xC0, 0xD0).rgb,
  baseAccentSecondary = Color(0x81, 0xA1, 0xC1).rgb,
  chatGradient = "#88C0D0,#81A1C1,#B48EAD,#ECEFF4"
)

internal class NordFrostTheme : PresetTheme(
  name = "Nord Frost",
  baseBackground = Color(0x20, 0x27, 0x2F).rgb,
  basePanel = Color(0x2E, 0x38, 0x43).rgb,
  baseInset = Color(0x3A, 0x46, 0x52).rgb,
  baseText = Color(0xE5, 0xEE, 0xF6).rgb,
  baseTextSecondary = Color(0xA7, 0xB9, 0xC8).rgb,
  baseAccent = Color(0x8F, 0xBC, 0xBB).rgb,
  baseAccentSecondary = Color(0x5E, 0x81, 0xAC).rgb,
  chatGradient = "#8FBCBB,#88C0D0,#5E81AC,#E5EEF6"
)

internal class NordNightTheme : PresetTheme(
  name = "Nord Night",
  baseBackground = Color(0x1D, 0x22, 0x2B).rgb,
  basePanel = Color(0x28, 0x2F, 0x3A).rgb,
  baseInset = Color(0x32, 0x3A, 0x47).rgb,
  baseText = Color(0xE6, 0xEA, 0xF0).rgb,
  baseTextSecondary = Color(0x9B, 0xA9, 0xB8).rgb,
  baseAccent = Color(0xB4, 0x8E, 0xAD).rgb,
  baseAccentSecondary = Color(0x81, 0xA1, 0xC1).rgb,
  chatGradient = "#B48EAD,#81A1C1,#88C0D0,#E6EAF0"
)

internal class SlateTheme : PresetTheme(
  name = "Slate",
  baseBackground = Color(15, 18, 22).rgb,
  basePanel = Color(24, 29, 35).rgb,
  baseInset = Color(35, 42, 50).rgb,
  baseText = Color(232, 236, 241).rgb,
  baseTextSecondary = Color(143, 153, 166).rgb,
  baseAccent = Color(116, 136, 158).rgb,
  baseAccentSecondary = Color(184, 198, 214).rgb,
  chatGradient = "#E8ECF1,#74889E,#232A32,#B8C6D6"
)
