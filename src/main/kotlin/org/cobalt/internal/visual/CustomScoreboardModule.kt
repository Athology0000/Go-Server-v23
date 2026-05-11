package org.cobalt.internal.visual

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerScoreEntry
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ColorSetting
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.ChatUtils
import org.cobalt.mixin.client.TabOverlayAccessor

object CustomScoreboardModule : Module("Custom Scoreboard") {

  override val category = ModuleCategory.VISUAL

  private val mc = Minecraft.getInstance()
  private val changeCache = mutableMapOf<String, NumberChange>()
  private const val DEFAULT_APPEARANCE =
    "TITLE,LOBBY_CODE,EMPTY_LINE,DATE,TIME,ISLAND,PLAYER_AMOUNT,LOCATION,VISITING,PROFILE,EMPTY_LINE2," +
      "PURSE,MOTES,BANK,BITS,COPPER,SOWDUST,GEMS,HEAT,COLD,NORTH_STARS,SOULFLOW,EMPTY_LINE3,EVENTS," +
      "COOKIE,QUIVER,POWER,TUNING,EMPTY_LINE4,OBJECTIVE,SLAYER,POWDER,MAYOR,PARTY,FOOTER,EXTRA"
  private const val DEFAULT_EVENTS_PRIORITY =
    "ANNIVERSARY,BROODMOTHER,CARNIVAL,DAMAGE,DARK_AUCTION,DOJO,DUNGEONS,ESSENCE,FLIGHT_DURATION," +
      "GALATEA,GARDEN,JACOB_MEDALS,JACOBS_CONTEST,KUUDRA,MAGMA_BOSS,MINING,NEW_YEAR,QUEUE,REDSTONE," +
      "RIFT,SERVER_RESTART,SPOOKY,TRAPPER,VOTING,WINTER"
  private const val DEFAULT_CHUNKED_STATS = "PURSE,BANK,BITS,GEMS,COPPER,MOTES,NORTH_STARS,SOULFLOW,SOWDUST"

  private val enabled = CheckboxSetting("Main Toggle", "Enable or disable the custom scoreboard.", false)
  private val hideWhenTab = CheckboxSetting("Hide While Tablist Open", "Hide while the tablist is open.", true)
  private val hideWhenChat = CheckboxSetting("Hide While Chat Open", "Hide while chat is open.", false)
  private val hideHypixelScoreboard = CheckboxSetting("Hide Hypixel Scoreboard", "Hide the vanilla Hypixel scoreboard.", true)
  private val textShadow = CheckboxSetting("Text Shadow", "Enable text shadow.", true)
  private val customLines = CheckboxSetting("Custom Lines", "Use selected custom lines instead of raw Hypixel lines.", true)
  private val actions = CheckboxSetting("Click and Hover Actions", "Keep action rows grouped like CustomScoreboard.", true)
  private val outsideSkyBlock = CheckboxSetting("Outside SkyBlock", "Show custom scoreboard outside SkyBlock using server lines.", true)
  private val appearance = TextSetting(
    "Appearance",
    "SkyHanni-style comma-separated scoreboard element order.",
    DEFAULT_APPEARANCE,
  )
  private val resetAppearance = ActionSetting("Reset Appearance", "Reset the SkyHanni-style appearance order.", "Reset") {
    appearance.value = DEFAULT_APPEARANCE
  }

  private val showEvents = CheckboxSetting("Events", "Show event lines using CustomScoreboard event priority.", true)
  private val eventsPriority = TextSetting(
    "Events Priority",
    "SkyHanni-style comma-separated event priority order.",
    DEFAULT_EVENTS_PRIORITY,
  )
  private val resetEventsPriority = ActionSetting("Reset Events Priority", "Reset the event priority order.", "Reset") {
    eventsPriority.value = DEFAULT_EVENTS_PRIORITY
  }
  private val showAllActiveEvents = CheckboxSetting("All Events", "Show all active event groups instead of the first one.", true)
  private val showTablistLines = CheckboxSetting("Tablist Lines", "Use tab-list widgets for extra elements.", true)
  private val chunkedStats = CheckboxSetting("Chunked Stats", "Combine compact stats onto fewer lines.", false)
  private val chunkedStatsOrder = TextSetting("Chunked Stats", "SkyHanni-style comma-separated chunked stat order.", DEFAULT_CHUNKED_STATS)
  private val statsPerLine = SliderSetting("Max Stats per Line", "Stats shown in each chunk.", 3.0, 1.0, 10.0, 1.0)

  private val useHypixelTitle = CheckboxSetting("Use Hypixel Title", "Use the server scoreboard title.", true)
  private val titleUseCustomText = CheckboxSetting("Use Custom Title", "Use a custom title instead of the default Hypixel title.", false)
  private val titleUseCustomOutsideSkyBlock = CheckboxSetting("Use Custom Title Outside SkyBlock", "Use a custom title outside of SkyBlock.", false)
  private val titleText = TextSetting("Custom Title", "Supports & color codes and \\n.", "SkyBlock")
  private val footerUseCustomText = CheckboxSetting("Footer Use Custom Text", "Override footer text.", true)
  private val footerText = TextSetting("Custom Footer", "Supports & color codes and \\n.", "dutt client")
  private val alphaFooterText = TextSetting("Custom Alpha Footer", "Supports & color codes and \\n.", "alpha.hypixel.net")
  private val titleAlignment = ModeSetting("Title Alignment", "Title text alignment.", 1, arrayOf("Left", "Center", "Right"))
  private val footerAlignment = ModeSetting("Footer Alignment", "Footer text alignment.", 1, arrayOf("Left", "Center", "Right"))
  private val defaultTextAlignment = ModeSetting("Text Alignment", "Default line alignment.", 0, arrayOf("Left", "Center", "Right"))
  private val verticalAlignment = ModeSetting("Vertical Alignment", "Scoreboard vertical position.", 1, arrayOf("Top", "Center", "Bottom"))
  private val horizontalAlignment = ModeSetting("Horizontal Alignment", "Scoreboard horizontal position.", 2, arrayOf("Left", "Center", "Right"))
  private val scale = SliderSetting("Scale", "Scoreboard scale.", 1.0, 0.1, 2.0, 0.05)
  private val lineSpacing = SliderSetting("Line Spacing", "Spacing between lines.", 10.0, 0.0, 20.0, 1.0)

  private val background = CheckboxSetting("Enabled", "Show a background behind the scoreboard.", true)
  private val backgroundColor = ColorSetting("Background Color", "Background color.", 0xA0000000.toInt())
  private val padding = SliderSetting("Background Border Size", "The size of the border around the background.", 5.0, 0.0, 20.0, 1.0)
  private val margin = SliderSetting("Margin", "Distance from screen border.", 5.0, 0.0, 20.0, 1.0)
  private val radius = SliderSetting("Rounded Corner Smoothness", "Rounded-corner radius.", 6.0, 0.0, 30.0, 1.0)
  private val blurEnabled = CheckboxSetting("Blur", "Use an extra translucent pass to imitate outline/background blur.", false)
  private val outlineBlur = SliderSetting("Outline Blur", "Amount that the outline is blurred.", 0.7, 0.0, 1.0, 0.1)
  private val borderEnabled = CheckboxSetting("Outline", "Enable the scoreboard outline.", true)
  private val borderSize = SliderSetting("Thickness", "Outline thickness.", 3.0, 0.0, 15.0, 1.0)
  private val borderTop = ColorSetting("Color Top", "Color of the top of the outline.", 0xFF32A0DB.toInt())
  private val borderBottom = ColorSetting("Color Bottom", "Color of the bottom of the outline.", 0xFF2BC95A.toInt())
  private val borderTopLeft = ColorSetting("Top Left Color", "Top-left border color.", 0xFF32A0DB.toInt())
  private val borderTopRight = ColorSetting("Top Right Color", "Top-right border color.", 0xFF32DB62.toInt())
  private val borderBottomLeft = ColorSetting("Bottom Left Color", "Bottom-left border color.", 0xFF29C8AE.toInt())
  private val borderBottomRight = ColorSetting("Bottom Right Color", "Bottom-right border color.", 0xFF2BC95A.toInt())
  private val imageBackground = CheckboxSetting("Custom Image Background", "Reserve image-background opacity behavior.", false)
  private val imageBackgroundTransparency = SliderSetting("Background Image Opacity", "Image/background opacity percent.", 90.0, 0.0, 100.0, 1.0)
  private val customImageFile = TextSetting("Custom Background Image", "Matches CustomScoreboard's scoreboard.png file option.", "")

  private val dateFormat = ModeSetting(
    "Date Format",
    "Date format.",
    8,
    arrayOf(
      "ISO yyyy-MM-dd",
      "yyyy MMMM dd",
      "MM/dd/yyyy",
      "MM-dd-yyyy",
      "dd/MM/yyyy",
      "dd-MM-yyyy",
      "dd MMMM yyyy",
      "dd MMM yyyy",
      "MMMM dd, yyyy",
      "MMM dd, yyyy",
    ),
  )
  private val time24hFormat = CheckboxSetting("24h SkyBlock Time", "Convert SkyBlock time lines to 24h where possible.", false)
  private val exactSkyBlockMinutes = CheckboxSetting("SkyBlock Time Exact Minutes", "Display exact SkyBlock minutes when they are available.", false)
  private val dateInLobbyCode = CheckboxSetting("Date in Lobby Code", "Show the current date in front of the server name.", true)
  private val smoothTime = CheckboxSetting("Cache Scoreboard on Island Switch", "Keep time/scoreboard lines visible between server updates.", false)
  private val coloredMonth = CheckboxSetting("Colored Month", "Color season/month date lines.", true)
  private val numberFormat = ModeSetting("Number Format", "Number formatting.", 1, arrayOf("Long", "Short"))
  private val numberDisplayFormat = ModeSetting(
    "Number Display Format",
    "Format prefix/value pairs.",
    0,
    arrayOf("Text: Number", "Colored Text Number", "Number Text", "Number Reset Text"),
  )
  private val showCurrencyGain = CheckboxSetting("Show Currency Gain", "Show temporary currency gain/loss.", true)
  private val showCookieBuff = CheckboxSetting("Cookie Buff", "Show cookie-buff status from tab/list data.", true)
  private val showGodPotion = CheckboxSetting("God Potion", "Show god-potion status from tab/list data.", true)
  private val showBitsAvailable = CheckboxSetting("Bits Available", "Show available bits when found.", true)
  private val showMaxIslandPlayers = CheckboxSetting("Show Max Island Players", "Show the maximum amount of players that can join your current island.", true)
  private val bankAlwaysCompact = CheckboxSetting("Bank Always Compact", "Always compact bank value.", false)
  private val coopBankLayout = ModeSetting("Co-op Bank Layout", "Bank line layout.", 0, arrayOf("Personal/Coop", "Coop/Personal", "Combined Value"))
  private val showPiggy = CheckboxSetting("Piggy Bank", "Use Piggy prefix when a piggy-bank line is found.", false)
  private val hidePurseInDungeons = CheckboxSetting("Hide Purse and Bank in Dungeons", "Hide purse/bank while in dungeons.", false)
  private val slayerLevel = CheckboxSetting("Slayer Level", "Show slayer level/stat lines.", true)
  private val magicalPower = CheckboxSetting("Magical Power", "Show Magical Power with Maxwell power.", true)
  private val compactTuning = CheckboxSetting("Compact Tuning", "Show tuning stats compact.", false)
  private val tuningAmount = SliderSetting("Tuning Amount", "Only show the first number of tunings.", 2.0, 1.0, 8.0, 1.0)
  private val colorArrowAmount = CheckboxSetting("Colored Arrow", "Color quiver amount by remaining arrows.", true)
  private val arrowDisplay = ModeSetting("Arrow Display", "Arrow display type.", 0, arrayOf("Number", "Percentage"))
  private val powderDisplay = ModeSetting("Powder/Whisper Display", "Powder display type.", 0, arrayOf("Current", "Total", "Current/Total"))
  private val showHypixelPowder = CheckboxSetting("Show Hypixel's Powder/Whisper Line", "Keep Hypixel powder lines.", true)
  private val showProfileName = CheckboxSetting("Profile Name", "Show profile name.", false)
  private val showPartyEverywhere = CheckboxSetting("Party Everywhere", "Show party wherever party lines exist.", true)
  private val maxPartyMembers = SliderSetting("Max Party Size", "Maximum party members to show.", 5.0, 1.0, 10.0, 1.0)
  private val showPartyLeader = CheckboxSetting("Show Party Leader", "Show party leader when found.", true)
  private val petPrefix = CheckboxSetting("Add Pet Prefix/Suffix", "Add Pet: prefix.", true)
  private val showPetMax = CheckboxSetting("Show Pet Max Level", "Keep Max pet level text.", true)
  private val showMayorTime = CheckboxSetting("Mayor Time till Election", "Show mayor/election time lines.", true)
  private val mayorPerksDisplay = ModeSetting("Mayor Perks", "Mayor perks display.", 1, arrayOf("Off", "Perk Amount", "All Perks"))
  private val ministerDisplay = ModeSetting("Show Minister", "Minister display.", 1, arrayOf("Off", "Compact", "Show"))
  private val showJerryInMinister = CheckboxSetting("Show Extra Jerry Mayor as Minister", "Treat Jerry as minister when found.", true)
  private val showExtraMayor = CheckboxSetting("Show Extra Mayor", "Show the extra mayor/minister style lines when found.", true)
  private val showActiveOnly = CheckboxSetting("Show Active Elements Only", "Hide empty inactive element lines.", false)
  private val hideIrrelevantLines = CheckboxSetting("Hide non relevant info", "Hide lines that are not relevant to the current location.", true)
  private val condenseConsecutiveSeparators = CheckboxSetting("Condense Consecutive Separator", "Condense separator runs.", true)
  private val hideSeparatorsAtStartEnd = CheckboxSetting("Hide Separators at Start and End", "Trim leading/trailing separators.", true)
  private val unknownLinesWarning = CheckboxSetting("Unknown Lines warning", "Reserved SkyHanni compatibility setting.", true)

  init {
    addSetting(
      enabled,
      hideWhenTab,
      hideWhenChat,
      hideHypixelScoreboard,
      textShadow,
      customLines,
      actions,
      outsideSkyBlock,
      appearance,
      resetAppearance,
      showEvents,
      eventsPriority,
      resetEventsPriority,
      showAllActiveEvents,
      showTablistLines,
      chunkedStats,
      chunkedStatsOrder,
      statsPerLine,
      useHypixelTitle,
      titleAlignment,
      titleUseCustomText,
      titleUseCustomOutsideSkyBlock,
      titleText,
      footerAlignment,
      footerUseCustomText,
      footerText,
      alphaFooterText,
      scale,
      lineSpacing,
      verticalAlignment,
      horizontalAlignment,
      defaultTextAlignment,
      background,
      backgroundColor,
      padding,
      margin,
      radius,
      blurEnabled,
      outlineBlur,
      borderEnabled,
      borderSize,
      borderTop,
      borderBottom,
      borderTopLeft,
      borderTopRight,
      borderBottomLeft,
      borderBottomRight,
      imageBackground,
      imageBackgroundTransparency,
      customImageFile,
      dateFormat,
      time24hFormat,
      exactSkyBlockMinutes,
      dateInLobbyCode,
      smoothTime,
      coloredMonth,
      numberFormat,
      numberDisplayFormat,
      showCurrencyGain,
      showCookieBuff,
      showGodPotion,
      showBitsAvailable,
      showMaxIslandPlayers,
      bankAlwaysCompact,
      coopBankLayout,
      showPiggy,
      hidePurseInDungeons,
      slayerLevel,
      magicalPower,
      compactTuning,
      tuningAmount,
      colorArrowAmount,
      arrowDisplay,
      powderDisplay,
      showHypixelPowder,
      showProfileName,
      showPartyEverywhere,
      maxPartyMembers,
      showPartyLeader,
      petPrefix,
      showPetMax,
      showMayorTime,
      mayorPerksDisplay,
      ministerDisplay,
      showJerryInMinister,
      showExtraMayor,
      showActiveOnly,
      hideIrrelevantLines,
      condenseConsecutiveSeparators,
      hideSeparatorsAtStartEnd,
      unknownLinesWarning,
    )
  }

  @JvmStatic
  fun renderCustomSidebar(graphics: GuiGraphics, objective: Objective?): Boolean {
    if (!enabled.value || objective == null) return false
    if (hideWhenTab.value && mc.options.keyPlayerList.isDown) return false
    if (hideWhenChat.value && mc.screen is ChatScreen) return false
    mc.player ?: return false

    val scoreboard = mc.level?.scoreboard ?: return false
    val rawLines = scoreboard
      .listPlayerScores(objective)
      .asSequence()
      .filterNot { it.isHidden() }
      .sortedByDescending { it.value() }
      .map { score -> scoreboardLine(score) }
      .mapNotNull(::cleanScoreboardComponent)
      .filterNot { strip(it).contains("hypixel.net", ignoreCase = true) }
      .toList()

    if (rawLines.isEmpty()) return false

    val tabLines = collectTabLines()
    val state = ScoreboardState(objective, rawLines, rawLines.map(::strip), tabLines)
    val skyBlockLike = state.isSkyBlockLike()
    if (!outsideSkyBlock.value && !skyBlockLike) return false

    val rows = if (customLines.value && (skyBlockLike || outsideSkyBlock.value)) buildCustomRows(state) else buildRawRows(state)
    if (rows.isEmpty()) return false

    drawRows(graphics, normalizeRows(rows))
    return hideHypixelScoreboard.value
  }

  private fun buildRawRows(state: ScoreboardState): List<ScoreboardRow> {
    val rows = mutableListOf<ScoreboardRow>()
    rows.addAll(titleRows(state))
    state.rawLines.forEach { rows.add(ScoreboardRow(it.copy(), RowKind.NORMAL, defaultAlign())) }
    rows.addAll(footerRows())
    return rows
  }

  private fun buildCustomRows(state: ScoreboardState): List<ScoreboardRow> {
    val used = mutableSetOf<Int>()
    val rows = mutableListOf<ScoreboardRow>()
    val chunkCandidates = mutableListOf<Pair<String, Component>>()
    val inDungeon = state.containsAny("dungeon", "catacombs", "master mode", "the catacombs")

    fun addSeparator() {
      rows.add(ScoreboardRow(separator(), RowKind.SEPARATOR, Align.CENTER))
    }

    fun addElement(label: String, vararg patterns: String, transform: (Component) -> Component = { it.copy() }) {
      val match = state.findUnused(used, *patterns) ?: return
      val line = transform(match.component)
      if (showActiveOnly.value && isInactiveLine(strip(line))) return
      rows.add(ScoreboardRow(line, RowKind.NORMAL, defaultAlign()))
      used.add(match.index)
    }

    fun addStat(label: String, cacheKey: String = label, forceCompact: Boolean = false, vararg patterns: String) {
      val match = state.findUnused(used, *patterns)
      val rawValue = if (match != null) {
        used.add(match.index)
        extractValue(match.clean, label)
      } else {
        val tabLine = state.findTab(*patterns) ?: return
        extractTabValue(tabLine)
      }
      val value = formatNumberText(rawValue, forceCompact)
      if (showActiveOnly.value && isInactiveLine(value)) return
      chunkCandidates.add(label to formatLabelValue(label, value, cacheKey))
    }

    fun addLineFromAny(label: String, cacheKey: String = label, vararg patterns: String, transformText: (String) -> Component = ::plainText) {
      val match = state.findUnused(used, *patterns)
      val line = if (match != null) {
        used.add(match.index)
        transformText(match.clean)
      } else {
        val tabLine = state.findTab(*patterns) ?: return
        formatLabelValue(label, extractTabValue(tabLine), cacheKey)
      }
      if (showActiveOnly.value && isInactiveLine(strip(line))) return
      rows.add(ScoreboardRow(line, RowKind.NORMAL, defaultAlign()))
    }

    rows.addAll(titleRows(state))
    addElement("date", "early spring", "spring ", "summer ", "autumn ", "winter ", "late winter") { formatDateLine(strip(it)) }
    addElement("time", "am", "pm") { formatTimeLine(strip(it)) }
    addElement("island", "island:", "area:", "location:")
    addElement("area", "\u23E3")
    addElement("lobby", "mini", "mega", "limbo")
    addSeparator()

    addObjectiveRows(state, used, rows)
    if (showEvents.value) addEventRows(state, used, rows)
    addSeparator()

    if (!hidePurseInDungeons.value || !inDungeon) {
      state.findUnused(used, "purse:", "piggy:", "coins:", "coin:")?.let {
        used.add(it.index)
        val label = when {
          showPiggy.value && it.clean.contains("piggy", ignoreCase = true) -> "Piggy"
          it.clean.contains("coin", ignoreCase = true) && !it.clean.contains("purse", ignoreCase = true) -> "Coins"
          else -> "Purse"
        }
        val value = formatNumberText(extractValue(it.clean, label), forceCompact = false)
        chunkCandidates.add(label to formatLabelValue(label, value, label))
      } ?: run {
        val tabCoins = state.findTab("purse:", "piggy:", "coins:", "coin:")
        if (tabCoins != null) {
          val label = when {
            showPiggy.value && tabCoins.contains("piggy", ignoreCase = true) -> "Piggy"
            tabCoins.contains("coin", ignoreCase = true) && !tabCoins.contains("purse", ignoreCase = true) -> "Coins"
            else -> "Purse"
          }
          val value = formatNumberText(extractTabValue(tabCoins), forceCompact = false)
          chunkCandidates.add(label to formatLabelValue(label, value, label))
        }
      }
      addStat(formatBankLabel(), "Bank", bankAlwaysCompact.value, "bank:", "coop bank", "co-op bank", "personal bank")
    }
    addStat("Bits", "Bits", false, "bits:")
    if (showBitsAvailable.value) {
      state.findTab("available bits", "bits available")?.let {
        chunkCandidates.add("Available Bits" to formatLabelValue("Available Bits", extractTabValue(it), "Bits"))
      }
    }
    addStat("Gems", "Gems", false, "gems:", "gem:")
    addStat("Copper", "Copper", false, "copper:")
    addStat("Motes", "Motes", false, "motes:")
    appendStats(rows, chunkCandidates)

    if (showCookieBuff.value) addLineFromAny("Cookie", "Cookie", "cookie buff:", "cookie:")
    if (showGodPotion.value) addLineFromAny("God Pot", "God Pot", "god potion:", "god pot:")
    addLineFromAny("Mithril Powder", "Powder", "mithril powder") {
      if (showHypixelPowder.value) formatPowderLine(it) else Component.empty()
    }
    addLineFromAny("Gemstone Powder", "Powder", "gemstone powder") {
      if (showHypixelPowder.value) formatPowderLine(it) else Component.empty()
    }
    addLineFromAny("Glacite Powder", "Powder", "glacite powder") {
      if (showHypixelPowder.value) formatPowderLine(it) else Component.empty()
    }
    addLineFromAny("Whispers", "Powder", "whispers") {
      if (showHypixelPowder.value) formatPowderLine(it) else Component.empty()
    }
    addLineFromAny("Cold", "Cold", "\u2744", "cold:")
    addLineFromAny("Heat", "Heat", "\u2668", "heat:")
    addLineFromAny("North Stars", "North Stars", "north stars:")
    addLineFromAny("Soulflow", "Soulflow", "soulflow:")
    addLineFromAny("Sowdust", "Sowdust", "sowdust:")
    addLineFromAny("Quiver", "Quiver", "quiver:", "arrows:") { formatQuiverLine(it) }
    addLineFromAny("Power", "Power", "power:", "selected power")
    if (magicalPower.value) addLineFromAny("Magical Power", "Power", "magical power:")
    addLineFromAny("Tunings", "Tunings", "tuning", "tunings") { formatTuningLine(it) }
    addLineFromAny("SkyBlock Level", "SkyBlock Level", "skyblock level", "level:")
    addLineFromAny("Chocolate", "Chocolate", "chocolate:")
    addLineFromAny("Fame", "Fame", "fame:")
    addLineFromAny("Pelts", "Pelts", "pelts:")

    if (showTablistLines.value) addTabWidgetRows(state, rows)

    if (showMayorTime.value) addElement("mayor", "mayor:", "election", "minister")
    if (mayorPerksDisplay.value > 0) addElement("mayor perks", "perk", "perks")
    if (ministerDisplay.value > 0) addElement("minister", "minister:")
    if (showExtraMayor.value && showJerryInMinister.value) addElement("jerry", "jerry")
    addElement("slayer", "slayer", "boss slain", "combat xp")
    if (slayerLevel.value) {
      addElement(
        "slayer stats",
        "zombie:",
        "spider:",
        "wolf:",
        "enderman:",
        "blaze:",
        "vampire:",
        "slayer xp:",
        "meter:",
        "rng meter",
      )
    }

    if (actions.value) addActionRows(state, used, rows)
    addFallbackRows(state, used, rows)
    rows.addAll(footerRows())
    return rows
  }

  private fun titleRows(state: ScoreboardState): List<ScoreboardRow> {
    val skyBlockLike = state.isSkyBlockLike()
    val titleLines = when {
      titleUseCustomText.value && (skyBlockLike || titleUseCustomOutsideSkyBlock.value) ->
        parseLegacyLines(titleText.value.ifBlank { "SkyBlock" })
      useHypixelTitle.value -> listOf(state.objective.displayName.copy())
      else -> listOf(Component.literal(titleText.value.ifBlank { "SkyBlock" }).withDefaultColor(textRgb()))
    }
    return titleLines.map { ScoreboardRow(it, RowKind.TITLE, alignment(titleAlignment.value)) }
  }

  private fun footerRows(): List<ScoreboardRow> {
    val footer = if (footerUseCustomText.value) customFooterText() else "dutt client"
    val lines = footer.replace("\\n", "\n").split('\n')
    return lines.map { line ->
      val component = if (line.equals("dutt client", ignoreCase = true)) {
        ChatUtils.buildGradient(line, borderTopLeft.value and 0x00FFFFFF, 0xFF0539F9.toInt() and 0x00FFFFFF)
      } else {
        parseLegacyLine(line)
      }
      ScoreboardRow(component, RowKind.FOOTER, alignment(footerAlignment.value))
    }
  }

  private fun customFooterText(): String {
    val lines = (mc.level?.scoreboard?.let { scoreboard ->
      scoreboard.getDisplayObjective(net.minecraft.world.scores.DisplaySlot.SIDEBAR)?.let { objective ->
        scoreboard.listPlayerScores(objective).map { scoreboardLine(it).string }
      }
    } ?: emptyList()).joinToString(" ")
    val alpha = lines.contains("alpha", ignoreCase = true) || mc.connection?.serverData?.ip?.contains("alpha", ignoreCase = true) == true
    return if (alpha) {
      alphaFooterText.value.ifBlank { footerText.value.ifBlank { "alpha.hypixel.net" } }
    } else {
      footerText.value.ifBlank { "dutt client" }
    }
  }

  private fun addObjectiveRows(state: ScoreboardState, used: MutableSet<Int>, rows: MutableList<ScoreboardRow>) {
    val index = state.cleanLines.indexOfFirst { it.contains("objective", ignoreCase = true) }
    if (index < 0 || !used.add(index)) return
    rows.add(ScoreboardRow(state.rawLines[index].copy(), RowKind.NORMAL, defaultAlign()))
    val next = index + 1
    if (next < state.rawLines.size && used.add(next)) {
      val clean = state.cleanLines[next]
      if (clean.isNotBlank() && !isVisualSeparator(clean)) {
        rows.add(ScoreboardRow(state.rawLines[next].copy(), RowKind.NORMAL, defaultAlign()))
      }
    }
  }

  private fun addEventRows(state: ScoreboardState, used: MutableSet<Int>, rows: MutableList<ScoreboardRow>) {
    var groupsAdded = 0
    val addedTabLines = mutableSetOf<String>()
    for (event in orderedEventDefinitions()) {
      val matches = state.cleanLines
        .mapIndexedNotNull { index, line ->
          if (index !in used && event.keywords.any { line.contains(it, ignoreCase = true) }) index else null
        }
        .take(event.maxLines)
      val tabMatches = state.tabLines
        .filter { line -> event.keywords.any { line.contains(it, ignoreCase = true) } }
        .filter { addedTabLines.add(it.lowercase(Locale.US)) }
        .take(event.maxLines)

      if (matches.isEmpty() && tabMatches.isEmpty()) continue
      if (!showAllActiveEvents.value && groupsAdded > 0) break
      matches.forEach { index ->
        used.add(index)
        rows.add(ScoreboardRow(state.rawLines[index].copy(), RowKind.NORMAL, defaultAlign()))
      }
      tabMatches.forEach { line ->
        rows.add(ScoreboardRow(plainText(line), RowKind.NORMAL, defaultAlign()))
      }
      groupsAdded++
    }
  }

  private fun addTabWidgetRows(state: ScoreboardState, rows: MutableList<ScoreboardRow>) {
    fun addTabRow(label: String, cacheKey: String = label, vararg patterns: String, transform: (String) -> String = { it }) {
      val line = state.findTab(*patterns) ?: return
      val value = transform(extractTabValue(line))
      if (showActiveOnly.value && isInactiveLine(value)) return
      val component = formatLabelValue(label, value, cacheKey)
      val rendered = strip(component)
      if (rows.any { strip(it.component).equals(rendered, ignoreCase = true) }) return
      rows.add(ScoreboardRow(component, RowKind.NORMAL, defaultAlign()))
    }

    if (showProfileName.value) addTabRow("Profile", "Profile", "profile:")
    addTabRow("Cookie", "Cookie", "cookie buff:", "cookie:")
    addTabRow("God Pot", "God Pot", "god potion:", "god pot:")
    addTabRow("Purse", "Purse", "purse:", "piggy:")
    addTabRow("Coins", "Coins", "coins:", "coin:")
    addTabRow("Bank", "Bank", "bank:", "personal bank", "coop bank", "co-op bank")
    addTabRow("Bits", "Bits", "bits:", "available bits", "bits available")
    addTabRow("Gems", "Gems", "gems:", "gem:")
    addTabRow("Copper", "Copper", "copper:")
    addTabRow("Motes", "Motes", "motes:")
    addTabRow("Soulflow", "Soulflow", "soulflow:")
    addTabRow("North Stars", "North Stars", "north stars:")
    addTabRow("Sowdust", "Sowdust", "sowdust:")
    addTabRow("Chocolate", "Chocolate", "chocolate:")
    addTabRow("Fame", "Fame", "fame:")
    addTabRow("Pelts", "Pelts", "pelts:")
    addTabRow("Mithril Powder", "Powder", "mithril powder")
    addTabRow("Gemstone Powder", "Powder", "gemstone powder")
    addTabRow("Glacite Powder", "Powder", "glacite powder")
    addTabRow("Whispers", "Powder", "whispers")
    addTabRow("Cold", "Cold", "cold:", "\u2744")
    addTabRow("Heat", "Heat", "heat:", "\u2668")
    state.findTab("pet:")?.let {
      var value = extractTabValue(it)
      if (!showPetMax.value) value = value.replace(Regex("""\bMax\b""", RegexOption.IGNORE_CASE), "").trim()
      rows.add(ScoreboardRow(formatLabelValue(if (petPrefix.value) "Pet" else "", value, "Pet"), RowKind.NORMAL, defaultAlign()))
    }
    addTabRow("Power", "Power", "power:", "selected power")
    if (magicalPower.value) addTabRow("Magical Power", "Power", "magical power:")
    addTabRow("Tunings", "Tunings", "tuning", "tunings", transform = ::formatTuningText)
    addTabRow("Quiver", "Quiver", "quiver:", "arrows:")
    addTabRow("SkyBlock Level", "SkyBlock Level", "skyblock level")
    addTabRow("Players", "Players", "players:", "player count", transform = ::formatPlayersText)
    if (showMayorTime.value) addTabRow("Mayor", "Mayor", "mayor:", "election", "minister:")
    if (slayerLevel.value) addTabRow("Slayer", "Slayer", "slayer", "zombie:", "spider:", "wolf:", "enderman:", "blaze:", "vampire:")
    addPartyRows(state, rows)
  }

  private fun addActionRows(state: ScoreboardState, used: MutableSet<Int>, rows: MutableList<ScoreboardRow>) {
    val actionIndexes = state.cleanLines.mapIndexedNotNull { index, line ->
      if (index in used) return@mapIndexedNotNull null
      if (actionKeywords.any { line.contains(it, ignoreCase = true) }) index else null
    }
    if (actionIndexes.isEmpty()) return
    rows.add(ScoreboardRow(separator(), RowKind.SEPARATOR, Align.CENTER))
    actionIndexes.take(4).forEach { index ->
      used.add(index)
      rows.add(ScoreboardRow(state.rawLines[index].copy(), RowKind.NORMAL, defaultAlign()))
    }
  }

  private fun addPartyRows(state: ScoreboardState, rows: MutableList<ScoreboardRow>) {
    if (!showPartyEverywhere.value) return
    val partyLines = state.tabLines.filter { it.contains("party", ignoreCase = true) || it.contains("leader:", ignoreCase = true) }
    if (partyLines.isEmpty()) return
    val max = maxPartyMembers.value.toInt().coerceAtLeast(1)
    partyLines.take(max).forEachIndexed { index, line ->
      if (!showPartyLeader.value && line.contains("leader:", ignoreCase = true)) return@forEachIndexed
      rows.add(ScoreboardRow(formatLabelValue(if (index == 0) "Party" else "", line, "Party"), RowKind.NORMAL, defaultAlign()))
    }
  }

  private fun addFallbackRows(state: ScoreboardState, used: MutableSet<Int>, rows: MutableList<ScoreboardRow>) {
    state.rawLines.forEachIndexed { index, component ->
      if (index in used) return@forEachIndexed
      val clean = state.cleanLines[index]
      if (clean.isBlank() || isVisualSeparator(clean) || clean.length <= 2) return@forEachIndexed
      if (rows.count { it.kind == RowKind.NORMAL } >= 60) return@forEachIndexed
      rows.add(ScoreboardRow(component.copy(), RowKind.NORMAL, defaultAlign()))
      used.add(index)
    }
  }

  private fun appendStats(rows: MutableList<ScoreboardRow>, stats: List<Pair<String, Component>>) {
    if (!chunkedStats.value) {
      stats.forEach { rows.add(ScoreboardRow(it.second, RowKind.NORMAL, defaultAlign())) }
      return
    }

    val perLine = statsPerLine.value.toInt().coerceIn(1, 5)
    stats.chunked(perLine).forEach { chunk ->
      val component = Component.empty()
      chunk.forEachIndexed { index, pair ->
        if (index > 0) component.append(Component.literal("  ").withDefaultColor(mutedRgb()))
        component.append(pair.second)
      }
      rows.add(ScoreboardRow(component, RowKind.NORMAL, defaultAlign()))
    }
  }

  private fun normalizeRows(rows: List<ScoreboardRow>): List<ScoreboardRow> {
    val seenNormal = mutableSetOf<String>()
    val filtered = rows.filterNot { row ->
      val clean = strip(row.component)
      if (row.kind == RowKind.NORMAL && clean.isBlank()) return@filterNot true
      row.kind == RowKind.NORMAL && !seenNormal.add(clean.lowercase(Locale.US))
    }.toMutableList()

    if (condenseConsecutiveSeparators.value) {
      var index = 1
      while (index < filtered.size) {
        if (filtered[index].kind == RowKind.SEPARATOR && filtered[index - 1].kind == RowKind.SEPARATOR) {
          filtered.removeAt(index)
        } else {
          index++
        }
      }
    }

    if (hideSeparatorsAtStartEnd.value) {
      while (filtered.firstOrNull()?.kind == RowKind.SEPARATOR) filtered.removeAt(0)
      while (filtered.lastOrNull()?.kind == RowKind.SEPARATOR) filtered.removeAt(filtered.lastIndex)
    }
    return filtered
  }

  private fun drawRows(graphics: GuiGraphics, rows: List<ScoreboardRow>) {
    val font = mc.font
    val s = scale.value.toFloat().coerceAtLeast(0.1f)
    val pad = padding.value.toInt()
    val spacing = lineSpacing.value.toInt()
    val lineHeight = font.lineHeight + spacing
    val width = rows.maxOf { font.width(it.component) } + pad * 2
    val height = rows.size * lineHeight - spacing + pad * 2
    val x = horizontalPosition(graphics.guiWidth(), width, s)
    val y = verticalPosition(graphics.guiHeight(), height, s)

    graphics.pose().pushMatrix()
    graphics.pose().translate(x.toFloat(), y.toFloat())
    graphics.pose().scale(s, s)

    if (blurEnabled.value) {
      graphics.fill(-2, -2, width + 2, height + 2, 0x33000000)
    }
    val roundedRadius = radius.value.toInt().coerceIn(0, (width.coerceAtMost(height) / 2))
    val outlineSize = borderSize.value.toInt().coerceIn(0, 10)
    if (borderEnabled.value && outlineSize > 0) {
      drawRoundedOutline(graphics, width, height, roundedRadius, outlineSize)
    }
    if (background.value) {
      val bg = applyAlpha(backgroundColor.value, imageBackgroundTransparency.value.toInt())
      val inset = if (borderEnabled.value && outlineSize > 0) outlineSize else 0
      fillRoundedRect(
        graphics,
        inset,
        inset,
        width - inset * 2,
        height - inset * 2,
        (roundedRadius - inset).coerceAtLeast(0),
        bg,
      )
    }
    if (imageBackground.value && customImageFile.value.isNotBlank()) {
      val inset = if (borderEnabled.value && outlineSize > 0) outlineSize else 0
      fillRoundedRect(
        graphics,
        inset,
        inset,
        width - inset * 2,
        height - inset * 2,
        (roundedRadius - inset).coerceAtLeast(0),
        applyAlpha(0xFF000000.toInt(), (100 - imageBackgroundTransparency.value.toInt()).coerceIn(0, 100)),
      )
    }

    var rowY = pad
    rows.forEach { row ->
      val rowWidth = font.width(row.component)
      val rowX = when (row.alignment) {
        Align.LEFT -> pad
        Align.CENTER -> (width - rowWidth) / 2
        Align.RIGHT -> width - pad - rowWidth
      }
      graphics.drawString(font, row.component, rowX, rowY, textRgb(), textShadow.value)
      rowY += lineHeight
    }

    graphics.pose().popMatrix()
  }

  private fun horizontalPosition(screenWidth: Int, width: Int, scale: Float): Int {
    val m = margin.value.toInt()
    val scaled = width * scale
    return when (horizontalAlignment.value) {
      0 -> m
      1 -> ((screenWidth - scaled) / 2f).toInt()
      else -> (screenWidth - m - scaled).toInt()
    }
  }

  private fun verticalPosition(screenHeight: Int, height: Int, scale: Float): Int {
    val m = margin.value.toInt()
    val scaled = height * scale
    return when (verticalAlignment.value) {
      0 -> m
      2 -> (screenHeight - m - scaled).toInt()
      else -> ((screenHeight - scaled) / 2f).toInt()
    }
  }

  private fun fillRoundedRect(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, radius: Int, color: Int) {
    if (width <= 0 || height <= 0) return
    val r = radius.coerceIn(0, (width.coerceAtMost(height) / 2))
    if (r <= 0) {
      graphics.fill(x, y, x + width, y + height, color)
      return
    }
    for (row in 0 until height) {
      val inset = roundedInset(row, height, r)
      graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color)
    }
  }

  private fun drawRoundedOutline(graphics: GuiGraphics, width: Int, height: Int, radius: Int, size: Int) {
    if (width <= 0 || height <= 0 || size <= 0) return
    val r = radius.coerceIn(0, (width.coerceAtMost(height) / 2))
    if (background.value) {
      drawRoundedGradientRect(graphics, 0, 0, width, height, r)
      return
    }

    repeat(size) { offset ->
      val innerWidth = width - offset * 2
      val innerHeight = height - offset * 2
      if (innerWidth <= 0 || innerHeight <= 0) return@repeat
      val innerRadius = (r - offset).coerceAtLeast(0)
      for (row in 0 until innerHeight) {
        val inset = roundedInset(row, innerHeight, innerRadius)
        val y = offset + row
        val left = offset + inset
        val right = width - offset - inset
        val colorLeft = lerpArgb(borderTop.value, borderBottom.value, row.toFloat() / (innerHeight - 1).coerceAtLeast(1))
        val colorRight = lerpArgb(borderTop.value, borderBottom.value, row.toFloat() / (innerHeight - 1).coerceAtLeast(1))
        if (row < size || row >= innerHeight - size) {
          graphics.fillGradient(left, y, right, y + 1, colorLeft, colorRight)
        } else {
          graphics.fill(left, y, (left + size).coerceAtMost(right), y + 1, colorLeft)
          graphics.fill((right - size).coerceAtLeast(left), y, right, y + 1, colorRight)
        }
      }
    }
  }

  private fun drawRoundedGradientRect(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, radius: Int) {
    val r = radius.coerceIn(0, (width.coerceAtMost(height) / 2))
    for (row in 0 until height) {
      val inset = roundedInset(row, height, r)
      val t = row.toFloat() / (height - 1).coerceAtLeast(1)
      val left = lerpArgb(borderTop.value, borderBottom.value, t)
      val right = lerpArgb(borderTop.value, borderBottom.value, t)
      graphics.fillGradient(x + inset, y + row, x + width - inset, y + row + 1, left, right)
    }
  }

  private fun roundedInset(row: Int, height: Int, radius: Int): Int {
    if (radius <= 0) return 0
    val topDistance = radius - row - 1
    val bottomDistance = row - (height - radius)
    val distance = maxOf(topDistance, bottomDistance)
    if (distance <= 0) return 0
    val inside = sqrt((radius * radius - distance * distance).coerceAtLeast(0).toDouble())
    return (radius - inside).toInt().coerceAtLeast(0)
  }

  private fun lerpArgb(start: Int, end: Int, t: Float): Int {
    val ratio = t.coerceIn(0f, 1f)
    fun channel(color: Int, shift: Int): Int = (color ushr shift) and 0xFF
    fun mix(a: Int, b: Int): Int = (a + ((b - a) * ratio)).toInt().coerceIn(0, 255)
    val a = mix(channel(start, 24), channel(end, 24))
    val r = mix(channel(start, 16), channel(end, 16))
    val g = mix(channel(start, 8), channel(end, 8))
    val b = mix(channel(start, 0), channel(end, 0))
    return (a shl 24) or (r shl 16) or (g shl 8) or b
  }

  private fun cleanScoreboardComponent(component: Component): Component? {
    val clean = strip(component)
    if (clean.isBlank()) return null
    return component
  }

  private fun scoreboardLine(score: PlayerScoreEntry): Component {
    score.display()?.let { return it.copy() }
    val owner = score.owner()
    val team = mc.level?.scoreboard?.getPlayersTeam(owner)
    return if (team == null) {
      score.ownerName().copy()
    } else {
      Component.empty()
        .append(team.playerPrefix.copy())
        .append(Component.literal(owner))
        .append(team.playerSuffix.copy())
    }
  }

  private fun collectTabLines(): List<String> {
    val connection = mc.connection ?: return emptyList()
    val lines = mutableListOf<String>()
    val overlay = mc.gui.tabList as? TabOverlayAccessor
    overlay?.header?.string?.let { lines.addAll(splitTabText(it)) }
    overlay?.footer?.string?.let { lines.addAll(splitTabText(it)) }
    lines += connection.onlinePlayers
      .mapNotNull { info -> info.tabListDisplayName?.string ?: info.profile.name }
      .flatMap(::splitTabText)
    return lines.distinctBy { it.lowercase(Locale.US) }
  }

  private fun splitTabText(text: String): List<String> {
    return ChatFormatting.stripFormatting(text).orEmpty()
      .replace("\r", "\n")
      .split('\n')
      .map { it.replace(Regex("""\s+"""), " ").trim() }
      .filter { it.isNotEmpty() }
  }

  private fun formatDateLine(text: String): Component {
    val formatted = if (dateFormat.value >= 0) text else LocalDate.now().format(dateFormatter())
    if (!coloredMonth.value) return Component.literal(formatted).withDefaultColor(textRgb())
    val color = seasonColors.entries.firstOrNull { text.contains(it.key, ignoreCase = true) }?.value ?: textRgb()
    return Component.literal(formatted).withDefaultColor(color)
  }

  private fun formatTimeLine(text: String): Component {
    if (!time24hFormat.value) return Component.literal(text).withDefaultColor(textRgb())
    val match = Regex("""(?i)\b(\d{1,2}):(\d{2})\s*([ap]m)\b""").find(text) ?: return Component.literal(text).withDefaultColor(textRgb())
    var hour = match.groupValues[1].toIntOrNull() ?: return Component.literal(text).withDefaultColor(textRgb())
    val minute = match.groupValues[2]
    val marker = match.groupValues[3].lowercase()
    if (marker == "pm" && hour != 12) hour += 12
    if (marker == "am" && hour == 12) hour = 0
    return Component.literal(text.replace(match.value, "%02d:%s".format(hour, minute))).withDefaultColor(textRgb())
  }

  private fun formatPowderLine(text: String): Component {
    val value = when (powderDisplay.value) {
      1 -> text.replace(Regex("""(?i)\bcurrent\b"""), "Total")
      2 -> text
      else -> text.substringBefore("/").trim()
    }
    return Component.literal(value).withDefaultColor(textRgb())
  }

  private fun formatQuiverLine(text: String): Component {
    val value = if (arrowDisplay.value == 1) {
      val nums = numberRegex.findAll(text).mapNotNull { parseNumber(it.value) }.toList()
      if (nums.size >= 2 && nums[1] != 0.0) "${((nums[0] / nums[1]) * 100.0).roundToLong()}%" else text
    } else {
      text
    }
    val color = if (!colorArrowAmount.value) textRgb() else {
      val first = numberRegex.find(value)?.value?.let(::parseNumber) ?: 1.0
      when {
        first <= 16.0 -> 0xFF5555
        first <= 64.0 -> 0xFFFF55
        else -> 0x55FF55
      }
    }
    return Component.literal(value).withDefaultColor(color)
  }

  private fun formatTuningLine(text: String): Component {
    return Component.literal(formatTuningText(text)).withDefaultColor(textRgb())
  }

  private fun formatTuningText(text: String): String {
    if (compactTuning.value) {
      return text.replace(Regex("""\s*[|/]\s*"""), " ").replace(Regex("""\s+"""), " ").trim()
    }
    val amount = tuningAmount.value.toInt().coerceIn(1, 8)
    val parts = text.split(Regex("""\s*[|/]\s*""")).map { it.trim() }.filter { it.isNotEmpty() }
    return if (parts.size > amount) parts.take(amount).joinToString(" / ") else text
  }

  private fun formatPlayersText(text: String): String {
    if (showMaxIslandPlayers.value) return text
    return text.replace(Regex("""\s*/\s*\d+\b"""), "")
  }

  private fun formatNumberText(value: String, forceCompact: Boolean = false): String {
    if (!forceCompact && numberFormat.value == 0) return value
    return numberRegex.replace(value) { match ->
      val number = parseNumber(match.value) ?: return@replace match.value
      compactNumber(number)
    }
  }

  private fun orderedEventDefinitions(): List<EventDefinition> {
    val byKey = eventDefinitions.associateBy { normalizeToken(it.name) }
    val ordered = parseTokenList(eventsPriority.value).mapNotNull { byKey[normalizeToken(it)] }
    return ordered + eventDefinitions.filterNot { event -> ordered.any { it.name == event.name } }
  }

  private fun parseTokenList(value: String): List<String> {
    return value.split(',', ';', '\n')
      .map { it.trim() }
      .filter { it.isNotEmpty() }
  }

  private fun normalizeToken(value: String): String {
    return value.lowercase(Locale.US).filter { it.isLetterOrDigit() }
  }

  private fun formatLabelValue(label: String, value: String, cacheKey: String): MutableComponent {
    val cleanLabel = label.takeIf { it.isNotBlank() }
    val displayValue = if (showCurrencyGain.value) withCurrencyChange(cacheKey, value) else value
    val color = elementColor(cacheKey)
    return when (numberDisplayFormat.value) {
      1 -> Component.empty()
        .append(Component.literal(cleanLabel?.let { "$it: " }.orEmpty()).withDefaultColor(color))
        .append(Component.literal(displayValue).withDefaultColor(textRgb()))
      2 -> Component.empty()
        .append(Component.literal(displayValue).withDefaultColor(color))
        .append(Component.literal(cleanLabel?.let { " $it" }.orEmpty()).withDefaultColor(textRgb()))
      3 -> Component.empty()
        .append(Component.literal(displayValue).withDefaultColor(color))
        .append(Component.literal(cleanLabel?.let { " $it" }.orEmpty()).withDefaultColor(textRgb()))
      else -> Component.empty()
        .append(Component.literal(cleanLabel?.let { "$it: " }.orEmpty()).withDefaultColor(textRgb()))
        .append(Component.literal(displayValue).withDefaultColor(color))
    }
  }

  private fun withCurrencyChange(key: String, value: String): String {
    if (!showCurrencyGain.value) return value
    val number = numberRegex.findAll(value).lastOrNull()?.value?.let(::parseNumber) ?: return value
    val now = System.currentTimeMillis()
    val previous = changeCache[key]
    val diff = previous?.let { number - it.value } ?: 0.0
    val changedAt = if (abs(diff) > 0.0001) now else previous?.changedAt ?: now
    changeCache[key] = NumberChange(number, if (abs(diff) > 0.0001) diff else previous?.difference ?: 0.0, changedAt)
    val activeDiff = changeCache[key]?.difference ?: 0.0
    val age = now - changedAt
    if (abs(activeDiff) <= 0.0001 || age > 3000L) return value
    val sign = if (activeDiff > 0) "+" else "-"
    return "$value ($sign${compactNumber(abs(activeDiff))})"
  }

  private fun extractValue(text: String, label: String): String {
    val colon = text.indexOf(':')
    if (colon >= 0 && colon + 1 < text.length) return text.substring(colon + 1).trim()
    return text.replace(label, "", ignoreCase = true).trim().ifBlank { text }
  }

  private fun extractTabValue(text: String): String {
    val compact = text.replace(Regex("""\s+"""), " ").trim()
    val colon = compact.indexOf(':')
    if (colon >= 0 && colon + 1 < compact.length) return compact.substring(colon + 1).trim()
    val prefix = tabValuePrefixes.firstOrNull { compact.startsWith(it, ignoreCase = true) }
    if (prefix != null) {
      return compact.substring(prefix.length).trim(' ', '-', ':').ifBlank { compact }
    }
    return compact
  }

  private fun plainText(text: String): MutableComponent {
    return Component.literal(text).withDefaultColor(textRgb())
  }

  private fun parseLegacyLines(text: String): List<Component> {
    return text.replace("\\n", "\n").split('\n').map(::parseLegacyLine)
  }

  private fun parseLegacyLine(text: String): MutableComponent {
    val component = Component.empty()
    var color = textRgb()
    var index = 0
    while (index < text.length) {
      val char = text[index]
      if ((char == '&' || char == '\u00A7') && index + 1 < text.length) {
        ChatFormatting.getByCode(text[index + 1].lowercaseChar())?.color?.let { color = it }
        index += 2
        continue
      }
      component.append(Component.literal(char.toString()).withDefaultColor(color))
      index++
    }
    return component
  }

  private fun separator(): MutableComponent {
    return Component.literal("\u2500".repeat(12)).withDefaultColor(mutedRgb())
  }

  private fun dateFormatter(): DateTimeFormatter {
    val pattern = when (dateFormat.value) {
      0 -> "yyyy-MM-dd"
      1 -> "yyyy MMMM dd"
      2 -> "MM/dd/yyyy"
      3 -> "MM-dd-yyyy"
      4 -> "dd/MM/yyyy"
      5 -> "dd-MM-yyyy"
      6 -> "dd MMMM yyyy"
      7 -> "dd MMM yyyy"
      9 -> "MMM dd, yyyy"
      else -> "MMMM dd, yyyy"
    }
    return DateTimeFormatter.ofPattern(pattern, Locale.US)
  }

  private fun formatBankLabel(): String {
    return when (coopBankLayout.value) {
      1 -> "Coop/Personal"
      2 -> "Bank"
      else -> "Personal/Coop"
    }
  }

  private fun compactNumber(number: Double): String {
    val absNumber = abs(number)
    val suffix = when {
      absNumber >= 1_000_000_000_000.0 -> "T" to 1_000_000_000_000.0
      absNumber >= 1_000_000_000.0 -> "B" to 1_000_000_000.0
      absNumber >= 1_000_000.0 -> "M" to 1_000_000.0
      absNumber >= 1_000.0 -> "k" to 1_000.0
      else -> "" to 1.0
    }
    val scaled = number / suffix.second
    return if (suffix.first.isEmpty()) {
      scaled.roundToLong().toString()
    } else {
      val formatted = if (abs(scaled) >= 100.0) "%.0f" else "%.1f"
      formatted.format(Locale.US, scaled).trimEnd('0').trimEnd('.') + suffix.first
    }
  }

  private fun parseNumber(text: String): Double? {
    val multiplier = when (text.lastOrNull()?.lowercaseChar()) {
      'k' -> 1_000.0
      'm' -> 1_000_000.0
      'b' -> 1_000_000_000.0
      't' -> 1_000_000_000_000.0
      else -> 1.0
    }
    val cleaned = text.trim().removeSuffix("k").removeSuffix("K")
      .removeSuffix("m").removeSuffix("M")
      .removeSuffix("b").removeSuffix("B")
      .removeSuffix("t").removeSuffix("T")
      .replace(",", "")
    return cleaned.toDoubleOrNull()?.times(multiplier)
  }

  private fun isInactiveLine(text: String): Boolean {
    val lower = text.lowercase()
    return lower.isBlank() ||
      lower == "0" ||
      lower.endsWith(": 0") ||
      lower.contains("inactive") ||
      lower.contains("not active") ||
      lower.contains("none")
  }

  private fun isVisualSeparator(text: String): Boolean {
    val stripped = text.trim()
    return stripped.isNotEmpty() && stripped.all { it == '-' || it == '\u2500' || it == '=' || it == ' ' }
  }

  private fun ScoreboardState.isSkyBlockLike(): Boolean {
    return containsAny(
      "skyblock",
      "purse:",
      "piggy:",
      "coins:",
      "coin:",
      "bank:",
      "bits:",
      "available bits",
      "gems:",
      "gem:",
      "copper:",
      "motes:",
      "soulflow",
      "north stars",
      "sowdust",
      "quiver",
      "magical power",
      "mithril powder",
      "gemstone powder",
      "glacite powder",
      "whispers",
      "jacob",
      "kuudra",
      "rift",
      "garden",
      "dungeon",
      "slayer",
      "mayor",
      "objective",
      "cookie buff",
      "god potion",
      "chocolate:",
      "fame:",
      "pelts:",
      "profile:",
      "\u23E3",
    )
  }

  private fun ScoreboardState.containsAny(vararg needles: String): Boolean {
    return cleanLines.any { line -> needles.any { line.contains(it, ignoreCase = true) } } ||
      tabLines.any { line -> needles.any { line.contains(it, ignoreCase = true) } }
  }

  private fun ScoreboardState.findUnused(used: Set<Int>, vararg patterns: String): LineMatch? {
    return cleanLines.mapIndexedNotNull { index, line ->
      if (index in used) return@mapIndexedNotNull null
      if (patterns.any { line.contains(it, ignoreCase = true) }) LineMatch(index, rawLines[index], line) else null
    }.firstOrNull()
  }

  private fun ScoreboardState.findTab(vararg patterns: String): String? {
    return tabLines.firstOrNull { line -> patterns.any { line.contains(it, ignoreCase = true) } }
  }

  private fun strip(component: Component): String {
    return ChatFormatting.stripFormatting(component.string).orEmpty().trim()
  }

  private fun Component.withDefaultColor(color: Int): MutableComponent {
    return this.copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color and 0x00FFFFFF)))
  }

  private fun textRgb(): Int = 0xFFEAF2FF.toInt()

  private fun mutedRgb(): Int = 0xFF8E9AA8.toInt()

  private fun elementColor(key: String): Int {
    return when {
      key.contains("purse", ignoreCase = true) || key.contains("bank", ignoreCase = true) -> 0xFFAA00
      key.contains("bits", ignoreCase = true) -> 0x55FFFF
      key.contains("gems", ignoreCase = true) -> 0x55FF55
      key.contains("copper", ignoreCase = true) -> 0xFFAA55
      key.contains("motes", ignoreCase = true) -> 0xAA55FF
      key.contains("power", ignoreCase = true) -> 0x55AAFF
      key.contains("slayer", ignoreCase = true) -> 0xFF5555
      else -> textRgb()
    }
  }

  private fun applyAlpha(color: Int, alphaPercent: Int): Int {
    val alpha = (alphaPercent.coerceIn(0, 100) * 255 / 100) and 0xFF
    return (color and 0x00FFFFFF) or (alpha shl 24)
  }

  private fun alignment(index: Int): Align = when (index) {
    1 -> Align.CENTER
    2 -> Align.RIGHT
    else -> Align.LEFT
  }

  private fun defaultAlign(): Align = alignment(defaultTextAlignment.value)

  private val eventDefinitions = listOf(
    EventDefinition("Anniversary", listOf("anniversary", "party time")),
    EventDefinition("Broodmother", listOf("broodmother", "arachne")),
    EventDefinition("Carnival", listOf("carnival")),
    EventDefinition("Damage", listOf("damage", "dps")),
    EventDefinition("Dark Auction", listOf("dark auction", "starting in", "current item", "time left")),
    EventDefinition("Dojo", listOf("dojo", "discipline", "belt")),
    EventDefinition("Dungeons", listOf("dungeon", "catacombs", "cleared:", "secrets", "teammates", "starting in")),
    EventDefinition("Essence", listOf("essence")),
    EventDefinition("Flight Duration", listOf("flight duration", "flight")),
    EventDefinition("Galatea", listOf("galatea", "forest")),
    EventDefinition("Garden", listOf("garden", "pests", "visitor", "composter", "sprayonator", "plot")),
    EventDefinition("Jacob Medals", listOf("jacob", "medals", "contest")),
    EventDefinition("Jacob's Contest", listOf("jacob's contest", "jacobs contest", "collected")),
    EventDefinition("Kuudra", listOf("kuudra", "supplies", "tokens")),
    EventDefinition("Magma Boss", listOf("magma boss")),
    EventDefinition("Mining", listOf("raffle", "goblin", "mithril", "mineshaft", "fossil", "wind compass", "nearby players")),
    EventDefinition("New Year", listOf("new year", "cake")),
    EventDefinition("Queue", listOf("queue", "position")),
    EventDefinition("Redstone", listOf("redstone")),
    EventDefinition("Rift", listOf("rift", "hot dog", "clues", "protest", "effigy")),
    EventDefinition("Server Restart", listOf("restart", "server closing")),
    EventDefinition("Spooky", listOf("spooky", "candy")),
    EventDefinition("Trapper", listOf("trapper", "pelts", "trackable")),
    EventDefinition("Voting", listOf("voting", "vote")),
    EventDefinition("Winter", listOf("winter", "jerry", "gifts")),
  )

  private val seasonColors = mapOf(
    "spring" to 0x55FF55,
    "summer" to 0xFFFF55,
    "autumn" to 0xFFAA00,
    "winter" to 0x55FFFF,
  )

  private val numberRegex = Regex("""-?\d[\d,]*(?:\.\d+)?[kKmMbBtT]?""")

  private val tabValuePrefixes = listOf(
    "Available Bits",
    "Bits Available",
    "Cookie Buff",
    "God Potion",
    "God Pot",
    "Selected Power",
    "Magical Power",
    "SkyBlock Level",
    "Mithril Powder",
    "Gemstone Powder",
    "Glacite Powder",
    "North Stars",
    "Player Count",
    "Personal Bank",
    "Coop Bank",
    "Co-op Bank",
    "Profile",
    "Cookie",
    "Purse",
    "Piggy",
    "Coins",
    "Bank",
    "Bits",
    "Gems",
    "Gem",
    "Copper",
    "Motes",
    "Soulflow",
    "Sowdust",
    "Chocolate",
    "Fame",
    "Pelts",
    "Whispers",
    "Cold",
    "Heat",
    "Pet",
    "Power",
    "Tuning",
    "Tunings",
    "Quiver",
    "Arrows",
    "Players",
    "Mayor",
    "Minister",
    "Slayer",
    "Zombie",
    "Spider",
    "Wolf",
    "Enderman",
    "Blaze",
    "Vampire",
  )

  private val actionKeywords = listOf(
    "click",
    "right-click",
    "left-click",
    "warp",
    "join",
    "queue",
    "rejoin",
    "play again",
    "claim",
    "vote",
    "visit",
    "open",
  )

  private data class ScoreboardState(
    val objective: Objective,
    val rawLines: List<Component>,
    val cleanLines: List<String>,
    val tabLines: List<String>,
  )

  private data class LineMatch(val index: Int, val component: Component, val clean: String)

  private data class ScoreboardRow(val component: Component, val kind: RowKind, val alignment: Align)

  private data class EventDefinition(val name: String, val keywords: List<String>, val maxLines: Int = 6)

  private data class NumberChange(val value: Double, val difference: Double, val changedAt: Long)

  private enum class RowKind {
    TITLE,
    NORMAL,
    SEPARATOR,
    FOOTER,
  }

  private enum class Align {
    LEFT,
    CENTER,
    RIGHT,
  }
}
