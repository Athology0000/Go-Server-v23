/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.processing.context;

import java.util.Map;
import kotlin.Metadata;
import org.cobalt.api.pathfinder.pathing.configuration.PathfinderConfiguration;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001R\u0014\u0010\u0005\u001a\u00020\u00028&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004R\u0016\u0010\u0007\u001a\u0004\u0018\u00010\u00028&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0004R\u0014\u0010\u000b\u001a\u00020\b8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000f\u001a\u00020\f8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0011\u001a\u00020\f8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u000eR\u0014\u0010\u0013\u001a\u00020\f8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u000eR\u0014\u0010\u0017\u001a\u00020\u00148&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0019\u001a\u0004\u0018\u00010\u00028&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0004R\u0014\u0010\u001d\u001a\u00020\u001a8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010!\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010 R \u0010&\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020\u00010\"8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b$\u0010%R\u0014\u0010(\u001a\u00020\u00028VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b'\u0010\u0004R\u0014\u0010*\u001a\u00020\u00028VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b)\u0010\u0004R\u0016\u0010.\u001a\u0004\u0018\u00010+8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b,\u0010-\u00a8\u0006/\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getCurrentPathPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "currentPathPosition", "getPreviousPathPosition", "previousPathPosition", "", "getCurrentNodeDepth", "()I", "currentNodeDepth", "", "getCurrentNodeHeuristicValue", "()D", "currentNodeHeuristicValue", "getPathCostToPreviousPosition", "pathCostToPreviousPosition", "getBaseTransitionCost", "baseTransitionCost", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "getSearchContext", "()Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "searchContext", "getGrandparentPathPosition", "grandparentPathPosition", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "getPathfinderConfiguration", "()Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "pathfinderConfiguration", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "getNavigationPointProvider", "()Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "navigationPointProvider", "", "", "getSharedData", "()Ljava/util/Map;", "sharedData", "getStartPathPosition", "startPathPosition", "getTargetPathPosition", "targetPathPosition", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "getEnvironmentContext", "()Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "cobalt"})
public interface EvaluationContext {
    @NotNull
    public PathPosition getCurrentPathPosition();

    @Nullable
    public PathPosition getPreviousPathPosition();

    public int getCurrentNodeDepth();

    public double getCurrentNodeHeuristicValue();

    public double getPathCostToPreviousPosition();

    public double getBaseTransitionCost();

    @NotNull
    public SearchContext getSearchContext();

    @Nullable
    public PathPosition getGrandparentPathPosition();

    @NotNull
    default public PathfinderConfiguration getPathfinderConfiguration() {
        return this.getSearchContext().getPathfinderConfiguration();
    }

    @NotNull
    default public NavigationPointProvider getNavigationPointProvider() {
        return this.getSearchContext().getNavigationPointProvider();
    }

    @NotNull
    default public Map<String, Object> getSharedData() {
        return this.getSearchContext().getSharedData();
    }

    @NotNull
    default public PathPosition getStartPathPosition() {
        return this.getSearchContext().getStartPathPosition();
    }

    @NotNull
    default public PathPosition getTargetPathPosition() {
        return this.getSearchContext().getTargetPathPosition();
    }

    @Nullable
    default public EnvironmentContext getEnvironmentContext() {
        return this.getSearchContext().getEnvironmentContext();
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        @NotNull
        public static PathfinderConfiguration getPathfinderConfiguration(@NotNull EvaluationContext $this) {
            return $this.getPathfinderConfiguration();
        }

        @Deprecated
        @NotNull
        public static NavigationPointProvider getNavigationPointProvider(@NotNull EvaluationContext $this) {
            return $this.getNavigationPointProvider();
        }

        @Deprecated
        @NotNull
        public static Map<String, Object> getSharedData(@NotNull EvaluationContext $this) {
            return $this.getSharedData();
        }

        @Deprecated
        @NotNull
        public static PathPosition getStartPathPosition(@NotNull EvaluationContext $this) {
            return $this.getStartPathPosition();
        }

        @Deprecated
        @NotNull
        public static PathPosition getTargetPathPosition(@NotNull EvaluationContext $this) {
            return $this.getTargetPathPosition();
        }

        @Deprecated
        @Nullable
        public static EnvironmentContext getEnvironmentContext(@NotNull EvaluationContext $this) {
            return $this.getEnvironmentContext();
        }
    }
}

