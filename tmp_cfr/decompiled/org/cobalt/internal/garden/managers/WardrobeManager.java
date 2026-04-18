/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_2371
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_2371;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0010B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tR\"\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u000b\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0011"}, d2={"Lorg/cobalt/internal/garden/managers/WardrobeManager;", "", "<init>", "()V", "", "reset", "Lorg/cobalt/internal/garden/managers/WardrobeManager$LoadoutType;", "type", "swapTo", "(Lorg/cobalt/internal/garden/managers/WardrobeManager$LoadoutType;)V", "", "isSwapping", "Z", "()Z", "setSwapping", "(Z)V", "LoadoutType", "cobalt"})
public final class WardrobeManager {
    @NotNull
    public static final WardrobeManager INSTANCE = new WardrobeManager();
    private static volatile boolean isSwapping;

    private WardrobeManager() {
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
    public final void swapTo(@NotNull LoadoutType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        int targetSlot = switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
            case 1 -> GardenConfig.INSTANCE.getFarmingWardrobeSlot();
            case 2 -> GardenConfig.INSTANCE.getPestWardrobeSlot();
            case 3 -> GardenConfig.INSTANCE.getVisitorWardrobeSlot();
            default -> throw new NoWhenBranchMatchedException();
        };
        isSwapping = true;
        try {
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            class_310 mc = class_3102;
            mc.execute(() -> WardrobeManager.swapTo$lambda$0(mc));
            Thread.sleep(1200L);
            mc.execute(() -> WardrobeManager.swapTo$lambda$1(mc, targetSlot));
            Thread.sleep(800L);
            mc.execute(() -> WardrobeManager.swapTo$lambda$2(mc));
            Thread.sleep(400L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isSwapping = false;
        }
    }

    private static final void swapTo$lambda$0(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45730("wardrobe");
        }
    }

    private static final void swapTo$lambda$1(class_310 $mc, int $targetSlot) {
        block2: {
            class_437 class_4372 = $mc.field_1755;
            class_465 class_4652 = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
            if (class_4652 == null) {
                return;
            }
            class_465 screen = class_4652;
            int slotIndex = RangesKt.coerceIn((int)($targetSlot - 1), (int)0, (int)17);
            class_2371 class_23712 = screen.method_17577().field_7761;
            Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
            class_1735 class_17352 = (class_1735)CollectionsKt.getOrNull((List)((List)class_23712), (int)(slotIndex + 9));
            if (class_17352 == null) {
                return;
            }
            class_1735 slot = class_17352;
            class_636 class_6362 = $mc.field_1761;
            if (class_6362 == null) break block2;
            int n = screen.method_17577().field_7763;
            int n2 = slot.field_7874;
            class_746 class_7462 = $mc.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            class_6362.method_2906(n, n2, 0, class_1713.field_7790, (class_1657)class_7462);
        }
    }

    private static final void swapTo$lambda$2(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) break block0;
            class_7462.method_7346();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/garden/managers/WardrobeManager$LoadoutType;", "", "<init>", "(Ljava/lang/String;I)V", "FARMING", "PEST", "VISITOR", "cobalt"})
    public static final class LoadoutType
    extends Enum<LoadoutType> {
        public static final /* enum */ LoadoutType FARMING = new LoadoutType();
        public static final /* enum */ LoadoutType PEST = new LoadoutType();
        public static final /* enum */ LoadoutType VISITOR = new LoadoutType();
        private static final /* synthetic */ LoadoutType[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static LoadoutType[] values() {
            return (LoadoutType[])$VALUES.clone();
        }

        public static LoadoutType valueOf(String value) {
            return Enum.valueOf(LoadoutType.class, value);
        }

        @NotNull
        public static EnumEntries<LoadoutType> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = loadoutTypeArray = new LoadoutType[]{LoadoutType.FARMING, LoadoutType.PEST, LoadoutType.VISITOR};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[LoadoutType.values().length];
            try {
                nArray[LoadoutType.FARMING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LoadoutType.PEST.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LoadoutType.VISITOR.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

