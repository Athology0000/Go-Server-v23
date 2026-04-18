/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tR\"\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u000b\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/internal/garden/managers/EquipmentManager;", "", "<init>", "()V", "", "reset", "", "equipmentKeywords", "swapTo", "(Ljava/lang/String;)V", "", "isSwapping", "Z", "()Z", "setSwapping", "(Z)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nEquipmentManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EquipmentManager.kt\norg/cobalt/internal/garden/managers/EquipmentManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,82:1\n1586#2:83\n1661#2,3:84\n777#2:87\n873#2,2:88\n1924#2,2:90\n1807#2,3:92\n1926#2:95\n*S KotlinDebug\n*F\n+ 1 EquipmentManager.kt\norg/cobalt/internal/garden/managers/EquipmentManager\n*L\n19#1:83\n19#1:84,3\n20#1:87\n20#1:88,2\n42#1:90,2\n45#1:92,3\n42#1:95\n*E\n"})
public final class EquipmentManager {
    @NotNull
    public static final EquipmentManager INSTANCE = new EquipmentManager();
    private static volatile boolean isSwapping;

    private EquipmentManager() {
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
     * WARNING - void declaration
     */
    public final void swapTo(@NotNull String equipmentKeywords) {
        void $this$filterTo$iv$iv;
        String it;
        Iterable $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)equipmentKeywords, (String)"equipmentKeywords");
        String[] stringArray = new String[]{","};
        Iterable $this$map$iv = StringsKt.split$default((CharSequence)equipmentKeywords, (String[])stringArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(((Object)StringsKt.trim((CharSequence)it)).toString());
        }
        Iterable $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (String)element$iv$iv;
            boolean bl = false;
            boolean bl2 = !StringsKt.isBlank((CharSequence)it);
            if (!bl2) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List keywords = (List)destination$iv$iv;
        if (keywords.isEmpty()) {
            return;
        }
        isSwapping = true;
        try {
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            class_310 mc = class_3102;
            mc.execute(() -> EquipmentManager.swapTo$lambda$2(mc));
            Thread.sleep(1200L);
            List matchingIndices = new ArrayList();
            CountDownLatch latch = new CountDownLatch(1);
            mc.execute(() -> EquipmentManager.swapTo$lambda$3(mc, latch, keywords, matchingIndices));
            latch.await(2000L, TimeUnit.MILLISECONDS);
            if (matchingIndices.isEmpty()) {
                mc.execute(() -> EquipmentManager.swapTo$lambda$4(mc));
                Thread.sleep(300L);
                return;
            }
            Iterator iterator = matchingIndices.iterator();
            while (iterator.hasNext()) {
                int slotIndex = ((Number)iterator.next()).intValue();
                mc.execute(() -> EquipmentManager.swapTo$lambda$5(mc, slotIndex));
                Thread.sleep(RangesKt.coerceAtLeast((long)GardenConfig.INSTANCE.getSwapDelayMs(), (long)300L));
            }
            mc.execute(() -> EquipmentManager.swapTo$lambda$6(mc));
            Thread.sleep(300L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isSwapping = false;
        }
    }

    private static final void swapTo$lambda$2(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45730("eq");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private static final void swapTo$lambda$3(class_310 $mc, CountDownLatch $latch, List $keywords, List $matchingIndices) {
        try {
            class_465 class_4652;
            class_437 class_4372 = $mc.field_1755;
            if ((class_4372 instanceof class_465 ? (class_465)class_4372 : null) == null) {
                return;
            }
            class_465 screen = class_4652;
            class_2371 class_23712 = screen.method_17577().field_7761;
            Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
            class_2371 slots = class_23712;
            int inventoryStart = RangesKt.coerceAtLeast((int)(slots.size() - 36), (int)0);
            Iterable $this$forEachIndexed$iv = (Iterable)slots;
            boolean $i$f$forEachIndexed = false;
            int index$iv = 0;
            for (Object item$iv : $this$forEachIndexed$iv) {
                boolean bl;
                void s;
                block10: {
                    int n;
                    if ((n = index$iv++) < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    class_1735 class_17352 = (class_1735)item$iv;
                    int i = n;
                    boolean bl2 = false;
                    if (i < inventoryStart) continue;
                    Intrinsics.checkNotNullExpressionValue((Object)s.method_7677().method_7964().getString(), (String)"getString(...)");
                    Iterable $this$any$iv = $keywords;
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv : $this$any$iv) {
                            String itemName;
                            String kw = (String)element$iv;
                            boolean bl3 = false;
                            if (!StringsKt.contains((CharSequence)itemName, (CharSequence)kw, (boolean)true)) continue;
                            bl = true;
                            break block10;
                        }
                        bl = false;
                    }
                }
                if (!bl) continue;
                $matchingIndices.add(s.field_7874);
            }
        }
        finally {
            $latch.countDown();
        }
    }

    private static final void swapTo$lambda$4(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) break block0;
            class_7462.method_7346();
        }
    }

    private static final void swapTo$lambda$5(class_310 $mc, int $slotIndex) {
        block1: {
            class_437 class_4372 = $mc.field_1755;
            class_465 class_4652 = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
            if (class_4652 == null) {
                return;
            }
            class_465 screen = class_4652;
            class_636 class_6362 = $mc.field_1761;
            if (class_6362 == null) break block1;
            int n = screen.method_17577().field_7763;
            class_746 class_7462 = $mc.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            class_6362.method_2906(n, $slotIndex, 0, class_1713.field_7790, (class_1657)class_7462);
        }
    }

    private static final void swapTo$lambda$6(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) break block0;
            class_7462.method_7346();
        }
    }
}

