/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class BreezeDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int JUMP_TARGET_LINE_COLOR = ARGB.color((int)255, (int)255, (int)100, (int)255);
    private static final int TARGET_LINE_COLOR = ARGB.color((int)255, (int)100, (int)255, (int)255);
    private static final int INNER_CIRCLE_COLOR = ARGB.color((int)255, (int)0, (int)255, (int)0);
    private static final int MIDDLE_CIRCLE_COLOR = ARGB.color((int)255, (int)255, (int)165, (int)0);
    private static final int OUTER_CIRCLE_COLOR = ARGB.color((int)255, (int)255, (int)0, (int)0);
    private final Minecraft minecraft;

    public BreezeDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        ClientLevel clientLevel = this.minecraft.level;
        debugValueAccess.forEachEntity(DebugSubscriptions.BREEZES, (entity2, debugBreezeInfo) -> {
            debugBreezeInfo.attackTarget().map(clientLevel::getEntity).map(entity -> entity.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true))).ifPresent(vec3 -> {
                Gizmos.arrow((Vec3)entity2.position(), (Vec3)vec3, (int)TARGET_LINE_COLOR);
                Vec3 vec32 = vec3.add(0.0, (double)0.01f, 0.0);
                Gizmos.circle((Vec3)vec32, (float)4.0f, (GizmoStyle)GizmoStyle.stroke((int)INNER_CIRCLE_COLOR));
                Gizmos.circle((Vec3)vec32, (float)8.0f, (GizmoStyle)GizmoStyle.stroke((int)MIDDLE_CIRCLE_COLOR));
                Gizmos.circle((Vec3)vec32, (float)24.0f, (GizmoStyle)GizmoStyle.stroke((int)OUTER_CIRCLE_COLOR));
            });
            debugBreezeInfo.jumpTarget().ifPresent(blockPos -> {
                Gizmos.arrow((Vec3)entity2.position(), (Vec3)blockPos.getCenter(), (int)JUMP_TARGET_LINE_COLOR);
                Gizmos.cuboid((AABB)AABB.unitCubeFromLowerCorner((Vec3)Vec3.atLowerCornerOf((Vec3i)blockPos)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)1.0f, (float)1.0f, (float)0.0f, (float)0.0f)));
            });
        });
    }
}

