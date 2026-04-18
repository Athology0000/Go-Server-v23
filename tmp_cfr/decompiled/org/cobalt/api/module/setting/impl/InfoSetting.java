/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\b\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B#\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0016\u001a\u00020\u00028\u0016X\u0096D\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0010\u001a\u0004\b\u0017\u0010\u0012\u00a8\u0006\u0018"}, d2={"Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "name", "text", "Lorg/cobalt/api/module/setting/impl/InfoType;", "type", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/api/module/setting/impl/InfoType;)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Ljava/lang/String;", "getText", "()Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/InfoType;", "getType", "()Lorg/cobalt/api/module/setting/impl/InfoType;", "defaultValue", "getDefaultValue", "cobalt"})
public final class InfoSetting
extends Setting<String> {
    @NotNull
    private final String text;
    @NotNull
    private final InfoType type;
    @NotNull
    private final String defaultValue;

    public InfoSetting(@Nullable String name, @NotNull String text, @NotNull InfoType type) {
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        String string = name;
        if (string == null) {
            string = "";
        }
        super(string, "Info", "");
        this.text = text;
        this.type = type;
        this.defaultValue = "";
    }

    public /* synthetic */ InfoSetting(String string, String string2, InfoType infoType, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            infoType = InfoType.INFO;
        }
        this(string, string2, infoType);
    }

    @NotNull
    public final String getText() {
        return this.text;
    }

    @NotNull
    public final InfoType getType() {
        return this.type;
    }

    @Override
    @NotNull
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
    }

    @Override
    @NotNull
    public JsonElement write() {
        return (JsonElement)new JsonPrimitive("");
    }
}

