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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomCheckmark;", "", "<init>", "(Ljava/lang/String;I)V", "NONE", "WHITE", "GREEN", "FAILED", "UNEXPLORED", "cobalt"})
public final class RoomCheckmark
extends Enum<RoomCheckmark> {
    public static final /* enum */ RoomCheckmark NONE = new RoomCheckmark();
    public static final /* enum */ RoomCheckmark WHITE = new RoomCheckmark();
    public static final /* enum */ RoomCheckmark GREEN = new RoomCheckmark();
    public static final /* enum */ RoomCheckmark FAILED = new RoomCheckmark();
    public static final /* enum */ RoomCheckmark UNEXPLORED = new RoomCheckmark();
    private static final /* synthetic */ RoomCheckmark[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static RoomCheckmark[] values() {
        return (RoomCheckmark[])$VALUES.clone();
    }

    public static RoomCheckmark valueOf(String value) {
        return Enum.valueOf(RoomCheckmark.class, value);
    }

    @NotNull
    public static EnumEntries<RoomCheckmark> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = roomCheckmarkArray = new RoomCheckmark[]{RoomCheckmark.NONE, RoomCheckmark.WHITE, RoomCheckmark.GREEN, RoomCheckmark.FAILED, RoomCheckmark.UNEXPLORED};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

