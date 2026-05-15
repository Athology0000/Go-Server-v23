# Diana Macro Design

**Date:** 2026-03-23

## Goal

A fully automated Diana event macro for Hypixel Skyblock. The macro locates burrows via CRIT particle triangulation, pathfinds to them, digs them, kills the spawned mob using key simulation, waits at the kill location, then loops.

## Architecture

Two new files in `src/main/kotlin/org/phantom/internal/diana/`:

- **`DianaParticleTracker.kt`** â€” particle collector. Accepts CRIT particle positions (called from the netty thread), performs direction-averaging from the player position, extends the ray to the ground surface via `Level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)`. Thread-safe: `particles` is a `Collections.synchronizedList`. All reads of `particles.size` and snapshot copies use `synchronized(particles) { ... }`.
- **`DianaModule.kt`** â€” main `Module` subclass. Owns the state machine, subscribes to `TickEvent.Start` and `PacketEvent.Incoming`, drives `NativePathfinder`, renders a box at the predicted burrow via `OverlayRenderEngine`. Holds a `BezierTrackingRotationStrategy` instance stored as a field (not created per-tick).

Two existing files modified:

- **`MovementManager.kt`** â€” add `forcedAttack` and `forcedUse` fields; clear in `clearForcedMovement()`.
- **`KeyMappingMixin.java`** â€” replace the existing combined `keyAttack`/`keyUse` block with two separate blocks returning the new flags.

`DianaModule` is registered in `Phantom.onInitializeClient()`.

## Settings

| Name | Type | Default | Notes |
|---|---|---|---|
| Spade Slot | `SliderSetting` (1.0â€“9.0, step 1.0) | 1.0 | 1-indexed display; use `.value.toInt() - 1` for `inventory.selected` (0-indexed) |
| Weapon Slot | `SliderSetting` (1.0â€“9.0, step 1.0) | 2.0 | Same 1-indexed convention |
| Collect Duration | `SliderSetting` (20.0â€“120.0, step 1.0) | 60.0 | Ticks; use `.value.toInt()` |
| Post-Kill Wait | `SliderSetting` (20.0â€“200.0, step 1.0) | 80.0 | Ticks; use `.value.toInt()` |
| Min Particles | `SliderSetting` (3.0â€“30.0, step 1.0) | 8.0 | Minimum count to trust triangulation; use `.value.toInt()` |

## PathStatus Reference

`PathStatus` enum: `IDLE, PLANNING, EXECUTING, RECOVERING, REPLANNING, ARRIVED, FAILED`

**`NativePathfinder.tick()` return contract (verified from source):**
- Returns **`null`** for: `IDLE`, `PLANNING`, `ARRIVED`, `FAILED`
- Returns a **non-null `PathCommand`** for: `EXECUTING`, `RECOVERING`, `REPLANNING`

Always read `NativePathfinder.status` when `tick()` returns null â€” do not infer state from null alone.

## State Machine

```
IDLE â†’ ACTIVATING_SPADE â†’ COLLECTING_PARTICLES â†’ PATHFINDING â†’ DIGGING â†’ COMBAT â†’ WAITING â†’ IDLE
```

All transitions happen in `onTick()` subscribed to `TickEvent.Start`. Every state entry must start with:
```kotlin
val player = mc.player ?: run { cleanup(); return }
val level  = mc.level  ?: run { cleanup(); return }
```

### IDLE
- Switch hotbar: `player.inventory.selected = spadeSlot` (0-indexed).
- Lock movement: `MovementManager.setMovementLock(true)`.
- Set `hasForcedMovement = true` and `forcedUse = true`:
  ```kotlin
  MovementManager.setForcedMovement(false, false, false, false, false, false, false)  // sets hasForcedMovement = true
  MovementManager.forcedUse = true
  ```
  **`hasForcedMovement` must be explicitly set via `setForcedMovement(...)` â€” setting `forcedUse` alone is not enough because `KeyMappingMixin` gates all forced keys on `hasForcedMovement && forcedXxx`.**
- Transition to ACTIVATING_SPADE.

### ACTIVATING_SPADE
- Hold for exactly 1 tick (use key already set from IDLE tick).
- Clear: `MovementManager.forcedUse = false` (leave `isMovementLocked = true` and `hasForcedMovement = true`).
- Call `DianaParticleTracker.reset()`.
- Store `activationPlayerPos = player.position()`.
- Reset `collectTicksElapsed = 0`.
- Transition to COLLECTING_PARTICLES.

### COLLECTING_PARTICLES
- Increment `collectTicksElapsed` each tick.
- In `PacketEvent.Incoming` handler: if packet is `ClientboundLevelParticlesPacket` and `packet.particle.type == ParticleTypes.CRIT`, call `DianaParticleTracker.addParticle(packet.x, packet.y, packet.z)`.
- When `collectTicksElapsed >= collectDurationTicks`:
  - If `DianaParticleTracker.count() >= minParticles`:
    - `burrowPos = DianaParticleTracker.getBurrowPos(activationPlayerPos, level)` â†’ if non-null: transition to PATHFINDING, `pathfindingTicksElapsed = 0`.
    - If null (degenerate direction): transition to IDLE (retry).
  - If `count() < minParticles`: transition to IDLE (retry).
  - On either retry path: do **not** unlock movement â€” IDLE re-locks it next tick.

### PATHFINDING
- On entry: `NativePathfinder.setTarget(burrowPos.x, burrowPos.y, burrowPos.z)`.
- Each tick: increment `pathfindingTicksElapsed`. Call `val cmd = NativePathfinder.tick()`.
  - If `cmd != null` (status is EXECUTING, RECOVERING, or REPLANNING): `cmd.applyToPlayer()`. This also sets `hasForcedMovement = true` via `MovementManager.setForcedMovement(...)`.
  - If `cmd == null`: check `NativePathfinder.status`:
    - `ARRIVED`, or `player.position().distanceTo(burrowPos) â‰¤ 2.0`: switch hotbar to spade slot, `digTicksElapsed = 0` â†’ transition to DIGGING.
    - `FAILED`: `NativePathfinder.stop()`, `MovementManager.setMovementLock(false)`, log "Diana: pathfinding failed", transition to IDLE.
    - `IDLE`: `NativePathfinder.stop()`, `MovementManager.setMovementLock(false)`, transition to IDLE.
    - `PLANNING`: `MovementManager.clearForcedMovement()` â€” clears stale flags to prevent jump spam. Stay in PATHFINDING.
- Timeout: if `pathfindingTicksElapsed > 300`: `NativePathfinder.stop()`, `MovementManager.setMovementLock(false)`, log "Diana: pathfinding timed out", transition to IDLE.

### DIGGING
- Each tick: call `MovementManager.setForcedMovement(false, false, false, false, false, false, false)` (ensures `hasForcedMovement = true`), then `MovementManager.forcedUse = true`. **This call is unconditional every tick.**
- Scan for live mob: `level.getEntitiesOfClass(LivingEntity::class.java, AABB(burrowPos, burrowPos).inflate(6.0))`. (Use `getEntitiesOfClass` â€” do **not** use `entitiesForRendering()` which is render-thread-only.)
- Match candidates whose `entity.displayName.string` contains any of (case-insensitive): `"Minotaur"`, `"Minos Hunter"`, `"Minos Champion"`, `"Gaia Construct"`, `"Minos Inquisitor"`.
- On mob found: `forcedUse = false`, `targetEntityId = mob.id`, switch hotbar to weapon slot â†’ transition to COMBAT.
- Timeout: if `digTicksElapsed >= 60`: `forcedUse = false`, log "Diana: no mob spawned, retrying", transition to IDLE.
- Increment `digTicksElapsed` each tick.

### COMBAT

`rotationStrategy` is a `BezierTrackingRotationStrategy` stored as a module field:
```kotlin
private const val COMBAT_ROTATION_STEP_SCALE = 0.62   // same value as CombatMacroModule

private val rotationStrategy = BezierTrackingRotationStrategy(
  yawStepSampler   = { (RotationsModule.sample(RotationsModule.combatYawStep.value)   * COMBAT_ROTATION_STEP_SCALE).toFloat() },
  pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
  curveInProvider  = { RotationsModule.bezierCurveIn.value.toFloat() },
  curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
  minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
  // snapThreshold retains its default of 0.25f â€” matches CombatMacroModule behaviour
)
```

**Tick ordering:** `TickEvent.Start` fires at the HEAD of `Minecraft.tick()`, before `handleKeybinds()`. So: set `forcedAttack = true` at the **end** of the COMBAT branch, and clear it at the **start** of the next COMBAT tick. The flag is `true` when `handleKeybinds()` polls `isDown(keyAttack)` later in that same tick.

Each tick in COMBAT:
1. **Clear** `MovementManager.forcedAttack = false` (clears flag set last tick).
2. Look up target: `level.getEntity(targetEntityId)`. If null or `(entity as? LivingEntity)?.isAlive != true` â†’ transition to WAITING (`waitTicksElapsed = 0`). Return.
3. Re-check `mc.player != null` (disconnect race). If null â†’ `cleanup()`; return.
4. Aim: `RotationExecutor.rotateTo(AngleUtils.getRotation(targetEntity as Entity), rotationStrategy)`. `AngleUtils.getRotation(Entity)` computes yaw/pitch from player eye to mob automatically â€” no manual angle math needed.
5. Ensure `hasForcedMovement = true`: `MovementManager.setForcedMovement(false, false, false, false, false, false, false)`. (Follows established API contract â€” do not assign `hasForcedMovement` directly.)
6. **Set** `MovementManager.forcedAttack = true` (will be read by game input poll later this tick).

### WAITING
- Unlock movement: `MovementManager.setMovementLock(false)` (also calls `clearForcedMovement()`, resetting attack/use/hasForcedMovement flags).
- Increment `waitTicksElapsed` each tick.
- When `waitTicksElapsed >= postKillWaitTicks`: `DianaParticleTracker.reset()` â†’ transition to IDLE.

## DianaParticleTracker

```kotlin
object DianaParticleTracker {
  private val particles: MutableList<Vec3> = Collections.synchronizedList(mutableListOf())

  fun reset() { synchronized(particles) { particles.clear() } }

  // Called from netty thread â€” Collections.synchronizedList makes add() thread-safe
  fun addParticle(x: Double, y: Double, z: Double) { particles.add(Vec3(x, y, z)) }

  // Compound operation â€” must synchronize explicitly
  fun count(): Int = synchronized(particles) { particles.size }

  fun getBurrowPos(playerPos: Vec3, level: Level): Vec3? {
    val snapshot: List<Vec3>
    synchronized(particles) { snapshot = particles.toList() }
    if (snapshot.size < 2) return null   // defence-in-depth; caller already checked count() >= minParticles

    val dirs = snapshot.map { it.subtract(playerPos).normalize() }
    val avgDir = dirs.fold(Vec3.ZERO) { acc, v -> acc.add(v) }.scale(1.0 / dirs.size)
    if (avgDir.lengthSqr() < 1e-6) return null
    val dir = avgDir.normalize()

    // Project 40 blocks along the average direction, then snap to ground height.
    val estX = playerPos.x + dir.x * 40.0
    val estZ = playerPos.z + dir.z * 40.0
    val groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, estX.toInt(), estZ.toInt()).toDouble()
    return Vec3(estX, groundY, estZ)
  }
}
```

## Rendering

During PATHFINDING and DIGGING, render a box at `burrowPos` each tick. During COLLECTING_PARTICLES, `burrowPos` is null (not yet triangulated) so no box is shown â€” this is intentional.

```kotlin
// Called each tick in PATHFINDING and DIGGING states
val level = mc.level ?: return
val bp = burrowPos ?: return
OverlayRenderEngine.addBox(
  level,
  bp.x - 0.5, bp.y, bp.z - 0.5,
  bp.x + 0.5, bp.y + 1.0, bp.z + 0.5,
  fill    = OverlayRenderEngine.Color(255, 220, 0, 60),
  outline = OverlayRenderEngine.Color(255, 220, 0, 255),
  // lineWidth defaults to 1.5f
  durationTicks = 10,
  tag = "diana-burrow"
)
```

## MovementManager Changes

Add alongside existing forced-key fields:

```kotlin
@JvmField @Volatile var forcedAttack = false
@JvmField @Volatile var forcedUse    = false
```

Add to `clearForcedMovement()`:

```kotlin
forcedAttack = false
forcedUse    = false
```

## KeyMappingMixin Changes

**Replace** the existing combined block:

```java
// REMOVE:
if (self == mc.options.keyAttack || self == mc.options.keyUse) {
  cir.setReturnValue(false);
}
```

**Replace with:**

```java
if (self == mc.options.keyAttack) {
  cir.setReturnValue(MovementManager.hasForcedMovement && MovementManager.forcedAttack);
  return;
}
if (self == mc.options.keyUse) {
  cir.setReturnValue(MovementManager.hasForcedMovement && MovementManager.forcedUse);
  return;
}
```

Both new blocks include `return` to match the pattern of every other block in the mixin.

## cleanup() Helper

```kotlin
private fun cleanup() {
  NativePathfinder.stop()
  MovementManager.setMovementLock(false)   // calls clearForcedMovement()
  RotationExecutor.stopRotating()
  DianaParticleTracker.reset()
  state = State.IDLE
  burrowPos = null
  targetEntityId = -1
  pathfindingTicksElapsed = 0
  collectTicksElapsed = 0
  digTicksElapsed = 0
  waitTicksElapsed = 0
}
```

Called from `onDisable()` and from the null-guard at the top of each state's tick.

## Files Modified / Created

| File | Action |
|---|---|
| `src/main/kotlin/org/phantom/internal/diana/DianaParticleTracker.kt` | Create |
| `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt` | Create |
| `src/main/kotlin/org/phantom/api/util/player/MovementManager.kt` | Modify |
| `src/main/java/org/phantom/mixin/client/KeyMappingMixin.java` | Modify |
| `src/main/kotlin/org/phantom/Phantom.kt` | Modify (register module) |
