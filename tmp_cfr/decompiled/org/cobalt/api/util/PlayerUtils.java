/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\u00020\u0005*\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0014\u0010\t\u001a\u00020\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\t\u0010\nR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u000b8FX\u0087\u0004\u00a2\u0006\f\u0012\u0004\b\u000e\u0010\u0003\u001a\u0004\b\f\u0010\rR\u001a\u0010\u0014\u001a\u00020\u00108FX\u0087\u0004\u00a2\u0006\f\u0012\u0004\b\u0013\u0010\u0003\u001a\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u00158FX\u0087\u0004\u00a2\u0006\f\u0012\u0004\b\u0018\u0010\u0003\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/util/PlayerUtils;", "", "<init>", "()V", "Lnet/minecraft/class_746;", "Lnet/minecraft/class_2338;", "playerPos", "(Lnet/minecraft/class_746;)Lnet/minecraft/class_2338;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lnet/minecraft/class_243;", "getPosition", "()Lnet/minecraft/class_243;", "getPosition$annotations", "position", "", "getFov", "()I", "getFov$annotations", "fov", "Lorg/cobalt/api/util/helper/Rotation;", "getRotation", "()Lorg/cobalt/api/util/helper/Rotation;", "getRotation$annotations", "rotation", "cobalt"})
public final class PlayerUtils {
    @NotNull
    public static final PlayerUtils INSTANCE = new PlayerUtils();
    @NotNull
    private static final class_310 mc;

    private PlayerUtils() {
    }

    @Nullable
    public static final class_243 getPosition() {
        class_746 class_7462 = PlayerUtils.mc.field_1724;
        return class_7462 != null ? class_7462.method_73189() : null;
    }

    @JvmStatic
    public static /* synthetic */ void getPosition$annotations() {
    }

    public static final int getFov() {
        Object object = PlayerUtils.mc.field_1690.method_41808().method_41753();
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
        return ((Number)object).intValue();
    }

    @JvmStatic
    public static /* synthetic */ void getFov$annotations() {
    }

    @Nullable
    public static final Rotation getRotation() {
        Rotation rotation;
        class_746 class_7462 = PlayerUtils.mc.field_1724;
        if (class_7462 != null) {
            class_746 it = class_7462;
            boolean bl = false;
            rotation = new Rotation(it.method_36454(), it.method_36455());
        } else {
            rotation = null;
        }
        return rotation;
    }

    @JvmStatic
    public static /* synthetic */ void getRotation$annotations() {
    }

    @JvmStatic
    @NotNull
    public static final class_2338 playerPos(@NotNull class_746 $this$playerPos) {
        Intrinsics.checkNotNullParameter((Object)$this$playerPos, (String)"<this>");
        class_2338 class_23382 = class_2338.method_49637((double)$this$playerPos.method_23317(), (double)Math.ceil($this$playerPos.method_23318() - 0.25), (double)$this$playerPos.method_23321());
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        return class_23382;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

