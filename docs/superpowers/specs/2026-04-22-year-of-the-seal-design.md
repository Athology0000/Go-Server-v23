# Year of the Seal Macro — Design Spec

**Date:** 2026-04-22
**Feature:** `YearOfTheSealModule` — automatically pathfinds to the predicted beach ball landing position during the Year of the Seal SkyBlock event.

---

## Overview

A standalone Cobalt module (`object : Module("Year of the Seal")`) that:
1. Detects the beach ball entity each tick
2. Tracks its position history and detects bounces
3. Runs SkyHanni's 3-model polynomial regression to predict the landing position
4. Feeds the stable prediction to `NativePathfinder.setTarget()` to walk the player there

The prediction logic is a Kotlin port of SkyHanni's `BeachBallCatchHelper.kt` (PR #3432).

---

## File Layout

```
src/main/kotlin/org/cobalt/internal/seal/
  YearOfTheSealModule.kt   # module + BallPredictor + polynomial model classes
```

Registered in `Cobalt.onInitializeClient()` alongside other modules.

---

## Ball Entity Detection

The beach ball is represented by two entities at the same XZ position:
- **Invisible Slime** — physics entity, `maxHealth == 1024f`, `isInvisible == true`
- **Invisible Armor Stand** — visual skull display (ignored by this module)

Each `TickEvent.Start`, scan `mc.level?.entitiesForRendering()` for:
```
entity is Slime && entity.isInvisible && entity.maxHealth == 1024f
```

- New matches → add `BallPredictor(entityId)` to `predictors: MutableMap<Int, BallPredictor>`
- Entries whose entity is removed or no longer present → remove and call `NativePathfinder.stop()` if it was the active target

---

## BallPredictor

One instance per tracked ball entity. Owns all position history, bounce detection, and prediction state.

### State

| Field | Type | Purpose |
|---|---|---|
| `data` | `MutableList<Vec3>` | Full position history since entity spawn |
| `startIndex` | `Int` | Index into `data` marking start of current bounce segment |
| `minY` | `Double` | Lowest Y ever observed (ground level) |
| `bounceCounter` | `Int` | Number of confirmed bounces |
| `lastBounceTime` | `Long` (ms) | System time of last bounce, for 800ms debounce |
| `positive` | `Boolean` | Whether Y-delta was positive last update |
| `lastPosition` | `Vec3` | Previous position for delta calculation |
| `landingPos` | `Vec3?` | Current best landing prediction (null if insufficient data) |
| `predictionTick` | `Int` | Counter mod 3 — prediction updates every 3 ticks |

### `update(pos: Vec3)`

Called every tick with the slime's current position.

1. Append `pos` to `data`
2. Update `minY = min(minY, pos.y)`
3. **Bounce detection:**
   - Compute `diff = pos.y - lastPosition.y`
   - If `diff > 0` and `positive == false` and `System.currentTimeMillis() - lastBounceTime > 800`:
     - `bounceCounter++`, `lastBounceTime = now`, `startIndex = data.lastIndex`, `positive = true`
   - Else if `diff < 0`: `positive = false`
   - Skip if `lastPosition.distance(pos) < 0.3` (entity hasn't moved meaningfully)
4. **Prediction refresh** (every 3 ticks):
   - Run all 3 models against `data.subList(startIndex, data.lastIndex)`
   - Collect valid model outputs (those with enough data, landing within ±1 block of `minY`)
   - Average X and Z of valid outputs → set `landingPos`

---

## Polynomial Models

Three sealed subclasses of an abstract `PolyModel`:

```
abstract class PolyModel {
    abstract val minPoints: Int
    abstract fun sampleIndices(start: Int, current: Int): Triple<Int, Int, Int>
    fun predict(data: List<Vec3>, start: Int, minY: Double): Vec3?
}
```

`predict()` shared logic:
1. Pull three sample indices `(t1, t2, t3)` from the subclass
2. Extract Y values `(y1, y2, y3)` at those indices
3. Fit quadratic: solve for `a, b, c` in `y = a·t² + b·t + c`
4. Compute average X and Z drift per tick across the slice
5. Extrapolate forward (up to 300 ticks) until `poly(t) ≤ minY`
6. Return the extrapolated `Vec3` at landing, or `null` if the ball never reaches `minY` within the window

### SmallPoly
- `minPoints = 3`
- Samples: `t`, `t-1`, `t-2` (most recent 3 points, most responsive)

### AveragePoly
- `minPoints = 7`
- Samples: average of pairs at `(t-1, t-2)`, `(t-3, t-4)`, `(t-5, t-6)` → 3 smoothed Y values
- Reduces noise on noisy motion data

### SpreadPoly
- `minPoints = 5`
- Samples: `t-1`, `(t - start) / 2 + start`, `start + 1` (spread across the full bounce segment)
- Balances early trajectory with recent data

---

## Pathfinding Integration

In `onTick`:
1. If `!enabled` or `predictors.isEmpty()` → return
2. Pick the first predictor with `bounceCounter >= minBounces.value && landingPos != null`
3. If the landing position has shifted more than **0.5 blocks** from the last issued target:
   - Call `NativePathfinder.setTarget(landingPos.x, minY, landingPos.z)`
   - Store as `lastIssuedTarget`
4. Call `NativePathfinder.tick()` and apply the returned `PathCommand`
5. Handle `PathStatus.ARRIVED` / `PathStatus.FAILED` gracefully (stop forcing movement)

On disable / island change / all balls removed: `NativePathfinder.stop()`, `MovementManager.clearForcedMovement()`, `predictors.clear()`.

---

## Settings

| Setting | Type | Default | Description |
|---|---|---|---|
| `toggleKeybind` | `KeyBindSetting` | unbound | Enable/disable the macro |
| `minBounces` | `SliderSetting` | 2 (min 1, max 10) | Minimum observed bounces before pathfinding starts |

---

## Event Subscriptions

| Event | Action |
|---|---|
| `TickEvent.Start` | Scan for new ball entities, update predictors, pathfind |
| `WorldRenderEvent.Last` | (optional future) render landing spot overlay |

No `BlockChangeEvent` or `PacketEvent` subscriptions needed — position polling each tick is sufficient.

---

## Edge Cases

- **Multiple balls:** All are tracked, pathfinder targets the one that first meets `minBounces` threshold.
- **Ball despawns mid-flight:** Predictor removed, pathfinder stopped.
- **Not in Year of the Seal area:** The 1024 HP invisible slime filter is specific enough to avoid false positives elsewhere. If needed, an island/scoreboard check can be added later.
- **Module disabled while pathfinding:** Stop pathfinder and clear movement on the same tick.
- **First few ticks (< minPoints):** `landingPos` stays `null`; no pathfinding until models have enough data.
