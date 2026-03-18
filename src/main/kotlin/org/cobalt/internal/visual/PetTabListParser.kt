package org.cobalt.internal.visual

import net.minecraft.client.Minecraft
import org.cobalt.mixin.client.TabOverlayAccessor

data class PetData(
    val name: String,
    val level: Int,
    val heldItem: String,
    val xpCurrent: Long,
    val xpRequired: Long,
    val isMaxLevel: Boolean,
)

object PetTabListParser {

    private val mc = Minecraft.getInstance()

    var current: PetData? = null
        private set

    private val PET_NAME_LEVEL = Regex(
        """(?:\[Lv[.\s]*(\d+)]\s*(.+?)|(.+?)\s*\[Lv[.\s]*(\d+)])""",
        RegexOption.IGNORE_CASE
    )
    private val PET_ITEM = Regex(
        """(?:pet\s+item|held\s+item|item):\s*(.+)""",
        RegexOption.IGNORE_CASE
    )
    private val PET_XP = Regex(
        """(?:pet\s+)?xp:\s*([\d,]+)\s*/\s*([\d,]+)""",
        RegexOption.IGNORE_CASE
    )

    fun update() {
        val gui = mc.gui ?: return
        val tabOverlay = gui.tabList as? TabOverlayAccessor ?: return

        val rawText = buildString {
            tabOverlay.header?.let { append(stripFormatting(it.string)).append("\n") }
            tabOverlay.footer?.let { append(stripFormatting(it.string)).append("\n") }
        }

        current = if (rawText.isBlank()) null else parse(rawText)
    }

    private fun parse(text: String): PetData? {
        var name = ""
        var level = 0
        var heldItem = ""
        var xpCurrent = 0L
        var xpRequired = 0L

        for (line in text.lines()) {
            val trimmed = line.trim()
            if (name.isEmpty()) {
                PET_NAME_LEVEL.find(trimmed)?.let { m ->
                    level = (m.groupValues[1].ifEmpty { m.groupValues[4] }).toIntOrNull() ?: 0
                    name = (m.groupValues[2].ifEmpty { m.groupValues[3] }).trim()
                }
            }
            if (heldItem.isEmpty()) {
                PET_ITEM.find(trimmed)?.let { m -> heldItem = m.groupValues[1].trim() }
            }
            if (xpRequired == 0L) {
                PET_XP.find(trimmed)?.let { m ->
                    xpCurrent = m.groupValues[1].replace(",", "").toLongOrNull() ?: 0L
                    xpRequired = m.groupValues[2].replace(",", "").toLongOrNull() ?: 0L
                }
            }
        }

        if (name.isEmpty()) return null
        return PetData(name, level, heldItem, xpCurrent, xpRequired, xpRequired == 0L && level > 0)
    }

    private fun stripFormatting(text: String): String =
        text.replace(Regex("§[0-9a-fk-or]"), "")
}
