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
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\t\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\bR\u0014\u0010\n\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/InternalHeuristicUtils;", "", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;", "progress", "", "calculatePerpendicularDistanceSq", "(Lorg/cobalt/api/pathfinder/pathing/PathfindingProgress;)D", "calculatePerpendicularDistance", "EPSILON", "D", "cobalt"})
public final class InternalHeuristicUtils {
    @NotNull
    public static final InternalHeuristicUtils INSTANCE = new InternalHeuristicUtils();
    private static final double EPSILON = 1.0E-9;

    private InternalHeuristicUtils() {
    }

    public final double calculatePerpendicularDistanceSq(@NotNull PathfindingProgress progress) {
        Intrinsics.checkNotNullParameter((Object)progress, (String)"progress");
        PathPosition s = progress.getStart();
        PathPosition c = progress.getCurrent();
        PathPosition t = progress.getTarget();
        double sx = s.getCenteredX();
        double sy = s.getCenteredY();
        double sz = s.getCenteredZ();
        double cx = c.getCenteredX();
        double cy = c.getCenteredY();
        double cz = c.getCenteredZ();
        double tx = t.getCenteredX();
        double ty = t.getCenteredY();
        double tz = t.getCenteredZ();
        double lineX = tx - sx;
        double lineY = ty - sy;
        double lineZ = tz - sz;
        double lineSq = lineX * lineX + lineY * lineY + lineZ * lineZ;
        if (lineSq < 1.0E-9) {
            double dx = cx - sx;
            double dy = cy - sy;
            double dz = cz - sz;
            return dx * dx + dy * dy + dz * dz;
        }
        double toX = cx - sx;
        double toY = cy - sy;
        double toZ = cz - sz;
        double crossX = toY * lineZ - toZ * lineY;
        double crossY = toZ * lineX - toX * lineZ;
        double crossZ = toX * lineY - toY * lineX;
        double crossSq = crossX * crossX + crossY * crossY + crossZ * crossZ;
        return crossSq / lineSq;
    }

    public final double calculatePerpendicularDistance(@NotNull PathfindingProgress progress) {
        Intrinsics.checkNotNullParameter((Object)progress, (String)"progress");
        return Math.sqrt(this.calculatePerpendicularDistanceSq(progress));
    }
}

