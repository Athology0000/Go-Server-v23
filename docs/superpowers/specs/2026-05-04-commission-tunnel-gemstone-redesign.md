# Commission + Tunnel + Gemstone Redesign
**Date:** 2026-05-04
**Scope:** CommissionMacroModule (phantom patterns), TunnelMinerModule (multi-ore + vein data), GemstoneMinerModule (route-following etherwarp loop)

---

## 1. CommissionMacroModule â€” Port rdbt phantom Patterns

### 1.1 New Fields
Add to the module-level state block (all cleared in `resetState()` and `resetCommissionAfterClaim()`):

```kotlin
private var lastCompletedCommissionName: String? = null
private var ignoreTabUpdatesUntil = 0L
private var lastCommissionSyncSource: String? = null
private var firstPigeonAttemptAt = 0L
private var pigeonAttempts = 0
```

`lastCommissionName` (existing HUD display field) is kept as-is. `lastCompletedCommissionName` is the separate internal guard used to prevent re-selecting a stale completed entry.

### 1.2 `updateCommissionsIfChanged()` â€” Full phantom Rewrite
Replace the current body with:
1. Early return if `commissions == newCommissions`
2. If `ignoreTabUpdatesUntil > now` and `lastCommissionSyncSource == "GUI"` â†’ return (locked after GUI sync)
3. If `ignoreTabUpdatesUntil > now` and `lastCompletedCommissionName != null`:
   - If the new list still has that commission at progress=1 â†’ return (stale data)
   - Otherwise clear `ignoreTabUpdatesUntil`
4. Else if `ignoreTabUpdatesUntil <= now` â†’ clear it
5. Accept new commissions; set `lastCommissionSyncSource = "TAB"`
6. If `awaitingTabUpdate`:
   - Check `stillCompleted` = any commission in the new list has progressâ‰¥1 AND resolves to a known task
   - If `!stillCompleted` â†’ `awaitingTabUpdate = false`
   - Else if `lastCompletedCommissionName != null`: if that specific commission is now gone or progress<1 â†’ `awaitingTabUpdate = false`

### 1.3 `shouldWaitForLastCompleted()`
New private method:
```kotlin
private fun shouldWaitForLastCompleted(): Boolean {
    val name = lastCompletedCommissionName ?: return false
    val entry = commissions.firstOrNull { it.name == name }
    if (entry != null && entry.progress >= 1.0) return true
    lastCompletedCommissionName = null
    return false
}
```

### 1.4 `handleChoosing()` â€” Guard Order
Replace the current top of `handleChoosing()` with phantom order:
1. `if (shouldWaitForLastCompleted()) return`
2. `if (awaitingTabUpdate) return`
3. `val completed = findCompletedCommission()`
4. If completed found: check double-claim guard â€” if `completed.name == lastCompletedCommissionName && ignoreTabUpdatesUntil > now` â†’ `awaitingTabUpdate = true; return`
5. Otherwise proceed to `onCommissionComplete()`

### 1.5 `onCommissionComplete()` Changes
After the existing stop-everything calls, also set:
```kotlin
lastCompletedCommissionName = currentCommission?.name
```
(alongside the existing `lastCommissionName = currentCommission?.name`)

Reset `firstPigeonAttemptAt = 0L` and `pigeonAttempts = 0`.

### 1.6 `updateCommissionsFromGui()` â€” New Method
Called just before closing the claim GUI (before `mc.player?.closeContainer()`):
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

Call site: in `handleClaiming()`, in the branch where `findClaimSlot()` returns -1 and we are about to close the GUI.

### 1.7 `handleClaiming()` â€” Pigeon Cap + Drill Equip
**Pigeon section** (replace current unlimited right-click loop):
- If `pigeonSlot >= 0`:
  - `val pigeonTimedOut = firstPigeonAttemptAt > 0L && (now - firstPigeonAttemptAt) > 4_000L`
  - If `pigeonAttempts < 3 && !pigeonTimedOut`:
    - If wrong slot â†’ `holdHotbarSlot(pigeonSlot); delay(3)` and return
    - Else: if `firstPigeonAttemptAt == 0L` set it to `now`; `pigeonAttempts++`; `rightClick(); delay(10)`; return
  - Fall through to emissary path if attempts exhausted or timed out

**Drill equip before emissary right-click** (`ensureDrillEquippedForClaim()`):
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
Call this just before `faceEntity(emissary)` + `rightClick()` in the emissary interaction block. If it returns false, return early (the delay fires next tick).

### 1.8 `getAvoidanceEntities()` â€” Crystal/Star Sentries
Extend beyond players to also include nearby non-player entities whose name contains `"Sentry"`:
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

---

## 2. TunnelMinerModule â€” Multi-Ore + Vein Data

### 2.1 Multi-Ore Support
Change `oreOverride: TunnelOreType?` â†’ `oreOverrides: Set<TunnelOreType>`.

Update `startForAutomation()`:
```kotlin
fun startForAutomation(ores: Set<TunnelOreType>, source: String = "commission"): Boolean
```
Single-ore convenience overload kept:
```kotlin
fun startForAutomation(ore: TunnelOreType, source: String = "commission") =
    startForAutomation(setOf(ore), source)
```

Update `selectedOres()`:
```kotlin
private fun selectedOres(): Set<TunnelOreType> {
    val overrides = oreOverrides
    if (overrides.isNotEmpty()) return overrides
    return setOf(when (oreType.value) { ... })
}
```

### 2.2 Vein Data â€” JSON Resource
- Write `src/main/resources/phantom/glacite_veins.json` containing the GlaciteData.js coordinates converted to JSON format: `{ "glacite": [[[x,y,z],...], ...], "tungsten": [...], ... }`
- A conversion script (`scripts/convert_glacite_data.js`) reads GlaciteData.js and writes the JSON
- `TunnelVeinData.getVeins()` loads this JSON lazily on first call using Gson, maps to `TunnelVein` objects

```kotlin
object TunnelVeinData {
    private var cache: Map<TunnelOreType, List<TunnelVein>>? = null

    fun getVeins(selectedOres: Set<TunnelOreType>): List<TunnelVein> {
        val loaded = cache ?: loadFromResources().also { cache = it }
        return selectedOres.flatMap { loaded[it] ?: emptyList() }
    }

    private fun loadFromResources(): Map<TunnelOreType, List<TunnelVein>> { ... }
}
```

---

## 3. GemstoneMinerModule â€” Route-Following Etherwarp Loop

### 3.1 Behavior Summary
User records a route (via existing `RouteEditMode`) with WARP and MINE points. Each MINE point has a look position (gemstone block face) and a stand position (where the player lands). On enable, the macro finds the closest route point and begins the loop:

```
IDLE â†’ DECIDING â†’ WARP_TARGETING â†’ WARP_ALIGNING â†’ WARP_USING â†’ WARP_VERIFYING
                                                                        â†“
                                                                    [if MINE point]
                                                                     MINING
                                                                        â†“
                                                               advance routeIndex
                                                                  â†’ DECIDING
```

WARP-only points skip MINING and go straight back to DECIDING after verifying arrival.

### 3.2 Route Format
Uses existing `GemstoneRoutePoint`:
```kotlin
data class GemstoneRoutePoint(
    val standPos: BlockPos,    // where the player lands after warping
    val lookPos: BlockPos,     // the block face to aim at for etherwarp
    val gemstoneTypes: Set<GemstoneType>,
    val enabled: Boolean,
    val pointType: RoutePointType  // WARP = travel only, MINE/VEIN = mine after
)
```

Routes are loaded from RouteStore via `GemstoneRouteStoreBridge` (see Â§3.5).

### 3.3 State Machine
**DECIDING:**
- Get `route[routeIndex]` (skip disabled points)
- If already within `WARP_RADIUS` (1.0 blocks) of `standPos` AND point is MINE â†’ go to MINING
- Else â†’ go to WARP_TARGETING

**WARP_TARGETING:**
- Find visible face: `StyleEtherwarpTargeting.findTargetWithRetryProfile(point.lookPos.below())` falling back to `point.lookPos`
- If no face after `maxWarpAttempts` â†’ walk fallback (PathfindingModule) or fail point
- On success â†’ `WARP_ALIGNING`

**WARP_ALIGNING:**
- Apply rotation toward ether target
- After 2 ticks â†’ `WARP_USING`

**WARP_USING:**
- Hold `keyShift` (if etherwarpSneak enabled)
- Switch to AOTV slot, apply rotation, press `keyUse`
- â†’ `WARP_VERIFYING`

**WARP_VERIFYING:**
- Wait `warpVerifyTicks`
- If within 1.0 blocks of `standPos` â†’ `onArrivedAtPoint()`
- Else if attempts remain â†’ retry WARP_TARGETING
- Else â†’ walk fallback or fail

**MINING:**
- Call `MiningMacroModule.startForAutomation(gemstoneTypeName)`
- Poll `MiningMacroModule.isActive`:
  - If `!isActive && miningStarted` â†’ mining done, advance `routeIndex`, â†’ DECIDING
- Mining timeout â†’ fail point, advance

**FAILED:**
- Advance routeIndex past the bad point, `scanCooldown = 20`, â†’ DECIDING

### 3.4 Route Index Management
```kotlin
private var routeIndex = 0
private var route: List<GemstoneRoutePoint> = emptyList()

private fun advanceRoute() {
    routeIndex = (routeIndex + 1) % route.size.coerceAtLeast(1)
}

private fun findClosestRouteIndex(): Int {
    val player = mc.player ?: return 0
    return route.indices.minByOrNull { i ->
        val p = route[i]
        distance(player.x, player.y, player.z, p.standPos)
    } ?: 0
}
```

On enable: load route â†’ if empty, send message and disable â†’ else `routeIndex = findClosestRouteIndex()` â†’ `currentState = DECIDING`.

### 3.5 `GemstoneRouteStoreBridge` â€” Implementation
Port the same pattern as `TunnelRouteStoreBridge`:
```kotlin
object GemstoneRouteStoreBridge {
    fun loadActiveGemstoneRoute(
        fallbackTypes: Set<GemstoneType> = GemstoneType.entries.toSet()
    ): LoadedGemstoneRoute? {
        val route = RouteStore.resolveActiveRoute(RouteType.GEMSTONE) ?: return null
        val points = route.points
            .filter { it.enabled }
            .map { rp ->
                GemstoneRoutePoint(
                    standPos = rp.standPos,
                    lookPos = rp.veinPos,
                    gemstoneTypes = detectGemstoneTypes(rp.blockId, fallbackTypes),
                    enabled = true,
                    pointType = rp.type
                )
            }
        return if (points.isEmpty()) null else LoadedGemstoneRoute(route.name, points)
    }

    private fun detectGemstoneTypes(blockId: String?, fallback: Set<GemstoneType>): Set<GemstoneType> {
        if (blockId.isNullOrBlank()) return fallback
        return GemstoneType.entries.filter { type ->
            type.keywords.any { blockId.contains(it, ignoreCase = true) }
        }.toSet().ifEmpty { fallback }
    }
}
```

### 3.6 Settings (GemstoneMinerModule)
Keep existing settings. Add:
- `routeName: TextSetting` â€” optional explicit route name
- Remove `dynamicScan` and `routePoints` checkboxes (route-only mode now)
- Keep: `preferWarp`, `allowWalkFallback`, `etherwarpSneak`, `miningTimeoutSeconds`, `warpVerifyTicks`, `maxWarpAttempts`, `loopRoute`

### 3.7 What Gets Removed / Simplified
- `GemstoneScanner` is no longer the primary target source (still exists but not called by this module)
- `startForAutomation()` now loads the route and starts the loop (for commission integration)
- `MiningLoopController` integration kept for timeout tracking

---

## Summary of Files Changed

| File | Change |
|---|---|
| `CommissionMacroModule.kt` | +5 fields, rewrite `updateCommissionsIfChanged`, add `shouldWaitForLastCompleted`, `updateCommissionsFromGui`, `ensureDrillEquippedForClaim`, update `handleChoosing`, `handleClaiming`, `onCommissionComplete`, `getAvoidanceEntities` |
| `TunnelMinerModule.kt` | `oreOverride` â†’ `oreOverrides: Set<TunnelOreType>`, update `startForAutomation` + `selectedOres` |
| `TunnelTypes.kt` | `TunnelVeinData.getVeins()` â€” load from JSON resource |
| `resources/phantom/glacite_veins.json` | New â€” GlaciteData.js coordinates as JSON |
| `GemstoneMinerModule.kt` (gemstone/) | Full rewrite to route-following loop |
| `GemstoneRouteStoreBridge.kt` | Implement `loadActiveGemstoneRoute()` |
| `GemstoneTypes.kt` | Add `pointType` field to `GemstoneRoutePoint` |
