# Garden Macro Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port ihanuat's 22 garden farming managers into Phantom as `GardenMacroModule` â€” one Module with subcategorized settings, a combined NVG HUD, and a serial worker thread â€” using Phantom's Module/Setting/Event/NVG systems throughout.

**Architecture:** `GardenMacroModule` (object Module) owns all settings, the state machine, and HUD. 22 manager objects handle individual concerns (pest, visitor, gear, economy, session). `GardenWorkerThread` serializes async tasks. `ScriptBridge` wraps all external script commands. Two mixin accessors expose inventory and tab list fields. No unit tests (project has none).

**Tech Stack:** Kotlin, Fabric 1.21.11, `@SubscribeEvent`, `inGroup()`, `hudElement()` DSL, `NVGRenderer`, Mixin `@Accessor`, `mc.player?.connection?.sendCommand()`.

---

## File Map

| Action | File |
|---|---|
| Create | `src/main/kotlin/org/phantom/internal/garden/GardenState.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/GardenWorkerThread.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/ScriptBridge.kt` |
| Create | `src/main/java/org/phantom/mixin/client/GardenInventoryAccessor.java` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestTabListParser.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestCleaningSequencer.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestAotvManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestPrepSwapManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestReturnManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PestBonusManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/CropFeverManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/VisitorManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/WardrobeManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/EquipmentManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/GearManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/RodManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/GeorgeManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/BookCombineManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/JunkManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/BoosterCookieManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/ProfitManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/PetXpTracker.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/DynamicRestManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/RecoveryManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/managers/RestartManager.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/GardenHud.kt` |
| Create | `src/main/kotlin/org/phantom/internal/garden/GardenMacroModule.kt` |
| Modify | `src/main/kotlin/org/phantom/Phantom.kt` |

---

## Task 1: Foundation â€” GardenState, GardenWorkerThread, ScriptBridge

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/garden/GardenState.kt`
- Create: `src/main/kotlin/org/phantom/internal/garden/GardenWorkerThread.kt`
- Create: `src/main/kotlin/org/phantom/internal/garden/ScriptBridge.kt`

- [ ] **Step 1: Create GardenState.kt**

```kotlin
package org.phantom.internal.garden

enum class GardenState {
    OFF, FARMING, CLEANING, VISITING, AUTOSELLING, RESTING, RECOVERING
}
```

- [ ] **Step 2: Create GardenWorkerThread.kt**

```kotlin
package org.phantom.internal.garden

import java.util.concurrent.LinkedBlockingDeque

object GardenWorkerThread {

    private val queue = LinkedBlockingDeque<Pair<String, () -> Unit>>()
    @Volatile private var thread: Thread? = null

    fun submit(name: String, block: () -> Unit) {
        ensureRunning()
        queue.offer(name to block)
    }

    fun shutdown() {
        thread?.interrupt()
        queue.clear()
        try { thread?.join(2000) } catch (_: InterruptedException) {}
        thread = null
    }

    private fun ensureRunning() {
        val t = thread
        if (t != null && t.isAlive) return
        thread = Thread({
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val (_, task) = queue.take()
                    task()
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, "GardenWorkerThread").also { it.isDaemon = true; it.start() }
    }
}
```

- [ ] **Step 3: Create ScriptBridge.kt**

```kotlin
package org.phantom.internal.garden

import net.minecraft.client.Minecraft

object ScriptBridge {

    private val mc get() = Minecraft.getInstance()

    fun startFarming(script: String)      = send("ez-macrostart $script")
    fun startPestScript(script: String)   = send("ez-macrostart $script")
    fun startVisitorScript(script: String) = send("ez-macrostart $script")
    fun startReturnScript(script: String) = send("ez-macrostart $script")
    fun stopScript()                      = send("ez-macrostop")
    fun setSpawn()                        = send("setspawn")
    fun warpGarden()                      = send("warp garden")

    private fun send(cmd: String) {
        mc.player?.connection?.sendCommand(cmd)
    }
}
```

- [ ] **Step 4: Build check**

```bash
./gradlew build 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/
git commit -m "feat: add GardenState, GardenWorkerThread, ScriptBridge"
```

---

## Task 2: GardenInventoryAccessor mixin

**Files:**
- Create: `src/main/java/org/phantom/mixin/client/GardenInventoryAccessor.java`

- [ ] **Step 1: Create accessor**

```java
package org.phantom.mixin.client;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Inventory.class)
public interface GardenInventoryAccessor {
    @Accessor("items")
    NonNullList<ItemStack> getItems();
}
```

- [ ] **Step 2: Build check**

```bash
./gradlew build 2>&1 | tail -20
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/phantom/mixin/client/GardenInventoryAccessor.java
git commit -m "feat: add GardenInventoryAccessor mixin"
```

---

## Task 3: PestTabListParser

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/garden/managers/PestTabListParser.kt`

Uses `TabOverlayAccessor` (created in Pet Display HUD plan Task 1 â€” if Pet Display was not implemented first, create `TabOverlayAccessor.java` now per that plan's Task 1).

- [ ] **Step 1: Create PestTabListParser.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.mixin.client.TabOverlayAccessor

data class TabListData(
    val alivePests: Int,
    val cooldownSeconds: Int,
    val infestedPlots: List<String>,
    val bonusActive: Boolean,
)

object PestTabListParser {

    private val mc = Minecraft.getInstance()

    private val ALIVE_REGEX   = Regex("""alive[:\s]+(\d+)""", RegexOption.IGNORE_CASE)
    private val COOLDOWN_REGEX = Regex("""cooldown[:\s]+(\d+)s""", RegexOption.IGNORE_CASE)
    private val PLOT_REGEX    = Regex("""plot[:\s]+([a-zA-Z0-9_ ,]+)""", RegexOption.IGNORE_CASE)
    private val BONUS_REGEX   = Regex("""bonus[:\s]+active""", RegexOption.IGNORE_CASE)

    fun parse(): TabListData {
        val gui = mc.gui ?: return empty()
        val overlay = gui.tabList as? TabOverlayAccessor ?: return empty()

        val raw = buildString {
            overlay.header?.let { append(strip(it.string)).append("\n") }
            overlay.footer?.let { append(strip(it.string)).append("\n") }
            // Also check player display names
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

    private fun strip(text: String) = text.replace(Regex("Â§[0-9a-fk-or]"), "")
    private fun empty() = TabListData(0, 0, emptyList(), false)
}
```

- [ ] **Step 2: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/PestTabListParser.kt
git commit -m "feat: add PestTabListParser"
```

---

## Task 4: Pest managers (7 files)

**Files:**
- Create: `managers/PestManager.kt`
- Create: `managers/PestCleaningSequencer.kt`
- Create: `managers/PestAotvManager.kt`
- Create: `managers/PestPrepSwapManager.kt`
- Create: `managers/PestReturnManager.kt`
- Create: `managers/PestBonusManager.kt`
- Create: `managers/CropFeverManager.kt`

All paths under `src/main/kotlin/org/phantom/internal/garden/managers/`.

- [ ] **Step 1: Create PestManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft

object PestManager {

    @Volatile var lastAliveCount = 0
    @Volatile var cleaningCooldownUntil = 0L
    @Volatile var prepSwapDoneForCycle = false

    fun reset() {
        lastAliveCount = 0
        cleaningCooldownUntil = 0L
        prepSwapDoneForCycle = false
    }

    /** Called each tick while state == FARMING. Returns true if cleaning should start. */
    fun update(threshold: Int): Boolean {
        val data = PestTabListParser.parse()
        lastAliveCount = data.alivePests

        if (System.currentTimeMillis() < cleaningCooldownUntil) return false
        return data.alivePests >= threshold
    }

    fun startCooldown(durationMs: Long) {
        cleaningCooldownUntil = System.currentTimeMillis() + durationMs
        prepSwapDoneForCycle = false
    }
}
```

- [ ] **Step 2: Create PestCleaningSequencer.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge

object PestCleaningSequencer {

    @Volatile var isRunning = false

    fun reset() { isRunning = false }

    fun startSequence() {
        if (isRunning) return
        isRunning = true
        GardenWorkerThread.submit("pest-clean") {
            val mc = Minecraft.getInstance()
            try {
                // 1. Stop farming script
                mc.execute { ScriptBridge.stopScript() }
                Thread.sleep(500)

                // 2. Set spawn
                mc.execute { ScriptBridge.setSpawn() }
                Thread.sleep(300)

                // 3. Gear swap (wardrobe/equipment)
                if (GardenMacroModule.autoWardrobeEnabled) {
                    GearManager.swapForPest()
                    Thread.sleep(2000)
                }

                // 4. Optional AOTV to roof
                if (GardenMacroModule.aotvEnabled) {
                    PestAotvManager.teleportToRoof()
                    Thread.sleep(1500)
                }

                // 5. Start pest cleaning script
                mc.execute { ScriptBridge.startPestScript(GardenMacroModule.pestScript) }
                Thread.sleep(500)

            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isRunning = false
            }
        }
    }
}
```

- [ ] **Step 3: Create PestAotvManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.rotation.strategy.TimedEaseStrategy

object PestAotvManager {

    @Volatile var isActive = false

    fun reset() { isActive = false }

    fun teleportToRoof() {
        val mc = Minecraft.getInstance()
        isActive = true
        try {
            // Rotate pitch to configured roof angle
            val pitch = GardenMacroModule.roofPitch.toFloat()
            mc.execute {
                RotationExecutor.rotateTo(
                    net.minecraft.world.phys.Vec2(mc.player?.yRot ?: 0f, pitch),
                    TimedEaseStrategy(400)
                )
            }
            Thread.sleep(500)

            // Right-click AOTV (item in offhand or hotbar â€” find by name)
            mc.execute {
                val player = mc.player ?: return@execute
                val aotv = (0..8).firstOrNull { slot ->
                    player.inventory.getItem(slot).hoverName.string.contains("Aspect of the Void", ignoreCase = true)
                }
                if (aotv != null) {
                    player.inventory.selected = aotv
                }
                (mc as? org.phantom.mixin.client.MinecraftAccessor)?.rightClick()
            }
            Thread.sleep(800)
        } finally {
            isActive = false
        }
    }
}
```

- [ ] **Step 4: Create PestPrepSwapManager.kt**

```kotlin
package org.phantom.internal.garden.managers

object PestPrepSwapManager {

    @Volatile var swapDone = false

    fun reset() { swapDone = false }

    /** Returns true if a prep-swap should be triggered (pest count approaching threshold). */
    fun shouldPrepSwap(aliveCount: Int, threshold: Int): Boolean {
        if (swapDone) return false
        return aliveCount >= (threshold - 1).coerceAtLeast(1)
    }

    fun markDone() { swapDone = true }
}
```

- [ ] **Step 5: Create PestReturnManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge

object PestReturnManager {

    @Volatile var isReturning = false

    fun reset() { isReturning = false }

    fun startReturn() {
        if (isReturning) return
        isReturning = true
        GardenWorkerThread.submit("pest-return") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { ScriptBridge.startReturnScript(GardenMacroModule.returnScript) }
                Thread.sleep(500)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isReturning = false
                mc.execute { GardenMacroModule.setState(GardenState.FARMING) }
            }
        }
    }
}
```

- [ ] **Step 6: Create PestBonusManager.kt**

```kotlin
package org.phantom.internal.garden.managers

object PestBonusManager {

    @Volatile var bonusActive = false
    @Volatile var lastChecked = 0L

    fun reset() {
        bonusActive = false
        lastChecked = 0L
    }

    fun update() {
        val now = System.currentTimeMillis()
        if (now - lastChecked < 5000) return
        lastChecked = now
        val data = PestTabListParser.parse()
        bonusActive = data.bonusActive
    }
}
```

- [ ] **Step 7: Create CropFeverManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft

object CropFeverManager {

    @Volatile var feverActive = false
    @Volatile var feverDetectedAt = 0L
    private const val FEVER_TIMEOUT_MS = 65_000L

    fun reset() {
        feverActive = false
        feverDetectedAt = 0L
    }

    fun onChatMessage(message: String) {
        if (message.contains("CROP FEVER", ignoreCase = true)) {
            feverActive = true
            feverDetectedAt = System.currentTimeMillis()
        }
    }

    fun update() {
        if (feverActive && System.currentTimeMillis() - feverDetectedAt > FEVER_TIMEOUT_MS) {
            feverActive = false
        }
    }

    /** Returns true if pest cleaning should be delayed. */
    fun shouldDelay() = feverActive
}
```

- [ ] **Step 8: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 9: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/
git commit -m "feat: add pest managers (PestManager, PestCleaningSequencer, PestAotv, PestPrepSwap, PestReturn, PestBonus, CropFever)"
```

---

## Task 5: VisitorManager

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/garden/managers/VisitorManager.kt`

- [ ] **Step 1: Create VisitorManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge

object VisitorManager {

    @Volatile var isHandlingVisitor = false
    @Volatile var visitorCooldownUntil = 0L
    @Volatile var visitorDetected = false
    private val VISITOR_COOLDOWN_MS = 30_000L

    fun reset() {
        isHandlingVisitor = false
        visitorCooldownUntil = 0L
        visitorDetected = false
    }

    fun update(): Boolean {
        if (isHandlingVisitor) return false
        if (System.currentTimeMillis() < visitorCooldownUntil) return false
        val data = PestTabListParser.parse()
        // Detect visitor presence from tab list â€” Hypixel shows visitor count
        // Check tab list footer for "Visitor" text
        val gui = Minecraft.getInstance().gui ?: return false
        val overlay = gui.tabList as? org.phantom.mixin.client.TabOverlayAccessor ?: return false
        val footer = overlay.footer?.string?.replace(Regex("Â§[0-9a-fk-or]"), "") ?: return false
        return footer.contains("visitor", ignoreCase = true) && !footer.contains("visitors: 0", ignoreCase = true)
    }

    fun startVisitorSequence() {
        if (isHandlingVisitor) return
        isHandlingVisitor = true
        GardenWorkerThread.submit("visitor") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { ScriptBridge.stopScript() }
                Thread.sleep(300)

                if (GardenMacroModule.autoWardrobeEnabled) {
                    GearManager.swapForVisitor()
                    Thread.sleep(2000)
                }

                mc.execute { ScriptBridge.startVisitorScript(GardenMacroModule.visitorScript) }
                Thread.sleep(500)

            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isHandlingVisitor = false
                visitorCooldownUntil = System.currentTimeMillis() + VISITOR_COOLDOWN_MS
                mc.execute { GardenMacroModule.setState(GardenState.FARMING) }
            }
        }
    }

    fun onChatMessage(message: String) {
        if (message.contains("offer accepted", ignoreCase = true)) {
            // ROI tracking hook â€” ProfitManager listens to this
        }
    }
}
```

- [ ] **Step 2: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/VisitorManager.kt
git commit -m "feat: add VisitorManager"
```

---

## Task 6: Gear managers (WardrobeManager, EquipmentManager, GearManager, RodManager)

**Files:**
- Create: `managers/WardrobeManager.kt`
- Create: `managers/EquipmentManager.kt`
- Create: `managers/GearManager.kt`
- Create: `managers/RodManager.kt`

- [ ] **Step 1: Create WardrobeManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.DyeColor
import org.phantom.internal.garden.GardenWorkerThread

object WardrobeManager {

    enum class LoadoutType { FARMING, PEST, VISITOR }

    @Volatile var isSwapping = false

    fun reset() { isSwapping = false }

    fun swapTo(type: LoadoutType) {
        val targetSlot = when (type) {
            LoadoutType.FARMING -> GardenMacroModule.farmingWardrobeSlot.toInt()
            LoadoutType.PEST    -> GardenMacroModule.pestWardrobeSlot.toInt()
            LoadoutType.VISITOR -> GardenMacroModule.visitorWardrobeSlot.toInt()
        }
        isSwapping = true
        try {
            val mc = Minecraft.getInstance()
            // Open wardrobe via command
            mc.execute { mc.player?.connection?.sendCommand("wardrobe") }
            Thread.sleep(1200) // wait for GUI

            // Click the target slot in the wardrobe GUI
            mc.execute {
                val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                // Wardrobe slot layout: slots start at index 9, each slot is 1 item
                // Slot index in container = (targetSlot - 1) + offset
                val slotIndex = (targetSlot - 1).coerceIn(0, 17)
                val slot = screen.menu.slots.getOrNull(slotIndex + 9) ?: return@execute
                // Simulate left-click on the slot
                (mc.gameMode)?.handleInventoryMouseClick(
                    screen.menu.containerId, slot.index, 0,
                    net.minecraft.world.inventory.ClickType.PICKUP,
                    mc.player!!
                )
            }
            Thread.sleep(800)

            // Close GUI
            mc.execute { mc.player?.closeContainer() }
            Thread.sleep(400)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            isSwapping = false
        }
    }
}
```

- [ ] **Step 2: Create EquipmentManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import org.phantom.internal.garden.GardenWorkerThread

object EquipmentManager {

    @Volatile var isSwapping = false

    fun reset() { isSwapping = false }

    fun swapTo(armorSetName: String) {
        if (armorSetName.isBlank()) return
        isSwapping = true
        try {
            val mc = Minecraft.getInstance()
            mc.execute { mc.player?.connection?.sendCommand("equipment") }
            Thread.sleep(1200)

            mc.execute {
                val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                // Find slot whose display name matches armorSetName
                val slot = screen.menu.slots.firstOrNull { slot ->
                    slot.item.hoverName.string.contains(armorSetName, ignoreCase = true)
                } ?: return@execute
                (mc.gameMode)?.handleInventoryMouseClick(
                    screen.menu.containerId, slot.index, 0,
                    net.minecraft.world.inventory.ClickType.PICKUP,
                    mc.player!!
                )
            }
            Thread.sleep(GardenMacroModule.swapDelayMs.toLong())
            mc.execute { mc.player?.closeContainer() }
            Thread.sleep(300)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            isSwapping = false
        }
    }
}
```

- [ ] **Step 3: Create GearManager.kt**

```kotlin
package org.phantom.internal.garden.managers

object GearManager {

    @Volatile var isSwapping = false

    fun reset() { isSwapping = false }

    fun swapForPest() {
        isSwapping = true
        try {
            if (GardenMacroModule.autoWardrobeEnabled) {
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.PEST)
            }
            if (GardenMacroModule.pestArmor.isNotBlank()) {
                EquipmentManager.swapTo(GardenMacroModule.pestArmor)
            }
        } finally {
            isSwapping = false
        }
    }

    fun swapForFarming() {
        isSwapping = true
        try {
            if (GardenMacroModule.autoWardrobeEnabled) {
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.FARMING)
            }
            if (GardenMacroModule.farmingArmor.isNotBlank()) {
                EquipmentManager.swapTo(GardenMacroModule.farmingArmor)
            }
        } finally {
            isSwapping = false
        }
    }

    fun swapForVisitor() {
        isSwapping = true
        try {
            if (GardenMacroModule.autoWardrobeEnabled) {
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.VISITOR)
            }
            if (GardenMacroModule.visitorArmor.isNotBlank()) {
                EquipmentManager.swapTo(GardenMacroModule.visitorArmor)
            }
        } finally {
            isSwapping = false
        }
    }
}
```

- [ ] **Step 4: Create RodManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft

object RodManager {

    @Volatile var isActive = false
    @Volatile var lastCastTime = 0L
    private const val CAST_INTERVAL_MS = 30_000L

    fun reset() {
        isActive = false
        lastCastTime = 0L
    }

    /** Called each tick while FARMING. Auto-casts rod if enough time has elapsed. */
    fun update() {
        val now = System.currentTimeMillis()
        if (now - lastCastTime < CAST_INTERVAL_MS) return
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        // Find rod in hotbar
        val rodSlot = (0..8).firstOrNull { slot ->
            player.inventory.getItem(slot).hoverName.string.contains("rod", ignoreCase = true)
        } ?: return

        val prev = player.inventory.selected
        player.inventory.selected = rodSlot
        (mc as? org.phantom.mixin.client.MinecraftAccessor)?.rightClick()
        player.inventory.selected = prev
        lastCastTime = now
    }
}
```

- [ ] **Step 5: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/WardrobeManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/EquipmentManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/GearManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/RodManager.kt
git commit -m "feat: add gear managers (Wardrobe, Equipment, Gear, Rod)"
```

---

## Task 7: Economy managers (George, BookCombine, Junk, BoosterCookie)

**Files:**
- Create: `managers/GeorgeManager.kt`
- Create: `managers/BookCombineManager.kt`
- Create: `managers/JunkManager.kt`
- Create: `managers/BoosterCookieManager.kt`

- [ ] **Step 1: Create GeorgeManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.mixin.client.GardenInventoryAccessor

object GeorgeManager {

    @Volatile var isHandling = false
    @Volatile var lastSellTime = 0L
    private const val SELL_COOLDOWN_MS = 60_000L

    fun reset() {
        isHandling = false
        lastSellTime = 0L
    }

    /** Returns true if George sell should trigger. */
    fun shouldSell(): Boolean {
        if (isHandling) return false
        if (System.currentTimeMillis() - lastSellTime < SELL_COOLDOWN_MS) return false
        val rarities = GardenMacroModule.georgeRarity.split(",").map { it.trim().uppercase() }
        return findTargetPets(rarities).isNotEmpty()
    }

    fun startSell() {
        isHandling = true
        GardenWorkerThread.submit("george-sell") {
            val mc = Minecraft.getInstance()
            try {
                val rarities = GardenMacroModule.georgeRarity.split(",").map { it.trim().uppercase() }
                val pets = findTargetPets(rarities)
                if (pets.isEmpty()) return@submit

                mc.execute { mc.player?.connection?.sendCommand("george") }
                Thread.sleep(1500)

                mc.execute {
                    val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                    // Find "Sell Pets" or similar button in George GUI
                    val sellSlot = screen.menu.slots.firstOrNull { slot ->
                        slot.item.hoverName.string.contains("sell", ignoreCase = true)
                    } ?: return@execute
                    (mc.gameMode)?.handleInventoryMouseClick(
                        screen.menu.containerId, sellSlot.index, 0,
                        net.minecraft.world.inventory.ClickType.PICKUP, mc.player!!
                    )
                }
                Thread.sleep(1000)
                mc.execute { mc.player?.closeContainer() }
                Thread.sleep(300)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isHandling = false
                lastSellTime = System.currentTimeMillis()
                mc.execute { GardenMacroModule.setState(GardenState.FARMING) }
            }
        }
    }

    private fun findTargetPets(rarities: List<String>): List<ItemStack> {
        val mc = Minecraft.getInstance()
        val inv = (mc.player?.inventory as? GardenInventoryAccessor)?.items ?: return emptyList()
        return inv.filter { stack ->
            if (stack.isEmpty) return@filter false
            val name = stack.hoverName.string
            name.contains("pet", ignoreCase = true) &&
            rarities.any { name.contains(it, ignoreCase = true) }
        }
    }
}
```

- [ ] **Step 2: Create BookCombineManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.mixin.client.GardenInventoryAccessor

object BookCombineManager {

    @Volatile var isHandling = false

    fun reset() { isHandling = false }

    fun shouldCombine(): Boolean {
        if (isHandling) return false
        val mc = Minecraft.getInstance()
        val inv = (mc.player?.inventory as? GardenInventoryAccessor)?.items ?: return false
        val level = GardenMacroModule.bookCombineLevel.toInt()
        val bookCount = inv.count { stack ->
            !stack.isEmpty && stack.hoverName.string.contains("enchanted book", ignoreCase = true) &&
            stack.hoverName.string.contains("$level", ignoreCase = true)
        }
        return bookCount >= 2
    }

    fun startCombine() {
        isHandling = true
        GardenWorkerThread.submit("book-combine") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { mc.player?.connection?.sendCommand("anvil") }
                Thread.sleep(1200)
                // Place books into anvil slots â€” slot 0 and 1 are input
                mc.execute {
                    val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                    val inv = (mc.player?.inventory as? GardenInventoryAccessor)?.items ?: return@execute
                    val level = GardenMacroModule.bookCombineLevel.toInt()
                    val bookSlots = inv.indices.filter { i ->
                        val s = inv[i]
                        !s.isEmpty && s.hoverName.string.contains("enchanted book", ignoreCase = true) &&
                        s.hoverName.string.contains("$level", ignoreCase = true)
                    }.take(2)
                    bookSlots.forEachIndexed { _, invSlot ->
                        (mc.gameMode)?.handleInventoryMouseClick(
                            screen.menu.containerId,
                            screen.menu.slots.size - 36 + invSlot,
                            0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, mc.player!!
                        )
                        Thread.sleep(200)
                    }
                }
                Thread.sleep(800)
                mc.execute { mc.player?.closeContainer() }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isHandling = false
                mc.execute { GardenMacroModule.setState(GardenState.FARMING) }
            }
        }
    }
}
```

- [ ] **Step 3: Create JunkManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.mixin.client.GardenInventoryAccessor

object JunkManager {

    @Volatile var isHandling = false

    fun reset() { isHandling = false }

    fun shouldDrop(): Boolean {
        if (isHandling) return false
        val junkList = GardenMacroModule.junkItems.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
        if (junkList.isEmpty()) return false
        val mc = Minecraft.getInstance()
        val inv = (mc.player?.inventory as? GardenInventoryAccessor)?.items ?: return false
        return inv.any { stack -> !stack.isEmpty && junkList.any { junk -> stack.hoverName.string.lowercase().contains(junk) } }
    }

    fun startDrop() {
        isHandling = true
        GardenWorkerThread.submit("junk-drop") {
            val mc = Minecraft.getInstance()
            try {
                val junkList = GardenMacroModule.junkItems.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
                val inv = (mc.player?.inventory as? GardenInventoryAccessor)?.items ?: return@submit
                mc.execute {
                    val player = mc.player ?: return@execute
                    for (i in inv.indices) {
                        val stack = inv[i]
                        if (!stack.isEmpty && junkList.any { junk -> stack.hoverName.string.lowercase().contains(junk) }) {
                            player.drop(stack, false)
                        }
                    }
                }
                Thread.sleep(200)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isHandling = false
                mc.execute { GardenMacroModule.setState(GardenState.FARMING) }
            }
        }
    }
}
```

- [ ] **Step 4: Create BoosterCookieManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.mixin.client.GardenInventoryAccessor

object BoosterCookieManager {

    @Volatile var isActive = false
    @Volatile var lastUseTime = 0L
    private const val USE_COOLDOWN_MS = 3_600_000L // 1 hour

    fun reset() {
        isActive = false
        lastUseTime = 0L
    }

    fun shouldUseCookie(): Boolean {
        if (GardenMacroModule.cookieItem.isBlank()) return false
        if (System.currentTimeMillis() - lastUseTime < USE_COOLDOWN_MS) return false
        return findCookieSlot() >= 0
    }

    fun useCookie() {
        val slot = findCookieSlot()
        if (slot < 0) return
        GardenWorkerThread.submit("booster-cookie") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute {
                    val player = mc.player ?: return@execute
                    player.inventory.selected = slot
                    (mc as? org.phantom.mixin.client.MinecraftAccessor)?.rightClick()
                }
                Thread.sleep(500)
                lastUseTime = System.currentTimeMillis()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun findCookieSlot(): Int {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return -1
        val target = GardenMacroModule.cookieItem.trim().lowercase()
        return (0..8).firstOrNull { slot ->
            player.inventory.getItem(slot).hoverName.string.lowercase().contains(target)
        } ?: -1
    }
}
```

- [ ] **Step 5: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/GeorgeManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/BookCombineManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/JunkManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/BoosterCookieManager.kt
git commit -m "feat: add economy managers (George, BookCombine, Junk, BoosterCookie)"
```

---

## Task 8: ProfitManager + PetXpTracker

**Files:**
- Create: `managers/ProfitManager.kt`
- Create: `managers/PetXpTracker.kt`

- [ ] **Step 1: Create ProfitManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenWorkerThread
import java.net.URL

object ProfitManager {

    @Volatile var sessionProfit = 0L
    @Volatile var dailyProfit   = 0L
    @Volatile var lifetimeProfit = 0L
    @Volatile var lastBazaarRefresh = 0L
    private val bazaarPrices = mutableMapOf<String, Double>()
    private var sessionStart = System.currentTimeMillis()

    // Simple item price table (expandable) â€” key = item name (lowercase)
    private val hardcodedPrices = mapOf(
        "enchanted carrot" to 1_000.0,
        "enchanted potato" to 1_200.0,
        "enchanted wheat" to 800.0,
        "enchanted sugar cane" to 900.0,
        "enchanted cactus" to 1_500.0,
    )

    fun reset() {
        sessionProfit = 0L
        sessionStart = System.currentTimeMillis()
    }

    fun fullReset() {
        sessionProfit = 0L
        dailyProfit   = 0L
        lifetimeProfit = 0L
        sessionStart = System.currentTimeMillis()
    }

    fun addProfit(amount: Long) {
        sessionProfit  += amount
        dailyProfit    += amount
        lifetimeProfit += amount
    }

    fun onChatMessage(message: String) {
        // Parse dropped items from chat â€” e.g. "+ 1x Enchanted Carrot"
        val dropMatch = Regex("""\+\s*(\d+)x?\s+(.+)""").find(message) ?: return
        val count = dropMatch.groupValues[1].toLongOrNull() ?: return
        val item  = dropMatch.groupValues[2].trim().lowercase()
        val price = bazaarPrices[item] ?: hardcodedPrices[item] ?: return
        addProfit((count * price).toLong())
    }

    fun refreshBazaarIfNeeded() {
        val interval = GardenMacroModule.bazaarRefreshSecs.toLong() * 1000
        if (System.currentTimeMillis() - lastBazaarRefresh < interval) return
        lastBazaarRefresh = System.currentTimeMillis()
        GardenWorkerThread.submit("bazaar-refresh") {
            try {
                val json = URL("https://api.hypixel.net/v2/skyblock/bazaar").readText()
                val root = JsonParser.parseString(json).asJsonObject
                val products = root.getAsJsonObject("products") ?: return@submit
                val newPrices = mutableMapOf<String, Double>()
                for ((name, data) in products.entrySet()) {
                    val buy = data.asJsonObject
                        .getAsJsonObject("quick_status")
                        ?.get("buyPrice")?.asDouble ?: continue
                    newPrices[name.lowercase().replace("_", " ")] = buy
                }
                Minecraft.getInstance().execute {
                    bazaarPrices.clear()
                    bazaarPrices.putAll(newPrices)
                }
            } catch (_: Exception) { /* network failure â€” use cached prices */ }
        }
    }
}
```

- [ ] **Step 2: Create PetXpTracker.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.mixin.client.TabOverlayAccessor

object PetXpTracker {

    @Volatile var currentXp = 0L
    @Volatile var xpToNextLevel = 0L
    @Volatile var totalXpGained = 0L

    private val XP_REGEX = Regex("""pet\s+xp[:\s]+([\d,]+)\s*/\s*([\d,]+)""", RegexOption.IGNORE_CASE)

    fun reset() {
        currentXp = 0L
        xpToNextLevel = 0L
        totalXpGained = 0L
    }

    fun update() {
        val mc = Minecraft.getInstance()
        val overlay = mc.gui?.tabList as? TabOverlayAccessor ?: return
        val text = buildString {
            overlay.header?.let { append(it.string.replace(Regex("Â§[0-9a-fk-or]"), "")) }
            overlay.footer?.let { append(it.string.replace(Regex("Â§[0-9a-fk-or]"), "")) }
        }
        XP_REGEX.find(text)?.let { m ->
            val newXp = m.groupValues[1].replace(",", "").toLongOrNull() ?: return
            if (newXp > currentXp && currentXp > 0) {
                totalXpGained += (newXp - currentXp)
            }
            currentXp = newXp
            xpToNextLevel = m.groupValues[2].replace(",", "").toLongOrNull() ?: 0L
        }
    }
}
```

- [ ] **Step 3: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/ProfitManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/PetXpTracker.kt
git commit -m "feat: add ProfitManager and PetXpTracker"
```

---

## Task 9: Session managers (DynamicRestManager, RecoveryManager, RestartManager)

**Files:**
- Create: `managers/DynamicRestManager.kt`
- Create: `managers/RecoveryManager.kt`
- Create: `managers/RestartManager.kt`

- [ ] **Step 1: Create DynamicRestManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge
import kotlin.random.Random

object DynamicRestManager {

    @Volatile var farmingStartTime = 0L
    @Volatile var targetWorkDurationMs = 0L
    @Volatile var isResting = false
    @Volatile var reconnectAt = 0L

    fun reset() {
        farmingStartTime = System.currentTimeMillis()
        targetWorkDurationMs = calculateWorkDuration()
        isResting = false
    }

    fun fullReset() {
        reset()
        reconnectAt = 0L
    }

    fun update(): Boolean {
        if (isResting) return false
        if (targetWorkDurationMs <= 0) return false
        val elapsed = System.currentTimeMillis() - farmingStartTime
        return elapsed >= targetWorkDurationMs
    }

    fun startRest() {
        if (isResting) return
        isResting = true
        GardenWorkerThread.submit("rest-start") {
            val mc = Minecraft.getInstance()
            try {
                mc.execute { ScriptBridge.stopScript() }
                Thread.sleep(500)
                mc.execute { ScriptBridge.setSpawn() }
                Thread.sleep(500)
                val breakMs = calculateBreakDuration()
                reconnectAt = System.currentTimeMillis() + breakMs
                mc.execute { mc.player?.connection?.sendDisconnect(
                    net.minecraft.network.chat.Component.literal("Rest break")
                ) }
                Thread.sleep(breakMs)
                // After break, reconnect is handled by RecoveryManager detecting disconnect
                isResting = false
                farmingStartTime = System.currentTimeMillis()
                targetWorkDurationMs = calculateWorkDuration()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    fun timeUntilRestMs(): Long {
        if (targetWorkDurationMs <= 0) return Long.MAX_VALUE
        val elapsed = System.currentTimeMillis() - farmingStartTime
        return (targetWorkDurationMs - elapsed).coerceAtLeast(0)
    }

    private fun calculateWorkDuration(): Long {
        val base = GardenMacroModule.workDurationMins * 60_000L
        val offset = Random.nextLong(GardenMacroModule.workOffsetMins * 60_000L + 1)
        return base + offset
    }

    private fun calculateBreakDuration(): Long {
        val base = GardenMacroModule.breakDurationMins * 60_000L
        val offset = Random.nextLong(GardenMacroModule.breakOffsetMins * 60_000L + 1)
        return base + offset
    }
}
```

- [ ] **Step 2: Create RecoveryManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge
import kotlin.random.Random

object RecoveryManager {

    @Volatile var recoveryAttempts = 0
    @Volatile var isRecovering = false
    @Volatile var wasConnected = true

    fun reset() {
        recoveryAttempts = 0
        isRecovering = false
        wasConnected = true
    }

    /** Called from GardenMacroModule TickEvent.End when mc.connection == null and state is active. */
    fun onDisconnect() {
        if (isRecovering) return
        if (recoveryAttempts >= GardenMacroModule.maxRecoveryAttempts.toInt()) {
            GardenMacroModule.setState(GardenState.OFF)
            return
        }
        isRecovering = true
        recoveryAttempts++
        GardenWorkerThread.submit("recovery") {
            val mc = Minecraft.getInstance()
            try {
                val delayMin = GardenMacroModule.reconnectDelayMin.toLong() * 1000
                val delayMax = GardenMacroModule.reconnectDelayMax.toLong() * 1000
                val delay = delayMin + Random.nextLong(delayMax - delayMin + 1)
                Thread.sleep(delay)

                // Reconnect to last server
                mc.execute {
                    val serverData = mc.currentServer ?: return@execute
                    net.minecraft.client.multiplayer.resolver.ServerAddress.parseString(serverData.ip)?.let { addr ->
                        mc.setScreen(net.minecraft.client.gui.screens.ConnectScreen(
                            net.minecraft.client.gui.screens.TitleScreen(),
                            mc, addr, serverData, false, null
                        ))
                    }
                }
                Thread.sleep(10_000) // Wait for connection

                mc.execute {
                    ScriptBridge.warpGarden()
                }
                Thread.sleep(3_000)
                mc.execute {
                    GardenMacroModule.setState(GardenState.FARMING)
                    ScriptBridge.startFarming(GardenMacroModule.farmScript)
                }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                isRecovering = false
            }
        }
    }
}
```

- [ ] **Step 3: Create RestartManager.kt**

```kotlin
package org.phantom.internal.garden.managers

import net.minecraft.client.Minecraft
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.GardenState
import org.phantom.internal.garden.GardenWorkerThread
import org.phantom.internal.garden.ScriptBridge

object RestartManager {

    @Volatile var restartDetected = false
    @Volatile var abortAt = 0L

    fun reset() {
        restartDetected = false
        abortAt = 0L
    }

    private val RESTART_PATTERNS = listOf(
        "server going down",
        "server is restarting",
        "evacuate",
        "server restart in"
    )

    fun onChatMessage(message: String) {
        val lower = message.lowercase()
        if (RESTART_PATTERNS.any { lower.contains(it) }) {
            restartDetected = true
            abortAt = System.currentTimeMillis() + 30_000 // 30s grace period
        }
        if (lower.contains("jacob's farming contest") && lower.contains("over")) {
            // Jacob's contest ended â€” safe to abort now
            if (restartDetected) triggerAbort()
        }
    }

    fun update() {
        if (!restartDetected) return
        if (System.currentTimeMillis() >= abortAt) triggerAbort()
    }

    private fun triggerAbort() {
        restartDetected = false
        GardenWorkerThread.submit("restart-abort") {
            val mc = Minecraft.getInstance()
            mc.execute {
                ScriptBridge.stopScript()
                GardenMacroModule.setState(GardenState.OFF)
            }
        }
    }
}
```

- [ ] **Step 4: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/managers/DynamicRestManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/RecoveryManager.kt \
        src/main/kotlin/org/phantom/internal/garden/managers/RestartManager.kt
git commit -m "feat: add session managers (DynamicRest, Recovery, Restart)"
```

---

## Task 10: GardenHud

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/garden/GardenHud.kt`

- [ ] **Step 1: Create GardenHud.kt**

```kotlin
package org.phantom.internal.garden

import kotlin.math.cos
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.api.util.ui.helper.Gradient
import org.phantom.internal.garden.managers.DynamicRestManager
import org.phantom.internal.garden.managers.ProfitManager
import java.util.concurrent.TimeUnit

object GardenHud {

    fun render(
        x: Float, y: Float, w: Float, h: Float,
        state: GardenState,
        showProfit: Boolean,
        showRest: Boolean,
    ) {
        val radius = 6f
        val pad = 10f
        val textColor = 0xFFFFFFFF.toInt()
        val dimColor  = 0xBBAFCFFF.toInt()

        // â”€â”€ Background â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        NVGRenderer.rect(x, y, w, h, 0xEE0D1530.toInt(), radius)

        // â”€â”€ Animated border â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        val angle  = (System.currentTimeMillis() % 10000L).toFloat() / 10000f * (Math.PI.toFloat() * 2f)
        val shiftX = cos(angle) * (w * 0.4f)
        NVGRenderer.hollowGradientRectShifted(
            x, y, w, h, 1.5f,
            0xFF6AB8FF.toInt(), 0xFF1A6EFF.toInt(),
            Gradient.LeftToRight, radius, shiftX, 0f
        )

        var cy = y + 14f

        // â”€â”€ Title + state badge â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        NVGRenderer.text("GARDEN MACRO", x + pad, cy, 11f, 0xFF6AB8FF.toInt())
        val stateStr = state.name
        val stateColor = when (state) {
            GardenState.FARMING    -> 0xFF4CFF72.toInt()
            GardenState.CLEANING   -> 0xFFFFD84C.toInt()
            GardenState.VISITING   -> 0xFF4CFFE0.toInt()
            GardenState.AUTOSELLING -> 0xFFFF944C.toInt()
            GardenState.RESTING    -> 0xFF888888.toInt()
            GardenState.RECOVERING -> 0xFFFF4C4C.toInt()
            GardenState.OFF        -> 0xFF444444.toInt()
        }
        val stateW = NVGRenderer.textWidth(stateStr, 9f)
        NVGRenderer.rect(x + w - pad - stateW - 8f, cy - 9f, stateW + 8f, 12f, (stateColor and 0x00FFFFFF) or 0x33000000, 3f)
        NVGRenderer.text(stateStr, x + w - pad - stateW - 4f, cy, 9f, stateColor)

        cy += 14f

        // â”€â”€ Runtime â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        val runtime = formatDuration(System.currentTimeMillis() - GardenMacroModule.sessionStartTime)
        NVGRenderer.text("Runtime: $runtime", x + pad, cy, 10f, dimColor)
        cy += 14f

        // â”€â”€ Rest countdown â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (showRest && state == GardenState.FARMING) {
            val restMs = DynamicRestManager.timeUntilRestMs()
            val restStr = "Next Rest: ${formatDuration(restMs)}"
            NVGRenderer.text(restStr, x + pad, cy, 10f, dimColor)
            cy += 11f

            // Progress bar
            val barW = w - pad * 2f
            val barH = 5f
            val totalMs = DynamicRestManager.targetWorkDurationMs.coerceAtLeast(1L)
            val elapsed = totalMs - restMs.coerceAtLeast(0L)
            val ratio = (elapsed.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
            NVGRenderer.rect(x + pad, cy, barW, barH, 0xFF1A2050.toInt(), 3f)
            if (ratio > 0f) {
                NVGRenderer.gradientRect(x + pad, cy, barW * ratio, barH, 0xFF1A6EFF.toInt(), 0xFF6AB8FF.toInt(), Gradient.LeftToRight, 3f)
            }
            cy += 12f
        }

        // â”€â”€ Divider â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (showProfit) {
            NVGRenderer.rect(x + pad, cy, w - pad * 2f, 1f, 0x331A3070.toInt(), 0f)
            cy += 8f

            NVGRenderer.text("Profit", x + pad, cy, 10f, 0xFF6AB8FF.toInt())
            cy += 13f

            fun profitLine(label: String, value: Long) {
                NVGRenderer.text(label, x + pad, cy, 9f, dimColor)
                val valStr = formatProfit(value)
                val valColor = if (value >= 0) 0xFF4CFF72.toInt() else 0xFFFF4C4C.toInt()
                val valW = NVGRenderer.textWidth(valStr, 9f)
                NVGRenderer.text(valStr, x + w - pad - valW, cy, 9f, valColor)
                cy += 12f
            }

            profitLine("Session:", ProfitManager.sessionProfit)
            profitLine("Daily:",   ProfitManager.dailyProfit)
            profitLine("Lifetime:", ProfitManager.lifetimeProfit)
        }
    }

    private fun formatDuration(ms: Long): String {
        val s = (ms / 1000).coerceAtLeast(0)
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return if (h > 0) "%02d:%02d:%02d".format(h, m, sec)
        else "%02d:%02d".format(m, sec)
    }

    private fun formatProfit(value: Long): String {
        val abs = Math.abs(value)
        val sign = if (value < 0) "-" else "+"
        return when {
            abs >= 1_000_000 -> "$sign${"%.1f".format(abs / 1_000_000.0)}M"
            abs >= 1_000     -> "$sign${"%.1f".format(abs / 1_000.0)}K"
            else             -> "$sign$abs"
        }
    }
}
```

- [ ] **Step 2: Build check** â†’ `./gradlew build 2>&1 | tail -20`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/GardenHud.kt
git commit -m "feat: add GardenHud NVG renderer"
```

---

## Task 11: GardenMacroModule

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/garden/GardenMacroModule.kt`

This is the central module. It owns all settings, the state machine, HUD, and routes events to managers.

- [ ] **Step 1: Create GardenMacroModule.kt**

```kotlin
package org.phantom.internal.garden

import net.minecraft.client.Minecraft
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.module.setting.inGroup
import org.phantom.internal.garden.managers.*

object GardenMacroModule : Module("Garden Macro") {

    private val mc = Minecraft.getInstance()

    // â”€â”€ State â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Volatile var state = GardenState.OFF
        private set
    @Volatile var sessionStartTime = System.currentTimeMillis()
    @Volatile var autosellingManager: String? = null
    @Volatile private var wasConnected = true

    fun setState(newState: GardenState) {
        mc.execute { state = newState }
    }

    // â”€â”€ Settings â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    // General
    private val enabledSetting = CheckboxSetting("Enabled", "Run the garden macro.", false)

    // Scripts
    private val scriptInfo = InfoSetting("Scripts", "External script command names (no leading slash).", InfoType.INFO)
    private val farmScriptSetting    = TextSetting("Farm Script",    "Script name for farming.",       "farm")
    private val pestScriptSetting    = TextSetting("Pest Script",    "Script name for pest cleaning.", "pest")
    private val returnScriptSetting  = TextSetting("Return Script",  "Script name after pest clean.",  "return")
    private val visitorScriptSetting = TextSetting("Visitor Script", "Script name for visitors.",      "visitor")

    // Pest
    private val pestInfo         = InfoSetting("Pest", "", InfoType.INFO)
    private val pestThresholdSetting = SliderSetting("Pest Threshold", "Alive count to trigger cleaning.", 4.0, 1.0, 8.0, step = 1.0)
    private val aotvEnabledSetting   = CheckboxSetting("AOTV to Roof",  "Teleport to roof before pest clean.", false)
    private val aotvPlotsSetting     = TextSetting("AOTV Plots",     "Comma-separated plot names.",       "")
    private val prepSwapSetting      = CheckboxSetting("Prep Swap",     "Swap gear before threshold hit.",   false)
    private val roofPitchSetting     = SliderSetting("Roof Pitch",     "Camera pitch for roof teleport.",   -80.0, -90.0, 90.0)

    // Visitor
    private val visitorInfo        = InfoSetting("Visitor", "", InfoType.INFO)
    private val autoVisitorSetting = CheckboxSetting("Auto Visitor", "Handle visitor offers automatically.", false)

    // Wardrobe
    private val wardrobeInfo          = InfoSetting("Wardrobe", "", InfoType.INFO)
    private val autoWardrobeSetting   = CheckboxSetting("Auto Wardrobe",   "Swap wardrobe slots automatically.",      false)
    private val farmingSlotSetting    = SliderSetting("Farming Slot",   "Wardrobe slot for farming.",          1.0, 1.0, 18.0, step = 1.0)
    private val pestSlotSetting       = SliderSetting("Pest Slot",      "Wardrobe slot for pest cleaning.",    2.0, 1.0, 18.0, step = 1.0)
    private val visitorSlotSetting    = SliderSetting("Visitor Slot",   "Wardrobe slot for visitors.",         3.0, 1.0, 18.0, step = 1.0)

    // Equipment
    private val equipmentInfo     = InfoSetting("Equipment", "", InfoType.INFO)
    private val farmingArmorSetting = TextSetting("Farming Armor", "Armor set name for farming.",   "")
    private val pestArmorSetting    = TextSetting("Pest Armor",    "Armor set name for pest.",      "")
    private val visitorArmorSetting = TextSetting("Visitor Armor", "Armor set name for visitors.",  "")
    private val swapDelaySetting    = SliderSetting("Swap Delay", "Ms between equipment swaps.", 300.0, 0.0, 2000.0)

    // Economy
    private val economyInfo          = InfoSetting("Economy", "", InfoType.INFO)
    private val bazaarRefreshSetting = SliderSetting("Bazaar Refresh", "Seconds between Bazaar updates.", 120.0, 30.0, 600.0)
    private val georgeThreshSetting  = SliderSetting("George Threshold", "Profit threshold to sell pets.", 100000.0, 0.0, 10000000.0)
    private val georgeRaritySetting  = TextSetting("George Rarity", "Rarities to sell (LEGENDARY,MYTHIC).", "LEGENDARY")
    private val bookLevelSetting     = SliderSetting("Book Level", "Enchant level to combine books at.", 5.0, 1.0, 10.0, step = 1.0)
    private val junkItemsSetting     = TextSetting("Junk Items", "Comma-separated item names to drop.", "")
    private val cookieItemSetting    = TextSetting("Cookie Item", "Item name to use booster cookie on.", "")

    // Rest
    private val restInfo           = InfoSetting("Rest", "", InfoType.INFO)
    private val workDurationSetting  = SliderSetting("Work Duration",  "Minutes to farm before resting.",   60.0, 1.0, 240.0)
    private val workOffsetSetting    = SliderSetting("Work Offset",    "Random offset for work duration.",   5.0, 0.0, 30.0)
    private val breakDurationSetting = SliderSetting("Break Duration", "Minutes to rest before resuming.",  10.0, 1.0, 60.0)
    private val breakOffsetSetting   = SliderSetting("Break Offset",   "Random offset for break duration.",  2.0, 0.0, 15.0)

    // Advanced
    private val advancedInfo          = InfoSetting("Advanced", "", InfoType.INFO)
    private val hideFilteredChatSetting = CheckboxSetting("Hide Chat Spam", "Filter bot-related chat messages.", false)
    private val maxRecoverySetting      = SliderSetting("Max Recovery", "Max auto-reconnect attempts.",     15.0, 1.0, 30.0, step = 1.0)
    private val reconnectMinSetting     = SliderSetting("Reconnect Min", "Min seconds before reconnecting.", 30.0, 5.0, 120.0)
    private val reconnectMaxSetting     = SliderSetting("Reconnect Max", "Max seconds before reconnecting.", 60.0, 5.0, 120.0)

    // â”€â”€ Convenience accessors â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    val farmScript        get() = farmScriptSetting.value
    val pestScript        get() = pestScriptSetting.value
    val returnScript      get() = returnScriptSetting.value
    val visitorScript     get() = visitorScriptSetting.value
    val pestThreshold     get() = pestThresholdSetting.value.toInt()
    val aotvEnabled       get() = aotvEnabledSetting.value
    val roofPitch         get() = roofPitchSetting.value
    val autoWardrobeEnabled get() = autoWardrobeSetting.value
    val farmingWardrobeSlot get() = farmingSlotSetting.value
    val pestWardrobeSlot  get() = pestSlotSetting.value
    val visitorWardrobeSlot get() = visitorSlotSetting.value
    val farmingArmor      get() = farmingArmorSetting.value
    val pestArmor         get() = pestArmorSetting.value
    val visitorArmor      get() = visitorArmorSetting.value
    val swapDelayMs       get() = swapDelaySetting.value
    val bazaarRefreshSecs get() = bazaarRefreshSetting.value
    val georgeThreshold   get() = georgeThreshSetting.value
    val georgeRarity      get() = georgeRaritySetting.value
    val bookCombineLevel  get() = bookLevelSetting.value
    val junkItems         get() = junkItemsSetting.value
    val cookieItem        get() = cookieItemSetting.value
    val workDurationMins  get() = workDurationSetting.value.toLong()
    val workOffsetMins    get() = workOffsetSetting.value.toLong()
    val breakDurationMins get() = breakDurationSetting.value.toLong()
    val breakOffsetMins   get() = breakOffsetSetting.value.toLong()
    val maxRecoveryAttempts get() = maxRecoverySetting.value
    val reconnectDelayMin get() = reconnectMinSetting.value
    val reconnectDelayMax get() = reconnectMaxSetting.value

    // â”€â”€ HUD â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private val hudWidth  = 200f
    private val hudHeight = 155f

    private val gardenHudElement = hudElement("garden-macro-hud", "Garden Macro HUD", "Macro status and profit tracking") {
        anchor  = HudAnchor.TOP_LEFT
        offsetX = 10f
        offsetY = 10f

        // Declare settings BEFORE height/render so lambdas can capture them
        val showProfitSetting = setting(CheckboxSetting("Show Profit", "Show profit section.", true))
        val showRestSetting   = setting(CheckboxSetting("Show Rest",   "Show rest countdown.", true))

        fun computeHeight(): Float {
            var h = 48f
            if (showRestSetting.value) h += 36f
            if (showProfitSetting.value) h += 62f
            return h
        }

        width  { hudWidth }
        height { computeHeight() }

        render { x, y, _ ->
            if (!enabledSetting.value && state == GardenState.OFF) return@render
            GardenHud.render(x, y, hudWidth, computeHeight(), state, showProfitSetting.value, showRestSetting.value)
        }
    }

    // â”€â”€ init â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    init {
        // Register settings with groups
        listOf(enabledSetting).forEach { it.inGroup("General") }
        listOf(scriptInfo, farmScriptSetting, pestScriptSetting, returnScriptSetting, visitorScriptSetting).forEach { it.inGroup("Scripts") }
        listOf(pestInfo, pestThresholdSetting, aotvEnabledSetting, aotvPlotsSetting, prepSwapSetting, roofPitchSetting).forEach { it.inGroup("Pest") }
        listOf(visitorInfo, autoVisitorSetting).forEach { it.inGroup("Visitor") }
        listOf(wardrobeInfo, autoWardrobeSetting, farmingSlotSetting, pestSlotSetting, visitorSlotSetting).forEach { it.inGroup("Wardrobe") }
        listOf(equipmentInfo, farmingArmorSetting, pestArmorSetting, visitorArmorSetting, swapDelaySetting).forEach { it.inGroup("Equipment") }
        listOf(economyInfo, bazaarRefreshSetting, georgeThreshSetting, georgeRaritySetting, bookLevelSetting, junkItemsSetting, cookieItemSetting).forEach { it.inGroup("Economy") }
        listOf(restInfo, workDurationSetting, workOffsetSetting, breakDurationSetting, breakOffsetSetting).forEach { it.inGroup("Rest") }
        listOf(advancedInfo, hideFilteredChatSetting, maxRecoverySetting, reconnectMinSetting, reconnectMaxSetting).forEach { it.inGroup("Advanced") }

        addSetting(
            enabledSetting,
            scriptInfo, farmScriptSetting, pestScriptSetting, returnScriptSetting, visitorScriptSetting,
            pestInfo, pestThresholdSetting, aotvEnabledSetting, aotvPlotsSetting, prepSwapSetting, roofPitchSetting,
            visitorInfo, autoVisitorSetting,
            wardrobeInfo, autoWardrobeSetting, farmingSlotSetting, pestSlotSetting, visitorSlotSetting,
            equipmentInfo, farmingArmorSetting, pestArmorSetting, visitorArmorSetting, swapDelaySetting,
            economyInfo, bazaarRefreshSetting, georgeThreshSetting, georgeRaritySetting, bookLevelSetting, junkItemsSetting, cookieItemSetting,
            restInfo, workDurationSetting, workOffsetSetting, breakDurationSetting, breakOffsetSetting,
            advancedInfo, hideFilteredChatSetting, maxRecoverySetting, reconnectMinSetting, reconnectMaxSetting
        )

        EventBus.register(this)
    }

    // â”€â”€ Events â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @SubscribeEvent
    fun onTick(event: TickEvent.End) {
        val enabled = enabledSetting.value
        val wasEnabled = state != GardenState.OFF

        // Start/stop on enabled toggle
        if (enabled && state == GardenState.OFF) {
            startMacro()
            return
        }
        if (!enabled && state != GardenState.OFF) {
            stopMacro()
            return
        }
        if (!enabled) return

        // Disconnect detection
        val connected = mc.connection != null
        if (!connected && wasConnected && state != GardenState.RECOVERING && state != GardenState.RESTING) {
            setState(GardenState.RECOVERING)
            RecoveryManager.onDisconnect()
        }
        wasConnected = connected

        if (!connected) return

        // Route to managers
        CropFeverManager.update()
        PestBonusManager.update()
        RestartManager.update()
        ProfitManager.refreshBazaarIfNeeded()
        PetXpTracker.update()

        when (state) {
            GardenState.FARMING -> tickFarming()
            GardenState.RESTING -> { /* DynamicRestManager handles reconnect timer */ }
            else -> { /* Other states managed by their worker tasks */ }
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val msg = event.message?.replace(Regex("Â§[0-9a-fk-or]"), "") ?: return

        // Chat spam filter
        if (hideFilteredChatSetting.value) {
            val lower = msg.lowercase()
            if (lower.contains("pet killed") || lower.contains("macro started") || lower.contains("script started")) {
                event.cancel()
                return
            }
        }

        CropFeverManager.onChatMessage(msg)
        RestartManager.onChatMessage(msg)
        ProfitManager.onChatMessage(msg)
        VisitorManager.onChatMessage(msg)
    }

    private fun tickFarming() {
        // Rest check
        if (DynamicRestManager.update()) {
            setState(GardenState.RESTING)
            DynamicRestManager.startRest()
            return
        }

        // Visitor check
        if (autoVisitorSetting.value && VisitorManager.update() && !CropFeverManager.shouldDelay()) {
            setState(GardenState.VISITING)
            VisitorManager.startVisitorSequence()
            return
        }

        // AUTOSELLING â€” priority: George > BookCombine > Junk
        if (autosellingManager == null) {
            if (GeorgeManager.shouldSell()) {
                autosellingManager = "george"
                setState(GardenState.AUTOSELLING)
                GeorgeManager.startSell()
                return
            }
            if (BookCombineManager.shouldCombine()) {
                autosellingManager = "book"
                setState(GardenState.AUTOSELLING)
                BookCombineManager.startCombine()
                return
            }
            if (JunkManager.shouldDrop()) {
                autosellingManager = "junk"
                setState(GardenState.AUTOSELLING)
                JunkManager.startDrop()
                return
            }
        }

        // Pest prep-swap check
        if (prepSwapSetting.value && !PestPrepSwapManager.swapDone) {
            val count = PestManager.lastAliveCount
            if (PestPrepSwapManager.shouldPrepSwap(count, pestThreshold)) {
                PestPrepSwapManager.markDone()
                GardenWorkerThread.submit("prep-swap") { GearManager.swapForPest() }
            }
        }

        // Pest cleaning trigger
        if (!CropFeverManager.shouldDelay() && PestManager.update(pestThreshold)) {
            setState(GardenState.CLEANING)
            PestCleaningSequencer.startSequence()
        }
    }

    private fun startMacro() {
        sessionStartTime = System.currentTimeMillis()
        state = GardenState.FARMING
        // Reset all managers
        listOf<() -> Unit>(
            PestManager::reset, PestCleaningSequencer::reset, PestAotvManager::reset,
            PestPrepSwapManager::reset, PestReturnManager::reset, PestBonusManager::reset,
            CropFeverManager::reset, VisitorManager::reset, WardrobeManager::reset,
            EquipmentManager::reset, GearManager::reset, RodManager::reset,
            GeorgeManager::reset, BookCombineManager::reset, JunkManager::reset,
            BoosterCookieManager::reset, ProfitManager::reset, PetXpTracker::reset,
            DynamicRestManager::reset, RecoveryManager::reset, RestartManager::reset,
        ).forEach { it() }
        autosellingManager = null
        DynamicRestManager.reset()
        ScriptBridge.startFarming(farmScript)
    }

    private fun stopMacro() {
        GardenWorkerThread.shutdown()
        ScriptBridge.stopScript()
        state = GardenState.OFF
    }
}
```

- [ ] **Step 2: Build check** â†’ `./gradlew build 2>&1 | tail -20`

Fix any compilation errors before proceeding.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/garden/GardenMacroModule.kt
git commit -m "feat: add GardenMacroModule â€” central module with state machine, settings, HUD"
```

---

## Task 12: Register in Phantom.kt

**Files:**
- Modify: `src/main/kotlin/org/phantom/Phantom.kt`

- [ ] **Step 1: Add import and register**

Add import:
```kotlin
import org.phantom.internal.garden.GardenMacroModule
```

Add to `ModuleManager.addModules(listOf(...))`:
```kotlin
GardenMacroModule,
```

- [ ] **Step 2: Final build check**

```bash
./gradlew build 2>&1 | tail -30
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/Phantom.kt
git commit -m "feat: register GardenMacroModule in Phantom"
```

---

## Known Caveats

- **Wardrobe/Equipment GUI click logic** may need tuning to match the actual Hypixel slot indices â€” test in-game and adjust slot offsets.
- **PestTabListParser regex** may need updating once actual Hypixel tab list format is observed â€” the patterns are reasonable defaults.
- **RecoveryManager reconnect** uses `ConnectScreen` API â€” verify the constructor signature against MC 1.21.11 and adjust if needed.
- **AotvManager right-click** assumes AOTV is in the hotbar â€” extend to check offhand if needed.
- **SliderSetting.value** returns `Double` â€” use `.toInt()`, `.toFloat()`, `.toLong()` as needed throughout.
