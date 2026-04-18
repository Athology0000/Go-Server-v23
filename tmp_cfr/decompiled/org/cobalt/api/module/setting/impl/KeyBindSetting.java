/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.lwjgl.glfw.GLFW
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.util.helper.KeyBind;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0015\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "Lorg/cobalt/api/module/setting/Setting;", "Lorg/cobalt/api/util/helper/KeyBind;", "", "name", "description", "defaultValue", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/api/util/helper/KeyBind;)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Lorg/cobalt/api/util/helper/KeyBind;", "getDefaultValue", "()Lorg/cobalt/api/util/helper/KeyBind;", "getKeyName", "()Ljava/lang/String;", "keyName", "cobalt"})
public final class KeyBindSetting
extends Setting<KeyBind> {
    @NotNull
    private final KeyBind defaultValue;

    public KeyBindSetting(@NotNull String name, @NotNull String description, @NotNull KeyBind defaultValue) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter((Object)defaultValue, (String)"defaultValue");
        super(name, description, defaultValue);
        this.defaultValue = defaultValue;
    }

    @Override
    @NotNull
    public KeyBind getDefaultValue() {
        return this.defaultValue;
    }

    @NotNull
    public final String getKeyName() {
        String string;
        switch (((KeyBind)this.getValue()).getKeyCode()) {
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
                string = GLFW.glfwGetKeyName((int)((KeyBind)this.getValue()).getKeyCode(), (int)0);
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

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        ((KeyBind)this.getValue()).setKeyCode(element.getAsInt());
    }

    @Override
    @NotNull
    public JsonElement write() {
        return (JsonElement)new JsonPrimitive((Number)((KeyBind)this.getValue()).getKeyCode());
    }
}

