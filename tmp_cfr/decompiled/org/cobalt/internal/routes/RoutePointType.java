/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.routes;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\u0012\b\u0086\u0081\u0002\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\rB!\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\b\u001a\u0004\b\u000b\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\b\u001a\u0004\b\f\u0010\nj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/routes/RoutePointType;", "", "", "id", "label", "icon", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "Ljava/lang/String;", "getId", "()Ljava/lang/String;", "getLabel", "getIcon", "Companion", "WALK", "WARP", "MINE", "VEIN", "LANTERN", "KILL", "cobalt"})
public final class RoutePointType
extends Enum<RoutePointType> {
    @NotNull
    public static final Companion Companion;
    @NotNull
    private final String id;
    @NotNull
    private final String label;
    @NotNull
    private final String icon;
    public static final /* enum */ RoutePointType WALK;
    public static final /* enum */ RoutePointType WARP;
    public static final /* enum */ RoutePointType MINE;
    public static final /* enum */ RoutePointType VEIN;
    public static final /* enum */ RoutePointType LANTERN;
    public static final /* enum */ RoutePointType KILL;
    private static final /* synthetic */ RoutePointType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private RoutePointType(String id, String label, String icon) {
        this.id = id;
        this.label = label;
        this.icon = icon;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final String getLabel() {
        return this.label;
    }

    @NotNull
    public final String getIcon() {
        return this.icon;
    }

    public static RoutePointType[] values() {
        return (RoutePointType[])$VALUES.clone();
    }

    public static RoutePointType valueOf(String value) {
        return Enum.valueOf(RoutePointType.class, value);
    }

    @NotNull
    public static EnumEntries<RoutePointType> getEntries() {
        return $ENTRIES;
    }

    static {
        WALK = new RoutePointType("walk", "Walk", "\ud83d\udeb6");
        WARP = new RoutePointType("warp", "Warp", "\u26a1");
        MINE = new RoutePointType("mine", "Anchor", "\u26cf");
        VEIN = new RoutePointType("vein", "Vein", "\ud83d\udca0");
        LANTERN = new RoutePointType("lantern", "Lantern", "\ud83d\udd6f");
        KILL = new RoutePointType("kill", "Kill", "\u2694");
        $VALUES = routePointTypeArray = new RoutePointType[]{RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN, RoutePointType.KILL};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        Companion = new Companion(null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/routes/RoutePointType$Companion;", "", "<init>", "()V", "", "id", "Lorg/cobalt/internal/routes/RoutePointType;", "fromId", "(Ljava/lang/String;)Lorg/cobalt/internal/routes/RoutePointType;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nRouteType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RouteType.kt\norg/cobalt/internal/routes/RoutePointType$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,67:1\n296#2,2:68\n*S KotlinDebug\n*F\n+ 1 RouteType.kt\norg/cobalt/internal/routes/RoutePointType$Companion\n*L\n29#1:68,2\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final RoutePointType fromId(@Nullable String id) {
            RoutePointType routePointType;
            Object v4;
            block4: {
                Iterable $this$firstOrNull$iv = (Iterable)RoutePointType.getEntries();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    String string;
                    RoutePointType it = (RoutePointType)((Object)element$iv);
                    boolean bl = false;
                    String string2 = it.getId();
                    String string3 = id;
                    if (string3 != null) {
                        String string4 = string3.toLowerCase(Locale.ROOT);
                        string = string4;
                        Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"toLowerCase(...)");
                    } else {
                        string = null;
                    }
                    if (!Intrinsics.areEqual((Object)string2, (Object)string)) continue;
                    v4 = element$iv;
                    break block4;
                }
                v4 = null;
            }
            if ((routePointType = (RoutePointType)v4) == null) {
                routePointType = WALK;
            }
            return routePointType;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

