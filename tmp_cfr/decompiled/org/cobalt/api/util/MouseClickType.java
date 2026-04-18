/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/api/util/MouseClickType;", "", "<init>", "(Ljava/lang/String;I)V", "LEFT", "RIGHT", "MIDDLE", "cobalt"})
public final class MouseClickType
extends Enum<MouseClickType> {
    public static final /* enum */ MouseClickType LEFT = new MouseClickType();
    public static final /* enum */ MouseClickType RIGHT = new MouseClickType();
    public static final /* enum */ MouseClickType MIDDLE = new MouseClickType();
    private static final /* synthetic */ MouseClickType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static MouseClickType[] values() {
        return (MouseClickType[])$VALUES.clone();
    }

    public static MouseClickType valueOf(String value) {
        return Enum.valueOf(MouseClickType.class, value);
    }

    @NotNull
    public static EnumEntries<MouseClickType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = mouseClickTypeArray = new MouseClickType[]{MouseClickType.LEFT, MouseClickType.RIGHT, MouseClickType.MIDDLE};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

