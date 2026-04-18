/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  net.minecraft.class_310
 *  net.minecraft.class_412
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_639
 *  net.minecraft.class_642
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_639;
import net.minecraft.class_642;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u001b\u0010\b\u001a\u00020\u00042\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\u0004\b\b\u0010\tR\"\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\"\u0010\u0012\u001a\u00020\u00118\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0012\u0010\u0014\"\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/garden/managers/RecoveryManager;", "", "<init>", "()V", "", "reset", "Lkotlin/Function0;", "restartFarming", "onDisconnect", "(Lkotlin/jvm/functions/Function0;)V", "", "recoveryAttempts", "I", "getRecoveryAttempts", "()I", "setRecoveryAttempts", "(I)V", "", "isRecovering", "Z", "()Z", "setRecovering", "(Z)V", "cobalt"})
public final class RecoveryManager {
    @NotNull
    public static final RecoveryManager INSTANCE = new RecoveryManager();
    private static volatile int recoveryAttempts;
    private static volatile boolean isRecovering;

    private RecoveryManager() {
    }

    public final int getRecoveryAttempts() {
        return recoveryAttempts;
    }

    public final void setRecoveryAttempts(int n) {
        recoveryAttempts = n;
    }

    public final boolean isRecovering() {
        return isRecovering;
    }

    public final void setRecovering(boolean bl) {
        isRecovering = bl;
    }

    public final void reset() {
        recoveryAttempts = 0;
        isRecovering = false;
    }

    public final void onDisconnect(@NotNull Function0<Unit> restartFarming) {
        Intrinsics.checkNotNullParameter(restartFarming, (String)"restartFarming");
        if (isRecovering) {
            return;
        }
        if (recoveryAttempts >= GardenConfig.INSTANCE.getMaxRecoveryAttempts()) {
            return;
        }
        isRecovering = true;
        int n = recoveryAttempts;
        recoveryAttempts = n + 1;
        GardenWorkerThread.INSTANCE.submit("recovery", (Function0<Unit>)((Function0)() -> RecoveryManager.onDisconnect$lambda$0(restartFarming)));
    }

    private static final void onDisconnect$lambda$0$0(class_310 $mc, class_639 $addr, class_642 $serverData) {
        class_412.method_36877((class_437)((class_437)new class_442()), (class_310)$mc, (class_639)$addr, (class_642)$serverData, (boolean)false, null);
    }

    private static final void onDisconnect$lambda$0$1() {
        ScriptBridge.INSTANCE.warpGarden();
    }

    private static final void onDisconnect$lambda$0$2(Function0 $restartFarming) {
        $restartFarming.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit onDisconnect$lambda$0(Function0 $restartFarming) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            long delayMin = GardenConfig.INSTANCE.getReconnectDelayMin() * 1000L;
            long delayMax = GardenConfig.INSTANCE.getReconnectDelayMax() * 1000L;
            long delay = delayMin + (delayMax > delayMin ? Random.Default.nextLong(delayMax - delayMin) : 0L);
            Thread.sleep(delay);
            class_642 serverData = mc.method_1558();
            if (serverData != null) {
                class_639 class_6392 = class_639.method_2950((String)serverData.field_3761);
                Intrinsics.checkNotNullExpressionValue((Object)class_6392, (String)"parseString(...)");
                class_639 addr = class_6392;
                mc.execute(() -> RecoveryManager.onDisconnect$lambda$0$0(mc, addr, serverData));
            }
            Thread.sleep(10000L);
            mc.execute(RecoveryManager::onDisconnect$lambda$0$1);
            Thread.sleep(3000L);
            mc.execute(() -> RecoveryManager.onDisconnect$lambda$0$2($restartFarming));
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isRecovering = false;
        }
        return Unit.INSTANCE;
    }
}

