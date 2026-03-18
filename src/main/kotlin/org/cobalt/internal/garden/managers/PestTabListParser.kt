package org.cobalt.internal.garden.managers

import net.minecraft.client.Minecraft
import org.cobalt.mixin.client.TabOverlayAccessor

data class TabListData(
    val alivePests: Int,
    val cooldownSeconds: Int,
    val infestedPlots: List<String>,
    val bonusActive: Boolean,
)

object PestTabListParser {

    private val mc = Minecraft.getInstance()

    private val ALIVE_REGEX    = Regex("""alive[:\s]+(\d+)""", RegexOption.IGNORE_CASE)
    private val COOLDOWN_REGEX = Regex("""cooldown[:\s]+(\d+)s""", RegexOption.IGNORE_CASE)
    private val PLOT_REGEX     = Regex("""plot[:\s]+([a-zA-Z0-9_ ,]+)""", RegexOption.IGNORE_CASE)
    private val BONUS_REGEX    = Regex("""bonus[:\s]+active""", RegexOption.IGNORE_CASE)

    fun parse(): TabListData {
        val gui = mc.gui
        val overlay = gui.tabList as? TabOverlayAccessor ?: return empty()

        val raw = buildString {
            overlay.header?.let { append(strip(it.string)).append("\n") }
            overlay.footer?.let { append(strip(it.string)).append("\n") }
            mc.connection?.listedOnlinePlayers?.forEach { info ->
                info.tabListDisplayName?.let { append(strip(it.string)).append("\n") }
            }
        }

        val alive    = ALIVE_REGEX.find(raw)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val cooldown = COOLDOWN_REGEX.find(raw)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val plots    = PLOT_REGEX.find(raw)?.groupValues?.get(1)
            ?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        val bonus    = BONUS_REGEX.containsMatchIn(raw)

        return TabListData(alive, cooldown, plots, bonus)
    }

    private fun strip(text: String) = text.replace(Regex("§[0-9a-fk-or]"), "")
    private fun empty() = TabListData(0, 0, emptyList(), false)
}
