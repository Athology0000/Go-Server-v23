/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.visual;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_310;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.bridge.module.IDarkModeShader;
import org.cobalt.render.DarkModeRenderer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\u000b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\r\u0010\u0004J\u000f\u0010\u000f\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0015\u001a\u00020\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0018\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u0019R\u0014\u0010\u001e\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0019R\u0014\u0010\u001f\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0019R\u0014\u0010 \u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010\u0019R\u0014\u0010!\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u0019R\u0016\u0010\"\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010#\u00a8\u0006$"}, d2={"Lorg/cobalt/internal/visual/DarkModeModule;", "Lorg/cobalt/api/module/Module;", "Lorg/cobalt/bridge/module/IDarkModeShader;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "updateRenderer", "", "isEnabled", "()Z", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "tintColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "intensity", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "blendMode", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "vignetteStrength", "saturation", "contrast", "chromaticAberration", "brightness", "wasEnabled", "Z", "cobalt"})
public final class DarkModeModule
extends Module
implements IDarkModeShader {
    @NotNull
    public static final DarkModeModule INSTANCE = new DarkModeModule();
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Color tint overlay (works with Fullbright).", false);
    @NotNull
    private static final ColorSetting tintColor = new ColorSetting("Tint Color", "Overlay color (RGBA).", -13623104);
    @NotNull
    private static final SliderSetting intensity = new SliderSetting("Intensity", "How strong the tint effect is (0-100%).", 0.6, 0.0, 1.0, 0.0, 32, null);
    @NotNull
    private static final ModeSetting blendMode;
    @NotNull
    private static final SliderSetting vignetteStrength;
    @NotNull
    private static final SliderSetting saturation;
    @NotNull
    private static final SliderSetting contrast;
    @NotNull
    private static final SliderSetting chromaticAberration;
    @NotNull
    private static final SliderSetting brightness;
    private static boolean wasEnabled;

    private DarkModeModule() {
        super("Dark Mode");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            wasEnabled = false;
            return;
        }
        if (!wasEnabled) {
            this.updateRenderer();
            wasEnabled = true;
        }
        this.updateRenderer();
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        if (mc.field_1687 == null || !mc.field_1690.method_31044().method_31034()) {
            return;
        }
        DarkModeRenderer.renderDarkModeOverlay();
    }

    private final void updateRenderer() {
        int argb = tintColor.getValue();
        float r = (float)(argb >>> 16 & 0xFF) / 255.0f;
        float g = (float)(argb >>> 8 & 0xFF) / 255.0f;
        float b = (float)(argb & 0xFF) / 255.0f;
        DarkModeRenderer.setTintColor(r, g, b);
        DarkModeRenderer.setIntensity((float)((Number)intensity.getValue()).doubleValue());
        DarkModeRenderer.setBlendMode(((Number)blendMode.getValue()).intValue());
        DarkModeRenderer.setVignetteStrength((float)((Number)vignetteStrength.getValue()).doubleValue());
        DarkModeRenderer.setSaturation((float)((Number)saturation.getValue()).doubleValue());
        DarkModeRenderer.setContrast((float)((Number)contrast.getValue()).doubleValue());
        DarkModeRenderer.setChromaticAberration((float)((Number)chromaticAberration.getValue()).doubleValue());
        DarkModeRenderer.setBrightness((float)((Number)brightness.getValue()).doubleValue());
    }

    @Override
    public boolean isEnabled() {
        return (Boolean)enabled.getValue();
    }

    static {
        Object[] objectArray = new String[]{"Multiply", "Overlay", "Additive", "Screen"};
        blendMode = new ModeSetting("Blend Mode", "How the tint is blended with the world.", 0, (String[])objectArray);
        vignetteStrength = new SliderSetting("Vignette", "Darkens screen edges (0-100%).", 0.3, 0.0, 1.0, 0.0, 32, null);
        saturation = new SliderSetting("Saturation", "Color saturation (0-200%).", 1.0, 0.0, 2.0, 0.0, 32, null);
        contrast = new SliderSetting("Contrast", "Image contrast (0-200%).", 1.1, 0.0, 2.0, 0.0, 32, null);
        chromaticAberration = new SliderSetting("Chromatic Aberration", "RGB color shift at edges (0-1%).", 0.002, 0.0, 0.01, 0.0, 32, null);
        brightness = new SliderSetting("Brightness", "Brightness multiplier (10-500%).", 1.5, 0.1, 5.0, 0.0, 32, null);
        objectArray = new Setting[]{enabled, tintColor, intensity, blendMode, vignetteStrength, saturation, contrast, chromaticAberration, brightness};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }
}

