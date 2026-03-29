package org.cobalt.internal.pathfinding

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft

data class RouteWaypoint(val x: Double, val y: Double, val z: Double)
data class KillWaypoint(val x: Double, val y: Double, val z: Double, val label: String = "")

internal object PatrolWaypointStore {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file: File by lazy {
        File(Minecraft.getInstance().gameDirectory, "config/cobalt/patrol_waypoints.json")
    }

    var routeWaypoints: MutableList<RouteWaypoint> = mutableListOf()
    var killWaypoints:  MutableList<KillWaypoint>  = mutableListOf()

    fun load() {
        routeWaypoints.clear()   // idempotent - prevent double-append on reload
        killWaypoints.clear()
        if (!file.exists()) return
        val text = file.bufferedReader().use { it.readText() }
        if (text.isBlank()) return
        runCatching {
            val root = JsonParser.parseString(text).asJsonObject
            root.getAsJsonArray("routeWaypoints")?.forEach { el ->
                val o = el.asJsonObject
                routeWaypoints.add(RouteWaypoint(
                    o["x"].asDouble, o["y"].asDouble, o["z"].asDouble
                ))
            }
            root.getAsJsonArray("killWaypoints")?.forEach { el ->
                val o = el.asJsonObject
                killWaypoints.add(KillWaypoint(
                    o["x"].asDouble, o["y"].asDouble, o["z"].asDouble,
                    o["label"]?.asString ?: ""
                ))
            }
        }
    }

    fun save() {
        file.parentFile?.mkdirs()
        val root = com.google.gson.JsonObject()

        val rArr = JsonArray()
        routeWaypoints.forEach { wp ->
            val o = com.google.gson.JsonObject()
            o.addProperty("x", wp.x); o.addProperty("y", wp.y); o.addProperty("z", wp.z)
            rArr.add(o)
        }
        root.add("routeWaypoints", rArr)

        val kArr = JsonArray()
        killWaypoints.forEach { wp ->
            val o = com.google.gson.JsonObject()
            o.addProperty("x", wp.x); o.addProperty("y", wp.y); o.addProperty("z", wp.z)
            o.addProperty("label", wp.label)
            kArr.add(o)
        }
        root.add("killWaypoints", kArr)

        file.bufferedWriter().use { it.write(gson.toJson(root)) }
    }
}
