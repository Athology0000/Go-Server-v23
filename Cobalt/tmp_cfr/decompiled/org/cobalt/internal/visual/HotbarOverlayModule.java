/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1041
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.visual;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1041;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.GuiRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.qol.ItemLockingModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bR\u0014\u0010\n\u001a\u00020\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0011\u0010\u0010\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0013\u001a\u00020\u00128\u0002@\u0002X\u0082.\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0015\u001a\u00020\u00128\u0006\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0014\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006\u0018"}, d2={"Lorg/cobalt/internal/visual/HotbarOverlayModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "event", "", "onGui", "(Lorg/cobalt/api/event/impl/render/GuiRenderEvent;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "", "isEnabled", "()Z", "Lorg/cobalt/api/hud/HudElement;", "hudRef", "Lorg/cobalt/api/hud/HudElement;", "hotbarHud", "getHotbarHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
public final class HotbarOverlayModule
extends Module {
    @NotNull
    public static final HotbarOverlayModule INSTANCE = new HotbarOverlayModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    private static HudElement hudRef;
    @NotNull
    private static final HudElement hotbarHud;

    private HotbarOverlayModule() {
        super("Liquid Hotbar");
    }

    public final boolean isEnabled() {
        return (Boolean)enabledSetting.getValue();
    }

    @NotNull
    public final HudElement getHotbarHud() {
        return hotbarHud;
    }

    @SubscribeEvent
    public final void onGui(@NotNull GuiRenderEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!this.isEnabled() || HotbarOverlayModule.mc.field_1755 != null) {
            return;
        }
        class_746 class_7462 = HotbarOverlayModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float guiScale = window.method_4495();
        HudElement hudElement = hudRef;
        if (hudElement == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"hudRef");
            hudElement = null;
        }
        Pair<Float, Float> pair = hudElement.getScreenPosition(window.method_4480(), window.method_4507());
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        int gx = (int)(sx / guiScale);
        int gy = (int)(sy / guiScale);
        event.getGraphics().method_25294(gx, gy, gx + 182, gy + 22, 0x50101010);
        class_1799 class_17992 = player.method_6079();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getOffhandItem(...)");
        class_1799 offhand = class_17992;
        if (!offhand.method_7960()) {
            event.getGraphics().method_25294(gx - 29, gy, gx - 5, gy + 22, 0x50101010);
        }
        for (int slot = 0; slot < 9; ++slot) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)player.method_31548().method_5438(slot), (String)"getItem(...)");
            int slotX = gx + 1 + slot * 20;
            if (!stack.method_7960()) {
                event.getGraphics().method_51427(stack, slotX + 2, gy + 3);
                event.getGraphics().method_51431(HotbarOverlayModule.mc.field_1772, stack, slotX + 2, gy + 3);
            }
            ItemLockingModule.INSTANCE.renderHotbarSlotOverlay(event.getGraphics(), slot, slotX, gy + 1);
        }
        if (!offhand.method_7960()) {
            event.getGraphics().method_51427(offhand, gx - 29 + 4, gy + 3);
            event.getGraphics().method_51431(HotbarOverlayModule.mc.field_1772, offhand, gx - 29 + 4, gy + 3);
        }
    }

    private static final float hotbarHud$lambda$0$0() {
        return (float)mc.method_22683().method_4495() * 182.0f;
    }

    private static final float hotbarHud$lambda$0$1() {
        return (float)mc.method_22683().method_4495() * 22.0f;
    }

    private static final Unit hotbarHud$lambda$0$2(float f, float f2, float f3) {
        if (!INSTANCE.isEnabled()) {
            return Unit.INSTANCE;
        }
        float sc = mc.method_22683().method_4495();
        float bw = sc * 182.0f;
        float bh = sc * 22.0f;
        float rad = 4.0f * sc;
        class_746 class_7462 = HotbarOverlayModule.mc.field_1724;
        int sel = class_7462 != null && (class_7462 = class_7462.method_31548()) != null ? class_7462.method_67532() : 0;
        NVGRenderer.rect((1.0f + (float)sel * 20.0f) * sc, sc, 20.0f * sc, 20.0f * sc, 0x55000000, 4.0f * sc);
        float angle = (float)(System.currentTimeMillis() % 12000L) / 12000.0f * ((float)Math.PI * 2);
        float shiftX = (float)Math.cos(angle) * (bw * 0.45f);
        float shiftY = (float)Math.sin(angle) * (bh * 0.45f);
        NVGRenderer.hollowGradientRectShifted(0.0f, 0.0f, bw, bh, sc * 1.5f, -13769985, -38195, Gradient.LeftToRight, rad, shiftX, shiftY);
        class_746 class_7463 = HotbarOverlayModule.mc.field_1724;
        boolean bl = class_7463 != null && (class_7463 = class_7463.method_6079()) != null ? !class_7463.method_7960() : false;
        if (bl) {
            float ow = 24.0f * sc;
            float ox = -29.0f * sc;
            float oShiftX = (float)Math.cos(angle) * (ow * 0.45f);
            float oShiftY = (float)Math.sin(angle) * (bh * 0.45f);
            NVGRenderer.hollowGradientRectShifted(ox, 0.0f, ow, bh, sc * 1.5f, -13769985, -38195, Gradient.LeftToRight, rad, oShiftX, oShiftY);
        }
        return Unit.INSTANCE;
    }

    private static final Unit hotbarHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.BOTTOM_CENTER);
        $this$hudElement.setOffsetX(0.0f);
        $this$hudElement.setOffsetY(0.0f);
        $this$hudElement.setMinScale(1.0f);
        $this$hudElement.setMaxScale(1.0f);
        $this$hudElement.width((Function0<Float>)((Function0)HotbarOverlayModule::hotbarHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)HotbarOverlayModule::hotbarHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)HotbarOverlayModule::hotbarHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabledSetting = new CheckboxSetting("Enabled", "Replace the vanilla hotbar with a liquid glass panel.", true);
        hotbarHud = HudModuleDSLKt.hudElement(INSTANCE, "liquid-hotbar", "Liquid Hotbar", "Draggable liquid glass hotbar", (Function1<? super HudElementBuilder, Unit>)((Function1)HotbarOverlayModule::hotbarHud$lambda$0));
        Setting[] settingArray = new Setting[]{enabledSetting};
        INSTANCE.addSetting(settingArray);
        hudRef = hotbarHud;
        EventBus.register(INSTANCE);
    }
}

