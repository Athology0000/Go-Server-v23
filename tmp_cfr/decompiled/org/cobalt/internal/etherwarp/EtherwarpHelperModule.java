/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.etherwarp;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.internal.etherwarp.EtherwarpLogic;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u000f\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0012\u001a\u00020\u00118\u0006\u00a2\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0017\u001a\u00020\u00168\u0006\u00a2\u0006\f\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR\u0017\u0010\u001c\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010 \u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b \u0010\u001d\u001a\u0004\b!\u0010\u001fR\u0017\u0010#\u001a\u00020\"8\u0006\u00a2\u0006\f\n\u0004\b#\u0010$\u001a\u0004\b%\u0010&R\u0017\u0010'\u001a\u00020\"8\u0006\u00a2\u0006\f\n\u0004\b'\u0010$\u001a\u0004\b(\u0010&\u00a8\u0006)"}, d2={"Lorg/cobalt/internal/etherwarp/EtherwarpHelperModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "", "argb", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "toOverlayColor", "(I)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "", "TAG", "Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "renderMode", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "getRenderMode", "()Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "canWarpColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "getCanWarpColor", "()Lorg/cobalt/api/module/setting/impl/ColorSetting;", "cannotWarpColor", "getCannotWarpColor", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "outlineWidth", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getOutlineWidth", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "fillOpacity", "getFillOpacity", "cobalt"})
public final class EtherwarpHelperModule
extends Module {
    @NotNull
    public static final EtherwarpHelperModule INSTANCE = new EtherwarpHelperModule();
    @NotNull
    private static final String TAG = "etherwarp";
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Show etherwarp target block with ESP.", false);
    @NotNull
    private static final ModeSetting renderMode;
    @NotNull
    private static final ColorSetting canWarpColor;
    @NotNull
    private static final ColorSetting cannotWarpColor;
    @NotNull
    private static final SliderSetting outlineWidth;
    @NotNull
    private static final SliderSetting fillOpacity;

    private EtherwarpHelperModule() {
        super("Etherwarp");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final ModeSetting getRenderMode() {
        return renderMode;
    }

    @NotNull
    public final ColorSetting getCanWarpColor() {
        return canWarpColor;
    }

    @NotNull
    public final ColorSetting getCannotWarpColor() {
        return cannotWarpColor;
    }

    @NotNull
    public final SliderSetting getOutlineWidth() {
        return outlineWidth;
    }

    @NotNull
    public final SliderSetting getFillOpacity() {
        return fillOpacity;
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            EtherwarpHelperModule $this$onRender_u24lambda_u240 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        if (!EtherwarpLogic.INSTANCE.holdingEtherwarpItem()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        EtherwarpLogic.EtherPos result = EtherwarpLogic.INSTANCE.getEtherwarpResult();
        class_2338 class_23382 = result.getPos();
        if (class_23382 == null) {
            EtherwarpHelperModule $this$onRender_u24lambda_u241 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_2338 pos = class_23382;
        int baseColor = result.getSucceeded() ? canWarpColor.getValue().intValue() : cannotWarpColor.getValue().intValue();
        OverlayRenderEngine.Color outline = this.toOverlayColor(baseColor);
        OverlayRenderEngine.Color fill = outline.withAlpha(RangesKt.coerceIn((int)((int)(((Number)fillOpacity.getValue()).doubleValue() * 255.0)), (int)0, (int)255));
        double pad = 0.002;
        double minX = (double)pos.method_10263() - pad;
        double minY = (double)pos.method_10264() - pad;
        double minZ = (double)pos.method_10260() - pad;
        double maxX = (double)pos.method_10263() + 1.0 + pad;
        double maxY = (double)pos.method_10264() + 1.0 + pad;
        double maxZ = (double)pos.method_10260() + 1.0 + pad;
        float lineWidth = (float)((Number)outlineWidth.getValue()).doubleValue();
        switch (((Number)renderMode.getValue()).intValue()) {
            case 0: {
                OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, minX, minY, minZ, maxX, maxY, maxZ, null, outline, lineWidth, 2, TAG, false, 4096, null);
                break;
            }
            case 1: {
                OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, minX, minY, minZ, maxX, maxY, maxZ, fill, null, lineWidth, 2, TAG, false, 4096, null);
                break;
            }
            default: {
                OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, minX, minY, minZ, maxX, maxY, maxZ, fill, outline, lineWidth, 2, TAG, false, 4096, null);
            }
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

    static {
        Object[] objectArray = new String[]{"Outline", "Filled", "Outline + Filled"};
        renderMode = new ModeSetting("Render Mode", "ESP rendering style.", 0, (String[])objectArray);
        canWarpColor = new ColorSetting("Can Warp Color", "Color when block is warpable.", -16711936);
        cannotWarpColor = new ColorSetting("Cannot Warp Color", "Color when block is not warpable.", -65536);
        outlineWidth = new SliderSetting("Outline Width", "Thickness of outline.", 2.2, 0.5, 8.0, 0.0, 32, null);
        fillOpacity = new SliderSetting("Fill Opacity", "Opacity of filled area.", 0.35, 0.0, 1.0, 0.0, 32, null);
        objectArray = new Setting[6];
        objectArray[0] = enabled;
        objectArray[1] = renderMode;
        objectArray[2] = canWarpColor;
        objectArray[3] = cannotWarpColor;
        objectArray[4] = outlineWidth;
        objectArray[5] = fillOpacity;
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }
}

