/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicContext;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicWeights;
import org.cobalt.api.pathfinder.pathing.heuristic.IHeuristicStrategy;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010\u0006\n\u0002\b\u0011\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\f\u0010\rJ\u0015\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u001b\u0010\u0013\u001a\u00020\u000e2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\nH\u0096\u0080\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0019\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u0000H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0019\u001a\u0004\b\u001a\u0010\u001bR\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010\u001c\u001a\u0004\b\u001d\u0010\u0016R\u0017\u0010\u001f\u001a\u00020\u001e8\u0006\u00a2\u0006\f\n\u0004\b\u001f\u0010 \u001a\u0004\b!\u0010\"R\"\u0010#\u001a\u00020\u001e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b#\u0010 \u001a\u0004\b$\u0010\"\"\u0004\b%\u0010&R$\u0010'\u001a\u0004\u0018\u00010\u00008\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b'\u0010(\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,R\u0011\u0010.\u001a\u00020\u001e8F\u00a2\u0006\u0006\u001a\u0004\b-\u0010\"\u00a8\u0006/"}, d2={"Lorg/cobalt/api/pathfinder/Node;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "position", "start", "target", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "heuristicWeights", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "heuristicStrategy", "", "depth", "<init>", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;I)V", "", "isTarget", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Z", "", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "()I", "compareTo", "(Lorg/cobalt/api/pathfinder/Node;)I", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "I", "getDepth", "", "heuristic", "D", "getHeuristic", "()D", "gCost", "getGCost", "setGCost", "(D)V", "parent", "Lorg/cobalt/api/pathfinder/Node;", "getParent", "()Lorg/cobalt/api/pathfinder/Node;", "setParent", "(Lorg/cobalt/api/pathfinder/Node;)V", "getFCost", "fCost", "cobalt"})
public final class Node
implements Comparable<Node> {
    @NotNull
    private final PathPosition position;
    private final int depth;
    private final double heuristic;
    private double gCost;
    @Nullable
    private Node parent;

    public Node(@NotNull PathPosition position, @NotNull PathPosition start, @NotNull PathPosition target, @NotNull HeuristicWeights heuristicWeights, @NotNull IHeuristicStrategy heuristicStrategy, int depth) {
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        Intrinsics.checkNotNullParameter((Object)heuristicWeights, (String)"heuristicWeights");
        Intrinsics.checkNotNullParameter((Object)heuristicStrategy, (String)"heuristicStrategy");
        this.position = position;
        this.depth = depth;
        this.heuristic = heuristicStrategy.calculate(new HeuristicContext(this.position, start, target, heuristicWeights));
    }

    @NotNull
    public final PathPosition getPosition() {
        return this.position;
    }

    public final int getDepth() {
        return this.depth;
    }

    public final double getHeuristic() {
        return this.heuristic;
    }

    public final double getGCost() {
        return this.gCost;
    }

    public final void setGCost(double d) {
        this.gCost = d;
    }

    @Nullable
    public final Node getParent() {
        return this.parent;
    }

    public final void setParent(@Nullable Node node) {
        this.parent = node;
    }

    public final double getFCost() {
        return this.gCost + this.heuristic;
    }

    public final boolean isTarget(@NotNull PathPosition target) {
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        return Intrinsics.areEqual((Object)this.position, (Object)target);
    }

    public boolean equals(@Nullable Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Node cfr_ignored_0 = (Node)other;
        return Intrinsics.areEqual((Object)this.position, (Object)((Node)other).position);
    }

    public int hashCode() {
        return this.position.hashCode();
    }

    @Override
    public int compareTo(@NotNull Node other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        int fCostComparison = Double.compare(this.getFCost(), other.getFCost());
        if (fCostComparison != 0) {
            return fCostComparison;
        }
        int heuristicComparison = Double.compare(this.heuristic, other.heuristic);
        if (heuristicComparison != 0) {
            return heuristicComparison;
        }
        return Intrinsics.compare((int)this.depth, (int)other.depth);
    }
}

