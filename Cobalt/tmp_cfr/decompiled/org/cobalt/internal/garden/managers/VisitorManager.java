/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_355
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_355;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.cobalt.internal.garden.managers.GearManager;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001b\u0010\u000b\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010R\"\u0010\u0011\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0011\u0010\b\"\u0004\b\u0013\u0010\u0014R\"\u0010\u0016\u001a\u00020\u00158\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u00158\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u0017\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/garden/managers/VisitorManager;", "", "<init>", "()V", "", "reset", "", "shouldHandle", "()Z", "Lkotlin/Function0;", "onComplete", "startVisitorSequence", "(Lkotlin/jvm/functions/Function0;)V", "", "message", "onChatMessage", "(Ljava/lang/String;)V", "isHandlingVisitor", "Z", "setHandlingVisitor", "(Z)V", "", "visitorCooldownUntil", "J", "getVisitorCooldownUntil", "()J", "setVisitorCooldownUntil", "(J)V", "VISITOR_COOLDOWN_MS", "cobalt"})
public final class VisitorManager {
    @NotNull
    public static final VisitorManager INSTANCE = new VisitorManager();
    private static volatile boolean isHandlingVisitor;
    private static volatile long visitorCooldownUntil;
    private static final long VISITOR_COOLDOWN_MS = 30000L;

    private VisitorManager() {
    }

    public final boolean isHandlingVisitor() {
        return isHandlingVisitor;
    }

    public final void setHandlingVisitor(boolean bl) {
        isHandlingVisitor = bl;
    }

    public final long getVisitorCooldownUntil() {
        return visitorCooldownUntil;
    }

    public final void setVisitorCooldownUntil(long l) {
        visitorCooldownUntil = l;
    }

    public final void reset() {
        isHandlingVisitor = false;
        visitorCooldownUntil = 0L;
    }

    public final boolean shouldHandle() {
        String string;
        CharSequence charSequence;
        Regex regex;
        if (isHandlingVisitor) {
            return false;
        }
        if (System.currentTimeMillis() < visitorCooldownUntil) {
            return false;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_355 class_3552 = mc.field_1705.method_1750();
        TabOverlayAccessor tabOverlayAccessor = class_3552 instanceof TabOverlayAccessor ? (TabOverlayAccessor)class_3552 : null;
        if (tabOverlayAccessor == null) {
            return false;
        }
        TabOverlayAccessor overlay = tabOverlayAccessor;
        Object object = overlay.getFooter();
        if (object == null || (object = object.getString()) == null || (object = (regex = new Regex("\u00a7[0-9a-fk-or]")).replace(charSequence = (CharSequence)object, string = "")) == null) {
            return false;
        }
        Object footer = object;
        return StringsKt.contains((CharSequence)((CharSequence)footer), (CharSequence)"visitor", (boolean)true) && !StringsKt.contains((CharSequence)((CharSequence)footer), (CharSequence)"visitors: 0", (boolean)true);
    }

    public final void startVisitorSequence(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        if (isHandlingVisitor) {
            return;
        }
        isHandlingVisitor = true;
        GardenWorkerThread.INSTANCE.submit("visitor", (Function0<Unit>)((Function0)() -> VisitorManager.startVisitorSequence$lambda$0(onComplete)));
    }

    public final void onChatMessage(@NotNull String message) {
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
    }

    private static final void startVisitorSequence$lambda$0$0() {
        ScriptBridge.INSTANCE.stopScript();
    }

    private static final void startVisitorSequence$lambda$0$1() {
        ScriptBridge.INSTANCE.startVisitorScript(GardenConfig.INSTANCE.getVisitorScript());
    }

    private static final void startVisitorSequence$lambda$0$2(Function0 $onComplete) {
        $onComplete.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startVisitorSequence$lambda$0(Function0 $onComplete) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            mc.execute(VisitorManager::startVisitorSequence$lambda$0$0);
            Thread.sleep(300L);
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled()) {
                GearManager.INSTANCE.swapForVisitor();
                Thread.sleep(2000L);
            }
            mc.execute(VisitorManager::startVisitorSequence$lambda$0$1);
            Thread.sleep(500L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isHandlingVisitor = false;
            visitorCooldownUntil = System.currentTimeMillis() + 30000L;
            mc.execute(() -> VisitorManager.startVisitorSequence$lambda$0$2($onComplete));
        }
        return Unit.INSTANCE;
    }
}

