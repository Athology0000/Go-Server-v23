package org.cobalt.internal.combat

import java.util.UUID
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.render.Render3D
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.internal.helper.Config
import org.cobalt.internal.pathfinding.DuskPathfinder
import org.cobalt.internal.pathfinding.PathPlanProfiles
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.rotation.RotationsModule

object CombatMacroModule : Module("Combat Macro") {

  private val mc: Minecraft = Minecraft.getInstance()
  private val rotationStrategy = BezierTrackingRotationStrategy(
    yawStepSampler = { (RotationsModule.sample(RotationsModule.combatYawStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
    pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
    curveInProvider = { RotationsModule.bezierCurveIn.value.toFloat() },
    curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
    minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
    snapThresholdProvider = { RotationsModule.bezierSnapThreshold.value.toFloat() },
  )
  private val builtInBlacklistedNames = setOf(
    "blacksmith",
    "click",
    "knifethrower",
    "toolsmith",
    "gimley",
    "hornum",
    "brynmor",
    "forge foreman",
    "fred",
    "sargwyn",
    "tarwen",
    "pdp2ut6lwf",
    "95j67t0hlr",
    "u07nk5sfh6",
    "12r7qltkkd",
    "lumina",
    "armor stand",
    "gjthi54bdc",
    "fragilis",
    "m68691nsos",
    "gemstone grinder",
    "geo",
    "4ud6865lsx",
    "crystal hollows",
    "gwendolyn",
    "emissary braum",
    "8s3f5ek78w",
    "lqf888h37w",
    "thormyr",
    "emmor",
    "08481rzc37",
    "5h260k1i05",
    "grandan",
    "5m965eru09",
    "emkam",
    "queen mismyla",
    "75576yv62m",
    "v4v8vjd7g8",
    "erren",
    "redros",
    "v7245ab06o",
    "zto57x66p5",
    "tornora",
    "leg1xn0us7",
    "castle guard",
    "i2ztjr35j6",
    "j4bjv2gi6i",
    "465awyh20w",
    "42toq9l273",
    "ticket master",
    "witches stew",
    "alcina",
    "witch",
    "6en215sve0",
    "bylma",
    "4yd3iv3sk7",
    "marigold",
    "gold essence shop",
    "l15l66bsyx",
    "emissary wilson",
    "3nba7e0ea6"
  )

  private val enabled = CheckboxSetting(
    "Enabled",
    "Pathfind to a target and attack until stopped.",
    false
  )

  private val info = InfoSetting(
    "Target",
    "Set a target name (partial match). Leave blank to target nearest mob.",
    InfoType.INFO
  )

  private val targetName = TextSetting(
    "Target Name",
    "Entity name to target (partial match).",
    ""
  )

  private val searchRange = SliderSetting(
    "Search Range",
    "Max distance to search for targets.",
    32.0,
    8.0,
    96.0
  )

  private val minCps = SliderSetting(
    "Min CPS",
    "Minimum clicks per second.",
    6.0,
    1.0,
    20.0
  )

  private val maxCps = SliderSetting(
    "Max CPS",
    "Maximum clicks per second.",
    10.0,
    1.0,
    20.0
  )

  private val attackRange = SliderSetting(
    "Attack Range",
    "Distance to start attacking.",
    3.2,
    2.0,
    6.0
  )

  private val chaseStopBuffer = SliderSetting(
    "Chase Stop Buffer",
    "Stop pathing this far before attack range to reduce strafe jitter.",
    1.1,
    0.0,
    3.0
  )

  private val stayNearStart = CheckboxSetting(
    "Stay Near Start",
    "Keep combat inside the general area where macro was enabled.",
    true
  )

  private val startAreaRadius = SliderSetting(
    "Start Area Radius",
    "Max horizontal distance from start area for combat targets.",
    24.0,
    8.0,
    96.0
  )

  private val autoHeal = CheckboxSetting(
    "Auto Heal",
    "Auto-use a healing item when health is low.",
    true
  )

  private val healAtHealth = SliderSetting(
    "Heal At Health",
    "Use heal item at or below this health.",
    10.0,
    1.0,
    20.0
  )

  private val stuckTicksSetting = SliderSetting(
    "Stuck Ticks",
    "Ticks without movement before warp hub.",
    80.0,
    20.0,
    200.0
  )

  private val warpOnStuck = CheckboxSetting(
    "Warp Hub On Stuck",
    "Warp to hub if stuck while macro is active.",
    true
  )

  private val requireLos = CheckboxSetting(
    "Require LOS",
    "Only attack when you have line of sight.",
    true
  )


  private val aimTolerance = SliderSetting(
    "Aim Tolerance",
    "Max yaw/pitch error before attacking.",
    15.0,
    4.0,
    45.0
  )

  private val minAttackCooldown = SliderSetting(
    "Min Attack Cooldown",
    "Minimum vanilla cooldown needed before an attack.",
    0.2,
    0.0,
    1.0
  )

  private val stuckRepathTries = SliderSetting(
    "Stuck Repath Tries",
    "Repath attempts before optional hub warp.",
    2.0,
    0.0,
    8.0
  )

  private val autoLearnLastKill = CheckboxSetting(
    "Auto Learn Last Kill",
    "Set last killed enemy as target and whitelist entry.",
    true
  )

  private val whitelistOnly = CheckboxSetting(
    "Whitelist Only",
    "Only target entities matching learned whitelist names.",
    true
  )

  private val learnedWhitelistText = TextSetting(
    "Learned Whitelist",
    "Auto-learned target names from kills.",
    "EMPTY"
  )

  private val lastKillText = TextSetting(
    "Last Kill",
    "Most recently learned enemy name.",
    "-"
  )

  val isActive: Boolean get() = enabled.value

  fun startForAutomation(mobName: String) {
    targetName.value = mobName
    whitelistOnly.value = false
    enabled.value = true
  }

  fun stopForAutomation() {
    enabled.value = false
  }

  private var lastTargetPos: BlockPos? = null
  private var lastMoveX = 0.0
  private var lastMoveY = 0.0
  private var lastMoveZ = 0.0
  private var stuckTicks = 0
  private var nextAttackNs = 0L
  private var startedPath = false
  private var currentTargetId: UUID? = null
  private var lastPathStartTick = 0L
  private var killCandidateId: UUID? = null
  private var killCandidateName: String? = null
  private var killCandidateExpiresTick = 0L
  private var killCandidateAttackTick = 0L
  private val learnedWhitelist = LinkedHashSet<String>()
  private var lastSyncedWhitelistRaw = ""
  private var drillWarnTick = 0L
  private var lastHealUseTick = 0L
  private var stuckRepathCount = 0
  private var startAreaOrigin: BlockPos? = null
  private var pendingHealRelease = false
  private var pendingHealRestoreSlot = -1

  init {
    addSetting(
      enabled,
      info,
      targetName,
      searchRange,
      minCps,
      maxCps,
      attackRange,
      chaseStopBuffer,
      stayNearStart,
      startAreaRadius,
      autoHeal,
      healAtHealth,
      stuckTicksSetting,
      warpOnStuck,
      requireLos,
      aimTolerance,
      minAttackCooldown,
      stuckRepathTries,
      autoLearnLastKill,
      whitelistOnly,
      learnedWhitelistText,
      lastKillText
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    // Release heal key and restore slot from previous heal use
    if (pendingHealRelease) {
      mc.options.keyUse?.setDown(false)
      pendingHealRelease = false
    }
    if (pendingHealRestoreSlot >= 0) {
      mc.options.keyHotbarSlots[pendingHealRestoreSlot].setDown(true)
      mc.options.keyHotbarSlots[pendingHealRestoreSlot].setDown(false)
      pendingHealRestoreSlot = -1
    }

    val player = mc.player ?: return
    if (startedPath && !DuskPathfinder.isActive()) {
      startedPath = false
      lastTargetPos = null
    }
    syncLearnedWhitelistFromSetting()
    if (!whitelistOnly.value) {
      whitelistOnly.value = true
    }
    if (!enabled.value) {
      stopMacro()
      return
    }

    val level = mc.level ?: return
    PathfindingModule.ensureEnabledForAutomation("combat macro")
    updateKillTracking(level)

    if (player.isDeadOrDying || player.health <= 0f) {
      stopMacro()
      return
    }

    if (stayNearStart.value && startAreaOrigin == null) {
      startAreaOrigin = player.blockPosition()
    }

    tryAutoHeal(player, level.gameTime)
    if (enforceStartArea(player, level)) {
      return
    }

    val target = resolveTarget(player) ?: run {
      if (startedPath && DuskPathfinder.isActive()) {
        DuskPathfinder.stop(mc, "No target found.")
      }
      startedPath = false
      lastTargetPos = null
      currentTargetId = null
      return
    }

    val dist = player.distanceTo(target)
    val inAttackRange = dist <= attackRange.value
    val inCloseChaseRange = dist <= attackRange.value + chaseStopBuffer.value

    ensurePreferredWeapon(player, target, level.gameTime)

    if (inCloseChaseRange) {
      val rotation = AngleUtils.getRotation(target)
      RotationExecutor.rotateTo(rotation, rotationStrategy)
    } else {
      RotationExecutor.stopRotating()
    }

    if (inAttackRange) {
      if (startedPath && DuskPathfinder.isActive()) {
        DuskPathfinder.stop(mc, "Target in range.")
      }
      startedPath = false
      lastTargetPos = null
      stuckRepathCount = 0
      attemptAttack(player, target)
    } else {
      val targetPos = target.blockPosition()
      val last = lastTargetPos
      val targetMovedFar = last == null || last.distSqr(targetPos) > TARGET_REPATH_DISTANCE_SQ
      if (!DuskPathfinder.isActive() || targetMovedFar) {
        if (level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = level.gameTime
          val started = startCombatPathToTarget(level, targetPos)
          if (started) {
            lastTargetPos = targetPos
            startedPath = true
          } else {
            startedPath = false
            if (!DuskPathfinder.isActive()) {
              lastTargetPos = null
            }
          }
        }
      }
    }

    updateStuck(player, inAttackRange, level)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    val level = mc.level ?: return
    val targetId = currentTargetId ?: return
    val target = level.entitiesForRendering().firstOrNull { it.uuid == targetId } as? LivingEntity ?: return
    if (!target.isAlive || target.health <= 0f) return

    val color = currentTargetRenderColor()
    Render3D.drawBox(event.context, target.boundingBox.inflate(TARGET_BOX_INFLATE), color, true)
  }

  private fun resolveTarget(player: Player): LivingEntity? {
    val level = mc.level ?: return null
    val filter = targetName.value.trim().lowercase()
    val blacklisted = builtInBlacklistedNames
    val startOrigin =
      if (stayNearStart.value) {
        startAreaOrigin ?: player.blockPosition().also { startAreaOrigin = it }
      } else {
        null
      }
    val startAreaRangeSq = startAreaRadius.value * startAreaRadius.value

    val searchRangeSq = searchRange.value * searchRange.value
    var best: LivingEntity? = null
    var bestDist = Double.POSITIVE_INFINITY
    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, filter, true)) continue
      if (startOrigin != null) {
        val ox = living.x - (startOrigin.x + 0.5)
        val oz = living.z - (startOrigin.z + 0.5)
        if (ox * ox + oz * oz > startAreaRangeSq) continue
      }
      val dx = living.x - player.x
      val dy = living.y - player.y
      val dz = living.z - player.z
      val distSq = dx * dx + dy * dy + dz * dz
      if (distSq > searchRangeSq) continue
      if (distSq < bestDist) {
        best = living
        bestDist = distSq
      }
    }
    if (best != null) {
      currentTargetId = best.uuid
    }
    return best
  }

  private fun isValidTarget(
    living: LivingEntity,
    player: Player,
    blacklisted: Set<String>,
    nameFilter: String,
    requireRange: Boolean
  ): Boolean {
    if (living is ArmorStand) return false
    if (living == player) return false
    if (!living.isAlive) return false
    val displayName = normalizeNameForMatch(living.name.string)
    if (blacklisted.contains(displayName)) return false
    if (nameFilter.isNotEmpty() && !displayName.contains(nameFilter)) return false
    if (whitelistOnly.value && learnedWhitelist.isNotEmpty()) {
      val whitelistMatch = learnedWhitelist.any { entry ->
        displayName.contains(entry) || entry.contains(displayName)
      }
      if (!whitelistMatch) return false
    }
    if (living is Player && mc.connection?.getPlayerInfo(living.uuid) != null) return false
    return true
  }

  private fun enforceStartArea(
    player: Player,
    level: ClientLevel
  ): Boolean {
    if (!stayNearStart.value) return false
    val origin = startAreaOrigin ?: return false
    val radius = startAreaRadius.value + START_AREA_RETURN_BUFFER
    val distSq = horizontalDistSq(player.x, player.z, origin.x + 0.5, origin.z + 0.5)
    if (distSq <= radius * radius) {
      return false
    }

    val pathAlreadyReturning = startedPath && lastTargetPos == origin && DuskPathfinder.isActive()
    if (!pathAlreadyReturning && level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
      lastPathStartTick = level.gameTime
      val started = DuskPathfinder.start(mc, origin, PathPlanProfiles.COMBAT_ID)
      if (started) {
        lastTargetPos = origin
        startedPath = true
      }
    }
    currentTargetId = null
    RotationExecutor.stopRotating()
    return true
  }

  private fun horizontalDistSq(ax: Double, az: Double, bx: Double, bz: Double): Double {
    val dx = ax - bx
    val dz = az - bz
    return dx * dx + dz * dz
  }

  private fun attemptAttack(player: Player, target: LivingEntity) {
    if (!isAttackReady(player, target)) return

    val now = System.nanoTime()
    if (now < nextAttackNs) return

    val minRate = min(minCps.value, maxCps.value).coerceAtLeast(0.1)
    val maxRate = max(minCps.value, maxCps.value).coerceAtLeast(minRate)
    val cps = minRate + (Math.random() * (maxRate - minRate))
    val delayNs = (1_000_000_000.0 / cps).toLong()

    nextAttackNs = now + delayNs
    MouseUtils.leftClick()
    registerKillCandidate(target)
  }

  private fun registerKillCandidate(target: LivingEntity) {
    val level = mc.level ?: return
    killCandidateId = target.uuid
    killCandidateName = sanitizeTargetName(target.name.string)
    killCandidateAttackTick = level.gameTime
    killCandidateExpiresTick = level.gameTime + KILL_CANDIDATE_TTL_TICKS
  }

  private fun updateKillTracking(level: ClientLevel) {
    if (!autoLearnLastKill.value) return
    val candidateId = killCandidateId ?: return
    if (level.gameTime > killCandidateExpiresTick) {
      clearKillCandidate()
      return
    }

    val candidate = level.entitiesForRendering().firstOrNull { it.uuid == candidateId } as? LivingEntity
    if (candidate == null) {
      if (level.gameTime - killCandidateAttackTick >= KILL_DISAPPEAR_CONFIRM_TICKS) {
        applyLearnedKillTarget(killCandidateName ?: "")
        clearKillCandidate()
      }
      return
    }
    if (candidate.isAlive && candidate.health > 0f) {
      return
    }

    applyLearnedKillTarget(killCandidateName ?: candidate.name.string)
    clearKillCandidate()
  }

  private fun clearKillCandidate() {
    killCandidateId = null
    killCandidateName = null
    killCandidateExpiresTick = 0L
    killCandidateAttackTick = 0L
  }

  private fun applyLearnedKillTarget(rawName: String) {
    val learnedName = sanitizeTargetName(rawName)
    if (learnedName.isBlank()) return
    var changed = false

    if (targetName.value != learnedName) {
      targetName.value = learnedName
      changed = true
    }
    if (lastKillText.value != learnedName) {
      lastKillText.value = learnedName
      changed = true
    }
    if (learnedWhitelist.add(learnedName.lowercase())) {
      changed = true
    }

    val serializedWhitelist = serializeWhitelist()
    if (learnedWhitelistText.value != serializedWhitelist) {
      learnedWhitelistText.value = serializedWhitelist
      changed = true
    }
    lastSyncedWhitelistRaw = learnedWhitelistText.value

    ChatUtils.sendMessage("Combat macro learned target: $learnedName")
    if (changed) {
      Config.saveModulesConfig()
    }
  }

  private fun sanitizeTargetName(raw: String): String {
    val noFormatting = raw.replace(Regex("\\u00A7."), "").trim()
    val noHpSuffix = noFormatting.replace(Regex("\\s+[0-9.,]+(?:[kKmMbB])?\\s*[\\u2764]?$"), "")
    return noHpSuffix.trim()
  }

  private fun normalizeNameForMatch(raw: String): String {
    return sanitizeTargetName(raw).lowercase()
  }

  private fun syncLearnedWhitelistFromSetting() {
    val raw = learnedWhitelistText.value
    if (raw == lastSyncedWhitelistRaw) return

    learnedWhitelist.clear()
    deserializeWhitelist(raw).forEach { learnedWhitelist.add(it) }
    learnedWhitelist.add(DEFAULT_WHITELIST_ENTRY)

    val normalized = serializeWhitelist()
    lastSyncedWhitelistRaw = normalized
    if (learnedWhitelistText.value != normalized) {
      learnedWhitelistText.value = normalized
    }
  }

  private fun deserializeWhitelist(raw: String): List<String> {
    if (raw.isBlank() || raw.equals("EMPTY", ignoreCase = true)) return emptyList()
    return raw
      .split(",")
      .asSequence()
      .map { it.trim().removeSuffix("...") }
      .filter { it.isNotEmpty() }
      .map(::normalizeNameForMatch)
      .filter { it.isNotBlank() && it != "empty" }
      .distinct()
      .toList()
  }

  private fun serializeWhitelist(): String {
    if (learnedWhitelist.isEmpty()) return "EMPTY"
    return learnedWhitelist.joinToString(", ")
  }

  private fun ensurePreferredWeapon(player: Player, target: LivingEntity, nowTick: Long) {
    val name = normalizeNameForMatch(target.name.string)
    if (!name.contains("ice walker")) return

    val selected = player.inventory.getItem(player.inventory.selectedSlot)
    val selectedName = selected.hoverName?.string.orEmpty().lowercase()
    if (selectedName.contains("drill")) return

    val drillSlot = InventoryUtils.findItemInHotbar("drill")
    if (drillSlot in 0..8) {
      InventoryUtils.holdHotbarSlot(drillSlot)
      return
    }

    if (nowTick - drillWarnTick >= DRILL_WARN_INTERVAL_TICKS) {
      drillWarnTick = nowTick
      ChatUtils.sendMessage("Combat macro: no drill found in hotbar for Ice Walker target.")
    }
  }

  private fun tryAutoHeal(player: Player, nowTick: Long) {
    if (!autoHeal.value) return
    val effectiveHealth = player.health + player.absorptionAmount
    if (effectiveHealth > healAtHealth.value.toFloat()) return
    if (nowTick - lastHealUseTick < AUTO_HEAL_COOLDOWN_TICKS) return

    val healSlot = findHealHotbarSlot(player)
    if (healSlot !in 0..8) return

    lastHealUseTick = nowTick
    val previousSlot = player.inventory.selectedSlot

    if (previousSlot != healSlot) {
      mc.options.keyHotbarSlots[healSlot].setDown(true)
      mc.options.keyHotbarSlots[healSlot].setDown(false)
    }

    mc.options.keyUse?.setDown(true)
    pendingHealRelease = true
    pendingHealRestoreSlot = if (previousSlot != healSlot) previousSlot else -1
  }

  private fun findHealHotbarSlot(player: Player): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue
      val name = stack.hoverName?.string.orEmpty()
      if (isHealingItemName(name)) {
        return i
      }
    }
    return -1
  }

  private fun isHealingItemName(rawName: String): Boolean {
    val normalized = rawName
      .lowercase()
      .replace(Regex("[^a-z0-9]+"), " ")
      .trim()

    return normalized.contains("wand of mending") ||
      normalized.contains("wand of restoration") ||
      normalized.contains("wand of atonement") ||
      normalized.contains("zombie sword") ||
      normalized.contains("gloomlock grimoire") ||
      normalized.contains("healing wand")
  }

  private fun currentTargetRenderColor(): Color {
    val phase = (System.currentTimeMillis() % TARGET_BOX_CYCLE_MS).toDouble() / TARGET_BOX_CYCLE_MS.toDouble()
    val t = 0.5 - 0.5 * cos(phase * Math.PI * 2.0)
    val r = lerpChannel(CYAN_COLOR.red, PINK_COLOR.red, t)
    val g = lerpChannel(CYAN_COLOR.green, PINK_COLOR.green, t)
    val b = lerpChannel(CYAN_COLOR.blue, PINK_COLOR.blue, t)
    return Color(r, g, b, TARGET_BOX_ALPHA)
  }

  private fun lerpChannel(start: Int, end: Int, t: Double): Int {
    return (start + (end - start) * t).toInt().coerceIn(0, 255)
  }

  private fun isAttackReady(player: Player, target: LivingEntity): Boolean {
    val cooldown = player.getAttackStrengthScale(0.0f)
    if (cooldown < minAttackCooldown.value.toFloat()) return false
    if (requireLos.value && !player.hasLineOfSight(target)) return false

    val rotation = AngleUtils.getRotation(target)
    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val pitchError = abs(rotation.pitch - player.xRot)
    val tolerance = aimTolerance.value
    if (yawError > tolerance || pitchError > tolerance) return false

    return true
  }

  private fun updateStuck(
    player: Player,
    inAttackRange: Boolean,
    level: ClientLevel
  ) {
    if (inAttackRange || !startedPath || !DuskPathfinder.isActive()) {
      stuckTicks = 0
      stuckRepathCount = 0
      lastMoveX = player.x
      lastMoveY = player.y
      lastMoveZ = player.z
      return
    }

    val dx = player.x - lastMoveX
    val dy = player.y - lastMoveY
    val dz = player.z - lastMoveZ
    val moved = dx * dx + dy * dy + dz * dz > 0.0008
    if (moved) {
      stuckTicks = 0
      stuckRepathCount = 0
      lastMoveX = player.x
      lastMoveY = player.y
      lastMoveZ = player.z
      return
    }

    stuckTicks++
    if (stuckTicks < stuckTicksSetting.value.toInt()) {
      return
    }
    stuckTicks = 0

    val maxRepaths = stuckRepathTries.value.toInt().coerceAtLeast(0)
    if (stuckRepathCount < maxRepaths) {
      val target =
        currentTargetId?.let { id ->
          level.entitiesForRendering().firstOrNull { it.uuid == id } as? LivingEntity
        }
      if (target != null && target.isAlive && target.health > 0f) {
        DuskPathfinder.stop(mc, "Combat repath.")
        val targetPos = target.blockPosition()
        val restarted = startCombatPathToTarget(level, targetPos)
        if (restarted) {
          startedPath = true
          lastTargetPos = targetPos
          lastPathStartTick = level.gameTime
          stuckRepathCount++
          return
        }
      }
    }

    stuckRepathCount = 0
    if (warpOnStuck.value) {
      (player as? LocalPlayer)?.connection?.sendCommand("warp hub")
      ChatUtils.sendMessage("Combat macro stuck. Warping to hub.")
      stopMacro()
    }
  }

  private fun startCombatPathToTarget(level: ClientLevel, targetPos: BlockPos): Boolean {
    // Jitter the destination slightly so the path never goes perfectly straight to the mob.
    val dest = jitterTargetPos(targetPos)

    if (DuskPathfinder.start(mc, dest, PathPlanProfiles.COMBAT_ID)) {
      return true
    }

    val direct = MinecraftPathingRules.resolveTarget(level, dest)
    if (direct != null && DuskPathfinder.start(mc, direct, PathPlanProfiles.COMBAT_ID)) {
      return true
    }

    val fallback = findNearestWalkableAround(level, dest, CHASE_RESOLVE_RADIUS, CHASE_RESOLVE_VERTICAL)
    if (fallback != null && DuskPathfinder.start(mc, fallback, PathPlanProfiles.COMBAT_ID)) {
      return true
    }
    return false
  }

  /** Returns a copy of [pos] offset by a small random amount on X/Z so paths curve off-centre. */
  private fun jitterTargetPos(pos: BlockPos): BlockPos {
    val offX = (Math.random() * (PATH_JITTER_RANGE * 2 + 1) - PATH_JITTER_RANGE).toInt()
    val offZ = (Math.random() * (PATH_JITTER_RANGE * 2 + 1) - PATH_JITTER_RANGE).toInt()
    return pos.offset(offX, 0, offZ)
  }

  private fun findNearestWalkableAround(
    level: ClientLevel,
    origin: BlockPos,
    radius: Int,
    vertical: Int
  ): BlockPos? {
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    for (dy in -vertical..vertical) {
      for (dx in -radius..radius) {
        for (dz in -radius..radius) {
          val pos = origin.offset(dx, dy, dz)
          if (!MinecraftPathingRules.isWalkable(level, pos)) continue
          val distSq = pos.distSqr(origin).toDouble()
          if (distSq < bestDistSq) {
            bestDistSq = distSq
            best = pos
          }
        }
      }
    }
    return best
  }

  private fun stopMacro() {
    if (startedPath && DuskPathfinder.isActive()) {
      DuskPathfinder.stop(mc, "Combat macro stopped.")
    }
    RotationExecutor.stopRotating()
    lastTargetPos = null
    stuckTicks = 0
    nextAttackNs = 0L
    startedPath = false
    currentTargetId = null
    lastPathStartTick = 0L
    clearKillCandidate()
    drillWarnTick = 0L
    lastHealUseTick = 0L
    stuckRepathCount = 0
    startAreaOrigin = null
    if (pendingHealRelease) {
      mc.options.keyUse?.setDown(false)
      pendingHealRelease = false
    }
    pendingHealRestoreSlot = -1
  }

  private const val KILL_CANDIDATE_TTL_TICKS = 80L
  private const val KILL_DISAPPEAR_CONFIRM_TICKS = 2L
  private const val DRILL_WARN_INTERVAL_TICKS = 60L
  private const val AUTO_HEAL_COOLDOWN_TICKS = 20L
  private const val DEFAULT_WHITELIST_ENTRY = "ice walker"
  private const val MIN_PATH_START_INTERVAL_TICKS = 1L
  private const val TARGET_REPATH_DISTANCE_SQ = 2.25
  private const val COMBAT_ROTATION_STEP_SCALE = 0.62
  private const val PATH_JITTER_RANGE = 2   // blocks of random XZ offset per path start
  private const val CHASE_RESOLVE_RADIUS = 3
  private const val CHASE_RESOLVE_VERTICAL = 2
  private const val START_AREA_RETURN_BUFFER = 2.0
  private const val TARGET_BOX_CYCLE_MS = 4000L
  private const val TARGET_BOX_ALPHA = 170
  private const val TARGET_BOX_INFLATE = 0.08
  private val CYAN_COLOR = Color(0, 255, 255)
  private val PINK_COLOR = Color(255, 105, 180)
}
