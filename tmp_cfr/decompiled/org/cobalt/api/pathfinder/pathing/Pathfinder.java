/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing;

import java.util.concurrent.CompletionStage;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.pathing.result.PathfinderResult;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J%\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ/\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u00022\b\u0010\n\u001a\u0004\u0018\u00010\tH&\u00a2\u0006\u0004\b\u0007\u0010\u000bJ\u000f\u0010\r\u001a\u00020\fH&\u00a2\u0006\u0004\b\r\u0010\u000e\u00a8\u0006\u000f\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/Pathfinder;", "", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "start", "target", "Ljava/util/concurrent/CompletionStage;", "Lorg/cobalt/api/pathfinder/pathing/result/PathfinderResult;", "findPath", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Ljava/util/concurrent/CompletionStage;", "Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;", "context", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/pathing/context/EnvironmentContext;)Ljava/util/concurrent/CompletionStage;", "", "abort", "()V", "cobalt"})
public interface Pathfinder {
    @NotNull
    default public CompletionStage<PathfinderResult> findPath(@NotNull PathPosition start, @NotNull PathPosition target) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        return this.findPath(start, target, null);
    }

    @NotNull
    public CompletionStage<PathfinderResult> findPath(@NotNull PathPosition var1, @NotNull PathPosition var2, @Nullable EnvironmentContext var3);

    public void abort();

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        @NotNull
        public static CompletionStage<PathfinderResult> findPath(@NotNull Pathfinder $this, @NotNull PathPosition start, @NotNull PathPosition target) {
            Intrinsics.checkNotNullParameter((Object)start, (String)"start");
            Intrinsics.checkNotNullParameter((Object)target, (String)"target");
            return $this.findPath(start, target);
        }
    }
}

