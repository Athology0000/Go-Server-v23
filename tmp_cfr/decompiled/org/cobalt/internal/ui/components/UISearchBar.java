/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\u0003J\u000f\u0010\t\u001a\u00020\u0007H\u0016\u00a2\u0006\u0004\b\t\u0010\u0003J\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u000f\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u000eJ'\u0010\u0013\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0016\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0015H\u0016\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0019H\u0016\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0016\u0010!\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010 \u00a8\u0006\""}, d2={"Lorg/cobalt/internal/ui/components/UISearchBar;", "Lorg/cobalt/internal/ui/UIComponent;", "<init>", "()V", "", "getSearchText", "()Ljava/lang/String;", "", "clearSearch", "render", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "inputHandler", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "focused", "Z", "dragging", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUISearchBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UISearchBar.kt\norg/cobalt/internal/ui/components/UISearchBar\n+ 2 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,168:1\n6#2:169\n6#2:170\n1#3:171\n*S KotlinDebug\n*F\n+ 1 UISearchBar.kt\norg/cobalt/internal/ui/components/UISearchBar\n*L\n70#1:169\n89#1:170\n*E\n"})
public final class UISearchBar
extends UIComponent {
    @NotNull
    private final TextInputHandler inputHandler = new TextInputHandler("", 64);
    private boolean focused;
    private boolean dragging;

    public UISearchBar() {
        super(0.0f, 0.0f, 300.0f, 40.0f);
    }

    @NotNull
    public final String getSearchText() {
        return this.inputHandler.getText();
    }

    public final void clearSearch() {
        this.inputHandler.setText("");
        this.focused = false;
    }

    @Override
    public void render() {
        String text;
        int borderColor = this.focused ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBorder();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getInputBg(), 5.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0f, borderColor, 5.0f);
        float textX = this.getX() + 15.0f;
        float textY = this.getY() + 14.0f;
        if (this.focused) {
            this.inputHandler.updateScroll(this.getWidth() - 30.0f, 13.0f);
        }
        NVGRenderer.pushScissor(this.getX() + 15.0f, this.getY() + 5.0f, this.getWidth() - 30.0f, this.getHeight() - 10.0f);
        if (this.focused) {
            this.inputHandler.renderSelection(textX, textY, 13.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getSelection());
        }
        if (((CharSequence)(text = this.inputHandler.getText())).length() == 0 && !this.focused) {
            NVGRenderer.text$default("Search...", textX, textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getSearchPlaceholderText(), null, 32, null);
        } else {
            NVGRenderer.text$default(text, textX - this.inputHandler.getTextOffset(), textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        }
        if (this.focused) {
            this.inputHandler.renderCursor(textX, textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText());
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseClicked(int button) {
        if (button != 0) {
            return false;
        }
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            this.focused = true;
            this.dragging = true;
            boolean $i$f$getMouseX = false;
            this.inputHandler.startSelection((float)class_310.method_1551().field_1729.method_1603(), this.getX() + 15.0f, 13.0f);
            return true;
        }
        if (this.focused) {
            this.focused = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0) {
            this.dragging = false;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (button == 0 && this.dragging && this.focused) {
            boolean $i$f$getMouseX = false;
            this.inputHandler.updateSelection((float)class_310.method_1551().field_1729.method_1603(), this.getX() + 15.0f, 13.0f);
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.focused) {
            return false;
        }
        char c = (char)input.comp_4793();
        if (c >= ' ' && c != '\u007f') {
            this.inputHandler.insertText(String.valueOf(c));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.focused) {
            return false;
        }
        boolean ctrl = (input.comp_4797() & 2) != 0;
        boolean shift = (input.comp_4797() & 1) != 0;
        switch (input.comp_4795()) {
            case 256: {
                this.focused = false;
                return true;
            }
            case 259: {
                this.inputHandler.backspace();
                return true;
            }
            case 261: {
                this.inputHandler.delete();
                return true;
            }
            case 263: {
                this.inputHandler.moveCursorLeft(shift);
                return true;
            }
            case 262: {
                this.inputHandler.moveCursorRight(shift);
                return true;
            }
            case 268: {
                this.inputHandler.moveCursorToStart(shift);
                return true;
            }
            case 269: {
                this.inputHandler.moveCursorToEnd(shift);
                return true;
            }
            case 65: {
                if (!ctrl) break;
                this.inputHandler.selectAll();
                return true;
            }
            case 67: {
                if (!ctrl) break;
                String string = this.inputHandler.copy();
                if (string != null) {
                    String it = string;
                    boolean bl = false;
                    class_310.method_1551().field_1774.method_1455(it);
                }
                return true;
            }
            case 88: {
                if (!ctrl) break;
                String string = this.inputHandler.cut();
                if (string != null) {
                    String it = string;
                    boolean bl = false;
                    class_310.method_1551().field_1774.method_1455(it);
                }
                return true;
            }
            case 86: {
                if (!ctrl) break;
                String string = class_310.method_1551().field_1774.method_1460();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getClipboard(...)");
                String clipboard = string;
                if (((CharSequence)clipboard).length() > 0) {
                    this.inputHandler.insertText(clipboard);
                }
                return true;
            }
        }
        return false;
    }
}

