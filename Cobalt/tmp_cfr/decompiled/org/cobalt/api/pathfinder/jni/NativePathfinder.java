/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.jni;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.pathfinder.jni.ActionType;
import org.cobalt.api.pathfinder.jni.MovementProfile;
import org.cobalt.api.pathfinder.jni.PathCommand;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.jni.WorldBufferResult;
import org.cobalt.api.pathfinder.jni.WorldBufferSerializer;
import org.cobalt.pathfinder.NativePathfinderBridge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u0013\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J%\u0010\u000b\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ-\u0010\u000e\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ%\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\u0004\b\u0016\u0010\u0017J\r\u0010\u0018\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0003J\r\u0010\u0019\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0019\u0010\u0003J\u000f\u0010\u001b\u001a\u0004\u0018\u00010\u001a\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u000f\u0010\u001d\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u0003R\u0016\u0010\u001f\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0011\u0010!\u001a\u00020\u00128F\u00a2\u0006\u0006\u001a\u0004\b!\u0010\"R0\u0010&\u001a\b\u0012\u0004\u0012\u00020$0#2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020$0#8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b(\u0010)R\u0016\u0010+\u001a\u00020*8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0016\u0010-\u001a\u00020\u00128\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b-\u0010.R\u0011\u00101\u001a\u00020*8F\u00a2\u0006\u0006\u001a\u0004\b/\u00100\u00a8\u00062"}, d2={"Lorg/cobalt/api/pathfinder/jni/NativePathfinder;", "", "<init>", "()V", "", "init", "destroy", "", "x", "y", "z", "setTarget", "(DDD)V", "radius", "setTargetWithRadius", "(DDDD)V", "", "waypoints", "", "loop", "Lorg/cobalt/api/pathfinder/jni/MovementProfile;", "profile", "setRoute", "([DZLorg/cobalt/api/pathfinder/jni/MovementProfile;)V", "stop", "onLevelChange", "Lorg/cobalt/api/pathfinder/jni/PathCommand;", "tick", "()Lorg/cobalt/api/pathfinder/jni/PathCommand;", "refreshPathNodes", "", "handle", "J", "isInitialized", "()Z", "", "Lnet/minecraft/class_243;", "value", "cachedPathNodes", "Ljava/util/List;", "getCachedPathNodes", "()Ljava/util/List;", "Lorg/cobalt/api/pathfinder/jni/PathStatus;", "lastTickStatus", "Lorg/cobalt/api/pathfinder/jni/PathStatus;", "prevJump", "Z", "getStatus", "()Lorg/cobalt/api/pathfinder/jni/PathStatus;", "status", "cobalt"})
@SourceDebugExtension(value={"SMAP\nNativePathfinder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NativePathfinder.kt\norg/cobalt/api/pathfinder/jni/NativePathfinder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,161:1\n1#2:162\n*E\n"})
public final class NativePathfinder {
    @NotNull
    public static final NativePathfinder INSTANCE = new NativePathfinder();
    private static long handle;
    @NotNull
    private static List<? extends class_243> cachedPathNodes;
    @NotNull
    private static PathStatus lastTickStatus;
    private static boolean prevJump;

    private NativePathfinder() {
    }

    public final boolean isInitialized() {
        return handle != 0L;
    }

    @NotNull
    public final List<class_243> getCachedPathNodes() {
        return cachedPathNodes;
    }

    public final void init() {
        if (handle != 0L) {
            return;
        }
        handle = NativePathfinderBridge.createEngine();
    }

    public final void destroy() {
        if (handle == 0L) {
            return;
        }
        NativePathfinderBridge.destroyEngine(handle);
        handle = 0L;
    }

    public final void setTarget(double x, double y, double z) {
        if (handle == 0L) {
            return;
        }
        NativePathfinderBridge.setTarget(handle, x, y, z);
    }

    public final void setTargetWithRadius(double x, double y, double z, double radius) {
        if (handle == 0L) {
            return;
        }
        NativePathfinderBridge.setTargetWithRadius(handle, x, y, z, radius);
    }

    public final void setRoute(@NotNull double[] waypoints, boolean loop, @NotNull MovementProfile profile) {
        Intrinsics.checkNotNullParameter((Object)waypoints, (String)"waypoints");
        Intrinsics.checkNotNullParameter((Object)((Object)profile), (String)"profile");
        if (handle == 0L) {
            return;
        }
        NativePathfinderBridge.setRoute(handle, waypoints, loop, profile.ordinal());
    }

    public final void stop() {
        if (handle == 0L) {
            return;
        }
        NativePathfinderBridge.stop(handle);
        prevJump = false;
    }

    public final void onLevelChange() {
        WorldBufferSerializer.INSTANCE.invalidate();
        prevJump = false;
    }

    @NotNull
    public final PathStatus getStatus() {
        Object object;
        if (handle == 0L) {
            return PathStatus.IDLE;
        }
        int ordinal = NativePathfinderBridge.getStatus(handle);
        List list = (List)PathStatus.getEntries();
        boolean bl = 0 <= ordinal ? ordinal < list.size() : false;
        if (bl) {
            object = list.get(ordinal);
        } else {
            int it = ordinal;
            boolean bl2 = false;
            object = PathStatus.FAILED;
        }
        return (PathStatus)((Object)object);
    }

    @Nullable
    public final PathCommand tick() {
        Object object;
        boolean jumpRaw;
        Object object2;
        if (handle == 0L) {
            return null;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_746 class_7462 = mc.field_1724;
        if (class_7462 == null) {
            return null;
        }
        class_746 player = class_7462;
        WorldBufferResult worldBufferResult = WorldBufferSerializer.INSTANCE.serialize(mc);
        if (worldBufferResult == null) {
            return null;
        }
        WorldBufferResult world = worldBufferResult;
        int[] r = NativePathfinderBridge.update(handle, world.getBuf(), world.getBx(), world.getBy(), world.getBz(), player.method_23317(), player.method_23318(), player.method_23321(), player.method_36454(), player.method_36455(), player.method_24828());
        int statusOrdinal = r[7];
        int actionOrdinal = r[8];
        List list = (List)PathStatus.getEntries();
        boolean bl = 0 <= statusOrdinal ? statusOrdinal < list.size() : false;
        if (bl) {
            object2 = list.get(statusOrdinal);
        } else {
            int it = statusOrdinal;
            boolean bl2 = false;
            object2 = PathStatus.FAILED;
        }
        PathStatus parsedStatus = (PathStatus)((Object)object2);
        if (parsedStatus == PathStatus.EXECUTING && lastTickStatus != PathStatus.EXECUTING) {
            this.refreshPathNodes();
        } else if ((parsedStatus == PathStatus.IDLE || parsedStatus == PathStatus.ARRIVED || parsedStatus == PathStatus.FAILED) && !((Collection)cachedPathNodes).isEmpty()) {
            cachedPathNodes = CollectionsKt.emptyList();
        }
        lastTickStatus = parsedStatus;
        if (parsedStatus == PathStatus.IDLE || parsedStatus == PathStatus.PLANNING || parsedStatus == PathStatus.ARRIVED || parsedStatus == PathStatus.FAILED) {
            prevJump = false;
            return null;
        }
        boolean bl3 = jumpRaw = r[2] != 0;
        if (jumpRaw && player.method_24828()) {
            prevJump = false;
        }
        boolean jumpPulse = jumpRaw && !prevJump;
        prevJump = jumpRaw;
        boolean bl4 = r[0] != 0;
        boolean bl5 = r[1] != 0;
        boolean bl6 = jumpPulse;
        boolean bl7 = r[3] != 0;
        boolean bl8 = r[4] != 0;
        float f = Float.intBitsToFloat(r[5]);
        float f2 = Float.intBitsToFloat(r[6]);
        PathStatus pathStatus = parsedStatus;
        List list2 = (List)ActionType.getEntries();
        boolean bl9 = 0 <= actionOrdinal ? actionOrdinal < list2.size() : false;
        if (bl9) {
            object = list2.get(actionOrdinal);
        } else {
            int n = actionOrdinal;
            PathStatus pathStatus2 = pathStatus;
            float f3 = f2;
            float f4 = f;
            boolean bl10 = bl8;
            boolean bl11 = bl7;
            boolean bl12 = bl6;
            boolean bl13 = bl5;
            boolean bl14 = bl4;
            boolean bl15 = false;
            ActionType actionType = ActionType.WALK;
            bl4 = bl14;
            bl5 = bl13;
            bl6 = bl12;
            bl7 = bl11;
            bl8 = bl10;
            f = f4;
            f2 = f3;
            pathStatus = pathStatus2;
            object = actionType;
        }
        float f5 = Float.intBitsToFloat(r[9]);
        ActionType actionType = (ActionType)((Object)object);
        PathStatus pathStatus3 = pathStatus;
        float f6 = f2;
        float f7 = f;
        boolean bl16 = bl8;
        boolean bl17 = bl7;
        boolean bl18 = bl6;
        boolean bl19 = bl5;
        boolean bl20 = bl4;
        return new PathCommand(bl20, bl19, bl18, bl17, bl16, f7, f6, pathStatus3, actionType, f5);
    }

    private final void refreshPathNodes() {
        if (handle == 0L) {
            cachedPathNodes = CollectionsKt.emptyList();
            return;
        }
        float[] raw = NativePathfinderBridge.getPathNodes(handle);
        ArrayList<class_243> result = new ArrayList<class_243>(raw.length / 3);
        int i = 0;
        while (i + 2 < raw.length) {
            result.add(new class_243((double)raw[i], (double)raw[i + 1], (double)raw[i + 2]));
            i += 3;
        }
        cachedPathNodes = result;
    }

    static {
        cachedPathNodes = CollectionsKt.emptyList();
        lastTickStatus = PathStatus.IDLE;
    }
}

