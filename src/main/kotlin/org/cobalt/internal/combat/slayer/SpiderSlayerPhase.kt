package org.cobalt.internal.combat.slayer

import java.util.UUID
import kotlin.math.sqrt
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation

internal object SpiderSlayerPhase {
  private var hatchlingsActive = false
  private var hatchlingsActivatedTick = -1L
  private var hatchlingsUntilTick = -1L
  private var hatchlingsBossId: UUID? = null
  private var hatchlingsBossAnchor: Vec3? = null
  private val hatchlingTargetIds = LinkedHashSet<UUID>()
  private val hatchlingShotCounts = mutableMapOf<UUID, Int>()
  private var selectedHatchlingTargetId: UUID? = null

  val bossAnchor: Vec3? get() = hatchlingsBossAnchor
  val selectedTargetId: UUID? get() = selectedHatchlingTargetId

  fun activate(nowTick: Long, boss: LivingEntity?, fallbackBossPos: Vec3?) {
    hatchlingsActive = true
    hatchlingsActivatedTick = nowTick
    hatchlingsUntilTick =
      if (nowTick >= 0L) nowTick + HATCHLING_PHASE_FALLBACK_TICKS else -1L
    hatchlingsBossId = boss?.uuid
    hatchlingsBossAnchor = boss?.position() ?: fallbackBossPos
    hatchlingTargetIds.clear()
    hatchlingShotCounts.clear()
    selectedHatchlingTargetId = null
  }
  fun clear() {
    hatchlingsActive = false
    hatchlingsActivatedTick = -1L
    hatchlingsUntilTick = -1L
    hatchlingsBossId = null
    hatchlingsBossAnchor = null
    hatchlingTargetIds.clear()
    hatchlingShotCounts.clear()
    selectedHatchlingTargetId = null
  }

  fun update(
    level: ClientLevel,
    boss: LivingEntity?,
    enabled: Boolean,
    slayerBossActive: Boolean,
    isSlayerBoss: (LivingEntity) -> Boolean,
    matchesPhaseTarget: (LivingEntity) -> Boolean,
    horizontalDistSq: (Double, Double, Double, Double) -> Double,
  ) {
    if (!enabled) {
      clear()
      return
    }
    if (!hatchlingsActive) return
    if (!slayerBossActive) {
      clear()
      return
    }

    val trackedBoss = resolveTrackedBoss(level, boss, isSlayerBoss)
    if (trackedBoss != null) {
      if (hatchlingsBossId == null) hatchlingsBossId = trackedBoss.uuid
      if (hatchlingsBossAnchor == null) hatchlingsBossAnchor = trackedBoss.position()
      val anchor = hatchlingsBossAnchor
      if (anchor != null && trackedBoss.position().distanceTo(anchor) >= HATCHLING_CLEAR_DISTANCE) {
        clear()
        return
      }
    }

    if (hatchlingsUntilTick >= 0L && level.gameTime >= hatchlingsUntilTick) {
      if (trackedBoss != null && hasNearbyAdds(level, trackedBoss, isSlayerBoss, matchesPhaseTarget, horizontalDistSq)) {
        hatchlingsUntilTick = level.gameTime + HATCHLING_PHASE_REFRESH_TICKS
      } else {
        clear()
      }
    }
  }

  fun isActive(nowTick: Long, enabled: Boolean): Boolean {
    if (!enabled || !hatchlingsActive) return false
    return hatchlingsUntilTick < 0L || nowTick < hatchlingsUntilTick
  }

  fun resolveTrackedBoss(
    level: ClientLevel,
    fallbackBoss: LivingEntity?,
    isSlayerBoss: (LivingEntity) -> Boolean,
  ): LivingEntity? {
    val trackedId = hatchlingsBossId
    if (trackedId != null) {
      val tracked = level.entitiesForRendering().firstOrNull { it.uuid == trackedId } as? LivingEntity
      if (tracked != null && tracked.isAlive && tracked.health > 0f) {
        return tracked
      }
    }
    return fallbackBoss?.takeIf { it.isAlive && it.health > 0f && isSlayerBoss(it) }
  }

  fun hasNearbyAdds(
    level: ClientLevel,
    boss: LivingEntity,
    isSlayerBoss: (LivingEntity) -> Boolean,
    matchesPhaseTarget: (LivingEntity) -> Boolean,
    horizontalDistSq: (Double, Double, Double, Double) -> Double,
  ): Boolean {
    val bossPos = boss.position()
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<LivingEntity>()
      .filter { it.isAlive && it.health > 0f }
      .filter { entity -> !isSlayerBoss(entity) }
      .any { entity ->
        matchesPhaseTarget(entity) &&
          isShotEligible(entity) &&
          horizontalDistSq(entity.x, entity.z, bossPos.x, bossPos.z) <= PHASE_ADD_SEARCH_RADIUS_SQ
      }
  }

  fun isShotEligible(living: LivingEntity): Boolean {
    val shots = hatchlingShotCounts[living.uuid] ?: 0
    if (shots >= HATCHLING_MAX_SHOTS_PER_TARGET) return false
    if (living.uuid in hatchlingTargetIds) return true
    return hatchlingTargetIds.size < HATCHLING_MAX_TARGETS
  }

  fun registerSelectedTarget(living: LivingEntity) {
    hatchlingTargetIds.add(living.uuid)
    selectedHatchlingTargetId = living.uuid
  }

  fun registerShot(target: LivingEntity): Boolean {
    hatchlingTargetIds.add(target.uuid)
    val shots = (hatchlingShotCounts[target.uuid] ?: 0) + 1
    hatchlingShotCounts[target.uuid] = shots
    selectedHatchlingTargetId = target.uuid
    if (shots >= HATCHLING_MAX_SHOTS_PER_TARGET && selectedHatchlingTargetId == target.uuid) {
      selectedHatchlingTargetId = null
    }
    return shots >= HATCHLING_MAX_SHOTS_PER_TARGET
  }

  fun shouldReleaseToBossOnNoTarget(nowTick: Long): Boolean {
    if (!hatchlingsActive || hatchlingsActivatedTick < 0L || nowTick < 0L) return false
    return nowTick - hatchlingsActivatedTick >= HATCHLING_NO_TARGET_RELEASE_TICKS
  }

  fun aimRotation(player: Player, target: LivingEntity): Rotation {
    val aimPoint = Vec3(
      target.x,
      target.eyeY + HATCHLING_AIM_Y_OFFSET,
      target.z
    )
    val base = AngleUtils.getRotation(player.eyePosition, aimPoint)
    return Rotation(base.yaw, (base.pitch - HATCHLING_EXTRA_LOOK_UP).coerceIn(-90f, 90f))
  }

  fun targetNameMatches(normalizedName: String): Boolean =
    addNameMatches(normalizedName) || shootMeNameMatches(normalizedName)

  fun addNameMatches(normalizedName: String): Boolean =
    PHASE_ADD_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  fun shootMeNameMatches(normalizedName: String): Boolean =
    PHASE_SHOOT_ME_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }

  fun distanceFromBoss(player: Player, bossPos: Vec3, horizontalDistSq: (Double, Double, Double, Double) -> Double): Double =
    sqrt(horizontalDistSq(player.x, player.z, bossPos.x, bossPos.z))

  const val MIN_BACKSTEP_DISTANCE = 5.0
  const val STEPBACK_YAW_TOLERANCE = 10.0
  const val STEPBACK_PITCH_TOLERANCE = 22.0
  const val HATCHLING_LOOK_UP_PITCH = -35f
  const val PHASE_ADD_SEARCH_RADIUS_SQ = 100.0

  private const val HATCHLING_PHASE_FALLBACK_TICKS = 160L
  private const val HATCHLING_PHASE_REFRESH_TICKS = 40L
  private const val HATCHLING_NO_TARGET_RELEASE_TICKS = 12L
  private const val HATCHLING_CLEAR_DISTANCE = 12.0
  private const val HATCHLING_MAX_TARGETS = 4
  private const val HATCHLING_MAX_SHOTS_PER_TARGET = 9
  private const val HATCHLING_AIM_Y_OFFSET = 0.9
  private const val HATCHLING_EXTRA_LOOK_UP = 8f
  private val PHASE_ADD_KEYWORDS = arrayOf("hatchling", "hatchlings", "broodling")
  private val PHASE_SHOOT_ME_KEYWORDS = arrayOf("shoot me")
}
