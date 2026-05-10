# Commission + Tunnel + Gemstone Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port rdbt v5 commission-macro guard patterns into `CommissionMacroModule`, add multi-ore support to `TunnelMinerModule`, and redesign `GemstoneMinerModule` from scanner-based to a route-following etherwarp loop.

**Architecture:** CommissionMacroModule receives targeted field additions and method rewrites that preserve all existing infrastructure (routes, watchdog, maintenance controller). TunnelMinerModule gets a minimal generalization of `oreOverride` → `oreOverrides: Set`. GemstoneMinerModule replaces its `GemstoneScanner` scanning with a cyclic route loop loaded from `GemstoneRouteStoreBridge`; the existing warp state machine (WARP_TARGETING → WARP_ALIGNING → WARP_USING → WARP_VERIFYING) is preserved and reused.

**Tech Stack:** Kotlin, Fabric MC 1.21.11, Cobalt module/event/pathfinding APIs, existing `RouteStore`, `MiningLoopController`, `StrictMiningTargetFilter`, `StyleEtherwarpTargeting`

---

## Files

| File | Action |
|---|---|
| `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` | Modify — add 5 fields, rewrite 4 methods, add 4 methods |
| `src/main/kotlin/org/cobalt/internal/mining/tunnels/TunnelMinerModule.kt` | Modify — `oreOverride` → `oreOverrides: Set`, update `startForAutomation` + `selectedOres` |
| `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneTypes.kt` | Modify — add `pointType: RoutePointType` to `GemstoneRoutePoint` |
| `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneRouteStoreBridge.kt` | Rewrite — implement `loadActiveGemstoneRoute()` mirroring `TunnelRouteStoreBridge` |
| `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneMinerModule.kt` | Modify — replace scanner target with route loop; update settings, fields, all target-using methods |

---

## Task 1: CommissionMacroModule — Add 5 new state fields

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt:123-148`

- [ ] **Step 1: Add the 5 new fields after the existing state block**

Find the block that ends with `private var emissariesUnlocked = true` (line ~148) and insert after `private var awaitingTabUpdate = false` (line ~143):

```kotlin
  private var lastCompletedCommissionName: String? = null
  private var ignoreTabUpdatesUntil = 0L
  private var lastCommissionSyncSource: String? = null
  private var firstPigeonAttemptAt = 0L
  private var pigeonAttempts = 0
```

- [ ] **Step 2: Clear all 5 new fields in `resetState()`**

In `resetState()` (line ~980), find the block that clears `awaitingTabUpdate = false` and add after it:

```kotlin
    lastCompletedCommissionName = null
    ignoreTabUpdatesUntil = 0L
    lastCommissionSyncSource = null
    firstPigeonAttemptAt = 0L
    pigeonAttempts = 0
```

- [ ] **Step 3: Clear all 5 new fields in `resetCommissionAfterClaim()`**

In `resetCommissionAfterClaim()` (line ~1018), add after `awaitingTabUpdate = false`:

```kotlin
    lastCompletedCommissionName = null
    ignoreTabUpdatesUntil = 0L
    lastCommissionSyncSource = null
    firstPigeonAttemptAt = 0L
    pigeonAttempts = 0
```

- [ ] **Step 4: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL (existing warnings are fine)

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): add v5 state fields for stale-guard and pigeon cap"
```

---

## Task 2: CommissionMacroModule — Rewrite `updateCommissionsIfChanged()`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — `updateCommissionsIfChanged()`

- [ ] **Step 1: Replace the body of `updateCommissionsIfChanged()` (lines ~1230–1241)**

Replace the current method entirely with:

```kotlin
  private fun updateCommissionsIfChanged(newCommissions: List<TabCommission>) {
    if (commissions == newCommissions) return

    val now = System.currentTimeMillis()

    if (ignoreTabUpdatesUntil > now && lastCommissionSyncSource == "GUI") return

    if (ignoreTabUpdatesUntil > now && lastCompletedCommissionName != null) {
      val stillStale = newCommissions.any {
        it.name == lastCompletedCommissionName && it.progress >= 1.0
      }
      if (stillStale) return
      ignoreTabUpdatesUntil = 0L
    } else if (ignoreTabUpdatesUntil <= now) {
      ignoreTabUpdatesUntil = 0L
    }

    if (newCommissions.isNotEmpty()) detectorAttempts = 0

    commissions = newCommissions
    lastCommissionSyncSource = "TAB"

    if (awaitingTabUpdate) {
      val completedName = lastCompletedCommissionName
      if (completedName != null) {
        val entry = newCommissions.firstOrNull { it.name == completedName }
        if (entry == null || entry.progress < 1.0) awaitingTabUpdate = false
      } else {
        val stillCompleted = newCommissions.any { it.progress >= 1.0 && CommissionData.resolveTask(it.name) != null }
        if (!stillCompleted) awaitingTabUpdate = false
      }
    }
  }
```

- [ ] **Step 2: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): v5 updateCommissionsIfChanged with stale tab-update guard"
```

---

## Task 3: CommissionMacroModule — Add `shouldWaitForLastCompleted()`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt`

- [ ] **Step 1: Add the new private method anywhere in the private helpers section (e.g. before `handleChoosing()`)**

```kotlin
  private fun shouldWaitForLastCompleted(): Boolean {
    val name = lastCompletedCommissionName ?: return false
    val entry = commissions.firstOrNull { it.name == name }
    if (entry != null && entry.progress >= 1.0) return true
    lastCompletedCommissionName = null
    return false
  }
```

- [ ] **Step 2: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): add shouldWaitForLastCompleted() guard helper"
```

---

## Task 4: CommissionMacroModule — Update `onCommissionComplete()` + fix `handleChoosing()` guard order

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — `onCommissionComplete()`, `handleChoosing()`

- [ ] **Step 1: Update `onCommissionComplete()` to set `lastCompletedCommissionName` and reset pigeon counters**

In `onCommissionComplete()` (line ~938), after the existing `lastCommissionName = currentCommission?.name` line, add:

```kotlin
    lastCompletedCommissionName = currentCommission?.name
    firstPigeonAttemptAt = 0L
    pigeonAttempts = 0
```

- [ ] **Step 2: Rewrite the guard block at the top of `handleChoosing()` (lines ~391–441)**

Replace from the top of `handleChoosing()` up through and including the `if (completed != null && !awaitingTabUpdate)` block with the v5 guard order:

```kotlin
  private fun handleChoosing() {
    CommissionMacroWatchdog.resetRuntimeOnly()

    val screen = mc.screen as? AbstractContainerScreen<*>

    if (screen != null) {
      val parsed = parseCommissionSelectionFromGui(screen)

      if (parsed.commissions.isNotEmpty()) {
        updateCommissionsIfChanged(parsed.commissions)
        mc.player?.closeContainer()
        detectorAttempts = 0
        setStatus("Detected commissions from GUI.")
        delay(2)
        return
      }

      detectorAttempts++

      if (detectorAttempts >= 30) {
        mc.player?.closeContainer()
        detectorAttempts = 0
        setStatus("No commissions detected.")
        return
      }

      setStatus("Reading commission GUI...")
      return
    }

    val area = detectAreaFromTabList()
    val now = System.currentTimeMillis()

    if (area != null && !area.contains("Dwarven", ignoreCase = true) && !area.contains("Forge", ignoreCase = true)) {
      if (now - lastAreaWarpAt > 10_000L) {
        sendCommand("warpforge")
        lastAreaWarpAt = now
        setStatus("Not in Dwarven Mines, warping...")
      }
      return
    }

    // v5 guard order
    if (shouldWaitForLastCompleted()) return
    if (awaitingTabUpdate) return

    val completed = findCompletedCommission()

    if (completed != null) {
      // Double-claim guard: if same commission is still marked completed and tab update lock is active, wait
      if (completed.name == lastCompletedCommissionName && ignoreTabUpdatesUntil > now) {
        awaitingTabUpdate = true
        return
      }
      currentCommission = completed
      onCommissionComplete()
      return
    }

    val active = getActiveCommissions()

    if (active.isEmpty()) {
      if (tryOpenOldCommissionDetector()) return
      setStatus("No commissions detected.")
      return
    }

    val supportedTasks = getSupportedTasks(active)

    if (supportedTasks.isEmpty()) {
      ChatUtils.sendMessage("Commission Macro: no supported commissions available.")
      enabled.value = false
      return
    }

    val avoidEntities = getAvoidanceEntities()
    val chosen = findAvailableCommission(supportedTasks, avoidEntities)

    if (chosen == null) {
      ChatUtils.sendMessage("Commission Macro: no available spots, finding new lobby.")
      sendCommand("hub")
      delayedReset(80)
      return
    }

    startCommission(chosen.first, chosen.second)
  }
```

- [ ] **Step 3: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): v5 handleChoosing guard order + double-claim guard + onCommissionComplete fields"
```

---

## Task 5: CommissionMacroModule — Add `updateCommissionsFromGui()` + call it before closing claim GUI

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — add method, update `handleClaiming()`

- [ ] **Step 1: Add `updateCommissionsFromGui()` as a private method**

```kotlin
  private fun updateCommissionsFromGui(screen: AbstractContainerScreen<*>) {
    val parsed = parseCommissionSelectionFromGui(screen)
    if (parsed.commissions.isEmpty()) return
    commissions = parsed.commissions
    awaitingTabUpdate = false
    lastCommissionSyncSource = "GUI"
    ignoreTabUpdatesUntil = System.currentTimeMillis() + 5_000L
    val currentName = currentCommission?.name
    val matching = if (currentName != null) commissions.firstOrNull { it.name == currentName } else null
    if (matching == null || matching.progress >= 1.0) currentCommission = null
  }
```

- [ ] **Step 2: Call `updateCommissionsFromGui(screen)` in `handleClaiming()` just before `mc.player?.closeContainer()` in the "no claim slot found" branch**

Find `handleClaiming()` (line ~648). Inside the `if (screen != null)` block, the branch that runs when `claimSlot < 0` currently reads:

```kotlin
      mc.player?.closeContainer()
      resetCommissionAfterClaim()
      setState(State.WAITING_GUI_CLOSE)
      return
```

Replace with:

```kotlin
      updateCommissionsFromGui(screen)
      mc.player?.closeContainer()
      resetCommissionAfterClaim()
      setState(State.WAITING_GUI_CLOSE)
      return
```

- [ ] **Step 3: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): add updateCommissionsFromGui() with 5s tab-lock, call before closing claim GUI"
```

---

## Task 6: CommissionMacroModule — Add `ensureDrillEquippedForClaim()` + pigeon attempt cap in `handleClaiming()`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — add method, rewrite pigeon and emissary sections of `handleClaiming()`

- [ ] **Step 1: Add `ensureDrillEquippedForClaim()` as a private method**

```kotlin
  private fun ensureDrillEquippedForClaim(): Boolean {
    val player = mc.player ?: return true
    val toolSlot = findMiningToolSlot(player)
    if (toolSlot < 0) return true
    if (player.inventory.selectedSlot != toolSlot) {
      InventoryUtils.holdHotbarSlot(toolSlot)
      delay(3)
      return false
    }
    return true
  }
```

- [ ] **Step 2: Rewrite the non-GUI part of `handleClaiming()` (the pigeon + emissary section, lines ~680–718)**

Replace from `val player = mc.player ?: return` through the end of `handleClaiming()` with:

```kotlin
    val player = mc.player ?: return
    val pigeonSlot = InventoryUtils.findItemInHotbar("Royal Pigeon")
    val now = System.currentTimeMillis()

    if (pigeonSlot >= 0) {
      val pigeonTimedOut = firstPigeonAttemptAt > 0L && (now - firstPigeonAttemptAt) > 4_000L
      if (pigeonAttempts < 3 && !pigeonTimedOut) {
        if (player.inventory.selectedSlot != pigeonSlot) {
          InventoryUtils.holdHotbarSlot(pigeonSlot)
          delay(3)
        } else {
          if (firstPigeonAttemptAt == 0L) firstPigeonAttemptAt = now
          pigeonAttempts++
          rightClick()
          delay(10)
        }
        setStatus("Opening Royal Pigeon...")
        return
      }
      // Fall through to emissary if attempts exhausted or timed out
    }

    val target = getClosestEmissaryLocation(player)

    if (!hasArrivedAt(target, 4.0)) {
      travelPurpose = null
      startUtilityPath(target, 2.5)
      setStatus("Traveling to emissary...")
      return
    }

    val emissary = findNearestEmissary(player)

    if (emissary == null && emissariesUnlocked) {
      emissariesUnlocked = false
      ChatUtils.sendMessage("Commission Macro: emissary not found, reverting to King.")
      startUtilityPath(CommissionData.emissaryLocations.first(), 2.5)
      return
    }

    if (!ensureDrillEquippedForClaim()) return

    if (emissary != null) faceEntity(emissary) else faceBlock(target)

    rightClick()
    delay(10)
    setStatus("Opening emissary...")
  }
```

- [ ] **Step 3: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): pigeon attempt cap (3/4s), ensureDrillEquippedForClaim() before emissary"
```

---

## Task 7: CommissionMacroModule — Extend `getAvoidanceEntities()` for Crystal/Star Sentries

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — `getAvoidanceEntities()`

- [ ] **Step 1: Replace `getAvoidanceEntities()` (lines ~1123–1130) with Sentry-aware version**

```kotlin
  private fun getAvoidanceEntities(): List<Entity> {
    val level = mc.level ?: return emptyList()
    val player = mc.player ?: return emptyList()
    val radius = avoidanceRadius.value
    if (radius <= 0.0) return emptyList()

    val players = level.players().filter { it.uuid != player.uuid }
    val sentries = level.getEntities(player, player.boundingBox.inflate(radius + 32.0))
      .filter { it.name.string.contains("Sentry", ignoreCase = true) }
    return players + sentries
  }
```

- [ ] **Step 2: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat(commission): include Crystal/Star Sentry entities in avoidance check"
```

---

## Task 8: TunnelMinerModule — Multi-ore: `oreOverride` → `oreOverrides: Set`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/tunnels/TunnelMinerModule.kt`

- [ ] **Step 1: Change the `oreOverride` field declaration (line ~210)**

Replace:
```kotlin
  private var oreOverride: TunnelOreType? = null
```
With:
```kotlin
  private var oreOverrides: Set<TunnelOreType> = emptySet()
```

- [ ] **Step 2: Replace `startForAutomation(ore: TunnelOreType, ...)` with a set-accepting version plus a single-ore overload**

Replace the existing `startForAutomation` method (lines ~249–268):

```kotlin
  fun startForAutomation(
    ores: Set<TunnelOreType>,
    source: String = "commission"
  ): Boolean {
    automationMode = true
    automationSource = source
    oreOverrides = ores
    lastOre = ores.firstOrNull()
    lastFailure = "none"
    failedTargets = 0
    miningTicks = 0
    scanCooldown = 0
    currentTarget = null
    lastEtherwarpTarget = null
    currentState = State.SCANNING
    isActive = true
    enabled.value = true
    status.value = "Automation: ${ores.joinToString("+") { it.displayName }}"
    return true
  }

  fun startForAutomation(
    ore: TunnelOreType,
    source: String = "commission"
  ): Boolean = startForAutomation(setOf(ore), source)
```

- [ ] **Step 3: Update `selectedOres()` to use `oreOverrides` (line ~712)**

Replace:
```kotlin
  private fun selectedOres(): Set<TunnelOreType> {
    val override = oreOverride

    if (override != null) {
      return setOf(override)
    }

    return setOf(
      when (oreType.value) {
        1 -> TunnelOreType.TUNGSTEN
        2 -> TunnelOreType.UMBER
        3 -> TunnelOreType.PERIDOT
        4 -> TunnelOreType.AQUAMARINE
        5 -> TunnelOreType.ONYX
        6 -> TunnelOreType.CITRINE
        else -> TunnelOreType.GLACITE
      }
    )
  }
```
With:
```kotlin
  private fun selectedOres(): Set<TunnelOreType> {
    val overrides = oreOverrides
    if (overrides.isNotEmpty()) return overrides
    return setOf(
      when (oreType.value) {
        1 -> TunnelOreType.TUNGSTEN
        2 -> TunnelOreType.UMBER
        3 -> TunnelOreType.PERIDOT
        4 -> TunnelOreType.AQUAMARINE
        5 -> TunnelOreType.ONYX
        6 -> TunnelOreType.CITRINE
        else -> TunnelOreType.GLACITE
      }
    )
  }
```

- [ ] **Step 4: Update `stopAll()` to clear `oreOverrides` instead of `oreOverride` (line ~789)**

Replace:
```kotlin
    oreOverride = null
```
With:
```kotlin
    oreOverrides = emptySet()
```

- [ ] **Step 5: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/tunnels/TunnelMinerModule.kt
git commit -m "feat(tunnel): multi-ore support — oreOverride → oreOverrides: Set with single-ore overload"
```

---

## Task 9: GemstoneTypes — Add `pointType` field to `GemstoneRoutePoint`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneTypes.kt`

- [ ] **Step 1: Add the `RoutePointType` import and `pointType` field**

Add import after `import net.minecraft.core.BlockPos`:
```kotlin
import org.cobalt.internal.routes.RoutePointType
```

Replace `GemstoneRoutePoint` data class:
```kotlin
data class GemstoneRoutePoint(
  val name: String = "point",
  val standPos: BlockPos,
  val lookPos: BlockPos,
  val gemstoneTypes: Set<GemstoneType> = GemstoneType.entries.toSet(),
  val enabled: Boolean = true,
  val pointType: RoutePointType = RoutePointType.MINE
)
```

- [ ] **Step 2: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneTypes.kt
git commit -m "feat(gemstone): add pointType field to GemstoneRoutePoint"
```

---

## Task 10: GemstoneRouteStoreBridge — Implement `loadActiveGemstoneRoute()`

**Files:**
- Rewrite: `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneRouteStoreBridge.kt`

- [ ] **Step 1: Replace the stub file entirely**

```kotlin
package org.cobalt.internal.mining.gemstone

import net.minecraft.core.BlockPos
import org.cobalt.internal.routes.RoutePoint
import org.cobalt.internal.routes.RoutePointType
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType

/**
 * Loads the active RouteType.GEMSTONE route from RouteStore and converts it to
 * the compact format consumed by GemstoneMinerModule.
 *
 * Route priority:
 * 1. routeName parameter (from GemstoneMinerModule's Route Name setting)
 * 2. /pathroute gemstone <name> assignment (slot key: gemstone-route)
 * 3. RouteStore loaded route for RouteType.GEMSTONE
 * 4. First saved RouteType.GEMSTONE route on disk
 */
object GemstoneRouteStoreBridge {

  private const val GEMSTONE_SLOT_KEY = "gemstone-route"

  fun loadActiveGemstoneRoute(
    fallbackTypes: Set<GemstoneType> = GemstoneType.entries.toSet(),
    routeName: String? = null
  ): LoadedGemstoneRoute? {
    val routes = RouteStore.listByType(RouteType.GEMSTONE)
    if (routes.isEmpty()) return null

    val requestedName = routeName?.takeIf { it.isNotEmpty() }
      ?: RouteStore.getSlotRoute(GEMSTONE_SLOT_KEY)
      ?: RouteStore.getLoadedName(RouteType.GEMSTONE)

    val route = when {
      !requestedName.isNullOrBlank() ->
        routes.firstOrNull { it.name.equals(requestedName, ignoreCase = true) }
      else -> null
    } ?: RouteStore.getLoaded(RouteType.GEMSTONE)
      ?: routes.firstOrNull()
      ?: return null

    val points = route.points.mapIndexed { index, point ->
      point.toGemstonePoint(index, fallbackTypes)
    }

    return if (points.isEmpty()) null else LoadedGemstoneRoute(route.name, points)
  }

  fun routeNames(): List<String> = RouteStore.listByType(RouteType.GEMSTONE).map { it.name }

  private fun RoutePoint.toGemstonePoint(
    index: Int,
    fallbackTypes: Set<GemstoneType>
  ): GemstoneRoutePoint {
    return GemstoneRoutePoint(
      name = "${type.id}#$index",
      standPos = pos,
      lookPos = mineEnd ?: pos,
      gemstoneTypes = typesFor(blockId, fallbackTypes),
      enabled = true,
      pointType = type
    )
  }

  private fun typesFor(
    blockId: String?,
    fallbackTypes: Set<GemstoneType>
  ): Set<GemstoneType> {
    val text = blockId.orEmpty().lowercase()
    val fromBlockId = GemstoneType.entries.filter { type ->
      type.keywords.any { keyword -> text.contains(keyword.lowercase()) }
    }.toSet()
    if (fromBlockId.isNotEmpty()) return fromBlockId
    if (fallbackTypes.isNotEmpty()) return fallbackTypes
    return GemstoneType.entries.toSet()
  }
}
```

- [ ] **Step 2: Build to verify compile**

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneRouteStoreBridge.kt
git commit -m "feat(gemstone): implement GemstoneRouteStoreBridge mirroring TunnelRouteStoreBridge"
```

---

## Task 11: GemstoneMinerModule — Route-following loop redesign

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneMinerModule.kt`

This task rewires the module from scanner-based to route-indexed. The warp state machine (WARP_TARGETING → WARP_ALIGNING → WARP_USING → WARP_VERIFYING) is preserved; only the "scan / decide" and "mark complete" steps change.

### Step 1 — Update fields

- [ ] Replace `private var gemstoneOverride: GemstoneType? = null` with:

```kotlin
  private var gemstoneOverride: GemstoneType? = null
  private var route: List<GemstoneRoutePoint> = emptyList()
  private var routeIndex = 0
  private var loadedRouteName: String? = null
```

- [ ] Change `var currentTarget: GemstoneScanner.GemstoneTarget?` to:

```kotlin
  var currentTarget: GemstoneRoutePoint? = null
    private set
```

### Step 2 — Remove settings `dynamicScan`, `routePoints`, `orderedRoutes`; add `routeName`

- [ ] Remove the three `CheckboxSetting` field declarations for `dynamicScan`, `routePoints`, `orderedRoutes`.

- [ ] Add a `routeName` setting after the `loopRoute` setting:

```kotlin
  private val routeName = TextSetting(
    "Route Name",
    "Optional exact RouteType.GEMSTONE route name. Leave blank for auto.",
    ""
  )
```

- [ ] In `addSetting(...)`, remove `dynamicScan`, `routePoints`, `orderedRoutes` and add `routeName` before `loopRoute`:

```kotlin
    addSetting(
      enabled,
      info,
      gemstoneType,
      routeName,
      loopRoute,
      preferWarp,
      allowWalkFallback,
      requireOneBlockRadius,
      etherwarpSneak,
      miningTimeoutSeconds,
      warpVerifyTicks,
      maxWarpAttempts,
      status
    )
```

### Step 3 — Add `loadRoute()`, `advanceRoute()`, `findClosestRouteIndex()`

- [ ] Add these private helpers:

```kotlin
  private fun loadRoute() {
    val loaded = GemstoneRouteStoreBridge.loadActiveGemstoneRoute(
      fallbackTypes = gemstoneOverride?.let { setOf(it) } ?: GemstoneType.entries.toSet(),
      routeName = routeName.value.trim().takeIf { it.isNotEmpty() }
    )
    route = loaded?.points ?: emptyList()
    loadedRouteName = loaded?.routeName
  }

  private fun advanceRoute() {
    if (route.isEmpty()) return
    routeIndex = (routeIndex + 1) % route.size
  }

  private fun findClosestRouteIndex(): Int {
    val player = mc.player ?: return 0
    if (route.isEmpty()) return 0
    return route.indices.minByOrNull { i ->
      val p = route[i]
      val dx = player.x - (p.standPos.x + 0.5)
      val dy = player.y - p.standPos.y.toDouble()
      val dz = player.z - (p.standPos.z + 0.5)
      dx * dx + dy * dy + dz * dz
    } ?: 0
  }
```

### Step 4 — Update `startForAutomation()`

- [ ] Replace the body of `startForAutomation()` with:

```kotlin
  fun startForAutomation(
    gemstone: GemstoneType?,
    source: String = "automation"
  ): Boolean {
    automationMode = true
    automationSource = source
    gemstoneOverride = gemstone
    lastGemstone = gemstone
    lastFailure = "none"
    failedTargets = 0
    miningTicks = 0
    scanCooldown = 0
    currentTarget = null
    lastEtherwarpTarget = null

    loadRoute()
    if (route.isEmpty()) {
      ChatUtils.sendMessage(" Gemstone Miner: no route loaded, cannot start.")
      automationMode = false
      return false
    }
    routeIndex = findClosestRouteIndex()

    currentState = State.SCANNING
    isActive = true
    enabled.value = true

    MiningLoopController.start(
      loopKind = MiningLoopController.LoopKind.GEMSTONE,
      currentTick = currentTick()
    )

    status.value = "Automation: ${gemstone?.displayName ?: "All"}"
    return true
  }
```

### Step 5 — Update `IDLE` handler in `onTick()`

- [ ] Replace the `State.IDLE -> { ... }` block with:

```kotlin
      State.IDLE -> {
        loadRoute()
        if (route.isEmpty()) {
          ChatUtils.sendMessage(" Gemstone Miner: no gemstone route found. Load a route and try again.")
          enabled.value = false
          return
        }
        routeIndex = findClosestRouteIndex()
        currentState = State.SCANNING
        MiningLoopController.transition(
          nextState = MiningLoopController.LoopState.DECIDING,
          currentTick = currentTick()
        )
        status.value = "Deciding"
      }
```

### Step 6 — Rewrite `scanForTarget()` to use route index

- [ ] Replace the entire `scanForTarget()` method with:

```kotlin
  private fun scanForTarget() {
    val tick = currentTick()

    MiningLoopController.transition(
      nextState = MiningLoopController.LoopState.DECIDING,
      currentTick = tick
    )

    if (route.isEmpty()) {
      lastFailure = "no route loaded"
      status.value = "No route"
      currentState = State.FAILED
      return
    }

    val point = route[routeIndex]
    currentTarget = point
    pathStarted = false
    miningStarted = false
    miningTicks = 0
    warpTicks = 0
    warpAttemptsForTarget = 0
    lastEtherwarpTarget = null
    lastGemstone = point.gemstoneTypes.firstOrNull()

    MiningLoopController.setTarget(
      nextTarget = point.toLoopTarget(routeIndex, loadedRouteName),
      currentTick = tick
    )

    if (!automationMode) {
      ChatUtils.sendMessage(
        " Gemstone Miner: deciding ${point.gemstoneTypes.firstOrNull()?.displayName ?: "Unknown"} point #$routeIndex type=${point.pointType.id}"
      )
    }

    if (isWithinStandRadius(point.standPos, WARP_RADIUS) && point.pointType != RoutePointType.WARP) {
      onArrivedAtGemstoneTarget()
      return
    }

    if (preferWarp.value && hasWarpItem()) {
      startWarpTargeting()
    } else if (allowWalkFallback.value) {
      startPath(point)
    } else {
      markCurrentTargetFailed("warp unavailable and walk fallback disabled")
    }
  }
```

### Step 7 — Update `tickWarpTargeting()` to use `lookPos` instead of `standPos`

- [ ] In `tickWarpTargeting()`, change the `StyleEtherwarpTargeting.findTargetWithRetryProfile` calls from `target.standPos.below()` / `target.standPos` to:

```kotlin
    val etherTarget = StyleEtherwarpTargeting.findTargetWithRetryProfile(
      blockPos = target.lookPos.below(),
      attemptSeconds = warpAttemptsForTarget
    ) ?: StyleEtherwarpTargeting.findTargetWithRetryProfile(
      blockPos = target.lookPos,
      attemptSeconds = warpAttemptsForTarget
    )
```

### Step 8 — Update `tickWarpVerifying()` to handle WARP-only points

- [ ] In `tickWarpVerifying()`, replace `onArrivedAtGemstoneTarget()` call with point-type check:

```kotlin
    if (isWithinStandRadius(target.standPos, WARP_RADIUS)) {
      if (target.pointType == RoutePointType.WARP) {
        advanceRoute()
        currentTarget = null
        lastEtherwarpTarget = null
        warpAttemptsForTarget = 0
        scanCooldown = 1
        currentState = State.SCANNING
        status.value = "Warp point complete, deciding"
      } else {
        onArrivedAtGemstoneTarget()
      }
      return
    }
```

### Step 9 — Update `startPath()` to accept `GemstoneRoutePoint`

- [ ] Replace `startPath(target: GemstoneScanner.GemstoneTarget)` with:

```kotlin
  private fun startPath(point: GemstoneRoutePoint) {
    MiningLoopController.markPathing(currentTick())

    val accepted = PathfindingModule.startTo(
      x = point.standPos.x + 0.5,
      y = point.standPos.y.toDouble(),
      z = point.standPos.z + 0.5,
      owner = PathOwner.COMMISSION,
      source = "GemstoneMiner:$automationSource",
      timeoutTicks = 1200,
      arrivalRadius = WALK_RADIUS,
      onArrive = {
        if (enabled.value && currentState == State.PATHING) {
          if (!requireOneBlockRadius.value || isWithinStandRadius(point.standPos, WALK_RADIUS)) {
            onArrivedAtGemstoneTarget()
          } else {
            markCurrentTargetFailed("path arrived outside 1 block radius")
          }
        }
      },
      onFail = { reason ->
        if (enabled.value && currentState == State.PATHING) {
          markCurrentTargetFailed("path failed: $reason")
        }
      }
    )

    if (!accepted) {
      markCurrentTargetFailed("path request rejected")
      return
    }

    pathStarted = true
    currentState = State.PATHING
    status.value = "Pathing to ${point.gemstoneTypes.firstOrNull()?.displayName ?: "gemstone"}"
  }
```

### Step 10 — Update `tickPathing()` display

- [ ] Replace `status.value = "Pathing: ${target.type.displayName}"` with:

```kotlin
    status.value = "Pathing: ${target.gemstoneTypes.firstOrNull()?.displayName ?: "gemstone"}"
```

### Step 11 — Update `onArrivedAtGemstoneTarget()`

- [ ] Replace `onArrivedAtGemstoneTarget()` entirely:

```kotlin
  private fun onArrivedAtGemstoneTarget() {
    val target = currentTarget ?: run { currentState = State.SCANNING; return }

    if (requireOneBlockRadius.value && !isWithinStandRadius(target.standPos, 1.0)) {
      markCurrentTargetFailed("not within 1 block mining radius")
      return
    }

    val miningType = gemstoneOverride?.miningTypeName
      ?: target.gemstoneTypes.firstOrNull()?.miningTypeName
      ?: "ruby"

    StrictMiningTargetFilter.setGemstone(
      gemstoneName = miningType,
      source = "gemstone:${target.gemstoneTypes.firstOrNull()?.displayName ?: "Unknown"}"
    )

    MiningMacroModule.startForAutomation(miningType)

    miningStarted = true
    miningTicks = 0
    currentState = State.MINING
    MiningLoopController.markMining(currentTick())
    status.value = "Mining ${target.gemstoneTypes.firstOrNull()?.displayName ?: "gemstone"}"
  }
```

### Step 12 — Update `tickMining()` to advance route instead of calling GemstoneScanner

- [ ] Replace the `if (!MiningMacroModule.isActive && miningStarted)` block in `tickMining()` with:

```kotlin
    if (!MiningMacroModule.isActive && miningStarted) {
      advanceRoute()
      MiningLoopController.markAdvancing(currentTick())
      miningStarted = false
      miningTicks = 0
      currentTarget = null
      lastEtherwarpTarget = null
      scanCooldown = 10

      if (loopRoute.value || automationMode) {
        currentState = State.SCANNING
        MiningLoopController.markDeciding(currentTick())
        status.value = "Route advanced, deciding"
      } else {
        stopAll()
      }
      return
    }
```

- [ ] Replace `status.value = "Mining ${target.type.displayName} ${miningTicks / 20}s"` with:

```kotlin
    status.value = "Mining ${target.gemstoneTypes.firstOrNull()?.displayName ?: "gemstone"} ${miningTicks / 20}s"
```

### Step 13 — Update `markCurrentTargetFailed()` — remove GemstoneScanner call, advance route

- [ ] Replace `GemstoneScanner.markRouteTargetFailed(target)` with `advanceRoute()` in `markCurrentTargetFailed()`.

### Step 14 — Update `stopAll()` — clear new fields, remove GemstoneScanner calls

- [ ] In `stopAll()`, add route field resets and remove `GemstoneScanner.markRouteTargetFailed` references:

```kotlin
    route = emptyList()
    routeIndex = 0
    loadedRouteName = null
    gemstoneOverride = null
```

(These go after the existing clears. Remove `gemstoneOverride = null` from its current position if it exists and only keep it here.)

### Step 15 — Replace the `GemstoneScanner.GemstoneTarget.toLoopTarget()` extension

- [ ] Remove `private fun GemstoneScanner.GemstoneTarget.toLoopTarget()` and add:

```kotlin
  private fun GemstoneRoutePoint.toLoopTarget(
    index: Int,
    routeName: String?
  ): MiningLoopController.LoopTarget {
    return MiningLoopController.LoopTarget(
      kind = MiningLoopController.LoopKind.GEMSTONE,
      routeName = routeName,
      routeIndex = index,
      standPos = standPos,
      targetPos = lookPos,
      displayType = gemstoneTypes.firstOrNull()?.displayName ?: "Unknown",
      source = "route"
    )
  }
```

### Step 16 — Add missing imports and build

- [ ] Add imports at the top of `GemstoneMinerModule.kt` if not already present:

```kotlin
import org.cobalt.internal.routes.RoutePointType
```

- [ ] Remove unused import for `GemstoneScanner` if it is no longer referenced anywhere in the file.

- [ ] Build:

```
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/gemstone/GemstoneMinerModule.kt
git commit -m "feat(gemstone): replace scanner-based targeting with route-following loop using GemstoneRouteStoreBridge"
```

---

## Self-Review

**Spec coverage check:**

| Spec section | Task |
|---|---|
| §1.1 Five new fields | Task 1 |
| §1.2 updateCommissionsIfChanged v5 | Task 2 |
| §1.3 shouldWaitForLastCompleted | Task 3 |
| §1.4 handleChoosing guard order + double-claim | Task 4 |
| §1.5 onCommissionComplete sets lastCompletedCommissionName + pigeon reset | Task 4 (step 1) |
| §1.6 updateCommissionsFromGui | Task 5 |
| §1.7 Pigeon cap + ensureDrillEquippedForClaim | Task 6 |
| §1.8 Crystal/Star Sentry avoidance | Task 7 |
| §2.1 Multi-ore oreOverrides: Set | Task 8 |
| §2.2 TunnelVeinData JSON resource | *Not included — deferred: requires converting 470KB GlaciteData.js external file* |
| §3.1–3.4 Route-following loop, state machine, route index | Task 11 |
| §3.5 GemstoneRouteStoreBridge | Task 10 |
| §3.6 Settings update | Task 11 step 2 |
| GemstoneTypes pointType | Task 9 |

**TunnelVeinData note:** The `TunnelVeinData.getVeins()` stub (`return emptyList()`) is not fixed in this plan. Implementing it requires a separate conversion script to transform `GlaciteData.js` (470 KB, at `C:\Users\aeare\Downloads\V5-main\data\GlaciteData.js`) into `src/main/resources/cobalt/glacite_veins.json`. This can be done in a follow-up — it only affects the "Recorded Data" checkbox in TunnelMinerModule, not the route-store or dynamic scan modes.
