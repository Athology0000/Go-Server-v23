/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.tooltips;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/ui/components/tooltips/TooltipPosition;", "", "<init>", "(Ljava/lang/String;I)V", "ABOVE", "BELOW", "LEFT", "RIGHT", "cobalt"})
public final class TooltipPosition
extends Enum<TooltipPosition> {
    public static final /* enum */ TooltipPosition ABOVE = new TooltipPosition();
    public static final /* enum */ TooltipPosition BELOW = new TooltipPosition();
    public static final /* enum */ TooltipPosition LEFT = new TooltipPosition();
    public static final /* enum */ TooltipPosition RIGHT = new TooltipPosition();
    private static final /* synthetic */ TooltipPosition[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static TooltipPosition[] values() {
        return (TooltipPosition[])$VALUES.clone();
    }

    public static TooltipPosition valueOf(String value) {
        return Enum.valueOf(TooltipPosition.class, value);
    }

    @NotNull
    public static EnumEntries<TooltipPosition> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = tooltipPositionArray = new TooltipPosition[]{TooltipPosition.ABOVE, TooltipPosition.BELOW, TooltipPosition.LEFT, TooltipPosition.RIGHT};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

