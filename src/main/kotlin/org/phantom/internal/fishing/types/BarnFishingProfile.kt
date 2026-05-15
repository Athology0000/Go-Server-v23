package org.phantom.internal.fishing.types

import net.minecraft.world.entity.projectile.FishingHook
import org.phantom.internal.fishing.FishingMacroModule
import org.phantom.internal.fishing.FishingRuntime
import org.phantom.internal.fishing.FishingTypeProfile

object BarnFishingProfile : FishingTypeProfile {
  override val type = FishingMacroModule.FishingType.BARN
  override val displayName = "Barn"

  override fun canReel(
    hook: FishingHook,
    runtime: FishingRuntime,
  ): Boolean {
    return true
  }

  override fun hookTimeoutTicks(runtime: FishingRuntime): Long {
    // Barn mode should generally not force recast too aggressively.
    return runtime.hookTimeoutTicks.coerceAtLeast(400L)
  }

  override fun recastDelayTicks(runtime: FishingRuntime): Long {
    // Small delay to avoid rapid recast spam.
    return runtime.recastDelayTicks.coerceAtLeast(16L)
  }
}
