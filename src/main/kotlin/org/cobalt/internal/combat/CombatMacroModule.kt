package org.cobalt.internal.combat

import java.util.UUID
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import java.awt.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
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
import org.cobalt.api.module.setting.inGroup
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
import org.cobalt.api.util.getLoreLines
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

  private val cryptZombieSlayer = CheckboxSetting(
    "Crypt Zombie Slayer",
    "Crypt Slayer flow: farm zombies/ghouls until Slayer boss is detected in tab, then focus boss.",
    false
  )

  private val slayerStatus = TextSetting(
    "Slayer Status",
    "Current Crypt Slayer state.",
    "Off"
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

  private val autoWandOfAtonement = CheckboxSetting(
    "Wand Of Atonement",
    "Use Wand of Atonement for auto-heal. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoZombieSword = CheckboxSetting(
    "Zombie Sword",
    "Use Zombie Sword variants for auto-heal. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoOverflux = CheckboxSetting(
    "Overflux",
    "Use Overflux when the Slayer boss spawns. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoRagnarok = CheckboxSetting(
    "Ragnarok",
    "Use Ragnarok when the Slayer boss spawns. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoSwordOfBadHealth = CheckboxSetting(
    "Sword Of Bad Health",
    "Use Sword of Bad Health during the Slayer boss fight. Auto-enables when the item is detected in hotbar.",
    false
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
  val isRunning: Boolean get() = enabled.value
  val modeDisplay: String get() = if (cryptZombieSlayer.value) "Slayer Macro" else "Combat Macro"
  val slayerDisplay: String get() = if (cryptZombieSlayer.value) slayerStatus.value else "Disabled"
  val statusDisplay: String
    get() =
      when {
        !enabled.value -> "Off"
        cryptZombieSlayer.value -> slayerStatus.value
        currentTargetId != null && startedPath && DuskPathfinder.isActive() -> "Pathing To Target"
        currentTargetId != null -> "Engaging Target"
        startedPath && DuskPathfinder.isActive() -> "Pathing"
        else -> "Searching"
      }
  val targetDisplay: String
    get() {
      val activeTargetName = resolveCurrentTargetName()
      if (!activeTargetName.isNullOrBlank()) {
        return activeTargetName
      }
      if (cryptZombieSlayer.value) {
        return if (slayerBossActive) "Slayer Boss" else "Zombie / Ghoul"
      }
      val filter = targetName.value.trim()
      return if (filter.isNotEmpty()) filter else "Nearest Mob"
    }
  val targetHealthDisplay: String
    get() {
      val target = resolveCurrentTarget() ?: return "-- / --"
      return "${formatHudHealth(target.health)} / ${formatHudHealth(target.maxHealth)}"
    }
  val targetHealthRatio: Float
    get() {
      val target = resolveCurrentTarget() ?: return 0f
      val maxHealth = target.maxHealth.coerceAtLeast(1f)
      return (target.health.coerceAtLeast(0f) / maxHealth).coerceIn(0f, 1f)
    }

  fun startForAutomation(mobName: String) {
    cryptZombieSlayer.value = false
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
  private var slayerBossActive = false
  private var slayerBossLastSeenTick = 0L
  private var slayerOverfluxUsedThisBoss = false
  private var slayerRagnarokUsedThisBoss = false
  private var slayerLastBadHealthUseTick = -1L
  private var slayerLastOverfluxUseTick = -1L
  private var slayerLastRagnarokUseTick = -1L
  private var slayerLastTabScanTick = -1L
  private var slayerTabCache: List<String> = emptyList()
  private var slayerNeedsQuestRestart = false
  private var slayerLastBatphoneAttemptTick = -1L
  private var slayerLastBatphoneUseTick = -1L
  private var slayerLastGuiActionTick = -1L
  private var slayerWarnNoBatphoneTick = -1L
  private var slayerModeEnabled = false
  private var wandWasInHotbar = false
  private var zombieSwordWasInHotbar = false
  private var overfluxWasInHotbar = false
  private var ragnarokWasInHotbar = false
  private var badHealthWasInHotbar = false

  init {
    assignSettingGroups()
    addSetting(
      enabled,
      info,
      targetName,
      cryptZombieSlayer,
      slayerStatus,
      searchRange,
      minCps,
      maxCps,
      attackRange,
      chaseStopBuffer,
      stayNearStart,
      startAreaRadius,
      autoHeal,
      autoWandOfAtonement,
      autoZombieSword,
      autoOverflux,
      autoRagnarok,
      autoSwordOfBadHealth,
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

  private fun assignSettingGroups() {
    enabled.inGroup(TAB_COMBAT_GROUP)
    info.inGroup(TAB_COMBAT_GROUP)
    targetName.inGroup(TAB_COMBAT_GROUP)
    searchRange.inGroup(TAB_COMBAT_GROUP)
    minCps.inGroup(TAB_COMBAT_GROUP)
    maxCps.inGroup(TAB_COMBAT_GROUP)
    attackRange.inGroup(TAB_COMBAT_GROUP)
    chaseStopBuffer.inGroup(TAB_COMBAT_GROUP)
    stayNearStart.inGroup(TAB_COMBAT_GROUP)
    startAreaRadius.inGroup(TAB_COMBAT_GROUP)
    stuckTicksSetting.inGroup(TAB_COMBAT_GROUP)
    warpOnStuck.inGroup(TAB_COMBAT_GROUP)
    requireLos.inGroup(TAB_COMBAT_GROUP)
    aimTolerance.inGroup(TAB_COMBAT_GROUP)
    minAttackCooldown.inGroup(TAB_COMBAT_GROUP)
    stuckRepathTries.inGroup(TAB_COMBAT_GROUP)
    autoLearnLastKill.inGroup(TAB_COMBAT_GROUP)
    whitelistOnly.inGroup(TAB_COMBAT_GROUP)
    learnedWhitelistText.inGroup(TAB_COMBAT_GROUP)
    lastKillText.inGroup(TAB_COMBAT_GROUP)

    cryptZombieSlayer.inGroup(TAB_SLAYER_GROUP)
    slayerStatus.inGroup(TAB_SLAYER_GROUP)

    autoHeal.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoWandOfAtonement.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoZombieSword.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoOverflux.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoRagnarok.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoSwordOfBadHealth.inGroup(TAB_AUTO_ITEMS_GROUP)
    healAtHealth.inGroup(TAB_AUTO_ITEMS_GROUP)
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
    syncAutoItemToggles(player)
    if (startedPath && !DuskPathfinder.isActive()) {
      startedPath = false
      lastTargetPos = null
    }
    syncLearnedWhitelistFromSetting()
    if (cryptZombieSlayer.value && !slayerModeEnabled) {
      slayerModeEnabled = true
      slayerNeedsQuestRestart = true
      slayerLastBatphoneAttemptTick = -1L
      slayerLastBatphoneUseTick = -1L
      slayerLastGuiActionTick = -1L
    }
    if (!cryptZombieSlayer.value) {
      slayerModeEnabled = false
    }
    if (!cryptZombieSlayer.value && !whitelistOnly.value) {
      whitelistOnly.value = true
    }
    if (cryptZombieSlayer.value && whitelistOnly.value) {
      whitelistOnly.value = false
    }
    if (!enabled.value) {
      stopMacro()
      return
    }

    val level = mc.level ?: return
    PathfindingModule.ensureEnabledForAutomation("combat macro")
    updateKillTracking(level)
    if (cryptZombieSlayer.value) {
      updateSlayerBossState(level.gameTime)
      if (handleMaddoxGui(level.gameTime)) {
        return
      }
      if (tryRestartSlayerQuest(player, level.gameTime)) {
        return
      }
    } else {
      slayerStatus.value = "Off"
      clearSlayerBossState(false)
      slayerNeedsQuestRestart = false
    }

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

    val target = if (cryptZombieSlayer.value) resolveSlayerTarget(player, level) else resolveTarget(player)
    if (target == null) {
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
    if (cryptZombieSlayer.value) {
      tryUseSlayerSupportItems(player, target, level.gameTime)
    }

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

  private fun resolveSlayerTarget(player: Player, level: ClientLevel): LivingEntity? {
    val blacklisted = builtInBlacklistedNames
    val startOrigin =
      if (stayNearStart.value) {
        startAreaOrigin ?: player.blockPosition().also { startAreaOrigin = it }
      } else {
        null
      }
    val startAreaRangeSq = startAreaRadius.value * startAreaRadius.value
    val searchRangeSq = searchRange.value * searchRange.value
    var bestBoss: LivingEntity? = null
    var bestBossDist = Double.POSITIVE_INFINITY
    var bestFarmMob: LivingEntity? = null
    var bestFarmMobDist = Double.POSITIVE_INFINITY

    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, "", true)) continue

      val name = normalizeNameForMatch(living.name.string)
      val isBoss = isSlayerBossName(name)
      val isFarmMob = isSlayerFarmMobName(name)
      if (!isBoss && !isFarmMob) continue

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

      if (isBoss && distSq < bestBossDist) {
        bestBoss = living
        bestBossDist = distSq
      }
      if (isFarmMob && distSq < bestFarmMobDist) {
        bestFarmMob = living
        bestFarmMobDist = distSq
      }
    }

    if (bestBoss != null) {
      onSlayerBossDetected(level.gameTime)
    } else if (slayerBossActive && level.gameTime - slayerBossLastSeenTick > SLAYER_BOSS_ENTITY_LOST_TICKS) {
      clearSlayerBossState()
    }

    val selected = if (slayerBossActive) bestBoss else bestBoss ?: bestFarmMob
    if (selected != null) {
      currentTargetId = selected.uuid
    }
    return selected
  }

  private fun updateSlayerBossState(nowTick: Long) {
    val tabLines = readTabListLines(nowTick)
    val hasBossTabLine = tabLines.any { line -> SLAYER_BOSS_TAB_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasClearTabLine = tabLines.any { line -> SLAYER_BOSS_CLEAR_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasQuestTabLine = tabLines.any { line -> SLAYER_QUEST_TAB_KEYWORDS.any { keyword -> line.contains(keyword) } }

    if (hasBossTabLine) {
      onSlayerBossDetected(nowTick)
      slayerNeedsQuestRestart = false
    } else {
      if (hasClearTabLine) {
        if (slayerBossActive) {
          clearSlayerBossState()
        }
        slayerNeedsQuestRestart = true
      } else if (hasQuestTabLine) {
        slayerNeedsQuestRestart = false
      }
      if (slayerBossActive && nowTick - slayerBossLastSeenTick > SLAYER_BOSS_TAB_LOST_TICKS) {
        clearSlayerBossState()
      }
    }

    slayerStatus.value =
      when {
        slayerBossActive -> "Boss Active"
        slayerNeedsQuestRestart -> "Restarting Quest"
        else -> "Farming Zombies"
      }
  }

  private fun onSlayerBossDetected(nowTick: Long) {
    val wasActive = slayerBossActive
    slayerBossActive = true
    slayerBossLastSeenTick = nowTick
    slayerNeedsQuestRestart = false
    if (!wasActive) {
      slayerOverfluxUsedThisBoss = false
      slayerRagnarokUsedThisBoss = false
      slayerLastBadHealthUseTick = -1L
      ChatUtils.sendMessage("Combat macro: Slayer boss detected.")
    }
  }

  private fun clearSlayerBossState(announce: Boolean = true) {
    if (slayerBossActive && announce) {
      ChatUtils.sendMessage("Combat macro: Slayer boss no longer detected.")
    }
    slayerBossActive = false
    slayerBossLastSeenTick = 0L
    slayerOverfluxUsedThisBoss = false
    slayerRagnarokUsedThisBoss = false
    slayerLastBadHealthUseTick = -1L
  }

  private fun tryUseSlayerSupportItems(player: Player, target: LivingEntity, nowTick: Long) {
    if (!slayerBossActive) return
    if (!isSlayerBossName(normalizeNameForMatch(target.name.string))) return
    if (pendingHealRelease || pendingHealRestoreSlot >= 0) return

    if (autoOverflux.value && !slayerOverfluxUsedThisBoss && nowTick - slayerLastOverfluxUseTick >= SLAYER_OVERFLUX_COOLDOWN_TICKS) {
      if (useHotbarUtilityItem(player, SLAYER_OVERFLUX_KEYWORDS)) {
        slayerOverfluxUsedThisBoss = true
        slayerLastOverfluxUseTick = nowTick
        return
      }
    }

    if (autoRagnarok.value && !slayerRagnarokUsedThisBoss && nowTick - slayerLastRagnarokUseTick >= SLAYER_RAGNAROK_COOLDOWN_TICKS) {
      if (useHotbarUtilityItem(player, SLAYER_RAGNAROK_KEYWORDS)) {
        slayerRagnarokUsedThisBoss = true
        slayerLastRagnarokUseTick = nowTick
        return
      }
    }

    if (autoSwordOfBadHealth.value && nowTick - slayerLastBadHealthUseTick >= SLAYER_BAD_HEALTH_REUSE_TICKS) {
      if (useHotbarUtilityItem(player, SLAYER_BAD_HEALTH_KEYWORDS)) {
        slayerLastBadHealthUseTick = nowTick
      }
    }
  }

  private fun tryRestartSlayerQuest(player: Player, nowTick: Long): Boolean {
    if (!slayerNeedsQuestRestart || slayerBossActive) return false
    if (isLikelyMaddoxScreen(nowTick)) return true
    if (mc.screen is AbstractContainerScreen<*>) return false
    if (pendingHealRelease || pendingHealRestoreSlot >= 0) return true
    if (slayerLastBatphoneAttemptTick >= 0L && nowTick - slayerLastBatphoneAttemptTick < SLAYER_BATPHONE_RETRY_TICKS) {
      return true
    }

    var batphoneSlot = findHotbarSlotByKeywords(player, SLAYER_BATPHONE_KEYWORDS)
    if (batphoneSlot !in 0..8) {
      val moved = moveBatphoneToHotbar(player)
      if (!moved) {
        slayerStatus.value = "Need Maddox Batphone"
        if (slayerWarnNoBatphoneTick < 0L || nowTick - slayerWarnNoBatphoneTick >= SLAYER_NO_BATPHONE_WARN_TICKS) {
          slayerWarnNoBatphoneTick = nowTick
          ChatUtils.sendMessage("Combat macro: Maddox Batphone not found in inventory/hotbar.")
        }
        slayerLastBatphoneAttemptTick = nowTick
        return true
      }
      batphoneSlot = findHotbarSlotByKeywords(player, SLAYER_BATPHONE_KEYWORDS)
    }

    if (batphoneSlot !in 0..8) {
      slayerLastBatphoneAttemptTick = nowTick
      return true
    }

    if (useHotbarUtilityItem(player, SLAYER_BATPHONE_KEYWORDS)) {
      slayerLastBatphoneAttemptTick = nowTick
      slayerLastBatphoneUseTick = nowTick
      slayerStatus.value = "Calling Maddox..."
      return true
    }

    return false
  }

  private fun handleMaddoxGui(nowTick: Long): Boolean {
    if (!slayerNeedsQuestRestart) return false
    if (!isLikelyMaddoxScreen(nowTick)) return false
    if (nowTick - slayerLastGuiActionTick < SLAYER_GUI_ACTION_COOLDOWN_TICKS) return true

    val player = mc.player ?: return true
    val menu = player.containerMenu
    val actionSlot = findMaddoxMenuActionSlot(menu.slots)
    if (actionSlot >= 0) {
      InventoryUtils.clickSlot(actionSlot)
      slayerLastGuiActionTick = nowTick
      slayerLastBatphoneAttemptTick = nowTick
      slayerStatus.value = "Restarting Slayer..."
      return true
    }

    if (slayerLastBatphoneUseTick >= 0L && nowTick - slayerLastBatphoneUseTick >= SLAYER_MADDOX_GUI_TIMEOUT_TICKS) {
      player.closeContainer()
      slayerLastGuiActionTick = nowTick
      slayerLastBatphoneAttemptTick = nowTick
      return true
    }

    return true
  }

  private fun isLikelyMaddoxScreen(nowTick: Long): Boolean {
    val screen = mc.screen as? AbstractContainerScreen<*> ?: return false
    val title = normalizeNameForMatch(screen.title.string)
    if (SLAYER_MADDOX_SCREEN_KEYWORDS.any { keyword -> title.contains(keyword) }) return true
    return slayerLastBatphoneUseTick >= 0L && nowTick - slayerLastBatphoneUseTick <= SLAYER_MADDOX_GUI_TIMEOUT_TICKS
  }

  private fun moveBatphoneToHotbar(player: Player): Boolean {
    val sourceSlot = findFirstContainerSlotByKeywords(player.containerMenu.slots, SLAYER_BATPHONE_KEYWORDS) ?: return false
    if (sourceSlot in PLAYER_HOTBAR_MENU_SLOT_START..PLAYER_HOTBAR_MENU_SLOT_END) return true

    val targetHotbarSlot = findBestHotbarSwapSlot(player)
    InventoryUtils.swapSlotWithHotbar(sourceSlot, targetHotbarSlot)
    return true
  }

  private fun findFirstContainerSlotByKeywords(
    slots: List<net.minecraft.world.inventory.Slot>,
    keywords: Array<String>
  ): Int? {
    for (slot in slots) {
      if (!slot.hasItem()) continue
      val normalizedName = normalizeNameForMatch(slot.item.hoverName.string)
      if (keywords.any { keyword -> normalizedName.contains(keyword) }) {
        return slot.index
      }
    }
    return null
  }

  private fun findBestHotbarSwapSlot(player: Player): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      if (inventory.getItem(i).isEmpty) {
        return i
      }
    }
    return inventory.selectedSlot.coerceIn(0, 8)
  }

  private fun findMaddoxMenuActionSlot(slots: List<net.minecraft.world.inventory.Slot>): Int {
    var revenantSlot = -1
    var restartSlot = -1
    var confirmSlot = -1

    for (slot in slots) {
      if (!slot.hasItem()) continue
      val stack = slot.item
      val name = normalizeNameForMatch(stack.hoverName.string)
      val lore = stack.getLoreLines().joinToString(" ") { normalizeNameForMatch(it.string) }
      val text = "$name $lore"

      val hasRevenantKeyword = SLAYER_MADDOX_REVENANT_KEYWORDS.any { keyword -> text.contains(keyword) }
      val hasRestartKeyword = SLAYER_MADDOX_RESTART_KEYWORDS.any { keyword -> text.contains(keyword) }
      val hasConfirmKeyword = SLAYER_MADDOX_CONFIRM_KEYWORDS.any { keyword -> text.contains(keyword) }

      if (hasConfirmKeyword && hasRevenantKeyword) return slot.index
      if (hasRestartKeyword && hasRevenantKeyword) return slot.index

      if (hasRevenantKeyword && revenantSlot == -1) {
        revenantSlot = slot.index
      }
      if (hasRestartKeyword && restartSlot == -1) {
        restartSlot = slot.index
      }
      if (hasConfirmKeyword && confirmSlot == -1) {
        confirmSlot = slot.index
      }
    }

    return when {
      confirmSlot >= 0 -> confirmSlot
      restartSlot >= 0 -> restartSlot
      revenantSlot >= 0 -> revenantSlot
      else -> -1
    }
  }

  private fun useHotbarUtilityItem(player: Player, keywords: Array<String>): Boolean {
    val slot = findHotbarSlotByKeywords(player, keywords)
    if (slot !in 0..8) return false

    val previousSlot = player.inventory.selectedSlot
    if (previousSlot != slot) {
      mc.options.keyHotbarSlots[slot].setDown(true)
      mc.options.keyHotbarSlots[slot].setDown(false)
    }

    mc.options.keyUse?.setDown(true)
    pendingHealRelease = true
    pendingHealRestoreSlot = if (previousSlot != slot) previousSlot else -1
    return true
  }

  private fun findHotbarSlotByKeywords(player: Player, keywords: Array<String>): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue
      val normalizedName = normalizeNameForMatch(stack.hoverName?.string.orEmpty())
      if (keywords.any { keyword -> normalizedName.contains(keyword) }) {
        return i
      }
    }
    return -1
  }

  private fun isSlayerBossName(normalizedName: String): Boolean {
    return SLAYER_BOSS_ENTITY_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun isSlayerFarmMobName(normalizedName: String): Boolean {
    return SLAYER_FARM_MOB_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun readTabListLines(nowTick: Long): List<String> {
    if (slayerLastTabScanTick >= 0L && nowTick - slayerLastTabScanTick < SLAYER_TAB_SCAN_INTERVAL_TICKS) {
      return slayerTabCache
    }
    slayerLastTabScanTick = nowTick
    val connection = mc.connection ?: run {
      slayerTabCache = emptyList()
      return slayerTabCache
    }
    slayerTabCache =
      try {
        resolveTabEntries(connection)
          .mapNotNull { resolveEntryDisplayName(it) }
          .map { ChatFormatting.stripFormatting(it)?.lowercase(Locale.ROOT)?.trim() ?: "" }
          .filter { it.isNotBlank() }
      } catch (_: Exception) {
        emptyList()
      }
    return slayerTabCache
  }

  private fun resolveTabEntries(connection: Any): List<Any> {
    for (name in listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")) {
      val method = connection.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
      when (result) {
        is Collection<*> -> return result.filterNotNull()
        is Iterable<*> -> return result.filterNotNull()
      }
    }
    return emptyList()
  }

  private fun resolveEntryDisplayName(entry: Any): String? {
    for (name in listOf("getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName")) {
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val text = coerceText(runCatching { method.invoke(entry) }.getOrNull())
      if (!text.isNullOrBlank()) return text
    }
    for (name in listOf("getProfile", "getGameProfile", "profile")) {
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
      val nameMethod = profile.javaClass.methods.firstOrNull { it.name == "getName" && it.parameterCount == 0 } ?: continue
      val profileName = runCatching { nameMethod.invoke(profile) as? String }.getOrNull()
      if (!profileName.isNullOrBlank()) return profileName
    }
    return null
  }

  private fun coerceText(value: Any?): String? {
    if (value == null) return null
    if (value is String) return value
    val m = value.javaClass.methods.firstOrNull { it.name == "getString" && it.parameterCount == 0 }
    val raw = m?.let { runCatching { it.invoke(value) }.getOrNull() }
    return if (raw is String) raw else value.toString()
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

  private fun resolveCurrentTarget(): LivingEntity? {
    val level = mc.level ?: return null
    val targetId = currentTargetId ?: return null
    val liveTarget = level.entitiesForRendering().firstOrNull { it.uuid == targetId } as? LivingEntity ?: return null
    if (!liveTarget.isAlive || liveTarget.health <= 0f) return null
    return liveTarget
  }

  private fun resolveCurrentTargetName(): String? {
    return resolveCurrentTarget()?.let { target ->
      sanitizeTargetName(target.name.string).ifBlank { null }
    }
  }

  private fun formatHudHealth(value: Float): String {
    val rounded = (value * 10f).toInt() / 10f
    return if (rounded % 1f == 0f) {
      rounded.toInt().toString()
    } else {
      rounded.toString()
    }
  }

  private fun syncAutoItemToggles(player: Player) {
    var changed = false

    val wandInHotbar = findHotbarSlotByKeywords(player, SLAYER_WAND_OF_ATONEMENT_KEYWORDS) in 0..8
    if (wandInHotbar && !wandWasInHotbar && !autoWandOfAtonement.value) {
      autoWandOfAtonement.value = true
      changed = true
    }
    wandWasInHotbar = wandInHotbar

    val zombieSwordInHotbar = findHotbarSlotByKeywords(player, SLAYER_ZOMBIE_SWORD_KEYWORDS) in 0..8
    if (zombieSwordInHotbar && !zombieSwordWasInHotbar && !autoZombieSword.value) {
      autoZombieSword.value = true
      changed = true
    }
    zombieSwordWasInHotbar = zombieSwordInHotbar

    val overfluxInHotbar = findHotbarSlotByKeywords(player, SLAYER_OVERFLUX_KEYWORDS) in 0..8
    if (overfluxInHotbar && !overfluxWasInHotbar && !autoOverflux.value) {
      autoOverflux.value = true
      changed = true
    }
    overfluxWasInHotbar = overfluxInHotbar

    val ragnarokInHotbar = findHotbarSlotByKeywords(player, SLAYER_RAGNAROK_KEYWORDS) in 0..8
    if (ragnarokInHotbar && !ragnarokWasInHotbar && !autoRagnarok.value) {
      autoRagnarok.value = true
      changed = true
    }
    ragnarokWasInHotbar = ragnarokInHotbar

    val badHealthInHotbar = findHotbarSlotByKeywords(player, SLAYER_BAD_HEALTH_KEYWORDS) in 0..8
    if (badHealthInHotbar && !badHealthWasInHotbar && !autoSwordOfBadHealth.value) {
      autoSwordOfBadHealth.value = true
      changed = true
    }
    badHealthWasInHotbar = badHealthInHotbar

    if (changed) {
      Config.saveModulesConfig()
    }
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
    if (autoWandOfAtonement.value) {
      val wandSlot = findHotbarSlotByKeywords(player, SLAYER_WAND_OF_ATONEMENT_KEYWORDS)
      if (wandSlot in 0..8) {
        return wandSlot
      }
    }

    if (autoZombieSword.value) {
      val zombieSwordSlot = findHotbarSlotByKeywords(player, SLAYER_ZOMBIE_SWORD_KEYWORDS)
      if (zombieSwordSlot in 0..8) {
        return zombieSwordSlot
      }
    }

    return -1
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
    slayerStatus.value = "Off"
    clearSlayerBossState(false)
    slayerLastTabScanTick = -1L
    slayerTabCache = emptyList()
    slayerNeedsQuestRestart = false
    slayerLastBatphoneAttemptTick = -1L
    slayerLastBatphoneUseTick = -1L
    slayerLastGuiActionTick = -1L
    slayerWarnNoBatphoneTick = -1L
    slayerModeEnabled = false
  }

  private const val TAB_COMBAT_GROUP = "Combat Macro"
  private const val TAB_SLAYER_GROUP = "Slayer Macro"
  private const val TAB_AUTO_ITEMS_GROUP = "Auto Items"
  private const val KILL_CANDIDATE_TTL_TICKS = 80L
  private const val KILL_DISAPPEAR_CONFIRM_TICKS = 2L
  private const val DRILL_WARN_INTERVAL_TICKS = 60L
  private const val AUTO_HEAL_COOLDOWN_TICKS = 20L
  private const val SLAYER_TAB_SCAN_INTERVAL_TICKS = 5L
  private const val SLAYER_BOSS_TAB_LOST_TICKS = 30L
  private const val SLAYER_BOSS_ENTITY_LOST_TICKS = 20L
  private const val SLAYER_OVERFLUX_COOLDOWN_TICKS = 600L
  private const val SLAYER_RAGNAROK_COOLDOWN_TICKS = 400L
  private const val SLAYER_BAD_HEALTH_REUSE_TICKS = 80L
  private const val SLAYER_BATPHONE_RETRY_TICKS = 30L
  private const val SLAYER_MADDOX_GUI_TIMEOUT_TICKS = 60L
  private const val SLAYER_GUI_ACTION_COOLDOWN_TICKS = 5L
  private const val SLAYER_NO_BATPHONE_WARN_TICKS = 200L
  private const val PLAYER_HOTBAR_MENU_SLOT_START = 36
  private const val PLAYER_HOTBAR_MENU_SLOT_END = 44
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
  private val SLAYER_BOSS_ENTITY_KEYWORDS = arrayOf("revenant horror", "atoned horror", "deformed revenant")
  private val SLAYER_FARM_MOB_KEYWORDS = arrayOf("zombie", "crypt ghoul", "golden ghoul")
  private val SLAYER_BOSS_TAB_KEYWORDS = arrayOf("slay the boss", "revenant horror", "atoned horror")
  private val SLAYER_BOSS_CLEAR_KEYWORDS = arrayOf("boss slain", "slayer quest complete")
  private val SLAYER_QUEST_TAB_KEYWORDS = arrayOf("slayer quest", "zombie slayer", "revenant horror")
  private val SLAYER_WAND_OF_ATONEMENT_KEYWORDS = arrayOf("wand of atonement")
  private val SLAYER_ZOMBIE_SWORD_KEYWORDS = arrayOf("zombie sword")
  private val SLAYER_OVERFLUX_KEYWORDS = arrayOf("overflux")
  private val SLAYER_RAGNAROK_KEYWORDS = arrayOf("ragnarok", "ragnorak")
  private val SLAYER_BAD_HEALTH_KEYWORDS = arrayOf("sword of bad health")
  private val SLAYER_BATPHONE_KEYWORDS = arrayOf("maddox batphone", "batphone")
  private val SLAYER_MADDOX_SCREEN_KEYWORDS = arrayOf("maddox", "slayer")
  private val SLAYER_MADDOX_REVENANT_KEYWORDS = arrayOf("revenant horror", "zombie slayer", "revenant")
  private val SLAYER_MADDOX_RESTART_KEYWORDS = arrayOf("restart", "start quest", "begin quest", "start slayer")
  private val SLAYER_MADDOX_CONFIRM_KEYWORDS = arrayOf("confirm", "click to confirm")
  private val CYAN_COLOR = Color(0, 255, 255)
  private val PINK_COLOR = Color(255, 105, 180)
}
