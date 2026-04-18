/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_310;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0017\u0010\b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000f\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u0014\u0010\u0015J'\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u001a\u0010\u0015R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001bR\u0016\u0010\u001c\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0016\u0010\u001e\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001d\u00a8\u0006\u001f"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIRangeSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/RangeSetting;)V", "", "mouseX", "getValueFromX", "(D)D", "value", "", "getThumbX", "(D)F", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "mouseReleased", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "isDraggingStart", "Z", "isDraggingEnd", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIRangeSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIRangeSetting.kt\norg/cobalt/internal/ui/components/settings/UIRangeSetting\n+ 2 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,172:1\n6#2:173\n6#2:174\n*S KotlinDebug\n*F\n+ 1 UIRangeSetting.kt\norg/cobalt/internal/ui/components/settings/UIRangeSetting\n*L\n119#1:173\n139#1:174\n*E\n"})
public final class UIRangeSetting
extends UIComponent {
    @NotNull
    private final RangeSetting setting;
    private boolean isDraggingStart;
    private boolean isDraggingEnd;

    public UIRangeSetting(@NotNull RangeSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
    }

    private final double getValueFromX(double mouseX) {
        double relativeX = RangesKt.coerceIn((double)(mouseX - (double)(this.getX() + this.getWidth() - 220.0f)), (double)0.0, (double)200.0);
        double percentage = relativeX / (double)200.0f;
        return this.setting.getMin() + percentage * (this.setting.getMax() - this.setting.getMin());
    }

    private final float getThumbX(double value) {
        double percentage = (value - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
        return this.getX() + this.getWidth() - 220.0f + (float)(percentage * (double)200.0f);
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        float sliderX = this.getX() + this.getWidth() - 220.0f;
        float sliderY = this.getY() + this.getHeight() / 2.0f - 2.0f;
        float startThumbX = this.getThumbX(((Number)((Pair)this.setting.getValue()).getFirst()).doubleValue());
        float endThumbX = this.getThumbX(((Number)((Pair)this.setting.getValue()).getSecond()).doubleValue());
        NVGRenderer.rect(sliderX, sliderY, 200.0f, 4.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderTrack(), 2.0f);
        NVGRenderer.rect(startThumbX, sliderY, endThumbX - startThumbX, 4.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 2.0f);
        NVGRenderer.circle(startThumbX, sliderY + 2.0f, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderThumb());
        NVGRenderer.circle(endThumbX, sliderY + 2.0f, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderThumb());
        String string = "%.2f - %.2f";
        Object[] objectArray = new Object[]{((Pair)this.setting.getValue()).getFirst(), ((Pair)this.setting.getValue()).getSecond()};
        String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        String text = string2;
        float textWidth = NVGRenderer.textWidth$default(text, 13.0f, null, 4, null);
        NVGRenderer.rect(sliderX - textWidth - 26.0f, this.getY() + this.getHeight() / 2.0f - 12.0f, textWidth + 16.0f, 24.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 4.0f);
        NVGRenderer.hollowRect(sliderX - textWidth - 26.0f, this.getY() + this.getHeight() / 2.0f - 12.0f, textWidth + 16.0f, 24.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 4.0f);
        NVGRenderer.text$default(text, sliderX - textWidth - 18.0f, this.getY() + this.getHeight() / 2.0f - 6.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        if (button == 0) {
            float sliderX = this.getX() + this.getWidth() - 220.0f;
            float sliderY = this.getY() + this.getHeight() / 2.0f - 2.0f;
            float startThumbX = this.getThumbX(((Number)((Pair)this.setting.getValue()).getFirst()).doubleValue());
            float endThumbX = this.getThumbX(((Number)((Pair)this.setting.getValue()).getSecond()).doubleValue());
            if (ExtensionsKt.isHoveringOver(startThumbX - 6.0f, sliderY - 4.0f, 12.0f, 12.0f)) {
                this.isDraggingStart = true;
                return true;
            }
            if (ExtensionsKt.isHoveringOver(endThumbX - 6.0f, sliderY - 4.0f, 12.0f, 12.0f)) {
                this.isDraggingEnd = true;
                return true;
            }
            if (ExtensionsKt.isHoveringOver(sliderX, sliderY - 5.0f, 200.0f, 14.0f)) {
                double distToEnd;
                boolean $i$f$getMouseX = false;
                double clickedValue = this.getValueFromX(class_310.method_1551().field_1729.method_1603());
                double distToStart = Math.abs(clickedValue - ((Number)((Pair)this.setting.getValue()).getFirst()).doubleValue());
                if (distToStart < (distToEnd = Math.abs(clickedValue - ((Number)((Pair)this.setting.getValue()).getSecond()).doubleValue()))) {
                    this.setting.setValue(new Pair((Object)clickedValue, ((Pair)this.setting.getValue()).getSecond()));
                    this.isDraggingStart = true;
                } else {
                    this.setting.setValue(new Pair(((Pair)this.setting.getValue()).getFirst(), (Object)clickedValue));
                    this.isDraggingEnd = true;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (button == 0) {
            boolean $i$f$getMouseX = false;
            double newValue = this.getValueFromX(class_310.method_1551().field_1729.method_1603());
            if (this.isDraggingStart) {
                this.setting.setValue(new Pair((Object)RangesKt.coerceAtMost((double)newValue, (double)((Number)((Pair)this.setting.getValue()).getSecond()).doubleValue()), ((Pair)this.setting.getValue()).getSecond()));
                return true;
            }
            if (this.isDraggingEnd) {
                this.setting.setValue(new Pair(((Pair)this.setting.getValue()).getFirst(), (Object)RangesKt.coerceAtLeast((double)newValue, (double)((Number)((Pair)this.setting.getValue()).getFirst()).doubleValue())));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0 && (this.isDraggingStart || this.isDraggingEnd)) {
            this.isDraggingStart = false;
            this.isDraggingEnd = false;
            return true;
        }
        return false;
    }
}

