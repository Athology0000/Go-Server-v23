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
import org.cobalt.api.pathfinder.pathing.processing.Cost;
import org.cobalt.api.pathfinder.pathing.processing.Processor;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.pathing.processing.context.SearchContext;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\b\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0002H\u0016\u00a2\u0006\u0004\b\b\u0010\t\u00a8\u0006\n\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "Lorg/cobalt/api/pathfinder/pathing/processing/Processor;", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "context", "", "isValid", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Z", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "calculateCostContribution", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "cobalt"})
public interface NodeProcessor
extends Processor {
    default public boolean isValid(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        return true;
    }

    @NotNull
    default public Cost calculateCostContribution(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        return Cost.Companion.getZERO();
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        @Deprecated
        public static boolean isValid(@NotNull NodeProcessor $this, @NotNull EvaluationContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            return $this.isValid(context);
        }

        @Deprecated
        @NotNull
        public static Cost calculateCostContribution(@NotNull NodeProcessor $this, @NotNull EvaluationContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            return $this.calculateCostContribution(context);
        }

        @Deprecated
        public static void initializeSearch(@NotNull NodeProcessor $this, @NotNull SearchContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            $this.initializeSearch(context);
        }

        @Deprecated
        public static void finalizeSearch(@NotNull NodeProcessor $this, @NotNull SearchContext context) {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            $this.finalizeSearch(context);
        }
    }
}

