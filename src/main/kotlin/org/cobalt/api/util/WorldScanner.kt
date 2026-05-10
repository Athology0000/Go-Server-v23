package org.cobalt.api.util

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
import net.minecraft.world.level.chunk.LevelChunk
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.BlockChangeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.client.TickEvent
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

object WorldScanner {

    private val mc = Minecraft.getInstance()
    private val lock = ReentrantLock()
    private val chunks = HashMap<Long, MutableList<BlockPos>>()

    @Volatile var enabled = false
        private set

    private var targets: List<String> = emptyList()
    private var bounds: Bounds? = null

    private val pendingChunks = ConcurrentLinkedQueue<Pair<Int, Int>>()
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "WorldScanner").also { it.isDaemon = true }
    }

    private var lastLevel: net.minecraft.client.multiplayer.ClientLevel? = null
    private var lastDimension: net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>? = null

    data class Bounds(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int, val minZ: Int, val maxZ: Int)

    init {
        EventBus.register(this)
    }

    fun toggle(state: Boolean? = null) {
        val next = state ?: !enabled
        if (next == enabled) return
        enabled = next
        if (!enabled) {
            pendingChunks.clear()
            clear()
        }
    }

    fun setTargets(targetList: List<String>?) {
        if (targetList.isNullOrEmpty()) {
            targets = emptyList()
            clear()
            return
        }
        targets = targetList.map { it.lowercase() }
        clear()
    }

    fun setBounds(minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int) {
        bounds = Bounds(minX, maxX, minY, maxY, minZ, maxZ)
    }

    fun clearBounds() {
        bounds = null
    }

    fun clear() {
        lock.lock()
        try { chunks.clear() } finally { lock.unlock() }
    }

    fun getBlocks(): List<BlockPos> {
        lock.lock()
        try { return chunks.values.flatten() } finally { lock.unlock() }
    }

    fun getChunkMap(): Map<Long, List<BlockPos>> {
        lock.lock()
        try { return chunks.mapValues { it.value.toList() } } finally { lock.unlock() }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Incoming) {
        if (!enabled) return
        val packet = event.packet
        if (packet is ClientboundLevelChunkWithLightPacket) {
            pendingChunks.add(Pair(packet.x, packet.z))
        }
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (!enabled) return
        val level = mc.level ?: return

        if (level !== lastLevel) {
            lastLevel = level
            pendingChunks.clear()
            clear()
        }

        val dimension = level.dimension()
        if (lastDimension != dimension) {
            lastDimension = dimension
            pendingChunks.clear()
            clear()
        }

        while (pendingChunks.isNotEmpty()) {
            val (cx, cz) = pendingChunks.poll() ?: break
            val chunk = runCatching { level.chunkSource.getChunk(cx, cz, false) }.getOrNull() ?: continue
            val minY = level.minY
            executor.submit { searchChunk(chunk, cx, cz, minY) }
        }
    }

    @SubscribeEvent
    fun onBlockChange(event: BlockChangeEvent) {
        if (!enabled) return
        val pos = event.pos
        if (!isInBounds(pos.x, pos.y, pos.z)) return
        val id = event.newBlock.block.descriptionId.lowercase()
        val isTarget = matchesTarget(id)
        val key = chunkKey(pos.x shr 4, pos.z shr 4)

        executor.submit {
            lock.lock()
            try {
                val list = chunks[key]
                if (isTarget) {
                    if (list == null) {
                        chunks[key] = mutableListOf(pos.immutable())
                    } else if (list.none { it.x == pos.x && it.y == pos.y && it.z == pos.z }) {
                        list.add(pos.immutable())
                    }
                } else if (list != null) {
                    list.removeAll { it.x == pos.x && it.y == pos.y && it.z == pos.z }
                    if (list.isEmpty()) chunks.remove(key)
                }
            } finally {
                lock.unlock()
            }
        }
    }

    private fun searchChunk(chunk: LevelChunk, cx: Int, cz: Int, minBuildHeight: Int) {
        try {
            if (!enabled) return
            val found = ArrayList<BlockPos>()
            val sections = chunk.sections

            for (sIndex in sections.indices) {
                val section = sections[sIndex] ?: continue
                if (section.hasOnlyAir()) continue

                for (ly in 0..15) {
                    for (lx in 0..15) {
                        for (lz in 0..15) {
                            val wx = (cx shl 4) + lx
                            val wy = minBuildHeight + (sIndex shl 4) + ly
                            val wz = (cz shl 4) + lz

                            if (!isInBounds(wx, wy, wz)) continue

                            val state = section.getBlockState(lx, ly, lz)
                            if (state.isAir) continue

                            val id = state.block.descriptionId.lowercase()
                            if (matchesTarget(id)) found.add(BlockPos(wx, wy, wz))
                        }
                    }
                }
            }

            if (!enabled) return
            val key = chunkKey(cx, cz)
            lock.lock()
            try {
                if (found.isNotEmpty()) chunks[key] = found
                else chunks.remove(key)
            } finally {
                lock.unlock()
            }
        } catch (_: Throwable) {}
    }

    private fun isInBounds(x: Int, y: Int, z: Int): Boolean {
        val b = bounds ?: return true
        return x >= b.minX && x <= b.maxX && y >= b.minY && y <= b.maxY && z >= b.minZ && z <= b.maxZ
    }

    private fun matchesTarget(id: String): Boolean = targets.any { id.contains(it) }

    private fun chunkKey(cx: Int, cz: Int): Long =
        (cx.toLong() and 0xFFFFFFFFL) or ((cz.toLong() and 0xFFFFFFFFL) shl 32)
}
