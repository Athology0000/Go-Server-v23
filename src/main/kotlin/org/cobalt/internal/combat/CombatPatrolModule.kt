package org.cobalt.internal.combat

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.ChatUtils

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
        if (patrolPoints.isNotEmpty()) { patrolPoints.removeAt(patrolPoints.lastIndex); updateInfo() }
    }
    private val clearRouteAction = ActionSetting("Clear Route", "Remove all patrol points.", "Clear") {
        patrolPoints.clear(); updateInfo()
    }
    private val saveRouteAction = ActionSetting("Save Route", "Save route to disk.", "Save") { saveRoute() }
    private val loadRouteAction = ActionSetting("Load Route", "Load route from disk.", "Load") { loadRoute() }
    private val startPatrolAction = ActionSetting("Start Patrol", "Start the patrol.", "Start") { startPatrol() }
    private val stopPatrolAction = ActionSetting("Stop Patrol", "Stop the patrol.", "Stop") { stopPatrol() }

    init {
        addSetting(
            routeName, pointsInfo, statusInfo,
            recordOnRightClick, loopRoute, startFromNearest, pointType,
            killZoneRadius, killZoneDwellTicks, aotvSlot,
            addPointAction, removeLastAction, clearRouteAction,
            saveRouteAction, loadRouteAction,
            startPatrolAction, stopPatrolAction,
        )
    }

    private fun currentPointType() = when (pointType.value) {
        1 -> CombatPatrolPointType.WARP
        2 -> CombatPatrolPointType.KILL
        else -> CombatPatrolPointType.WALK
    }

    private fun addPointFromPlayer() {
        val player = mc.player ?: return
        val pos = player.blockPosition()
        patrolPoints.add(CombatPatrolPoint(pos.x, pos.y, pos.z, currentPointType()))
        ChatUtils.sendMessage("Patrol point added (${patrolPoints.size} total, type=${currentPointType().id}).")
        updateInfo()
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
        updateInfo()
        ChatUtils.sendMessage("Loaded patrol route \"$name\" (${patrolPoints.size} points).")
    }

    internal fun updateInfo() {
        pointsInfo.value = "${patrolPoints.size} points"
    }

    fun startPatrol() { /* filled in Task 2 */ }
    fun stopPatrol(msg: String = "") { /* filled in Task 2 */ }
}
