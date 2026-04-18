/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2675
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2675;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001DB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0010H\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0012J!\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0014\u001a\u00020\u00132\b\b\u0002\u0010\u0016\u001a\u00020\u0015\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0013\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001a\u00a2\u0006\u0004\b\u001c\u0010\u001dJ'\u0010!\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u00172\u0006\u0010 \u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\b!\u0010\"J\u0011\u0010#\u001a\u0004\u0018\u00010\u0013H\u0002\u00a2\u0006\u0004\b#\u0010$J\u0015\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00130\u001aH\u0002\u00a2\u0006\u0004\b%\u0010\u001dJ\u001f\u0010&\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b&\u0010'J\u001f\u0010)\u001a\u00020\u001b2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010(\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010-\u001a\u00020\u00152\u0006\u0010,\u001a\u00020+H\u0002\u00a2\u0006\u0004\b-\u0010.J\u000f\u0010/\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b/\u0010\u0003R\u0014\u00101\u001a\u0002008\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b1\u00102R\u0014\u00103\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0014\u00106\u001a\u0002058\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0014\u00109\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u0014\u0010;\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b;\u0010:R\u0014\u0010<\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b<\u0010=R\u0014\u0010?\u001a\u00020>8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b?\u0010@R\u0014\u0010A\u001a\u00020>8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bA\u0010@R\u0014\u0010B\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bB\u0010=R\u0018\u0010,\u001a\u0004\u0018\u00010+8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b,\u0010C\u00a8\u0006E"}, d2={"Lorg/cobalt/internal/mining/MiningPrecisionTracker;", "", "<init>", "()V", "", "ensureInitialized", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "event", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_2338;", "blockPos", "", "allowTentative", "Lnet/minecraft/class_243;", "getPrecisionPointFor", "(Lnet/minecraft/class_2338;Z)Lnet/minecraft/class_243;", "", "", "buildOverlayRows", "()Ljava/util/List;", "position", "", "confidenceBoost", "pushSample", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_243;I)V", "currentObservedBlock", "()Lnet/minecraft/class_2338;", "observedBlocks", "belongsToBlock", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_243;)Z", "point", "formatLocalPoint", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_243;)Ljava/lang/String;", "Lorg/cobalt/internal/mining/MiningPrecisionTracker$PrecisionSample;", "sample", "isFresh", "(Lorg/cobalt/internal/mining/MiningPrecisionTracker$PrecisionSample;)Z", "clear", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "TAG", "Ljava/lang/String;", "", "SAMPLE_EXPIRY_MS", "J", "", "SAMPLE_RADIUS_SQ", "D", "BLOCK_EPSILON", "MIN_CONFIDENCE", "I", "", "MAX_SPREAD", "F", "EXACT_SPREAD", "MAX_COUNT", "Lorg/cobalt/internal/mining/MiningPrecisionTracker$PrecisionSample;", "PrecisionSample", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningPrecisionTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningPrecisionTracker.kt\norg/cobalt/internal/mining/MiningPrecisionTracker\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,235:1\n296#2,2:236\n1#3:238\n*S KotlinDebug\n*F\n+ 1 MiningPrecisionTracker.kt\norg/cobalt/internal/mining/MiningPrecisionTracker\n*L\n55#1:236,2\n*E\n"})
public final class MiningPrecisionTracker {
    @NotNull
    public static final MiningPrecisionTracker INSTANCE = new MiningPrecisionTracker();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final String TAG = "mining-precision";
    private static final long SAMPLE_EXPIRY_MS = 1200L;
    private static final double SAMPLE_RADIUS_SQ = 0.0324;
    private static final double BLOCK_EPSILON = 0.24;
    private static final int MIN_CONFIDENCE = 2;
    private static final float MAX_SPREAD = 0.35f;
    private static final float EXACT_SPREAD = 0.08f;
    private static final int MAX_COUNT = 6;
    @Nullable
    private static volatile PrecisionSample sample;

    private MiningPrecisionTracker() {
    }

    public final void ensureInitialized() {
    }

    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Incoming event) {
        Object v1;
        class_243 position;
        float spread;
        class_2675 pkt;
        block5: {
            Intrinsics.checkNotNullParameter((Object)event, (String)"event");
            if (!((Boolean)MiningModule.INSTANCE.getPrecisionActive().getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
                return;
            }
            class_2596<?> class_25962 = event.getPacket();
            class_2675 class_26752 = class_25962 instanceof class_2675 ? (class_2675)class_25962 : null;
            if (class_26752 == null) {
                return;
            }
            pkt = class_26752;
            spread = Math.max(Math.abs(pkt.method_11548()), Math.max(Math.abs(pkt.method_11549()), Math.abs(pkt.method_11550())));
            if (spread > 0.35f || pkt.method_11545() > 6) {
                return;
            }
            position = new class_243(pkt.method_11544(), pkt.method_11547(), pkt.method_11546());
            Iterable $this$firstOrNull$iv = this.observedBlocks();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                class_2338 it = (class_2338)element$iv;
                boolean bl = false;
                if (!INSTANCE.belongsToBlock(it, position)) continue;
                v1 = element$iv;
                break block5;
            }
            v1 = null;
        }
        class_2338 class_23382 = v1;
        if (class_23382 == null) {
            return;
        }
        class_2338 blockPos = class_23382;
        int confidenceBoost = pkt.method_11545() == 0 || spread <= 0.08f ? 2 : 1;
        this.pushSample(blockPos, position, confidenceBoost);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)MiningModule.INSTANCE.getPrecisionActive().getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            this.clear();
            return;
        }
        PrecisionSample precisionSample = sample;
        if (precisionSample == null) {
            return;
        }
        PrecisionSample current = precisionSample;
        List<class_2338> observed = this.observedBlocks();
        if (observed.isEmpty() || !observed.contains(current.getBlockPos()) || !this.isFresh(current)) {
            this.clear();
        }
    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        PrecisionSample precisionSample = sample;
        if (precisionSample == null) {
            return;
        }
        PrecisionSample current = precisionSample;
        if (Intrinsics.areEqual((Object)event.getPos(), (Object)current.getBlockPos()) && event.getNewBlock().method_26215()) {
            this.clear();
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = MiningPrecisionTracker.mc.field_1687;
        if (class_6382 == null) {
            MiningPrecisionTracker $this$onRender_u24lambda_u240 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)MiningModule.INSTANCE.getPrecisionActive().getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_2338 class_23382 = this.currentObservedBlock();
        if (class_23382 == null) {
            MiningPrecisionTracker $this$onRender_u24lambda_u241 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_2338 blockPos = class_23382;
        class_243 class_2432 = MiningPrecisionTracker.getPrecisionPointFor$default(this, blockPos, false, 2, null);
        if (class_2432 == null) {
            MiningPrecisionTracker $this$onRender_u24lambda_u242 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_243 point2 = class_2432;
        OverlayRenderEngine.Color pointFill = new OverlayRenderEngine.Color(255, 216, 76, 102);
        OverlayRenderEngine.Color pointOutline = new OverlayRenderEngine.Color(255, 216, 76, 255);
        OverlayRenderEngine.Color blockOutline = new OverlayRenderEngine.Color(45, 226, 255, 255);
        double half = 0.07;
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
        OverlayRenderEngine.INSTANCE.outlineBlockColor((class_1937)level2, blockPos, blockOutline, 2, TAG, 2.0f);
        OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, point2.field_1352 - half, point2.field_1351 - half, point2.field_1350 - half, point2.field_1352 + half, point2.field_1351 + half, point2.field_1350 + half, pointFill, pointOutline, 1.8f, 2, TAG, false, 4096, null);
        class_746 class_7462 = MiningPrecisionTracker.mc.field_1724;
        if (class_7462 != null && (class_7462 = class_7462.method_33571()) != null) {
            class_746 eye = class_7462;
            boolean bl = false;
            OverlayRenderEngine.addLine$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, eye.field_1352, eye.field_1351, eye.field_1350, point2.field_1352, point2.field_1351, point2.field_1350, pointOutline, 1.3f, 2, TAG, false, 2048, null);
        }
        OverlayRenderEngine.INSTANCE.render(event.getContext());
    }

    @Nullable
    public final class_243 getPrecisionPointFor(@NotNull class_2338 blockPos, boolean allowTentative) {
        Intrinsics.checkNotNullParameter((Object)blockPos, (String)"blockPos");
        PrecisionSample precisionSample = sample;
        if (precisionSample == null) {
            return null;
        }
        PrecisionSample current = precisionSample;
        if (!Intrinsics.areEqual((Object)current.getBlockPos(), (Object)blockPos)) {
            return null;
        }
        if (!this.isFresh(current)) {
            return null;
        }
        if (!allowTentative && current.getConfidence() < 2) {
            return null;
        }
        return current.getPosition();
    }

    public static /* synthetic */ class_243 getPrecisionPointFor$default(MiningPrecisionTracker miningPrecisionTracker, class_2338 class_23382, boolean bl, int n, Object object) {
        if ((n & 2) != 0) {
            bl = false;
        }
        return miningPrecisionTracker.getPrecisionPointFor(class_23382, bl);
    }

    @NotNull
    public final List<String> buildOverlayRows() {
        class_243 class_2432;
        class_2338 blockPos;
        if (!((Boolean)MiningModule.INSTANCE.getPrecisionActive().getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            return CollectionsKt.emptyList();
        }
        class_2338 class_23382 = blockPos = this.currentObservedBlock();
        if (class_23382 != null) {
            class_2338 p0 = class_23382;
            boolean bl = false;
            class_2432 = MiningPrecisionTracker.getPrecisionPointFor$default(this, p0, false, 2, null);
        } else {
            class_2432 = null;
        }
        class_243 point2 = class_2432;
        String status = point2 != null ? "Ready" : "Searching";
        Object[] objectArray = new String[]{"Precision: " + status};
        List rows = CollectionsKt.mutableListOf((Object[])objectArray);
        if (blockPos != null && point2 != null) {
            rows.add("Prec Pt:  " + this.formatLocalPoint(blockPos, point2));
        }
        return rows;
    }

    private final void pushSample(class_2338 blockPos, class_243 position, int confidenceBoost) {
        MiningModule.INSTANCE.getPrecisionActive().setValue(true);
        long now = System.currentTimeMillis();
        PrecisionSample current = sample;
        if (current == null || !Intrinsics.areEqual((Object)current.getBlockPos(), (Object)blockPos) || !this.isFresh(current)) {
            sample = new PrecisionSample(blockPos, position, confidenceBoost, now);
            return;
        }
        boolean sameCluster = current.getPosition().method_1025(position) <= 0.0324;
        class_243 class_2432 = sameCluster ? current.getPosition().method_1021(0.65).method_1019(position.method_1021(0.35)) : position;
        Intrinsics.checkNotNull((Object)class_2432);
        class_243 nextPosition = class_2432;
        int nextConfidence = sameCluster ? RangesKt.coerceAtMost((int)(current.getConfidence() + confidenceBoost), (int)6) : confidenceBoost;
        sample = new PrecisionSample(blockPos, nextPosition, nextConfidence, now);
    }

    private final class_2338 currentObservedBlock() {
        List<class_2338> observed = this.observedBlocks();
        PrecisionSample current = sample;
        if (current != null && this.isFresh(current) && observed.contains(current.getBlockPos())) {
            return current.getBlockPos();
        }
        return (class_2338)CollectionsKt.firstOrNull(observed);
    }

    private final List<class_2338> observedBlocks() {
        LinkedHashSet<class_2338> observed;
        block2: {
            class_2338 p0;
            observed = new LinkedHashSet<class_2338>();
            class_2338 class_23382 = MiningModule.INSTANCE.getDetectedBlockPos();
            if (class_23382 != null) {
                p0 = class_23382;
                boolean bl = false;
                observed.add(p0);
            }
            class_2338 class_23383 = MiningMacroModule.INSTANCE.getPrecisionTargetBlock();
            if (class_23383 != null) {
                p0 = class_23383;
                boolean bl = false;
                observed.add(p0);
            }
            class_2338 class_23384 = MiningMacroModule.INSTANCE.getTrackedTargetBlock();
            if (class_23384 == null) break block2;
            p0 = class_23384;
            boolean bl = false;
            observed.add(p0);
        }
        return CollectionsKt.toList((Iterable)observed);
    }

    private final boolean belongsToBlock(class_2338 blockPos, class_243 position) {
        return position.field_1352 >= (double)blockPos.method_10263() - 0.24 && position.field_1352 <= (double)blockPos.method_10263() + 1.0 + 0.24 && position.field_1351 >= (double)blockPos.method_10264() - 0.24 && position.field_1351 <= (double)blockPos.method_10264() + 1.0 + 0.24 && position.field_1350 >= (double)blockPos.method_10260() - 0.24 && position.field_1350 <= (double)blockPos.method_10260() + 1.0 + 0.24;
    }

    private final String formatLocalPoint(class_2338 blockPos, class_243 point2) {
        Locale locale = Locale.US;
        String string = "%.2f %.2f %.2f";
        Object[] objectArray = new Object[]{point2.field_1352 - (double)blockPos.method_10263(), point2.field_1351 - (double)blockPos.method_10264(), point2.field_1350 - (double)blockPos.method_10260()};
        String string2 = String.format(locale, string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        return string2;
    }

    private final boolean isFresh(PrecisionSample sample) {
        return System.currentTimeMillis() - sample.getLastSeenMs() <= 1200L;
    }

    private final void clear() {
        sample = null;
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J8\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0006H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u0011J\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u000fR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\"\u001a\u0004\b#\u0010\u0011R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010$\u001a\u0004\b%\u0010\u0013\u00a8\u0006&"}, d2={"Lorg/cobalt/internal/mining/MiningPrecisionTracker$PrecisionSample;", "", "Lnet/minecraft/class_2338;", "blockPos", "Lnet/minecraft/class_243;", "position", "", "confidence", "", "lastSeenMs", "<init>", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_243;IJ)V", "component1", "()Lnet/minecraft/class_2338;", "component2", "()Lnet/minecraft/class_243;", "component3", "()I", "component4", "()J", "copy", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_243;IJ)Lorg/cobalt/internal/mining/MiningPrecisionTracker$PrecisionSample;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Lnet/minecraft/class_2338;", "getBlockPos", "Lnet/minecraft/class_243;", "getPosition", "I", "getConfidence", "J", "getLastSeenMs", "cobalt"})
    private static final class PrecisionSample {
        @NotNull
        private final class_2338 blockPos;
        @NotNull
        private final class_243 position;
        private final int confidence;
        private final long lastSeenMs;

        public PrecisionSample(@NotNull class_2338 blockPos, @NotNull class_243 position, int confidence, long lastSeenMs) {
            Intrinsics.checkNotNullParameter((Object)blockPos, (String)"blockPos");
            Intrinsics.checkNotNullParameter((Object)position, (String)"position");
            this.blockPos = blockPos;
            this.position = position;
            this.confidence = confidence;
            this.lastSeenMs = lastSeenMs;
        }

        @NotNull
        public final class_2338 getBlockPos() {
            return this.blockPos;
        }

        @NotNull
        public final class_243 getPosition() {
            return this.position;
        }

        public final int getConfidence() {
            return this.confidence;
        }

        public final long getLastSeenMs() {
            return this.lastSeenMs;
        }

        @NotNull
        public final class_2338 component1() {
            return this.blockPos;
        }

        @NotNull
        public final class_243 component2() {
            return this.position;
        }

        public final int component3() {
            return this.confidence;
        }

        public final long component4() {
            return this.lastSeenMs;
        }

        @NotNull
        public final PrecisionSample copy(@NotNull class_2338 blockPos, @NotNull class_243 position, int confidence, long lastSeenMs) {
            Intrinsics.checkNotNullParameter((Object)blockPos, (String)"blockPos");
            Intrinsics.checkNotNullParameter((Object)position, (String)"position");
            return new PrecisionSample(blockPos, position, confidence, lastSeenMs);
        }

        public static /* synthetic */ PrecisionSample copy$default(PrecisionSample precisionSample, class_2338 class_23382, class_243 class_2432, int n, long l, int n2, Object object) {
            if ((n2 & 1) != 0) {
                class_23382 = precisionSample.blockPos;
            }
            if ((n2 & 2) != 0) {
                class_2432 = precisionSample.position;
            }
            if ((n2 & 4) != 0) {
                n = precisionSample.confidence;
            }
            if ((n2 & 8) != 0) {
                l = precisionSample.lastSeenMs;
            }
            return precisionSample.copy(class_23382, class_2432, n, l);
        }

        @NotNull
        public String toString() {
            return "PrecisionSample(blockPos=" + this.blockPos + ", position=" + this.position + ", confidence=" + this.confidence + ", lastSeenMs=" + this.lastSeenMs + ")";
        }

        public int hashCode() {
            int result = this.blockPos.hashCode();
            result = result * 31 + this.position.hashCode();
            result = result * 31 + Integer.hashCode(this.confidence);
            result = result * 31 + Long.hashCode(this.lastSeenMs);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PrecisionSample)) {
                return false;
            }
            PrecisionSample precisionSample = (PrecisionSample)other;
            if (!Intrinsics.areEqual((Object)this.blockPos, (Object)precisionSample.blockPos)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.position, (Object)precisionSample.position)) {
                return false;
            }
            if (this.confidence != precisionSample.confidence) {
                return false;
            }
            return this.lastSeenMs == precisionSample.lastSeenMs;
        }
    }
}

