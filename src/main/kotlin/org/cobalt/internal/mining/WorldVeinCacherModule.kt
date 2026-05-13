package org.cobalt.internal.mining

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import java.awt.Color
import net.minecraft.world.phys.AABB
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.util.render.Render3D
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.util.ChatUtils

/**
 * Continuous vein cacher. While enabled, every few ticks it sweeps a small
 * cube around the player and incrementally records any vein blocks it sees
 * into per-(material, area) JSON files in config/cobalt/:
 *
 *   mithril_gray_dwarven_data.json
 *   titanium_dwarven_data.json
 *   tungsten_glacite_data.json
 *   ruby_gemstone_dwarven_data.json
 *   ...
 *
 * Files are append-only in practice — re-walking the same ground costs nothing
 * because the in-memory set deduplicates before the next disk flush. The
 * mining macro auto-merges every `*_<area>_data.json` file at scan time, so
 * just leave this on while you run laps and the macro gets a complete map.
 */
object WorldVeinCacherModule : Module("World Vein Cacher") {

    override val category = ModuleCategory.MINING

    private val info = InfoSetting(
        "World Vein Cacher",
        "Toggle on and walk around — every vein you pass gets saved.",
        InfoType.INFO,
    )

    val enabled = CheckboxSetting(
        "Enabled",
        "Continuously cache every vein near you to per-material files.",
        false,
    )

    private val scanRadius = SliderSetting(
        "Scan Radius",
        "Horizontal block radius swept around the player each pass.",
        24.0, 8.0, 96.0, 1.0,
    )

    private val scanVertical = SliderSetting(
        "Scan Vertical",
        "Vertical range (above + below) swept each pass.",
        32.0, 8.0, 128.0, 1.0,
    )

    private val scanIntervalTicks = SliderSetting(
        "Scan Interval (ticks)",
        "How often to sweep. Lower = faster pickup, higher = less CPU.",
        5.0, 1.0, 40.0, 1.0,
    )

    private val flushIntervalTicks = SliderSetting(
        "Flush Interval (ticks)",
        "How often to write dirty groups to disk. 20 = once per second.",
        20.0, 5.0, 200.0, 1.0,
    )

    private val oneShotButton = ActionSetting(
        "Full Cache Now",
        "Do one big synchronous sweep at the current radius (freezes briefly).",
        "Run",
    ) {
        oneShotScan()
    }

    val highlightCached = CheckboxSetting(
        "Highlight Cached",
        "Outline every cached vein anchor near you. Independent from Enabled.",
        false,
    )

    private val highlightRadius = SliderSetting(
        "Highlight Radius",
        "Maximum horizontal distance to draw highlights for.",
        64.0, 16.0, 192.0, 1.0,
    )

    private val highlightLimit = SliderSetting(
        "Highlight Limit",
        "Maximum number of boxes drawn per frame.",
        512.0, 32.0, 4096.0, 1.0,
    )

    private val clearAllButton = ActionSetting(
        "Delete All Caches",
        "Erase every *_dwarven_data.json / *_glacite_data.json in config/cobalt.",
        "Clear",
    ) {
        deleteAllCacheFiles()
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    /** (typeLabel, area) -> long keys. Mirrors disk; flushed on a timer. */
    private val cache = HashMap<Pair<String, MiningArea>, LinkedHashSet<Long>>()
    private val dirty = HashSet<Pair<String, MiningArea>>()

    private var tickCounter: Long = 0L

    init {
        addSetting(
            info,
            enabled,
            scanRadius,
            scanVertical,
            scanIntervalTicks,
            flushIntervalTicks,
            highlightCached,
            highlightRadius,
            highlightLimit,
            oneShotButton,
            clearAllButton,
        )
        EventBus.register(this)
    }

    override fun onEnable() {
        // Pre-load existing per-(type, area) files into memory so we don't
        // re-write content already on disk.
        loadAllFromDisk()
    }

    override fun onDisable() {
        flushAll()
    }

    private val HIGHLIGHT_DWARVEN = Color(80, 200, 255, 180)
    private val HIGHLIGHT_GLACITE = Color(140, 220, 255, 180)
    private val HIGHLIGHT_FILL = Color(120, 200, 255, 35)

    @SubscribeEvent
    fun onRender(event: WorldRenderEvent.Last) {
        if (!highlightCached.value) return
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        // Lazy load if the user toggled highlight without ever enabling caching.
        if (cache.isEmpty()) loadAllFromDisk()
        if (cache.isEmpty()) return

        val ctx = event.context
        val px = player.x
        val pz = player.z
        val r = highlightRadius.value
        val rSq = r * r
        val limit = highlightLimit.value.toInt().coerceAtLeast(1)
        var drawn = 0

        for ((groupKey, anchors) in cache) {
            val area = groupKey.second
            val stroke = if (area == MiningArea.DWARVEN) HIGHLIGHT_DWARVEN else HIGHLIGHT_GLACITE
            for (key in anchors) {
                if (drawn >= limit) return
                val pos = BlockPos.of(key)
                val dx = (pos.x + 0.5) - px
                val dz = (pos.z + 0.5) - pz
                if (dx * dx + dz * dz > rSq) continue
                val box = AABB(pos).inflate(0.001)
                Render3D.drawStyledBox(
                    context = ctx,
                    box = box,
                    strokeColor = stroke,
                    fillColor = HIGHLIGHT_FILL,
                    esp = false,
                    lineWidth = 1.5f,
                )
                drawn++
            }
        }
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
        if (!enabled.value) return
        tickCounter++

        val interval = scanIntervalTicks.value.toLong().coerceAtLeast(1L)
        if (tickCounter % interval == 0L) {
            sweepAroundPlayer()
        }

        val flushEvery = flushIntervalTicks.value.toLong().coerceAtLeast(1L)
        if (tickCounter % flushEvery == 0L && dirty.isNotEmpty()) {
            flushDirty()
        }
    }

    private fun sweepAroundPlayer() {
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val player = mc.player ?: return

        val origin = player.blockPosition()
        val rH = scanRadius.value.toInt()
        val rV = scanVertical.value.toInt()
        val pos = BlockPos.MutableBlockPos()

        for (dx in -rH..rH) {
            val x = origin.x + dx
            for (dz in -rH..rH) {
                val z = origin.z + dz
                for (dy in -rV..rV) {
                    val y = origin.y + dy
                    pos.set(x, y, z)
                    recordIfVein(level, pos)
                }
            }
        }
    }

    private fun oneShotScan() {
        val mc = Minecraft.getInstance()
        val level = mc.level
        val player = mc.player
        if (level == null || player == null) {
            ChatUtils.sendMessage("World Vein Cacher: not in a world.")
            return
        }
        if (cache.isEmpty()) loadAllFromDisk()

        val origin = player.blockPosition()
        val rH = scanRadius.value.toInt()
        val rV = scanVertical.value.toInt()
        val pos = BlockPos.MutableBlockPos()

        val before = totalCached()
        val start = System.nanoTime()
        for (dx in -rH..rH) {
            val x = origin.x + dx
            for (dz in -rH..rH) {
                val z = origin.z + dz
                for (dy in -rV..rV) {
                    val y = origin.y + dy
                    pos.set(x, y, z)
                    recordIfVein(level, pos)
                }
            }
        }
        flushAll()
        val ms = (System.nanoTime() - start) / 1_000_000L
        val added = totalCached() - before
        ChatUtils.sendMessage("World Vein Cacher: +$added new veins in ${ms}ms (total ${totalCached()}).")
    }

    /** Block IDs that look like vein blocks but are decorative in a specific area. */
    private val AREA_ID_BLACKLIST: Map<MiningArea, Set<String>> = mapOf(
        // Purple stained glass is amethyst-gemstone decoration in Dwarven Mines
        // (Royal Mines / Forge etc.), not actually mineable.
        MiningArea.DWARVEN to setOf(
            "minecraft:purple_stained_glass",
            "minecraft:purple_stained_glass_pane",
        ),
    )

    private fun recordIfVein(level: net.minecraft.world.level.Level, pos: BlockPos) {
        val state = level.getBlockState(pos)
        if (state.isAir) return
        val id = BuiltInRegistries.BLOCK.getKey(state.block).toString()
        if (MiningBlockRegistry.isBlacklisted(id)) return
        val type = MiningBlockRegistry.BLOCK_ID_TO_TYPE[id] ?: return
        val area = MiningAnchorStore.areaForType(type, pos.y)
        if (AREA_ID_BLACKLIST[area]?.contains(id) == true) return
        val groupKey = type to area
        val set = cache.getOrPut(groupKey) { LinkedHashSet() }
        val k = pos.asLong()
        if (set.add(k)) {
            dirty.add(groupKey)
        }
    }

    private fun flushDirty() {
        val configDir = configDir() ?: return
        for (key in dirty) {
            val anchors = cache[key] ?: continue
            writeFile(configDir, key, anchors)
        }
        dirty.clear()
        MiningAnchorStore.invalidateCache()
    }

    private fun flushAll() {
        val configDir = configDir() ?: return
        for ((key, anchors) in cache) {
            writeFile(configDir, key, anchors)
        }
        dirty.clear()
        MiningAnchorStore.invalidateCache()
    }

    private fun writeFile(
        configDir: File,
        key: Pair<String, MiningArea>,
        anchors: Set<Long>,
    ) {
        val (type, area) = key
        val file = File(configDir, "${sanitize(type)}_${area.name.lowercase()}_data.json")
        val arr = JsonArray()
        for (k in anchors) arr.add(k)
        try {
            file.writeText(gson.toJson(arr))
        } catch (_: Exception) {
            // Best-effort write; in-memory copy is retained for retry.
        }
    }

    private fun loadAllFromDisk() {
        cache.clear()
        dirty.clear()
        val dir = configDir() ?: return
        if (!dir.isDirectory) return
        for (area in MiningArea.entries) {
            val suffix = "_${area.name.lowercase()}_data.json"
            val files = dir.listFiles { _, name -> name.endsWith(suffix) } ?: continue
            for (f in files) {
                if (f.name == area.fileName) continue
                val typeSlug = f.name.removeSuffix(suffix)
                val type = unslug(typeSlug) ?: continue
                val anchors = readLongs(f)
                if (anchors.isNotEmpty()) {
                    cache[type to area] = anchors
                }
            }
        }
    }

    private fun readLongs(file: File): LinkedHashSet<Long> {
        if (!file.exists()) return LinkedHashSet()
        return try {
            val json = JsonParser.parseString(file.readText())
            if (!json.isJsonArray) return LinkedHashSet()
            val out = LinkedHashSet<Long>()
            for (el in json.asJsonArray) {
                if (el.isJsonPrimitive && el.asJsonPrimitive.isNumber) {
                    out.add(el.asJsonPrimitive.asLong)
                }
            }
            out
        } catch (_: Exception) {
            LinkedHashSet()
        }
    }

    private fun deleteAllCacheFiles() {
        val dir = configDir() ?: return
        var n = 0
        for (area in MiningArea.entries) {
            val suffix = "_${area.name.lowercase()}_data.json"
            dir.listFiles { _, name -> name.endsWith(suffix) }?.forEach {
                if (it.delete()) n++
            }
        }
        cache.clear()
        dirty.clear()
        MiningAnchorStore.invalidateCache()
        ChatUtils.sendMessage("World Vein Cacher: deleted $n cache files.")
    }

    private fun configDir(): File? {
        val dir = File(Minecraft.getInstance().gameDirectory, "config/cobalt")
        if (!dir.exists()) dir.mkdirs()
        return if (dir.isDirectory) dir else null
    }

    private fun totalCached(): Int = cache.values.sumOf { it.size }

    /** "Mithril (Gray)" → "mithril_gray". */
    private fun sanitize(label: String): String =
        label.lowercase()
            .replace("(", "")
            .replace(")", "")
            .replace(Regex("\\s+"), "_")
            .trim('_')

    /** Reverse [sanitize] using the known type labels in the registry. */
    private fun unslug(slug: String): String? {
        for (label in MiningBlockRegistry.BLOCK_TYPES) {
            if (sanitize(label) == slug) return label
        }
        return null
    }
}
