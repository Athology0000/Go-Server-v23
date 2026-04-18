/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\b \u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH&\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u0011\u0010\u0010J'\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012H\u0016\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001f\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0012H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0017\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u001bH\u0016\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0017\u0010 \u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u001fH\u0016\u00a2\u0006\u0004\b \u0010!J\u001d\u0010\"\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\"\u0010#R\"\u0010\u0003\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0003\u0010$\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\"\u0010\u0004\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0004\u0010$\u001a\u0004\b)\u0010&\"\u0004\b*\u0010(R\"\u0010\u0005\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010$\u001a\u0004\b+\u0010&\"\u0004\b,\u0010(R\"\u0010\u0006\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010$\u001a\u0004\b-\u0010&\"\u0004\b.\u0010(\u00a8\u0006/"}, d2={"Lorg/cobalt/internal/ui/UIComponent;", "", "", "x", "y", "width", "height", "<init>", "(FFFF)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "updateBounds", "(FF)Lorg/cobalt/internal/ui/UIComponent;", "F", "getX", "()F", "setX", "(F)V", "getY", "setY", "getWidth", "setWidth", "getHeight", "setHeight", "cobalt"})
public abstract class UIComponent {
    private float x;
    private float y;
    private float width;
    private float height;

    public UIComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float f) {
        this.x = f;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float f) {
        this.y = f;
    }

    public final float getWidth() {
        return this.width;
    }

    public final void setWidth(float f) {
        this.width = f;
    }

    public final float getHeight() {
        return this.height;
    }

    public final void setHeight(float f) {
        this.height = f;
    }

    public abstract void render();

    public boolean mouseClicked(int button) {
        return false;
    }

    public boolean mouseReleased(int button) {
        return false;
    }

    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        return false;
    }

    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        return false;
    }

    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return false;
    }

    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        return false;
    }

    @NotNull
    public final UIComponent updateBounds(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}

