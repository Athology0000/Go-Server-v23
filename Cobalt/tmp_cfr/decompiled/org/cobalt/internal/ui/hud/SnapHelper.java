/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$FloatRef
 *  kotlin.jvm.internal.Ref$ObjectRef
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.hud;

import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001:\u0002!\"B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ)\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\f2\u0006\u0010\n\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJW\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00022\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001aR\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u001aR0\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00152\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00158\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001f\u0010 \u00a8\u0006#"}, d2={"Lorg/cobalt/internal/ui/hud/SnapHelper;", "", "", "gridSize", "snapThreshold", "<init>", "(FF)V", "", "clearGuides", "()V", "x", "y", "Lkotlin/Pair;", "snapToGrid", "(FF)Lkotlin/Pair;", "moduleX", "moduleY", "moduleW", "moduleH", "screenWidth", "screenHeight", "", "Lorg/cobalt/internal/ui/hud/SnapHelper$ModuleBounds;", "otherModuleBounds", "findAlignmentGuides", "(FFFFFFLjava/util/List;)Lkotlin/Pair;", "F", "Lorg/cobalt/internal/ui/hud/SnapHelper$GuideLine;", "value", "activeGuides", "Ljava/util/List;", "getActiveGuides", "()Ljava/util/List;", "GuideLine", "ModuleBounds", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSnapHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SnapHelper.kt\norg/cobalt/internal/ui/hud/SnapHelper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,96:1\n1915#2,2:97\n1915#2,2:99\n1915#2,2:101\n*S KotlinDebug\n*F\n+ 1 SnapHelper.kt\norg/cobalt/internal/ui/hud/SnapHelper\n*L\n44#1:97,2\n78#1:99,2\n84#1:101,2\n*E\n"})
public final class SnapHelper {
    private final float gridSize;
    private final float snapThreshold;
    @NotNull
    private List<GuideLine> activeGuides;

    public SnapHelper(float gridSize, float snapThreshold) {
        this.gridSize = gridSize;
        this.snapThreshold = snapThreshold;
        this.activeGuides = CollectionsKt.emptyList();
    }

    public /* synthetic */ SnapHelper(float f, float f2, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            f = 10.0f;
        }
        if ((n & 2) != 0) {
            f2 = 5.0f;
        }
        this(f, f2);
    }

    @NotNull
    public final List<GuideLine> getActiveGuides() {
        return this.activeGuides;
    }

    public final void clearGuides() {
        this.activeGuides = CollectionsKt.emptyList();
    }

    @NotNull
    public final Pair<Float, Float> snapToGrid(float x, float y) {
        float snappedX = (float)Math.rint(x / this.gridSize) * this.gridSize;
        float snappedY = (float)Math.rint(y / this.gridSize) * this.gridSize;
        return TuplesKt.to((Object)Float.valueOf(snappedX), (Object)Float.valueOf(snappedY));
    }

    @NotNull
    public final Pair<Float, Float> findAlignmentGuides(float moduleX, float moduleY, float moduleW, float moduleH, float screenWidth, float screenHeight, @NotNull List<ModuleBounds> otherModuleBounds) {
        float target;
        Intrinsics.checkNotNullParameter(otherModuleBounds, (String)"otherModuleBounds");
        float left = moduleX;
        float right = moduleX + moduleW;
        float centerX = moduleX + moduleW / 2.0f;
        float top = moduleY;
        float bottom = moduleY + moduleH;
        float centerY = moduleY + moduleH / 2.0f;
        Object[] objectArray = new Float[]{Float.valueOf(0.0f), Float.valueOf(screenWidth / 2.0f), Float.valueOf(screenWidth)};
        List xTargets = CollectionsKt.mutableListOf((Object[])objectArray);
        Object[] objectArray2 = new Float[]{Float.valueOf(0.0f), Float.valueOf(screenHeight / 2.0f), Float.valueOf(screenHeight)};
        List yTargets = CollectionsKt.mutableListOf((Object[])objectArray2);
        Iterable $this$forEach$iv = otherModuleBounds;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            ModuleBounds bounds = (ModuleBounds)element$iv;
            boolean bl = false;
            xTargets.add(Float.valueOf(bounds.getX()));
            xTargets.add(Float.valueOf(bounds.getX() + bounds.getW()));
            xTargets.add(Float.valueOf(bounds.getX() + bounds.getW() / 2.0f));
            yTargets.add(Float.valueOf(bounds.getY()));
            yTargets.add(Float.valueOf(bounds.getY() + bounds.getH()));
            yTargets.add(Float.valueOf(bounds.getY() + bounds.getH() / 2.0f));
        }
        Ref.FloatRef snappedX = new Ref.FloatRef();
        snappedX.element = moduleX;
        Ref.FloatRef snappedY = new Ref.FloatRef();
        snappedY.element = moduleY;
        Ref.FloatRef bestXDiff = new Ref.FloatRef();
        bestXDiff.element = this.snapThreshold + 1.0f;
        Ref.FloatRef bestYDiff = new Ref.FloatRef();
        bestYDiff.element = this.snapThreshold + 1.0f;
        Ref.ObjectRef xGuide = new Ref.ObjectRef();
        Ref.ObjectRef yGuide = new Ref.ObjectRef();
        Iterable $this$forEach$iv2 = xTargets;
        boolean $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv2) {
            target = ((Number)element$iv).floatValue();
            boolean bl = false;
            SnapHelper.findAlignmentGuides$checkX(this, bestXDiff, snappedX, (Ref.ObjectRef<GuideLine>)xGuide, target, left, target);
            SnapHelper.findAlignmentGuides$checkX(this, bestXDiff, snappedX, (Ref.ObjectRef<GuideLine>)xGuide, target, centerX, target - moduleW / 2.0f);
            SnapHelper.findAlignmentGuides$checkX(this, bestXDiff, snappedX, (Ref.ObjectRef<GuideLine>)xGuide, target, right, target - moduleW);
        }
        $this$forEach$iv2 = yTargets;
        $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv2) {
            target = ((Number)element$iv).floatValue();
            boolean bl = false;
            SnapHelper.findAlignmentGuides$checkY(this, bestYDiff, snappedY, (Ref.ObjectRef<GuideLine>)yGuide, target, top, target);
            SnapHelper.findAlignmentGuides$checkY(this, bestYDiff, snappedY, (Ref.ObjectRef<GuideLine>)yGuide, target, centerY, target - moduleH / 2.0f);
            SnapHelper.findAlignmentGuides$checkY(this, bestYDiff, snappedY, (Ref.ObjectRef<GuideLine>)yGuide, target, bottom, target - moduleH);
        }
        Object[] objectArray3 = new GuideLine[]{xGuide.element, yGuide.element};
        this.activeGuides = CollectionsKt.listOfNotNull((Object[])objectArray3);
        return TuplesKt.to((Object)Float.valueOf(snappedX.element), (Object)Float.valueOf(snappedY.element));
    }

    private static final void findAlignmentGuides$checkX(SnapHelper this$0, Ref.FloatRef bestXDiff, Ref.FloatRef snappedX, Ref.ObjectRef<GuideLine> xGuide, float target, float edge, float newX) {
        float diff = Math.abs(edge - target);
        if (diff <= this$0.snapThreshold && diff < bestXDiff.element) {
            bestXDiff.element = diff;
            snappedX.element = newX;
            xGuide.element = new GuideLine(true, target);
        }
    }

    private static final void findAlignmentGuides$checkY(SnapHelper this$0, Ref.FloatRef bestYDiff, Ref.FloatRef snappedY, Ref.ObjectRef<GuideLine> yGuide, float target, float edge, float newY) {
        float diff = Math.abs(edge - target);
        if (diff <= this$0.snapThreshold && diff < bestYDiff.element) {
            bestYDiff.element = diff;
            snappedY.element = newY;
            yGuide.element = new GuideLine(false, target);
        }
    }

    public SnapHelper() {
        this(0.0f, 0.0f, 3, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u000f\u001a\u00020\u00022\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0003\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/ui/hud/SnapHelper$GuideLine;", "", "", "isVertical", "", "position", "<init>", "(ZF)V", "component1", "()Z", "component2", "()F", "copy", "(ZF)Lorg/cobalt/internal/ui/hud/SnapHelper$GuideLine;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Z", "F", "getPosition", "cobalt"})
    public static final class GuideLine {
        private final boolean isVertical;
        private final float position;

        public GuideLine(boolean isVertical, float position) {
            this.isVertical = isVertical;
            this.position = position;
        }

        public final boolean isVertical() {
            return this.isVertical;
        }

        public final float getPosition() {
            return this.position;
        }

        public final boolean component1() {
            return this.isVertical;
        }

        public final float component2() {
            return this.position;
        }

        @NotNull
        public final GuideLine copy(boolean isVertical, float position) {
            return new GuideLine(isVertical, position);
        }

        public static /* synthetic */ GuideLine copy$default(GuideLine guideLine, boolean bl, float f, int n, Object object) {
            if ((n & 1) != 0) {
                bl = guideLine.isVertical;
            }
            if ((n & 2) != 0) {
                f = guideLine.position;
            }
            return guideLine.copy(bl, f);
        }

        @NotNull
        public String toString() {
            return "GuideLine(isVertical=" + this.isVertical + ", position=" + this.position + ")";
        }

        public int hashCode() {
            int result = Boolean.hashCode(this.isVertical);
            result = result * 31 + Float.hashCode(this.position);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GuideLine)) {
                return false;
            }
            GuideLine guideLine = (GuideLine)other;
            if (this.isVertical != guideLine.isVertical) {
                return false;
            }
            return Float.compare(this.position, guideLine.position) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001d\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001a\u001a\u0004\b\u001e\u0010\n\u00a8\u0006\u001f"}, d2={"Lorg/cobalt/internal/ui/hud/SnapHelper$ModuleBounds;", "", "", "x", "y", "w", "h", "<init>", "(FFFF)V", "component1", "()F", "component2", "component3", "component4", "copy", "(FFFF)Lorg/cobalt/internal/ui/hud/SnapHelper$ModuleBounds;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getX", "getY", "getW", "getH", "cobalt"})
    public static final class ModuleBounds {
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public ModuleBounds(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getW() {
            return this.w;
        }

        public final float getH() {
            return this.h;
        }

        public final float component1() {
            return this.x;
        }

        public final float component2() {
            return this.y;
        }

        public final float component3() {
            return this.w;
        }

        public final float component4() {
            return this.h;
        }

        @NotNull
        public final ModuleBounds copy(float x, float y, float w, float h) {
            return new ModuleBounds(x, y, w, h);
        }

        public static /* synthetic */ ModuleBounds copy$default(ModuleBounds moduleBounds, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                f = moduleBounds.x;
            }
            if ((n & 2) != 0) {
                f2 = moduleBounds.y;
            }
            if ((n & 4) != 0) {
                f3 = moduleBounds.w;
            }
            if ((n & 8) != 0) {
                f4 = moduleBounds.h;
            }
            return moduleBounds.copy(f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "ModuleBounds(x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ModuleBounds)) {
                return false;
            }
            ModuleBounds moduleBounds = (ModuleBounds)other;
            if (Float.compare(this.x, moduleBounds.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, moduleBounds.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, moduleBounds.w) != 0) {
                return false;
            }
            return Float.compare(this.h, moduleBounds.h) == 0;
        }
    }
}

