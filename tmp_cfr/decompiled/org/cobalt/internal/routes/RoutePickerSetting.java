/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.routes;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.RouteType;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0010\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016R$\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00028V@VX\u0096\u000e\u00a2\u0006\f\u001a\u0004\b\u0018\u0010\u0016\"\u0004\b\u0019\u0010\u001a\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/routes/RoutePickerSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "name", "description", "Lorg/cobalt/internal/routes/RouteType;", "routeType", "slotKey", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/internal/routes/RouteType;Ljava/lang/String;)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Lorg/cobalt/internal/routes/RouteType;", "getRouteType", "()Lorg/cobalt/internal/routes/RouteType;", "Ljava/lang/String;", "getSlotKey", "()Ljava/lang/String;", "v", "getValue", "setValue", "(Ljava/lang/String;)V", "value", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRoutePickerSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RoutePickerSetting.kt\norg/cobalt/internal/routes/RoutePickerSetting\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,27:1\n1#2:28\n*E\n"})
public final class RoutePickerSetting
extends Setting<String> {
    @NotNull
    private final RouteType routeType;
    @NotNull
    private final String slotKey;

    public RoutePickerSetting(@NotNull String name, @NotNull String description, @NotNull RouteType routeType, @NotNull String slotKey) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter((Object)((Object)routeType), (String)"routeType");
        Intrinsics.checkNotNullParameter((Object)slotKey, (String)"slotKey");
        super(name, description, "");
        this.routeType = routeType;
        this.slotKey = slotKey;
    }

    @NotNull
    public final RouteType getRouteType() {
        return this.routeType;
    }

    @NotNull
    public final String getSlotKey() {
        return this.slotKey;
    }

    @Override
    @NotNull
    public String getValue() {
        String string = RouteStore.INSTANCE.getSlotRoute(this.slotKey);
        if (string == null) {
            string = "";
        }
        return string;
    }

    @Override
    public void setValue(@NotNull String v) {
        CharSequence charSequence;
        Intrinsics.checkNotNullParameter((Object)v, (String)"v");
        RouteStore routeStore = RouteStore.INSTANCE;
        String string = this.slotKey;
        CharSequence charSequence2 = v;
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            String string2 = string;
            RouteStore routeStore2 = routeStore;
            boolean bl = false;
            Object var6_6 = null;
            routeStore = routeStore2;
            string = string2;
            charSequence = var6_6;
        } else {
            charSequence = charSequence2;
        }
        routeStore.setSlotRoute(string, (String)charSequence);
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
    }

    @Override
    @NotNull
    public JsonElement write() {
        JsonNull jsonNull = JsonNull.INSTANCE;
        Intrinsics.checkNotNullExpressionValue((Object)jsonNull, (String)"INSTANCE");
        return (JsonElement)jsonNull;
    }
}

