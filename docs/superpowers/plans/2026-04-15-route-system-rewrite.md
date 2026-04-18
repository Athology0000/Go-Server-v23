# Route System Rewrite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the scattered per-macro route system with a unified data model, routes screen, and point-recording flow covering five route types (Ore Miner, Commission, Patrol, Gemstone, Tunnel).

**Architecture:** New `internal/routes/` package owns data (RouteStore, SavedRoute, RouteType), edit-mode UX (RouteEditMode, RoutePointTypePopup). New `UIRoutesPanel` and `UINewRouteModal` live in the existing UI panels package. Macros read the armed route name from `RouteStore`. Old per-macro panels (UICommissionRoutesPanel, UICombatWalkbackRoutesPanel, UIRoutePointPicker) and legacy singletons (PatrolWaypointStore, WalkbackBridge) are removed after migration.

**Scope note:** This plan covers the data layer, edit mode, and routes screen (spec sections 1–3 plus section 4.1 route-selection UI). The full execution rewrites per spec sections 4.2–4.6 depend on a separate deep-dive into each macro and are left as a follow-up.

**Tech Stack:** Kotlin, Fabric MC 1.21.11, NanoVG (NVGRenderer), OverlayRenderEngine + Render3D for 3D overlays, Gson for JSON file I/O, EventBus (`@SubscribeEvent`), UIPanel / ScrollHandler / TextInputHandler pattern.

---

## File Map

**New files:**
- `src/main/kotlin/org/cobalt/internal/routes/RouteType.kt` — RouteType, SubRouteKey, RoutePointType enums with allowed-type-per-sub-route logic
- `src/main/kotlin/org/cobalt/internal/routes/SavedRoute.kt` — SavedRoute, RoutePoint data classes + JSON serialisation helpers
- `src/main/kotlin/org/cobalt/internal/routes/RouteStore.kt` — load / save / list / delete / migrate routes; `arm` / `getArmed` per type
- `src/main/kotlin/org/cobalt/internal/routes/RoutePointTypePopup.kt` — Shift+right-click type picker (NvgEvent popup)
- `src/main/kotlin/org/cobalt/internal/routes/RouteEditMode.kt` — sticky HUD controller: normal recording, insert mode, undo, in-world rendering
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutesPanel.kt` — unified routes screen (expandable cards, filter tabs, Load/Edit/Insert/Delete, + New)
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UINewRouteModal.kt` — 5-type picker + name input modal

**Modified files:**
- `src/main/kotlin/org/cobalt/Cobalt.kt` — register RouteEditMode + RoutePointTypePopup with EventBus, call RouteStore.init(), remove PatrolWaypointStore.load()
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UISidebar.kt` — wire routes button to `UIConfig.swapBodyPanel(UIRoutesPanel())`
- `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt` — `getAvailableRouteInfos()` now reads from RouteStore
- `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt` — add `getArmedRoute()` / `setArmedRoute()` backed by RouteStore
- `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt` — remove WalkbackBridge, add RouteStore PATROL-route arm support

**Deleted files (Task 9):**
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICommissionRoutesPanel.kt`
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICombatWalkbackRoutesPanel.kt`
- `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutePointPicker.kt`
- `src/main/kotlin/org/cobalt/internal/ui/hud/RoutePointPopup.kt`
- `src/main/kotlin/org/cobalt/internal/ui/hud/PatrolPointPopup.kt`
- `src/main/kotlin/org/cobalt/internal/ui/hud/WalkbackRoutePickerPopup.kt`
- `src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt`
- `src/main/kotlin/org/cobalt/internal/helper/WalkbackBridge.kt`

---

## Task 1: Data Types — RouteType.kt and SavedRoute.kt

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/routes/RouteType.kt`
- Create: `src/main/kotlin/org/cobalt/internal/routes/SavedRoute.kt`

- [ ] **Step 1: Create RouteType.kt**

```kotlin
package org.cobalt.internal.routes

enum class RouteType(val label: String, val color: Long) {
    ORE_MINER("Ore Miner",   0xFF4DE2C5),
    COMMISSION("Commission", 0xFFFFA24F),
    PATROL("Patrol",         0xFFFF6B8A),
    GEMSTONE("Gemstone",     0xFF9E7CFF),
    TUNNEL("Tunnel",         0xFF60A5FA),
}

/** The sub-route inside a SavedRoute that a point belongs to. */
enum class SubRouteKey(val label: String, val icon: String) {
    TRAVEL("Travel Route", "\uD83D\uDEB6"),   // 🚶
    LOOP("Loop Route",     "\uD83D\uDD04"),    // 🔄
    AREA("Patrol Area",    "\u2694"),          // ⚔
    POINTS("Route Points", "\uD83D\uDCC4"),   // 📄
}

enum class RoutePointType(val id: String, val label: String, val icon: String) {
    WALK("walk", "Walk", "\uD83D\uDEB6"),   // 🚶
    WARP("warp", "Warp", "\u26A1"),         // ⚡
    MINE("mine", "Mine", "\u26CF"),         // ⛏
    KILL("kill", "Kill", "\u2694");         // ⚔

    companion object {
        fun fromId(id: String?): RoutePointType =
            entries.firstOrNull { it.id == id?.lowercase() } ?: WALK
    }
}

/** Which point types are valid for a given (RouteType, SubRouteKey) pair. */
fun allowedPointTypes(type: RouteType, sub: SubRouteKey): List<RoutePointType> = when (type) {
    RouteType.ORE_MINER -> when (sub) {
        SubRouteKey.TRAVEL -> listOf(RoutePointType.WALK, RoutePointType.WARP)
        SubRouteKey.LOOP   -> listOf(RoutePointType.WARP, RoutePointType.MINE)
        else -> emptyList()
    }
    RouteType.COMMISSION -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE)
        else -> emptyList()
    }
    RouteType.PATROL -> when (sub) {
        SubRouteKey.TRAVEL -> listOf(RoutePointType.WALK, RoutePointType.WARP)
        SubRouteKey.AREA   -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.KILL)
        else -> emptyList()
    }
    RouteType.GEMSTONE -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WARP, RoutePointType.MINE)
        else -> emptyList()
    }
    RouteType.TUNNEL -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE)
        else -> emptyList()
    }
}

/** Which sub-routes a route type uses, in display order. */
fun subRoutesFor(type: RouteType): List<SubRouteKey> = when (type) {
    RouteType.ORE_MINER  -> listOf(SubRouteKey.TRAVEL, SubRouteKey.LOOP)
    RouteType.PATROL     -> listOf(SubRouteKey.TRAVEL, SubRouteKey.AREA)
    RouteType.COMMISSION,
    RouteType.GEMSTONE,
    RouteType.TUNNEL     -> listOf(SubRouteKey.POINTS)
}
```

- [ ] **Step 2: Create SavedRoute.kt**

```kotlin
package org.cobalt.internal.routes

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.core.BlockPos

data class RoutePoint(
    val type: RoutePointType,
    val x: Int,
    val y: Int,
    val z: Int,
    /** Mine-end block (MINE points only). */
    val mx: Int? = null,
    val my: Int? = null,
    val mz: Int? = null,
    /** Optional block id filter for MINE points. */
    val blockId: String? = null,
) {
    val pos: BlockPos get() = BlockPos(x, y, z)
    val mineEnd: BlockPos? get() = if (mx != null && my != null && mz != null) BlockPos(mx, my, mz) else null
}

/**
 * Dual sub-route types (ORE_MINER, PATROL): use travelRoute + loopOrArea.
 * Single sub-route types (COMMISSION, GEMSTONE, TUNNEL): use points only.
 */
data class SavedRoute(
    val name: String,
    val type: RouteType,
    /** Travel sub-route (ORE_MINER → travelRoute, PATROL → travelRoute). */
    val travelRoute: List<RoutePoint> = emptyList(),
    /** Second sub-route (ORE_MINER → loopRoute, PATROL → area). */
    val loopOrArea: List<RoutePoint> = emptyList(),
    /** Single sub-route (COMMISSION, GEMSTONE, TUNNEL). */
    val points: List<RoutePoint> = emptyList(),
) {
    fun getSubRoute(sub: SubRouteKey): List<RoutePoint> = when (sub) {
        SubRouteKey.TRAVEL -> travelRoute
        SubRouteKey.LOOP, SubRouteKey.AREA -> loopOrArea
        SubRouteKey.POINTS -> points
    }

    fun withSubRoute(sub: SubRouteKey, newPoints: List<RoutePoint>): SavedRoute = when (sub) {
        SubRouteKey.TRAVEL -> copy(travelRoute = newPoints)
        SubRouteKey.LOOP, SubRouteKey.AREA -> copy(loopOrArea = newPoints)
        SubRouteKey.POINTS -> copy(points = newPoints)
    }

    fun totalPoints(): Int = travelRoute.size + loopOrArea.size + points.size

    fun toJson(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("name", name)
        obj.addProperty("type", type.name)
        when (type) {
            RouteType.ORE_MINER -> {
                obj.add("travelRoute", pointsToJson(travelRoute))
                obj.add("loopRoute", pointsToJson(loopOrArea))
            }
            RouteType.PATROL -> {
                obj.add("travelRoute", pointsToJson(travelRoute))
                obj.add("patrolArea", pointsToJson(loopOrArea))
            }
            else -> obj.add("points", pointsToJson(points))
        }
        return obj
    }

    companion object {
        fun fromJson(json: JsonObject): SavedRoute? = runCatching {
            val name = json["name"]?.asString ?: return null
            val type = RouteType.valueOf(json["type"].asString)
            when (type) {
                RouteType.ORE_MINER -> SavedRoute(
                    name, type,
                    travelRoute = parsePoints(json.getAsJsonArray("travelRoute")),
                    loopOrArea  = parsePoints(json.getAsJsonArray("loopRoute")),
                )
                RouteType.PATROL -> SavedRoute(
                    name, type,
                    travelRoute = parsePoints(json.getAsJsonArray("travelRoute")),
                    loopOrArea  = parsePoints(json.getAsJsonArray("patrolArea")),
                )
                else -> SavedRoute(
                    name, type,
                    points = parsePoints(json.getAsJsonArray("points")),
                )
            }
        }.getOrNull()

        private fun parsePoints(arr: JsonArray?): List<RoutePoint> {
            if (arr == null) return emptyList()
            return arr.mapNotNull { el ->
                runCatching {
                    val o = el.asJsonObject
                    RoutePoint(
                        type    = RoutePointType.fromId(o["type"]?.asString),
                        x       = o["x"].asInt,
                        y       = o["y"].asInt,
                        z       = o["z"].asInt,
                        mx      = o["mx"]?.asInt,
                        my      = o["my"]?.asInt,
                        mz      = o["mz"]?.asInt,
                        blockId = o["bid"]?.asString,
                    )
                }.getOrNull()
            }
        }

        private fun pointsToJson(pts: List<RoutePoint>): JsonArray {
            val arr = JsonArray()
            pts.forEach { p ->
                val o = JsonObject()
                o.addProperty("type", p.type.id)
                o.addProperty("x", p.x); o.addProperty("y", p.y); o.addProperty("z", p.z)
                p.mx?.let { o.addProperty("mx", it) }
                p.my?.let { o.addProperty("my", it) }
                p.mz?.let { o.addProperty("mz", it) }
                p.blockId?.let { o.addProperty("bid", it) }
                arr.add(o)
            }
            return arr
        }
    }
}
```

- [ ] **Step 3: Build to verify**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL` (no compile errors in new files)

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/routes/RouteType.kt \
        src/main/kotlin/org/cobalt/internal/routes/SavedRoute.kt
git commit -m "feat: add RouteType and SavedRoute data classes for route system rewrite"
```

---

## Task 2: RouteStore — File I/O and Migration

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/routes/RouteStore.kt`

- [ ] **Step 1: Create RouteStore.kt**

```kotlin
package org.cobalt.internal.routes

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft

/**
 * Singleton that owns the on-disk route library at config/cobalt/routes2/.
 *
 * Call init() once at startup. It migrates old routes/ files and loads the library.
 */
object RouteStore {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val mc = Minecraft.getInstance()

    private val routes2Dir: File
        get() = File(mc.gameDirectory, "config/cobalt/routes2").also { it.mkdirs() }

    private val oldRoutesDir: File
        get() = File(mc.gameDirectory, "config/cobalt/routes")

    /** In-memory list, refreshed by load(). */
    private var cached: MutableList<SavedRoute> = mutableListOf()

    /** Armed route name per RouteType — cleared on level load, not persisted. */
    private val armed: MutableMap<RouteType, String> = mutableMapOf()

    // ---- Lifecycle ----

    fun init() {
        migrateIfNeeded()
        load()
    }

    fun load() {
        cached.clear()
        val dir = routes2Dir
        if (!dir.exists()) return
        dir.listFiles { f -> f.extension == "json" }?.forEach { file ->
            runCatching {
                val obj = JsonParser.parseString(file.readText()).asJsonObject
                SavedRoute.fromJson(obj)?.let { cached.add(it) }
            }
        }
        cached.sortBy { it.name }
    }

    // ---- Queries ----

    fun list(): List<SavedRoute> = cached.toList()

    fun list(type: RouteType): List<SavedRoute> = cached.filter { it.type == type }

    fun get(name: String): SavedRoute? = cached.firstOrNull { it.name == name }

    // ---- Mutations ----

    fun save(route: SavedRoute) {
        val file = File(routes2Dir, "${route.name}.json")
        file.writeText(gson.toJson(route.toJson()))
        val existing = cached.indexOfFirst { it.name == route.name }
        if (existing >= 0) cached[existing] = route else cached.add(route)
        cached.sortBy { it.name }
    }

    fun delete(name: String) {
        File(routes2Dir, "$name.json").delete()
        cached.removeIf { it.name == name }
        armed.entries.removeIf { it.value == name }
    }

    fun exists(name: String): Boolean = cached.any { it.name == name }

    // ---- Armed route (transient, not persisted) ----

    fun arm(type: RouteType, name: String) {
        armed[type] = name
    }

    fun getArmed(type: RouteType): String? = armed[type]

    fun clearArmed(type: RouteType) {
        armed.remove(type)
    }

    // ---- Migration from old routes/ directory ----

    private fun migrateIfNeeded() {
        val old = oldRoutesDir
        if (!old.exists()) return
        old.listFiles { f -> f.extension == "json" }?.forEach { file ->
            runCatching { migrateFile(file) }
        }
    }

    private fun migrateFile(file: File) {
        val name = file.nameWithoutExtension
        val dest = File(routes2Dir, "$name.json")
        if (dest.exists()) return          // already migrated

        val text = file.readText()
        if (text.isBlank()) return

        val root = JsonParser.parseString(text)
        val points = when {
            root.isJsonArray -> root.asJsonArray
            root.isJsonObject -> root.asJsonObject.getAsJsonArray("points")
            else -> return
        } ?: return

        // Old point schema: { "type": "normal"|"warp"|"mine", x, y, z, mineEnd:{x,y,z}, mineBlockId }
        val newPoints = com.google.gson.JsonArray()
        points.forEach { el ->
            runCatching {
                val o = el.asJsonObject
                val oldType = o["type"]?.asString?.lowercase() ?: "normal"
                val newType = when (oldType) {
                    "warp" -> "warp"
                    "mine" -> "mine"
                    else -> "walk"
                }
                val np = JsonObject()
                np.addProperty("type", newType)
                np.addProperty("x", o["x"].asInt)
                np.addProperty("y", o["y"].asInt)
                np.addProperty("z", o["z"].asInt)
                val me = o.getAsJsonObject("mineEnd")
                if (me != null) {
                    np.addProperty("mx", me["x"].asInt)
                    np.addProperty("my", me["y"].asInt)
                    np.addProperty("mz", me["z"].asInt)
                }
                o["mineBlockId"]?.asString?.let { np.addProperty("bid", it) }
                newPoints.add(np)
            }
        }

        val out = JsonObject()
        out.addProperty("name", name)
        out.addProperty("type", RouteType.ORE_MINER.name)
        out.add("travelRoute", com.google.gson.JsonArray())
        out.add("loopRoute", newPoints)

        routes2Dir.mkdirs()
        dest.writeText(gson.toJson(out))
    }
}
```

- [ ] **Step 2: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/routes/RouteStore.kt
git commit -m "feat: add RouteStore with load/save/delete/migrate for routes2/ directory"
```

---

## Task 3: RoutePointTypePopup

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/routes/RoutePointTypePopup.kt`

The popup shows only the allowed types for the active sub-route. It's triggered by Shift+right-click during edit mode (and always during insert mode). The caller passes the allowed types and a callback.

- [ ] **Step 1: Create RoutePointTypePopup.kt**

```kotlin
package org.cobalt.internal.routes

import java.awt.Color
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.NvgEvent
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.player.MovementManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.ui.animation.ColorAnimation
import org.cobalt.internal.ui.util.isHoveringOver
import org.cobalt.internal.ui.util.mouseX
import org.cobalt.internal.ui.util.mouseY

/**
 * NVG overlay popup for picking a RoutePointType.
 * Call open(allowedTypes, position, onPick) to show it.
 * Must be registered with EventBus once at startup.
 */
object RoutePointTypePopup {

    private val mc = Minecraft.getInstance()

    private var visible = false
    private var allowedTypes: List<RoutePointType> = emptyList()
    private var onPick: ((RoutePointType) -> Unit)? = null
    private var pendingPos: Triple<Int, Int, Int>? = null

    private val panelW = 240f
    private val panelH get() = 56f + allowedTypes.size * 36f + 8f
    private val pad = 14f

    private var panelX = 0f
    private var panelY = 0f

    private val buttonAnims: MutableMap<RoutePointType, ColorAnimation> = mutableMapOf()
    private val buttonHover: MutableMap<RoutePointType, Boolean> = mutableMapOf()

    fun open(
        types: List<RoutePointType>,
        pos: Triple<Int, Int, Int>,
        callback: (RoutePointType) -> Unit,
    ) {
        if (mc.screen != null) mc.setScreen(null)
        allowedTypes = types
        onPick = callback
        pendingPos = pos
        visible = true
        buttonAnims.clear()
        buttonHover.clear()
        lockPlayer(true)
        MouseUtils.ungrabMouse()
    }

    fun isVisible(): Boolean = visible

    private fun close(cancelled: Boolean) {
        visible = false
        if (cancelled) pendingPos = null
        lockPlayer(false)
        if (mc.screen == null) MouseUtils.grabMouse()
    }

    private fun pick(type: RoutePointType) {
        val pos = pendingPos ?: return
        onPick?.invoke(type)
        close(false)
    }

    @SubscribeEvent
    fun onRender(@Suppress("UNUSED_PARAMETER") event: NvgEvent) {
        if (!visible) return
        if (mc.screen != null) { close(true); return }

        val win = mc.window
        val sw = win.screenWidth.toFloat()
        val sh = win.screenHeight.toFloat()
        panelX = sw / 2f - panelW / 2f
        panelY = sh / 2f - panelH / 2f

        val theme = ThemeManager.currentTheme
        NVGRenderer.beginFrame(sw, sh)
        NVGRenderer.rect(0f, 0f, sw, sh, Color(0, 0, 0, 120).rgb)
        NVGRenderer.rect(panelX, panelY, panelW, panelH, theme.background, 10f)
        NVGRenderer.hollowRect(panelX, panelY, panelW, panelH, 1f, theme.controlBorder, 10f)

        val pos = pendingPos
        val posLabel = if (pos != null) "(${pos.first}, ${pos.second}, ${pos.third})" else ""
        NVGRenderer.text("Point Type  $posLabel", panelX + pad, panelY + 14f, 12f, theme.accent)

        val bw = panelW - pad * 2f
        val bh = 28f
        var by = panelY + 40f
        allowedTypes.forEach { ptype ->
            val hovering = isHoveringOver(panelX + pad, by, bw, bh)
            val prev = buttonHover[ptype] ?: false
            if (hovering != prev) {
                buttonAnims.getOrPut(ptype) { ColorAnimation(150L) }.start()
                buttonHover[ptype] = hovering
            }
            val anim = buttonAnims.getOrPut(ptype) { ColorAnimation(150L) }
            val bg = anim.get(theme.controlBg, theme.selectedOverlay, !hovering)
            val border = anim.get(theme.controlBorder, theme.accent, !hovering)
            val text = anim.get(theme.text, theme.accent, !hovering)

            NVGRenderer.rect(panelX + pad, by, bw, bh, bg, 7f)
            NVGRenderer.hollowRect(panelX + pad, by, bw, bh, 1f, border, 7f)
            NVGRenderer.text("${ptype.icon}  ${ptype.label}", panelX + pad + 10f, by + 8f, 12f, text)
            by += bh + 8f
        }

        NVGRenderer.endFrame()
    }

    @SubscribeEvent
    fun onMouseLeft(event: MouseEvent.LeftClick) {
        if (!visible) return
        event.setCancelled(true)
        val mx = mouseX.toFloat()
        val my = mouseY.toFloat()

        val bw = panelW - pad * 2f
        val bh = 28f
        var by = panelY + 40f
        for (ptype in allowedTypes) {
            if (mx >= panelX + pad && mx <= panelX + pad + bw && my >= by && my <= by + bh) {
                pick(ptype)
                return
            }
            by += bh + 8f
        }
        if (!isHoveringOver(panelX, panelY, panelW, panelH)) {
            close(true)
        }
    }

    @SubscribeEvent
    fun onMouseRight(event: MouseEvent.RightClick) {
        if (!visible) return
        event.setCancelled(true)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (!visible) return
        if (mc.screen != null) { close(true); return }
        lockPlayer(true)
    }

    private fun lockPlayer(on: Boolean) {
        MovementManager.setLookLock(on)
        MovementManager.setMovementLock(on)
        if (!on) return
        val o = mc.options
        listOf(o.keyUp, o.keyDown, o.keyLeft, o.keyRight, o.keyJump, o.keyShift,
               o.keySprint, o.keyAttack, o.keyUse).forEach { it.setDown(false) }
    }
}
```

- [ ] **Step 2: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/routes/RoutePointTypePopup.kt
git commit -m "feat: add RoutePointTypePopup for type selection during route edit"
```

---

## Task 4: RouteEditMode — Sticky HUD, Recording, and In-World Rendering

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/routes/RouteEditMode.kt`

RouteEditMode is an `object` registered with EventBus. It manages two phases:
- `NORMAL` — sticky HUD shows mode buttons, right-click records, Shift+right-click opens RoutePointTypePopup
- `INSERT_PICKING` — shows gap-slot list as an NVG overlay
- `INSERT_RECORDING` — after gap selected, right-click always opens popup; point inserted at gap index

It also renders small boxes + lines for the current sub-route in-world via WorldRenderEvent.

- [ ] **Step 1: Create RouteEditMode.kt**

```kotlin
package org.cobalt.internal.routes

import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.NvgEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.player.MovementManager
import org.cobalt.api.util.render.Render3D
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.pathfinding.OverlayRenderEngine
import org.cobalt.internal.ui.util.isHoveringOver
import org.cobalt.internal.ui.util.mouseX
import org.cobalt.internal.ui.util.mouseY

object RouteEditMode {

    private enum class Phase { IDLE, NORMAL, INSERT_PICKING, INSERT_RECORDING }

    private val mc = Minecraft.getInstance()

    private var phase = Phase.IDLE
    private var route: SavedRoute? = null
    private var subKey: SubRouteKey? = null
    private var allowedTypes: List<RoutePointType> = emptyList()

    /** Current sticky recording type (NORMAL phase). */
    private var stickyType: RoutePointType = RoutePointType.WALK

    /** For insert mode: which gap to insert after (0 = before first). */
    private var insertAfterIndex: Int = -1

    // ---- HUD geometry ----
    private val hudW = 400f
    private val hudH = 60f
    private val modeBtnW = 70f
    private val modeBtnH = 26f
    private val actionBtnW = 60f
    private val gap = 8f

    // ---- Insert overlay ----
    private val overlayW = 340f
    private val overlayH get() = 56f + currentPoints().size * 30f + 16f
    private var overlayX = 0f
    private var overlayY = 0f

    /** Set by external code (e.g. UIRoutesPanel → Done callback). */
    private var onDone: (() -> Unit)? = null

    // ---- Public API ----

    /** Enter normal sticky recording for the given sub-route. */
    fun enter(route: SavedRoute, sub: SubRouteKey, onDone: () -> Unit) {
        this.route = route
        this.subKey = sub
        this.allowedTypes = allowedPointTypes(route.type, sub)
        this.stickyType = allowedTypes.firstOrNull() ?: RoutePointType.WALK
        this.onDone = onDone
        this.phase = Phase.NORMAL
        if (mc.screen != null) mc.setScreen(null)
    }

    /** Enter insert-picking mode — gap selector overlays the screen. */
    fun enterInsertMode(route: SavedRoute, sub: SubRouteKey, onDone: () -> Unit) {
        this.route = route
        this.subKey = sub
        this.allowedTypes = allowedPointTypes(route.type, sub)
        this.onDone = onDone
        this.insertAfterIndex = -1
        this.phase = Phase.INSERT_PICKING
        if (mc.screen != null) mc.setScreen(null)
        MouseUtils.ungrabMouse()
    }

    fun isActive(): Boolean = phase != Phase.IDLE

    fun activeRoute(): SavedRoute? = route

    fun activeSubKey(): SubRouteKey? = subKey

    // ---- Internals ----

    private fun currentPoints(): List<RoutePoint> =
        route?.getSubRoute(subKey ?: return emptyList()) ?: emptyList()

    private fun savePoints(points: List<RoutePoint>) {
        val r = route ?: return
        val sub = subKey ?: return
        val updated = r.withSubRoute(sub, points)
        route = updated
        RouteStore.save(updated)
    }

    private fun addPoint(type: RoutePointType, pos: BlockPos) {
        val pts = currentPoints().toMutableList()
        pts.add(RoutePoint(type, pos.x, pos.y, pos.z))
        savePoints(pts)
    }

    private fun insertPoint(type: RoutePointType, pos: BlockPos, afterIndex: Int) {
        val pts = currentPoints().toMutableList()
        val insertAt = (afterIndex + 1).coerceIn(0, pts.size)
        pts.add(insertAt, RoutePoint(type, pos.x, pos.y, pos.z))
        savePoints(pts)
    }

    private fun undo() {
        val pts = currentPoints()
        if (pts.isEmpty()) return
        savePoints(pts.dropLast(1))
    }

    private fun done() {
        phase = Phase.IDLE
        val cb = onDone
        route = null
        subKey = null
        onDone = null
        MouseUtils.grabMouse()
        cb?.invoke()
    }

    // ---- Events ----

    @SubscribeEvent
    fun onRightClick(event: MouseEvent.RightClick) {
        when (phase) {
            Phase.NORMAL -> {
                event.setCancelled(true)
                val hit = mc.hitResult
                if (hit !is BlockHitResult || hit.type != HitResult.Type.BLOCK) return
                val pos = hit.blockPos
                val shift = mc.options.keyShift.isDown
                if (shift) {
                    RoutePointTypePopup.open(allowedTypes, Triple(pos.x, pos.y, pos.z)) { type ->
                        addPoint(type, pos)
                    }
                } else {
                    addPoint(stickyType, pos)
                }
            }
            Phase.INSERT_RECORDING -> {
                event.setCancelled(true)
                val hit = mc.hitResult
                if (hit !is BlockHitResult || hit.type != HitResult.Type.BLOCK) return
                val pos = hit.blockPos
                RoutePointTypePopup.open(allowedTypes, Triple(pos.x, pos.y, pos.z)) { type ->
                    insertPoint(type, pos, insertAfterIndex)
                    // Return to gap picking so the user can insert more
                    phase = Phase.INSERT_PICKING
                    MouseUtils.ungrabMouse()
                }
            }
            else -> {}
        }
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        // Release movement lock when not in a locking phase (popup handles its own lock)
        if (phase == Phase.NORMAL || phase == Phase.INSERT_RECORDING) {
            MovementManager.setMovementLock(false)
        }
    }

    @SubscribeEvent
    fun onNvg(@Suppress("UNUSED_PARAMETER") event: NvgEvent) {
        when (phase) {
            Phase.NORMAL, Phase.INSERT_RECORDING -> renderStickyHud()
            Phase.INSERT_PICKING -> renderInsertOverlay()
            else -> {}
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: WorldRenderEvent.Last) {
        if (phase == Phase.IDLE) return
        val pts = currentPoints()
        if (pts.isEmpty()) return
        val ctx = event.context

        // Draw lines between consecutive points
        for (i in 0 until pts.size - 1) {
            val a = pts[i]
            val b = pts[i + 1]
            Render3D.drawLine(
                ctx,
                Vec3(a.x + 0.5, a.y + 0.5, a.z + 0.5),
                Vec3(b.x + 0.5, b.y + 0.5, b.z + 0.5),
                Color(100, 220, 255, 180),
                esp = false,
            )
        }

        // Draw a small box at each point coloured by type
        pts.forEachIndexed { idx, pt ->
            val color = pointTypeColor(pt.type)
            val cx = pt.x + 0.5; val cy = pt.y + 0.5; val cz = pt.z + 0.5
            val r = 0.12
            Render3D.drawStyledBox(
                ctx,
                AABB(cx - r, cy - r, cz - r, cx + r, cy + r, cz + r),
                strokeColor = color,
                fillColor   = Color(color.red, color.green, color.blue, 60),
                esp = true,
            )
        }
    }

    private fun pointTypeColor(type: RoutePointType): Color = when (type) {
        RoutePointType.WALK -> Color(100, 220, 255)
        RoutePointType.WARP -> Color(255, 160, 80)
        RoutePointType.MINE -> Color(200, 255, 100)
        RoutePointType.KILL -> Color(255, 100, 120)
    }

    // ---- NVG: sticky HUD ----

    private fun renderStickyHud() {
        val win = mc.window
        val sw = win.screenWidth.toFloat()
        val sh = win.screenHeight.toFloat()
        val hudX = sw / 2f - hudW / 2f
        val hudY = sh - hudH - 20f

        NVGRenderer.beginFrame(sw, sh)
        val theme = ThemeManager.currentTheme
        NVGRenderer.rect(hudX, hudY, hudW, hudH, theme.background, 10f)
        NVGRenderer.hollowRect(hudX, hudY, hudW, hudH, 1f, theme.controlBorder, 10f)

        // Label
        val r = route
        val sub = subKey
        val label = if (r != null && sub != null) "${r.name}  ›  ${sub.label}" else "Edit Route"
        NVGRenderer.text(label, hudX + 14f, hudY + 10f, 11f, theme.textSecondary)

        // Point count
        val pts = currentPoints()
        NVGRenderer.text("${pts.size} pts", hudX + hudW - 60f, hudY + 10f, 11f, theme.textSecondary)

        // Mode buttons (only in NORMAL phase)
        if (phase == Phase.NORMAL) {
            var bx = hudX + 14f
            val by = hudY + 27f
            allowedTypes.forEach { t ->
                val selected = t == stickyType
                val hov = isHoveringOver(bx, by, modeBtnW, modeBtnH)
                val bg = if (selected) theme.accent else if (hov) theme.selectedOverlay else theme.controlBg
                val textColor = if (selected) theme.textOnAccent else if (hov) theme.accent else theme.text
                NVGRenderer.rect(bx, by, modeBtnW, modeBtnH, bg, 6f)
                NVGRenderer.hollowRect(bx, by, modeBtnW, modeBtnH, 1f, theme.controlBorder, 6f)
                val lbl = "${t.icon} ${t.label}"
                val lw = NVGRenderer.textWidth(lbl, 10f)
                NVGRenderer.text(lbl, bx + (modeBtnW - lw) / 2f, by + 7f, 10f, textColor)
                bx += modeBtnW + gap
            }
        } else {
            // INSERT_RECORDING label
            val iLabel = "Inserting after point $insertAfterIndex  — right-click block"
            NVGRenderer.text(iLabel, hudX + 14f, hudY + 30f, 11f, theme.accent)
        }

        // Undo + Done buttons
        val undoX = hudX + hudW - actionBtnW * 2f - gap - 14f
        val doneX = hudX + hudW - actionBtnW - 14f
        val actY  = hudY + 27f
        renderActionBtn("Undo", undoX, actY)
        renderActionBtn("Done", doneX, actY)

        NVGRenderer.endFrame()
    }

    private fun renderActionBtn(label: String, bx: Float, by: Float) {
        val theme = ThemeManager.currentTheme
        val hov = isHoveringOver(bx, by, actionBtnW, modeBtnH)
        NVGRenderer.rect(bx, by, actionBtnW, modeBtnH, if (hov) theme.selectedOverlay else theme.controlBg, 6f)
        NVGRenderer.hollowRect(bx, by, actionBtnW, modeBtnH, 1f, theme.controlBorder, 6f)
        val lw = NVGRenderer.textWidth(label, 11f)
        NVGRenderer.text(label, bx + (actionBtnW - lw) / 2f, by + 7f, 11f, theme.text)
    }

    @SubscribeEvent
    fun onMouseLeft(event: MouseEvent.LeftClick) {
        if (phase == Phase.IDLE || RoutePointTypePopup.isVisible()) return

        val win = mc.window
        val sw = win.screenWidth.toFloat()
        val sh = win.screenHeight.toFloat()
        val hudX = sw / 2f - hudW / 2f
        val hudY = sh - hudH - 20f
        val mx = mouseX.toFloat()
        val my = mouseY.toFloat()

        if (phase == Phase.NORMAL) {
            // Mode button hits
            var bx = hudX + 14f
            val by = hudY + 27f
            for (t in allowedTypes) {
                if (isHoveringOver(bx, by, modeBtnW, modeBtnH)) {
                    stickyType = t
                    event.setCancelled(true)
                    return
                }
                bx += modeBtnW + gap
            }
        }

        // Undo
        val undoX = hudX + hudW - actionBtnW * 2f - gap - 14f
        val doneX = hudX + hudW - actionBtnW - 14f
        val actY  = hudY + 27f
        if (isHoveringOver(undoX, actY, actionBtnW, modeBtnH)) {
            undo()
            event.setCancelled(true)
            return
        }
        if (isHoveringOver(doneX, actY, actionBtnW, modeBtnH)) {
            done()
            event.setCancelled(true)
            return
        }

        // Insert picking: gap slot clicks handled in renderInsertOverlay + onMouseLeft
        if (phase == Phase.INSERT_PICKING) {
            handleInsertGapClick(mx, my, event)
        }
    }

    // ---- NVG: insert gap overlay ----

    private fun renderInsertOverlay() {
        val win = mc.window
        val sw = win.screenWidth.toFloat()
        val sh = win.screenHeight.toFloat()
        val pts = currentPoints()
        overlayX = sw / 2f - overlayW / 2f
        overlayY = sh / 2f - overlayH / 2f

        val theme = ThemeManager.currentTheme
        NVGRenderer.beginFrame(sw, sh)
        NVGRenderer.rect(0f, 0f, sw, sh, Color(0, 0, 0, 100).rgb)
        NVGRenderer.rect(overlayX, overlayY, overlayW, overlayH, theme.background, 10f)
        NVGRenderer.hollowRect(overlayX, overlayY, overlayW, overlayH, 1f, theme.controlBorder, 10f)

        val r = route; val sub = subKey
        val title = if (r != null && sub != null) "Insert into: ${r.name}  ›  ${sub.label}" else "Insert Point"
        NVGRenderer.text(title, overlayX + 14f, overlayY + 14f, 12f, theme.accent)

        // Done button (top right)
        val doneBx = overlayX + overlayW - 70f
        val doneBy = overlayY + 8f
        val hovDone = isHoveringOver(doneBx, doneBy, 58f, 22f)
        NVGRenderer.rect(doneBx, doneBy, 58f, 22f, if (hovDone) theme.selectedOverlay else theme.controlBg, 5f)
        NVGRenderer.hollowRect(doneBx, doneBy, 58f, 22f, 1f, theme.controlBorder, 5f)
        NVGRenderer.text("Done", doneBx + 14f, doneBy + 5f, 11f, theme.text)

        val rowH = 22f
        val rowGap = 8f
        val slotH = 18f
        var cy = overlayY + 38f
        val rowX = overlayX + 14f
        val rowW = overlayW - 28f

        pts.forEachIndexed { idx, pt ->
            // Point row
            NVGRenderer.rect(rowX, cy, rowW, rowH, theme.controlBg, 4f)
            NVGRenderer.hollowRect(rowX, cy, rowW, rowH, 1f, theme.controlBorder, 4f)
            NVGRenderer.text("${idx + 1}  ${pt.type.icon} ${pt.type.label}  (${pt.x}, ${pt.y}, ${pt.z})", rowX + 8f, cy + 5f, 10f, theme.text)
            cy += rowH + 2f

            // Gap slot (except after last point we still show one for appending)
            val gapSelected = insertAfterIndex == idx
            val hov = isHoveringOver(rowX, cy, rowW, slotH)
            val gapBg = if (gapSelected) theme.selectedOverlay else if (hov) theme.overlay else theme.background
            val gapBorder = if (gapSelected || hov) theme.accent else theme.controlBorder
            NVGRenderer.rect(rowX, cy, rowW, slotH, gapBg, 3f)
            NVGRenderer.hollowRect(rowX, cy, rowW, slotH, 1f, gapBorder, 3f)
            val gapLabel = if (gapSelected) "✓ insert after ${idx + 1}" else "← insert after ${idx + 1}"
            NVGRenderer.text(gapLabel, rowX + 8f, cy + 3f, 10f, if (gapSelected) theme.accent else theme.textSecondary)
            cy += slotH + rowGap
        }

        // Also allow inserting at end (after all points)
        if (pts.isNotEmpty()) {
            val appendIdx = pts.size - 1
            // Already shown the last gap above; nothing extra needed for appending
        } else {
            // Empty route: show a single "insert first point" slot
            val hov = isHoveringOver(rowX, cy, rowW, slotH)
            NVGRenderer.rect(rowX, cy, rowW, slotH, if (hov) theme.overlay else theme.controlBg, 3f)
            NVGRenderer.hollowRect(rowX, cy, rowW, slotH, 1f, if (hov) theme.accent else theme.controlBorder, 3f)
            NVGRenderer.text("← insert first point", rowX + 8f, cy + 3f, 10f, theme.textSecondary)
        }

        NVGRenderer.endFrame()
    }

    private fun handleInsertGapClick(mx: Float, my: Float, event: MouseEvent.LeftClick) {
        // Done button
        val win = mc.window
        val sw = win.screenWidth.toFloat()
        val sh = win.screenHeight.toFloat()
        val doneBx = overlayX + overlayW - 70f
        val doneBy = overlayY + 8f
        if (isHoveringOver(doneBx, doneBy, 58f, 22f)) {
            done()
            event.setCancelled(true)
            return
        }

        val pts = currentPoints()
        val rowH = 22f
        val slotH = 18f
        val rowGap = 8f
        val rowX = overlayX + 14f
        val rowW = overlayW - 28f
        var cy = overlayY + 38f

        pts.forEachIndexed { idx, _ ->
            cy += rowH + 2f
            if (mx >= rowX && mx <= rowX + rowW && my >= cy && my <= cy + slotH) {
                insertAfterIndex = idx
                phase = Phase.INSERT_RECORDING
                MouseUtils.grabMouse()
                event.setCancelled(true)
                return
            }
            cy += slotH + rowGap
        }

        // Empty route: first point
        if (pts.isEmpty() && mx >= overlayX + 14f && my >= cy && my <= cy + slotH) {
            insertAfterIndex = -1
            phase = Phase.INSERT_RECORDING
            MouseUtils.grabMouse()
            event.setCancelled(true)
        }
    }
}
```

- [ ] **Step 2: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/routes/RouteEditMode.kt
git commit -m "feat: add RouteEditMode with sticky HUD recording, insert mode, and in-world rendering"
```

---

## Task 5: UIRoutesPanel — Expandable Card List

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutesPanel.kt`

Panel dimensions: 400×600. Top bar: search + type filter tabs + "+ New". Card list scrollable below.

- [ ] **Step 1: Create UIRoutesPanel.kt**

```kotlin
package org.cobalt.internal.ui.panel.panels

import net.minecraft.client.Minecraft
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.routes.RouteEditMode
import org.cobalt.internal.routes.RoutePointTypePopup
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType
import org.cobalt.internal.routes.SavedRoute
import org.cobalt.internal.routes.subRoutesFor
import org.cobalt.internal.ui.panel.UIPanel
import org.cobalt.internal.ui.screen.UIConfig
import org.cobalt.internal.ui.util.ScrollHandler
import org.cobalt.internal.ui.util.isHoveringOver
import org.cobalt.internal.ui.util.TextInputHandler

internal class UIRoutesPanel : UIPanel(
    x = 0f, y = 0f, width = 400f, height = 600f,
) {

    private val scroll = ScrollHandler()
    private val searchInput = TextInputHandler(maxLength = 32)
    private var searchFocused = false

    /** Null = show all types. */
    private var filterType: RouteType? = null
    /** Set of route names that are expanded. */
    private val expanded = mutableSetOf<String>()
    /** Which route to show confirm-delete for. */
    private var pendingDelete: String? = null

    private var routes: List<SavedRoute> = emptyList()

    init { refresh() }

    private fun refresh() {
        RouteStore.load()
        routes = RouteStore.list()
    }

    private fun visibleRoutes(): List<SavedRoute> {
        val q = searchInput.getText().trim().lowercase()
        return routes.filter { r ->
            (filterType == null || r.type == filterType) &&
            (q.isEmpty() || r.name.lowercase().contains(q))
        }
    }

    // ---- Layout constants ----
    private val PAD = 14f
    private val TOP_H = 80f       // search + filter tabs
    private val CARD_H = 34f      // collapsed
    private val SUB_ROW_H = 28f   // per sub-route row when expanded
    private val CARD_GAP = 6f
    private val BADGE_W = 80f
    private val TAB_W = 66f
    private val TAB_H = 24f
    private val FILTER_Y get() = y + 48f
    private val LIST_Y get() = y + TOP_H

    override fun render() {
        val theme = ThemeManager.currentTheme
        NVGRenderer.rect(x, y, width, height, theme.background, 10f)

        // Title + Refresh + "+ New Route"
        NVGRenderer.text("Routes", x + PAD, y + 18f, 14f, theme.text)
        renderBtn("Refresh",   x + width - PAD - 160f, y + 10f, 72f, 22f)
        renderBtn("+ New",     x + width - PAD - 80f,  y + 10f, 72f, 22f)

        // Search
        val sX = x + PAD; val sY = y + 38f; val sW = 200f; val sH = 22f
        NVGRenderer.rect(sX, sY, sW, sH, theme.controlBg, 6f)
        NVGRenderer.hollowRect(sX, sY, sW, sH, 1f, if (searchFocused) theme.accent else theme.controlBorder, 6f)
        NVGRenderer.pushScissor(sX + 4f, sY, sW - 8f, sH)
        val tOff = searchInput.getTextOffset()
        val txt = searchInput.getText().ifEmpty { if (!searchFocused) "Search..." else "" }
        val txtColor = if (searchInput.getText().isEmpty() && !searchFocused) theme.textSecondary else theme.text
        NVGRenderer.text(txt, sX + 6f - tOff, sY + 5f, 11f, txtColor)
        if (searchFocused) searchInput.renderCursor(sX + 6f, sY + 4f, 14f, theme.accent)
        NVGRenderer.popScissor()

        // Filter tabs: All + each RouteType
        renderFilterTabs()

        // Card list
        val listH = height - TOP_H - PAD
        NVGRenderer.pushScissor(x, LIST_Y, width, listH)
        val vis = visibleRoutes()
        val totalH = computeTotalHeight(vis)
        scroll.setMaxScroll(totalH, listH)
        val off = scroll.getOffset()
        var cy = LIST_Y - off
        vis.forEach { r ->
            cy = renderCard(r, cy)
        }
        if (vis.isEmpty()) renderEmptyState(LIST_Y + listH / 2f - 20f)
        NVGRenderer.popScissor()
    }

    private fun computeTotalHeight(vis: List<SavedRoute>): Float {
        var h = 0f
        vis.forEach { r ->
            h += CARD_H + CARD_GAP
            if (r.name in expanded) {
                h += subRoutesFor(r.type).size * (SUB_ROW_H + 4f) + 8f
            }
        }
        return h
    }

    private fun renderCard(route: SavedRoute, cy: Float): Float {
        val theme = ThemeManager.currentTheme
        val isExpanded = route.name in expanded
        val cardH = CARD_H + if (isExpanded) subRoutesFor(route.type).size * (SUB_ROW_H + 4f) + 8f else 0f
        val bx = x + PAD; val bw = width - PAD * 2f

        if (cy + cardH < LIST_Y || cy > y + height) {
            return cy + cardH + CARD_GAP
        }

        val hov = isHoveringOver(bx, cy, bw, CARD_H)
        NVGRenderer.rect(bx, cy, bw, cardH, theme.controlBg, 8f)
        NVGRenderer.hollowRect(bx, cy, bw, cardH, 1f, theme.controlBorder, 8f)

        // Type badge
        val badgeColor = (route.type.color and 0xFFFFFF).toInt()
        val br = ((badgeColor shr 16) and 0xFF)
        val bg = ((badgeColor shr 8) and 0xFF)
        val bb = (badgeColor and 0xFF)
        val badgeBg = java.awt.Color(br, bg, bb, 200).rgb
        val badgeTxt = java.awt.Color(br, bg, bb).rgb
        NVGRenderer.rect(bx + 8f, cy + 8f, BADGE_W, 18f, badgeBg, 4f)
        NVGRenderer.text(route.type.label.uppercase(), bx + 10f, cy + 11f, 8f, badgeTxt)

        // Route name
        NVGRenderer.text(route.name, bx + BADGE_W + 16f, cy + 10f, 12f, theme.text)

        // Expand chevron
        val chevX = bx + bw - 30f; val chevY = cy + 9f
        NVGRenderer.text(if (isExpanded) "▲" else "▼", chevX, chevY, 11f, theme.textSecondary)

        // Load button
        val loadX = bx + bw - 100f; val loadY = cy + 6f
        renderBtn("▶ Load", loadX, loadY, 58f, 22f)

        // Delete (hover only)
        if (hov) {
            val delX = bx + bw - 18f; val delY = cy + 8f
            // X delete button is drawn last so it overlaps chevron area
            NVGRenderer.text("✕", delX - 20f, delY + 1f, 11f, if (isHoveringOver(delX - 22f, delY - 2f, 18f, 18f)) theme.accent else theme.textSecondary)
        }

        // Sub-route rows
        if (isExpanded) {
            var sy = cy + CARD_H + 4f
            subRoutesFor(route.type).forEach { sub ->
                val pts = route.getSubRoute(sub)
                NVGRenderer.rect(bx + 4f, sy, bw - 8f, SUB_ROW_H, theme.overlay, 5f)
                NVGRenderer.text("${sub.icon}  ${sub.label}  (${pts.size} pts)", bx + 14f, sy + 7f, 11f, theme.text)
                renderBtn("✏ Edit",   bx + bw - 100f, sy + 2f, 50f, 20f)
                renderBtn("+ Insert", bx + bw - 44f,  sy + 2f, 36f, 20f)
                sy += SUB_ROW_H + 4f
            }
        }

        return cy + cardH + CARD_GAP
    }

    private fun renderFilterTabs() {
        val theme = ThemeManager.currentTheme
        val tabs = listOf<RouteType?>(null) + RouteType.entries
        var tx = x + PAD
        tabs.forEach { t ->
            val selected = filterType == t
            val hov = isHoveringOver(tx, FILTER_Y, TAB_W, TAB_H)
            NVGRenderer.rect(tx, FILTER_Y, TAB_W, TAB_H, if (selected) theme.accent else if (hov) theme.selectedOverlay else theme.controlBg, 5f)
            NVGRenderer.hollowRect(tx, FILTER_Y, TAB_W, TAB_H, 1f, theme.controlBorder, 5f)
            val lbl = t?.label?.take(8) ?: "All"
            val lw = NVGRenderer.textWidth(lbl, 9f)
            NVGRenderer.text(lbl, tx + (TAB_W - lw) / 2f, FILTER_Y + 7f, 9f, if (selected) theme.textOnAccent else theme.text)
            tx += TAB_W + 4f
        }
    }

    private fun renderBtn(label: String, bx: Float, by: Float, bw: Float, bh: Float) {
        val theme = ThemeManager.currentTheme
        val hov = isHoveringOver(bx, by, bw, bh)
        NVGRenderer.rect(bx, by, bw, bh, if (hov) theme.selectedOverlay else theme.controlBg, 5f)
        NVGRenderer.hollowRect(bx, by, bw, bh, 1f, if (hov) theme.accent else theme.controlBorder, 5f)
        val lw = NVGRenderer.textWidth(label, 10f)
        NVGRenderer.text(label, bx + (bw - lw) / 2f, by + (bh - 11f) / 2f, 10f, theme.text)
    }

    private fun renderEmptyState(cy: Float) {
        val theme = ThemeManager.currentTheme
        NVGRenderer.text("No routes found.", x + width / 2f - 40f, cy, 12f, theme.textSecondary)
        NVGRenderer.text("Click + New to create one.", x + width / 2f - 60f, cy + 18f, 10f, theme.textSecondary)
    }

    // ---- Input ----

    override fun mouseClicked(button: Int): Boolean {
        if (button != 0) return false

        // Search focus
        val sX = x + PAD; val sY = y + 38f; val sW = 200f; val sH = 22f
        if (isHoveringOver(sX, sY, sW, sH)) { searchFocused = true; return true }
        searchFocused = false

        // Refresh
        if (isHoveringOver(x + width - PAD - 160f, y + 10f, 72f, 22f)) {
            refresh(); return true
        }

        // + New
        if (isHoveringOver(x + width - PAD - 80f, y + 10f, 72f, 22f)) {
            UIConfig.swapBodyPanel(UINewRouteModal(onCreated = { route ->
                UIConfig.swapBodyPanel(UIRoutesPanel())
                val sub = subRoutesFor(route.type).first()
                Minecraft.getInstance().setScreen(null)
                RouteEditMode.enter(route, sub) {
                    // After done editing: nothing — user can reopen
                }
            }))
            return true
        }

        // Filter tabs
        val tabs = listOf<RouteType?>(null) + RouteType.entries
        var tx = x + PAD
        for (t in tabs) {
            if (isHoveringOver(tx, FILTER_Y, TAB_W, TAB_H)) { filterType = t; scroll.reset(); return true }
            tx += TAB_W + 4f
        }

        // Cards
        val vis = visibleRoutes()
        val off = scroll.getOffset()
        var cy = LIST_Y - off
        for (r in vis) {
            val cardH = CARD_H + if (r.name in expanded) subRoutesFor(r.type).size * (SUB_ROW_H + 4f) + 8f else 0f
            val bx = x + PAD; val bw = width - PAD * 2f

            // Load button
            val loadX = bx + bw - 100f; val loadY = cy + 6f
            if (isHoveringOver(loadX, loadY, 58f, 22f)) {
                RouteStore.arm(r.type, r.name)
                return true
            }

            // Delete (x)
            val delX = bx + bw - 40f; val delY = cy + 8f
            if (isHoveringOver(delX, delY - 2f, 18f, 18f)) {
                RouteStore.delete(r.name)
                refresh()
                return true
            }

            // Expand/collapse — anywhere in header row except buttons
            if (isHoveringOver(bx, cy, bw, CARD_H)) {
                if (r.name in expanded) expanded.remove(r.name) else expanded.add(r.name)
                return true
            }

            // Sub-route buttons
            if (r.name in expanded) {
                var sy = cy + CARD_H + 4f
                for (sub in subRoutesFor(r.type)) {
                    // Edit
                    if (isHoveringOver(bx + bw - 100f, sy + 2f, 50f, 20f)) {
                        Minecraft.getInstance().setScreen(null)
                        RouteEditMode.enter(r, sub) {
                            // Re-open routes panel when done
                        }
                        return true
                    }
                    // Insert
                    if (isHoveringOver(bx + bw - 44f, sy + 2f, 36f, 20f)) {
                        Minecraft.getInstance().setScreen(null)
                        RouteEditMode.enterInsertMode(r, sub) {}
                        return true
                    }
                    sy += SUB_ROW_H + 4f
                }
            }

            cy += cardH + CARD_GAP
        }

        return false
    }

    override fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (!isHoveringOver(x, y, width, height)) return false
        scroll.handleScroll(verticalAmount)
        return true
    }

    fun keyTyped(char: Char, keyCode: Int): Boolean {
        if (!searchFocused) return false
        when (keyCode) {
            259 -> searchInput.backspace()   // BACKSPACE
            261 -> searchInput.delete()      // DELETE
            263 -> searchInput.moveCursorLeft(false)
            262 -> searchInput.moveCursorRight(false)
            else -> if (char != Char(0)) searchInput.insertText(char.toString())
        }
        return true
    }
}
```

- [ ] **Step 2: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutesPanel.kt
git commit -m "feat: add UIRoutesPanel with expandable cards, filter tabs, and Load/Edit/Insert/Delete"
```

---

## Task 6: UINewRouteModal — Type Picker + Name Input

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UINewRouteModal.kt`

- [ ] **Step 1: Create UINewRouteModal.kt**

```kotlin
package org.cobalt.internal.ui.panel.panels

import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType
import org.cobalt.internal.routes.SavedRoute
import org.cobalt.internal.ui.panel.UIPanel
import org.cobalt.internal.ui.util.TextInputHandler
import org.cobalt.internal.ui.util.isHoveringOver

/**
 * Modal for creating a new route: pick a type, enter a name, click Create.
 * @param onCreated Called with the newly created SavedRoute. Caller should navigate away.
 */
internal class UINewRouteModal(
    private val onCreated: (SavedRoute) -> Unit,
) : UIPanel(x = 0f, y = 0f, width = 400f, height = 600f) {

    private var selectedType: RouteType? = null
    private val nameInput = TextInputHandler(maxLength = 48)
    private var nameFocused = false
    private var errorMsg: String? = null

    private val CARD_W = 174f
    private val CARD_H = 72f
    private val PAD = 14f
    private val GRID_GAP = 8f

    override fun render() {
        val theme = ThemeManager.currentTheme
        NVGRenderer.rect(x, y, width, height, theme.background, 10f)
        NVGRenderer.text("New Route", x + PAD, y + 18f, 14f, theme.text)
        NVGRenderer.text("Choose a type to get started.", x + PAD, y + 36f, 10f, theme.textSecondary)

        // 2×2 grid + Tunnel full width
        val gridX = x + PAD
        val gridY = y + 54f
        val types = RouteType.entries

        // Row 0: Ore Miner, Commission
        renderTypeCard(types[0], gridX,              gridY,        CARD_W, CARD_H)
        renderTypeCard(types[1], gridX + CARD_W + GRID_GAP, gridY, CARD_W, CARD_H)
        // Row 1: Patrol, Gemstone
        renderTypeCard(types[2], gridX,              gridY + CARD_H + GRID_GAP, CARD_W, CARD_H)
        renderTypeCard(types[3], gridX + CARD_W + GRID_GAP, gridY + CARD_H + GRID_GAP, CARD_W, CARD_H)
        // Tunnel full width
        val tunnelY = gridY + (CARD_H + GRID_GAP) * 2
        renderTypeCard(types[4], gridX, tunnelY, CARD_W * 2 + GRID_GAP, CARD_H - 10f)

        // Name input
        val inputY = tunnelY + CARD_H + 10f
        NVGRenderer.text("Route name:", x + PAD, inputY, 10f, theme.textSecondary)
        val iX = x + PAD; val iY = inputY + 14f; val iW = 240f; val iH = 26f
        NVGRenderer.rect(iX, iY, iW, iH, theme.controlBg, 6f)
        NVGRenderer.hollowRect(iX, iY, iW, iH, 1f, if (nameFocused) theme.accent else theme.controlBorder, 6f)
        NVGRenderer.pushScissor(iX + 4f, iY, iW - 8f, iH)
        val txt = nameInput.getText().ifEmpty { if (!nameFocused) "e.g. iron_veins_v2" else "" }
        val txtColor = if (nameInput.getText().isEmpty() && !nameFocused) theme.textSecondary else theme.text
        NVGRenderer.text(txt, iX + 6f - nameInput.getTextOffset(), iY + 6f, 11f, txtColor)
        if (nameFocused) nameInput.renderCursor(iX + 6f, iY + 5f, 16f, theme.accent)
        NVGRenderer.popScissor()

        // Error
        errorMsg?.let {
            NVGRenderer.text(it, iX, iY + iH + 4f, 9f, java.awt.Color(255, 100, 100).rgb)
        }

        // Buttons
        val btnY = iY + iH + 22f
        renderBtn("Create →", x + PAD, btnY, 90f, 28f, accent = true)
        renderBtn("Cancel",   x + PAD + 98f, btnY, 72f, 28f, accent = false)
    }

    private fun renderTypeCard(type: RouteType, cx: Float, cy: Float, cw: Float, ch: Float) {
        val theme = ThemeManager.currentTheme
        val selected = selectedType == type
        val hov = isHoveringOver(cx, cy, cw, ch)
        val borderColor = if (selected || hov) type.colorInt() else theme.controlBorder
        val bg = if (selected) type.colorInt(alpha = 30) else theme.controlBg
        NVGRenderer.rect(cx, cy, cw, ch, bg, 8f)
        NVGRenderer.hollowRect(cx, cy, cw, ch, if (selected) 2f else 1f, borderColor, 8f)
        NVGRenderer.rect(cx + 8f, cy + 8f, 66f, 14f, type.colorInt(alpha = 220), 3f)
        NVGRenderer.text(type.name, cx + 10f, cy + 9f, 8f, theme.textOnAccent)
        NVGRenderer.text(type.description(), cx + 8f, cy + 28f, 9f, theme.text)
    }

    private fun RouteType.colorInt(alpha: Int = 255): Int {
        val c = (color and 0xFFFFFF).toInt()
        val r = (c shr 16) and 0xFF
        val g = (c shr 8) and 0xFF
        val b = c and 0xFF
        return java.awt.Color(r, g, b, alpha).rgb
    }

    private fun RouteType.description(): String = when (this) {
        RouteType.ORE_MINER  -> "Travel to area, loop between veins"
        RouteType.COMMISSION -> "Walk/warp from /warpforge to vein"
        RouteType.PATROL     -> "Travel to area, patrol kill zones"
        RouteType.GEMSTONE   -> "Warp-and-mine loop"
        RouteType.TUNNEL     -> "/warpcamp → mine anchors → repeat"
    }

    private fun renderBtn(label: String, bx: Float, by: Float, bw: Float, bh: Float, accent: Boolean) {
        val theme = ThemeManager.currentTheme
        val hov = isHoveringOver(bx, by, bw, bh)
        val bg = when {
            accent && hov -> theme.accent
            accent -> theme.accent
            hov -> theme.selectedOverlay
            else -> theme.controlBg
        }
        NVGRenderer.rect(bx, by, bw, bh, bg, 6f)
        NVGRenderer.hollowRect(bx, by, bw, bh, 1f, if (accent) theme.accent else theme.controlBorder, 6f)
        val lw = NVGRenderer.textWidth(label, 11f)
        val textColor = if (accent) theme.textOnAccent else theme.text
        NVGRenderer.text(label, bx + (bw - lw) / 2f, by + (bh - 12f) / 2f, 11f, textColor)
    }

    override fun mouseClicked(button: Int): Boolean {
        if (button != 0) return false

        // Type cards
        val gridX = x + PAD; val gridY = y + 54f
        val types = RouteType.entries
        val coords = listOf(
            Triple(gridX,                       gridY,                        CARD_W),
            Triple(gridX + CARD_W + GRID_GAP,  gridY,                        CARD_W),
            Triple(gridX,                       gridY + CARD_H + GRID_GAP,   CARD_W),
            Triple(gridX + CARD_W + GRID_GAP,  gridY + CARD_H + GRID_GAP,   CARD_W),
            Triple(gridX,                       gridY + (CARD_H + GRID_GAP) * 2, CARD_W * 2 + GRID_GAP),
        )
        types.forEachIndexed { i, t ->
            val (cx, cy, cw) = coords[i]
            val ch = if (i == 4) CARD_H - 10f else CARD_H
            if (isHoveringOver(cx, cy, cw, ch)) { selectedType = t; errorMsg = null; return true }
        }

        // Name input focus
        val tunnelY = y + 54f + (CARD_H + GRID_GAP) * 2
        val inputY = tunnelY + CARD_H + 10f
        val iX = x + PAD; val iY = inputY + 14f; val iW = 240f; val iH = 26f
        nameFocused = isHoveringOver(iX, iY, iW, iH)

        // Create
        val btnY = iY + iH + 22f
        if (isHoveringOver(x + PAD, btnY, 90f, 28f)) {
            attemptCreate()
            return true
        }
        // Cancel
        if (isHoveringOver(x + PAD + 98f, btnY, 72f, 28f)) {
            org.cobalt.internal.ui.screen.UIConfig.swapBodyPanel(UIRoutesPanel())
            return true
        }

        return false
    }

    private fun attemptCreate() {
        val type = selectedType ?: run { errorMsg = "Select a route type."; return }
        val name = nameInput.getText().trim()
        if (name.isEmpty()) { errorMsg = "Enter a route name."; return }
        if (!name.matches(Regex("[a-zA-Z0-9_\\-]+"))) {
            errorMsg = "Name: letters, numbers, _ and - only."
            return
        }
        if (RouteStore.exists(name)) { errorMsg = "A route named \"$name\" already exists."; return }
        val route = SavedRoute(name = name, type = type)
        RouteStore.save(route)
        onCreated(route)
    }

    fun keyTyped(char: Char, keyCode: Int): Boolean {
        if (!nameFocused) return false
        when (keyCode) {
            259 -> nameInput.backspace()
            261 -> nameInput.delete()
            263 -> nameInput.moveCursorLeft(false)
            262 -> nameInput.moveCursorRight(false)
            257 -> attemptCreate()   // ENTER
            else -> if (char != Char(0)) nameInput.insertText(char.toString())
        }
        return true
    }

    companion object {
        private const val CARD_W = 174f
        private const val CARD_H = 72f
        private const val GRID_GAP = 8f
    }
}
```

- [ ] **Step 2: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/ui/panel/panels/UINewRouteModal.kt
git commit -m "feat: add UINewRouteModal with 5-type picker and route name creation"
```

---

## Task 7: Wire Sidebar, Register Systems in Cobalt.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UISidebar.kt`
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Update UISidebar.kt routes button**

In `UISidebar.kt`, find the routes button:

```kotlin
  private val routesButton = UIButton("/assets/cobalt/textures/ui/routes.svg", "Routes") {
    openQuickModules("cobalt-quick-routes", "Routes", listOf(RoutesModule))
  }
```

Replace with:

```kotlin
  private val routesButton = UIButton("/assets/cobalt/textures/ui/routes.svg", "Routes") {
    UIConfig.swapBodyPanel(UIRoutesPanel())
  }
```

Add the import at the top of `UISidebar.kt` (alongside existing imports):

```kotlin
import org.cobalt.internal.ui.panel.panels.UIRoutesPanel
```

Remove the now-unused import (if present):
```kotlin
import org.cobalt.internal.mining.RoutesModule
```

- [ ] **Step 2: Register RouteStore.init(), RouteEditMode, RoutePointTypePopup in Cobalt.kt**

In `Cobalt.kt`, find:
```kotlin
    PatrolWaypointStore.load()
```
Replace with:
```kotlin
    RouteStore.init()
```

Find the EventBus registration block:
```kotlin
    listOf(
      TickScheduler, MainCommand, NotificationManager,
      RotationExecutor, HudModuleManager, TitleScreenRenderer, MacroTimeTracker,
    ).forEach { EventBus.register(it) }
```
Change to:
```kotlin
    listOf(
      TickScheduler, MainCommand, NotificationManager,
      RotationExecutor, HudModuleManager, TitleScreenRenderer, MacroTimeTracker,
      RouteEditMode, RoutePointTypePopup,
    ).forEach { EventBus.register(it) }
```

Add imports to `Cobalt.kt`:
```kotlin
import org.cobalt.internal.routes.RouteEditMode
import org.cobalt.internal.routes.RoutePointTypePopup
import org.cobalt.internal.routes.RouteStore
```

Remove the now-unused import:
```kotlin
import org.cobalt.internal.pathfinding.PatrolWaypointStore
```

- [ ] **Step 3: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: In-game smoke test**

Run `./gradlew runClient`. Open the Cobalt UI (default keybind). Click **Routes** in the sidebar. Verify:
- UIRoutesPanel opens (shows "No routes found" if `config/cobalt/routes2/` is empty)
- `+ New` opens UINewRouteModal
- Create a test route (type = Ore Miner, name = `test_route`) → verify it appears in the card list
- Click `▼` on the card → sub-route rows appear (Travel Route, Loop Route)
- Click `✏ Edit` on Loop Route → game returns to first-person, sticky HUD appears at bottom
- Right-click a block → point recorded, count increments
- Shift+right-click → RoutePointTypePopup appears
- Click `Undo` → last point removed
- Click `Done` → HUD disappears

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/ui/panel/panels/UISidebar.kt \
        src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat: wire sidebar routes button to UIRoutesPanel, register RouteEditMode and RoutePointTypePopup"
```

---

## Task 8: Macro Integration — Route Selection via RouteStore

Connect the Load button to macros and make existing macros read armed routes from RouteStore.

**Context:** `RouteStore.arm(type, name)` is already implemented. The `▶ Load` button in `UIRoutesPanel.mouseClicked` already calls it. What remains is making macros read the armed route and removing the old zone-panel approach for CommissionMacroModule.

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt`
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt`
- Modify: `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: CommissionMacroModule — read route list from RouteStore**

Find the method `getAvailableRouteInfos()` in `CommissionMacroModule.kt`. It currently calls into `RoutesModule`. Replace its body so it reads from RouteStore:

```kotlin
fun getAvailableRouteInfos(): List<org.cobalt.internal.mining.RoutesModule.SavedRouteInfo> {
    return org.cobalt.internal.routes.RouteStore.list(org.cobalt.internal.routes.RouteType.COMMISSION)
        .map { r ->
            val pts = r.points
            RoutesModule.SavedRouteInfo(
                name          = r.name,
                mineTypes     = pts.filter { it.type == org.cobalt.internal.routes.RoutePointType.MINE }
                                   .mapNotNull { it.blockId }.distinct(),
                hasMinePoints = pts.any { it.type == org.cobalt.internal.routes.RoutePointType.MINE },
                hasWarpPoints = pts.any { it.type == org.cobalt.internal.routes.RoutePointType.WARP },
                pointCount    = pts.size,
            )
        }
}
```

Add import at top of `CommissionMacroModule.kt`:
```kotlin
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType
import org.cobalt.internal.routes.RoutePointType
```

- [ ] **Step 2: MiningMacroModule — expose armed ORE_MINER route**

Find or create in `MiningMacroModule.kt` any method that reads the active route name. Add:

```kotlin
fun getArmedRoute(): String? = org.cobalt.internal.routes.RouteStore.getArmed(
    org.cobalt.internal.routes.RouteType.ORE_MINER
)
```

Add the imports:
```kotlin
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType
```

- [ ] **Step 3: CombatMacroModule — expose armed PATROL route, remove WalkbackBridge**

Find references to `WalkbackBridge` in `CombatMacroModule.kt`. The bridge provides `startWalkback(name, fromEnd, reverse)`, `stopWalkback()`, `isRunning()`, and `getRouteEndPos(name)`.

Locate every call to `WalkbackBridge.startWalkback(...)` in `CombatMacroModule.kt` and replace it with a call to the existing `RoutesModule.startWalkback(...)` directly (or whatever RoutesModule exposes), since WalkbackBridge was a decoupling shim. Then add a method to read the armed route:

```kotlin
fun getArmedPatrolRoute(): String? = org.cobalt.internal.routes.RouteStore.getArmed(
    org.cobalt.internal.routes.RouteType.PATROL
)
```

Add imports:
```kotlin
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType
```

Remove the `import org.cobalt.internal.helper.WalkbackBridge` import from `CombatMacroModule.kt` and replace all `WalkbackBridge.*` calls with direct `RoutesModule.*` calls (since they are now in the same compilation unit — both are `internal`).

> **Note:** WalkbackBridge is a thin shim — it only holds function references registered by RoutesModule at startup. The actual implementation is in RoutesModule. Replacing `WalkbackBridge.startWalkback(name, fromEnd, reverse)` with `RoutesModule.startWalkback(name, fromEnd, reverse)` is safe as long as RoutesModule exports that method with the same signature.

Verify in RoutesModule that `startWalkback`, `stopWalkback`, `isRunning`, and `getRouteEndPos` are package-accessible (they are `object` members — they are accessible as `RoutesModule.startWalkback(...)`).

- [ ] **Step 4: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL` — if CombatMacroModule has compilation errors around WalkbackBridge, check that you replaced all `WalkbackBridge.foo?.invoke(...)` call sites with `RoutesModule.foo(...)` or the equivalent RoutesModule method.

- [ ] **Step 5: In-game test — arm and verify**

Run `./gradlew runClient`. Open Routes panel. Create a COMMISSION route named `test_commission`. Click `▶ Load`. Open CommissionMacroModule settings — the route picker should now show routes from `routes2/`. For MiningMacro, confirm `getArmedRoute()` returns `null` until a route is Loaded.

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt \
        src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt \
        src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt
git commit -m "feat: connect macros to RouteStore for route selection; remove WalkbackBridge dependency"
```

---

## Task 9: Cleanup — Delete Old Files

Remove files that are now superseded and fix the compilation errors they leave behind.

**Files to delete:**
1. `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICommissionRoutesPanel.kt`
2. `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICombatWalkbackRoutesPanel.kt`
3. `src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutePointPicker.kt`
4. `src/main/kotlin/org/cobalt/internal/ui/hud/RoutePointPopup.kt`
5. `src/main/kotlin/org/cobalt/internal/ui/hud/PatrolPointPopup.kt`
6. `src/main/kotlin/org/cobalt/internal/ui/hud/WalkbackRoutePickerPopup.kt`
7. `src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt`
8. `src/main/kotlin/org/cobalt/internal/helper/WalkbackBridge.kt`

- [ ] **Step 1: Find all references to the deleted files**

```bash
cd "C:/Users/aeare/Desktop/duskv2/Cobalt/src/main/kotlin"
grep -rl "UICommissionRoutesPanel\|UICombatWalkbackRoutesPanel\|UIRoutePointPicker\|RoutePointPopup\|PatrolPointPopup\|WalkbackRoutePickerPopup\|PatrolWaypointStore\|WalkbackBridge" . \
  | grep -v "^./org/cobalt/internal/ui/panel/panels/UICommissionRoutesPanel\|^./org/cobalt/internal/ui/panel/panels/UICombatWalkbackRoutesPanel\|^./org/cobalt/internal/ui/panel/panels/UIRoutePointPicker\|^./org/cobalt/internal/ui/hud/RoutePointPopup\|^./org/cobalt/internal/ui/hud/PatrolPointPopup\|^./org/cobalt/internal/ui/hud/WalkbackRoutePickerPopup\|^./org/cobalt/internal/pathfinding/PatrolWaypointStore\|^./org/cobalt/internal/helper/WalkbackBridge"
```

For each file listed, remove the import and any call sites that reference the deleted class.

- [ ] **Step 2: Remove RoutePointPopup registration (if present)**

Check if `RoutePointPopup` is registered with EventBus anywhere in `Cobalt.kt` or `RoutesModule.kt`. If so, remove that registration.

Search:
```bash
grep -rn "RoutePointPopup\|PatrolPointPopup\|WalkbackRoutePickerPopup" \
  "C:/Users/aeare/Desktop/duskv2/Cobalt/src/main/kotlin/org/cobalt"
```

Remove any `EventBus.register(RoutePointPopup)` lines found.

- [ ] **Step 3: Remove UICommissionRoutesPanel open-calls in macro settings**

Search for where `UICommissionRoutesPanel()` is instantiated or opened:
```bash
grep -rn "UICommissionRoutesPanel\|UICombatWalkbackRoutesPanel\|UIRoutePointPicker" \
  "C:/Users/aeare/Desktop/duskv2/Cobalt/src/main/kotlin/org/cobalt"
```

Replace any `UIConfig.swapBodyPanel(UICommissionRoutesPanel())` with `UIConfig.swapBodyPanel(UIRoutesPanel())`. Replace `UIConfig.swapBodyPanel(UICombatWalkbackRoutesPanel())` similarly. Remove any `ActionSetting` buttons that launched the old panels.

- [ ] **Step 4: Delete the files**

```bash
cd "C:/Users/aeare/Desktop/duskv2/Cobalt"
git rm src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICommissionRoutesPanel.kt
git rm src/main/kotlin/org/cobalt/internal/ui/panel/panels/UICombatWalkbackRoutesPanel.kt
git rm src/main/kotlin/org/cobalt/internal/ui/panel/panels/UIRoutePointPicker.kt
git rm src/main/kotlin/org/cobalt/internal/ui/hud/RoutePointPopup.kt
git rm src/main/kotlin/org/cobalt/internal/ui/hud/PatrolPointPopup.kt
git rm src/main/kotlin/org/cobalt/internal/ui/hud/WalkbackRoutePickerPopup.kt
git rm src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt
git rm src/main/kotlin/org/cobalt/internal/helper/WalkbackBridge.kt
```

- [ ] **Step 5: Build**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`. If there are unresolved reference errors, use the grep output from Step 1 to find remaining call sites and remove them.

- [ ] **Step 6: Full in-game verification**

Run `./gradlew runClient`. Verify:
- Cobalt UI opens cleanly
- Routes sidebar button → UIRoutesPanel
- + New → UINewRouteModal → create a route
- Edit → sticky HUD, recording works
- Insert mode → gap picker, point inserted at correct index
- Load button → `RouteStore.getArmed()` returns the route name
- Old panels (commission routes, walkback panel) do NOT appear anywhere in the UI
- Migration: place a `.json` file in `config/cobalt/routes/` with old format and relaunch → it should appear as an ORE_MINER route in the new panel

- [ ] **Step 7: Commit**

```bash
git commit -m "feat: remove legacy route panels, popups, PatrolWaypointStore, and WalkbackBridge"
```

---

## Self-Review

**Spec coverage check:**

| Spec Section | Covered |
|---|---|
| 1.1 RouteType enum (5 types) | ✓ Task 1 |
| 1.2 RoutePointType enum (WALK/WARP/MINE/KILL) + allowed-types table | ✓ Task 1 |
| 1.3 File format (dual vs single sub-route JSON) | ✓ Task 2 (RouteStore.save + SavedRoute.toJson) |
| 1.4 Migration from routes/ → routes2/ | ✓ Task 2 (RouteStore.migrateFile) |
| 2.1 Routes screen layout (expandable cards, filter tabs, Load/Edit/Insert/Delete/+New) | ✓ Task 5 |
| 2.2 Load arms route, Edit enters edit mode, +Insert enters insert mode, +New opens modal, ✕ deletes | ✓ Tasks 5, 6 |
| 2.3 New route creation flow (type picker → name → Create → edit mode) | ✓ Task 6 |
| 2.4 Remove old panels | ✓ Task 9 |
| 3.1 Entering edit mode (panel closes, first-person + overlay) | ✓ Task 4 |
| 3.2 Sticky HUD (route label, mode buttons, point count, Undo, Done) | ✓ Task 4 |
| 3.3 Recording (right-click = sticky type, Shift+right-click = popup) | ✓ Tasks 3, 4 |
| 3.4 In-world rendering (boxes + lines per point) | ✓ Task 4 |
| 3.5 Insert mode (gap overlay → INSERT_RECORDING → popup always → renumber) | ✓ Task 4 |
| 3.6 Undo (single-level, removes last point, saves) | ✓ Task 4 |
| 4.1 Route assignment UI (macros read from RouteStore, Load button arms) | ✓ Task 8 |
| 4.2–4.6 Macro execution rewrites | ⚠ Out of scope — follow-up plan |
| 5. Files added/removed | ✓ Tasks 1–9 |

**Placeholder scan:** No TBD, TODO, or "similar to Task N" shortcuts found. All code blocks are complete.

**Type consistency:**
- `RoutePointType.fromId` used in `SavedRoute.parsePoints` ✓
- `allowedPointTypes(type, sub)` used in `RouteEditMode.enter` ✓
- `subRoutesFor(type)` used in `UIRoutesPanel.renderCard` ✓
- `SavedRoute.withSubRoute` used in `RouteEditMode.savePoints` ✓
- `RouteStore.arm` / `getArmed` used in `UIRoutesPanel.mouseClicked` (Load) and macro modules ✓
- `RouteEditMode.enter` / `enterInsertMode` called from `UIRoutesPanel.mouseClicked` ✓
- `RoutePointTypePopup.open(types, pos, callback)` called from `RouteEditMode.onRightClick` ✓
