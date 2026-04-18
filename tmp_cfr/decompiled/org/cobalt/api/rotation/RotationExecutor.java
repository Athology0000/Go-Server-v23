/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.rotation;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.pathfinding.DebugLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\b\u00a2\u0006\u0004\b\u000b\u0010\u0003J\r\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0012J7\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u00132\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0013H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0016\u0010\"\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010$\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b$\u0010#R\u0018\u0010%\u001a\u0004\u0018\u00010\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0016\u0010\r\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\r\u0010\u001e\u00a8\u0006'"}, d2={"Lorg/cobalt/api/rotation/RotationExecutor;", "", "<init>", "()V", "Lorg/cobalt/api/util/helper/Rotation;", "endRot", "Lorg/cobalt/api/rotation/IRotationStrategy;", "strategy", "", "rotateTo", "(Lorg/cobalt/api/util/helper/Rotation;Lorg/cobalt/api/rotation/IRotationStrategy;)V", "stopRotating", "", "isRotating", "()Z", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "onRotate", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "", "rotation", "prevRotation", "min", "max", "applyGCD", "(FFLjava/lang/Float;Ljava/lang/Float;)F", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "DEBUG_TICK_FILE", "Z", "", "DEBUG_TICK_INTERVAL", "J", "targetYaw", "F", "targetPitch", "currStrat", "Lorg/cobalt/api/rotation/IRotationStrategy;", "cobalt"})
public final class RotationExecutor {
    @NotNull
    public static final RotationExecutor INSTANCE = new RotationExecutor();
    @NotNull
    private static final class_310 mc;
    private static final boolean DEBUG_TICK_FILE = true;
    private static final long DEBUG_TICK_INTERVAL = 5L;
    private static float targetYaw;
    private static float targetPitch;
    @Nullable
    private static IRotationStrategy currStrat;
    private static boolean isRotating;

    private RotationExecutor() {
    }

    public final void rotateTo(@NotNull Rotation endRot, @NotNull IRotationStrategy strategy) {
        Intrinsics.checkNotNullParameter((Object)endRot, (String)"endRot");
        Intrinsics.checkNotNullParameter((Object)strategy, (String)"strategy");
        if (isRotating && currStrat == strategy) {
            targetYaw = endRot.getYaw();
            targetPitch = endRot.getPitch();
            return;
        }
        this.stopRotating();
        targetYaw = endRot.getYaw();
        targetPitch = endRot.getPitch();
        currStrat = strategy;
        strategy.onStart();
        isRotating = true;
    }

    public final void stopRotating() {
        IRotationStrategy iRotationStrategy = currStrat;
        if (iRotationStrategy != null) {
            iRotationStrategy.onStop();
        }
        currStrat = null;
        isRotating = false;
    }

    public final boolean isRotating() {
        return isRotating;
    }

    @SubscribeEvent
    public final void onRotate(@NotNull WorldRenderEvent.Last event) {
        block10: {
            Intrinsics.checkNotNullParameter((Object)event, (String)"event");
            class_746 class_7462 = RotationExecutor.mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            if (!isRotating) {
                if (DebugLog.INSTANCE.getDebugFileEnabled()) {
                    class_638 class_6382 = RotationExecutor.mc.field_1687;
                    if (class_6382 != null) {
                        class_638 level2 = class_6382;
                        boolean bl = false;
                        if (level2.method_75260() % 5L == 0L) {
                            String string = "%.2f";
                            Object[] objectArray = new Object[]{Float.valueOf(player.method_36454())};
                            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(player.method_36455())};
                            String string3 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
                            DebugLog.INSTANCE.debugTickFile(mc, "Rotation", "idle yaw=" + string2 + " pitch=" + string3, level2.method_75260());
                        }
                    }
                }
                return;
            }
            IRotationStrategy iRotationStrategy = currStrat;
            if (iRotationStrategy == null) break block10;
            IRotationStrategy it = iRotationStrategy;
            boolean bl = false;
            Rotation result = it.onRotate(player, targetYaw, targetPitch);
            if (result == null) {
                INSTANCE.stopRotating();
            } else {
                if (DebugLog.INSTANCE.getDebugFileEnabled()) {
                    class_638 class_6383 = RotationExecutor.mc.field_1687;
                    if (class_6383 != null) {
                        class_638 level3 = class_6383;
                        boolean bl2 = false;
                        if (level3.method_75260() % 5L == 0L) {
                            String string = "%.2f";
                            Object[] objectArray = new Object[]{Float.valueOf(player.method_36454())};
                            String string4 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(result.getYaw())};
                            String string5 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(player.method_36455())};
                            String string6 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(result.getPitch())};
                            String string7 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string7, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(targetYaw)};
                            String string8 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string8, (String)"format(...)");
                            string = "%.2f";
                            objectArray = new Object[]{Float.valueOf(targetPitch)};
                            String string9 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                            Intrinsics.checkNotNullExpressionValue((Object)string9, (String)"format(...)");
                            DebugLog.INSTANCE.debugTickFile(mc, "Rotation", "yaw=" + string4 + "->" + string5 + " pitch=" + string6 + "->" + string7 + " target=" + string8 + "/" + string9, level3.method_75260());
                        }
                    }
                }
                player.method_36456(AngleUtils.INSTANCE.normalizeAngle(RotationExecutor.applyGCD$default(INSTANCE, result.getYaw(), player.method_36454(), null, null, 12, null)));
                player.method_36457(RangesKt.coerceIn((float)INSTANCE.applyGCD(result.getPitch(), player.method_36455(), Float.valueOf(-90.0f), Float.valueOf(90.0f)), (float)-90.0f, (float)90.0f));
            }
        }
    }

    private final float applyGCD(float rotation, float prevRotation, Float min, Float max) {
        if (Float.isNaN(rotation) || Float.isNaN(prevRotation)) {
            return prevRotation;
        }
        Object object = RotationExecutor.mc.field_1690.method_42495().method_41753();
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
        double sensitivity = ((Number)object).doubleValue();
        if (Double.isNaN(sensitivity)) {
            return prevRotation;
        }
        double f = sensitivity * 0.6 + 0.2;
        double gcd = f * f * f * 1.2;
        float delta = AngleUtils.INSTANCE.getRotationDelta(prevRotation, rotation);
        if (Float.isNaN(delta) || gcd == 0.0 || Double.isNaN(gcd)) {
            return prevRotation;
        }
        double scaled = (double)delta / gcd;
        if (Double.isNaN(scaled) || Double.isInfinite(scaled)) {
            return prevRotation;
        }
        double roundedDelta = (double)MathKt.roundToInt((double)scaled) * gcd;
        double result = (double)prevRotation + roundedDelta;
        if (max != null && result > (double)max.floatValue()) {
            result -= gcd;
        }
        if (min != null && result < (double)min.floatValue()) {
            result += gcd;
        }
        return (float)result;
    }

    static /* synthetic */ float applyGCD$default(RotationExecutor rotationExecutor, float f, float f2, Float f3, Float f4, int n, Object object) {
        if ((n & 4) != 0) {
            f3 = null;
        }
        if ((n & 8) != 0) {
            f4 = null;
        }
        return rotationExecutor.applyGCD(f, f2, f3, f4);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

