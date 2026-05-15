# Mining Anchor Radius + Profit Tracker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix the macro's step-closer-to-blocks logic by replacing the too-small hardcoded drift constant with a configurable anchor radius, add a routes exception, and add a coins/hr + runtime profit section to the Mining HUD.

**Architecture:** Feature 1 is a targeted fix in `MiningMacroModule` (replace constant, fix one else-branch, add one setting) plus a one-line getter in `RoutesModule`. Feature 2 is a new `MiningProfitTracker` singleton (EventBus listener, bazaar fetch, chat parsing) wired into the existing Mining HUD panel as a collapsible section.

**Tech Stack:** Kotlin, Fabric Minecraft 1.21.1, NanoVG via NVGRenderer, Hypixel Bazaar API (same URL as `ProfitManager` in garden)

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `src/main/kotlin/org/phantom/internal/mining/RoutesModule.kt` | Modify | Expose `routeOwnsMining` public getter |
| `src/main/kotlin/org/phantom/internal/mining/MiningMacroModule.kt` | Modify | Add `anchorRadius` setting, replace drift constant, fix else-branch, call `resetSession` on start |
| `src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt` | Create | Session timing, bazaar prices, chat parsing, coins/hr |
| `src/main/kotlin/org/phantom/internal/mining/CommissionMacroModule.kt` | Modify | Call `MiningProfitTracker.resetSession()` when macro starts |
| `src/main/kotlin/org/phantom/internal/mining/MiningHudModule.kt` | Modify | Add `Show Profit` setting and profit section to bottom of panel |

---

## Task 1: Expose `routeOwnsMining` in RoutesModule

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/mining/RoutesModule.kt`

The field `routeOwnsMiningMacro` is `private var`. We need a public getter so `MiningMacroModule` can read it without reflection.

- [ ] **Step 1: Add the public getter**

In `RoutesModule.kt`, find the existing line:
```kotlin
val isRunning: Boolean get() = routeRunning
```
Add the new getter directly after it:
```kotlin
val isRunning: Boolean get() = routeRunning
val routeOwnsMining: Boolean get() = routeOwnsMiningMacro
```

- [ ] **Step 2: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/RoutesModule.kt
git commit -m "feat: expose routeOwnsMining getter on RoutesModule"
```

---

## Task 2: Add `anchorRadius` setting + fix drift constant + fix step-closer else-branch

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/mining/MiningMacroModule.kt`

Three changes in one file:
1. Add `anchorRadius` `SliderSetting`
2. Replace `NEARBY_STEP_MAX_DRIFT` constant with `anchorRadius.value` in `hasDriftedFromVeinStart`, add routes exception, and update `canStepToNearbyTarget`
3. Fix the `target == null` else-branch to pathfind when `usePathfinding` is enabled

- [ ] **Step 1: Add the `anchorRadius` setting declaration**

Find the `stepToNearbyBlocks` setting declaration (around line 147):
```kotlin
  private val stepToNearbyBlocks = CheckboxSetting(
    "Step To Nearby Blocks",
    "Walk a short distance to reach blocks that are just outside mining range while staying near the vein start.",
    true
  )
```
Add `anchorRadius` directly after it:
```kotlin
  private val stepToNearbyBlocks = CheckboxSetting(
    "Step To Nearby Blocks",
    "Walk a short distance to reach blocks that are just outside mining range while staying near the vein start.",
    true
  )

  private val anchorRadius = SliderSetting(
    "Anchor Radius",
    "Maximum distance from the vein start position before returning to anchor. Disabled when running via Routes.",
    14.0,
    4.0,
    48.0
  )
```

- [ ] **Step 2: Register `anchorRadius` in `addSetting(...)`**

Find the `addSetting(...)` call in the `init` block. It currently includes `stepToNearbyBlocks`. Add `anchorRadius` directly after it:
```kotlin
      stepToNearbyBlocks,
      anchorRadius,
      goldenGoblinInterrupt,
```

- [ ] **Step 3: Remove the `NEARBY_STEP_MAX_DRIFT` constant**

Find and delete this line (around line 422):
```kotlin
  private const val NEARBY_STEP_MAX_DRIFT = 3.25
```

- [ ] **Step 4: Fix `hasDriftedFromVeinStart`**

Find the function (around line 1690):
```kotlin
  private fun hasDriftedFromVeinStart(player: Player): Boolean {
    val anchor = veinStartAnchor ?: return false
    val maxDriftSq = NEARBY_STEP_MAX_DRIFT * NEARBY_STEP_MAX_DRIFT
    return player.blockPosition().distSqr(anchor) > maxDriftSq
  }
```
Replace with:
```kotlin
  private fun hasDriftedFromVeinStart(player: Player): Boolean {
    if (RoutesModule.isRunning && RoutesModule.routeOwnsMining) return false
    val anchor = veinStartAnchor ?: return false
    val r = anchorRadius.value
    return player.blockPosition().distSqr(anchor) > r * r
  }
```

- [ ] **Step 5: Fix `canStepToNearbyTarget`**

Find the function (around line 1679):
```kotlin
  private fun canStepToNearbyTarget(player: Player, target: BlockPos): Boolean {
    val anchor = veinStartAnchor ?: return true
    val maxPlayerDriftSq = NEARBY_STEP_MAX_DRIFT * NEARBY_STEP_MAX_DRIFT
    if (player.blockPosition().distSqr(anchor) > maxPlayerDriftSq) {
      return false
    }

    val maxTargetDist = mineRange.value + maxNearbyStepDistance()
    return anchor.distSqr(target) <= maxTargetDist * maxTargetDist
  }
```
Replace with:
```kotlin
  private fun canStepToNearbyTarget(player: Player, target: BlockPos): Boolean {
    if (RoutesModule.isRunning && RoutesModule.routeOwnsMining) return true
    val anchor = veinStartAnchor ?: return true
    val r = anchorRadius.value
    if (player.blockPosition().distSqr(anchor) > r * r) return false
    val maxTargetDist = r + mineRange.value
    return anchor.distSqr(target) <= maxTargetDist * maxTargetDist
  }
```

- [ ] **Step 6: Fix the `target == null` else-branch**

Find this block (around line 615â€“618) inside the `target == null` guard. It is the `else` that fires when `shortStepAllowed` is false:
```kotlin
        } else {
          MovementManager.clearForcedMovement()
        }
```
Replace with:
```kotlin
        } else {
          if (usePathfinding.value && canStepToNearbyTarget(player, nearest)) {
            moveToward(level, player, nearest)
          } else {
            MovementManager.clearForcedMovement()
          }
        }
```

- [ ] **Step 7: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`. If the compiler says `NEARBY_STEP_MAX_DRIFT` is unresolved anywhere else, search for it with `grep -rn "NEARBY_STEP_MAX_DRIFT"` and remove/replace any remaining uses.

- [ ] **Step 8: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/MiningMacroModule.kt
git commit -m "feat: add anchorRadius setting, fix drift constant, fix step-closer pathfinding fallback"
```

---

## Task 3: Create `MiningProfitTracker`

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt`

Tracks session start time and accumulated coin value. Parses `+N ItemName` chat messages, looks up bazaar prices, and provides `coinsPerHour()` and `runtimeMs()`. Uses the same Hypixel bazaar endpoint as `ProfitManager` in garden.

- [ ] **Step 1: Create the file**

Create `src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt`:

```kotlin
package org.phantom.internal.mining

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import java.net.URL

object MiningProfitTracker {

    @Volatile var sessionCoins = 0L
        private set
    @Volatile var sessionStartTime = 0L
        private set

    private val bazaarPrices = mutableMapOf<String, Double>()
    private var lastBazaarRefresh = 0L
    private val BAZAAR_REFRESH_INTERVAL_MS = 5 * 60 * 1000L  // 5 minutes

    private val itemPickupRegex = Regex("""\+\s*(\d[\d,]*)\s+(.+)""")

    init {
        EventBus.register(this)
    }

    fun ensureInitialized() { /* touching this object causes the init block to run */ }

    fun resetSession() {
        sessionCoins = 0L
        sessionStartTime = System.currentTimeMillis()
        refreshBazaarIfNeeded()
    }

    fun runtimeMs(): Long = if (sessionStartTime == 0L) 0L
        else (System.currentTimeMillis() - sessionStartTime).coerceAtLeast(0L)

    fun coinsPerHour(): Long {
        val elapsedHours = runtimeMs() / 3_600_000.0
        if (elapsedHours < 0.001) return 0L
        return (sessionCoins / elapsedHours).toLong()
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val message = event.message ?: return
        val m = itemPickupRegex.find(message) ?: return
        val countStr = m.groupValues[1].replace(",", "")
        val count = countStr.toLongOrNull() ?: return
        val item = m.groupValues[2].trim().lowercase()
        val price = bazaarPrices[item] ?: return
        sessionCoins += (count * price).toLong()
    }

    private fun refreshBazaarIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastBazaarRefresh < BAZAAR_REFRESH_INTERVAL_MS) return
        lastBazaarRefresh = now
        Thread({
            try {
                val json = URL("https://api.hypixel.net/v2/skyblock/bazaar").readText()
                val root = JsonParser.parseString(json).asJsonObject
                val prods = root.getAsJsonObject("products") ?: return@Thread
                val prices = mutableMapOf<String, Double>()
                for ((name, data) in prods.entrySet()) {
                    val buy = data.asJsonObject
                        .getAsJsonObject("quick_status")
                        ?.get("buyPrice")?.asDouble ?: continue
                    prices[name.lowercase().replace("_", " ")] = buy
                }
                Minecraft.getInstance().execute {
                    bazaarPrices.clear()
                    bazaarPrices.putAll(prices)
                }
            } catch (_: Exception) { /* network failure â€” keep cached prices */ }
        }, "mining-bazaar-refresh").also { it.isDaemon = true }.start()
    }
}
```

- [ ] **Step 2: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt
git commit -m "feat: add MiningProfitTracker with bazaar fetch and chat-based coin tracking"
```

---

## Task 4: Wire `MiningProfitTracker` into macro start points

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/mining/MiningMacroModule.kt`
- Modify: `src/main/kotlin/org/phantom/internal/mining/CommissionMacroModule.kt`

`MiningProfitTracker` must be initialized (so its `init` block runs and it registers with EventBus) and `resetSession()` must be called each time a mining session starts.

- [ ] **Step 1: Initialize tracker in `MiningMacroModule.init`**

Find the existing `ensureInitialized` call in `MiningMacroModule.init`:
```kotlin
    MiningPrecisionTracker.ensureInitialized()
```
Add the tracker call directly after it:
```kotlin
    MiningPrecisionTracker.ensureInitialized()
    MiningProfitTracker.ensureInitialized()
```

- [ ] **Step 2: Reset session when `MiningMacroModule` starts**

Find the `wasEnabled` flip block (around line 453):
```kotlin
    if (!wasEnabled) {
      wasEnabled = true
      val selections = resolveTypeSelections()
```
Add `resetSession()` as the first line inside that block:
```kotlin
    if (!wasEnabled) {
      wasEnabled = true
      MiningProfitTracker.resetSession()
      val selections = resolveTypeSelections()
```

- [ ] **Step 3: Reset session when `CommissionMacroModule` starts**

In `CommissionMacroModule.kt`, find `transitionToLoopStart` (around line 862):
```kotlin
  private fun transitionToLoopStart(mode: CommissionMode) {
    transitionToCommissionSource(mode, alreadyAtSource = false, allowDwarvenWarp = true)
  }
```
Replace with:
```kotlin
  private fun transitionToLoopStart(mode: CommissionMode) {
    MiningProfitTracker.resetSession()
    transitionToCommissionSource(mode, alreadyAtSource = false, allowDwarvenWarp = true)
  }
```

- [ ] **Step 4: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/MiningMacroModule.kt \
        src/main/kotlin/org/phantom/internal/mining/CommissionMacroModule.kt
git commit -m "feat: wire MiningProfitTracker reset into macro start points"
```

---

## Task 5: Add profit section to `MiningHudModule`

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/mining/MiningHudModule.kt`

Adds a `Show Profit` `CheckboxSetting` and a new section at the bottom of the existing panel. The section shows: active macro name, coins/hr, runtime.

- [ ] **Step 1: Add helper functions for active macro name and formatting**

At the bottom of `MiningHudModule.kt`, just before the closing `}` of the object, add:

```kotlin
  private fun activeMacroLabel(): String = when {
    RoutesModule.isRunning && RoutesModule.routeOwnsMining -> "Routes"
    CommissionMacroModule.isRunning                       -> "Commission Macro"
    MiningMacroModule.isActive                            -> "Mining Macro"
    else                                                  -> "â€”"
  }

  private fun formatRuntime(ms: Long): String {
    val s = (ms / 1000).coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
  }

  private fun formatCoinsPerHour(cph: Long): String {
    val abs = Math.abs(cph)
    return when {
      abs >= 1_000_000 -> "${"%.1f".format(abs / 1_000_000.0)}M"
      abs >= 1_000     -> "${"%.1f".format(abs / 1_000.0)}K"
      else             -> "$abs"
    }
  }
```

- [ ] **Step 2: Update `panelHeight()` to account for the profit section**

Find `panelHeight()`:
```kotlin
  private fun panelHeight(): Float {
    val rowsHeight = lineHeight * overlayRows().size
    val buffsHeight = lineHeight * buffRows().size
    val commissions = commissionRows()
    val commissionsHeight =
      if (commissions.isEmpty()) 0f
      else dividerGap + sectionGap + sectionLabelGap + lineHeight * commissions.size
    return padding * 2 + rowsHeight + dividerGap + sectionGap + sectionLabelGap + buffsHeight + commissionsHeight
  }
```
Replace with:
```kotlin
  private fun panelHeight(showProfit: Boolean): Float {
    val rowsHeight = lineHeight * overlayRows().size
    val buffsHeight = lineHeight * buffRows().size
    val commissions = commissionRows()
    val commissionsHeight =
      if (commissions.isEmpty()) 0f
      else dividerGap + sectionGap + sectionLabelGap + lineHeight * commissions.size
    val profitHeight =
      if (!showProfit) 0f
      else dividerGap + sectionGap + sectionLabelGap + lineHeight * 3f  // Active, Coins/hr, Runtime
    return padding * 2 + rowsHeight + dividerGap + sectionGap + sectionLabelGap + buffsHeight + commissionsHeight + profitHeight
  }
```

- [ ] **Step 3: Update `panelWidth()` to include profit rows in width calculation**

Find `panelWidth()`:
```kotlin
  private fun panelWidth(): Float {
    val lines =
      overlayRows() +
        listOf("BUFFS") +
        buffRows() +
        listOf("COMMISSIONS") +
        commissionRows().mapIndexed { index, row -> "Commission ${index + 1}: ${row.label} ${row.detail}" }
    return (lines.maxOfOrNull { NVGRenderer.textWidth(it, textSize) } ?: 180f) + padding * 2
  }
```
Replace with:
```kotlin
  private fun panelWidth(): Float {
    val cph = formatCoinsPerHour(MiningProfitTracker.coinsPerHour())
    val runtime = formatRuntime(MiningProfitTracker.runtimeMs())
    val lines =
      overlayRows() +
        listOf("BUFFS") +
        buffRows() +
        listOf("COMMISSIONS") +
        commissionRows().mapIndexed { index, row -> "Commission ${index + 1}: ${row.label} ${row.detail}" } +
        listOf(
          "MACRO",
          "Active: ${activeMacroLabel()}",
          "Coins/hr: $cph",
          "Runtime: $runtime",
        )
    return (lines.maxOfOrNull { NVGRenderer.textWidth(it, textSize) } ?: 180f) + padding * 2
  }
```

- [ ] **Step 4: Add `showProfit` setting and update `hudElement` builder**

In the `hudElement` builder, find:
```kotlin
    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when mining tracking is idle", false))

    width { panelWidth() }
    height { panelHeight() }

    render { x, y, _ ->
      if (onlyWhenActive.value && !shouldShow()) return@render

      val rows = overlayRows()
      val buffs = buffRows()
      val commissions = commissionRows()
      val panelW = panelWidth()
      val panelH = panelHeight()
```
Replace with:
```kotlin
    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when mining tracking is idle", false))
    val showProfit = setting(CheckboxSetting("Show Profit", "Show active macro, coins/hr, and runtime.", true))

    width { panelWidth() }
    height { panelHeight(showProfit.value) }

    render { x, y, _ ->
      if (onlyWhenActive.value && !shouldShow()) return@render

      val rows = overlayRows()
      val buffs = buffRows()
      val commissions = commissionRows()
      val panelW = panelWidth()
      val panelH = panelHeight(showProfit.value)
```

- [ ] **Step 5: Add the profit section render block**

The profit section is anchored to the bottom of the panel, so Y positions are computed from `panelH` downward â€” no need to track intermediate Y values from the sections above.

In the `render` lambda, find this closing sequence (the `}` that ends the commissions `if` block, immediately before the `render` lambda's closing `}`):
```kotlin
      }
    }
  }
```
(The innermost `}` closes `if (commissions.isNotEmpty())`, the middle `}` closes `render { x, y, _ ->`, the outer `}` closes `miningHud = hudElement(...)`)

Replace that closing sequence with:
```kotlin
      }

      if (showProfit.value) {
        val macroStartY = y + panelH - padding - lineHeight * 3f
        val macroLabelY = macroStartY - sectionLabelGap - 2f
        val profitDividerY = macroLabelY - sectionGap - dividerGap / 2f

        NVGRenderer.line(x + padding, profitDividerY, x + panelW - padding, profitDividerY, 1f, ThemeManager.currentTheme.controlBorder)
        NVGRenderer.text("MACRO", x + padding, macroLabelY, 11f, ThemeManager.currentTheme.textSecondary)

        val cph = formatCoinsPerHour(MiningProfitTracker.coinsPerHour())
        val runtime = formatRuntime(MiningProfitTracker.runtimeMs())
        renderRow("Active: ${activeMacroLabel()}", x + padding, macroStartY)
        renderRow("Coins/hr: $cph", x + padding, macroStartY + lineHeight)
        renderRow("Runtime: $runtime", x + padding, macroStartY + lineHeight * 2f)
      }
    }
  }
```

- [ ] **Step 6: Add missing imports to `MiningHudModule`**

At the top of `MiningHudModule.kt`, add the missing import lines. Find the existing import block and add:
```kotlin
import org.phantom.internal.mining.CommissionMacroModule
import org.phantom.internal.mining.MiningProfitTracker
import org.phantom.internal.mining.RoutesModule
```
(These may already be present via wildcard â€” the build in step 7 will confirm.)

- [ ] **Step 7: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`. If there are unresolved reference errors on `RoutesModule`, `CommissionMacroModule`, or `MiningProfitTracker`, add the explicit imports from step 6.

- [ ] **Step 8: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/MiningHudModule.kt
git commit -m "feat: add profit section to Mining HUD (active macro, coins/hr, runtime)"
```
