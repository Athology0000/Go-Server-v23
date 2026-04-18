/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathing.processing.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.pathfinder.pathing.processing.Cost;
import org.cobalt.api.pathfinder.pathing.processing.NodeProcessor;
import org.cobalt.api.pathfinder.pathing.processing.context.EvaluationContext;
import org.cobalt.api.pathfinder.pathing.processing.impl.AvoidanceCache;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/AvoidanceProcessor;", "Lorg/cobalt/api/pathfinder/pathing/processing/NodeProcessor;", "<init>", "()V", "Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;", "context", "", "isValid", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Z", "Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "calculateCostContribution", "(Lorg/cobalt/api/pathfinder/pathing/processing/context/EvaluationContext;)Lorg/cobalt/api/pathfinder/pathing/processing/Cost;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "cobalt"})
public final class AvoidanceProcessor
implements NodeProcessor {
    @NotNull
    private final class_310 mc;

    public AvoidanceProcessor() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
    }

    @Override
    public boolean isValid(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        class_638 class_6382 = this.mc.field_1687;
        if (class_6382 == null) {
            return true;
        }
        class_638 level2 = class_6382;
        PathPosition pos = context.getCurrentPathPosition();
        class_2338 blockPos = new class_2338(pos.getFlooredX(), pos.getFlooredY(), pos.getFlooredZ());
        return !AvoidanceCache.INSTANCE.isAvoided((class_1937)level2, blockPos);
    }

    @Override
    @NotNull
    public Cost calculateCostContribution(@NotNull EvaluationContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        return Cost.Companion.getZERO();
    }
}

