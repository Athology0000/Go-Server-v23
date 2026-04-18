/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.properties.PropertyDelegateProvider
 *  kotlin.properties.ReadWriteProperty
 *  kotlin.reflect.KProperty
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting;

import com.google.gson.JsonElement;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.PropertyDelegateProvider;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.cobalt.api.module.setting.SettingsContainer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0014\b&\u0018\u0000 (*\u0004\b\u0000\u0010\u00012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00028\u00000\u00022\u001a\u0012\u0004\u0012\u00020\u0003\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00028\u00000\u00020\u0004:\u0001(B\u001f\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00028\u0000\u00a2\u0006\u0004\b\t\u0010\nJ1\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00028\u00000\u00022\u0006\u0010\u000b\u001a\u00020\u00032\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\fH\u0096\u0082\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ%\u0010\u0010\u001a\u00028\u00002\u0006\u0010\u000b\u001a\u00020\u00032\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\fH\u0096\u0082\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J-\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u000b\u001a\u00020\u00032\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\f2\u0006\u0010\b\u001a\u00028\u0000H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0015H&\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u000f\u0010\u0019\u001a\u00020\u0015H&\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001b\u001a\u0004\b\u001e\u0010\u001dR\"\u0010\b\u001a\u00028\u00008\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0012\n\u0004\b\b\u0010\u001f\u001a\u0004\b\u0010\u0010 \"\u0004\b\u0013\u0010!R\"\u0010\"\u001a\u00020\u00058\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0012\n\u0004\b\"\u0010\u001b\u001a\u0004\b#\u0010\u001d\"\u0004\b$\u0010%R\u0014\u0010'\u001a\u00028\u00008VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b&\u0010 \u00a8\u0006)"}, d2={"Lorg/cobalt/api/module/setting/Setting;", "T", "Lkotlin/properties/ReadWriteProperty;", "Lorg/cobalt/api/module/setting/SettingsContainer;", "Lkotlin/properties/PropertyDelegateProvider;", "", "name", "description", "value", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", "thisRef", "Lkotlin/reflect/KProperty;", "property", "provideDelegate", "(Lorg/cobalt/api/module/setting/SettingsContainer;Lkotlin/reflect/KProperty;)Lkotlin/properties/ReadWriteProperty;", "getValue", "(Lorg/cobalt/api/module/setting/SettingsContainer;Lkotlin/reflect/KProperty;)Ljava/lang/Object;", "", "setValue", "(Lorg/cobalt/api/module/setting/SettingsContainer;Lkotlin/reflect/KProperty;Ljava/lang/Object;)V", "Lcom/google/gson/JsonElement;", "element", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "Ljava/lang/String;", "getName", "()Ljava/lang/String;", "getDescription", "Ljava/lang/Object;", "()Ljava/lang/Object;", "(Ljava/lang/Object;)V", "uiGroup", "getUiGroup", "setUiGroup", "(Ljava/lang/String;)V", "getDefaultValue", "defaultValue", "Companion", "cobalt"})
public abstract class Setting<T>
implements ReadWriteProperty<SettingsContainer, T>,
PropertyDelegateProvider<SettingsContainer, ReadWriteProperty<? super SettingsContainer, T>> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    private T value;
    @NotNull
    private String uiGroup;
    @NotNull
    public static final String DEFAULT_UI_GROUP = "General";

    public Setting(@NotNull String name, @NotNull String description, T value) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        this.name = name;
        this.description = description;
        this.value = value;
        this.uiGroup = DEFAULT_UI_GROUP;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getDescription() {
        return this.description;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T t) {
        this.value = t;
    }

    @NotNull
    public String getUiGroup() {
        return this.uiGroup;
    }

    public void setUiGroup(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        this.uiGroup = string;
    }

    public T getDefaultValue() {
        return this.getValue();
    }

    @NotNull
    public ReadWriteProperty<SettingsContainer, T> provideDelegate(@NotNull SettingsContainer thisRef, @NotNull KProperty<?> property) {
        Intrinsics.checkNotNullParameter((Object)thisRef, (String)"thisRef");
        Intrinsics.checkNotNullParameter(property, (String)"property");
        Setting[] settingArray = new Setting[]{this};
        thisRef.addSetting(settingArray);
        return this;
    }

    public T getValue(@NotNull SettingsContainer thisRef, @NotNull KProperty<?> property) {
        Intrinsics.checkNotNullParameter((Object)thisRef, (String)"thisRef");
        Intrinsics.checkNotNullParameter(property, (String)"property");
        return this.getValue();
    }

    public void setValue(@NotNull SettingsContainer thisRef, @NotNull KProperty<?> property, T value) {
        Intrinsics.checkNotNullParameter((Object)thisRef, (String)"thisRef");
        Intrinsics.checkNotNullParameter(property, (String)"property");
        this.setValue(value);
    }

    public abstract void read(@NotNull JsonElement var1);

    @NotNull
    public abstract JsonElement write();

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/api/module/setting/Setting$Companion;", "", "<init>", "()V", "", "DEFAULT_UI_GROUP", "Ljava/lang/String;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

