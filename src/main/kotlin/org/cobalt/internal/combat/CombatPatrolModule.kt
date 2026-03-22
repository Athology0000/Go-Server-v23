package org.cobalt.internal.combat

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.util.ChatUtils
import org.cobalt.internal.pathfinding.PathfindingModule

enum class CombatPatrolPointType(val id: String) {
    WALK("walk"), WARP("warp"), KILL("kill");
    companion object {
        fun fromId(id: String?) = entries.firstOrNull { it.id == id } ?: WALK
    }
}

data class CombatPatrolPoint(val x: Int, val y: Int, val z: Int, val type: CombatPatrolPointType)

object CombatPatrolModule : Module("Combat Patrol") {

    private val mc: Minecraft = Minecraft.getInstance()
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val patrolDir: File by lazy {
        File(mc.gameDirectory, "config/cobalt/combat_patrol")
    }

    internal val patrolPoints = mutableListOf<CombatPatrolPoint>()

    enum class PatrolState { IDLE, NAVIGATING, WARPING, COMBAT_INTERRUPT, AT_KILL_ZONE }

    var patrolState: PatrolState = PatrolState.IDLE
        private set
    var patrolOwnsPathfinder: Boolean = false
        private set

    private var patrolRunning = false
    private var routeIndex = 0
    private var killZoneClearTicks = 0
    private var killZoneClearedThisTick = false

    val enabled = CheckboxSetting("Enabled", "Enable the Combat Patrol module.", false)

    private val routeName = TextSetting("Route Name", "Name used for save/load.", "default")
    private val pointsInfo = InfoSetting("Points", "Number of recorded points.", InfoType.INFO)
    private val statusInfo = InfoSetting("Status", "Current patrol state.", InfoType.INFO)
    private val recordOnRightClick = CheckboxSetting("Record on Right Click", "Append a point when you right-click a block.", false)
    val loopRoute = CheckboxSetting("Loop Route", "Wrap back to first point at end.", true)
    val startFromNearest = CheckboxSetting("Start From Nearest", "Begin at the closest point to your position.", true)
    val pointType = ModeSetting("Point Type", "Type used when adding points.", 0, arrayOf("Walk", "Warp", "Kill"))
    val killZoneRadius = SliderSetting("Kill Zone Radius", "Mob search radius around a Kill point (blocks).", 16.0, 4.0, 64.0)
    val killZoneDwellTicks = SliderSetting("Kill Zone Dwell Ticks", "Zero-mob ticks required before advancing past a Kill point.", 60.0, 10.0, 200.0, step = 1.0)
    val aotvSlot = ModeSetting("AOTV Slot", "Hotbar slot (1-9) holding your AOTV item.", 0, arrayOf("1","2","3","4","5","6","7","8","9"))

    private val addPointAction = ActionSetting("Add Point", "Record your current position.", "Add") { addPointFromPlayer() }
    private val removeLastAction = ActionSetting("Remove Last", "Remove the last recorded point.", "Remove") {
        if (patrolPoints.isNotEmpty()) { patrolPoints.removeAt(patrolPoints.lastIndex); updatePointsInfo() }
    }
    private val clearRouteAction = ActionSetting("Clear Route", "Remove all patrol points.", "Clear") {
        patrolPoints.clear(); updatePointsInfo()
    }
    private val saveRouteAction = ActionSetting("Save Route", "Save route to disk.", "Save") { saveRoute() }
    private val loadRouteAction = ActionSetting("Load Route", "Load route from disk.", "Load") { loadRoute() }
    private val startPatrolAction = ActionSetting("Start Patrol", "Start the patrol.", "Start") { startPatrol() }
    private val stopPatrolAction = ActionSetting("Stop Patrol", "Stop the patrol.", "Stop") { stopPatrol() }

    init {
        addSetting(
            enabled,
            routeName, pointsInfo, statusInfo,
            recordOnRightClick, loopRoute, startFromNearest, pointType,
            killZoneRadius, killZoneDwellTicks, aotvSlot,
            addPointAction, removeLastAction, clearRouteAction,
            saveRouteAction, loadRouteAction,
            startPatrolAction, stopPatrolAction,
        )
        statusInfo.value = "Idle"
        org.cobalt.api.event.EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        updatePointsInfo()
        if (!enabled.value) {
            if (patrolRunning) stopPatrol("Patrol module disabled.")
            return
        }
        if (patrolPoints.isEmpty()) {
            if (patrolRunning) stopPatrol("No patrol points.")
            return
        }
        if (!patrolRunning) return

        when (patrolState) {
            PatrolState.NAVIGATING -> {
                NativePathfinder.tick()?.applyToPlayer()
                val s = NativePathfinder.status
                if (s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
                    val current = patrolPoints.getOrNull(routeIndex)
                    if (s == PathStatus.FAILED) ChatUtils.sendMessage("Patrol: pathfinding failed at point ${routeIndex + 1}, skipping.")
                    if (current?.type == CombatPatrolPointType.KILL) {
                        NativePathfinder.stop()
                        patrolOwnsPathfinder = false
                        patrolState = PatrolState.AT_KILL_ZONE
                        killZoneClearTicks = 0
                    } else {
                        advanceAndNavigate()
                    }
                }
            }
            PatrolState.WARPING -> {
                // Warp sub-machine runs in onRender (added in Task 5). Timeout handled there.
            }
            PatrolState.AT_KILL_ZONE -> {
                if (killZoneClearedThisTick) {
                    killZoneClearTicks++
                    if (killZoneClearTicks >= killZoneDwellTicks.value.toInt()) {
                        killZoneClearTicks = 0
                        advanceAndNavigate()
                    }
                } else {
                    killZoneClearTicks = 0
                }
                killZoneClearedThisTick = false
            }
            PatrolState.COMBAT_INTERRUPT, PatrolState.IDLE -> { /* CombatMacroModule owns pathfinder */ }
        }
        statusInfo.value = patrolState.name
    }

    @SubscribeEvent
    fun onRightClick(@Suppress("UNUSED_PARAMETER") event: MouseEvent.RightClick) {
        if (!enabled.value || !recordOnRightClick.value) return
        val hit = mc.hitResult
        if (hit is BlockHitResult && hit.type == HitResult.Type.BLOCK) {
            val pos = hit.blockPos
            patrolPoints.add(CombatPatrolPoint(pos.x, pos.y, pos.z, currentPointType()))
            ChatUtils.sendMessage("Patrol point recorded at ${pos.x} ${pos.y} ${pos.z} (${currentPointType().id}).")
            updatePointsInfo()
        }
    }

    private fun currentPointType() = when (pointType.value) {
        1 -> CombatPatrolPointType.WARP
        2 -> CombatPatrolPointType.KILL
        else -> CombatPatrolPointType.WALK
    }

    private fun addPointFromPlayer() {
        val player = mc.player ?: return
        val pos = player.blockPosition()
        val type = currentPointType()
        patrolPoints.add(CombatPatrolPoint(pos.x, pos.y, pos.z, type))
        ChatUtils.sendMessage("Patrol point added (${patrolPoints.size} total, type=${type.id}).")
        updatePointsInfo()
    }

    private fun isValidName(name: String): Boolean {
        if (name == "." || name == "..") return false
        if (name.endsWith(".") || name.endsWith(" ")) return false
        return name.none { it in charArrayOf('\\', '/', ':', '*', '?', '"', '<', '>', '|') }
    }

    private fun routeFile(name: String) = File(patrolDir, "$name.json")

    private fun saveRoute() {
        val name = routeName.value.trim()
        if (name.isEmpty()) { ChatUtils.sendMessage("Route name is empty."); return }
        if (!isValidName(name)) { ChatUtils.sendMessage("Invalid route name characters."); return }
        if (!patrolDir.exists()) patrolDir.mkdirs()
        val root = JsonObject()
        val arr = JsonArray()
        patrolPoints.forEach { p ->
            val o = JsonObject()
            o.addProperty("x", p.x); o.addProperty("y", p.y); o.addProperty("z", p.z)
            o.addProperty("type", p.type.id)
            arr.add(o)
        }
        root.add("points", arr)
        routeFile(name).writeText(gson.toJson(root))
        ChatUtils.sendMessage("Saved patrol route \"$name\" (${patrolPoints.size} points).")
    }

    private fun loadRoute() {
        val name = routeName.value.trim()
        if (name.isEmpty()) { ChatUtils.sendMessage("Route name is empty."); return }
        if (!isValidName(name)) { ChatUtils.sendMessage("Invalid route name characters."); return }
        val file = routeFile(name)
        if (!file.exists()) { ChatUtils.sendMessage("Route \"$name\" not found."); return }
        val text = runCatching { file.readText() }.getOrNull()?.trim().orEmpty()
        if (text.isEmpty()) { ChatUtils.sendMessage("Route file is empty."); return }
        val parsed = runCatching { JsonParser.parseString(text) }.getOrNull() ?: run {
            ChatUtils.sendMessage("Route file is invalid JSON."); return
        }
        val arr = parsed.asJsonObject?.getAsJsonArray("points") ?: run {
            ChatUtils.sendMessage("Route file has no \"points\" array."); return
        }
        val loaded = mutableListOf<CombatPatrolPoint>()
        arr.forEach { el ->
            val o = el.asJsonObject
            val x = o["x"]?.asInt ?: return@forEach
            val y = o["y"]?.asInt ?: return@forEach
            val z = o["z"]?.asInt ?: return@forEach
            val t = CombatPatrolPointType.fromId(o["type"]?.asString)
            loaded.add(CombatPatrolPoint(x, y, z, t))
        }
        patrolPoints.clear()
        patrolPoints.addAll(loaded)
        updatePointsInfo()
        ChatUtils.sendMessage("Loaded patrol route \"$name\" (${patrolPoints.size} points).")
    }

    internal fun updatePointsInfo() {
        pointsInfo.value = "${patrolPoints.size} points"
    }

    fun startPatrol() {
        if (patrolPoints.isEmpty()) { ChatUtils.sendMessage("No patrol points. Add some first."); return }
        PathfindingModule.ensureEnabledForAutomation("combat-patrol")
        routeIndex = if (startFromNearest.value) findNearestIndex() else 0
        patrolRunning = true
        killZoneClearTicks = 0
        navigateTo(patrolPoints[routeIndex])
        ChatUtils.sendMessage("Combat patrol started at point ${routeIndex + 1}/${patrolPoints.size}.")
    }

    fun stopPatrol(msg: String = "") {
        if (patrolRunning && msg.isNotEmpty()) ChatUtils.sendMessage(msg)
        patrolRunning = false
        patrolOwnsPathfinder = false
        patrolState = PatrolState.IDLE
        killZoneClearTicks = 0
        killZoneClearedThisTick = false
        NativePathfinder.stop()
        statusInfo.value = "Idle"
    }

    val isPatrolRunning: Boolean get() = patrolRunning

    private fun advanceAndNavigate() {
        routeIndex++
        if (routeIndex >= patrolPoints.size) {
            if (loopRoute.value) {
                routeIndex = 0
            } else {
                stopPatrol("Patrol complete.")
                return
            }
        }
        navigateTo(patrolPoints[routeIndex])
    }

    internal fun navigateTo(point: CombatPatrolPoint) {
        when (point.type) {
            CombatPatrolPointType.WALK, CombatPatrolPointType.KILL -> {
                val level = mc.level
                val resolved = if (level != null) {
                    MinecraftPathingRules.resolveTarget(level, BlockPos(point.x, point.y, point.z))
                        ?: BlockPos(point.x, point.y, point.z)
                } else BlockPos(point.x, point.y, point.z)
                NativePathfinder.setTarget(resolved.x + 0.5, resolved.y.toDouble(), resolved.z + 0.5)
                patrolOwnsPathfinder = true
                patrolState = PatrolState.NAVIGATING
            }
            CombatPatrolPointType.WARP -> {
                // Full warp logic added in Task 5. Fall back to walking for now.
                NativePathfinder.setTarget(point.x + 0.5, point.y.toDouble(), point.z + 0.5)
                patrolOwnsPathfinder = true
                patrolState = PatrolState.NAVIGATING
            }
        }
    }

    private fun findNearestIndex(): Int {
        val player = mc.player ?: return 0
        val px = player.x; val py = player.y; val pz = player.z
        return patrolPoints.indices.minByOrNull { i ->
            val p = patrolPoints[i]
            val dx = p.x + 0.5 - px; val dy = p.y - py; val dz = p.z + 0.5 - pz
            dx * dx + dy * dy + dz * dz
        } ?: 0
    }

    /** Called by CombatMacroModule when a stray mob is found while patrol is navigating. */
    fun onCombatInterrupt() {
        if (patrolState != PatrolState.NAVIGATING && patrolState != PatrolState.WARPING) return
        NativePathfinder.stop()
        patrolOwnsPathfinder = false
        patrolState = PatrolState.COMBAT_INTERRUPT
    }

    /** Called by CombatMacroModule when no target remains (edge-triggered: only acts on COMBAT_INTERRUPT). */
    fun onCombatResume() {
        if (patrolState != PatrolState.COMBAT_INTERRUPT) return
        val point = patrolPoints.getOrNull(routeIndex) ?: run { stopPatrol("Route index out of bounds."); return }
        navigateTo(point)
    }

    /** Called by CombatMacroModule each tick there are no mobs in the kill zone. */
    fun onKillZoneCleared() {
        if (patrolState != PatrolState.AT_KILL_ZONE) return
        killZoneClearedThisTick = true
    }

    val currentKillPoint: CombatPatrolPoint?
        get() = if (patrolState == PatrolState.AT_KILL_ZONE) patrolPoints.getOrNull(routeIndex) else null

    val killZoneRadiusValue: Double get() = killZoneRadius.value
}
