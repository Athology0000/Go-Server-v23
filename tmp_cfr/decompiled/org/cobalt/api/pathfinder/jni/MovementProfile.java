/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.jni;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/api/pathfinder/jni/MovementProfile;", "", "<init>", "(Ljava/lang/String;I)V", "DEFAULT", "MINING", "COMBAT", "GROUND_ONLY", "cobalt"})
public final class MovementProfile
extends Enum<MovementProfile> {
    public static final /* enum */ MovementProfile DEFAULT = new MovementProfile();
    public static final /* enum */ MovementProfile MINING = new MovementProfile();
    public static final /* enum */ MovementProfile COMBAT = new MovementProfile();
    public static final /* enum */ MovementProfile GROUND_ONLY = new MovementProfile();
    private static final /* synthetic */ MovementProfile[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static MovementProfile[] values() {
        return (MovementProfile[])$VALUES.clone();
    }

    public static MovementProfile valueOf(String value) {
        return Enum.valueOf(MovementProfile.class, value);
    }

    @NotNull
    public static EnumEntries<MovementProfile> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = movementProfileArray = new MovementProfile[]{MovementProfile.DEFAULT, MovementProfile.MINING, MovementProfile.COMBAT, MovementProfile.GROUND_ONLY};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

