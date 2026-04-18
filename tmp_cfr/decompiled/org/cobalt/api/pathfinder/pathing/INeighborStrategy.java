/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.cobalt.api.pathfinder.wrapper.PathVector;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00e6\u0080\u0001\u0018\u00002\u00020\u0001J\u0015\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H&\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0004\u0010\b\u00a8\u0006\t\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "", "", "Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "getOffsets", "()Ljava/lang/Iterable;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "currentPosition", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Ljava/lang/Iterable;", "cobalt"})
public interface INeighborStrategy {
    @NotNull
    public Iterable<PathVector> getOffsets();

    @NotNull
    default public Iterable<PathVector> getOffsets(@NotNull PathPosition currentPosition) {
        Intrinsics.checkNotNullParameter((Object)currentPosition, (String)"currentPosition");
        return this.getOffsets();
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        @NotNull
        public static Iterable<PathVector> getOffsets(@NotNull INeighborStrategy $this, @NotNull PathPosition currentPosition) {
            Intrinsics.checkNotNullParameter((Object)currentPosition, (String)"currentPosition");
            return $this.getOffsets(currentPosition);
        }
    }
}

