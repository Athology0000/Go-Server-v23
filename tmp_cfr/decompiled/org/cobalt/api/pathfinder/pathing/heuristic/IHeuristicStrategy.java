/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.heuristic;

import kotlin.Metadata;
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicContext;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u001f\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H&\u00a2\u0006\u0004\b\n\u0010\u000b\u00a8\u0006\f\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/IHeuristicStrategy;", "", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicContext;", "context", "", "calculate", "(Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicContext;)D", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "from", "to", "calculateTransitionCost", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)D", "cobalt"})
public interface IHeuristicStrategy {
    public double calculate(@NotNull HeuristicContext var1);

    public double calculateTransitionCost(@NotNull PathPosition var1, @NotNull PathPosition var2);
}

