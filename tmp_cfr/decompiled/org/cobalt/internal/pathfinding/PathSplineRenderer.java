/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_243
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_243;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001bB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001b\u0010\b\u001a\u00020\u00072\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\b\u0010\tJ7\u0010\u0010\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001f\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0005H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00168\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0014\u0010\u0019\u001a\u00020\u000e8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001a\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/pathfinding/PathSplineRenderer;", "", "<init>", "()V", "", "Lnet/minecraft/class_243;", "nodes", "Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "buildSpline", "(Ljava/util/List;)Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "p0", "p1", "p2", "p3", "", "t", "catmullRom", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lnet/minecraft/class_243;D)Lnet/minecraft/class_243;", "a", "b", "xzDist", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;)D", "", "STEPS", "I", "AOTV_XZ_THRESHOLD", "D", "SplineResult", "cobalt"})
public final class PathSplineRenderer {
    @NotNull
    public static final PathSplineRenderer INSTANCE = new PathSplineRenderer();
    private static final int STEPS = 10;
    private static final double AOTV_XZ_THRESHOLD = 2.0;

    private PathSplineRenderer() {
    }

    @NotNull
    public final SplineResult buildSpline(@NotNull List<? extends class_243> nodes) {
        Intrinsics.checkNotNullParameter(nodes, (String)"nodes");
        if (nodes.size() < 2) {
            return new SplineResult(CollectionsKt.toList((Iterable)nodes), new boolean[nodes.size()]);
        }
        ArrayList<Object> points = new ArrayList<Object>((nodes.size() - 1) * 10 + 1);
        ArrayList<Boolean> aotv = new ArrayList<Boolean>((nodes.size() - 1) * 10 + 1);
        int n = nodes.size() - 1;
        for (int i = 0; i < n; ++i) {
            class_243 p0 = nodes.get(RangesKt.coerceAtLeast((int)(i - 1), (int)0));
            class_243 p1 = nodes.get(i);
            class_243 p2 = nodes.get(i + 1);
            class_243 p3 = nodes.get(RangesKt.coerceAtMost((int)(i + 2), (int)(nodes.size() - 1)));
            boolean segIsAotv = this.xzDist(p1, p2) > 2.0 && p1.field_1351 == p2.field_1351;
            for (int step = 0; step < 10; ++step) {
                double t = (double)step / (double)10;
                points.add(this.catmullRom(p0, p1, p2, p3, t));
                aotv.add(segIsAotv);
            }
        }
        points.add(CollectionsKt.last(nodes));
        aotv.add(false);
        return new SplineResult((List<? extends class_243>)points, CollectionsKt.toBooleanArray((Collection)aotv));
    }

    private final class_243 catmullRom(class_243 p0, class_243 p1, class_243 p2, class_243 p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return new class_243(0.5 * ((double)2 * p1.field_1352 + (-p0.field_1352 + p2.field_1352) * t + ((double)2 * p0.field_1352 - (double)5 * p1.field_1352 + (double)4 * p2.field_1352 - p3.field_1352) * t2 + (-p0.field_1352 + (double)3 * p1.field_1352 - (double)3 * p2.field_1352 + p3.field_1352) * t3), 0.5 * ((double)2 * p1.field_1351 + (-p0.field_1351 + p2.field_1351) * t + ((double)2 * p0.field_1351 - (double)5 * p1.field_1351 + (double)4 * p2.field_1351 - p3.field_1351) * t2 + (-p0.field_1351 + (double)3 * p1.field_1351 - (double)3 * p2.field_1351 + p3.field_1351) * t3), 0.5 * ((double)2 * p1.field_1350 + (-p0.field_1350 + p2.field_1350) * t + ((double)2 * p0.field_1350 - (double)5 * p1.field_1350 + (double)4 * p2.field_1350 - p3.field_1350) * t2 + (-p0.field_1350 + (double)3 * p1.field_1350 - (double)3 * p2.field_1350 + p3.field_1350) * t3));
    }

    private final double xzDist(class_243 a, class_243 b) {
        double dx = a.field_1352 - b.field_1352;
        double dz = a.field_1350 - b.field_1350;
        return Math.sqrt(dx * dx + dz * dz);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0018\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ*\u0010\r\u001a\u00020\u00002\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001b\u0010\u0011\u001a\u00020\u00102\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0011\u0010\u0017\u001a\u00020\u0016H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0018R\u001d\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0019\u001a\u0004\b\u001a\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u0006\u0010\f\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "", "", "Lnet/minecraft/class_243;", "points", "", "isAotv", "<init>", "(Ljava/util/List;[Z)V", "component1", "()Ljava/util/List;", "component2", "()[Z", "copy", "(Ljava/util/List;[Z)Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Ljava/util/List;", "getPoints", "[Z", "cobalt"})
    public static final class SplineResult {
        @NotNull
        private final List<class_243> points;
        @NotNull
        private final boolean[] isAotv;

        public SplineResult(@NotNull List<? extends class_243> points, @NotNull boolean[] isAotv) {
            Intrinsics.checkNotNullParameter(points, (String)"points");
            Intrinsics.checkNotNullParameter((Object)isAotv, (String)"isAotv");
            this.points = points;
            this.isAotv = isAotv;
        }

        @NotNull
        public final List<class_243> getPoints() {
            return this.points;
        }

        @NotNull
        public final boolean[] isAotv() {
            return this.isAotv;
        }

        @NotNull
        public final List<class_243> component1() {
            return this.points;
        }

        @NotNull
        public final boolean[] component2() {
            return this.isAotv;
        }

        @NotNull
        public final SplineResult copy(@NotNull List<? extends class_243> points, @NotNull boolean[] isAotv) {
            Intrinsics.checkNotNullParameter(points, (String)"points");
            Intrinsics.checkNotNullParameter((Object)isAotv, (String)"isAotv");
            return new SplineResult(points, isAotv);
        }

        public static /* synthetic */ SplineResult copy$default(SplineResult splineResult, List list, boolean[] blArray, int n, Object object) {
            if ((n & 1) != 0) {
                list = splineResult.points;
            }
            if ((n & 2) != 0) {
                blArray = splineResult.isAotv;
            }
            return splineResult.copy(list, blArray);
        }

        @NotNull
        public String toString() {
            return "SplineResult(points=" + this.points + ", isAotv=" + Arrays.toString(this.isAotv) + ")";
        }

        public int hashCode() {
            int result = ((Object)this.points).hashCode();
            result = result * 31 + Arrays.hashCode(this.isAotv);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SplineResult)) {
                return false;
            }
            SplineResult splineResult = (SplineResult)other;
            if (!Intrinsics.areEqual(this.points, splineResult.points)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.isAotv, (Object)splineResult.isAotv);
        }
    }
}

