/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.api.pathfinder.Node;
import org.cobalt.api.pathfinder.pathfinder.AbstractPathfinder;
import org.cobalt.api.pathfinder.pathfinder.heap.PrimitiveMinHeap;
import org.cobalt.api.pathfinder.pathfinder.processing.EvaluationContextImpl;
import org.cobalt.api.pathfinder.pathing.configuration.PathfinderConfiguration;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.cobalt.api.pathfinder.util.RegionKey;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.cobalt.api.pathfinder.wrapper.PathVector;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u00016B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J'\u0010\r\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0014\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\nH\u0014\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0014\u00a2\u0006\u0004\b\u0011\u0010\u0012J7\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\u0017H\u0014\u00a2\u0006\u0004\b\u0019\u0010\u001aJ7\u0010\u001e\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ/\u0010$\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00132\u0006\u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0017\u0010)\u001a\u00020(2\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010+\u001a\u00020\b2\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b+\u0010,J\u0017\u0010-\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u0006H\u0014\u00a2\u0006\u0004\b-\u0010.J\u000f\u0010/\u001a\u00020\fH\u0014\u00a2\u0006\u0004\b/\u0010\u0012J\u000f\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\b1\u00102R\u001a\u00104\u001a\b\u0012\u0004\u0012\u000200038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b4\u00105\u00a8\u00067"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/AStarPathfinder;", "Lorg/cobalt/api/pathfinder/pathfinder/AbstractPathfinder;", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "configuration", "<init>", "(Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;)V", "Lorg/cobalt/api/pathfinder/Node;", "node", "", "fCost", "Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;", "openSet", "", "insertStartNode", "(Lorg/cobalt/api/pathfinder/Node;DLorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;)V", "extractBestNode", "(Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;)Lorg/cobalt/api/pathfinder/Node;", "initializeSearch", "()V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "requestStart", "requestTarget", "currentNode", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "searchContext", "processSuccessors", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;)V", "existing", "", "packedPos", "updateExistingNode", "(Lorg/cobalt/api/pathfinder/Node;JLorg/cobalt/api/pathfinder/Node;Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;)V", "position", "start", "target", "parent", "createNeighborNode", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;)Lorg/cobalt/api/pathfinder/Node;", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "context", "", "isValidByCustomProcessors", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Z", "calculateGCost", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)D", "markNodeAsExpanded", "(Lorg/cobalt/api/pathfinder/Node;)V", "performAlgorithmCleanup", "Lorg/cobalt/api/pathfinder/pathfinder/AStarPathfinder$PathfindingSession;", "getSessionOrThrow", "()Lorg/cobalt/api/pathfinder/pathfinder/AStarPathfinder$PathfindingSession;", "Ljava/lang/ThreadLocal;", "currentSession", "Ljava/lang/ThreadLocal;", "PathfindingSession", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAStarPathfinder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AStarPathfinder.kt\norg/cobalt/api/pathfinder/pathfinder/AStarPathfinder\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,179:1\n1786#2,3:180\n1#3:183\n*S KotlinDebug\n*F\n+ 1 AStarPathfinder.kt\norg/cobalt/api/pathfinder/pathfinder/AStarPathfinder\n*L\n144#1:180,3\n*E\n"})
public final class AStarPathfinder
extends AbstractPathfinder {
    @NotNull
    private final ThreadLocal<PathfindingSession> currentSession;

    public AStarPathfinder(@NotNull PathfinderConfiguration configuration) {
        Intrinsics.checkNotNullParameter((Object)configuration, (String)"configuration");
        super(configuration);
        this.currentSession = new ThreadLocal();
    }

    @Override
    protected void insertStartNode(@NotNull Node node, double fCost, @NotNull PrimitiveMinHeap openSet) {
        Intrinsics.checkNotNullParameter((Object)node, (String)"node");
        Intrinsics.checkNotNullParameter((Object)openSet, (String)"openSet");
        PathfindingSession session = this.getSessionOrThrow();
        long packedPos = RegionKey.INSTANCE.pack(node.getPosition());
        openSet.insertOrUpdate(packedPos, fCost);
        ((Map)session.getOpenSetNodes()).put(packedPos, node);
    }

    @Override
    @NotNull
    protected Node extractBestNode(@NotNull PrimitiveMinHeap openSet) {
        Intrinsics.checkNotNullParameter((Object)openSet, (String)"openSet");
        PathfindingSession session = this.getSessionOrThrow();
        long packedPos = openSet.extractMin();
        Object object = session.getOpenSetNodes().get(packedPos);
        Intrinsics.checkNotNull((Object)object);
        Node node = (Node)object;
        session.getOpenSetNodes().remove(packedPos);
        return node;
    }

    @Override
    protected void initializeSearch() {
        this.currentSession.set(new PathfindingSession());
    }

    @Override
    protected void processSuccessors(@NotNull PathPosition requestStart, @NotNull PathPosition requestTarget, @NotNull Node currentNode, @NotNull PrimitiveMinHeap openSet, @NotNull SearchContext searchContext) {
        Intrinsics.checkNotNullParameter((Object)requestStart, (String)"requestStart");
        Intrinsics.checkNotNullParameter((Object)requestTarget, (String)"requestTarget");
        Intrinsics.checkNotNullParameter((Object)currentNode, (String)"currentNode");
        Intrinsics.checkNotNullParameter((Object)openSet, (String)"openSet");
        Intrinsics.checkNotNullParameter((Object)searchContext, (String)"searchContext");
        PathfindingSession session = this.getSessionOrThrow();
        Iterable<PathVector> offsets = this.getNeighborStrategy().getOffsets(currentNode.getPosition());
        for (PathVector offset : offsets) {
            PathPosition neighborPos = currentNode.getPosition().add(offset);
            long packedPos = RegionKey.INSTANCE.pack(neighborPos);
            if (openSet.contains(packedPos)) {
                Object object = session.getOpenSetNodes().get(packedPos);
                Intrinsics.checkNotNull((Object)object);
                Node existing = (Node)object;
                this.updateExistingNode(existing, packedPos, currentNode, searchContext, openSet);
                continue;
            }
            if (session.getClosedSet().contains(packedPos)) continue;
            Node neighbor = this.createNeighborNode(neighborPos, requestStart, requestTarget, currentNode);
            neighbor.setParent(currentNode);
            EvaluationContextImpl context = new EvaluationContextImpl(searchContext, neighbor, currentNode, this.getPathfinderConfiguration().getHeuristicStrategy());
            if (!this.isValidByCustomProcessors(context)) continue;
            double gCost = this.calculateGCost(context);
            neighbor.setGCost(gCost);
            double fCost = neighbor.getFCost();
            double heapKey = this.calculateHeapKey(neighbor, fCost);
            openSet.insertOrUpdate(packedPos, heapKey);
            ((Map)session.getOpenSetNodes()).put(packedPos, neighbor);
        }
    }

    private final void updateExistingNode(Node existing, long packedPos, Node currentNode, SearchContext searchContext, PrimitiveMinHeap openSet) {
        EvaluationContextImpl context = new EvaluationContextImpl(searchContext, existing, currentNode, this.getPathfinderConfiguration().getHeuristicStrategy());
        double newG = this.calculateGCost(context);
        double tol = Math.ulp(Math.max(Math.abs(newG), Math.abs(existing.getGCost())));
        if (newG + tol >= existing.getGCost()) {
            return;
        }
        if (!this.isValidByCustomProcessors(context)) {
            return;
        }
        existing.setParent(currentNode);
        existing.setGCost(newG);
        double newF = existing.getFCost();
        double newKey = this.calculateHeapKey(existing, newF);
        double oldKey = openSet.getCost(packedPos);
        if (newKey + Math.ulp(newKey) < oldKey) {
            openSet.insertOrUpdate(packedPos, newKey);
        } else if (Math.abs(newKey - oldKey) <= Math.ulp(newKey)) {
            openSet.insertOrUpdate(packedPos, oldKey - Math.ulp(oldKey));
        }
    }

    private final Node createNeighborNode(PathPosition position, PathPosition start, PathPosition target, Node parent) {
        return new Node(position, start, target, this.getPathfinderConfiguration().getHeuristicWeights(), this.getPathfinderConfiguration().getHeuristicStrategy(), parent.getDepth() + 1);
    }

    private final boolean isValidByCustomProcessors(EvaluationContext context) {
        boolean bl;
        block3: {
            Iterable $this$all$iv = this.getProcessors();
            boolean $i$f$all = false;
            if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$all$iv) {
                    NodeProcessor it = (NodeProcessor)element$iv;
                    boolean bl2 = false;
                    if (it.isValid(context)) continue;
                    bl = false;
                    break block3;
                }
                bl = true;
            }
        }
        return bl;
    }

    /*
     * WARNING - void declaration
     */
    private final double calculateGCost(EvaluationContext context) {
        double baseCost = context.getBaseTransitionCost();
        Iterable iterable = this.getProcessors();
        double d = 0.0;
        for (Object t : iterable) {
            void it;
            NodeProcessor nodeProcessor = (NodeProcessor)t;
            double d2 = d;
            boolean bl = false;
            double d3 = it.calculateCostContribution(context).getValue();
            d = d2 + d3;
        }
        double additionalCost = d;
        double transitionCost = Math.max(0.0, baseCost + additionalCost);
        return context.getPathCostToPreviousPosition() + transitionCost;
    }

    @Override
    protected void markNodeAsExpanded(@NotNull Node node) {
        Intrinsics.checkNotNullParameter((Object)node, (String)"node");
        PathfindingSession session = this.getSessionOrThrow();
        long packedPos = RegionKey.INSTANCE.pack(node.getPosition());
        session.getClosedSet().add(packedPos);
    }

    @Override
    protected void performAlgorithmCleanup() {
        this.currentSession.remove();
    }

    private final PathfindingSession getSessionOrThrow() {
        PathfindingSession pathfindingSession = this.currentSession.get();
        if (pathfindingSession == null) {
            throw new IllegalStateException("Pathfinding session not initialized. Call initializeSearch() first.");
        }
        return pathfindingSession;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0007\u001a\u0004\b\b\u0010\tR\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/AStarPathfinder$PathfindingSession;", "", "<init>", "()V", "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;", "Lorg/cobalt/api/pathfinder/Node;", "openSetNodes", "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;", "getOpenSetNodes", "()Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;", "Lit/unimi/dsi/fastutil/longs/LongSet;", "closedSet", "Lit/unimi/dsi/fastutil/longs/LongSet;", "getClosedSet", "()Lit/unimi/dsi/fastutil/longs/LongSet;", "cobalt"})
    private static final class PathfindingSession {
        @NotNull
        private final Long2ObjectMap<Node> openSetNodes = (Long2ObjectMap)new Long2ObjectOpenHashMap();
        @NotNull
        private final LongSet closedSet = (LongSet)new LongOpenHashSet();

        @NotNull
        public final Long2ObjectMap<Node> getOpenSetNodes() {
            return this.openSetNodes;
        }

        @NotNull
        public final LongSet getClosedSet() {
            return this.closedSet;
        }
    }
}

