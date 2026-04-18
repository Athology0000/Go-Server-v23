/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.mining;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import org.cobalt.internal.mining.RoutesModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0014\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0006\u0010\u0003R\u0014\u0010\b\u001a\u00020\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\t\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/internal/mining/RoutePointPickerScreen;", "Lnet/minecraft/class_437;", "<init>", "()V", "", "init", "onClose", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Companion", "cobalt"})
public final class RoutePointPickerScreen
extends class_437 {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final class_310 mc;

    public RoutePointPickerScreen() {
        super((class_2561)class_2561.method_43470((String)"Route Point Type"));
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
    }

    protected void method_25426() {
        int centerX = this.field_22789 / 2;
        int startY = this.field_22790 / 2 - 40;
        int buttonWidth = 160;
        int buttonHeight = 20;
        int gap = 6;
        this.method_37063((class_364)class_4185.method_46430((class_2561)((class_2561)class_2561.method_43470((String)"Normal")), arg_0 -> RoutePointPickerScreen.init$lambda$0(this, arg_0)).method_46434(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)((class_2561)class_2561.method_43470((String)"Warp")), arg_0 -> RoutePointPickerScreen.init$lambda$1(this, arg_0)).method_46434(centerX - buttonWidth / 2, startY + buttonHeight + gap, buttonWidth, buttonHeight).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)((class_2561)class_2561.method_43470((String)"Mine Anchor")), arg_0 -> RoutePointPickerScreen.init$lambda$2(this, arg_0)).method_46434(centerX - buttonWidth / 2, startY + (buttonHeight + gap) * 2, buttonWidth, buttonHeight).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)((class_2561)class_2561.method_43470((String)"Cancel")), arg_0 -> RoutePointPickerScreen.init$lambda$3(this, arg_0)).method_46434(centerX - buttonWidth / 2, startY + (buttonHeight + gap) * 3 + 4, buttonWidth, buttonHeight).method_46431());
    }

    public void method_25419() {
        this.mc.method_1507(null);
    }

    private static final void init$lambda$0(RoutePointPickerScreen this$0, class_4185 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        RoutesModule.INSTANCE.applyPickedType(RoutesModule.RoutePointType.NORMAL);
        this$0.method_25419();
    }

    private static final void init$lambda$1(RoutePointPickerScreen this$0, class_4185 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        RoutesModule.INSTANCE.applyPickedType(RoutesModule.RoutePointType.WARP);
        this$0.method_25419();
    }

    private static final void init$lambda$2(RoutePointPickerScreen this$0, class_4185 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        RoutesModule.INSTANCE.applyPickedType(RoutesModule.RoutePointType.MINE);
        this$0.method_25419();
    }

    private static final void init$lambda$3(RoutePointPickerScreen this$0, class_4185 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        this$0.method_25419();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/internal/mining/RoutePointPickerScreen$Companion;", "", "<init>", "()V", "", "open", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public final void open() {
            class_310.method_1551().method_1507((class_437)new RoutePointPickerScreen());
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

