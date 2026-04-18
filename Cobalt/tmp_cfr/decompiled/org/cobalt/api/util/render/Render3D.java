/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_12178
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9848
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package org.cobalt.api.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_12178;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_9848;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.util.render.FrustumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J1\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJG\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\b2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\u0012\u001a\u00020\u0011H\u0007\u00a2\u0006\u0004\b\u0013\u0010\u0014JC\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00152\u0006\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\u0018\u001a\u00020\u0011H\u0007\u00a2\u0006\u0004\b\u0019\u0010\u001aJ/\u0010\u001e\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\t\u001a\u00020\bH\u0007\u00a2\u0006\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/api/util/render/Render3D;", "", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "Lnet/minecraft/class_238;", "box", "Ljava/awt/Color;", "color", "", "esp", "", "drawBox", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_238;Ljava/awt/Color;Z)V", "strokeColor", "fillColor", "", "lineWidth", "drawStyledBox", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_238;Ljava/awt/Color;Ljava/awt/Color;ZF)V", "Lnet/minecraft/class_243;", "start", "end", "thickness", "drawLine", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Ljava/awt/Color;ZF)V", "worldPos", "", "text", "drawWorldLabel", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_243;Ljava/lang/String;Ljava/awt/Color;)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRender3D.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Render3D.kt\norg/cobalt/api/util/render/Render3D\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,127:1\n1#2:128\n*E\n"})
public final class Render3D {
    @NotNull
    public static final Render3D INSTANCE = new Render3D();

    private Render3D() {
    }

    @JvmStatic
    public static final void drawBox(@NotNull WorldRenderContext context, @NotNull class_238 box, @NotNull Color color, boolean esp) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter((Object)box, (String)"box");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        Render3D.drawStyledBox(context, box, color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 150), esp, 2.5f);
    }

    public static /* synthetic */ void drawBox$default(WorldRenderContext worldRenderContext, class_238 class_2383, Color color, boolean bl, int n, Object object) {
        if ((n & 8) != 0) {
            bl = false;
        }
        Render3D.drawBox(worldRenderContext, class_2383, color, bl);
    }

    @JvmStatic
    public static final void drawStyledBox(@NotNull WorldRenderContext context, @NotNull class_238 box, @NotNull Color strokeColor, @Nullable Color fillColor, boolean esp, float lineWidth) {
        int n;
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter((Object)box, (String)"box");
        Intrinsics.checkNotNullParameter((Object)strokeColor, (String)"strokeColor");
        if (!FrustumUtils.isVisible(context.getFrustum(), box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324)) {
            return;
        }
        int stroke = class_9848.method_61324((int)strokeColor.getAlpha(), (int)strokeColor.getRed(), (int)strokeColor.getGreen(), (int)strokeColor.getBlue());
        Color color = fillColor;
        if (color != null) {
            Color it = color;
            boolean bl = false;
            n = class_9848.method_61324((int)it.getAlpha(), (int)it.getRed(), (int)it.getGreen(), (int)it.getBlue());
        } else {
            n = class_9848.method_61324((int)0, (int)0, (int)0, (int)0);
        }
        int fill = n;
        class_12179 class_121792 = class_12179.method_75537((int)stroke, (float)lineWidth, (int)fill);
        Intrinsics.checkNotNullExpressionValue((Object)class_121792, (String)"strokeAndFill(...)");
        class_12179 style = class_121792;
        class_12178 class_121782 = class_12180.method_75541((class_238)box, (class_12179)style);
        Intrinsics.checkNotNullExpressionValue((Object)class_121782, (String)"cuboid(...)");
        class_12178 props = class_121782;
        if (esp) {
            props.method_75533();
        }
    }

    public static /* synthetic */ void drawStyledBox$default(WorldRenderContext worldRenderContext, class_238 class_2383, Color color, Color color2, boolean bl, float f, int n, Object object) {
        if ((n & 8) != 0) {
            color2 = null;
        }
        if ((n & 0x10) != 0) {
            bl = false;
        }
        if ((n & 0x20) != 0) {
            f = 2.5f;
        }
        Render3D.drawStyledBox(worldRenderContext, class_2383, color, color2, bl, f);
    }

    @JvmStatic
    public static final void drawLine(@NotNull WorldRenderContext context, @NotNull class_243 start, @NotNull class_243 end, @NotNull Color color, boolean esp, float thickness) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)end, (String)"end");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        if (!FrustumUtils.isVisible(context.getFrustum(), Math.min(start.field_1352, end.field_1352), Math.min(start.field_1351, end.field_1351), Math.min(start.field_1350, end.field_1350), Math.max(start.field_1352, end.field_1352), Math.max(start.field_1351, end.field_1351), Math.max(start.field_1350, end.field_1350))) {
            return;
        }
        int argbColor = class_9848.method_61324((int)color.getAlpha(), (int)color.getRed(), (int)color.getGreen(), (int)color.getBlue());
        class_12178 class_121782 = class_12180.method_75546((class_243)start, (class_243)end, (int)argbColor, (float)thickness);
        Intrinsics.checkNotNullExpressionValue((Object)class_121782, (String)"line(...)");
        class_12178 props = class_121782;
        if (esp) {
            props.method_75533();
        }
    }

    public static /* synthetic */ void drawLine$default(WorldRenderContext worldRenderContext, class_243 class_2432, class_243 class_2433, Color color, boolean bl, float f, int n, Object object) {
        if ((n & 0x10) != 0) {
            bl = false;
        }
        if ((n & 0x20) != 0) {
            f = 1.0f;
        }
        Render3D.drawLine(worldRenderContext, class_2432, class_2433, color, bl, f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @JvmStatic
    public static final void drawWorldLabel(@NotNull WorldRenderContext context, @NotNull class_243 worldPos, @NotNull String text, @NotNull Color color) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter((Object)worldPos, (String)"worldPos");
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_4184 camera = context.getCamera();
        class_243 class_2432 = camera.method_71156();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
        class_243 cam = class_2432;
        class_4587 class_45872 = context.getMatrixStack();
        if (class_45872 == null) {
            class_45872 = new class_4587();
        }
        class_4587 matrices = class_45872;
        class_327 class_3272 = mc.field_1772;
        Intrinsics.checkNotNullExpressionValue((Object)class_3272, (String)"font");
        class_327 font = class_3272;
        class_4597.class_4598 class_45982 = mc.method_22940().method_23000();
        Intrinsics.checkNotNullExpressionValue((Object)class_45982, (String)"bufferSource(...)");
        class_4597.class_4598 buffer = class_45982;
        float textWidth = font.method_1727(text);
        float scale = 0.025f;
        try {
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate((int)770, (int)771, (int)1, (int)771);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask((boolean)false);
            matrices.method_22903();
            matrices.method_22904(worldPos.field_1352 - cam.field_1352, worldPos.field_1351 - cam.field_1351, worldPos.field_1350 - cam.field_1350);
            matrices.method_22907((Quaternionfc)camera.method_23767());
            matrices.method_22905(-scale, -scale, scale);
            font.method_27521(text, -textWidth / 2.0f, 0.0f, color.getRGB(), false, matrices.method_23760().method_23761(), (class_4597)buffer, class_327.class_6415.field_33994, 0, 0xF000F0);
            matrices.method_22909();
            buffer.method_22993();
        }
        finally {
            GlStateManager._depthMask((boolean)true);
            GlStateManager._enableDepthTest();
            GlStateManager._disableBlend();
        }
    }
}

