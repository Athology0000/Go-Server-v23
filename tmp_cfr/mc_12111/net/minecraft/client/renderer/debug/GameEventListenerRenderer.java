/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class GameEventListenerRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final float BOX_HEIGHT = 1.0f;

    private void forEachListener(DebugValueAccess debugValueAccess, ListenerVisitor listenerVisitor) {
        debugValueAccess.forEachBlock(DebugSubscriptions.GAME_EVENT_LISTENERS, (blockPos, debugGameEventListenerInfo) -> listenerVisitor.accept(blockPos.getCenter(), debugGameEventListenerInfo.listenerRadius()));
        debugValueAccess.forEachEntity(DebugSubscriptions.GAME_EVENT_LISTENERS, (entity, debugGameEventListenerInfo) -> listenerVisitor.accept(entity.position(), debugGameEventListenerInfo.listenerRadius()));
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        this.forEachListener(debugValueAccess, (vec3, i) -> {
            double d = (double)i * 2.0;
            Gizmos.cuboid((AABB)AABB.ofSize((Vec3)vec3, (double)d, (double)d, (double)d), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.35f, (float)1.0f, (float)1.0f, (float)0.0f)));
        });
        this.forEachListener(debugValueAccess, (vec3, i) -> Gizmos.cuboid((AABB)AABB.ofSize((Vec3)vec3, (double)0.5, (double)1.0, (double)0.5).move(0.0, 0.5, 0.0), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.35f, (float)1.0f, (float)1.0f, (float)0.0f))));
        this.forEachListener(debugValueAccess, (vec3, i) -> {
            Gizmos.billboardText((String)"Listener Origin", (Vec3)vec3.add(0.0, 1.8, 0.0), (TextGizmo.Style)TextGizmo.Style.whiteAndCentered().withScale(0.4f));
            Gizmos.billboardText((String)BlockPos.containing((Position)vec3).toString(), (Vec3)vec3.add(0.0, 1.5, 0.0), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)-6959665).withScale(0.4f));
        });
        debugValueAccess.forEachEvent(DebugSubscriptions.GAME_EVENTS, (debugGameEventInfo, i, j) -> {
            Vec3 vec3 = debugGameEventInfo.pos();
            double d = 0.4;
            AABB aABB = AABB.ofSize((Vec3)vec3.add(0.0, 0.5, 0.0), (double)0.4, (double)0.9, (double)0.4);
            Gizmos.cuboid((AABB)aABB, (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.2f, (float)1.0f, (float)1.0f, (float)1.0f)));
            Gizmos.billboardText((String)debugGameEventInfo.event().getRegisteredName(), (Vec3)vec3.add(0.0, 0.85, 0.0), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)-7564911).withScale(0.12f));
        });
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface ListenerVisitor {
        public void accept(Vec3 var1, int var2);
    }
}

