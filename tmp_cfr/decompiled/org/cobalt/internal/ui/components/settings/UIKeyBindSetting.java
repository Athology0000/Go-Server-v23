/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_11908
 *  net.minecraft.class_3675
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_11908;
import net.minecraft.class_3675;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.helper.KeyBind;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000eH\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0012R\u0016\u0010\u0013\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIKeyBindSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/KeyBindSetting;)V", "", "render", "()V", "", "button", "", "mouseClicked", "(I)Z", "Lnet/minecraft/class_11908;", "input", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "isListening", "Z", "cobalt"})
public final class UIKeyBindSetting
extends UIComponent {
    @NotNull
    private final KeyBindSetting setting;
    private boolean isListening;

    public UIKeyBindSetting(@NotNull KeyBindSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
    }

    @Override
    public void render() {
        String string;
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        if (this.isListening) {
            string = "Listening...";
        } else {
            String string2 = this.setting.getKeyName().toUpperCase(Locale.ROOT);
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
        }
        String text = string;
        float textWidth = NVGRenderer.textWidth$default(text, 15.0f, null, 4, null);
        NVGRenderer.rect(this.getX() + this.getWidth() - textWidth - 40.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, textWidth + 20.0f, 25.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 5.0f);
        NVGRenderer.hollowRect(this.getX() + this.getWidth() - textWidth - 40.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, textWidth + 20.0f, 25.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
        NVGRenderer.text$default(text, this.getX() + this.getWidth() - textWidth - 30.0f, this.getY() + this.getHeight() / 2.0f - 7.5f, 15.0f, this.isListening ? ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary() : ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
    }

    @Override
    public boolean mouseClicked(int button) {
        String string;
        if (this.isListening) {
            string = "Listening...";
        } else {
            String string2 = this.setting.getKeyName().toUpperCase(Locale.ROOT);
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toUpperCase(...)");
        }
        String text = string;
        float textWidth = NVGRenderer.textWidth$default(text, 15.0f, null, 4, null);
        if (this.isListening) {
            ((KeyBind)this.setting.getValue()).setKeyCode(button);
            this.isListening = false;
            return true;
        }
        if (ExtensionsKt.isHoveringOver(this.getX() + this.getWidth() - textWidth - 40.0f, this.getY() + this.getHeight() / 2.0f - 12.5f, textWidth + 20.0f, 25.0f) && button == 0) {
            this.isListening = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!this.isListening) {
            return false;
        }
        int keyCode = class_3675.method_15985((class_11908)input).method_1444();
        KeyBind keyBind = (KeyBind)this.setting.getValue();
        keyBind.setKeyCode(switch (keyCode) {
            case 256, 259 -> -1;
            case 257 -> ((KeyBind)this.setting.getValue()).getKeyCode();
            default -> keyCode;
        });
        this.isListening = false;
        return true;
    }
}

