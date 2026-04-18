/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.internal.dungeons.map.RouteStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ*\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001b\u0010\u0011\u001a\u00020\u00102\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0016\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\nR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\nR\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0019\u001a\u0004\b\u001a\u0010\f\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/dungeons/map/LoadedRoute;", "", "", "rawKey", "", "Lorg/cobalt/internal/dungeons/map/RouteStep;", "steps", "<init>", "(Ljava/lang/String;Ljava/util/List;)V", "component1", "()Ljava/lang/String;", "component2", "()Ljava/util/List;", "copy", "(Ljava/lang/String;Ljava/util/List;)Lorg/cobalt/internal/dungeons/map/LoadedRoute;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getRawKey", "Ljava/util/List;", "getSteps", "cobalt"})
public final class LoadedRoute {
    @NotNull
    private final String rawKey;
    @NotNull
    private final List<RouteStep> steps;

    public LoadedRoute(@NotNull String rawKey, @NotNull List<RouteStep> steps) {
        Intrinsics.checkNotNullParameter((Object)rawKey, (String)"rawKey");
        Intrinsics.checkNotNullParameter(steps, (String)"steps");
        this.rawKey = rawKey;
        this.steps = steps;
    }

    @NotNull
    public final String getRawKey() {
        return this.rawKey;
    }

    @NotNull
    public final List<RouteStep> getSteps() {
        return this.steps;
    }

    @NotNull
    public final String component1() {
        return this.rawKey;
    }

    @NotNull
    public final List<RouteStep> component2() {
        return this.steps;
    }

    @NotNull
    public final LoadedRoute copy(@NotNull String rawKey, @NotNull List<RouteStep> steps) {
        Intrinsics.checkNotNullParameter((Object)rawKey, (String)"rawKey");
        Intrinsics.checkNotNullParameter(steps, (String)"steps");
        return new LoadedRoute(rawKey, steps);
    }

    public static /* synthetic */ LoadedRoute copy$default(LoadedRoute loadedRoute, String string, List list, int n, Object object) {
        if ((n & 1) != 0) {
            string = loadedRoute.rawKey;
        }
        if ((n & 2) != 0) {
            list = loadedRoute.steps;
        }
        return loadedRoute.copy(string, list);
    }

    @NotNull
    public String toString() {
        return "LoadedRoute(rawKey=" + this.rawKey + ", steps=" + this.steps + ")";
    }

    public int hashCode() {
        int result = this.rawKey.hashCode();
        result = result * 31 + ((Object)this.steps).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoadedRoute)) {
            return false;
        }
        LoadedRoute loadedRoute = (LoadedRoute)other;
        if (!Intrinsics.areEqual((Object)this.rawKey, (Object)loadedRoute.rawKey)) {
            return false;
        }
        return Intrinsics.areEqual(this.steps, loadedRoute.steps);
    }
}

