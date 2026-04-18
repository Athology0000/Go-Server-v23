/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.heuristic;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.api.pathfinder.pathing.PathfindingProgress;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicContext;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicWeights;
import org.cobalt.api.pathfinder.pathing.heuristic.IHeuristicStrategy;
import org.cobalt.api.pathfinder.pathing.heuristic.InternalHeuristicUtils;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\f\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\f\u0010\r\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/SquaredHeuristicStrategy;", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicContext;", "context", "", "calculate", "(Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicContext;)D", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "from", "to", "calculateTransitionCost", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)D", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSquaredHeuristicStrategy.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SquaredHeuristicStrategy.kt\norg/cobalt/api/pathfinder/pathing/heuristic/SquaredHeuristicStrategy\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,56:1\n1#2:57\n*E\n"})
public final class SquaredHeuristicStrategy
implements IHeuristicStrategy {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private static final double D1 = 1.0;
    private static final double D2 = Math.sqrt(2.0);
    private static final double D3 = Math.sqrt(3.0);

    @Override
    public double calculate(@NotNull HeuristicContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        PathfindingProgress p = context.getPathfindingProgress();
        HeuristicWeights w = context.getHeuristicWeights();
        PathPosition current = p.getCurrent();
        PathPosition target = p.getTarget();
        int manhattan = Math.abs(current.getFlooredX() - target.getFlooredX()) + Math.abs(current.getFlooredY() - target.getFlooredY()) + Math.abs(current.getFlooredZ() - target.getFlooredZ());
        double manhattanSq = manhattan * manhattan;
        int dx = Math.abs(current.getFlooredX() - target.getFlooredX());
        int dy = Math.abs(current.getFlooredY() - target.getFlooredY());
        int dz = Math.abs(current.getFlooredZ() - target.getFlooredZ());
        int min = Math.min(dx, Math.min(dy, dz));
        int max = Math.max(dx, Math.max(dy, dz));
        int mid = dx + dy + dz - min - max;
        double octile = (D3 - D2) * (double)min + (D2 - 1.0) * (double)mid + 1.0 * (double)max;
        double octileSq = octile * octile;
        double perpendicularSq = InternalHeuristicUtils.INSTANCE.calculatePerpendicularDistanceSq(p);
        int it = current.getFlooredY() - target.getFlooredY();
        boolean bl = false;
        double heightSq = it * it;
        return manhattanSq * w.getManhattanWeight() + octileSq * w.getOctileWeight() + perpendicularSq * w.getPerpendicularWeight() + heightSq * w.getHeightWeight();
    }

    @Override
    public double calculateTransitionCost(@NotNull PathPosition from, @NotNull PathPosition to) {
        Intrinsics.checkNotNullParameter((Object)from, (String)"from");
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        double dx = to.getCenteredX() - from.getCenteredX();
        double dy = to.getCenteredY() - from.getCenteredY();
        double dz = to.getCenteredZ() - from.getCenteredZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006\u00a8\u0006\t"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/SquaredHeuristicStrategy$Companion;", "", "<init>", "()V", "", "D1", "D", "D2", "D3", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

