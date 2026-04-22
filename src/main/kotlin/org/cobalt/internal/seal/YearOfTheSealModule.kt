package org.cobalt.internal.seal

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.util.player.MovementManager

object YearOfTheSealModule : Module("Year of the Seal") {

    private val mc = Minecraft.getInstance()

    private val enabledSetting = CheckboxSetting(
        "Enabled",
        "Automatically pathfinds to the predicted beach ball landing position.",
        false
    )

    private val toggleKeybind = KeyBindSetting(
        "Toggle Key",
        "Keybind to enable/disable the Year of the Seal macro.",
        KeyBind(-1)
    )

    private val minBounces = SliderSetting(
        "Min Bounce Quality",
        "Minimum observed bounces before pathfinding starts. Higher = more data, more accurate prediction.",
        2.0, 1.0, 10.0, 1.0
    )

    private class BallPredictor {
        val data = mutableListOf<Vec3>()    // all positions recorded since spawn
        var startIndex = 0                  // slice start within data (reset each bounce)
        var minY = Double.MAX_VALUE         // lowest Y seen (ground level)
        var bounceCounter = 0
        var lastBounceMs = 0L
        var positiveYDelta = false          // whether last Y delta was upward
        var lastPos = Vec3(0.0, 0.0, 0.0)
        var predTick = 0                    // counter mod 3 for throttled prediction updates
        var landingPos: Vec3? = null        // current best landing prediction

        fun update(pos: Vec3) {
            // implemented in Task 3
            data.add(pos)
        }
    }

    // Ball trackers keyed by entity ID
    private val predictors = mutableMapOf<Int, BallPredictor>()
    // Last target issued to NativePathfinder (to avoid replanning on every tick)
    private var lastIssuedTarget: Vec3? = null

    init {
        addSetting(enabledSetting, toggleKeybind, minBounces)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (toggleKeybind.value.isPressed()) {
            enabledSetting.value = !enabledSetting.value
        }
        if (!enabledSetting.value) {
            if (predictors.isNotEmpty()) stopAll()
            return
        }
        val level = mc.level ?: return

        // Discover new beach ball slimes (invisible, 1024 max HP)
        level.entitiesForRendering().forEach { entity ->
            if (entity is Slime && entity.isInvisible && entity.maxHealth == 1024f) {
                predictors.putIfAbsent(entity.id, BallPredictor())
            }
        }

        // Update existing predictors, prune removed entities
        val iter = predictors.iterator()
        while (iter.hasNext()) {
            val (id, predictor) = iter.next()
            val entity = level.getEntity(id)
            if (entity == null || entity.isRemoved) {
                iter.remove()
                continue
            }
            predictor.update(Vec3(entity.x, entity.y, entity.z))
        }

        // Pathfinding — implemented in Task 5
    }

    private fun stopAll() {
        NativePathfinder.stop()
        MovementManager.clearForcedMovement()
        predictors.clear()
        lastIssuedTarget = null
    }
}
