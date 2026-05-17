package org.phantom.api.pathfinder.cache

import org.phantom.api.pathfinder.jni.NativeVoxelFlags

class CachedChunk(
    @JvmField val minY: Int,
    @JvmField val maxY: Int
) {

    companion object {
        @JvmField
        val AIR_FLAGS: Short = (
            NativeVoxelFlags.PASSABLE or
                NativeVoxelFlags.PASSABLE_FLY or
                NativeVoxelFlags.ETHER_PASSABLE or
                NativeVoxelFlags.ETHER_TELEPORT_CLEAR
            ).toShort()
    }

    private val sections: Array<ShortArray?> = arrayOfNulls((maxY - minY + 15) shr 4)
    // Parallel snow-layer plane, allocated lazily per section alongside flags.
    // 0 = not snow, 1..7 = SnowLayerBlock layers. Kept out of the flag word.
    private val snowSections: Array<ByteArray?> = arrayOfNulls((maxY - minY + 15) shr 4)

    @Volatile
    @JvmField
    var ready: Boolean = false

    fun getFlags(localX: Int, y: Int, localZ: Int): Short {
        if (y < minY || y >= maxY) return AIR_FLAGS
        val sectionIndex = (y - minY) shr 4
        if (sectionIndex < 0 || sectionIndex >= sections.size) return AIR_FLAGS
        val section = sections[sectionIndex] ?: return AIR_FLAGS
        return section[((y and 15) shl 8) or ((localZ and 15) shl 4) or (localX and 15)]
    }

    fun setFlags(localX: Int, y: Int, localZ: Int, flags: Short) {
        if (y < minY || y >= maxY) return
        val sectionIndex = (y - minY) shr 4
        if (sectionIndex < 0 || sectionIndex >= sections.size) return
        var section = sections[sectionIndex]
        if (section == null) {
            section = ShortArray(4096) { AIR_FLAGS }
            sections[sectionIndex] = section
        }
        section[((y and 15) shl 8) or ((localZ and 15) shl 4) or (localX and 15)] = flags
    }

    fun getSnow(localX: Int, y: Int, localZ: Int): Byte {
        if (y < minY || y >= maxY) return 0
        val sectionIndex = (y - minY) shr 4
        if (sectionIndex < 0 || sectionIndex >= snowSections.size) return 0
        val section = snowSections[sectionIndex] ?: return 0
        return section[((y and 15) shl 8) or ((localZ and 15) shl 4) or (localX and 15)]
    }

    fun setSnow(localX: Int, y: Int, localZ: Int, snowLayers: Byte) {
        if (y < minY || y >= maxY) return
        val sectionIndex = (y - minY) shr 4
        if (sectionIndex < 0 || sectionIndex >= snowSections.size) return
        // Don't allocate a snow plane just to store "no snow".
        if (snowLayers.toInt() == 0 && snowSections[sectionIndex] == null) return
        var section = snowSections[sectionIndex]
        if (section == null) {
            section = ByteArray(4096)
            snowSections[sectionIndex] = section
        }
        section[((y and 15) shl 8) or ((localZ and 15) shl 4) or (localX and 15)] = snowLayers
    }

    fun hasSection(index: Int): Boolean = index in sections.indices && sections[index] != null

    fun hasSnowSection(index: Int): Boolean =
        index in snowSections.indices && snowSections[index] != null

    fun copySectionFlags(index: Int, dest: ShortArray, destOffset: Int) {
        val section = if (index in sections.indices) sections[index] else null
        section?.copyInto(dest, destOffset)
    }

    /** Copies this section's snow plane into dest, or zero-fills it if absent. */
    fun copySectionSnow(index: Int, dest: ByteArray, destOffset: Int) {
        val section = if (index in snowSections.indices) snowSections[index] else null
        if (section != null) {
            section.copyInto(dest, destOffset)
        } else {
            dest.fill(0, destOffset, destOffset + 4096)
        }
    }

    fun setSection(sectionIndex: Int, data: ShortArray) {
        if (sectionIndex in sections.indices) {
            sections[sectionIndex] = data
        }
    }

    fun setSnowSection(sectionIndex: Int, data: ByteArray?) {
        if (sectionIndex in snowSections.indices) {
            snowSections[sectionIndex] = data
        }
    }
}
