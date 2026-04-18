/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 *  net.minecraft.class_642
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.hud.modules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_642;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.render.HudGlassBlurRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\bA\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 v2\u00020\u0001:\u0004wxyvB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ7\u0010\u0012\u001a\u00020\u00112\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J%\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u000e2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J7\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJE\u0010 \u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u001f\u001a\u00020\u00112\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00142\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002\u00a2\u0006\u0004\b \u0010!J7\u0010%\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u00192\u0006\u0010#\u001a\u00020\u00192\u0006\u0010$\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b%\u0010&J'\u0010(\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010'\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\b(\u0010)J?\u0010-\u001a\u00020\u00062\u0006\u0010*\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010$\u001a\u00020\u00192\u0006\u0010+\u001a\u00020\u00142\u0006\u0010,\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b-\u0010.JE\u00101\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010/\u001a\u00020\u00192\u0006\u00100\u001a\u00020\u00192\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b1\u00102J7\u00104\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010/\u001a\u00020\u00192\u0006\u00103\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b4\u00105J/\u00107\u001a\u00020\u00062\u0006\u00106\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010*\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b7\u00108J\u001d\u00109\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b9\u0010:J\u0017\u0010<\u001a\u00020\u000b2\u0006\u0010;\u001a\u00020\u0001H\u0002\u00a2\u0006\u0004\b<\u0010=J\u0017\u0010>\u001a\u00020\u000b2\u0006\u0010;\u001a\u00020\u0001H\u0002\u00a2\u0006\u0004\b>\u0010=J!\u0010@\u001a\u0004\u0018\u00010\u000b2\u0006\u0010;\u001a\u00020\u00012\u0006\u0010?\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b@\u0010AJ\u0017\u0010B\u001a\u00020\t2\u0006\u0010;\u001a\u00020\u0001H\u0002\u00a2\u0006\u0004\bB\u0010CJ\u000f\u0010D\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bD\u0010EJ\u000f\u0010F\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bF\u0010EJ\u000f\u0010G\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bG\u0010EJ\u000f\u0010H\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bH\u0010EJ\u001d\u0010I\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\bI\u0010:J\u001d\u0010J\u001a\u00020\u00192\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002\u00a2\u0006\u0004\bJ\u0010KJ\u0017\u0010M\u001a\u00020\u00192\u0006\u0010L\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\bM\u0010NJ\u0017\u0010O\u001a\u00020\u00192\u0006\u0010'\u001a\u00020\u0016H\u0002\u00a2\u0006\u0004\bO\u0010PJ\u0017\u0010Q\u001a\u00020\u00192\u0006\u0010*\u001a\u00020\tH\u0002\u00a2\u0006\u0004\bQ\u0010RJ'\u0010T\u001a\u00020\t2\u0006\u0010*\u001a\u00020\t2\u0006\u0010S\u001a\u00020\u00192\u0006\u0010$\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\bT\u0010UJ\u001f\u0010W\u001a\u00020\u00142\u0006\u0010+\u001a\u00020\u00142\u0006\u0010V\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\bW\u0010XJ\u000f\u0010Y\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bY\u0010\u0003J3\u0010^\u001a\u00020\u0019\"\u0004\b\u0000\u0010Z*\b\u0012\u0004\u0012\u00028\u00000[2\u0012\u0010]\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00190\\H\u0002\u00a2\u0006\u0004\b^\u0010_R\u0014\u0010a\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010bR\u001c\u0010d\u001a\n c*\u0004\u0018\u00010\t0\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u0010eR\u0016\u0010f\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bf\u0010gR\u0016\u0010i\u001a\u00020h8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bi\u0010jR\u0016\u0010k\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bk\u0010gR\u0016\u0010l\u001a\u00020h8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bl\u0010jR\u0016\u0010m\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bm\u0010gR\u0016\u0010n\u001a\u00020h8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bn\u0010jR\u001c\u0010o\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bo\u0010pR\u0017\u0010r\u001a\u00020q8\u0006\u00a2\u0006\f\n\u0004\br\u0010s\u001a\u0004\bt\u0010u\u00a8\u0006z"}, d2={"Lorg/cobalt/api/hud/modules/WatermarkModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "", "brand", "", "showPanel", "backgroundEnabled", "", "Lorg/cobalt/api/hud/modules/WatermarkModule$MacroRow;", "rows", "Lorg/cobalt/api/hud/modules/WatermarkModule$Layout;", "computeLayout", "(Ljava/lang/String;ZZLjava/util/List;)Lorg/cobalt/api/hud/modules/WatermarkModule$Layout;", "", "accent", "Lorg/cobalt/api/hud/modules/WatermarkModule$SegmentSpec;", "buildSegments", "(Ljava/lang/String;I)Ljava/util/List;", "", "x", "y", "shadow", "renderSimpleText", "(FFLjava/lang/String;IZ)V", "layout", "renderWatermarkCard", "(FFLorg/cobalt/api/hud/modules/WatermarkModule$Layout;Ljava/lang/String;ILjava/util/List;)V", "cx", "cy", "size", "renderBadge", "(FFFILjava/lang/String;)V", "segment", "renderSegment", "(FFLorg/cobalt/api/hud/modules/WatermarkModule$SegmentSpec;)V", "text", "color", "glowColor", "renderAnimatedGlowText", "(Ljava/lang/String;FFFII)V", "width", "visibleHeight", "renderMacroSection", "(FFFFLjava/util/List;I)V", "row", "renderMacroRow", "(FFFLorg/cobalt/api/hud/modules/WatermarkModule$MacroRow;I)V", "rightX", "renderHeaderPill", "(FFLjava/lang/String;I)V", "resolveMacroRows", "(Z)Ljava/util/List;", "module", "isMacroCandidate", "(Lorg/cobalt/api/module/Module;)Z", "isModuleActive", "methodName", "readBooleanFlag", "(Lorg/cobalt/api/module/Module;Ljava/lang/String;)Ljava/lang/Boolean;", "resolveKeybind", "(Lorg/cobalt/api/module/Module;)Ljava/lang/String;", "currentFpsText", "()Ljava/lang/String;", "currentTpsText", "currentServerHost", "currentPingValueText", "macroRowsForLayout", "macroContentHeight", "(Ljava/util/List;)F", "expanded", "updateMacroExpansion", "(Z)F", "segmentWidth", "(Lorg/cobalt/api/hud/modules/WatermarkModule$SegmentSpec;)F", "keyPillWidth", "(Ljava/lang/String;)F", "maxWidth", "ellipsize", "(Ljava/lang/String;FF)Ljava/lang/String;", "alpha", "withAlpha", "(II)I", "sampleFrameRate", "T", "", "Lkotlin/Function1;", "selector", "sumOfFloat", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;)F", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "kotlin.jvm.PlatformType", "clientVersion", "Ljava/lang/String;", "smoothedTps", "F", "", "lastTickNs", "J", "smoothedFps", "lastFrameNs", "macroExpand", "lastMacroAnimNs", "retainedMacroRows", "Ljava/util/List;", "Lorg/cobalt/api/hud/HudElement;", "watermark", "Lorg/cobalt/api/hud/HudElement;", "getWatermark", "()Lorg/cobalt/api/hud/HudElement;", "Companion", "SegmentSpec", "MacroRow", "Layout", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWatermarkModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WatermarkModule.kt\norg/cobalt/api/hud/modules/WatermarkModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,686:1\n1924#2,3:687\n1924#2,3:691\n777#2:694\n873#2,2:695\n1642#2,10:697\n1915#2:707\n1916#2:709\n1652#2:710\n1586#2:711\n1661#2,3:712\n812#2,12:715\n296#2,2:727\n812#2,12:731\n296#2,2:743\n812#2,12:745\n1#3:690\n1#3:708\n1401#4,2:729\n*S KotlinDebug\n*F\n+ 1 WatermarkModule.kt\norg/cobalt/api/hud/modules/WatermarkModule\n*L\n329#1:687,3\n447#1:691,3\n503#1:694\n503#1:695,2\n506#1:697,10\n506#1:707\n506#1:709\n506#1:710\n520#1:711\n520#1:712,3\n542#1:715,12\n543#1:727,2\n562#1:731,12\n563#1:743,2\n564#1:745,12\n506#1:708\n550#1:729,2\n*E\n"})
public final class WatermarkModule
extends Module {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final class_310 mc;
    private final String clientVersion;
    private float smoothedTps;
    private long lastTickNs;
    private float smoothedFps;
    private long lastFrameNs;
    private float macroExpand;
    private long lastMacroAnimNs;
    @NotNull
    private List<MacroRow> retainedMacroRows;
    @NotNull
    private final HudElement watermark;
    private static final float SIMPLE_TEXT_SIZE = 18.0f;
    private static final float CARD_WIDTH = 428.0f;
    private static final float MAIN_BAR_HEIGHT = 30.0f;
    private static final float CARD_CORNER = 12.0f;
    private static final float MAIN_BAR_PAD_X = 10.0f;
    private static final float BADGE_SIZE = 18.0f;
    private static final float BADGE_GAP = 9.0f;
    private static final float SEGMENT_GAP = 10.0f;
    private static final float SEGMENT_VALUE_SIZE = 11.6f;
    private static final float SEGMENT_LABEL_SIZE = 10.3f;
    private static final float SEGMENT_INNER_GAP = 4.0f;
    private static final float SERVER_SEGMENT_MAX_WIDTH = 94.0f;
    private static final float PANEL_PAD_X = 12.0f;
    private static final float PANEL_PAD_Y = 10.0f;
    private static final float PANEL_HEADER_HEIGHT = 22.0f;
    private static final float PANEL_ROW_HEIGHT = 17.0f;
    private static final float PANEL_ROW_GAP = 6.0f;
    private static final float HEADER_TITLE_SIZE = 10.5f;
    private static final float HEADER_SUBTITLE_SIZE = 9.0f;
    private static final float ROW_LABEL_SIZE = 10.8f;
    private static final float KEY_PILL_TEXT_SIZE = 9.4f;
    private static final int WHITE_TEXT = -856074;
    private static final int MUTED_TEXT = -1718711915;
    private static final float GLASS_BLUR_STRENGTH = 1.35f;
    private static final int CARD_BG = 1276383512;
    private static final int SECTION_TINT = 521803552;

    public WatermarkModule() {
        super("Watermark");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        this.mc = class_3102;
        this.clientVersion = FabricLoader.getInstance().getModContainer("cobalt").map(arg_0 -> WatermarkModule.clientVersion$lambda$1(WatermarkModule::clientVersion$lambda$0, arg_0)).orElse("dev");
        this.smoothedTps = 20.0f;
        this.smoothedFps = 60.0f;
        this.retainedMacroRows = CollectionsKt.emptyList();
        EventBus.register(this);
        this.watermark = HudModuleDSLKt.hudElement(this, "watermark", "Watermark", "Displays Dutt Client branding", (Function1<? super HudElementBuilder, Unit>)((Function1)arg_0 -> WatermarkModule.watermark$lambda$0(this, arg_0)));
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 level2 = this.mc.field_1687;
        if (level2 == null) {
            this.smoothedTps = 20.0f;
            this.lastTickNs = 0L;
            return;
        }
        long now = System.nanoTime();
        if (this.lastTickNs != 0L) {
            long deltaNs = now - this.lastTickNs;
            double deltaMs = (double)deltaNs / 1000000.0;
            boolean bl = 5.0 <= deltaMs ? deltaMs <= 250.0 : false;
            if (bl) {
                float sample = RangesKt.coerceIn((float)((float)(1000.0 / deltaMs)), (float)0.0f, (float)20.0f);
                this.smoothedTps = this.smoothedTps * 0.82f + sample * 0.18f;
            }
        }
        this.lastTickNs = now;
    }

    @NotNull
    public final HudElement getWatermark() {
        return this.watermark;
    }

    private final Layout computeLayout(String brand, boolean showPanel, boolean backgroundEnabled, List<MacroRow> rows) {
        if (!backgroundEnabled) {
            return new Layout(NVGRenderer.textWidth$default(brand, 18.0f, null, 4, null), 18.0f, 0.0f, 0.0f);
        }
        float macroFullHeight = showPanel && !((Collection)rows).isEmpty() ? this.macroContentHeight(rows) : 0.0f;
        return new Layout(428.0f, 30.0f, macroFullHeight, macroFullHeight * this.macroExpand);
    }

    static /* synthetic */ Layout computeLayout$default(WatermarkModule watermarkModule, String string, boolean bl, boolean bl2, List list, int n, Object object) {
        if ((n & 8) != 0) {
            list = watermarkModule.macroRowsForLayout(bl);
        }
        return watermarkModule.computeLayout(string, bl, bl2, list);
    }

    private final List<SegmentSpec> buildSegments(String brand, int accent) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        String fpsText = this.currentFpsText();
        String tpsText = this.currentTpsText();
        String host = this.ellipsize(this.currentServerHost(), 94.0f, 11.6f);
        String ping = this.currentPingValueText();
        Object[] objectArray = new SegmentSpec[]{new SegmentSpec(this.ellipsize(brand, 92.0f, 12.3f), null, accent, 0, 12.3f, 0.0f, accent, 42, null), new SegmentSpec("prod", "v" + this.clientVersion, theme.getAccentSecondary(), 0, 0.0f, 0.0f, theme.getAccentSecondary(), 56, null), new SegmentSpec(fpsText, "FPS", -856074, 0, 0.0f, 0.0f, null, 120, null), new SegmentSpec(tpsText, "TPS", -856074, 0, 0.0f, 0.0f, null, 120, null), new SegmentSpec(ping, "MS", -856074, 0, 0.0f, 0.0f, null, 120, null), new SegmentSpec(host, "SERVER", -856074, 0, 0.0f, 0.0f, null, 120, null)};
        return CollectionsKt.listOf((Object[])objectArray);
    }

    private final void renderSimpleText(float x, float y, String brand, int accent, boolean shadow) {
        if (shadow) {
            NVGRenderer.textShadow$default(brand, x, y, 18.0f, accent, null, 32, null);
        } else {
            NVGRenderer.text$default(brand, x, y, 18.0f, accent, null, 32, null);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void renderWatermarkCard(float x, float y, Layout layout, String brand, int accent, List<MacroRow> rows) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        long now = System.currentTimeMillis();
        float width = layout.getWidth();
        float totalHeight = RangesKt.coerceAtLeast((float)layout.getTotalHeight(), (float)30.0f);
        float shiftX = (float)Math.cos((float)(now % 10000L) / 10000.0f * ((float)Math.PI * 2)) * (width * 0.32f);
        NVGRenderer.rect(x + 3.0f, y + 2.0f, width, totalHeight, 0x10000000, 13.0f);
        NVGRenderer.rect(x, y, width, totalHeight, CARD_BG, 12.0f);
        NVGRenderer.gradientRect(x, y, width, totalHeight, 0x18FFFFFF, 521803552, Gradient.TopToBottom, 12.0f);
        NVGRenderer.gradientRect(x, y, width, 30.0f + layout.getMacroVisibleHeight() * 0.55f, 0x2EFFFFFF, 0, Gradient.TopToBottom, 12.0f);
        NVGRenderer.hollowGradientRectShifted(x, y, width, totalHeight, 1.2f, this.withAlpha(accent, 96), this.withAlpha(theme.getAccentSecondary(), 82), Gradient.LeftToRight, 12.0f, shiftX, 0.0f);
        if (layout.getMacroVisibleHeight() > 0.75f) {
            NVGRenderer.rect(x + 14.0f, y + 30.0f - 0.5f, width - 28.0f, 1.0f, this.withAlpha(accent, (int)(28.0f + 42.0f * this.macroExpand)), 0.5f);
        }
        float badgeCenterX = x + 10.0f + 9.0f;
        float badgeCenterY = y + 15.0f;
        this.renderBadge(badgeCenterX, badgeCenterY, 18.0f, accent, brand);
        float cursorX = 0.0f;
        cursorX = x + 10.0f + 18.0f + 9.0f;
        List<SegmentSpec> segments = this.buildSegments(brand, accent);
        Iterable $this$forEachIndexed$iv = segments;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void segment;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            SegmentSpec segmentSpec = (SegmentSpec)item$iv;
            int index = n;
            boolean bl = false;
            this.renderSegment(cursorX, y + 9.0f, (SegmentSpec)segment);
            cursorX += this.segmentWidth((SegmentSpec)segment);
            if (index == CollectionsKt.getLastIndex(segments)) continue;
            float lineX = cursorX + 5.0f;
            NVGRenderer.rect(lineX, y + 7.0f, 1.0f, 16.0f, 0x19FFFFFF, 0.5f);
            cursorX += 10.0f;
        }
        if (!((Collection)rows).isEmpty() && layout.getMacroVisibleHeight() > 0.5f) {
            this.renderMacroSection(x, y + 30.0f, width, layout.getMacroVisibleHeight(), rows, accent);
        }
        if (layout.getMacroVisibleHeight() > 0.0f) {
            NVGRenderer.circle(x + width - 34.0f, y + totalHeight - 18.0f, 9.0f, this.withAlpha(theme.getAccentSecondary(), (int)(8.0f + 12.0f * this.macroExpand)));
        }
    }

    private final void renderBadge(float cx, float cy, float size, int accent, String brand) {
        float radius = size / 2.0f;
        int ringColor = this.withAlpha(accent, 90);
        int innerColor = -1507713754;
        Object object = StringsKt.firstOrNull((CharSequence)((Object)StringsKt.trim((CharSequence)brand)).toString());
        if (object == null || (object = String.valueOf(Character.toUpperCase(((Character)object).charValue()))) == null) {
            object = "C";
        }
        Object initial = object;
        NVGRenderer.circle(cx, cy, radius + 2.3f, 0x17000000);
        NVGRenderer.circle(cx, cy, radius + 0.8f, ringColor);
        NVGRenderer.circle(cx, cy, radius - 0.8f, innerColor);
        NVGRenderer.circle(cx - radius * 0.28f, cy - radius * 0.28f, radius * 0.42f, 0x2AFFFFFF);
        NVGRenderer.circle(cx + radius * 0.06f, cy + radius * 0.12f, radius * 0.78f, 0xE000000);
        float glyphWidth = NVGRenderer.textWidth$default((String)initial, 10.8f, null, 4, null);
        NVGRenderer.text$default((String)initial, cx - glyphWidth / 2.0f, cy - 5.2f, 10.8f, -856074, null, 32, null);
    }

    private final void renderSegment(float x, float y, SegmentSpec segment) {
        String string;
        block6: {
            block5: {
                String string2;
                if (segment.getGlowColor() != null) {
                    this.renderAnimatedGlowText(segment.getPrimary(), x, y, segment.getPrimarySize(), segment.getPrimaryColor(), segment.getGlowColor());
                } else {
                    NVGRenderer.text$default(segment.getPrimary(), x, y, segment.getPrimarySize(), segment.getPrimaryColor(), null, 32, null);
                }
                if ((string = segment.getSuffix()) == null) break block5;
                String it = string2 = string;
                boolean bl = false;
                string = !StringsKt.isBlank((CharSequence)it) ? string2 : null;
                if (string != null) break block6;
            }
            return;
        }
        String suffix = string;
        float primaryWidth = NVGRenderer.textWidth$default(segment.getPrimary(), segment.getPrimarySize(), null, 4, null);
        NVGRenderer.text$default(suffix, x + primaryWidth + 4.0f, y + (segment.getPrimarySize() - segment.getSuffixSize()) * 0.35f, segment.getSuffixSize(), segment.getSuffixColor(), null, 32, null);
    }

    private final void renderAnimatedGlowText(String text, float x, float y, float size, int color, int glowColor) {
        float pulse = ((float)Math.sin((float)(System.currentTimeMillis() % 2300L) / 2300.0f * ((float)Math.PI * 2)) + 1.0f) * 0.5f;
        int glowAlpha = (int)(22.0f + pulse * 22.0f);
        int shimmerAlpha = (int)(12.0f + pulse * 14.0f);
        NVGRenderer.text$default(text, x - 0.7f, y, size, this.withAlpha(glowColor, glowAlpha), null, 32, null);
        NVGRenderer.text$default(text, x + 0.7f, y, size, this.withAlpha(glowColor, glowAlpha), null, 32, null);
        NVGRenderer.text$default(text, x, y - 0.7f, size, this.withAlpha(glowColor, glowAlpha), null, 32, null);
        NVGRenderer.text$default(text, x, y + 0.7f, size, this.withAlpha(glowColor, glowAlpha), null, 32, null);
        NVGRenderer.text$default(text, x, y - 0.1f, size, this.withAlpha(-856074, shimmerAlpha), null, 32, null);
        NVGRenderer.text$default(text, x, y, size, color, null, 32, null);
    }

    /*
     * WARNING - void declaration
     */
    private final void renderMacroSection(float x, float y, float width, float visibleHeight, List<MacroRow> rows, int accent) {
        float contentAlpha = RangesKt.coerceIn((float)this.macroExpand, (float)0.0f, (float)1.0f);
        NVGRenderer.push();
        NVGRenderer.pushScissor(x + 1.0f, y, width - 2.0f, visibleHeight);
        NVGRenderer.globalAlpha(contentAlpha);
        float headerY = y + 10.0f;
        NVGRenderer.circle(x + 12.0f + 3.0f, headerY + 5.5f, 2.5f, this.withAlpha(accent, 176));
        NVGRenderer.text$default("ACTIVE MACROS", x + 12.0f + 11.0f, headerY, 10.5f, -1718711915, null, 32, null);
        this.renderHeaderPill(x + width - 12.0f, headerY - 2.0f, (String)(rows.size() == 1 ? "1 LIVE" : rows.size() + " LIVE"), accent);
        float rowY = 0.0f;
        rowY = y + 10.0f + 22.0f;
        Iterable $this$forEachIndexed$iv = rows;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void row;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            MacroRow macroRow = (MacroRow)item$iv;
            int index = n;
            boolean bl = false;
            this.renderMacroRow(x + 12.0f, rowY, width - 24.0f, (MacroRow)row, accent);
            rowY += 17.0f;
            if (index == CollectionsKt.getLastIndex(rows)) continue;
            NVGRenderer.rect(x + 12.0f, rowY + 1.0f, width - 24.0f, 1.0f, 0x10FFFFFF, 0.5f);
            rowY += 6.0f;
        }
        NVGRenderer.popScissor();
        NVGRenderer.pop();
    }

    private final void renderMacroRow(float x, float y, float width, MacroRow row, int accent) {
        float pillWidth = this.keyPillWidth(row.getKeybind());
        float pillX = x + width - pillWidth;
        float labelMaxWidth = width - pillWidth - 12.0f;
        String label = this.ellipsize(row.getLabel(), labelMaxWidth, 10.8f);
        NVGRenderer.text$default(label, x, y + 1.0f, 10.8f, -856074, null, 32, null);
        NVGRenderer.rect(pillX, y - 2.0f, pillWidth, 15.0f, this.withAlpha(accent, 18), 7.0f);
        NVGRenderer.hollowRect(pillX, y - 2.0f, pillWidth, 15.0f, 1.0f, this.withAlpha(accent, 78), 7.0f);
        NVGRenderer.text$default(row.getKeybind(), pillX + (pillWidth - NVGRenderer.textWidth$default(row.getKeybind(), 9.4f, null, 4, null)) / 2.0f, y + 1.0f, 9.4f, -856074, null, 32, null);
    }

    private final void renderHeaderPill(float rightX, float y, String text, int accent) {
        float pillWidth = NVGRenderer.textWidth$default(text, 9.4f, null, 4, null) + 14.0f;
        float pillX = rightX - pillWidth;
        NVGRenderer.rect(pillX, y, pillWidth, 15.0f, this.withAlpha(accent, 16), 7.0f);
        NVGRenderer.hollowRect(pillX, y, pillWidth, 15.0f, 1.0f, this.withAlpha(accent, 72), 7.0f);
        NVGRenderer.text$default(text, pillX + (pillWidth - NVGRenderer.textWidth$default(text, 9.4f, null, 4, null)) / 2.0f, y + 3.0f, 9.4f, -856074, null, 32, null);
    }

    /*
     * WARNING - void declaration
     */
    private final List<MacroRow> resolveMacroRows(boolean showPanel) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Object element$iv$iv$iv;
        void $this$mapNotNullTo$iv$iv;
        void $this$mapNotNull$iv;
        void $this$filterTo$iv$iv;
        if (!showPanel) {
            return CollectionsKt.emptyList();
        }
        Iterable $this$filter$iv = ModuleManager.getModules();
        boolean $i$f$filter22 = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Module p0 = (Module)element$iv$iv;
            boolean bl = false;
            if (!this.isMacroCandidate(p0)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List macroModules = (List)destination$iv$iv;
        Iterable $i$f$filter22 = macroModules;
        boolean $i$f$mapNotNull22 = false;
        destination$iv$iv = $this$mapNotNull$iv;
        Collection destination$iv$iv2 = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator bl = $this$forEach$iv$iv$iv.iterator();
        while (bl.hasNext()) {
            MacroRow it$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = bl.next();
            boolean bl2 = false;
            Module module = (Module)element$iv$iv;
            boolean bl3 = false;
            if ((!this.isModuleActive(module) ? null : new MacroRow(module.getName(), this.resolveKeybind(module))) == null) continue;
            it$iv$iv = it$iv$iv;
            boolean bl4 = false;
            destination$iv$iv2.add(it$iv$iv);
        }
        List activeRows = (List)destination$iv$iv2;
        if (!((Collection)activeRows).isEmpty()) {
            return activeRows;
        }
        if (!HudModuleManager.INSTANCE.isEditorOpen()) {
            return CollectionsKt.emptyList();
        }
        Iterable $i$f$mapNotNull22 = CollectionsKt.take((Iterable)macroModules, (int)3);
        boolean $i$f$map = false;
        destination$iv$iv2 = $this$map$iv;
        Collection destination$iv$iv3 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void module;
            element$iv$iv$iv = (Module)item$iv$iv;
            Collection collection = destination$iv$iv3;
            boolean bl5 = false;
            collection.add(new MacroRow(module.getName(), this.resolveKeybind((Module)module)));
        }
        List previewRows = (List)destination$iv$iv3;
        return !((Collection)previewRows).isEmpty() ? previewRows : CollectionsKt.listOf((Object)new MacroRow("Mining Macro", "R"));
    }

    private final boolean isMacroCandidate(Module module) {
        String string = module.getName();
        Locale locale = Locale.US;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        String name = string2;
        return StringsKt.contains$default((CharSequence)name, (CharSequence)"macro", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)"patrol", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)"forge", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)name, (Object)"routes");
    }

    /*
     * WARNING - void declaration
     */
    private final boolean isModuleActive(Module module) {
        Object v0;
        Object object;
        block4: {
            void $this$filterIsInstanceTo$iv$iv;
            object = this.readBooleanFlag(module, "isRunning");
            if (object != null) {
                boolean it = (Boolean)object;
                boolean bl = false;
                return it;
            }
            object = this.readBooleanFlag(module, "isActive");
            if (object != null) {
                boolean it = (Boolean)object;
                boolean bl = false;
                return it;
            }
            Iterable $this$filterIsInstance$iv = module.getSettings();
            boolean $i$f$filterIsInstance = false;
            Iterable bl = $this$filterIsInstance$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterIsInstanceTo = false;
            for (Object element$iv$iv : $this$filterIsInstanceTo$iv$iv) {
                if (!(element$iv$iv instanceof CheckboxSetting)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            Iterable $this$firstOrNull$iv = (List)destination$iv$iv;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                CheckboxSetting it = (CheckboxSetting)element$iv;
                boolean bl2 = false;
                if (!StringsKt.equals((String)it.getName(), (String)"Enabled", (boolean)true)) continue;
                v0 = element$iv;
                break block4;
            }
            v0 = null;
        }
        object = v0;
        return object != null ? ((Boolean)((Setting)object).getValue()).booleanValue() : false;
    }

    /*
     * WARNING - void declaration
     */
    private final Boolean readBooleanFlag(Module module, String methodName) {
        Object object;
        Object object2 = this;
        try {
            Object object3;
            Object v1;
            block3: {
                void $this$firstOrNull$iv;
                WatermarkModule $this$readBooleanFlag_u24lambda_u240 = object2;
                boolean bl = false;
                Method[] methodArray = module.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] objectArray = methodArray;
                boolean $i$f$firstOrNull = false;
                for (void element$iv : $this$firstOrNull$iv) {
                    Method it = (Method)element$iv;
                    boolean bl2 = false;
                    if (!(Intrinsics.areEqual((Object)it.getName(), (Object)methodName) && it.getParameterCount() == 0 && (Intrinsics.areEqual(it.getReturnType(), Boolean.TYPE) || Intrinsics.areEqual(it.getReturnType(), Boolean.TYPE)))) continue;
                    v1 = element$iv;
                    break block3;
                }
                v1 = null;
            }
            Method method = v1;
            Object object4 = object3 = method != null ? method.invoke((Object)module, new Object[0]) : null;
            object = Result.constructor-impl((Object)(object3 instanceof Boolean ? (Boolean)object3 : null));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (Boolean)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    /*
     * WARNING - void declaration
     */
    private final String resolveKeybind(Module module) {
        String string;
        KeyBindSetting keyBindSetting;
        Object v0;
        Object element$iv$iv;
        Iterable $this$filterIsInstanceTo$iv$iv;
        Collection destination$iv$iv;
        Iterable $this$filterIsInstance$iv;
        boolean $i$f$filterIsInstance;
        block8: {
            void $this$firstOrNull$iv;
            Iterable iterable = module.getSettings();
            $i$f$filterIsInstance = false;
            void var6_4 = $this$filterIsInstance$iv;
            destination$iv$iv = new ArrayList();
            boolean $i$f$filterIsInstanceTo = false;
            Iterator iterator = $this$filterIsInstanceTo$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = iterator.next();
                if (!(element$iv$iv instanceof KeyBindSetting)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filterIsInstance$iv = (List)destination$iv$iv;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                KeyBindSetting it = (KeyBindSetting)element$iv;
                boolean bl = false;
                if (!StringsKt.contains((CharSequence)it.getName(), (CharSequence)"Toggle", (boolean)true)) continue;
                v0 = element$iv;
                break block8;
            }
            v0 = null;
        }
        if ((keyBindSetting = (KeyBindSetting)v0) == null) {
            $this$filterIsInstance$iv = module.getSettings();
            $i$f$filterIsInstance = false;
            $this$filterIsInstanceTo$iv$iv = $this$filterIsInstance$iv;
            destination$iv$iv = new ArrayList();
            boolean $i$f$filterIsInstanceTo = false;
            Iterator iterator = $this$filterIsInstanceTo$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = iterator.next();
                if (!(element$iv$iv instanceof KeyBindSetting)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            keyBindSetting = (KeyBindSetting)CollectionsKt.firstOrNull((List)((List)destination$iv$iv));
        }
        KeyBindSetting keybind = keyBindSetting;
        if (keybind == null) {
            string = "MANUAL";
        } else if (StringsKt.equals((String)keybind.getKeyName(), (String)"None", (boolean)true)) {
            string = "NONE";
        } else {
            String string2 = keybind.getKeyName();
            Locale locale = Locale.US;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"US");
            String string3 = string2.toUpperCase(locale);
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toUpperCase(...)");
        }
        return string;
    }

    private final String currentFpsText() {
        float fps = this.lastFrameNs != 0L ? this.smoothedFps : (HudModuleManager.INSTANCE.isEditorOpen() ? 10.0f : -1.0f);
        return fps > 0.0f ? String.valueOf((int)fps) : "--";
    }

    private final String currentTpsText() {
        String string;
        float tps;
        float f = this.mc.field_1687 != null ? this.smoothedTps : (tps = HudModuleManager.INSTANCE.isEditorOpen() ? 17.3f : -1.0f);
        if (tps >= 0.0f) {
            Locale locale = Locale.US;
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{Float.valueOf(RangesKt.coerceIn((float)tps, (float)0.0f, (float)20.0f))};
            String string3 = String.format(locale, string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        } else {
            string = "--";
        }
        return string;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final String currentServerHost() {
        String string;
        block6: {
            block5: {
                CharSequence charSequence;
                String string2;
                String string3;
                class_642 class_6422 = this.mc.method_1558();
                if (class_6422 == null || (string3 = class_6422.field_3761) == null || (string2 = StringsKt.substringBefore$default((String)string3, (char)':', null, (int)2, null)) == null) break block5;
                CharSequence charSequence2 = string2;
                if (StringsKt.isBlank((CharSequence)charSequence2)) {
                    boolean bl = false;
                    charSequence = null;
                } else {
                    charSequence = charSequence2;
                }
                string = (String)charSequence;
                if (string != null) break block6;
            }
            if (!HudModuleManager.INSTANCE.isEditorOpen()) return "local";
            return "mc.hypixel.net";
        }
        String string4 = string;
        return string4;
    }

    private final String currentPingValueText() {
        Integer n;
        class_746 player = this.mc.field_1724;
        if (player != null) {
            class_640 class_6402 = player.field_3944.method_2871(player.method_5667());
            n = class_6402 != null ? Integer.valueOf(class_6402.method_2959()) : null;
        } else {
            n = null;
        }
        Integer ping = n;
        return ping != null && ping >= 0 ? String.valueOf(ping) : (HudModuleManager.INSTANCE.isEditorOpen() ? "48" : "--");
    }

    private final List<MacroRow> macroRowsForLayout(boolean showPanel) {
        if (!showPanel) {
            return CollectionsKt.emptyList();
        }
        List<MacroRow> liveRows = this.resolveMacroRows(true);
        return !((Collection)liveRows).isEmpty() ? liveRows : (this.macroExpand > 0.01f && !((Collection)this.retainedMacroRows).isEmpty() ? this.retainedMacroRows : CollectionsKt.emptyList());
    }

    private final float macroContentHeight(List<MacroRow> rows) {
        if (rows.isEmpty()) {
            return 0.0f;
        }
        float rowAreaHeight = (float)rows.size() * 17.0f + (float)Math.max(0, rows.size() - 1) * 6.0f;
        return 42.0f + rowAreaHeight;
    }

    private final float updateMacroExpansion(boolean expanded) {
        float target = expanded ? 1.0f : 0.0f;
        long now = System.nanoTime();
        float deltaSeconds = this.lastMacroAnimNs != 0L ? RangesKt.coerceIn((float)((float)(now - this.lastMacroAnimNs) / 1.0E9f), (float)0.0f, (float)0.05f) : 0.016666668f;
        this.lastMacroAnimNs = now;
        float speed = target > this.macroExpand ? 6.2f : 8.4f;
        float step = speed * deltaSeconds;
        this.macroExpand = RangesKt.coerceIn((float)(this.macroExpand < target ? Math.min(target, this.macroExpand + step) : (this.macroExpand > target ? Math.max(target, this.macroExpand - step) : target)), (float)0.0f, (float)1.0f);
        return this.macroExpand;
    }

    private final float segmentWidth(SegmentSpec segment) {
        float f;
        String string;
        String string2 = segment.getSuffix();
        if (string2 != null) {
            String string3;
            String it = string3 = string2;
            boolean bl = false;
            string = !StringsKt.isBlank((CharSequence)it) ? string3 : null;
        } else {
            string = null;
        }
        String suffix = string;
        float primary = NVGRenderer.textWidth$default(segment.getPrimary(), segment.getPrimarySize(), null, 4, null);
        String string4 = suffix;
        if (string4 != null) {
            String it = string4;
            boolean bl = false;
            f = NVGRenderer.textWidth$default(it, segment.getSuffixSize(), null, 4, null);
        } else {
            f = 0.0f;
        }
        float suffixWidth = f;
        return primary + (suffix != null ? 4.0f + suffixWidth : 0.0f);
    }

    private final float keyPillWidth(String text) {
        return NVGRenderer.textWidth$default(text, 9.4f, null, 4, null) + 14.0f;
    }

    private final String ellipsize(String text, float maxWidth, float size) {
        if (NVGRenderer.textWidth$default(text, size, null, 4, null) <= maxWidth) {
            return text;
        }
        for (int length = text.length() - 1; 0 < length; --length) {
            String string = text.substring(0, length);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            String candidate = ((Object)StringsKt.trimEnd((CharSequence)string)).toString() + "...";
            if (!(NVGRenderer.textWidth$default(candidate, size, null, 4, null) <= maxWidth)) continue;
            return candidate;
        }
        return "...";
    }

    private final int withAlpha(int color, int alpha) {
        return RangesKt.coerceIn((int)alpha, (int)0, (int)255) << 24 | color & 0xFFFFFF;
    }

    private final void sampleFrameRate() {
        long now = System.nanoTime();
        if (this.lastFrameNs != 0L) {
            double deltaMs = (double)(now - this.lastFrameNs) / 1000000.0;
            boolean bl = 2.0 <= deltaMs ? deltaMs <= 250.0 : false;
            if (bl) {
                float sample = RangesKt.coerceIn((float)((float)(1000.0 / deltaMs)), (float)1.0f, (float)360.0f);
                this.smoothedFps = this.smoothedFps * 0.85f + sample * 0.15f;
            }
        }
        this.lastFrameNs = now;
    }

    private final <T> float sumOfFloat(Iterable<? extends T> $this$sumOfFloat, Function1<? super T, Float> selector) {
        float sum = 0.0f;
        for (T value : $this$sumOfFloat) {
            sum += ((Number)selector.invoke(value)).floatValue();
        }
        return sum;
    }

    private static final String clientVersion$lambda$0(ModContainer it) {
        return it.getMetadata().getVersion().getFriendlyString();
    }

    private static final String clientVersion$lambda$1(Function1 $tmp0, Object p0) {
        return (String)$tmp0.invoke(p0);
    }

    private static final float watermark$lambda$0$0(WatermarkModule this$0, TextSetting $text, CheckboxSetting $macroPanel, CheckboxSetting $background) {
        return WatermarkModule.computeLayout$default(this$0, (String)$text.getValue(), (Boolean)$macroPanel.getValue(), (Boolean)$background.getValue(), null, 8, null).getWidth();
    }

    private static final float watermark$lambda$0$1(WatermarkModule this$0, TextSetting $text, CheckboxSetting $macroPanel, CheckboxSetting $background) {
        return WatermarkModule.computeLayout$default(this$0, (String)$text.getValue(), (Boolean)$macroPanel.getValue(), (Boolean)$background.getValue(), null, 8, null).getTotalHeight();
    }

    private static final Unit watermark$lambda$0$2(WatermarkModule this$0, CheckboxSetting $background, TextSetting $text, ColorSetting $color, CheckboxSetting $shadow, CheckboxSetting $macroPanel, float screenX, float screenY, float f) {
        List<MacroRow> rows;
        this$0.sampleFrameRate();
        if (!((Boolean)$background.getValue()).booleanValue()) {
            this$0.renderSimpleText(screenX, screenY, (String)$text.getValue(), $color.getValue(), (Boolean)$shadow.getValue());
            return Unit.INSTANCE;
        }
        List<MacroRow> liveRows = this$0.resolveMacroRows((Boolean)$macroPanel.getValue());
        if (!((Collection)liveRows).isEmpty()) {
            this$0.retainedMacroRows = liveRows;
        }
        boolean expandTarget = (Boolean)$macroPanel.getValue() != false && !((Collection)liveRows).isEmpty();
        float expansion = this$0.updateMacroExpansion(expandTarget);
        List<MacroRow> list = !((Collection)liveRows).isEmpty() ? liveRows : (expansion > 0.01f && !((Collection)this$0.retainedMacroRows).isEmpty() ? this$0.retainedMacroRows : (rows = CollectionsKt.emptyList()));
        if (!expandTarget && expansion <= 0.01f) {
            this$0.retainedMacroRows = CollectionsKt.emptyList();
        }
        Layout layout = this$0.computeLayout((String)$text.getValue(), (Boolean)$macroPanel.getValue(), true, rows);
        this$0.renderWatermarkCard(screenX, screenY, layout, (String)$text.getValue(), $color.getValue(), rows);
        return Unit.INSTANCE;
    }

    private static final Unit watermark$lambda$0$3(CheckboxSetting $background, WatermarkModule this$0, CheckboxSetting $macroPanel, TextSetting $text, float screenX, float screenY, float scale) {
        if (!((Boolean)$background.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        List<MacroRow> rows = this$0.macroRowsForLayout((Boolean)$macroPanel.getValue());
        Layout layout = this$0.computeLayout((String)$text.getValue(), (Boolean)$macroPanel.getValue(), true, rows);
        HudGlassBlurRenderer.renderBlurRect(screenX, screenY, layout.getWidth() * scale, layout.getTotalHeight() * scale, 12.0f * scale, 1.35f);
        return Unit.INSTANCE;
    }

    private static final Unit watermark$lambda$0(WatermarkModule this$0, HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        TextSetting text = (TextSetting)$this$hudElement.setting((Setting)new TextSetting("Text", "Display text", "Dutt Client"));
        ColorSetting color = (ColorSetting)$this$hudElement.setting((Setting)new ColorSetting("Color", "Accent color", ThemeManager.INSTANCE.getCurrentTheme().getAccent()));
        CheckboxSetting shadow = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Shadow", "Show shadow on the brand text.", true));
        CheckboxSetting background = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Background", "Show the glass watermark card.", true));
        CheckboxSetting macroPanel = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Macro Panel", "Expand the watermark to show active macros and keybinds.", true));
        $this$hudElement.width((Function0<Float>)((Function0)() -> WatermarkModule.watermark$lambda$0$0(this$0, text, macroPanel, background)));
        $this$hudElement.height((Function0<Float>)((Function0)() -> WatermarkModule.watermark$lambda$0$1(this$0, text, macroPanel, background)));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> WatermarkModule.watermark$lambda$0$2(this$0, background, text, color, shadow, macroPanel, arg_0, arg_1, arg_2)));
        $this$hudElement.preRender((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> WatermarkModule.watermark$lambda$0$3(background, this$0, macroPanel, text, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\u0006R\u0014\u0010\f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\f\u0010\u0006R\u0014\u0010\r\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\r\u0010\u0006R\u0014\u0010\u000e\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u0006R\u0014\u0010\u000f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0006R\u0014\u0010\u0010\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0006R\u0014\u0010\u0011\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0006R\u0014\u0010\u0012\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0006R\u0014\u0010\u0013\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0006R\u0014\u0010\u0014\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0006R\u0014\u0010\u0015\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0006R\u0014\u0010\u0016\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0006R\u0014\u0010\u0017\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0006R\u0014\u0010\u0018\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0006R\u0014\u0010\u0019\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0006R\u0014\u0010\u001a\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0006R\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0006R\u0014\u0010 \u001a\u00020\u001b8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b \u0010\u001dR\u0014\u0010!\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b!\u0010\u001d\u00a8\u0006\""}, d2={"Lorg/cobalt/api/hud/modules/WatermarkModule$Companion;", "", "<init>", "()V", "", "SIMPLE_TEXT_SIZE", "F", "CARD_WIDTH", "MAIN_BAR_HEIGHT", "CARD_CORNER", "MAIN_BAR_PAD_X", "BADGE_SIZE", "BADGE_GAP", "SEGMENT_GAP", "SEGMENT_VALUE_SIZE", "SEGMENT_LABEL_SIZE", "SEGMENT_INNER_GAP", "SERVER_SEGMENT_MAX_WIDTH", "PANEL_PAD_X", "PANEL_PAD_Y", "PANEL_HEADER_HEIGHT", "PANEL_ROW_HEIGHT", "PANEL_ROW_GAP", "HEADER_TITLE_SIZE", "HEADER_SUBTITLE_SIZE", "ROW_LABEL_SIZE", "KEY_PILL_TEXT_SIZE", "", "WHITE_TEXT", "I", "MUTED_TEXT", "GLASS_BLUR_STRENGTH", "CARD_BG", "SECTION_TINT", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001d\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001a\u001a\u0004\b\u001e\u0010\nR\u0011\u0010 \u001a\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010\n\u00a8\u0006!"}, d2={"Lorg/cobalt/api/hud/modules/WatermarkModule$Layout;", "", "", "width", "barHeight", "macroFullHeight", "macroVisibleHeight", "<init>", "(FFFF)V", "component1", "()F", "component2", "component3", "component4", "copy", "(FFFF)Lorg/cobalt/api/hud/modules/WatermarkModule$Layout;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getWidth", "getBarHeight", "getMacroFullHeight", "getMacroVisibleHeight", "getTotalHeight", "totalHeight", "cobalt"})
    private static final class Layout {
        private final float width;
        private final float barHeight;
        private final float macroFullHeight;
        private final float macroVisibleHeight;

        public Layout(float width, float barHeight, float macroFullHeight, float macroVisibleHeight) {
            this.width = width;
            this.barHeight = barHeight;
            this.macroFullHeight = macroFullHeight;
            this.macroVisibleHeight = macroVisibleHeight;
        }

        public final float getWidth() {
            return this.width;
        }

        public final float getBarHeight() {
            return this.barHeight;
        }

        public final float getMacroFullHeight() {
            return this.macroFullHeight;
        }

        public final float getMacroVisibleHeight() {
            return this.macroVisibleHeight;
        }

        public final float getTotalHeight() {
            return this.barHeight + this.macroVisibleHeight;
        }

        public final float component1() {
            return this.width;
        }

        public final float component2() {
            return this.barHeight;
        }

        public final float component3() {
            return this.macroFullHeight;
        }

        public final float component4() {
            return this.macroVisibleHeight;
        }

        @NotNull
        public final Layout copy(float width, float barHeight, float macroFullHeight, float macroVisibleHeight) {
            return new Layout(width, barHeight, macroFullHeight, macroVisibleHeight);
        }

        public static /* synthetic */ Layout copy$default(Layout layout, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                f = layout.width;
            }
            if ((n & 2) != 0) {
                f2 = layout.barHeight;
            }
            if ((n & 4) != 0) {
                f3 = layout.macroFullHeight;
            }
            if ((n & 8) != 0) {
                f4 = layout.macroVisibleHeight;
            }
            return layout.copy(f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "Layout(width=" + this.width + ", barHeight=" + this.barHeight + ", macroFullHeight=" + this.macroFullHeight + ", macroVisibleHeight=" + this.macroVisibleHeight + ")";
        }

        public int hashCode() {
            int result = Float.hashCode(this.width);
            result = result * 31 + Float.hashCode(this.barHeight);
            result = result * 31 + Float.hashCode(this.macroFullHeight);
            result = result * 31 + Float.hashCode(this.macroVisibleHeight);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Layout)) {
                return false;
            }
            Layout layout = (Layout)other;
            if (Float.compare(this.width, layout.width) != 0) {
                return false;
            }
            if (Float.compare(this.barHeight, layout.barHeight) != 0) {
                return false;
            }
            if (Float.compare(this.macroFullHeight, layout.macroFullHeight) != 0) {
                return false;
            }
            return Float.compare(this.macroVisibleHeight, layout.macroVisibleHeight) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0011\u0010\u0013\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/hud/modules/WatermarkModule$MacroRow;", "", "", "label", "keybind", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "component2", "copy", "(Ljava/lang/String;Ljava/lang/String;)Lorg/cobalt/api/hud/modules/WatermarkModule$MacroRow;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getLabel", "getKeybind", "cobalt"})
    private static final class MacroRow {
        @NotNull
        private final String label;
        @NotNull
        private final String keybind;

        public MacroRow(@NotNull String label, @NotNull String keybind) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)keybind, (String)"keybind");
            this.label = label;
            this.keybind = keybind;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        @NotNull
        public final String getKeybind() {
            return this.keybind;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        @NotNull
        public final String component2() {
            return this.keybind;
        }

        @NotNull
        public final MacroRow copy(@NotNull String label, @NotNull String keybind) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            Intrinsics.checkNotNullParameter((Object)keybind, (String)"keybind");
            return new MacroRow(label, keybind);
        }

        public static /* synthetic */ MacroRow copy$default(MacroRow macroRow, String string, String string2, int n, Object object) {
            if ((n & 1) != 0) {
                string = macroRow.label;
            }
            if ((n & 2) != 0) {
                string2 = macroRow.keybind;
            }
            return macroRow.copy(string, string2);
        }

        @NotNull
        public String toString() {
            return "MacroRow(label=" + this.label + ", keybind=" + this.keybind + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + this.keybind.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MacroRow)) {
                return false;
            }
            MacroRow macroRow = (MacroRow)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)macroRow.label)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.keybind, (Object)macroRow.keybind);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0010\b\u0082\b\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\b\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0010\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012J\u0010\u0010\u0014\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0015J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u0018JZ\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u001b\u0010\u001d\u001a\u00020\u001c2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0011\u0010\u001f\u001a\u00020\u0005H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001f\u0010\u0012J\u0011\u0010 \u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b \u0010\u000fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010!\u001a\u0004\b\"\u0010\u000fR\u0019\u0010\u0004\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010!\u001a\u0004\b#\u0010\u000fR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010$\u001a\u0004\b%\u0010\u0012R\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010$\u001a\u0004\b&\u0010\u0012R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010'\u001a\u0004\b(\u0010\u0015R\u0017\u0010\n\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010'\u001a\u0004\b)\u0010\u0015R\u0019\u0010\u000b\u001a\u0004\u0018\u00010\u00058\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010*\u001a\u0004\b+\u0010\u0018\u00a8\u0006,"}, d2={"Lorg/cobalt/api/hud/modules/WatermarkModule$SegmentSpec;", "", "", "primary", "suffix", "", "primaryColor", "suffixColor", "", "primarySize", "suffixSize", "glowColor", "<init>", "(Ljava/lang/String;Ljava/lang/String;IIFFLjava/lang/Integer;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "()I", "component4", "component5", "()F", "component6", "component7", "()Ljava/lang/Integer;", "copy", "(Ljava/lang/String;Ljava/lang/String;IIFFLjava/lang/Integer;)Lorg/cobalt/api/hud/modules/WatermarkModule$SegmentSpec;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getPrimary", "getSuffix", "I", "getPrimaryColor", "getSuffixColor", "F", "getPrimarySize", "getSuffixSize", "Ljava/lang/Integer;", "getGlowColor", "cobalt"})
    private static final class SegmentSpec {
        @NotNull
        private final String primary;
        @Nullable
        private final String suffix;
        private final int primaryColor;
        private final int suffixColor;
        private final float primarySize;
        private final float suffixSize;
        @Nullable
        private final Integer glowColor;

        public SegmentSpec(@NotNull String primary, @Nullable String suffix, int primaryColor, int suffixColor, float primarySize, float suffixSize, @Nullable Integer glowColor) {
            Intrinsics.checkNotNullParameter((Object)primary, (String)"primary");
            this.primary = primary;
            this.suffix = suffix;
            this.primaryColor = primaryColor;
            this.suffixColor = suffixColor;
            this.primarySize = primarySize;
            this.suffixSize = suffixSize;
            this.glowColor = glowColor;
        }

        public /* synthetic */ SegmentSpec(String string, String string2, int n, int n2, float f, float f2, Integer n3, int n4, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n4 & 2) != 0) {
                string2 = null;
            }
            if ((n4 & 8) != 0) {
                n2 = -1718711915;
            }
            if ((n4 & 0x10) != 0) {
                f = 11.6f;
            }
            if ((n4 & 0x20) != 0) {
                f2 = 10.3f;
            }
            if ((n4 & 0x40) != 0) {
                n3 = null;
            }
            this(string, string2, n, n2, f, f2, n3);
        }

        @NotNull
        public final String getPrimary() {
            return this.primary;
        }

        @Nullable
        public final String getSuffix() {
            return this.suffix;
        }

        public final int getPrimaryColor() {
            return this.primaryColor;
        }

        public final int getSuffixColor() {
            return this.suffixColor;
        }

        public final float getPrimarySize() {
            return this.primarySize;
        }

        public final float getSuffixSize() {
            return this.suffixSize;
        }

        @Nullable
        public final Integer getGlowColor() {
            return this.glowColor;
        }

        @NotNull
        public final String component1() {
            return this.primary;
        }

        @Nullable
        public final String component2() {
            return this.suffix;
        }

        public final int component3() {
            return this.primaryColor;
        }

        public final int component4() {
            return this.suffixColor;
        }

        public final float component5() {
            return this.primarySize;
        }

        public final float component6() {
            return this.suffixSize;
        }

        @Nullable
        public final Integer component7() {
            return this.glowColor;
        }

        @NotNull
        public final SegmentSpec copy(@NotNull String primary, @Nullable String suffix, int primaryColor, int suffixColor, float primarySize, float suffixSize, @Nullable Integer glowColor) {
            Intrinsics.checkNotNullParameter((Object)primary, (String)"primary");
            return new SegmentSpec(primary, suffix, primaryColor, suffixColor, primarySize, suffixSize, glowColor);
        }

        public static /* synthetic */ SegmentSpec copy$default(SegmentSpec segmentSpec, String string, String string2, int n, int n2, float f, float f2, Integer n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                string = segmentSpec.primary;
            }
            if ((n4 & 2) != 0) {
                string2 = segmentSpec.suffix;
            }
            if ((n4 & 4) != 0) {
                n = segmentSpec.primaryColor;
            }
            if ((n4 & 8) != 0) {
                n2 = segmentSpec.suffixColor;
            }
            if ((n4 & 0x10) != 0) {
                f = segmentSpec.primarySize;
            }
            if ((n4 & 0x20) != 0) {
                f2 = segmentSpec.suffixSize;
            }
            if ((n4 & 0x40) != 0) {
                n3 = segmentSpec.glowColor;
            }
            return segmentSpec.copy(string, string2, n, n2, f, f2, n3);
        }

        @NotNull
        public String toString() {
            return "SegmentSpec(primary=" + this.primary + ", suffix=" + this.suffix + ", primaryColor=" + this.primaryColor + ", suffixColor=" + this.suffixColor + ", primarySize=" + this.primarySize + ", suffixSize=" + this.suffixSize + ", glowColor=" + this.glowColor + ")";
        }

        public int hashCode() {
            int result = this.primary.hashCode();
            result = result * 31 + (this.suffix == null ? 0 : this.suffix.hashCode());
            result = result * 31 + Integer.hashCode(this.primaryColor);
            result = result * 31 + Integer.hashCode(this.suffixColor);
            result = result * 31 + Float.hashCode(this.primarySize);
            result = result * 31 + Float.hashCode(this.suffixSize);
            result = result * 31 + (this.glowColor == null ? 0 : ((Object)this.glowColor).hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SegmentSpec)) {
                return false;
            }
            SegmentSpec segmentSpec = (SegmentSpec)other;
            if (!Intrinsics.areEqual((Object)this.primary, (Object)segmentSpec.primary)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.suffix, (Object)segmentSpec.suffix)) {
                return false;
            }
            if (this.primaryColor != segmentSpec.primaryColor) {
                return false;
            }
            if (this.suffixColor != segmentSpec.suffixColor) {
                return false;
            }
            if (Float.compare(this.primarySize, segmentSpec.primarySize) != 0) {
                return false;
            }
            if (Float.compare(this.suffixSize, segmentSpec.suffixSize) != 0) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.glowColor, (Object)segmentSpec.glowColor);
        }
    }
}

