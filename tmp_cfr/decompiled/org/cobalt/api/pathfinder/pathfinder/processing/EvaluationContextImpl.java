/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathfinder.processing;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.Node;
import org.cobalt.api.pathfinder.pathing.heuristic.IHeuristicStrategy;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\n\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0004\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nR\u001a\u0010\u0003\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0003\u0010\u000b\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u000eR\u0016\u0010\u0006\u001a\u0004\u0018\u00010\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u000eR\u0014\u0010\b\u001a\u00020\u00078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\u000fR\u0014\u0010\u0013\u001a\u00020\u00108VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0015\u001a\u0004\u0018\u00010\u00108VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0012R\u0014\u0010\u0019\u001a\u00020\u00168VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001d\u001a\u00020\u001a8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001f\u001a\u00020\u001a8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001cR\u0014\u0010!\u001a\u00020\u001a8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b \u0010\u001cR\u0016\u0010#\u001a\u0004\u0018\u00010\u00108VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\"\u0010\u0012\u00a8\u0006$"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/processing/EvaluationContextImpl;", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "searchContext", "Lorg/cobalt/api/pathfinder/Node;", "engineNode", "parentEngineNode", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "heuristicStrategy", "<init>", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;Lorg/cobalt/api/pathfinder/Node;Lorg/cobalt/api/pathfinder/Node;Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;)V", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "getSearchContext", "()Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "Lorg/cobalt/api/pathfinder/Node;", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getCurrentPathPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "currentPathPosition", "getPreviousPathPosition", "previousPathPosition", "", "getCurrentNodeDepth", "()I", "currentNodeDepth", "", "getCurrentNodeHeuristicValue", "()D", "currentNodeHeuristicValue", "getPathCostToPreviousPosition", "pathCostToPreviousPosition", "getBaseTransitionCost", "baseTransitionCost", "getGrandparentPathPosition", "grandparentPathPosition", "cobalt"})
public final class EvaluationContextImpl
implements EvaluationContext {
    @NotNull
    private final SearchContext searchContext;
    @NotNull
    private final Node engineNode;
    @Nullable
    private final Node parentEngineNode;
    @NotNull
    private final IHeuristicStrategy heuristicStrategy;

    public EvaluationContextImpl(@NotNull SearchContext searchContext, @NotNull Node engineNode, @Nullable Node parentEngineNode, @NotNull IHeuristicStrategy heuristicStrategy) {
        Intrinsics.checkNotNullParameter((Object)searchContext, (String)"searchContext");
        Intrinsics.checkNotNullParameter((Object)engineNode, (String)"engineNode");
        Intrinsics.checkNotNullParameter((Object)heuristicStrategy, (String)"heuristicStrategy");
        this.searchContext = searchContext;
        this.engineNode = engineNode;
        this.parentEngineNode = parentEngineNode;
        this.heuristicStrategy = heuristicStrategy;
    }

    @Override
    @NotNull
    public SearchContext getSearchContext() {
        return this.searchContext;
    }

    @Override
    @NotNull
    public PathPosition getCurrentPathPosition() {
        return this.engineNode.getPosition();
    }

    @Override
    @Nullable
    public PathPosition getPreviousPathPosition() {
        Node node = this.parentEngineNode;
        return node != null ? node.getPosition() : null;
    }

    @Override
    public int getCurrentNodeDepth() {
        return this.engineNode.getDepth();
    }

    @Override
    public double getCurrentNodeHeuristicValue() {
        return this.engineNode.getHeuristic();
    }

    @Override
    public double getPathCostToPreviousPosition() {
        Node node = this.parentEngineNode;
        return node != null ? node.getGCost() : 0.0;
    }

    @Override
    public double getBaseTransitionCost() {
        PathPosition to;
        if (this.parentEngineNode == null) {
            return 0.0;
        }
        PathPosition from = this.parentEngineNode.getPosition();
        double baseCost = this.heuristicStrategy.calculateTransitionCost(from, to = this.engineNode.getPosition());
        if (Double.isNaN(baseCost) || Double.isInfinite(baseCost)) {
            throw new IllegalStateException("Heuristic transition cost produced an invalid numeric value: " + baseCost);
        }
        return Math.max(baseCost, 0.0);
    }

    @Override
    @Nullable
    public PathPosition getGrandparentPathPosition() {
        Node node = this.parentEngineNode;
        return node != null && (node = node.getParent()) != null ? node.getPosition() : null;
    }
}

