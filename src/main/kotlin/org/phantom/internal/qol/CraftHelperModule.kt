package org.phantom.internal.qol

import java.util.Locale
import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.HudModuleManager
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.setting.impl.ActionSetting
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.module.setting.inGroup
import org.phantom.api.ui.theme.ThemeGradient
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.ui.theme.ThemeSurface
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.getSkyblockId
import org.phantom.api.util.ui.NVGRenderer

object CraftHelperModule : Module("Craft Helper") {

  private data class ProgressNode(
    val id: String,
    val displayName: String,
    val required: Int,
    val directAmount: Int,
    val carryOver: Int,
    val outputCount: Int,
    val hasRecipe: Boolean,
    val loading: Boolean,
    val cycleDetected: Boolean,
    val depthLimited: Boolean,
    val children: List<ProgressNode>,
  ) {
    val available: Int
      get() = directAmount + carryOver

    val done: Boolean
      get() = available >= required

    val descendantsLoading: Boolean
      get() = loading || children.any { it.descendantsLoading }

    val craftable: Boolean
      get() = done || (
        !loading &&
          !cycleDetected &&
          !depthLimited &&
          hasRecipe &&
          children.isNotEmpty() &&
          children.all { it.done || it.craftable }
        )
  }

  private data class DisplayLine(
    val text: String,
    val color: Int,
    val size: Float = BODY_TEXT_SIZE,
  )

  private data class DisplayState(
    val title: String,
    val subtitle: String,
    val titleColor: Int,
    val lines: List<DisplayLine>,
  )

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Enable the SkyOcean-style craft helper overlay.",
    false,
  )

  private val selectionInfo = InfoSetting(
    "Selection",
    "Set the target with the held-item button or /phantom craftheld.",
    InfoType.INFO,
  ).inGroup("Selection")

  private val targetIdSetting = TextSetting(
    "Target Item ID",
    "SkyBlock internal item id from the held item or NEU repo.",
    "",
  ).inGroup("Selection")

  private val targetAmountSetting = SliderSetting(
    "Target Amount",
    "How many of the selected item you want to make.",
    1.0,
    1.0,
    10_000.0,
    step = 1.0,
  ).inGroup("Selection")

  private val setHeldItemAction = ActionSetting(
    "Set Held Item",
    "Use your currently held SkyBlock item as the craft helper target.",
    "Set",
  ) {
    setTargetFromHeld()
  }.inGroup("Selection")

  private val clearTargetAction = ActionSetting(
    "Clear Target",
    "Clear the current craft helper target.",
    "Clear",
  ) {
    clearTarget()
  }.inGroup("Selection")

  private val displayInfo = InfoSetting(
    "Display",
    "This first version tracks your player inventory only.",
    InfoType.INFO,
  ).inGroup("Display")

  private val hideCompletedSetting = CheckboxSetting(
    "Hide Completed",
    "Hide finished child lines to keep the tree compact.",
    false,
  ).inGroup("Display")

  private val backgroundSetting = CheckboxSetting(
    "Background",
    "Draw a panel behind the craft helper text.",
    true,
  ).inGroup("Display")

  private val maxDepthSetting = SliderSetting(
    "Max Depth",
    "Maximum recipe recursion depth before stopping expansion.",
    6.0,
    1.0,
    12.0,
    step = 1.0,
  ).inGroup("Display")

  private val maxLinesSetting = SliderSetting(
    "Max Lines",
    "Maximum number of recipe lines shown in the HUD.",
    18.0,
    6.0,
    40.0,
    step = 1.0,
  ).inGroup("Display")

  @Volatile
  private var displayState = buildIdleState()

  @Volatile
  private var manualTargetName: String? = null

  @Volatile
  private var lastRefreshMs = 0L

  private val craftHud = hudElement(
    "craft-helper-hud",
    "Craft Helper HUD",
    "Shows the selected craft tree and inventory progress.",
  ) {
    anchor = HudAnchor.TOP_RIGHT
    offsetX = 10f
    offsetY = 150f
    minScale = 0.8f
    maxScale = 1.6f

    width { panelWidth(currentDisplayState()) }
    height { panelHeight(currentDisplayState()) }

    render { _, _, _ ->
      val state = currentDisplayState()
      renderPanel(state)
    }
  }

  init {
    addSetting(
      enabledSetting,
      selectionInfo,
      targetIdSetting,
      targetAmountSetting,
      setHeldItemAction,
      clearTargetAction,
      displayInfo,
      hideCompletedSetting,
      backgroundSetting,
      maxDepthSetting,
      maxLinesSetting,
    )

    EventBus.register(this)
  }

  fun setTargetFromHeld(notify: Boolean = true): Boolean {
    val player = mc.player ?: run {
      if (notify) ChatUtils.sendMessage("Craft Helper: No world loaded.")
      return false
    }
    val held = player.mainHandItem
    if (held.isEmpty) {
      if (notify) ChatUtils.sendMessage("Craft Helper: Hold a SkyBlock item first.")
      return false
    }

    val itemId = held.getSkyblockId().trim()
    if (itemId.isEmpty()) {
      if (notify) ChatUtils.sendMessage("Craft Helper: The held item has no SkyBlock ID.")
      return false
    }

    manualTargetName = (ChatFormatting.stripFormatting(held.hoverName.string) ?: held.hoverName.string).trim()
    setTarget(itemId, 1, notify)
    return true
  }

  fun setTarget(id: String, amount: Int = currentAmount(), notify: Boolean = true) {
    val normalizedId = NeuRecipeService.normalizeId(id)
    if (normalizedId.isEmpty()) {
      clearTarget(notify)
      return
    }

    enabledSetting.value = true
    targetIdSetting.value = normalizedId
    targetAmountSetting.value = amount.coerceIn(1, 10_000).toDouble()
    NeuRecipeService.requestItem(normalizedId)
    refreshNow()

    if (!notify) return
    val name = NeuRecipeService.getCachedItem(normalizedId)?.displayName ?: manualTargetName ?: normalizedId
    ChatUtils.sendMessage("Craft Helper target set to ${currentAmount()}x $name.")
  }

  fun setTargetAmount(amount: Int, notify: Boolean = true) {
    val clamped = amount.coerceIn(1, 10_000)
    targetAmountSetting.value = clamped.toDouble()
    refreshNow()
    if (notify) {
      ChatUtils.sendMessage("Craft Helper amount set to $clamped.")
    }
  }

  fun clearTarget(notify: Boolean = true) {
    targetIdSetting.value = ""
    targetAmountSetting.value = 1.0
    manualTargetName = null
    refreshNow()
    if (notify) {
      ChatUtils.sendMessage("Craft Helper target cleared.")
    }
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
    val now = System.currentTimeMillis()
    if (now - lastRefreshMs < REFRESH_INTERVAL_MS) return
    lastRefreshMs = now
    rebuildDisplayState()
  }

  private fun refreshNow() {
    lastRefreshMs = 0L
    rebuildDisplayState()
  }

  private fun rebuildDisplayState() {
    displayState = when {
      !enabledSetting.value && !HudModuleManager.isEditorOpen -> buildIdleState()
      currentTargetId() == null -> buildNoTargetState()
      else -> buildActiveState()
    }
  }

  private fun buildActiveState(): DisplayState {
    val targetId = currentTargetId() ?: return buildNoTargetState()
    val rootItem = NeuRecipeService.getCachedItem(targetId)
    if (rootItem == null) {
      NeuRecipeService.requestItem(targetId)
      return DisplayState(
        title = "Craft Helper",
        subtitle = "${currentAmount()}x ${manualTargetName ?: NeuRecipeService.prettyId(targetId)}",
        titleColor = COLOR_LOADING,
        lines = listOf(
          DisplayLine("LOAD Waiting for recipe data...", COLOR_LOADING),
          DisplayLine("Source: NEU item repo", COLOR_MUTED, SMALL_TEXT_SIZE),
        ),
      )
    }

    val player = mc.player
    if (player == null) {
      return DisplayState(
        title = "Craft Helper",
        subtitle = "${currentAmount()}x ${rootItem.displayName}",
        titleColor = ThemeManager.currentTheme.accent,
        lines = listOf(
          DisplayLine("MISS Join a world to evaluate inventory.", COLOR_MISSING),
        ),
      )
    }

    val inventory = collectInventoryCounts()
    val remainders = HashMap<String, Int>()
    val rootNode = buildProgressNode(
      id = targetId,
      required = currentAmount(),
      inventory = inventory,
      remainders = remainders,
      visited = emptySet(),
      depth = 0,
    )

    val titleColor = when {
      rootNode.done -> COLOR_READY
      rootNode.descendantsLoading -> COLOR_LOADING
      rootNode.craftable -> COLOR_CRAFTABLE
      else -> COLOR_MISSING
    }

    val lines = ArrayList<DisplayLine>()
    flattenNode(
      node = rootNode,
      prefix = "",
      childPrefix = "",
      depth = 0,
      output = lines,
      maxLines = maxLinesSetting.value.toInt(),
    )

    if (lines.isEmpty()) {
      lines += DisplayLine("READY All tracked lines are complete.", COLOR_READY)
    }

    return DisplayState(
      title = "Craft Helper",
      subtitle = "${currentAmount()}x ${rootNode.displayName}",
      titleColor = titleColor,
      lines = lines,
    )
  }

  private fun buildNoTargetState(): DisplayState {
    return if (HudModuleManager.isEditorOpen) {
      DisplayState(
        title = "Craft Helper",
        subtitle = "No target selected",
        titleColor = ThemeManager.currentTheme.accent,
        lines = listOf(
          DisplayLine("Use Set Held Item in the module settings.", COLOR_MUTED),
          DisplayLine("Command: /phantom craftheld", COLOR_MUTED, SMALL_TEXT_SIZE),
        ),
      )
    } else {
      buildIdleState()
    }
  }

  private fun buildIdleState(): DisplayState =
    DisplayState(
      title = "Craft Helper",
      subtitle = "Disabled",
      titleColor = COLOR_MUTED,
      lines = emptyList(),
    )

  private fun currentDisplayState(): DisplayState =
    when {
      enabledSetting.value -> displayState
      HudModuleManager.isEditorOpen -> buildNoTargetState()
      else -> displayState
    }

  private fun currentTargetId(): String? =
    NeuRecipeService.normalizeId(targetIdSetting.value).ifBlank { null }

  private fun currentAmount(): Int =
    targetAmountSetting.value.toInt().coerceAtLeast(1)

  private fun collectInventoryCounts(): MutableMap<String, Int> {
    val player = mc.player ?: return LinkedHashMap()
    val counts = LinkedHashMap<String, Int>()
    for (index in 0 until player.inventory.containerSize) {
      val stack = player.inventory.getItem(index)
      if (stack.isEmpty) continue

      val itemId = NeuRecipeService.normalizeId(stack.getSkyblockId())
      if (itemId.isEmpty()) continue
      counts[itemId] = (counts[itemId] ?: 0) + stack.count.coerceAtLeast(1)
    }
    return counts
  }

  private fun buildProgressNode(
    id: String,
    required: Int,
    inventory: MutableMap<String, Int>,
    remainders: MutableMap<String, Int>,
    visited: Set<String>,
    depth: Int,
  ): ProgressNode {
    val normalizedId = NeuRecipeService.normalizeId(id)
    val cached = NeuRecipeService.getCachedItem(normalizedId)
    if (cached == null) {
      NeuRecipeService.requestItem(normalizedId)
    }

    val displayName = cached?.displayName
      ?: if (depth == 0) manualTargetName ?: NeuRecipeService.prettyId(normalizedId)
      else NeuRecipeService.prettyId(normalizedId)

    var carryOver = remainders[normalizedId]?.coerceAtLeast(0) ?: 0
    if (carryOver > 0) {
      val usedCarry = carryOver.coerceAtMost(required)
      carryOver = usedCarry
      val leftoverCarry = (remainders[normalizedId] ?: 0) - usedCarry
      if (leftoverCarry > 0) {
        remainders[normalizedId] = leftoverCarry
      } else {
        remainders.remove(normalizedId)
      }
    }

    val directNeeded = (required - carryOver).coerceAtLeast(0)
    val directAmount = takeInventoryAmount(inventory, normalizedId, directNeeded)
    val missing = (required - carryOver - directAmount).coerceAtLeast(0)
    val recipe = cached?.recipe
    val outputCount = recipe?.outputCount?.coerceAtLeast(1) ?: 1
    val cycleDetected = normalizedId in visited
    val depthLimited = depth >= maxDepthSetting.value.toInt()
    val children = ArrayList<ProgressNode>()

    if (!cycleDetected && !depthLimited && recipe != null && missing > 0) {
      val craftsNeeded = ceilDiv(missing, outputCount)
      val leftover = craftsNeeded * outputCount - missing
      if (leftover > 0) {
        remainders[normalizedId] = (remainders[normalizedId] ?: 0) + leftover
      }

      val nextVisited = visited + normalizedId
      for (ingredient in recipe.ingredients) {
        children += buildProgressNode(
          id = ingredient.id,
          required = ingredient.amount * craftsNeeded,
          inventory = inventory,
          remainders = remainders,
          visited = nextVisited,
          depth = depth + 1,
        )
      }
    }

    return ProgressNode(
      id = normalizedId,
      displayName = displayName,
      required = required,
      directAmount = directAmount,
      carryOver = carryOver,
      outputCount = outputCount,
      hasRecipe = recipe != null,
      loading = cached == null,
      cycleDetected = cycleDetected,
      depthLimited = depthLimited,
      children = children,
    )
  }

  private fun flattenNode(
    node: ProgressNode,
    prefix: String,
    childPrefix: String,
    depth: Int,
    output: MutableList<DisplayLine>,
    maxLines: Int,
  ) {
    if (output.size >= maxLines) return
    if (depth > 0 && hideCompletedSetting.value && node.done) return

    val status = when {
      node.done -> "READY"
      node.descendantsLoading -> "LOAD"
      node.craftable -> "CRAFT"
      else -> "MISS"
    }

    val color = when (status) {
      "READY" -> COLOR_READY
      "LOAD" -> COLOR_LOADING
      "CRAFT" -> COLOR_CRAFTABLE
      else -> COLOR_MISSING
    }

    val note = when {
      node.loading -> " (loading)"
      node.cycleDetected -> " (cycle)"
      node.depthLimited && node.children.isEmpty() && node.hasRecipe && !node.done -> " (depth)"
      else -> ""
    }

    output += DisplayLine(
      text = "$prefix$status ${node.available}/${node.required} ${node.displayName}$note",
      color = color,
    )

    if (output.size >= maxLines) return

    val visibleChildren = node.children.filterNot { hideCompletedSetting.value && it.done }
    visibleChildren.forEachIndexed { index, child ->
      if (output.size >= maxLines) return@forEachIndexed
      val isLast = index == visibleChildren.lastIndex
      flattenNode(
        node = child,
        prefix = childPrefix + if (isLast) "\\-- " else "|-- ",
        childPrefix = childPrefix + if (isLast) "    " else "|   ",
        depth = depth + 1,
        output = output,
        maxLines = maxLines,
      )
    }

    if (output.size >= maxLines && (node.children.size > visibleChildren.size || visibleChildren.isNotEmpty())) {
      output[output.lastIndex] = output.last().copy(text = output.last().text)
    }
  }

  private fun renderPanel(state: DisplayState) {
    if (!enabledSetting.value && !HudModuleManager.isEditorOpen) return
    if (state.title == "Craft Helper" && state.subtitle == "Disabled" && state.lines.isEmpty() && !HudModuleManager.isEditorOpen) return

    val width = panelWidth(state)
    val height = panelHeight(state)
    val theme = ThemeManager.currentTheme
    val accent = ThemeGradient.colors().first

    if (backgroundSetting.value) {
      NVGRenderer.rect(2f, 2f, width, height, 0x1A000000)
      NVGRenderer.rect(0f, 0f, width, height, ThemeSurface.panel(0xE1), PANEL_RADIUS)
      NVGRenderer.gradientRect(0f, 0f, width, HEADER_HEIGHT + 10f, ThemeGradient.withAlpha(accent, 22), 0x00000000, org.phantom.api.util.ui.helper.Gradient.TopToBottom, PANEL_RADIUS)
      NVGRenderer.hollowRect(0f, 0f, width, height, 1f, withAlpha(accent, 96), PANEL_RADIUS)
      NVGRenderer.rect(PADDING_X, HEADER_HEIGHT, width - PADDING_X * 2f, 1f, withAlpha(accent, 48))
    }

    NVGRenderer.text(state.title.uppercase(Locale.US), PADDING_X, PADDING_Y, TITLE_TEXT_SIZE, accent)
    NVGRenderer.text(state.subtitle, PADDING_X, PADDING_Y + 14f, SUBTITLE_TEXT_SIZE, theme.textSecondary)

    var lineY = HEADER_HEIGHT + BODY_TOP_PADDING
    for (line in state.lines) {
      NVGRenderer.text(line.text, PADDING_X, lineY, line.size, line.color)
      lineY += lineHeight(line.size)
    }
  }

  private fun panelWidth(state: DisplayState): Float {
    var width = max(
      PANEL_MIN_WIDTH,
      max(
        NVGRenderer.textWidth(state.title.uppercase(Locale.US), TITLE_TEXT_SIZE),
        NVGRenderer.textWidth(state.subtitle, SUBTITLE_TEXT_SIZE),
      ) + PADDING_X * 2f,
    )

    for (line in state.lines) {
      width = max(width, NVGRenderer.textWidth(line.text, line.size) + PADDING_X * 2f)
    }

    return width
  }

  private fun panelHeight(state: DisplayState): Float {
    val lineArea = state.lines.sumOf { lineHeight(it.size).toDouble() }.toFloat()
    return HEADER_HEIGHT + BODY_TOP_PADDING + lineArea + PADDING_Y
  }

  private fun takeInventoryAmount(inventory: MutableMap<String, Int>, id: String, requested: Int): Int {
    if (requested <= 0) return 0
    val current = inventory[id] ?: return 0
    val used = current.coerceAtMost(requested)
    val remaining = current - used
    if (remaining > 0) {
      inventory[id] = remaining
    } else {
      inventory.remove(id)
    }
    return used
  }

  private fun ceilDiv(value: Int, divisor: Int): Int {
    val safeDivisor = divisor.coerceAtLeast(1)
    return if (value <= 0) 0 else (value + safeDivisor - 1) / safeDivisor
  }

  private fun lineHeight(size: Float): Float =
    max(12f, size + 2.5f)

  private fun withAlpha(color: Int, alpha: Int): Int =
    ((alpha.coerceIn(0, 255) shl 24) or (color and 0x00FFFFFF))

  private const val REFRESH_INTERVAL_MS = 250L
  private const val PANEL_MIN_WIDTH = 210f
  private const val PANEL_RADIUS = 8f
  private const val PADDING_X = 9f
  private const val PADDING_Y = 8f
  private const val HEADER_HEIGHT = 30f
  private const val BODY_TOP_PADDING = 8f
  private const val TITLE_TEXT_SIZE = 11.4f
  private const val SUBTITLE_TEXT_SIZE = 10.2f
  private const val BODY_TEXT_SIZE = 10.5f
  private const val SMALL_TEXT_SIZE = 9.2f
  private const val COLOR_READY = 0xFF74F2A0.toInt()
  private const val COLOR_CRAFTABLE = 0xFFF1C965.toInt()
  private const val COLOR_MISSING = 0xFFFF7A7A.toInt()
  private const val COLOR_LOADING = 0xFF91B6FF.toInt()
  private const val COLOR_MUTED = 0xFF9DA7B5.toInt()
}
