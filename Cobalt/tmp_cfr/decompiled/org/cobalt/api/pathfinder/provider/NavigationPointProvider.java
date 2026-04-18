/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.provider;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.provider.NavigationPoint;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0006J!\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00022\b\u0010\b\u001a\u0004\u0018\u00010\u0007H&\u00a2\u0006\u0004\b\u0005\u0010\t\u00a8\u0006\n\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "position", "Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "getNavigationPoint", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "cobalt"})
public interface NavigationPointProvider {
    @NotNull
    default public NavigationPoint getNavigationPoint(@NotNull PathPosition position) {
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        return this.getNavigationPoint(position, null);
    }

    @NotNull
    public NavigationPoint getNavigationPoint(@NotNull PathPosition var1, @Nullable EnvironmentContext var2);

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        @NotNull
        public static NavigationPoint getNavigationPoint(@NotNull NavigationPointProvider $this, @NotNull PathPosition position) {
            Intrinsics.checkNotNullParameter((Object)position, (String)"position");
            return $this.getNavigationPoint(position);
        }
    }
}

