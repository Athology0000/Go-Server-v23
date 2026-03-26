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
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
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
import org.cobalt.api.pathfinder.jni.MovementProfile
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.helper.WalkbackBridge
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

  private val slayerType = ModeSetting(
    "Slayer Type",
    "Which slayer quest type to run.",
    0,
    arrayOf("Zombie", "Wolf", "Spider", "Enderman", "Vampire", "Blaze")
  )

  private val slayerTier = SliderSetting(
    "Slayer Tier",
    "Quest tier to buy (1 = cheapest, 5 = hardest).",
    1.0,
    1.0,
    5.0,
    step = 1.0
  )

  private val slayerAutoWarp = CheckboxSetting(
    "Auto Warp",
    "Warp to the farming area after buying a new quest.",
    true
  )

  private val slayerLocation = ModeSetting(
    "Location",
    "Farming location for zombie slayer.",
    0,
    arrayOf("Zombie Graveyard", "Zombie Crypt")
  )

  private val slayerWalkbackRoute = TextSetting(
    "Walkback Route",
    "Route name to follow back to farming area after boss kill (if Auto Warp is off). Leave blank to skip.",
    ""
  )

  // ── Patrol settings (mirror of Combat Patrol module, settable from slayer tab) ───────────────
  private val patrolRouteNameProxy = TextSetting(
    "Patrol Route",
    "Patrol route name. Shared with the Combat Patrol module.",
    CombatPatrolModule.routeName.value
  )
  private val patrolLoadAction = ActionSetting("Load Patrol Route", "Load the patrol route by name.", "Load") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.loadRoute()
  }
  private val patrolSaveAction = ActionSetting("Save Patrol Route", "Save the current patrol route.", "Save") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.saveRoute()
  }
  private val patrolStartAction = ActionSetting("Start Patrol", "Start patrol immediately.", "Start") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.startPatrol()
  }
  private val patrolStopAction = ActionSetting("Stop Patrol", "Stop the running patrol.", "Stop") {
    CombatPatrolModule.stopPatrol()
  }

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

  private val oneTapMode = CheckboxSetting(
    "One Tap Mode",
    "Keep moving through mobs — don't stop at each kill. Switch immediately when target dies.",
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
        currentTargetId != null && startedPath && nativeActive() -> "Pathing To Target"
        currentTargetId != null -> "Engaging Target"
        startedPath && nativeActive() -> "Pathing"
        else -> "Searching"
      }
  val targetDisplay: String
    get() {
      val activeTargetName = resolveCurrentTargetName()
      if (!activeTargetName.isNullOrBlank()) {
        return activeTargetName
      }
      if (cryptZombieSlayer.value) {
        if (slayerBossActive) return "Slayer Boss"
        val activeName = resolveCurrentTargetName()
        if (!activeName.isNullOrBlank()) {
          val norm = normalizeNameForMatch(activeName)
          if (isSlayerPriorityMobName(norm)) return "⭐ $activeName"
        }
        return when (slayerType.value) {
          0 -> "Zombie / Ghoul"
          1 -> "Wolf"
          2 -> "Spider"
          3 -> "Voidling"
          4 -> "Vampire"
          5 -> "Blaze"
          else -> "Mob"
        }
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
  private var slayerQuestReady = false
  private var slayerRagnarokUsedPreBoss = false
  private var slayerAutoDetected = false
  private var slayerDetectStartTick = -1L
  private var slayerNeedsQuestClaim = false
  private var slayerNeedsWalkback = false
  private var slayerWalkbackJustFarm = false  // true = walkback is walk-IN to farm area (skip quest restart)
  private var slayerWalkInDelayUntilTick = -1L  // wait for warp to finish before starting walkin route
  private var slayerDeathRespawnPending = false  // player died; trigger walkback once they respawn
  private var lastKnownLevel: net.minecraft.client.multiplayer.ClientLevel? = null  // server-switch detection
  private var slayerBossLastPos: net.minecraft.world.phys.Vec3? = null
  private var slayerClaimStartTick = -1L
  private var slayerClaimLastClickTick = -1L
  private var slayerLastBatphoneAttemptTick = -1L
  private var slayerLastBatphoneUseTick = -1L
  private var slayerLastGuiActionTick = -1L
  private var slayerWarnNoBatphoneTick = -1L
  private var slayerModeEnabled = false
  /** True once the player has physically been confirmed inside the crypt since the last warp-hub.
   *  The outside-crypt recovery check only fires when this is true, preventing the startup spam
   *  loop where the player warps to hub and the check immediately re-triggers before they arrive. */
  private var slayerEnteredCrypt = false
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
      slayerType,
      slayerTier,
      slayerAutoWarp,
      slayerLocation,
      slayerWalkbackRoute,
      patrolRouteNameProxy,
      patrolLoadAction,
      patrolSaveAction,
      patrolStartAction,
      patrolStopAction,
      searchRange,
      minCps,
      maxCps,
      attackRange,
      chaseStopBuffer,
      stayNearStart,
      startAreaRadius,
      oneTapMode,
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
    slayerType.inGroup(TAB_SLAYER_GROUP)
    slayerTier.inGroup(TAB_SLAYER_GROUP)
    slayerAutoWarp.inGroup(TAB_SLAYER_GROUP)

    slayerLocation.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    slayerWalkbackRoute.inGroup(TAB_SLAYER_SETTINGS_GROUP)

    patrolRouteNameProxy.inGroup(TAB_PATROL_GROUP)
    patrolLoadAction.inGroup(TAB_PATROL_GROUP)
    patrolSaveAction.inGroup(TAB_PATROL_GROUP)
    patrolStartAction.inGroup(TAB_PATROL_GROUP)
    patrolStopAction.inGroup(TAB_PATROL_GROUP)

    oneTapMode.inGroup(TAB_COMBAT_GROUP)
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
    // Server-switch detection: if level instance changes while macro is active, stop everything.
    val currentLevel = mc.level
    if (currentLevel !== lastKnownLevel) {
      if (lastKnownLevel != null && (enabled.value || slayerModeEnabled)) {
        stopMacro()
        ChatUtils.sendMessage("Combat macro stopped: server change detected.")
      }
      lastKnownLevel = currentLevel
    }

    // Release heal key and restore slot from previous heal use
    if (pendingHealRelease) {
      mc.options.keyUse?.setDown(false)
      pendingHealRelease = false
    }
    if (pendingHealRestoreSlot >= 0) {
      InventoryUtils.holdHotbarSlot(pendingHealRestoreSlot)
      pendingHealRestoreSlot = -1
    }

    val player = mc.player ?: return
    syncAutoItemToggles(player)
    if (startedPath && !nativeActive()) {
      startedPath = false
      lastTargetPos = null
    }
    if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder && !slayerNeedsWalkback) {
      val cmd = NativePathfinder.tick()
      if (cmd != null) cmd.applyToPlayer()
      else MovementManager.clearForcedMovement()  // PLANNING: clear stale flags (prevents jump spam)
    }
    syncLearnedWhitelistFromSetting()
    if (cryptZombieSlayer.value && !slayerModeEnabled && enabled.value) {
      slayerModeEnabled = true
      slayerNeedsQuestRestart = false  // detection will set this if no quest found
      slayerQuestReady = false
      slayerRagnarokUsedPreBoss = false
      slayerAutoDetected = false
      slayerEnteredCrypt = false
      slayerDetectStartTick = mc.level?.gameTime ?: -1L
      slayerLastBatphoneAttemptTick = -1L
      slayerLastBatphoneUseTick = -1L
      slayerLastGuiActionTick = -1L
      // If not already in the farming area, navigate there via walkback route.
      if (slayerLocation.value == 1) {
        val initPos = mc.player?.blockPosition()
        val alreadyInCrypt = initPos != null && CRYPT_PATROL_WAYPOINTS.any { wp ->
          val dx = wp.x - initPos.x; val dz = wp.z - initPos.z
          dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
        }
        if (!alreadyInCrypt) {
          slayerEnteredCrypt = false
          triggerWalkToFarmArea(justFarm = true)
        } else {
          slayerEnteredCrypt = true
        }
      } else if (slayerLocation.value == 0 && slayerWalkbackRoute.value.isNotBlank()
          && CombatPatrolModule.patrolPoints.isNotEmpty()) {
        // Graveyard: check proximity to any patrol point.
        val initPos = mc.player?.blockPosition()
        val nearFarm = initPos == null || CombatPatrolModule.patrolPoints.any { p ->
          val dx = p.x - initPos.x; val dz = p.z - initPos.z
          dx * dx + dz * dz < GRAVEYARD_PROXIMITY_RANGE_SQ
        }
        if (!nearFarm) triggerWalkToFarmArea(justFarm = true)
      }
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
      if (!slayerAutoDetected) tryAutoDetectSlayerQuest(level)
      updateSlayerBossState(level.gameTime)
      if (slayerNeedsQuestClaim) {
        if (handleClaimSlayerQuest(player, level)) return
      }
      // Legacy hub-warp delay — drained on upgrade; can be removed once all users have new config.
      if (slayerWalkInDelayUntilTick >= 0L) {
        if (level.gameTime < slayerWalkInDelayUntilTick) {
          slayerStatus.value = "Warping to Hub..."
          return
        }
        slayerWalkInDelayUntilTick = -1L
        triggerWalkToFarmArea(justFarm = true)
      }
      if (slayerNeedsWalkback) {
        // Walkback owns the pathfinder — prevent CombatMacroModule from double-ticking it.
        startedPath = false
        lastTargetPos = null
        if (WalkbackBridge.isRunning?.invoke() == true) {
          // Boss spawned during walkback — interrupt immediately and engage.
          if (slayerBossActive) {
            WalkbackBridge.stopWalkback?.invoke()
            slayerNeedsWalkback = false
            startAreaOrigin = null
            slayerStatus.value = "Boss! Engaging..."
            // fall through to combat resolution below
          } else {
            slayerStatus.value = if (slayerWalkbackJustFarm) "Walking to Farm..." else "Walking Back..."
            return
          }
        } else {
          slayerNeedsWalkback = false
          startAreaOrigin = null
          if (slayerWalkbackJustFarm) {
            slayerWalkbackJustFarm = false
            // Walkback complete — ensure quest detection runs for fresh quest
            slayerAutoDetected = false
            slayerDetectStartTick = mc.level?.gameTime ?: -1L
          } else {
            slayerNeedsQuestRestart = true
          }
        }
      }
      if (handleMaddoxGui(level.gameTime)) {
        return
      }
      if (tryRestartSlayerQuest(player, level.gameTime)) {
        return
      }
      // Use Ragnarok pre-emptively when quest is ready (boss spawns on next kill)
      if (slayerQuestReady && !slayerBossActive && !slayerRagnarokUsedPreBoss && autoRagnarok.value
        && level.gameTime - slayerLastRagnarokUseTick >= SLAYER_RAGNAROK_COOLDOWN_TICKS) {
        if (useHotbarUtilityItem(player, SLAYER_RAGNAROK_KEYWORDS)) {
          slayerRagnarokUsedPreBoss = true
          slayerRagnarokUsedThisBoss = true
          slayerLastRagnarokUseTick = level.gameTime
        }
      }
    } else {
      slayerStatus.value = "Off"
      clearSlayerBossState(false)
      slayerNeedsQuestRestart = false
    }

    if (player.isDeadOrDying || player.health <= 0f) {
      if (cryptZombieSlayer.value && !slayerDeathRespawnPending) {
        // Slayer: don't stop — queue walkback for when the player respawns.
        slayerDeathRespawnPending = true
        if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
        if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
        if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
        slayerNeedsWalkback = false
        nativeStop()
        slayerStatus.value = "Died — respawning..."
      } else if (!cryptZombieSlayer.value) {
        stopMacro()
      }
      return
    }
    // Respawn after slayer death: trigger walkback so player returns to farm area.
    if (slayerDeathRespawnPending) {
      slayerDeathRespawnPending = false
      startAreaOrigin = null
      triggerWalkToFarmArea(justFarm = false)
    }

    if (stayNearStart.value && startAreaOrigin == null) {
      val pos = player.blockPosition()
      // For crypt location, only anchor the start area when the player is actually
      // inside the crypt — prevents hub spawn from being used as the anchor point
      // which would block the walk-in route.
      val shouldAnchor = !(cryptZombieSlayer.value && slayerLocation.value == 1) ||
        CRYPT_PATROL_WAYPOINTS.any { wp ->
          val dx = wp.x - pos.x; val dz = wp.z - pos.z
          dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
        }
      if (shouldAnchor) startAreaOrigin = pos
    }

    tryAutoHeal(player, level.gameTime)
    // For crypt slayer: track when the player is confirmed inside the crypt, and recover
    // (warp hub + walk-in) only if they previously entered and then unexpectedly left.
    // The slayerEnteredCrypt guard prevents the startup spam loop where the player warps to
    // hub and the outside-crypt check immediately re-fires before they have walked back in.
    if (cryptZombieSlayer.value && slayerLocation.value == 1 && !slayerBossActive
      && slayerWalkInDelayUntilTick < 0L && !slayerNeedsWalkback) {
      val inCrypt = CRYPT_PATROL_WAYPOINTS.any { wp ->
        val dx = wp.x - player.x; val dz = wp.z - player.z
        dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
      }
      if (inCrypt) {
        slayerEnteredCrypt = true   // confirmed inside — enable recovery trigger
      } else if (slayerEnteredCrypt) {
        // Was inside the crypt but now isn't — navigate back via walkback route.
        ChatUtils.sendMessage("Combat macro: outside crypt, walking back.")
        startAreaOrigin = null
        slayerEnteredCrypt = false
        triggerWalkToFarmArea(justFarm = false)
        return
      }
      // If !inCrypt && !slayerEnteredCrypt: still making initial walk-in, do nothing here
    }
    // Don't enforce start area when the slayer boss is active — chase it wherever it goes
    if (!(cryptZombieSlayer.value && slayerBossActive) && enforceStartArea(player, level)) {
      return
    }

    val target = if (cryptZombieSlayer.value) resolveSlayerTarget(player, level) else resolveTarget(player)
    if (target == null) {
      // Boss is active but not yet visible — walk toward its last known position so we can find it.
      if (cryptZombieSlayer.value && slayerBossActive) {
        val bossPos = slayerBossLastPos
        if (bossPos != null) {
          val bossBlockPos = BlockPos(bossPos.x.toInt(), bossPos.y.toInt(), bossPos.z.toInt())
          if (!nativeActive() || lastTargetPos != bossBlockPos) {
            if (level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
              lastPathStartTick = level.gameTime
              NativePathfinder.setTarget(bossBlockPos.x + 0.5, bossBlockPos.y.toDouble(), bossBlockPos.z + 0.5)
              val started = true
              if (started) {
                lastTargetPos = bossBlockPos
                startedPath = true
              }
            }
          }
          currentTargetId = null
          return
        }
      }
      // No target — hand off to CombatPatrolModule if patrol points are configured.
      if (CombatPatrolModule.patrolPoints.isNotEmpty() && !CombatPatrolModule.isPatrolRunning) {
        CombatPatrolModule.startPatrol()
      }
      if (CombatPatrolModule.isPatrolRunning) {
        when (CombatPatrolModule.patrolState) {
          CombatPatrolModule.PatrolState.COMBAT_INTERRUPT -> CombatPatrolModule.onCombatResume()
          CombatPatrolModule.PatrolState.AT_KILL_ZONE     -> CombatPatrolModule.onKillZoneCleared()
          else -> { /* NAVIGATING/WARPING — patrol owns movement */ }
        }
        if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder) nativeStop()
        startedPath = false; lastTargetPos = null; currentTargetId = null
        return
      }

      if (startedPath && nativeActive()) {
        nativeStop()
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
      ensureSlayerWeapon(player)
      tryUseSlayerSupportItems(player, target, level.gameTime)
    }

    if (inCloseChaseRange) {
      // Always update the rotation target so the camera smoothly tracks a moving mob.
      // BezierTrackingRotationStrategy + snapThreshold handle natural deceleration near target.
      RotationExecutor.rotateTo(AngleUtils.getRotation(target), rotationStrategy)
    } else if (dist > attackRange.value + chaseStopBuffer.value + ROTATION_STOP_HYSTERESIS) {
      // Only stop rotating once clearly out of range — prevents rapid start/stop at the boundary.
      RotationExecutor.stopRotating()
    }

    if (inAttackRange) {
      if (!oneTapMode.value && startedPath && nativeActive()) {
        nativeStop()
      }
      if (!oneTapMode.value) {
        startedPath = false
        lastTargetPos = null
      }
      stuckRepathCount = 0
      attemptAttack(player, target)
    } else {
      // Target spotted — stop kill patrol so combat macro can take over pathfinding.
      if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
      if (CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.NAVIGATING ||
          CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.WARPING) {
        CombatPatrolModule.onCombatInterrupt()
      }
      val targetPos = target.blockPosition()
      val last = lastTargetPos
      val targetMovedFar = last == null || last.distSqr(targetPos) > TARGET_REPATH_DISTANCE_SQ
      if (!nativeActive() || targetMovedFar) {
        if (level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = level.gameTime
          val started = startCombatPathToTarget(level, targetPos)
          if (started) {
            lastTargetPos = targetPos
            startedPath = true
          } else {
            startedPath = false
            if (!nativeActive()) {
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
    // Kill zone mode: only target mobs near the current kill point.
    val killPoint = CombatPatrolModule.currentKillPoint
    if (killPoint != null) {
      val kx = killPoint.x + 0.5; val ky = killPoint.y.toDouble(); val kz = killPoint.z + 0.5
      val radiusSq = CombatPatrolModule.killZoneRadiusValue * CombatPatrolModule.killZoneRadiusValue
      val blacklisted = builtInBlacklistedNames
      val filter = targetName.value.trim().lowercase()
      var best: LivingEntity? = null
      var bestDist = Double.POSITIVE_INFINITY
      for (entity in level.entitiesForRendering()) {
        val living = entity as? LivingEntity ?: continue
        if (!isValidTarget(living, player, blacklisted, filter, true)) continue
        val dx = living.x - kx; val dy = living.y - ky; val dz = living.z - kz
        val distSq = dx * dx + dy * dy + dz * dz
        if (distSq > radiusSq) continue
        val playerDist = player.distanceToSqr(living)
        if (playerDist < bestDist) { best = living; bestDist = playerDist }
      }
      if (best != null) currentTargetId = best.uuid
      return best
    }
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
    var bestPriorityMob: LivingEntity? = null
    var bestPriorityMobDist = Double.POSITIVE_INFINITY
    var bestFarmMob: LivingEntity? = null
    var bestFarmMobDist = Double.POSITIVE_INFINITY

    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, "", true)) continue

      val name = normalizeNameForMatch(living.name.string)
      val isBoss = isSlayerBossName(name)
      val isPriority = isSlayerPriorityMobName(name)
      val isFarmMob = isSlayerFarmMobName(name)
      if (!isBoss && !isPriority && !isFarmMob) continue

      if (startOrigin != null && !isBoss) {
        // Boss ignores start area — chase it wherever it goes
        val ox = living.x - (startOrigin.x + 0.5)
        val oz = living.z - (startOrigin.z + 0.5)
        if (ox * ox + oz * oz > startAreaRangeSq) continue
      }

      val dx = living.x - player.x
      val dy = living.y - player.y
      val dz = living.z - player.z
      val distSq = dx * dx + dy * dy + dz * dz
      if (!isBoss && distSq > searchRangeSq) continue // boss ignores search range

      if (isBoss && distSq < bestBossDist) {
        bestBoss = living
        bestBossDist = distSq
      }
      if (isPriority && distSq < bestPriorityMobDist) {
        bestPriorityMob = living
        bestPriorityMobDist = distSq
      }
      if (isFarmMob && distSq < bestFarmMobDist) {
        bestFarmMob = living
        bestFarmMobDist = distSq
      }
    }

    if (bestBoss != null) {
      onSlayerBossDetected(level.gameTime)
      slayerBossLastPos = bestBoss.position()
    } else if (slayerBossActive && level.gameTime - slayerBossLastSeenTick > SLAYER_BOSS_ENTITY_LOST_TICKS) {
      clearSlayerBossState()
    }

    // Priority: boss > priority farm mob (rare/bonus) > regular farm mob
    val selected = if (slayerBossActive) bestBoss
      else bestBoss ?: bestPriorityMob ?: bestFarmMob
    if (selected != null) {
      currentTargetId = selected.uuid
    }
    return selected
  }

  private fun updateSlayerBossState(nowTick: Long) {
    val tabLines = readTabListLines(nowTick)
    val hasBossTabLine = tabLines.any { line -> SLAYER_BOSS_TAB_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasClearTabLine = tabLines.any { line -> SLAYER_BOSS_CLEAR_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasQuestTabLine = tabLines.any { line ->
      SLAYER_QUEST_TAB_KEYWORDS.any { keyword -> line.contains(keyword) }
        || SLAYER_GENERIC_QUEST_KEYWORDS.any { keyword -> line.contains(keyword) }
    }

    if (hasBossTabLine) {
      onSlayerBossDetected(nowTick)
      slayerNeedsQuestRestart = false
      slayerQuestReady = false
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
        slayerNeedsQuestClaim -> "Claiming Loot"
        slayerQuestReady -> "Quest Ready!"
        slayerNeedsQuestRestart -> "Restarting Quest"
        else -> "Farming ${when (slayerType.value) { 0 -> "Zombies"; 1 -> "Wolves"; 2 -> "Spiders"; 3 -> "Voidlings"; 4 -> "Vampires"; 5 -> "Blazes"; else -> "Mobs" }}"
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
    slayerRagnarokUsedPreBoss = false
    slayerQuestReady = false
    slayerLastBadHealthUseTick = -1L
    slayerBossLastPos = null
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
    // Don't fire the batphone until quest detection has settled
    if (!slayerAutoDetected) return true
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
    val (actionSlot, isFinalPurchase) = findMaddoxMenuActionSlot(menu.slots)
    if (actionSlot >= 0) {
      InventoryUtils.clickSlot(actionSlot)
      slayerLastGuiActionTick = nowTick
      slayerLastBatphoneAttemptTick = nowTick
      if (isFinalPurchase) {
        player.closeContainer()
        slayerNeedsQuestRestart = false
        slayerQuestReady = false
        slayerRagnarokUsedPreBoss = false
        slayerAutoDetected = true
        startAreaOrigin = null
        if (cryptZombieSlayer.value && slayerLocation.value == 1) {
          // Crypt: check if already inside the farming area — if so skip the warp and walk-in.
          val alreadyInCrypt = CRYPT_PATROL_WAYPOINTS.any { wp ->
            val dx = wp.x - player.x; val dz = wp.z - player.z
            dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
          }
          if (alreadyInCrypt) {
            slayerEnteredCrypt = true
          } else {
            slayerEnteredCrypt = false
            triggerWalkToFarmArea(justFarm = true)
          }
        } else if (slayerAutoWarp.value) {
          val warp = slayerWarpCommand
          if (warp.isNotEmpty()) player.connection?.sendCommand(warp)
        }
      }
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

  /** Called when the loot claim step finishes (success or timeout). Starts the walkback route
   *  or proceeds directly to quest restart. */
  private fun finishSlayerClaim() {
    slayerNeedsQuestClaim = false
    slayerBossLastPos = null
    // Crypt: use the configured walkback route to walk out and back to hub for quest restart.
    if (slayerLocation.value == 1 && cryptZombieSlayer.value) {
      val routeName = slayerWalkbackRoute.value.trim()
      if (!slayerAutoWarp.value && routeName.isNotBlank()) {
        val started = WalkbackBridge.startWalkback?.invoke(routeName, 5) ?: false
        if (started) {
          slayerNeedsWalkback = true
          return
        }
      }
      slayerNeedsQuestRestart = true
      return
    }
    // Graveyard: use configured walkback route if auto-warp is off.
    val routeName = slayerWalkbackRoute.value.trim()
    if (!slayerAutoWarp.value && routeName.isNotBlank()) {
      val started = WalkbackBridge.startWalkback?.invoke(routeName, 5) ?: false
      if (started) {
        slayerNeedsWalkback = true
        return
      }
    }
    slayerNeedsQuestRestart = true
  }

  /**
   * Walks to the boss death position, right-clicks the loot bag entity, then transitions
   * to quest restart. Returns true while the claim step is still in progress.
   */
  private fun handleClaimSlayerQuest(player: LocalPlayer, level: ClientLevel): Boolean {
    val nowTick = level.gameTime

    // Timeout — give up and restart the quest anyway
    if (slayerClaimStartTick >= 0L && nowTick - slayerClaimStartTick > SLAYER_CLAIM_TIMEOUT_TICKS) {
      finishSlayerClaim()
      return false
    }

    val deathPos = slayerBossLastPos
    if (deathPos == null) {
      // No tracked position — skip straight to restart
      finishSlayerClaim()
      return false
    }

    // Search for a loot bag ArmorStand near the death position
    val lootEntity = level.entitiesForRendering()
      .filterIsInstance<ArmorStand>()
      .filter { entity ->
        val dx = entity.x - deathPos.x
        val dy = entity.y - deathPos.y
        val dz = entity.z - deathPos.z
        dx * dx + dy * dy + dz * dz < SLAYER_LOOT_SEARCH_RADIUS_SQ
      }
      .filter { entity ->
        val name = normalizeNameForMatch(entity.name.string)
        SLAYER_LOOT_KEYWORDS.any { name.contains(it) }
      }
      .minByOrNull { entity ->
        val dx = entity.x - player.x
        val dy = entity.y - player.y
        val dz = entity.z - player.z
        dx * dx + dy * dy + dz * dz
      }

    if (lootEntity == null) {
      // No loot bag found yet — walk to death pos and keep waiting
      val targetPos = BlockPos(deathPos.x.toInt(), deathPos.y.toInt(), deathPos.z.toInt())
      if (!nativeActive() || lastTargetPos != targetPos) {
        if (nowTick - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = nowTick
          NativePathfinder.setTarget(targetPos.x + 0.5, targetPos.y.toDouble(), targetPos.z + 0.5)
          val started = true
          if (started) {
            lastTargetPos = targetPos
            startedPath = true
          }
        }
      }
      return true
    }

    // Loot entity found — stop pathing, look at it and right-click
    if (startedPath && nativeActive()) {
      nativeStop()
      startedPath = false
    }

    val dx = lootEntity.x - player.x
    val dy = lootEntity.y - player.y
    val dz = lootEntity.z - player.z
    val distSq = dx * dx + dy * dy + dz * dz

    if (distSq > SLAYER_LOOT_CLAIM_RANGE_SQ) {
      val targetPos = lootEntity.blockPosition()
      if (!nativeActive()) {
        if (nowTick - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = nowTick
          NativePathfinder.setTarget(targetPos.x + 0.5, targetPos.y.toDouble(), targetPos.z + 0.5)
          lastTargetPos = targetPos
          startedPath = true
        }
      }
      return true
    }

    val rotation = AngleUtils.getRotation(lootEntity)
    RotationExecutor.rotateTo(rotation, rotationStrategy)

    if (nowTick - slayerClaimLastClickTick >= SLAYER_CLAIM_CLICK_INTERVAL_TICKS) {
      slayerClaimLastClickTick = nowTick
      MouseUtils.rightClick()
    }

    // After a short dwell at the loot bag, assume claimed and move on
    if (nowTick - slayerClaimStartTick > SLAYER_CLAIM_DWELL_TICKS) {
      finishSlayerClaim()
    }

    return true
  }

  private fun isLikelyMaddoxScreen(nowTick: Long): Boolean {
    val screen = mc.screen as? AbstractContainerScreen<*> ?: return false
    val title = normalizeNameForMatch(screen.title.string)
    if (SLAYER_MADDOX_SCREEN_KEYWORDS.any { keyword -> title.contains(keyword) }) return true
    return slayerLastBatphoneUseTick >= 0L && nowTick - slayerLastBatphoneUseTick <= SLAYER_MADDOX_GUI_TIMEOUT_TICKS
  }

  @SubscribeEvent
  fun onChatReceive(event: ChatEvent.Receive) {
    val raw = event.message ?: return
    val msg = ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT)?.trim() ?: return

    if (enabled.value) {
      // Player name mentioned in chat → play system alert sound.
      val ign = mc.player?.gameProfile?.name?.lowercase(Locale.ROOT) ?: ""
      if (ign.isNotEmpty() && msg.contains(ign)) {
        java.awt.Toolkit.getDefaultToolkit().beep()
      }
      // Evacuated from hub or moved to different server → walk back to farm area.
      if (msg.contains("evacuated from") || msg.contains("moved to a different server")) {
        triggerWalkToFarmArea(justFarm = false)
        return
      }
    }

    if (!cryptZombieSlayer.value) return

    if (SLAYER_QUEST_READY_CHAT_KEYWORDS.any { msg.contains(it) }) {
      slayerQuestReady = true
      slayerNeedsQuestRestart = false
      ChatUtils.sendMessage("Combat macro: Quest ready — Ragnarok prepped for boss spawn.")
    }

    if (SLAYER_BOSS_SPAWNED_CHAT_KEYWORDS.any { msg.contains(it) }) {
      val level = mc.level ?: return
      onSlayerBossDetected(level.gameTime)
      slayerQuestReady = false
    }

    if (SLAYER_BOSS_KILLED_CHAT_KEYWORDS.any { msg.contains(it) }) {
      clearSlayerBossState(false)
      slayerNeedsQuestClaim = true
      slayerClaimStartTick = mc.level?.gameTime ?: -1L
    }
  }

  private fun tryAutoDetectSlayerQuest(level: ClientLevel) {
    val nowTick = level.gameTime

    // Scan scoreboard sidebar
    val scoreboard = level.scoreboard
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR)
    if (objective != null) {
      for (score in scoreboard.listPlayerScores(objective)) {
        val ownerName = score.owner()
        val team = scoreboard.getPlayersTeam(ownerName)
        val raw = if (team != null) team.playerPrefix.string + ownerName + team.playerSuffix.string else ownerName
        val line = ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT)?.trim() ?: continue
        if (applyQuestDetection(line, "scoreboard")) return
      }
    }

    // Scan tab list
    val tabLines = readTabListLines(nowTick)
    for (line in tabLines) {
      if (applyQuestDetection(line, "tab")) return
    }

    // Nothing found yet — only conclude "no quest" after the detection window expires
    if (slayerDetectStartTick >= 0L && nowTick - slayerDetectStartTick >= SLAYER_DETECT_WINDOW_TICKS) {
      slayerAutoDetected = true
      slayerNeedsQuestRestart = true // no quest found in window → buy one
    }
    // else: still within window — farming continues normally, detection runs next tick
  }

  /** Returns true and updates state if the line matches any active slayer quest. */
  private fun applyQuestDetection(line: String, source: String): Boolean {
    var typeIdx = -1
    when {
      line.contains("zombie slayer") || (line.contains("revenant") && line.contains("slayer")) -> typeIdx = 0
      line.contains("wolf slayer") || (line.contains("sven") && line.contains("slayer")) -> typeIdx = 1
      line.contains("spider slayer") || (line.contains("tarantula") && line.contains("slayer")) -> typeIdx = 2
      line.contains("enderman slayer") || (line.contains("voidgloom") && line.contains("slayer")) -> typeIdx = 3
      line.contains("vampire slayer") || (line.contains("riftstalker") && line.contains("slayer")) -> typeIdx = 4
      line.contains("blaze slayer") || (line.contains("inferno") && line.contains("slayer")) -> typeIdx = 5
    }
    if (typeIdx < 0) return false

    val tier = when {
      line.contains(" v") && !line.contains(" vi") -> 5
      line.contains(" iv") -> 4
      line.contains(" iii") -> 3
      line.contains(" ii") -> 2
      line.contains(" i") -> 1
      else -> -1
    }
    if (slayerType.value != typeIdx) {
      slayerType.value = typeIdx
      ChatUtils.sendMessage("Combat macro: Detected ${slayerType.options[typeIdx]} Slayer from $source.")
    }
    if (tier in 1..5) slayerTier.value = tier.toDouble()
    slayerNeedsQuestRestart = false
    slayerAutoDetected = true
    return true
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

  /**
   * Returns (slotIndex, isFinalPurchase).
   * isFinalPurchase = true  → slot is "Start Quest" / "Restart" / "Confirm" — quest will be bought.
   * isFinalPurchase = false → slot is just the revenant-icon navigation step.
   */
  private fun findMaddoxMenuActionSlot(slots: List<net.minecraft.world.inventory.Slot>): Pair<Int, Boolean> {
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

      if (hasConfirmKeyword && hasRevenantKeyword) return Pair(slot.index, true)
      if (hasRestartKeyword && hasRevenantKeyword) return Pair(slot.index, true)

      if (hasRevenantKeyword && revenantSlot == -1) revenantSlot = slot.index
      if (hasRestartKeyword && restartSlot == -1) restartSlot = slot.index
      if (hasConfirmKeyword && confirmSlot == -1) confirmSlot = slot.index
    }

    return when {
      confirmSlot >= 0 -> Pair(confirmSlot, true)
      restartSlot >= 0 -> Pair(restartSlot, true)
      revenantSlot >= 0 -> Pair(revenantSlot, false) // navigation only, not yet purchased
      else -> Pair(-1, false)
    }
  }

  private fun useHotbarUtilityItem(player: Player, keywords: Array<String>): Boolean {
    val slot = findHotbarSlotByKeywords(player, keywords)
    if (slot !in 0..8) return false

    val previousSlot = player.inventory.selectedSlot
    if (previousSlot != slot) {
      InventoryUtils.holdHotbarSlot(slot)
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

  private fun isSlayerPriorityMobName(normalizedName: String): Boolean {
    return SLAYER_PRIORITY_MOB_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
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

    val pathAlreadyReturning = startedPath && lastTargetPos == origin && nativeActive()
    if (!pathAlreadyReturning && level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
      lastPathStartTick = level.gameTime
      NativePathfinder.setTarget(origin.x + 0.5, origin.y.toDouble(), origin.z + 0.5)
      if (true) {
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
        if (oneTapMode.value) currentTargetId = null
        clearKillCandidate()
      }
      return
    }
    if (candidate.isAlive && candidate.health > 0f) {
      return
    }

    applyLearnedKillTarget(killCandidateName ?: candidate.name.string)
    if (oneTapMode.value) currentTargetId = null
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

  /** Ensures a weapon is selected when in slayer mode. Switches away from utility items
   *  (batphone, overflux, etc.) to the nearest sword/weapon in the hotbar. */
  private fun ensureSlayerWeapon(player: Player) {
    if (pendingHealRelease || pendingHealRestoreSlot >= 0) return
    val currentSlot = player.inventory.selectedSlot
    val current = player.inventory.getItem(currentSlot)
    if (!current.isEmpty) {
      val name = normalizeNameForMatch(current.hoverName?.string.orEmpty())
      val isUtility = SLAYER_NON_WEAPON_KEYWORDS.any { name.contains(it) }
      if (!isUtility) return // already holding a weapon
    }
    // Current item is a utility item or empty — find a weapon
    val weaponSlot = findHotbarSlotByKeywords(player, SLAYER_WEAPON_KEYWORDS)
    if (weaponSlot in 0..8 && weaponSlot != currentSlot) {
      InventoryUtils.holdHotbarSlot(weaponSlot)
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
      InventoryUtils.holdHotbarSlot(healSlot)
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
    if (inAttackRange || !startedPath || !nativeActive()) {
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
        nativeStop()
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
      if (cryptZombieSlayer.value) {
        ChatUtils.sendMessage("Combat macro stuck. Triggering walkback.")
        startAreaOrigin = null
        stuckTicks = 0
        slayerEnteredCrypt = false
        triggerWalkToFarmArea(justFarm = false)
      } else {
        nativeStop()
        (player as? LocalPlayer)?.connection?.sendCommand("warp hub")
        ChatUtils.sendMessage("Combat macro stuck. Warping to hub.")
        stopMacro()
      }
    }
  }

  private fun startCombatPathToTarget(level: ClientLevel, targetPos: BlockPos): Boolean {
    // Jitter the destination slightly so the path never goes perfectly straight to the mob.
    val dest = jitterTargetPos(targetPos)
    NativePathfinder.setTarget(dest.x + 0.5, dest.y.toDouble(), dest.z + 0.5)
    return true
  }

  /** Returns a copy of [pos] offset by a small random amount on X/Z so paths curve off-centre. */
  private fun jitterTargetPos(pos: BlockPos): BlockPos {
    val offX = (Math.random() * (PATH_JITTER_RANGE * 2 + 1) - PATH_JITTER_RANGE).toInt()
    val offZ = (Math.random() * (PATH_JITTER_RANGE * 2 + 1) - PATH_JITTER_RANGE).toInt()
    return pos.offset(offX, 0, offZ)
  }

  /**
   * Navigate back to the farming area using the configured walkback route.
   * [justFarm] = true  → just walk in; don't restart the quest after arriving.
   * [justFarm] = false → buy a new quest once walkback completes.
   */
  private fun triggerWalkToFarmArea(justFarm: Boolean) {
    if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
    if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
    if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
    nativeStop()
    val routeName = when {
      slayerLocation.value == 1 -> CRYPT_WALKBACK_ROUTE_NAME
      slayerWalkbackRoute.value.isNotBlank() -> slayerWalkbackRoute.value.trim()
      else -> ""
    }
    if (routeName.isBlank()) {
      if (!justFarm) slayerNeedsQuestRestart = true
      return
    }
    val started = WalkbackBridge.startWalkback?.invoke(routeName, 0) ?: false
    if (started) {
      slayerNeedsWalkback = true
      slayerWalkbackJustFarm = justFarm
    } else {
      ChatUtils.sendMessage("Combat macro: walkback route \"$routeName\" not found.")
      if (!justFarm) slayerNeedsQuestRestart = true
    }
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

  private fun nativeActive(): Boolean =
    NativePathfinder.status.let { it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED }

  private fun nativeStop() {
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
  }

  private fun stopMacro() {
    if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
    if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
    if (startedPath && nativeActive()) {
      nativeStop()
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
    slayerQuestReady = false
    slayerRagnarokUsedPreBoss = false
    slayerAutoDetected = false
    slayerDetectStartTick = -1L
    slayerNeedsQuestClaim = false
    if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
    slayerNeedsWalkback = false
    slayerWalkbackJustFarm = false
    slayerDeathRespawnPending = false
    slayerWalkInDelayUntilTick = -1L
    slayerBossLastPos = null
    slayerClaimStartTick = -1L
    slayerClaimLastClickTick = -1L
    slayerLastBatphoneAttemptTick = -1L
    slayerLastBatphoneUseTick = -1L
    slayerLastGuiActionTick = -1L
    slayerWarnNoBatphoneTick = -1L
    slayerModeEnabled = false
    slayerEnteredCrypt = false
  }

  private const val TAB_COMBAT_GROUP = "Combat Macro"
  private const val TAB_SLAYER_GROUP = "Slayer Macro"
  private const val TAB_SLAYER_SETTINGS_GROUP = "Slayer Settings"
  private const val TAB_PATROL_GROUP = "Patrol"
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
  private const val SLAYER_BATPHONE_RETRY_TICKS = 80L
  private const val SLAYER_MADDOX_GUI_TIMEOUT_TICKS = 80L
  private const val SLAYER_GUI_ACTION_COOLDOWN_TICKS = 5L
  private const val SLAYER_NO_BATPHONE_WARN_TICKS = 200L
  private const val SLAYER_DETECT_WINDOW_TICKS = 100L  // 5 s to scan before assuming no quest
  private const val PLAYER_HOTBAR_MENU_SLOT_START = 36
  private const val PLAYER_HOTBAR_MENU_SLOT_END = 44
  private const val DEFAULT_WHITELIST_ENTRY = "ice walker"
  private const val MIN_PATH_START_INTERVAL_TICKS = 20L
  private const val TARGET_REPATH_DISTANCE_SQ = 16.0  // repath when mob moves 4+ blocks
  private const val COMBAT_ROTATION_STEP_SCALE = 0.62
  private const val PATH_JITTER_RANGE = 2              // slight offset so paths vary naturally
  private const val ROTATION_STOP_HYSTERESIS = 2.5     // don't stop rotating until clearly out of range
  private const val CRYPT_WALKBACK_ROUTE_NAME = "cryptwalkback"
  private const val SLAYER_WALKIN_WARP_DELAY_TICKS = 40L  // ticks to wait after warp hub before starting walkin route
  private const val CRYPT_PROXIMITY_RANGE_SQ = 1600.0    // 40-block radius — if within this of any patrol point, already in crypt
  private const val GRAVEYARD_PROXIMITY_RANGE_SQ = 900.0  // 30-block radius — if within this of any patrol point, already at graveyard
  private const val CHASE_RESOLVE_RADIUS = 3
  private const val CHASE_RESOLVE_VERTICAL = 2
  private const val START_AREA_RETURN_BUFFER = 2.0
  private const val TARGET_BOX_CYCLE_MS = 4000L
  private const val TARGET_BOX_ALPHA = 170
  private const val TARGET_BOX_INFLATE = 0.08
  private val SLAYER_BOSS_ENTITY_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("revenant horror")
    1 -> arrayOf("sven packmaster")
    2 -> arrayOf("tarantula broodfather")
    3 -> arrayOf("voidgloom seraph")
    4 -> arrayOf("riftstalker bloodfiend")
    5 -> arrayOf("inferno demonlord")
    else -> arrayOf()
  }
  // Priority mobs: rare/high-XP mobs that should be targeted over regular farm mobs.
  // Only applies at the relevant tier (zombie: tier 3+).
  private val SLAYER_PRIORITY_MOB_KEYWORDS get() = when {
    slayerType.value == 0 && slayerTier.value >= 3 -> arrayOf(
      "atoned horror", "atoned revenant", "atoned champion", "revenant champion",
      "revenant sycophant", "deformed revenant"
    )
    else -> arrayOf()
  }
  private val SLAYER_FARM_MOB_KEYWORDS get() = when (slayerType.value) {
    0 -> when (slayerLocation.value) {
      0 -> arrayOf("zombie") // Zombie Graveyard: regular zombies only
      1 -> arrayOf("crypt ghoul", "golden ghoul") // Zombie Crypt
      else -> arrayOf("zombie", "crypt ghoul", "golden ghoul")
    }
    1 -> arrayOf("pack wolf", "old wolf", "pit wolf", "zombie wolf", "wolf")
    2 -> arrayOf("dasher spider", "voracious spider", "weaver spider", "spider")
    3 -> arrayOf("voidling extremist", "voidling radical", "voidling fanatic", "enderman")
    4 -> arrayOf("bat", "vampiric bat", "bloodfiend")
    5 -> arrayOf("blaze", "smoldering blaze", "emerald slime")
    else -> arrayOf()
  }
  // Only "slay the boss" reliably signals the boss is active.
  // Boss entity names (e.g. "revenant horror") also appear in the quest PROGRESS tab line
  // ("Kill Revenant Horror 50/200"), which would incorrectly set slayerBossActive = true.
  private val SLAYER_BOSS_TAB_KEYWORDS = arrayOf("slay the boss")
  private val SLAYER_BOSS_CLEAR_KEYWORDS = arrayOf("boss slain", "slayer quest complete")
  private val SLAYER_QUEST_TAB_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("slayer quest", "zombie slayer", "revenant horror")
    1 -> arrayOf("slayer quest", "wolf slayer", "sven packmaster")
    2 -> arrayOf("slayer quest", "spider slayer", "tarantula broodfather")
    3 -> arrayOf("slayer quest", "enderman slayer", "voidgloom seraph")
    4 -> arrayOf("slayer quest", "vampire slayer", "riftstalker bloodfiend")
    5 -> arrayOf("slayer quest", "blaze slayer", "inferno demonlord")
    else -> arrayOf("slayer quest")
  }
  // Items that are NOT weapons — switch away from these when preparing to attack
  private val SLAYER_NON_WEAPON_KEYWORDS = arrayOf(
    "batphone", "overflux", "ragnarok", "ragnorak", "wand of atonement",
    "sword of bad health", "drill", "pickaxe", "gauntlet"
  )
  // Items that ARE weapons — prefer these for attacking
  private val SLAYER_WEAPON_KEYWORDS = arrayOf(
    "sword", "blade", "scythe", "katana", "saber", "cleaver", "axe", "rapier", "claymore"
  )
  private val SLAYER_WAND_OF_ATONEMENT_KEYWORDS = arrayOf("wand of atonement")
  private val SLAYER_ZOMBIE_SWORD_KEYWORDS = arrayOf("zombie sword")
  private val SLAYER_OVERFLUX_KEYWORDS = arrayOf("overflux")
  private val SLAYER_RAGNAROK_KEYWORDS = arrayOf("ragnarok", "ragnorak")
  private val SLAYER_BAD_HEALTH_KEYWORDS = arrayOf("sword of bad health")
  private val SLAYER_BATPHONE_KEYWORDS = arrayOf("maddox batphone", "batphone")
  private val SLAYER_MADDOX_SCREEN_KEYWORDS = arrayOf("maddox", "slayer")
  // Generic tab keywords that indicate ANY active slayer quest, regardless of type
  private val SLAYER_GENERIC_QUEST_KEYWORDS = arrayOf("slayer quest", "zombie slayer", "wolf slayer",
    "spider slayer", "enderman slayer", "vampire slayer", "blaze slayer")
  private val SLAYER_MADDOX_REVENANT_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("revenant horror", "zombie slayer", "revenant")
    1 -> arrayOf("sven packmaster", "wolf slayer", "sven")
    2 -> arrayOf("tarantula broodfather", "spider slayer", "tarantula")
    3 -> arrayOf("voidgloom seraph", "enderman slayer", "voidgloom")
    4 -> arrayOf("riftstalker bloodfiend", "vampire slayer", "vampire")
    5 -> arrayOf("inferno demonlord", "blaze slayer", "inferno")
    else -> arrayOf()
  }
  private val SLAYER_MADDOX_RESTART_KEYWORDS = arrayOf("restart", "start quest", "begin quest", "start slayer", "slayer quest", "new quest")
  private val SLAYER_MADDOX_CONFIRM_KEYWORDS = arrayOf("confirm", "click to confirm", "accept", "purchase")
  private val SLAYER_QUEST_READY_CHAT_KEYWORDS = arrayOf(
    "ready to spawn your boss",
    "spawn your boss",
    "right-click to summon",
    "slayer quest is ready",
    "kill a mob to spawn the boss",
    "boss will spawn"
  )
  private val SLAYER_BOSS_SPAWNED_CHAT_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("revenant horror has spawned", "your revenant horror spawned")
    1 -> arrayOf("sven packmaster has spawned", "your sven packmaster spawned")
    2 -> arrayOf("tarantula broodfather has spawned")
    3 -> arrayOf("voidgloom seraph has spawned")
    4 -> arrayOf("riftstalker bloodfiend has spawned")
    5 -> arrayOf("inferno demonlord has spawned")
    else -> arrayOf()
  }
  private val SLAYER_BOSS_KILLED_CHAT_KEYWORDS = arrayOf(
    "you have slain the boss",
    "your slayer quest has been completed",
    "slayer quest complete",
    "boss slain"
  )
  private val SLAYER_LOOT_KEYWORDS = arrayOf("loot", "bag", "drops")
  private const val SLAYER_LOOT_SEARCH_RADIUS_SQ = 36.0  // 6 block radius
  private const val SLAYER_LOOT_CLAIM_RANGE_SQ = 9.0     // 3 block range to right-click
  private const val SLAYER_CLAIM_TIMEOUT_TICKS = 200L    // 10 s before giving up
  private const val SLAYER_CLAIM_DWELL_TICKS = 60L       // 3 s at loot bag before moving on
  private const val SLAYER_CLAIM_CLICK_INTERVAL_TICKS = 5L
  private val slayerWarpCommand get() = when (slayerType.value) {
    0 -> when (slayerLocation.value) {
      0 -> "warp crypts"  // Zombie Graveyard
      else -> ""             // Zombie Crypt: walkback route handles return
    }
    1 -> "warp hub"
    2 -> "warp spiders_den"
    3 -> "warp end"
    4 -> "warp rift"
    5 -> "warp blazing_fortress"
    else -> ""
  }
  // Reference positions spread across the crypt — used only for "is player inside crypt?" proximity checks.
  private val CRYPT_PATROL_WAYPOINTS = listOf(
    BlockPos(-152, 57, -102), BlockPos(-133, 50, -101), BlockPos(-132, 45, -121),
    BlockPos(-129, 41, -134), BlockPos(-129, 41, -143), BlockPos(-119, 41, -136),
    BlockPos(-106, 46, -129), BlockPos(-102, 48, -119), BlockPos(-89, 46, -104),
    BlockPos(-77, 46, -105),  BlockPos(-65, 50, -120),  BlockPos(-48, 55, -136),
    BlockPos(-48, 57, -141),  BlockPos(-46, 55, -136),  BlockPos(-80, 46, -101),
    BlockPos(-88, 42, -88),   BlockPos(-96, 38, -86),   BlockPos(-106, 38, -87),
    BlockPos(-114, 43, -89),  BlockPos(-133, 50, -105), BlockPos(-147, 56, -100),
    BlockPos(-145, 57, -114), BlockPos(-136, 58, -127), BlockPos(-121, 55, -126),
    BlockPos(-109, 50, -119), BlockPos(-102, 48, -119),
  )

  private val CYAN_COLOR = Color(0, 255, 255)
  private val PINK_COLOR = Color(255, 105, 180)
}
