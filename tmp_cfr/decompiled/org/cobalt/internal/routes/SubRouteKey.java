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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0007\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0007\u001a\u0004\b\n\u0010\tj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/routes/SubRouteKey;", "", "", "label", "icon", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "Ljava/lang/String;", "getLabel", "()Ljava/lang/String;", "getIcon", "TRAVEL", "LOOP", "AREA", "POINTS", "cobalt"})
public final class SubRouteKey
extends Enum<SubRouteKey> {
    @NotNull
    private final String label;
    @NotNull
    private final String icon;
    public static final /* enum */ SubRouteKey TRAVEL = new SubRouteKey("Travel Route", "\ud83d\udeb6");
    public static final /* enum */ SubRouteKey LOOP = new SubRouteKey("Loop Route", "\ud83d\udd04");
    public static final /* enum */ SubRouteKey AREA = new SubRouteKey("Patrol Area", "\u2694");
    public static final /* enum */ SubRouteKey POINTS = new SubRouteKey("Route Points", "\ud83d\udcc4");
    private static final /* synthetic */ SubRouteKey[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private SubRouteKey(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    @NotNull
    public final String getLabel() {
        return this.label;
    }

    @NotNull
    public final String getIcon() {
        return this.icon;
    }

    public static SubRouteKey[] values() {
        return (SubRouteKey[])$VALUES.clone();
    }

    public static SubRouteKey valueOf(String value) {
        return Enum.valueOf(SubRouteKey.class, value);
    }

    @NotNull
    public static EnumEntries<SubRouteKey> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = subRouteKeyArray = new SubRouteKey[]{SubRouteKey.TRAVEL, SubRouteKey.LOOP, SubRouteKey.AREA, SubRouteKey.POINTS};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

