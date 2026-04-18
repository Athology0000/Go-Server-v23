/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001d\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001d\u0010\u000e\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\f\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0015\u0010\u000e\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\f\u00a2\u0006\u0004\b\u000e\u0010\u0010J\u0015\u0010\u000e\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\u0011\u00a2\u0006\u0004\b\u000e\u0010\u0012R\u0014\u0010\u0014\u001a\u00020\u00138\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0019\u001a\u0004\u0018\u00010\u00168BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/util/AngleUtils;", "", "<init>", "()V", "", "angle", "normalizeAngle", "(F)F", "from", "to", "getRotationDelta", "(FF)F", "Lnet/minecraft/class_243;", "Lorg/cobalt/api/util/helper/Rotation;", "getRotation", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;)Lorg/cobalt/api/util/helper/Rotation;", "(Lnet/minecraft/class_243;)Lorg/cobalt/api/util/helper/Rotation;", "Lnet/minecraft/class_1297;", "(Lnet/minecraft/class_1297;)Lorg/cobalt/api/util/helper/Rotation;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lnet/minecraft/class_746;", "getPlayer", "()Lnet/minecraft/class_746;", "player", "cobalt"})
public final class AngleUtils {
    @NotNull
    public static final AngleUtils INSTANCE = new AngleUtils();
    @NotNull
    private static final class_310 mc;

    private AngleUtils() {
    }

    private final class_746 getPlayer() {
        return AngleUtils.mc.field_1724;
    }

    public final float normalizeAngle(float angle) {
        float a = angle % 360.0f;
        if (a >= 180.0f) {
            a -= 360.0f;
        }
        if (a < -180.0f) {
            a += 360.0f;
        }
        return a;
    }

    public final float getRotationDelta(float from, float to) {
        float delta = this.normalizeAngle(to) - this.normalizeAngle(from);
        if (delta > 180.0f) {
            delta -= 360.0f;
        }
        if (delta < -180.0f) {
            delta += 360.0f;
        }
        return delta;
    }

    @NotNull
    public final Rotation getRotation(@NotNull class_243 from, @NotNull class_243 to) {
        Intrinsics.checkNotNullParameter((Object)from, (String)"from");
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        double xDiff = to.field_1352 - from.field_1352;
        double yDiff = to.field_1351 - from.field_1351;
        double zDiff = to.field_1350 - from.field_1350;
        double dist = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        return new Rotation((float)Math.toDegrees(Math.atan2(zDiff, xDiff)) - 90.0f, -((float)Math.toDegrees(Math.atan2(yDiff, dist))));
    }

    @NotNull
    public final Rotation getRotation(@NotNull class_243 to) {
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        class_746 class_7462 = this.getPlayer();
        Intrinsics.checkNotNull((Object)class_7462);
        class_243 class_2432 = class_7462.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        return this.getRotation(class_2432, to);
    }

    @NotNull
    public final Rotation getRotation(@NotNull class_1297 to) {
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        class_746 class_7462 = this.getPlayer();
        Intrinsics.checkNotNull((Object)class_7462);
        class_243 class_2432 = class_7462.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 class_2433 = to.method_73189().method_1031(0.0, RangesKt.coerceAtMost((double)((double)to.method_17682() * 0.85), (double)1.7), 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
        return this.getRotation(class_2432, class_2433);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

