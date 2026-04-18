/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.debug.DebugGoalInfo$DebugGoal
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class GoalSelectorDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private final Minecraft minecraft;

    public GoalSelectorDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        BlockPos blockPos = BlockPos.containing((double)camera.position().x, (double)0.0, (double)camera.position().z);
        debugValueAccess.forEachEntity(DebugSubscriptions.GOAL_SELECTORS, (entity, debugGoalInfo) -> {
            if (blockPos.closerThan((Vec3i)entity.blockPosition(), 160.0)) {
                for (int i = 0; i < debugGoalInfo.goals().size(); ++i) {
                    DebugGoalInfo.DebugGoal debugGoal = (DebugGoalInfo.DebugGoal)debugGoalInfo.goals().get(i);
                    double d = (double)entity.getBlockX() + 0.5;
                    double e = entity.getY() + 2.0 + (double)i * 0.25;
                    double f = (double)entity.getBlockZ() + 0.5;
                    int j = debugGoal.isRunning() ? -16711936 : -3355444;
                    Gizmos.billboardText((String)debugGoal.name(), (Vec3)new Vec3(d, e, f), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)j));
                }
            }
        });
    }
}

