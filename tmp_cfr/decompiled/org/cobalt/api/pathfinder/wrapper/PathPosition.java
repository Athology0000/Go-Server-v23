/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_3532
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.wrapper;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_3532;
import org.cobalt.api.pathfinder.wrapper.PathVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0013\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\t\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\u000b\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0015\u0010\f\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0015\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0015\u0010\u000f\u001a\u00020\u00002\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\rJ%\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0015\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0010\u0010\u0014J%\u0010\u0015\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0011J\u0015\u0010\u0015\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0015\u0010\u0014J\r\u0010\u0016\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0016\u0010\u0017J\r\u0010\u0018\u001a\u00020\u0000\u00a2\u0006\u0004\b\u0018\u0010\u0019J\r\u0010\u001a\u001a\u00020\u0000\u00a2\u0006\u0004\b\u001a\u0010\u0019J\u0015\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\u0000\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u001b\u0010\u001f\u001a\u00020\u001e2\b\u0010\b\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u001f\u0010 J\u0011\u0010\"\u001a\u00020!H\u0096\u0080\u0004\u00a2\u0006\u0004\b\"\u0010#J\u0010\u0010$\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b$\u0010%J\u0010\u0010&\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b&\u0010%J\u0010\u0010'\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b'\u0010%J.\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b(\u0010\u0011J\u0011\u0010*\u001a\u00020)H\u00d6\u0081\u0004\u00a2\u0006\u0004\b*\u0010+R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010,\u001a\u0004\b-\u0010%R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010,\u001a\u0004\b.\u0010%R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010,\u001a\u0004\b/\u0010%R\u0011\u00101\u001a\u00020!8F\u00a2\u0006\u0006\u001a\u0004\b0\u0010#R\u0011\u00103\u001a\u00020!8F\u00a2\u0006\u0006\u001a\u0004\b2\u0010#R\u0011\u00105\u001a\u00020!8F\u00a2\u0006\u0006\u001a\u0004\b4\u0010#R\u0011\u00107\u001a\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b6\u0010%R\u0011\u00109\u001a\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b8\u0010%R\u0011\u0010;\u001a\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b:\u0010%\u00a8\u0006<"}, d2={"Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "", "", "x", "y", "z", "<init>", "(DDD)V", "other", "distanceSquared", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)D", "distance", "setX", "(D)Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "setY", "setZ", "add", "(DDD)Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "vector", "(Lorg/cobalt/api/pathfinder/wrapper/PathVector;)Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "subtract", "toVector", "()Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "floor", "()Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "mid", "end", "midPoint", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "component1", "()D", "component2", "component3", "copy", "", "toString", "()Ljava/lang/String;", "D", "getX", "getY", "getZ", "getFlooredX", "flooredX", "getFlooredY", "flooredY", "getFlooredZ", "flooredZ", "getCenteredX", "centeredX", "getCenteredY", "centeredY", "getCenteredZ", "centeredZ", "cobalt"})
public final class PathPosition {
    private final double x;
    private final double y;
    private final double z;

    public PathPosition(double x, double y, double z) {
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

    public final int getFlooredX() {
        return class_3532.method_15357((double)this.x);
    }

    public final int getFlooredY() {
        return class_3532.method_15357((double)this.y);
    }

    public final int getFlooredZ() {
        return class_3532.method_15357((double)this.z);
    }

    public final double getCenteredX() {
        return (double)this.getFlooredX() + 0.5;
    }

    public final double getCenteredY() {
        return (double)this.getFlooredY() + 0.5;
    }

    public final double getCenteredZ() {
        return (double)this.getFlooredZ() + 0.5;
    }

    public final double distanceSquared(@NotNull PathPosition other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return class_3532.method_33723((double)(this.x - other.x)) + class_3532.method_33723((double)(this.y - other.y)) + class_3532.method_33723((double)(this.z - other.z));
    }

    public final double distance(@NotNull PathPosition other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return Math.sqrt(this.distanceSquared(other));
    }

    @NotNull
    public final PathPosition setX(double x) {
        return PathPosition.copy$default(this, x, 0.0, 0.0, 6, null);
    }

    @NotNull
    public final PathPosition setY(double y) {
        return PathPosition.copy$default(this, 0.0, y, 0.0, 5, null);
    }

    @NotNull
    public final PathPosition setZ(double z) {
        return PathPosition.copy$default(this, 0.0, 0.0, z, 3, null);
    }

    @NotNull
    public final PathPosition add(double x, double y, double z) {
        return new PathPosition(this.x + x, this.y + y, this.z + z);
    }

    @NotNull
    public final PathPosition add(@NotNull PathVector vector) {
        Intrinsics.checkNotNullParameter((Object)vector, (String)"vector");
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    @NotNull
    public final PathPosition subtract(double x, double y, double z) {
        return new PathPosition(this.x - x, this.y - y, this.z - z);
    }

    @NotNull
    public final PathPosition subtract(@NotNull PathVector vector) {
        Intrinsics.checkNotNullParameter((Object)vector, (String)"vector");
        return this.subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    @NotNull
    public final PathVector toVector() {
        return new PathVector(this.x, this.y, this.z);
    }

    @NotNull
    public final PathPosition floor() {
        return new PathPosition(this.getFlooredX(), this.getFlooredY(), this.getFlooredZ());
    }

    @NotNull
    public final PathPosition mid() {
        return new PathPosition((double)this.getFlooredX() + 0.5, (double)this.getFlooredY() + 0.5, (double)this.getFlooredZ() + 0.5);
    }

    @NotNull
    public final PathPosition midPoint(@NotNull PathPosition end) {
        Intrinsics.checkNotNullParameter((Object)end, (String)"end");
        return new PathPosition((this.x + end.x) / (double)2, (this.y + end.y) / (double)2, (this.z + end.z) / (double)2);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof PathPosition && this.getFlooredX() == ((PathPosition)other).getFlooredX() && this.getFlooredY() == ((PathPosition)other).getFlooredY() && this.getFlooredZ() == ((PathPosition)other).getFlooredZ();
    }

    public int hashCode() {
        int result = this.getFlooredX();
        result = 31 * result + this.getFlooredY();
        result = 31 * result + this.getFlooredZ();
        return result;
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
    public final PathPosition copy(double x, double y, double z) {
        return new PathPosition(x, y, z);
    }

    public static /* synthetic */ PathPosition copy$default(PathPosition pathPosition, double d, double d2, double d3, int n, Object object) {
        if ((n & 1) != 0) {
            d = pathPosition.x;
        }
        if ((n & 2) != 0) {
            d2 = pathPosition.y;
        }
        if ((n & 4) != 0) {
            d3 = pathPosition.z;
        }
        return pathPosition.copy(d, d2, d3);
    }

    @NotNull
    public String toString() {
        return "PathPosition(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
    }
}

