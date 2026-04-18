/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.mixin.client.MinecraftAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u0017\u0010\t\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nR\"\u0010\f\u001a\u00020\u000b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u000b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\r\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/garden/managers/RodManager;", "", "<init>", "()V", "", "reset", "update", "", "force", "useRod", "(Z)V", "", "lastCastTime", "J", "getLastCastTime", "()J", "setLastCastTime", "(J)V", "CAST_INTERVAL_MS", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRodManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RodManager.kt\norg/cobalt/internal/garden/managers/RodManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,36:1\n296#2,2:37\n*S KotlinDebug\n*F\n+ 1 RodManager.kt\norg/cobalt/internal/garden/managers/RodManager\n*L\n25#1:37,2\n*E\n"})
public final class RodManager {
    @NotNull
    public static final RodManager INSTANCE = new RodManager();
    private static volatile long lastCastTime;
    private static final long CAST_INTERVAL_MS = 30000L;

    private RodManager() {
    }

    public final long getLastCastTime() {
        return lastCastTime;
    }

    public final void setLastCastTime(long l) {
        lastCastTime = l;
    }

    public final void reset() {
        lastCastTime = 0L;
    }

    public final void update() {
        long now = System.currentTimeMillis();
        if (now - lastCastTime < 30000L) {
            return;
        }
        this.useRod(false);
    }

    public final void useRod(boolean force) {
        Object v3;
        class_746 player;
        class_310 mc;
        block5: {
            long now = System.currentTimeMillis();
            if (!force && now - lastCastTime < 30000L) {
                return;
            }
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            mc = class_3102;
            class_746 class_7462 = mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            player = class_7462;
            Iterable $this$firstOrNull$iv = (Iterable)new IntRange(0, 8);
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                int slot = ((Number)element$iv).intValue();
                boolean bl = false;
                String string = player.method_31548().method_5438(slot).method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                if (!StringsKt.contains((CharSequence)string, (CharSequence)"rod", (boolean)true)) continue;
                v3 = element$iv;
                break block5;
            }
            v3 = null;
        }
        Integer n = v3;
        if (n == null) {
            return;
        }
        int rodSlot = n;
        int prev = player.method_31548().method_67532();
        player.method_31548().method_61496(rodSlot);
        MinecraftAccessor minecraftAccessor = mc instanceof MinecraftAccessor ? (MinecraftAccessor)mc : null;
        if (minecraftAccessor != null) {
            minecraftAccessor.rightClick();
        }
        Thread.sleep(RangesKt.coerceAtLeast((long)GardenConfig.INSTANCE.getRodSwapDelayMs(), (long)100L));
        player.method_31548().method_61496(prev);
        lastCastTime = System.currentTimeMillis();
    }

    public static /* synthetic */ void useRod$default(RodManager rodManager, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        rodManager.useRod(bl);
    }
}

