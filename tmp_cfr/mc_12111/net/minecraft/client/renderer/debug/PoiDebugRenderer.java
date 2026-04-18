/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.network.protocol.game.DebugEntityNameGenerator
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugPoiInfo
 *  net.minecraft.util.debug.DebugSubscriptions
 *  net.minecraft.util.debug.DebugValueAccess
 */
package net.minecraft.client.renderer.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugPoiInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

@Environment(value=EnvType.CLIENT)
public class PoiDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
    private static final float TEXT_SCALE = 0.32f;
    private static final int ORANGE = -23296;
    private final BrainDebugRenderer brainRenderer;

    public PoiDebugRenderer(BrainDebugRenderer brainDebugRenderer) {
        this.brainRenderer = brainDebugRenderer;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        BlockPos blockPos = BlockPos.containing((double)d, (double)e, (double)f);
        debugValueAccess.forEachBlock(DebugSubscriptions.POIS, (blockPos2, debugPoiInfo) -> {
            if (blockPos.closerThan((Vec3i)blockPos2, 30.0)) {
                PoiDebugRenderer.highlightPoi(blockPos2);
                this.renderPoiInfo((DebugPoiInfo)debugPoiInfo, debugValueAccess);
            }
        });
        this.brainRenderer.getGhostPois(debugValueAccess).forEach((blockPos2, list) -> {
            if (debugValueAccess.getBlockValue(DebugSubscriptions.POIS, blockPos2) != null) {
                return;
            }
            if (blockPos.closerThan((Vec3i)blockPos2, 30.0)) {
                this.renderGhostPoi((BlockPos)blockPos2, (List<String>)list);
            }
        });
    }

    private static void highlightPoi(BlockPos blockPos) {
        float f = 0.05f;
        Gizmos.cuboid((BlockPos)blockPos, (float)0.05f, (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
    }

    private void renderGhostPoi(BlockPos blockPos, List<String> list) {
        float f = 0.05f;
        Gizmos.cuboid((BlockPos)blockPos, (float)0.05f, (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
        Gizmos.billboardTextOverBlock((String)list.toString(), (BlockPos)blockPos, (int)0, (int)-256, (float)0.32f);
        Gizmos.billboardTextOverBlock((String)"Ghost POI", (BlockPos)blockPos, (int)1, (int)-65536, (float)0.32f);
    }

    private void renderPoiInfo(DebugPoiInfo debugPoiInfo, DebugValueAccess debugValueAccess) {
        int i = 0;
        if (SharedConstants.DEBUG_BRAIN) {
            List<String> list = this.getTicketHolderNames(debugPoiInfo, false, debugValueAccess);
            if (list.size() < 4) {
                PoiDebugRenderer.renderTextOverPoi("Owners: " + String.valueOf(list), debugPoiInfo, i, -256);
            } else {
                PoiDebugRenderer.renderTextOverPoi(list.size() + " ticket holders", debugPoiInfo, i, -256);
            }
            ++i;
            List<String> list2 = this.getTicketHolderNames(debugPoiInfo, true, debugValueAccess);
            if (list2.size() < 4) {
                PoiDebugRenderer.renderTextOverPoi("Candidates: " + String.valueOf(list2), debugPoiInfo, i, -23296);
            } else {
                PoiDebugRenderer.renderTextOverPoi(list2.size() + " potential owners", debugPoiInfo, i, -23296);
            }
            ++i;
        }
        PoiDebugRenderer.renderTextOverPoi("Free tickets: " + debugPoiInfo.freeTicketCount(), debugPoiInfo, i, -256);
        PoiDebugRenderer.renderTextOverPoi(debugPoiInfo.poiType().getRegisteredName(), debugPoiInfo, ++i, -1);
    }

    private static void renderTextOverPoi(String string, DebugPoiInfo debugPoiInfo, int i, int j) {
        Gizmos.billboardTextOverBlock((String)string, (BlockPos)debugPoiInfo.pos(), (int)i, (int)j, (float)0.32f);
    }

    private List<String> getTicketHolderNames(DebugPoiInfo debugPoiInfo, boolean bl, DebugValueAccess debugValueAccess) {
        ArrayList<String> list = new ArrayList<String>();
        debugValueAccess.forEachEntity(DebugSubscriptions.BRAINS, (entity, debugBrainDump) -> {
            boolean bl2;
            boolean bl3 = bl2 = bl ? debugBrainDump.hasPotentialPoi(debugPoiInfo.pos()) : debugBrainDump.hasPoi(debugPoiInfo.pos());
            if (bl2) {
                list.add(DebugEntityNameGenerator.getEntityName((UUID)entity.getUUID()));
            }
        });
        return list;
    }
}

