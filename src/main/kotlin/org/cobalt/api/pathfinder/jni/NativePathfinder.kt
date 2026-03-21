package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import org.cobalt.pathfinder.NativePathfinderBridge

/**
 * Singleton wrapper around the native pathfinder engine.
 *
 * Usage:
 *   NativePathfinder.init()                      // once at mod startup
 *   NativePathfinder.setTarget(x, y, z)
 *   // each tick:
 *   NativePathfinder.tick()?.applyToPlayer()
 *   NativePathfinder.destroy()                   // on mod unload
 */
object NativePathfinder {

    private var handle: Long = 0L

    val isInitialized: Boolean get() = handle != 0L

    fun init() {
        if (handle != 0L) return
        handle = NativePathfinderBridge.createEngine()
    }

    fun destroy() {
        if (handle == 0L) return
        NativePathfinderBridge.destroyEngine(handle)
        handle = 0L
    }

    fun setTarget(x: Double, y: Double, z: Double) {
        if (handle == 0L) return
        NativePathfinderBridge.setTarget(handle, x, y, z)
    }

    fun setTargetWithRadius(x: Double, y: Double, z: Double, radius: Double) {
        if (handle == 0L) return
        NativePathfinderBridge.setTargetWithRadius(handle, x, y, z, radius)
    }

    /**
     * @param waypoints list of Vec3-like triples as flat [x0,y0,z0, x1,y1,z1, ...]
     * @param loop whether to loop the route
     * @param profile MovementProfile ordinal
     */
    fun setRoute(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile) {
        if (handle == 0L) return
        NativePathfinderBridge.setRoute(handle, waypoints, loop, profile.ordinal)
    }

    fun stop() {
        if (handle == 0L) return
        NativePathfinderBridge.stop(handle)
    }

    val status: PathStatus
        get() {
            if (handle == 0L) return PathStatus.IDLE
            val ordinal = NativePathfinderBridge.getStatus(handle)
            return PathStatus.entries.getOrElse(ordinal) { PathStatus.FAILED }
        }

    /**
     * Must be called every tick (TickEvent.Start) on the main thread.
     * Returns null if the engine is not initialized or the world is unavailable.
     */
    fun tick(): PathCommand? {
        if (handle == 0L) return null
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return null

        val world = WorldBufferSerializer.serialize(mc) ?: return null

        val r = NativePathfinderBridge.update(
            handle,
            world.buf,
            world.bx, world.by, world.bz,
            player.x, player.y, player.z,
            player.yRot, player.xRot,
            player.onGround()
        )

        // int[10]: [0]=forward [1]=back [2]=jump [3]=sneak [4]=sprint
        //          [5]=targetYaw (float bits) [6]=targetPitch (float bits)
        //          [7]=PathStatus ordinal [8]=ActionType ordinal
        //          [9]=distanceToTarget (float bits)
        val statusOrdinal = r[7]
        val actionOrdinal = r[8]

        return PathCommand(
            forward  = r[0] != 0,
            back     = r[1] != 0,
            jump     = r[2] != 0,
            sneak    = r[3] != 0,
            sprint   = r[4] != 0,
            targetYaw   = java.lang.Float.intBitsToFloat(r[5]),
            targetPitch = java.lang.Float.intBitsToFloat(r[6]),
            status      = PathStatus.entries.getOrElse(statusOrdinal) { PathStatus.FAILED },
            activeAction = ActionType.entries.getOrElse(actionOrdinal) { ActionType.WALK },
            distanceToTarget = java.lang.Float.intBitsToFloat(r[9])
        )
    }
}
