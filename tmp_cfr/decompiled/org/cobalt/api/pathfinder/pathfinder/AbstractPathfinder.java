/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.cobalt.api.pathfinder.Node;
import org.cobalt.api.pathfinder.pathfinder.heap.PrimitiveMinHeap;
import org.cobalt.api.pathfinder.pathfinder.processing.EvaluationContextImpl;
import org.cobalt.api.pathfinder.pathfinder.processing.SearchContextImpl;
import org.cobalt.api.pathfinder.pathing.INeighborStrategy;
import org.cobalt.api.pathfinder.pathing.Pathfinder;
import org.cobalt.api.pathfinder.pathing.configuration.PathfinderConfiguration;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.cobalt.api.pathfinder.pathing.result.Path;
import org.cobalt.api.pathfinder.pathing.result.PathState;
import org.cobalt.api.pathfinder.pathing.result.PathfinderResult;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.result.PathImpl;
import org.cobalt.api.pathfinder.result.PathfinderResultImpl;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u0003\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u0000 _2\u00020\u0001:\u0001_B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J/\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\b\u0010\n\u001a\u0004\u0018\u00010\tH\u0016\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0010\u001a\u00020\u000fH\u0016\u00a2\u0006\u0004\b\u0010\u0010\u0011J/\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\tH\u0002\u00a2\u0006\u0004\b\u0013\u0010\u000eJ)\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\tH\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001d\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u0018\u00a2\u0006\u0004\b\u001a\u0010\u001bJ'\u0010\u001d\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\u001c\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ'\u0010#\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00062\u0006\u0010\"\u001a\u00020!H\u0002\u00a2\u0006\u0004\b#\u0010$J\u001f\u0010'\u001a\u00020\u00162\u0006\u0010%\u001a\u00020\u00062\u0006\u0010&\u001a\u00020\u0006H\u0004\u00a2\u0006\u0004\b'\u0010(J\u0017\u0010+\u001a\u00020*2\u0006\u0010)\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b+\u0010,J/\u0010/\u001a\u00020\f2\u0006\u0010.\u001a\u00020-2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\u001c\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b/\u00100J'\u00103\u001a\u0002022\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u00101\u001a\u00020\u0016H\u0004\u00a2\u0006\u0004\b3\u00104J\u001d\u00107\u001a\b\u0012\u0004\u0012\u00020\u0006062\u0006\u00105\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b7\u00108J'\u0010<\u001a\u00020\u000f2\u0006\u00109\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010;\u001a\u00020:H$\u00a2\u0006\u0004\b<\u0010=J\u0017\u0010>\u001a\u00020\u00162\u0006\u0010;\u001a\u00020:H$\u00a2\u0006\u0004\b>\u0010?J\u000f\u0010@\u001a\u00020\u000fH$\u00a2\u0006\u0004\b@\u0010\u0011J\u0017\u0010A\u001a\u00020\u000f2\u0006\u00109\u001a\u00020\u0016H$\u00a2\u0006\u0004\bA\u0010BJ\u000f\u0010C\u001a\u00020\u000fH$\u00a2\u0006\u0004\bC\u0010\u0011J7\u0010H\u001a\u00020\u000f2\u0006\u0010D\u001a\u00020\u00062\u0006\u0010E\u001a\u00020\u00062\u0006\u0010)\u001a\u00020\u00162\u0006\u0010;\u001a\u00020:2\u0006\u0010G\u001a\u00020FH$\u00a2\u0006\u0004\bH\u0010IR\u001a\u0010\u0003\u001a\u00020\u00028\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\b\u0003\u0010J\u001a\u0004\bK\u0010LR\u001a\u0010N\u001a\u00020M8\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\bN\u0010O\u001a\u0004\bP\u0010QR \u0010S\u001a\b\u0012\u0004\u0012\u00020R068\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\bS\u0010T\u001a\u0004\bU\u0010VR\u001a\u0010X\u001a\u00020W8\u0004X\u0084\u0004\u00a2\u0006\f\n\u0004\bX\u0010Y\u001a\u0004\bZ\u0010[R\u0014\u0010]\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010^\u00a8\u0006`"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/AbstractPathfinder;", "Lorg/cobalt/api/pathfinder/pathing/Pathfinder;", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "pathfinderConfiguration", "<init>", "(Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;)V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "start", "target", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "context", "Ljava/util/concurrent/CompletionStage;", "Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "findPath", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Ljava/util/concurrent/CompletionStage;", "", "abort", "()V", "environmentContext", "initiatePathing", "executePathingAlgorithm", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "Lorg/cobalt/api/pathfinder/Node;", "neighbor", "", "fCost", "calculateHeapKey", "(Lorg/cobalt/api/pathfinder/Node;D)D", "fallbackNode", "createAbortedResult", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;)Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "originalStart", "originalTarget", "", "throwable", "handlePathingException", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Ljava/lang/Throwable;)Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "startPos", "targetPos", "createStartNode", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Lorg/cobalt/api/pathfinder/Node;", "currentNode", "", "hasReachedPathLengthLimit", "(Lorg/cobalt/api/pathfinder/Node;)Z", "", "depthReached", "determinePostLoopResult", "(ILorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;)Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "endNode", "Lorg/cobalt/api/pathfinder/pathing/result/Path;", "reconstructPath", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;)Lorg/cobalt/api/pathfinder/pathing/result/Path;", "leafNode", "", "tracePathPositionsFromNode", "(Lorg/cobalt/api/pathfinder/Node;)Ljava/util/List;", "node", "Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;", "openSet", "insertStartNode", "(Lorg/cobalt/api/pathfinder/Node;DLorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;)V", "extractBestNode", "(Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;)Lorg/cobalt/api/pathfinder/Node;", "initializeSearch", "markNodeAsExpanded", "(Lorg/cobalt/api/pathfinder/Node;)V", "performAlgorithmCleanup", "requestStart", "requestTarget", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "searchContext", "processSuccessors", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/Node;Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;)V", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "getPathfinderConfiguration", "()Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "navigationPointProvider", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "getNavigationPointProvider", "()Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "processors", "Ljava/util/List;", "getProcessors", "()Ljava/util/List;", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "neighborStrategy", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "getNeighborStrategy", "()Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "Ljava/util/concurrent/atomic/AtomicBoolean;", "abortRequested", "Ljava/util/concurrent/atomic/AtomicBoolean;", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAbstractPathfinder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractPathfinder.kt\norg/cobalt/api/pathfinder/pathfinder/AbstractPathfinder\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,294:1\n1915#2,2:295\n1807#2,3:297\n1915#2,2:300\n*S KotlinDebug\n*F\n+ 1 AbstractPathfinder.kt\norg/cobalt/api/pathfinder/pathfinder/AbstractPathfinder\n*L\n116#1:295,2\n127#1:297,3\n181#1:300,2\n*E\n"})
public abstract class AbstractPathfinder
implements Pathfinder {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final PathfinderConfiguration pathfinderConfiguration;
    @NotNull
    private final NavigationPointProvider navigationPointProvider;
    @NotNull
    private final List<NodeProcessor> processors;
    @NotNull
    private final INeighborStrategy neighborStrategy;
    @NotNull
    private final AtomicBoolean abortRequested;
    @NotNull
    private static final Set<PathPosition> EMPTY_PATH_POSITIONS = new LinkedHashSet(0);
    private static final double TIE_BREAKER_WEIGHT = 1.0E-6;
    @NotNull
    private static final ExecutorService PATHING_EXECUTOR_SERVICE;

    public AbstractPathfinder(@NotNull PathfinderConfiguration pathfinderConfiguration) {
        Intrinsics.checkNotNullParameter((Object)pathfinderConfiguration, (String)"pathfinderConfiguration");
        this.pathfinderConfiguration = pathfinderConfiguration;
        this.navigationPointProvider = this.pathfinderConfiguration.getProvider();
        this.processors = this.pathfinderConfiguration.getProcessors();
        this.neighborStrategy = this.pathfinderConfiguration.getNeighborStrategy();
        this.abortRequested = new AtomicBoolean(false);
    }

    @NotNull
    protected final PathfinderConfiguration getPathfinderConfiguration() {
        return this.pathfinderConfiguration;
    }

    @NotNull
    protected final NavigationPointProvider getNavigationPointProvider() {
        return this.navigationPointProvider;
    }

    @NotNull
    protected final List<NodeProcessor> getProcessors() {
        return this.processors;
    }

    @NotNull
    protected final INeighborStrategy getNeighborStrategy() {
        return this.neighborStrategy;
    }

    @Override
    @NotNull
    public CompletionStage<PathfinderResult> findPath(@NotNull PathPosition start, @NotNull PathPosition target, @Nullable EnvironmentContext context) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        this.abortRequested.set(false);
        return this.initiatePathing(start, target, context);
    }

    @Override
    public void abort() {
        this.abortRequested.set(true);
    }

    private final CompletionStage<PathfinderResult> initiatePathing(PathPosition start, PathPosition target, EnvironmentContext environmentContext) {
        CompletionStage completionStage;
        PathPosition effectiveStart = start.floor();
        PathPosition effectiveTarget = target.floor();
        if (this.pathfinderConfiguration.getAsync()) {
            CompletionStage completionStage2 = CompletableFuture.supplyAsync(() -> AbstractPathfinder.initiatePathing$lambda$0(this, effectiveStart, effectiveTarget, environmentContext), PATHING_EXECUTOR_SERVICE).exceptionally(arg_0 -> AbstractPathfinder.initiatePathing$lambda$1(this, start, target, arg_0));
            Intrinsics.checkNotNull((Object)completionStage2);
            completionStage = completionStage2;
        } else {
            CompletableFuture<PathfinderResult> completableFuture;
            try {
                completableFuture = CompletableFuture.completedFuture(this.executePathingAlgorithm(effectiveStart, effectiveTarget, environmentContext));
            }
            catch (Exception e) {
                completableFuture = CompletableFuture.completedFuture(this.handlePathingException(start, target, e));
            }
            CompletableFuture<PathfinderResult> completableFuture2 = completableFuture;
            Intrinsics.checkNotNull(completableFuture2);
            completionStage = completableFuture2;
        }
        return completionStage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final PathfinderResult executePathingAlgorithm(PathPosition start, PathPosition target, EnvironmentContext environmentContext) {
        this.initializeSearch();
        SearchContextImpl searchContext = new SearchContextImpl(start, target, this.pathfinderConfiguration, this.navigationPointProvider, environmentContext);
        try {
            double $i$f$forEach;
            boolean isStartNodeInvalid;
            Object $this$any$iv;
            Node startNode;
            block36: {
                Iterable $this$forEach$iv = this.processors;
                boolean $i$f$forEach2 = false;
                for (Object element$iv : $this$forEach$iv) {
                    NodeProcessor it = (NodeProcessor)element$iv;
                    boolean bl = false;
                    it.initializeSearch(searchContext);
                }
                startNode = this.createStartNode(start, target);
                EvaluationContextImpl startNodeContext = new EvaluationContextImpl(searchContext, startNode, null, this.pathfinderConfiguration.getHeuristicStrategy());
                $this$any$iv = this.processors;
                boolean $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    v0 = false;
                } else {
                    Iterator bl = $this$any$iv.iterator();
                    while (bl.hasNext()) {
                        Object element$iv = bl.next();
                        NodeProcessor it = (NodeProcessor)element$iv;
                        boolean bl2 = false;
                        if (!(!it.isValid(startNodeContext))) continue;
                        v0 = true;
                        break block36;
                    }
                    v0 = isStartNodeInvalid = false;
                }
            }
            if (isStartNodeInvalid) {
                $this$any$iv = new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, (Collection<PathPosition>)EMPTY_PATH_POSITIONS));
                return $this$any$iv;
            }
            PrimitiveMinHeap openSet = new PrimitiveMinHeap(1024);
            try {
                $i$f$forEach = this.calculateHeapKey(startNode, startNode.getFCost());
            }
            catch (Throwable t) {
                $i$f$forEach = startNode.getFCost();
            }
            double startKey = $i$f$forEach;
            this.insertStartNode(startNode, startKey, openSet);
            int currentDepth = 0;
            Node bestFallbackNode = startNode;
            while (!openSet.isEmpty() && currentDepth < this.pathfinderConfiguration.getMaxIterations()) {
                ++currentDepth;
                if (this.abortRequested.get()) {
                    PathfinderResult t = this.createAbortedResult(start, target, bestFallbackNode);
                    return t;
                }
                Node currentNode = this.extractBestNode(openSet);
                this.markNodeAsExpanded(currentNode);
                if (currentNode.getHeuristic() < bestFallbackNode.getHeuristic()) {
                    bestFallbackNode = currentNode;
                }
                if (this.hasReachedPathLengthLimit(currentNode)) {
                    PathfinderResult finalizeErrors = new PathfinderResultImpl(PathState.LENGTH_LIMITED, this.reconstructPath(start, target, currentNode));
                    return finalizeErrors;
                }
                if (currentNode.isTarget(target)) {
                    PathfinderResult finalizeErrors = new PathfinderResultImpl(PathState.FOUND, this.reconstructPath(start, target, currentNode));
                    return finalizeErrors;
                }
                this.processSuccessors(start, target, currentNode, openSet, searchContext);
            }
            PathfinderResult currentNode = this.determinePostLoopResult(currentDepth, start, target, bestFallbackNode);
            return currentNode;
        }
        catch (Exception e) {
            PathfinderResult isStartNodeInvalid = new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, (Collection<PathPosition>)EMPTY_PATH_POSITIONS));
            return isStartNodeInvalid;
        }
        finally {
            List finalizeErrors = new ArrayList();
            Iterable $this$forEach$iv = this.processors;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                NodeProcessor processor = (NodeProcessor)element$iv;
                boolean bl = false;
                try {
                    processor.finalizeSearch(searchContext);
                }
                catch (Exception e) {
                    finalizeErrors.add(e);
                }
            }
            this.performAlgorithmCleanup();
        }
    }

    public final double calculateHeapKey(@NotNull Node neighbor, double fCost) {
        Intrinsics.checkNotNullParameter((Object)neighbor, (String)"neighbor");
        double heuristic = neighbor.getHeuristic();
        double tieBreaker = 1.0E-6 * (heuristic / (Math.abs(fCost) + 1.0));
        double heapKey = fCost - tieBreaker;
        if (Double.isNaN(heapKey) || Double.isInfinite(heapKey)) {
            heapKey = fCost;
        }
        return heapKey;
    }

    private final PathfinderResult createAbortedResult(PathPosition start, PathPosition target, Node fallbackNode) {
        this.abortRequested.set(false);
        return new PathfinderResultImpl(PathState.ABORTED, this.reconstructPath(start, target, fallbackNode));
    }

    private final PathfinderResult handlePathingException(PathPosition originalStart, PathPosition originalTarget, Throwable throwable) {
        return new PathfinderResultImpl(PathState.FAILED, new PathImpl(originalStart, originalTarget, (Collection<PathPosition>)EMPTY_PATH_POSITIONS));
    }

    @NotNull
    protected final Node createStartNode(@NotNull PathPosition startPos, @NotNull PathPosition targetPos) {
        Intrinsics.checkNotNullParameter((Object)startPos, (String)"startPos");
        Intrinsics.checkNotNullParameter((Object)targetPos, (String)"targetPos");
        return new Node(startPos, startPos, targetPos, this.pathfinderConfiguration.getHeuristicWeights(), this.pathfinderConfiguration.getHeuristicStrategy(), 0);
    }

    private final boolean hasReachedPathLengthLimit(Node currentNode) {
        int maxLength = this.pathfinderConfiguration.getMaxLength();
        return maxLength > 0 && currentNode.getDepth() >= maxLength;
    }

    private final PathfinderResult determinePostLoopResult(int depthReached, PathPosition start, PathPosition target, Node fallbackNode) {
        return depthReached >= this.pathfinderConfiguration.getMaxIterations() ? (PathfinderResult)new PathfinderResultImpl(PathState.MAX_ITERATIONS_REACHED, this.reconstructPath(start, target, fallbackNode)) : (this.pathfinderConfiguration.getFallback() ? (PathfinderResult)new PathfinderResultImpl(PathState.FALLBACK, this.reconstructPath(start, target, fallbackNode)) : (PathfinderResult)new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, (Collection<PathPosition>)EMPTY_PATH_POSITIONS)));
    }

    @NotNull
    protected final Path reconstructPath(@NotNull PathPosition start, @NotNull PathPosition target, @NotNull Node endNode) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        Intrinsics.checkNotNullParameter((Object)endNode, (String)"endNode");
        if (endNode.getParent() == null && endNode.getDepth() == 0) {
            return new PathImpl(start, target, CollectionsKt.listOf((Object)endNode.getPosition()));
        }
        List<PathPosition> pathPositions = this.tracePathPositionsFromNode(endNode);
        return new PathImpl(start, target, (Collection<PathPosition>)pathPositions);
    }

    private final List<PathPosition> tracePathPositionsFromNode(Node leafNode) {
        return CollectionsKt.reversed((Iterable)SequencesKt.toList((Sequence)SequencesKt.map((Sequence)SequencesKt.generateSequence((Object)leafNode, AbstractPathfinder::tracePathPositionsFromNode$lambda$0), AbstractPathfinder::tracePathPositionsFromNode$lambda$1)));
    }

    protected abstract void insertStartNode(@NotNull Node var1, double var2, @NotNull PrimitiveMinHeap var4);

    @NotNull
    protected abstract Node extractBestNode(@NotNull PrimitiveMinHeap var1);

    protected abstract void initializeSearch();

    protected abstract void markNodeAsExpanded(@NotNull Node var1);

    protected abstract void performAlgorithmCleanup();

    protected abstract void processSuccessors(@NotNull PathPosition var1, @NotNull PathPosition var2, @NotNull Node var3, @NotNull PrimitiveMinHeap var4, @NotNull SearchContext var5);

    private static final PathfinderResult initiatePathing$lambda$0(AbstractPathfinder this$0, PathPosition $effectiveStart, PathPosition $effectiveTarget, EnvironmentContext $environmentContext) {
        return this$0.executePathingAlgorithm($effectiveStart, $effectiveTarget, $environmentContext);
    }

    private static final PathfinderResult initiatePathing$lambda$1(AbstractPathfinder this$0, PathPosition $start, PathPosition $target, Throwable throwable) {
        Intrinsics.checkNotNull((Object)throwable);
        return this$0.handlePathingException($start, $target, throwable);
    }

    private static final Node tracePathPositionsFromNode$lambda$0(Node it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getParent();
    }

    private static final PathPosition tracePathPositionsFromNode$lambda$1(Node it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getPosition();
    }

    private static final void _init_$lambda$0() {
        AbstractPathfinder.Companion.shutdownExecutor();
    }

    static {
        ExecutorService executorService = Executors.newWorkStealingPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
        Intrinsics.checkNotNullExpressionValue((Object)executorService, (String)"newWorkStealingPool(...)");
        PATHING_EXECUTOR_SERVICE = executorService;
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractPathfinder::_init_$lambda$0));
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0003R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\b\u0010\tR\u0014\u0010\u000b\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\fR\u0014\u0010\u000e\u001a\u00020\r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/AbstractPathfinder$Companion;", "", "<init>", "()V", "", "shutdownExecutor", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "EMPTY_PATH_POSITIONS", "Ljava/util/Set;", "", "TIE_BREAKER_WEIGHT", "D", "Ljava/util/concurrent/ExecutorService;", "PATHING_EXECUTOR_SERVICE", "Ljava/util/concurrent/ExecutorService;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        private final void shutdownExecutor() {
            PATHING_EXECUTOR_SERVICE.shutdown();
            try {
                if (!PATHING_EXECUTOR_SERVICE.awaitTermination(5L, TimeUnit.SECONDS)) {
                    PATHING_EXECUTOR_SERVICE.shutdownNow();
                }
            }
            catch (InterruptedException e) {
                PATHING_EXECUTOR_SERVICE.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

