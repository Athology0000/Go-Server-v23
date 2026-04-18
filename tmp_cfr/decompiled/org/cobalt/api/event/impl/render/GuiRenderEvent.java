/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.event.impl.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.cobalt.api.event.Event;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u000b\u001a\u0004\b\f\u0010\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "Lorg/cobalt/api/event/Event;", "Lnet/minecraft/class_332;", "graphics", "Lnet/minecraft/class_9779;", "delta", "<init>", "(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "Lnet/minecraft/class_332;", "getGraphics", "()Lnet/minecraft/class_332;", "Lnet/minecraft/class_9779;", "getDelta", "()Lnet/minecraft/class_9779;", "cobalt"})
public final class GuiRenderEvent
extends Event {
    @NotNull
    private final class_332 graphics;
    @NotNull
    private final class_9779 delta;

    public GuiRenderEvent(@NotNull class_332 graphics, @NotNull class_9779 delta) {
        Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
        Intrinsics.checkNotNullParameter((Object)delta, (String)"delta");
        super(false, 1, null);
        this.graphics = graphics;
        this.delta = delta;
    }

    @NotNull
    public final class_332 getGraphics() {
        return this.graphics;
    }

    @NotNull
    public final class_9779 getDelta() {
        return this.delta;
    }
}

