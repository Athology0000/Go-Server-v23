/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.result;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2={"Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "", "<init>", "(Ljava/lang/String;I)V", "ABORTED", "FOUND", "FAILED", "FALLBACK", "LENGTH_LIMITED", "MAX_ITERATIONS_REACHED", "cobalt"})
public final class PathState
extends Enum<PathState> {
    public static final /* enum */ PathState ABORTED = new PathState();
    public static final /* enum */ PathState FOUND = new PathState();
    public static final /* enum */ PathState FAILED = new PathState();
    public static final /* enum */ PathState FALLBACK = new PathState();
    public static final /* enum */ PathState LENGTH_LIMITED = new PathState();
    public static final /* enum */ PathState MAX_ITERATIONS_REACHED = new PathState();
    private static final /* synthetic */ PathState[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static PathState[] values() {
        return (PathState[])$VALUES.clone();
    }

    public static PathState valueOf(String value) {
        return Enum.valueOf(PathState.class, value);
    }

    @NotNull
    public static EnumEntries<PathState> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = pathStateArray = new PathState[]{PathState.ABORTED, PathState.FOUND, PathState.FAILED, PathState.FALLBACK, PathState.LENGTH_LIMITED, PathState.MAX_ITERATIONS_REACHED};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

