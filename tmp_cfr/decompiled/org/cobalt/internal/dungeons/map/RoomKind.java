/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.cobalt.internal.dungeons.map.MapColorHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u0000 \u00042\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0004B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomKind;", "", "<init>", "(Ljava/lang/String;I)V", "Companion", "BLOOD", "ENTRANCE", "PUZZLE", "RARE", "YELLOW", "TRAP", "UNKNOWN", "FAIRY", "NORMAL", "cobalt"})
public final class RoomKind
extends Enum<RoomKind> {
    @NotNull
    public static final Companion Companion;
    public static final /* enum */ RoomKind BLOOD;
    public static final /* enum */ RoomKind ENTRANCE;
    public static final /* enum */ RoomKind PUZZLE;
    public static final /* enum */ RoomKind RARE;
    public static final /* enum */ RoomKind YELLOW;
    public static final /* enum */ RoomKind TRAP;
    public static final /* enum */ RoomKind UNKNOWN;
    public static final /* enum */ RoomKind FAIRY;
    public static final /* enum */ RoomKind NORMAL;
    private static final /* synthetic */ RoomKind[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static RoomKind[] values() {
        return (RoomKind[])$VALUES.clone();
    }

    public static RoomKind valueOf(String value) {
        return Enum.valueOf(RoomKind.class, value);
    }

    @NotNull
    public static EnumEntries<RoomKind> getEntries() {
        return $ENTRIES;
    }

    static {
        BLOOD = new RoomKind();
        ENTRANCE = new RoomKind();
        PUZZLE = new RoomKind();
        RARE = new RoomKind();
        YELLOW = new RoomKind();
        TRAP = new RoomKind();
        UNKNOWN = new RoomKind();
        FAIRY = new RoomKind();
        NORMAL = new RoomKind();
        $VALUES = roomKindArray = new RoomKind[]{RoomKind.BLOOD, RoomKind.ENTRANCE, RoomKind.PUZZLE, RoomKind.RARE, RoomKind.YELLOW, RoomKind.TRAP, RoomKind.UNKNOWN, RoomKind.FAIRY, RoomKind.NORMAL};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        Companion = new Companion(null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\u000b\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/internal/dungeons/map/RoomKind$Companion;", "", "<init>", "()V", "", "name", "Lorg/cobalt/internal/dungeons/map/RoomKind;", "fromName", "(Ljava/lang/String;)Lorg/cobalt/internal/dungeons/map/RoomKind;", "", "color", "fromMapColor", "(B)Lorg/cobalt/internal/dungeons/map/RoomKind;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nDungeonTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/RoomKind$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,352:1\n296#2,2:353\n*S KotlinDebug\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/RoomKind$Companion\n*L\n191#1:353,2\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final RoomKind fromName(@Nullable String name) {
            RoomKind roomKind;
            Object v0;
            block2: {
                Iterable $this$firstOrNull$iv = (Iterable)RoomKind.getEntries();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    RoomKind it = (RoomKind)((Object)element$iv);
                    boolean bl = false;
                    if (!StringsKt.equals((String)it.name(), (String)name, (boolean)true)) continue;
                    v0 = element$iv;
                    break block2;
                }
                v0 = null;
            }
            if ((roomKind = (RoomKind)v0) == null) {
                roomKind = UNKNOWN;
            }
            return roomKind;
        }

        @NotNull
        public final RoomKind fromMapColor(byte color) {
            byte by = color;
            return by == MapColorHint.ROOM_ENTRANCE.getColor() ? ENTRANCE : (by == MapColorHint.ROOM_BLOOD.getColor() ? BLOOD : (by == MapColorHint.ROOM_UNOPENED.getColor() ? NORMAL : (by == MapColorHint.ROOM_BOSS.getColor() ? YELLOW : (by == MapColorHint.ROOM_FAIRY.getColor() ? FAIRY : (by == MapColorHint.ROOM_NORMAL.getColor() ? NORMAL : (by == MapColorHint.ROOM_PUZZLE.getColor() ? PUZZLE : (by == MapColorHint.ROOM_TRAP.getColor() ? TRAP : UNKNOWN)))))));
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

