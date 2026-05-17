package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.PacketEvent
import org.phantom.internal.pathfinding.OverlayRenderEngine.Color
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Thread-safe detector of Diana Griffin burrow locations via their particle packet signatures.
 *
 * SkyHanni reference: GriffinBurrowParticleFinder.kt:
 *   START    -> ENCHANTED_HIT, count=4, speed=0.01, offset=(0.5, 0.1, 0.5)
 *   MOB      -> CRIT,          count=3, speed=0.01, offset=(0.5, 0.1, 0.5)
 *   TREASURE -> DRIPPING_LAVA, count=2, speed=0.01, offset=(0.35, 0.1, 0.35)
 *   ENCHANT  -> ENCHANT,       count=5, speed=0.05, offset=(0.5, 0.4, 0.5)
 *
 * The burrow is only exposed after an ENCHANT marker and one typed marker agree on the
 * same block, which removes the false positives caused by nearby combat CRIT particles.
 */
object DianaParticleTracker {

    private val mc = Minecraft.getInstance()

    enum class BurrowType(val displayName: String, val rgb: Int) {
        START("Start", 0x55FF55),
        MOB("Mob", 0xFF5555),
        TREASURE("Treasure", 0xFFAA00),
        GUESS("Guess", 0xFFFFFF),
        UNKNOWN("Burrow", 0xFFD700);

        fun toColor(alpha: Int): Color {
            return Color((rgb shr 16) and 0xFF, (rgb shr 8) and 0xFF, rgb and 0xFF, alpha)
        }
    }

    data class BurrowRecord(
        val bx: Int,
        val by: Int,
        val bz: Int,
        var type: BurrowType = BurrowType.UNKNOWN,
        var hasEnchant: Boolean = false,
        var found: Boolean = false,
        var lastSeenMs: Long,
    )

    // Keyed by packed (bx, bz); one record per unique XZ block column.
    private val seenBurrows = HashMap<Long, BurrowRecord>()

    @Volatile private var activationPos: Vec3? = null
    @Volatile private var lastParticleMs: Long = 0L
    @Volatile private var packetCount: Int = 0

    init { EventBus.register(this) }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Incoming) {
        if (event.packet is ClientboundRespawnPacket) {
            reset()
            return
        }

        val pkt = event.packet as? ClientboundLevelParticlesPacket ?: return
        val marker = markerFor(pkt) ?: return

        val player = mc.player ?: return
        if (activationPos == null) activationPos = player.position()

        val bx = pkt.x.roundToInt()
        val by = pkt.y.roundToInt() - 1
        val bz = pkt.z.roundToInt()
        val key = (bx.toLong() shl 32) or (bz.toLong() and 0xFFFFFFFFL)

        val now = System.currentTimeMillis()
        synchronized(seenBurrows) {
            val rec = seenBurrows.getOrPut(key) { BurrowRecord(bx, by, bz, lastSeenMs = now) }
            when (marker) {
                ParticleMarker.ENCHANT -> rec.hasEnchant = true
                ParticleMarker.START -> rec.type = BurrowType.START
                ParticleMarker.MOB -> rec.type = BurrowType.MOB
                ParticleMarker.TREASURE -> rec.type = BurrowType.TREASURE
            }
            rec.found = rec.hasEnchant && rec.type != BurrowType.UNKNOWN
            rec.lastSeenMs = now
        }
        lastParticleMs = now
        packetCount++
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val message = ChatFormatting.stripFormatting(event.message ?: return) ?: return
        if (message.startsWith("You dug out a Griffin Burrow!") ||
            message.startsWith("You finished the Griffin burrow chain!") ||
            message == "Poof! You have cleared your griffin burrows!"
        ) {
            reset()
        }
    }

    fun reset() {
        synchronized(seenBurrows) { seenBurrows.clear() }
        activationPos = null
        lastParticleMs = 0L
        packetCount = 0
    }

    /** Remove a single burrow entry so the helper stops rendering it after it's been dug. */
    fun removeBurrow(bx: Int, bz: Int) {
        val key = (bx.toLong() shl 32) or (bz.toLong() and 0xFFFFFFFFL)
        synchronized(seenBurrows) { seenBurrows.remove(key) }
    }

    fun addGuess(pos: Vec3, type: BurrowType = BurrowType.GUESS) {
        val bx = pos.x.roundToInt()
        val by = pos.y.roundToInt()
        val bz = pos.z.roundToInt()
        val key = (bx.toLong() shl 32) or (bz.toLong() and 0xFFFFFFFFL)
        val now = System.currentTimeMillis()
        synchronized(seenBurrows) {
            val rec = seenBurrows.getOrPut(key) { BurrowRecord(bx, by, bz, lastSeenMs = now) }
            rec.type = type
            rec.hasEnchant = true
            rec.found = true
            rec.lastSeenMs = now
        }
        lastParticleMs = now
    }

    /** Number of matching CRIT packets received - used by the macro threshold check. */
    fun count(): Int = packetCount

    fun getLastParticleMs(): Long = lastParticleMs

    fun getActivationPos(): Vec3? = activationPos

    fun getBurrowRecords(level: Level, expireMs: Long = Long.MAX_VALUE): List<Pair<Vec3, BurrowType>> {
        val now = System.currentTimeMillis()
        val snapshot: List<BurrowRecord>
        synchronized(seenBurrows) {
            snapshot = seenBurrows.values
                .filter { it.found && now - it.lastSeenMs <= expireMs }
                .sortedByDescending { it.lastSeenMs }
                .toList()
        }

        return snapshot.map { rec ->
            val heightmapY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, rec.bx, rec.bz) - 1
            val surfaceY = if (heightmapY > rec.by - 3 && heightmapY < rec.by + 5) heightmapY else rec.by
            Vec3(rec.bx + 0.5, surfaceY.toDouble(), rec.bz + 0.5) to rec.type
        }
    }

    /**
     * All known burrow positions whose last-seen timestamp is within [expireMs] ms,
     * sorted most-recently-seen first. Y is snapped to surface via heightmap.
     */
    fun getBurrowPositions(level: Level, expireMs: Long = Long.MAX_VALUE): List<Vec3> {
        return getBurrowRecords(level, expireMs).map { it.first }
    }

    /** Most recently confirmed burrow position, or null. */
    fun getBurrowPos(level: Level): Vec3? = getBurrowPositions(level).firstOrNull()

    fun getBurrowRecord(level: Level): Pair<Vec3, BurrowType>? = getBurrowRecords(level).firstOrNull()

    private fun markerFor(packet: ClientboundLevelParticlesPacket): ParticleMarker? {
        val type = packet.particle.type
        val count = packet.count
        val speed = packet.maxSpeed
        val x = packet.xDist
        val y = packet.yDist
        val z = packet.zDist

        return when {
            type == ParticleTypes.ENCHANTED_HIT &&
                count == 4 && close(speed, 0.01f) && close(x, 0.5f) && close(y, 0.1f) && close(z, 0.5f) ->
                ParticleMarker.START

            type == ParticleTypes.CRIT &&
                count == 3 && close(speed, 0.01f) && close(x, 0.5f) && close(y, 0.1f) && close(z, 0.5f) ->
                ParticleMarker.MOB

            type == ParticleTypes.DRIPPING_LAVA &&
                count == 2 && close(speed, 0.01f) && close(x, 0.35f) && close(y, 0.1f) && close(z, 0.35f) ->
                ParticleMarker.TREASURE

            type == ParticleTypes.ENCHANT &&
                count == 5 && close(speed, 0.05f) && close(x, 0.5f) && close(y, 0.4f) && close(z, 0.5f) ->
                ParticleMarker.ENCHANT

            else -> null
        }
    }

    private fun close(actual: Float, expected: Float): Boolean = abs(actual - expected) <= 0.005f

    private enum class ParticleMarker {
        START,
        MOB,
        TREASURE,
        ENCHANT,
    }
}
