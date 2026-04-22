package org.cobalt.internal.seal

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.util.helper.KeyBind

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

    // Placeholder — full implementation added in Task 2
    private class BallPredictor

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
        // stub — detection and pathfinding added in later tasks
    }
}
