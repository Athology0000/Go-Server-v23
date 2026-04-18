/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
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
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.rotation.EasingType;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.rotation.strategy.TimedEaseStrategy;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.mixin.client.MinecraftAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003R\"\u0010\b\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\b\u0010\t\u001a\u0004\b\b\u0010\n\"\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/internal/garden/managers/PestAotvManager;", "", "<init>", "()V", "", "reset", "teleportToRoof", "", "isActive", "Z", "()Z", "setActive", "(Z)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPestAotvManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PestAotvManager.kt\norg/cobalt/internal/garden/managers/PestAotvManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,44:1\n296#2,2:45\n*S KotlinDebug\n*F\n+ 1 PestAotvManager.kt\norg/cobalt/internal/garden/managers/PestAotvManager\n*L\n31#1:45,2\n*E\n"})
public final class PestAotvManager {
    @NotNull
    public static final PestAotvManager INSTANCE = new PestAotvManager();
    private static volatile boolean isActive;

    private PestAotvManager() {
    }

    public final boolean isActive() {
        return isActive;
    }

    public final void setActive(boolean bl) {
        isActive = bl;
    }

    public final void reset() {
        isActive = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void teleportToRoof() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        isActive = true;
        try {
            float pitch = (float)GardenConfig.INSTANCE.getRoofPitch();
            mc.execute(() -> PestAotvManager.teleportToRoof$lambda$0(mc, pitch));
            Thread.sleep(500L);
            mc.execute(() -> PestAotvManager.teleportToRoof$lambda$1(mc));
            Thread.sleep(800L);
        }
        finally {
            isActive = false;
        }
    }

    private static final void teleportToRoof$lambda$0(class_310 $mc, float $pitch) {
        class_746 class_7462 = $mc.field_1724;
        RotationExecutor.INSTANCE.rotateTo(new Rotation(class_7462 != null ? class_7462.method_36454() : 0.0f, $pitch), new TimedEaseStrategy(EasingType.LINEAR, EasingType.LINEAR, 400L));
    }

    private static final void teleportToRoof$lambda$1(class_310 $mc) {
        block4: {
            Object v2;
            class_746 player;
            block3: {
                class_746 class_7462 = $mc.field_1724;
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
                    if (!StringsKt.contains((CharSequence)string, (CharSequence)"Aspect of the Void", (boolean)true)) continue;
                    v2 = element$iv;
                    break block3;
                }
                v2 = null;
            }
            Integer aotv = v2;
            if (aotv != null) {
                player.method_31548().method_61496(aotv.intValue());
            }
            MinecraftAccessor minecraftAccessor = $mc instanceof MinecraftAccessor ? (MinecraftAccessor)$mc : null;
            if (minecraftAccessor == null) break block4;
            minecraftAccessor.rightClick();
        }
    }
}

