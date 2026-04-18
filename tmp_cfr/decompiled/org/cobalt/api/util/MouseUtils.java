/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_310;
import org.cobalt.mixin.client.MinecraftAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u000f\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\n\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\n\u0010\u0003J\u000f\u0010\u000b\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u000b\u0010\u0003R\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0016\u0010\b\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\b\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/util/MouseUtils;", "", "<init>", "()V", "", "ungrabMouse", "grabMouse", "", "isMouseUngrabbed", "()Z", "leftClick", "rightClick", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Z", "cobalt"})
public final class MouseUtils {
    @NotNull
    public static final MouseUtils INSTANCE = new MouseUtils();
    @NotNull
    private static final class_310 mc;
    private static boolean isMouseUngrabbed;

    private MouseUtils() {
    }

    @JvmStatic
    public static final void ungrabMouse() {
        isMouseUngrabbed = true;
    }

    @JvmStatic
    public static final void grabMouse() {
        isMouseUngrabbed = false;
    }

    @JvmStatic
    public static final boolean isMouseUngrabbed() {
        return isMouseUngrabbed;
    }

    @JvmStatic
    public static final void leftClick() {
        class_310 class_3102 = mc;
        Intrinsics.checkNotNull((Object)class_3102, (String)"null cannot be cast to non-null type org.cobalt.mixin.client.MinecraftAccessor");
        ((MinecraftAccessor)class_3102).leftClick();
    }

    @JvmStatic
    public static final void rightClick() {
        class_310 class_3102 = mc;
        Intrinsics.checkNotNull((Object)class_3102, (String)"null cannot be cast to non-null type org.cobalt.mixin.client.MinecraftAccessor");
        ((MinecraftAccessor)class_3102).rightClick();
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

