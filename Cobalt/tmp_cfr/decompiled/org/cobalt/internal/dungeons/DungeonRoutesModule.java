/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.IndexedValue
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1420
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1297;
import net.minecraft.class_1420;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.helper.KeyBind;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.dungeons.map.DungeonRoom;
import org.cobalt.internal.dungeons.map.DungeonScanState;
import org.cobalt.internal.dungeons.map.LoadedRoute;
import org.cobalt.internal.dungeons.map.RouteStep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00ac\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0015\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ7\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u001f\u0010 \u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b \u0010!J5\u0010)\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u000f2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020#0\"2\u0006\u0010&\u001a\u00020%2\u0006\u0010(\u001a\u00020'H\u0002\u00a2\u0006\u0004\b)\u0010*J+\u0010.\u001a\b\u0012\u0004\u0012\u00020%0\"2\f\u0010+\u001a\b\u0012\u0004\u0012\u00020%0\"2\u0006\u0010-\u001a\u00020,H\u0002\u00a2\u0006\u0004\b.\u0010/J7\u00104\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u00100\u001a\u00020%2\u0006\u00101\u001a\u00020%2\u0006\u0010(\u001a\u00020'2\u0006\u00103\u001a\u000202H\u0002\u00a2\u0006\u0004\b4\u00105J'\u0010:\u001a\u0002092\u0006\u00106\u001a\u00020#2\u0006\u00107\u001a\u00020,2\u0006\u00108\u001a\u00020,H\u0002\u00a2\u0006\u0004\b:\u0010;J\u0017\u0010<\u001a\u0002092\u0006\u00106\u001a\u00020#H\u0002\u00a2\u0006\u0004\b<\u0010=J\u0017\u0010@\u001a\u00020'2\u0006\u0010?\u001a\u00020>H\u0002\u00a2\u0006\u0004\b@\u0010AJ\u001f\u0010C\u001a\u00020>2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010B\u001a\u00020>H\u0002\u00a2\u0006\u0004\bC\u0010DJ\u000f\u0010E\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bE\u0010\u0003J\u0017\u0010(\u001a\u00020'2\u0006\u0010F\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b(\u0010GJ\u001f\u0010I\u001a\u00020'2\u0006\u0010F\u001a\u00020\u00152\u0006\u0010H\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\bI\u0010JR\u0014\u0010K\u001a\u00020,8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bK\u0010LR\u0014\u0010M\u001a\u00020,8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bM\u0010LR\u0014\u0010N\u001a\u00020,8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bN\u0010LR\u0014\u0010O\u001a\u00020,8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bO\u0010LR\u0014\u0010Q\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010RR\u0014\u0010S\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u0010RR\u0014\u0010T\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bT\u0010RR\u0014\u0010U\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010RR\u0014\u0010V\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bV\u0010RR\u0014\u0010W\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010RR\u0014\u0010X\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bX\u0010RR\u0014\u0010Y\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010RR\u0014\u0010Z\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bZ\u0010RR\u0014\u0010[\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010RR\u0014\u0010\\\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010RR\u0014\u0010]\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010RR\u0014\u0010_\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010`R\u0014\u0010a\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010`R\u0014\u0010c\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0014\u0010e\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010dR\u0014\u0010f\u001a\u00020b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010dR\u0014\u0010h\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0014\u0010j\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bj\u0010iR\u0014\u0010k\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010iR\u0014\u0010l\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010iR\u0014\u0010m\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u0010iR\u0014\u0010n\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010iR\u0014\u0010o\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010iR\u0014\u0010p\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010iR\u0014\u0010q\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010iR\u0014\u0010r\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010iR\u0014\u0010s\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010iR\u0014\u0010t\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010iR\u0018\u0010u\u001a\u0004\u0018\u00010>8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bu\u0010vR\u0016\u0010w\u001a\u00020\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0016\u0010y\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\by\u0010zR\u0016\u0010{\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b{\u0010z\u00a8\u0006|"}, d2={"Lorg/cobalt/internal/dungeons/DungeonRoutesModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "room", "Lorg/cobalt/internal/dungeons/map/RouteStep;", "step", "", "stepNumber", "totalSteps", "renderStep", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lorg/cobalt/internal/dungeons/map/DungeonRoom;Lorg/cobalt/internal/dungeons/map/RouteStep;II)V", "Lorg/cobalt/internal/dungeons/map/LoadedRoute;", "route", "toggleRoute", "(Lorg/cobalt/internal/dungeons/map/LoadedRoute;)V", "", "sendChat", "advance", "(Lorg/cobalt/internal/dungeons/map/LoadedRoute;Z)V", "", "Lnet/minecraft/class_2338;", "pathNodes", "Lnet/minecraft/class_243;", "secretCenter", "Ljava/awt/Color;", "color", "drawPath", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Ljava/util/List;Lnet/minecraft/class_243;Ljava/awt/Color;)V", "path", "", "maxLen", "clampPath", "(Ljava/util/List;D)Ljava/util/List;", "start", "end", "", "lw", "drawDashedLine", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Ljava/awt/Color;F)V", "pos", "halfSize", "height", "Lnet/minecraft/class_238;", "markerBox", "(Lnet/minecraft/class_2338;DD)Lnet/minecraft/class_238;", "blockBox", "(Lnet/minecraft/class_2338;)Lnet/minecraft/class_238;", "", "type", "secretTypeColor", "(Ljava/lang/String;)Ljava/awt/Color;", "rawKey", "buildSignature", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;Ljava/lang/String;)Ljava/lang/String;", "resetRoute", "argb", "(I)Ljava/awt/Color;", "alpha", "colorAlpha", "(II)Ljava/awt/Color;", "MAX_PATH_LENGTH", "D", "DASH_LENGTH", "GAP_LENGTH", "BAT_RANGE_SQ", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "autoStart", "restartOnComplete", "showChatMessages", "progressive", "showPath", "showEtherwarps", "showMines", "showInteracts", "showEnderPearls", "showSecretTarget", "showSecretLabel", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "advanceRadius", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "lineWidth", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "startStopKey", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "nextKey", "prevKey", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "pathColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "etherwarpColor", "mineColor", "interactColor", "enderPearlColor", "chestColor", "itemColor", "batColor", "witherColor", "exitColor", "fairyColor", "defaultSecretColor", "activeSignature", "Ljava/lang/String;", "activeStepIndex", "I", "routeActive", "Z", "batSeenNearSecret", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDungeonRoutesModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DungeonRoutesModule.kt\norg/cobalt/internal/dungeons/DungeonRoutesModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,479:1\n1#2:480\n1#2:495\n1#2:509\n1#2:523\n1#2:537\n1#2:551\n1807#3,3:481\n1642#3,10:484\n1915#3:494\n1916#3:496\n1652#3:497\n1642#3,10:498\n1915#3:508\n1916#3:510\n1652#3:511\n1642#3,10:512\n1915#3:522\n1916#3:524\n1652#3:525\n1642#3,10:526\n1915#3:536\n1916#3:538\n1652#3:539\n1642#3,10:540\n1915#3:550\n1916#3:552\n1652#3:553\n363#3,7:554\n1915#3,2:561\n*S KotlinDebug\n*F\n+ 1 DungeonRoutesModule.kt\norg/cobalt/internal/dungeons/DungeonRoutesModule\n*L\n267#1:495\n272#1:509\n278#1:523\n284#1:537\n287#1:551\n196#1:481,3\n267#1:484,10\n267#1:494\n267#1:496\n267#1:497\n272#1:498,10\n272#1:508\n272#1:510\n272#1:511\n278#1:512,10\n278#1:522\n278#1:524\n278#1:525\n284#1:526,10\n284#1:536\n284#1:538\n284#1:539\n287#1:540,10\n287#1:550\n287#1:552\n287#1:553\n360#1:554,7\n372#1:561,2\n*E\n"})
public final class DungeonRoutesModule
extends Module {
    @NotNull
    public static final DungeonRoutesModule INSTANCE = new DungeonRoutesModule();
    private static final double MAX_PATH_LENGTH = 28.0;
    private static final double DASH_LENGTH = 0.5;
    private static final double GAP_LENGTH = 0.3;
    private static final double BAT_RANGE_SQ = 256.0;
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Render Hunch-style routes for the current dungeon room.", true);
    @NotNull
    private static final CheckboxSetting autoStart = new CheckboxSetting("Auto Start", "Automatically start the route when entering a new room.", true);
    @NotNull
    private static final CheckboxSetting restartOnComplete = new CheckboxSetting("Restart On Complete", "Restart the route after all secrets are reached.", false);
    @NotNull
    private static final CheckboxSetting showChatMessages = new CheckboxSetting("Chat Messages", "Show chat notifications for route events.", true);
    @NotNull
    private static final CheckboxSetting progressive = new CheckboxSetting("Progressive", "Only render the current step; auto-advance when you reach it.", true);
    @NotNull
    private static final CheckboxSetting showPath = new CheckboxSetting("Show Path", "Render dashed path lines and node boxes.", true);
    @NotNull
    private static final CheckboxSetting showEtherwarps = new CheckboxSetting("Show Etherwarps", "Render etherwarp spots.", true);
    @NotNull
    private static final CheckboxSetting showMines = new CheckboxSetting("Show Mines", "Render mine / stonk spots.", true);
    @NotNull
    private static final CheckboxSetting showInteracts = new CheckboxSetting("Show Interacts", "Render interact / TNT / lever spots.", true);
    @NotNull
    private static final CheckboxSetting showEnderPearls = new CheckboxSetting("Show Ender Pearls", "Render ender pearl throw positions.", true);
    @NotNull
    private static final CheckboxSetting showSecretTarget = new CheckboxSetting("Show Secret Target", "Render a box at the target secret.", true);
    @NotNull
    private static final CheckboxSetting showSecretLabel = new CheckboxSetting("Show Secret Label", "Show 'N/Total TYPE' label above the secret box.", true);
    @NotNull
    private static final SliderSetting advanceRadius = new SliderSetting("Advance Radius", "Proximity radius (blocks) for ITEM / EXITROUTE auto-advance.", 2.0, 1.0, 6.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting lineWidth = new SliderSetting("Line Width", "Path line thickness.", 3.0, 1.0, 6.0, 0.0, 32, null);
    @NotNull
    private static final KeyBindSetting startStopKey = new KeyBindSetting("Start / Stop Key", "Toggle route active state.", new KeyBind(-1));
    @NotNull
    private static final KeyBindSetting nextKey = new KeyBindSetting("Next Waypoint Key", "Skip to the next route step.", new KeyBind(-1));
    @NotNull
    private static final KeyBindSetting prevKey = new KeyBindSetting("Prev Waypoint Key", "Go back to the previous step.", new KeyBind(-1));
    @NotNull
    private static final ColorSetting pathColor = new ColorSetting("Path Color", "Primary route path color.", -11936569);
    @NotNull
    private static final ColorSetting etherwarpColor = new ColorSetting("Etherwarp Color", "Etherwarp marker color.", -6390529);
    @NotNull
    private static final ColorSetting mineColor = new ColorSetting("Mine Color", "Mine / stonk marker color.", -23985);
    @NotNull
    private static final ColorSetting interactColor = new ColorSetting("Interact Color", "Interact / TNT / lever marker color.", -8527509);
    @NotNull
    private static final ColorSetting enderPearlColor = new ColorSetting("Ender Pearl Color", "Ender pearl throw marker color.", -13011512);
    @NotNull
    private static final ColorSetting chestColor = new ColorSetting("Chest Color", "Color for CHEST secrets.", -16591366);
    @NotNull
    private static final ColorSetting itemColor = new ColorSetting("Item Color", "Color for ITEM secrets.", -16629510);
    @NotNull
    private static final ColorSetting batColor = new ColorSetting("Bat Color", "Color for BAT secrets.", -7454208);
    @NotNull
    private static final ColorSetting witherColor = new ColorSetting("Wither Color", "Color for WITHER secrets.", -11184811);
    @NotNull
    private static final ColorSetting exitColor = new ColorSetting("Exit Color", "Color for EXITROUTE secrets.", -16711808);
    @NotNull
    private static final ColorSetting fairyColor = new ColorSetting("Fairy Color", "Color for FAIRYSOUL secrets.", -30465);
    @NotNull
    private static final ColorSetting defaultSecretColor = new ColorSetting("Secret Color", "Color for all other secrets.", -7574);
    @Nullable
    private static String activeSignature;
    private static int activeStepIndex;
    private static boolean routeActive;
    private static boolean batSeenNearSecret;

    private DungeonRoutesModule() {
        super("Dungeon Routes");
    }

    /*
     * Unable to fully structure code
     */
    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        block29: {
            Intrinsics.checkNotNullParameter((Object)event, (String)"event");
            if (!((Boolean)DungeonRoutesModule.enabled.getValue()).booleanValue()) {
                return;
            }
            DungeonScanState.INSTANCE.tick();
            v0 = class_310.method_1551().field_1724;
            if (v0 == null) {
                $this$onTick_u24lambda_u240 = this;
                $i$a$-run-DungeonRoutesModule$onTick$player$1 = false;
                $this$onTick_u24lambda_u240.resetRoute();
                return;
            }
            player = v0;
            v1 = DungeonScanState.INSTANCE.getCurrentRoom();
            if (v1 == null) {
                $this$onTick_u24lambda_u241 = this;
                $i$a$-run-DungeonRoutesModule$onTick$room$1 = false;
                $this$onTick_u24lambda_u241.resetRoute();
                return;
            }
            room = v1;
            v2 = DungeonScanState.INSTANCE.resolveRoute(room);
            if (v2 == null) {
                $this$onTick_u24lambda_u242 = this;
                $i$a$-run-DungeonRoutesModule$onTick$route$1 = false;
                $this$onTick_u24lambda_u242.resetRoute();
                return;
            }
            route = v2;
            signature = this.buildSignature(room, route.getRawKey());
            if (!Intrinsics.areEqual((Object)signature, (Object)DungeonRoutesModule.activeSignature)) {
                DungeonRoutesModule.activeSignature = signature;
                DungeonRoutesModule.activeStepIndex = 0;
                DungeonRoutesModule.routeActive = (Boolean)DungeonRoutesModule.autoStart.getValue();
                if (DungeonRoutesModule.routeActive && ((Boolean)DungeonRoutesModule.showChatMessages.getValue()).booleanValue()) {
                    ChatUtils.sendMessage("Route started - " + route.getSteps().size() + " secrets");
                }
            }
            if (((KeyBind)DungeonRoutesModule.startStopKey.getValue()).isPressed()) {
                this.toggleRoute(route);
            }
            if (DungeonRoutesModule.routeActive) {
                if (((KeyBind)DungeonRoutesModule.nextKey.getValue()).isPressed()) {
                    this.advance(route, true);
                }
                if (((KeyBind)DungeonRoutesModule.prevKey.getValue()).isPressed() && DungeonRoutesModule.activeStepIndex > 0) {
                    $this$onTick_u24lambda_u241 = DungeonRoutesModule.activeStepIndex;
                    DungeonRoutesModule.activeStepIndex = $this$onTick_u24lambda_u241 + -1;
                    if (((Boolean)DungeonRoutesModule.showChatMessages.getValue()).booleanValue()) {
                        step = (RouteStep)CollectionsKt.getOrNull(route.getSteps(), (int)DungeonRoutesModule.activeStepIndex);
                        v3 = route.getSteps().size();
                        v4 = step;
                        if (v4 != null && (v4 = v4.getSecretType()) != null) {
                            v5 = v4.toUpperCase(Locale.ROOT);
                            v6 = v5;
                            Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"toUpperCase(...)");
                        } else {
                            v6 = v7 = null;
                        }
                        if (v6 == null) {
                            v7 = "";
                        }
                        ChatUtils.sendMessage("Waypoint " + (DungeonRoutesModule.activeStepIndex + 1) + "/" + v3 + " " + v7);
                    }
                }
            }
            if (!((Boolean)DungeonRoutesModule.progressive.getValue()).booleanValue() || !DungeonRoutesModule.routeActive) {
                return;
            }
            v8 = (RouteStep)CollectionsKt.getOrNull(route.getSteps(), (int)DungeonRoutesModule.activeStepIndex);
            if (v8 == null) {
                return;
            }
            step = v8;
            v9 = DungeonScanState.INSTANCE.relativeToActual(room, step.getSecretPos());
            if (v9 == null) {
                return;
            }
            target = v9;
            v10 = class_243.method_24953((class_2382)((class_2382)target));
            Intrinsics.checkNotNullExpressionValue((Object)v10, (String)"atCenterOf(...)");
            targetCenter = v10;
            v11 = step.getSecretType().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v11, (String)"toLowerCase(...)");
            var9_18 = v11;
            switch (var9_18.hashCode()) {
                case 3242771: {
                    if (!var9_18.equals("item")) {
                        ** break;
                    }
                    ** GOTO lbl80
                }
                case 97301: {
                    if (var9_18.equals("bat")) break;
                    ** break;
                }
                case -1352139797: {
                    if (!var9_18.equals("exitroute")) ** break;
lbl80:
                    // 2 sources

                    if (!((distSq = player.method_73189().method_1025(targetCenter)) <= ((Number)DungeonRoutesModule.advanceRadius.getValue()).doubleValue() * ((Number)DungeonRoutesModule.advanceRadius.getValue()).doubleValue())) ** break;
                    this.advance(route, (Boolean)DungeonRoutesModule.showChatMessages.getValue());
                    ** break;
                }
            }
            v12 = class_310.method_1551().field_1687;
            if (v12 == null) {
                return;
            }
            level = v12;
            v13 = level.method_18112();
            Intrinsics.checkNotNullExpressionValue((Object)v13, (String)"entitiesForRendering(...)");
            $this$any$iv = v13;
            $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                v14 = false;
            } else {
                for (T element$iv : $this$any$iv) {
                    entity = (class_1297)element$iv;
                    $i$a$-any-DungeonRoutesModule$onTick$batsNearby$1 = false;
                    if (!(entity instanceof class_1420 != false && entity.method_73189().method_1025(targetCenter) <= 256.0)) continue;
                    v14 = true;
                    break block29;
                }
                v14 = batsNearby = false;
            }
        }
        if (DungeonRoutesModule.batSeenNearSecret && !batsNearby) {
            DungeonRoutesModule.batSeenNearSecret = false;
            this.advance(route, (Boolean)DungeonRoutesModule.showChatMessages.getValue());
        } else {
            DungeonRoutesModule.batSeenNearSecret = batsNearby;
        }
lbl108:
        // 6 sources

    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!(((Boolean)enabled.getValue()).booleanValue() && routeActive && ((Boolean)progressive.getValue()).booleanValue())) {
            return;
        }
        DungeonRoom dungeonRoom = DungeonScanState.INSTANCE.getCurrentRoom();
        if (dungeonRoom == null) {
            return;
        }
        DungeonRoom room = dungeonRoom;
        LoadedRoute loadedRoute = DungeonScanState.INSTANCE.resolveRoute(room);
        if (loadedRoute == null) {
            return;
        }
        LoadedRoute route = loadedRoute;
        RouteStep routeStep = (RouteStep)CollectionsKt.getOrNull(route.getSteps(), (int)activeStepIndex);
        if (routeStep == null) {
            return;
        }
        RouteStep step = routeStep;
        class_2338 class_23382 = DungeonScanState.INSTANCE.relativeToActual(room, step.getSecretPos());
        if (class_23382 == null) {
            return;
        }
        class_2338 secretActual = class_23382;
        if (Intrinsics.areEqual((Object)event.getPos(), (Object)secretActual)) {
            this.advance(route, (Boolean)showChatMessages.getValue());
            return;
        }
        for (class_2338 interactPos : step.getInteracts()) {
            class_2338 actual;
            if (DungeonScanState.INSTANCE.relativeToActual(room, interactPos) == null || !Intrinsics.areEqual((Object)event.getPos(), (Object)actual)) continue;
            this.advance(route, (Boolean)showChatMessages.getValue());
            return;
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        List list;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        DungeonRoom dungeonRoom = DungeonScanState.INSTANCE.getCurrentRoom();
        if (dungeonRoom == null) {
            return;
        }
        DungeonRoom room = dungeonRoom;
        LoadedRoute loadedRoute = DungeonScanState.INSTANCE.resolveRoute(room);
        if (loadedRoute == null) {
            return;
        }
        LoadedRoute route = loadedRoute;
        if (room.getCorner() == null || room.getDirection() == null) {
            return;
        }
        if (((Boolean)progressive.getValue()).booleanValue() && routeActive) {
            RouteStep routeStep = (RouteStep)CollectionsKt.getOrNull(route.getSteps(), (int)activeStepIndex);
            if (routeStep == null) {
                return;
            }
            RouteStep step = routeStep;
            list = CollectionsKt.listOf((Object)new IndexedValue(activeStepIndex, (Object)step));
        } else if (!((Boolean)progressive.getValue()).booleanValue()) {
            list = CollectionsKt.toList((Iterable)CollectionsKt.withIndex((Iterable)route.getSteps()));
        } else {
            return;
        }
        List stepsToRender = list;
        for (IndexedValue indexedValue : stepsToRender) {
            int index = indexedValue.component1();
            RouteStep step = (RouteStep)indexedValue.component2();
            this.renderStep(event.getContext(), room, step, index + 1, route.getSteps().size());
        }
    }

    private final void renderStep(WorldRenderContext context, DungeonRoom room, RouteStep step, int stepNumber, int totalSteps) {
        class_2338 it$iv$iv;
        boolean bl;
        class_2338 it;
        boolean bl2;
        Object element$iv$iv;
        Object element$iv$iv$iv;
        Iterator iterator;
        boolean $i$f$forEach;
        Iterable $this$forEach$iv$iv$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        boolean $i$f$mapNotNullTo;
        Collection destination$iv$iv;
        boolean $i$f$mapNotNull;
        Iterable $this$mapNotNull$iv;
        class_2338 target;
        Object object = target = (Boolean)showSecretTarget.getValue() != false || (Boolean)showPath.getValue() != false ? DungeonScanState.INSTANCE.relativeToActual(room, step.getSecretPos()) : null;
        if (((Boolean)showPath.getValue()).booleanValue() && target != null) {
            $this$mapNotNull$iv = step.getPathLocations();
            $i$f$mapNotNull = false;
            Iterable iterable = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            $i$f$mapNotNullTo = false;
            $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            $i$f$forEach = false;
            iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = element$iv$iv$iv = iterator.next();
                bl2 = false;
                it = (class_2338)element$iv$iv;
                boolean bl3 = false;
                if (DungeonScanState.INSTANCE.relativeToActual(room, it) == null) continue;
                bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            Iterator pathNodes = (List)destination$iv$iv;
            class_243 class_2432 = class_243.method_24953((class_2382)((class_2382)target));
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"atCenterOf(...)");
            this.drawPath(context, (List<? extends class_2338>)((Object)pathNodes), class_2432, this.color(pathColor.getValue()));
        }
        if (((Boolean)showEtherwarps.getValue()).booleanValue()) {
            $this$mapNotNull$iv = step.getEtherwarps();
            $i$f$mapNotNull = false;
            $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            $i$f$mapNotNullTo = false;
            $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            $i$f$forEach = false;
            iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = element$iv$iv$iv = iterator.next();
                bl2 = false;
                it = (class_2338)element$iv$iv;
                boolean bl4 = false;
                if (DungeonScanState.INSTANCE.relativeToActual(room, it) == null) continue;
                bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            for (class_2338 pos : (List)destination$iv$iv) {
                Render3D.drawStyledBox(context, this.blockBox(pos), this.color(etherwarpColor.getValue()), this.colorAlpha(etherwarpColor.getValue(), 80), true, 2.2f);
            }
        }
        if (((Boolean)showMines.getValue()).booleanValue()) {
            $this$mapNotNull$iv = step.getMines();
            $i$f$mapNotNull = false;
            $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            $i$f$mapNotNullTo = false;
            $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            $i$f$forEach = false;
            iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = element$iv$iv$iv = iterator.next();
                bl2 = false;
                it = (class_2338)element$iv$iv;
                boolean bl5 = false;
                if (DungeonScanState.INSTANCE.relativeToActual(room, it) == null) continue;
                bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            for (class_2338 pos : (List)destination$iv$iv) {
                Render3D.drawStyledBox(context, this.blockBox(pos), this.color(mineColor.getValue()), this.colorAlpha(mineColor.getValue(), 70), true, 1.9f);
            }
        }
        if (((Boolean)showInteracts.getValue()).booleanValue()) {
            $this$mapNotNull$iv = step.getInteracts();
            $i$f$mapNotNull = false;
            $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            $i$f$mapNotNullTo = false;
            $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            $i$f$forEach = false;
            iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = element$iv$iv$iv = iterator.next();
                bl2 = false;
                it = (class_2338)element$iv$iv;
                boolean bl6 = false;
                if (DungeonScanState.INSTANCE.relativeToActual(room, it) == null) continue;
                bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            for (class_2338 pos : (List)destination$iv$iv) {
                Render3D.drawStyledBox(context, this.blockBox(pos), this.color(interactColor.getValue()), this.colorAlpha(interactColor.getValue(), 70), true, 2.0f);
            }
            $this$mapNotNull$iv = step.getTnts();
            $i$f$mapNotNull = false;
            $this$mapNotNullTo$iv$iv = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            $i$f$mapNotNullTo = false;
            $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            $i$f$forEach = false;
            iterator = $this$forEach$iv$iv$iv.iterator();
            while (iterator.hasNext()) {
                element$iv$iv = element$iv$iv$iv = iterator.next();
                bl2 = false;
                it = (class_2338)element$iv$iv;
                boolean bl7 = false;
                if (DungeonScanState.INSTANCE.relativeToActual(room, it) == null) continue;
                bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            for (class_2338 pos : (List)destination$iv$iv) {
                Render3D.drawStyledBox(context, this.blockBox(pos), this.color(interactColor.getValue()), this.colorAlpha(interactColor.getValue(), 55), true, 2.0f);
            }
        }
        if (((Boolean)showEnderPearls.getValue()).booleanValue()) {
            for (class_2338 pos : step.getEnderPearls()) {
                Render3D.drawStyledBox(context, this.blockBox(pos), this.color(enderPearlColor.getValue()), this.colorAlpha(enderPearlColor.getValue(), 85), true, 2.4f);
            }
        }
        if (((Boolean)showSecretTarget.getValue()).booleanValue() && target != null) {
            Color typeColor = this.secretTypeColor(step.getSecretType());
            Render3D.drawStyledBox(context, this.blockBox(target), typeColor, new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), 90), true, 2.6f);
            if (((Boolean)showSecretLabel.getValue()).booleanValue()) {
                class_243 class_2433 = class_243.method_24953((class_2382)((class_2382)target)).method_1031(0.0, 1.4, 0.0);
                Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
                class_243 labelPos = class_2433;
                String string = step.getSecretType().toUpperCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toUpperCase(...)");
                Render3D.drawWorldLabel(context, labelPos, stepNumber + "/" + totalSteps + " " + string, typeColor);
            }
        }
    }

    private final void toggleRoute(LoadedRoute route) {
        boolean bl = routeActive = !routeActive;
        if (routeActive) {
            activeStepIndex = 0;
            if (((Boolean)showChatMessages.getValue()).booleanValue()) {
                ChatUtils.sendMessage("Route started - " + route.getSteps().size() + " secrets");
            }
        } else if (((Boolean)showChatMessages.getValue()).booleanValue()) {
            ChatUtils.sendMessage("Route stopped.");
        }
    }

    private final void advance(LoadedRoute route, boolean sendChat) {
        batSeenNearSecret = false;
        int next = activeStepIndex + 1;
        if (next > CollectionsKt.getLastIndex(route.getSteps())) {
            if (((Boolean)restartOnComplete.getValue()).booleanValue()) {
                activeStepIndex = 0;
                if (sendChat) {
                    ChatUtils.sendMessage("Route restarted!");
                }
            } else {
                routeActive = false;
                if (sendChat) {
                    ChatUtils.sendMessage("Route completed!");
                }
            }
        } else {
            activeStepIndex = next;
            if (sendChat) {
                String string;
                String string2;
                RouteStep step = (RouteStep)CollectionsKt.getOrNull(route.getSteps(), (int)next);
                int n = route.getSteps().size();
                Object object = step;
                if (object != null && (object = ((RouteStep)object).getSecretType()) != null) {
                    String string3 = ((String)object).toUpperCase(Locale.ROOT);
                    string2 = string3;
                    Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toUpperCase(...)");
                } else {
                    string2 = string = null;
                }
                if (string2 == null) {
                    string = "";
                }
                ChatUtils.sendMessage("Waypoint " + (next + 1) + "/" + n + " " + string);
            }
        }
    }

    private final void drawPath(WorldRenderContext context, List<? extends class_2338> pathNodes, class_243 secretCenter, Color color) {
        List index$iv2;
        int n;
        class_243 playerPos;
        block5: {
            class_746 class_7462 = class_310.method_1551().field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            class_243 class_2432 = player.method_73189();
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
            playerPos = class_2432;
            double radSq = ((Number)advanceRadius.getValue()).doubleValue() * ((Number)advanceRadius.getValue()).doubleValue();
            List<? extends class_2338> $this$indexOfFirst$iv = pathNodes;
            boolean $i$f$indexOfFirst = false;
            int index$iv2 = 0;
            Iterator<? extends class_2338> iterator = $this$indexOfFirst$iv.iterator();
            while (iterator.hasNext()) {
                class_2338 item$iv;
                class_2338 pos = item$iv = iterator.next();
                boolean bl = false;
                if (playerPos.method_1025(class_243.method_24953((class_2382)((class_2382)pos))) > radSq) {
                    n = index$iv2;
                    break block5;
                }
                ++index$iv2;
            }
            n = -1;
        }
        int firstUnreachedIdx = n;
        List<? extends class_2338> unreachedNodes = firstUnreachedIdx >= 0 ? pathNodes.subList(firstUnreachedIdx, pathNodes.size()) : CollectionsKt.emptyList();
        List $this$drawPath_u24lambda_u241 = index$iv2 = CollectionsKt.createListBuilder();
        boolean bl = false;
        class_243 class_2433 = playerPos.method_1031(0.0, 0.9, 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
        $this$drawPath_u24lambda_u241.add(class_2433);
        Iterable $this$forEach$iv = unreachedNodes;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            class_2338 pos = (class_2338)element$iv;
            boolean bl2 = false;
            class_243 class_2434 = class_243.method_24953((class_2382)((class_2382)pos)).method_1031(0.0, 0.15, 0.0);
            Intrinsics.checkNotNullExpressionValue((Object)class_2434, (String)"add(...)");
            $this$drawPath_u24lambda_u241.add(class_2434);
        }
        class_243 class_2435 = secretCenter.method_1031(0.0, 0.5, 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)class_2435, (String)"add(...)");
        $this$drawPath_u24lambda_u241.add(class_2435);
        List rawPath = CollectionsKt.build((List)index$iv2);
        List<class_243> effectivePath = this.clampPath(rawPath, 28.0);
        float lw = (float)((Number)lineWidth.getValue()).doubleValue();
        int n2 = CollectionsKt.getLastIndex(effectivePath);
        for (int i = 0; i < n2; ++i) {
            this.drawDashedLine(context, effectivePath.get(i), effectivePath.get(i + 1), color, lw);
        }
    }

    private final List<class_243> clampPath(List<? extends class_243> path, double maxLen) {
        if (path.size() < 2) {
            return path;
        }
        Object[] objectArray = new class_243[]{CollectionsKt.first(path)};
        List result = CollectionsKt.mutableListOf((Object[])objectArray);
        double walked = 0.0;
        int n = path.size();
        for (int i = 1; i < n; ++i) {
            class_243 curr;
            class_243 prev = path.get(i - 1);
            double seg = Math.sqrt(prev.method_1025(curr = path.get(i)));
            if (walked + seg >= maxLen) {
                double remaining = maxLen - walked;
                class_243 class_2432 = curr.method_1020(prev).method_1029();
                Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"normalize(...)");
                class_243 dir = class_2432;
                class_243 class_2433 = prev.method_1019(dir.method_1021(remaining));
                Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"add(...)");
                result.add(class_2433);
                break;
            }
            walked += seg;
            result.add(curr);
        }
        return result;
    }

    private final void drawDashedLine(WorldRenderContext context, class_243 start, class_243 end, Color color, float lw) {
        class_243 class_2432 = end.method_1020(start);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"subtract(...)");
        class_243 segVec = class_2432;
        double totalLen = Math.sqrt(segVec.method_1027());
        if (totalLen < 0.001) {
            return;
        }
        class_243 class_2433 = segVec.method_1029();
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"normalize(...)");
        class_243 dir = class_2433;
        double pos = 0.0;
        boolean drawing = true;
        while (pos < totalLen) {
            double chunkLen = drawing ? 0.5 : 0.3;
            double nextPos = Math.min(pos + chunkLen, totalLen);
            if (drawing) {
                class_243 e;
                class_243 s;
                Intrinsics.checkNotNullExpressionValue((Object)start.method_1019(dir.method_1021(pos)), (String)"add(...)");
                Intrinsics.checkNotNullExpressionValue((Object)start.method_1019(dir.method_1021(nextPos)), (String)"add(...)");
                Render3D.drawLine(context, s, e, color, true, lw);
            }
            pos = nextPos;
            drawing = !drawing;
        }
    }

    private final class_238 markerBox(class_2338 pos, double halfSize, double height) {
        return new class_238((double)pos.method_10263() + 0.5 - halfSize, (double)pos.method_10264() + 0.03, (double)pos.method_10260() + 0.5 - halfSize, (double)pos.method_10263() + 0.5 + halfSize, (double)pos.method_10264() + height, (double)pos.method_10260() + 0.5 + halfSize);
    }

    private final class_238 blockBox(class_2338 pos) {
        return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1.0, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 1.0);
    }

    private final Color secretTypeColor(String type) {
        String string = type.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        return switch (string) {
            case "chest" -> this.color(chestColor.getValue());
            case "item" -> this.color(itemColor.getValue());
            case "bat" -> this.color(batColor.getValue());
            case "wither" -> this.color(witherColor.getValue());
            case "exitroute" -> this.color(exitColor.getValue());
            case "interact", "lever" -> this.color(interactColor.getValue());
            case "fairysoul" -> this.color(fairyColor.getValue());
            default -> this.color(defaultSecretColor.getValue());
        };
    }

    private final String buildSignature(DungeonRoom room, String rawKey) {
        Object[] objectArray = new Object[5];
        objectArray[0] = rawKey;
        String string = room.getName();
        if (string == null) {
            string = "";
        }
        objectArray[1] = string;
        class_2338 class_23382 = room.getCorner();
        objectArray[2] = class_23382 != null ? class_23382.method_10263() : 0;
        class_2338 class_23383 = room.getCorner();
        objectArray[3] = class_23383 != null ? class_23383.method_10260() : 0;
        objectArray[4] = room.getRotation();
        return CollectionsKt.joinToString$default((Iterable)CollectionsKt.listOf((Object[])objectArray), (CharSequence)":", null, null, (int)0, null, null, (int)62, null);
    }

    private final void resetRoute() {
        if (activeSignature == null) {
            return;
        }
        activeSignature = null;
        activeStepIndex = 0;
        routeActive = false;
        batSeenNearSecret = false;
    }

    private final Color color(int argb) {
        return new Color(argb, true);
    }

    private final Color colorAlpha(int argb, int alpha) {
        Color c = new Color(argb, true);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), RangesKt.coerceIn((int)alpha, (int)0, (int)255));
    }

    static {
        Setting[] settingArray = new Setting[]{enabled, autoStart, restartOnComplete, showChatMessages, progressive, showPath, showEtherwarps, showMines, showInteracts, showEnderPearls, showSecretTarget, showSecretLabel, advanceRadius, lineWidth, startStopKey, nextKey, prevKey, pathColor, etherwarpColor, mineColor, interactColor, enderPearlColor, chestColor, itemColor, batColor, witherColor, exitColor, fairyColor, defaultSecretColor};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

