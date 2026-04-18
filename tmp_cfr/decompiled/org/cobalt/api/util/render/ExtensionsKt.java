/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util.render;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.util.render.Render3D;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\u001a+\u0010\b\u001a\u00020\u0007*\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\b\u0010\t\u001a=\u0010\u000f\u001a\u00020\u0007*\u00020\u00002\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0011"}, d2={"Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "Lnet/minecraft/class_238;", "box", "Ljava/awt/Color;", "color", "", "esp", "", "drawBox", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_238;Ljava/awt/Color;Z)V", "Lnet/minecraft/class_243;", "start", "end", "", "thickness", "drawLine", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Ljava/awt/Color;ZF)V", "cobalt"})
public final class ExtensionsKt {
    public static final void drawBox(@NotNull WorldRenderContext $this$drawBox, @NotNull class_238 box, @NotNull Color color, boolean esp) {
        Intrinsics.checkNotNullParameter((Object)$this$drawBox, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)box, (String)"box");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        Render3D.drawBox($this$drawBox, box, color, esp);
    }

    public static /* synthetic */ void drawBox$default(WorldRenderContext worldRenderContext, class_238 class_2383, Color color, boolean bl, int n, Object object) {
        if ((n & 4) != 0) {
            bl = false;
        }
        ExtensionsKt.drawBox(worldRenderContext, class_2383, color, bl);
    }

    public static final void drawLine(@NotNull WorldRenderContext $this$drawLine, @NotNull class_243 start, @NotNull class_243 end, @NotNull Color color, boolean esp, float thickness) {
        Intrinsics.checkNotNullParameter((Object)$this$drawLine, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)end, (String)"end");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        Render3D.drawLine($this$drawLine, start, end, color, esp, thickness);
    }

    public static /* synthetic */ void drawLine$default(WorldRenderContext worldRenderContext, class_243 class_2432, class_243 class_2433, Color color, boolean bl, float f, int n, Object object) {
        if ((n & 8) != 0) {
            bl = false;
        }
        if ((n & 0x10) != 0) {
            f = 1.0f;
        }
        ExtensionsKt.drawLine(worldRenderContext, class_2432, class_2433, color, bl, f);
    }
}

