/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_634;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0011\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\f\u001a\u00020\u00042\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\n\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0010J\u000f\u0010\u0012\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0010R\"\u0010\u0013\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0017R\"\u0010\u0018\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\u0014\u001a\u0004\b\u0019\u0010\u0010\"\u0004\b\u001a\u0010\u0017R\"\u0010\u001b\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u001b\u0010\t\"\u0004\b\u001d\u0010\u001e\u00a8\u0006\u001f"}, d2={"Lorg/cobalt/internal/garden/managers/DynamicRestManager;", "", "<init>", "()V", "", "reset", "fullReset", "", "shouldRest", "()Z", "Lkotlin/Function0;", "onReconnect", "startRest", "(Lkotlin/jvm/functions/Function0;)V", "", "timeUntilRestMs", "()J", "calculateWork", "calculateBreak", "farmingStartTime", "J", "getFarmingStartTime", "setFarmingStartTime", "(J)V", "targetWorkDurationMs", "getTargetWorkDurationMs", "setTargetWorkDurationMs", "isResting", "Z", "setResting", "(Z)V", "cobalt"})
public final class DynamicRestManager {
    @NotNull
    public static final DynamicRestManager INSTANCE = new DynamicRestManager();
    private static volatile long farmingStartTime;
    private static volatile long targetWorkDurationMs;
    private static volatile boolean isResting;

    private DynamicRestManager() {
    }

    public final long getFarmingStartTime() {
        return farmingStartTime;
    }

    public final void setFarmingStartTime(long l) {
        farmingStartTime = l;
    }

    public final long getTargetWorkDurationMs() {
        return targetWorkDurationMs;
    }

    public final void setTargetWorkDurationMs(long l) {
        targetWorkDurationMs = l;
    }

    public final boolean isResting() {
        return isResting;
    }

    public final void setResting(boolean bl) {
        isResting = bl;
    }

    public final void reset() {
        farmingStartTime = System.currentTimeMillis();
        targetWorkDurationMs = this.calculateWork();
        isResting = false;
    }

    public final void fullReset() {
        this.reset();
    }

    public final boolean shouldRest() {
        if (isResting || targetWorkDurationMs <= 0L) {
            return false;
        }
        return System.currentTimeMillis() - farmingStartTime >= targetWorkDurationMs;
    }

    public final void startRest(@NotNull Function0<Unit> onReconnect) {
        Intrinsics.checkNotNullParameter(onReconnect, (String)"onReconnect");
        if (isResting) {
            return;
        }
        isResting = true;
        GardenWorkerThread.INSTANCE.submit("rest-start", (Function0<Unit>)((Function0)() -> DynamicRestManager.startRest$lambda$0(onReconnect)));
    }

    public final long timeUntilRestMs() {
        if (targetWorkDurationMs <= 0L) {
            return Long.MAX_VALUE;
        }
        return RangesKt.coerceAtLeast((long)(targetWorkDurationMs - (System.currentTimeMillis() - farmingStartTime)), (long)0L);
    }

    private final long calculateWork() {
        long base = GardenConfig.INSTANCE.getWorkDurationMins() * 60000L;
        long offset = GardenConfig.INSTANCE.getWorkOffsetMins() > 0L ? Random.Default.nextLong(GardenConfig.INSTANCE.getWorkOffsetMins() * 60000L) : 0L;
        return base + offset;
    }

    private final long calculateBreak() {
        long base = GardenConfig.INSTANCE.getBreakDurationMins() * 60000L;
        long offset = GardenConfig.INSTANCE.getBreakOffsetMins() > 0L ? Random.Default.nextLong(GardenConfig.INSTANCE.getBreakOffsetMins() * 60000L) : 0L;
        return base + offset;
    }

    private static final void startRest$lambda$0$0() {
        ScriptBridge.INSTANCE.stopScript();
    }

    private static final void startRest$lambda$0$1() {
        ScriptBridge.INSTANCE.setSpawn();
    }

    private static final void startRest$lambda$0$2(class_310 $mc) {
        block0: {
            class_634 class_6342 = $mc.method_1562();
            if (class_6342 == null || (class_6342 = class_6342.method_48296()) == null) break block0;
            class_6342.method_10747((class_2561)class_2561.method_43470((String)"Rest break"));
        }
    }

    private static final void startRest$lambda$0$3(Function0 $onReconnect) {
        $onReconnect.invoke();
    }

    private static final Unit startRest$lambda$0(Function0 $onReconnect) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            mc.execute(DynamicRestManager::startRest$lambda$0$0);
            Thread.sleep(500L);
            mc.execute(DynamicRestManager::startRest$lambda$0$1);
            Thread.sleep(500L);
            long breakMs = INSTANCE.calculateBreak();
            mc.execute(() -> DynamicRestManager.startRest$lambda$0$2(mc));
            Thread.sleep(breakMs);
            isResting = false;
            farmingStartTime = System.currentTimeMillis();
            targetWorkDurationMs = INSTANCE.calculateWork();
            mc.execute(() -> DynamicRestManager.startRest$lambda$0$3($onReconnect));
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            isResting = false;
        }
        return Unit.INSTANCE;
    }
}

