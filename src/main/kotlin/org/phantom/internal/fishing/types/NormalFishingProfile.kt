package org.phantom.internal.fishing.types

import org.phantom.internal.fishing.FishingMacroModule
import org.phantom.internal.fishing.FishingTypeProfile

object NormalFishingProfile : FishingTypeProfile {
  override val type = FishingMacroModule.FishingType.NORMAL
  override val displayName = "Normal"
}
