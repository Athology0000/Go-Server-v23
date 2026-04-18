/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.random.Random
 *  kotlin.ranges.LongRange
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.random.Random;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenHud;
import org.cobalt.internal.garden.GardenState;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.internal.garden.ScriptBridge;
import org.cobalt.internal.garden.managers.BookCombineManager;
import org.cobalt.internal.garden.managers.BoosterCookieManager;
import org.cobalt.internal.garden.managers.CropFeverManager;
import org.cobalt.internal.garden.managers.DynamicRestManager;
import org.cobalt.internal.garden.managers.EquipmentManager;
import org.cobalt.internal.garden.managers.FlightManager;
import org.cobalt.internal.garden.managers.GearManager;
import org.cobalt.internal.garden.managers.GeorgeManager;
import org.cobalt.internal.garden.managers.JunkManager;
import org.cobalt.internal.garden.managers.PestAotvManager;
import org.cobalt.internal.garden.managers.PestBonusManager;
import org.cobalt.internal.garden.managers.PestCleaningSequencer;
import org.cobalt.internal.garden.managers.PestManager;
import org.cobalt.internal.garden.managers.PestPrepSwapManager;
import org.cobalt.internal.garden.managers.PestReturnManager;
import org.cobalt.internal.garden.managers.PetXpTracker;
import org.cobalt.internal.garden.managers.ProfitManager;
import org.cobalt.internal.garden.managers.RecoveryManager;
import org.cobalt.internal.garden.managers.RestartManager;
import org.cobalt.internal.garden.managers.RodManager;
import org.cobalt.internal.garden.managers.VisitorManager;
import org.cobalt.internal.garden.managers.WardrobeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\bB\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\u0003J\u0017\u0010\f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u000eH\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0003J\u0017\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0016\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0003J\u0017\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0017H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u000f\u0010\u001a\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u0003J\u000f\u0010\u001b\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u0003R\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0011\u0010 \u001a\u00020\u001f8F\u00a2\u0006\u0006\u001a\u0004\b \u0010!R$\u0010#\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00048\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b#\u0010$\u001a\u0004\b%\u0010&R\"\u0010(\u001a\u00020'8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b(\u0010)\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R$\u0010.\u001a\u0004\u0018\u00010\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b.\u0010/\u001a\u0004\b0\u00101\"\u0004\b2\u0010\u0015R\u0016\u00103\u001a\u00020\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0016\u00105\u001a\u00020\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b5\u00104R\u0016\u00106\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b6\u0010)R\u0014\u00108\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b8\u00109R\u0014\u0010;\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010<R\u0014\u0010=\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b=\u0010<R\u0014\u0010>\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010<R\u0014\u0010?\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b?\u0010<R\u0014\u0010@\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u00109R\u0014\u0010B\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bB\u0010CR\u0014\u0010D\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bD\u00109R\u0014\u0010E\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bE\u0010CR\u0014\u0010F\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u00109R\u0014\u0010G\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bG\u0010CR\u0014\u0010H\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010CR\u0014\u0010I\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u00109R\u0014\u0010J\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bJ\u0010<R\u0014\u0010K\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u00109R\u0014\u0010L\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bL\u00109R\u0014\u0010M\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u00109R\u0014\u0010N\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u00109R\u0014\u0010O\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010<R\u0014\u0010P\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u00109R\u0014\u0010Q\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010CR\u0014\u0010R\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u00109R\u0014\u0010S\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u00109R\u0014\u0010T\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bT\u00109R\u0014\u0010U\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010CR\u0014\u0010V\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bV\u0010CR\u0014\u0010W\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010CR\u0014\u0010X\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bX\u00109R\u0014\u0010Y\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010<R\u0014\u0010Z\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bZ\u0010<R\u0014\u0010[\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010<R\u0014\u0010\\\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010CR\u0014\u0010]\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u00109R\u0014\u0010^\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b^\u00109R\u0014\u0010_\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u00109R\u0014\u0010`\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010CR\u0014\u0010a\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u00109R\u0014\u0010b\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010<R\u0014\u0010c\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010CR\u0014\u0010d\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u00109R\u0014\u0010e\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010CR\u0014\u0010f\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010CR\u0014\u0010g\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bg\u0010CR\u0014\u0010h\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u00109R\u0014\u0010i\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u00109R\u0014\u0010j\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bj\u0010<R\u0014\u0010k\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010CR\u0014\u0010l\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010CR\u0014\u0010m\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u00109R\u0014\u0010n\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010<R\u0014\u0010o\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010<R\u0014\u0010p\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u00109R\u0014\u0010q\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010CR\u0014\u0010r\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010CR\u0014\u0010s\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010CR\u0014\u0010t\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010CR\u0014\u0010u\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010CR\u0014\u0010v\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bv\u00109R\u0014\u0010w\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u00109R\u0014\u0010x\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u00109R\u0014\u0010y\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u00109R\u0014\u0010z\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u00109R\u0014\u0010{\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010CR\u0014\u0010|\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010CR\u0014\u0010}\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b}\u0010CR\u0014\u0010~\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010CR\u0014\u0010\u007f\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u007f\u0010CR\u0016\u0010\u0080\u0001\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010CR\u0016\u0010\u0081\u0001\u001a\u00020A8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010CR\u0016\u0010\u0082\u0001\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010<R\u0016\u0010\u0083\u0001\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0083\u0001\u0010<R\u0018\u0010\u0085\u0001\u001a\u00030\u0084\u00018\u0002X\u0082D\u00a2\u0006\b\n\u0006\b\u0085\u0001\u0010\u0086\u0001R\u0018\u0010\u0088\u0001\u001a\u00030\u0087\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0089\u0001R\u0016\u0010\u008a\u0001\u001a\u00020'8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u008a\u0001\u0010)R\u0016\u0010\u008b\u0001\u001a\u00020'8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u008b\u0001\u0010)R\u0016\u0010\u008c\u0001\u001a\u00020'8\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u008c\u0001\u0010)\u00a8\u0006\u008d\u0001"}, d2={"Lorg/cobalt/internal/garden/GardenMacroModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/internal/garden/GardenState;", "newState", "", "setState", "(Lorg/cobalt/internal/garden/GardenState;)V", "syncConfig", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "tickFarming", "", "message", "handleUnexpectedScriptStop", "(Ljava/lang/String;)V", "queueUnexpectedScriptRestart", "Lkotlin/Function0;", "resolveUnexpectedStopRestartAction", "()Lkotlin/jvm/functions/Function0;", "startMacro", "stopMacro", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "isActive", "()Z", "value", "state", "Lorg/cobalt/internal/garden/GardenState;", "getState", "()Lorg/cobalt/internal/garden/GardenState;", "", "sessionStartTime", "J", "getSessionStartTime", "()J", "setSessionStartTime", "(J)V", "autosellingManager", "Ljava/lang/String;", "getAutosellingManager", "()Ljava/lang/String;", "setAutosellingManager", "wasConnected", "Z", "autoRestartQueued", "startupGraceUntilMs", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "farmScriptSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "pestScriptSetting", "returnScriptSetting", "visitorScriptSetting", "autoRestartStoppedScriptSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "pestThresholdSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "triggerPestOnChatSetting", "pestChatTriggerDelaySetting", "aotvEnabledSetting", "roofPitchSetting", "aotvRoofPitchHumanizationSetting", "prepSwapSetting", "aotvPlotsSetting", "breakBlocksBeforeAotvSetting", "delayPestForCropFeverSetting", "enablePlotTpRewarpSetting", "holdWUntilWallSetting", "plotTpNumberSetting", "autoVisitorSetting", "visitorThresholdSetting", "autoWardrobeSetting", "autoWardrobePestSetting", "autoWardrobeVisitorSetting", "farmingSlotSetting", "pestSlotSetting", "visitorSlotSetting", "autoEquipmentSetting", "farmingEquipmentSetting", "pestEquipmentSetting", "visitorEquipmentSetting", "swapDelaySetting", "autoRodPestCdSetting", "autoRodPestSpawnSetting", "autoRodReturnToFarmSetting", "rodSwapDelaySetting", "autoGeorgeSellSetting", "georgeRaritySetting", "georgeSellThresholdSetting", "autoBookCombineSetting", "bookLevelSetting", "bookThresholdSetting", "bookCombineDelaySetting", "alwaysActiveCombineSetting", "autoDropJunkSetting", "junkItemsSetting", "junkThresholdSetting", "junkItemDropDelaySetting", "autoBoosterCookieSetting", "boosterCookieItemsSetting", "cookieItemSetting", "autoStashManagerSetting", "bazaarRefreshSetting", "workDurationSetting", "workOffsetSetting", "breakDurationSetting", "breakOffsetSetting", "persistSessionTimerSetting", "autoResumeAfterDynamicRestSetting", "hideFilteredChatSetting", "guiOnlyInGardenSetting", "autoRecoverUnexpectedDisconnectSetting", "maxRecoverySetting", "reconnectMinSetting", "reconnectMaxSetting", "rotationTimeSetting", "guiClickDelaySetting", "additionalRandomDelaySetting", "gardenWarpDelaySetting", "unflyModeSetting", "petTrackerListSetting", "", "hudWidth", "F", "Lorg/cobalt/api/hud/HudElement;", "gardenHudElement", "Lorg/cobalt/api/hud/HudElement;", "AUTO_RESTART_WAIT_TIMEOUT_MS", "AUTO_RESTART_POLL_MS", "AUTO_RESTART_AFTER_MENU_CLOSE_DELAY_MS", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGardenMacroModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GardenMacroModule.kt\norg/cobalt/internal/garden/GardenMacroModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,635:1\n1915#2,2:636\n*S KotlinDebug\n*F\n+ 1 GardenMacroModule.kt\norg/cobalt/internal/garden/GardenMacroModule\n*L\n612#1:636,2\n*E\n"})
public final class GardenMacroModule
extends Module {
    @NotNull
    public static final GardenMacroModule INSTANCE = new GardenMacroModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static volatile GardenState state;
    private static volatile long sessionStartTime;
    @Nullable
    private static volatile String autosellingManager;
    private static volatile boolean wasConnected;
    private static volatile boolean autoRestartQueued;
    private static volatile long startupGraceUntilMs;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final TextSetting farmScriptSetting;
    @NotNull
    private static final TextSetting pestScriptSetting;
    @NotNull
    private static final TextSetting returnScriptSetting;
    @NotNull
    private static final TextSetting visitorScriptSetting;
    @NotNull
    private static final CheckboxSetting autoRestartStoppedScriptSetting;
    @NotNull
    private static final SliderSetting pestThresholdSetting;
    @NotNull
    private static final CheckboxSetting triggerPestOnChatSetting;
    @NotNull
    private static final SliderSetting pestChatTriggerDelaySetting;
    @NotNull
    private static final CheckboxSetting aotvEnabledSetting;
    @NotNull
    private static final SliderSetting roofPitchSetting;
    @NotNull
    private static final SliderSetting aotvRoofPitchHumanizationSetting;
    @NotNull
    private static final CheckboxSetting prepSwapSetting;
    @NotNull
    private static final TextSetting aotvPlotsSetting;
    @NotNull
    private static final CheckboxSetting breakBlocksBeforeAotvSetting;
    @NotNull
    private static final CheckboxSetting delayPestForCropFeverSetting;
    @NotNull
    private static final CheckboxSetting enablePlotTpRewarpSetting;
    @NotNull
    private static final CheckboxSetting holdWUntilWallSetting;
    @NotNull
    private static final TextSetting plotTpNumberSetting;
    @NotNull
    private static final CheckboxSetting autoVisitorSetting;
    @NotNull
    private static final SliderSetting visitorThresholdSetting;
    @NotNull
    private static final CheckboxSetting autoWardrobeSetting;
    @NotNull
    private static final CheckboxSetting autoWardrobePestSetting;
    @NotNull
    private static final CheckboxSetting autoWardrobeVisitorSetting;
    @NotNull
    private static final SliderSetting farmingSlotSetting;
    @NotNull
    private static final SliderSetting pestSlotSetting;
    @NotNull
    private static final SliderSetting visitorSlotSetting;
    @NotNull
    private static final CheckboxSetting autoEquipmentSetting;
    @NotNull
    private static final TextSetting farmingEquipmentSetting;
    @NotNull
    private static final TextSetting pestEquipmentSetting;
    @NotNull
    private static final TextSetting visitorEquipmentSetting;
    @NotNull
    private static final SliderSetting swapDelaySetting;
    @NotNull
    private static final CheckboxSetting autoRodPestCdSetting;
    @NotNull
    private static final CheckboxSetting autoRodPestSpawnSetting;
    @NotNull
    private static final CheckboxSetting autoRodReturnToFarmSetting;
    @NotNull
    private static final SliderSetting rodSwapDelaySetting;
    @NotNull
    private static final CheckboxSetting autoGeorgeSellSetting;
    @NotNull
    private static final TextSetting georgeRaritySetting;
    @NotNull
    private static final SliderSetting georgeSellThresholdSetting;
    @NotNull
    private static final CheckboxSetting autoBookCombineSetting;
    @NotNull
    private static final SliderSetting bookLevelSetting;
    @NotNull
    private static final SliderSetting bookThresholdSetting;
    @NotNull
    private static final SliderSetting bookCombineDelaySetting;
    @NotNull
    private static final CheckboxSetting alwaysActiveCombineSetting;
    @NotNull
    private static final CheckboxSetting autoDropJunkSetting;
    @NotNull
    private static final TextSetting junkItemsSetting;
    @NotNull
    private static final SliderSetting junkThresholdSetting;
    @NotNull
    private static final SliderSetting junkItemDropDelaySetting;
    @NotNull
    private static final CheckboxSetting autoBoosterCookieSetting;
    @NotNull
    private static final TextSetting boosterCookieItemsSetting;
    @NotNull
    private static final TextSetting cookieItemSetting;
    @NotNull
    private static final CheckboxSetting autoStashManagerSetting;
    @NotNull
    private static final SliderSetting bazaarRefreshSetting;
    @NotNull
    private static final SliderSetting workDurationSetting;
    @NotNull
    private static final SliderSetting workOffsetSetting;
    @NotNull
    private static final SliderSetting breakDurationSetting;
    @NotNull
    private static final SliderSetting breakOffsetSetting;
    @NotNull
    private static final CheckboxSetting persistSessionTimerSetting;
    @NotNull
    private static final CheckboxSetting autoResumeAfterDynamicRestSetting;
    @NotNull
    private static final CheckboxSetting hideFilteredChatSetting;
    @NotNull
    private static final CheckboxSetting guiOnlyInGardenSetting;
    @NotNull
    private static final CheckboxSetting autoRecoverUnexpectedDisconnectSetting;
    @NotNull
    private static final SliderSetting maxRecoverySetting;
    @NotNull
    private static final SliderSetting reconnectMinSetting;
    @NotNull
    private static final SliderSetting reconnectMaxSetting;
    @NotNull
    private static final SliderSetting rotationTimeSetting;
    @NotNull
    private static final SliderSetting guiClickDelaySetting;
    @NotNull
    private static final SliderSetting additionalRandomDelaySetting;
    @NotNull
    private static final SliderSetting gardenWarpDelaySetting;
    @NotNull
    private static final TextSetting unflyModeSetting;
    @NotNull
    private static final TextSetting petTrackerListSetting;
    private static final float hudWidth;
    @NotNull
    private static final HudElement gardenHudElement;
    private static final long AUTO_RESTART_WAIT_TIMEOUT_MS = 8000L;
    private static final long AUTO_RESTART_POLL_MS = 100L;
    private static final long AUTO_RESTART_AFTER_MENU_CLOSE_DELAY_MS = 140L;

    private GardenMacroModule() {
        super("Garden Macro");
    }

    public final boolean isActive() {
        return (Boolean)enabledSetting.getValue();
    }

    @NotNull
    public final GardenState getState() {
        return state;
    }

    public final long getSessionStartTime() {
        return sessionStartTime;
    }

    public final void setSessionStartTime(long l) {
        sessionStartTime = l;
    }

    @Nullable
    public final String getAutosellingManager() {
        return autosellingManager;
    }

    public final void setAutosellingManager(@Nullable String string) {
        autosellingManager = string;
    }

    public final void setState(@NotNull GardenState newState) {
        Intrinsics.checkNotNullParameter((Object)((Object)newState), (String)"newState");
        state = newState;
    }

    private final void syncConfig() {
        GardenConfig.INSTANCE.setFarmScript((String)farmScriptSetting.getValue());
        GardenConfig.INSTANCE.setPestScript((String)pestScriptSetting.getValue());
        GardenConfig.INSTANCE.setReturnScript((String)returnScriptSetting.getValue());
        GardenConfig.INSTANCE.setVisitorScript((String)visitorScriptSetting.getValue());
        GardenConfig.INSTANCE.setAutoRestartStoppedScript((Boolean)autoRestartStoppedScriptSetting.getValue());
        GardenConfig.INSTANCE.setPestThreshold((int)((Number)pestThresholdSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setTriggerPestOnChat((Boolean)triggerPestOnChatSetting.getValue());
        GardenConfig.INSTANCE.setPestChatTriggerDelayMs((long)((Number)pestChatTriggerDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAotvEnabled((Boolean)aotvEnabledSetting.getValue());
        GardenConfig.INSTANCE.setRoofPitch(((Number)roofPitchSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAotvRoofPitchHumanization((int)((Number)aotvRoofPitchHumanizationSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setBreakBlocksBeforeAotv((Boolean)breakBlocksBeforeAotvSetting.getValue());
        GardenConfig.INSTANCE.setDelayPestForCropFever((Boolean)delayPestForCropFeverSetting.getValue());
        GardenConfig.INSTANCE.setEnablePlotTpRewarp((Boolean)enablePlotTpRewarpSetting.getValue());
        GardenConfig.INSTANCE.setHoldWUntilWall((Boolean)holdWUntilWallSetting.getValue());
        GardenConfig.INSTANCE.setPlotTpNumber((String)plotTpNumberSetting.getValue());
        GardenConfig.INSTANCE.setVisitorThreshold((int)((Number)visitorThresholdSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoWardrobeEnabled((Boolean)autoWardrobeSetting.getValue());
        GardenConfig.INSTANCE.setAutoWardrobePest((Boolean)autoWardrobePestSetting.getValue());
        GardenConfig.INSTANCE.setAutoWardrobeVisitor((Boolean)autoWardrobeVisitorSetting.getValue());
        GardenConfig.INSTANCE.setFarmingWardrobeSlot((int)((Number)farmingSlotSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setPestWardrobeSlot((int)((Number)pestSlotSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setVisitorWardrobeSlot((int)((Number)visitorSlotSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoEquipment((Boolean)autoEquipmentSetting.getValue());
        GardenConfig.INSTANCE.setFarmingEquipment((String)farmingEquipmentSetting.getValue());
        GardenConfig.INSTANCE.setPestEquipment((String)pestEquipmentSetting.getValue());
        GardenConfig.INSTANCE.setVisitorEquipment((String)visitorEquipmentSetting.getValue());
        GardenConfig.INSTANCE.setSwapDelayMs((long)((Number)swapDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoRodPestCd((Boolean)autoRodPestCdSetting.getValue());
        GardenConfig.INSTANCE.setAutoRodPestSpawn((Boolean)autoRodPestSpawnSetting.getValue());
        GardenConfig.INSTANCE.setAutoRodReturnToFarm((Boolean)autoRodReturnToFarmSetting.getValue());
        GardenConfig.INSTANCE.setRodSwapDelayMs((long)((Number)rodSwapDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoGeorgeSell((Boolean)autoGeorgeSellSetting.getValue());
        GardenConfig.INSTANCE.setGeorgeRarity((String)georgeRaritySetting.getValue());
        GardenConfig.INSTANCE.setGeorgeSellThreshold((int)((Number)georgeSellThresholdSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoBookCombine((Boolean)autoBookCombineSetting.getValue());
        GardenConfig.INSTANCE.setBookCombineLevel((int)((Number)bookLevelSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setBookThreshold((int)((Number)bookThresholdSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setBookCombineDelayMs((long)((Number)bookCombineDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAlwaysActiveCombine((Boolean)alwaysActiveCombineSetting.getValue());
        GardenConfig.INSTANCE.setAutoDropJunk((Boolean)autoDropJunkSetting.getValue());
        GardenConfig.INSTANCE.setJunkItems((String)junkItemsSetting.getValue());
        GardenConfig.INSTANCE.setJunkThreshold((int)((Number)junkThresholdSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setJunkItemDropDelayMs((long)((Number)junkItemDropDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAutoBoosterCookie((Boolean)autoBoosterCookieSetting.getValue());
        GardenConfig.INSTANCE.setBoosterCookieItems((String)boosterCookieItemsSetting.getValue());
        GardenConfig.INSTANCE.setCookieItem((String)cookieItemSetting.getValue());
        GardenConfig.INSTANCE.setAutoStashManager((Boolean)autoStashManagerSetting.getValue());
        GardenConfig.INSTANCE.setBazaarRefreshSecs((long)((Number)bazaarRefreshSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setWorkDurationMins((long)((Number)workDurationSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setWorkOffsetMins((long)((Number)workOffsetSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setBreakDurationMins((long)((Number)breakDurationSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setBreakOffsetMins((long)((Number)breakOffsetSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setPersistSessionTimer((Boolean)persistSessionTimerSetting.getValue());
        GardenConfig.INSTANCE.setAutoResumeAfterDynamicRest((Boolean)autoResumeAfterDynamicRestSetting.getValue());
        GardenConfig.INSTANCE.setAutoRecoverUnexpectedDisconnect((Boolean)autoRecoverUnexpectedDisconnectSetting.getValue());
        GardenConfig.INSTANCE.setMaxRecoveryAttempts((int)((Number)maxRecoverySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setReconnectDelayMin((long)((Number)reconnectMinSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setReconnectDelayMax((long)((Number)reconnectMaxSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setRotationTimeMs((long)((Number)rotationTimeSetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setGuiClickDelayMs((long)((Number)guiClickDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setAdditionalRandomDelayMs((long)((Number)additionalRandomDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setGardenWarpDelayMs((long)((Number)gardenWarpDelaySetting.getValue()).doubleValue());
        GardenConfig.INSTANCE.setUnflyMode((String)unflyModeSetting.getValue());
        GardenConfig.INSTANCE.setPetTrackerList((String)petTrackerListSetting.getValue());
        GardenConfig.INSTANCE.setGuiOnlyInGarden((Boolean)guiOnlyInGardenSetting.getValue());
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        boolean connected;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        boolean enabled = (Boolean)enabledSetting.getValue();
        if (enabled && state == GardenState.OFF) {
            this.startMacro();
            return;
        }
        if (!enabled && state != GardenState.OFF) {
            this.stopMacro();
            return;
        }
        if (!enabled) {
            return;
        }
        this.syncConfig();
        boolean bl = connected = mc.method_1562() != null;
        if (!connected && wasConnected && state != GardenState.RECOVERING && state != GardenState.RESTING) {
            if (GardenConfig.INSTANCE.getAutoRecoverUnexpectedDisconnect()) {
                this.setState(GardenState.RECOVERING);
                RecoveryManager.INSTANCE.onDisconnect((Function0<Unit>)((Function0)GardenMacroModule::onTick$lambda$0));
            } else {
                this.stopMacro();
            }
        }
        wasConnected = connected;
        if (!connected) {
            return;
        }
        CropFeverManager.INSTANCE.update();
        PestBonusManager.INSTANCE.update();
        RestartManager.INSTANCE.update((Function0<Unit>)((Function0)GardenMacroModule::onTick$lambda$1));
        ProfitManager.INSTANCE.refreshBazaarIfNeeded();
        PetXpTracker.INSTANCE.update();
        switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1: {
                this.tickFarming();
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                if (PestCleaningSequencer.INSTANCE.isRunning()) break;
                this.setState(GardenState.FARMING);
                ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
            }
        }
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        String string;
        CharSequence charSequence;
        Regex regex;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string2 = event.getMessage();
        if (string2 == null || (string2 = (regex = new Regex("\u00a7[0-9a-fk-or]")).replace(charSequence = (CharSequence)string2, string = "")) == null) {
            return;
        }
        String msg = string2;
        if (((Boolean)hideFilteredChatSetting.getValue()).booleanValue()) {
            String string3 = msg.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            String lower = string3;
            if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"pet killed", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"macro started", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"script started", (boolean)false, (int)2, null)) {
                event.setCancelled(true);
                return;
            }
        }
        this.handleUnexpectedScriptStop(msg);
        CropFeverManager.INSTANCE.onChatMessage(msg);
        PestCleaningSequencer.INSTANCE.onChatMessage(msg);
        RestartManager.INSTANCE.onChatMessage(msg, (Function0<Unit>)((Function0)GardenMacroModule::onChat$lambda$0));
        ProfitManager.INSTANCE.onChatMessage(msg);
        VisitorManager.INSTANCE.onChatMessage(msg);
    }

    private final void tickFarming() {
        boolean pestSpawnedAfterPrep;
        if (System.currentTimeMillis() < startupGraceUntilMs) {
            return;
        }
        if (DynamicRestManager.INSTANCE.shouldRest()) {
            this.setState(GardenState.RESTING);
            DynamicRestManager.INSTANCE.startRest((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$0));
            return;
        }
        if (((Boolean)autoVisitorSetting.getValue()).booleanValue() && VisitorManager.INSTANCE.shouldHandle() && !CropFeverManager.INSTANCE.shouldDelay()) {
            this.setState(GardenState.VISITING);
            VisitorManager.INSTANCE.startVisitorSequence((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$1));
            return;
        }
        if (autosellingManager == null) {
            if (GardenConfig.INSTANCE.getAutoGeorgeSell() && GeorgeManager.INSTANCE.shouldSell()) {
                autosellingManager = "george";
                this.setState(GardenState.AUTOSELLING);
                GeorgeManager.INSTANCE.startSell((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$2));
                return;
            }
            if (GardenConfig.INSTANCE.getAutoBookCombine() && BookCombineManager.INSTANCE.shouldCombine()) {
                autosellingManager = "book";
                this.setState(GardenState.AUTOSELLING);
                BookCombineManager.INSTANCE.startCombine((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$3));
                return;
            }
            if (GardenConfig.INSTANCE.getAutoDropJunk() && JunkManager.INSTANCE.shouldDrop()) {
                autosellingManager = "junk";
                this.setState(GardenState.AUTOSELLING);
                JunkManager.INSTANCE.startDrop((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$4));
                return;
            }
        }
        boolean pestShouldClean = PestManager.INSTANCE.update(GardenConfig.INSTANCE.getPestThreshold());
        if (System.currentTimeMillis() >= PestManager.INSTANCE.getCleaningCooldownUntil()) {
            PestPrepSwapManager.INSTANCE.markActive();
        }
        if (((Boolean)prepSwapSetting.getValue()).booleanValue() && !PestPrepSwapManager.INSTANCE.getSwapDone() && System.currentTimeMillis() >= PestManager.INSTANCE.getCleaningCooldownUntil() && PestPrepSwapManager.INSTANCE.shouldPrepSwap(PestManager.INSTANCE.getLastCooldownSeconds()) && PestPrepSwapManager.INSTANCE.markStarted()) {
            GardenWorkerThread.INSTANCE.submit("prep-swap", (Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$5));
        }
        boolean cropFeverDelay = GardenConfig.INSTANCE.getDelayPestForCropFever() && CropFeverManager.INSTANCE.shouldDelay();
        boolean bl = pestSpawnedAfterPrep = (Boolean)prepSwapSetting.getValue() != false && PestPrepSwapManager.INSTANCE.getSwapDone() && PestManager.INSTANCE.getLastAliveCount() > 0;
        if (!cropFeverDelay && !PestPrepSwapManager.INSTANCE.isRunning() && (pestShouldClean || pestSpawnedAfterPrep)) {
            this.setState(GardenState.CLEANING);
            PestCleaningSequencer.INSTANCE.startSequence((Function0<Unit>)((Function0)GardenMacroModule::tickFarming$lambda$6));
        }
    }

    private final void handleUnexpectedScriptStop(String message) {
        if (!GardenConfig.INSTANCE.getAutoRestartStoppedScript() || !((Boolean)enabledSetting.getValue()).booleanValue() || state == GardenState.OFF) {
            return;
        }
        String string = message.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String lower = string;
        if (!(StringsKt.contains$default((CharSequence)lower, (CharSequence)"taunahi", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)lower, (CharSequence)"script stopped", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)lower, (CharSequence)"menu opened (your equipment and stats)", (boolean)false, (int)2, null))) {
            return;
        }
        if (ScriptBridge.INSTANCE.wasIntentionalStopRecently()) {
            return;
        }
        this.queueUnexpectedScriptRestart();
    }

    private final void queueUnexpectedScriptRestart() {
        if (autoRestartQueued) {
            return;
        }
        if (this.resolveUnexpectedStopRestartAction() == null) {
            return;
        }
        autoRestartQueued = true;
        GardenWorkerThread.INSTANCE.submit("auto-script-restart", (Function0<Unit>)((Function0)GardenMacroModule::queueUnexpectedScriptRestart$lambda$0));
    }

    private final Function0<Unit> resolveUnexpectedStopRestartAction() {
        if (!((Boolean)enabledSetting.getValue()).booleanValue() || state == GardenState.OFF) {
            return null;
        }
        if (RestartManager.INSTANCE.getRestartDetected() || RecoveryManager.INSTANCE.isRecovering() || DynamicRestManager.INSTANCE.isResting()) {
            return null;
        }
        if (autosellingManager != null || state == GardenState.AUTOSELLING) {
            return null;
        }
        if (PestPrepSwapManager.INSTANCE.isRunning()) {
            return null;
        }
        if (ScriptBridge.INSTANCE.hasLastStartedScript()) {
            return GardenMacroModule::resolveUnexpectedStopRestartAction$lambda$0;
        }
        return PestReturnManager.INSTANCE.isReturning() && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getReturnScript()) ? GardenMacroModule::resolveUnexpectedStopRestartAction$lambda$1 : ((VisitorManager.INSTANCE.isHandlingVisitor() || state == GardenState.VISITING) && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getVisitorScript()) ? GardenMacroModule::resolveUnexpectedStopRestartAction$lambda$2 : ((PestCleaningSequencer.INSTANCE.isRunning() || state == GardenState.CLEANING) && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getPestScript()) ? GardenMacroModule::resolveUnexpectedStopRestartAction$lambda$3 : (state == GardenState.FARMING && !StringsKt.isBlank((CharSequence)GardenConfig.INSTANCE.getFarmScript()) ? GardenMacroModule::resolveUnexpectedStopRestartAction$lambda$4 : null)));
    }

    private final void startMacro() {
        this.syncConfig();
        if (!GardenConfig.INSTANCE.getPersistSessionTimer()) {
            sessionStartTime = System.currentTimeMillis();
        }
        state = GardenState.FARMING;
        startupGraceUntilMs = System.currentTimeMillis() + 5000L;
        Object[] objectArray = new Function0[]{new Function0<Unit>((Object)PestManager.INSTANCE){

            public final void invoke() {
                ((PestManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PestCleaningSequencer.INSTANCE){

            public final void invoke() {
                ((PestCleaningSequencer)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PestAotvManager.INSTANCE){

            public final void invoke() {
                ((PestAotvManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PestPrepSwapManager.INSTANCE){

            public final void invoke() {
                ((PestPrepSwapManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PestReturnManager.INSTANCE){

            public final void invoke() {
                ((PestReturnManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PestBonusManager.INSTANCE){

            public final void invoke() {
                ((PestBonusManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)CropFeverManager.INSTANCE){

            public final void invoke() {
                ((CropFeverManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)VisitorManager.INSTANCE){

            public final void invoke() {
                ((VisitorManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)WardrobeManager.INSTANCE){

            public final void invoke() {
                ((WardrobeManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)EquipmentManager.INSTANCE){

            public final void invoke() {
                ((EquipmentManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)GearManager.INSTANCE){

            public final void invoke() {
                ((GearManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)RodManager.INSTANCE){

            public final void invoke() {
                ((RodManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)GeorgeManager.INSTANCE){

            public final void invoke() {
                ((GeorgeManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)BookCombineManager.INSTANCE){

            public final void invoke() {
                ((BookCombineManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)JunkManager.INSTANCE){

            public final void invoke() {
                ((JunkManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)BoosterCookieManager.INSTANCE){

            public final void invoke() {
                ((BoosterCookieManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)ProfitManager.INSTANCE){

            public final void invoke() {
                ((ProfitManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)PetXpTracker.INSTANCE){

            public final void invoke() {
                ((PetXpTracker)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)DynamicRestManager.INSTANCE){

            public final void invoke() {
                ((DynamicRestManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)RecoveryManager.INSTANCE){

            public final void invoke() {
                ((RecoveryManager)this.receiver).reset();
            }
        }, new Function0<Unit>((Object)RestartManager.INSTANCE){

            public final void invoke() {
                ((RestartManager)this.receiver).reset();
            }
        }};
        Iterable $this$forEach$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Function0 it = (Function0)element$iv;
            boolean bl = false;
            it.invoke();
        }
        autosellingManager = null;
        wasConnected = true;
        autoRestartQueued = false;
        GardenWorkerThread.INSTANCE.submit("macro-start", (Function0<Unit>)((Function0)GardenMacroModule::startMacro$lambda$1));
    }

    private final void stopMacro() {
        autoRestartQueued = false;
        GardenWorkerThread.INSTANCE.shutdown();
        ScriptBridge.INSTANCE.stopScript();
        state = GardenState.OFF;
    }

    private static final float gardenHudElement$lambda$0$computeHeight(CheckboxSetting showRestSetting, CheckboxSetting showProfitSetting) {
        float h = 48.0f;
        if (((Boolean)showRestSetting.getValue()).booleanValue()) {
            h += 36.0f;
        }
        if (((Boolean)showProfitSetting.getValue()).booleanValue()) {
            h += 62.0f;
        }
        return h;
    }

    private static final float gardenHudElement$lambda$0$0() {
        return hudWidth;
    }

    private static final float gardenHudElement$lambda$0$1(CheckboxSetting $showRestSetting, CheckboxSetting $showProfitSetting) {
        return GardenMacroModule.gardenHudElement$lambda$0$computeHeight($showRestSetting, $showProfitSetting);
    }

    private static final Unit gardenHudElement$lambda$0$2(CheckboxSetting $showProfitSetting, CheckboxSetting $showRestSetting, float x, float y, float f) {
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            if (state == GardenState.OFF) {
                return Unit.INSTANCE;
            }
        }
        GardenHud.INSTANCE.render(x, y, hudWidth, GardenMacroModule.gardenHudElement$lambda$0$computeHeight($showRestSetting, $showProfitSetting), state, (Boolean)$showProfitSetting.getValue(), (Boolean)$showRestSetting.getValue(), sessionStartTime);
        return Unit.INSTANCE;
    }

    private static final Unit gardenHudElement$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        CheckboxSetting showProfitSetting = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Show Profit", "Show profit section.", true));
        CheckboxSetting showRestSetting = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Show Rest", "Show rest countdown.", true));
        $this$hudElement.width((Function0<Float>)((Function0)GardenMacroModule::gardenHudElement$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)() -> GardenMacroModule.gardenHudElement$lambda$0$1(showRestSetting, showProfitSetting)));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> GardenMacroModule.gardenHudElement$lambda$0$2(showProfitSetting, showRestSetting, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }

    private static final Unit onTick$lambda$0() {
        INSTANCE.setState(GardenState.FARMING);
        ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
        return Unit.INSTANCE;
    }

    private static final Unit onTick$lambda$1() {
        INSTANCE.stopMacro();
        return Unit.INSTANCE;
    }

    private static final Unit onChat$lambda$0() {
        INSTANCE.stopMacro();
        return Unit.INSTANCE;
    }

    private static final void tickFarming$lambda$5$0() {
        ScriptBridge.INSTANCE.stopScript();
    }

    private static final void tickFarming$lambda$5$1() {
        ScriptBridge.INSTANCE.setSpawn();
    }

    private static final void tickFarming$lambda$5$2() {
        ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
    }

    private static final Unit tickFarming$lambda$0() {
        if (GardenConfig.INSTANCE.getAutoResumeAfterDynamicRest()) {
            INSTANCE.setState(GardenState.FARMING);
            ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
        } else {
            INSTANCE.stopMacro();
        }
        return Unit.INSTANCE;
    }

    private static final Unit tickFarming$lambda$1() {
        INSTANCE.setState(GardenState.FARMING);
        return Unit.INSTANCE;
    }

    private static final Unit tickFarming$lambda$2() {
        autosellingManager = null;
        INSTANCE.setState(GardenState.FARMING);
        return Unit.INSTANCE;
    }

    private static final Unit tickFarming$lambda$3() {
        autosellingManager = null;
        INSTANCE.setState(GardenState.FARMING);
        return Unit.INSTANCE;
    }

    private static final Unit tickFarming$lambda$4() {
        autosellingManager = null;
        INSTANCE.setState(GardenState.FARMING);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit tickFarming$lambda$5() {
        boolean prepCompleted = false;
        try {
            mc.execute(GardenMacroModule::tickFarming$lambda$5$0);
            Thread.sleep(RangesKt.random((LongRange)new LongRange(20L, 40L), (Random)((Random)Random.Default)) + 300L);
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() || GardenConfig.INSTANCE.getAutoEquipment()) {
                GearManager.INSTANCE.swapForPest();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 350L);
            }
            if (GardenConfig.INSTANCE.getAutoRodPestCd()) {
                RodManager.INSTANCE.useRod(true);
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 200L);
            }
            FlightManager.INSTANCE.startFlying();
            Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 150L);
            mc.execute(GardenMacroModule::tickFarming$lambda$5$1);
            Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 250L);
            FlightManager.INSTANCE.stopFlying();
            Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 150L);
            if (GardenConfig.INSTANCE.getAutoWardrobeEnabled() || GardenConfig.INSTANCE.getAutoEquipment()) {
                GearManager.INSTANCE.swapForFarming();
                Thread.sleep(RangesKt.random((LongRange)new LongRange(10L, 30L), (Random)((Random)Random.Default)) + 350L);
            }
            prepCompleted = true;
        }
        catch (InterruptedException interruptedException) {
            try {
                Thread.currentThread().interrupt();
            }
            catch (Throwable throwable) {
                PestPrepSwapManager.INSTANCE.markFailed();
                throw throwable;
            }
            PestPrepSwapManager.INSTANCE.markFailed();
        }
        PestPrepSwapManager.INSTANCE.markDone();
        mc.execute(GardenMacroModule::tickFarming$lambda$5$2);
        return Unit.INSTANCE;
    }

    private static final Unit tickFarming$lambda$6() {
        INSTANCE.setState(GardenState.FARMING);
        ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
        return Unit.INSTANCE;
    }

    private static final void queueUnexpectedScriptRestart$lambda$0$0() {
        if (GardenMacroModule.mc.field_1755 != null) {
            class_746 class_7462 = GardenMacroModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
            if (GardenMacroModule.mc.field_1755 != null) {
                mc.method_1507(null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit queueUnexpectedScriptRestart$lambda$0() {
        try {
            long deadline = System.currentTimeMillis() + 8000L;
            long menuClosedAt = 0L;
            while (System.currentTimeMillis() < deadline) {
                if (Thread.currentThread().isInterrupted()) {
                    Unit unit = Unit.INSTANCE;
                    return unit;
                }
                if (mc.method_1562() == null || GardenMacroModule.mc.field_1724 == null) {
                    menuClosedAt = 0L;
                    Thread.sleep(100L);
                    continue;
                }
                if (GardenMacroModule.mc.field_1755 != null) {
                    menuClosedAt = 0L;
                    mc.execute(GardenMacroModule::queueUnexpectedScriptRestart$lambda$0$0);
                    Thread.sleep(100L);
                    continue;
                }
                if (menuClosedAt == 0L) {
                    menuClosedAt = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - menuClosedAt >= 140L) break;
                Thread.sleep(100L);
            }
            if (mc.method_1562() == null || GardenMacroModule.mc.field_1724 == null || GardenMacroModule.mc.field_1755 != null) {
                Unit unit = Unit.INSTANCE;
                return unit;
            }
            Function0<Unit> function0 = INSTANCE.resolveUnexpectedStopRestartAction();
            if (function0 != null) {
                function0.invoke();
            }
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            autoRestartQueued = false;
        }
        return Unit.INSTANCE;
    }

    private static final Unit resolveUnexpectedStopRestartAction$lambda$0() {
        ScriptBridge.INSTANCE.restartLastScript();
        return Unit.INSTANCE;
    }

    private static final Unit resolveUnexpectedStopRestartAction$lambda$1() {
        ScriptBridge.INSTANCE.startReturnScript(GardenConfig.INSTANCE.getReturnScript());
        return Unit.INSTANCE;
    }

    private static final Unit resolveUnexpectedStopRestartAction$lambda$2() {
        ScriptBridge.INSTANCE.startVisitorScript(GardenConfig.INSTANCE.getVisitorScript());
        return Unit.INSTANCE;
    }

    private static final Unit resolveUnexpectedStopRestartAction$lambda$3() {
        ScriptBridge.INSTANCE.startPestScript(GardenConfig.INSTANCE.getPestScript());
        return Unit.INSTANCE;
    }

    private static final Unit resolveUnexpectedStopRestartAction$lambda$4() {
        ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
        return Unit.INSTANCE;
    }

    private static final void startMacro$lambda$1$0() {
        ScriptBridge.INSTANCE.stopScript();
    }

    private static final void startMacro$lambda$1$1() {
        ScriptBridge.INSTANCE.startFarming(GardenConfig.INSTANCE.getFarmScript());
    }

    private static final Unit startMacro$lambda$1() {
        mc.execute(GardenMacroModule::startMacro$lambda$1$0);
        Thread.sleep(RangesKt.random((LongRange)new LongRange(20L, 40L), (Random)((Random)Random.Default)) + 300L);
        mc.execute(GardenMacroModule::startMacro$lambda$1$1);
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        state = GardenState.OFF;
        sessionStartTime = System.currentTimeMillis();
        wasConnected = true;
        enabledSetting = new CheckboxSetting("Enabled", "Run the garden macro.", false);
        farmScriptSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Farm Script", "Taunahi script name for farming (e.g. netherwart:1).", "netherwart:1"), "Scripts");
        pestScriptSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Pest Script", "Taunahi script name for pest cleaning.", "misc:pestCleaner"), "Scripts");
        returnScriptSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Return Script", "Taunahi script name to run after pest clean.", "misc:visitor"), "Scripts");
        visitorScriptSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Visitor Script", "Taunahi script name for visitors.", "misc:visitor"), "Scripts");
        autoRestartStoppedScriptSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Restart", "Restart the active Taunahi script if it stops because the equipment menu opened.", false), "Scripts");
        pestThresholdSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Pest Threshold", "Alive pest count to trigger cleaning.", 4.0, 1.0, 8.0, 1.0), "Pest");
        triggerPestOnChatSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Trigger Pest On Chat", "Trigger pest clean from chat message.", true), "Pest");
        pestChatTriggerDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Pest Chat Delay", "Ms to wait after chat trigger before cleaning.", 0.0, 0.0, 5000.0, 0.0, 32, null), "Pest");
        aotvEnabledSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("AOTV to Roof", "Teleport to roof before pest clean.", false), "Pest");
        roofPitchSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Roof Pitch", "Camera pitch for roof teleport.", -80.0, -90.0, 90.0, 0.0, 32, null), "Pest");
        aotvRoofPitchHumanizationSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Roof Pitch Humanization", "Random pitch offset for AOTV.", 3.0, 0.0, 10.0, 1.0), "Pest");
        prepSwapSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Prep Swap", "Swap gear before threshold hit.", false), "Pest");
        aotvPlotsSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("AOTV Plots", "Comma-separated plot names.", ""), "Pest");
        breakBlocksBeforeAotvSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Break Blocks Before AOTV", "Break blocks before AOTV teleport.", false), "Pest");
        delayPestForCropFeverSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Delay Pest For Crop Fever", "Wait for crop fever to expire before pest.", false), "Pest");
        enablePlotTpRewarpSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Enable Plot TP Rewarp", "Re-warp to garden after plot teleport.", false), "Pest");
        holdWUntilWallSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Hold W Until Wall", "Hold forward key until hitting a wall.", false), "Pest");
        plotTpNumberSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Plot TP Number", "Plot number to teleport to.", "0"), "Pest");
        autoVisitorSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Visitor", "Handle visitor offers automatically.", false), "Visitor");
        visitorThresholdSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Visitor Threshold", "Time in seconds before handling visitor.", 5.0, 1.0, 30.0, 1.0), "Visitor");
        autoWardrobeSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Wardrobe", "Master toggle for automatic wardrobe swaps.", false), "Wardrobe");
        autoWardrobePestSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Wardrobe (Pest)", "Swap wardrobe slot when going to pest.", true), "Wardrobe");
        autoWardrobeVisitorSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Wardrobe (Visitor)", "Swap wardrobe slot for visitors.", false), "Wardrobe");
        farmingSlotSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Farming Slot", "Wardrobe slot for farming.", 1.0, 1.0, 18.0, 1.0), "Wardrobe");
        pestSlotSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Pest Slot", "Wardrobe slot for pest cleaning.", 2.0, 1.0, 18.0, 1.0), "Wardrobe");
        visitorSlotSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Visitor Slot", "Wardrobe slot for visitors.", 3.0, 1.0, 18.0, 1.0), "Wardrobe");
        autoEquipmentSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Equipment", "Automatically swap Skyblock equipment items.", true), "Equipment");
        farmingEquipmentSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Farming Equipment", "Comma-separated equipment keywords for farming.", "lotus, blossom"), "Equipment");
        pestEquipmentSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Pest Equipment", "Comma-separated equipment keywords for pest.", "pesthunter, pest vest"), "Equipment");
        visitorEquipmentSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Visitor Equipment", "Equipment preset name for visitors.", ""), "Equipment");
        swapDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Swap Delay", "Ms between equipment swaps.", 300.0, 0.0, 2000.0, 0.0, 32, null), "Equipment");
        autoRodPestCdSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Rod (Pest CD)", "Use rod when pest cooldown is active.", false), "Rod");
        autoRodPestSpawnSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Rod (Pest Spawn)", "Use rod when pest spawns.", false), "Rod");
        autoRodReturnToFarmSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Rod (Return To Farm)", "Use rod when returning to farm.", false), "Rod");
        rodSwapDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Rod Swap Delay", "Ms to wait between rod swaps.", 100.0, 0.0, 2000.0, 0.0, 32, null), "Rod");
        autoGeorgeSellSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto George Sell", "Automatically sell pets to George.", false), "Economy");
        georgeRaritySetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("George Rarity", "Rarities to sell (LEGENDARY,MYTHIC).", "LEGENDARY"), "Economy");
        georgeSellThresholdSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("George Sell Threshold", "Rarity level (1-6) to sell at.", 3.0, 1.0, 6.0, 1.0), "Economy");
        autoBookCombineSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Book Combine", "Automatically combine enchanted books.", false), "Economy");
        bookLevelSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Book Level", "Enchant level to combine books at.", 5.0, 1.0, 10.0, 1.0), "Economy");
        bookThresholdSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Book Threshold", "Number of books before triggering combine.", 7.0, 1.0, 20.0, 1.0), "Economy");
        bookCombineDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Book Combine Delay", "Ms to wait between book combine actions.", 300.0, 0.0, 2000.0, 0.0, 32, null), "Economy");
        alwaysActiveCombineSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Always Active Combine", "Combine books even when not farming.", false), "Economy");
        autoDropJunkSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Drop Junk", "Automatically drop junk items.", false), "Economy");
        junkItemsSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Junk Items", "Comma-separated item names to drop.", "Fruit Bowl,Farming Exp Boost,Sunder VI"), "Economy");
        junkThresholdSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Junk Threshold", "Stack size before dropping junk.", 3.0, 1.0, 64.0, 1.0), "Economy");
        junkItemDropDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Junk Drop Delay", "Ms to wait between dropping items.", 300.0, 0.0, 2000.0, 0.0, 32, null), "Economy");
        autoBoosterCookieSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Booster Cookie", "Automatically use booster cookies.", true), "Economy");
        boosterCookieItemsSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Cookie Items", "Comma-separated items to use cookies on.", "Atmospheric Filter,Squeaky Toy,Beady Eyes,Clipped Wings,Overclocker,Mantid Claw,Flowering Bouquet,Bookworm,Chirping Stereo,Firefly,Capsule,Vinyl"), "Economy");
        cookieItemSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Cookie Item", "Single item to apply booster cookie to.", ""), "Economy");
        autoStashManagerSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Stash Manager", "Automatically manage stash storage.", false), "Economy");
        bazaarRefreshSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Bazaar Refresh", "Seconds between Bazaar price updates.", 120.0, 30.0, 600.0, 0.0, 32, null), "Economy");
        workDurationSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Work Duration", "Minutes to farm before resting.", 60.0, 1.0, 240.0, 0.0, 32, null), "Rest");
        workOffsetSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Work Offset", "Random offset for work duration.", 5.0, 0.0, 30.0, 0.0, 32, null), "Rest");
        breakDurationSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Break Duration", "Minutes to rest before resuming.", 10.0, 1.0, 60.0, 0.0, 32, null), "Rest");
        breakOffsetSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Break Offset", "Random offset for break duration.", 2.0, 0.0, 15.0, 0.0, 32, null), "Rest");
        persistSessionTimerSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Persist Session Timer", "Keep session timer across restarts.", true), "Rest");
        autoResumeAfterDynamicRestSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Resume After Rest", "Automatically resume after rest ends.", true), "Rest");
        hideFilteredChatSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Hide Chat Spam", "Filter bot-related chat messages.", false), "Advanced");
        guiOnlyInGardenSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("GUI Only In Garden", "Only show GUI when in the garden.", true), "Advanced");
        autoRecoverUnexpectedDisconnectSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Recover Disconnect", "Reconnect after unexpected disconnects.", true), "Advanced");
        maxRecoverySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Max Recovery", "Max auto-reconnect attempts.", 15.0, 1.0, 30.0, 1.0), "Advanced");
        reconnectMinSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Reconnect Min", "Min seconds before reconnecting.", 30.0, 5.0, 120.0, 0.0, 32, null), "Advanced");
        reconnectMaxSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Reconnect Max", "Max seconds before reconnecting.", 60.0, 5.0, 120.0, 0.0, 32, null), "Advanced");
        rotationTimeSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Rotation Time", "Ms for camera rotation movements.", 500.0, 50.0, 2000.0, 0.0, 32, null), "Advanced");
        guiClickDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("GUI Click Delay", "Ms to wait between GUI clicks.", 500.0, 50.0, 2000.0, 0.0, 32, null), "Advanced");
        additionalRandomDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Additional Random Delay", "Extra random ms added to GUI actions.", 0.0, 0.0, 1000.0, 0.0, 32, null), "Advanced");
        gardenWarpDelaySetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Garden Warp Delay", "Ms to wait after warping to garden.", 3000.0, 0.0, 10000.0, 0.0, 32, null), "Advanced");
        unflyModeSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Unfly Mode", "DOUBLE_TAP_SPACE or SNEAK.", "DOUBLE_TAP_SPACE"), "Advanced");
        petTrackerListSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Pet Tracker List", "Pets to track (ID:Name:Level:Rarity).", "PET_ROSE_DRAGON:Rose Dragon:200:LEGENDARY"), "Advanced");
        hudWidth = 200.0f;
        gardenHudElement = HudModuleDSLKt.hudElement(INSTANCE, "garden-macro-hud", "Garden Macro HUD", "Macro status and profit tracking", (Function1<? super HudElementBuilder, Unit>)((Function1)GardenMacroModule::gardenHudElement$lambda$0));
        Setting[] settingArray = new Setting[]{enabledSetting, farmScriptSetting, pestScriptSetting, returnScriptSetting, visitorScriptSetting, autoRestartStoppedScriptSetting, pestThresholdSetting, triggerPestOnChatSetting, pestChatTriggerDelaySetting, aotvEnabledSetting, roofPitchSetting, aotvRoofPitchHumanizationSetting, prepSwapSetting, aotvPlotsSetting, breakBlocksBeforeAotvSetting, delayPestForCropFeverSetting, enablePlotTpRewarpSetting, holdWUntilWallSetting, plotTpNumberSetting, autoVisitorSetting, visitorThresholdSetting, autoWardrobeSetting, autoWardrobePestSetting, autoWardrobeVisitorSetting, farmingSlotSetting, pestSlotSetting, visitorSlotSetting, autoEquipmentSetting, farmingEquipmentSetting, pestEquipmentSetting, visitorEquipmentSetting, swapDelaySetting, autoRodPestCdSetting, autoRodPestSpawnSetting, autoRodReturnToFarmSetting, rodSwapDelaySetting, autoGeorgeSellSetting, georgeRaritySetting, georgeSellThresholdSetting, autoBookCombineSetting, bookLevelSetting, bookThresholdSetting, bookCombineDelaySetting, alwaysActiveCombineSetting, autoDropJunkSetting, junkItemsSetting, junkThresholdSetting, junkItemDropDelaySetting, autoBoosterCookieSetting, boosterCookieItemsSetting, cookieItemSetting, autoStashManagerSetting, bazaarRefreshSetting, workDurationSetting, workOffsetSetting, breakDurationSetting, breakOffsetSetting, persistSessionTimerSetting, autoResumeAfterDynamicRestSetting, hideFilteredChatSetting, guiOnlyInGardenSetting, autoRecoverUnexpectedDisconnectSetting, maxRecoverySetting, reconnectMinSetting, reconnectMaxSetting, rotationTimeSetting, guiClickDelaySetting, additionalRandomDelaySetting, gardenWarpDelaySetting, unflyModeSetting, petTrackerListSetting};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[GardenState.values().length];
            try {
                nArray[GardenState.FARMING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.RESTING.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.CLEANING.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

