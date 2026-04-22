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

    /** Tracks position history and predicts the landing position for one beach ball entity. */
    private class BallPredictor {
        val data = mutableListOf<Vec3>()    // all positions recorded since spawn
        var startIndex = 0                  // slice start within data (reset each bounce)
        var minY = Double.MAX_VALUE         // lowest Y seen (ground level)
        var bounceCounter = 0
        var lastBounceMs = 0L
        var positiveYDelta = true           // whether last Y delta was upward (true = start neutral so first rise doesn't count as bounce)
        var lastPos = Vec3(0.0, 0.0, 0.0)
        var predTick = 0                    // counter mod 3 for throttled prediction updates
        var landingPos: Vec3? = null        // current best landing prediction

        fun update(pos: Vec3) {
            data.add(pos)
            if (pos.y < minY) minY = pos.y

            // Skip bounce logic on first tick — lastPos is uninitialized
            if (data.size == 1) { lastPos = pos; return }

            // Bounce detection — skip if movement too small or debounce active
            val dist = pos.distanceTo(lastPos)
            if (dist >= 0.3) {
                val dy = pos.y - lastPos.y
                val nowMs = System.currentTimeMillis()
                if (dy > 0.0 && !positiveYDelta && (nowMs - lastBounceMs) > 800L) {
                    // Ball just started going up — it bounced
                    bounceCounter++
                    lastBounceMs = nowMs
                    startIndex = data.lastIndex   // reset prediction slice to this bounce
                }
                positiveYDelta = dy > 0.0
                lastPos = pos

                // Throttle prediction to every 3 meaningful position updates
                predTick++
                if (predTick >= 3) {
                    predTick = 0
                    landingPos = runPrediction()
                }
            }
        }

        private fun runPrediction(): Vec3? {
            // implemented in Task 4
            return null
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
            if (entity is Slime && entity.isInvisible && entity.maxHealth == 1024f && entity.size == 4) {
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
