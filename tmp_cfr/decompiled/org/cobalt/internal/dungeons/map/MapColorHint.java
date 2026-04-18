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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u0005\n\u0002\b\u0014\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/internal/dungeons/map/MapColorHint;", "", "", "color", "<init>", "(Ljava/lang/String;IB)V", "B", "getColor", "()B", "EMPTY", "CHECK_WHITE", "CHECK_GREEN", "CHECK_FAIL", "CHECK_UNKNOWN", "ROOM_ENTRANCE", "ROOM_NORMAL", "ROOM_UNOPENED", "ROOM_TRAP", "ROOM_BOSS", "ROOM_PUZZLE", "ROOM_FAIRY", "ROOM_BLOOD", "cobalt"})
public final class MapColorHint
extends Enum<MapColorHint> {
    private final byte color;
    public static final /* enum */ MapColorHint EMPTY = new MapColorHint(0);
    public static final /* enum */ MapColorHint CHECK_WHITE = new MapColorHint(34);
    public static final /* enum */ MapColorHint CHECK_GREEN = new MapColorHint(30);
    public static final /* enum */ MapColorHint CHECK_FAIL = new MapColorHint(18);
    public static final /* enum */ MapColorHint CHECK_UNKNOWN = new MapColorHint(119);
    public static final /* enum */ MapColorHint ROOM_ENTRANCE = new MapColorHint(30);
    public static final /* enum */ MapColorHint ROOM_NORMAL = new MapColorHint(63);
    public static final /* enum */ MapColorHint ROOM_UNOPENED = new MapColorHint(85);
    public static final /* enum */ MapColorHint ROOM_TRAP = new MapColorHint(62);
    public static final /* enum */ MapColorHint ROOM_BOSS = new MapColorHint(74);
    public static final /* enum */ MapColorHint ROOM_PUZZLE = new MapColorHint(66);
    public static final /* enum */ MapColorHint ROOM_FAIRY = new MapColorHint(82);
    public static final /* enum */ MapColorHint ROOM_BLOOD = new MapColorHint(18);
    private static final /* synthetic */ MapColorHint[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private MapColorHint(byte color) {
        this.color = color;
    }

    public final byte getColor() {
        return this.color;
    }

    public static MapColorHint[] values() {
        return (MapColorHint[])$VALUES.clone();
    }

    public static MapColorHint valueOf(String value) {
        return Enum.valueOf(MapColorHint.class, value);
    }

    @NotNull
    public static EnumEntries<MapColorHint> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = mapColorHintArray = new MapColorHint[]{MapColorHint.EMPTY, MapColorHint.CHECK_WHITE, MapColorHint.CHECK_GREEN, MapColorHint.CHECK_FAIL, MapColorHint.CHECK_UNKNOWN, MapColorHint.ROOM_ENTRANCE, MapColorHint.ROOM_NORMAL, MapColorHint.ROOM_UNOPENED, MapColorHint.ROOM_TRAP, MapColorHint.ROOM_BOSS, MapColorHint.ROOM_PUZZLE, MapColorHint.ROOM_FAIRY, MapColorHint.ROOM_BLOOD};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

