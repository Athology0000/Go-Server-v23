/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pathfinding;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u001a\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u001b\u0010\t\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/pathfinding/KillWaypoint;", "", "", "x", "y", "z", "<init>", "(DDD)V", "component1", "()D", "component2", "component3", "copy", "(DDD)Lorg/cobalt/internal/pathfinding/KillWaypoint;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getX", "getY", "getZ", "cobalt"})
final class KillWaypoint {
    private final double x;
    private final double y;
    private final double z;

    public KillWaypoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }

    public final double component1() {
        return this.x;
    }

    public final double component2() {
        return this.y;
    }

    public final double component3() {
        return this.z;
    }

    @NotNull
    public final KillWaypoint copy(double x, double y, double z) {
        return new KillWaypoint(x, y, z);
    }

    public static /* synthetic */ KillWaypoint copy$default(KillWaypoint killWaypoint, double d, double d2, double d3, int n, Object object) {
        if ((n & 1) != 0) {
            d = killWaypoint.x;
        }
        if ((n & 2) != 0) {
            d2 = killWaypoint.y;
        }
        if ((n & 4) != 0) {
            d3 = killWaypoint.z;
        }
        return killWaypoint.copy(d, d2, d3);
    }

    @NotNull
    public String toString() {
        return "KillWaypoint(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
    }

    public int hashCode() {
        int result = Double.hashCode(this.x);
        result = result * 31 + Double.hashCode(this.y);
        result = result * 31 + Double.hashCode(this.z);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KillWaypoint)) {
            return false;
        }
        KillWaypoint killWaypoint = (KillWaypoint)other;
        if (Double.compare(this.x, killWaypoint.x) != 0) {
            return false;
        }
        if (Double.compare(this.y, killWaypoint.y) != 0) {
            return false;
        }
        return Double.compare(this.z, killWaypoint.z) == 0;
    }
}

