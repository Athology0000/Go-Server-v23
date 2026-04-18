/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.cobalt.api.pathfinder.pathing.INeighborStrategy;
import org.cobalt.api.pathfinder.wrapper.PathVector;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0007R\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0007R\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u000f\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010\f\u001a\u0004\b\u0010\u0010\u000eR\u0017\u0010\u0011\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u0011\u0010\f\u001a\u0004\b\u0012\u0010\u000e\u00a8\u0006\u0013"}, d2={"Lorg/cobalt/api/pathfinder/pathing/NeighborStrategies;", "", "<init>", "()V", "", "Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "VERTICAL_AND_HORIZONTAL_OFFSETS", "Ljava/util/List;", "DIAGONAL_3D_OFFSETS", "HORIZONTAL_DIAGONAL_AND_VERTICAL_OFFSETS", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "VERTICAL_AND_HORIZONTAL", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "getVERTICAL_AND_HORIZONTAL", "()Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "DIAGONAL_3D", "getDIAGONAL_3D", "HORIZONTAL_DIAGONAL_AND_VERTICAL", "getHORIZONTAL_DIAGONAL_AND_VERTICAL", "cobalt"})
public final class NeighborStrategies {
    @NotNull
    public static final NeighborStrategies INSTANCE = new NeighborStrategies();
    @NotNull
    private static final List<PathVector> VERTICAL_AND_HORIZONTAL_OFFSETS;
    @NotNull
    private static final List<PathVector> DIAGONAL_3D_OFFSETS;
    @NotNull
    private static final List<PathVector> HORIZONTAL_DIAGONAL_AND_VERTICAL_OFFSETS;
    @NotNull
    private static final INeighborStrategy VERTICAL_AND_HORIZONTAL;
    @NotNull
    private static final INeighborStrategy DIAGONAL_3D;
    @NotNull
    private static final INeighborStrategy HORIZONTAL_DIAGONAL_AND_VERTICAL;

    private NeighborStrategies() {
    }

    @NotNull
    public final INeighborStrategy getVERTICAL_AND_HORIZONTAL() {
        return VERTICAL_AND_HORIZONTAL;
    }

    @NotNull
    public final INeighborStrategy getDIAGONAL_3D() {
        return DIAGONAL_3D;
    }

    @NotNull
    public final INeighborStrategy getHORIZONTAL_DIAGONAL_AND_VERTICAL() {
        return HORIZONTAL_DIAGONAL_AND_VERTICAL;
    }

    private static final Iterable VERTICAL_AND_HORIZONTAL$lambda$0() {
        return VERTICAL_AND_HORIZONTAL_OFFSETS;
    }

    private static final Iterable DIAGONAL_3D$lambda$0() {
        return DIAGONAL_3D_OFFSETS;
    }

    private static final Iterable HORIZONTAL_DIAGONAL_AND_VERTICAL$lambda$0() {
        return HORIZONTAL_DIAGONAL_AND_VERTICAL_OFFSETS;
    }

    static {
        Object object = new PathVector[]{new PathVector(1.0, 0.0, 0.0), new PathVector(-1.0, 0.0, 0.0), new PathVector(0.0, 0.0, 1.0), new PathVector(0.0, 0.0, -1.0), new PathVector(0.0, 1.0, 0.0), new PathVector(0.0, -1.0, 0.0)};
        VERTICAL_AND_HORIZONTAL_OFFSETS = CollectionsKt.listOf((Object[])object);
        Object $this$DIAGONAL_3D_OFFSETS_u24lambda_u240 = object = CollectionsKt.createListBuilder();
        boolean bl = false;
        for (int x = -1; x < 2; ++x) {
            for (int y = -1; y < 2; ++y) {
                for (int z = -1; z < 2; ++z) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    $this$DIAGONAL_3D_OFFSETS_u24lambda_u240.add(new PathVector(x, y, z));
                }
            }
        }
        DIAGONAL_3D_OFFSETS = CollectionsKt.build((List)object);
        object = new PathVector[]{new PathVector(1.0, 0.0, 0.0), new PathVector(-1.0, 0.0, 0.0), new PathVector(0.0, 0.0, 1.0), new PathVector(0.0, 0.0, -1.0), new PathVector(0.0, 1.0, 0.0), new PathVector(0.0, -1.0, 0.0), new PathVector(1.0, 0.0, 1.0), new PathVector(1.0, 0.0, -1.0), new PathVector(-1.0, 0.0, 1.0), new PathVector(-1.0, 0.0, -1.0)};
        HORIZONTAL_DIAGONAL_AND_VERTICAL_OFFSETS = CollectionsKt.listOf((Object[])object);
        VERTICAL_AND_HORIZONTAL = NeighborStrategies::VERTICAL_AND_HORIZONTAL$lambda$0;
        DIAGONAL_3D = NeighborStrategies::DIAGONAL_3D$lambda$0;
        HORIZONTAL_DIAGONAL_AND_VERTICAL = NeighborStrategies::HORIZONTAL_DIAGONAL_AND_VERTICAL$lambda$0;
    }
}

