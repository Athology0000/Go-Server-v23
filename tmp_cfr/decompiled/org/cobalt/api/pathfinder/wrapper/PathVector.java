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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\t\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\r\u0010\nJ\u0015\u0010\u000e\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0015\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0015\u0010\u0011\u001a\u00020\u00002\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0011\u0010\u000fJ\u0015\u0010\u0012\u001a\u00020\u00002\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0015\u0010\u0015\u001a\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0015\u0010\u000fJ\r\u0010\u0016\u001a\u00020\u0000\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0015\u0010\u0018\u001a\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0018\u0010\u000fJ\u0015\u0010\u0019\u001a\u00020\u00002\u0006\u0010\b\u001a\u00020\u0000\u00a2\u0006\u0004\b\u0019\u0010\u0013J\u0010\u0010\u001a\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\fJ\u0010\u0010\u001b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001b\u0010\fJ\u0010\u0010\u001c\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\fJ.\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001b\u0010 \u001a\u00020\u001f2\b\u0010\b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b \u0010!J\u0011\u0010#\u001a\u00020\"H\u00d6\u0081\u0004\u00a2\u0006\u0004\b#\u0010$J\u0011\u0010&\u001a\u00020%H\u00d6\u0081\u0004\u00a2\u0006\u0004\b&\u0010'R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010(\u001a\u0004\b)\u0010\fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010(\u001a\u0004\b*\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010(\u001a\u0004\b+\u0010\f\u00a8\u0006,"}, d2={"Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "", "", "x", "y", "z", "<init>", "(DDD)V", "other", "dot", "(Lorg/cobalt/api/pathfinder/wrapper/PathVector;)D", "length", "()D", "distance", "setX", "(D)Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "setY", "setZ", "subtract", "(Lorg/cobalt/api/pathfinder/wrapper/PathVector;)Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "value", "multiply", "normalize", "()Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "divide", "add", "component1", "component2", "component3", "copy", "(DDD)Lorg/cobalt/api/pathfinder/wrapper/PathVector;", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getX", "getY", "getZ", "cobalt"})
public final class PathVector {
    private final double x;
    private final double y;
    private final double z;

    public PathVector(double x, double y, double z) {
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

    public final double dot(@NotNull PathVector other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public final double length() {
        return Math.sqrt(class_3532.method_33723((double)this.x) + class_3532.method_33723((double)this.y) + class_3532.method_33723((double)this.z));
    }

    public final double distance(@NotNull PathVector other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return Math.sqrt(class_3532.method_33723((double)(this.x - other.x)) + class_3532.method_33723((double)(this.y - other.y)) + class_3532.method_33723((double)(this.z - other.z)));
    }

    @NotNull
    public final PathVector setX(double x) {
        return PathVector.copy$default(this, x, 0.0, 0.0, 6, null);
    }

    @NotNull
    public final PathVector setY(double y) {
        return PathVector.copy$default(this, 0.0, y, 0.0, 5, null);
    }

    @NotNull
    public final PathVector setZ(double z) {
        return PathVector.copy$default(this, 0.0, 0.0, z, 3, null);
    }

    @NotNull
    public final PathVector subtract(@NotNull PathVector other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return new PathVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    @NotNull
    public final PathVector multiply(double value) {
        return new PathVector(this.x * value, this.y * value, this.z * value);
    }

    @NotNull
    public final PathVector normalize() {
        double magnitude = this.length();
        return new PathVector(this.x / magnitude, this.y / magnitude, this.z / magnitude);
    }

    @NotNull
    public final PathVector divide(double value) {
        return new PathVector(this.x / value, this.y / value, this.z / value);
    }

    @NotNull
    public final PathVector add(@NotNull PathVector other) {
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return new PathVector(this.x + other.x, this.y + other.y, this.z + other.z);
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
    public final PathVector copy(double x, double y, double z) {
        return new PathVector(x, y, z);
    }

    public static /* synthetic */ PathVector copy$default(PathVector pathVector, double d, double d2, double d3, int n, Object object) {
        if ((n & 1) != 0) {
            d = pathVector.x;
        }
        if ((n & 2) != 0) {
            d2 = pathVector.y;
        }
        if ((n & 4) != 0) {
            d3 = pathVector.z;
        }
        return pathVector.copy(d, d2, d3);
    }

    @NotNull
    public String toString() {
        return "PathVector(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
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
        if (!(other instanceof PathVector)) {
            return false;
        }
        PathVector pathVector = (PathVector)other;
        if (Double.compare(this.x, pathVector.x) != 0) {
            return false;
        }
        if (Double.compare(this.y, pathVector.y) != 0) {
            return false;
        }
        return Double.compare(this.z, pathVector.z) == 0;
    }
}

