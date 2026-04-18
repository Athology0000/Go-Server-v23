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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/api/pathfinder/jni/ActionType;", "", "<init>", "(Ljava/lang/String;I)V", "WALK", "SPRINT", "JUMP", "SPRINT_JUMP", "FALL", "LADDER", "WATER_SWIM", "AOTV", "ETHERWARP", "cobalt"})
public final class ActionType
extends Enum<ActionType> {
    public static final /* enum */ ActionType WALK = new ActionType();
    public static final /* enum */ ActionType SPRINT = new ActionType();
    public static final /* enum */ ActionType JUMP = new ActionType();
    public static final /* enum */ ActionType SPRINT_JUMP = new ActionType();
    public static final /* enum */ ActionType FALL = new ActionType();
    public static final /* enum */ ActionType LADDER = new ActionType();
    public static final /* enum */ ActionType WATER_SWIM = new ActionType();
    public static final /* enum */ ActionType AOTV = new ActionType();
    public static final /* enum */ ActionType ETHERWARP = new ActionType();
    private static final /* synthetic */ ActionType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static ActionType[] values() {
        return (ActionType[])$VALUES.clone();
    }

    public static ActionType valueOf(String value) {
        return Enum.valueOf(ActionType.class, value);
    }

    @NotNull
    public static EnumEntries<ActionType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = actionTypeArray = new ActionType[]{ActionType.WALK, ActionType.SPRINT, ActionType.JUMP, ActionType.SPRINT_JUMP, ActionType.FALL, ActionType.LADDER, ActionType.WATER_SWIM, ActionType.AOTV, ActionType.ETHERWARP};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

