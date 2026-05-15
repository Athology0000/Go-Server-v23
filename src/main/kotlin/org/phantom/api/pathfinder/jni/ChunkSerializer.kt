package org.phantom.api.pathfinder.jni

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.level.chunk.LevelChunk
import org.phantom.api.event.EventBus
import org.phantom.api.pathfinder.cache.CachedWorld

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
