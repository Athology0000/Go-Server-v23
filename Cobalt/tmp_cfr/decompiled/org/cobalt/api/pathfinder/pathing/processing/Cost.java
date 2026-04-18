/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.processing;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\b\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0011\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u0002H\u00c2\u0001\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\f\u001a\u00020\u000b2\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\f\u0010\rJ\u0011\u0010\u000f\u001a\u00020\u000eH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\u0007\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "", "", "value", "<init>", "(D)V", "component1", "()D", "copy", "(D)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getValue", "Companion", "cobalt"})
public final class Cost {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final double value;
    @NotNull
    private static final Cost ZERO = new Cost(0.0);

    private Cost(double value) {
        this.value = value;
    }

    public final double getValue() {
        return this.value;
    }

    public final double component1() {
        return this.value;
    }

    private final Cost copy(double value) {
        return new Cost(value);
    }

    static /* synthetic */ Cost copy$default(Cost cost, double d, int n, Object object) {
        if ((n & 1) != 0) {
            d = cost.value;
        }
        return cost.copy(d);
    }

    @NotNull
    public String toString() {
        return "Cost(value=" + this.value + ")";
    }

    public int hashCode() {
        return Double.hashCode(this.value);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Cost)) {
            return false;
        }
        Cost cost = (Cost)other;
        return Double.compare(this.value, cost.value) == 0;
    }

    public /* synthetic */ Cost(double value, DefaultConstructorMarker $constructor_marker) {
        this(value);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\t\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/Cost$Companion;", "", "<init>", "()V", "", "value", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "of", "(D)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "ZERO", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "getZERO", "()Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "cobalt"})
    @SourceDebugExtension(value={"SMAP\nCost.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cost.kt\norg/cobalt/api/pathfinder/pathing/processing/Cost$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,16:1\n1#2:17\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Cost getZERO() {
            return ZERO;
        }

        @NotNull
        public final Cost of(double value) {
            if (!(!Double.isNaN(value) && value >= 0.0)) {
                boolean bl = false;
                String string = "Cost must be a positive number or 0";
                throw new IllegalArgumentException(string.toString());
            }
            return new Cost(value, null);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

