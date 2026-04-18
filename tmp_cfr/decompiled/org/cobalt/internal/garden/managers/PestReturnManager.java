/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_310;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u001b\u0010\b\u001a\u00020\u00042\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\u0004\b\b\u0010\tR\"\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u000b\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/internal/garden/managers/PestReturnManager;", "", "<init>", "()V", "", "reset", "Lkotlin/Function0;", "onComplete", "startReturn", "(Lkotlin/jvm/functions/Function0;)V", "", "isReturning", "Z", "()Z", "setReturning", "(Z)V", "cobalt"})
public final class PestReturnManager {
    @NotNull
    public static final PestReturnManager INSTANCE = new PestReturnManager();
    private static volatile boolean isReturning;

    private PestReturnManager() {
    }

    public final boolean isReturning() {
        return isReturning;
    }

    public final void setReturning(boolean bl) {
        isReturning = bl;
    }

    public final void reset() {
        isReturning = false;
    }

    public final void startReturn(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        if (isReturning) {
            return;
        }
        isReturning = true;
        GardenWorkerThread.INSTANCE.submit("pest-return", (Function0<Unit>)((Function0)() -> PestReturnManager.startReturn$lambda$0(onComplete)));
    }

    private static final void startReturn$lambda$0$0() {
        ScriptBridge.INSTANCE.startReturnScript(GardenConfig.INSTANCE.getReturnScript());
    }

    private static final void startReturn$lambda$0$1(Function0 $onComplete) {
        $onComplete.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startReturn$lambda$0(Function0 $onComplete) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            mc.execute(PestReturnManager::startReturn$lambda$0$0);
            Thread.sleep(500L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isReturning = false;
            mc.execute(() -> PestReturnManager.startReturn$lambda$0$1($onComplete));
        }
        return Unit.INSTANCE;
    }
}

