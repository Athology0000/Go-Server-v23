/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.authlib.properties.Property
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.Charsets
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_1304
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_2596
 *  net.minecraft.class_2649
 *  net.minecraft.class_2653
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_746
 *  net.minecraft.class_9296
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2fStack
 */
package org.cobalt.internal.visual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_1041;
import net.minecraft.class_1304;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2596;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_746;
import net.minecraft.class_9296;
import net.minecraft.class_9334;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.GuiRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.hud.HudModuleManager;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.visual.PetData;
import org.cobalt.internal.visual.PetTabListParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00a0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001f\u0010\f\u001a\u00020\u000b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011J'\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0003J\u0019\u0010\u001a\u001a\u0004\u0018\u00010\u00042\u0006\u0010\t\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001c\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u0003J\u001f\u0010\u001e\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u000f\u0010 \u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b \u0010\u0003J\u0017\u0010#\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020!H\u0007\u00a2\u0006\u0004\b#\u0010$J\u000f\u0010%\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010(\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020'H\u0007\u00a2\u0006\u0004\b(\u0010)J\u0017\u0010+\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020*H\u0007\u00a2\u0006\u0004\b+\u0010,J\u0015\u0010.\u001a\b\u0012\u0004\u0012\u00020\b0-H\u0002\u00a2\u0006\u0004\b.\u0010/J\u001f\u00102\u001a\u00020\u00182\u0006\u00100\u001a\u00020\u00132\u0006\u00101\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b2\u00103JG\u0010;\u001a\u00020\u00182\u0006\u00105\u001a\u0002042\u0006\u0010\t\u001a\u00020\b2\u0006\u00106\u001a\u00020\u00132\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u00020\u00132\u0006\u00109\u001a\u00020\u00132\u0006\u0010:\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b;\u0010<JG\u0010C\u001a\u00020\u00182\u0006\u00100\u001a\u00020\u00132\u0006\u00101\u001a\u00020\u00132\u0006\u0010=\u001a\u00020\u00132\u0006\u0010>\u001a\u00020\u00132\u0006\u0010@\u001a\u00020?2\u0006\u0010A\u001a\u00020?2\u0006\u0010B\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\bC\u0010DR\u0014\u0010F\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0014\u0010I\u001a\u00020H8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010JR\u0014\u0010K\u001a\u00020H8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010JR\u0014\u0010L\u001a\u00020H8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bL\u0010JR\u0014\u0010M\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bM\u0010NR\u0014\u0010O\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bO\u0010NR\u0014\u0010P\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bP\u0010NR\u0014\u0010Q\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bQ\u0010NR\u0014\u0010R\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bR\u0010NR\u0014\u0010S\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bS\u0010NR\u0014\u0010T\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bT\u0010NR\u0014\u0010U\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bU\u0010NR\u0014\u0010V\u001a\u00020\u00138\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bV\u0010NR\u0018\u0010W\u001a\u0004\u0018\u00010\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u0018\u0010Y\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0018\u0010\\\u001a\u0004\u0018\u00010[8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\\\u0010]R\u0018\u0010^\u001a\u0004\u0018\u00010[8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b^\u0010]R\u0018\u0010_\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b_\u0010ZR\u0016\u0010`\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b`\u0010ZR\u0016\u0010a\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\ba\u0010bR\u0018\u0010c\u001a\u0004\u0018\u00010[8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bc\u0010]R\u0016\u0010d\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bd\u0010NR\u0016\u0010e\u001a\u00020\u000e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\be\u0010fR0\u0010i\u001a\u001e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040gj\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0004`h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010jR\u0016\u0010k\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bk\u0010bR\u0016\u0010l\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bl\u0010bR\u0014\u0010n\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010oR\u0017\u0010q\u001a\u00020p8\u0006\u00a2\u0006\f\n\u0004\bq\u0010r\u001a\u0004\bs\u0010t\u00a8\u0006u"}, d2={"Lorg/cobalt/internal/visual/PetDisplayModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "s", "stripFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "Lnet/minecraft/class_1799;", "stack", "petName", "", "matchesPet", "(Lnet/minecraft/class_1799;Ljava/lang/String;)Z", "", "value", "compactNumber", "(J)Ljava/lang/String;", "text", "", "maxWidth", "size", "ellipsize", "(Ljava/lang/String;FF)Ljava/lang/String;", "", "ensureGhostLoaded", "extractSkullUrl", "(Lnet/minecraft/class_1799;)Ljava/lang/String;", "fetchItemsApi", "skullUrl", "tryStartHeadLoad", "(Ljava/lang/String;Ljava/lang/String;)V", "updateCachedPetItem", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "event", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "hudHeight", "()F", "Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "onGuiRender", "(Lorg/cobalt/api/event/impl/render/GuiRenderEvent;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "", "currentArmorStacks", "()Ljava/util/List;", "x", "y", "renderArmorRow", "(FF)V", "Lnet/minecraft/class_332;", "graphics", "originX", "originY", "renderScale", "localX", "localY", "renderHudItem", "(Lnet/minecraft/class_332;Lnet/minecraft/class_1799;FFFFF)V", "w", "ratio", "", "c1", "c2", "label", "renderProgressBar", "(FFFFIILjava/lang/String;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "glowSetting", "showHeldItemSetting", "W", "F", "ICON", "CORNER", "PAD", "TEXT_X", "BAR_H", "ARMOR_SLOT", "ARMOR_ICON", "ARMOR_GAP", "cachedPetItem", "Lnet/minecraft/class_1799;", "cachedPetName", "Ljava/lang/String;", "Lorg/cobalt/api/util/ui/helper/Image;", "petHeadImage", "Lorg/cobalt/api/util/ui/helper/Image;", "petHeadPendingDel", "petHeadPendingPath", "petHeadItemKey", "petHeadLoading", "Z", "ghostImage", "displayRatio", "lastRenderMs", "J", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "petSkullUrls", "Ljava/util/HashMap;", "apiCached", "apiFetching", "Lkotlin/text/Regex;", "formattingCodeRegex", "Lkotlin/text/Regex;", "Lorg/cobalt/api/hud/HudElement;", "petHud", "Lorg/cobalt/api/hud/HudElement;", "getPetHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPetDisplayModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PetDisplayModule.kt\norg/cobalt/internal/visual/PetDisplayModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,503:1\n1#2:504\n184#3,2:505\n296#4,2:507\n1924#4,3:509\n*S KotlinDebug\n*F\n+ 1 PetDisplayModule.kt\norg/cobalt/internal/visual/PetDisplayModule\n*L\n234#1:505,2\n250#1:507,2\n433#1:509,3\n*E\n"})
public final class PetDisplayModule
extends Module {
    @NotNull
    public static final PetDisplayModule INSTANCE = new PetDisplayModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final CheckboxSetting glowSetting;
    @NotNull
    private static final CheckboxSetting showHeldItemSetting;
    private static final float W = 176.0f;
    private static final float ICON = 34.0f;
    private static final float CORNER = 9.0f;
    private static final float PAD = 8.0f;
    private static final float TEXT_X = 52.0f;
    private static final float BAR_H = 8.0f;
    private static final float ARMOR_SLOT = 24.0f;
    private static final float ARMOR_ICON = 16.0f;
    private static final float ARMOR_GAP = 4.0f;
    @Nullable
    private static volatile class_1799 cachedPetItem;
    @Nullable
    private static volatile String cachedPetName;
    @Nullable
    private static volatile Image petHeadImage;
    @Nullable
    private static volatile Image petHeadPendingDel;
    @Nullable
    private static volatile String petHeadPendingPath;
    @NotNull
    private static volatile String petHeadItemKey;
    private static volatile boolean petHeadLoading;
    @Nullable
    private static Image ghostImage;
    private static float displayRatio;
    private static long lastRenderMs;
    @NotNull
    private static final HashMap<String, String> petSkullUrls;
    private static volatile boolean apiCached;
    private static volatile boolean apiFetching;
    @NotNull
    private static final Regex formattingCodeRegex;
    @NotNull
    private static final HudElement petHud;

    private PetDisplayModule() {
        super("Pet Display");
    }

    private final String stripFormatting(String s) {
        return formattingCodeRegex.replace((CharSequence)s, "");
    }

    private final boolean matchesPet(class_1799 stack, String petName) {
        if (stack.method_7960()) {
            return false;
        }
        String string = stack.method_7964().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        return StringsKt.contains((CharSequence)this.stripFormatting(string), (CharSequence)petName, (boolean)true);
    }

    private final String compactNumber(long value) {
        Object object;
        if (value >= 1000000L) {
            String string = "%.1fm";
            Object[] objectArray = new Object[]{Float.valueOf((float)value / 1000000.0f)};
            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
            object = StringsKt.removeSuffix((String)string2, (CharSequence)".0m") + "m";
        } else if (value >= 1000L) {
            String string = "%.1fk";
            Object[] objectArray = new Object[]{Float.valueOf((float)value / 1000.0f)};
            String string3 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            object = StringsKt.removeSuffix((String)string3, (CharSequence)".0k") + "k";
        } else {
            object = String.valueOf(value);
        }
        return object;
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

    private final void ensureGhostLoaded() {
        Object object;
        if (ghostImage != null) {
            return;
        }
        Object object2 = this;
        try {
            PetDisplayModule $this$ensureGhostLoaded_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/cobalt/textures/ui/ghost.svg"));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        ghostImage = (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    private final String extractSkullUrl(class_1799 stack) {
        Object object;
        class_9296 class_92962 = (class_9296)stack.method_58694(class_9334.field_49617);
        if (class_92962 == null) {
            return null;
        }
        class_9296 profile = class_92962;
        Collection collection = profile.method_73313().properties().get((Object)"textures");
        Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"get(...)");
        Collection textures = collection;
        Object object2 = (Property)CollectionsKt.firstOrNull((Iterable)textures);
        if (object2 == null || (object2 = object2.value()) == null) {
            return null;
        }
        Object b64 = object2;
        Object object3 = this;
        try {
            PetDisplayModule $this$extractSkullUrl_u24lambda_u240 = object3;
            boolean bl = false;
            byte[] byArray = Base64.getDecoder().decode((String)b64);
            Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"decode(...)");
            byte[] byArray2 = byArray;
            String json = new String(byArray2, Charsets.UTF_8);
            Integer n = StringsKt.indexOf$default((CharSequence)json, (String)"\"url\"", (int)0, (boolean)false, (int)6, null);
            int it = ((Number)n).intValue();
            boolean bl2 = false;
            Integer n2 = it >= 0 ? n : null;
            if (n2 == null) {
                return null;
            }
            int i1 = n2;
            int q1 = StringsKt.indexOf$default((CharSequence)json, (char)'\"', (int)(StringsKt.indexOf$default((CharSequence)json, (char)':', (int)i1, (boolean)false, (int)4, null) + 1), (boolean)false, (int)4, null) + 1;
            String string = json.substring(q1, StringsKt.indexOf$default((CharSequence)json, (char)'\"', (int)q1, (boolean)false, (int)4, null));
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            object = Result.constructor-impl((Object)string);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object3 = object;
        return (String)(Result.isFailure-impl((Object)object3) ? null : object3);
    }

    private final void fetchItemsApi() {
        Thread thread;
        if (apiCached || apiFetching) {
            return;
        }
        apiFetching = true;
        Thread it = thread = new Thread(PetDisplayModule::fetchItemsApi$lambda$0, "pet-items-api");
        boolean bl = false;
        it.setDaemon(true);
        thread.start();
    }

    private final void tryStartHeadLoad(String petName, String skullUrl) {
        Thread thread;
        if (petHeadLoading) {
            return;
        }
        String key = petName + "|" + skullUrl;
        if (Intrinsics.areEqual((Object)petHeadItemKey, (Object)key)) {
            return;
        }
        petHeadLoading = true;
        Thread it = thread = new Thread(() -> PetDisplayModule.tryStartHeadLoad$lambda$0(skullUrl, petName, key), "pet-head-fetch");
        boolean bl = false;
        it.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private final void updateCachedPetItem() {
        String $i$a$-synchronized-PetDisplayModule$updateCachedPetItem$apiUrl$22;
        String url;
        PetData pet = PetTabListParser.INSTANCE.getCurrent();
        if (pet == null) {
            cachedPetItem = null;
            cachedPetName = null;
            return;
        }
        if (cachedPetName != null && !StringsKt.equals((String)cachedPetName, (String)pet.getName(), (boolean)true)) {
            cachedPetItem = null;
            petHeadItemKey = "";
            displayRatio = 0.0f;
        }
        cachedPetName = pet.getName();
        if (StringsKt.startsWith$default((String)petHeadItemKey, (String)(pet.getName() + "|"), (boolean)false, (int)2, null)) {
            return;
        }
        class_1799 item = cachedPetItem;
        if (item != null && this.matchesPet(item, pet.getName()) && (url = this.extractSkullUrl(item)) != null) {
            this.tryStartHeadLoad(pet.getName(), url);
            return;
        }
        HashMap<String, String> hashMap = petSkullUrls;
        synchronized (hashMap) {
            boolean $i$a$-synchronized-PetDisplayModule$updateCachedPetItem$apiUrl$22 = false;
            String string = pet.getName().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
            $i$a$-synchronized-PetDisplayModule$updateCachedPetItem$apiUrl$22 = petSkullUrls.get(string);
        }
        String apiUrl = $i$a$-synchronized-PetDisplayModule$updateCachedPetItem$apiUrl$22;
        if (apiUrl != null) {
            this.tryStartHeadLoad(pet.getName(), apiUrl);
        } else {
            Object v4;
            block13: {
                void $this$firstOrNull$iv;
                this.fetchItemsApi();
                class_746 class_7462 = PetDisplayModule.mc.field_1724;
                if (class_7462 == null) {
                    return;
                }
                class_746 player = class_7462;
                Sequence sequence = SequencesKt.map((Sequence)CollectionsKt.asSequence((Iterable)((Iterable)RangesKt.until((int)0, (int)player.method_31548().method_5439()))), arg_0 -> PetDisplayModule.updateCachedPetItem$lambda$1(player, arg_0));
                class_2371 class_23712 = player.field_7512.field_7761;
                Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
                $i$a$-synchronized-PetDisplayModule$updateCachedPetItem$apiUrl$22 = SequencesKt.plus((Sequence)sequence, (Sequence)SequencesKt.map((Sequence)CollectionsKt.asSequence((Iterable)((Iterable)class_23712)), PetDisplayModule::updateCachedPetItem$lambda$2));
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    class_1799 it = (class_1799)element$iv;
                    boolean bl = false;
                    Intrinsics.checkNotNull((Object)it);
                    if (!INSTANCE.matchesPet(it, pet.getName())) continue;
                    v4 = element$iv;
                    break block13;
                }
                v4 = null;
            }
            class_1799 found = v4;
            if (found != null) {
                cachedPetItem = found.method_7972();
                String url2 = this.extractSkullUrl(found);
                if (url2 != null) {
                    this.tryStartHeadLoad(pet.getName(), url2);
                }
            }
        }
    }

    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = cachedPetName;
        if (string == null) {
            return;
        }
        String petName = string;
        class_2596<?> pkt = event.getPacket();
        if (pkt instanceof class_2649) {
            class_1799 copy;
            Object v2;
            block9: {
                List list = ((class_2649)pkt).comp_3839();
                Intrinsics.checkNotNullExpressionValue((Object)list, (String)"items(...)");
                Iterable $this$firstOrNull$iv = list;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    class_1799 it = (class_1799)element$iv;
                    boolean bl = false;
                    Intrinsics.checkNotNull((Object)it);
                    if (!INSTANCE.matchesPet(it, petName)) continue;
                    v2 = element$iv;
                    break block9;
                }
                v2 = null;
            }
            class_1799 class_17992 = v2;
            if (class_17992 == null) {
                return;
            }
            class_1799 match = class_17992;
            class_1799 class_17993 = match.method_7972();
            Intrinsics.checkNotNullExpressionValue((Object)class_17993, (String)"copy(...)");
            cachedPetItem = copy = class_17993;
            String string2 = this.extractSkullUrl(copy);
            if (string2 == null) {
                return;
            }
            String url = string2;
            this.tryStartHeadLoad(petName, url);
        } else if (pkt instanceof class_2653) {
            class_1799 copy;
            class_1799 class_17994 = ((class_2653)pkt).method_11449();
            Intrinsics.checkNotNullExpressionValue((Object)class_17994, (String)"getItem(...)");
            if (!this.matchesPet(class_17994, petName)) {
                return;
            }
            class_1799 class_17995 = ((class_2653)pkt).method_11449().method_7972();
            Intrinsics.checkNotNullExpressionValue((Object)class_17995, (String)"copy(...)");
            cachedPetItem = copy = class_17995;
            String string3 = this.extractSkullUrl(copy);
            if (string3 == null) {
                return;
            }
            String url = string3;
            this.tryStartHeadLoad(petName, url);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final float hudHeight() {
        if ((Boolean)showHeldItemSetting.getValue() == false) return 96.0f;
        PetData petData = PetTabListParser.INSTANCE.getCurrent();
        Object object = petData;
        if (petData == null) return 96.0f;
        String string = ((PetData)object).getHeldItem();
        object = string;
        if (string == null) return 96.0f;
        if (((CharSequence)object).length() <= 0) return 96.0f;
        return 110.0f;
    }

    @NotNull
    public final HudElement getPetHud() {
        return petHud;
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onGuiRender(@NotNull GuiRenderEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue() || !petHud.getEnabled()) {
            return;
        }
        if (PetDisplayModule.mc.field_1755 != null && !HudModuleManager.INSTANCE.isEditorOpen()) {
            return;
        }
        float guiScale = mc.method_22683().method_4495();
        if (guiScale <= 0.0f) {
            return;
        }
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        Pair<Float, Float> pair = petHud.getScreenPosition(window.method_4480(), window.method_4507());
        float screenX = ((Number)pair.component1()).floatValue();
        float screenY = ((Number)pair.component2()).floatValue();
        float scale = petHud.getScale();
        float originX = screenX / guiScale;
        float originY = screenY / guiScale;
        float renderScale = scale / guiScale;
        float armorY = this.hudHeight() - 8.0f - 8.0f - 24.0f - 8.0f;
        Iterable $this$forEachIndexed$iv = this.currentArmorStacks();
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void stack;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            class_1799 class_17992 = (class_1799)item$iv;
            int index = n;
            boolean bl = false;
            if (stack.method_7960()) continue;
            INSTANCE.renderHudItem(event.getGraphics(), (class_1799)stack, originX, originY, renderScale, 53.0f + (float)index * 28.0f + 4.0f, armorY + 4.0f);
        }
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue() || !petHud.getEnabled()) {
            return;
        }
        PetTabListParser.INSTANCE.update();
        this.updateCachedPetItem();
    }

    private final List<class_1799> currentArmorStacks() {
        class_746 class_7462 = PetDisplayModule.mc.field_1724;
        if (class_7462 == null) {
            Object[] objectArray = new class_1799[4];
            Intrinsics.checkNotNullExpressionValue((Object)class_1799.field_8037, (String)"EMPTY");
            Intrinsics.checkNotNullExpressionValue((Object)class_1799.field_8037, (String)"EMPTY");
            Intrinsics.checkNotNullExpressionValue((Object)class_1799.field_8037, (String)"EMPTY");
            Intrinsics.checkNotNullExpressionValue((Object)class_1799.field_8037, (String)"EMPTY");
            return CollectionsKt.listOf((Object[])objectArray);
        }
        class_746 player = class_7462;
        Object[] objectArray = new class_1799[4];
        Intrinsics.checkNotNullExpressionValue((Object)player.method_6118(class_1304.field_6169), (String)"getItemBySlot(...)");
        Intrinsics.checkNotNullExpressionValue((Object)player.method_6118(class_1304.field_6174), (String)"getItemBySlot(...)");
        Intrinsics.checkNotNullExpressionValue((Object)player.method_6118(class_1304.field_6172), (String)"getItemBySlot(...)");
        Intrinsics.checkNotNullExpressionValue((Object)player.method_6118(class_1304.field_6166), (String)"getItemBySlot(...)");
        return CollectionsKt.listOf((Object[])objectArray);
    }

    private final void renderArmorRow(float x, float y) {
        for (int index = 0; index < 4; ++index) {
            float slotX = x + (float)index * 28.0f;
            NVGRenderer.rect(slotX, y, 24.0f, 24.0f, 1495345984, 4.0f);
            NVGRenderer.hollowRect(slotX, y, 24.0f, 24.0f, 1.0f, 0x18FFFFFF, 4.0f);
        }
    }

    private final void renderHudItem(class_332 graphics, class_1799 stack, float originX, float originY, float renderScale, float localX, float localY) {
        Matrix3x2fStack matrix3x2fStack = graphics.method_51448();
        Intrinsics.checkNotNullExpressionValue((Object)matrix3x2fStack, (String)"pose(...)");
        Matrix3x2fStack pose = matrix3x2fStack;
        pose.pushMatrix();
        pose.translate(originX + localX * renderScale, originY + localY * renderScale);
        pose.scale(renderScale, renderScale);
        graphics.method_51427(stack, 0, 0);
        pose.popMatrix();
    }

    private final void renderProgressBar(float x, float y, float w, float ratio, int c1, int c2, String label) {
        float barR = 4.0f;
        NVGRenderer.rect(x, y, w, 8.0f, 1880300073, barR);
        float fillW = RangesKt.coerceIn((float)(w * ratio), (float)0.0f, (float)w);
        if (fillW > 0.0f) {
            NVGRenderer.pushScissor(x, y, fillW, 8.0f);
            NVGRenderer.gradientRect(x, y, w, 8.0f, c1, c2, Gradient.LeftToRight, barR);
            NVGRenderer.popScissor();
        }
        NVGRenderer.text$default(label, x + w / 2.0f - NVGRenderer.textWidth$default(label, 8.0f, null, 4, null) / 2.0f, y - 2.0f, 8.0f, -637534209, null, 32, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void fetchItemsApi$lambda$0() {
        try {
            JsonArray jsonArray;
            URLConnection uRLConnection = new URL("https://api.hypixel.net/v2/resources/skyblock/items").openConnection();
            Intrinsics.checkNotNull((Object)uRLConnection, (String)"null cannot be cast to non-null type java.net.HttpURLConnection");
            HttpURLConnection conn = (HttpURLConnection)uRLConnection;
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(15000);
            InputStream inputStream = conn.getInputStream();
            Intrinsics.checkNotNullExpressionValue((Object)inputStream, (String)"getInputStream(...)");
            InputStream inputStream2 = inputStream;
            Object object = Charsets.UTF_8;
            Reader reader = new InputStreamReader(inputStream2, (Charset)object);
            int n = 8192;
            JsonObject root = JsonParser.parseReader((Reader)(reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, n))).getAsJsonObject();
            if (root.getAsJsonArray("items") == null) {
                return;
            }
            JsonArray items = jsonArray;
            Iterator iterator = items.iterator();
            Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
            object = iterator;
            while (object.hasNext()) {
                Object object2;
                JsonElement el = (JsonElement)object.next();
                JsonObject obj = el.getAsJsonObject();
                Object object3 = obj.get("name");
                if (object3 == null || (object3 = object3.getAsString()) == null) continue;
                Object name = object3;
                Object object4 = obj.get("skin");
                if (object4 == null || (object4 = object4.getAsString()) == null) continue;
                Object skin = object4;
                PetDisplayModule petDisplayModule = INSTANCE;
                try {
                    String string;
                    JsonElement jsonElement;
                    Object object5;
                    byte[] byArray;
                    PetDisplayModule $this$fetchItemsApi_u24lambda_u240_u240 = petDisplayModule;
                    boolean bl = false;
                    Intrinsics.checkNotNullExpressionValue((Object)Base64.getDecoder().decode((String)skin), (String)"decode(...)");
                    String decoded = new String(byArray, Charsets.UTF_8);
                    JsonObject texObj = JsonParser.parseString((String)decoded).getAsJsonObject();
                    JsonObject jsonObject = texObj.getAsJsonObject("textures");
                    if (jsonObject != null && (object5 = jsonObject.getAsJsonObject("SKIN")) != null && (jsonElement = object5.get("url")) != null && (string = jsonElement.getAsString()) != null) {
                        String url = string;
                        object5 = petSkullUrls;
                        synchronized (object5) {
                            boolean bl2 = false;
                            Map map = petSkullUrls;
                            String string2 = ((String)name).toLowerCase(Locale.ROOT);
                            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                            map.put(string2, url);
                            jsonElement = Unit.INSTANCE;
                        }
                    }
                    object2 = Result.constructor-impl((Object)Unit.INSTANCE);
                }
                catch (Throwable throwable) {
                    object2 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                }
            }
            apiCached = true;
        }
        catch (Exception exception) {
        }
        finally {
            apiFetching = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void tryStartHeadLoad$lambda$0(String $skullUrl, String $petName, String $key) {
        try {
            BufferedImage bufferedImage;
            URLConnection uRLConnection = new URL($skullUrl).openConnection();
            Intrinsics.checkNotNull((Object)uRLConnection, (String)"null cannot be cast to non-null type java.net.HttpURLConnection");
            HttpURLConnection conn = (HttpURLConnection)uRLConnection;
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            if (ImageIO.read(conn.getInputStream()) == null) {
                return;
            }
            BufferedImage skin = bufferedImage;
            int size = 128;
            BufferedImage out = new BufferedImage(size, size, 2);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(skin, 0, 0, size, size, 8, 8, 16, 16, null);
            g.drawImage(skin, 0, 0, size, size, 40, 8, 48, 16, null);
            g.dispose();
            File tmp = new File(System.getProperty("java.io.tmpdir"), "cobalt_pet_head_" + StringsKt.replace$default((String)$petName, (String)" ", (String)"_", (boolean)false, (int)4, null) + ".png");
            ImageIO.write((RenderedImage)out, "PNG", tmp);
            petHeadPendingPath = tmp.getAbsolutePath();
            petHeadItemKey = $key;
        }
        catch (Exception exception) {
        }
        finally {
            petHeadLoading = false;
        }
    }

    private static final class_1799 updateCachedPetItem$lambda$1(class_746 $player, int it) {
        return $player.method_31548().method_5438(it);
    }

    private static final class_1799 updateCachedPetItem$lambda$2(class_1735 it) {
        return it.method_7677();
    }

    private static final float petHud$lambda$0$0() {
        return 176.0f;
    }

    private static final float petHud$lambda$0$1() {
        return INSTANCE.hudHeight();
    }

    /*
     * Unable to fully structure code
     */
    private static final Unit petHud$lambda$0$2(HudElementBuilder $this_hudElement, float x, float y, float var3_3) {
        if (!((Boolean)PetDisplayModule.enabledSetting.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        PetDisplayModule.INSTANCE.ensureGhostLoaded();
        v0 = PetDisplayModule.petHeadPendingPath;
        if (v0 != null) {
            path = v0;
            $i$a$-let-PetDisplayModule$petHud$1$3$1 = false;
            PetDisplayModule.petHeadPendingPath = null;
            var8_7 = $this_hudElement;
            try {
                $this$petHud_u24lambda_u240_u242_u240_u240 = var8_7;
                $i$a$-runCatching-PetDisplayModule$petHud$1$3$1$newImg$1 = false;
                $this$petHud_u24lambda_u240_u242_u240_u240 = Result.constructor-impl((Object)NVGRenderer.createImage(path));
            }
            catch (Throwable $i$a$-runCatching-PetDisplayModule$petHud$1$3$1$newImg$1) {
                $this$petHud_u24lambda_u240_u242_u240_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)$i$a$-runCatching-PetDisplayModule$petHud$1$3$1$newImg$1));
            }
            var8_7 = $this$petHud_u24lambda_u240_u242_u240_u240;
            newImg = (Image)(Result.isFailure-impl((Object)var8_7) != false ? null : var8_7);
            PetDisplayModule.petHeadPendingDel = PetDisplayModule.petHeadImage;
            PetDisplayModule.petHeadImage = newImg;
        }
        v1 = PetDisplayModule.petHeadPendingDel;
        if (v1 != null) {
            it = v1;
            $i$a$-let-PetDisplayModule$petHud$1$3$2 = false;
            var8_7 = $this_hudElement;
            try {
                $this$petHud_u24lambda_u240_u242_u241_u240 = var8_7;
                $i$a$-runCatching-PetDisplayModule$petHud$1$3$2$1 = false;
                NVGRenderer.deleteImage(it);
                $this$petHud_u24lambda_u240_u242_u241_u240 = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable $i$a$-runCatching-PetDisplayModule$petHud$1$3$2$1) {
                $this$petHud_u24lambda_u240_u242_u241_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)$i$a$-runCatching-PetDisplayModule$petHud$1$3$2$1));
            }
            PetDisplayModule.petHeadPendingDel = null;
        }
        now = System.currentTimeMillis();
        h = PetDisplayModule.INSTANCE.hudHeight();
        c1 = ThemeManager.INSTANCE.getCurrentTheme().getAccent();
        c2 = ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary();
        data = PetTabListParser.INSTANCE.getCurrent();
        titleX = x + 52.0f;
        titleMaxW = 116.0f;
        if (!((Boolean)PetDisplayModule.showHeldItemSetting.getValue()).booleanValue()) ** GOTO lbl-1000
        v2 = data;
        v3 = v2 != null && (v2 = v2.getHeldItem()) != null ? ((CharSequence)v2).length() > 0 : false;
        if (v3) {
            v4 = true;
        } else lbl-1000:
        // 2 sources

        {
            v4 = false;
        }
        hasHeldItem = v4;
        detailY = hasHeldItem != false ? y + 45.0f : y + 32.0f;
        armorY = y + h - 8.0f - 8.0f - 24.0f - 8.0f;
        armorStartX = titleX;
        v5 = data == null ? 0.0f : (data.isMaxLevel() != false ? 1.0f : (progressRatio = data.getXpRequired() > 0L ? RangesKt.coerceIn((float)((float)data.getXpCurrent() / (float)data.getXpRequired()), (float)0.0f, (float)1.0f) : 0.0f));
        if (((Boolean)PetDisplayModule.glowSetting.getValue()).booleanValue()) {
            NVGRenderer.rect(x + 6.0f, y + 7.0f, 176.0f, h, 0x28000000, 11.0f);
            NVGRenderer.rect(x + 3.0f, y + 3.0f, 176.0f, h, 0x1A000000, 10.0f);
        }
        NVGRenderer.rect(x, y, 176.0f, h, -434494924, 9.0f);
        NVGRenderer.gradientRect(x, y, 176.0f, h * 0.55f, 0x10FFFFFF, 0, Gradient.TopToBottom, 9.0f);
        NVGRenderer.hollowRect(x, y, 176.0f, h, 1.0f, 0x1FFFFFFF, 9.0f);
        iconX = x + 8.0f;
        iconY = y + 8.0f;
        NVGRenderer.rect(iconX, iconY, 34.0f, 34.0f, -13945524, 6.0f);
        NVGRenderer.hollowRect(iconX, iconY, 34.0f, 34.0f, 1.0f, 0x20FFFFFF, 6.0f);
        if (data == null) {
            ghost = PetDisplayModule.ghostImage;
            if (ghost != null) {
                NVGRenderer.image$default(ghost, iconX + 5.0f, iconY + 5.0f, 24.0f, 24.0f, 0.0f, 0, 96, null);
            } else {
                sym = "\u2726";
                NVGRenderer.text$default(sym, iconX + 17.0f - NVGRenderer.textWidth$default(sym, 20.0f, null, 4, null) / 2.0f, iconY + 17.0f - 10.0f, 20.0f, 0x33FFFFFF, null, 32, null);
            }
            NVGRenderer.textShadow$default("No pet active", titleX, y + 18.0f, 11.0f, -1, null, 32, null);
            NVGRenderer.text$default("Open the pet menu to refresh the card.", titleX, y + 33.0f, 9.0f, -1345989121, null, 32, null);
            PetDisplayModule.INSTANCE.renderArmorRow(armorStartX, armorY);
            PetDisplayModule.INSTANCE.renderProgressBar(titleX, y + h - 8.0f - 8.0f, 116.0f, 0.0f, c1, c2, "0%");
            return Unit.INSTANCE;
        }
        headImg = PetDisplayModule.petHeadImage;
        if (headImg != null) {
            NVGRenderer.image$default(headImg, iconX + 3.0f, iconY + 3.0f, 28.0f, 28.0f, 4.0f, 0, 64, null);
        } else {
            sym = "\u2726";
            NVGRenderer.text$default(sym, iconX + 17.0f - NVGRenderer.textWidth$default(sym, 20.0f, null, 4, null) / 2.0f, iconY + 17.0f - 10.0f, 20.0f, c1, null, 32, null);
        }
        textColor = -1;
        dimColor = -1345989121;
        title = PetDisplayModule.INSTANCE.ellipsize(data.getName(), titleMaxW, 12.0f);
        NVGRenderer.textShadow$default(title, titleX, y + 18.0f, 12.0f, textColor, null, 32, null);
        if (((Boolean)PetDisplayModule.showHeldItemSetting.getValue()).booleanValue() && ((CharSequence)data.getHeldItem()).length() > 0) {
            heldText = PetDisplayModule.INSTANCE.ellipsize(data.getHeldItem(), titleMaxW, 9.0f);
            NVGRenderer.text$default(heldText, titleX, y + 32.0f, 9.0f, dimColor, null, 32, null);
        }
        dt = PetDisplayModule.lastRenderMs == 0L ? 0.0f : (float)RangesKt.coerceIn((long)(now - PetDisplayModule.lastRenderMs), (long)0L, (long)100L) / 1000.0f;
        lerpSpeed = 2.5f;
        PetDisplayModule.displayRatio = progressRatio > PetDisplayModule.displayRatio ? RangesKt.coerceAtMost((float)(PetDisplayModule.displayRatio + lerpSpeed * dt), (float)progressRatio) : progressRatio;
        $this$petHud_u24lambda_u240_u242_u242 = var26_34 = new StringBuilder();
        $i$a$-buildString-PetDisplayModule$petHud$1$3$summary$1 = false;
        $this$petHud_u24lambda_u240_u242_u242.append("Lvl ");
        $this$petHud_u24lambda_u240_u242_u242.append(data.getLevel());
        $this$petHud_u24lambda_u240_u242_u242.append(" \u2022 ");
        $this$petHud_u24lambda_u240_u242_u242.append((String)(data.isMaxLevel() != false ? "MAX" : (int)(progressRatio * (float)100) + "%"));
        if (!data.isMaxLevel() && data.getXpRequired() > 0L && !data.isPercentageFormat()) {
            $this$petHud_u24lambda_u240_u242_u242.append(" \u2022 ");
            $this$petHud_u24lambda_u240_u242_u242.append(PetDisplayModule.INSTANCE.compactNumber(data.getXpCurrent()));
            $this$petHud_u24lambda_u240_u242_u242.append("/");
            $this$petHud_u24lambda_u240_u242_u242.append(PetDisplayModule.INSTANCE.compactNumber(data.getXpRequired()));
        }
        summary = var26_34.toString();
        NVGRenderer.text$default(PetDisplayModule.INSTANCE.ellipsize(summary, titleMaxW, 9.0f), titleX, detailY, 9.0f, dimColor, null, 32, null);
        PetDisplayModule.INSTANCE.renderArmorRow(armorStartX, armorY);
        barX = titleX;
        barY = y + h - 8.0f - 8.0f;
        barW = 116.0f;
        barText = data.isMaxLevel() != false ? "MAX" : (int)(progressRatio * (float)100) + "%";
        PetDisplayModule.INSTANCE.renderProgressBar(barX, barY, barW, PetDisplayModule.displayRatio, c1, c2, barText);
        PetDisplayModule.lastRenderMs = now;
        return Unit.INSTANCE;
    }

    private static final Unit petHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.BOTTOM_RIGHT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        $this$hudElement.width((Function0<Float>)((Function0)PetDisplayModule::petHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)PetDisplayModule::petHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> PetDisplayModule.petHud$lambda$0$2($this$hudElement, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabledSetting = new CheckboxSetting("Enabled", "Show the pet info HUD.", true);
        glowSetting = new CheckboxSetting("Glow", "Animated glow border.", true);
        showHeldItemSetting = new CheckboxSetting("Show Held Item", "Show the pet's held item line.", true);
        petHeadItemKey = "";
        petSkullUrls = new HashMap();
        formattingCodeRegex = new Regex("\\u00A7[0-9A-FK-ORa-fk-or]");
        petHud = HudModuleDSLKt.hudElement(INSTANCE, "pet-display", "Pet Display", "Animated pet info HUD", (Function1<? super HudElementBuilder, Unit>)((Function1)PetDisplayModule::petHud$lambda$0));
        Setting[] settingArray = new Setting[]{enabledSetting, glowSetting, showHeldItemSetting};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

