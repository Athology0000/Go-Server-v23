/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B9\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\b\b\u0002\u0010\t\u001a\u00020\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u0013\u001a\u0004\b\u0016\u0010\u0015R\u0017\u0010\t\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u0013\u001a\u0004\b\u0017\u0010\u0015R\u001a\u0010\u0006\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0013\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "", "name", "description", "defaultValue", "min", "max", "step", "<init>", "(Ljava/lang/String;Ljava/lang/String;DDDD)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "D", "getMin", "()D", "getMax", "getStep", "getDefaultValue", "()Ljava/lang/Double;", "cobalt"})
public final class SliderSetting
extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;
    private final double defaultValue;

    public SliderSetting(@NotNull String name, @NotNull String description, double defaultValue, double min, double max, double step) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = defaultValue;
    }

    public /* synthetic */ SliderSetting(String string, String string2, double d, double d2, double d3, double d4, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 0x20) != 0) {
            d4 = 0.0;
        }
        this(string, string2, d, d2, d3, d4);
    }

    public final double getMin() {
        return this.min;
    }

    public final double getMax() {
        return this.max;
    }

    public final double getStep() {
        return this.step;
    }

    @Override
    @NotNull
    public Double getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        this.setValue(RangesKt.coerceIn((double)element.getAsDouble(), (double)this.min, (double)this.max));
    }

    @Override
    @NotNull
    public JsonElement write() {
        return (JsonElement)new JsonPrimitive((Number)this.getValue());
    }
}

