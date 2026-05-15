# Mining Coin Popup Effect Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show a floating `+X coins` screen-space text effect each time the mining macro breaks an ore block, using live Hypixel bazaar prices fetched on client start.

**Architecture:** `MiningProfitTracker` gains a `getPriceForKey()` accessor and a startup bazaar fetch. A new `MiningCoinPopupModule` listens to `BlockChangeEvent` to spawn popup data, then renders and advances them each `NvgEvent` frame using NanoVG. Registered in `Phantom.kt` alongside other mining modules.

**Tech Stack:** Kotlin, Fabric MC 1.21.1, NanoVG via `NVGRenderer`, `EventBus` / `@SubscribeEvent`, `BlockChangeEvent`, `NvgEvent`

---

## File Map

| File | Action |
|------|--------|
| `src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt` | Modify â€” startup fetch + price accessor |
| `src/main/kotlin/org/phantom/internal/mining/MiningCoinPopupModule.kt` | Create â€” popup spawn + render |
| `src/main/kotlin/org/phantom/Phantom.kt` | Modify â€” register module |

---

## Task 1: Add startup bazaar fetch and `getPriceForKey` to `MiningProfitTracker`

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt`

- [ ] **Step 1: Add `getPriceForKey` accessor**

Find the `ensureInitialized` function:
```kotlin
    fun ensureInitialized() { /* touching this object causes the init block to run */ }
```
Add `getPriceForKey` directly after it:
```kotlin
    fun ensureInitialized() { /* touching this object causes the init block to run */ }

    fun getPriceForKey(key: String): Double? = bazaarPrices[key]
```

- [ ] **Step 2: Call `refreshBazaarIfNeeded()` on startup**

Find the `init` block:
```kotlin
    init {
        EventBus.register(this)
    }
```
Replace with:
```kotlin
    init {
        EventBus.register(this)
        refreshBazaarIfNeeded()
    }
```
Because `lastBazaarRefresh` starts at `0L`, this triggers an immediate background fetch every time the client launches.

- [ ] **Step 3: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/mining/MiningProfitTracker.kt
git commit -m "feat: fetch bazaar prices on client start, expose getPriceForKey"
```

---

## Task 2: Create `MiningCoinPopupModule`

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/mining/MiningCoinPopupModule.kt`

- [ ] **Step 1: Create the file**

Create `src/main/kotlin/org/phantom/internal/mining/MiningCoinPopupModule.kt` with this exact content:

```kotlin
package org.phantom.internal.mining

import kotlin.random.Random
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.BlockChangeEvent
import org.phantom.api.event.impl.render.NvgEvent
import org.phantom.api.module.Module
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.util.ui.NVGRenderer

object MiningCoinPopupModule : Module("Mining Coin Popups") {

    private val enabled = CheckboxSetting(
        "Enabled",
        "Show floating +coins text when the macro mines a block.",
        false
    )

    private val fontSize = SliderSetting(
        "Font Size",
        "Size of the popup text.",
        14.0,
        12.0,
        20.0
    )

    init {
        addSetting(enabled, fontSize)
        EventBus.register(this)
    }

    private data class CoinPopup(
        val label: String,
        val startX: Float,
        var y: Float,
        val startMs: Long,
    )

    private val popups = mutableListOf<CoinPopup>()
    private var lastNvgMs = 0L

    private val BLOCK_TYPE_TO_BAZAAR_KEY = mapOf(
        "Mithril (Gray)"      to "mithril ore",
        "Mithril (Dark)"      to "mithril ore",
        "Mithril (Hot)"       to "mithril ore",
        "Titanium"            to "titanium ore",
        "Ruby Gemstone"       to "flawed ruby gem",
        "Amber Gemstone"      to "flawed amber gem",
        "Amethyst Gemstone"   to "flawed amethyst gem",
        "Jade Gemstone"       to "flawed jade gem",
        "Sapphire Gemstone"   to "flawed sapphire gem",
        "Opal Gemstone"       to "flawed opal gem",
        "Topaz Gemstone"      to "flawed topaz gem",
        "Jasper Gemstone"     to "flawed jasper gem",
        "Onyx Gemstone"       to "flawed onyx gem",
        "Aquamarine Gemstone" to "flawed aquamarine gem",
        "Citrine Gemstone"    to "flawed citrine gem",
        "Peridot Gemstone"    to "flawed peridot gem",
        "Umber"               to "umber",
        "Tungsten"            to "tungsten",
        "Glacite"             to "glacite",
    )

    @SubscribeEvent
    fun onBlockChange(event: BlockChangeEvent) {
        if (!enabled.value || !MiningMacroModule.isActive) return
        if (!event.newBlock.isAir) return
        val blockId = BuiltInRegistries.BLOCK.getKey(event.oldBlock.block).toString()
        val blockType = MiningBlockRegistry.BLOCK_ID_TO_TYPE[blockId] ?: return
        val bazaarKey = BLOCK_TYPE_TO_BAZAAR_KEY[blockType] ?: return
        val price = MiningProfitTracker.getPriceForKey(bazaarKey) ?: return
        if (price <= 0.0) return
        val mc = Minecraft.getInstance()
        val x = mc.window.guiScaledWidth / 2f + (Random.nextFloat() * 120f - 60f)
        val y = mc.window.guiScaledHeight * 0.8f
        popups.add(CoinPopup(formatValue(price), x, y, System.currentTimeMillis()))
    }

    @SubscribeEvent
    fun onNvg(@Suppress("UNUSED_PARAMETER") event: NvgEvent) {
        if (!enabled.value || popups.isEmpty()) return
        val now = System.currentTimeMillis()
        val delta = if (lastNvgMs == 0L) 0f else ((now - lastNvgMs) / 1000f).coerceAtMost(0.1f)
        lastNvgMs = now
        val iter = popups.iterator()
        while (iter.hasNext()) {
            val popup = iter.next()
            val elapsed = now - popup.startMs
            if (elapsed >= 2000L) { iter.remove(); continue }
            popup.y -= 40f * delta
            val alpha = ((1f - elapsed / 2000f) * 0xFF).toInt().coerceIn(0, 255)
            val color = (alpha shl 24) or 0x004CFF72
            NVGRenderer.text(popup.label, popup.startX, popup.y, fontSize.value.toFloat(), color)
        }
    }

    private fun formatValue(coins: Double): String = when {
        coins >= 1_000_000.0 -> "+${"%.1f".format(coins / 1_000_000.0)}M"
        coins >= 1_000.0     -> "+${"%.1f".format(coins / 1_000.0)}K"
        else                 -> "+${coins.toLong()}"
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
git add src/main/kotlin/org/phantom/internal/mining/MiningCoinPopupModule.kt
git commit -m "feat: add MiningCoinPopupModule with floating coin text effect"
```

---

## Task 3: Register `MiningCoinPopupModule` in `Phantom.kt`

**Files:**
- Modify: `src/main/kotlin/org/phantom/Phantom.kt`

- [ ] **Step 1: Add the import**

Find the existing mining module imports at the top of `Phantom.kt`. They include lines like:
```kotlin
import org.phantom.internal.mining.MiningMacroModule
import org.phantom.internal.mining.MiningHudModule
```
Add the new import alongside them:
```kotlin
import org.phantom.internal.mining.MiningCoinPopupModule
```

- [ ] **Step 2: Register the module**

In `onInitializeClient()`, find the `ModuleManager.addModules(listOf(...))` call. It currently contains `MiningHudModule` and `MiningMacroModule`. Add `MiningCoinPopupModule` directly after `MiningHudModule`:
```kotlin
        MiningHudModule,
        MiningCoinPopupModule,
```

- [ ] **Step 3: Build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/Phantom.kt
git commit -m "feat: register MiningCoinPopupModule"
```
