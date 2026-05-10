package org.cobalt.api.pathfinder.jni

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.level.chunk.LevelChunk
import org.cobalt.api.event.EventBus
import org.cobalt.api.pathfinder.cache.CachedWorld

object ChunkSerializer {

    fun register() {
        CachedWorld.register()
        EventBus.register(CachedWorld)
    }

    fun onChunkLoad(world: ClientLevel, chunk: LevelChunk) {
        // Delegated to CachedWorld via CHUNK_LOAD event registered in register()
    }

    fun invalidate() {
        CachedWorld.clear()
        NativePathfinderJNI.clearWorld()
    }
}
