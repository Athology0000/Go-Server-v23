/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.dungeons;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.dungeons.DungeonKeyUtilsKt;
import org.cobalt.internal.dungeons.DungeonsModule;
import org.cobalt.internal.dungeons.map.DoorKind;
import org.cobalt.internal.dungeons.map.DungeonDoor;
import org.cobalt.internal.dungeons.map.DungeonFloor;
import org.cobalt.internal.dungeons.map.DungeonRoom;
import org.cobalt.internal.dungeons.map.DungeonScanState;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.cobalt.internal.dungeons.map.MapPlayerMarker;
import org.cobalt.internal.dungeons.map.RoomCheckmark;
import org.cobalt.internal.dungeons.map.RoomKind;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\u0003J'\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ'\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u000fJ/\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J/\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0014J/\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0014J'\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0017\u0010\u000fJ'\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0018\u0010\u000fJ\u001f\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010\"\u001a\u00020\u001d2\u0006\u0010!\u001a\u00020 H\u0002\u00a2\u0006\u0004\b\"\u0010#J\u001f\u0010&\u001a\u00020\u001d2\u0006\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b&\u0010'J\u0019\u0010)\u001a\u0004\u0018\u00010(2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010-\u001a\u00020+2\u0006\u0010,\u001a\u00020+H\u0002\u00a2\u0006\u0004\b-\u0010.J\u0017\u00100\u001a\u00020/2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b0\u00101J/\u00102\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b2\u0010\u0014J\u001f\u00104\u001a\u00020\u001d2\u0006\u0010$\u001a\u00020\u001d2\u0006\u00103\u001a\u00020\u001dH\u0002\u00a2\u0006\u0004\b4\u00105J9\u0010;\u001a\u00020\u00062\u0006\u00106\u001a\u00020(2\u0006\u00107\u001a\u00020\n2\u0006\u00108\u001a\u00020\n2\u0006\u00109\u001a\u00020\n2\b\b\u0002\u0010:\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b;\u0010<J\u0019\u0010>\u001a\u0004\u0018\u00010(2\u0006\u0010=\u001a\u00020+H\u0002\u00a2\u0006\u0004\b>\u0010?R\u0014\u0010@\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0014\u0010B\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bB\u0010AR\u0014\u0010C\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bC\u0010AR\u0014\u0010D\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bD\u0010AR\u0014\u0010E\u001a\u00020\n8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bE\u0010AR\u0014\u0010G\u001a\u00020F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bG\u0010HR)\u0010N\u001a\u0010\u0012\u0004\u0012\u00020+\u0012\u0006\u0012\u0004\u0018\u00010(0I8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bJ\u0010K\u001a\u0004\bL\u0010MR)\u0010R\u001a\u0010\u0012\u0004\u0012\u00020O\u0012\u0006\u0012\u0004\u0018\u00010(0I8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bP\u0010K\u001a\u0004\bQ\u0010MR)\u0010U\u001a\u0010\u0012\u0004\u0012\u00020+\u0012\u0006\u0012\u0004\u0018\u00010(0I8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bS\u0010K\u001a\u0004\bT\u0010MR\u0014\u0010W\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u0014\u0010Y\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010XR\u0014\u0010Z\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bZ\u0010XR\u0014\u0010[\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010XR\u0014\u0010\\\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010XR\u0014\u0010]\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010XR\u0014\u0010^\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b^\u0010XR\u0014\u0010`\u001a\u00020_8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010aR\u0014\u0010c\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0014\u0010e\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010dR\u0014\u0010f\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010dR\u001c\u0010h\u001a\b\u0012\u0004\u0012\u00020\u001d0g8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0014\u0010k\u001a\u00020j8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010l\u00a8\u0006m"}, d2={"Lorg/cobalt/internal/dungeons/DungeonMapModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "renderHud", "", "mapX", "mapY", "scale", "drawRooms", "(FFF)V", "drawDoors", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "room", "drawCheckmark", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;FFF)V", "drawRoomIcon", "drawRoomName", "drawMapMarkers", "drawSelfMarker", "width", "height", "renderFooter", "(FF)V", "", "roomFillColor", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;)I", "Lorg/cobalt/internal/dungeons/map/DoorKind;", "type", "doorFillColor", "(Lorg/cobalt/internal/dungeons/map/DoorKind;)I", "argb", "factor", "darken", "(IF)I", "Lorg/cobalt/api/util/ui/helper/Image;", "roomIconFor", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;)Lorg/cobalt/api/util/ui/helper/Image;", "", "value", "normalizeRoomKey", "(Ljava/lang/String;)Ljava/lang/String;", "", "roomHasWitherKey", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;)Z", "drawWitherKeyRoom", "alpha", "withAlpha", "(II)I", "image", "centerX", "centerY", "size", "rotationRadians", "drawCenteredImage", "(Lorg/cobalt/api/util/ui/helper/Image;FFFF)V", "fileName", "loadMapImage", "(Ljava/lang/String;)Lorg/cobalt/api/util/ui/helper/Image;", "PAD", "F", "HEADER_H", "FOOTER_H", "ROOM_FILL", "DOOR_FILL", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "mapImages$delegate", "Lkotlin/Lazy;", "getMapImages", "()Ljava/util/Map;", "mapImages", "Lorg/cobalt/internal/dungeons/map/RoomCheckmark;", "checkmarkImages$delegate", "getCheckmarkImages", "checkmarkImages", "roomIcons$delegate", "getRoomIcons", "roomIcons", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "showWitherKeyRoom", "funnyMode", "showCheckmarks", "showRoomNames", "showRoomIcons", "showPartyMarkers", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "mapSize", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "accentStart", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "accentEnd", "roomNameColor", "", "witherKeyRoomIndices", "Ljava/util/Set;", "Lorg/cobalt/api/hud/HudElement;", "hud", "Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDungeonMapModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonMapModule.kt\norg/cobalt/internal/dungeons/DungeonMapModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,565:1\n1#2:566\n437#3:567\n513#3,5:568\n1807#4,3:573\n1300#4,2:576\n1315#4,4:578\n*S KotlinDebug\n*F\n+ 1 DungeonMapModule.kt\norg/cobalt/internal/dungeons/DungeonMapModule\n*L\n472#1:567\n472#1:568,5\n475#1:573,3\n65#1:576,2\n65#1:578,4\n*E\n"})
public final class DungeonMapModule
extends Module {
    @NotNull
    public static final DungeonMapModule INSTANCE = new DungeonMapModule();
    private static final float PAD = 10.0f;
    private static final float HEADER_H = 26.0f;
    private static final float FOOTER_H = 16.0f;
    private static final float ROOM_FILL = 0.8f;
    private static final float DOOR_FILL = 0.26f;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Lazy mapImages$delegate;
    @NotNull
    private static final Lazy checkmarkImages$delegate;
    @NotNull
    private static final Lazy roomIcons$delegate;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final CheckboxSetting showWitherKeyRoom;
    @NotNull
    private static final CheckboxSetting funnyMode;
    @NotNull
    private static final CheckboxSetting showCheckmarks;
    @NotNull
    private static final CheckboxSetting showRoomNames;
    @NotNull
    private static final CheckboxSetting showRoomIcons;
    @NotNull
    private static final CheckboxSetting showPartyMarkers;
    @NotNull
    private static final SliderSetting mapSize;
    @NotNull
    private static final ColorSetting accentStart;
    @NotNull
    private static final ColorSetting accentEnd;
    @NotNull
    private static final ColorSetting roomNameColor;
    @NotNull
    private static Set<Integer> witherKeyRoomIndices;
    @NotNull
    private static final HudElement hud;

    private DungeonMapModule() {
        super("Dungeon Map");
    }

    private final Map<String, Image> getMapImages() {
        Lazy lazy = mapImages$delegate;
        return (Map)lazy.getValue();
    }

    private final Map<RoomCheckmark, Image> getCheckmarkImages() {
        Lazy lazy = checkmarkImages$delegate;
        return (Map)lazy.getValue();
    }

    private final Map<String, Image> getRoomIcons() {
        Lazy lazy = roomIcons$delegate;
        return (Map)lazy.getValue();
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Set set;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        DungeonScanState.INSTANCE.tick();
        if (((Boolean)showWitherKeyRoom.getValue()).booleanValue() && DungeonScanState.INSTANCE.isInDungeon()) {
            Set set2;
            class_638 class_6382 = DungeonMapModule.mc.field_1687;
            if (class_6382 != null) {
                class_638 p0 = class_6382;
                boolean bl = false;
                set2 = DungeonKeyUtilsKt.findWitherKeyRoomIndices(p0);
            } else {
                set2 = set = null;
            }
            if (set2 == null) {
                set = SetsKt.emptySet();
            }
        } else {
            set = SetsKt.emptySet();
        }
        witherKeyRoomIndices = set;
    }

    private final void renderHud() {
        String string;
        float width = (float)((Number)mapSize.getValue()).doubleValue() + 20.0f;
        float height = (float)((Number)mapSize.getValue()).doubleValue() + 26.0f + 16.0f + 10.0f;
        float mapAreaSize = (float)((Number)mapSize.getValue()).doubleValue();
        float mapX = 10.0f;
        float mapY = 26.0f;
        NVGRenderer.gradientRect(0.0f, 0.0f, width, height, -267053286, -266724063, Gradient.TopToBottom, 10.0f);
        NVGRenderer.hollowGradientRect(0.5f, 0.5f, width - 1.0f, height - 1.0f, 1.8f, accentStart.getValue(), accentEnd.getValue(), Gradient.LeftToRight, 10.0f);
        NVGRenderer.gradientRect(0.0f, 0.0f, width, 26.0f, accentStart.getValue(), accentEnd.getValue(), Gradient.LeftToRight, 10.0f);
        if (DungeonScanState.INSTANCE.isInDungeon() && DungeonScanState.INSTANCE.getFloor() != DungeonFloor.NONE) {
            StringBuilder stringBuilder;
            StringBuilder $this$renderHud_u24lambda_u240 = stringBuilder = new StringBuilder();
            boolean bl = false;
            $this$renderHud_u24lambda_u240.append(DungeonScanState.INSTANCE.getFloor().getShortName());
            Object object = DungeonScanState.INSTANCE.getCurrentRoom();
            if (object != null && (object = ((DungeonRoom)object).getName()) != null) {
                Object object2;
                Object it = object2 = object;
                boolean bl2 = false;
                object = !StringsKt.isBlank((CharSequence)((CharSequence)it)) ? object2 : null;
                if (object != null) {
                    it = object;
                    boolean bl3 = false;
                    $this$renderHud_u24lambda_u240.append("  ");
                    $this$renderHud_u24lambda_u240.append((String)it);
                }
            }
            string = stringBuilder.toString();
        } else {
            string = "Dungeon Map";
        }
        String headerLabel = string;
        NVGRenderer.text$default(headerLabel, 10.0f, 6.0f, 15.0f, -722949, null, 32, null);
        NVGRenderer.rect(mapX, mapY, mapAreaSize, mapAreaSize, -871822577, 8.0f);
        if (!DungeonScanState.INSTANCE.isInDungeon() || DungeonScanState.INSTANCE.getFloor() == DungeonFloor.NONE) {
            String text = "Not in Dungeon";
            float textWidth = NVGRenderer.textWidth$default(text, 14.0f, null, 4, null);
            NVGRenderer.text$default(text, mapX + mapAreaSize / 2.0f - textWidth / 2.0f, mapY + mapAreaSize / 2.0f - 8.0f, 14.0f, -1907998, null, 32, null);
            this.renderFooter(width, height);
            return;
        }
        DungeonFloor floor = DungeonScanState.INSTANCE.getFloor();
        float roomScale = Math.min(mapAreaSize / (float)floor.getRoomsWide(), mapAreaSize / (float)floor.getRoomsTall());
        this.drawDoors(mapX, mapY, roomScale);
        this.drawRooms(mapX, mapY, roomScale);
        if (((Boolean)showPartyMarkers.getValue()).booleanValue()) {
            this.drawMapMarkers(mapX, mapY, roomScale);
        }
        this.drawSelfMarker(mapX, mapY, roomScale);
        this.renderFooter(width, height);
    }

    private final void drawRooms(float mapX, float mapY, float scale) {
        HashSet<DungeonRoom> seen = new HashSet<DungeonRoom>();
        for (DungeonRoom room : DungeonScanState.INSTANCE.getRooms()) {
            if (room == null || !seen.add(room) || !((Boolean)funnyMode.getValue()).booleanValue() && !room.getExplored()) continue;
            int fill = this.roomFillColor(room);
            float roomInset = 0.099999994f;
            for (GridComponent component : room.getComponents()) {
                float cellX = (float)component.getX() / 2.0f + roomInset;
                float cellZ = (float)component.getZ() / 2.0f + roomInset;
                NVGRenderer.rect(mapX + cellX * scale, mapY + cellZ * scale, 0.8f * scale, 0.8f * scale, fill, 4.0f);
            }
            for (GridComponent first : room.getComponents()) {
                for (GridComponent second : room.getComponents()) {
                    float z;
                    float x;
                    if (Intrinsics.areEqual((Object)first, (Object)second) || Math.abs(first.getX() - second.getX()) + Math.abs(first.getZ() - second.getZ()) != 2 || first.getX() > second.getX() || first.getZ() > second.getZ()) continue;
                    if (first.getX() == second.getX()) {
                        x = (float)first.getX() / 2.0f + roomInset;
                        z = (float)first.getZ() / 2.0f + roomInset + 0.8f;
                        NVGRenderer.rect(mapX + x * scale, mapY + z * scale, 0.8f * scale, 0.19999999f * scale, fill, 2.0f);
                        continue;
                    }
                    x = (float)first.getX() / 2.0f + roomInset + 0.8f;
                    z = (float)first.getZ() / 2.0f + roomInset;
                    NVGRenderer.rect(mapX + x * scale, mapY + z * scale, 0.19999999f * scale, 0.8f * scale, fill, 2.0f);
                }
            }
            if (this.roomHasWitherKey(room)) {
                this.drawWitherKeyRoom(room, mapX, mapY, scale);
            }
            if (((Boolean)showCheckmarks.getValue()).booleanValue()) {
                this.drawCheckmark(room, mapX, mapY, scale);
            }
            if (((Boolean)showRoomIcons.getValue()).booleanValue()) {
                this.drawRoomIcon(room, mapX, mapY, scale);
            }
            if (!((Boolean)showRoomNames.getValue()).booleanValue()) continue;
            this.drawRoomName(room, mapX, mapY, scale);
        }
    }

    private final void drawDoors(float mapX, float mapY, float scale) {
        HashSet<Integer> seen = new HashSet<Integer>();
        for (DungeonDoor door : DungeonScanState.INSTANCE.getDoors()) {
            int index;
            if (door == null || !seen.add(index = door.getComponent().doorIndex())) continue;
            int fill = this.doorFillColor(door.getType());
            float roomInset = 0.099999994f;
            float halfDoorInset = 0.37f;
            float baseX = (float)door.getComponent().getX() / 2.0f;
            float baseZ = (float)door.getComponent().getZ() / 2.0f;
            if ((door.getComponent().getX() & 1) == 1) {
                NVGRenderer.rect(mapX + (baseX + 0.5f - 0.13f) * scale, mapY + (baseZ + roomInset) * scale, 0.26f * scale, 0.8f * scale, fill, 3.0f);
                continue;
            }
            NVGRenderer.rect(mapX + (baseX + roomInset) * scale, mapY + (baseZ + 0.5f - 0.13f) * scale, 0.8f * scale, 0.26f * scale, fill, 3.0f);
        }
    }

    private final void drawCheckmark(DungeonRoom room, float mapX, float mapY, float scale) {
        int n;
        GridComponent gridComponent = (GridComponent)CollectionsKt.firstOrNull(room.getComponents());
        if (gridComponent == null) {
            return;
        }
        GridComponent origin = gridComponent;
        float centerX = mapX + ((float)origin.getX() / 2.0f + 0.5f) * scale;
        float centerY = mapY + ((float)origin.getZ() / 2.0f + 0.5f) * scale;
        Image image = this.getCheckmarkImages().get((Object)room.getCheckmark());
        if (room.getCheckmark() == RoomCheckmark.NONE) {
            return;
        }
        if (image != null) {
            DungeonMapModule.drawCenteredImage$default(this, image, centerX, centerY, RangesKt.coerceIn((float)(scale * 0.22f), (float)10.0f, (float)18.0f), 0.0f, 16, null);
            return;
        }
        switch (WhenMappings.$EnumSwitchMapping$0[room.getCheckmark().ordinal()]) {
            case 1: {
                n = -657931;
                break;
            }
            case 2: {
                n = -10158215;
                break;
            }
            case 3: {
                n = -41635;
                break;
            }
            case 4: {
                n = -6776680;
                break;
            }
            case 5: {
                return;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        int color = n;
        NVGRenderer.circle(centerX, centerY, 3.6f, color);
    }

    private final void drawRoomIcon(DungeonRoom room, float mapX, float mapY, float scale) {
        Image image = this.roomIconFor(room);
        if (image == null) {
            return;
        }
        Image image2 = image;
        GridComponent gridComponent = (GridComponent)CollectionsKt.firstOrNull(room.getComponents());
        if (gridComponent == null) {
            return;
        }
        GridComponent origin = gridComponent;
        float centerX = mapX + ((float)origin.getX() / 2.0f + 0.5f) * scale;
        float centerY = mapY + ((float)origin.getZ() / 2.0f + 0.5f) * scale;
        float iconSize = RangesKt.coerceIn((float)(scale * 0.26f), (float)12.0f, (float)18.0f);
        float yOffset = (Boolean)showCheckmarks.getValue() != false && room.getCheckmark() != RoomCheckmark.NONE ? iconSize * 0.75f : 0.0f;
        DungeonMapModule.drawCenteredImage$default(this, image2, centerX, centerY - yOffset, iconSize, 0.0f, 16, null);
    }

    private final void drawRoomName(DungeonRoom room, float mapX, float mapY, float scale) {
        String string = room.getName();
        if (string == null) {
            return;
        }
        String name = string;
        GridComponent gridComponent = (GridComponent)CollectionsKt.firstOrNull(room.getComponents());
        if (gridComponent == null) {
            return;
        }
        GridComponent origin = gridComponent;
        String text = StringsKt.take((String)name, (int)12);
        float width = NVGRenderer.textWidth$default(text, 10.0f, null, 4, null);
        float centerX = mapX + ((float)origin.getX() / 2.0f + 0.5f) * scale;
        float centerY = mapY + ((float)origin.getZ() / 2.0f + 0.5f) * scale + ((Boolean)showRoomIcons.getValue() != false && this.roomIconFor(room) != null ? 4.0f : 0.0f);
        NVGRenderer.text$default(text, centerX - width / 2.0f, centerY - 6.0f, 10.0f, roomNameColor.getValue(), null, 32, null);
    }

    private final void drawMapMarkers(float mapX, float mapY, float scale) {
        Image otherMarker = this.getMapImages().get("markerOther.png");
        for (MapPlayerMarker marker : DungeonScanState.INSTANCE.getMapMarkers()) {
            float x = mapX + (float)marker.getComponentX() / 2.0f * scale;
            float y = mapY + (float)marker.getComponentZ() / 2.0f * scale;
            if (otherMarker != null) {
                DungeonMapModule.drawCenteredImage$default(this, otherMarker, x, y, RangesKt.coerceIn((float)(scale * 0.22f), (float)9.0f, (float)15.0f), 0.0f, 16, null);
                continue;
            }
            NVGRenderer.circle(x, y, 2.4f, -1184275);
        }
    }

    private final void drawSelfMarker(float mapX, float mapY, float scale) {
        class_746 class_7462 = DungeonMapModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        float roomX = (float)((player.method_23317() - (double)-200) / 16.0 / 2.0);
        float roomZ = (float)((player.method_23321() - (double)-200) / 16.0 / 2.0);
        float x = mapX + roomX * scale;
        float y = mapY + roomZ * scale;
        Image selfMarker = this.getMapImages().get("markerSelf.png");
        if (selfMarker != null) {
            this.drawCenteredImage(selfMarker, x, y, RangesKt.coerceIn((float)(scale * 0.28f), (float)12.0f, (float)18.0f), (float)Math.toRadians(player.method_36454() + 90.0f));
        } else {
            NVGRenderer.circle(x, y, 3.8f, -15065823);
            NVGRenderer.circle(x, y, 2.8f, accentStart.getValue());
            double yawRadians = Math.toRadians(player.method_36454() + 90.0f);
            float tipX = x + (float)Math.cos(yawRadians) * 7.0f;
            float tipY = y + (float)Math.sin(yawRadians) * 7.0f;
            NVGRenderer.line(x, y, tipX, tipY, 2.0f, accentEnd.getValue());
        }
    }

    private final void renderFooter(float width, float height) {
        String status = DungeonScanState.INSTANCE.getReferenceStatus();
        String clipped = status.length() > 54 ? StringsKt.take((String)status, (int)54) + "..." : status;
        NVGRenderer.text$default(clipped, 10.0f, height - 16.0f + 1.0f, 10.0f, -3749941, null, 32, null);
    }

    private final int roomFillColor(DungeonRoom room) {
        int base = switch (WhenMappings.$EnumSwitchMapping$1[room.getType().ordinal()]) {
            case 1 -> -14323651;
            case 2 -> -6739157;
            case 3 -> -8501800;
            case 4 -> -5477075;
            case 5 -> -2527577;
            case 6 -> -3561938;
            case 7 -> -2046642;
            case 8 -> -9549269;
            case 9 -> -12368306;
            default -> throw new NoWhenBranchMatchedException();
        };
        return room.getExplored() || (Boolean)funnyMode.getValue() != false ? base : this.darken(base, 0.45f);
    }

    private final int doorFillColor(DoorKind type) {
        return switch (WhenMappings.$EnumSwitchMapping$2[type.ordinal()]) {
            case 1 -> -13861817;
            case 2 -> -5621961;
            case 3 -> -14539478;
            case 4 -> -11451593;
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final int darken(int argb, float factor) {
        int alpha = argb >>> 24 & 0xFF;
        int red = RangesKt.coerceIn((int)((int)((float)(argb >>> 16 & 0xFF) * factor)), (int)0, (int)255);
        int green = RangesKt.coerceIn((int)((int)((float)(argb >>> 8 & 0xFF) * factor)), (int)0, (int)255);
        int blue = RangesKt.coerceIn((int)((int)((float)(argb & 0xFF) * factor)), (int)0, (int)255);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private final Image roomIconFor(DungeonRoom room) {
        String string = room.getName();
        if (string == null) {
            return null;
        }
        String name = string;
        return this.getRoomIcons().get(this.normalizeRoomKey(name));
    }

    /*
     * WARNING - void declaration
     */
    private final String normalizeRoomKey(String value) {
        void $this$filterTo$iv$iv;
        String string = value.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String $this$filter$iv = string;
        boolean $i$f$filter = false;
        CharSequence charSequence = $this$filter$iv;
        Appendable destination$iv$iv = new StringBuilder();
        boolean $i$f$filterTo = false;
        int n = $this$filterTo$iv$iv.length();
        for (int index$iv$iv = 0; index$iv$iv < n; ++index$iv$iv) {
            char element$iv$iv;
            char it = element$iv$iv = $this$filterTo$iv$iv.charAt(index$iv$iv);
            boolean bl = false;
            if (!Character.isLetterOrDigit(it)) continue;
            destination$iv$iv.append(element$iv$iv);
        }
        return ((StringBuilder)destination$iv$iv).toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean roomHasWitherKey(DungeonRoom room) {
        GridComponent it;
        if (((Collection)witherKeyRoomIndices).isEmpty()) return false;
        boolean bl = true;
        if (!bl) return false;
        Iterable $this$any$iv = room.getComponents();
        boolean $i$f$any = false;
        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
            return false;
        }
        Iterator iterator = $this$any$iv.iterator();
        do {
            if (!iterator.hasNext()) return false;
            Object element$iv = iterator.next();
            it = (GridComponent)element$iv;
            boolean bl2 = false;
        } while (!witherKeyRoomIndices.contains(it.roomIndex()));
        return true;
    }

    private final void drawWitherKeyRoom(DungeonRoom room, float mapX, float mapY, float scale) {
        float h;
        float w;
        float y;
        float x;
        int fill = this.withAlpha(DungeonsModule.INSTANCE.witherKeyMapColor$cobalt(), 52);
        int outline = this.withAlpha(DungeonsModule.INSTANCE.witherKeyMapColor$cobalt(), 225);
        float roomInset = 0.099999994f;
        for (GridComponent component : room.getComponents()) {
            float cellX = (float)component.getX() / 2.0f + roomInset;
            float cellZ = (float)component.getZ() / 2.0f + roomInset;
            x = mapX + cellX * scale;
            y = mapY + cellZ * scale;
            w = 0.8f * scale;
            h = 0.8f * scale;
            NVGRenderer.rect(x, y, w, h, fill, 4.0f);
            NVGRenderer.hollowRect(x, y, w, h, 1.35f, outline, 4.0f);
        }
        for (GridComponent first : room.getComponents()) {
            for (GridComponent second : room.getComponents()) {
                if (Intrinsics.areEqual((Object)first, (Object)second) || Math.abs(first.getX() - second.getX()) + Math.abs(first.getZ() - second.getZ()) != 2 || first.getX() > second.getX() || first.getZ() > second.getZ()) continue;
                if (first.getX() == second.getX()) {
                    x = mapX + ((float)first.getX() / 2.0f + roomInset) * scale;
                    y = mapY + ((float)first.getZ() / 2.0f + roomInset + 0.8f) * scale;
                    w = 0.8f * scale;
                    h = 0.19999999f * scale;
                    NVGRenderer.rect(x, y, w, h, fill, 2.0f);
                    NVGRenderer.hollowRect(x, y, w, h, 1.1f, outline, 2.0f);
                    continue;
                }
                x = mapX + ((float)first.getX() / 2.0f + roomInset + 0.8f) * scale;
                y = mapY + ((float)first.getZ() / 2.0f + roomInset) * scale;
                w = 0.19999999f * scale;
                h = 0.8f * scale;
                NVGRenderer.rect(x, y, w, h, fill, 2.0f);
                NVGRenderer.hollowRect(x, y, w, h, 1.1f, outline, 2.0f);
            }
        }
        Iterator cellX = ((Iterable)room.getComponents()).iterator();
        if (!cellX.hasNext()) {
            throw new NoSuchElementException();
        }
        GridComponent it422 = (GridComponent)cellX.next();
        boolean bl = false;
        int it422 = it422.getX();
        while (cellX.hasNext()) {
            GridComponent it32 = (GridComponent)cellX.next();
            $i$a$-minOf-DungeonMapModule$drawWitherKeyRoom$minX$1 = false;
            int it32 = it32.getX();
            if (it422 <= it32) continue;
            it422 = it32;
        }
        int minX = it422;
        Iterator it422 = ((Iterable)room.getComponents()).iterator();
        if (!it422.hasNext()) {
            throw new NoSuchElementException();
        }
        GridComponent it622 = (GridComponent)it422.next();
        boolean bl2 = false;
        int it622 = it622.getX();
        while (it422.hasNext()) {
            GridComponent it52 = (GridComponent)it422.next();
            $i$a$-maxOf-DungeonMapModule$drawWitherKeyRoom$maxX$1 = false;
            int it52 = it52.getX();
            if (it622 >= it52) continue;
            it622 = it52;
        }
        int maxX = it622;
        Iterator it622 = ((Iterable)room.getComponents()).iterator();
        if (!it622.hasNext()) {
            throw new NoSuchElementException();
        }
        GridComponent it822 = (GridComponent)it622.next();
        boolean bl3 = false;
        int it822 = it822.getZ();
        while (it622.hasNext()) {
            GridComponent it72 = (GridComponent)it622.next();
            $i$a$-minOf-DungeonMapModule$drawWitherKeyRoom$minZ$1 = false;
            int it72 = it72.getZ();
            if (it822 <= it72) continue;
            it822 = it72;
        }
        int minZ = it822;
        Iterator it822 = ((Iterable)room.getComponents()).iterator();
        if (!it822.hasNext()) {
            throw new NoSuchElementException();
        }
        GridComponent it22 = (GridComponent)it822.next();
        boolean bl4 = false;
        int it22 = it22.getZ();
        while (it822.hasNext()) {
            GridComponent it92 = (GridComponent)it822.next();
            $i$a$-maxOf-DungeonMapModule$drawWitherKeyRoom$maxZ$1 = false;
            int it92 = it92.getZ();
            if (it22 >= it92) continue;
            it22 = it92;
        }
        int maxZ = it22;
        float centerX = mapX + ((float)(minX + maxX) / 4.0f + 0.5f) * scale;
        float centerY = mapY + ((float)(minZ + maxZ) / 4.0f + 0.5f) * scale;
        String label = scale >= 28.0f ? "WK" : "K";
        float fontSize = label.length() == 2 ? 8.5f : 9.5f;
        float textWidth = NVGRenderer.textWidth$default(label, fontSize, null, 4, null);
        float badgeWidth = textWidth + 7.0f;
        float badgeHeight = fontSize + 4.0f;
        NVGRenderer.rect(centerX - badgeWidth * 0.5f, centerY - badgeHeight * 0.5f, badgeWidth, badgeHeight, outline, 3.0f);
        NVGRenderer.text$default(label, centerX - textWidth * 0.5f, centerY - fontSize * 0.52f, fontSize, -16248811, null, 32, null);
    }

    private final int withAlpha(int argb, int alpha) {
        return RangesKt.coerceIn((int)alpha, (int)0, (int)255) << 24 | argb & 0xFFFFFF;
    }

    private final void drawCenteredImage(Image image, float centerX, float centerY, float size, float rotationRadians) {
        float half = size * 0.5f;
        if (rotationRadians == 0.0f) {
            NVGRenderer.image$default(image, centerX - half, centerY - half, size, size, 0.0f, 0, 96, null);
            return;
        }
        NVGRenderer.push();
        NVGRenderer.translate(centerX, centerY);
        NVGRenderer.rotate(rotationRadians);
        NVGRenderer.image$default(image, -half, -half, size, size, 0.0f, 0, 96, null);
        NVGRenderer.pop();
    }

    static /* synthetic */ void drawCenteredImage$default(DungeonMapModule dungeonMapModule, Image image, float f, float f2, float f3, float f4, int n, Object object) {
        if ((n & 0x10) != 0) {
            f4 = 0.0f;
        }
        dungeonMapModule.drawCenteredImage(image, f, f2, f3, f4);
    }

    private final Image loadMapImage(String fileName) {
        Object object;
        Object object2 = this;
        try {
            DungeonMapModule $this$loadMapImage_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)NVGRenderer.createImage("/assets/dungeons/map/" + fileName));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (Image)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    /*
     * WARNING - void declaration
     */
    private static final Map mapImages_delegate$lambda$0() {
        void $this$associateWithTo$iv$iv;
        void $this$associateWith$iv;
        Object object = new String[]{"arrow.png", "blaze_powder.png", "book_normal.png", "boss_diamond.png", "boss_la.png", "boss_midas.png", "boss_sa.png", "boss_strong.png", "boss_superior.png", "boss_wise.png", "boss_young.png", "bucket_water.png", "chest.png", "creeper.png", "endframe_side.png", "failedRoom.png", "greenCheck.png", "ice.png", "markerOther.png", "markerSelf.png", "planks_oak.png", "questionMark.png", "shears.png", "spawner.png", "whiteCheck.png"};
        object = CollectionsKt.listOf((Object[])object);
        DungeonMapModule dungeonMapModule = INSTANCE;
        boolean $i$f$associateWith = false;
        LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
        void var4_4 = $this$associateWith$iv;
        Map destination$iv$iv = result$iv;
        boolean $i$f$associateWithTo = false;
        for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
            void p0;
            String string = (String)element$iv$iv;
            Object t = element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            Image image = dungeonMapModule.loadMapImage((String)p0);
            map.put(t, image);
        }
        return destination$iv$iv;
    }

    private static final Map checkmarkImages_delegate$lambda$0() {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)((Object)RoomCheckmark.WHITE), (Object)INSTANCE.getMapImages().get("whiteCheck.png")), TuplesKt.to((Object)((Object)RoomCheckmark.GREEN), (Object)INSTANCE.getMapImages().get("greenCheck.png")), TuplesKt.to((Object)((Object)RoomCheckmark.FAILED), (Object)INSTANCE.getMapImages().get("failedRoom.png")), TuplesKt.to((Object)((Object)RoomCheckmark.UNEXPLORED), (Object)INSTANCE.getMapImages().get("questionMark.png"))};
        return MapsKt.mapOf((Pair[])pairArray);
    }

    private static final Map roomIcons_delegate$lambda$0() {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"arrowtrap", (Object)INSTANCE.getMapImages().get("arrow.png")), TuplesKt.to((Object)"blaze", (Object)INSTANCE.getMapImages().get("blaze_powder.png")), TuplesKt.to((Object)"bombdefuse", (Object)INSTANCE.getMapImages().get("shears.png")), TuplesKt.to((Object)"boulder", (Object)INSTANCE.getMapImages().get("spawner.png")), TuplesKt.to((Object)"creeper", (Object)INSTANCE.getMapImages().get("creeper.png")), TuplesKt.to((Object)"creeperbeams", (Object)INSTANCE.getMapImages().get("creeper.png")), TuplesKt.to((Object)"icefill", (Object)INSTANCE.getMapImages().get("ice.png")), TuplesKt.to((Object)"icepath", (Object)INSTANCE.getMapImages().get("ice.png")), TuplesKt.to((Object)"quiz", (Object)INSTANCE.getMapImages().get("book_normal.png")), TuplesKt.to((Object)"teleportmaze", (Object)INSTANCE.getMapImages().get("endframe_side.png")), TuplesKt.to((Object)"threeweirdos", (Object)INSTANCE.getMapImages().get("chest.png")), TuplesKt.to((Object)"tictactoe", (Object)INSTANCE.getMapImages().get("planks_oak.png")), TuplesKt.to((Object)"waterboard", (Object)INSTANCE.getMapImages().get("bucket_water.png"))};
        return MapsKt.mapOf((Pair[])pairArray);
    }

    private static final float hud$lambda$0$0() {
        return (float)((Number)mapSize.getValue()).doubleValue() + 20.0f;
    }

    private static final float hud$lambda$0$1() {
        return (float)((Number)mapSize.getValue()).doubleValue() + 26.0f + 16.0f + 10.0f;
    }

    private static final Unit hud$lambda$0$2(float f, float f2, float f3) {
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        INSTANCE.renderHud();
        return Unit.INSTANCE;
    }

    private static final Unit hud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_RIGHT);
        $this$hudElement.setOffsetX(18.0f);
        $this$hudElement.setOffsetY(34.0f);
        $this$hudElement.setMinScale(0.65f);
        $this$hudElement.setMaxScale(2.2f);
        $this$hudElement.width((Function0<Float>)((Function0)DungeonMapModule::hud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)DungeonMapModule::hud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)DungeonMapModule::hud$lambda$0$2));
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        mapImages$delegate = LazyKt.lazy(DungeonMapModule::mapImages_delegate$lambda$0);
        checkmarkImages$delegate = LazyKt.lazy(DungeonMapModule::checkmarkImages_delegate$lambda$0);
        roomIcons$delegate = LazyKt.lazy(DungeonMapModule::roomIcons_delegate$lambda$0);
        enabled = new CheckboxSetting("Enabled", "Render the dungeon map HUD.", true);
        showWitherKeyRoom = new CheckboxSetting("Show Wither Key Room", "Highlight the room containing a dropped Wither Key.", true);
        funnyMode = new CheckboxSetting("Funny Mode", "Show scanned rooms even before they are explored on the vanilla map.", true);
        showCheckmarks = new CheckboxSetting("Show Checkmarks", "Show room completion dots.", true);
        showRoomNames = new CheckboxSetting("Show Room Names", "Show known room names on the map.", false);
        showRoomIcons = new CheckboxSetting("Show Room Icons", "Render bundled room and puzzle icons on the map when a match exists.", true);
        showPartyMarkers = new CheckboxSetting("Show Party Markers", "Show player markers from the dungeon map.", true);
        mapSize = new SliderSetting("Map Size", "Size of the square map area.", 168.0, 120.0, 280.0, 1.0);
        accentStart = new ColorSetting("Accent Start", "Header and border gradient start.", -11672891);
        accentEnd = new ColorSetting("Accent End", "Header and border gradient end.", -29606);
        roomNameColor = new ColorSetting("Name Color", "Color used for room labels.", -1250068);
        witherKeyRoomIndices = SetsKt.emptySet();
        hud = HudModuleDSLKt.hudElement(INSTANCE, "dungeon-map", "Dungeon Map", "Illegal-style dungeon map HUD", (Function1<? super HudElementBuilder, Unit>)((Function1)DungeonMapModule::hud$lambda$0));
        Setting[] settingArray = new Setting[]{enabled, showWitherKeyRoom, funnyMode, showCheckmarks, showRoomNames, showRoomIcons, showPartyMarkers, mapSize, accentStart, accentEnd, roomNameColor};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;
        public static final /* synthetic */ int[] $EnumSwitchMapping$2;

        static {
            int[] nArray = new int[RoomCheckmark.values().length];
            try {
                nArray[RoomCheckmark.WHITE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomCheckmark.GREEN.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomCheckmark.FAILED.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomCheckmark.UNEXPLORED.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomCheckmark.NONE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[RoomKind.values().length];
            try {
                nArray[RoomKind.ENTRANCE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.BLOOD.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.PUZZLE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.TRAP.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.FAIRY.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.YELLOW.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.RARE.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.NORMAL.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[RoomKind.UNKNOWN.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
            nArray = new int[DoorKind.values().length];
            try {
                nArray[DoorKind.ENTRANCE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DoorKind.BLOOD.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DoorKind.WITHER.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DoorKind.NORMAL.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$2 = nArray;
        }
    }
}

