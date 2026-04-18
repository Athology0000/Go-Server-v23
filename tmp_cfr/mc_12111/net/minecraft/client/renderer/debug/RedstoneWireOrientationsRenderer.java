/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class RedstoneWireOrientationsRenderer
implements DebugRenderer.SimpleDebugRenderer {
    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        debugValueAccess.forEachBlock(DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, (blockPos, orientation) -> {
            Vec3 vec3 = blockPos.getBottomCenter().subtract(0.0, 0.1, 0.0);
            Gizmos.arrow((Vec3)vec3, (Vec3)vec3.add(orientation.getFront().getUnitVec3().scale(0.5)), (int)-16776961);
            Gizmos.arrow((Vec3)vec3, (Vec3)vec3.add(orientation.getUp().getUnitVec3().scale(0.4)), (int)-65536);
            Gizmos.arrow((Vec3)vec3, (Vec3)vec3.add(orientation.getSide().getUnitVec3().scale(0.3)), (int)-256);
        });
    }
}

