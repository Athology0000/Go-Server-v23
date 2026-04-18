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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomShape;", "", "<init>", "(Ljava/lang/String;I)V", "UNKNOWN", "ONE_BY_ONE", "ONE_BY_TWO", "ONE_BY_THREE", "ONE_BY_FOUR", "TWO_BY_TWO", "L_SHAPE", "cobalt"})
public final class RoomShape
extends Enum<RoomShape> {
    public static final /* enum */ RoomShape UNKNOWN = new RoomShape();
    public static final /* enum */ RoomShape ONE_BY_ONE = new RoomShape();
    public static final /* enum */ RoomShape ONE_BY_TWO = new RoomShape();
    public static final /* enum */ RoomShape ONE_BY_THREE = new RoomShape();
    public static final /* enum */ RoomShape ONE_BY_FOUR = new RoomShape();
    public static final /* enum */ RoomShape TWO_BY_TWO = new RoomShape();
    public static final /* enum */ RoomShape L_SHAPE = new RoomShape();
    private static final /* synthetic */ RoomShape[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static RoomShape[] values() {
        return (RoomShape[])$VALUES.clone();
    }

    public static RoomShape valueOf(String value) {
        return Enum.valueOf(RoomShape.class, value);
    }

    @NotNull
    public static EnumEntries<RoomShape> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = roomShapeArray = new RoomShape[]{RoomShape.UNKNOWN, RoomShape.ONE_BY_ONE, RoomShape.ONE_BY_TWO, RoomShape.ONE_BY_THREE, RoomShape.ONE_BY_FOUR, RoomShape.TWO_BY_TWO, RoomShape.L_SHAPE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

