/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.tooltips.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\t\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\t\u0010\nR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u000bR\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\fR\u0014\u0010\r\u001a\u00020\u00048\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\r\u0010\f\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/ui/components/tooltips/impl/UITextTooltip;", "Lorg/cobalt/internal/ui/UIComponent;", "", "text", "", "textHeight", "<init>", "(Ljava/lang/String;F)V", "", "render", "()V", "Ljava/lang/String;", "F", "padding", "cobalt"})
public final class UITextTooltip
extends UIComponent {
    @NotNull
    private final String text;
    private final float textHeight;
    private final float padding;

    public UITextTooltip(@NotNull String text, float textHeight) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        super(0.0f, 0.0f, NVGRenderer.textWidth$default(text, textHeight, null, 4, null) + 16.0f, textHeight + 16.0f);
        this.text = text;
        this.textHeight = textHeight;
        this.padding = 8.0f;
        this.setWidth(NVGRenderer.textWidth$default(this.text, this.textHeight, null, 4, null) + this.padding * (float)2);
        this.setHeight(this.textHeight + this.padding * (float)2);
    }

    public /* synthetic */ UITextTooltip(String string, float f, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 2) != 0) {
            f = 16.0f;
        }
        this(string, f);
    }

    @Override
    public void render() {
        float textWidth = NVGRenderer.textWidth$default(this.text, this.textHeight, null, 4, null);
        NVGRenderer.text$default(this.text, this.getX() + this.getWidth() / (float)2 - textWidth / (float)2, this.getY() + this.getHeight() / (float)2 - this.textHeight / (float)2, this.textHeight, ThemeManager.INSTANCE.getCurrentTheme().getTooltipText(), null, 32, null);
    }
}

