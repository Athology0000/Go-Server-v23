package org.cobalt.internal.garden.managers

import org.cobalt.internal.garden.GardenConfig

object GearManager {

    @Volatile var isSwapping = false

    fun reset() { isSwapping = false }

    fun swapForPest() {
        isSwapping = true
        try {
            if (GardenConfig.autoWardrobeEnabled)
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.PEST)
            if (GardenConfig.pestArmor.isNotBlank())
                EquipmentManager.swapTo(GardenConfig.pestArmor)
        } finally { isSwapping = false }
    }

    fun swapForFarming() {
        isSwapping = true
        try {
            if (GardenConfig.autoWardrobeEnabled)
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.FARMING)
            if (GardenConfig.farmingArmor.isNotBlank())
                EquipmentManager.swapTo(GardenConfig.farmingArmor)
        } finally { isSwapping = false }
    }

    fun swapForVisitor() {
        isSwapping = true
        try {
            if (GardenConfig.autoWardrobeEnabled)
                WardrobeManager.swapTo(WardrobeManager.LoadoutType.VISITOR)
            if (GardenConfig.visitorArmor.isNotBlank())
                EquipmentManager.swapTo(GardenConfig.visitorArmor)
        } finally { isSwapping = false }
    }
}
