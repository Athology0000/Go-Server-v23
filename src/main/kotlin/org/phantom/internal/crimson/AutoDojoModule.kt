/*
 * Ported and adapted from skies-starred/OdinClient AutoDojo.kt.
 * Original source is BSD-3-Clause licensed, Copyright (c) 2026, Starred.
 */
package org.phantom.internal.crimson

import java.awt.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.skeleton.Skeleton
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.util.AngleUtils
import org.phantom.api.util.InventoryUtils
import org.phantom.api.util.render.Render3D
import org.phantom.mixin.client.MinecraftAccessor

object AutoDojoModule : Module("Auto Dojo") {

  override val category = ModuleCategory.COMBAT
  override val isMacro = true
  override val autoDisableOnWorldUnload = true

  private val enabled = CheckboxSetting(
    "Enabled",
    "Automatically assists Hypixel SkyBlock Dojo tests.",
    false
  )
  private val enableControl = CheckboxSetting(
    "Enable Control",
    "Automatically aim at the real skeleton in Test of Control.",
    true
  )
  private val controlPredictionTicks = SliderSetting(
    "Control Prediction Ticks",
    "How many ticks ahead to predict skeleton movement.",
    5.0,
    1.0,
    20.0,
    1.0
  )
  private val enableMastery = CheckboxSetting(
    "Enable Mastery",
    "Automatically draw and release the bow for Test of Mastery yellow wool.",
    true
  )
  private val masteryShootDelay = SliderSetting(
    "Mastery Shoot Delay",
    "Milliseconds remaining before a yellow block expires before releasing the bow.",
    600.0,
    0.0,
    2000.0,
    50.0
  )
  private val enableDiscipline = CheckboxSetting(
    "Enable Discipline",
    "Automatically switch swords in Test of Discipline.",
    true
  )
  private val disciplineAutoAttack = CheckboxSetting(
    "Discipline Auto Attack",
    "Automatically attack the selected Test of Discipline mob.",
    true
  )
  private val renderStyle = ModeSetting(
    "Render Style",
    "How Auto Dojo targets are rendered.",
    2,
    arrayOf("Filled", "Outline", "Filled Outline")
  )

  private val mc = Minecraft.getInstance()
  private val rotation = SmoothRotation()
  private val masteryBlocks = mutableListOf<MasteryBlock>()

  private var dojoType = DojoType.NONE
  private var targetSkeleton: Entity? = null
  private var lookCooldown = 0L
  private var lastSkeletonPos: Vec3? = null
  private var skeletonVelocity = Vec3.ZERO
  private var firingState = 0
  private var firingTimer = 0
  private var isDrawing = false

  init {
    addSetting(
      enabled,
      enableControl,
      controlPredictionTicks,
      enableMastery,
      masteryShootDelay,
      enableDiscipline,
      disciplineAutoAttack,
      renderStyle,
    )
    EventBus.register(this)
  }

  override fun onDisable() {
    reset()
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val text = event.message?.lowercase() ?: return

    if ("rank:" in text) {
      reset()
      return
    }

    if ("objective" !in text) return
    when {
      "control" in text -> {
        dojoType = DojoType.CONTROL
        lastSkeletonPos = null
      }
      "mastery" in text -> {
        dojoType = DojoType.MASTERY
        selectBow()
      }
      "discipline" in text -> dojoType = DojoType.DISCIPLINE
    }
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) {
      reset()
      return
    }
    if (dojoType == DojoType.NONE) return

    when (dojoType) {
      DojoType.CONTROL -> if (enableControl.value) handleControl()
      DojoType.MASTERY -> if (enableMastery.value) handleMastery()
      DojoType.DISCIPLINE -> if (enableDiscipline.value) handleDiscipline()
      DojoType.NONE,
      DojoType.FORCE -> Unit
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    if (dojoType == DojoType.NONE) return

    if (dojoType == DojoType.CONTROL) {
      targetSkeleton?.takeIf { it.isAlive }?.let { entity ->
        drawTargetBox(event, entity.boundingBox.inflate(0.08))
      }
    }

    if (dojoType == DojoType.MASTERY) {
      for (block in masteryBlocks) {
        drawTargetBox(
          event,
          AABB(
            block.x.toDouble(),
            block.y.toDouble(),
            block.z.toDouble(),
            block.x + 1.0,
            block.y + 1.0,
            block.z + 1.0,
          )
        )
      }
    }

    rotation.update()
  }

  private fun handleControl() {
    val player = mc.player ?: return
    val level = mc.level ?: return

    var closestSkeleton: Entity? = null
    var minDistance = 25.0
    for (entity in level.entitiesForRendering()) {
      if (entity !is WitherSkeleton && !(entity is Skeleton && entity.type == EntityType.WITHER_SKELETON)) continue
      if (entity.getItemBySlot(EquipmentSlot.HEAD).item == Items.REDSTONE_BLOCK) continue

      val distance = player.position().distanceTo(entity.position())
      if (distance >= minDistance) continue
      minDistance = distance
      closestSkeleton = entity
    }

    targetSkeleton = closestSkeleton ?: return
    val currentPos = closestSkeleton.position()
    lastSkeletonPos?.let { skeletonVelocity = currentPos.subtract(it) }
    lastSkeletonPos = currentPos

    val now = System.currentTimeMillis()
    if (now - lookCooldown <= 40L) return
    lookCooldown = now

    setRotation(
      currentPos.x + skeletonVelocity.x * controlPredictionTicks.value,
      currentPos.y + skeletonVelocity.y * 2.0 + 2.5,
      currentPos.z + skeletonVelocity.z * controlPredictionTicks.value,
    )
  }

  private fun handleMastery() {
    val player = mc.player ?: return
    val level = mc.level ?: return
    val now = System.currentTimeMillis()

    masteryBlocks.removeAll { block ->
      block.expiryTime < now || level.getBlockState(BlockPos(block.x, block.y, block.z)).block != Blocks.YELLOW_WOOL
    }

    if (firingState == 1) {
      firingTimer++
      if (firingTimer < 2) return
      mc.options.keyUse.setDown(true)
      isDrawing = true
      firingState = 0
      firingTimer = 0
      return
    }

    scanForMasteryBlocks()
    val closest = masteryBlocks.firstOrNull() ?: return
    setRotation(closest.x + 0.5, closest.y + 1.1, closest.z + 0.5)

    val bowSlot = findItemSlot(Items.BOW) ?: return
    player.inventory.selectedSlot = bowSlot

    if (!isDrawing) {
      mc.options.keyUse.setDown(true)
      isDrawing = true
    }

    val timeRemaining = closest.expiryTime - now
    if (timeRemaining >= masteryShootDelay.value.toLong()) return
    if (!isDrawing) return

    mc.options.keyUse.setDown(false)
    isDrawing = false
    firingState = 1
    firingTimer = 0
    masteryBlocks.removeAt(0)
  }

  private fun handleDiscipline() {
    val player = mc.player ?: return
    val level = mc.level ?: return

    var bestZombie: Zombie? = null
    var minDistance = 7.0
    for (entity in level.entitiesForRendering()) {
      if (entity !is Zombie) continue

      val dx = entity.x - player.x
      val dy = entity.y + 1.2 - (player.y + player.eyeHeight)
      val dz = entity.z - player.z
      val horizontal = sqrt(dx * dx + dz * dz)
      val distance = sqrt(dx * dx + dy * dy + dz * dz)
      if (distance > 6.0) continue

      val targetYaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
      val targetPitch = Math.toDegrees(atan2(-dy, horizontal)).toFloat()
      val yawDiff = abs(AngleUtils.normalizeAngle(targetYaw - player.yRot))
      val pitchDiff = abs(targetPitch - player.xRot)
      if (yawDiff >= 20f || pitchDiff >= 35f || distance >= minDistance) continue

      minDistance = distance
      bestZombie = entity
    }

    val zombie = bestZombie ?: return
    val targetSword = when (zombie.getItemBySlot(EquipmentSlot.HEAD).item) {
      Items.LEATHER_HELMET -> Items.WOODEN_SWORD
      Items.IRON_HELMET -> Items.IRON_SWORD
      Items.GOLDEN_HELMET -> Items.GOLDEN_SWORD
      Items.DIAMOND_HELMET -> Items.DIAMOND_SWORD
      else -> return
    }

    val swordSlot = findItemSlot(targetSword) ?: return
    if (player.inventory.selectedSlot != swordSlot) player.inventory.selectedSlot = swordSlot
    if (disciplineAutoAttack.value) {
      (mc as? MinecraftAccessor)?.leftClick()
    }
  }

  private fun scanForMasteryBlocks() {
    val player = mc.player ?: return
    val level = mc.level ?: return
    val now = System.currentTimeMillis()
    val playerPos = player.blockPosition()

    for (x in -25..25) {
      for (y in -10..10) {
        for (z in -25..25) {
          val distance = sqrt((x * x + z * z).toDouble())
          if (distance > 25.0) continue

          val pos = playerPos.offset(x, y, z)
          if (level.getBlockState(pos).block != Blocks.YELLOW_WOOL) continue
          if (masteryBlocks.any { it.x == pos.x && it.z == pos.z }) continue
          masteryBlocks.add(MasteryBlock(pos.x, pos.y, pos.z, now + 3500L))
        }
      }
    }
  }

  private fun setRotation(x: Double, y: Double, z: Double) {
    val rot = rotationFor(x, y, z)
    rotation.smartSmoothLook(rot.yaw, rot.pitch, 350)
  }

  private fun rotationFor(x: Double, y: Double, z: Double): TargetRotation {
    val player = mc.player ?: return TargetRotation(0f, 0f)
    val dx = x - player.x
    val dy = y - (player.y + player.eyeHeight)
    val dz = z - player.z
    val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
    val pitch = Math.toDegrees(atan2(-dy, sqrt(dx * dx + dz * dz))).toFloat()
    return TargetRotation(yaw, pitch.coerceIn(-90f, 90f))
  }

  private fun findItemSlot(item: Item): Int? {
    val player = mc.player ?: return null
    for (i in 0..8) {
      if (InventoryUtils.itemAt(i).item == item || player.inventory.getItem(i).item == item) return i
    }
    return null
  }

  private fun selectBow() {
    val bowSlot = findItemSlot(Items.BOW) ?: return
    mc.player?.inventory?.selectedSlot = bowSlot
  }

  private fun drawTargetBox(event: WorldRenderEvent.Last, box: AABB) {
    val stroke = Color(85, 255, 255, 220)
    val fill = Color(85, 255, 255, 70)
    when (renderStyle.value) {
      0 -> Render3D.drawStyledBox(event.context, box, Color(stroke.red, stroke.green, stroke.blue, 0), fill, esp = true, lineWidth = 0.5f)
      1 -> Render3D.drawStyledBox(event.context, box, stroke, null, esp = true, lineWidth = 2.5f)
      else -> Render3D.drawStyledBox(event.context, box, stroke, fill, esp = true, lineWidth = 2.5f)
    }
  }

  private fun reset() {
    dojoType = DojoType.NONE
    targetSkeleton = null
    lookCooldown = 0L
    lastSkeletonPos = null
    skeletonVelocity = Vec3.ZERO
    masteryBlocks.clear()
    firingState = 0
    firingTimer = 0
    if (isDrawing) mc.options.keyUse.setDown(false)
    isDrawing = false
    rotation.reset()
  }

  private enum class DojoType {
    NONE,
    CONTROL,
    FORCE,
    MASTERY,
    DISCIPLINE,
  }

  private data class MasteryBlock(
    val x: Int,
    val y: Int,
    val z: Int,
    val expiryTime: Long,
  )

  private data class TargetRotation(
    val yaw: Float,
    val pitch: Float,
  )

  private class SmoothRotation {
    private var startYaw = 0f
    private var startPitch = 0f
    private var endYaw = 0f
    private var endPitch = 0f
    private var startTime = 0L
    private var endTime = 0L
    private var done = true

    fun reset() {
      done = true
      startTime = 0L
      endTime = 0L
    }

    fun smartSmoothLook(targetYaw: Float, targetPitch: Float, msPer180: Int) {
      val player = Minecraft.getInstance().player ?: return
      if (!done && abs(AngleUtils.normalizeAngle(targetYaw - endYaw)) < 0.1f && abs(targetPitch - endPitch) < 0.1f) {
        return
      }

      val yawDiff = abs(AngleUtils.normalizeAngle(targetYaw - player.yRot))
      val pitchDiff = abs(targetPitch - player.xRot)
      val maxDiff = max(yawDiff, pitchDiff)
      smoothLook(targetYaw, targetPitch, (maxDiff / 180f * msPer180).toLong().coerceAtLeast(10L))
    }

    fun update() {
      if (done) return
      val player = Minecraft.getInstance().player ?: return
      val now = System.currentTimeMillis()
      if (now >= endTime) {
        player.setYRot(endYaw)
        player.setXRot(endPitch)
        done = true
        return
      }

      val progress = (now - startTime).toDouble() / (endTime - startTime).toDouble()
      val eased = (1.0 - (1.0 - progress).pow(3.0)).toFloat().coerceIn(0f, 1f)
      player.setYRot(startYaw + (endYaw - startYaw) * eased)
      player.setXRot(startPitch + (endPitch - startPitch) * eased)
    }

    private fun smoothLook(targetYaw: Float, targetPitch: Float, timeMs: Long) {
      val player = Minecraft.getInstance().player ?: return
      startYaw = player.yRot
      startPitch = player.xRot
      endYaw = startYaw + AngleUtils.normalizeAngle(targetYaw - startYaw)
      endPitch = targetPitch
      startTime = System.currentTimeMillis()
      endTime = startTime + timeMs
      done = false
    }
  }
}
