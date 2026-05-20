# Diana Detection Debug Instrumentation — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a mod-wide `DebugModule` (single Master Debug toggle) and instrument all Diana macro burrow/mob detection signals so we can observe what Hypixel actually sends before changing any macro behavior.

**Architecture:** New singleton `DebugModule` exposes `masterDebug` (CheckboxSetting). A one-line patch to `ModuleDebug.isEnabled` makes every existing `module.debug { ... }` call across the mod fire when master is on, regardless of per-module flags. Then add `debug { ... }` calls at six Diana detection signal sites — five chat patterns in `DianaMacroModule.onBurrowDugChat` and one `deathTime > 0` check in the COMBAT tick branch. No macro state-machine changes.

**Tech Stack:** Kotlin, Fabric 1.21.11, Java 21, existing `ModuleDebug` / `Module.debug { }` infrastructure.

**Spec:** `docs/superpowers/specs/2026-05-20-diana-detection-debug-design.md`

**Testing:** This project has no unit tests (per CLAUDE.md). Each task ends with `./gradlew build` and a commit. In-game manual verification happens after the whole plan is done.

---

## File Structure

- **Create:** `src/main/kotlin/org/phantom/internal/debug/DebugModule.kt` — new singleton, owns the master toggle
- **Modify:** `src/main/kotlin/org/phantom/api/module/ModuleDebug.kt:13-15` — one-line global override
- **Modify:** `src/main/kotlin/org/phantom/internal/BuiltinModules.kt:124-205` — register `DebugModule` in `all()`
- **Modify:** `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt` — add `MOB_SPAWN_REGEX`, six `debug { }` call sites

---

## Task 1: Create `DebugModule`

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/debug/DebugModule.kt`

- [ ] **Step 1: Write the new file**

```kotlin
package org.phantom.internal.debug

import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting

/**
 * Mod-wide debug master switch. When `masterDebug` is on, every
 * `module.debug { ... }` call across the mod fires (chat + slf4j) regardless of
 * the per-module debug toggle. Wired into `ModuleDebug.isEnabled`.
 */
object DebugModule : Module("Debug") {
    override val category = ModuleCategory.CORE

    val masterDebug = CheckboxSetting(
        "Master Debug",
        "Force every module's debug output on regardless of per-module debug toggle.",
        false,
    )

    init {
        addSetting(masterDebug)
    }
}
```

- [ ] **Step 2: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL. `DebugModule.kt` compiles; no usages yet so no other files affected.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/debug/DebugModule.kt
git commit -m "feat(debug): add DebugModule with Master Debug toggle"
```

---

## Task 2: Wire `DebugModule.masterDebug` into `ModuleDebug.isEnabled`

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/module/ModuleDebug.kt:13-15`

- [ ] **Step 1: Edit `isEnabled`**

Replace:

```kotlin
  @JvmStatic
  fun isEnabled(moduleName: String): Boolean {
    return ModuleManager.getModules().firstOrNull { it.name == moduleName }?.isDebugEnabled() == true
  }
```

with:

```kotlin
  @JvmStatic
  fun isEnabled(moduleName: String): Boolean {
    if (org.phantom.internal.debug.DebugModule.masterDebug.value) return true
    return ModuleManager.getModules().firstOrNull { it.name == moduleName }?.isDebugEnabled() == true
  }
```

(Fully-qualified reference avoids an import cycle between `api/module/` and `internal/debug/`.)

- [ ] **Step 2: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/api/module/ModuleDebug.kt
git commit -m "feat(debug): honor DebugModule.masterDebug in ModuleDebug.isEnabled"
```

---

## Task 3: Register `DebugModule` in `BuiltinModules`

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/BuiltinModules.kt`

- [ ] **Step 1: Add the import**

Near the existing diana imports (`src\main\kotlin\org\phantom\internal\BuiltinModules.kt:22`), add:

```kotlin
import org.phantom.internal.debug.DebugModule
```

- [ ] **Step 2: Add to the `all()` list**

In the `all(): List<Module>` body (around line 124-205), add `DebugModule` to the "HUD / core UI" section near the top so it's grouped with core modules. After the line:

```kotlin
    // HUD / core UI
    WatermarkModule(),
    InventoryHudModule(),
    MiningHudModule,
```

insert before the next `// Pathfinding` comment:

```kotlin

    // Debug
    DebugModule,
```

so the section reads:

```kotlin
    // HUD / core UI
    WatermarkModule(),
    InventoryHudModule(),
    MiningHudModule,

    // Debug
    DebugModule,

    // Pathfinding
    PathfindingModule,
```

- [ ] **Step 3: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/BuiltinModules.kt
git commit -m "feat(debug): register DebugModule with the module manager"
```

---

## Task 4: Add `MOB_SPAWN_REGEX` constant to `DianaMacroModule`

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt` — add near the existing `DIANA_MOB_NAMES` constant (search for `private val DIANA_MOB_NAMES` to find it).

- [ ] **Step 1: Insert the regex constant**

Find the existing line:

```kotlin
    private val DIANA_MOB_NAMES = listOf(
```

Immediately above it (so the regex sits next to the mob-name list it's matched against), insert:

```kotlin
    // Matches Hypixel's mob-burrow spawn chat. The exclamation prefix varies
    // ("Woah! / Oi! / Uh oh! / Watch out! / ..."), so capture is keyed on the
    // suffix "You dug out a <name>!" and the caller validates name against
    // DIANA_MOB_NAMES to reject burrow rare-drop lines ("RARE DROP! You dug
    // out a Griffin Feather!") that share the same suffix.
    private val MOB_SPAWN_REGEX = Regex("""^[A-Za-z][A-Za-z ]*! You dug out a (.+)!$""")
```

- [ ] **Step 2: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL. The constant is added but unused yet — Kotlin will not warn on an unused `private val` in an `object`.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/diana/DianaModule.kt
git commit -m "chore(diana): add MOB_SPAWN_REGEX for mob-burrow chat detection"
```

---

## Task 5: Instrument `onBurrowDugChat` with debug logging for all detection signals

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt` — `onBurrowDugChat` is at approximately line 2984.

- [ ] **Step 1: Add debug calls at the top of `onBurrowDugChat`**

Locate:

```kotlin
    @SubscribeEvent
    fun onBurrowDugChat(event: ChatEvent.Receive) {
        if (state != State.DIGGING && state != State.COMBAT) return
        val msg = ChatFormatting.stripFormatting(event.message ?: return) ?: return
```

Replace with:

```kotlin
    @SubscribeEvent
    fun onBurrowDugChat(event: ChatEvent.Receive) {
        val msg = ChatFormatting.stripFormatting(event.message ?: return) ?: return

        // Debug-only observation. All new patterns fire here, BEFORE the
        // state guard, so we can confirm what Hypixel sends regardless of
        // the macro's current state. None of these change macro behavior.
        if (msg.startsWith("You dug out a Griffin Burrow!")) {
            debug { "burrow-dug chat: $msg" }
        }
        MOB_SPAWN_REGEX.matchEntire(msg)?.groups?.get(1)?.value?.let { name ->
            if (name in DIANA_MOB_NAMES) {
                debug { "mob-spawn chat: $name" }
            }
        }
        if (msg.startsWith("Wow! You dug out") && msg.endsWith("coins!")) {
            debug { "treasure-confirmed (coins): $msg" }
        }
        if (msg.startsWith("RARE DROP! You dug out") && !msg.contains("Magic Find")) {
            debug { "treasure-confirmed (rare burrow): $msg" }
        }
        if (msg.startsWith("RARE DROP!") && msg.contains("Magic Find")) {
            debug { "mob-kill (rare drop): $msg" }
        }
        if (msg == "Couldn't find an appropriate burrow, try again!") {
            debug { "spade-activation failed chat" }
        }

        if (state != State.DIGGING && state != State.COMBAT) return
```

The rest of the function (defender chat, dig-confirmed, etc.) is unchanged.

- [ ] **Step 2: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/diana/DianaModule.kt
git commit -m "feat(diana): debug log every burrow/mob/treasure/spade chat signal"
```

---

## Task 6: Instrument COMBAT branch with `deathTime > 0` debug

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt` — COMBAT branch of `onTick`, near line 2844.

- [ ] **Step 1: Add the deathTime debug log**

Locate:

```kotlin
                val target = (level.getEntity(targetEntityId) as? LivingEntity)
                    ?.takeIf { it.isAlive }
                    ?: if (combatIsRareMob) {
                        findRareDianaMob(rareMobFocusPos ?: player.position(), mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value)
                    } else {
                        burrowPos?.let { findDianaMob(it, mobSearchRangeSetting.value) }
                    }
                if (target == null) {
```

Insert a per-tick debug check between the `target` resolution and the `if (target == null)` guard. The `isAlive` filter already on `?.takeIf { it.isAlive }` drops dead entities — to observe death-animation start we need the raw lookup separately. Replace the block above with:

```kotlin
                val rawTarget = level.getEntity(targetEntityId) as? LivingEntity
                if (rawTarget != null && rawTarget.deathTime > 0) {
                    debug { "mob-kill (deathTime=${rawTarget.deathTime}) entity=${rawTarget.id}" }
                }
                val target = rawTarget
                    ?.takeIf { it.isAlive }
                    ?: if (combatIsRareMob) {
                        findRareDianaMob(rareMobFocusPos ?: player.position(), mobSearchRangeSetting.value + nearbyBurrowRadiusSetting.value)
                    } else {
                        burrowPos?.let { findDianaMob(it, mobSearchRangeSetting.value) }
                    }
                if (target == null) {
```

Behavior is unchanged — `target` resolves to the same value as before (a non-null `LivingEntity.isAlive` or the fallback search). The new lines only observe.

- [ ] **Step 2: Build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/diana/DianaModule.kt
git commit -m "feat(diana): debug log mob target deathTime during combat"
```

---

## Task 7: Final build + smoke

**Files:** none (verification only)

- [ ] **Step 1: Clean build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL. No warnings related to the new code.

- [ ] **Step 2: Verify byte-identical default behavior**

Confirm the new `DebugModule.masterDebug` default is `false` (Task 1, Step 1) and `DianaMacroModule.isDebugEnabled()` default is `false` (unchanged, inherited from `Module`). With both off, every new `debug { }` call must short-circuit inside `ModuleDebug.log` before chat or slf4j fire — i.e., macro chat output is unchanged from pre-plan.

Visually inspect the diff `git log --stat -7` to confirm:
- One new file (`DebugModule.kt`)
- Four modified files (`ModuleDebug.kt`, `BuiltinModules.kt`, `DianaModule.kt`)
- No unrelated changes

- [ ] **Step 3: Manual in-game verification (post-merge, out of scope for the engineer)**

Defer this to whoever runs the mod. The checklist for the user:
1. Open Cobalt UI → Core category → Debug → toggle `Master Debug` on
2. Run Diana Macro through one mob burrow + one treasure burrow + one failed spade
3. Confirm chat shows lines like:
   - `[Phantom][Diana Macro] burrow-dug chat: You dug out a Griffin Burrow! (1/8)`
   - `[Phantom][Diana Macro] mob-spawn chat: Minos Hunter`
   - `[Phantom][Diana Macro] mob-kill (deathTime=1) entity=12345`
   - `[Phantom][Diana Macro] treasure-confirmed (coins): Wow! You dug out 10,000 coins!`
   - `[Phantom][Diana Macro] spade-activation failed chat`
4. Toggle `Master Debug` off → all those lines stop.

No engineer step here — the engineer's job ends after Task 7 Step 2.
