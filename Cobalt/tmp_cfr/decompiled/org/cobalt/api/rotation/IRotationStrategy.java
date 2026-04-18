/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.rotation;

import kotlin.Metadata;
import net.minecraft.class_746;
import org.cobalt.api.util.helper.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J)\u0010\b\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004H&\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\f\u00a8\u0006\u000e\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/rotation/IRotationStrategy;", "", "Lnet/minecraft/class_746;", "player", "", "targetYaw", "targetPitch", "Lorg/cobalt/api/util/helper/Rotation;", "onRotate", "(Lnet/minecraft/class_746;FF)Lorg/cobalt/api/util/helper/Rotation;", "", "onStart", "()V", "onStop", "cobalt"})
public interface IRotationStrategy {
    @Nullable
    public Rotation onRotate(@NotNull class_746 var1, float var2, float var3);

    default public void onStart() {
    }

    default public void onStop() {
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        public static void onStart(@NotNull IRotationStrategy $this) {
            $this.onStart();
        }

        @Deprecated
        public static void onStop(@NotNull IRotationStrategy $this) {
            $this.onStop();
        }
    }
}

