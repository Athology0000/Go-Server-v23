/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.components;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.panel.panels.UIAddonList;
import org.cobalt.internal.ui.screen.UIConfig;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0019\u0012\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\u0007\u001a\u00020\u0003H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rR\u001c\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0012\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/ui/components/UIBackButton;", "Lorg/cobalt/internal/ui/UIComponent;", "Lkotlin/Function0;", "", "onClick", "<init>", "(Lkotlin/jvm/functions/Function0;)V", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lkotlin/jvm/functions/Function0;", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "colorAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "Z", "Companion", "cobalt"})
public final class UIBackButton
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final Function0<Unit> onClick;
    @NotNull
    private final ColorAnimation colorAnim;
    private boolean wasHovering;
    @NotNull
    private static final Image leftArrow = NVGRenderer.createImage("/assets/cobalt/textures/ui/arrow-left.svg");

    public UIBackButton(@Nullable Function0<Unit> onClick) {
        super(0.0f, 0.0f, 30.0f, 30.0f);
        this.onClick = onClick;
        this.colorAnim = new ColorAnimation(150L);
    }

    public /* synthetic */ UIBackButton(Function0 function0, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            function0 = null;
        }
        this((Function0<Unit>)function0);
    }

    @Override
    public void render() {
        boolean hovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (hovering != this.wasHovering) {
            this.colorAnim.start();
            this.wasHovering = hovering;
        }
        int bgColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), !hovering);
        int borderColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !hovering);
        int arrowColor = this.colorAnim.get(ThemeManager.INSTANCE.getCurrentTheme().getText(), ThemeManager.INSTANCE.getCurrentTheme().getAccent(), !hovering);
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), bgColor, 5.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0f, borderColor, 5.0f);
        NVGRenderer.image(leftArrow, this.getX() + this.getWidth() / 2.0f - 10.0f, this.getY() + this.getHeight() / 2.0f - 10.0f, 20.0f, 20.0f, 0.0f, arrowColor);
    }

    @Override
    public boolean mouseClicked(int button) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight()) && button == 0) {
            Function0<Unit> function0 = this.onClick;
            if (function0 != null) {
                function0.invoke();
            } else {
                UIConfig.INSTANCE.swapBodyPanel(new UIAddonList());
            }
        }
        return false;
    }

    public UIBackButton() {
        this(null, 1, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/UIBackButton$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "leftArrow", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

