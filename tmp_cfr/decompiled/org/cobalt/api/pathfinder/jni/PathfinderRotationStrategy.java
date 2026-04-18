/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.jni;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_746;
import org.cobalt.api.rotation.IRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J'\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0010\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u000fR\u0016\u0010\u0012\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0015\u001a\u00020\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0018\u001a\u00020\u00178\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/pathfinder/jni/PathfinderRotationStrategy;", "Lorg/cobalt/api/rotation/IRotationStrategy;", "<init>", "()V", "", "onStart", "Lnet/minecraft/class_746;", "player", "", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "smoothYaw", "F", "smoothPitch", "", "initialized", "Z", "", "lastNs", "J", "", "SMOOTH_SPEED", "D", "cobalt"})
public final class PathfinderRotationStrategy
implements IRotationStrategy {
    @NotNull
    public static final PathfinderRotationStrategy INSTANCE = new PathfinderRotationStrategy();
    private static float smoothYaw;
    private static float smoothPitch;
    private static boolean initialized;
    private static long lastNs;
    private static final double SMOOTH_SPEED = 14.0;

    private PathfinderRotationStrategy() {
    }

    @Override
    public void onStart() {
        initialized = false;
    }

    @Override
    @NotNull
    public Rotation onRotate(@NotNull class_746 player, float targetYaw, float targetPitch) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        long now = System.nanoTime();
        if (!initialized) {
            smoothYaw = player.method_36454();
            smoothPitch = player.method_36455();
            initialized = true;
            lastNs = now;
            return new Rotation(smoothYaw, smoothPitch);
        }
        float dt = RangesKt.coerceIn((float)((float)((double)(now - lastNs) / 1.0E9)), (float)0.0f, (float)0.1f);
        lastNs = now;
        float t = (float)(1.0 - Math.exp(-14.0 * (double)dt));
        smoothYaw += AngleUtils.INSTANCE.getRotationDelta(smoothYaw, targetYaw) * t;
        smoothPitch += (targetPitch - smoothPitch) * t;
        return new Rotation(smoothYaw, smoothPitch);
    }
}

