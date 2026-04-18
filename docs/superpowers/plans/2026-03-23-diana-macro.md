# Diana Macro Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a fully automated Diana event macro that triangulates burrow positions from CRIT particles, pathfinds to them, digs them, kills the mob, waits, then loops.

**Architecture:** `DianaParticleTracker` (thread-safe particle collector + triangulator) + `DianaModule` (state machine driving NativePathfinder and key simulation). Forced attack/use keys are wired into the existing `MovementManager`/`KeyMappingMixin` infrastructure.

**Tech Stack:** Kotlin, Fabric MC 1.21.11, NativePathfinder JNI, OverlayRenderEngine, BezierTrackingRotationStrategy, `Collections.synchronizedList`

**Spec:** `docs/superpowers/specs/2026-03-23-diana-macro-design.md`

**Build command** (no unit tests in this project — all verification is compile-only):
```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

---

## File Map

| File | Action |
|---|---|
| `src/main/kotlin/org/cobalt/api/util/player/MovementManager.kt` | Modify — add `forcedAttack`, `forcedUse` |
| `src/main/java/org/cobalt/mixin/client/KeyMappingMixin.java` | Modify — wire new flags for keyAttack/keyUse |
| `src/main/kotlin/org/cobalt/internal/diana/DianaParticleTracker.kt` | Create |
| `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt` | Create |
| `src/main/kotlin/org/cobalt/Cobalt.kt` | Modify — register DianaModule |

---

## Task 1: Add forcedAttack and forcedUse to MovementManager and KeyMappingMixin

**Files:**
- Modify: `src/main/kotlin/org/cobalt/api/util/player/MovementManager.kt`
- Modify: `src/main/java/org/cobalt/mixin/client/KeyMappingMixin.java`

- [ ] **Step 1: Add `forcedAttack` and `forcedUse` fields to `MovementManager.kt`**

  Open `src/main/kotlin/org/cobalt/api/util/player/MovementManager.kt`.

  After the `forcedSprint` field (line 43), add:
  ```kotlin
  @JvmField
  @Volatile
  var forcedAttack = false

  @JvmField
  @Volatile
  var forcedUse = false
  ```

  At the bottom of `clearForcedMovement()`, before the closing `}`, add:
  ```kotlin
  forcedAttack = false
  forcedUse    = false
  ```

- [ ] **Step 2: Wire the new flags in `KeyMappingMixin.java`**

  Open `src/main/java/org/cobalt/mixin/client/KeyMappingMixin.java`.

  Find and **replace** the existing combined block (currently the last block in the method):
  ```java
  if (self == mc.options.keyAttack || self == mc.options.keyUse) {
    cir.setReturnValue(false);
  }
  ```

  Replace it with:
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

- [ ] **Step 3: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL` with no errors.

- [ ] **Step 4: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/api/util/player/MovementManager.kt
  git add src/main/java/org/cobalt/mixin/client/KeyMappingMixin.java
  git commit -m "feat: add forcedAttack and forcedUse to MovementManager and KeyMappingMixin"
  ```

---

## Task 2: Create DianaParticleTracker

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/diana/DianaParticleTracker.kt`

- [ ] **Step 1: Create the file**

  ```kotlin
  package org.cobalt.internal.diana

  import net.minecraft.world.level.Level
  import net.minecraft.world.level.levelgen.Heightmap
  import net.minecraft.world.phys.Vec3
  import java.util.Collections

  /**
   * Thread-safe collector of Diana CRIT particle positions.
   * addParticle() is called from the netty thread; all other methods from the tick thread.
   */
  object DianaParticleTracker {

    private val particles: MutableList<Vec3> = Collections.synchronizedList(mutableListOf())

    fun reset() {
      synchronized(particles) { particles.clear() }
    }

    /** Called from the netty thread — Collections.synchronizedList makes add() thread-safe. */
    fun addParticle(x: Double, y: Double, z: Double) {
      particles.add(Vec3(x, y, z))
    }

    /** Compound operation — must synchronize explicitly. */
    fun count(): Int = synchronized(particles) { particles.size }

    /**
     * Averages normalized direction vectors from [playerPos] to each collected particle,
     * extends the ray 40 blocks forward, then snaps Y to the ground heightmap.
     * Returns null if there are too few particles or the average direction is degenerate.
     */
    fun getBurrowPos(playerPos: Vec3, level: Level): Vec3? {
      val snapshot: List<Vec3>
      synchronized(particles) { snapshot = particles.toList() }
      if (snapshot.size < 2) return null  // defence-in-depth; caller checks count() >= minParticles

      val dirs = snapshot.map { it.subtract(playerPos).normalize() }
      val avgDir = dirs.fold(Vec3.ZERO) { acc, v -> acc.add(v) }.scale(1.0 / dirs.size)
      if (avgDir.lengthSqr() < 1e-6) return null
      val dir = avgDir.normalize()

      val estX = playerPos.x + dir.x * 40.0
      val estZ = playerPos.z + dir.z * 40.0
      val groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, estX.toInt(), estZ.toInt()).toDouble()
      return Vec3(estX, groundY, estZ)
    }
  }
  ```

- [ ] **Step 2: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaParticleTracker.kt
  git commit -m "feat: add DianaParticleTracker — thread-safe CRIT particle collector and triangulator"
  ```

---

## Task 3: DianaModule skeleton — state enum, settings, fields, cleanup, registration

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Create `DianaModule.kt` with the skeleton**

  ```kotlin
  package org.cobalt.internal.diana

  import net.minecraft.client.Minecraft
  import net.minecraft.world.phys.Vec3
  import org.cobalt.api.event.EventBus
  import org.cobalt.api.event.annotation.SubscribeEvent
  import org.cobalt.api.event.impl.client.PacketEvent
  import org.cobalt.api.event.impl.client.TickEvent
  import org.cobalt.api.event.impl.render.WorldRenderEvent
  import org.cobalt.api.module.Module
  import org.cobalt.api.module.setting.impl.CheckboxSetting
  import org.cobalt.api.module.setting.impl.SliderSetting
  import org.cobalt.api.pathfinder.jni.NativePathfinder
  import org.cobalt.api.pathfinder.jni.PathStatus
  import org.cobalt.api.rotation.RotationExecutor
  import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
  import org.cobalt.api.util.AngleUtils
  import org.cobalt.api.util.ChatUtils
  import org.cobalt.api.util.player.MovementManager
  import org.cobalt.internal.pathfinding.OverlayRenderEngine
  import org.cobalt.internal.rotation.RotationsModule
  import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
  import net.minecraft.core.particles.ParticleTypes
  import net.minecraft.world.entity.LivingEntity
  import net.minecraft.world.phys.AABB

  object DianaModule : Module("Diana") {

    private val mc = Minecraft.getInstance()

    // ── State ─────────────────────────────────────────────────────────────────

    private enum class State {
      IDLE, ACTIVATING_SPADE, COLLECTING_PARTICLES, PATHFINDING, DIGGING, COMBAT, WAITING
    }

    private var state = State.IDLE
    private var wasEnabled = false

    // Per-state counters
    private var collectTicksElapsed    = 0
    private var pathfindingTicksElapsed = 0
    private var digTicksElapsed        = 0
    private var waitTicksElapsed       = 0

    // Inter-state data
    private var activationPlayerPos: Vec3? = null
    private var burrowPos: Vec3?           = null
    private var targetEntityId: Int        = -1

    // ── Settings ──────────────────────────────────────────────────────────────

    private val enabledSetting = CheckboxSetting("Enabled", "Start or stop the Diana macro.", false)

    private val spadeSlotSetting = SliderSetting(
      "Spade Slot", "Hotbar slot of the Ancestral Spade (1–9).", 1.0, 1.0, 9.0, step = 1.0
    )
    private val weaponSlotSetting = SliderSetting(
      "Weapon Slot", "Hotbar slot of your main weapon (1–9).", 2.0, 1.0, 9.0, step = 1.0
    )
    private val collectDurationSetting = SliderSetting(
      "Collect Duration", "Ticks to gather CRIT particles before triangulating.", 60.0, 20.0, 120.0, step = 1.0
    )
    private val postKillWaitSetting = SliderSetting(
      "Post-Kill Wait", "Ticks to wait at the kill location before looping.", 80.0, 20.0, 200.0, step = 1.0
    )
    private val minParticlesSetting = SliderSetting(
      "Min Particles", "Minimum CRIT particles required to trust triangulation.", 8.0, 3.0, 30.0, step = 1.0
    )

    // Convenience accessors (0-indexed slot)
    private val spadeSlot  get() = spadeSlotSetting.value.toInt() - 1
    private val weaponSlot get() = weaponSlotSetting.value.toInt() - 1

    // ── Constants ─────────────────────────────────────────────────────────────

    private val DIANA_MOB_NAMES = listOf(
      "Minotaur", "Minos Hunter", "Minos Champion", "Gaia Construct", "Minos Inquisitor"
    )

    // ── Rotation ──────────────────────────────────────────────────────────────

    private const val COMBAT_ROTATION_STEP_SCALE = 0.62

    private val rotationStrategy = BezierTrackingRotationStrategy(
      yawStepSampler   = { (RotationsModule.sample(RotationsModule.combatYawStep.value)   * COMBAT_ROTATION_STEP_SCALE).toFloat() },
      pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
      curveInProvider  = { RotationsModule.bezierCurveIn.value.toFloat() },
      curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
      minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
      // snapThreshold retains default 0.25f — matches CombatMacroModule behaviour
    )

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
      addSetting(
        enabledSetting, spadeSlotSetting, weaponSlotSetting,
        collectDurationSetting, postKillWaitSetting, minParticlesSetting,
      )
      EventBus.register(this)
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────

    private fun cleanup() {
      NativePathfinder.stop()
      MovementManager.setMovementLock(false)   // also calls clearForcedMovement()
      RotationExecutor.stopRotating()
      DianaParticleTracker.reset()
      state = State.IDLE
      burrowPos = null
      activationPlayerPos = null
      targetEntityId = -1
      pathfindingTicksElapsed = 0
      collectTicksElapsed = 0
      digTicksElapsed = 0
      waitTicksElapsed = 0
    }

    private fun start() { /* nothing needed on start */ }
    private fun stop()  { cleanup() }

    // ── Tick stub (states implemented in later tasks) ─────────────────────────

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
      if (!enabledSetting.value) {
        if (wasEnabled) stop()
        wasEnabled = false
        return
      }
      if (!wasEnabled) start()
      wasEnabled = true

      val player = mc.player ?: run { stop(); wasEnabled = false; return }
      val level  = mc.level  ?: run { stop(); wasEnabled = false; return }

      // States implemented in Tasks 4–9
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Incoming) {
      // Particle collection implemented in Task 5
    }

    @SubscribeEvent
    fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
      // Rendering implemented in Task 9
    }
  }
  ```

- [ ] **Step 2: Register DianaModule in `Cobalt.kt`**

  Open `src/main/kotlin/org/cobalt/Cobalt.kt`.

  Add the import near the other `diana` imports (or with other internal imports):
  ```kotlin
  import org.cobalt.internal.diana.DianaModule
  ```

  In `ModuleManager.addModules(listOf(...))`, find the last line `ChatFilterModule` (no trailing comma) and replace it with:
  ```kotlin
  ChatFilterModule,
  DianaModule,
  ```
  i.e. `ChatFilterModule` needs a comma added and `DianaModule,` added on the next line.

- [ ] **Step 3: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git add src/main/kotlin/org/cobalt/Cobalt.kt
  git commit -m "feat: add DianaModule skeleton with settings, state enum, cleanup, and registration"
  ```

---

## Task 4: Implement IDLE and ACTIVATING_SPADE states

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Replace the `onTick` state dispatch stub with IDLE and ACTIVATING_SPADE**

  Find the comment `// States implemented in Tasks 4–9` inside `onTick` and replace it with:

  ```kotlin
  when (state) {
    State.IDLE -> {
      player.inventory.selected = spadeSlot
      MovementManager.setMovementLock(true)
      // setForcedMovement sets hasForcedMovement = true (required for forcedUse to fire in KeyMappingMixin)
      MovementManager.setForcedMovement(false, false, false, false, false, false, false)
      MovementManager.forcedUse = true
      state = State.ACTIVATING_SPADE
    }

    State.ACTIVATING_SPADE -> {
      // Hold use key for exactly 1 tick (set in IDLE), then release
      MovementManager.forcedUse = false
      DianaParticleTracker.reset()
      activationPlayerPos = player.position()
      collectTicksElapsed = 0
      state = State.COLLECTING_PARTICLES
    }

    State.COLLECTING_PARTICLES -> { /* Task 5 */ }
    State.PATHFINDING          -> { /* Task 6 */ }
    State.DIGGING              -> { /* Task 7 */ }
    State.COMBAT               -> { /* Task 8 */ }
    State.WAITING              -> { /* Task 9 */ }
  }
  ```

- [ ] **Step 2: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana IDLE and ACTIVATING_SPADE states"
  ```

---

## Task 5: Implement COLLECTING_PARTICLES state and packet handler

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Implement the `onPacket` handler**

  Find `fun onPacket(event: PacketEvent.Incoming)` and replace its body:

  ```kotlin
  @SubscribeEvent
  fun onPacket(event: PacketEvent.Incoming) {
    if (state != State.COLLECTING_PARTICLES) return
    val pkt = event.packet as? ClientboundLevelParticlesPacket ?: return
    if (pkt.particle.type != ParticleTypes.CRIT) return
    DianaParticleTracker.addParticle(pkt.x, pkt.y, pkt.z)
  }
  ```

- [ ] **Step 2: Implement the COLLECTING_PARTICLES branch in `onTick`**

  Replace `State.COLLECTING_PARTICLES -> { /* Task 5 */ }` with:

  ```kotlin
  State.COLLECTING_PARTICLES -> {
    collectTicksElapsed++
    if (collectTicksElapsed < collectDurationSetting.value.toInt()) return

    // Collection window done — attempt triangulation
    if (DianaParticleTracker.count() < minParticlesSetting.value.toInt()) {
      // Too few particles — retry from IDLE (movement stays locked, IDLE re-locks next tick)
      state = State.IDLE
      return
    }
    val pos = DianaParticleTracker.getBurrowPos(activationPlayerPos ?: player.position(), level)
    if (pos == null) {
      state = State.IDLE
      return
    }
    burrowPos = pos
    pathfindingTicksElapsed = 0
    state = State.PATHFINDING
  }
  ```

- [ ] **Step 3: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana COLLECTING_PARTICLES state and CRIT particle packet handler"
  ```

---

## Task 6: Implement PATHFINDING state

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Implement the PATHFINDING branch**

  Replace `State.PATHFINDING -> { /* Task 6 */ }` with:

  ```kotlin
  State.PATHFINDING -> {
    val bp = burrowPos ?: run { state = State.IDLE; return }

    // First tick in PATHFINDING: send target to pathfinder
    if (pathfindingTicksElapsed == 0) {
      NativePathfinder.setTarget(bp.x, bp.y, bp.z)
    }
    pathfindingTicksElapsed++

    // Timeout guard
    if (pathfindingTicksElapsed > 300) {
      NativePathfinder.stop()
      MovementManager.setMovementLock(false)
      ChatUtils.sendMessage("Diana: pathfinding timed out, retrying.")
      state = State.IDLE
      return
    }

    val cmd = NativePathfinder.tick()
    if (cmd != null) {
      // EXECUTING / RECOVERING / REPLANNING — apply movement command
      cmd.applyToPlayer()
    } else {
      // IDLE, PLANNING, ARRIVED, or FAILED
      when (NativePathfinder.status) {
        PathStatus.ARRIVED -> {
          player.inventory.selected = spadeSlot
          digTicksElapsed = 0
          state = State.DIGGING
        }
        PathStatus.FAILED -> {
          NativePathfinder.stop()
          MovementManager.setMovementLock(false)
          ChatUtils.sendMessage("Diana: pathfinding failed, retrying.")
          state = State.IDLE
        }
        PathStatus.IDLE -> {
          NativePathfinder.stop()
          MovementManager.setMovementLock(false)
          state = State.IDLE
        }
        PathStatus.PLANNING -> {
          // Normal planning phase — clear stale forced flags to prevent jump spam
          MovementManager.clearForcedMovement()
        }
        else -> { /* REPLANNING, RECOVERING, EXECUTING handled via cmd != null above */ }
      }
    }

    // Also check distance for early arrival (catches cases where ARRIVED status is missed)
    if (state == State.PATHFINDING && player.position().distanceTo(bp) <= 2.0) {
      NativePathfinder.stop()
      player.inventory.selected = spadeSlot
      digTicksElapsed = 0
      state = State.DIGGING
    }
  }
  ```

- [ ] **Step 2: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana PATHFINDING state"
  ```

---

## Task 7: Implement DIGGING state

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Implement the DIGGING branch**

  Replace `State.DIGGING -> { /* Task 7 */ }` with:

  ```kotlin
  State.DIGGING -> {
    val bp = burrowPos ?: run { state = State.IDLE; return }

    // Hold right-click every tick (setForcedMovement also ensures hasForcedMovement = true)
    MovementManager.setForcedMovement(false, false, false, false, false, false, false)
    MovementManager.forcedUse = true

    digTicksElapsed++

    // Scan for a Diana mob near the burrow (DIANA_MOB_NAMES is a module-level constant — see Task 3 skeleton)
    val mob = level.getEntitiesOfClass(
      LivingEntity::class.java,
      AABB(bp, bp).inflate(6.0)
    ).firstOrNull { e ->
      val name = e.displayName.string
      DIANA_MOB_NAMES.any { n -> name.contains(n, ignoreCase = true) }
    }

    if (mob != null) {
      MovementManager.forcedUse = false
      targetEntityId = mob.id
      player.inventory.selected = weaponSlot
      state = State.COMBAT
      return
    }

    if (digTicksElapsed >= 60) {
      MovementManager.forcedUse = false
      ChatUtils.sendMessage("Diana: no mob spawned after digging, retrying.")
      state = State.IDLE
    }
  }
  ```

- [ ] **Step 2: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana DIGGING state"
  ```

---

## Task 8: Implement COMBAT state

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Implement the COMBAT branch**

  Replace `State.COMBAT -> { /* Task 8 */ }` with:

  ```kotlin
  State.COMBAT -> {
    // Clear flag set last tick (latch pattern: set at end of tick, clear at start of next)
    MovementManager.forcedAttack = false

    val target = level.getEntity(targetEntityId)
    if (target == null || (target as? LivingEntity)?.isAlive != true) {
      waitTicksElapsed = 0
      state = State.WAITING
      return
    }

    // Re-check player (disconnect race — AngleUtils.getRotation dereferences player!!)
    mc.player ?: run { cleanup(); return }

    // Aim at mob
    RotationExecutor.rotateTo(AngleUtils.getRotation(target), rotationStrategy)

    // Ensure hasForcedMovement is true so forcedAttack fires in KeyMappingMixin
    MovementManager.setForcedMovement(false, false, false, false, false, false, false)

    // Set attack flag — will be polled by game's handleKeybinds() later this tick
    MovementManager.forcedAttack = true
  }
  ```

- [ ] **Step 2: Build to verify**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana COMBAT state with BezierTrackingRotationStrategy and forcedAttack latch"
  ```

---

## Task 9: Implement WAITING state and burrow rendering

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt`

- [ ] **Step 1: Implement the WAITING branch**

  Replace `State.WAITING -> { /* Task 9 */ }` with:

  ```kotlin
  State.WAITING -> {
    // Unlock movement on first tick of WAITING (clearForcedMovement called internally)
    if (waitTicksElapsed == 0) {
      MovementManager.setMovementLock(false)
    }
    waitTicksElapsed++
    if (waitTicksElapsed >= postKillWaitSetting.value.toInt()) {
      DianaParticleTracker.reset()
      state = State.IDLE
    }
  }
  ```

- [ ] **Step 2: Implement the `onRender` handler for the burrow box**

  Replace the `onRender` stub with:

  ```kotlin
  @SubscribeEvent
  fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    if (state !in listOf(State.PATHFINDING, State.DIGGING, State.COMBAT)) return
    val level = mc.level ?: return
    val bp = burrowPos ?: return
    OverlayRenderEngine.addBox(
      level,
      bp.x - 0.5, bp.y, bp.z - 0.5,
      bp.x + 0.5, bp.y + 1.0, bp.z + 0.5,
      fill    = OverlayRenderEngine.Color(255, 220, 0, 60),
      outline = OverlayRenderEngine.Color(255, 220, 0, 255),
      durationTicks = 10,
      tag = "diana-burrow"
    )
  }
  ```

- [ ] **Step 3: Build final verification**

  ```bash
  ./gradlew build
  ```
  Expected: `BUILD SUCCESSFUL` with only warnings (no errors).

- [ ] **Step 4: Commit**

  ```bash
  git add src/main/kotlin/org/cobalt/internal/diana/DianaModule.kt
  git commit -m "feat: implement Diana WAITING state and burrow box rendering — Diana macro complete"
  ```
