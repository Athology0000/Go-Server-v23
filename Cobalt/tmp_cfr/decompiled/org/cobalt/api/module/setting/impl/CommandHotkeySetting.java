/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CommandHotkeyValue;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B!\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;", "Lorg/cobalt/api/module/setting/Setting;", "Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;", "", "name", "description", "defaultValue", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;", "getDefaultValue", "()Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;", "cobalt"})
public final class CommandHotkeySetting
extends Setting<CommandHotkeyValue> {
    @NotNull
    private final CommandHotkeyValue defaultValue;

    public CommandHotkeySetting(@NotNull String name, @NotNull String description, @NotNull CommandHotkeyValue defaultValue) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter((Object)defaultValue, (String)"defaultValue");
        super(name, description, defaultValue);
        this.defaultValue = defaultValue;
    }

    public /* synthetic */ CommandHotkeySetting(String string, String string2, CommandHotkeyValue commandHotkeyValue, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            commandHotkeyValue = new CommandHotkeyValue(null, null, 3, null);
        }
        this(string, string2, commandHotkeyValue);
    }

    @Override
    @NotNull
    public CommandHotkeyValue getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        JsonElement jsonElement = obj.get("keyCode");
        int keyCode = jsonElement != null ? jsonElement.getAsInt() : -1;
        Object object = obj.get("command");
        if (object == null || (object = object.getAsString()) == null) {
            object = "";
        }
        Object command = object;
        ((CommandHotkeyValue)this.getValue()).getKeyBind().setKeyCode(keyCode);
        ((CommandHotkeyValue)this.getValue()).setCommand((String)command);
    }

    @Override
    @NotNull
    public JsonElement write() {
        JsonObject obj = new JsonObject();
        obj.addProperty("keyCode", (Number)((CommandHotkeyValue)this.getValue()).getKeyBind().getKeyCode());
        obj.addProperty("command", ((CommandHotkeyValue)this.getValue()).getCommand());
        return (JsonElement)obj;
    }
}

