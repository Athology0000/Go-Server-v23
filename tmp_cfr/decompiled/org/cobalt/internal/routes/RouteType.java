/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.routes;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u000b\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/internal/routes/RouteType;", "", "", "label", "", "color", "<init>", "(Ljava/lang/String;ILjava/lang/String;J)V", "Ljava/lang/String;", "getLabel", "()Ljava/lang/String;", "J", "getColor", "()J", "ORE_MINER", "COMMISSION", "PATROL", "GEMSTONE", "TUNNEL", "cobalt"})
public final class RouteType
extends Enum<RouteType> {
    @NotNull
    private final String label;
    private final long color;
    public static final /* enum */ RouteType ORE_MINER = new RouteType("Ore Miner", 4283294405L);
    public static final /* enum */ RouteType COMMISSION = new RouteType("Commission", 4294943311L);
    public static final /* enum */ RouteType PATROL = new RouteType("Patrol", 4294929290L);
    public static final /* enum */ RouteType GEMSTONE = new RouteType("Gemstone", 4288576767L);
    public static final /* enum */ RouteType TUNNEL = new RouteType("Tunnel", 4284524026L);
    private static final /* synthetic */ RouteType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private RouteType(String label, long color) {
        this.label = label;
        this.color = color;
    }

    @NotNull
    public final String getLabel() {
        return this.label;
    }

    public final long getColor() {
        return this.color;
    }

    public static RouteType[] values() {
        return (RouteType[])$VALUES.clone();
    }

    public static RouteType valueOf(String value) {
        return Enum.valueOf(RouteType.class, value);
    }

    @NotNull
    public static EnumEntries<RouteType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = routeTypeArray = new RouteType[]{RouteType.ORE_MINER, RouteType.COMMISSION, RouteType.PATROL, RouteType.GEMSTONE, RouteType.TUNNEL};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

