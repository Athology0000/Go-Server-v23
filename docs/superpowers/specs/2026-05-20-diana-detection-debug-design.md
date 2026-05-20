# Diana Detection Debug Instrumentation

**Date:** 2026-05-20
**Scope:** Add a mod-wide debug toggle, then instrument Diana macro's burrow/mob detection signals with debug-only output. No macro behavior changes in this spec — instrumentation only.

## Motivation

Diana macro detection currently has four failure modes:

1. Burrow false-finish — macro advances on tick count without physical confirmation
2. Burrow missed-finish — `"You dug out a Griffin Burrow!"` chat missed → swings until `digTimeout`
3. Mob false-kill — target lost via despawn / chunk flicker counted as kill
4. Mob missed-kill — kill takes ~12 reacquire ticks to register

User-supplied chat logs surface additional Hypixel signals we currently ignore:

- `[Exclamation]! You dug out a [DianaMob]!` — definitive mob-spawn line (variable exclamations: `Woah!`, `Oi!`, `Uh oh!`, etc.)
- `Wow! You dug out N coins!` — treasure burrow finished, no mob
- `RARE DROP! You dug out a [Item]!` — treasure burrow finished, no mob
- `RARE DROP! [Item] (+N ✯ Magic Find)` — mob killed (Magic Find suffix disambiguates from burrow rare drops)
- `Couldn't find an appropriate burrow, try again!` — spade activation failed
- `entity.deathTime > 0` (per-tick state, not chat) — mob entered death animation

Before wiring these into the state machine, we instrument them and confirm they fire as expected in-game.

## Design

### Part 1 — Mod-wide debug toggle (`DebugModule`)

The codebase already has per-module debug:

- Every `Module` auto-includes a `debugSetting`
- `module.debug { "..." }` routes through `ModuleDebug.log` → chat + slf4j
- `ModuleDebug.isEnabled(moduleName)` checks the per-module flag

Missing piece: a single switch to light up debug across every module without toggling each one individually.

**New file:** `src/main/kotlin/org/phantom/internal/debug/DebugModule.kt`

```kotlin
object DebugModule : Module("Debug") {
    override val category = ModuleCategory.CORE
    val masterDebug = CheckboxSetting(
        "Master Debug",
        "Force every module's debug output on regardless of per-module debug toggle.",
        false,
    )
    init { addSetting(masterDebug) }
}
```

**One-line patch** to `ModuleDebug.isEnabled` (`api/module/ModuleDebug.kt`):

```kotlin
fun isEnabled(moduleName: String): Boolean {
    if (DebugModule.masterDebug.value) return true
    return ModuleManager.getModules().firstOrNull { it.name == moduleName }?.isDebugEnabled() == true
}
```

Registration: add `DebugModule` to `Cobalt.onInitializeClient()` alongside the other internal modules.

All existing `module.debug { ... }` call sites benefit immediately. No call-site changes elsewhere.

### Part 2 — Diana detection instrumentation

All changes are debug-only output. The macro state machine, chat handlers' existing transitions, and combat logic are unchanged in this spec.

**File:** `src/main/kotlin/org/phantom/internal/diana/DianaModule.kt`

Add a regex constant at module scope:

```kotlin
private val MOB_SPAWN_REGEX = Regex("^[A-Za-z ]+! You dug out a (.+)!$")
```

Extend `onBurrowDugChat(event)` so that, in addition to the existing transitions, it logs every recognised signal via `debug { ... }`:

| Signal | Debug line |
|---|---|
| `You dug out a Griffin Burrow!` | `burrow-dug chat: $msg` |
| `MOB_SPAWN_REGEX` matches AND `group(1)` ∈ `DIANA_MOB_NAMES` | `mob-spawn chat: $mobName` |
| `Wow! You dug out ` … ` coins!` | `treasure-confirmed (coins): $msg` |
| `RARE DROP! You dug out ` …`!` (NO `Magic Find` suffix) | `treasure-confirmed (rare burrow): $msg` |
| `RARE DROP! ` … `(+N ✯ Magic Find)` | `mob-kill (rare drop): $msg` |
| `Couldn't find an appropriate burrow, try again!` | `spade-activation failed chat` |

All new debug logging is placed at the **top** of `onBurrowDugChat`, before the existing `if (state != DIGGING && state != COMBAT) return` guard. That way debug fires in every state, while the existing state-transition logic stays gated as today.

In `onTick` COMBAT branch, immediately after `val target = ... ?: ...`:

```kotlin
val deathTime = target?.deathTime ?: 0
if (deathTime > 0) debug { "mob-kill (deathTime=$deathTime) entity=${target!!.id}" }
```

No state transition on `deathTime` yet — purely an observation log.

### Out of scope (future spec)

- Wiring `mob-spawn chat` into a `mobBurrowSpawned` flag that bypasses `POST_DUG_MOB_GRACE_TICKS`
- Wiring `treasure-confirmed` chats into immediate WAITING transition
- Wiring `deathTime > 0` and `mob-kill (rare drop)` chat into immediate kill counting
- `BlockChangeEvent`-based physical burrow break confirmation
- Per-subsystem debug categories (Diana, Pathfinder, etc.) — current scope is one global toggle

## Acceptance

1. `DebugModule` appears in the Core category with one Checkbox (`Master Debug`).
2. With `Master Debug` off and `Diana Macro` debug off → no debug chat from Diana.
3. With `Master Debug` on (regardless of per-module flags) → every Diana detection signal listed above produces a chat line of the form `[Phantom][Diana Macro] <signal text>`.
4. Macro behavior with all debug off is byte-identical to pre-change.
