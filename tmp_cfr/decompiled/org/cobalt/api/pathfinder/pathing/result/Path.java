/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.markers.KMappedMarker
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.result;

import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMappedMarker;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u001e\n\u0002\b\u0003\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u000f\u0010\u0004\u001a\u00020\u0003H&\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0006\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\b\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\b\u0010\u0007J\u0015\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\tH&\u00a2\u0006\u0004\b\n\u0010\u000b\u00a8\u0006\f\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/result/Path;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "", "length", "()I", "getStart", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getEnd", "", "collect", "()Ljava/util/Collection;", "cobalt"})
public interface Path
extends Iterable<PathPosition>,
KMappedMarker {
    public int length();

    @NotNull
    public PathPosition getStart();

    @NotNull
    public PathPosition getEnd();

    @NotNull
    public Collection<PathPosition> collect();
}

