/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.result;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.result.Path;
import org.cobalt.api.pathfinder.pathing.result.PathState;
import org.cobalt.api.pathfinder.pathing.result.PathfinderResult;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\t\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\u000b\u0010\nJ\u000f\u0010\f\u001a\u00020\bH\u0016\u00a2\u0006\u0004\b\f\u0010\nJ\u000f\u0010\r\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0011R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0012\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/pathfinder/result/PathfinderResultImpl;", "Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "pathState", "Lorg/cobalt/api/pathfinder/pathing/result/Path;", "path", "<init>", "(Lorg/cobalt/api/pathfinder/pathing/result/PathState;Lorg/cobalt/api/pathfinder/pathing/result/Path;)V", "", "successful", "()Z", "hasFailed", "hasFallenBack", "getPathState", "()Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "getPath", "()Lorg/cobalt/api/pathfinder/pathing/result/Path;", "Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "Lorg/cobalt/api/pathfinder/pathing/result/Path;", "cobalt"})
public final class PathfinderResultImpl
implements PathfinderResult {
    @NotNull
    private final PathState pathState;
    @NotNull
    private final Path path;

    public PathfinderResultImpl(@NotNull PathState pathState, @NotNull Path path) {
        Intrinsics.checkNotNullParameter((Object)((Object)pathState), (String)"pathState");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        this.pathState = pathState;
        this.path = path;
    }

    @Override
    public boolean successful() {
        return this.pathState == PathState.FOUND || this.pathState == PathState.FALLBACK || this.pathState == PathState.MAX_ITERATIONS_REACHED;
    }

    @Override
    public boolean hasFailed() {
        return this.pathState == PathState.FAILED || this.pathState == PathState.ABORTED || this.pathState == PathState.LENGTH_LIMITED;
    }

    @Override
    public boolean hasFallenBack() {
        return this.pathState == PathState.FALLBACK;
    }

    @Override
    @NotNull
    public PathState getPathState() {
        return this.pathState;
    }

    @Override
    @NotNull
    public Path getPath() {
        return this.path;
    }
}

