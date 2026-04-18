/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIActionSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/ActionSetting;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "cobalt"})
public final class UIActionSetting
extends UIComponent {
    @NotNull
    private final ActionSetting setting;

    public UIActionSetting(@NotNull ActionSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
    }

    @Override
    public void render() {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, theme.getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
        String label = this.setting.getButtonLabel();
        float labelWidth = NVGRenderer.textWidth$default(label, 13.0f, null, 4, null);
        float buttonWidth = RangesKt.coerceAtLeast((float)(labelWidth + 28.0f), (float)90.0f);
        float buttonHeight = 30.0f;
        float buttonX = this.getX() + this.getWidth() - buttonWidth - 20.0f;
        float buttonY = this.getY() + this.getHeight() / 2.0f - buttonHeight / 2.0f;
        boolean hovering = ExtensionsKt.isHoveringOver(buttonX, buttonY, buttonWidth, buttonHeight);
        int baseColor = theme.getAccent();
        int buttonColor = hovering ? new Color(baseColor).darker().getRGB() : baseColor;
        int textColor = theme.getTextOnAccent();
        NVGRenderer.rect(buttonX, buttonY, buttonWidth, buttonHeight, buttonColor, 8.0f);
        NVGRenderer.hollowRect(buttonX, buttonY, buttonWidth, buttonHeight, 1.5f, theme.getControlBorder(), 8.0f);
        NVGRenderer.text$default(label, buttonX + buttonWidth / 2.0f - labelWidth / 2.0f, buttonY + 9.0f, 13.0f, textColor, null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        float buttonY;
        if (button != 0) {
            return false;
        }
        String label = this.setting.getButtonLabel();
        float labelWidth = NVGRenderer.textWidth$default(label, 13.0f, null, 4, null);
        float buttonWidth = RangesKt.coerceAtLeast((float)(labelWidth + 28.0f), (float)90.0f);
        float buttonHeight = 30.0f;
        float buttonX = this.getX() + this.getWidth() - buttonWidth - 20.0f;
        if (ExtensionsKt.isHoveringOver(buttonX, buttonY = this.getY() + this.getHeight() / 2.0f - buttonHeight / 2.0f, buttonWidth, buttonHeight)) {
            this.setting.trigger();
            return true;
        }
        return false;
    }
}

