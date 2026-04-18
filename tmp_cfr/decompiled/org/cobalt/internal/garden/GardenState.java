/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/internal/garden/GardenState;", "", "<init>", "(Ljava/lang/String;I)V", "OFF", "FARMING", "CLEANING", "VISITING", "AUTOSELLING", "RESTING", "RECOVERING", "cobalt"})
public final class GardenState
extends Enum<GardenState> {
    public static final /* enum */ GardenState OFF = new GardenState();
    public static final /* enum */ GardenState FARMING = new GardenState();
    public static final /* enum */ GardenState CLEANING = new GardenState();
    public static final /* enum */ GardenState VISITING = new GardenState();
    public static final /* enum */ GardenState AUTOSELLING = new GardenState();
    public static final /* enum */ GardenState RESTING = new GardenState();
    public static final /* enum */ GardenState RECOVERING = new GardenState();
    private static final /* synthetic */ GardenState[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static GardenState[] values() {
        return (GardenState[])$VALUES.clone();
    }

    public static GardenState valueOf(String value) {
        return Enum.valueOf(GardenState.class, value);
    }

    @NotNull
    public static EnumEntries<GardenState> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = gardenStateArray = new GardenState[]{GardenState.OFF, GardenState.FARMING, GardenState.CLEANING, GardenState.VISITING, GardenState.AUTOSELLING, GardenState.RESTING, GardenState.RECOVERING};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

