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
package org.cobalt.internal.ui.components.settings;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\rJ'\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0014H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0018H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0016\u0010!\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010 \u00a8\u0006\""}, d2={"Lorg/cobalt/internal/ui/components/settings/UITextSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/TextSetting;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "inputHandler", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "focused", "Z", "dragging", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUITextSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UITextSetting.kt\norg/cobalt/internal/ui/components/settings/UITextSetting\n+ 2 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,169:1\n6#2:170\n6#2:171\n1#3:172\n*S KotlinDebug\n*F\n+ 1 UITextSetting.kt\norg/cobalt/internal/ui/components/settings/UITextSetting\n*L\n68#1:170\n89#1:171\n*E\n"})
public final class UITextSetting
extends UIComponent {
    @NotNull
    private final TextSetting setting;
    @NotNull
    private final TextInputHandler inputHandler;
    private boolean focused;
    private boolean dragging;

    public UITextSetting(@NotNull TextSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.inputHandler = new TextInputHandler((String)this.setting.getValue(), 0, 2, null);
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + 14.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + 32.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        float inputX = this.getX() + this.getWidth() - 280.0f;
        float inputY = this.getY() + 15.0f;
        int borderColor = this.focused ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getInputBorder();
        NVGRenderer.rect(inputX, inputY, 260.0f, 30.0f, ThemeManager.INSTANCE.getCurrentTheme().getInputBg(), 5.0f);
        NVGRenderer.hollowRect(inputX, inputY, 260.0f, 30.0f, 2.0f, borderColor, 5.0f);
        float textX = inputX + 10.0f;
        float textY = inputY + 9.0f;
        if (this.focused) {
            this.inputHandler.updateScroll(240.0f, 13.0f);
        }
        NVGRenderer.pushScissor(inputX + 10.0f, inputY, 240.0f, 30.0f);
        if (this.focused) {
            this.inputHandler.renderSelection(textX, textY, 13.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getSelection());
        }
        NVGRenderer.text$default(this.inputHandler.getText(), textX - this.inputHandler.getTextOffset(), textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        if (this.focused) {
            this.inputHandler.renderCursor(textX, textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText());
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseClicked(int button) {
        float inputY;
        if (button != 0) {
            return false;
        }
        float inputX = this.getX() + this.getWidth() - 280.0f;
        if (ExtensionsKt.isHoveringOver(inputX, inputY = this.getY() + 15.0f, 260.0f, 30.0f)) {
            this.focused = true;
            this.dragging = true;
            boolean $i$f$getMouseX = false;
            this.inputHandler.startSelection((float)class_310.method_1551().field_1729.method_1603(), inputX + 10.0f, 13.0f);
            return true;
        }
        if (this.focused) {
            this.setting.setValue(this.inputHandler.getText());
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
            float inputX = this.getX() + this.getWidth() - 280.0f;
            boolean $i$f$getMouseX = false;
            this.inputHandler.updateSelection((float)class_310.method_1551().field_1729.method_1603(), inputX + 10.0f, 13.0f);
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
            case 256: 
            case 257: {
                this.setting.setValue(this.inputHandler.getText());
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

