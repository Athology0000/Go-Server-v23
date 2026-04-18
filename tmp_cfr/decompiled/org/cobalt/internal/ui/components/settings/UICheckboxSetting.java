/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000eR\u0016\u0010\u0010\u001a\u00020\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/ui/components/settings/UICheckboxSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/CheckboxSetting;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "Companion", "cobalt"})
public final class UICheckboxSetting
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final CheckboxSetting setting;
    @NotNull
    private ColorAnimation colorAnim;
    @NotNull
    private static final Image checkmarkIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/checkmark.svg");

    public UICheckboxSetting(@NotNull CheckboxSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.colorAnim = new ColorAnimation(150L);
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        NVGRenderer.rect(this.getX() + this.getWidth() - 45.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, 25.0f, 25.0f, this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), (Boolean)this.setting.getValue() == false), 5.0f);
        NVGRenderer.hollowRect(this.getX() + this.getWidth() - 45.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, 25.0f, 25.0f, 1.5f, this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), (Boolean)this.setting.getValue() == false), 5.0f);
        if (((Boolean)this.setting.getValue()).booleanValue()) {
            NVGRenderer.image$default(checkmarkIcon, this.getX() + this.getWidth() - 42.5f, this.getY() + this.getHeight() / 2.0f - 10.0f, 20.0f, 20.0f, 0.0f, this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getTransparent(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), (Boolean)this.setting.getValue() == false), 32, null);
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        if (ExtensionsKt.isHoveringOver(this.getX() + this.getWidth() - 45.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, 25.0f, 25.0f)) {
            this.setting.setValue((Boolean)this.setting.getValue() == false);
            this.colorAnim.start();
            return true;
        }
        return false;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/ui/components/settings/UICheckboxSetting$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "checkmarkIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "getCheckmarkIcon", "()Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Image getCheckmarkIcon() {
            return checkmarkIcon;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

