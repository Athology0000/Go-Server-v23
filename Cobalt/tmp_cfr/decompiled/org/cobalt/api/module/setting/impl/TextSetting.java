/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0017\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eR\u001a\u0010\u0005\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0005\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2={"Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "name", "description", "defaultValue", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Ljava/lang/String;", "getDefaultValue", "()Ljava/lang/String;", "cobalt"})
public final class TextSetting
extends Setting<String> {
    @NotNull
    private final String defaultValue;

    public TextSetting(@NotNull String name, @NotNull String description, @NotNull String defaultValue) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter((Object)defaultValue, (String)"defaultValue");
        super(name, description, defaultValue);
        this.defaultValue = defaultValue;
    }

    @Override
    @NotNull
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        String string = element.getAsString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getAsString(...)");
        this.setValue(string);
    }

    @Override
    @NotNull
    public JsonElement write() {
        return (JsonElement)new JsonPrimitive((String)this.getValue());
    }
}

