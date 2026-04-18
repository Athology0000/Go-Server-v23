/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.result;

import kotlin.Metadata;
import org.cobalt.api.pathfinder.pathing.result.Path;
import org.cobalt.api.pathfinder.pathing.result.PathState;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u000f\u0010\u0003\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u000f\u0010\u0005\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0005\u0010\u0004J\u000f\u0010\u0006\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0006\u0010\u0004J\u000f\u0010\b\u001a\u00020\u0007H&\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH&\u00a2\u0006\u0004\b\u000b\u0010\f\u00a8\u0006\r\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "", "", "successful", "()Z", "hasFailed", "hasFallenBack", "Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "getPathState", "()Lorg/cobalt/api/pathfinder/pathing/result/PathState;", "Lorg/cobalt/api/pathfinder/pathing/result/Path;", "getPath", "()Lorg/cobalt/api/pathfinder/pathing/result/Path;", "cobalt"})
public interface PathfinderResult {
    public boolean successful();

    public boolean hasFailed();

    public boolean hasFallenBack();

    @NotNull
    public PathState getPathState();

    @NotNull
    public Path getPath();
}

