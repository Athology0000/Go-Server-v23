/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3726
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.visual;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3726;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0012R\u0014\u0010\u0015\u001a\u00020\u00148\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0018\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001cR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0014\u0010!\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u0019R\u0014\u0010#\u001a\u00020\"8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010$\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/visual/BlockOutlineModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "", "argb", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "toOverlayColor", "(I)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "resolveColor", "()I", "baseArgb", "rainbowColor", "(I)I", "duttColor", "", "TAG", "Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "blurRadius", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "threshold", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "outlineColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "outlineEnabled", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "colorMode", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "cobalt"})
public final class BlockOutlineModule
extends Module {
    @NotNull
    public static final BlockOutlineModule INSTANCE = new BlockOutlineModule();
    @NotNull
    private static final String TAG = "block-outline";
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Show a custom outline on the targeted block.", false);
    @NotNull
    private static final SliderSetting blurRadius = new SliderSetting("Blur Radius", "Outline thickness/blur (1-16).", 4.0, 1.0, 16.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting threshold = new SliderSetting("Threshold", "Edge falloff (0 = soft, 1 = hard).", 0.3, 0.0, 1.0, 0.0, 32, null);
    @NotNull
    private static final ColorSetting outlineColor = new ColorSetting("Outline Color", "Block outline color (RGBA).", -1);
    @NotNull
    private static final CheckboxSetting outlineEnabled = new CheckboxSetting("Outline", "Render the outline around the block.", true);
    @NotNull
    private static final ModeSetting colorMode;

    private BlockOutlineModule() {
        super("Block Outline");
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_638 class_6382 = mc.field_1687;
        if (class_6382 == null) {
            BlockOutlineModule $this$onRender_u24lambda_u240 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_239 hit = mc.field_1765;
        if (!(hit instanceof class_3965) || ((class_3965)hit).method_17783() != class_239.class_240.field_1332) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_2338 class_23382 = ((class_3965)hit).method_17777();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
        class_2338 blockPos = class_23382;
        class_2680 class_26802 = level2.method_8320(blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 blockState = class_26802;
        if (blockState.method_26215() || !level2.method_8621().method_11952(blockPos)) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_746 class_7462 = mc.field_1724;
        if (class_7462 == null) {
            BlockOutlineModule $this$onRender_u24lambda_u241 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_746 player = class_7462;
        class_265 class_2652 = blockState.method_26172((class_1922)level2, blockPos, class_3726.method_16195((class_1297)((class_1297)player)));
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getShape(...)");
        class_265 shape = class_2652;
        if (shape.method_1110()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
        class_238 class_2383 = shape.method_1107();
        Intrinsics.checkNotNullExpressionValue((Object)class_2383, (String)"bounds(...)");
        class_238 bounds = class_2383;
        double pad = 0.002;
        double minX = (double)blockPos.method_10263() + bounds.field_1323 - pad;
        double minY = (double)blockPos.method_10264() + bounds.field_1322 - pad;
        double minZ = (double)blockPos.method_10260() + bounds.field_1321 - pad;
        double maxX = (double)blockPos.method_10263() + bounds.field_1320 + pad;
        double maxY = (double)blockPos.method_10264() + bounds.field_1325 + pad;
        double maxZ = (double)blockPos.method_10260() + bounds.field_1324 + pad;
        int argb = this.resolveColor();
        OverlayRenderEngine.Color baseColor = this.toOverlayColor(argb);
        float radius = RangesKt.coerceIn((float)((float)((Number)blurRadius.getValue()).doubleValue()), (float)1.0f, (float)16.0f);
        int minAlpha = RangesKt.coerceIn((int)MathKt.roundToInt((double)((double)baseColor.getA() * ((Number)threshold.getValue()).doubleValue())), (int)0, (int)255);
        if (!((Boolean)outlineEnabled.getValue()).booleanValue()) {
            OverlayRenderEngine.INSTANCE.render(event.getContext());
            return;
        }
        int passes = Math.max(1, MathKt.roundToInt((float)(radius / 3.5f)));
        float baseWidth = 1.2f;
        for (int i = 0; i < passes; ++i) {
            float t = passes == 1 ? 0.0f : (float)i / (float)(passes - 1);
            float width = baseWidth + radius * (0.35f + 0.65f * t);
            int alpha = RangesKt.coerceIn((int)MathKt.roundToInt((float)((float)baseColor.getA() + (float)(minAlpha - baseColor.getA()) * t)), (int)0, (int)255);
            OverlayRenderEngine.Color color = baseColor.withAlpha(alpha);
            OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, minX, minY, minZ, maxX, maxY, maxZ, null, color, width, 2, TAG, false, 4096, null);
        }
        OverlayRenderEngine.INSTANCE.render(event.getContext());
    }

    private final OverlayRenderEngine.Color toOverlayColor(int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        return new OverlayRenderEngine.Color(r, g, b, a);
    }

    private final int resolveColor() {
        return switch (((Number)colorMode.getValue()).intValue()) {
            case 1 -> this.rainbowColor(outlineColor.getValue());
            case 2 -> this.duttColor(outlineColor.getValue());
            default -> outlineColor.getValue();
        };
    }

    private final int rainbowColor(int baseArgb) {
        float time;
        int alpha = baseArgb >>> 24 & 0xFF;
        float hue = time = (float)(System.currentTimeMillis() % 4000L) / 4000.0f;
        int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private final int duttColor(int baseArgb) {
        int alpha = baseArgb >>> 24 & 0xFF;
        float t = (float)(System.currentTimeMillis() % 5000L) / 5000.0f;
        float blend = 0.5f - 0.5f * (float)Math.cos((double)t * (Math.PI * 2));
        int pink = 16739021;
        int cyan = 3007231;
        int r = (int)((float)(pink >>> 16 & 0xFF) * (1.0f - blend) + (float)(cyan >>> 16 & 0xFF) * blend);
        int g = (int)((float)(pink >>> 8 & 0xFF) * (1.0f - blend) + (float)(cyan >>> 8 & 0xFF) * blend);
        int b = (int)((float)(pink & 0xFF) * (1.0f - blend) + (float)(cyan & 0xFF) * blend);
        return alpha << 24 | r << 16 | g << 8 | b;
    }

    static {
        Object[] objectArray = new String[]{"Static", "Rainbow", "Dutt"};
        colorMode = new ModeSetting("Color Mode", "Color mode for the outline.", 0, (String[])objectArray);
        objectArray = new Setting[]{enabled, blurRadius, threshold, outlineColor, outlineEnabled, colorMode};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }
}

