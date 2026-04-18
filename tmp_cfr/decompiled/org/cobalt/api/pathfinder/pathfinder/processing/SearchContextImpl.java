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

import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.configuration.PathfinderConfiguration;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\b\u0005\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0004\b\u000b\u0010\fR\u001a\u0010\u0003\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0003\u0010\r\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0004\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0004\u0010\r\u001a\u0004\b\u0010\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u00058\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\b\u001a\u00020\u00078\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\b\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016R\u001c\u0010\n\u001a\u0004\u0018\u00010\t8\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\n\u0010\u0017\u001a\u0004\b\u0018\u0010\u0019R&\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u001a8\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001f\u0010 \u00a8\u0006!"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/processing/SearchContextImpl;", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "startPathPosition", "targetPathPosition", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "pathfinderConfiguration", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "navigationPointProvider", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "<init>", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getStartPathPosition", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "getTargetPathPosition", "Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "getPathfinderConfiguration", "()Lorg/cobalt/api/pathfinder/pathing/configuration/PathfinderConfiguration;", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "getNavigationPointProvider", "()Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "getEnvironmentContext", "()Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "", "", "", "sharedData", "Ljava/util/Map;", "getSharedData", "()Ljava/util/Map;", "cobalt"})
public final class SearchContextImpl
implements SearchContext {
    @NotNull
    private final PathPosition startPathPosition;
    @NotNull
    private final PathPosition targetPathPosition;
    @NotNull
    private final PathfinderConfiguration pathfinderConfiguration;
    @NotNull
    private final NavigationPointProvider navigationPointProvider;
    @Nullable
    private final EnvironmentContext environmentContext;
    @NotNull
    private final Map<String, Object> sharedData;

    public SearchContextImpl(@NotNull PathPosition startPathPosition, @NotNull PathPosition targetPathPosition, @NotNull PathfinderConfiguration pathfinderConfiguration, @NotNull NavigationPointProvider navigationPointProvider, @Nullable EnvironmentContext environmentContext) {
        Intrinsics.checkNotNullParameter((Object)startPathPosition, (String)"startPathPosition");
        Intrinsics.checkNotNullParameter((Object)targetPathPosition, (String)"targetPathPosition");
        Intrinsics.checkNotNullParameter((Object)pathfinderConfiguration, (String)"pathfinderConfiguration");
        Intrinsics.checkNotNullParameter((Object)navigationPointProvider, (String)"navigationPointProvider");
        this.startPathPosition = startPathPosition;
        this.targetPathPosition = targetPathPosition;
        this.pathfinderConfiguration = pathfinderConfiguration;
        this.navigationPointProvider = navigationPointProvider;
        this.environmentContext = environmentContext;
        this.sharedData = new HashMap();
    }

    @Override
    @NotNull
    public PathPosition getStartPathPosition() {
        return this.startPathPosition;
    }

    @Override
    @NotNull
    public PathPosition getTargetPathPosition() {
        return this.targetPathPosition;
    }

    @Override
    @NotNull
    public PathfinderConfiguration getPathfinderConfiguration() {
        return this.pathfinderConfiguration;
    }

    @Override
    @NotNull
    public NavigationPointProvider getNavigationPointProvider() {
        return this.navigationPointProvider;
    }

    @Override
    @Nullable
    public EnvironmentContext getEnvironmentContext() {
        return this.environmentContext;
    }

    @Override
    @NotNull
    public Map<String, Object> getSharedData() {
        return this.sharedData;
    }
}

