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
 *  kotlin.math.MathKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud.modules;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.class_1041;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.GuiRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bR\u0014\u0010\n\u001a\u00020\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u000eR\u0014\u0010\u0011\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u000eR\u0014\u0010\u0012\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u000eR\u0014\u0010\u0013\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u000eR\u0014\u0010\u0014\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0016\u001a\u00020\u00158\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u00158\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00158\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0017R\u0016\u0010\u001b\u001a\u00020\u001a8\u0002@\u0002X\u0082.\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u00158\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u0017R\u0014\u0010\u001e\u001a\u00020\u00158\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0017R\u0017\u0010 \u001a\u00020\u001f8\u0006\u00a2\u0006\f\n\u0004\b \u0010!\u001a\u0004\b\"\u0010#\u00a8\u0006$"}, d2={"Lorg/cobalt/api/hud/modules/InventoryHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "event", "", "onGuiRender", "(Lorg/cobalt/api/event/impl/render/GuiRenderEvent;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "slotSize", "F", "slotGap", "padding", "baseScale", "borderRadius", "borderThickness", "itemOffset", "", "outlineStart", "I", "outlineEnd", "panelColor", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "backgroundSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "ROWS", "COLS", "Lorg/cobalt/api/hud/HudElement;", "inventoryHud", "Lorg/cobalt/api/hud/HudElement;", "getInventoryHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
public final class InventoryHudModule
extends Module {
    @NotNull
    private final class_310 mc;
    private final float slotSize;
    private final float slotGap;
    private final float padding;
    private final float baseScale;
    private final float borderRadius;
    private final float borderThickness;
    private final float itemOffset;
    private final int outlineStart;
    private final int outlineEnd;
    private final int panelColor;
    private CheckboxSetting backgroundSetting;
    private final int ROWS;
    private final int COLS;
    @NotNull
    private final HudElement inventoryHud;

    public InventoryHudModule() {
        super("Inventory HUD");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
        this.slotSize = 20.0f;
        this.slotGap = 4.0f;
        this.padding = 8.0f;
        this.baseScale = 1.46f;
        this.borderRadius = 9.0f;
        this.borderThickness = 1.5f;
        this.itemOffset = 2.0f;
        this.outlineStart = -13769985;
        this.outlineEnd = -38195;
        this.panelColor = 0x50101010;
        this.ROWS = 3;
        this.COLS = 9;
        this.inventoryHud = HudModuleDSLKt.hudElement(this, "inventory-hud", "Inventory HUD", "Displays your inventory items", (Function1<? super HudElementBuilder, Unit>)((Function1)arg_0 -> InventoryHudModule.inventoryHud$lambda$0(this, arg_0)));
        EventBus.register(this);
    }

    @NotNull
    public final HudElement getInventoryHud() {
        return this.inventoryHud;
    }

    @SubscribeEvent
    public final void onGuiRender(@NotNull GuiRenderEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!this.inventoryHud.getEnabled()) {
            return;
        }
        if (this.mc.field_1755 != null && !HudModuleManager.INSTANCE.isEditorOpen()) {
            return;
        }
        class_746 class_7462 = this.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        class_1041 class_10412 = this.mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float guiScale = window.method_4495();
        if (guiScale <= 0.0f) {
            return;
        }
        Pair<Float, Float> pair = this.inventoryHud.getScreenPosition(window.method_4480(), window.method_4507());
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        float originX = sx / guiScale;
        float originY = sy / guiScale;
        float renderScale = this.inventoryHud.getScale() / guiScale;
        float ss = this.slotSize * this.baseScale;
        float sg = this.slotGap * this.baseScale;
        float p = this.padding * this.baseScale;
        class_332 graphics = event.getGraphics();
        for (int i = 0; i < 27; ++i) {
            class_1799 stack;
            int inventoryIndex = i + 9;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(inventoryIndex), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            int row = i / this.COLS;
            int col = i % this.COLS;
            if (row >= this.ROWS) continue;
            float slotX = p + (float)col * (ss + sg) + this.itemOffset * this.baseScale;
            float slotY = p + (float)row * (ss + sg) + this.itemOffset * this.baseScale;
            int drawX = MathKt.roundToInt((float)(originX + slotX * renderScale));
            int drawY = MathKt.roundToInt((float)(originY + slotY * renderScale));
            graphics.method_51427(stack, drawX, drawY);
            graphics.method_51431(this.mc.field_1772, stack, drawX, drawY);
        }
    }

    private static final float inventoryHud$lambda$0$0(InventoryHudModule this$0) {
        float ss = this$0.slotSize * this$0.baseScale;
        float sg = this$0.slotGap * this$0.baseScale;
        float p = this$0.padding * this$0.baseScale;
        return p * (float)2 + (float)this$0.COLS * ss + (float)(this$0.COLS - 1) * sg;
    }

    private static final float inventoryHud$lambda$0$1(InventoryHudModule this$0) {
        float ss = this$0.slotSize * this$0.baseScale;
        float sg = this$0.slotGap * this$0.baseScale;
        float p = this$0.padding * this$0.baseScale;
        return p * (float)2 + (float)this$0.ROWS * ss + (float)(this$0.ROWS - 1) * sg;
    }

    private static final Unit inventoryHud$lambda$0$2(InventoryHudModule this$0, CheckboxSetting $background, float f, float f2, float f3) {
        float ss = this$0.slotSize * this$0.baseScale;
        float sg = this$0.slotGap * this$0.baseScale;
        float p = this$0.padding * this$0.baseScale;
        float br = this$0.borderRadius * this$0.baseScale;
        float bt = this$0.borderThickness * this$0.baseScale;
        float totalW = p * (float)2 + (float)this$0.COLS * ss + (float)(this$0.COLS - 1) * sg;
        float totalH = p * (float)2 + (float)this$0.ROWS * ss + (float)(this$0.ROWS - 1) * sg;
        if (((Boolean)$background.getValue()).booleanValue()) {
            NVGRenderer.rect(0.0f, 0.0f, totalW, totalH, this$0.panelColor, br);
        }
        float angle = (float)(System.currentTimeMillis() % 12000L) / 12000.0f * ((float)Math.PI * 2);
        float shiftX = (float)Math.cos(angle) * (totalW * 0.45f);
        float shiftY = (float)Math.sin(angle) * (totalH * 0.45f);
        NVGRenderer.hollowGradientRectShifted(0.0f, 0.0f, totalW, totalH, bt, this$0.outlineStart, this$0.outlineEnd, Gradient.LeftToRight, br, shiftX, shiftY);
        return Unit.INSTANCE;
    }

    private static final Unit inventoryHud$lambda$0(InventoryHudModule this$0, HudElementBuilder $this$hudElement) {
        CheckboxSetting background;
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setMinScale(1.0f);
        $this$hudElement.setAnchor(HudAnchor.BOTTOM_CENTER);
        $this$hudElement.setOffsetX(0.0f);
        $this$hudElement.setOffsetY(24.0f);
        this$0.backgroundSetting = background = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Background", "Show panel background", true));
        $this$hudElement.width((Function0<Float>)((Function0)() -> InventoryHudModule.inventoryHud$lambda$0$0(this$0)));
        $this$hudElement.height((Function0<Float>)((Function0)() -> InventoryHudModule.inventoryHud$lambda$0$1(this$0)));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> InventoryHudModule.inventoryHud$lambda$0$2(this$0, background, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }
}

