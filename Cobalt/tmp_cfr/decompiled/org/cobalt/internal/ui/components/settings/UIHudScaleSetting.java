/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_310;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u000e\u001a\u00020\rH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014J'\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u0014R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001aR\u0016\u0010\u001b\u001a\u00020\u00128\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001e\u001a\u00020\b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\fR\u0014\u0010 \u001a\u00020\b8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010\f\u00a8\u0006!"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIHudScaleSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/hud/HudElement;", "element", "<init>", "(Lorg/cobalt/api/hud/HudElement;)V", "", "mouseX", "", "getValueFromX", "(D)F", "getThumbX", "()F", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "mouseReleased", "Lorg/cobalt/api/hud/HudElement;", "isDragging", "Z", "getMin", "min", "getMax", "max", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIHudScaleSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIHudScaleSetting.kt\norg/cobalt/internal/ui/components/settings/UIHudScaleSetting\n+ 2 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,130:1\n6#2:131\n6#2:132\n*S KotlinDebug\n*F\n+ 1 UIHudScaleSetting.kt\norg/cobalt/internal/ui/components/settings/UIHudScaleSetting\n*L\n103#1:131\n114#1:132\n*E\n"})
public final class UIHudScaleSetting
extends UIComponent {
    @NotNull
    private final HudElement element;
    private boolean isDragging;

    public UIHudScaleSetting(@NotNull HudElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.element = element;
    }

    private final float getMin() {
        return this.element.getMinScale();
    }

    private final float getMax() {
        return this.element.getMaxScale();
    }

    private final float getValueFromX(double mouseX) {
        double relativeX = RangesKt.coerceIn((double)(mouseX - (double)(this.getX() + this.getWidth() - 220.0f)), (double)0.0, (double)200.0);
        float percentage = (float)(relativeX / (double)200.0f);
        return RangesKt.coerceIn((float)(this.getMin() + percentage * (this.getMax() - this.getMin())), (float)this.getMin(), (float)this.getMax());
    }

    private final float getThumbX() {
        float percentage = RangesKt.coerceIn((float)((this.element.getScale() - this.getMin()) / (this.getMax() - this.getMin())), (float)0.0f, (float)1.0f);
        return this.getX() + this.getWidth() - 220.0f + percentage * 200.0f;
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default("Scale", this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default("Adjust HUD element size", this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        float sliderX = this.getX() + this.getWidth() - 220.0f;
        float sliderY = this.getY() + this.getHeight() / 2.0f - 2.0f;
        float thumbX = this.getThumbX();
        String string = "%.2fx";
        Object[] objectArray = new Object[]{Float.valueOf(this.element.getScale())};
        String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        String text = string2;
        float textWidth = NVGRenderer.textWidth$default(text, 13.0f, null, 4, null);
        NVGRenderer.rect(sliderX, sliderY, 200.0f, 4.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderTrack(), 2.0f);
        NVGRenderer.rect(sliderX, sliderY, thumbX - sliderX, 4.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 2.0f);
        NVGRenderer.circle(thumbX, sliderY + 2.0f, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderThumb());
        NVGRenderer.rect(sliderX - textWidth - 26.0f, this.getY() + this.getHeight() / 2.0f - 12.0f, textWidth + 16.0f, 24.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 4.0f);
        NVGRenderer.hollowRect(sliderX - textWidth - 26.0f, this.getY() + this.getHeight() / 2.0f - 12.0f, textWidth + 16.0f, 24.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 4.0f);
        NVGRenderer.text$default(text, sliderX - textWidth - 18.0f, this.getY() + this.getHeight() / 2.0f - 6.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        if (button == 0) {
            float thumbX = this.getThumbX();
            float sliderX = this.getX() + this.getWidth() - 220.0f;
            float sliderY = this.getY() + this.getHeight() / 2.0f - 2.0f;
            if (ExtensionsKt.isHoveringOver(thumbX - 6.0f, sliderY - 4.0f, 12.0f, 12.0f)) {
                this.isDragging = true;
                return true;
            }
            if (ExtensionsKt.isHoveringOver(sliderX, sliderY - 5.0f, 200.0f, 14.0f)) {
                boolean $i$f$getMouseX = false;
                this.element.setScale(this.getValueFromX(class_310.method_1551().field_1729.method_1603()));
                this.isDragging = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (this.isDragging && button == 0) {
            boolean $i$f$getMouseX = false;
            this.element.setScale(this.getValueFromX(class_310.method_1551().field_1729.method_1603()));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0 && this.isDragging) {
            this.isDragging = false;
            return true;
        }
        return false;
    }
}

