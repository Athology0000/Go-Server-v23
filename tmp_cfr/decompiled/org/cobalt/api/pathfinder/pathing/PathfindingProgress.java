/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u001a\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u001b\u0010\t\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "start", "current", "target", "<init>", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)V", "component1", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "component2", "component3", "copy", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getStart", "getCurrent", "getTarget", "cobalt"})
public final class PathfindingProgress {
    @NotNull
    private final PathPosition start;
    @NotNull
    private final PathPosition current;
    @NotNull
    private final PathPosition target;

    public PathfindingProgress(@NotNull PathPosition start, @NotNull PathPosition current, @NotNull PathPosition target) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)current, (String)"current");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        this.start = start;
        this.current = current;
        this.target = target;
    }

    @NotNull
    public final PathPosition getStart() {
        return this.start;
    }

    @NotNull
    public final PathPosition getCurrent() {
        return this.current;
    }

    @NotNull
    public final PathPosition getTarget() {
        return this.target;
    }

    @NotNull
    public final PathPosition component1() {
        return this.start;
    }

    @NotNull
    public final PathPosition component2() {
        return this.current;
    }

    @NotNull
    public final PathPosition component3() {
        return this.target;
    }

    @NotNull
    public final PathfindingProgress copy(@NotNull PathPosition start, @NotNull PathPosition current, @NotNull PathPosition target) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)current, (String)"current");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        return new PathfindingProgress(start, current, target);
    }

    public static /* synthetic */ PathfindingProgress copy$default(PathfindingProgress pathfindingProgress, PathPosition pathPosition, PathPosition pathPosition2, PathPosition pathPosition3, int n, Object object) {
        if ((n & 1) != 0) {
            pathPosition = pathfindingProgress.start;
        }
        if ((n & 2) != 0) {
            pathPosition2 = pathfindingProgress.current;
        }
        if ((n & 4) != 0) {
            pathPosition3 = pathfindingProgress.target;
        }
        return pathfindingProgress.copy(pathPosition, pathPosition2, pathPosition3);
    }

    @NotNull
    public String toString() {
        return "PathfindingProgress(start=" + this.start + ", current=" + this.current + ", target=" + this.target + ")";
    }

    public int hashCode() {
        int result = this.start.hashCode();
        result = result * 31 + this.current.hashCode();
        result = result * 31 + this.target.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PathfindingProgress)) {
            return false;
        }
        PathfindingProgress pathfindingProgress = (PathfindingProgress)other;
        if (!Intrinsics.areEqual((Object)this.start, (Object)pathfindingProgress.start)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.current, (Object)pathfindingProgress.current)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.target, (Object)pathfindingProgress.target);
    }
}

