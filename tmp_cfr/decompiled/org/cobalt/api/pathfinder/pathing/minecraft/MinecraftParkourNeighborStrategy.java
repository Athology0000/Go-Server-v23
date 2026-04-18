/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.minecraft;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import org.cobalt.api.pathfinder.pathing.INeighborStrategy;
import org.cobalt.api.pathfinder.wrapper.PathVector;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0016\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u0002\u00a2\u0006\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/api/pathfinder/pathing/minecraft/MinecraftParkourNeighborStrategy;", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "<init>", "()V", "", "Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "getOffsets", "()Ljava/lang/Iterable;", "", "buildOffsets", "()Ljava/util/List;", "OFFSETS", "Ljava/util/List;", "cobalt"})
public final class MinecraftParkourNeighborStrategy
implements INeighborStrategy {
    @NotNull
    public static final MinecraftParkourNeighborStrategy INSTANCE = new MinecraftParkourNeighborStrategy();
    @NotNull
    private static final List<PathVector> OFFSETS = INSTANCE.buildOffsets();

    private MinecraftParkourNeighborStrategy() {
    }

    @Override
    @NotNull
    public Iterable<PathVector> getOffsets() {
        return OFFSETS;
    }

    private final List<PathVector> buildOffsets() {
        int dz;
        int dx;
        Object object2;
        ArrayList<PathVector> offsets = new ArrayList<PathVector>(128);
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)1, (Object)0), TuplesKt.to((Object)-1, (Object)0), TuplesKt.to((Object)0, (Object)1), TuplesKt.to((Object)0, (Object)-1)};
        List dirs = CollectionsKt.listOf((Object[])objectArray);
        int maxStepUp = 1;
        int maxStepDown = 3;
        int maxClimb = 10;
        int maxJump = 3;
        block0: for (Object object2 : dirs) {
            int dx2 = ((Number)object2.component1()).intValue();
            int dz2 = ((Number)object2.component2()).intValue();
            offsets.add(new PathVector(dx2, 0.0, dz2));
            int dy = 1;
            while (true) {
                offsets.add(new PathVector(dx2, dy, dz2));
                if (dy == maxClimb) break;
                ++dy;
            }
            dy = 1;
            while (true) {
                offsets.add(new PathVector(dx2, -((double)dy), dz2));
                if (dy == maxStepDown) continue block0;
                ++dy;
            }
        }
        object2 = new Pair[]{TuplesKt.to((Object)1, (Object)1), TuplesKt.to((Object)1, (Object)-1), TuplesKt.to((Object)-1, (Object)1), TuplesKt.to((Object)-1, (Object)-1)};
        List diagonals = CollectionsKt.listOf((Object[])object2);
        block3: for (Pair pair : diagonals) {
            dx = ((Number)pair.component1()).intValue();
            dz = ((Number)pair.component2()).intValue();
            int dy = -maxStepDown;
            if (dy > maxStepUp) continue;
            while (true) {
                offsets.add(new PathVector(dx, dy, dz));
                if (dy == maxStepUp) continue block3;
                ++dy;
            }
        }
        offsets.add(new PathVector(0.0, 1.0, 0.0));
        offsets.add(new PathVector(0.0, -1.0, 0.0));
        block5: for (Pair pair : dirs) {
            dx = ((Number)pair.component1()).intValue();
            dz = ((Number)pair.component2()).intValue();
            int len = 2;
            while (true) {
                int scaleX = dx * len;
                int scaleZ = dz * len;
                int dy = -maxStepDown;
                if (dy <= maxStepUp) {
                    while (true) {
                        offsets.add(new PathVector(scaleX, dy, scaleZ));
                        if (dy == maxStepUp) break;
                        ++dy;
                    }
                }
                if (len == maxJump) continue block5;
                ++len;
            }
        }
        return offsets;
    }
}

