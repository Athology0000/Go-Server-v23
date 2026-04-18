/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.rotation.strategy;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_746;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u00002\u00020\u0001B\u00af\u0001\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0002\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u0012\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u0012\u0010\b\u0002\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u0012\u0010\b\u0002\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u0012\u0010\b\u0002\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u0012\u0010\b\u0002\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u0012\u0010\b\u0002\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t\u00a2\u0006\u0004\b\u0010\u0010\u0011J)\u0010\u0017\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0017\u0010\u0018J?\u0010\u001f\u001a\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u00022\u0006\u0010\u001e\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b\u001f\u0010 J'\u0010$\u001a\u00020\u00022\u0006\u0010!\u001a\u00020\u00022\u0006\u0010\"\u001a\u00020\u00022\u0006\u0010#\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b$\u0010%R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010&R\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010&R\u0014\u0010\u0005\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010&R\u0014\u0010\u0006\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010&R\u0014\u0010\u0007\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010&R\u0014\u0010\b\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010&R\u001c\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010'R\u001c\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000b\u0010'R\u001c\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\f\u0010'R\u001c\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010'R\u001c\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000e\u0010'R\u001c\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000f\u0010'\u00a8\u0006("}, d2={"Lorg/cobalt/api/rotation/strategy/BezierTrackingRotationStrategy;", "Lorg/cobalt/api/rotation/IRotationStrategy;", "", "maxYawStep", "maxPitchStep", "curveIn", "curveOut", "minScale", "snapThreshold", "Lkotlin/Function0;", "yawStepSampler", "pitchStepSampler", "curveInProvider", "curveOutProvider", "minScaleProvider", "snapThresholdProvider", "<init>", "(FFFFFFLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "Lnet/minecraft/class_746;", "player", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "delta", "maxStep", "cIn", "cOut", "mScale", "snap", "smoothStep", "(FFFFFF)F", "t", "p1", "p2", "cubicBezier", "(FFF)F", "F", "Lkotlin/jvm/functions/Function0;", "cobalt"})
public final class BezierTrackingRotationStrategy
implements IRotationStrategy {
    private final float maxYawStep;
    private final float maxPitchStep;
    private final float curveIn;
    private final float curveOut;
    private final float minScale;
    private final float snapThreshold;
    @Nullable
    private final Function0<Float> yawStepSampler;
    @Nullable
    private final Function0<Float> pitchStepSampler;
    @Nullable
    private final Function0<Float> curveInProvider;
    @Nullable
    private final Function0<Float> curveOutProvider;
    @Nullable
    private final Function0<Float> minScaleProvider;
    @Nullable
    private final Function0<Float> snapThresholdProvider;

    public BezierTrackingRotationStrategy(float maxYawStep, float maxPitchStep, float curveIn, float curveOut, float minScale, float snapThreshold, @Nullable Function0<Float> yawStepSampler, @Nullable Function0<Float> pitchStepSampler, @Nullable Function0<Float> curveInProvider, @Nullable Function0<Float> curveOutProvider, @Nullable Function0<Float> minScaleProvider, @Nullable Function0<Float> snapThresholdProvider) {
        this.maxYawStep = maxYawStep;
        this.maxPitchStep = maxPitchStep;
        this.curveIn = curveIn;
        this.curveOut = curveOut;
        this.minScale = minScale;
        this.snapThreshold = snapThreshold;
        this.yawStepSampler = yawStepSampler;
        this.pitchStepSampler = pitchStepSampler;
        this.curveInProvider = curveInProvider;
        this.curveOutProvider = curveOutProvider;
        this.minScaleProvider = minScaleProvider;
        this.snapThresholdProvider = snapThresholdProvider;
    }

    public /* synthetic */ BezierTrackingRotationStrategy(float f, float f2, float f3, float f4, float f5, float f6, Function0 function0, Function0 function02, Function0 function03, Function0 function04, Function0 function05, Function0 function06, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            f = 12.0f;
        }
        if ((n & 2) != 0) {
            f2 = 10.0f;
        }
        if ((n & 4) != 0) {
            f3 = 0.06f;
        }
        if ((n & 8) != 0) {
            f4 = 0.94f;
        }
        if ((n & 0x10) != 0) {
            f5 = 0.18f;
        }
        if ((n & 0x20) != 0) {
            f6 = 0.25f;
        }
        if ((n & 0x40) != 0) {
            function0 = null;
        }
        if ((n & 0x80) != 0) {
            function02 = null;
        }
        if ((n & 0x100) != 0) {
            function03 = null;
        }
        if ((n & 0x200) != 0) {
            function04 = null;
        }
        if ((n & 0x400) != 0) {
            function05 = null;
        }
        if ((n & 0x800) != 0) {
            function06 = null;
        }
        this(f, f2, f3, f4, f5, f6, (Function0<Float>)function0, (Function0<Float>)function02, (Function0<Float>)function03, (Function0<Float>)function04, (Function0<Float>)function05, (Function0<Float>)function06);
    }

    @Override
    @Nullable
    public Rotation onRotate(@NotNull class_746 player, float targetYaw, float targetPitch) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Function0<Float> function0 = this.yawStepSampler;
        float effectiveYaw = function0 != null ? ((Number)function0.invoke()).floatValue() : this.maxYawStep;
        Function0<Float> function02 = this.pitchStepSampler;
        float effectivePitch = function02 != null ? ((Number)function02.invoke()).floatValue() : this.maxPitchStep;
        Function0<Float> function03 = this.curveInProvider;
        float effectiveCurveIn = function03 != null ? ((Number)function03.invoke()).floatValue() : this.curveIn;
        Function0<Float> function04 = this.curveOutProvider;
        float effectiveCurveOut = function04 != null ? ((Number)function04.invoke()).floatValue() : this.curveOut;
        Function0<Float> function05 = this.minScaleProvider;
        float effectiveMinScale = function05 != null ? ((Number)function05.invoke()).floatValue() : this.minScale;
        Function0<Float> function06 = this.snapThresholdProvider;
        float effectiveSnap = function06 != null ? ((Number)function06.invoke()).floatValue() : this.snapThreshold;
        float yawDelta = AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), targetYaw);
        float pitchDelta = targetPitch - player.method_36455();
        float nextYaw = player.method_36454() + this.smoothStep(yawDelta, effectiveYaw, effectiveCurveIn, effectiveCurveOut, effectiveMinScale, effectiveSnap);
        float nextPitch = RangesKt.coerceIn((float)(player.method_36455() + this.smoothStep(pitchDelta, effectivePitch, effectiveCurveIn, effectiveCurveOut, effectiveMinScale, effectiveSnap)), (float)-90.0f, (float)90.0f);
        return new Rotation(nextYaw, nextPitch);
    }

    private final float smoothStep(float delta, float maxStep, float cIn, float cOut, float mScale, float snap) {
        float absDelta = Math.abs(delta);
        if (absDelta < snap) {
            return delta;
        }
        float stepLimit = Math.min(absDelta, maxStep);
        float t = RangesKt.coerceIn((float)(absDelta / maxStep), (float)0.0f, (float)1.0f);
        float eased = this.cubicBezier(t, cIn, cOut);
        float scale = mScale + (1.0f - mScale) * eased;
        float step = stepLimit * scale;
        return delta < 0.0f ? -step : step;
    }

    private final float cubicBezier(float t, float p1, float p2) {
        float inv = 1.0f - t;
        return 3.0f * inv * inv * t * p1 + 3.0f * inv * t * t * p2 + t * t * t;
    }

    public BezierTrackingRotationStrategy() {
        this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, null, null, null, null, null, null, 4095, null);
    }
}

