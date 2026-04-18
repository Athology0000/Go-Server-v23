/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2482
 *  net.minecraft.class_2510
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.jni;

import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2482;
import net.minecraft.class_2510;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.pathfinder.jni.ActionType;
import org.cobalt.api.pathfinder.jni.MovementInputs;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.jni.PathfinderRotationStrategy;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.api.util.player.MovementManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\u000e\n\u0002\b\u0012\b\u0086\b\u0018\u0000 f2\u00020\u0001:\u0001fBW\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0006\u0010\u000f\u001a\u00020\b\u00a2\u0006\u0004\b\u0010\u0010\u0011J\r\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J!\u0010\u001a\u001a\u00020\u00192\b\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0018\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0019\u0010\u001c\u001a\u00020\u00022\b\u0010\u0016\u001a\u0004\u0018\u00010\u0015H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u001f\u0010\"\u001a\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010!\u001a\u00020 H\u0002\u00a2\u0006\u0004\b\"\u0010#J\u0017\u0010$\u001a\u00020\u00172\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0019\u0010&\u001a\u0004\u0018\u00010 2\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b&\u0010'J\u0019\u0010(\u001a\u0004\u0018\u00010 2\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b(\u0010'J%\u0010,\u001a\u00020+2\u0006\u0010\u0016\u001a\u00020\u00152\f\u0010*\u001a\b\u0012\u0004\u0012\u00020 0)H\u0002\u00a2\u0006\u0004\b,\u0010-J/\u0010/\u001a\u0004\u0018\u00010+2\u0006\u0010\u0016\u001a\u00020\u00152\f\u0010*\u001a\b\u0012\u0004\u0012\u00020 0)2\u0006\u0010.\u001a\u00020+H\u0002\u00a2\u0006\u0004\b/\u00100J%\u00102\u001a\u00020 2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020 0)2\u0006\u00101\u001a\u00020+H\u0002\u00a2\u0006\u0004\b2\u00103J\u001f\u00106\u001a\u00020 2\u0006\u00104\u001a\u00020 2\u0006\u00105\u001a\u00020 H\u0002\u00a2\u0006\u0004\b6\u00107J/\u0010<\u001a\u00020+2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u00109\u001a\u0002082\u0006\u0010:\u001a\u00020+2\u0006\u0010;\u001a\u00020+H\u0002\u00a2\u0006\u0004\b<\u0010=J\u000f\u0010>\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b>\u0010?J\u000f\u0010@\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b@\u0010?J\u0010\u0010A\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bA\u0010?J\u0010\u0010B\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bB\u0010?J\u0010\u0010C\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bC\u0010?J\u0010\u0010D\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bD\u0010?J\u0010\u0010E\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bE\u0010?J\u0010\u0010F\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\bF\u0010GJ\u0010\u0010H\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\bH\u0010GJ\u0010\u0010I\u001a\u00020\u000bH\u00c6\u0003\u00a2\u0006\u0004\bI\u0010JJ\u0010\u0010K\u001a\u00020\rH\u00c6\u0003\u00a2\u0006\u0004\bK\u0010LJ\u0010\u0010M\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\bM\u0010GJt\u0010N\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0004\bN\u0010OJ\u001b\u0010Q\u001a\u00020\u00022\b\u0010P\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\bQ\u0010RJ\u0011\u0010S\u001a\u00020+H\u00d6\u0081\u0004\u00a2\u0006\u0004\bS\u0010TJ\u0011\u0010V\u001a\u00020UH\u00d6\u0081\u0004\u00a2\u0006\u0004\bV\u0010WR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010X\u001a\u0004\bY\u0010?R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010X\u001a\u0004\bZ\u0010?R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010X\u001a\u0004\b[\u0010?R\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010X\u001a\u0004\b\\\u0010?R\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010X\u001a\u0004\b]\u0010?R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010^\u001a\u0004\b_\u0010GR\u0017\u0010\n\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010^\u001a\u0004\b`\u0010GR\u0017\u0010\f\u001a\u00020\u000b8\u0006\u00a2\u0006\f\n\u0004\b\f\u0010a\u001a\u0004\bb\u0010JR\u0017\u0010\u000e\u001a\u00020\r8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010c\u001a\u0004\bd\u0010LR\u0017\u0010\u000f\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010^\u001a\u0004\be\u0010G\u00a8\u0006g"}, d2={"Lorg/cobalt/api/pathfinder/jni/PathCommand;", "", "", "forward", "back", "jump", "sneak", "sprint", "", "targetYaw", "targetPitch", "Lorg/cobalt/api/pathfinder/jni/PathStatus;", "status", "Lorg/cobalt/api/pathfinder/jni/ActionType;", "activeAction", "distanceToTarget", "<init>", "(ZZZZZFFLorg/cobalt/api/pathfinder/jni/PathStatus;Lorg/cobalt/api/pathfinder/jni/ActionType;F)V", "", "applyToPlayer", "()V", "Lnet/minecraft/class_746;", "player", "Lorg/cobalt/api/util/helper/Rotation;", "targetRotation", "Lorg/cobalt/api/pathfinder/jni/MovementInputs;", "resolveMovementInputs", "(Lnet/minecraft/class_746;Lorg/cobalt/api/util/helper/Rotation;)Lorg/cobalt/api/pathfinder/jni/MovementInputs;", "shouldAssistJump", "(Lnet/minecraft/class_746;)Z", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_243;", "traversalNode", "isAutoStepLanding", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_243;)Z", "resolveGuidedRotation", "(Lnet/minecraft/class_746;)Lorg/cobalt/api/util/helper/Rotation;", "resolveAveragedGuidePoint", "(Lnet/minecraft/class_746;)Lnet/minecraft/class_243;", "resolveGuidePoint", "", "nodes", "", "nearestNodeIndex", "(Lnet/minecraft/class_746;Ljava/util/List;)I", "nearestIndex", "firstUpcomingTraversalNode", "(Lnet/minecraft/class_746;Ljava/util/List;I)Ljava/lang/Integer;", "index", "nodeDirection", "(Ljava/util/List;I)Lnet/minecraft/class_243;", "baseNode", "direction", "centerTunnelNode", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;)Lnet/minecraft/class_243;", "Lnet/minecraft/class_2338;", "origin", "stepX", "stepZ", "scanContiguousWalkable", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;II)I", "usesGroundMovement", "()Z", "usesStrafeAdjustment", "component1", "component2", "component3", "component4", "component5", "component6", "()F", "component7", "component8", "()Lorg/cobalt/api/pathfinder/jni/PathStatus;", "component9", "()Lorg/cobalt/api/pathfinder/jni/ActionType;", "component10", "copy", "(ZZZZZFFLorg/cobalt/api/pathfinder/jni/PathStatus;Lorg/cobalt/api/pathfinder/jni/ActionType;F)Lorg/cobalt/api/pathfinder/jni/PathCommand;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Z", "getForward", "getBack", "getJump", "getSneak", "getSprint", "F", "getTargetYaw", "getTargetPitch", "Lorg/cobalt/api/pathfinder/jni/PathStatus;", "getStatus", "Lorg/cobalt/api/pathfinder/jni/ActionType;", "getActiveAction", "getDistanceToTarget", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPathCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCommand.kt\norg/cobalt/api/pathfinder/jni/PathCommand\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,316:1\n1#2:317\n*E\n"})
public final class PathCommand {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final boolean forward;
    private final boolean back;
    private final boolean jump;
    private final boolean sneak;
    private final boolean sprint;
    private final float targetYaw;
    private final float targetPitch;
    @NotNull
    private final PathStatus status;
    @NotNull
    private final ActionType activeAction;
    private final float distanceToTarget;
    private static final double AUTO_JUMP_MIN_RISE = 0.45;
    private static final double AUTO_JUMP_MAX_RISE = 1.25;
    private static final double AUTO_JUMP_CURRENT_NODE_DISTANCE_SQ = 0.25;
    private static final double AUTO_JUMP_MIN_NODE_DISTANCE_SQ = 0.04;
    private static final double AUTO_JUMP_MAX_NODE_DISTANCE_SQ = 2.25;
    private static final double AUTO_JUMP_TRIGGER_DISTANCE_SQ = 1.96;
    private static final int AUTO_JUMP_NODE_LOOKAHEAD = 2;
    private static final double AUTO_JUMP_SOLID_BLOCK_HEIGHT = 0.75;
    private static final float CLOSE_ALIGN_DISTANCE = 1.6f;
    private static final int LOOKAHEAD_NODE_COUNT = 2;
    private static final int ROTATION_LOOKAHEAD = 4;
    private static final int CENTER_SCAN_RADIUS = 3;
    private static final double STRAIGHT_AXIS_RATIO = 1.5;
    private static final double MIN_DIRECTION_MAG = 0.05;
    private static final double GUIDE_LOOK_HEIGHT = 1.62;

    public PathCommand(boolean forward, boolean back, boolean jump, boolean sneak, boolean sprint, float targetYaw, float targetPitch, @NotNull PathStatus status, @NotNull ActionType activeAction, float distanceToTarget) {
        Intrinsics.checkNotNullParameter((Object)((Object)status), (String)"status");
        Intrinsics.checkNotNullParameter((Object)((Object)activeAction), (String)"activeAction");
        this.forward = forward;
        this.back = back;
        this.jump = jump;
        this.sneak = sneak;
        this.sprint = sprint;
        this.targetYaw = targetYaw;
        this.targetPitch = targetPitch;
        this.status = status;
        this.activeAction = activeAction;
        this.distanceToTarget = distanceToTarget;
    }

    public final boolean getForward() {
        return this.forward;
    }

    public final boolean getBack() {
        return this.back;
    }

    public final boolean getJump() {
        return this.jump;
    }

    public final boolean getSneak() {
        return this.sneak;
    }

    public final boolean getSprint() {
        return this.sprint;
    }

    public final float getTargetYaw() {
        return this.targetYaw;
    }

    public final float getTargetPitch() {
        return this.targetPitch;
    }

    @NotNull
    public final PathStatus getStatus() {
        return this.status;
    }

    @NotNull
    public final ActionType getActiveAction() {
        return this.activeAction;
    }

    public final float getDistanceToTarget() {
        return this.distanceToTarget;
    }

    public final void applyToPlayer() {
        Object object;
        class_746 player;
        block5: {
            block4: {
                player = class_310.method_1551().field_1724;
                object = player;
                if (object == null) break block4;
                class_746 p0 = object;
                boolean bl = false;
                Rotation rotation = this.resolveGuidedRotation(p0);
                object = rotation;
                if (rotation != null) break block5;
            }
            object = new Rotation(this.targetYaw, this.targetPitch);
        }
        Object targetRotation = object;
        boolean shouldJump = this.jump || this.shouldAssistJump(player);
        MovementInputs movement = this.resolveMovementInputs(player, (Rotation)targetRotation);
        MovementManager.setMovementLock(true);
        MovementManager.setForcedMovement(movement.getForward(), movement.getBackward(), movement.getLeft(), movement.getRight(), shouldJump, this.sneak, movement.getSprint());
        class_746 class_7462 = player;
        if (class_7462 != null) {
            class_7462.method_5728(movement.getSprint());
        }
        RotationExecutor.INSTANCE.rotateTo((Rotation)targetRotation, PathfinderRotationStrategy.INSTANCE);
    }

    private final MovementInputs resolveMovementInputs(class_746 player, Rotation targetRotation) {
        double dz;
        if (player == null || !this.usesGroundMovement()) {
            boolean shouldSprint = this.sprint && this.forward && !this.back;
            return new MovementInputs(this.forward, this.back, false, false, shouldSprint);
        }
        class_243 class_2432 = this.resolveGuidePoint(player);
        if (class_2432 == null) {
            PathCommand $this$resolveMovementInputs_u24lambda_u240 = this;
            boolean bl = false;
            double yawRad = Math.toRadians(targetRotation.getYaw());
            class_2432 = new class_243(player.method_23317() - Math.sin(yawRad), player.method_23318(), player.method_23321() + Math.cos(yawRad));
        }
        class_243 guidePoint = class_2432;
        double dx = guidePoint.field_1352 - player.method_23317();
        double len = Math.sqrt(dx * dx + (dz = guidePoint.field_1350 - player.method_23321()) * dz);
        if (len < 0.05) {
            return new MovementInputs(false, false, false, false, false);
        }
        double nx = dx / len;
        double nz = dz / len;
        double yawRad = Math.toRadians(targetRotation.getYaw());
        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        float localForward = (float)(-nx * sinYaw + nz * cosYaw);
        float localStrafe = (float)(nx * cosYaw + nz * sinYaw);
        float forwardThreshold = this.distanceToTarget <= 1.6f ? 0.08f : 0.18f;
        float strafeThreshold = this.distanceToTarget <= 1.6f ? 0.08f : 0.18f;
        boolean shouldMoveForward = this.forward && localForward > -0.05f;
        boolean shouldMoveBackward = this.back && localForward < -0.18f;
        boolean shouldMoveLeft = this.usesStrafeAdjustment() && localStrafe < -strafeThreshold;
        boolean shouldMoveRight = this.usesStrafeAdjustment() && localStrafe > strafeThreshold;
        boolean shouldSprint = this.sprint && shouldMoveForward && !shouldMoveBackward && localForward > forwardThreshold;
        return new MovementInputs(shouldMoveForward, shouldMoveBackward, shouldMoveLeft, shouldMoveRight, shouldSprint);
    }

    private final boolean shouldAssistJump(class_746 player) {
        double dz;
        if (this.activeAction == ActionType.JUMP || this.activeAction == ActionType.SPRINT_JUMP) {
            return true;
        }
        if (player == null || !player.method_24828()) {
            return false;
        }
        if (!this.usesGroundMovement()) {
            return false;
        }
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
        if (nodes.size() < 2) {
            return false;
        }
        int nearestIndex = this.nearestNodeIndex(player, nodes);
        if (nearestIndex < 0) {
            return false;
        }
        Integer n = this.firstUpcomingTraversalNode(player, nodes, nearestIndex);
        if (n == null) {
            return false;
        }
        int traversalIndex = n;
        class_243 previousNode = nodes.get(traversalIndex - 1);
        class_243 traversalNode = nodes.get(traversalIndex);
        double rise = traversalNode.field_1351 - previousNode.field_1351;
        if (rise < 0.45 || rise > 1.25) {
            return false;
        }
        if (this.isAutoStepLanding((class_1937)level2, traversalNode)) {
            return false;
        }
        double dx = traversalNode.field_1352 - player.method_23317();
        double horizontalDistSq = dx * dx + (dz = traversalNode.field_1350 - player.method_23321()) * dz;
        return horizontalDistSq <= 1.96;
    }

    private final boolean isAutoStepLanding(class_1937 level2, class_243 traversalNode) {
        class_2338 class_23382 = class_2338.method_49637((double)traversalNode.field_1352, (double)(traversalNode.field_1351 - 1.0), (double)traversalNode.field_1350);
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 landingPos = class_23382;
        class_2680 class_26802 = level2.method_8320(landingPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 landingState = class_26802;
        class_2248 class_22482 = landingState.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 landingBlock = class_22482;
        if (landingBlock instanceof class_2482 || landingBlock instanceof class_2510) {
            return true;
        }
        class_265 class_2652 = landingState.method_26220((class_1922)level2, landingPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        if (shape.method_1110()) {
            return false;
        }
        return shape.method_1107().field_1325 < 0.75;
    }

    private final Rotation resolveGuidedRotation(class_746 player) {
        if (!this.usesGroundMovement()) {
            return new Rotation(this.targetYaw, this.targetPitch);
        }
        class_243 class_2432 = this.resolveAveragedGuidePoint(player);
        if (class_2432 == null) {
            return new Rotation(this.targetYaw, this.targetPitch);
        }
        class_243 guide = class_2432;
        class_243 class_2433 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getEyePosition(...)");
        return AngleUtils.INSTANCE.getRotation(class_2433, guide);
    }

    private final class_243 resolveAveragedGuidePoint(class_746 player) {
        List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
        if (nodes.isEmpty()) {
            return null;
        }
        int nearestIndex = this.nearestNodeIndex(player, nodes);
        if (nearestIndex < 0) {
            return null;
        }
        double sumX = 0.0;
        double sumZ = 0.0;
        int count = 0;
        for (int i = 1; i < 5; ++i) {
            class_243 node = nodes.get(Math.min(CollectionsKt.getLastIndex(nodes), nearestIndex + i));
            sumX += node.field_1352;
            sumZ += node.field_1350;
            ++count;
        }
        class_243 refNode = nodes.get(Math.min(CollectionsKt.getLastIndex(nodes), nearestIndex + 2));
        double guideY = Math.max(refNode.field_1351 + 1.62, player.method_33571().field_1351);
        return new class_243(sumX / (double)count + 0.5, guideY, sumZ / (double)count + 0.5);
    }

    private final class_243 resolveGuidePoint(class_746 player) {
        List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
        if (nodes.isEmpty()) {
            return null;
        }
        int nearestIndex = this.nearestNodeIndex(player, nodes);
        if (nearestIndex < 0) {
            return null;
        }
        int guideIndex = Math.min(CollectionsKt.getLastIndex(nodes), nearestIndex + 2);
        class_243 baseNode = nodes.get(guideIndex);
        class_243 direction = this.nodeDirection(nodes, guideIndex);
        class_243 centered = this.centerTunnelNode(baseNode, direction);
        double guideY = Math.max(baseNode.field_1351 + 1.62, player.method_33571().field_1351);
        return new class_243(centered.field_1352, guideY, centered.field_1350);
    }

    private final int nearestNodeIndex(class_746 player, List<? extends class_243> nodes) {
        int nearestIndex = -1;
        double nearestDistSq = Double.POSITIVE_INFINITY;
        double px = player.method_23317();
        double pz = player.method_23321();
        int n = ((Collection)nodes).size();
        for (int index = 0; index < n; ++index) {
            class_243 node = nodes.get(index);
            double dx = node.field_1352 - px;
            double dz = node.field_1350 - pz;
            double distSq = dx * dx + dz * dz;
            if (!(distSq < nearestDistSq)) continue;
            nearestDistSq = distSq;
            nearestIndex = index;
        }
        return nearestIndex;
    }

    private final Integer firstUpcomingTraversalNode(class_746 player, List<? extends class_243> nodes, int nearestIndex) {
        int endIndex;
        double px = player.method_23317();
        double pz = player.method_23321();
        class_243 nearest = nodes.get(nearestIndex);
        double nearestDx = nearest.field_1352 - px;
        double nearestDz = nearest.field_1350 - pz;
        double nearestHorizontalDistSq = nearestDx * nearestDx + nearestDz * nearestDz;
        int startIndex = nearestHorizontalDistSq <= 0.25 && nearestIndex < CollectionsKt.getLastIndex(nodes) ? nearestIndex + 1 : nearestIndex;
        int firstIndex = Math.max(1, startIndex);
        if (firstIndex > (endIndex = Math.min(CollectionsKt.getLastIndex(nodes), startIndex + 2))) {
            return null;
        }
        int index = firstIndex;
        if (index <= endIndex) {
            while (true) {
                class_243 node = nodes.get(index);
                double dx = node.field_1352 - px;
                double dz = node.field_1350 - pz;
                double horizontalDistSq = dx * dx + dz * dz;
                if (!(horizontalDistSq < 0.04)) {
                    if (horizontalDistSq > 2.25) {
                        return null;
                    }
                    return index;
                }
                if (index == endIndex) break;
                ++index;
            }
        }
        return null;
    }

    private final class_243 nodeDirection(List<? extends class_243> nodes, int index) {
        class_243 prev = nodes.get(Math.max(0, index - 1));
        class_243 next = nodes.get(Math.min(CollectionsKt.getLastIndex(nodes), index + 1));
        return new class_243(next.field_1352 - prev.field_1352, next.field_1351 - prev.field_1351, next.field_1350 - prev.field_1350);
    }

    private final class_243 centerTunnelNode(class_243 baseNode, class_243 direction) {
        class_243 class_2432;
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            return baseNode;
        }
        class_638 level2 = class_6382;
        class_2338 class_23382 = class_2338.method_49637((double)baseNode.field_1352, (double)baseNode.field_1351, (double)baseNode.field_1350);
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 basePos = class_23382;
        double absDx = Math.abs(direction.field_1352);
        double absDz = Math.abs(direction.field_1350);
        if (absDx < 0.05 && absDz < 0.05) {
            return baseNode;
        }
        if (absDz >= absDx * 1.5) {
            int minX = this.scanContiguousWalkable((class_1937)level2, basePos, -1, 0);
            int maxX = this.scanContiguousWalkable((class_1937)level2, basePos, 1, 0);
            double centerX = (double)(minX + maxX + 1) / 2.0;
            class_2432 = new class_243(centerX, (double)basePos.method_10264(), (double)basePos.method_10260() + 0.5);
        } else if (absDx >= absDz * 1.5) {
            int minZ = this.scanContiguousWalkable((class_1937)level2, basePos, 0, -1);
            int maxZ = this.scanContiguousWalkable((class_1937)level2, basePos, 0, 1);
            double centerZ = (double)(minZ + maxZ + 1) / 2.0;
            class_2432 = new class_243((double)basePos.method_10263() + 0.5, (double)basePos.method_10264(), centerZ);
        } else {
            class_2432 = baseNode;
        }
        return class_2432;
    }

    private final int scanContiguousWalkable(class_1937 level2, class_2338 origin, int stepX, int stepZ) {
        class_2338 current = origin;
        for (int step = 1; step < 4; ++step) {
            class_2338 next;
            Intrinsics.checkNotNullExpressionValue((Object)current.method_10069(stepX, 0, stepZ), (String)"offset(...)");
            if (!MinecraftPathingRules.INSTANCE.isWalkable(level2, next)) break;
            current = next;
        }
        return stepX != 0 ? current.method_10263() : current.method_10260();
    }

    private final boolean usesGroundMovement() {
        return this.activeAction == ActionType.WALK || this.activeAction == ActionType.SPRINT || this.activeAction == ActionType.JUMP || this.activeAction == ActionType.SPRINT_JUMP;
    }

    private final boolean usesStrafeAdjustment() {
        return this.usesGroundMovement() && !this.back;
    }

    public final boolean component1() {
        return this.forward;
    }

    public final boolean component2() {
        return this.back;
    }

    public final boolean component3() {
        return this.jump;
    }

    public final boolean component4() {
        return this.sneak;
    }

    public final boolean component5() {
        return this.sprint;
    }

    public final float component6() {
        return this.targetYaw;
    }

    public final float component7() {
        return this.targetPitch;
    }

    @NotNull
    public final PathStatus component8() {
        return this.status;
    }

    @NotNull
    public final ActionType component9() {
        return this.activeAction;
    }

    public final float component10() {
        return this.distanceToTarget;
    }

    @NotNull
    public final PathCommand copy(boolean forward, boolean back, boolean jump, boolean sneak, boolean sprint, float targetYaw, float targetPitch, @NotNull PathStatus status, @NotNull ActionType activeAction, float distanceToTarget) {
        Intrinsics.checkNotNullParameter((Object)((Object)status), (String)"status");
        Intrinsics.checkNotNullParameter((Object)((Object)activeAction), (String)"activeAction");
        return new PathCommand(forward, back, jump, sneak, sprint, targetYaw, targetPitch, status, activeAction, distanceToTarget);
    }

    public static /* synthetic */ PathCommand copy$default(PathCommand pathCommand, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, float f, float f2, PathStatus pathStatus, ActionType actionType, float f3, int n, Object object) {
        if ((n & 1) != 0) {
            bl = pathCommand.forward;
        }
        if ((n & 2) != 0) {
            bl2 = pathCommand.back;
        }
        if ((n & 4) != 0) {
            bl3 = pathCommand.jump;
        }
        if ((n & 8) != 0) {
            bl4 = pathCommand.sneak;
        }
        if ((n & 0x10) != 0) {
            bl5 = pathCommand.sprint;
        }
        if ((n & 0x20) != 0) {
            f = pathCommand.targetYaw;
        }
        if ((n & 0x40) != 0) {
            f2 = pathCommand.targetPitch;
        }
        if ((n & 0x80) != 0) {
            pathStatus = pathCommand.status;
        }
        if ((n & 0x100) != 0) {
            actionType = pathCommand.activeAction;
        }
        if ((n & 0x200) != 0) {
            f3 = pathCommand.distanceToTarget;
        }
        return pathCommand.copy(bl, bl2, bl3, bl4, bl5, f, f2, pathStatus, actionType, f3);
    }

    @NotNull
    public String toString() {
        return "PathCommand(forward=" + this.forward + ", back=" + this.back + ", jump=" + this.jump + ", sneak=" + this.sneak + ", sprint=" + this.sprint + ", targetYaw=" + this.targetYaw + ", targetPitch=" + this.targetPitch + ", status=" + this.status + ", activeAction=" + this.activeAction + ", distanceToTarget=" + this.distanceToTarget + ")";
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.forward);
        result = result * 31 + Boolean.hashCode(this.back);
        result = result * 31 + Boolean.hashCode(this.jump);
        result = result * 31 + Boolean.hashCode(this.sneak);
        result = result * 31 + Boolean.hashCode(this.sprint);
        result = result * 31 + Float.hashCode(this.targetYaw);
        result = result * 31 + Float.hashCode(this.targetPitch);
        result = result * 31 + this.status.hashCode();
        result = result * 31 + this.activeAction.hashCode();
        result = result * 31 + Float.hashCode(this.distanceToTarget);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PathCommand)) {
            return false;
        }
        PathCommand pathCommand = (PathCommand)other;
        if (this.forward != pathCommand.forward) {
            return false;
        }
        if (this.back != pathCommand.back) {
            return false;
        }
        if (this.jump != pathCommand.jump) {
            return false;
        }
        if (this.sneak != pathCommand.sneak) {
            return false;
        }
        if (this.sprint != pathCommand.sprint) {
            return false;
        }
        if (Float.compare(this.targetYaw, pathCommand.targetYaw) != 0) {
            return false;
        }
        if (Float.compare(this.targetPitch, pathCommand.targetPitch) != 0) {
            return false;
        }
        if (this.status != pathCommand.status) {
            return false;
        }
        if (this.activeAction != pathCommand.activeAction) {
            return false;
        }
        return Float.compare(this.distanceToTarget, pathCommand.distanceToTarget) == 0;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u0006R\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0006R\u0014\u0010\u0011\u001a\u00020\u00108\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0013\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u000eR\u0014\u0010\u0014\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0015\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u000eR\u0014\u0010\u0016\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0006R\u0014\u0010\u0017\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0006R\u0014\u0010\u0018\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0006\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/api/pathfinder/jni/PathCommand$Companion;", "", "<init>", "()V", "", "AUTO_JUMP_MIN_RISE", "D", "AUTO_JUMP_MAX_RISE", "AUTO_JUMP_CURRENT_NODE_DISTANCE_SQ", "AUTO_JUMP_MIN_NODE_DISTANCE_SQ", "AUTO_JUMP_MAX_NODE_DISTANCE_SQ", "AUTO_JUMP_TRIGGER_DISTANCE_SQ", "", "AUTO_JUMP_NODE_LOOKAHEAD", "I", "AUTO_JUMP_SOLID_BLOCK_HEIGHT", "", "CLOSE_ALIGN_DISTANCE", "F", "LOOKAHEAD_NODE_COUNT", "ROTATION_LOOKAHEAD", "CENTER_SCAN_RADIUS", "STRAIGHT_AXIS_RATIO", "MIN_DIRECTION_MAG", "GUIDE_LOOK_HEIGHT", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

