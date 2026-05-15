package org.phantom.internal.qol

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URI
import java.util.Locale
import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.inGroup

object ColoredEnchantsModule : Module("Colored Enchants") {

  override val category = ModuleCategory.QOL

  private val enabled = CheckboxSetting(
    "Enabled",
    "Color SkyBlock enchantment tooltip lines by enchant type and level.",
    false,
  ).inGroup("General")

  private val replaceRoman = CheckboxSetting(
    "Replace Roman",
    "Show numeric enchant levels instead of roman numerals.",
    true,
  ).inGroup("General")

  private val dataInfo = InfoSetting(
    "Enchant Data",
    "Loads SkyBlock enchant levels from Athen's public data endpoint; falls back to common levels if unavailable.",
    InfoType.INFO,
  ).inGroup("General")

  private val ultimateColor = ColorSetting("Ultimate Color", "", 0xFFCBA6F7.toInt()).inGroup("Colors")
  private val maxColor = ColorSetting("Max Color", "", 0xFFFF5555.toInt()).inGroup("Colors")
  private val highColor = ColorSetting("High Color", "", 0xFFFFAA00.toInt()).inGroup("Colors")
  private val normalColor = ColorSetting("Normal Color", "", 0xFF55AAFF.toInt()).inGroup("Colors")
  private val badColor = ColorSetting("Bad Color", "", 0xFFAAAAAA.toInt()).inGroup("Colors")

  @Volatile private var enchantData: Map<String, EnchantInfo> = fallbackEnchantData()

  init {
    addSetting(enabled, replaceRoman, dataInfo, ultimateColor, maxColor, highColor, normalColor, badColor)
    loadEnchantDataAsync()

    ItemTooltipCallback.EVENT.register { _, _, _, lines ->
      if (!enabled.value || lines.isEmpty()) return@register
      colorTooltip(lines)
    }
  }

  private fun colorTooltip(lines: MutableList<Component>) {
    var foundEnchantLine = false
    for (index in lines.indices) {
      val raw = lines[index].string
      val stripped = ChatFormatting.stripFormatting(raw).orEmpty()
      if (foundEnchantLine && stripped.isBlank()) break
      if (stripped.contains("Gemstone") || stripped.contains("Reforge")) continue

      val colored = colorEnchantLine(stripped, lines[index].style) ?: continue
      lines[index] = colored
      foundEnchantLine = true
    }
  }

  private fun colorEnchantLine(stripped: String, baseStyle: Style): MutableComponent? {
    var cursor = 0
    var changed = false
    val result = Component.empty()

    for (match in ENCHANT_PATTERN.findAll(stripped)) {
      val start = match.range.first
      val endExclusive = match.range.last + 1
      if (start > cursor) {
        result.append(Component.literal(stripped.substring(cursor, start)).withStyle(baseStyle))
      }

      val rawName = match.groups["name"]?.value ?: continue
      val rawLevel = match.groups["level"]?.value ?: continue
      val level = parseRoman(rawLevel) ?: continue
      val info = enchantData[rawName.lowercase(Locale.US)]

      if (info == null) {
        result.append(Component.literal(stripped.substring(start, endExclusive)).withStyle(baseStyle))
      } else {
        val levelText = if (replaceRoman.value) level.toString() else rawLevel
        result.append(
          Component.literal("${info.loreName} $levelText")
            .withStyle(styleFor(info, level))
        )
        changed = true
      }
      cursor = endExclusive
    }

    if (!changed) return null
    if (cursor < stripped.length) {
      result.append(Component.literal(stripped.substring(cursor)).withStyle(baseStyle))
    }
    return result
  }

  private fun styleFor(info: EnchantInfo, level: Int): Style {
    val color = when {
      info.category == EnchantCategory.ULTIMATE -> ultimateColor.value
      level >= info.maxLevel -> maxColor.value
      level > info.goodLevel -> highColor.value
      level == info.goodLevel -> normalColor.value
      else -> badColor.value
    }

    return Style.EMPTY
      .withColor(TextColor.fromRgb(color and 0x00FFFFFF))
      .withBold(info.category == EnchantCategory.ULTIMATE)
  }

  private fun loadEnchantDataAsync() {
    CompletableFuture.runAsync {
      val loaded = runCatching {
        val connection = URI(DATA_URL).toURL().openConnection()
        connection.setRequestProperty("User-Agent", "Phantom")
        connection.getInputStream().bufferedReader().use { reader ->
          parseEnchantData(JsonParser.parseReader(reader).asJsonObject)
        }
      }.getOrNull()
      if (!loaded.isNullOrEmpty()) enchantData = loaded
    }
  }

  private fun parseEnchantData(root: JsonObject): Map<String, EnchantInfo> {
    val result = linkedMapOf<String, EnchantInfo>()
    for (categoryName in listOf("NORMAL", "STACKING", "ULTIMATE")) {
      val category = EnchantCategory.valueOf(categoryName)
      val group = root.getAsJsonObject(categoryName) ?: continue
      for ((key, value) in group.entrySet()) {
        val obj = value.asJsonObject
        val loreName = obj.get("loreName")?.asString ?: key.replaceFirstChar { it.uppercaseChar() }
        val maxLevel = obj.get("maxLevel")?.asInt ?: 5
        val goodLevel = obj.get("goodLevel")?.asInt ?: maxLevel
        result[key.lowercase(Locale.US)] = EnchantInfo(loreName, category, maxLevel, goodLevel)
      }
    }
    return result
  }

  private fun fallbackEnchantData(): Map<String, EnchantInfo> {
    val entries = listOf(
      EnchantInfo("Sharpness", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Smite", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Critical", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Ender Slayer", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Giant Killer", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Titan Killer", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Power", EnchantCategory.NORMAL, 7, 5),
      EnchantInfo("Efficiency", EnchantCategory.NORMAL, 10, 5),
      EnchantInfo("Compact", EnchantCategory.STACKING, 10, 0),
      EnchantInfo("Expertise", EnchantCategory.STACKING, 10, 0),
      EnchantInfo("Champion", EnchantCategory.STACKING, 10, 0),
      EnchantInfo("Ultimate Wise", EnchantCategory.ULTIMATE, 5, 0),
      EnchantInfo("Soul Eater", EnchantCategory.ULTIMATE, 5, 0),
      EnchantInfo("Duplex", EnchantCategory.ULTIMATE, 5, 0),
      EnchantInfo("Fatal Tempo", EnchantCategory.ULTIMATE, 5, 0),
      EnchantInfo("One For All", EnchantCategory.ULTIMATE, 5, 0),
      EnchantInfo("Chimera", EnchantCategory.ULTIMATE, 5, 0),
    )
    return entries.associateBy { it.loreName.lowercase(Locale.US) }
  }

  private fun parseRoman(value: String): Int? {
    var total = 0
    var last = 0
    for (ch in value.uppercase(Locale.US).reversed()) {
      val current = ROMAN[ch] ?: return null
      if (current < last) total -= current else total += current
      last = current
    }
    return total.takeIf { it > 0 }
  }

  private data class EnchantInfo(
    val loreName: String,
    val category: EnchantCategory,
    val maxLevel: Int,
    val goodLevel: Int,
  )

  private enum class EnchantCategory {
    NORMAL,
    STACKING,
    ULTIMATE,
  }

  private val ENCHANT_PATTERN = Regex("""(?<name>[A-Za-z][A-Za-z' -]*?) (?<level>[IVXLCDM]+)(?=,|$)""")
  private val ROMAN = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
  private const val DATA_URL = "https://data.aerii.xyz/enchants/sba.json"
}
