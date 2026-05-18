package org.phantom.internal.diana

import net.minecraft.world.phys.Vec3
import org.phantom.api.pathfinder.jni.NativePathfinderJNI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Thin off-thread wrapper around the native teleport-first hybrid planner.
 *
 * The Diana macro submits a plan request when it (re)plans toward a burrow and
 * polls [poll] on later ticks. The native planner reads the globally-synced
 * world snapshot (kept current every tick by CachedWorld), so no world
 * serialization is needed here.
 */
object DianaTeleportPlanner {

    enum class HopType { WALK, AOTV, ETHERWARP }

    data class Hop(
        /** Landing block of this step (integer block coords). */
        val x: Int,
        val y: Int,
        val z: Int,
        val type: HopType,
        /** Cast angles — only meaningful for AOTV / ETHERWARP hops. */
        val yaw: Float,
        val pitch: Float,
        /** AOTV hop whose landing is mid-air (sky chain) — needs fast timing. */
        val airborne: Boolean = false,
    ) {
        /** Standing position the player will occupy at this node. */
        fun standPos(): Vec3 = Vec3(x + 0.5, y.toDouble(), z + 0.5)
    }

    data class Plan(
        val hops: List<Hop>,
        val reachedGoal: Boolean,
        val timeMs: Long,
        val nodesExplored: Int,
    )

    private val executor: ExecutorService = Executors.newSingleThreadExecutor { r ->
        Thread(r, "diana-teleport-planner").apply { isDaemon = true }
    }

    @Volatile private var future: Future<Plan?>? = null

    /** True while a plan request is still computing. */
    val isPlanning: Boolean get() = future?.isDone == false

    /** Submit a new plan request, replacing any in-flight one. */
    fun submit(
        start: Vec3,
        goal: Vec3,
        goalReachedRadius: Double,
        transmissionRange: Double,
        etherwarpRange: Double,
        availableMana: Int,
        aotvEnabled: Boolean,
        etherwarpEnabled: Boolean,
    ) {
        future?.cancel(false)
        val sx = Math.floor(start.x).toInt()
        val sy = Math.floor(start.y).toInt()
        val sz = Math.floor(start.z).toInt()
        val gx = Math.floor(goal.x).toInt()
        val gy = Math.floor(goal.y).toInt()
        val gz = Math.floor(goal.z).toInt()
        future = executor.submit<Plan?> {
            val res = NativePathfinderJNI.findTeleportFirstPath(
                sx, sy, sz,
                gx, gy, gz,
                goalReachedRadius,
                transmissionRange,
                etherwarpRange,
                availableMana,
                TRANSMISSION_MANA,
                ETHERWARP_MANA,
                aotvEnabled,
                etherwarpEnabled,
                MAX_ITERATIONS,
                MAX_NODES,
            ) ?: return@submit null

            val count = res.hopTypes.size
            val hops = ArrayList<Hop>(count)
            for (i in 0 until count) {
                val px = res.path[i * 3]
                val py = res.path[i * 3 + 1]
                val pz = res.path[i * 3 + 2]
                val code = res.hopTypes[i]
                val type = when (code) {
                    1, 3 -> HopType.AOTV
                    2 -> HopType.ETHERWARP
                    else -> HopType.WALK
                }
                hops.add(Hop(px, py, pz, type, res.yaw[i], res.pitch[i], code == 3))
            }
            Plan(hops, res.reachedGoal, res.timeMs, res.nodesExplored)
        }
    }

    /**
     * Returns the finished plan and clears it, or null if still computing /
     * no request outstanding / the search failed.
     */
    fun poll(): Plan? {
        val f = future ?: return null
        if (!f.isDone) return null
        future = null
        return try {
            f.get()
        } catch (_: Exception) {
            null
        }
    }

    fun cancel() {
        future?.cancel(false)
        future = null
    }

    // Maxed-AOTV mana costs (matches the reference + native defaults).
    private const val TRANSMISSION_MANA = 27
    private const val ETHERWARP_MANA = 108
    // Kept well under the native hard caps so the background plan returns in a
    // few ms instead of flood-filling forever (the original stall bug).
    private const val MAX_ITERATIONS = 16_000
    private const val MAX_NODES = 8_000
}
