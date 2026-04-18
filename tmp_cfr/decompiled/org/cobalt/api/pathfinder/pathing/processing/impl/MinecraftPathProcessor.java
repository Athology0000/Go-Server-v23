/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.processing.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.pathfinder.pathing.context.EnvironmentContext;
import org.cobalt.api.pathfinder.pathing.processing.Cost;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.provider.NavigationPoint;
import org.cobalt.api.pathfinder.provider.NavigationPointProvider;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001f\u0010\u0011\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u001f\u0010\u0014\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/MinecraftPathProcessor;", "Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "context", "", "isValid", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Z", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "calculateCostContribution", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338;", "blockPos", "", "calculateClearancePenalty", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)D", "pos", "hasBlockingCollision", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Companion", "cobalt"})
public final class MinecraftPathProcessor
implements NodeProcessor {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final class_310 mc;
    private static final double DEFAULT_MOB_JUMP_HEIGHT = 1.125;
    private static final double LOW_CEILING_PENALTY = 0.08;
    private static final double ADJACENT_WALL_PENALTY = 0.14;
    private static final double ADJACENT_CORNER_PENALTY = 0.1;
    private static final double SECOND_RING_WALL_PENALTY = 0.045;
    private static final double SECOND_RING_CORNER_PENALTY = 0.028;

    public MinecraftPathProcessor() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
    }

    @Override
    public boolean isValid(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        NavigationPointProvider provider = context.getNavigationPointProvider();
        PathPosition pos = context.getCurrentPathPosition();
        PathPosition prev = context.getPreviousPathPosition();
        EnvironmentContext env = context.getEnvironmentContext();
        NavigationPoint currentPoint = provider.getNavigationPoint(pos, env);
        if (!currentPoint.isTraversable()) {
            return false;
        }
        if (prev == null) {
            return true;
        }
        NavigationPoint prevPoint = provider.getNavigationPoint(prev, env);
        double dy = pos.getY() - prev.getY();
        int dx = pos.getFlooredX() - prev.getFlooredX();
        int dz = pos.getFlooredZ() - prev.getFlooredZ();
        if (dy > 1.125) {
            return false;
        }
        if (Math.abs(dx) == 1 && Math.abs(dz) == 1) {
            PathPosition corner1Pos = prev.add(dx, 0.0, 0.0);
            PathPosition corner2Pos = prev.add(0.0, 0.0, dz);
            NavigationPoint c1Point = provider.getNavigationPoint(corner1Pos, env);
            NavigationPoint c2Point = provider.getNavigationPoint(corner2Pos, env);
            if (!c1Point.isTraversable() || !c2Point.isTraversable()) {
                return false;
            }
        }
        return dy < -0.5 ? true : (dy > 0.5 ? prevPoint.hasFloor() || currentPoint.isClimbable() : currentPoint.hasFloor() || prevPoint.hasFloor() || currentPoint.isClimbable() || prevPoint.isClimbable());
    }

    @Override
    @NotNull
    public Cost calculateCostContribution(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        class_638 class_6382 = this.mc.field_1687;
        if (class_6382 == null) {
            return Cost.Companion.getZERO();
        }
        class_638 level2 = class_6382;
        PathPosition currentPos = context.getCurrentPathPosition();
        PathPosition pathPosition = context.getPreviousPathPosition();
        if (pathPosition == null) {
            return Cost.Companion.getZERO();
        }
        PathPosition prevPos = pathPosition;
        NavigationPointProvider provider = context.getNavigationPointProvider();
        EnvironmentContext env = context.getEnvironmentContext();
        NavigationPoint currentPoint = provider.getNavigationPoint(currentPos, env);
        NavigationPoint prevPoint = provider.getNavigationPoint(prevPos, env);
        double dy = currentPoint.getFloorLevel() - prevPoint.getFloorLevel();
        double additionalCost = 0.0;
        if (dy > 0.1) {
            additionalCost += 0.5 * dy;
        } else if (dy < -0.1) {
            additionalCost += 0.1 * Math.abs(dy);
        }
        class_2338 blockPos = new class_2338(currentPos.getFlooredX(), currentPos.getFlooredY(), currentPos.getFlooredZ());
        additionalCost += this.calculateClearancePenalty((class_1937)level2, blockPos);
        PathPosition gpPos = context.getGrandparentPathPosition();
        if (gpPos != null) {
            double normalizedDot;
            double v1x = prevPos.getX() - gpPos.getX();
            double v1z = prevPos.getZ() - gpPos.getZ();
            double v2x = currentPos.getX() - prevPos.getX();
            double v2z = currentPos.getZ() - prevPos.getZ();
            double dot = v1x * v2x + v1z * v2z;
            double mag1 = Math.sqrt(v1x * v1x + v1z * v1z);
            double mag2 = Math.sqrt(v2x * v2x + v2z * v2z);
            if (mag1 > 0.1 && mag2 > 0.1 && (normalizedDot = dot / (mag1 * mag2)) < 0.99) {
                additionalCost += 0.05;
            }
        }
        return Cost.Companion.of(additionalCost);
    }

    private final double calculateClearancePenalty(class_1937 level2, class_2338 blockPos) {
        double penalty = 0.0;
        for (int headOffset = 0; headOffset < 2; ++headOffset) {
            for (int dx = -2; dx < 3; ++dx) {
                for (int dz = -2; dz < 3; ++dz) {
                    class_2338 samplePos;
                    if (dx == 0 && dz == 0) continue;
                    Intrinsics.checkNotNullExpressionValue((Object)blockPos.method_10069(dx, headOffset, dz), (String)"offset(...)");
                    if (!this.hasBlockingCollision(level2, samplePos)) continue;
                    int absX = Math.abs(dx);
                    int absZ = Math.abs(dz);
                    int chebyshev = Math.max(absX, absZ);
                    boolean axial = absX == 0 || absZ == 0;
                    double basePenalty = chebyshev <= 1 && axial ? 0.14 : (chebyshev <= 1 ? 0.1 : (axial ? 0.045 : 0.028));
                    penalty += headOffset == 0 ? basePenalty : basePenalty * 0.85;
                }
            }
        }
        for (int i = 2; i < 4; ++i) {
            class_2338 class_23382 = blockPos.method_10086(i);
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
            if (!this.hasBlockingCollision(level2, class_23382)) continue;
            penalty += 0.08 / (double)(i - 1);
        }
        return penalty;
    }

    private final boolean hasBlockingCollision(class_1937 level2, class_2338 pos) {
        return !level2.method_8320(pos).method_26220((class_1922)level2, pos).method_1110();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u0006\u00a8\u0006\f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/MinecraftPathProcessor$Companion;", "", "<init>", "()V", "", "DEFAULT_MOB_JUMP_HEIGHT", "D", "LOW_CEILING_PENALTY", "ADJACENT_WALL_PENALTY", "ADJACENT_CORNER_PENALTY", "SECOND_RING_WALL_PENALTY", "SECOND_RING_CORNER_PENALTY", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

