/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.screen;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1041;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.UIScreen;
import org.cobalt.internal.ui.animation.Animation;
import org.cobalt.internal.ui.animation.BounceAnimation;
import org.cobalt.internal.ui.components.tooltips.TooltipManager;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.panel.panels.UIAddonList;
import org.cobalt.internal.ui.panel.panels.UISidebar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\r\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000bH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J'\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0017\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u0016H\u0016\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u001aH\u0016\u00a2\u0006\u0004\b\u001b\u0010\u001cJ/\u0010!\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u00112\u0006\u0010 \u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b!\u0010\"J\u000f\u0010#\u001a\u00020\u0006H\u0014\u00a2\u0006\u0004\b#\u0010\u0003J\u000f\u0010$\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b$\u0010\u0003J\u0015\u0010'\u001a\u00020%2\u0006\u0010&\u001a\u00020%\u00a2\u0006\u0004\b'\u0010(J\u0015\u0010)\u001a\u00020\u00062\u0006\u0010&\u001a\u00020%\u00a2\u0006\u0004\b)\u0010*J\r\u0010+\u001a\u00020\u0006\u00a2\u0006\u0004\b+\u0010\u0003J\r\u0010,\u001a\u00020%\u00a2\u0006\u0004\b,\u0010-R\u0014\u0010/\u001a\u00020.8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b/\u00100R\u0016\u00101\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b1\u00102R\u0014\u00104\u001a\u0002038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b4\u00105R\u0016\u00106\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0018\u00108\u001a\u0004\u0018\u00010%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b8\u00107R\u0014\u0010:\u001a\u0002098\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b:\u0010;R\u0014\u0010<\u001a\u0002098\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b<\u0010;R\u0014\u0010=\u001a\u0002098\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b=\u0010;\u00a8\u0006>"}, d2={"Lorg/cobalt/internal/ui/screen/UIConfig;", "Lorg/cobalt/internal/ui/UIScreen;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "Lnet/minecraft/class_11909;", "click", "", "doubled", "mouseClicked", "(Lnet/minecraft/class_11909;Z)Z", "mouseReleased", "(Lnet/minecraft/class_11909;)Z", "", "offsetX", "offsetY", "mouseDragged", "(Lnet/minecraft/class_11909;DD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "mouseX", "mouseY", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DDDD)Z", "init", "onClose", "Lorg/cobalt/internal/ui/panel/UIPanel;", "panel", "swapBodyPanel", "(Lorg/cobalt/internal/ui/panel/UIPanel;)Lorg/cobalt/internal/ui/panel/UIPanel;", "setAuxPanel", "(Lorg/cobalt/internal/ui/panel/UIPanel;)V", "clearAuxPanel", "getBodyPanel", "()Lorg/cobalt/internal/ui/panel/UIPanel;", "Lorg/cobalt/internal/ui/animation/BounceAnimation;", "openAnim", "Lorg/cobalt/internal/ui/animation/BounceAnimation;", "wasClosed", "Z", "Lorg/cobalt/internal/ui/panel/panels/UISidebar;", "sidebar", "Lorg/cobalt/internal/ui/panel/panels/UISidebar;", "body", "Lorg/cobalt/internal/ui/panel/UIPanel;", "auxPanel", "", "SCREEN_PADDING", "F", "BODY_GAP", "AUX_GAP", "cobalt"})
public final class UIConfig
extends UIScreen {
    @NotNull
    public static final UIConfig INSTANCE = new UIConfig();
    @NotNull
    private static final BounceAnimation openAnim = new BounceAnimation(400L);
    private static boolean wasClosed = true;
    @NotNull
    private static final UISidebar sidebar = new UISidebar();
    @NotNull
    private static UIPanel body = new UIAddonList();
    @Nullable
    private static UIPanel auxPanel;
    private static final float SCREEN_PADDING = 20.0f;
    private static final float BODY_GAP = 10.0f;
    private static final float AUX_GAP = 10.0f;

    private UIConfig() {
    }

    @SubscribeEvent
    public final void onRender(@NotNull NvgEvent event) {
        UIComponent uIComponent;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!Intrinsics.areEqual((Object)this.getMc().field_1755, (Object)((Object)this))) {
            return;
        }
        class_1041 class_10412 = this.getMc().method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float width = window.method_4480();
        float height = window.method_4507();
        NVGRenderer.INSTANCE.beginFrame(width, height);
        if (openAnim.isAnimating()) {
            float scale = ((Number)Animation.get$default(openAnim, Float.valueOf(0.0f), Float.valueOf(1.0f), false, 4, null)).floatValue();
            float cx = width / 2.0f;
            float cy = height / 2.0f;
            NVGRenderer.translate(cx, cy);
            NVGRenderer.scale(scale, scale);
            NVGRenderer.translate(-cx, -cy);
        }
        UIPanel aux = auxPanel;
        float contentWidth = sidebar.getWidth() + 10.0f + body.getWidth() + (aux != null ? 10.0f + aux.getWidth() : 0.0f);
        float f = sidebar.getHeight();
        float f2 = body.getHeight();
        UIPanel uIPanel = aux;
        float f3 = uIPanel != null ? uIPanel.getHeight() : 0.0f;
        float contentHeight = Math.max(f, Math.max(f2, f3));
        float originX = RangesKt.coerceAtLeast((float)((width - contentWidth) / 2.0f), (float)20.0f);
        float originY = RangesKt.coerceAtLeast((float)((height - contentHeight) / 2.0f), (float)20.0f);
        sidebar.updateBounds(originX, originY).render();
        body.updateBounds(originX + sidebar.getWidth() + 10.0f, originY).render();
        if (aux != null && (uIComponent = aux.updateBounds(originX + sidebar.getWidth() + 10.0f + body.getWidth() + 10.0f, originY)) != null) {
            uIComponent.render();
        }
        TooltipManager.INSTANCE.renderAll();
        NVGRenderer.INSTANCE.endFrame();
    }

    public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        UIPanel uIPanel = auxPanel;
        return (uIPanel != null ? uIPanel.mouseClicked(click.method_74245()) : false) || body.mouseClicked(click.method_74245()) || sidebar.mouseClicked(click.method_74245()) || super.method_25402(click, doubled);
    }

    public boolean method_25406(@NotNull class_11909 click) {
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        UIPanel uIPanel = auxPanel;
        return (uIPanel != null ? uIPanel.mouseReleased(click.method_74245()) : false) || body.mouseReleased(click.method_74245()) || super.method_25406(click);
    }

    public boolean method_25403(@NotNull class_11909 click, double offsetX, double offsetY) {
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        UIPanel uIPanel = auxPanel;
        return (uIPanel != null ? uIPanel.mouseDragged(click.method_74245(), offsetX, offsetY) : false) || body.mouseDragged(click.method_74245(), offsetX, offsetY) || super.method_25403(click, offsetX, offsetY);
    }

    public boolean method_25400(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return body.charTyped(input) || super.method_25400(input);
    }

    public boolean method_25404(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return body.keyPressed(input) || super.method_25404(input);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        UIPanel uIPanel = auxPanel;
        return (uIPanel != null ? uIPanel.mouseScrolled(horizontalAmount, verticalAmount) : false) || body.mouseScrolled(horizontalAmount, verticalAmount) || super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    protected void method_25426() {
        if (wasClosed) {
            openAnim.start();
            wasClosed = false;
        }
        super.method_25426();
    }

    public void method_25419() {
        Config.INSTANCE.saveModulesConfig();
        wasClosed = true;
        super.method_25419();
    }

    @NotNull
    public final UIPanel swapBodyPanel(@NotNull UIPanel panel) {
        Intrinsics.checkNotNullParameter((Object)panel, (String)"panel");
        UIPanel previous = body;
        body = panel;
        auxPanel = null;
        return previous;
    }

    public final void setAuxPanel(@NotNull UIPanel panel) {
        Intrinsics.checkNotNullParameter((Object)panel, (String)"panel");
        auxPanel = panel;
    }

    public final void clearAuxPanel() {
        auxPanel = null;
    }

    @NotNull
    public final UIPanel getBodyPanel() {
        return body;
    }

    static {
        EventBus.register((Object)INSTANCE);
    }
}

