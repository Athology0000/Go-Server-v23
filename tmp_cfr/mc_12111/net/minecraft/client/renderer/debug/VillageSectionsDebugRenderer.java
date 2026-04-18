/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

@Environment(value=EnvType.CLIENT)
public class VillageSectionsDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        debugValueAccess.forEachBlock(DebugSubscriptions.VILLAGE_SECTIONS, (blockPos, unit) -> {
            SectionPos sectionPos = SectionPos.of((BlockPos)blockPos);
            Gizmos.cuboid((BlockPos)sectionPos.center(), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.15f, (float)0.2f, (float)1.0f, (float)0.2f)));
        });
    }
}

