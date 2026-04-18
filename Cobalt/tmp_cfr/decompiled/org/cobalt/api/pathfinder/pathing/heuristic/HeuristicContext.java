/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.heuristic;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.PathfindingProgress;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicWeights;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007B)\b\u0016\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\b\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\r\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0014\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicContext;", "", "Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "pathfindingProgress", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "heuristicWeights", "<init>", "(Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;)V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "position", "startPosition", "targetPosition", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;)V", "Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "getPathfindingProgress", "()Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "getHeuristicWeights", "()Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "getPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getStartPosition", "getTargetPosition", "cobalt"})
public final class HeuristicContext {
    @NotNull
    private final PathfindingProgress pathfindingProgress;
    @NotNull
    private final HeuristicWeights heuristicWeights;

    public HeuristicContext(@NotNull PathfindingProgress pathfindingProgress, @NotNull HeuristicWeights heuristicWeights) {
        Intrinsics.checkNotNullParameter((Object)pathfindingProgress, (String)"pathfindingProgress");
        Intrinsics.checkNotNullParameter((Object)heuristicWeights, (String)"heuristicWeights");
        this.pathfindingProgress = pathfindingProgress;
        this.heuristicWeights = heuristicWeights;
    }

    @NotNull
    public final PathfindingProgress getPathfindingProgress() {
        return this.pathfindingProgress;
    }

    @NotNull
    public final HeuristicWeights getHeuristicWeights() {
        return this.heuristicWeights;
    }

    public HeuristicContext(@NotNull PathPosition position, @NotNull PathPosition startPosition, @NotNull PathPosition targetPosition, @NotNull HeuristicWeights heuristicWeights) {
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        Intrinsics.checkNotNullParameter((Object)startPosition, (String)"startPosition");
        Intrinsics.checkNotNullParameter((Object)targetPosition, (String)"targetPosition");
        Intrinsics.checkNotNullParameter((Object)heuristicWeights, (String)"heuristicWeights");
        this(new PathfindingProgress(startPosition, position, targetPosition), heuristicWeights);
    }

    @NotNull
    public final PathPosition getPosition() {
        return this.pathfindingProgress.getCurrent();
    }

    @NotNull
    public final PathPosition getStartPosition() {
        return this.pathfindingProgress.getStart();
    }

    @NotNull
    public final PathPosition getTargetPosition() {
        return this.pathfindingProgress.getTarget();
    }
}

