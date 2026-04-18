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

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/pathfinder/jni/PathStatus;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "PLANNING", "EXECUTING", "RECOVERING", "REPLANNING", "ARRIVED", "FAILED", "cobalt"})
public final class PathStatus
extends Enum<PathStatus> {
    public static final /* enum */ PathStatus IDLE = new PathStatus();
    public static final /* enum */ PathStatus PLANNING = new PathStatus();
    public static final /* enum */ PathStatus EXECUTING = new PathStatus();
    public static final /* enum */ PathStatus RECOVERING = new PathStatus();
    public static final /* enum */ PathStatus REPLANNING = new PathStatus();
    public static final /* enum */ PathStatus ARRIVED = new PathStatus();
    public static final /* enum */ PathStatus FAILED = new PathStatus();
    private static final /* synthetic */ PathStatus[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static PathStatus[] values() {
        return (PathStatus[])$VALUES.clone();
    }

    public static PathStatus valueOf(String value) {
        return Enum.valueOf(PathStatus.class, value);
    }

    @NotNull
    public static EnumEntries<PathStatus> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = pathStatusArray = new PathStatus[]{PathStatus.IDLE, PathStatus.PLANNING, PathStatus.EXECUTING, PathStatus.RECOVERING, PathStatus.REPLANNING, PathStatus.ARRIVED, PathStatus.FAILED};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

