/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.configuration;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.INeighborStrategy;
import org.cobalt.api.pathfinder.pathing.NeighborStrategies;
import org.cobalt.api.pathfinder.pathing.configuration.DefaultNavigationPointProvider;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicWeights;
import org.cobalt.api.pathfinder.pathing.heuristic.IHeuristicStrategy;
import org.cobalt.api.pathfinder.pathing.heuristic.LinearHeuristicStrategy;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u000e\n\u0002\b\u0014\b\u0086\b\u0018\u0000 >2\u00020\u0001:\u0001>Bg\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010\u0017\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0016J\u0010\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u0019J\u0010\u0010\u001b\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0010\u0010\u001d\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u00c6\u0003\u00a2\u0006\u0004\b\u001f\u0010 J\u0010\u0010!\u001a\u00020\u000fH\u00c6\u0003\u00a2\u0006\u0004\b!\u0010\"J\u0010\u0010#\u001a\u00020\u0011H\u00c6\u0003\u00a2\u0006\u0004\b#\u0010$Jp\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\n2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0012\u001a\u00020\u0011H\u00c6\u0001\u00a2\u0006\u0004\b%\u0010&J\u001b\u0010(\u001a\u00020\u00052\b\u0010'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b(\u0010)J\u0011\u0010*\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b*\u0010\u0016J\u0011\u0010,\u001a\u00020+H\u00d6\u0081\u0004\u00a2\u0006\u0004\b,\u0010-R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010.\u001a\u0004\b/\u0010\u0016R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010.\u001a\u0004\b0\u0010\u0016R\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u00101\u001a\u0004\b2\u0010\u0019R\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u00101\u001a\u0004\b3\u0010\u0019R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u00104\u001a\u0004\b5\u0010\u001cR\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u00106\u001a\u0004\b7\u0010\u001eR\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u00108\u001a\u0004\b9\u0010 R\u0017\u0010\u0010\u001a\u00020\u000f8\u0006\u00a2\u0006\f\n\u0004\b\u0010\u0010:\u001a\u0004\b;\u0010\"R\u0017\u0010\u0012\u001a\u00020\u00118\u0006\u00a2\u0006\f\n\u0004\b\u0012\u0010<\u001a\u0004\b=\u0010$\u00a8\u0006?"}, d2={"Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "", "", "maxIterations", "maxLength", "", "async", "fallback", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "provider", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "heuristicWeights", "", "Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "processors", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "neighborStrategy", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "heuristicStrategy", "<init>", "(IIZZLorg/cobalt/api/pathfinder/provider/NavigationPointProvider;Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;Ljava/util/List;Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;)V", "component1", "()I", "component2", "component3", "()Z", "component4", "component5", "()Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "component6", "()Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "component7", "()Ljava/util/List;", "component8", "()Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "component9", "()Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "copy", "(IIZZLorg/cobalt/api/pathfinder/provider/NavigationPointProvider;Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;Ljava/util/List;Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;)Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getMaxIterations", "getMaxLength", "Z", "getAsync", "getFallback", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "getProvider", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "getHeuristicWeights", "Ljava/util/List;", "getProcessors", "Lorg/cobalt/api/pathfinder/pathing/INeighborStrategy;", "getNeighborStrategy", "Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "getHeuristicStrategy", "Companion", "cobalt"})
public final class PathfinderConfiguration {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int maxIterations;
    private final int maxLength;
    private final boolean async;
    private final boolean fallback;
    @NotNull
    private final NavigationPointProvider provider;
    @NotNull
    private final HeuristicWeights heuristicWeights;
    @NotNull
    private final List<NodeProcessor> processors;
    @NotNull
    private final INeighborStrategy neighborStrategy;
    @NotNull
    private final IHeuristicStrategy heuristicStrategy;
    @NotNull
    private static final PathfinderConfiguration DEFAULT = new PathfinderConfiguration(0, 0, false, false, null, null, null, null, null, 511, null);

    public PathfinderConfiguration(int maxIterations, int maxLength, boolean async, boolean fallback, @NotNull NavigationPointProvider provider, @NotNull HeuristicWeights heuristicWeights, @NotNull List<? extends NodeProcessor> processors, @NotNull INeighborStrategy neighborStrategy, @NotNull IHeuristicStrategy heuristicStrategy) {
        Intrinsics.checkNotNullParameter((Object)provider, (String)"provider");
        Intrinsics.checkNotNullParameter((Object)heuristicWeights, (String)"heuristicWeights");
        Intrinsics.checkNotNullParameter(processors, (String)"processors");
        Intrinsics.checkNotNullParameter((Object)neighborStrategy, (String)"neighborStrategy");
        Intrinsics.checkNotNullParameter((Object)heuristicStrategy, (String)"heuristicStrategy");
        this.maxIterations = maxIterations;
        this.maxLength = maxLength;
        this.async = async;
        this.fallback = fallback;
        this.provider = provider;
        this.heuristicWeights = heuristicWeights;
        this.processors = processors;
        this.neighborStrategy = neighborStrategy;
        this.heuristicStrategy = heuristicStrategy;
    }

    public /* synthetic */ PathfinderConfiguration(int n, int n2, boolean bl, boolean bl2, NavigationPointProvider navigationPointProvider, HeuristicWeights heuristicWeights, List list, INeighborStrategy iNeighborStrategy, IHeuristicStrategy iHeuristicStrategy, int n3, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n3 & 1) != 0) {
            n = 5000;
        }
        if ((n3 & 2) != 0) {
            n2 = 0;
        }
        if ((n3 & 4) != 0) {
            bl = false;
        }
        if ((n3 & 8) != 0) {
            bl2 = true;
        }
        if ((n3 & 0x10) != 0) {
            navigationPointProvider = DefaultNavigationPointProvider.INSTANCE;
        }
        if ((n3 & 0x20) != 0) {
            heuristicWeights = HeuristicWeights.Companion.getDEFAULT_WEIGHTS();
        }
        if ((n3 & 0x40) != 0) {
            list = CollectionsKt.emptyList();
        }
        if ((n3 & 0x80) != 0) {
            iNeighborStrategy = NeighborStrategies.INSTANCE.getVERTICAL_AND_HORIZONTAL();
        }
        if ((n3 & 0x100) != 0) {
            iHeuristicStrategy = new LinearHeuristicStrategy();
        }
        this(n, n2, bl, bl2, navigationPointProvider, heuristicWeights, list, iNeighborStrategy, iHeuristicStrategy);
    }

    public final int getMaxIterations() {
        return this.maxIterations;
    }

    public final int getMaxLength() {
        return this.maxLength;
    }

    public final boolean getAsync() {
        return this.async;
    }

    public final boolean getFallback() {
        return this.fallback;
    }

    @NotNull
    public final NavigationPointProvider getProvider() {
        return this.provider;
    }

    @NotNull
    public final HeuristicWeights getHeuristicWeights() {
        return this.heuristicWeights;
    }

    @NotNull
    public final List<NodeProcessor> getProcessors() {
        return this.processors;
    }

    @NotNull
    public final INeighborStrategy getNeighborStrategy() {
        return this.neighborStrategy;
    }

    @NotNull
    public final IHeuristicStrategy getHeuristicStrategy() {
        return this.heuristicStrategy;
    }

    public final int component1() {
        return this.maxIterations;
    }

    public final int component2() {
        return this.maxLength;
    }

    public final boolean component3() {
        return this.async;
    }

    public final boolean component4() {
        return this.fallback;
    }

    @NotNull
    public final NavigationPointProvider component5() {
        return this.provider;
    }

    @NotNull
    public final HeuristicWeights component6() {
        return this.heuristicWeights;
    }

    @NotNull
    public final List<NodeProcessor> component7() {
        return this.processors;
    }

    @NotNull
    public final INeighborStrategy component8() {
        return this.neighborStrategy;
    }

    @NotNull
    public final IHeuristicStrategy component9() {
        return this.heuristicStrategy;
    }

    @NotNull
    public final PathfinderConfiguration copy(int maxIterations, int maxLength, boolean async, boolean fallback, @NotNull NavigationPointProvider provider, @NotNull HeuristicWeights heuristicWeights, @NotNull List<? extends NodeProcessor> processors, @NotNull INeighborStrategy neighborStrategy, @NotNull IHeuristicStrategy heuristicStrategy) {
        Intrinsics.checkNotNullParameter((Object)provider, (String)"provider");
        Intrinsics.checkNotNullParameter((Object)heuristicWeights, (String)"heuristicWeights");
        Intrinsics.checkNotNullParameter(processors, (String)"processors");
        Intrinsics.checkNotNullParameter((Object)neighborStrategy, (String)"neighborStrategy");
        Intrinsics.checkNotNullParameter((Object)heuristicStrategy, (String)"heuristicStrategy");
        return new PathfinderConfiguration(maxIterations, maxLength, async, fallback, provider, heuristicWeights, processors, neighborStrategy, heuristicStrategy);
    }

    public static /* synthetic */ PathfinderConfiguration copy$default(PathfinderConfiguration pathfinderConfiguration, int n, int n2, boolean bl, boolean bl2, NavigationPointProvider navigationPointProvider, HeuristicWeights heuristicWeights, List list, INeighborStrategy iNeighborStrategy, IHeuristicStrategy iHeuristicStrategy, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = pathfinderConfiguration.maxIterations;
        }
        if ((n3 & 2) != 0) {
            n2 = pathfinderConfiguration.maxLength;
        }
        if ((n3 & 4) != 0) {
            bl = pathfinderConfiguration.async;
        }
        if ((n3 & 8) != 0) {
            bl2 = pathfinderConfiguration.fallback;
        }
        if ((n3 & 0x10) != 0) {
            navigationPointProvider = pathfinderConfiguration.provider;
        }
        if ((n3 & 0x20) != 0) {
            heuristicWeights = pathfinderConfiguration.heuristicWeights;
        }
        if ((n3 & 0x40) != 0) {
            list = pathfinderConfiguration.processors;
        }
        if ((n3 & 0x80) != 0) {
            iNeighborStrategy = pathfinderConfiguration.neighborStrategy;
        }
        if ((n3 & 0x100) != 0) {
            iHeuristicStrategy = pathfinderConfiguration.heuristicStrategy;
        }
        return pathfinderConfiguration.copy(n, n2, bl, bl2, navigationPointProvider, heuristicWeights, list, iNeighborStrategy, iHeuristicStrategy);
    }

    @NotNull
    public String toString() {
        return "PathfinderConfiguration(maxIterations=" + this.maxIterations + ", maxLength=" + this.maxLength + ", async=" + this.async + ", fallback=" + this.fallback + ", provider=" + this.provider + ", heuristicWeights=" + this.heuristicWeights + ", processors=" + this.processors + ", neighborStrategy=" + this.neighborStrategy + ", heuristicStrategy=" + this.heuristicStrategy + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.maxIterations);
        result = result * 31 + Integer.hashCode(this.maxLength);
        result = result * 31 + Boolean.hashCode(this.async);
        result = result * 31 + Boolean.hashCode(this.fallback);
        result = result * 31 + this.provider.hashCode();
        result = result * 31 + this.heuristicWeights.hashCode();
        result = result * 31 + ((Object)this.processors).hashCode();
        result = result * 31 + this.neighborStrategy.hashCode();
        result = result * 31 + this.heuristicStrategy.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PathfinderConfiguration)) {
            return false;
        }
        PathfinderConfiguration pathfinderConfiguration = (PathfinderConfiguration)other;
        if (this.maxIterations != pathfinderConfiguration.maxIterations) {
            return false;
        }
        if (this.maxLength != pathfinderConfiguration.maxLength) {
            return false;
        }
        if (this.async != pathfinderConfiguration.async) {
            return false;
        }
        if (this.fallback != pathfinderConfiguration.fallback) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.provider, (Object)pathfinderConfiguration.provider)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.heuristicWeights, (Object)pathfinderConfiguration.heuristicWeights)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.processors, pathfinderConfiguration.processors)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.neighborStrategy, (Object)pathfinderConfiguration.neighborStrategy)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.heuristicStrategy, (Object)pathfinderConfiguration.heuristicStrategy);
    }

    public PathfinderConfiguration() {
        this(0, 0, false, false, null, null, null, null, null, 511, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "pathfinderConfiguration", "deepCopy", "(Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;)Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "DEFAULT", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "getDEFAULT", "()Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final PathfinderConfiguration getDEFAULT() {
            return DEFAULT;
        }

        @NotNull
        public final PathfinderConfiguration deepCopy(@NotNull PathfinderConfiguration pathfinderConfiguration) {
            Intrinsics.checkNotNullParameter((Object)pathfinderConfiguration, (String)"pathfinderConfiguration");
            return PathfinderConfiguration.copy$default(pathfinderConfiguration, 0, 0, false, false, null, null, CollectionsKt.toList((Iterable)pathfinderConfiguration.getProcessors()), null, null, 447, null);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

