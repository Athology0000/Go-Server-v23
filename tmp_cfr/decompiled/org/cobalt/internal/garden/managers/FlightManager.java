/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.internal.garden.GardenConfig;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u000f\u0010\u0007\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0007\u0010\u0003J\u000f\u0010\b\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\b\u0010\u0003\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/garden/managers/FlightManager;", "", "<init>", "()V", "", "startFlying", "stopFlying", "tapJump", "tapShift", "cobalt"})
public final class FlightManager {
    @NotNull
    public static final FlightManager INSTANCE = new FlightManager();

    private FlightManager() {
    }

    public final void startFlying() {
        this.tapJump();
        Thread.sleep(120L);
        this.tapJump();
        Thread.sleep(250L);
    }

    public final void stopFlying() {
        String string = ((Object)StringsKt.trim((CharSequence)GardenConfig.INSTANCE.getUnflyMode())).toString().toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toUpperCase(...)");
        if (Intrinsics.areEqual((Object)string, (Object)"SNEAK")) {
            this.tapShift();
            Thread.sleep(150L);
        } else {
            this.tapJump();
            Thread.sleep(120L);
            this.tapJump();
            Thread.sleep(250L);
        }
    }

    private final void tapJump() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        mc.execute(() -> FlightManager.tapJump$lambda$0(mc));
        Thread.sleep(80L);
        mc.execute(() -> FlightManager.tapJump$lambda$1(mc));
    }

    private final void tapShift() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        mc.execute(() -> FlightManager.tapShift$lambda$0(mc));
        Thread.sleep(80L);
        mc.execute(() -> FlightManager.tapShift$lambda$1(mc));
    }

    private static final void tapJump$lambda$0(class_310 $mc) {
        $mc.field_1690.field_1903.method_23481(true);
    }

    private static final void tapJump$lambda$1(class_310 $mc) {
        $mc.field_1690.field_1903.method_23481(false);
    }

    private static final void tapShift$lambda$0(class_310 $mc) {
        $mc.field_1690.field_1832.method_23481(true);
    }

    private static final void tapShift$lambda$1(class_310 $mc) {
        $mc.field_1690.field_1832.method_23481(false);
    }
}

