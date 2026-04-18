/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.configuration;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.provider.NavigationPoint;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c2\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0016\u00a2\u0006\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/api/pathfinder/pathing/configuration/DefaultNavigationPointProvider;", "Lorg/cobalt/api/pathfinder/provider/NavigationPointProvider;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "position", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "environmentContext", "Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "getNavigationPoint", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "cobalt"})
final class DefaultNavigationPointProvider
implements NavigationPointProvider {
    @NotNull
    public static final DefaultNavigationPointProvider INSTANCE = new DefaultNavigationPointProvider();

    private DefaultNavigationPointProvider() {
    }

    @Override
    @NotNull
    public NavigationPoint getNavigationPoint(@NotNull PathPosition position, @Nullable EnvironmentContext environmentContext) {
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        return new NavigationPoint(){

            public boolean isTraversable() {
                return true;
            }

            public boolean hasFloor() {
                return true;
            }

            public double getFloorLevel() {
                return 0.0;
            }

            public boolean isClimbable() {
                return false;
            }

            public boolean isLiquid() {
                return false;
            }
        };
    }
}

