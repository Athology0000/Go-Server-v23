/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.Property
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.CharsKt
 *  kotlin.text.Charsets
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1531
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_9296
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2fStack
 */
package org.cobalt.internal.visual;

import com.mojang.authlib.properties.Property;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.CharsKt;
import kotlin.text.Charsets;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9296;
import net.minecraft.class_9334;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.GuiRenderContext;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0016\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0003wxyB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0011\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011J!\u0010\u0016\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J)\u0010\u0019\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aJ)\u0010\u001d\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ!\u0010\u001f\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u001f\u0010\u0017J\u0019\u0010\"\u001a\u0004\u0018\u00010!2\u0006\u0010 \u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\"\u0010#J\u0017\u0010%\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0019\u0010(\u001a\u0004\u0018\u00010\u00072\u0006\u0010'\u001a\u00020!H\u0002\u00a2\u0006\u0004\b(\u0010)J\u0019\u0010,\u001a\u00020+2\b\u0010*\u001a\u0004\u0018\u00010\u000fH\u0002\u00a2\u0006\u0004\b,\u0010-J\u0019\u0010.\u001a\u00020+2\b\u0010*\u001a\u0004\u0018\u00010\u000fH\u0002\u00a2\u0006\u0004\b.\u0010-J\u0017\u0010/\u001a\u00020\f2\u0006\u0010*\u001a\u00020\u000fH\u0002\u00a2\u0006\u0004\b/\u00100J?\u00108\u001a\u00020\f2\u0006\u00101\u001a\u00020+2\u0006\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020+2\u0006\u00104\u001a\u00020\u00072\u0006\u00106\u001a\u0002052\u0006\u00107\u001a\u000205H\u0002\u00a2\u0006\u0004\b8\u00109J/\u0010=\u001a\u00020\f2\u0006\u0010*\u001a\u00020\u000f2\u0006\u0010:\u001a\u00020+2\u0006\u0010;\u001a\u00020+2\u0006\u0010<\u001a\u00020+H\u0002\u00a2\u0006\u0004\b=\u0010>J'\u0010B\u001a\u00020\u00072\u0006\u0010?\u001a\u00020\u00072\u0006\u0010@\u001a\u00020+2\u0006\u0010A\u001a\u00020+H\u0002\u00a2\u0006\u0004\bB\u0010CJ\u0017\u0010E\u001a\u00020\u00072\u0006\u0010D\u001a\u000205H\u0002\u00a2\u0006\u0004\bE\u0010FJ\u001f\u0010I\u001a\u0002052\u0006\u0010G\u001a\u0002052\u0006\u0010H\u001a\u000205H\u0002\u00a2\u0006\u0004\bI\u0010JR\u0014\u0010L\u001a\u00020K8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bL\u0010MR\u0014\u0010O\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010PR\u0014\u0010R\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010SR\u0014\u0010T\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bT\u0010PR\u0014\u0010U\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010PR\u0014\u0010V\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bV\u0010PR\u0018\u0010W\u001a\u0004\u0018\u00010\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u001a\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u001b0Y8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bZ\u0010[R \u0010]\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u001b0\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010^R \u0010_\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u001b0\\8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010^R\u0014\u0010`\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010XR\u0014\u0010b\u001a\u00020a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010cR\u0014\u0010e\u001a\u00020d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010fR\u0014\u0010g\u001a\u00020d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bg\u0010fR\u0014\u0010h\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0014\u0010j\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bj\u0010iR\u0014\u0010k\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bk\u0010iR\u0014\u0010l\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bl\u0010iR\u0014\u0010m\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bm\u0010iR\u0014\u0010n\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bn\u0010iR\u0014\u0010o\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bo\u0010iR\u0014\u0010p\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bp\u0010iR\u0014\u0010q\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bq\u0010iR\u0014\u0010r\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\br\u0010iR\u0014\u0010s\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bs\u0010iR\u0014\u0010t\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bt\u0010iR\u0014\u0010u\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bu\u0010iR\u0014\u0010v\u001a\u00020+8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bv\u0010i\u00a8\u0006z"}, d2={"Lorg/cobalt/internal/visual/DeployableHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "isLanternActive", "()Z", "", "getLanternStatusLabel", "()Ljava/lang/String;", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "currentDisplayDeployable", "()Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "Lnet/minecraft/class_1531;", "stand", "Lnet/minecraft/class_1657;", "player", "detectDeployable", "(Lnet/minecraft/class_1531;Lnet/minecraft/class_1657;)Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "displayName", "detectNamedDeployable", "(Lnet/minecraft/class_1531;Lnet/minecraft/class_1657;Ljava/lang/String;)Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "spec", "detectTotemDeployable", "(Lnet/minecraft/class_1531;Lnet/minecraft/class_1657;Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;)Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "detectFlareDeployable", "anchor", "Lnet/minecraft/class_1799;", "findNearbyHeadItem", "(Lnet/minecraft/class_1531;)Lnet/minecraft/class_1799;", "raw", "sanitizeName", "(Ljava/lang/String;)Ljava/lang/String;", "stack", "extractSkullTextureId", "(Lnet/minecraft/class_1799;)Ljava/lang/String;", "active", "", "baseWidthFor", "(Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;)F", "baseHeightFor", "renderHudCard", "(Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;)V", "x", "y", "w", "value", "", "accent", "textColor", "renderStatChip", "(FFFLjava/lang/String;II)V", "screenX", "screenY", "scale", "renderHudIcon", "(Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;FFF)V", "text", "maxWidth", "size", "ellipsize", "(Ljava/lang/String;FF)Ljava/lang/String;", "totalSeconds", "formatSeconds", "(I)Ljava/lang/String;", "color", "alpha", "withAlpha", "(II)I", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "displayStyle", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "liveIconSetting", "previewInEditor", "showStatsInCompact", "activeDeployable", "Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "", "deployableSpecs", "Ljava/util/List;", "", "deployableByPrefix", "Ljava/util/Map;", "deployableByFlareTexture", "previewDeployable", "Lorg/cobalt/api/hud/HudElement;", "deployableHud", "Lorg/cobalt/api/hud/HudElement;", "Lkotlin/text/Regex;", "DEPLOYABLE_SECONDS_REGEX", "Lkotlin/text/Regex;", "TOTEM_SECONDS_REGEX", "PAD", "F", "CORNER", "ICON_BOX", "HEADER_HEIGHT", "STAT_TOP_GAP", "CHIP_HEIGHT", "CHIP_GAP", "COMPACT_HEIGHT", "COMPACT_MIN_WIDTH", "DETAILED_WIDTH", "NAME_TEXT_SIZE", "TIME_TEXT_SIZE", "SUBTITLE_TEXT_SIZE", "CHIP_TEXT_SIZE", "DeployableCategory", "DeployableSpec", "ActiveDeployable", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDeployableHudModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeployableHudModule.kt\norg/cobalt/internal/visual/DeployableHudModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,611:1\n296#2,2:612\n1220#2,2:615\n1249#2,4:617\n1642#2,10:621\n1915#2:631\n1916#2:633\n1652#2:634\n1#3:614\n1#3:632\n*S KotlinDebug\n*F\n+ 1 DeployableHudModule.kt\norg/cobalt/internal/visual/DeployableHudModule\n*L\n348#1:612,2\n267#1:615,2\n267#1:617,4\n269#1:621,10\n269#1:631\n269#1:633\n269#1:634\n269#1:632\n*E\n"})
public final class DeployableHudModule
extends Module {
    @NotNull
    public static final DeployableHudModule INSTANCE;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final ModeSetting displayStyle;
    @NotNull
    private static final CheckboxSetting liveIconSetting;
    @NotNull
    private static final CheckboxSetting previewInEditor;
    @NotNull
    private static final CheckboxSetting showStatsInCompact;
    @Nullable
    private static volatile ActiveDeployable activeDeployable;
    @NotNull
    private static final List<DeployableSpec> deployableSpecs;
    @NotNull
    private static final Map<String, DeployableSpec> deployableByPrefix;
    @NotNull
    private static final Map<String, DeployableSpec> deployableByFlareTexture;
    @NotNull
    private static final ActiveDeployable previewDeployable;
    @NotNull
    private static final HudElement deployableHud;
    @NotNull
    private static final Regex DEPLOYABLE_SECONDS_REGEX;
    @NotNull
    private static final Regex TOTEM_SECONDS_REGEX;
    private static final float PAD = 8.0f;
    private static final float CORNER = 9.0f;
    private static final float ICON_BOX = 28.0f;
    private static final float HEADER_HEIGHT = 28.0f;
    private static final float STAT_TOP_GAP = 6.0f;
    private static final float CHIP_HEIGHT = 16.0f;
    private static final float CHIP_GAP = 4.0f;
    private static final float COMPACT_HEIGHT = 44.0f;
    private static final float COMPACT_MIN_WIDTH = 132.0f;
    private static final float DETAILED_WIDTH = 196.0f;
    private static final float NAME_TEXT_SIZE = 11.0f;
    private static final float TIME_TEXT_SIZE = 13.0f;
    private static final float SUBTITLE_TEXT_SIZE = 9.0f;
    private static final float CHIP_TEXT_SIZE = 8.5f;

    private DeployableHudModule() {
        super("Deployable HUD");
    }

    public final boolean isLanternActive() {
        Object object = activeDeployable;
        return (object != null && (object = ((ActiveDeployable)object).getSpec()) != null ? ((DeployableSpec)object).getCategory() : null) == DeployableCategory.LANTERN;
    }

    @Nullable
    public final String getLanternStatusLabel() {
        ActiveDeployable activeDeployable = DeployableHudModule.activeDeployable;
        if (activeDeployable == null) {
            return null;
        }
        ActiveDeployable active = activeDeployable;
        if (active.getSpec().getCategory() != DeployableCategory.LANTERN) {
            return null;
        }
        return ((Object)StringsKt.trim((CharSequence)active.getSpec().getDisplayPrefix())).toString() + " " + this.formatSeconds(active.getSeconds());
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            activeDeployable = null;
            return;
        }
        class_746 player = DeployableHudModule.mc.field_1724;
        class_638 level2 = DeployableHudModule.mc.field_1687;
        if (player == null || level2 == null) {
            activeDeployable = null;
            return;
        }
        ActiveDeployable best = null;
        for (Object t : level2.method_18112()) {
            ActiveDeployable detected;
            ActiveDeployable currentBest;
            class_1531 stand;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1531 class_15312 = entity instanceof class_1531 ? (class_1531)entity : null;
            if (class_15312 == null || this.detectDeployable(stand = class_15312, (class_1657)player) == null || (currentBest = best) != null && detected.getSpec().getPriority() <= currentBest.getSpec().getPriority() && (detected.getSpec().getPriority() != currentBest.getSpec().getPriority() || detected.getSeconds() <= currentBest.getSeconds())) continue;
            best = detected;
        }
        activeDeployable = best;
    }

    private final ActiveDeployable currentDisplayDeployable() {
        ActiveDeployable activeDeployable = DeployableHudModule.activeDeployable;
        if (activeDeployable == null) {
            activeDeployable = HudModuleManager.INSTANCE.isEditorOpen() && (Boolean)previewInEditor.getValue() != false ? previewDeployable : null;
        }
        return activeDeployable;
    }

    private final ActiveDeployable detectDeployable(class_1531 stand, class_1657 player) {
        String customName;
        Object object = stand.method_5797();
        if (object == null || (object = object.getString()) == null) {
            object = "";
        }
        if (!StringsKt.isBlank((CharSequence)(customName = this.sanitizeName((String)object)))) {
            return this.detectNamedDeployable(stand, player, customName);
        }
        if (!stand.method_5767()) {
            return null;
        }
        return this.detectFlareDeployable(stand, player);
    }

    private final ActiveDeployable detectNamedDeployable(class_1531 stand, class_1657 player, String displayName) {
        ActiveDeployable activeDeployable;
        Object object;
        Object v2;
        block7: {
            String string = displayName;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string2 = string.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
            String lowered = string2;
            Iterable $this$firstOrNull$iv = deployableByPrefix.entrySet();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Map.Entry it = (Map.Entry)element$iv;
                boolean bl = false;
                if (!StringsKt.startsWith$default((String)lowered, (String)((String)it.getKey()), (boolean)false, (int)2, null)) continue;
                v2 = element$iv;
                break block7;
            }
            v2 = null;
        }
        if ((object = (Map.Entry)v2) == null || (object = (DeployableSpec)object.getValue()) == null) {
            return null;
        }
        Object spec = object;
        if (player.method_5858((class_1297)stand) > ((DeployableSpec)spec).getRangeSq()) {
            return null;
        }
        if (((DeployableSpec)spec).getCategory() == DeployableCategory.TOTEM) {
            activeDeployable = this.detectTotemDeployable(stand, player, (DeployableSpec)spec);
        } else {
            Object object2 = Regex.find$default((Regex)DEPLOYABLE_SECONDS_REGEX, (CharSequence)displayName, (int)0, (int)2, null);
            if (object2 == null || (object2 = object2.getGroupValues()) == null || (object2 = (String)CollectionsKt.getOrNull((List)object2, (int)1)) == null || (object2 = StringsKt.toIntOrNull((String)object2)) == null) {
                return null;
            }
            int seconds = (Integer)object2;
            class_1799 class_17992 = this.findNearbyHeadItem(stand);
            if (class_17992 == null) {
                class_1799 class_17993 = ((DeployableSpec)spec).getPreviewItem().method_7972();
                class_17992 = class_17993;
                Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"copy(...)");
            }
            class_1799 icon = class_17992;
            activeDeployable = new ActiveDeployable((DeployableSpec)spec, seconds, icon, stand.method_5667());
        }
        return activeDeployable;
    }

    private final ActiveDeployable detectTotemDeployable(class_1531 stand, class_1657 player, DeployableSpec spec) {
        class_638 class_6382 = DeployableHudModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        String string = player.method_7334().name();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"name(...)");
        String string2 = string;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String ownerLine = "owner: " + string3;
        Integer seconds = null;
        boolean ownedByPlayer = false;
        class_1799 icon = null;
        for (Object t : level2.method_18112()) {
            class_1799 head;
            String text;
            class_1531 nearby;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1531 class_15312 = entity instanceof class_1531 ? (class_1531)entity : null;
            if (class_15312 == null || Math.abs((nearby = class_15312).method_23317() - stand.method_23317()) > 0.15 || Math.abs(nearby.method_23321() - stand.method_23321()) > 0.15 || Math.abs(nearby.method_23318() - stand.method_23318()) > 1.2) continue;
            Object object = nearby.method_5797();
            if (object == null || (object = object.getString()) == null) {
                object = "";
            }
            if (!StringsKt.isBlank((CharSequence)(text = this.sanitizeName((String)object)))) {
                MatchResult match = Regex.find$default((Regex)TOTEM_SECONDS_REGEX, (CharSequence)text, (int)0, (int)2, null);
                if (match != null) {
                    Object object2 = (String)CollectionsKt.getOrNull((List)match.getGroupValues(), (int)1);
                    int minutes = object2 != null && (object2 = StringsKt.toIntOrNull((String)object2)) != null ? (Integer)object2 : 0;
                    Object object3 = (String)CollectionsKt.getOrNull((List)match.getGroupValues(), (int)2);
                    int secs = object3 != null && (object3 = StringsKt.toIntOrNull((String)object3)) != null ? (Integer)object3 : 0;
                    seconds = minutes * 60 + secs;
                }
                String string4 = text;
                Locale locale2 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
                String string5 = string4.toLowerCase(locale2);
                Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"toLowerCase(...)");
                if (Intrinsics.areEqual((Object)string5, (Object)ownerLine)) {
                    ownedByPlayer = true;
                }
            }
            Intrinsics.checkNotNullExpressionValue((Object)nearby.method_6118(class_1304.field_6169), (String)"getItemBySlot(...)");
            if (head.method_7960() || icon != null) continue;
            icon = head.method_7972();
        }
        if (!ownedByPlayer || seconds == null) {
            return null;
        }
        int n = seconds;
        class_1799 class_17992 = icon;
        if (class_17992 == null) {
            class_1799 class_17993 = spec.getPreviewItem().method_7972();
            class_17992 = class_17993;
            Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"copy(...)");
        }
        return new ActiveDeployable(spec, n, class_17992, stand.method_5667());
    }

    private final ActiveDeployable detectFlareDeployable(class_1531 stand, class_1657 player) {
        class_1799 class_17992 = stand.method_6118(class_1304.field_6169);
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItemBySlot(...)");
        class_1799 head = class_17992;
        if (head.method_7960()) {
            return null;
        }
        String string = this.extractSkullTextureId(head);
        if (string == null) {
            return null;
        }
        String textureId = string;
        DeployableSpec deployableSpec = deployableByFlareTexture.get(textureId);
        if (deployableSpec == null) {
            return null;
        }
        DeployableSpec spec = deployableSpec;
        if (player.method_5858((class_1297)stand) > spec.getRangeSq()) {
            return null;
        }
        int seconds = RangesKt.coerceAtLeast((int)(180 - stand.field_6012 / 20), (int)0);
        class_1799 class_17993 = head.method_7972();
        Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"copy(...)");
        return new ActiveDeployable(spec, seconds, class_17993, stand.method_5667());
    }

    private final class_1799 findNearbyHeadItem(class_1531 anchor) {
        class_638 class_6382 = DeployableHudModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        for (Object t : level2.method_18112()) {
            class_1799 head;
            class_1531 stand;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1531 class_15312 = entity instanceof class_1531 ? (class_1531)entity : null;
            if (class_15312 == null || Math.abs((stand = class_15312).method_23317() - anchor.method_23317()) > 0.15 || Math.abs(stand.method_23321() - anchor.method_23321()) > 0.15 || Math.abs(stand.method_23318() - anchor.method_23318()) > 1.1) continue;
            Intrinsics.checkNotNullExpressionValue((Object)stand.method_6118(class_1304.field_6169), (String)"getItemBySlot(...)");
            if (head.method_7960()) continue;
            return head.method_7972();
        }
        return null;
    }

    private final String sanitizeName(String raw) {
        String string = class_124.method_539((String)raw);
        if (string == null) {
            string = raw;
        }
        return ((Object)StringsKt.trim((CharSequence)string)).toString();
    }

    private final String extractSkullTextureId(class_1799 stack) {
        CharSequence charSequence;
        Object $this$extractSkullTextureId_u24lambda_u240;
        class_9296 class_92962 = (class_9296)stack.method_58694(class_9334.field_49617);
        if (class_92962 == null) {
            return null;
        }
        class_9296 profile = class_92962;
        Collection collection = profile.method_73313().properties().get((Object)"textures");
        Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"get(...)");
        Collection textures = collection;
        Object object = (Property)CollectionsKt.firstOrNull((Iterable)textures);
        if (object == null || (object = object.value()) == null) {
            return null;
        }
        Object value = object;
        Object object2 = this;
        try {
            $this$extractSkullTextureId_u24lambda_u240 = object2;
            boolean bl = false;
            byte[] byArray = Base64.getDecoder().decode((String)value);
            Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"decode(...)");
            byte[] byArray2 = byArray;
            $this$extractSkullTextureId_u24lambda_u240 = Result.constructor-impl((Object)new String(byArray2, Charsets.UTF_8));
        }
        catch (Throwable bl) {
            $this$extractSkullTextureId_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$extractSkullTextureId_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)object2) ? null : object2);
        if (string == null) {
            return null;
        }
        String decoded = string;
        String urlKey = "\"url\"";
        int keyIndex = StringsKt.indexOf$default((CharSequence)decoded, (String)urlKey, (int)0, (boolean)false, (int)6, null);
        if (keyIndex < 0) {
            return null;
        }
        int colonIndex = StringsKt.indexOf$default((CharSequence)decoded, (char)':', (int)keyIndex, (boolean)false, (int)4, null);
        int startQuote = StringsKt.indexOf$default((CharSequence)decoded, (char)'\"', (int)(colonIndex + 1), (boolean)false, (int)4, null) + 1;
        if (startQuote <= 0) {
            return null;
        }
        int endQuote = StringsKt.indexOf$default((CharSequence)decoded, (char)'\"', (int)startQuote, (boolean)false, (int)4, null);
        if (endQuote <= startQuote) {
            return null;
        }
        String string2 = decoded.substring(startQuote, endQuote);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        String url = string2;
        CharSequence charSequence2 = StringsKt.substringAfterLast$default((String)url, (char)'/', null, (int)2, null);
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            boolean bl = false;
            charSequence = null;
        } else {
            charSequence = charSequence2;
        }
        return (String)charSequence;
    }

    private final float baseWidthFor(ActiveDeployable active) {
        ActiveDeployable display;
        ActiveDeployable activeDeployable = active;
        if (activeDeployable == null) {
            activeDeployable = display = previewDeployable;
        }
        return ((Number)displayStyle.getValue()).intValue() == 0 ? Math.max(132.0f, 52.0f + Math.max(NVGRenderer.textWidth$default(display.getSpec().getCompactName(), 11.0f, null, 4, null), NVGRenderer.textWidth$default(this.formatSeconds(display.getSeconds()), 13.0f, null, 4, null))) : 196.0f;
    }

    private final float baseHeightFor(ActiveDeployable active) {
        int rows;
        ActiveDeployable display;
        ActiveDeployable activeDeployable = active;
        if (activeDeployable == null) {
            activeDeployable = display = previewDeployable;
        }
        return ((Number)displayStyle.getValue()).intValue() == 0 ? 44.0f : 44.0f + ((rows = Math.max(1, (int)Math.ceil((double)display.getSpec().getStatLines().size() / 2.0))) > 0 ? 6.0f + (float)rows * 16.0f + (float)RangesKt.coerceAtLeast((int)(rows - 1), (int)0) * 4.0f : 0.0f);
    }

    /*
     * WARNING - void declaration
     */
    private final void renderHudCard(ActiveDeployable active) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        int accent = active.getSpec().getAccent();
        float width = this.baseWidthFor(active);
        float height = this.baseHeightFor(active);
        NVGRenderer.rect(4.0f, 5.0f, width, height, 0x24000000, 11.0f);
        NVGRenderer.rect(2.0f, 2.0f, width, height, 0x12000000, 10.0f);
        NVGRenderer.rect(0.0f, 0.0f, width, height, -367781596, 9.0f);
        NVGRenderer.gradientRect(0.0f, 0.0f, width, height * 0.55f, this.withAlpha(accent, 18), 0, Gradient.TopToBottom, 9.0f);
        NVGRenderer.hollowRect(0.0f, 0.0f, width, height, 1.0f, this.withAlpha(accent, 105), 9.0f);
        NVGRenderer.rect(8.0f, 8.0f, 28.0f, 28.0f, -14537668, 7.0f);
        NVGRenderer.hollowRect(8.0f, 8.0f, 28.0f, 28.0f, 1.0f, this.withAlpha(accent, 80), 7.0f);
        float headerX = 44.0f;
        float headerW = width - headerX - 8.0f;
        NVGRenderer.textShadow$default(this.ellipsize(active.getSpec().getCompactName(), headerW, 11.0f), headerX, 9.0f, 11.0f, theme.getTextPrimary(), null, 32, null);
        String timeText = this.formatSeconds(active.getSeconds());
        float timeWidth = NVGRenderer.textWidth$default(timeText, 13.0f, null, 4, null);
        NVGRenderer.text$default(timeText, width - 8.0f - timeWidth, 10.0f, 13.0f, accent, null, 32, null);
        if (((Number)displayStyle.getValue()).intValue() == 0) {
            String subtitle;
            String string;
            if (((Boolean)showStatsInCompact.getValue()).booleanValue()) {
                string = (String)CollectionsKt.firstOrNull(active.getSpec().getStatLines());
                if (string == null) {
                    string = "";
                }
            } else {
                String string2 = active.getSpec().getCategory().name();
                Locale locale = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                String string3 = string2.toLowerCase(locale);
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                string2 = string3;
                if (((CharSequence)string2).length() > 0) {
                    String string4;
                    void it2;
                    char c = string2.charAt(0);
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean bl = false;
                    if (Character.isLowerCase((char)it2)) {
                        Locale locale2 = Locale.ROOT;
                        Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
                        string4 = CharsKt.titlecase((char)it2, (Locale)locale2);
                    } else {
                        string4 = String.valueOf((char)it2);
                    }
                    StringBuilder stringBuilder2 = stringBuilder.append((Object)string4);
                    String it2 = string2;
                    int n = 1;
                    String string5 = it2.substring(n);
                    Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"substring(...)");
                    string = stringBuilder2.append(string5).toString();
                } else {
                    string = string2;
                }
            }
            if (!StringsKt.isBlank((CharSequence)(subtitle = string))) {
                NVGRenderer.text$default(this.ellipsize(subtitle, headerW, 9.0f), headerX, 24.0f, 9.0f, theme.getTextSecondary(), null, 32, null);
            }
            return;
        }
        List<String> stats = active.getSpec().getStatLines();
        if (stats.isEmpty()) {
            return;
        }
        float chipYStart = 42.0f;
        float chipWidth = (width - 16.0f - 4.0f) / 2.0f;
        Iterator iterator = ((Iterable)stats).iterator();
        int n = 0;
        while (iterator.hasNext()) {
            int index = n++;
            String stat = (String)iterator.next();
            int row = index / 2;
            int col = index % 2;
            float chipX = 8.0f + (float)col * (chipWidth + 4.0f);
            float chipY = chipYStart + (float)row * 20.0f;
            this.renderStatChip(chipX, chipY, chipWidth, stat, accent, theme.getText());
        }
    }

    private final void renderStatChip(float x, float y, float w, String value, int accent, int textColor) {
        NVGRenderer.rect(x, y, w, 16.0f, this.withAlpha(accent, 22), 5.0f);
        NVGRenderer.hollowRect(x, y, w, 16.0f, 1.0f, this.withAlpha(accent, 80), 5.0f);
        String text = this.ellipsize(value, w - 8.0f, 8.5f);
        NVGRenderer.text$default(text, x + 4.0f, y + 3.0f, 8.5f, textColor, null, 32, null);
    }

    private final void renderHudIcon(ActiveDeployable active, float screenX, float screenY, float scale) {
        class_1799 stack;
        class_332 class_3322 = GuiRenderContext.INSTANCE.getGraphics();
        if (class_3322 == null) {
            return;
        }
        class_332 graphics = class_3322;
        class_1799 class_17992 = stack = (Boolean)liveIconSetting.getValue() != false && !active.getIconStack().method_7960() ? active.getIconStack() : active.getSpec().getPreviewItem();
        if (stack.method_7960()) {
            return;
        }
        float guiScale = mc.method_22683().method_4495();
        if (guiScale <= 0.0f) {
            return;
        }
        float originX = screenX / guiScale;
        float originY = screenY / guiScale;
        float renderScale = scale / guiScale;
        float localX = 14.0f;
        float localY = 14.0f;
        Matrix3x2fStack matrix3x2fStack = graphics.method_51448();
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2fStack, (String)"pose(...)");
        Matrix3x2fStack pose = matrix3x2fStack;
        pose.pushMatrix();
        pose.translate(originX + localX * renderScale, originY + localY * renderScale);
        pose.scale(renderScale, renderScale);
        graphics.method_51427(stack, 0, 0);
        pose.popMatrix();
    }

    private final String ellipsize(String text, float maxWidth, float size) {
        if (NVGRenderer.textWidth$default(text, size, null, 4, null) <= maxWidth) {
            return text;
        }
        for (int end = text.length(); end > 1; --end) {
            String string = text.substring(0, end);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            String candidate = ((Object)StringsKt.trimEnd((CharSequence)string)).toString() + "...";
            if (!(NVGRenderer.textWidth$default(candidate, size, null, 4, null) <= maxWidth)) continue;
            return candidate;
        }
        return "...";
    }

    private final String formatSeconds(int totalSeconds) {
        Object object;
        int seconds = RangesKt.coerceAtLeast((int)totalSeconds, (int)0);
        int minutes = seconds / 60;
        int remainder = seconds % 60;
        if (minutes > 0) {
            String string = "%d:%02d";
            Object[] objectArray = new Object[]{minutes, remainder};
            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            object = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        } else {
            object = seconds + "s";
        }
        return object;
    }

    private final int withAlpha(int color, int alpha) {
        Color base = new Color(color, true);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), RangesKt.coerceIn((int)alpha, (int)0, (int)255)).getRGB();
    }

    private static final float deployableHud$lambda$0$0() {
        return INSTANCE.baseWidthFor(INSTANCE.currentDisplayDeployable());
    }

    private static final float deployableHud$lambda$0$1() {
        return INSTANCE.baseHeightFor(INSTANCE.currentDisplayDeployable());
    }

    private static final Unit deployableHud$lambda$0$2(float f, float f2, float f3) {
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        ActiveDeployable activeDeployable = INSTANCE.currentDisplayDeployable();
        if (activeDeployable == null) {
            return Unit.INSTANCE;
        }
        ActiveDeployable active = activeDeployable;
        INSTANCE.renderHudCard(active);
        return Unit.INSTANCE;
    }

    private static final Unit deployableHud$lambda$0$3(float screenX, float screenY, float scale) {
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        ActiveDeployable activeDeployable = INSTANCE.currentDisplayDeployable();
        if (activeDeployable == null) {
            return Unit.INSTANCE;
        }
        ActiveDeployable active = activeDeployable;
        INSTANCE.renderHudIcon(active, screenX, screenY, scale);
        return Unit.INSTANCE;
    }

    private static final Unit deployableHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_RIGHT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(96.0f);
        $this$hudElement.width((Function0<Float>)((Function0)DeployableHudModule::deployableHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)DeployableHudModule::deployableHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)DeployableHudModule::deployableHud$lambda$0$2));
        $this$hudElement.postRender((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)DeployableHudModule::deployableHud$lambda$0$3));
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    static {
        void $this$mapNotNullTo$iv$iv;
        void $this$associateByTo$iv$iv;
        INSTANCE = new DeployableHudModule();
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabledSetting = new CheckboxSetting("Enabled", "Show the active SkyBlock deployable HUD.", true);
        Object[] objectArray = new String[]{"Compact", "Detailed"};
        displayStyle = new ModeSetting("Style", "Choose the deployable HUD layout.", 0, (String[])objectArray);
        liveIconSetting = new CheckboxSetting("Live Icon", "Use the detected deployable head item when available.", true);
        previewInEditor = new CheckboxSetting("Editor Preview", "Show a preview deployable while the HUD editor is open.", true);
        showStatsInCompact = new CheckboxSetting("Compact Subtitle", "Show the first bonus line under the name in compact mode.", true);
        objectArray = new DeployableSpec[14];
        objectArray[0] = new DeployableSpec("radiant", "Radiant ", "RADIANT", DeployableCategory.ORB, 1, 324.0, -30132, CollectionsKt.listOf((Object)"+1% HP/s"), null, new class_1799((class_1935)class_1802.field_8668), 256, null);
        Object[] objectArray2 = new String[]{"+2% HP/s", "+50% Mana Regen", "+10 Strength"};
        objectArray[1] = new DeployableSpec("mana_flux", "Mana Flux ", "MANA FLUX", DeployableCategory.ORB, 2, 324.0, -12003073, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_8668), 256, null);
        objectArray2 = new String[]{"+2.5% HP/s", "+100% Mana Regen", "+25 Strength", "+5 Vitality", "+5 Mending"};
        objectArray[2] = new DeployableSpec("overflux", "Overflux ", "OVERFLUX", DeployableCategory.ORB, 3, 324.0, -5342209, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_8668), 256, null);
        objectArray2 = new String[]{"+3% HP/s", "+125% Mana Regen", "+35 Strength", "+7.5 Vitality", "+7.5 Mending"};
        objectArray[3] = new DeployableSpec("plasmaflux", "Plasmaflux ", "PLASMAFLUX", DeployableCategory.ORB, 4, 400.0, -41585, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_8668), 256, null);
        objectArray2 = new String[]{"+10 Vitality", "+10 True Def"};
        objectArray[4] = new DeployableSpec("warning_flare", "Warning", "WARNING", DeployableCategory.FLARE, 5, 1600.0, -13235, CollectionsKt.listOf((Object[])objectArray2), "22e2bf6c1ec330247927ba63479e5872ac66b06903c86c82b52dac9f1c971458", new class_1799((class_1935)class_1802.field_8183));
        objectArray2 = new String[]{"+50% Mana Regen", "+20 Vitality", "+20 True Def", "+10 Ferocity"};
        objectArray[5] = new DeployableSpec("alert_flare", "Alert", "ALERT", DeployableCategory.FLARE, 6, 1600.0, -31417, CollectionsKt.listOf((Object[])objectArray2), "9d2bf9864720d87fd06b84efa80b795c48ed539b16523c3b1f1990b40c003f6b", new class_1799((class_1935)class_1802.field_8814));
        objectArray2 = new String[]{"+125% Mana Regen", "+30 Vitality", "+25 True Def", "+10 Ferocity", "+5 Bonus AS"};
        objectArray[6] = new DeployableSpec("sos_flare", "SOS", "SOS", DeployableCategory.FLARE, 7, 1600.0, -47803, CollectionsKt.listOf((Object[])objectArray2), "c0062cc98ebda72a6a4b89783adcef2815b483a01d73ea87b3df76072a89d13b", new class_1799((class_1935)class_1802.field_8814));
        objectArray[7] = new DeployableSpec("umberella", "Umberella ", "UMBERELLA", DeployableCategory.MISC, 8, 900.0, -10886745, CollectionsKt.listOf((Object)"+5 Trophy Fish"), null, new class_1799((class_1935)class_1802.field_8378), 256, null);
        objectArray[8] = new DeployableSpec("totem_of_corruption", "Totem of Corruption", "TOTEM", DeployableCategory.TOTEM, 9, 900.0, -7447297, CollectionsKt.listOf((Object)"Own Totem Active"), null, new class_1799((class_1935)class_1802.field_17515), 256, null);
        objectArray[9] = new DeployableSpec("dwarven_lantern", "Dwarven Lantern ", "DWARVEN", DeployableCategory.LANTERN, 10, 900.0, -11414, CollectionsKt.listOf((Object)"+20 Mining Speed"), null, new class_1799((class_1935)class_1802.field_16539), 256, null);
        objectArray2 = new String[]{"+40 Mining Speed", "+10 Mining Fortune"};
        objectArray[10] = new DeployableSpec("mithril_lantern", "Mithril Lantern ", "MITHRIL", DeployableCategory.LANTERN, 11, 900.0, -10234881, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_22016), 256, null);
        objectArray2 = new String[]{"+60 Mining Speed", "+15 Mining Fortune", "+5 Heat Res"};
        objectArray[11] = new DeployableSpec("titanium_lantern", "Titanium Lantern ", "TITANIUM", DeployableCategory.LANTERN, 12, 900.0, -3681566, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_22016), 256, null);
        objectArray2 = new String[]{"+80 Mining Speed", "+20 Mining Fortune", "+10 Heat Res", "+5 Cold Res"};
        objectArray[12] = new DeployableSpec("glacite_lantern", "Glacite Lantern ", "GLACITE", DeployableCategory.LANTERN, 13, 900.0, -7934209, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_22016), 256, null);
        objectArray2 = new String[]{"+100 Mining Speed", "+25 Mining Fortune", "+20 Heat Res", "+10 Cold Res", "+2.5 Gem Spread"};
        objectArray[13] = new DeployableSpec("will_o_wisp", "Will-o'-wisp ", "WISP", DeployableCategory.LANTERN, 14, 900.0, -10092620, CollectionsKt.listOf((Object[])objectArray2), null, new class_1799((class_1935)class_1802.field_22016), 256, null);
        deployableSpecs = CollectionsKt.listOf((Object[])objectArray);
        Iterable $this$associateBy$iv = deployableSpecs;
        boolean $i$f$associateBy = false;
        int capacity$iv22 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv22);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            DeployableSpec deployableSpec = (DeployableSpec)element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            String string = it.getDisplayPrefix();
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string2 = string.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
            map.put(string2, element$iv$iv);
        }
        deployableByPrefix = destination$iv$iv;
        Iterable $this$mapNotNull$iv = deployableSpecs;
        boolean $i$f$mapNotNull = false;
        Iterable capacity$iv22 = $this$mapNotNull$iv;
        Collection destination$iv$iv2 = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            Pair pair;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            DeployableSpec spec = (DeployableSpec)element$iv$iv;
            boolean bl2 = false;
            if (spec.getFlareTextureId() != null) {
                String it;
                boolean bl3 = false;
                pair = TuplesKt.to((Object)it, (Object)spec);
            } else {
                pair = null;
            }
            if (pair == null) continue;
            Pair it$iv$iv = pair;
            boolean bl4 = false;
            destination$iv$iv2.add(it$iv$iv);
        }
        deployableByFlareTexture = MapsKt.toMap((Iterable)((List)iterable));
        DeployableSpec deployableSpec = (DeployableSpec)CollectionsKt.last(deployableSpecs);
        class_1799 class_17992 = ((DeployableSpec)CollectionsKt.last(deployableSpecs)).getPreviewItem().method_7972();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"copy(...)");
        previewDeployable = new ActiveDeployable(deployableSpec, 300, class_17992, null, 8, null);
        deployableHud = HudModuleDSLKt.hudElement(INSTANCE, "deployable-hud", "Deployable HUD", "Shows the active SkyBlock deployable", (Function1<? super HudElementBuilder, Unit>)((Function1)DeployableHudModule::deployableHud$lambda$0));
        objectArray = new Setting[]{enabledSetting, displayStyle, liveIconSetting, previewInEditor, showStatsInCompact};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
        DEPLOYABLE_SECONDS_REGEX = new Regex(".*?(\\d+)s$");
        TOTEM_SECONDS_REGEX = new Regex("Remaining:\\s*(?:(\\d{1,2})m\\s*)?(\\d{1,2})s", RegexOption.IGNORE_CASE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0082\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0012\u0010\u0012\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J:\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\bH\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u000fJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u000fR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\"\u001a\u0004\b#\u0010\u0011R\u0019\u0010\t\u001a\u0004\u0018\u00010\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010$\u001a\u0004\b%\u0010\u0013\u00a8\u0006&"}, d2={"Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "", "Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "spec", "", "seconds", "Lnet/minecraft/class_1799;", "iconStack", "Ljava/util/UUID;", "sourceId", "<init>", "(Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;ILnet/minecraft/class_1799;Ljava/util/UUID;)V", "component1", "()Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "component2", "()I", "component3", "()Lnet/minecraft/class_1799;", "component4", "()Ljava/util/UUID;", "copy", "(Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;ILnet/minecraft/class_1799;Ljava/util/UUID;)Lorg/cobalt/internal/visual/DeployableHudModule$ActiveDeployable;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "getSpec", "I", "getSeconds", "Lnet/minecraft/class_1799;", "getIconStack", "Ljava/util/UUID;", "getSourceId", "cobalt"})
    private static final class ActiveDeployable {
        @NotNull
        private final DeployableSpec spec;
        private final int seconds;
        @NotNull
        private final class_1799 iconStack;
        @Nullable
        private final UUID sourceId;

        public ActiveDeployable(@NotNull DeployableSpec spec, int seconds, @NotNull class_1799 iconStack, @Nullable UUID sourceId) {
            Intrinsics.checkNotNullParameter((Object)spec, (String)"spec");
            Intrinsics.checkNotNullParameter((Object)iconStack, (String)"iconStack");
            this.spec = spec;
            this.seconds = seconds;
            this.iconStack = iconStack;
            this.sourceId = sourceId;
        }

        public /* synthetic */ ActiveDeployable(DeployableSpec deployableSpec, int n, class_1799 class_17992, UUID uUID, int n2, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n2 & 8) != 0) {
                uUID = null;
            }
            this(deployableSpec, n, class_17992, uUID);
        }

        @NotNull
        public final DeployableSpec getSpec() {
            return this.spec;
        }

        public final int getSeconds() {
            return this.seconds;
        }

        @NotNull
        public final class_1799 getIconStack() {
            return this.iconStack;
        }

        @Nullable
        public final UUID getSourceId() {
            return this.sourceId;
        }

        @NotNull
        public final DeployableSpec component1() {
            return this.spec;
        }

        public final int component2() {
            return this.seconds;
        }

        @NotNull
        public final class_1799 component3() {
            return this.iconStack;
        }

        @Nullable
        public final UUID component4() {
            return this.sourceId;
        }

        @NotNull
        public final ActiveDeployable copy(@NotNull DeployableSpec spec, int seconds, @NotNull class_1799 iconStack, @Nullable UUID sourceId) {
            Intrinsics.checkNotNullParameter((Object)spec, (String)"spec");
            Intrinsics.checkNotNullParameter((Object)iconStack, (String)"iconStack");
            return new ActiveDeployable(spec, seconds, iconStack, sourceId);
        }

        public static /* synthetic */ ActiveDeployable copy$default(ActiveDeployable activeDeployable, DeployableSpec deployableSpec, int n, class_1799 class_17992, UUID uUID, int n2, Object object) {
            if ((n2 & 1) != 0) {
                deployableSpec = activeDeployable.spec;
            }
            if ((n2 & 2) != 0) {
                n = activeDeployable.seconds;
            }
            if ((n2 & 4) != 0) {
                class_17992 = activeDeployable.iconStack;
            }
            if ((n2 & 8) != 0) {
                uUID = activeDeployable.sourceId;
            }
            return activeDeployable.copy(deployableSpec, n, class_17992, uUID);
        }

        @NotNull
        public String toString() {
            return "ActiveDeployable(spec=" + this.spec + ", seconds=" + this.seconds + ", iconStack=" + this.iconStack + ", sourceId=" + this.sourceId + ")";
        }

        public int hashCode() {
            int result = this.spec.hashCode();
            result = result * 31 + Integer.hashCode(this.seconds);
            result = result * 31 + this.iconStack.hashCode();
            result = result * 31 + (this.sourceId == null ? 0 : this.sourceId.hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ActiveDeployable)) {
                return false;
            }
            ActiveDeployable activeDeployable = (ActiveDeployable)other;
            if (!Intrinsics.areEqual((Object)this.spec, (Object)activeDeployable.spec)) {
                return false;
            }
            if (this.seconds != activeDeployable.seconds) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.iconStack, (Object)activeDeployable.iconStack)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.sourceId, (Object)activeDeployable.sourceId);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;", "", "<init>", "(Ljava/lang/String;I)V", "ORB", "FLARE", "LANTERN", "TOTEM", "MISC", "cobalt"})
    private static final class DeployableCategory
    extends Enum<DeployableCategory> {
        public static final /* enum */ DeployableCategory ORB = new DeployableCategory();
        public static final /* enum */ DeployableCategory FLARE = new DeployableCategory();
        public static final /* enum */ DeployableCategory LANTERN = new DeployableCategory();
        public static final /* enum */ DeployableCategory TOTEM = new DeployableCategory();
        public static final /* enum */ DeployableCategory MISC = new DeployableCategory();
        private static final /* synthetic */ DeployableCategory[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static DeployableCategory[] values() {
            return (DeployableCategory[])$VALUES.clone();
        }

        public static DeployableCategory valueOf(String value) {
            return Enum.valueOf(DeployableCategory.class, value);
        }

        @NotNull
        public static EnumEntries<DeployableCategory> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = deployableCategoryArray = new DeployableCategory[]{DeployableCategory.ORB, DeployableCategory.FLARE, DeployableCategory.LANTERN, DeployableCategory.TOTEM, DeployableCategory.MISC};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0015\b\u0082\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\b\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00020\r\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0002\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0015J\u0010\u0010\u0017\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0015J\u0010\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0010\u0010\u001e\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u001e\u0010\u001bJ\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00020\rH\u00c6\u0003\u00a2\u0006\u0004\b\u001f\u0010 J\u0012\u0010!\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b!\u0010\u0015J\u0010\u0010\"\u001a\u00020\u0010H\u00c6\u0003\u00a2\u0006\u0004\b\"\u0010#J|\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\b2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00020\r2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u0010H\u00c6\u0001\u00a2\u0006\u0004\b$\u0010%J\u001b\u0010(\u001a\u00020'2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b(\u0010)J\u0011\u0010*\u001a\u00020\bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b*\u0010\u001bJ\u0011\u0010+\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b+\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010,\u001a\u0004\b-\u0010\u0015R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010,\u001a\u0004\b.\u0010\u0015R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010,\u001a\u0004\b/\u0010\u0015R\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u00100\u001a\u0004\b1\u0010\u0019R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u00102\u001a\u0004\b3\u0010\u001bR\u0017\u0010\u000b\u001a\u00020\n8\u0006\u00a2\u0006\f\n\u0004\b\u000b\u00104\u001a\u0004\b5\u0010\u001dR\u0017\u0010\f\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\f\u00102\u001a\u0004\b6\u0010\u001bR\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00020\r8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u00107\u001a\u0004\b8\u0010 R\u0019\u0010\u000f\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010,\u001a\u0004\b9\u0010\u0015R\u0017\u0010\u0011\u001a\u00020\u00108\u0006\u00a2\u0006\f\n\u0004\b\u0011\u0010:\u001a\u0004\b;\u0010#\u00a8\u0006<"}, d2={"Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "", "", "key", "displayPrefix", "compactName", "Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;", "category", "", "priority", "", "rangeSq", "accent", "", "statLines", "flareTextureId", "Lnet/minecraft/class_1799;", "previewItem", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;IDILjava/util/List;Ljava/lang/String;Lnet/minecraft/class_1799;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "component4", "()Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;", "component5", "()I", "component6", "()D", "component7", "component8", "()Ljava/util/List;", "component9", "component10", "()Lnet/minecraft/class_1799;", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;IDILjava/util/List;Ljava/lang/String;Lnet/minecraft/class_1799;)Lorg/cobalt/internal/visual/DeployableHudModule$DeployableSpec;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getKey", "getDisplayPrefix", "getCompactName", "Lorg/cobalt/internal/visual/DeployableHudModule$DeployableCategory;", "getCategory", "I", "getPriority", "D", "getRangeSq", "getAccent", "Ljava/util/List;", "getStatLines", "getFlareTextureId", "Lnet/minecraft/class_1799;", "getPreviewItem", "cobalt"})
    private static final class DeployableSpec {
        @NotNull
        private final String key;
        @NotNull
        private final String displayPrefix;
        @NotNull
        private final String compactName;
        @NotNull
        private final DeployableCategory category;
        private final int priority;
        private final double rangeSq;
        private final int accent;
        @NotNull
        private final List<String> statLines;
        @Nullable
        private final String flareTextureId;
        @NotNull
        private final class_1799 previewItem;

        public DeployableSpec(@NotNull String key, @NotNull String displayPrefix, @NotNull String compactName, @NotNull DeployableCategory category, int priority, double rangeSq, int accent, @NotNull List<String> statLines, @Nullable String flareTextureId, @NotNull class_1799 previewItem) {
            Intrinsics.checkNotNullParameter((Object)key, (String)"key");
            Intrinsics.checkNotNullParameter((Object)displayPrefix, (String)"displayPrefix");
            Intrinsics.checkNotNullParameter((Object)compactName, (String)"compactName");
            Intrinsics.checkNotNullParameter((Object)((Object)category), (String)"category");
            Intrinsics.checkNotNullParameter(statLines, (String)"statLines");
            Intrinsics.checkNotNullParameter((Object)previewItem, (String)"previewItem");
            this.key = key;
            this.displayPrefix = displayPrefix;
            this.compactName = compactName;
            this.category = category;
            this.priority = priority;
            this.rangeSq = rangeSq;
            this.accent = accent;
            this.statLines = statLines;
            this.flareTextureId = flareTextureId;
            this.previewItem = previewItem;
        }

        public /* synthetic */ DeployableSpec(String string, String string2, String string3, DeployableCategory deployableCategory, int n, double d, int n2, List list, String string4, class_1799 class_17992, int n3, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n3 & 0x100) != 0) {
                string4 = null;
            }
            if ((n3 & 0x200) != 0) {
                class_17992 = new class_1799((class_1935)class_1802.field_8668);
            }
            this(string, string2, string3, deployableCategory, n, d, n2, list, string4, class_17992);
        }

        @NotNull
        public final String getKey() {
            return this.key;
        }

        @NotNull
        public final String getDisplayPrefix() {
            return this.displayPrefix;
        }

        @NotNull
        public final String getCompactName() {
            return this.compactName;
        }

        @NotNull
        public final DeployableCategory getCategory() {
            return this.category;
        }

        public final int getPriority() {
            return this.priority;
        }

        public final double getRangeSq() {
            return this.rangeSq;
        }

        public final int getAccent() {
            return this.accent;
        }

        @NotNull
        public final List<String> getStatLines() {
            return this.statLines;
        }

        @Nullable
        public final String getFlareTextureId() {
            return this.flareTextureId;
        }

        @NotNull
        public final class_1799 getPreviewItem() {
            return this.previewItem;
        }

        @NotNull
        public final String component1() {
            return this.key;
        }

        @NotNull
        public final String component2() {
            return this.displayPrefix;
        }

        @NotNull
        public final String component3() {
            return this.compactName;
        }

        @NotNull
        public final DeployableCategory component4() {
            return this.category;
        }

        public final int component5() {
            return this.priority;
        }

        public final double component6() {
            return this.rangeSq;
        }

        public final int component7() {
            return this.accent;
        }

        @NotNull
        public final List<String> component8() {
            return this.statLines;
        }

        @Nullable
        public final String component9() {
            return this.flareTextureId;
        }

        @NotNull
        public final class_1799 component10() {
            return this.previewItem;
        }

        @NotNull
        public final DeployableSpec copy(@NotNull String key, @NotNull String displayPrefix, @NotNull String compactName, @NotNull DeployableCategory category, int priority, double rangeSq, int accent, @NotNull List<String> statLines, @Nullable String flareTextureId, @NotNull class_1799 previewItem) {
            Intrinsics.checkNotNullParameter((Object)key, (String)"key");
            Intrinsics.checkNotNullParameter((Object)displayPrefix, (String)"displayPrefix");
            Intrinsics.checkNotNullParameter((Object)compactName, (String)"compactName");
            Intrinsics.checkNotNullParameter((Object)((Object)category), (String)"category");
            Intrinsics.checkNotNullParameter(statLines, (String)"statLines");
            Intrinsics.checkNotNullParameter((Object)previewItem, (String)"previewItem");
            return new DeployableSpec(key, displayPrefix, compactName, category, priority, rangeSq, accent, statLines, flareTextureId, previewItem);
        }

        public static /* synthetic */ DeployableSpec copy$default(DeployableSpec deployableSpec, String string, String string2, String string3, DeployableCategory deployableCategory, int n, double d, int n2, List list, String string4, class_1799 class_17992, int n3, Object object) {
            if ((n3 & 1) != 0) {
                string = deployableSpec.key;
            }
            if ((n3 & 2) != 0) {
                string2 = deployableSpec.displayPrefix;
            }
            if ((n3 & 4) != 0) {
                string3 = deployableSpec.compactName;
            }
            if ((n3 & 8) != 0) {
                deployableCategory = deployableSpec.category;
            }
            if ((n3 & 0x10) != 0) {
                n = deployableSpec.priority;
            }
            if ((n3 & 0x20) != 0) {
                d = deployableSpec.rangeSq;
            }
            if ((n3 & 0x40) != 0) {
                n2 = deployableSpec.accent;
            }
            if ((n3 & 0x80) != 0) {
                list = deployableSpec.statLines;
            }
            if ((n3 & 0x100) != 0) {
                string4 = deployableSpec.flareTextureId;
            }
            if ((n3 & 0x200) != 0) {
                class_17992 = deployableSpec.previewItem;
            }
            return deployableSpec.copy(string, string2, string3, deployableCategory, n, d, n2, list, string4, class_17992);
        }

        @NotNull
        public String toString() {
            return "DeployableSpec(key=" + this.key + ", displayPrefix=" + this.displayPrefix + ", compactName=" + this.compactName + ", category=" + this.category + ", priority=" + this.priority + ", rangeSq=" + this.rangeSq + ", accent=" + this.accent + ", statLines=" + this.statLines + ", flareTextureId=" + this.flareTextureId + ", previewItem=" + this.previewItem + ")";
        }

        public int hashCode() {
            int result = this.key.hashCode();
            result = result * 31 + this.displayPrefix.hashCode();
            result = result * 31 + this.compactName.hashCode();
            result = result * 31 + this.category.hashCode();
            result = result * 31 + Integer.hashCode(this.priority);
            result = result * 31 + Double.hashCode(this.rangeSq);
            result = result * 31 + Integer.hashCode(this.accent);
            result = result * 31 + ((Object)this.statLines).hashCode();
            result = result * 31 + (this.flareTextureId == null ? 0 : this.flareTextureId.hashCode());
            result = result * 31 + this.previewItem.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DeployableSpec)) {
                return false;
            }
            DeployableSpec deployableSpec = (DeployableSpec)other;
            if (!Intrinsics.areEqual((Object)this.key, (Object)deployableSpec.key)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.displayPrefix, (Object)deployableSpec.displayPrefix)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.compactName, (Object)deployableSpec.compactName)) {
                return false;
            }
            if (this.category != deployableSpec.category) {
                return false;
            }
            if (this.priority != deployableSpec.priority) {
                return false;
            }
            if (Double.compare(this.rangeSq, deployableSpec.rangeSq) != 0) {
                return false;
            }
            if (this.accent != deployableSpec.accent) {
                return false;
            }
            if (!Intrinsics.areEqual(this.statLines, deployableSpec.statLines)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.flareTextureId, (Object)deployableSpec.flareTextureId)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.previewItem, (Object)deployableSpec.previewItem);
        }
    }
}

