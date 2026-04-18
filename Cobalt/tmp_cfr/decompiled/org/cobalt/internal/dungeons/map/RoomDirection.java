/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.dungeons.map;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomDirection;", "", "<init>", "(Ljava/lang/String;I)V", "NW", "NE", "SE", "SW", "cobalt"})
public final class RoomDirection
extends Enum<RoomDirection> {
    public static final /* enum */ RoomDirection NW = new RoomDirection();
    public static final /* enum */ RoomDirection NE = new RoomDirection();
    public static final /* enum */ RoomDirection SE = new RoomDirection();
    public static final /* enum */ RoomDirection SW = new RoomDirection();
    private static final /* synthetic */ RoomDirection[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static RoomDirection[] values() {
        return (RoomDirection[])$VALUES.clone();
    }

    public static RoomDirection valueOf(String value) {
        return Enum.valueOf(RoomDirection.class, value);
    }

    @NotNull
    public static EnumEntries<RoomDirection> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = roomDirectionArray = new RoomDirection[]{RoomDirection.NW, RoomDirection.NE, RoomDirection.SE, RoomDirection.SW};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

