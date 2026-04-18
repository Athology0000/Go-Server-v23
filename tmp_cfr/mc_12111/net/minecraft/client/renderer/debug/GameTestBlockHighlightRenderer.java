/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.Util
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class GameTestBlockHighlightRenderer {
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final float PADDING = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void highlightPos(BlockPos blockPos, BlockPos blockPos2) {
        String string = blockPos2.toShortString();
        this.markers.put(blockPos, new Marker(0x6000FF00, string, Util.getMillis() + 10000L));
    }

    public void clear() {
        this.markers.clear();
    }

    public void emitGizmos() {
        long l = Util.getMillis();
        this.markers.entrySet().removeIf(entry -> l > ((Marker)entry.getValue()).removeAtTime);
        this.markers.forEach((blockPos, marker) -> this.renderMarker((BlockPos)blockPos, (Marker)marker));
    }

    private void renderMarker(BlockPos blockPos, Marker marker) {
        Gizmos.cuboid((BlockPos)blockPos, (float)0.02f, (GizmoStyle)GizmoStyle.fill((int)marker.color()));
        if (!marker.text.isEmpty()) {
            Gizmos.billboardText((String)marker.text, (Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos, (double)0.5, (double)1.2, (double)0.5), (TextGizmo.Style)TextGizmo.Style.whiteAndCentered().withScale(0.16f)).setAlwaysOnTop();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Marker
    extends Record {
        private final int color;
        final String text;
        final long removeAtTime;

        Marker(int i, String string, long l) {
            this.color = i;
            this.text = string;
            this.removeAtTime = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "text", "removeAtTime"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "text", "removeAtTime"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "text", "removeAtTime"}, this, object);
        }

        public int color() {
            return this.color;
        }

        public String text() {
            return this.text;
        }

        public long removeAtTime() {
            return this.removeAtTime;
        }
    }
}

