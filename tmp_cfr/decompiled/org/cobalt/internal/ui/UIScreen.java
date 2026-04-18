/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.cobalt.api.util.TickScheduler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b \u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bR\u001a\u0010\n\u001a\u00020\t8\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\f\u0010\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/ui/UIScreen;", "Lnet/minecraft/class_437;", "<init>", "()V", "", "openUI", "", "isPauseScreen", "()Z", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "getMc", "()Lnet/minecraft/class_310;", "cobalt"})
public abstract class UIScreen
extends class_437 {
    @NotNull
    private final class_310 mc;

    public UIScreen() {
        super((class_2561)class_2561.method_43473());
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
    }

    @NotNull
    protected final class_310 getMc() {
        return this.mc;
    }

    public final void openUI() {
        TickScheduler.schedule(1L, () -> UIScreen.openUI$lambda$0(this));
    }

    public boolean method_25421() {
        return false;
    }

    private static final void openUI$lambda$0(UIScreen this$0) {
        this$0.mc.method_1507((class_437)this$0);
    }
}

