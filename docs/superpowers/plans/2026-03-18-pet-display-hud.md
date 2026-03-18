# Pet Display HUD Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a navy blue pill-shaped NVG HUD in `internal/visual/` showing active pet name, level, held pet item, and an animated gradient XP-to-next-level progress bar, with a glowing light blue border.

**Architecture:** `PetDisplayModule` (object Module) owns the `hudElement()` and settings. `PetTabListParser` parses the Hypixel SkyBlock tab list footer/header for pet data each tick. A `TabOverlayAccessor` mixin exposes the `PlayerTabOverlay` header/footer/playerInfo fields needed for parsing.

**Tech Stack:** Kotlin, Fabric 1.21.11, NanoVG (`NVGRenderer`), Mixin `@Accessor`, `hudElement()` DSL, `inGroup()` setting groups.

---

## File Map

| Action | File |
|---|---|
| Create | `src/main/kotlin/org/cobalt/internal/visual/PetTabListParser.kt` |
| Create | `src/main/kotlin/org/cobalt/internal/visual/PetDisplayModule.kt` |
| Create | `src/main/java/org/cobalt/mixin/client/TabOverlayAccessor.java` |
| Modify | `src/main/kotlin/org/cobalt/Cobalt.kt` |

---

## Task 1: TabOverlayAccessor mixin

**Files:**
- Create: `src/main/java/org/cobalt/mixin/client/TabOverlayAccessor.java`

- [ ] **Step 1: Create the accessor**

```java
package org.cobalt.mixin.client;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerTabOverlay.class)
public interface TabOverlayAccessor {
    @Accessor("header")
    Component getHeader();

    @Accessor("footer")
    Component getFooter();
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew build 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/cobalt/mixin/client/TabOverlayAccessor.java
git commit -m "feat: add TabOverlayAccessor mixin for pet/tab data"
```

---

## Task 2: PetTabListParser

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/visual/PetTabListParser.kt`

Hypixel SkyBlock embeds pet info in the tab list header. Strip MC formatting codes (`§[0-9a-fk-or]`) then regex-match.

- [ ] **Step 1: Create parser**

```kotlin
package org.cobalt.internal.visual

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import org.cobalt.mixin.client.TabOverlayAccessor

data class PetData(
    val name: String,         // e.g. "Tiger"
    val level: Int,           // e.g. 98
    val heldItem: String,     // e.g. "Tier Boost" — empty if none
    val xpCurrent: Long,      // current XP in this level
    val xpRequired: Long,     // XP required to next level (0 = max level)
    val isMaxLevel: Boolean,
)

object PetTabListParser {

    private val mc = Minecraft.getInstance()

    /** Cached result — updated each tick by PetDisplayModule */
    var current: PetData? = null
        private set

    // Regex patterns for Hypixel SkyBlock tab list footer/header
    // Example formats (stripped of formatting codes):
    //   "[Lv 98] Tiger"  or  "Tiger [Lv 98]"
    //   "Pet Item: Tier Boost"  or  "Held Item: Tier Boost"
    //   "Pet XP: 45,234 / 60,000"  or  "XP: 45,234/60,000"
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
    private val MAX_LEVEL = Regex(
        """(?:max\s+level|lv\s*100)""",
        RegexOption.IGNORE_CASE
    )

    fun update() {
        val gui = mc.gui ?: return
        val tabOverlay = gui.tabList as? TabOverlayAccessor ?: return

        val rawText = buildString {
            tabOverlay.header?.let { append(stripFormatting(it.string)).append("\n") }
            tabOverlay.footer?.let { append(stripFormatting(it.string)).append("\n") }
        }

        if (rawText.isBlank()) {
            // Try player list display names as fallback
            current = null
            return
        }

        current = parse(rawText)
    }

    private fun parse(text: String): PetData? {
        var name = ""
        var level = 0
        var heldItem = ""
        var xpCurrent = 0L
        var xpRequired = 0L
        var isMax = false

        for (line in text.lines()) {
            val trimmed = line.trim()

            if (name.isEmpty()) {
                PET_NAME_LEVEL.find(trimmed)?.let { m ->
                    level = (m.groupValues[1].ifEmpty { m.groupValues[4] }).toIntOrNull() ?: 0
                    name = (m.groupValues[2].ifEmpty { m.groupValues[3] }).trim()
                }
            }

            if (heldItem.isEmpty()) {
                PET_ITEM.find(trimmed)?.let { m ->
                    heldItem = m.groupValues[1].trim()
                }
            }

            if (xpRequired == 0L) {
                PET_XP.find(trimmed)?.let { m ->
                    xpCurrent = m.groupValues[1].replace(",", "").toLongOrNull() ?: 0L
                    xpRequired = m.groupValues[2].replace(",", "").toLongOrNull() ?: 0L
                }
            }

            if (MAX_LEVEL.containsMatchIn(trimmed)) isMax = true
        }

        if (name.isEmpty()) return null
        return PetData(name, level, heldItem, xpCurrent, xpRequired, isMax || level >= 100)
    }

    private fun stripFormatting(text: String): String =
        text.replace(Regex("§[0-9a-fk-or]"), "")
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/visual/PetTabListParser.kt
git commit -m "feat: add PetTabListParser for tab list pet data extraction"
```

---

## Task 3: PetDisplayModule with NVG HUD

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/visual/PetDisplayModule.kt`

**Design:**
- Navy blue pill background: `NVGRenderer.rect(x, y, w, h, 0xFF0A0F2E.toInt(), radius)`
- Glow: 3 hollow rects with decreasing alpha + increasing stroke/radius
- Border: `NVGRenderer.hollowGradientRect(...)` light blue `0xFFADD8FF` → `0xFF7EC8FF`
- Title row: `[petName  Lv N]`
- Held item row (optional): item name
- XP bar background: dark rect, `0xFF1A1F4E.toInt()`
- XP bar fill: `NVGRenderer.gradientRect(...)` with animated shift using `System.currentTimeMillis()`
- XP label: "78%" or "MAX" when at max level

```kotlin
package org.cobalt.internal.visual

import kotlin.math.cos
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object PetDisplayModule : Module("Pet Display") {

    private val mc = Minecraft.getInstance()

    // Settings
    private val enabled = CheckboxSetting("Enabled", "Show the pet display HUD.", true)
    private val showHeldItem = CheckboxSetting("Show Held Item", "Display the pet's held item.", true)
    private val glowPulse = CheckboxSetting("Glow Pulse", "Animate the border glow.", true)

    val petHud = hudElement("pet-display", "Pet Display", "Animated pet info HUD") {
        anchor  = HudAnchor.BOTTOM_RIGHT
        offsetX = -10f
        offsetY = -60f

        width  { 180f }
        height {
            val base = if (showHeldItem.value && PetTabListParser.current?.heldItem?.isNotEmpty() == true) 72f else 58f
            base
        }

        render { x, y, _ ->
            if (!enabled.value) return@render
            val data = PetTabListParser.current

            val w = 180f
            val h = if (showHeldItem.value && data?.heldItem?.isNotEmpty() == true) 72f else 58f
            val radius = h / 2f       // full pill radius
            val pad = 14f

            // --- Glow layers ---
            if (glowPulse.value) {
                val t = (System.currentTimeMillis() % 3000L).toFloat() / 3000f
                val pulse = 0.6f + 0.4f * cos(t * Math.PI.toFloat() * 2f)
                for (i in 3 downTo 1) {
                    val alpha = ((0x18 * pulse) * i / 3).toInt().coerceIn(0, 0x30)
                    NVGRenderer.hollowRect(
                        x - i * 1.5f, y - i * 1.5f,
                        w + i * 3f, h + i * 3f,
                        2f + i.toFloat(),
                        (alpha shl 24) or 0x7EC8FF,
                        radius + i * 1.5f
                    )
                }
            }

            // --- Background pill ---
            NVGRenderer.rect(x, y, w, h, 0xE60A0F2E.toInt(), radius)

            // --- Border (animated gradient) ---
            val angle = (System.currentTimeMillis() % 8000L).toFloat() / 8000f * (Math.PI.toFloat() * 2f)
            val shiftX = cos(angle) * (w * 0.4f)
            NVGRenderer.hollowGradientRectShifted(
                x, y, w, h,
                1.5f,
                0xFFADD8FF.toInt(), 0xFF4FA8E8.toInt(),
                Gradient.LeftToRight,
                radius, shiftX, 0f
            )

            if (data == null) {
                // No pet detected
                NVGRenderer.text("No pet detected", x + pad, y + h / 2f + 4f, 11f, 0x80FFFFFF.toInt())
                return@render
            }

            val textColor = 0xFFFFFFFF.toInt()
            val dimColor  = 0xBBB0C8FF.toInt()

            // --- Pet name + level ---
            val nameStr = data.name
            val lvStr   = "Lv ${data.level}"
            val lvWidth = NVGRenderer.textWidth(lvStr, 10f)
            NVGRenderer.textShadow(nameStr, x + pad, y + 16f, 13f, textColor)
            NVGRenderer.text(lvStr, x + w - pad - lvWidth, y + 16f, 10f, 0xFF7EC8FF.toInt())

            // --- Held item ---
            var nextY = 30f
            if (showHeldItem.value && data.heldItem.isNotEmpty()) {
                NVGRenderer.text(data.heldItem, x + pad, y + nextY, 10f, dimColor)
                nextY += 14f
            }

            // --- XP bar ---
            val barX = x + pad
            val barY = y + nextY
            val barW = w - pad * 2f
            val barH = 7f
            val barRadius = barH / 2f

            // Bar background
            NVGRenderer.rect(barX, barY, barW, barH, 0xFF1A1F4E.toInt(), barRadius)

            if (data.isMaxLevel) {
                // Full gold bar for max level
                NVGRenderer.gradientRect(barX, barY, barW, barH, 0xFFFFD700.toInt(), 0xFFFFA500.toInt(), Gradient.LeftToRight, barRadius)
                NVGRenderer.text("MAX", barX + barW / 2f - 10f, barY - 1f, 9f, 0xFFFFD700.toInt())
            } else if (data.xpRequired > 0) {
                val ratio = (data.xpCurrent.toFloat() / data.xpRequired.toFloat()).coerceIn(0f, 1f)
                val fillW = (barW * ratio).coerceAtLeast(barRadius * 2f)

                // Animated shimmer gradient
                val shimT = (System.currentTimeMillis() % 2000L).toFloat() / 2000f
                val shimShift = cos(shimT * Math.PI.toFloat() * 2f) * fillW * 0.5f
                NVGRenderer.hollowGradientRectShifted(
                    barX, barY, fillW, barH,
                    barH, // stroke == height = filled rect via stroke trick
                    0xFF00C6FF.toInt(), 0xFF0055FF.toInt(),
                    Gradient.LeftToRight,
                    barRadius, shimShift, 0f
                )

                // Actual filled bar (on top to clip to fill width)
                NVGRenderer.pushScissor(barX, barY, fillW, barH)
                NVGRenderer.gradientRect(
                    barX + shimShift * 0.3f, barY,
                    fillW, barH,
                    0xFF00C6FF.toInt(), 0xFF0055FF.toInt(),
                    Gradient.LeftToRight, barRadius
                )
                NVGRenderer.popScissor()

                // XP percent label
                val pct = "${(ratio * 100).toInt()}%"
                val pctW = NVGRenderer.textWidth(pct, 8f)
                NVGRenderer.text(pct, barX + barW / 2f - pctW / 2f, barY - 1f, 8f, dimColor)
            }
        }
    }

    init {
        addSetting(enabled, showHeldItem, glowPulse)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.End) {
        if (!enabled.value) return
        PetTabListParser.update()
    }
}
```

- [ ] **Step 1: Create PetDisplayModule.kt** (paste code above)

- [ ] **Step 2: Verify build**

```bash
./gradlew build 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/visual/PetDisplayModule.kt
git commit -m "feat: add PetDisplayModule with animated gradient XP bar and glow border"
```

---

## Task 4: Register in Cobalt.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Add import and register**

Add import:
```kotlin
import org.cobalt.internal.visual.PetDisplayModule
```

Add to `ModuleManager.addModules(listOf(...))` list:
```kotlin
PetDisplayModule,
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat: register PetDisplayModule"
```
