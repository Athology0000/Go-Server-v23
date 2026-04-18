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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/dungeons/map/DoorKind;", "", "<init>", "(Ljava/lang/String;I)V", "NORMAL", "WITHER", "BLOOD", "ENTRANCE", "cobalt"})
public final class DoorKind
extends Enum<DoorKind> {
    public static final /* enum */ DoorKind NORMAL = new DoorKind();
    public static final /* enum */ DoorKind WITHER = new DoorKind();
    public static final /* enum */ DoorKind BLOOD = new DoorKind();
    public static final /* enum */ DoorKind ENTRANCE = new DoorKind();
    private static final /* synthetic */ DoorKind[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static DoorKind[] values() {
        return (DoorKind[])$VALUES.clone();
    }

    public static DoorKind valueOf(String value) {
        return Enum.valueOf(DoorKind.class, value);
    }

    @NotNull
    public static EnumEntries<DoorKind> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = doorKindArray = new DoorKind[]{DoorKind.NORMAL, DoorKind.WITHER, DoorKind.BLOOD, DoorKind.ENTRANCE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

