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
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001R\u0014\u0010\u0005\u001a\u00020\u00028&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004R\u0014\u0010\u0007\u001a\u00020\u00028&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0004R\u0014\u0010\u000b\u001a\u00020\b8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000f\u001a\u00020\f8&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000eR \u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00108&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0018\u001a\u0004\u0018\u00010\u00158&X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006\u0019\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getStartPathPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "startPathPosition", "getTargetPathPosition", "targetPathPosition", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "getPathfinderConfiguration", "()Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "pathfinderConfiguration", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "getNavigationPointProvider", "()Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "navigationPointProvider", "", "", "getSharedData", "()Ljava/util/Map;", "sharedData", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "getEnvironmentContext", "()Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "cobalt"})
public interface SearchContext {
    @NotNull
    public PathPosition getStartPathPosition();

    @NotNull
    public PathPosition getTargetPathPosition();

    @NotNull
    public PathfinderConfiguration getPathfinderConfiguration();

    @NotNull
    public NavigationPointProvider getNavigationPointProvider();

    @NotNull
    public Map<String, Object> getSharedData();

    @Nullable
    public EnvironmentContext getEnvironmentContext();
}

