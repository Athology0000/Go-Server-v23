/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.internal.routes.RoutePoint;
import org.cobalt.internal.routes.RoutePointType;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.SubRouteKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u000e\b\u0086\b\u0018\u0000 22\u00020\u0001:\u00012BG\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\u0004\b\u000b\u0010\fJ\u001b\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000f\u0010\u0010J#\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\r2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\u0004\b\u0015\u0010\u0016J\r\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0016\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0016\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003\u00a2\u0006\u0004\b \u0010\u001fJ\u0016\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003\u00a2\u0006\u0004\b!\u0010\u001fJT\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\"\u0010#J\u001b\u0010&\u001a\u00020%2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b&\u0010'J\u0011\u0010(\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b(\u0010\u0016J\u0011\u0010)\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b)\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010*\u001a\u0004\b+\u0010\u001bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010,\u001a\u0004\b-\u0010\u001dR\u001d\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006\u00a2\u0006\f\n\u0004\b\b\u0010.\u001a\u0004\b/\u0010\u001fR\u001d\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006\u00a2\u0006\f\n\u0004\b\t\u0010.\u001a\u0004\b0\u0010\u001fR\u001d\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006\u00a2\u0006\f\n\u0004\b\n\u0010.\u001a\u0004\b1\u0010\u001f\u00a8\u00063"}, d2={"Lorg/cobalt/internal/routes/SavedRoute;", "", "", "name", "Lorg/cobalt/internal/routes/RouteType;", "type", "", "Lorg/cobalt/internal/routes/RoutePoint;", "travelRoute", "loopOrArea", "points", "<init>", "(Ljava/lang/String;Lorg/cobalt/internal/routes/RouteType;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "Lorg/cobalt/internal/routes/SubRouteKey;", "sub", "getSubRoute", "(Lorg/cobalt/internal/routes/SubRouteKey;)Ljava/util/List;", "newPoints", "withSubRoute", "(Lorg/cobalt/internal/routes/SubRouteKey;Ljava/util/List;)Lorg/cobalt/internal/routes/SavedRoute;", "", "totalPoints", "()I", "Lcom/google/gson/JsonObject;", "toJson", "()Lcom/google/gson/JsonObject;", "component1", "()Ljava/lang/String;", "component2", "()Lorg/cobalt/internal/routes/RouteType;", "component3", "()Ljava/util/List;", "component4", "component5", "copy", "(Ljava/lang/String;Lorg/cobalt/internal/routes/RouteType;Ljava/util/List;Ljava/util/List;Ljava/util/List;)Lorg/cobalt/internal/routes/SavedRoute;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getName", "Lorg/cobalt/internal/routes/RouteType;", "getType", "Ljava/util/List;", "getTravelRoute", "getLoopOrArea", "getPoints", "Companion", "cobalt"})
public final class SavedRoute {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String name;
    @NotNull
    private final RouteType type;
    @NotNull
    private final List<RoutePoint> travelRoute;
    @NotNull
    private final List<RoutePoint> loopOrArea;
    @NotNull
    private final List<RoutePoint> points;

    public SavedRoute(@NotNull String name, @NotNull RouteType type, @NotNull List<RoutePoint> travelRoute, @NotNull List<RoutePoint> loopOrArea, @NotNull List<RoutePoint> points) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter(travelRoute, (String)"travelRoute");
        Intrinsics.checkNotNullParameter(loopOrArea, (String)"loopOrArea");
        Intrinsics.checkNotNullParameter(points, (String)"points");
        this.name = name;
        this.type = type;
        this.travelRoute = travelRoute;
        this.loopOrArea = loopOrArea;
        this.points = points;
    }

    public /* synthetic */ SavedRoute(String string, RouteType routeType, List list, List list2, List list3, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            list = CollectionsKt.emptyList();
        }
        if ((n & 8) != 0) {
            list2 = CollectionsKt.emptyList();
        }
        if ((n & 0x10) != 0) {
            list3 = CollectionsKt.emptyList();
        }
        this(string, routeType, list, list2, list3);
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final RouteType getType() {
        return this.type;
    }

    @NotNull
    public final List<RoutePoint> getTravelRoute() {
        return this.travelRoute;
    }

    @NotNull
    public final List<RoutePoint> getLoopOrArea() {
        return this.loopOrArea;
    }

    @NotNull
    public final List<RoutePoint> getPoints() {
        return this.points;
    }

    @NotNull
    public final List<RoutePoint> getSubRoute(@NotNull SubRouteKey sub) {
        Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
        return switch (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()]) {
            case 1 -> this.travelRoute;
            case 2, 3 -> this.loopOrArea;
            case 4 -> this.points;
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    @NotNull
    public final SavedRoute withSubRoute(@NotNull SubRouteKey sub, @NotNull List<RoutePoint> newPoints) {
        Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
        Intrinsics.checkNotNullParameter(newPoints, (String)"newPoints");
        return switch (WhenMappings.$EnumSwitchMapping$0[sub.ordinal()]) {
            case 1 -> SavedRoute.copy$default(this, null, null, newPoints, null, null, 27, null);
            case 2, 3 -> SavedRoute.copy$default(this, null, null, null, newPoints, null, 23, null);
            case 4 -> SavedRoute.copy$default(this, null, null, null, null, newPoints, 15, null);
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    public final int totalPoints() {
        return this.travelRoute.size() + this.loopOrArea.size() + this.points.size();
    }

    @NotNull
    public final JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.name);
        obj.addProperty("type", this.type.name());
        switch (WhenMappings.$EnumSwitchMapping$1[this.type.ordinal()]) {
            case 1: {
                obj.add("travelRoute", (JsonElement)SavedRoute.Companion.pointsToJson(this.travelRoute));
                obj.add("loopRoute", (JsonElement)SavedRoute.Companion.pointsToJson(this.loopOrArea));
                break;
            }
            case 2: {
                obj.add("travelRoute", (JsonElement)SavedRoute.Companion.pointsToJson(this.travelRoute));
                obj.add("patrolArea", (JsonElement)SavedRoute.Companion.pointsToJson(this.loopOrArea));
                break;
            }
            default: {
                obj.add("points", (JsonElement)SavedRoute.Companion.pointsToJson(this.points));
            }
        }
        return obj;
    }

    @NotNull
    public final String component1() {
        return this.name;
    }

    @NotNull
    public final RouteType component2() {
        return this.type;
    }

    @NotNull
    public final List<RoutePoint> component3() {
        return this.travelRoute;
    }

    @NotNull
    public final List<RoutePoint> component4() {
        return this.loopOrArea;
    }

    @NotNull
    public final List<RoutePoint> component5() {
        return this.points;
    }

    @NotNull
    public final SavedRoute copy(@NotNull String name, @NotNull RouteType type, @NotNull List<RoutePoint> travelRoute, @NotNull List<RoutePoint> loopOrArea, @NotNull List<RoutePoint> points) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter(travelRoute, (String)"travelRoute");
        Intrinsics.checkNotNullParameter(loopOrArea, (String)"loopOrArea");
        Intrinsics.checkNotNullParameter(points, (String)"points");
        return new SavedRoute(name, type, travelRoute, loopOrArea, points);
    }

    public static /* synthetic */ SavedRoute copy$default(SavedRoute savedRoute, String string, RouteType routeType, List list, List list2, List list3, int n, Object object) {
        if ((n & 1) != 0) {
            string = savedRoute.name;
        }
        if ((n & 2) != 0) {
            routeType = savedRoute.type;
        }
        if ((n & 4) != 0) {
            list = savedRoute.travelRoute;
        }
        if ((n & 8) != 0) {
            list2 = savedRoute.loopOrArea;
        }
        if ((n & 0x10) != 0) {
            list3 = savedRoute.points;
        }
        return savedRoute.copy(string, routeType, list, list2, list3);
    }

    @NotNull
    public String toString() {
        return "SavedRoute(name=" + this.name + ", type=" + this.type + ", travelRoute=" + this.travelRoute + ", loopOrArea=" + this.loopOrArea + ", points=" + this.points + ")";
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = result * 31 + this.type.hashCode();
        result = result * 31 + ((Object)this.travelRoute).hashCode();
        result = result * 31 + ((Object)this.loopOrArea).hashCode();
        result = result * 31 + ((Object)this.points).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SavedRoute)) {
            return false;
        }
        SavedRoute savedRoute = (SavedRoute)other;
        if (!Intrinsics.areEqual((Object)this.name, (Object)savedRoute.name)) {
            return false;
        }
        if (this.type != savedRoute.type) {
            return false;
        }
        if (!Intrinsics.areEqual(this.travelRoute, savedRoute.travelRoute)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.loopOrArea, savedRoute.loopOrArea)) {
            return false;
        }
        return Intrinsics.areEqual(this.points, savedRoute.points);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\b\u0010\n\u001a\u0004\u0018\u00010\tH\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001d\u0010\u0010\u001a\u00020\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2={"Lorg/cobalt/internal/routes/SavedRoute$Companion;", "", "<init>", "()V", "Lcom/google/gson/JsonObject;", "json", "Lorg/cobalt/internal/routes/SavedRoute;", "fromJson", "(Lcom/google/gson/JsonObject;)Lorg/cobalt/internal/routes/SavedRoute;", "Lcom/google/gson/JsonArray;", "arr", "", "Lorg/cobalt/internal/routes/RoutePoint;", "parsePoints", "(Lcom/google/gson/JsonArray;)Ljava/util/List;", "pts", "pointsToJson", "(Ljava/util/List;)Lcom/google/gson/JsonArray;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nSavedRoute.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SavedRoute.kt\norg/cobalt/internal/routes/SavedRoute$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,127:1\n1642#2,10:128\n1915#2:138\n1916#2:140\n1652#2:141\n1915#2:142\n1916#2:144\n1#3:139\n1#3:143\n*S KotlinDebug\n*F\n+ 1 SavedRoute.kt\norg/cobalt/internal/routes/SavedRoute$Companion\n*L\n94#1:128,10\n94#1:138\n94#1:140\n94#1:141\n113#1:142\n113#1:144\n94#1:139\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @Nullable
        public final SavedRoute fromJson(@NotNull JsonObject json) {
            Object object;
            Intrinsics.checkNotNullParameter((Object)json, (String)"json");
            Object object2 = this;
            try {
                SavedRoute savedRoute;
                Companion $this$fromJson_u24lambda_u240 = object2;
                boolean bl = false;
                Object object3 = json.get("name");
                if (object3 == null || (object3 = object3.getAsString()) == null) {
                    return null;
                }
                Object name = object3;
                String string = json.get("type").getAsString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getAsString(...)");
                RouteType type = RouteType.valueOf(string);
                switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                    case 1: {
                        savedRoute = new SavedRoute((String)name, type, $this$fromJson_u24lambda_u240.parsePoints(json.getAsJsonArray("travelRoute")), $this$fromJson_u24lambda_u240.parsePoints(json.getAsJsonArray("loopRoute")), null, 16, null);
                        break;
                    }
                    case 2: {
                        savedRoute = new SavedRoute((String)name, type, $this$fromJson_u24lambda_u240.parsePoints(json.getAsJsonArray("travelRoute")), $this$fromJson_u24lambda_u240.parsePoints(json.getAsJsonArray("patrolArea")), null, 16, null);
                        break;
                    }
                    default: {
                        savedRoute = new SavedRoute((String)name, type, null, null, $this$fromJson_u24lambda_u240.parsePoints(json.getAsJsonArray("points")), 12, null);
                    }
                }
                object = Result.constructor-impl((Object)savedRoute);
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object2 = object;
            return (SavedRoute)(Result.isFailure-impl((Object)object2) ? null : object2);
        }

        /*
         * WARNING - void declaration
         */
        private final List<RoutePoint> parsePoints(JsonArray arr) {
            void $this$mapNotNullTo$iv$iv;
            if (arr == null) {
                return CollectionsKt.emptyList();
            }
            Iterable $this$mapNotNull$iv = (Iterable)arr;
            boolean $i$f$mapNotNull = false;
            Iterable iterable = $this$mapNotNull$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach = false;
            Iterator iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                RoutePoint it$iv$iv;
                Object object;
                Object element$iv$iv$iv;
                Object element$iv$iv = element$iv$iv$iv = iterator.next();
                boolean bl = false;
                JsonElement el = (JsonElement)element$iv$iv;
                boolean bl2 = false;
                Object object2 = Companion;
                try {
                    Companion $this$parsePoints_u24lambda_u240_u240 = object2;
                    boolean bl3 = false;
                    JsonObject o = el.getAsJsonObject();
                    JsonElement jsonElement = o.get("type");
                    JsonElement jsonElement2 = o.get("mx");
                    JsonElement jsonElement3 = o.get("my");
                    JsonElement jsonElement4 = o.get("mz");
                    JsonElement jsonElement5 = o.get("bid");
                    object = Result.constructor-impl((Object)new RoutePoint(RoutePointType.Companion.fromId(jsonElement != null ? jsonElement.getAsString() : null), o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt(), jsonElement2 != null ? Integer.valueOf(jsonElement2.getAsInt()) : null, jsonElement3 != null ? Integer.valueOf(jsonElement3.getAsInt()) : null, jsonElement4 != null ? Integer.valueOf(jsonElement4.getAsInt()) : null, jsonElement5 != null ? jsonElement5.getAsString() : null));
                }
                catch (Throwable throwable) {
                    object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                }
                object2 = object;
                if ((RoutePoint)(Result.isFailure-impl((Object)object2) ? null : object2) == null) continue;
                boolean bl4 = false;
                destination$iv$iv.add(it$iv$iv);
            }
            return (List)destination$iv$iv;
        }

        private final JsonArray pointsToJson(List<RoutePoint> pts) {
            JsonArray arr = new JsonArray();
            Iterable $this$forEach$iv = pts;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                int it;
                RoutePoint p = (RoutePoint)element$iv;
                boolean bl = false;
                JsonObject o = new JsonObject();
                o.addProperty("type", p.getType().getId());
                o.addProperty("x", (Number)p.getX());
                o.addProperty("y", (Number)p.getY());
                o.addProperty("z", (Number)p.getZ());
                Integer n = p.getMx();
                if (n != null) {
                    it = ((Number)n).intValue();
                    boolean bl2 = false;
                    o.addProperty("mx", (Number)it);
                }
                Integer n2 = p.getMy();
                if (n2 != null) {
                    it = ((Number)n2).intValue();
                    boolean bl3 = false;
                    o.addProperty("my", (Number)it);
                }
                Integer n3 = p.getMz();
                if (n3 != null) {
                    it = ((Number)n3).intValue();
                    boolean bl4 = false;
                    o.addProperty("mz", (Number)it);
                }
                if (p.getBlockId() != null) {
                    String it2;
                    boolean bl5 = false;
                    o.addProperty("bid", it2);
                }
                arr.add((JsonElement)o);
            }
            return arr;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @Metadata(mv={2, 3, 0}, k=3, xi=48)
        public static final class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] nArray = new int[RouteType.values().length];
                try {
                    nArray[RouteType.ORE_MINER.ordinal()] = 1;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                try {
                    nArray[RouteType.PATROL.ordinal()] = 2;
                }
                catch (NoSuchFieldError noSuchFieldError) {
                    // empty catch block
                }
                $EnumSwitchMapping$0 = nArray;
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[SubRouteKey.values().length];
            try {
                nArray[SubRouteKey.TRAVEL.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.LOOP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.AREA.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SubRouteKey.POINTS.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[RouteType.values().length];
            try {
                nArray[RouteType.ORE_MINER.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RouteType.PATROL.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

