/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.Mth
 *  net.minecraft.util.debug.DebugPathInfo
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.pathfinder.Node
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.level.pathfinder.Path$DebugData
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugPathInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class PathfindingRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final float MAX_RENDER_DIST = 80.0f;
    private static final int MAX_TARGETING_DIST = 8;
    private static final boolean SHOW_ONLY_SELECTED = false;
    private static final boolean SHOW_OPEN_CLOSED = true;
    private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
    private static final boolean SHOW_GROUND_LABELS = true;
    private static final float TEXT_SCALE = 0.32f;

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        debugValueAccess.forEachEntity(DebugSubscriptions.ENTITY_PATHS, (entity, debugPathInfo) -> PathfindingRenderer.renderPath(d, e, f, debugPathInfo.path(), debugPathInfo.maxNodeDistance()));
    }

    private static void renderPath(double d, double e, double f, Path path, float g) {
        PathfindingRenderer.renderPath(path, g, true, true, d, e, f);
    }

    public static void renderPath(Path path, float f, boolean bl, boolean bl2, double d, double e, double g) {
        PathfindingRenderer.renderPathLine(path, d, e, g);
        BlockPos blockPos = path.getTarget();
        if (PathfindingRenderer.distanceToCamera(blockPos, d, e, g) <= 80.0f) {
            Gizmos.cuboid((AABB)new AABB((double)((float)blockPos.getX() + 0.25f), (double)((float)blockPos.getY() + 0.25f), (double)blockPos.getZ() + 0.25, (double)((float)blockPos.getX() + 0.75f), (double)((float)blockPos.getY() + 0.75f), (double)((float)blockPos.getZ() + 0.75f)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.5f, (float)0.0f, (float)1.0f, (float)0.0f)));
            for (int i = 0; i < path.getNodeCount(); ++i) {
                Node node = path.getNode(i);
                if (!(PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, e, g) <= 80.0f)) continue;
                float h = i == path.getNextNodeIndex() ? 1.0f : 0.0f;
                float j = i == path.getNextNodeIndex() ? 0.0f : 1.0f;
                AABB aABB = new AABB((double)((float)node.x + 0.5f - f), (double)((float)node.y + 0.01f * (float)i), (double)((float)node.z + 0.5f - f), (double)((float)node.x + 0.5f + f), (double)((float)node.y + 0.25f + 0.01f * (float)i), (double)((float)node.z + 0.5f + f));
                Gizmos.cuboid((AABB)aABB, (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.5f, (float)h, (float)0.0f, (float)j)));
            }
        }
        Path.DebugData debugData = path.debugData();
        if (bl && debugData != null) {
            for (Node node2 : debugData.closedSet()) {
                if (!(PathfindingRenderer.distanceToCamera(node2.asBlockPos(), d, e, g) <= 80.0f)) continue;
                Gizmos.cuboid((AABB)new AABB((double)((float)node2.x + 0.5f - f / 2.0f), (double)((float)node2.y + 0.01f), (double)((float)node2.z + 0.5f - f / 2.0f), (double)((float)node2.x + 0.5f + f / 2.0f), (double)node2.y + 0.1, (double)((float)node2.z + 0.5f + f / 2.0f)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.5f, (float)1.0f, (float)0.8f, (float)0.8f)));
            }
            for (Node node2 : debugData.openSet()) {
                if (!(PathfindingRenderer.distanceToCamera(node2.asBlockPos(), d, e, g) <= 80.0f)) continue;
                Gizmos.cuboid((AABB)new AABB((double)((float)node2.x + 0.5f - f / 2.0f), (double)((float)node2.y + 0.01f), (double)((float)node2.z + 0.5f - f / 2.0f), (double)((float)node2.x + 0.5f + f / 2.0f), (double)node2.y + 0.1, (double)((float)node2.z + 0.5f + f / 2.0f)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.5f, (float)0.8f, (float)1.0f, (float)1.0f)));
            }
        }
        if (bl2) {
            for (int k = 0; k < path.getNodeCount(); ++k) {
                Node node3 = path.getNode(k);
                if (!(PathfindingRenderer.distanceToCamera(node3.asBlockPos(), d, e, g) <= 80.0f)) continue;
                Gizmos.billboardText((String)String.valueOf(node3.type), (Vec3)new Vec3((double)node3.x + 0.5, (double)node3.y + 0.75, (double)node3.z + 0.5), (TextGizmo.Style)TextGizmo.Style.whiteAndCentered().withScale(0.32f)).setAlwaysOnTop();
                Gizmos.billboardText((String)String.format(Locale.ROOT, "%.2f", Float.valueOf(node3.costMalus)), (Vec3)new Vec3((double)node3.x + 0.5, (double)node3.y + 0.25, (double)node3.z + 0.5), (TextGizmo.Style)TextGizmo.Style.whiteAndCentered().withScale(0.32f)).setAlwaysOnTop();
            }
        }
    }

    public static void renderPathLine(Path path, double d, double e, double f) {
        if (path.getNodeCount() < 2) {
            return;
        }
        Vec3 vec3 = path.getNode(0).asVec3();
        for (int i = 1; i < path.getNodeCount(); ++i) {
            Node node = path.getNode(i);
            if (PathfindingRenderer.distanceToCamera(node.asBlockPos(), d, e, f) > 80.0f) {
                vec3 = node.asVec3();
                continue;
            }
            float g = (float)i / (float)path.getNodeCount() * 0.33f;
            int j = ARGB.opaque((int)Mth.hsvToRgb((float)g, (float)0.9f, (float)0.9f));
            Gizmos.arrow((Vec3)vec3.add(0.5, 0.5, 0.5), (Vec3)node.asVec3().add(0.5, 0.5, 0.5), (int)j);
            vec3 = node.asVec3();
        }
    }

    private static float distanceToCamera(BlockPos blockPos, double d, double e, double f) {
        return (float)(Math.abs((double)blockPos.getX() - d) + Math.abs((double)blockPos.getY() - e) + Math.abs((double)blockPos.getZ() - f));
    }

    private static /* synthetic */ void method_75445(DebugValueAccess debugValueAccess, double d, double e, double f, Entity entity) {
        DebugPathInfo debugPathInfo = (DebugPathInfo)debugValueAccess.getEntityValue(DebugSubscriptions.ENTITY_PATHS, entity);
        if (debugPathInfo != null) {
            PathfindingRenderer.renderPath(d, e, f, debugPathInfo.path(), debugPathInfo.maxNodeDistance());
        }
    }
}

