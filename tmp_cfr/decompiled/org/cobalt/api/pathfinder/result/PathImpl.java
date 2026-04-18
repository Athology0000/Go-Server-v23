/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.result;

import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.result.Path;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\b\u0006\n\u0002\u0010(\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00020\fH\u0096\u0082\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0010\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0015\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00020\u0005H\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0014R\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0014R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u00058\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0015\u00a8\u0006\u0016"}, d2={"Lorg/cobalt/api/pathfinder/result/PathImpl;", "Lorg/cobalt/api/pathfinder/pathing/result/Path;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "start", "end", "", "positions", "<init>", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Ljava/util/Collection;)V", "getStart", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getEnd", "", "iterator", "()Ljava/util/Iterator;", "", "length", "()I", "collect", "()Ljava/util/Collection;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "Ljava/util/Collection;", "cobalt"})
public final class PathImpl
implements Path {
    @NotNull
    private final PathPosition start;
    @NotNull
    private final PathPosition end;
    @NotNull
    private final Collection<PathPosition> positions;

    public PathImpl(@NotNull PathPosition start, @NotNull PathPosition end, @NotNull Collection<PathPosition> positions) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)end, (String)"end");
        Intrinsics.checkNotNullParameter(positions, (String)"positions");
        this.start = start;
        this.end = end;
        this.positions = positions;
    }

    @Override
    @NotNull
    public PathPosition getStart() {
        return this.start;
    }

    @Override
    @NotNull
    public PathPosition getEnd() {
        return this.end;
    }

    @Override
    @NotNull
    public Iterator<PathPosition> iterator() {
        return this.positions.iterator();
    }

    @Override
    public int length() {
        return this.positions.size();
    }

    @Override
    @NotNull
    public Collection<PathPosition> collect() {
        return CollectionsKt.toList((Iterable)this.positions);
    }
}

