/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.processing;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0007\u0010\u0006\u00a8\u0006\b\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/Processor;", "", "Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;", "context", "", "initializeSearch", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/SearchContext;)V", "finalizeSearch", "cobalt"})
public interface Processor {
    default public void initializeSearch(@NotNull SearchContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
    }

    default public void finalizeSearch(@NotNull SearchContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        public static void initializeSearch(@NotNull Processor $this, @NotNull SearchContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            $this.initializeSearch(context);
        }

        @Deprecated
        public static void finalizeSearch(@NotNull Processor $this, @NotNull SearchContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            $this.finalizeSearch(context);
        }
    }
}

