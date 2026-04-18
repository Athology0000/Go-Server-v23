/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.module.setting.Setting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0012\u0018\u00002\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00020\u0001B;\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016\u00a2\u0006\u0004\b\u0011\u0010\u0012J#\u0010\u0014\u001a\u00020\u000e2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0002H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u0003*\u00020\u0003H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\b\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR\u0017\u0010\t\u001a\u00020\u00038\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u0018\u001a\u0004\b\u001b\u0010\u001aR&\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/api/module/setting/impl/RangeSetting;", "Lorg/cobalt/api/module/setting/Setting;", "Lkotlin/Pair;", "", "", "name", "description", "default", "min", "max", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/Pair;DD)V", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "newValue", "setAndClampValue", "(Lkotlin/Pair;)V", "roundTo", "(D)D", "D", "getMin", "()D", "getMax", "defaultValue", "Lkotlin/Pair;", "getDefaultValue", "()Lkotlin/Pair;", "cobalt"})
public final class RangeSetting
extends Setting<Pair<? extends Double, ? extends Double>> {
    private final double min;
    private final double max;
    @NotNull
    private final Pair<Double, Double> defaultValue;

    public RangeSetting(@NotNull String name, @NotNull String description, @NotNull Pair<Double, Double> pair, double min, double max) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter(pair, (String)"default");
        super(name, description, pair);
        this.min = min;
        this.max = max;
        this.defaultValue = pair;
    }

    public final double getMin() {
        return this.min;
    }

    public final double getMax() {
        return this.max;
    }

    @Override
    @NotNull
    public Pair<Double, Double> getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        Intrinsics.checkNotNullParameter((Object)element, (String)"element");
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonElement jsonElement = obj.get("start");
            double start = jsonElement != null ? jsonElement.getAsDouble() : ((Number)this.getDefaultValue().getFirst()).doubleValue();
            JsonElement jsonElement2 = obj.get("end");
            double end = jsonElement2 != null ? jsonElement2.getAsDouble() : ((Number)this.getDefaultValue().getSecond()).doubleValue();
            this.setAndClampValue((Pair<Double, Double>)new Pair((Object)start, (Object)end));
        }
    }

    @Override
    @NotNull
    public JsonElement write() {
        JsonObject jsonObject;
        JsonObject $this$write_u24lambda_u240 = jsonObject = new JsonObject();
        boolean bl = false;
        $this$write_u24lambda_u240.add("start", (JsonElement)new JsonPrimitive((Number)this.roundTo(((Number)((Pair)this.getValue()).getFirst()).doubleValue())));
        $this$write_u24lambda_u240.add("end", (JsonElement)new JsonPrimitive((Number)this.roundTo(((Number)((Pair)this.getValue()).getSecond()).doubleValue())));
        return (JsonElement)jsonObject;
    }

    private final void setAndClampValue(Pair<Double, Double> newValue) {
        double v1 = RangesKt.coerceIn((double)((Number)newValue.getFirst()).doubleValue(), (double)this.min, (double)this.max);
        double v2 = RangesKt.coerceIn((double)((Number)newValue.getSecond()).doubleValue(), (double)this.min, (double)this.max);
        this.setValue(new Pair((Object)Math.min(v1, v2), (Object)Math.max(v1, v2)));
    }

    private final double roundTo(double $this$roundTo) {
        return new BigDecimal($this$roundTo).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}

