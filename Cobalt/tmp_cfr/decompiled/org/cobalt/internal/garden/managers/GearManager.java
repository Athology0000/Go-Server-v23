/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.text.StringsKt;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.managers.EquipmentManager;
import org.cobalt.internal.garden.managers.WardrobeManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\u0003J\r\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\u0003R\"\u0010\n\u001a\u00020\t8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\n\u0010\u000b\u001a\u0004\b\n\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/garden/managers/GearManager;", "", "<init>", "()V", "", "reset", "swapForPest", "swapForFarming", "swapForVisitor", "", "isSwapping", "Z", "()Z", "setSwapping", "(Z)V", "cobalt"})
public final class GearManager {
    @NotNull
    public static final GearManager INSTANCE = new GearManager();
    private static volatile boolean isSwapping;

    private GearManager() {
    }

    public final boolean isSwapping() {
        return isSwapping;
    }

    public final void setSwapping(boolean bl) {
        isSwapping = bl;
    }

    public final void reset() {
        isSwapping = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void swapForPest() {
        isSwapping = true;
        try {
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() && GardenConfig.INSTANCE.getAutoWardrobePest()) {
                WardrobeManager.INSTANCE.swapTo(WardrobeManager.LoadoutType.PEST);
            }
            if (GardenConfig.INSTANCE.getAutoEquipment() && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getPestEquipment())) {
                EquipmentManager.INSTANCE.swapTo(GardenConfig.INSTANCE.getPestEquipment());
            }
        }
        finally {
            isSwapping = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void swapForFarming() {
        isSwapping = true;
        try {
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled()) {
                WardrobeManager.INSTANCE.swapTo(WardrobeManager.LoadoutType.FARMING);
            }
            if (GardenConfig.INSTANCE.getAutoEquipment() && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getFarmingEquipment())) {
                EquipmentManager.INSTANCE.swapTo(GardenConfig.INSTANCE.getFarmingEquipment());
            }
        }
        finally {
            isSwapping = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void swapForVisitor() {
        isSwapping = true;
        try {
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() && GardenConfig.INSTANCE.getAutoWardrobeVisitor()) {
                WardrobeManager.INSTANCE.swapTo(WardrobeManager.LoadoutType.VISITOR);
            }
            if (GardenConfig.INSTANCE.getAutoEquipment() && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getVisitorEquipment())) {
                EquipmentManager.INSTANCE.swapTo(GardenConfig.INSTANCE.getVisitorEquipment());
            }
        }
        finally {
            isSwapping = false;
        }
    }
}

