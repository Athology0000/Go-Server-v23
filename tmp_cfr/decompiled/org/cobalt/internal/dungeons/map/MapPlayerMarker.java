/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons.map;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u001a\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u001b\u0010\t\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/dungeons/map/MapPlayerMarker;", "", "", "componentX", "componentZ", "rotationRadians", "<init>", "(DDD)V", "component1", "()D", "component2", "component3", "copy", "(DDD)Lorg/cobalt/internal/dungeons/map/MapPlayerMarker;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getComponentX", "getComponentZ", "getRotationRadians", "cobalt"})
public final class MapPlayerMarker {
    private final double componentX;
    private final double componentZ;
    private final double rotationRadians;

    public MapPlayerMarker(double componentX, double componentZ, double rotationRadians) {
        this.componentX = componentX;
        this.componentZ = componentZ;
        this.rotationRadians = rotationRadians;
    }

    public final double getComponentX() {
        return this.componentX;
    }

    public final double getComponentZ() {
        return this.componentZ;
    }

    public final double getRotationRadians() {
        return this.rotationRadians;
    }

    public final double component1() {
        return this.componentX;
    }

    public final double component2() {
        return this.componentZ;
    }

    public final double component3() {
        return this.rotationRadians;
    }

    @NotNull
    public final MapPlayerMarker copy(double componentX, double componentZ, double rotationRadians) {
        return new MapPlayerMarker(componentX, componentZ, rotationRadians);
    }

    public static /* synthetic */ MapPlayerMarker copy$default(MapPlayerMarker mapPlayerMarker, double d, double d2, double d3, int n, Object object) {
        if ((n & 1) != 0) {
            d = mapPlayerMarker.componentX;
        }
        if ((n & 2) != 0) {
            d2 = mapPlayerMarker.componentZ;
        }
        if ((n & 4) != 0) {
            d3 = mapPlayerMarker.rotationRadians;
        }
        return mapPlayerMarker.copy(d, d2, d3);
    }

    @NotNull
    public String toString() {
        return "MapPlayerMarker(componentX=" + this.componentX + ", componentZ=" + this.componentZ + ", rotationRadians=" + this.rotationRadians + ")";
    }

    public int hashCode() {
        int result = Double.hashCode(this.componentX);
        result = result * 31 + Double.hashCode(this.componentZ);
        result = result * 31 + Double.hashCode(this.rotationRadians);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MapPlayerMarker)) {
            return false;
        }
        MapPlayerMarker mapPlayerMarker = (MapPlayerMarker)other;
        if (Double.compare(this.componentX, mapPlayerMarker.componentX) != 0) {
            return false;
        }
        if (Double.compare(this.componentZ, mapPlayerMarker.componentZ) != 0) {
            return false;
        }
        return Double.compare(this.rotationRadians, mapPlayerMarker.rotationRadians) == 0;
    }
}

