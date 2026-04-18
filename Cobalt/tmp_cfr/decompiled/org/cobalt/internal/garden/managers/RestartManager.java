/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J#\u0010\n\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\f\u001a\u00020\u00042\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\b\u00a2\u0006\u0004\b\f\u0010\rJ\u001d\u0010\u000e\u001a\u00020\u00042\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0002\u00a2\u0006\u0004\b\u000e\u0010\rR\"\u0010\u0010\u001a\u00020\u000f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\"\u0010\u0017\u001a\u00020\u00168\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/garden/managers/RestartManager;", "", "<init>", "()V", "", "reset", "", "message", "Lkotlin/Function0;", "onStop", "onChatMessage", "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V", "update", "(Lkotlin/jvm/functions/Function0;)V", "triggerAbort", "", "restartDetected", "Z", "getRestartDetected", "()Z", "setRestartDetected", "(Z)V", "", "abortAt", "J", "getAbortAt", "()J", "setAbortAt", "(J)V", "", "RESTART_PATTERNS", "Ljava/util/List;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRestartManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RestartManager.kt\norg/cobalt/internal/garden/managers/RestartManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,51:1\n1807#2,3:52\n*S KotlinDebug\n*F\n+ 1 RestartManager.kt\norg/cobalt/internal/garden/managers/RestartManager\n*L\n26#1:52,3\n*E\n"})
public final class RestartManager {
    @NotNull
    public static final RestartManager INSTANCE = new RestartManager();
    private static volatile boolean restartDetected;
    private static volatile long abortAt;
    @NotNull
    private static final List<String> RESTART_PATTERNS;

    private RestartManager() {
    }

    public final boolean getRestartDetected() {
        return restartDetected;
    }

    public final void setRestartDetected(boolean bl) {
        restartDetected = bl;
    }

    public final long getAbortAt() {
        return abortAt;
    }

    public final void setAbortAt(long l) {
        abortAt = l;
    }

    public final void reset() {
        restartDetected = false;
        abortAt = 0L;
    }

    public final void onChatMessage(@NotNull String message, @NotNull Function0<Unit> onStop) {
        boolean bl;
        String lower;
        block5: {
            Intrinsics.checkNotNullParameter((Object)message, (String)"message");
            Intrinsics.checkNotNullParameter(onStop, (String)"onStop");
            String string = message.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            lower = string;
            Iterable $this$any$iv = RESTART_PATTERNS;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    String it = (String)element$iv;
                    boolean bl2 = false;
                    if (!StringsKt.contains$default((CharSequence)lower, (CharSequence)it, (boolean)false, (int)2, null)) continue;
                    bl = true;
                    break block5;
                }
                bl = false;
            }
        }
        if (bl) {
            restartDetected = true;
            abortAt = System.currentTimeMillis() + 30000L;
        }
        if (restartDetected && StringsKt.contains$default((CharSequence)lower, (CharSequence)"over", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)lower, (CharSequence)"contest", (boolean)false, (int)2, null)) {
            this.triggerAbort(onStop);
        }
    }

    public final void update(@NotNull Function0<Unit> onStop) {
        Intrinsics.checkNotNullParameter(onStop, (String)"onStop");
        if (!restartDetected) {
            return;
        }
        if (System.currentTimeMillis() >= abortAt) {
            this.triggerAbort(onStop);
        }
    }

    private final void triggerAbort(Function0<Unit> onStop) {
        restartDetected = false;
        GardenWorkerThread.INSTANCE.submit("restart-abort", (Function0<Unit>)((Function0)() -> RestartManager.triggerAbort$lambda$0(onStop)));
    }

    private static final void triggerAbort$lambda$0$0(Function0 $onStop) {
        ScriptBridge.INSTANCE.stopScript();
        $onStop.invoke();
    }

    private static final Unit triggerAbort$lambda$0(Function0 $onStop) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        mc.execute(() -> RestartManager.triggerAbort$lambda$0$0($onStop));
        return Unit.INSTANCE;
    }

    static {
        Object[] objectArray = new String[]{"server going down", "server is restarting", "evacuate", "server restart in"};
        RESTART_PATTERNS = CollectionsKt.listOf((Object[])objectArray);
    }
}

