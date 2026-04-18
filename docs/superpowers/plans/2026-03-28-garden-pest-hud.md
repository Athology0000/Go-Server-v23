# Garden Pest HUD Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a positionable HUD element that shows real-time pest status in the Hypixel SkyBlock Garden: cooldown countdown, alive pest count, infested plots, last-spawn timestamp, farming bonus, and internal post-clean cooldown.

**Architecture:** A new `GardenHudModule` (Kotlin object) polls `PestTabListParser` on every `TickEvent.Start`, maintaining a local `cooldownDeadline` (epoch ms) so the cooldown counts down smoothly between ticks. Rendering is a `hudElement()` that draws labelled rows with `NVGRenderer`. Only renders when the Garden scoreboard/tab text is detected.

**Tech Stack:** Kotlin, Cobalt hudElement DSL, NVGRenderer, existing `PestTabListParser` + `PestManager`.

---

## File Map

| File | Action | Purpose |
|---|---|---|
| `src/main/kotlin/org/cobalt/internal/garden/GardenHudModule.kt` | Create | Module + hudElement with tick-driven state |
| `src/main/kotlin/org/cobalt/Cobalt.kt` | Modify | Register GardenHudModule |

---

### Task 1: GardenHudModule

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/garden/GardenHudModule.kt`

- [ ] **Step 1: Create GardenHudModule.kt**

```kotlin
package org.cobalt.internal.garden

import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.garden.managers.PestManager
import org.cobalt.internal.garden.managers.PestTabListParser
import org.cobalt.mixin.client.TabOverlayAccessor

object GardenHudModule : Module("Garden HUD") {

    private val mc = Minecraft.getInstance()

    // ── State updated every tick ──────────────────────────────────────────────
    private var alivePests       = 0
    private var infestedPlots    = emptyList<String>()
    private var bonusActive      = false
    private var cooldownDeadline = 0L   // epoch ms when cooldown expires
    private var lastSpawnedAt    = 0L   // epoch ms of last pest spawn
    private var prevAliveCount   = 0

    // ── Layout constants ──────────────────────────────────────────────────────
    private const val ROW_H   = 15f
    private const val FONT_SZ = 10f
    private const val PAD     = 8f
    private const val W       = 190f

    init {
        EventBus.register(this)
    }

    // ── hudElement ────────────────────────────────────────────────────────────

    val pestHud = hudElement("garden-pest-hud", "Garden Pest HUD", "Shows pest cooldown and status in the Garden") {
        anchor = HudAnchor.TOP_LEFT
        offsetX = 10f
        offsetY = 10f

        width  { W + PAD * 2 }
        height { PAD * 2 + ROW_H * visibleRowCount() }

        render { x, y, _ ->
            if (!isInGarden()) return@render

            val now = System.currentTimeMillis()
            val cdRemaining = ((cooldownDeadline - now) / 1000f).coerceAtLeast(0f)
            val internalCdRemaining = ((PestManager.cleaningCooldownUntil - now) / 1000f).coerceAtLeast(0f)
            val lastSpawnedSec = if (lastSpawnedAt > 0) ((now - lastSpawnedAt) / 1000f) else -1f

            var rowY = y + PAD

            fun row(label: String, value: String, valueColor: Int) {
                val theme = org.cobalt.api.ui.theme.ThemeManager.currentTheme
                NVGRenderer.text(label, x + PAD, rowY, FONT_SZ, theme.text)
                NVGRenderer.text(value, x + PAD + 110f, rowY, FONT_SZ, valueColor)
                rowY += ROW_H
            }

            val green  = 0xFF55FF55.toInt()
            val yellow = 0xFFFFFF55.toInt()
            val red    = 0xFFFF5555.toInt()
            val gray   = 0xFFAAAAAA.toInt()
            val white  = 0xFFFFFFFF.toInt()

            row("Curr Pest:",      "$alivePests",
                if (alivePests > 0) red else green)

            row("Pest Cooldown:",
                if (cdRemaining > 0) "%.0fs".format(cdRemaining) else "Ready",
                if (cdRemaining > 0) yellow else green)

            if (infestedPlots.isNotEmpty()) {
                row("Infested Plots:", infestedPlots.size.toString(), red)
            }

            if (lastSpawnedSec >= 0) {
                row("Last Spawned:", "%.0fs ago".format(lastSpawnedSec), gray)
            }

            row("Farming Bonus:",
                if (bonusActive) "Active" else "Inactive",
                if (bonusActive) green else gray)

            if (internalCdRemaining > 0) {
                row("Internal CD:", "%.0fs".format(internalCdRemaining), yellow)
            }
        }
    }

    // ── Tick-driven state update ──────────────────────────────────────────────

    @SubscribeEvent
    fun onTick(event: TickEvent.Start) {
        if (!pestHud.enabled) return
        if (!isInGarden()) return

        val data = PestTabListParser.parse()

        if (data.alivePests > prevAliveCount) {
            lastSpawnedAt = System.currentTimeMillis()
        }
        prevAliveCount = data.alivePests
        alivePests = data.alivePests
        infestedPlots = data.infestedPlots
        bonusActive = data.bonusActive

        // Reset deadline each tick when cooldown is non-zero, so between ticks
        // the HUD interpolates smoothly rather than jumping on each parse.
        if (data.cooldownSeconds > 0) {
            val newDeadline = System.currentTimeMillis() + data.cooldownSeconds * 1000L
            // Only move deadline forward (avoid snapping back if a stale parse arrives)
            if (newDeadline > cooldownDeadline) cooldownDeadline = newDeadline
        } else {
            cooldownDeadline = 0L
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun isInGarden(): Boolean {
        val gui = mc.gui
        val overlay = gui.tabList as? TabOverlayAccessor ?: return false
        val header = overlay.header?.string?.replace(Regex("\u00A7[0-9a-fk-or]"), "") ?: ""
        val footer = overlay.footer?.string?.replace(Regex("\u00A7[0-9a-fk-or]"), "") ?: ""
        return header.contains("Garden", ignoreCase = true) ||
               footer.contains("Garden", ignoreCase = true)
    }

    private fun visibleRowCount(): Float {
        val now = System.currentTimeMillis()
        var count = 3f  // Curr Pest + Cooldown + Farming Bonus always shown
        if (infestedPlots.isNotEmpty()) count++
        if (lastSpawnedAt > 0) count++
        if (PestManager.cleaningCooldownUntil > now) count++
        return count
    }
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/garden/GardenHudModule.kt
git commit -m "feat: add GardenHudModule with pest cooldown HUD"
```

---

### Task 2: Register GardenHudModule in Cobalt.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Add import**

```kotlin
import org.cobalt.internal.garden.GardenHudModule
```

- [ ] **Step 2: Register the module**

In `onInitializeClient()`, inside `ModuleManager.addModules(listOf(...))`, add after `GardenMacroModule`:

```kotlin
        GardenMacroModule,
        GardenHudModule,
```

- [ ] **Step 3: Verify build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat: register GardenHudModule"
```

---

### Task 3: Manual verification checklist

- [ ] Run client: `./gradlew runClient`
- [ ] In Hypixel SkyBlock Garden: Garden Pest HUD appears in top-left corner
- [ ] "Curr Pest" row shows correct count (matches scoreboard)
- [ ] "Pest Cooldown" counts down in real time (not just on tick, smooth seconds)
- [ ] "Infested Plots" row appears only when plots list is non-empty
- [ ] "Last Spawned" row appears after first pest spawn, shows elapsed seconds
- [ ] "Farming Bonus" shows green "Active" when bonus is active
- [ ] "Internal CD" row appears and counts down after pest cleaning finishes
- [ ] HUD does not appear on other islands (e.g. Hub)
- [ ] HUD position is draggable via Cobalt HUD editor
- [ ] Module toggles off via Cobalt settings panel

---
