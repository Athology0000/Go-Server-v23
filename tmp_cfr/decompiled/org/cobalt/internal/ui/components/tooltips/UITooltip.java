/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.tooltips;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.components.tooltips.TooltipManager;
import org.cobalt.internal.ui.components.tooltips.TooltipPosition;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B)\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ-\u0010\u000e\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000e\u0010\u000fJ+\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00122\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010\u0016\u001a\u00020\u0015H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0018R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0019R\u0014\u0010\u0007\u001a\u00020\u00068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u001aR\u0016\u0010\f\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\f\u0010\u001aR\u0016\u0010\r\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\r\u0010\u001aR\u0016\u0010\u001c\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0016\u0010!\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010\u001d\u00a8\u0006\""}, d2={"Lorg/cobalt/internal/ui/components/tooltips/UITooltip;", "Lorg/cobalt/internal/ui/UIComponent;", "Lkotlin/Function0;", "content", "Lorg/cobalt/internal/ui/components/tooltips/TooltipPosition;", "position", "", "padding", "<init>", "(Lkotlin/jvm/functions/Function0;Lorg/cobalt/internal/ui/components/tooltips/TooltipPosition;F)V", "targetX", "targetY", "targetWidth", "targetHeight", "updateBounds", "(FFFF)Lorg/cobalt/internal/ui/UIComponent;", "contentWidth", "contentHeight", "Lkotlin/Pair;", "calculatePosition", "(FF)Lkotlin/Pair;", "", "render", "()V", "Lkotlin/jvm/functions/Function0;", "Lorg/cobalt/internal/ui/components/tooltips/TooltipPosition;", "F", "", "isHovering", "Z", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "alphaAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "wasHovering", "cobalt"})
public final class UITooltip
extends UIComponent {
    @NotNull
    private final Function0<UIComponent> content;
    @NotNull
    private final TooltipPosition position;
    private final float padding;
    private float targetWidth;
    private float targetHeight;
    private boolean isHovering;
    @NotNull
    private final ColorAnimation alphaAnim;
    private boolean wasHovering;

    public UITooltip(@NotNull Function0<? extends UIComponent> content, @NotNull TooltipPosition position, float padding) {
        Intrinsics.checkNotNullParameter(content, (String)"content");
        Intrinsics.checkNotNullParameter((Object)((Object)position), (String)"position");
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.content = content;
        this.position = position;
        this.padding = padding;
        this.alphaAnim = new ColorAnimation(150L);
        TooltipManager.INSTANCE.register(this);
    }

    public /* synthetic */ UITooltip(Function0 function0, TooltipPosition tooltipPosition, float f, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 2) != 0) {
            tooltipPosition = TooltipPosition.ABOVE;
        }
        if ((n & 4) != 0) {
            f = 8.0f;
        }
        this((Function0<? extends UIComponent>)function0, tooltipPosition, f);
    }

    @NotNull
    public final UIComponent updateBounds(float targetX, float targetY, float targetWidth, float targetHeight) {
        this.setX(targetX);
        this.setY(targetY);
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        return this;
    }

    private final Pair<Float, Float> calculatePosition(float contentWidth, float contentHeight) {
        return switch (WhenMappings.$EnumSwitchMapping$0[this.position.ordinal()]) {
            case 1 -> new Pair((Object)Float.valueOf(this.getX() + this.targetWidth / 2.0f - contentWidth / 2.0f), (Object)Float.valueOf(this.getY() - contentHeight - this.padding));
            case 2 -> new Pair((Object)Float.valueOf(this.getX() + this.targetWidth / 2.0f - contentWidth / 2.0f), (Object)Float.valueOf(this.getY() + this.targetHeight + this.padding));
            case 3 -> new Pair((Object)Float.valueOf(this.getX() - contentWidth - this.padding), (Object)Float.valueOf(this.getY() + this.targetHeight / 2.0f - contentHeight / 2.0f));
            case 4 -> new Pair((Object)Float.valueOf(this.getX() + this.targetWidth + this.padding), (Object)Float.valueOf(this.getY() + this.targetHeight / 2.0f - contentHeight / 2.0f));
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    @Override
    public void render() {
        this.isHovering = ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.targetWidth, this.targetHeight);
        if (this.isHovering != this.wasHovering) {
            this.alphaAnim.start();
            this.wasHovering = this.isHovering;
        }
        if (this.isHovering) {
            UIComponent tooltipContent = (UIComponent)this.content.invoke();
            Pair<Float, Float> pair = this.calculatePosition(tooltipContent.getWidth(), tooltipContent.getHeight());
            float tooltipX = ((Number)pair.component1()).floatValue();
            float tooltipY = ((Number)pair.component2()).floatValue();
            int transparentBg = ThemeManager.INSTANCE.getCurrentTheme().getTooltipBackground() & 0xFFFFFF;
            int bgColor = this.alphaAnim.get(transparentBg, ThemeManager.INSTANCE.getCurrentTheme().getTooltipBackground(), !this.isHovering);
            NVGRenderer.rect(tooltipX, tooltipY, tooltipContent.getWidth(), tooltipContent.getHeight(), bgColor, 4.0f);
            NVGRenderer.hollowRect(tooltipX, tooltipY, tooltipContent.getWidth(), tooltipContent.getHeight(), 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getTooltipBorder(), 4.0f);
            tooltipContent.setX(tooltipX);
            tooltipContent.setY(tooltipY);
            tooltipContent.render();
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[TooltipPosition.values().length];
            try {
                nArray[TooltipPosition.ABOVE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TooltipPosition.BELOW.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TooltipPosition.LEFT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TooltipPosition.RIGHT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

