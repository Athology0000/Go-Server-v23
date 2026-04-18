/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.rotation.strategy;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_746;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.pathfinding.HeadRotationModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001BM\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\t\u0010\nJ'\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0003H\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0012R\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0012R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0012R\u001a\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0012R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/rotation/strategy/HeadRotationStrategy;", "Lorg/cobalt/api/rotation/IRotationStrategy;", "Lkotlin/Function0;", "", "speedScaleSampler", "accelScaleSampler", "pitchStepSampler", "maxSpeedSampler", "maxAccelSampler", "<init>", "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "Lnet/minecraft/class_746;", "player", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "Lkotlin/jvm/functions/Function0;", "cobalt"})
public final class HeadRotationStrategy
implements IRotationStrategy {
    @NotNull
    private final Function0<Float> speedScaleSampler;
    @NotNull
    private final Function0<Float> accelScaleSampler;
    @NotNull
    private final Function0<Float> pitchStepSampler;
    @NotNull
    private final Function0<Float> maxSpeedSampler;
    @NotNull
    private final Function0<Float> maxAccelSampler;

    public HeadRotationStrategy(@NotNull Function0<Float> speedScaleSampler, @NotNull Function0<Float> accelScaleSampler, @NotNull Function0<Float> pitchStepSampler, @NotNull Function0<Float> maxSpeedSampler, @NotNull Function0<Float> maxAccelSampler) {
        Intrinsics.checkNotNullParameter(speedScaleSampler, (String)"speedScaleSampler");
        Intrinsics.checkNotNullParameter(accelScaleSampler, (String)"accelScaleSampler");
        Intrinsics.checkNotNullParameter(pitchStepSampler, (String)"pitchStepSampler");
        Intrinsics.checkNotNullParameter(maxSpeedSampler, (String)"maxSpeedSampler");
        Intrinsics.checkNotNullParameter(maxAccelSampler, (String)"maxAccelSampler");
        this.speedScaleSampler = speedScaleSampler;
        this.accelScaleSampler = accelScaleSampler;
        this.pitchStepSampler = pitchStepSampler;
        this.maxSpeedSampler = maxSpeedSampler;
        this.maxAccelSampler = maxAccelSampler;
    }

    @Override
    @NotNull
    public Rotation onRotate(@NotNull class_746 player, float targetYaw, float targetPitch) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        float maxSpeedScale = ((Number)this.speedScaleSampler.invoke()).floatValue();
        float accelScale = ((Number)this.accelScaleSampler.invoke()).floatValue();
        float maxTurnSpeed = ((Number)this.maxSpeedSampler.invoke()).floatValue();
        float maxTurnAccel = ((Number)this.maxAccelSampler.invoke()).floatValue();
        float maxPitchStep = ((Number)this.pitchStepSampler.invoke()).floatValue();
        float yawDelta = AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetYaw);
        float yawStep = HeadRotationModule.computeTurnDelta$default(HeadRotationModule.INSTANCE, yawDelta, maxSpeedScale, accelScale, maxTurnSpeed, maxTurnAccel, null, 32, null);
        float newYaw = AngleUtils.INSTANCE.normalizeAngle(player.method_36454() + yawStep);
        float pitchDelta = targetPitch - player.method_36455();
        float pitchStep = HeadRotationModule.computePitchDelta$default(HeadRotationModule.INSTANCE, pitchDelta, maxSpeedScale, accelScale, maxPitchStep * 20.0f, maxPitchStep * 60.0f, null, 32, null);
        float newPitch = RangesKt.coerceIn((float)(player.method_36455() + pitchStep), (float)-89.9f, (float)89.9f);
        return new Rotation(newYaw, newPitch);
    }
}

