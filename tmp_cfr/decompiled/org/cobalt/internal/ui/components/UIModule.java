/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.animation.EaseOutAnimation;
import org.cobalt.internal.ui.panel.panels.UIModuleList;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\rH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0011\u001a\u00020\n2\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0015\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00140\u0013\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\u0019R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u001aR\u0016\u0010\u0007\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/ui/components/UIModule;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/Module;", "module", "Lorg/cobalt/internal/ui/panel/panels/UIModuleList;", "panel", "", "selected", "<init>", "(Lorg/cobalt/api/module/Module;Lorg/cobalt/internal/ui/panel/panels/UIModuleList;Z)V", "", "render", "()V", "", "button", "mouseClicked", "(I)Z", "setSelected", "(Z)V", "", "Lorg/cobalt/api/module/setting/Setting;", "getSettings", "()Ljava/util/List;", "Lorg/cobalt/api/module/Module;", "getModule", "()Lorg/cobalt/api/module/Module;", "Lorg/cobalt/internal/ui/panel/panels/UIModuleList;", "Z", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnimation", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "xOffsetAnimation", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "Companion", "cobalt"})
public final class UIModule
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Module module;
    @NotNull
    private final UIModuleList panel;
    private boolean selected;
    @NotNull
    private final ColorAnimation colorAnimation;
    @NotNull
    private final EaseOutAnimation xOffsetAnimation;
    @NotNull
    private static final Image selectedIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/selected.svg");

    public UIModule(@NotNull Module module, @NotNull UIModuleList panel, boolean selected) {
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        Intrinsics.checkNotNullParameter((Object)panel, (String)"panel");
        super(0.0f, 0.0f, 182.5f, 40.0f);
        this.module = module;
        this.panel = panel;
        this.selected = selected;
        this.colorAnimation = new ColorAnimation(150L);
        this.xOffsetAnimation = new EaseOutAnimation(200L);
    }

    @NotNull
    public final Module getModule() {
        return this.module;
    }

    @Override
    public void render() {
        int opaqueColor = this.colorAnimation.get(ThemeManager.INSTANCE.getCurrentTheme().getTransparent(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !this.selected);
        int mainColor = this.colorAnimation.get(ThemeManager.INSTANCE.getCurrentTheme().getTransparent(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !this.selected);
        int textColor = this.colorAnimation.get(ThemeManager.INSTANCE.getCurrentTheme().getText(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !this.selected);
        float xOffset = this.xOffsetAnimation.get(0.0f, 10.0f, !this.selected).floatValue();
        if (this.selected) {
            NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), opaqueColor, 5.0f);
            NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, mainColor, 5.0f);
            NVGRenderer.image$default(selectedIcon, this.getX() + 10.0f, this.getY() + this.getHeight() / (float)2 - 7.0f, 13.0f, 13.0f, 0.0f, mainColor, 32, null);
        }
        if (StringsKt.equals((String)this.module.getName(), (String)"Fairy", (boolean)true)) {
            NVGRenderer.textGradient$default(this.module.getName(), this.getX() + 20.0f + xOffset, this.getY() + this.getHeight() / 2.0f - 6.5f, 13.0f, -42296, -12589313, Gradient.LeftToRight, null, 128, null);
        } else if (StringsKt.equals((String)this.module.getName(), (String)"Full Bright", (boolean)true)) {
            float textX = this.getX() + 20.0f + xOffset;
            float textY = this.getY() + this.getHeight() / 2.0f - 6.5f;
            int glowColor = -2130710368;
            NVGRenderer.text$default(this.module.getName(), textX - 1.0f, textY, 13.0f, glowColor, null, 32, null);
            NVGRenderer.text$default(this.module.getName(), textX + 1.0f, textY, 13.0f, glowColor, null, 32, null);
            NVGRenderer.text$default(this.module.getName(), textX, textY - 1.0f, 13.0f, glowColor, null, 32, null);
            NVGRenderer.text$default(this.module.getName(), textX, textY + 1.0f, 13.0f, glowColor, null, 32, null);
            NVGRenderer.text$default(this.module.getName(), textX, textY, 13.0f, -2878, null, 32, null);
        } else {
            NVGRenderer.text$default(this.module.getName(), this.getX() + 20.0f + xOffset, this.getY() + this.getHeight() / 2.0f - 6.5f, 13.0f, textColor, null, 32, null);
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight()) && button == 0) {
            this.panel.setModule(this);
            return true;
        }
        return false;
    }

    public final void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            this.colorAnimation.start();
            this.xOffsetAnimation.start();
        }
    }

    public static /* synthetic */ void setSelected$default(UIModule uIModule, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        uIModule.setSelected(bl);
    }

    @NotNull
    public final List<Setting<?>> getSettings() {
        return this.module.getSettings();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/UIModule$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "selectedIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

