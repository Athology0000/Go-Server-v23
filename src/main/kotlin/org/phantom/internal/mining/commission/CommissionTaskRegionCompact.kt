package org.phantom.internal.mining.commission

import org.phantom.internal.mining.CommissionRegion
import org.phantom.internal.mining.CommissionTask

val CommissionTask.region: CommissionRegion
  get() = when {
    names.any { name ->
      val value = name.lowercase()
      value.contains("glacite tunnel") ||
        value.contains("tungsten") ||
        value.contains("umber") ||
        (value.contains("glacite") && !value.contains("walker"))
    } -> CommissionRegion.GLACITE

    else -> CommissionRegion.DWARVEN
  }