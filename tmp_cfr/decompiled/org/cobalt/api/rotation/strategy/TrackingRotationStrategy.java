/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.rotation.strategy;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_746;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J)\u0010\f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000eR\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/rotation/strategy/TrackingRotationStrategy;", "Lorg/cobalt/api/rotation/IRotationStrategy;", "", "maxYawStep", "maxPitchStep", "<init>", "(FF)V", "Lnet/minecraft/class_746;", "player", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "F", "cobalt"})
public final class TrackingRotationStrategy
implements IRotationStrategy {
    private final float maxYawStep;
    private final float maxPitchStep;

    public TrackingRotationStrategy(float maxYawStep, float maxPitchStep) {
        this.maxYawStep = maxYawStep;
        this.maxPitchStep = maxPitchStep;
    }

    public /* synthetic */ TrackingRotationStrategy(float f, float f2, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            f = 12.0f;
        }
        if ((n & 2) != 0) {
            f2 = 10.0f;
        }
        this(f, f2);
    }

    @Override
    @Nullable
    public Rotation onRotate(@NotNull class_746 player, float targetYaw, float targetPitch) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        float yawStep = RangesKt.coerceIn((float)AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetYaw), (float)(-this.maxYawStep), (float)this.maxYawStep);
        float pitchStep = RangesKt.coerceIn((float)(targetPitch - player.method_36455()), (float)(-this.maxPitchStep), (float)this.maxPitchStep);
        float nextYaw = player.method_36454() + yawStep;
        float nextPitch = RangesKt.coerceIn((float)(player.method_36455() + pitchStep), (float)-90.0f, (float)90.0f);
        return new Rotation(nextYaw, nextPitch);
    }

    public TrackingRotationStrategy() {
        this(0.0f, 0.0f, 3, null);
    }
}

