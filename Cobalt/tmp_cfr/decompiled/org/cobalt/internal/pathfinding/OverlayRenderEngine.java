/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_12178
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_9848
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pathfinding;

import java.util.ArrayList;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_12178;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_9848;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.util.render.FrustumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004QRSTB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\u0015\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0010\u0010\u0011Jw\u0010\"\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001b2\b\b\u0002\u0010\u001e\u001a\u00020\u001d2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010!\u001a\u00020\u0004\u00a2\u0006\u0004\b\"\u0010#J\u0083\u0001\u0010,\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020\u00142\u0006\u0010'\u001a\u00020\u00142\u0006\u0010(\u001a\u00020\u00142\u0006\u0010)\u001a\u00020\u00142\b\u0010*\u001a\u0004\u0018\u00010\u001b2\b\u0010+\u001a\u0004\u0018\u00010\u001b2\b\b\u0002\u0010\u001e\u001a\u00020\u001d2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010!\u001a\u00020\u0004\u00a2\u0006\u0004\b,\u0010-J3\u00100\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010/\u001a\u00020.2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0004\b0\u00101J3\u00102\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010/\u001a\u00020.2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0004\b2\u00101J=\u00103\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010/\u001a\u00020.2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u001e\u001a\u00020\u001d\u00a2\u0006\u0004\b3\u00104JE\u00105\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010/\u001a\u00020.2\u0006\u0010\u001c\u001a\u00020\u001b2\b\b\u0002\u0010 \u001a\u00020\u001f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u001e\u001a\u00020\u001d\u00a2\u0006\u0004\b5\u00106J\u0015\u00109\u001a\u00020\u00062\u0006\u00108\u001a\u000207\u00a2\u0006\u0004\b9\u0010:J\u001f\u0010=\u001a\u00020\u00062\u0006\u00108\u001a\u0002072\u0006\u0010<\u001a\u00020;H\u0002\u00a2\u0006\u0004\b=\u0010>J\u001f\u0010A\u001a\u00020\u00062\u0006\u00108\u001a\u0002072\u0006\u0010@\u001a\u00020?H\u0002\u00a2\u0006\u0004\bA\u0010BR\u001c\u0010E\u001a\n D*\u0004\u0018\u00010C0C8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bE\u0010FR\u0016\u0010G\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bG\u0010HR\u0014\u0010I\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010JR\u0014\u0010K\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010JR$\u0010N\u001a\u0012\u0012\u0004\u0012\u00020?0Lj\b\u0012\u0004\u0012\u00020?`M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010OR$\u0010P\u001a\u0012\u0012\u0004\u0012\u00020;0Lj\b\u0012\u0004\u0012\u00020;`M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010O\u00a8\u0006U"}, d2={"Lorg/cobalt/internal/pathfinding/OverlayRenderEngine;", "", "<init>", "()V", "", "value", "", "setEnabled", "(Z)V", "isEnabled", "()Z", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Stats;", "stats", "()Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Stats;", "", "tag", "clearTag", "(Ljava/lang/String;)V", "Lnet/minecraft/class_1937;", "level", "", "x1", "y1", "z1", "x2", "y2", "z2", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "color", "", "lineWidth", "", "durationTicks", "forceRender", "addLine", "(Lnet/minecraft/class_1937;DDDDDDLorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FILjava/lang/String;Z)V", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "fill", "outline", "addBox", "(Lnet/minecraft/class_1937;DDDDDDLorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FILjava/lang/String;Z)V", "Lnet/minecraft/class_2338;", "pos", "highlightBlock", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;ILjava/lang/String;)V", "highlightBlockFill", "outlineBlock", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;ILjava/lang/String;F)V", "outlineBlockColor", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;ILjava/lang/String;F)V", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "render", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;)V", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Box;", "box", "renderBox", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Box;)V", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Line;", "line", "renderLine", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Line;)V", "Lorg/apache/logging/log4j/Logger;", "kotlin.jvm.PlatformType", "logger", "Lorg/apache/logging/log4j/Logger;", "enabled", "Z", "highlightFill", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "highlightOutline", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "lines", "Ljava/util/ArrayList;", "boxes", "Stats", "Color", "Line", "Box", "cobalt"})
public final class OverlayRenderEngine {
    @NotNull
    public static final OverlayRenderEngine INSTANCE = new OverlayRenderEngine();
    private static final Logger logger = LogManager.getLogger((String)"cobalt-pathfinding");
    private static volatile boolean enabled = true;
    @NotNull
    private static final Color highlightFill = new Color(180, 0, 255, 90);
    @NotNull
    private static final Color highlightOutline = new Color(210, 80, 255, 255);
    @NotNull
    private static final ArrayList<Line> lines = new ArrayList();
    @NotNull
    private static final ArrayList<Box> boxes = new ArrayList();

    private OverlayRenderEngine() {
    }

    public final void setEnabled(boolean value) {
        enabled = value;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public final Stats stats() {
        return new Stats(enabled, lines.size(), boxes.size());
    }

    public final void clearTag(@NotNull String tag) {
        Intrinsics.checkNotNullParameter((Object)tag, (String)"tag");
        lines.removeIf(arg_0 -> OverlayRenderEngine.clearTag$lambda$1(arg_0 -> OverlayRenderEngine.clearTag$lambda$0(tag, arg_0), arg_0));
        boxes.removeIf(arg_0 -> OverlayRenderEngine.clearTag$lambda$3(arg_0 -> OverlayRenderEngine.clearTag$lambda$2(tag, arg_0), arg_0));
    }

    public final void addLine(@NotNull class_1937 level2, double x1, double y1, double z1, double x2, double y2, double z2, @NotNull Color color, float lineWidth, int durationTicks, @Nullable String tag, boolean forceRender) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        long expiresAt = level2.method_75260() + (long)durationTicks;
        lines.add(new Line(new class_243(x1, y1, z1), new class_243(x2, y2, z2), color, lineWidth, expiresAt, tag, forceRender));
    }

    public static /* synthetic */ void addLine$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, double d, double d2, double d3, double d4, double d5, double d6, Color color, float f, int n, String string, boolean bl, int n2, Object object) {
        if ((n2 & 0x100) != 0) {
            f = 1.5f;
        }
        if ((n2 & 0x200) != 0) {
            n = 2;
        }
        if ((n2 & 0x400) != 0) {
            string = null;
        }
        if ((n2 & 0x800) != 0) {
            bl = false;
        }
        overlayRenderEngine.addLine(class_19372, d, d2, d3, d4, d5, d6, color, f, n, string, bl);
    }

    public final void addBox(@NotNull class_1937 level2, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Nullable Color fill, @Nullable Color outline, float lineWidth, int durationTicks, @Nullable String tag, boolean forceRender) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        long expiresAt = level2.method_75260() + (long)durationTicks;
        boxes.add(new Box(minX, minY, minZ, maxX, maxY, maxZ, fill, outline, lineWidth, expiresAt, tag, forceRender));
    }

    public static /* synthetic */ void addBox$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, double d, double d2, double d3, double d4, double d5, double d6, Color color, Color color2, float f, int n, String string, boolean bl, int n2, Object object) {
        if ((n2 & 0x200) != 0) {
            f = 1.5f;
        }
        if ((n2 & 0x400) != 0) {
            n = 2;
        }
        if ((n2 & 0x800) != 0) {
            string = null;
        }
        if ((n2 & 0x1000) != 0) {
            bl = false;
        }
        overlayRenderEngine.addBox(class_19372, d, d2, d3, d4, d5, d6, color, color2, f, n, string, bl);
    }

    public final void highlightBlock(@NotNull class_1937 level2, @NotNull class_2338 pos, int durationTicks, @Nullable String tag) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        double pad = 0.002;
        OverlayRenderEngine.addBox$default(this, level2, (double)pos.method_10263() - pad, (double)pos.method_10264() - pad, (double)pos.method_10260() - pad, (double)pos.method_10263() + 1.0 + pad, (double)pos.method_10264() + 1.0 + pad, (double)pos.method_10260() + 1.0 + pad, highlightFill, highlightOutline, 2.0f, durationTicks, tag, false, 4096, null);
    }

    public static /* synthetic */ void highlightBlock$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, class_2338 class_23382, int n, String string, int n2, Object object) {
        if ((n2 & 4) != 0) {
            n = 40;
        }
        if ((n2 & 8) != 0) {
            string = null;
        }
        overlayRenderEngine.highlightBlock(class_19372, class_23382, n, string);
    }

    public final void highlightBlockFill(@NotNull class_1937 level2, @NotNull class_2338 pos, int durationTicks, @Nullable String tag) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        double pad = 0.002;
        OverlayRenderEngine.addBox$default(this, level2, (double)pos.method_10263() - pad, (double)pos.method_10264() - pad, (double)pos.method_10260() - pad, (double)pos.method_10263() + 1.0 + pad, (double)pos.method_10264() + 1.0 + pad, (double)pos.method_10260() + 1.0 + pad, highlightFill, null, 2.0f, durationTicks, tag, false, 4096, null);
    }

    public static /* synthetic */ void highlightBlockFill$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, class_2338 class_23382, int n, String string, int n2, Object object) {
        if ((n2 & 4) != 0) {
            n = 40;
        }
        if ((n2 & 8) != 0) {
            string = null;
        }
        overlayRenderEngine.highlightBlockFill(class_19372, class_23382, n, string);
    }

    public final void outlineBlock(@NotNull class_1937 level2, @NotNull class_2338 pos, int durationTicks, @Nullable String tag, float lineWidth) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        double pad = 0.002;
        OverlayRenderEngine.addBox$default(this, level2, (double)pos.method_10263() - pad, (double)pos.method_10264() - pad, (double)pos.method_10260() - pad, (double)pos.method_10263() + 1.0 + pad, (double)pos.method_10264() + 1.0 + pad, (double)pos.method_10260() + 1.0 + pad, null, highlightOutline, lineWidth, durationTicks, tag, false, 4096, null);
    }

    public static /* synthetic */ void outlineBlock$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, class_2338 class_23382, int n, String string, float f, int n2, Object object) {
        if ((n2 & 4) != 0) {
            n = 40;
        }
        if ((n2 & 8) != 0) {
            string = null;
        }
        if ((n2 & 0x10) != 0) {
            f = 2.2f;
        }
        overlayRenderEngine.outlineBlock(class_19372, class_23382, n, string, f);
    }

    public final void outlineBlockColor(@NotNull class_1937 level2, @NotNull class_2338 pos, @NotNull Color color, int durationTicks, @Nullable String tag, float lineWidth) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)color, (String)"color");
        double pad = 0.002;
        OverlayRenderEngine.addBox$default(this, level2, (double)pos.method_10263() - pad, (double)pos.method_10264() - pad, (double)pos.method_10260() - pad, (double)pos.method_10263() + 1.0 + pad, (double)pos.method_10264() + 1.0 + pad, (double)pos.method_10260() + 1.0 + pad, null, color, lineWidth, durationTicks, tag, false, 4096, null);
    }

    public static /* synthetic */ void outlineBlockColor$default(OverlayRenderEngine overlayRenderEngine, class_1937 class_19372, class_2338 class_23382, Color color, int n, String string, float f, int n2, Object object) {
        if ((n2 & 8) != 0) {
            n = 40;
        }
        if ((n2 & 0x10) != 0) {
            string = null;
        }
        if ((n2 & 0x20) != 0) {
            f = 2.2f;
        }
        overlayRenderEngine.outlineBlockColor(class_19372, class_23382, color, n, string, f);
    }

    public final void render(@NotNull WorldRenderContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        if (!enabled) {
            return;
        }
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        long now = level2.method_75260();
        lines.removeIf(arg_0 -> OverlayRenderEngine.render$lambda$1(arg_0 -> OverlayRenderEngine.render$lambda$0(now, arg_0), arg_0));
        boxes.removeIf(arg_0 -> OverlayRenderEngine.render$lambda$3(arg_0 -> OverlayRenderEngine.render$lambda$2(now, arg_0), arg_0));
        if (lines.isEmpty() && boxes.isEmpty()) {
            return;
        }
        try {
            Iterator<Box> iterator = boxes.iterator();
            Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
            Iterator<Object> iterator2 = iterator;
            while (iterator2.hasNext()) {
                Box box;
                Intrinsics.checkNotNullExpressionValue((Object)iterator2.next(), (String)"next(...)");
                this.renderBox(context, box);
            }
            Iterator<Line> iterator3 = lines.iterator();
            Intrinsics.checkNotNullExpressionValue(iterator3, (String)"iterator(...)");
            iterator2 = iterator3;
            while (iterator2.hasNext()) {
                Object object = iterator2.next();
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"next(...)");
                Line line = (Line)object;
                this.renderLine(context, line);
            }
        }
        catch (Exception ex) {
            enabled = false;
            lines.clear();
            boxes.clear();
            logger.error("OverlayRenderEngine disabled after render failure.", (Throwable)ex);
        }
    }

    private final void renderBox(WorldRenderContext context, Box box) {
        if (!box.getForceRender() && !FrustumUtils.isVisible(context.getFrustum(), box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ())) {
            return;
        }
        Color color = box.getOutline();
        if (color == null) {
            color = new Color(0, 0, 0, 0);
        }
        int stroke = color.toArgb();
        Color color2 = box.getFill();
        if (color2 == null) {
            color2 = new Color(0, 0, 0, 0);
        }
        int fill = color2.toArgb();
        class_12179 class_121792 = class_12179.method_75537((int)stroke, (float)box.getLineWidth(), (int)fill);
        Intrinsics.checkNotNullExpressionValue((Object)class_121792, (String)"strokeAndFill(...)");
        class_12179 style = class_121792;
        class_12178 class_121782 = class_12180.method_75541((class_238)new class_238(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()), (class_12179)style);
        Intrinsics.checkNotNullExpressionValue((Object)class_121782, (String)"cuboid(...)");
        class_12178 props = class_121782;
        props.method_75533();
    }

    private final void renderLine(WorldRenderContext context, Line line) {
        if (!line.getForceRender() && !FrustumUtils.isVisible(context.getFrustum(), Math.min(line.getStart().field_1352, line.getEnd().field_1352) - 0.5, Math.min(line.getStart().field_1351, line.getEnd().field_1351), Math.min(line.getStart().field_1350, line.getEnd().field_1350) - 0.5, Math.max(line.getStart().field_1352, line.getEnd().field_1352) + 0.5, Math.max(line.getStart().field_1351, line.getEnd().field_1351) + 1.0, Math.max(line.getStart().field_1350, line.getEnd().field_1350) + 0.5)) {
            return;
        }
        class_12178 class_121782 = class_12180.method_75546((class_243)line.getStart(), (class_243)line.getEnd(), (int)line.getColor().toArgb(), (float)line.getLineWidth());
        Intrinsics.checkNotNullExpressionValue((Object)class_121782, (String)"line(...)");
        class_12178 props = class_121782;
        props.method_75533();
    }

    private static final boolean clearTag$lambda$0(String $tag, Line it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Intrinsics.areEqual((Object)it.getTag(), (Object)$tag);
    }

    private static final boolean clearTag$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean clearTag$lambda$2(String $tag, Box it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Intrinsics.areEqual((Object)it.getTag(), (Object)$tag);
    }

    private static final boolean clearTag$lambda$3(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean render$lambda$0(long $now, Line it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getExpiresAt() < $now;
    }

    private static final boolean render$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean render$lambda$2(long $now, Box it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getExpiresAt() < $now;
    }

    private static final boolean render$lambda$3(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001a\n\u0002\u0010\b\n\u0002\b\u0016\b\u0086\b\u0018\u00002\u00020\u0001Bo\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0017J\u0010\u0010\u0019\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0019\u0010\u0017J\u0010\u0010\u001a\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u0017J\u0010\u0010\u001b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u0017J\u0010\u0010\u001c\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u0017J\u0012\u0010\u001d\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0012\u0010\u001f\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0004\b\u001f\u0010\u001eJ\u0010\u0010 \u001a\u00020\fH\u00c6\u0003\u00a2\u0006\u0004\b \u0010!J\u0010\u0010\"\u001a\u00020\u000eH\u00c6\u0003\u00a2\u0006\u0004\b\"\u0010#J\u0012\u0010$\u001a\u0004\u0018\u00010\u0010H\u00c6\u0003\u00a2\u0006\u0004\b$\u0010%J\u0010\u0010&\u001a\u00020\u0012H\u00c6\u0003\u00a2\u0006\u0004\b&\u0010'J\u008e\u0001\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00022\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00102\b\b\u0002\u0010\u0013\u001a\u00020\u0012H\u00c6\u0001\u00a2\u0006\u0004\b(\u0010)J\u001b\u0010+\u001a\u00020\u00122\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b+\u0010,J\u0011\u0010.\u001a\u00020-H\u00d6\u0081\u0004\u00a2\u0006\u0004\b.\u0010/J\u0011\u00100\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b0\u0010%R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u00101\u001a\u0004\b2\u0010\u0017R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u00101\u001a\u0004\b3\u0010\u0017R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u00101\u001a\u0004\b4\u0010\u0017R\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u00101\u001a\u0004\b5\u0010\u0017R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u00101\u001a\u0004\b6\u0010\u0017R\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u00101\u001a\u0004\b7\u0010\u0017R\u0019\u0010\n\u001a\u0004\u0018\u00010\t8\u0006\u00a2\u0006\f\n\u0004\b\n\u00108\u001a\u0004\b9\u0010\u001eR\u0019\u0010\u000b\u001a\u0004\u0018\u00010\t8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u00108\u001a\u0004\b:\u0010\u001eR\u0017\u0010\r\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\r\u0010;\u001a\u0004\b<\u0010!R\u0017\u0010\u000f\u001a\u00020\u000e8\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010=\u001a\u0004\b>\u0010#R\u0019\u0010\u0011\u001a\u0004\u0018\u00010\u00108\u0006\u00a2\u0006\f\n\u0004\b\u0011\u0010?\u001a\u0004\b@\u0010%R\u0017\u0010\u0013\u001a\u00020\u00128\u0006\u00a2\u0006\f\n\u0004\b\u0013\u0010A\u001a\u0004\bB\u0010'\u00a8\u0006C"}, d2={"Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Box;", "", "", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "fill", "outline", "", "lineWidth", "", "expiresAt", "", "tag", "", "forceRender", "<init>", "(DDDDDDLorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FJLjava/lang/String;Z)V", "component1", "()D", "component2", "component3", "component4", "component5", "component6", "component7", "()Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "component8", "component9", "()F", "component10", "()J", "component11", "()Ljava/lang/String;", "component12", "()Z", "copy", "(DDDDDDLorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FJLjava/lang/String;Z)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Box;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "D", "getMinX", "getMinY", "getMinZ", "getMaxX", "getMaxY", "getMaxZ", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "getFill", "getOutline", "F", "getLineWidth", "J", "getExpiresAt", "Ljava/lang/String;", "getTag", "Z", "getForceRender", "cobalt"})
    public static final class Box {
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;
        @Nullable
        private final Color fill;
        @Nullable
        private final Color outline;
        private final float lineWidth;
        private final long expiresAt;
        @Nullable
        private final String tag;
        private final boolean forceRender;

        public Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Nullable Color fill, @Nullable Color outline, float lineWidth, long expiresAt, @Nullable String tag, boolean forceRender) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.fill = fill;
            this.outline = outline;
            this.lineWidth = lineWidth;
            this.expiresAt = expiresAt;
            this.tag = tag;
            this.forceRender = forceRender;
        }

        public /* synthetic */ Box(double d, double d2, double d3, double d4, double d5, double d6, Color color, Color color2, float f, long l, String string, boolean bl, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 0x800) != 0) {
                bl = false;
            }
            this(d, d2, d3, d4, d5, d6, color, color2, f, l, string, bl);
        }

        public final double getMinX() {
            return this.minX;
        }

        public final double getMinY() {
            return this.minY;
        }

        public final double getMinZ() {
            return this.minZ;
        }

        public final double getMaxX() {
            return this.maxX;
        }

        public final double getMaxY() {
            return this.maxY;
        }

        public final double getMaxZ() {
            return this.maxZ;
        }

        @Nullable
        public final Color getFill() {
            return this.fill;
        }

        @Nullable
        public final Color getOutline() {
            return this.outline;
        }

        public final float getLineWidth() {
            return this.lineWidth;
        }

        public final long getExpiresAt() {
            return this.expiresAt;
        }

        @Nullable
        public final String getTag() {
            return this.tag;
        }

        public final boolean getForceRender() {
            return this.forceRender;
        }

        public final double component1() {
            return this.minX;
        }

        public final double component2() {
            return this.minY;
        }

        public final double component3() {
            return this.minZ;
        }

        public final double component4() {
            return this.maxX;
        }

        public final double component5() {
            return this.maxY;
        }

        public final double component6() {
            return this.maxZ;
        }

        @Nullable
        public final Color component7() {
            return this.fill;
        }

        @Nullable
        public final Color component8() {
            return this.outline;
        }

        public final float component9() {
            return this.lineWidth;
        }

        public final long component10() {
            return this.expiresAt;
        }

        @Nullable
        public final String component11() {
            return this.tag;
        }

        public final boolean component12() {
            return this.forceRender;
        }

        @NotNull
        public final Box copy(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Nullable Color fill, @Nullable Color outline, float lineWidth, long expiresAt, @Nullable String tag, boolean forceRender) {
            return new Box(minX, minY, minZ, maxX, maxY, maxZ, fill, outline, lineWidth, expiresAt, tag, forceRender);
        }

        public static /* synthetic */ Box copy$default(Box box, double d, double d2, double d3, double d4, double d5, double d6, Color color, Color color2, float f, long l, String string, boolean bl, int n, Object object) {
            if ((n & 1) != 0) {
                d = box.minX;
            }
            if ((n & 2) != 0) {
                d2 = box.minY;
            }
            if ((n & 4) != 0) {
                d3 = box.minZ;
            }
            if ((n & 8) != 0) {
                d4 = box.maxX;
            }
            if ((n & 0x10) != 0) {
                d5 = box.maxY;
            }
            if ((n & 0x20) != 0) {
                d6 = box.maxZ;
            }
            if ((n & 0x40) != 0) {
                color = box.fill;
            }
            if ((n & 0x80) != 0) {
                color2 = box.outline;
            }
            if ((n & 0x100) != 0) {
                f = box.lineWidth;
            }
            if ((n & 0x200) != 0) {
                l = box.expiresAt;
            }
            if ((n & 0x400) != 0) {
                string = box.tag;
            }
            if ((n & 0x800) != 0) {
                bl = box.forceRender;
            }
            return box.copy(d, d2, d3, d4, d5, d6, color, color2, f, l, string, bl);
        }

        @NotNull
        public String toString() {
            return "Box(minX=" + this.minX + ", minY=" + this.minY + ", minZ=" + this.minZ + ", maxX=" + this.maxX + ", maxY=" + this.maxY + ", maxZ=" + this.maxZ + ", fill=" + this.fill + ", outline=" + this.outline + ", lineWidth=" + this.lineWidth + ", expiresAt=" + this.expiresAt + ", tag=" + this.tag + ", forceRender=" + this.forceRender + ")";
        }

        public int hashCode() {
            int result = Double.hashCode(this.minX);
            result = result * 31 + Double.hashCode(this.minY);
            result = result * 31 + Double.hashCode(this.minZ);
            result = result * 31 + Double.hashCode(this.maxX);
            result = result * 31 + Double.hashCode(this.maxY);
            result = result * 31 + Double.hashCode(this.maxZ);
            result = result * 31 + (this.fill == null ? 0 : this.fill.hashCode());
            result = result * 31 + (this.outline == null ? 0 : this.outline.hashCode());
            result = result * 31 + Float.hashCode(this.lineWidth);
            result = result * 31 + Long.hashCode(this.expiresAt);
            result = result * 31 + (this.tag == null ? 0 : this.tag.hashCode());
            result = result * 31 + Boolean.hashCode(this.forceRender);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Box)) {
                return false;
            }
            Box box = (Box)other;
            if (Double.compare(this.minX, box.minX) != 0) {
                return false;
            }
            if (Double.compare(this.minY, box.minY) != 0) {
                return false;
            }
            if (Double.compare(this.minZ, box.minZ) != 0) {
                return false;
            }
            if (Double.compare(this.maxX, box.maxX) != 0) {
                return false;
            }
            if (Double.compare(this.maxY, box.maxY) != 0) {
                return false;
            }
            if (Double.compare(this.maxZ, box.maxZ) != 0) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.fill, (Object)box.fill)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.outline, (Object)box.outline)) {
                return false;
            }
            if (Float.compare(this.lineWidth, box.lineWidth) != 0) {
                return false;
            }
            if (this.expiresAt != box.expiresAt) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.tag, (Object)box.tag)) {
                return false;
            }
            return this.forceRender == box.forceRender;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\n\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\r\u0010\f\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\rJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\rJ\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\rJ8\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\rJ\u0011\u0010\u001a\u001a\u00020\u0019H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001c\u001a\u0004\b\u001e\u0010\rR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001c\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001c\u001a\u0004\b \u0010\r\u00a8\u0006!"}, d2={"Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "", "", "r", "g", "b", "a", "<init>", "(IIII)V", "alpha", "withAlpha", "(I)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "toArgb", "()I", "component1", "component2", "component3", "component4", "copy", "(IIII)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getR", "getG", "getB", "getA", "cobalt"})
    public static final class Color {
        private final int r;
        private final int g;
        private final int b;
        private final int a;

        public Color(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public final int getR() {
            return this.r;
        }

        public final int getG() {
            return this.g;
        }

        public final int getB() {
            return this.b;
        }

        public final int getA() {
            return this.a;
        }

        @NotNull
        public final Color withAlpha(int alpha) {
            return new Color(this.r, this.g, this.b, alpha);
        }

        public final int toArgb() {
            return class_9848.method_61324((int)this.a, (int)this.r, (int)this.g, (int)this.b);
        }

        public final int component1() {
            return this.r;
        }

        public final int component2() {
            return this.g;
        }

        public final int component3() {
            return this.b;
        }

        public final int component4() {
            return this.a;
        }

        @NotNull
        public final Color copy(int r, int g, int b, int a) {
            return new Color(r, g, b, a);
        }

        public static /* synthetic */ Color copy$default(Color color, int n, int n2, int n3, int n4, int n5, Object object) {
            if ((n5 & 1) != 0) {
                n = color.r;
            }
            if ((n5 & 2) != 0) {
                n2 = color.g;
            }
            if ((n5 & 4) != 0) {
                n3 = color.b;
            }
            if ((n5 & 8) != 0) {
                n4 = color.a;
            }
            return color.copy(n, n2, n3, n4);
        }

        @NotNull
        public String toString() {
            return "Color(r=" + this.r + ", g=" + this.g + ", b=" + this.b + ", a=" + this.a + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.r);
            result = result * 31 + Integer.hashCode(this.g);
            result = result * 31 + Integer.hashCode(this.b);
            result = result * 31 + Integer.hashCode(this.a);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Color)) {
                return false;
            }
            Color color = (Color)other;
            if (this.r != color.r) {
                return false;
            }
            if (this.g != color.g) {
                return false;
            }
            if (this.b != color.b) {
                return false;
            }
            return this.a == color.a;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0011\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\t\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\tH\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\rH\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u001dJX\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\t2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u00c6\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u001b\u0010!\u001a\u00020\r2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b!\u0010\"J\u0011\u0010$\u001a\u00020#H\u00d6\u0081\u0004\u00a2\u0006\u0004\b$\u0010%J\u0011\u0010&\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b&\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010'\u001a\u0004\b(\u0010\u0012R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010'\u001a\u0004\b)\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010*\u001a\u0004\b+\u0010\u0015R\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010,\u001a\u0004\b-\u0010\u0017R\u0017\u0010\n\u001a\u00020\t8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010.\u001a\u0004\b/\u0010\u0019R\u0019\u0010\f\u001a\u0004\u0018\u00010\u000b8\u0006\u00a2\u0006\f\n\u0004\b\f\u00100\u001a\u0004\b1\u0010\u001bR\u0017\u0010\u000e\u001a\u00020\r8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u00102\u001a\u0004\b3\u0010\u001d\u00a8\u00064"}, d2={"Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Line;", "", "Lnet/minecraft/class_243;", "start", "end", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "color", "", "lineWidth", "", "expiresAt", "", "tag", "", "forceRender", "<init>", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FJLjava/lang/String;Z)V", "component1", "()Lnet/minecraft/class_243;", "component2", "component3", "()Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "component4", "()F", "component5", "()J", "component6", "()Ljava/lang/String;", "component7", "()Z", "copy", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;FJLjava/lang/String;Z)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Line;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lnet/minecraft/class_243;", "getStart", "getEnd", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "getColor", "F", "getLineWidth", "J", "getExpiresAt", "Ljava/lang/String;", "getTag", "Z", "getForceRender", "cobalt"})
    public static final class Line {
        @NotNull
        private final class_243 start;
        @NotNull
        private final class_243 end;
        @NotNull
        private final Color color;
        private final float lineWidth;
        private final long expiresAt;
        @Nullable
        private final String tag;
        private final boolean forceRender;

        public Line(@NotNull class_243 start, @NotNull class_243 end, @NotNull Color color, float lineWidth, long expiresAt, @Nullable String tag, boolean forceRender) {
            Intrinsics.checkNotNullParameter((Object)start, (String)"start");
            Intrinsics.checkNotNullParameter((Object)end, (String)"end");
            Intrinsics.checkNotNullParameter((Object)color, (String)"color");
            this.start = start;
            this.end = end;
            this.color = color;
            this.lineWidth = lineWidth;
            this.expiresAt = expiresAt;
            this.tag = tag;
            this.forceRender = forceRender;
        }

        public /* synthetic */ Line(class_243 class_2432, class_243 class_2433, Color color, float f, long l, String string, boolean bl, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 0x40) != 0) {
                bl = false;
            }
            this(class_2432, class_2433, color, f, l, string, bl);
        }

        @NotNull
        public final class_243 getStart() {
            return this.start;
        }

        @NotNull
        public final class_243 getEnd() {
            return this.end;
        }

        @NotNull
        public final Color getColor() {
            return this.color;
        }

        public final float getLineWidth() {
            return this.lineWidth;
        }

        public final long getExpiresAt() {
            return this.expiresAt;
        }

        @Nullable
        public final String getTag() {
            return this.tag;
        }

        public final boolean getForceRender() {
            return this.forceRender;
        }

        @NotNull
        public final class_243 component1() {
            return this.start;
        }

        @NotNull
        public final class_243 component2() {
            return this.end;
        }

        @NotNull
        public final Color component3() {
            return this.color;
        }

        public final float component4() {
            return this.lineWidth;
        }

        public final long component5() {
            return this.expiresAt;
        }

        @Nullable
        public final String component6() {
            return this.tag;
        }

        public final boolean component7() {
            return this.forceRender;
        }

        @NotNull
        public final Line copy(@NotNull class_243 start, @NotNull class_243 end, @NotNull Color color, float lineWidth, long expiresAt, @Nullable String tag, boolean forceRender) {
            Intrinsics.checkNotNullParameter((Object)start, (String)"start");
            Intrinsics.checkNotNullParameter((Object)end, (String)"end");
            Intrinsics.checkNotNullParameter((Object)color, (String)"color");
            return new Line(start, end, color, lineWidth, expiresAt, tag, forceRender);
        }

        public static /* synthetic */ Line copy$default(Line line, class_243 class_2432, class_243 class_2433, Color color, float f, long l, String string, boolean bl, int n, Object object) {
            if ((n & 1) != 0) {
                class_2432 = line.start;
            }
            if ((n & 2) != 0) {
                class_2433 = line.end;
            }
            if ((n & 4) != 0) {
                color = line.color;
            }
            if ((n & 8) != 0) {
                f = line.lineWidth;
            }
            if ((n & 0x10) != 0) {
                l = line.expiresAt;
            }
            if ((n & 0x20) != 0) {
                string = line.tag;
            }
            if ((n & 0x40) != 0) {
                bl = line.forceRender;
            }
            return line.copy(class_2432, class_2433, color, f, l, string, bl);
        }

        @NotNull
        public String toString() {
            return "Line(start=" + this.start + ", end=" + this.end + ", color=" + this.color + ", lineWidth=" + this.lineWidth + ", expiresAt=" + this.expiresAt + ", tag=" + this.tag + ", forceRender=" + this.forceRender + ")";
        }

        public int hashCode() {
            int result = this.start.hashCode();
            result = result * 31 + this.end.hashCode();
            result = result * 31 + this.color.hashCode();
            result = result * 31 + Float.hashCode(this.lineWidth);
            result = result * 31 + Long.hashCode(this.expiresAt);
            result = result * 31 + (this.tag == null ? 0 : this.tag.hashCode());
            result = result * 31 + Boolean.hashCode(this.forceRender);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Line)) {
                return false;
            }
            Line line = (Line)other;
            if (!Intrinsics.areEqual((Object)this.start, (Object)line.start)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.end, (Object)line.end)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.color, (Object)line.color)) {
                return false;
            }
            if (Float.compare(this.lineWidth, line.lineWidth) != 0) {
                return false;
            }
            if (this.expiresAt != line.expiresAt) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.tag, (Object)line.tag)) {
                return false;
            }
            return this.forceRender == line.forceRender;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0011\u001a\u00020\u00022\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\fJ\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0019\u001a\u0004\b\u001a\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0019\u001a\u0004\b\u001b\u0010\f\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Stats;", "", "", "enabled", "", "lines", "boxes", "<init>", "(ZII)V", "component1", "()Z", "component2", "()I", "component3", "copy", "(ZII)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Stats;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Z", "getEnabled", "I", "getLines", "getBoxes", "cobalt"})
    public static final class Stats {
        private final boolean enabled;
        private final int lines;
        private final int boxes;

        public Stats(boolean enabled, int lines, int boxes) {
            this.enabled = enabled;
            this.lines = lines;
            this.boxes = boxes;
        }

        public final boolean getEnabled() {
            return this.enabled;
        }

        public final int getLines() {
            return this.lines;
        }

        public final int getBoxes() {
            return this.boxes;
        }

        public final boolean component1() {
            return this.enabled;
        }

        public final int component2() {
            return this.lines;
        }

        public final int component3() {
            return this.boxes;
        }

        @NotNull
        public final Stats copy(boolean enabled, int lines, int boxes) {
            return new Stats(enabled, lines, boxes);
        }

        public static /* synthetic */ Stats copy$default(Stats stats, boolean bl, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                bl = stats.enabled;
            }
            if ((n3 & 2) != 0) {
                n = stats.lines;
            }
            if ((n3 & 4) != 0) {
                n2 = stats.boxes;
            }
            return stats.copy(bl, n, n2);
        }

        @NotNull
        public String toString() {
            return "Stats(enabled=" + this.enabled + ", lines=" + this.lines + ", boxes=" + this.boxes + ")";
        }

        public int hashCode() {
            int result = Boolean.hashCode(this.enabled);
            result = result * 31 + Integer.hashCode(this.lines);
            result = result * 31 + Integer.hashCode(this.boxes);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Stats)) {
                return false;
            }
            Stats stats = (Stats)other;
            if (this.enabled != stats.enabled) {
                return false;
            }
            if (this.lines != stats.lines) {
                return false;
            }
            return this.boxes == stats.boxes;
        }
    }
}

