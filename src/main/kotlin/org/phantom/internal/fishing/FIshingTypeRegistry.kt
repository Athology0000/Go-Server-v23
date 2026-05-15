package org.phantom.internal.fishing

import org.phantom.internal.fishing.types.BarnFishingProfile
import org.phantom.internal.fishing.types.NormalFishingProfile
import org.phantom.internal.fishing.types.StriderSurferFishingProfile
import org.phantom.internal.fishing.types.TrophyFishingProfile
import org.phantom.internal.fishing.types.WormFishingProfile

object FishingTypeRegistry {
  private val profiles: Map<FishingMacroModule.FishingType, FishingTypeProfile> =
    listOf(
      NormalFishingProfile,
      BarnFishingProfile,
      TrophyFishingProfile,
      WormFishingProfile,
      StriderSurferFishingProfile,
    ).associateBy { it.type }

  fun get(type: FishingMacroModule.FishingType): FishingTypeProfile {
    return profiles[type] ?: NormalFishingProfile
  }
}
