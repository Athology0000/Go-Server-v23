/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1041;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.ui.UIScreen;
import org.cobalt.internal.ui.hud.HudSettingsPopup;
import org.cobalt.internal.ui.hud.SnapHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 S2\u00020\u0001:\u0001SB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\f\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u001f\u0010\u000e\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000e\u0010\rJ\u001f\u0010\u000f\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000f\u0010\rJ\u001f\u0010\u0010\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0010\u0010\rJ\u001f\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b\u0015\u0010\u0016J'\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ/\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u001f\u0010 J/\u0010!\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b!\u0010 J/\u0010\"\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\"\u0010 J/\u0010#\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b#\u0010 J\u0017\u0010$\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b$\u0010%J'\u0010)\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010'\u001a\u00020&2\u0006\u0010(\u001a\u00020&H\u0016\u00a2\u0006\u0004\b)\u0010*J/\u0010/\u001a\u00020\u00132\u0006\u0010+\u001a\u00020&2\u0006\u0010,\u001a\u00020&2\u0006\u0010-\u001a\u00020&2\u0006\u0010.\u001a\u00020&H\u0016\u00a2\u0006\u0004\b/\u00100J\u0017\u00103\u001a\u00020\u00132\u0006\u00102\u001a\u000201H\u0016\u00a2\u0006\u0004\b3\u00104J\u0017\u00106\u001a\u00020\u00132\u0006\u00102\u001a\u000205H\u0016\u00a2\u0006\u0004\b6\u00107J\u000f\u00108\u001a\u00020\u0006H\u0014\u00a2\u0006\u0004\b8\u0010\u0003J\u000f\u00109\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b9\u0010\u0003J1\u0010;\u001a\u0004\u0018\u00010:2\u0006\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b;\u0010<J7\u0010@\u001a\u00020\u00062\u0006\u0010=\u001a\u00020:2\u0006\u0010>\u001a\u00020\t2\u0006\u0010?\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b@\u0010AR\u0018\u0010B\u001a\u0004\u0018\u00010:8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bB\u0010CR\u0016\u0010D\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010ER\u0016\u0010F\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0016\u0010H\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bH\u0010GR\u0016\u0010I\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bI\u0010ER\u0016\u0010J\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bJ\u0010GR\u0016\u0010K\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bK\u0010GR\u0016\u0010L\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bL\u0010GR\u0014\u0010N\u001a\u00020M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010OR\u0014\u0010Q\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010R\u00a8\u0006T"}, d2={"Lorg/cobalt/internal/ui/screen/UIHudEditor;", "Lorg/cobalt/internal/ui/UIScreen;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "", "width", "height", "renderGrid", "(FF)V", "renderElementBounds", "renderGuides", "renderInstructions", "Lnet/minecraft/class_11909;", "click", "", "doubled", "mouseClicked", "(Lnet/minecraft/class_11909;Z)Z", "mx", "my", "", "button", "handleSettingsPopupClick", "(FFI)Z", "screenWidth", "screenHeight", "handleRightClick", "(FFFF)Z", "handleLeftClick", "tryStartResizing", "tryStartDragging", "mouseReleased", "(Lnet/minecraft/class_11909;)Z", "", "offsetX", "offsetY", "mouseDragged", "(Lnet/minecraft/class_11909;DD)Z", "mouseX", "mouseY", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DDDD)Z", "Lnet/minecraft/class_11908;", "input", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lnet/minecraft/class_11905;", "charTyped", "(Lnet/minecraft/class_11905;)Z", "init", "onClose", "Lorg/cobalt/api/hud/HudElement;", "findElementUnderCursor", "(FFFF)Lorg/cobalt/api/hud/HudElement;", "element", "newScreenX", "newScreenY", "updateElementPosition", "(Lorg/cobalt/api/hud/HudElement;FFFF)V", "selectedElement", "Lorg/cobalt/api/hud/HudElement;", "dragging", "Z", "dragOffsetX", "F", "dragOffsetY", "resizing", "initialMouseX", "initialMouseY", "initialWidth", "Lorg/cobalt/internal/ui/hud/SnapHelper;", "snapHelper", "Lorg/cobalt/internal/ui/hud/SnapHelper;", "Lorg/cobalt/internal/ui/hud/HudSettingsPopup;", "settingsPopup", "Lorg/cobalt/internal/ui/hud/HudSettingsPopup;", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIHudEditor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIHudEditor.kt\norg/cobalt/internal/ui/screen/UIHudEditor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,378:1\n1915#2,2:379\n1915#2,2:381\n777#2:387\n873#2,2:388\n1586#2:390\n1661#2,3:391\n777#2:396\n873#2,2:397\n1586#2:399\n1661#2,3:400\n546#2,6:403\n6#3:383\n9#3:384\n6#3:385\n9#3:386\n6#3:394\n9#3:395\n*S KotlinDebug\n*F\n+ 1 UIHudEditor.kt\norg/cobalt/internal/ui/screen/UIHudEditor\n*L\n80#1:379,2\n100#1:381,2\n207#1:387\n207#1:388,2\n208#1:390\n208#1:391,3\n258#1:396\n258#1:397,2\n259#1:399\n259#1:400,3\n316#1:403,6\n125#1:383\n126#1:384\n201#1:385\n202#1:386\n240#1:394\n253#1:395\n*E\n"})
public final class UIHudEditor
extends UIScreen {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private HudElement selectedElement;
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean resizing;
    private float initialMouseX;
    private float initialMouseY;
    private float initialWidth;
    @NotNull
    private final SnapHelper snapHelper = new SnapHelper(0.0f, 0.0f, 3, null);
    @NotNull
    private final HudSettingsPopup settingsPopup = new HudSettingsPopup();
    private static final float RESIZE_HANDLE_SIZE = 8.0f;

    public UIHudEditor() {
        EventBus.register((Object)this);
    }

    @SubscribeEvent
    public final void onRender(@NotNull NvgEvent event) {
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
        NVGRenderer.rect(0.0f, 0.0f, width, height, new Color(0, 0, 0, 128).getRGB());
        this.renderGrid(width, height);
        this.renderElementBounds(width, height);
        this.renderGuides(width, height);
        this.settingsPopup.render();
        this.renderInstructions(width, height);
        NVGRenderer.INSTANCE.endFrame();
    }

    private final void renderGrid(float width, float height) {
        float gridSize = 20.0f;
        int gridColor = new Color(255, 255, 255, 20).getRGB();
        for (float x = 0.0f; x <= width; x += gridSize) {
            NVGRenderer.line(x, 0.0f, x, height, 1.0f, gridColor);
        }
        for (float y = 0.0f; y <= height; y += gridSize) {
            NVGRenderer.line(0.0f, y, width, y, 1.0f, gridColor);
        }
    }

    private final void renderElementBounds(float width, float height) {
        Iterable $this$forEach$iv = HudModuleManager.INSTANCE.getElements();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            HudElement element = (HudElement)element$iv;
            boolean bl = false;
            Pair<Float, Float> pair = element.getScreenPosition(width, height);
            float sx = ((Number)pair.component1()).floatValue();
            float sy = ((Number)pair.component2()).floatValue();
            float w = element.getScaledWidth();
            float h = element.getScaledHeight();
            boolean isSelected = Intrinsics.areEqual((Object)element, (Object)this.selectedElement);
            int borderColor = isSelected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBorder();
            float borderThickness = isSelected ? 2.0f : 1.0f;
            NVGRenderer.hollowRect(sx, sy, w, h, borderThickness, borderColor, 4.0f);
            NVGRenderer.text$default(element.getName(), sx, sy + h + 6.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            if (!Intrinsics.areEqual((Object)element, (Object)this.selectedElement)) continue;
            float handleX = sx + w - 8.0f;
            float handleY = sy + h - 8.0f;
            NVGRenderer.rect(handleX, handleY, 8.0f, 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), 2.0f);
        }
    }

    private final void renderGuides(float width, float height) {
        if (!this.dragging) {
            return;
        }
        Iterable $this$forEach$iv = this.snapHelper.getActiveGuides();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            SnapHelper.GuideLine guide = (SnapHelper.GuideLine)element$iv;
            boolean bl = false;
            if (guide.isVertical()) {
                NVGRenderer.line(guide.getPosition(), 0.0f, guide.getPosition(), height, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getAccent());
                continue;
            }
            NVGRenderer.line(0.0f, guide.getPosition(), width, guide.getPosition(), 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getAccent());
        }
    }

    private final void renderInstructions(float width, float height) {
        String text = "Left-click to select, drag to move | Right-click for settings | Drag corner to resize | ESC to save and exit";
        float textWidth = NVGRenderer.textWidth$default(text, 12.0f, null, 4, null);
        float padding = 14.0f;
        float boxWidth = textWidth + padding * 2.0f;
        float boxHeight = 26.0f;
        float x = width / 2.0f - boxWidth / 2.0f;
        float y = height - boxHeight - 20.0f;
        NVGRenderer.rect(x, y, boxWidth, boxHeight, new Color(0, 0, 0, 140).getRGB(), 8.0f);
        NVGRenderer.hollowRect(x, y, boxWidth, boxHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 8.0f);
        NVGRenderer.text$default(text, x + padding, y + 7.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
    }

    public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        float screenWidth = this.getMc().method_22683().method_4480();
        float screenHeight = this.getMc().method_22683().method_4507();
        boolean $i$f$getMouseX = false;
        float mx = (float)class_310.method_1551().field_1729.method_1603();
        boolean $i$f$getMouseY = false;
        float my = (float)class_310.method_1551().field_1729.method_1604();
        int button = click.method_74245();
        if (this.handleSettingsPopupClick(mx, my, button)) {
            return true;
        }
        if (button == 1 && this.handleRightClick(mx, my, screenWidth, screenHeight)) {
            return true;
        }
        if (button == 0 && this.handleLeftClick(mx, my, screenWidth, screenHeight)) {
            return true;
        }
        return super.method_25402(click, doubled);
    }

    private final boolean handleSettingsPopupClick(float mx, float my, int button) {
        if (!this.settingsPopup.getVisible()) {
            return false;
        }
        if (this.settingsPopup.mouseClicked(mx, my, button)) {
            return true;
        }
        if (!this.settingsPopup.containsPoint(mx, my)) {
            this.settingsPopup.hide();
        }
        return false;
    }

    private final boolean handleRightClick(float mx, float my, float screenWidth, float screenHeight) {
        HudElement target = this.findElementUnderCursor(mx, my, screenWidth, screenHeight);
        if (target != null) {
            this.selectedElement = target;
            this.settingsPopup.show(target, screenWidth, screenHeight);
            return true;
        }
        return false;
    }

    private final boolean handleLeftClick(float mx, float my, float screenWidth, float screenHeight) {
        if (this.tryStartResizing(mx, my, screenWidth, screenHeight)) {
            return true;
        }
        return this.tryStartDragging(mx, my, screenWidth, screenHeight);
    }

    private final boolean tryStartResizing(float mx, float my, float screenWidth, float screenHeight) {
        HudElement hudElement = this.selectedElement;
        if (hudElement == null) {
            return false;
        }
        HudElement element = hudElement;
        Pair<Float, Float> pair = element.getScreenPosition(screenWidth, screenHeight);
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        float w = element.getScaledWidth();
        float h = element.getScaledHeight();
        float handleX = sx + w - 8.0f;
        float handleY = sy + h - 8.0f;
        if (mx >= handleX && mx <= handleX + 8.0f && my >= handleY && my <= handleY + 8.0f) {
            this.resizing = true;
            this.initialMouseX = mx;
            this.initialMouseY = my;
            this.initialWidth = w;
            return true;
        }
        return false;
    }

    private final boolean tryStartDragging(float mx, float my, float screenWidth, float screenHeight) {
        HudElement target;
        this.selectedElement = target = this.findElementUnderCursor(mx, my, screenWidth, screenHeight);
        if (target != null) {
            Pair<Float, Float> pair = target.getScreenPosition(screenWidth, screenHeight);
            float sx = ((Number)pair.component1()).floatValue();
            float sy = ((Number)pair.component2()).floatValue();
            this.dragOffsetX = mx - sx;
            this.dragOffsetY = my - sy;
            this.dragging = true;
            this.settingsPopup.hide();
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    public boolean method_25406(@NotNull class_11909 click) {
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        if (this.settingsPopup.mouseReleased(click.method_74245())) {
            return true;
        }
        if (click.method_74245() == 0 && this.dragging) {
            this.dragging = false;
            HudElement hudElement = this.selectedElement;
            if (hudElement != null) {
                void $this$mapTo$iv$iv;
                void $this$map$iv;
                HudElement it;
                void $this$filterTo$iv$iv;
                Iterable $this$filter$iv;
                HudElement element = hudElement;
                boolean bl = false;
                float screenWidth = this.getMc().method_22683().method_4480();
                float screenHeight = this.getMc().method_22683().method_4507();
                boolean $i$f$getMouseX = false;
                float mx = (float)class_310.method_1551().field_1729.method_1603();
                boolean $i$f$getMouseY = false;
                float my = (float)class_310.method_1551().field_1729.method_1604();
                float newScreenX = mx - this.dragOffsetX;
                float newScreenY = my - this.dragOffsetY;
                Pair<Float, Float> pair = (Pair<Float, Float>)HudModuleManager.INSTANCE.getElements();
                boolean $i$f$filter = false;
                void var12_15 = $this$filter$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$filterTo = false;
                for (Object element$iv$iv : $this$filterTo$iv$iv) {
                    it = (HudElement)element$iv$iv;
                    boolean bl2 = false;
                    if (!(!Intrinsics.areEqual((Object)it, (Object)element))) continue;
                    destination$iv$iv.add(element$iv$iv);
                }
                $this$filter$iv = (List)destination$iv$iv;
                boolean $i$f$map = false;
                $this$filterTo$iv$iv = $this$map$iv;
                destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                boolean $i$f$mapTo = false;
                for (Object item$iv$iv : $this$mapTo$iv$iv) {
                    it = (HudElement)item$iv$iv;
                    Collection collection = destination$iv$iv;
                    boolean bl3 = false;
                    Pair<Float, Float> pair2 = it.getScreenPosition(screenWidth, screenHeight);
                    float sx = ((Number)pair2.component1()).floatValue();
                    float sy = ((Number)pair2.component2()).floatValue();
                    collection.add(new SnapHelper.ModuleBounds(sx, sy, it.getScaledWidth(), it.getScaledHeight()));
                }
                List otherBounds = (List)destination$iv$iv;
                pair = this.snapHelper.findAlignmentGuides(newScreenX, newScreenY, element.getScaledWidth(), element.getScaledHeight(), screenWidth, screenHeight, otherBounds);
                float alignedX = ((Number)pair.component1()).floatValue();
                float alignedY = ((Number)pair.component2()).floatValue();
                this.updateElementPosition(element, (float)Math.rint(alignedX), (float)Math.rint(alignedY), screenWidth, screenHeight);
                this.snapHelper.clearGuides();
            }
            return true;
        }
        if (this.resizing) {
            this.resizing = false;
            return true;
        }
        return super.method_25406(click);
    }

    /*
     * WARNING - void declaration
     */
    public boolean method_25403(@NotNull class_11909 click, double offsetX, double offsetY) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        HudElement it;
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv;
        Intrinsics.checkNotNullParameter((Object)click, (String)"click");
        if (this.settingsPopup.mouseDragged(click.method_74245(), offsetX, offsetY)) {
            return true;
        }
        if (click.method_74245() != 0) {
            return super.method_25403(click, offsetX, offsetY);
        }
        HudElement hudElement = this.selectedElement;
        if (hudElement == null) {
            return super.method_25403(click, offsetX, offsetY);
        }
        HudElement element = hudElement;
        boolean $i$f$getMouseX = false;
        float mx = (float)class_310.method_1551().field_1729.method_1603();
        if (this.resizing) {
            float newWidth = this.initialWidth + (mx - this.initialMouseX);
            float newScale = newWidth / element.getBaseWidth();
            element.setScale(RangesKt.coerceIn((float)newScale, (float)0.5f, (float)3.0f));
            return true;
        }
        if (!this.dragging) {
            return super.method_25403(click, offsetX, offsetY);
        }
        float screenWidth = this.getMc().method_22683().method_4480();
        float screenHeight = this.getMc().method_22683().method_4507();
        boolean $i$f$getMouseY = false;
        float my = (float)class_310.method_1551().field_1729.method_1604();
        float newScreenX = mx - this.dragOffsetX;
        float newScreenY = my - this.dragOffsetY;
        Pair<Float, Float> pair = (Pair<Float, Float>)HudModuleManager.INSTANCE.getElements();
        boolean $i$f$filter = false;
        void var16_18 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (HudElement)element$iv$iv;
            boolean bl = false;
            if (!(!Intrinsics.areEqual((Object)it, (Object)element))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            it = (HudElement)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Pair<Float, Float> pair2 = it.getScreenPosition(screenWidth, screenHeight);
            float sx = ((Number)pair2.component1()).floatValue();
            float sy = ((Number)pair2.component2()).floatValue();
            collection.add(new SnapHelper.ModuleBounds(sx, sy, it.getScaledWidth(), it.getScaledHeight()));
        }
        List otherBounds = (List)destination$iv$iv;
        pair = this.snapHelper.findAlignmentGuides(newScreenX, newScreenY, element.getScaledWidth(), element.getScaledHeight(), screenWidth, screenHeight, otherBounds);
        float alignedX = ((Number)pair.component1()).floatValue();
        float alignedY = ((Number)pair.component2()).floatValue();
        this.updateElementPosition(element, (float)Math.rint(alignedX), (float)Math.rint(alignedY), screenWidth, screenHeight);
        return true;
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.settingsPopup.mouseScrolled(horizontalAmount, verticalAmount)) {
            return true;
        }
        return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean method_25404(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (this.settingsPopup.keyPressed(input)) {
            return true;
        }
        return super.method_25404(input);
    }

    public boolean method_25400(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (this.settingsPopup.charTyped(input)) {
            return true;
        }
        return super.method_25400(input);
    }

    protected void method_25426() {
        HudModuleManager.INSTANCE.setEditorOpen(true);
        super.method_25426();
    }

    public void method_25419() {
        HudModuleManager.INSTANCE.setEditorOpen(false);
        Config.INSTANCE.saveModulesConfig();
        EventBus.unregister((Object)this);
        super.method_25419();
    }

    private final HudElement findElementUnderCursor(float mouseX, float mouseY, float screenWidth, float screenHeight) {
        HudElement hudElement;
        block1: {
            List<HudElement> $this$lastOrNull$iv = HudModuleManager.INSTANCE.getElements();
            boolean $i$f$lastOrNull = false;
            ListIterator<HudElement> iterator$iv = $this$lastOrNull$iv.listIterator($this$lastOrNull$iv.size());
            while (iterator$iv.hasPrevious()) {
                HudElement element$iv;
                HudElement it = element$iv = iterator$iv.previous();
                boolean bl = false;
                if (!it.containsPoint(mouseX, mouseY, screenWidth, screenHeight)) continue;
                hudElement = element$iv;
                break block1;
            }
            hudElement = null;
        }
        return hudElement;
    }

    private final void updateElementPosition(HudElement element, float newScreenX, float newScreenY, float screenWidth, float screenHeight) {
        float w = element.getScaledWidth();
        float h = element.getScaledHeight();
        switch (WhenMappings.$EnumSwitchMapping$0[element.getAnchor().ordinal()]) {
            case 1: {
                element.setOffsetX(newScreenX);
                element.setOffsetY(newScreenY);
                break;
            }
            case 2: {
                element.setOffsetX(newScreenX - (screenWidth / 2.0f - w / 2.0f));
                element.setOffsetY(newScreenY);
                break;
            }
            case 3: {
                element.setOffsetX(screenWidth - w - newScreenX);
                element.setOffsetY(newScreenY);
                break;
            }
            case 4: {
                element.setOffsetX(newScreenX);
                element.setOffsetY(newScreenY - (screenHeight / 2.0f - h / 2.0f));
                break;
            }
            case 5: {
                element.setOffsetX(newScreenX - (screenWidth / 2.0f - w / 2.0f));
                element.setOffsetY(newScreenY - (screenHeight / 2.0f - h / 2.0f));
                break;
            }
            case 6: {
                element.setOffsetX(screenWidth - w - newScreenX);
                element.setOffsetY(newScreenY - (screenHeight / 2.0f - h / 2.0f));
                break;
            }
            case 7: {
                element.setOffsetX(newScreenX);
                element.setOffsetY(screenHeight - h - newScreenY);
                break;
            }
            case 8: {
                element.setOffsetX(newScreenX - (screenWidth / 2.0f - w / 2.0f));
                element.setOffsetY(screenHeight - h - newScreenY);
                break;
            }
            case 9: {
                element.setOffsetX(screenWidth - w - newScreenX);
                element.setOffsetY(screenHeight - h - newScreenY);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/screen/UIHudEditor$Companion;", "", "<init>", "()V", "", "RESIZE_HANDLE_SIZE", "F", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[HudAnchor.values().length];
            try {
                nArray[HudAnchor.TOP_LEFT.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.TOP_CENTER.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.TOP_RIGHT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER_LEFT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER_RIGHT.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_LEFT.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_CENTER.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_RIGHT.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

