package org.phantom.api.pathfinder.cache

import org.phantom.api.pathfinder.jni.NativeStateEncoder
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object WorldSerializer {

    private const val MAGIC = 0x5CAFEBAB
    // v5: flag section (4096 shorts) + parallel snow section (4096 bytes).
    private const val VERSION = 5
    private const val LEGACY_FLAGS_VERSION = 4 // flags only, no snow plane
    private const val LEGACY_STATE_ID_VERSION = 3 // stateIds; flags+snow recomputed
    private val CACHE_DIR = File("pathfinder_cache")

    init {
        if (!CACHE_DIR.exists()) CACHE_DIR.mkdirs()
    }

    fun save(name: String, chunks: Map<Long, CachedChunk>) {
        if (!CACHE_DIR.exists()) CACHE_DIR.mkdirs()
        val file = File(CACHE_DIR, "${sanitize(name)}.bin")
        val readyChunks = chunks.filterValues { it.ready }

        DataOutputStream(BufferedOutputStream(GZIPOutputStream(FileOutputStream(file), 8192))).use { out ->
            out.writeInt(MAGIC)
            out.writeInt(VERSION)
            out.writeInt(readyChunks.size)

            val byteBuffer = ByteBuffer.allocate(4096 * 2).order(ByteOrder.BIG_ENDIAN)
            val rawSection = ShortArray(4096)
            val rawSnow = ByteArray(4096)

            for ((key, chunk) in readyChunks) {
                out.writeLong(key)
                out.writeInt(chunk.minY)
                out.writeInt(chunk.maxY)

                val sectionCount = (chunk.maxY - chunk.minY + 15) shr 4
                var sectionMask = 0L
                for (i in 0 until sectionCount) {
                    if (chunk.hasSection(i)) sectionMask = sectionMask or (1L shl i)
                }
                out.writeLong(sectionMask)

                for (i in 0 until sectionCount) {
                    if ((sectionMask and (1L shl i)) != 0L) {
                        rawSection.fill(CachedChunk.AIR_FLAGS)
                        chunk.copySectionFlags(i, rawSection, 0)
                        byteBuffer.clear()
                        byteBuffer.asShortBuffer().put(rawSection)
                        out.write(byteBuffer.array())

                        // v5: parallel snow plane (zero-filled when absent).
                        chunk.copySectionSnow(i, rawSnow, 0)
                        out.write(rawSnow)
                    }
                }
            }
        }
    }

    fun load(name: String): ConcurrentHashMap<Long, CachedChunk>? {
        val file = File(CACHE_DIR, "${sanitize(name)}.bin")
        if (!file.exists()) return null

        return try {
            DataInputStream(BufferedInputStream(GZIPInputStream(FileInputStream(file)))).use { input ->
                if (input.readInt() != MAGIC) return null
                val version = input.readInt()
                if (version != VERSION &&
                    version != LEGACY_FLAGS_VERSION &&
                    version != LEGACY_STATE_ID_VERSION
                ) return null

                val count = input.readInt()
                val map = ConcurrentHashMap<Long, CachedChunk>(count)
                // v3 stored 4-byte stateIds; v4/v5 store 2-byte flags.
                val flagByteSize = if (version == LEGACY_STATE_ID_VERSION) 4096 * 4 else 4096 * 2
                val rawBytes = ByteArray(flagByteSize)
                val rawSnow = ByteArray(4096)

                repeat(count) {
                    val key = input.readLong()
                    val minY = input.readInt()
                    val maxY = input.readInt()
                    val chunk = CachedChunk(minY, maxY)
                    val sectionMask = input.readLong()
                    val sectionCount = (maxY - minY + 15) shr 4

                    for (i in 0 until sectionCount) {
                        if ((sectionMask and (1L shl i)) != 0L) {
                            input.readFully(rawBytes)
                            chunk.setSection(i, decodeSection(version, rawBytes))
                            when (version) {
                                VERSION -> {
                                    // Parallel snow block follows the flags.
                                    input.readFully(rawSnow)
                                    if (rawSnow.any { it.toInt() != 0 }) {
                                        chunk.setSnowSection(i, rawSnow.copyOf())
                                    }
                                }
                                LEGACY_STATE_ID_VERSION ->
                                    chunk.setSnowSection(i, decodeSnowFromStateIds(rawBytes))
                                // LEGACY_FLAGS_VERSION: no snow data, safe to skip.
                            }
                        }
                    }
                    chunk.ready = true
                    map[key] = chunk
                }
                map
            }
        } catch (e: Exception) {
            System.err.println("[Phantom] Ignoring corrupt world cache '${file.name}': ${e.javaClass.simpleName}: ${e.message}")
            if (!file.delete()) {
                System.err.println("[Phantom] Failed to delete corrupt world cache '${file.name}'.")
            }
            null
        }
    }

    private fun decodeSection(version: Int, rawBytes: ByteArray): ShortArray {
        return if (version != LEGACY_STATE_ID_VERSION) {
            val section = ShortArray(4096)
            ByteBuffer.wrap(rawBytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(section)
            section
        } else {
            val stateIds = IntArray(4096)
            ByteBuffer.wrap(rawBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(stateIds)
            ShortArray(4096) { index -> NativeStateEncoder.flagsShortForStateId(stateIds[index]) }
        }
    }

    /** Recompute the snow plane from a v3 stateId section; null if snow-free. */
    private fun decodeSnowFromStateIds(rawBytes: ByteArray): ByteArray? {
        val stateIds = IntArray(4096)
        ByteBuffer.wrap(rawBytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(stateIds)
        var plane: ByteArray? = null
        for (index in 0 until 4096) {
            val snow = NativeStateEncoder.snowLayersForStateId(stateIds[index])
            if (snow != 0) {
                val p = plane ?: ByteArray(4096).also { plane = it }
                p[index] = snow.toByte()
            }
        }
        return plane
    }

    private fun sanitize(name: String): String = name.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
}
