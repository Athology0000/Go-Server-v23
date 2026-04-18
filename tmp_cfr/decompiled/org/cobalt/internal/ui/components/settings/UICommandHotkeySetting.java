/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  org.jetbrains.annotations.NotNull
 *  org.lwjgl.glfw.GLFW
 */
package org.cobalt.internal.ui.components.settings;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.cobalt.api.module.setting.impl.CommandHotkeySetting;
import org.cobalt.api.module.setting.impl.CommandHotkeyValue;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.helper.KeyBind;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\rJ'\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0017\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0014H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0018H\u0016\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u001b\u0010\u001d\u001a\u00020\u001c*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001fR\u0014\u0010!\u001a\u00020 8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\"R\u0016\u0010#\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010%\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010$R\u0016\u0010&\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010$\u00a8\u0006'"}, d2={"Lorg/cobalt/internal/ui/components/settings/UICommandHotkeySetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "mouseReleased", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "keyCode", "", "keyName", "(Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;I)Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "inputHandler", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "focused", "Z", "dragging", "isListening", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUICommandHotkeySetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UICommandHotkeySetting.kt\norg/cobalt/internal/ui/components/settings/UICommandHotkeySetting\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,245:1\n1#2:246\n6#3:247\n6#3:248\n*S KotlinDebug\n*F\n+ 1 UICommandHotkeySetting.kt\norg/cobalt/internal/ui/components/settings/UICommandHotkeySetting\n*L\n108#1:247\n129#1:248\n*E\n"})
public final class UICommandHotkeySetting
extends UIComponent {
    @NotNull
    private final CommandHotkeySetting setting;
    @NotNull
    private final TextInputHandler inputHandler;
    private boolean focused;
    private boolean dragging;
    private boolean isListening;

    public UICommandHotkeySetting(@NotNull CommandHotkeySetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.inputHandler = new TextInputHandler(((CommandHotkeyValue)this.setting.getValue()).getCommand(), 0, 2, null);
    }

    @Override
    public void render() {
        String string;
        int borderColor;
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), theme.getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, theme.getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + 14.5f, 15.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + 32.0f, 12.0f, theme.getTextSecondary(), null, 32, null);
        float inputX = this.getX() + this.getWidth() - 280.0f;
        float inputY = this.getY() + 15.0f;
        float inputW = 260.0f;
        float inputH = 30.0f;
        int n = borderColor = this.focused ? theme.getAccent() : theme.getInputBorder();
        if (this.isListening) {
            string = "Listening...";
        } else {
            KeyBind it = ((CommandHotkeyValue)this.setting.getValue()).getKeyBind();
            boolean bl = false;
            string = this.keyName(this.setting, it.getKeyCode());
        }
        String keyText = string;
        float keyTextWidth = NVGRenderer.textWidth$default(keyText, 13.0f, null, 4, null);
        float keyWidth = RangesKt.coerceAtLeast((float)(keyTextWidth + 20.0f), (float)90.0f);
        float keyX = inputX - keyWidth - 10.0f;
        float keyY = inputY;
        float keyH = inputH;
        NVGRenderer.rect(keyX, keyY, keyWidth, keyH, theme.getControlBg(), 6.0f);
        NVGRenderer.hollowRect(keyX, keyY, keyWidth, keyH, 1.0f, theme.getControlBorder(), 6.0f);
        NVGRenderer.text$default(keyText, keyX + keyWidth / 2.0f - keyTextWidth / 2.0f, keyY + 8.5f, 13.0f, this.isListening ? theme.getTextSecondary() : theme.getText(), null, 32, null);
        NVGRenderer.rect(inputX, inputY, inputW, inputH, theme.getInputBg(), 5.0f);
        NVGRenderer.hollowRect(inputX, inputY, inputW, inputH, 2.0f, borderColor, 5.0f);
        float textX = inputX + 10.0f;
        float textY = inputY + 9.0f;
        if (this.focused) {
            this.inputHandler.updateScroll(240.0f, 13.0f);
        }
        NVGRenderer.pushScissor(inputX + 10.0f, inputY, 240.0f, 30.0f);
        if (this.focused) {
            this.inputHandler.renderSelection(textX, textY, 13.0f, 13.0f, theme.getSelection());
        }
        NVGRenderer.text$default(this.inputHandler.getText(), textX - this.inputHandler.getTextOffset(), textY, 13.0f, theme.getText(), null, 32, null);
        if (this.focused) {
            this.inputHandler.renderCursor(textX, textY, 13.0f, theme.getText());
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseClicked(int button) {
        String string;
        if (button != 0) {
            return false;
        }
        float inputX = this.getX() + this.getWidth() - 280.0f;
        float inputY = this.getY() + 15.0f;
        float inputW = 260.0f;
        float inputH = 30.0f;
        if (this.isListening) {
            string = "Listening...";
        } else {
            KeyBind it = ((CommandHotkeyValue)this.setting.getValue()).getKeyBind();
            boolean bl = false;
            string = this.keyName(this.setting, it.getKeyCode());
        }
        String keyText = string;
        float keyTextWidth = NVGRenderer.textWidth$default(keyText, 13.0f, null, 4, null);
        float keyWidth = RangesKt.coerceAtLeast((float)(keyTextWidth + 20.0f), (float)90.0f);
        float keyX = inputX - keyWidth - 10.0f;
        float keyY = inputY;
        if (ExtensionsKt.isHoveringOver(keyX, keyY, keyWidth, inputH)) {
            this.isListening = true;
            this.focused = false;
            return true;
        }
        if (ExtensionsKt.isHoveringOver(inputX, inputY, inputW, inputH)) {
            this.focused = true;
            this.dragging = true;
            boolean $i$f$getMouseX = false;
            this.inputHandler.startSelection((float)class_310.method_1551().field_1729.method_1603(), inputX + 10.0f, 13.0f);
            return true;
        }
        if (this.focused) {
            ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
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
            ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (this.isListening) {
            int keyCode = class_3675.method_15985((class_11908)input).method_1444();
            KeyBind keyBind = ((CommandHotkeyValue)this.setting.getValue()).getKeyBind();
            keyBind.setKeyCode(switch (keyCode) {
                case 256, 259 -> -1;
                case 257 -> ((CommandHotkeyValue)this.setting.getValue()).getKeyBind().getKeyCode();
                default -> keyCode;
            });
            this.isListening = false;
            return true;
        }
        if (!this.focused) {
            return false;
        }
        boolean ctrl = (input.comp_4797() & 2) != 0;
        boolean shift = (input.comp_4797() & 1) != 0;
        switch (input.comp_4795()) {
            case 256: 
            case 257: {
                ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
                this.focused = false;
                return true;
            }
            case 259: {
                this.inputHandler.backspace();
                ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
                return true;
            }
            case 261: {
                this.inputHandler.delete();
                ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
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
                    ((CommandHotkeyValue)this.setting.getValue()).setCommand(this.inputHandler.getText());
                }
                return true;
            }
        }
        return false;
    }

    private final String keyName(CommandHotkeySetting $this$keyName, int keyCode) {
        String string;
        switch (keyCode) {
            case -1: {
                string = "None";
                break;
            }
            case 343: 
            case 347: {
                string = "Super";
                break;
            }
            case 340: {
                string = "Left Shift";
                break;
            }
            case 344: {
                string = "Right Shift";
                break;
            }
            case 341: {
                string = "Left Control";
                break;
            }
            case 345: {
                string = "Right Control";
                break;
            }
            case 342: {
                string = "Left Alt";
                break;
            }
            case 346: {
                string = "Right Alt";
                break;
            }
            case 32: {
                string = "Space";
                break;
            }
            case 257: {
                string = "Enter";
                break;
            }
            case 258: {
                string = "Tab";
                break;
            }
            case 280: {
                string = "Caps Lock";
                break;
            }
            default: {
                string = GLFW.glfwGetKeyName((int)keyCode, (int)0);
                if (string != null) {
                    String string2 = string.toUpperCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
                    string = string2;
                    if (string2 != null) break;
                }
                string = "Unknown";
            }
        }
        return string;
    }
}

