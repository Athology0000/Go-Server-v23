package org.cobalt.internal.qol

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.blaze3d.platform.InputConstants
import java.net.URI
import java.util.Locale
import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.util.getSkyblockExtraAttributes
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.util.tagCompound
import org.lwjgl.glfw.GLFW

object MissingEnchantsModule : Module("Missing Enchants") {

  override val category = ModuleCategory.QOL

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Show missing SkyBlock enchants on item tooltips.",
    false,
  ).inGroup("General")

  private val keybind = KeyBindSetting(
    "Keybind",
    "Hold this key while hovering an item. Set to None to always show.",
    KeyBind(GLFW.GLFW_KEY_LEFT_SHIFT),
  ).inGroup("General")

  private val includeUltimateHint = CheckboxSetting(
    "Ultimate Hint",
    "Show an Ultimate enchant hint when the item has no ultimate enchant.",
    true,
  ).inGroup("General")

  private val dataInfo = InfoSetting(
    "Enchant Data",
    "Loads item enchant pools from Athen's public NEU-format enchant data.",
    InfoType.INFO,
  ).inGroup("General")

  @Volatile private var enchantsByType: Map<String, List<String>> = emptyMap()
  @Volatile private var enchantPools: List<List<String>> = emptyList()

  init {
    addSetting(enabled, keybind, includeUltimateHint, dataInfo)
    loadDataAsync()

    ItemTooltipCallback.EVENT.register { stack, _, _, lines ->
      if (!enabled.value || !shouldShow() || lines.isEmpty()) return@register
      addMissingEnchantLines(stack, lines)
    }
  }

  private fun addMissingEnchantLines(stack: ItemStack, tooltip: MutableList<Component>) {
    if (tooltip.any { (ChatFormatting.stripFormatting(it.string) ?: it.string).contains("Missing:", ignoreCase = true) }) {
      return
    }

    val type = detectItemType(tooltip) ?: return
    val wanted = enchantsByType[type] ?: return
    val present = stack.skyblockEnchantIds()
    if (present.any { it == "ultimate_one_for_all" || it == "one_for_all" }) return

    val missing = mutableListOf<String>()
    for (enchant in wanted) {
      if (enchant in present) continue
      if (isExclusiveWithPresent(enchant, present)) continue
      if (!enchant.startsWith("ultimate_")) missing += prettifyEnchant(enchant)
    }

    val hasUltimate = present.any { it.startsWith("ultimate_") }
    if (includeUltimateHint.value && !hasUltimate) {
      missing.add(0, "Ultimate enchant")
    }
    if (missing.isEmpty()) return

    val insertAt = tooltip.indexOfFirst { it.string.isBlank() }.let { if (it >= 0) it else tooltip.size }
    val additions = mutableListOf<Component>()
    additions += Component.empty()
    additions += Component.literal("Missing:")
      .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCBA6F7)).withItalic(false))

    missing.chunked(3).forEach { chunk ->
      additions += Component.literal(" - ${chunk.joinToString(", ")}")
        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCDD6F4)).withItalic(false))
    }
    tooltip.addAll(insertAt, additions)
  }

  private fun shouldShow(): Boolean {
    val code = keybind.value.keyCode
    if (code == -1) return true
    return InputConstants.isKeyDown(mc.window, code)
  }

  private fun detectItemType(tooltip: List<Component>): String? {
    for (line in tooltip.asReversed()) {
      val stripped = ChatFormatting.stripFormatting(line.string)
        ?.replace(Regex("\\s+"), " ")
        ?.trim()
        .orEmpty()
      val match = TYPE_PATTERN.find(stripped) ?: continue
      return match.groupValues[1].uppercase(Locale.US)
    }
    return null
  }

  private fun ItemStack.skyblockEnchantIds(): Set<String> {
    val attributes = getSkyblockExtraAttributes() ?: return emptySet()
    val enchantments = attributes.tagCompound("enchantments") ?: return emptySet()
    return enchantments.keySet().map { normalizeEnchantId(it) }.toSet()
  }

  private fun isExclusiveWithPresent(enchant: String, present: Set<String>): Boolean =
    enchantPools.any { pool -> enchant in pool && pool.any { it in present } }

  private fun loadDataAsync() {
    CompletableFuture.runAsync {
      val parsed = runCatching {
        val connection = URI(DATA_URL).toURL().openConnection()
        connection.setRequestProperty("User-Agent", "Cobalt")
        connection.getInputStream().bufferedReader().use { reader ->
          parseData(JsonParser.parseReader(reader).asJsonObject)
        }
      }.getOrNull()
      if (parsed != null) {
        enchantsByType = parsed.first
        enchantPools = parsed.second
      }
    }
  }

  private fun parseData(root: JsonObject): Pair<Map<String, List<String>>, List<List<String>>> {
    val enchants = root.getAsJsonObject("enchants")
      ?.entrySet()
      ?.associate { (type, value) ->
        type.uppercase(Locale.US) to value.asJsonArray.map { normalizeEnchantId(it.asString) }
      }
      .orEmpty()

    val pools = root.getAsJsonArray("enchant_pools")
      ?.map { pool -> pool.asJsonArray.map { normalizeEnchantId(it.asString) } }
      .orEmpty()

    return enchants to pools
  }

  private fun normalizeEnchantId(value: String): String =
    value.lowercase(Locale.US)
      .removePrefix("enchantment_")
      .let { if (it == "prosecute") "prosecute" else it }

  private fun prettifyEnchant(value: String): String =
    value.removePrefix("ultimate_")
      .split('_')
      .filter { it.isNotBlank() }
      .joinToString(" ") { word ->
        word.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.US) else char.toString() }
      }

  private val TYPE_PATTERN =
    Regex("""\b(?:COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|SPECIAL|VERY SPECIAL)\b\s+(?:DUNGEON\s+)?([A-Z]+(?: [A-Z]+)*)""")

  private const val DATA_URL = "https://data.aerii.xyz/enchants/neu.json"
}
