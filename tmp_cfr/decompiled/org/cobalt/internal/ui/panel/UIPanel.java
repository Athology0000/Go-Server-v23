/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import org.cobalt.internal.ui.UIComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0005\b \u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\rJ'\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0014H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0018H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u001d\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u001b8\u0006\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/ui/panel/UIPanel;", "Lorg/cobalt/internal/ui/UIComponent;", "", "x", "y", "width", "height", "<init>", "(FFFF)V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "", "components", "Ljava/util/List;", "getComponents", "()Ljava/util/List;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIPanel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIPanel.kt\norg/cobalt/internal/ui/panel/UIPanel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,32:1\n1807#2,3:33\n1807#2,3:36\n1807#2,3:39\n1807#2,3:42\n1807#2,3:45\n*S KotlinDebug\n*F\n+ 1 UIPanel.kt\norg/cobalt/internal/ui/panel/UIPanel\n*L\n17#1:33,3\n20#1:36,3\n23#1:39,3\n26#1:42,3\n29#1:45,3\n*E\n"})
public abstract class UIPanel
extends UIComponent {
    @NotNull
    private final List<UIComponent> components = new ArrayList();

    public UIPanel(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @NotNull
    public final List<UIComponent> getComponents() {
        return this.components;
    }

    @Override
    public boolean mouseClicked(int button) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = this.components;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIComponent it = (UIComponent)element$iv;
                    boolean bl2 = false;
                    if (!it.mouseClicked(button)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    @Override
    public boolean mouseReleased(int button) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = this.components;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIComponent it = (UIComponent)element$iv;
                    boolean bl2 = false;
                    if (!it.mouseReleased(button)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = this.components;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIComponent it = (UIComponent)element$iv;
                    boolean bl2 = false;
                    if (!it.mouseDragged(button, offsetX, offsetY)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        boolean bl;
        block3: {
            Intrinsics.checkNotNullParameter((Object)input, (String)"input");
            Iterable $this$any$iv = this.components;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIComponent it = (UIComponent)element$iv;
                    boolean bl2 = false;
                    if (!it.charTyped(input)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        boolean bl;
        block3: {
            Intrinsics.checkNotNullParameter((Object)input, (String)"input");
            Iterable $this$any$iv = this.components;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    UIComponent it = (UIComponent)element$iv;
                    boolean bl2 = false;
                    if (!it.keyPressed(input)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }
}

