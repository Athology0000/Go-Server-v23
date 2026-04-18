/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0011\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B?\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0007\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\rH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\rH\u0016\u00a2\u0006\u0004\b\u0011\u0010\u0012J\r\u0010\u0013\u001a\u00020\t\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0006\u001a\u00020\u00038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0015R\u001c\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0016R\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0016R\u0011\u0010\u0019\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\u001a\u001a\u00020\u00028\u0016X\u0096D\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001d\u00a8\u0006\u001e"}, d2={"Lorg/cobalt/api/module/setting/impl/ActionSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "", "name", "description", "staticLabel", "Lkotlin/Function0;", "buttonLabelProvider", "", "onClick", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "Lcom/google/gson/JsonElement;", "element", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "trigger", "()V", "Ljava/lang/String;", "Lkotlin/jvm/functions/Function0;", "getButtonLabel", "()Ljava/lang/String;", "buttonLabel", "defaultValue", "Z", "getDefaultValue", "()Ljava/lang/Boolean;", "cobalt"})
public final class ActionSetting
extends Setting<Boolean> {
    @NotNull
    private final String staticLabel;
    @Nullable
    private final Function0<String> buttonLabelProvider;
    @NotNull
    private final Function0<Unit> onClick;
    private final boolean defaultValue;

    public ActionSetting(@NotNull String name, @NotNull String description, @NotNull String staticLabel, @Nullable Function0<String> buttonLabelProvider, @NotNull Function0<Unit> onClick) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter((Object)staticLabel, (String)"staticLabel");
        Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
        super(name, description, false);
        this.staticLabel = staticLabel;
        this.buttonLabelProvider = buttonLabelProvider;
        this.onClick = onClick;
    }

    public /* synthetic */ ActionSetting(String string, String string2, String string3, Function0 function0, Function0 function02, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 8) != 0) {
            function0 = null;
        }
        this(string, string2, string3, (Function0<String>)function0, (Function0<Unit>)function02);
    }

    @NotNull
    public final String getButtonLabel() {
        Object object = this.buttonLabelProvider;
        if (object == null || (object = (String)object.invoke()) == null) {
            object = this.staticLabel;
        }
        return object;
    }

    @Override
    @NotNull
    public Boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
    }

    @Override
    @NotNull
    public JsonElement write() {
        return (JsonElement)new JsonPrimitive(Boolean.valueOf(false));
    }

    public final void trigger() {
        this.onClick.invoke();
    }
}

