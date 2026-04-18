/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2362
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2560
 *  net.minecraft.class_2577
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.visual;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2362;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2560;
import net.minecraft.class_2577;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.internal.helper.ClientGlowEspManager;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001hB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\u0003J\u001b\u0010\f\u001a\u00020\u00062\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\nH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J!\u0010\u0017\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0019\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u001f\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u001d2\u0006\u0010\u0019\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ'\u0010\"\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010!\u001a\u00020 2\u0006\u0010\u0019\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b\"\u0010#J'\u0010)\u001a\u00020\u00102\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020\u00132\u0006\u0010(\u001a\u00020'H\u0002\u00a2\u0006\u0004\b)\u0010*J?\u0010/\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u00132\u0006\u0010(\u001a\u00020'2\u0006\u0010,\u001a\u00020+2\u0006\u0010-\u001a\u00020'2\u0006\u0010.\u001a\u00020'H\u0002\u00a2\u0006\u0004\b/\u00100J'\u00103\u001a\u0002022\u0006\u00101\u001a\u00020'2\u0006\u0010-\u001a\u00020'2\u0006\u0010.\u001a\u00020'H\u0002\u00a2\u0006\u0004\b3\u00104J'\u00109\u001a\u0002052\u0006\u00106\u001a\u0002052\u0006\u00107\u001a\u0002052\u0006\u00108\u001a\u00020'H\u0002\u00a2\u0006\u0004\b9\u0010:J)\u0010<\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u00132\u0006\u0010;\u001a\u000205H\u0002\u00a2\u0006\u0004\b<\u0010=J\u0017\u0010?\u001a\u00020\u00102\u0006\u0010>\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b?\u0010@J\u0017\u0010A\u001a\u00020\u00102\u0006\u0010>\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\bA\u0010@J\u0017\u0010C\u001a\u0002022\u0006\u0010B\u001a\u000205H\u0002\u00a2\u0006\u0004\bC\u0010DR\u0014\u0010F\u001a\u00020E8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0014\u0010H\u001a\u0002058\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bH\u0010IR\u0014\u0010J\u001a\u00020'8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bJ\u0010KR\u0014\u0010L\u001a\u0002058\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bL\u0010IR\u001a\u0010N\u001a\b\u0012\u0004\u0012\u00020E0M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010OR\u001a\u0010P\u001a\b\u0012\u0004\u0012\u00020E0M8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010OR\u0014\u0010R\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010SR\u0014\u0010U\u001a\u00020T8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010VR\u0014\u0010W\u001a\u00020T8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010VR\u0014\u0010Y\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0014\u0010[\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010ZR\u0014\u0010]\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010^R\u0014\u0010_\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010^R\u0014\u0010`\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010SR\u0014\u0010a\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010SR\u0014\u0010b\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010SR\u0014\u0010c\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010^R\u0014\u0010d\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u0010^R\u0014\u0010e\u001a\u00020\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010^R\u0014\u0010f\u001a\u00020E8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bf\u0010GR\u0014\u0010g\u001a\u0002058\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bg\u0010I\u00a8\u0006i"}, d2={"Lorg/cobalt/internal/visual/WitherImpactOverlayModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "clearOverlay", "Lnet/minecraft/class_638;", "level", "clearMobEsp", "(Lnet/minecraft/class_638;)V", "Lnet/minecraft/class_1799;", "stack", "", "isWitherImpactBlade", "(Lnet/minecraft/class_1799;)Z", "Lnet/minecraft/class_243;", "startPos", "direction", "Lnet/minecraft/class_2338;", "findTargetBlock", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;)Lnet/minecraft/class_2338;", "targetBlock", "Lorg/cobalt/internal/visual/WitherImpactOverlayModule$MarkerBounds;", "computeMarkerBounds", "(Lnet/minecraft/class_2338;)Lorg/cobalt/internal/visual/WitherImpactOverlayModule$MarkerBounds;", "Lnet/minecraft/class_1937;", "renderBoomRadius", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V", "Lnet/minecraft/class_746;", "player", "syncMobEsp", "(Lnet/minecraft/class_638;Lnet/minecraft/class_746;Lnet/minecraft/class_2338;)V", "Lnet/minecraft/class_1309;", "entity", "center", "", "radius", "isEntityInsideRadius", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_243;D)Z", "", "lineWidth", "phase", "alphaScale", "renderRing", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_243;DFDD)V", "position", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "animatedGradientColor", "(DDD)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "", "start", "end", "t", "lerpChannel", "(IID)I", "distance", "raycastDisplacement", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;I)Lnet/minecraft/class_243;", "pos", "canTeleportThrough", "(Lnet/minecraft/class_2338;)Z", "isBlockFloor", "argb", "toOverlayColor", "(I)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "", "TAG", "Ljava/lang/String;", "WITHER_IMPACT_RANGE", "I", "DEFAULT_BOOM_RADIUS", "D", "RING_SEGMENTS", "", "witherBladeIds", "Ljava/util/Set;", "witherBladeNameHints", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "renderMode", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "markerSize", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "overlayColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "gradientEndColor", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "outlineWidth", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "fillOpacity", "showWhenInAir", "showBoomRadius", "mobEsp", "boomRadius", "ringLineWidth", "animationSpeed", "HYPERION_MOB_ESP_SCOPE", "HYPERION_MOB_ESP_PRIORITY", "MarkerBounds", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWitherImpactOverlayModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WitherImpactOverlayModule.kt\norg/cobalt/internal/visual/WitherImpactOverlayModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,457:1\n1807#2,3:458\n*S KotlinDebug\n*F\n+ 1 WitherImpactOverlayModule.kt\norg/cobalt/internal/visual/WitherImpactOverlayModule\n*L\n241#1:458,3\n*E\n"})
public final class WitherImpactOverlayModule
extends Module {
    @NotNull
    public static final WitherImpactOverlayModule INSTANCE = new WitherImpactOverlayModule();
    @NotNull
    private static final String TAG = "wither-impact-overlay";
    private static final int WITHER_IMPACT_RANGE = 10;
    private static final double DEFAULT_BOOM_RADIUS = 6.0;
    private static final int RING_SEGMENTS = 96;
    @NotNull
    private static final Set<String> witherBladeIds;
    @NotNull
    private static final Set<String> witherBladeNameHints;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final ModeSetting renderMode;
    @NotNull
    private static final ModeSetting markerSize;
    @NotNull
    private static final ColorSetting overlayColor;
    @NotNull
    private static final ColorSetting gradientEndColor;
    @NotNull
    private static final SliderSetting outlineWidth;
    @NotNull
    private static final SliderSetting fillOpacity;
    @NotNull
    private static final CheckboxSetting showWhenInAir;
    @NotNull
    private static final CheckboxSetting showBoomRadius;
    @NotNull
    private static final CheckboxSetting mobEsp;
    @NotNull
    private static final SliderSetting boomRadius;
    @NotNull
    private static final SliderSetting ringLineWidth;
    @NotNull
    private static final SliderSetting animationSpeed;
    @NotNull
    private static final String HYPERION_MOB_ESP_SCOPE = "hyp_mob_esp";
    private static final int HYPERION_MOB_ESP_PRIORITY = 20;

    private WitherImpactOverlayModule() {
        super("Wither Impact Overlay");
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        class_746 player;
        class_638 level2;
        block16: {
            block15: {
                Intrinsics.checkNotNullParameter((Object)event, (String)"event");
                class_310 class_3102 = class_310.method_1551();
                Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
                class_310 mc = class_3102;
                class_638 class_6382 = mc.field_1687;
                if (class_6382 == null) {
                    WitherImpactOverlayModule $this$onRender_u24lambda_u240 = this;
                    boolean bl = false;
                    $this$onRender_u24lambda_u240.clearOverlay();
                    WitherImpactOverlayModule.clearMobEsp$default($this$onRender_u24lambda_u240, null, 1, null);
                    return;
                }
                level2 = class_6382;
                class_746 class_7462 = mc.field_1724;
                if (class_7462 == null) {
                    WitherImpactOverlayModule $this$onRender_u24lambda_u241 = this;
                    boolean bl = false;
                    $this$onRender_u24lambda_u241.clearOverlay();
                    WitherImpactOverlayModule.clearMobEsp$default($this$onRender_u24lambda_u241, null, 1, null);
                    return;
                }
                player = class_7462;
                if (!((Boolean)enabled.getValue()).booleanValue()) break block15;
                class_1799 class_17992 = player.method_6047();
                Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
                if (this.isWitherImpactBlade(class_17992)) break block16;
            }
            this.clearOverlay();
            WitherImpactOverlayModule.clearMobEsp$default(this, null, 1, null);
            return;
        }
        class_243 class_2432 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 class_2433 = player.method_5828(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getViewVector(...)");
        class_2338 class_23382 = this.findTargetBlock(class_2432, class_2433);
        if (class_23382 == null) {
            WitherImpactOverlayModule $this$onRender_u24lambda_u242 = this;
            boolean bl = false;
            $this$onRender_u24lambda_u242.clearOverlay();
            WitherImpactOverlayModule.clearMobEsp$default($this$onRender_u24lambda_u242, null, 1, null);
            return;
        }
        class_2338 targetBlock = class_23382;
        if (!level2.method_8621().method_11952(targetBlock)) {
            this.clearOverlay();
            WitherImpactOverlayModule.clearMobEsp$default(this, null, 1, null);
            return;
        }
        if (!((Boolean)showWhenInAir.getValue()).booleanValue() && level2.method_8320(targetBlock).method_26215()) {
            this.clearOverlay();
            WitherImpactOverlayModule.clearMobEsp$default(this, null, 1, null);
            return;
        }
        this.clearOverlay();
        OverlayRenderEngine.Color outline = this.toOverlayColor(overlayColor.getValue());
        OverlayRenderEngine.Color fill = outline.withAlpha(RangesKt.coerceIn((int)((int)(((Number)fillOpacity.getValue()).doubleValue() * 255.0)), (int)0, (int)255));
        MarkerBounds bounds = this.computeMarkerBounds(targetBlock);
        float lineWidth = (float)((Number)outlineWidth.getValue()).doubleValue();
        switch (((Number)renderMode.getValue()).intValue()) {
            case 0: {
                OverlayRenderEngine.INSTANCE.addBox((class_1937)level2, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ(), bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ(), null, outline, lineWidth, 2, TAG, true);
                break;
            }
            case 1: {
                OverlayRenderEngine.INSTANCE.addBox((class_1937)level2, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ(), bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ(), fill, null, lineWidth, 2, TAG, true);
                break;
            }
            default: {
                OverlayRenderEngine.INSTANCE.addBox((class_1937)level2, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ(), bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ(), fill, outline, lineWidth, 2, TAG, true);
            }
        }
        if (((Boolean)showBoomRadius.getValue()).booleanValue()) {
            this.renderBoomRadius((class_1937)level2, targetBlock);
        }
        OverlayRenderEngine.INSTANCE.render(event.getContext());
        if (((Boolean)mobEsp.getValue()).booleanValue()) {
            this.syncMobEsp(level2, player, targetBlock);
        } else {
            this.clearMobEsp(level2);
        }
    }

    private final void clearOverlay() {
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
    }

    private final void clearMobEsp(class_638 level2) {
        ClientGlowEspManager.INSTANCE.clear(HYPERION_MOB_ESP_SCOPE, level2);
    }

    static /* synthetic */ void clearMobEsp$default(WitherImpactOverlayModule witherImpactOverlayModule, class_638 class_6382, int n, Object object) {
        if ((n & 1) != 0) {
            class_6382 = class_310.method_1551().field_1687;
        }
        witherImpactOverlayModule.clearMobEsp(class_6382);
    }

    private final boolean isWitherImpactBlade(class_1799 stack) {
        boolean bl;
        block5: {
            if (stack.method_7960()) {
                return false;
            }
            String id = SkyblockItemUtilsKt.getSkyblockId(stack);
            if (witherBladeIds.contains(id)) {
                return true;
            }
            String string = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String string2 = string;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            String name = string3;
            Iterable $this$any$iv = witherBladeNameHints;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    CharSequence p0 = (CharSequence)element$iv;
                    boolean bl2 = false;
                    if (!StringsKt.contains$default((CharSequence)name, (CharSequence)p0, (boolean)false, (int)2, null)) continue;
                    bl = true;
                    break block5;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final class_2338 findTargetBlock(class_243 startPos, class_243 direction) {
        class_243 class_2432 = this.raycastDisplacement(startPos, direction, 10);
        if (class_2432 == null) {
            class_243 class_2433 = direction.method_1021(10.0);
            class_2432 = class_2433;
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"scale(...)");
        }
        class_243 displacement = class_2432;
        return class_2338.method_49638((class_2374)((class_2374)startPos.method_1019(displacement))).method_10074();
    }

    private final MarkerBounds computeMarkerBounds(class_2338 targetBlock) {
        MarkerBounds markerBounds;
        double pad = 0.002;
        if (((Number)markerSize.getValue()).intValue() == 1) {
            double centerX = (double)targetBlock.method_10263() + 0.5;
            double centerZ = (double)targetBlock.method_10260() + 0.5;
            double halfWidth = 0.3 + pad;
            markerBounds = new MarkerBounds(centerX - halfWidth, (double)targetBlock.method_10264() - pad, centerZ - halfWidth, centerX + halfWidth, (double)targetBlock.method_10264() + 1.8 + pad, centerZ + halfWidth);
        } else {
            markerBounds = new MarkerBounds((double)targetBlock.method_10263() - pad, (double)targetBlock.method_10264() - pad, (double)targetBlock.method_10260() - pad, (double)targetBlock.method_10263() + 1.0 + pad, (double)targetBlock.method_10264() + 1.0 + pad, (double)targetBlock.method_10260() + 1.0 + pad);
        }
        return markerBounds;
    }

    private final void renderBoomRadius(class_1937 level2, class_2338 targetBlock) {
        double timeSeconds = (double)System.currentTimeMillis() / 1000.0;
        double speed = ((Number)animationSpeed.getValue()).doubleValue();
        double phase = timeSeconds * speed * 0.22 % 1.0;
        double pulse = 0.72 + 0.28 * ((Math.sin(timeSeconds * speed * Math.PI * 1.9) + 1.0) * 0.5);
        double radius = ((Number)boomRadius.getValue()).doubleValue();
        class_243 center = new class_243((double)targetBlock.method_10263() + 0.5, (double)targetBlock.method_10264() + 0.05, (double)targetBlock.method_10260() + 0.5);
        float width = (float)((Number)ringLineWidth.getValue()).doubleValue();
        this.renderRing(level2, center, radius, width, phase, pulse);
    }

    private final void syncMobEsp(class_638 level2, class_746 player, class_2338 targetBlock) {
        double radius = ((Number)boomRadius.getValue()).doubleValue();
        class_243 center = new class_243((double)targetBlock.method_10263() + 0.5, (double)targetBlock.method_10264() + 0.9, (double)targetBlock.method_10260() + 0.5);
        Iterable iterable = level2.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        List targets = SequencesKt.toList((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)SequencesKt.mapNotNull((Sequence)CollectionsKt.asSequence((Iterable)iterable), WitherImpactOverlayModule::syncMobEsp$lambda$0), arg_0 -> WitherImpactOverlayModule.syncMobEsp$lambda$1(player, center, radius, arg_0)), WitherImpactOverlayModule::syncMobEsp$lambda$2));
        ClientGlowEspManager.INSTANCE.sync(HYPERION_MOB_ESP_SCOPE, level2, targets);
    }

    private final boolean isEntityInsideRadius(class_1309 entity, class_243 center, double radius) {
        class_238 class_2383 = entity.method_5829();
        Intrinsics.checkNotNullExpressionValue((Object)class_2383, (String)"getBoundingBox(...)");
        class_238 box = class_2383;
        double closestX = RangesKt.coerceIn((double)center.field_1352, (double)box.field_1323, (double)box.field_1320);
        double closestY = RangesKt.coerceIn((double)center.field_1351, (double)box.field_1322, (double)box.field_1325);
        double closestZ = RangesKt.coerceIn((double)center.field_1350, (double)box.field_1321, (double)box.field_1324);
        double dx = closestX - center.field_1352;
        double dy = closestY - center.field_1351;
        double dz = closestZ - center.field_1350;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    private final void renderRing(class_1937 level2, class_243 center, double radius, float lineWidth, double phase, double alphaScale) {
        for (int segment = 0; segment < 96; ++segment) {
            double t0 = (double)segment / 96.0;
            double t1 = (double)(segment + 1) / 96.0;
            double angle0 = t0 * Math.PI * 2.0;
            double angle1 = t1 * Math.PI * 2.0;
            class_243 start = new class_243(center.field_1352 + Math.cos(angle0) * radius, center.field_1351, center.field_1350 + Math.sin(angle0) * radius);
            class_243 end = new class_243(center.field_1352 + Math.cos(angle1) * radius, center.field_1351, center.field_1350 + Math.sin(angle1) * radius);
            OverlayRenderEngine.Color color = this.animatedGradientColor((t0 + t1) * 0.5, phase, alphaScale);
            OverlayRenderEngine.INSTANCE.addLine(level2, start.field_1352, start.field_1351, start.field_1350, end.field_1352, end.field_1351, end.field_1350, color, lineWidth, 2, TAG, true);
        }
    }

    private final OverlayRenderEngine.Color animatedGradientColor(double position, double phase, double alphaScale) {
        double mix = 0.5 + 0.5 * Math.cos((position + phase) % 1.0 * Math.PI * 2.0);
        OverlayRenderEngine.Color start = this.toOverlayColor(overlayColor.getValue());
        OverlayRenderEngine.Color end = this.toOverlayColor(gradientEndColor.getValue());
        double alphaMultiplier = RangesKt.coerceIn((double)alphaScale, (double)0.0, (double)1.0);
        return new OverlayRenderEngine.Color(this.lerpChannel(start.getR(), end.getR(), mix), this.lerpChannel(start.getG(), end.getG(), mix), this.lerpChannel(start.getB(), end.getB(), mix), RangesKt.coerceIn((int)((int)((double)this.lerpChannel(start.getA(), end.getA(), mix) * alphaMultiplier)), (int)0, (int)255));
    }

    private final int lerpChannel(int start, int end, double t) {
        double clamped = RangesKt.coerceIn((double)t, (double)0.0, (double)1.0);
        return RangesKt.coerceIn((int)((int)((double)start + (double)(end - start) * clamped)), (int)0, (int)255);
    }

    private final class_243 raycastDisplacement(class_243 startPos, class_243 direction, int distance) {
        block16: {
            class_2338 xDiagonalOffset = direction.field_1352 > 0.0 ? new class_2338(1, 0, 0) : new class_2338(-1, 0, 0);
            class_2338 zDiagonalOffset = direction.field_1350 > 0.0 ? new class_2338(0, 0, 1) : new class_2338(0, 0, -1);
            int closestFloorY = Integer.MAX_VALUE;
            int offset = 0;
            if (offset > distance) break block16;
            while (true) {
                block19: {
                    class_2338 checkPos;
                    block21: {
                        class_243 pos;
                        block20: {
                            block17: {
                                block18: {
                                    class_243 justAhead;
                                    Intrinsics.checkNotNullExpressionValue((Object)startPos.method_1019(direction.method_1021((double)offset)), (String)"add(...)");
                                    Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49638((class_2374)((class_2374)pos)), (String)"containing(...)");
                                    if (!this.canTeleportThrough(checkPos)) {
                                        if (offset == 0) {
                                            return null;
                                        }
                                        return direction.method_1021((double)(offset - 1));
                                    }
                                    class_2338 class_23382 = checkPos.method_10084();
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
                                    if (this.canTeleportThrough(class_23382)) break block17;
                                    if (offset != 0) break block18;
                                    Intrinsics.checkNotNullExpressionValue((Object)startPos.method_1019(direction.method_1021(0.2)), (String)"add(...)");
                                    if (!(justAhead.field_1351 - Math.floor(justAhead.field_1351) <= 0.495)) {
                                        return null;
                                    }
                                    break block19;
                                }
                                return direction.method_1021((double)(offset - 1));
                            }
                            if (offset != 0 && direction.field_1352 < 0.0) {
                                class_2338 class_23383 = checkPos.method_10078();
                                Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"east(...)");
                                if (this.isBlockFloor(class_23383)) {
                                    class_2338 class_23384 = class_2338.method_49638((class_2374)((class_2374)pos.method_1020(direction))).method_10081((class_2382)zDiagonalOffset);
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"offset(...)");
                                    if (this.isBlockFloor(class_23384)) {
                                        return direction.method_1021((double)(offset - 1));
                                    }
                                }
                            }
                            if (offset != 0 && direction.field_1350 < 0.0 && direction.field_1352 < 0.0) {
                                class_2338 class_23385 = checkPos.method_10072();
                                Intrinsics.checkNotNullExpressionValue((Object)class_23385, (String)"south(...)");
                                if (this.isBlockFloor(class_23385)) {
                                    class_2338 class_23386 = class_2338.method_49638((class_2374)((class_2374)pos.method_1020(direction))).method_10081((class_2382)xDiagonalOffset);
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23386, (String)"offset(...)");
                                    if (this.isBlockFloor(class_23386)) {
                                        return direction.method_1021((double)(offset - 1));
                                    }
                                }
                            }
                            class_2338 class_23387 = checkPos.method_10074();
                            Intrinsics.checkNotNullExpressionValue((Object)class_23387, (String)"below(...)");
                            if (this.isBlockFloor(class_23387)) break block20;
                            class_2338 class_23388 = checkPos.method_10074().method_10081((class_2382)xDiagonalOffset);
                            Intrinsics.checkNotNullExpressionValue((Object)class_23388, (String)"offset(...)");
                            if (!this.isBlockFloor(class_23388)) break block21;
                            class_2338 class_23389 = checkPos.method_10074().method_10081((class_2382)zDiagonalOffset);
                            Intrinsics.checkNotNullExpressionValue((Object)class_23389, (String)"offset(...)");
                            if (!this.isBlockFloor(class_23389)) break block21;
                        }
                        if (pos.field_1351 - Math.floor(pos.field_1351) < 0.31) {
                            closestFloorY = checkPos.method_10264() - 1;
                        }
                    }
                    if (closestFloorY == checkPos.method_10264()) {
                        return direction.method_1021((double)(offset - 1));
                    }
                }
                if (offset == distance) break;
                ++offset;
            }
        }
        return direction.method_1021((double)distance);
    }

    private final boolean canTeleportThrough(class_2338 pos) {
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        if (state.method_26215()) {
            return true;
        }
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        class_265 class_2652 = state.method_26220((class_1922)level2, pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        return shape.method_1110() || block instanceof class_2577 || block instanceof class_2362 || block instanceof class_2560 || state.method_27852(class_2246.field_10477) && ((Number)((Object)state.method_11654((class_2769)class_2741.field_12536))).intValue() <= 3;
    }

    private final boolean isBlockFloor(class_2338 pos) {
        class_638 class_6382 = class_310.method_1551().field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2680 class_26802 = level2.method_8320(pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        class_265 class_2652 = state.method_26220((class_1922)level2, pos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        if (shape.method_1110()) {
            return false;
        }
        return shape.method_1107().field_1325 >= 1.0 || state.method_27852(class_2246.field_37576);
    }

    private final OverlayRenderEngine.Color toOverlayColor(int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        return new OverlayRenderEngine.Color(r, g, b, a);
    }

    private static final class_1309 syncMobEsp$lambda$0(class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it instanceof class_1309 ? (class_1309)it : null;
    }

    private static final boolean syncMobEsp$lambda$1(class_746 $player, class_243 $center, double $radius, class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player) && !(it instanceof class_1531) && !(it instanceof class_1657) && it.method_5805() && INSTANCE.isEntityInsideRadius(it, $center, $radius);
    }

    private static final ClientGlowEspManager.GlowTarget syncMobEsp$lambda$2(class_1309 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return new ClientGlowEspManager.GlowTarget(it, overlayColor.getValue(), 20);
    }

    static {
        Object[] objectArray = new String[]{"HYPERION", "ASTRAEA", "SCYLLA", "VALKYRIE"};
        witherBladeIds = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"hyperion", "astraea", "scylla", "valkyrie"};
        witherBladeNameHints = SetsKt.setOf((Object[])objectArray);
        enabled = new CheckboxSetting("Enabled", "Show the predicted Wither Impact landing block while holding a Wither blade.", true);
        objectArray = new String[]{"Outline", "Filled", "Outline + Filled"};
        renderMode = new ModeSetting("Render Mode", "ESP rendering style.", 2, (String[])objectArray);
        objectArray = new String[]{"Block", "Player"};
        markerSize = new ModeSetting("Marker Size", "Render the landing marker as a block-sized or player-sized box.", 0, (String[])objectArray);
        overlayColor = new ColorSetting("Overlay Color", "Landing block overlay color.", -11675393);
        gradientEndColor = new ColorSetting("Gradient End", "Secondary ring gradient color.", -38195);
        outlineWidth = new SliderSetting("Outline Width", "Thickness of the outline.", 2.2, 0.5, 8.0, 0.0, 32, null);
        fillOpacity = new SliderSetting("Fill Opacity", "Opacity of the filled overlay.", 0.25, 0.0, 1.0, 0.0, 32, null);
        showWhenInAir = new CheckboxSetting("Show In Air", "Still render when the landing spot has no supporting block below it.", false);
        showBoomRadius = new CheckboxSetting("Show Boom Radius", "Render the Hyperion implosion radius ring.", true);
        mobEsp = new CheckboxSetting("Mob ESP", "Glow ESP on mobs inside the Hyperion radius.", true);
        boomRadius = new SliderSetting("Boom Radius", "Implosion radius in blocks.", 6.0, 1.0, 10.0, 0.0, 32, null);
        ringLineWidth = new SliderSetting("Ring Width", "Thickness of the implosion ring.", 2.8, 0.5, 8.0, 0.0, 32, null);
        animationSpeed = new SliderSetting("Animation Speed", "Speed of the gradient and pulse animation.", 1.15, 0.1, 4.0, 0.0, 32, null);
        objectArray = new Setting[]{enabled, renderMode, markerSize, overlayColor, gradientEndColor, outlineWidth, fillOpacity, showWhenInAir, showBoomRadius, mobEsp, boomRadius, ringLineWidth, animationSpeed};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\fJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\fJ\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\fJL\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b \u0010\fR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b!\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b\"\u0010\fR\u0017\u0010\u0007\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b#\u0010\fR\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b$\u0010\f\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/visual/WitherImpactOverlayModule$MarkerBounds;", "", "", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "<init>", "(DDDDDD)V", "component1", "()D", "component2", "component3", "component4", "component5", "component6", "copy", "(DDDDDD)Lorg/cobalt/internal/visual/WitherImpactOverlayModule$MarkerBounds;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getMinX", "getMinY", "getMinZ", "getMaxX", "getMaxY", "getMaxZ", "cobalt"})
    private static final class MarkerBounds {
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;

        public MarkerBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public final double getMinX() {
            return this.minX;
        }

        public final double getMinY() {
            return this.minY;
        }

        public final double getMinZ() {
            return this.minZ;
        }

        public final double getMaxX() {
            return this.maxX;
        }

        public final double getMaxY() {
            return this.maxY;
        }

        public final double getMaxZ() {
            return this.maxZ;
        }

        public final double component1() {
            return this.minX;
        }

        public final double component2() {
            return this.minY;
        }

        public final double component3() {
            return this.minZ;
        }

        public final double component4() {
            return this.maxX;
        }

        public final double component5() {
            return this.maxY;
        }

        public final double component6() {
            return this.maxZ;
        }

        @NotNull
        public final MarkerBounds copy(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return new MarkerBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public static /* synthetic */ MarkerBounds copy$default(MarkerBounds markerBounds, double d, double d2, double d3, double d4, double d5, double d6, int n, Object object) {
            if ((n & 1) != 0) {
                d = markerBounds.minX;
            }
            if ((n & 2) != 0) {
                d2 = markerBounds.minY;
            }
            if ((n & 4) != 0) {
                d3 = markerBounds.minZ;
            }
            if ((n & 8) != 0) {
                d4 = markerBounds.maxX;
            }
            if ((n & 0x10) != 0) {
                d5 = markerBounds.maxY;
            }
            if ((n & 0x20) != 0) {
                d6 = markerBounds.maxZ;
            }
            return markerBounds.copy(d, d2, d3, d4, d5, d6);
        }

        @NotNull
        public String toString() {
            return "MarkerBounds(minX=" + this.minX + ", minY=" + this.minY + ", minZ=" + this.minZ + ", maxX=" + this.maxX + ", maxY=" + this.maxY + ", maxZ=" + this.maxZ + ")";
        }

        public int hashCode() {
            int result = Double.hashCode(this.minX);
            result = result * 31 + Double.hashCode(this.minY);
            result = result * 31 + Double.hashCode(this.minZ);
            result = result * 31 + Double.hashCode(this.maxX);
            result = result * 31 + Double.hashCode(this.maxY);
            result = result * 31 + Double.hashCode(this.maxZ);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MarkerBounds)) {
                return false;
            }
            MarkerBounds markerBounds = (MarkerBounds)other;
            if (Double.compare(this.minX, markerBounds.minX) != 0) {
                return false;
            }
            if (Double.compare(this.minY, markerBounds.minY) != 0) {
                return false;
            }
            if (Double.compare(this.minZ, markerBounds.minZ) != 0) {
                return false;
            }
            if (Double.compare(this.maxX, markerBounds.maxX) != 0) {
                return false;
            }
            if (Double.compare(this.maxY, markerBounds.maxY) != 0) {
                return false;
            }
            return Double.compare(this.maxZ, markerBounds.maxZ) == 0;
        }
    }
}

