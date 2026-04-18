/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.rotation.strategy;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_746;
import org.cobalt.api.rotation.EasingType;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.PlayerUtils;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ)\u0010\u0012\u001a\u0004\u0018\u00010\u00112\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0017R\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0017R\u0014\u0010\u0006\u001a\u00020\u00058\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0018R\u0016\u0010\u0019\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001b\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001aR\u0016\u0010\u001c\u001a\u00020\u00058\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u0018\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/api/rotation/strategy/TimedEaseStrategy;", "Lorg/cobalt/api/rotation/IRotationStrategy;", "Lorg/cobalt/api/rotation/EasingType;", "yawEasing", "pitchEasing", "", "duration", "<init>", "(Lorg/cobalt/api/rotation/EasingType;Lorg/cobalt/api/rotation/EasingType;J)V", "", "onStart", "()V", "Lnet/minecraft/class_746;", "player", "", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "pitch", "clampPitch", "(F)F", "Lorg/cobalt/api/rotation/EasingType;", "J", "startYaw", "F", "startPitch", "endTime", "cobalt"})
public final class TimedEaseStrategy
implements IRotationStrategy {
    @NotNull
    private final EasingType yawEasing;
    @NotNull
    private final EasingType pitchEasing;
    private final long duration;
    private float startYaw;
    private float startPitch;
    private long endTime;

    public TimedEaseStrategy(@NotNull EasingType yawEasing, @NotNull EasingType pitchEasing, long duration) {
        Intrinsics.checkNotNullParameter((Object)((Object)yawEasing), (String)"yawEasing");
        Intrinsics.checkNotNullParameter((Object)((Object)pitchEasing), (String)"pitchEasing");
        this.yawEasing = yawEasing;
        this.pitchEasing = pitchEasing;
        this.duration = duration;
    }

    @Override
    public void onStart() {
        Rotation rotation = PlayerUtils.getRotation();
        if (rotation == null) {
            throw new IllegalStateException("Player rotation is null");
        }
        Rotation rotation2 = rotation;
        float yaw = rotation2.component1();
        float pitch = rotation2.component2();
        this.startYaw = yaw;
        this.startPitch = pitch;
        this.endTime = System.currentTimeMillis() + this.duration;
    }

    @Override
    @Nullable
    public Rotation onRotate(@NotNull class_746 player, float targetYaw, float targetPitch) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        long now = System.currentTimeMillis();
        if (now >= this.endTime) {
            return null;
        }
        float progress = 1.0f - (float)(this.endTime - now) / (float)this.duration;
        float t = RangesKt.coerceIn((float)progress, (float)0.0f, (float)1.0f);
        float yawDelta = AngleUtils.INSTANCE.normalizeAngle(targetYaw - this.startYaw);
        float yaw = this.yawEasing.apply(this.startYaw, this.startYaw + yawDelta, t);
        float pitch = this.clampPitch(this.pitchEasing.apply(this.startPitch, this.clampPitch(targetPitch), t));
        return new Rotation(yaw, pitch);
    }

    private final float clampPitch(float pitch) {
        return RangesKt.coerceIn((float)pitch, (float)-90.0f, (float)90.0f);
    }
}

