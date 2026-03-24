package org.cobalt.internal.diana

import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import java.util.Collections

/**
 * Thread-safe collector of Diana CRIT particle positions.
 * addParticle() is called from the netty thread; all other methods from the tick thread.
 */
object DianaParticleTracker {

  private val particles: MutableList<Vec3> = Collections.synchronizedList(mutableListOf())

  fun reset() {
    synchronized(particles) { particles.clear() }
  }

  /** Called from the netty thread — Collections.synchronizedList makes add() thread-safe. */
  fun addParticle(x: Double, y: Double, z: Double) {
    particles.add(Vec3(x, y, z))
  }

  /** Compound operation — must synchronize explicitly. */
  fun count(): Int = synchronized(particles) { particles.size }

  /**
   * Averages normalized direction vectors from [playerPos] to each collected particle,
   * extends the ray 40 blocks forward, then snaps Y to the ground heightmap.
   * Returns null if there are too few particles or the average direction is degenerate.
   */
  fun getBurrowPos(playerPos: Vec3, level: Level): Vec3? {
    val snapshot: List<Vec3>
    synchronized(particles) { snapshot = particles.toList() }
    if (snapshot.size < 2) return null  // defence-in-depth; caller checks count() >= minParticles

    val dirs = snapshot.map { it.subtract(playerPos).normalize() }
    val avgDir = dirs.fold(Vec3.ZERO) { acc, v -> acc.add(v) }.scale(1.0 / dirs.size)
    if (avgDir.lengthSqr() < 1e-6) return null
    val dir = avgDir.normalize()

    val estX = playerPos.x + dir.x * 40.0
    val estZ = playerPos.z + dir.z * 40.0
    val groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, estX.toInt(), estZ.toInt()).toDouble()
    return Vec3(estX, groundY, estZ)
  }
}
