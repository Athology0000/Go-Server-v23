package org.cobalt.internal.mining

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

enum class MiningArea(val fileName: String) {
    DWARVEN("dwarven_data.json"),
    GLACITE("glacite_data.json"),
}

/**
 * Per-area learned-vein-anchor store.
 *
 * Area detection is by Y coordinate of the vein seed:
 *   - Y < 189  → Dwarven Mines  →  dwarven_data.json
 *   - Y ≥ 189  → Glacite Tunnels → glacite_data.json
 *
 * File format: a plain JSON array of `BlockPos.asLong()` keys.
 */
internal object MiningAnchorStore {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val cache = mutableMapOf<MiningArea, LinkedHashSet<Long>>()

    fun areaForY(y: Int): MiningArea = if (y < 189) MiningArea.DWARVEN else MiningArea.GLACITE

    fun areaFor(pos: BlockPos): MiningArea = areaForY(pos.y)

    fun get(area: MiningArea): LinkedHashSet<Long> =
        cache.getOrPut(area) { loadAllForArea(area) }

    /** Drop the in-memory cache so the next [get] reloads from disk. */
    fun invalidateCache() {
        cache.clear()
    }

    fun add(pos: BlockPos, cap: Int) {
        val area = areaFor(pos)
        val set = get(area)
        val key = pos.asLong()
        set.remove(key)
        set.add(key)
        while (set.size > cap) {
            val oldest = set.iterator().next()
            set.remove(oldest)
        }
        saveToDisk(area, set)
    }

    fun clear(area: MiningArea) {
        cache[area]?.clear()
        saveToDisk(area, emptySet())
    }

    fun clearAll() {
        MiningArea.entries.forEach { clear(it) }
    }

    private fun fileFor(area: MiningArea): File =
        File(Minecraft.getInstance().gameDirectory, "config/cobalt/${area.fileName}")

    private fun configDir(): File =
        File(Minecraft.getInstance().gameDirectory, "config/cobalt")

    /**
     * Load the area's primary file PLUS every `*_<area>_data.json` material file
     * produced by [WorldVeinCacherModule]. All keys are merged so the mining
     * macro sees one unified anchor pool per area regardless of which file the
     * anchor lives in. The mining macro still filters by selected ID at scan
     * time so non-relevant types don't get mined.
     */
    private fun loadAllForArea(area: MiningArea): LinkedHashSet<Long> {
        val combined = LinkedHashSet<Long>()
        combined.addAll(loadFromDisk(area))

        val dir = configDir()
        if (!dir.isDirectory) return combined
        val suffix = "_${area.name.lowercase()}_data.json"
        val files = dir.listFiles { _, name -> name.endsWith(suffix) } ?: return combined
        for (f in files) {
            if (f.name == area.fileName) continue
            combined.addAll(readLongArray(f))
        }
        return combined
    }

    private fun readLongArray(file: File): LinkedHashSet<Long> {
        if (!file.exists()) return LinkedHashSet()
        return try {
            val json = JsonParser.parseString(file.readText())
            if (!json.isJsonArray) return LinkedHashSet()
            val out = LinkedHashSet<Long>()
            for (el in json.asJsonArray) {
                if (el.isJsonPrimitive) {
                    val prim = el.asJsonPrimitive
                    if (prim.isNumber) out.add(prim.asLong)
                }
            }
            out
        } catch (_: Exception) {
            LinkedHashSet()
        }
    }

    private fun loadFromDisk(area: MiningArea): LinkedHashSet<Long> {
        val file = fileFor(area)
        if (!file.exists()) return LinkedHashSet()
        return try {
            val json = JsonParser.parseString(file.readText())
            if (!json.isJsonArray) return LinkedHashSet()
            val out = LinkedHashSet<Long>()
            for (el in json.asJsonArray) {
                if (el.isJsonPrimitive) {
                    val prim = el.asJsonPrimitive
                    if (prim.isNumber) out.add(prim.asLong)
                }
            }
            out
        } catch (_: Exception) {
            LinkedHashSet()
        }
    }

    private fun saveToDisk(area: MiningArea, anchors: Set<Long>) {
        val file = fileFor(area)
        file.parentFile?.mkdirs()
        val arr = JsonArray()
        for (key in anchors) arr.add(key)
        try {
            file.writeText(gson.toJson(arr))
        } catch (_: Exception) {
            // Silently ignore — anchors are still kept in-memory for this session.
        }
    }
}
