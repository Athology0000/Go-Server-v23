/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.processing.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.pathfinder.pathing.processing.Cost;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/MinecraftParkourProcessor;", "Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "context", "", "isValid", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Z", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "calculateCostContribution", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "cobalt"})
public final class MinecraftParkourProcessor
implements NodeProcessor {
    @NotNull
    private final class_310 mc;

    public MinecraftParkourProcessor() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
    }

    @Override
    public boolean isValid(@NotNull EvaluationContext context) {
        boolean isAxisJump;
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        class_638 class_6382 = this.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        PathPosition pathPosition = context.getPreviousPathPosition();
        if (pathPosition == null) {
            return true;
        }
        PathPosition prev = pathPosition;
        PathPosition current = context.getCurrentPathPosition();
        class_2338 prevPos = new class_2338(prev.getFlooredX(), prev.getFlooredY(), prev.getFlooredZ());
        class_2338 currPos = new class_2338(current.getFlooredX(), current.getFlooredY(), current.getFlooredZ());
        if (!MinecraftPathingRules.INSTANCE.isWalkable((class_1937)level2, currPos)) {
            return false;
        }
        int dx = currPos.method_10263() - prevPos.method_10263();
        int dy = currPos.method_10264() - prevPos.method_10264();
        int dz = currPos.method_10260() - prevPos.method_10260();
        int absDx = Math.abs(dx);
        int absDz = Math.abs(dz);
        if (absDx == 0 && absDz == 0) {
            if (dy == 0) {
                return true;
            }
            return MinecraftPathingRules.INSTANCE.isClimbable((class_1937)level2, prevPos) || MinecraftPathingRules.INSTANCE.isClimbable((class_1937)level2, currPos);
        }
        if (absDx <= 1 && absDz <= 1) {
            if (dy > 1 || dy < -3) {
                return false;
            }
            return absDx != 1 || absDz != 1 || MinecraftPathingRules.INSTANCE.canMoveDiagonal((class_1937)level2, prevPos, dx, dz);
        }
        boolean bl = isAxisJump = absDx >= 2 && absDz == 0 || absDz >= 2 && absDx == 0;
        if (!isAxisJump) {
            return false;
        }
        int len = Math.max(absDx, absDz);
        if (len > 3) {
            return false;
        }
        if (dy > 1 || dy < -3) {
            return false;
        }
        class_1937 class_19372 = (class_1937)level2;
        class_2338 class_23382 = prevPos.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"below(...)");
        if (!MinecraftPathingRules.INSTANCE.isStandable(class_19372, class_23382)) {
            return false;
        }
        class_2350 dir = absDx > absDz ? (dx > 0 ? class_2350.field_11034 : class_2350.field_11039) : (dz > 0 ? class_2350.field_11035 : class_2350.field_11043);
        class_2338 class_23383 = prevPos.method_10079(dir, len);
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"relative(...)");
        class_2338 landingBase = class_23383;
        class_2338 class_23384 = MinecraftPathingRules.INSTANCE.walkableAt((class_1937)level2, landingBase);
        if (class_23384 == null) {
            return false;
        }
        class_2338 resolved = class_23384;
        if (!Intrinsics.areEqual((Object)resolved, (Object)currPos)) {
            return false;
        }
        if (!MinecraftPathingRules.INSTANCE.gapClear((class_1937)level2, prevPos, dir, len)) {
            return false;
        }
        return MinecraftPathingRules.INSTANCE.hasRunway((class_1937)level2, prevPos, dir);
    }

    @Override
    @NotNull
    public Cost calculateCostContribution(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        return Cost.Companion.getZERO();
    }
}

