/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.heuristic;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\b\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001d\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001a\u001a\u0004\b\u001e\u0010\n\u00a8\u0006 "}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "", "", "manhattanWeight", "octileWeight", "perpendicularWeight", "heightWeight", "<init>", "(DDDD)V", "component1", "()D", "component2", "component3", "component4", "copy", "(DDDD)Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getManhattanWeight", "getOctileWeight", "getPerpendicularWeight", "getHeightWeight", "Companion", "cobalt"})
public final class HeuristicWeights {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final double manhattanWeight;
    private final double octileWeight;
    private final double perpendicularWeight;
    private final double heightWeight;
    @NotNull
    private static final HeuristicWeights DEFAULT_WEIGHTS = new HeuristicWeights(0.0, 1.0, 0.0, 0.0);

    public HeuristicWeights(double manhattanWeight, double octileWeight, double perpendicularWeight, double heightWeight) {
        this.manhattanWeight = manhattanWeight;
        this.octileWeight = octileWeight;
        this.perpendicularWeight = perpendicularWeight;
        this.heightWeight = heightWeight;
    }

    public final double getManhattanWeight() {
        return this.manhattanWeight;
    }

    public final double getOctileWeight() {
        return this.octileWeight;
    }

    public final double getPerpendicularWeight() {
        return this.perpendicularWeight;
    }

    public final double getHeightWeight() {
        return this.heightWeight;
    }

    public final double component1() {
        return this.manhattanWeight;
    }

    public final double component2() {
        return this.octileWeight;
    }

    public final double component3() {
        return this.perpendicularWeight;
    }

    public final double component4() {
        return this.heightWeight;
    }

    @NotNull
    public final HeuristicWeights copy(double manhattanWeight, double octileWeight, double perpendicularWeight, double heightWeight) {
        return new HeuristicWeights(manhattanWeight, octileWeight, perpendicularWeight, heightWeight);
    }

    public static /* synthetic */ HeuristicWeights copy$default(HeuristicWeights heuristicWeights, double d, double d2, double d3, double d4, int n, Object object) {
        if ((n & 1) != 0) {
            d = heuristicWeights.manhattanWeight;
        }
        if ((n & 2) != 0) {
            d2 = heuristicWeights.octileWeight;
        }
        if ((n & 4) != 0) {
            d3 = heuristicWeights.perpendicularWeight;
        }
        if ((n & 8) != 0) {
            d4 = heuristicWeights.heightWeight;
        }
        return heuristicWeights.copy(d, d2, d3, d4);
    }

    @NotNull
    public String toString() {
        return "HeuristicWeights(manhattanWeight=" + this.manhattanWeight + ", octileWeight=" + this.octileWeight + ", perpendicularWeight=" + this.perpendicularWeight + ", heightWeight=" + this.heightWeight + ")";
    }

    public int hashCode() {
        int result = Double.hashCode(this.manhattanWeight);
        result = result * 31 + Double.hashCode(this.octileWeight);
        result = result * 31 + Double.hashCode(this.perpendicularWeight);
        result = result * 31 + Double.hashCode(this.heightWeight);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HeuristicWeights)) {
            return false;
        }
        HeuristicWeights heuristicWeights = (HeuristicWeights)other;
        if (Double.compare(this.manhattanWeight, heuristicWeights.manhattanWeight) != 0) {
            return false;
        }
        if (Double.compare(this.octileWeight, heuristicWeights.octileWeight) != 0) {
            return false;
        }
        if (Double.compare(this.perpendicularWeight, heuristicWeights.perpendicularWeight) != 0) {
            return false;
        }
        return Double.compare(this.heightWeight, heuristicWeights.heightWeight) == 0;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "DEFAULT_WEIGHTS", "Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "getDEFAULT_WEIGHTS", "()Lorg/cobalt/api/pathfinder/pathing/heuristic/HeuristicWeights;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final HeuristicWeights getDEFAULT_WEIGHTS() {
            return DEFAULT_WEIGHTS;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

