/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_238
 *  net.minecraft.class_4604
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util.render;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_238;
import net.minecraft.class_4604;
import org.cobalt.mixin.render.FrustumInvoker;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\t\u0010\nJG\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b\t\u0010\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/util/render/FrustumUtils;", "", "<init>", "()V", "Lnet/minecraft/class_4604;", "frustum", "Lnet/minecraft/class_238;", "box", "", "isVisible", "(Lnet/minecraft/class_4604;Lnet/minecraft/class_238;)Z", "", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "(Lnet/minecraft/class_4604;DDDDDD)Z", "cobalt"})
public final class FrustumUtils {
    @NotNull
    public static final FrustumUtils INSTANCE = new FrustumUtils();

    private FrustumUtils() {
    }

    @JvmStatic
    public static final boolean isVisible(@NotNull class_4604 frustum, @NotNull class_238 box) {
        Intrinsics.checkNotNullParameter((Object)frustum, (String)"frustum");
        Intrinsics.checkNotNullParameter((Object)box, (String)"box");
        return FrustumUtils.isVisible(frustum, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324);
    }

    @JvmStatic
    public static final boolean isVisible(@NotNull class_4604 frustum, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Intrinsics.checkNotNullParameter((Object)frustum, (String)"frustum");
        int result = ((FrustumInvoker)frustum).invokeCubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        return result == -2 || result == -1;
    }
}

