/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.event.impl.render;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\r\u0010\u000eR\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/event/impl/render/GuiRenderContext;", "", "<init>", "()V", "Lnet/minecraft/class_332;", "graphics", "Lnet/minecraft/class_9779;", "delta", "", "set", "(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "getGraphics", "()Lnet/minecraft/class_332;", "getDelta", "()Lnet/minecraft/class_9779;", "lastGraphics", "Lnet/minecraft/class_332;", "lastDelta", "Lnet/minecraft/class_9779;", "cobalt"})
public final class GuiRenderContext {
    @NotNull
    public static final GuiRenderContext INSTANCE = new GuiRenderContext();
    @Nullable
    private static volatile class_332 lastGraphics;
    @Nullable
    private static volatile class_9779 lastDelta;

    private GuiRenderContext() {
    }

    @JvmStatic
    public static final void set(@NotNull class_332 graphics, @NotNull class_9779 delta) {
        Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
        Intrinsics.checkNotNullParameter((Object)delta, (String)"delta");
        lastGraphics = graphics;
        lastDelta = delta;
    }

    @Nullable
    public final class_332 getGraphics() {
        return lastGraphics;
    }

    @Nullable
    public final class_9779 getDelta() {
        return lastDelta;
    }
}

