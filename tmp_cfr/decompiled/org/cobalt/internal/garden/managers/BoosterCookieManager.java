/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.mixin.client.MinecraftAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\u0003J\u000f\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000b\u0010\fR\"\u0010\u000e\u001a\u00020\r8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0014\u001a\u00020\r8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u000f\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/garden/managers/BoosterCookieManager;", "", "<init>", "()V", "", "reset", "", "shouldUseCookie", "()Z", "useCookie", "", "findCookieSlot", "()I", "", "lastUseTime", "J", "getLastUseTime", "()J", "setLastUseTime", "(J)V", "USE_COOLDOWN_MS", "cobalt"})
@SourceDebugExtension(value={"SMAP\nBoosterCookieManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BoosterCookieManager.kt\norg/cobalt/internal/garden/managers/BoosterCookieManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,49:1\n296#2,2:50\n*S KotlinDebug\n*F\n+ 1 BoosterCookieManager.kt\norg/cobalt/internal/garden/managers/BoosterCookieManager\n*L\n44#1:50,2\n*E\n"})
public final class BoosterCookieManager {
    @NotNull
    public static final BoosterCookieManager INSTANCE = new BoosterCookieManager();
    private static volatile long lastUseTime;
    private static final long USE_COOLDOWN_MS = 3600000L;

    private BoosterCookieManager() {
    }

    public final long getLastUseTime() {
        return lastUseTime;
    }

    public final void setLastUseTime(long l) {
        lastUseTime = l;
    }

    public final void reset() {
        lastUseTime = 0L;
    }

    public final boolean shouldUseCookie() {
        if (StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getCookieItem())) {
            return false;
        }
        if (System.currentTimeMillis() - lastUseTime < 3600000L) {
            return false;
        }
        return this.findCookieSlot() >= 0;
    }

    public final void useCookie() {
        int slot = this.findCookieSlot();
        if (slot < 0) {
            return;
        }
        GardenWorkerThread.INSTANCE.submit("booster-cookie", (Function0<Unit>)((Function0)() -> BoosterCookieManager.useCookie$lambda$0(slot)));
    }

    private final int findCookieSlot() {
        Object v5;
        block2: {
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            class_310 mc = class_3102;
            class_746 class_7462 = mc.field_1724;
            if (class_7462 == null) {
                return -1;
            }
            class_746 player = class_7462;
            String string = ((Object)StringsKt.trim((CharSequence)GardenConfig.INSTANCE.getCookieItem())).toString().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            String target = string;
            Iterable $this$firstOrNull$iv = (Iterable)new IntRange(0, 8);
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                int slot = ((Number)element$iv).intValue();
                boolean bl = false;
                String string2 = player.method_31548().method_5438(slot).method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getString(...)");
                String string3 = string2.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                if (!StringsKt.contains$default((CharSequence)string3, (CharSequence)target, (boolean)false, (int)2, null)) continue;
                v5 = element$iv;
                break block2;
            }
            v5 = null;
        }
        Integer n = v5;
        return n != null ? n : -1;
    }

    private static final void useCookie$lambda$0$0(class_310 $mc, int $slot) {
        block1: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            player.method_31548().method_61496($slot);
            MinecraftAccessor minecraftAccessor = $mc instanceof MinecraftAccessor ? (MinecraftAccessor)$mc : null;
            if (minecraftAccessor == null) break block1;
            minecraftAccessor.rightClick();
        }
    }

    private static final Unit useCookie$lambda$0(int $slot) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            mc.execute(() -> BoosterCookieManager.useCookie$lambda$0$0(mc, $slot));
            Thread.sleep(500L);
            lastUseTime = System.currentTimeMillis();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        return Unit.INSTANCE;
    }
}

