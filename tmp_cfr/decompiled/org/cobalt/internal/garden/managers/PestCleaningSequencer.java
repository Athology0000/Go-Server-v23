/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  kotlin.ranges.LongRange
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden.managers;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.cobalt.internal.garden.managers.FlightManager;
import org.cobalt.internal.garden.managers.GearManager;
import org.cobalt.internal.garden.managers.PestAotvManager;
import org.cobalt.internal.garden.managers.PestManager;
import org.cobalt.internal.garden.managers.PestPrepSwapManager;
import org.cobalt.internal.garden.managers.RodManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u001b\u0010\b\u001a\u00020\u00042\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u0015\u0010\f\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011R\"\u0010\u0012\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0012\u0010\u0014\"\u0004\b\u0015\u0010\u0011R\u0016\u0010\u0016\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0013R\u0016\u0010\u0017\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0013R\u001e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0018\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/internal/garden/managers/PestCleaningSequencer;", "", "<init>", "()V", "", "reset", "Lkotlin/Function0;", "onComplete", "startSequence", "(Lkotlin/jvm/functions/Function0;)V", "", "message", "onChatMessage", "(Ljava/lang/String;)V", "", "warpToGarden", "finishSequence", "(Z)V", "isRunning", "Z", "()Z", "setRunning", "awaitingFinishChat", "finishQueued", "Lkotlin/jvm/functions/Function0;", "cobalt"})
public final class PestCleaningSequencer {
    @NotNull
    public static final PestCleaningSequencer INSTANCE = new PestCleaningSequencer();
    private static volatile boolean isRunning;
    private static volatile boolean awaitingFinishChat;
    private static volatile boolean finishQueued;
    @Nullable
    private static volatile Function0<Unit> onComplete;

    private PestCleaningSequencer() {
    }

    public final boolean isRunning() {
        return isRunning;
    }

    public final void setRunning(boolean bl) {
        isRunning = bl;
    }

    public final void reset() {
        isRunning = false;
        awaitingFinishChat = false;
        finishQueued = false;
        onComplete = null;
    }

    public final void startSequence(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        if (isRunning) {
            return;
        }
        isRunning = true;
        awaitingFinishChat = false;
        finishQueued = false;
        PestCleaningSequencer.onComplete = onComplete;
        GardenWorkerThread.INSTANCE.submit("pest-clean", (Function0<Unit>)((Function0)PestCleaningSequencer::startSequence$lambda$0));
    }

    public final void onChatMessage(@NotNull String message) {
        boolean isPestCleanerDone;
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (finishQueued) {
            return;
        }
        CharSequence charSequence = message;
        Regex regex = new Regex("\u00a7[0-9a-fk-or]");
        String string = "";
        String string2 = regex.replace(charSequence, string).toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        String lower = string2;
        boolean bl = isPestCleanerDone = StringsKt.contains$default((CharSequence)lower, (CharSequence)"pest cleaner", (boolean)false, (int)2, null) && (StringsKt.contains$default((CharSequence)lower, (CharSequence)"script stopped", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"finished", (boolean)false, (int)2, null));
        if (!isPestCleanerDone) {
            return;
        }
        finishQueued = true;
        awaitingFinishChat = false;
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        mc.execute(PestCleaningSequencer::onChatMessage$lambda$0);
        GardenWorkerThread.INSTANCE.submit("pest-clean-finish", (Function0<Unit>)((Function0)PestCleaningSequencer::onChatMessage$lambda$1));
    }

    private final void finishSequence(boolean warpToGarden) {
        Function0<Unit> callback = onComplete;
        onComplete = null;
        awaitingFinishChat = false;
        finishQueued = false;
        if (warpToGarden) {
            PestManager.INSTANCE.startCooldown(10000L);
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        mc.execute(() -> PestCleaningSequencer.finishSequence$lambda$0(callback));
    }

    private static final void startSequence$lambda$0$0() {
        ScriptBridge.INSTANCE.stopScript();
    }

    private static final void startSequence$lambda$0$1() {
        ScriptBridge.INSTANCE.setSpawn();
    }

    private static final void startSequence$lambda$0$2() {
        ScriptBridge.INSTANCE.startPestScript(GardenConfig.INSTANCE.getPestScript());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startSequence$lambda$0() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        boolean waitingForChat = false;
        try {
            mc.execute(PestCleaningSequencer::startSequence$lambda$0$0);
            Thread.sleep(RangesKt.random((LongRange)new LongRange(20L, 40L), (Random)((Random)Random.Default)) + 300L);
            if (!PestPrepSwapManager.INSTANCE.getSwapDone()) {
                FlightManager.INSTANCE.startFlying();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 150L);
                mc.execute(PestCleaningSequencer::startSequence$lambda$0$1);
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 250L);
                FlightManager.INSTANCE.stopFlying();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 150L);
            }
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() || GardenConfig.INSTANCE.getAutoEquipment()) {
                GearManager.INSTANCE.swapForPest();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 1500L);
            }
            if (GardenConfig.INSTANCE.getAutoRodPestSpawn()) {
                RodManager.INSTANCE.useRod(true);
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 200L);
            }
            if (GardenConfig.INSTANCE.getAotvEnabled()) {
                PestAotvManager.INSTANCE.teleportToRoof();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 1200L);
            }
            Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 40L), (Random)((Random)Random.Default)));
            awaitingFinishChat = true;
            waitingForChat = true;
            mc.execute(PestCleaningSequencer::startSequence$lambda$0$2);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            if (!waitingForChat) {
                INSTANCE.finishSequence(false);
            }
        }
        return Unit.INSTANCE;
    }

    private static final void onChatMessage$lambda$0() {
        ScriptBridge.INSTANCE.warpGarden();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit onChatMessage$lambda$1() {
        try {
            Thread.sleep(RangesKt.coerceAtLeast((long)GardenConfig.INSTANCE.getGardenWarpDelayMs(), (long)3000L));
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() || GardenConfig.INSTANCE.getAutoEquipment()) {
                GearManager.INSTANCE.swapForFarming();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 500L);
            }
            if (GardenConfig.INSTANCE.getAutoRodReturnToFarm()) {
                RodManager.INSTANCE.useRod(true);
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 200L);
            }
            PestPrepSwapManager.INSTANCE.reset();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            INSTANCE.finishSequence(true);
        }
        return Unit.INSTANCE;
    }

    private static final void finishSequence$lambda$0(Function0 $callback) {
        block0: {
            isRunning = false;
            Function0 function0 = $callback;
            if (function0 == null) break block0;
            function0.invoke();
        }
    }
}

