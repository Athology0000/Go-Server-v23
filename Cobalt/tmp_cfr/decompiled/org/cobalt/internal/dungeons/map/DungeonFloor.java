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
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u001d\b\u0086\u0081\u0002\u0018\u0000 \u00102\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0010B!\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\f\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\f\u001a\u0004\b\u000f\u0010\u000ej\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b \u00a8\u0006!"}, d2={"Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "", "", "shortName", "", "roomsWide", "roomsTall", "<init>", "(Ljava/lang/String;ILjava/lang/String;II)V", "Ljava/lang/String;", "getShortName", "()Ljava/lang/String;", "I", "getRoomsWide", "()I", "getRoomsTall", "Companion", "NONE", "ENTRANCE", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "M1", "M2", "M3", "M4", "M5", "M6", "M7", "cobalt"})
public final class DungeonFloor
extends Enum<DungeonFloor> {
    @NotNull
    public static final Companion Companion;
    @NotNull
    private final String shortName;
    private final int roomsWide;
    private final int roomsTall;
    public static final /* enum */ DungeonFloor NONE;
    public static final /* enum */ DungeonFloor ENTRANCE;
    public static final /* enum */ DungeonFloor F1;
    public static final /* enum */ DungeonFloor F2;
    public static final /* enum */ DungeonFloor F3;
    public static final /* enum */ DungeonFloor F4;
    public static final /* enum */ DungeonFloor F5;
    public static final /* enum */ DungeonFloor F6;
    public static final /* enum */ DungeonFloor F7;
    public static final /* enum */ DungeonFloor M1;
    public static final /* enum */ DungeonFloor M2;
    public static final /* enum */ DungeonFloor M3;
    public static final /* enum */ DungeonFloor M4;
    public static final /* enum */ DungeonFloor M5;
    public static final /* enum */ DungeonFloor M6;
    public static final /* enum */ DungeonFloor M7;
    private static final /* synthetic */ DungeonFloor[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private DungeonFloor(String shortName, int roomsWide, int roomsTall) {
        this.shortName = shortName;
        this.roomsWide = roomsWide;
        this.roomsTall = roomsTall;
    }

    @NotNull
    public final String getShortName() {
        return this.shortName;
    }

    public final int getRoomsWide() {
        return this.roomsWide;
    }

    public final int getRoomsTall() {
        return this.roomsTall;
    }

    public static DungeonFloor[] values() {
        return (DungeonFloor[])$VALUES.clone();
    }

    public static DungeonFloor valueOf(String value) {
        return Enum.valueOf(DungeonFloor.class, value);
    }

    @NotNull
    public static EnumEntries<DungeonFloor> getEntries() {
        return $ENTRIES;
    }

    static {
        NONE = new DungeonFloor("", 0, 0);
        ENTRANCE = new DungeonFloor("E", 4, 4);
        F1 = new DungeonFloor("F1", 4, 5);
        F2 = new DungeonFloor("F2", 5, 5);
        F3 = new DungeonFloor("F3", 5, 5);
        F4 = new DungeonFloor("F4", 6, 5);
        F5 = new DungeonFloor("F5", 6, 6);
        F6 = new DungeonFloor("F6", 6, 6);
        F7 = new DungeonFloor("F7", 6, 6);
        M1 = new DungeonFloor("M1", 4, 5);
        M2 = new DungeonFloor("M2", 5, 5);
        M3 = new DungeonFloor("M3", 5, 5);
        M4 = new DungeonFloor("M4", 6, 5);
        M5 = new DungeonFloor("M5", 6, 6);
        M6 = new DungeonFloor("M6", 6, 6);
        M7 = new DungeonFloor("M7", 6, 6);
        $VALUES = dungeonFloorArray = new DungeonFloor[]{DungeonFloor.NONE, DungeonFloor.ENTRANCE, DungeonFloor.F1, DungeonFloor.F2, DungeonFloor.F3, DungeonFloor.F4, DungeonFloor.F5, DungeonFloor.F6, DungeonFloor.F7, DungeonFloor.M1, DungeonFloor.M2, DungeonFloor.M3, DungeonFloor.M4, DungeonFloor.M5, DungeonFloor.M6, DungeonFloor.M7};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        Companion = new Companion(null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\f\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\f\u0010\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/dungeons/map/DungeonFloor$Companion;", "", "<init>", "()V", "", "name", "Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "fromName", "(Ljava/lang/String;)Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "roman", "", "masterMode", "fromRoman", "(Ljava/lang/String;Z)Lorg/cobalt/internal/dungeons/map/DungeonFloor;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nDungeonTypes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/DungeonFloor$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,352:1\n296#2,2:353\n296#2,2:356\n1#3:355\n*S KotlinDebug\n*F\n+ 1 DungeonTypes.kt\norg/cobalt/internal/dungeons/map/DungeonFloor$Companion\n*L\n148#1:353,2\n172#1:356,2\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final DungeonFloor fromName(@Nullable String name) {
            Object v1;
            String normalized;
            block3: {
                if (name == null) {
                    return NONE;
                }
                String string = ((Object)StringsKt.trim((CharSequence)name)).toString().toUpperCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toUpperCase(...)");
                normalized = string;
                Iterable $this$firstOrNull$iv = (Iterable)DungeonFloor.getEntries();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    DungeonFloor it = (DungeonFloor)((Object)element$iv);
                    boolean bl = false;
                    if (!StringsKt.equals((String)it.getShortName(), (String)normalized, (boolean)true)) continue;
                    v1 = element$iv;
                    break block3;
                }
                v1 = null;
            }
            DungeonFloor dungeonFloor = v1;
            if (dungeonFloor != null) {
                DungeonFloor it = dungeonFloor;
                boolean bl = false;
                return it;
            }
            return Intrinsics.areEqual((Object)normalized, (Object)"ENTRANCE") ? ENTRANCE : (StringsKt.startsWith$default((String)normalized, (String)"MASTER MODE FLOOR ", (boolean)false, (int)2, null) ? this.fromRoman(((Object)StringsKt.trim((CharSequence)StringsKt.removePrefix((String)normalized, (CharSequence)"MASTER MODE FLOOR "))).toString(), true) : (StringsKt.startsWith$default((String)normalized, (String)"FLOOR ", (boolean)false, (int)2, null) ? this.fromRoman(((Object)StringsKt.trim((CharSequence)StringsKt.removePrefix((String)normalized, (CharSequence)"FLOOR "))).toString(), false) : NONE));
        }

        private final DungeonFloor fromRoman(String roman, boolean masterMode) {
            DungeonFloor dungeonFloor;
            Object v1;
            block27: {
                int n;
                switch (roman) {
                    case "I": {
                        n = 1;
                        break;
                    }
                    case "II": {
                        n = 2;
                        break;
                    }
                    case "III": {
                        n = 3;
                        break;
                    }
                    case "IV": {
                        n = 4;
                        break;
                    }
                    case "V": {
                        n = 5;
                        break;
                    }
                    case "VI": {
                        n = 6;
                        break;
                    }
                    case "VII": {
                        n = 7;
                        break;
                    }
                    default: {
                        return NONE;
                    }
                }
                int number = n;
                String id = masterMode ? "M" + number : "F" + number;
                Iterable $this$firstOrNull$iv = (Iterable)DungeonFloor.getEntries();
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    DungeonFloor it = (DungeonFloor)((Object)element$iv);
                    boolean bl = false;
                    if (!Intrinsics.areEqual((Object)it.getShortName(), (Object)id)) continue;
                    v1 = element$iv;
                    break block27;
                }
                v1 = null;
            }
            if ((dungeonFloor = (DungeonFloor)v1) == null) {
                dungeonFloor = NONE;
            }
            return dungeonFloor;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

