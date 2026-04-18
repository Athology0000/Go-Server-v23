/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_11908
 *  net.minecraft.class_124
 *  net.minecraft.class_1657
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_2487
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_465
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.qol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_11908;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.GuiRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.KeyBindSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.api.util.helper.KeyBind;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.visual.HotbarOverlayModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u009e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002\u008c\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\u000b\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0017\u0010\u000e\u001a\u00020\b2\b\u0010\r\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\r\u0010\u0010\u001a\u00020\b\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001d\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001f\u0010\u0017\u001a\u00020\b2\b\u0010\u0007\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0017\u0010\u0016J?\u0010 \u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u001a2\b\u0010\u0007\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u001e\u00a2\u0006\u0004\b \u0010!J-\u0010&\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\"2\u0006\u0010\u0007\u001a\u00020\u00122\u0006\u0010$\u001a\u00020\u00062\u0006\u0010%\u001a\u00020\u0006\u00a2\u0006\u0004\b&\u0010'J-\u0010(\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\"2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010$\u001a\u00020\u00062\u0006\u0010%\u001a\u00020\u0006\u00a2\u0006\u0004\b(\u0010)J\u0017\u0010,\u001a\u00020\u00042\u0006\u0010+\u001a\u00020*H\u0007\u00a2\u0006\u0004\b,\u0010-J\u0017\u0010/\u001a\u00020\u00042\u0006\u0010+\u001a\u00020.H\u0007\u00a2\u0006\u0004\b/\u00100JG\u00105\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\"2\u0006\u0010$\u001a\u00020\u00062\u0006\u0010%\u001a\u00020\u00062\u0006\u00101\u001a\u00020\u00062\u0006\u00102\u001a\u00020\b2\u0006\u00103\u001a\u00020\b2\u0006\u00104\u001a\u00020\bH\u0002\u00a2\u0006\u0004\b5\u00106J\u0017\u00107\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b7\u00108J\u0017\u00109\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b9\u00108J\u001f\u0010<\u001a\u00020\u00042\u0006\u0010:\u001a\u00020\u00062\u0006\u0010;\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b<\u0010=J\u0017\u0010>\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b>\u0010?J\u000f\u0010@\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b@\u0010\u0003J\u0017\u0010A\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\bA\u0010BJ\u0017\u0010C\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\bC\u0010BJ\u0017\u0010D\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\bD\u0010BJ\u0017\u0010E\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bE\u0010\nJ\u0019\u0010G\u001a\u0004\u0018\u00010F2\u0006\u0010\u0007\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\bG\u0010HJ\u0019\u0010G\u001a\u0004\u0018\u00010F2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bG\u0010IJ\u001f\u0010K\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u0010J\u001a\u00020FH\u0002\u00a2\u0006\u0004\bK\u0010LJ\u0017\u0010M\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bM\u0010\nJ\u0017\u0010N\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\bN\u0010OJ\u001f\u0010R\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010Q\u001a\u00020PH\u0002\u00a2\u0006\u0004\bR\u0010SJ\u0017\u0010T\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bT\u0010\nJ\u0017\u0010U\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bU\u0010\nJ?\u0010X\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\"2\u0006\u0010$\u001a\u00020\u00062\u0006\u0010%\u001a\u00020\u00062\u0006\u00101\u001a\u00020\u00062\u0006\u0010V\u001a\u00020\u00062\u0006\u0010W\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bX\u0010YJ\u0017\u0010Z\u001a\u00020\u00182\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\bZ\u0010[J\u0017\u0010]\u001a\u00020\u00182\u0006\u0010\\\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b]\u0010^J\u0013\u0010_\u001a\u00020\u0018*\u00020\fH\u0002\u00a2\u0006\u0004\b_\u0010[J\u0019\u0010a\u001a\u0004\u0018\u00010F2\u0006\u0010`\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\ba\u0010bJ\u0017\u0010c\u001a\u00020\u00182\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bc\u0010dR\u0014\u0010e\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\be\u0010fR\u0014\u0010g\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bg\u0010fR\u0014\u0010h\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bh\u0010fR\u0014\u0010i\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bi\u0010fR\u0014\u0010j\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bj\u0010fR\u0014\u0010k\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bk\u0010fR\u0014\u0010l\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bl\u0010fR\u0014\u0010m\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bm\u0010fR\u0014\u0010n\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bn\u0010fR\u0014\u0010p\u001a\u00020o8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010qR\u0014\u0010s\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010tR\u0014\u0010u\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010tR\u0014\u0010v\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bv\u0010tR\u0014\u0010w\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010tR\u0014\u0010x\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u0010tR\u0014\u0010y\u001a\u00020r8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u0010tR\u0014\u0010z\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010{R\u0014\u0010|\u001a\u00020P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010{R\u0014\u0010~\u001a\u00020}8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010\u007fR\u0016\u0010\u0080\u0001\u001a\u00020}8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010\u007fR\u0016\u0010\u0081\u0001\u001a\u00020}8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010\u007fR\u001e\u0010\u0083\u0001\u001a\t\u0012\u0004\u0012\u00020\u00060\u0082\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0083\u0001\u0010\u0084\u0001R)\u0010\u0087\u0001\u001a\u0014\u0012\u0004\u0012\u00020F0\u0085\u0001j\t\u0012\u0004\u0012\u00020F`\u0086\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0087\u0001\u0010\u0088\u0001R)\u0010\u0089\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00180\u0085\u0001j\t\u0012\u0004\u0012\u00020\u0018`\u0086\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u0088\u0001R\u001b\u0010\u008a\u0001\u001a\u0004\u0018\u00010\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u008b\u0001\u00a8\u0006\u008d\u0001"}, d2={"Lorg/cobalt/internal/qol/ItemLockingModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "loadPersistedState", "", "slot", "", "isLockedHotbarSlot", "(I)Z", "isBlockedHotbarSlot", "Lnet/minecraft/class_1799;", "stack", "isProtectedItem", "(Lnet/minecraft/class_1799;)Z", "shouldCancelSelectedItemDrop", "()Z", "Lnet/minecraft/class_1735;", "Lnet/minecraft/class_11908;", "input", "handleContainerKeyPressed", "(Lnet/minecraft/class_1735;Lnet/minecraft/class_11908;)Z", "handleContainerKeyReleased", "", "title", "Lnet/minecraft/class_1703;", "menu", "slotId", "button", "Lnet/minecraft/class_1713;", "clickType", "shouldCancelContainerClick", "(Ljava/lang/String;Lnet/minecraft/class_1703;Lnet/minecraft/class_1735;IILnet/minecraft/class_1713;)Z", "Lnet/minecraft/class_332;", "graphics", "x", "y", "renderContainerSlotOverlay", "(Lnet/minecraft/class_332;Lnet/minecraft/class_1735;II)V", "renderHotbarSlotOverlay", "(Lnet/minecraft/class_332;III)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "onGuiRender", "(Lorg/cobalt/api/event/impl/render/GuiRenderEvent;)V", "size", "locked", "bound", "protected", "renderProtectionOutline", "(Lnet/minecraft/class_332;IIIZZZ)V", "toggleLockedSlot", "(I)V", "toggleSlotAction", "hotbarSlot", "inventorySlot", "bindSlots", "(II)V", "toggleProtectedItem", "(Lnet/minecraft/class_1799;)V", "persistState", "isPlayerHotbarSlot", "(Lnet/minecraft/class_1735;)Z", "isBindablePlayerSlot", "isBoundSlot", "isBoundHotbarSlot", "Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "getBoundSlotPair", "(Lnet/minecraft/class_1735;)Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "(I)Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "pair", "performBoundSlotSwap", "(Lnet/minecraft/class_1703;Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;)V", "clearBoundSlots", "isNpcSellScreen", "(Lnet/minecraft/class_1703;)Z", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "setting", "matches", "(Lnet/minecraft/class_11908;Lorg/cobalt/api/module/setting/impl/KeyBindSetting;)Z", "isHotbarSlot", "isInventoryStorageSlot", "inset", "color", "renderInsetOutline", "(Lnet/minecraft/class_332;IIIII)V", "displayName", "(Lnet/minecraft/class_1799;)Ljava/lang/String;", "text", "sanitizeTitle", "(Ljava/lang/String;)Ljava/lang/String;", "getSkyblockUuid", "entry", "parseBoundSlotPair", "(Ljava/lang/String;)Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "describePlayerSlot", "(I)Ljava/lang/String;", "OUTSIDE_SLOT_ID", "I", "HOTBAR_SLOT_COUNT", "PLAYER_INVENTORY_SLOT_COUNT", "INVENTORY_START_SLOT", "HOTBAR_SLOT_SIZE", "INVENTORY_SLOT_SIZE", "LOCKED_COLOR", "BOUND_COLOR", "PROTECTED_COLOR", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "protectItems", "lockHotbarSlots", "lockBoundSlots", "renderInventoryOverlays", "renderHotbarOverlays", "protectItemKey", "Lorg/cobalt/api/module/setting/impl/KeyBindSetting;", "lockSlotKey", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "lockedSlotsData", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "protectedItemsData", "boundSlotsData", "Ljava/util/TreeSet;", "lockedSlots", "Ljava/util/TreeSet;", "Ljava/util/LinkedHashSet;", "Lkotlin/collections/LinkedHashSet;", "boundSlots", "Ljava/util/LinkedHashSet;", "protectedItemUuids", "bindingStartSlot", "Ljava/lang/Integer;", "BoundSlotPair", "cobalt"})
@SourceDebugExtension(value={"SMAP\nItemLockingModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ItemLockingModule.kt\norg/cobalt/internal/qol/ItemLockingModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,599:1\n1642#2,10:600\n1915#2:610\n1916#2:612\n1652#2:613\n777#2:614\n873#2,2:615\n1915#2,2:617\n1642#2,10:619\n1915#2:629\n1916#2:631\n1652#2:632\n1915#2:633\n2792#2,3:634\n1916#2:637\n1586#2:638\n1661#2,3:639\n777#2:642\n873#2,2:643\n1915#2,2:645\n1807#2,3:647\n296#2,2:650\n296#2,2:652\n1807#2,3:654\n1#3:611\n1#3:630\n1#3:657\n*S KotlinDebug\n*F\n+ 1 ItemLockingModule.kt\norg/cobalt/internal/qol/ItemLockingModule\n*L\n145#1:600,10\n145#1:610\n145#1:612\n145#1:613\n146#1:614\n146#1:615,2\n147#1:617,2\n151#1:619,10\n151#1:629\n151#1:631\n151#1:632\n152#1:633\n153#1:634,3\n152#1:637\n162#1:638\n162#1:639,3\n163#1:642\n163#1:643,2\n164#1:645,2\n478#1:647,3\n492#1:650,2\n497#1:652,2\n529#1:654,3\n145#1:611\n151#1:630\n*E\n"})
public final class ItemLockingModule
extends Module {
    @NotNull
    public static final ItemLockingModule INSTANCE = new ItemLockingModule();
    private static final int OUTSIDE_SLOT_ID = -999;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
    private static final int INVENTORY_START_SLOT = 9;
    private static final int HOTBAR_SLOT_SIZE = 20;
    private static final int INVENTORY_SLOT_SIZE = 16;
    private static final int LOCKED_COLOR = -11688961;
    private static final int BOUND_COLOR = -10299029;
    private static final int PROTECTED_COLOR = -38294;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final CheckboxSetting protectItems;
    @NotNull
    private static final CheckboxSetting lockHotbarSlots;
    @NotNull
    private static final CheckboxSetting lockBoundSlots;
    @NotNull
    private static final CheckboxSetting renderInventoryOverlays;
    @NotNull
    private static final CheckboxSetting renderHotbarOverlays;
    @NotNull
    private static final KeyBindSetting protectItemKey;
    @NotNull
    private static final KeyBindSetting lockSlotKey;
    @NotNull
    private static final TextSetting lockedSlotsData;
    @NotNull
    private static final TextSetting protectedItemsData;
    @NotNull
    private static final TextSetting boundSlotsData;
    @NotNull
    private static final TreeSet<Integer> lockedSlots;
    @NotNull
    private static final LinkedHashSet<BoundSlotPair> boundSlots;
    @NotNull
    private static final LinkedHashSet<String> protectedItemUuids;
    @Nullable
    private static Integer bindingStartSlot;

    private ItemLockingModule() {
        super("Item Locking");
    }

    /*
     * WARNING - void declaration
     */
    public final void loadPersistedState() {
        void $this$filterTo$iv$iv;
        Iterable $this$mapTo$iv$iv;
        Object p0;
        Object $this$forEach$iv;
        void $this$filterTo$iv$iv2;
        Object it$iv$iv;
        Object element$iv$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        bindingStartSlot = null;
        lockedSlots.clear();
        char[] cArray = new char[]{','};
        Iterable $this$mapNotNull$iv = StringsKt.split$default((CharSequence)((CharSequence)lockedSlotsData.getValue()), (char[])cArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection collection = new ArrayList();
        boolean bl = false;
        Iterator $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            Object element$iv$iv$iv;
            element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl2 = false;
            String it = (String)element$iv$iv;
            boolean bl3 = false;
            if (StringsKt.toIntOrNull((String)((Object)StringsKt.trim((CharSequence)it)).toString()) == null) continue;
            boolean bl4 = false;
            collection.add(it$iv$iv);
        }
        Iterable $this$filter$iv = (List)collection;
        boolean $i$f$filter232 = false;
        $this$mapNotNullTo$iv$iv = $this$filter$iv;
        Collection collection2 = new ArrayList();
        boolean bl5 = false;
        for (Object element$iv$iv2 : $this$filterTo$iv$iv2) {
            int it = ((Number)element$iv$iv2).intValue();
            boolean bl4 = false;
            boolean bl6 = 0 <= it ? it < 9 : false;
            if (!bl6) continue;
            collection2.add(element$iv$iv2);
        }
        $this$filter$iv = (List)collection2;
        TreeSet<Integer> $i$f$filter232 = lockedSlots;
        boolean $i$f$forEach232 = false;
        for (Object t : $this$forEach$iv) {
            p0 = (Integer)t;
            boolean bl6 = false;
            $i$f$filter232.add((Integer)p0);
        }
        boundSlots.clear();
        $this$forEach$iv = new char[]{','};
        $this$mapNotNull$iv = StringsKt.split$default((CharSequence)((CharSequence)boundSlotsData.getValue()), (char[])$this$forEach$iv, (boolean)false, (int)0, (int)6, null);
        $i$f$mapNotNull = false;
        Iterable $i$f$forEach232 = $this$mapNotNull$iv;
        Collection collection3 = new ArrayList();
        boolean bl7 = false;
        $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        $i$f$forEach = false;
        Object it = $this$forEach$iv$iv$iv.iterator();
        while (it.hasNext()) {
            Object element$iv$iv$iv;
            element$iv$iv = element$iv$iv$iv = it.next();
            boolean bl8 = false;
            String p02 = (String)element$iv$iv;
            boolean bl72 = false;
            if (this.parseBoundSlotPair(p02) == null) continue;
            boolean bl9 = false;
            collection3.add(it$iv$iv);
        }
        $this$forEach$iv = (List)collection3;
        boolean $i$f$forEach3 = false;
        for (Object t : $this$forEach$iv) {
            boolean bl10;
            BoundSlotPair boundSlotPair;
            block11: {
                boundSlotPair = (BoundSlotPair)t;
                boolean bl9 = false;
                Iterable $this$none$iv = boundSlots;
                boolean $i$f$none = false;
                if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
                    bl10 = true;
                } else {
                    for (Object element$iv2 : $this$none$iv) {
                        BoundSlotPair existing = (BoundSlotPair)element$iv2;
                        boolean bl102 = false;
                        if (!(existing.getHotbarSlot() == boundSlotPair.getHotbarSlot() || existing.getInventorySlot() == boundSlotPair.getInventorySlot())) continue;
                        bl10 = false;
                        break block11;
                    }
                    bl10 = true;
                }
            }
            if (!bl10) continue;
            boundSlots.add(boundSlotPair);
        }
        protectedItemUuids.clear();
        $this$forEach$iv = new char[1];
        $this$forEach$iv[0] = 44;
        Iterable $this$map$iv = StringsKt.split$default((CharSequence)((CharSequence)protectedItemsData.getValue()), (char[])$this$forEach$iv, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        $this$mapNotNullTo$iv$iv = $this$map$iv;
        Collection collection4 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean bl11 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String $i$f$none = (String)item$iv$iv;
            Collection collection5 = collection4;
            boolean bl112 = false;
            collection5.add(((Object)StringsKt.trim((CharSequence)((CharSequence)it))).toString());
        }
        $this$filter$iv = (List)collection4;
        boolean $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        Collection collection6 = new ArrayList();
        boolean bl12 = false;
        for (Object element$iv$iv3 : $this$filterTo$iv$iv) {
            it = (String)element$iv$iv3;
            boolean bl122 = false;
            boolean bl13 = ((CharSequence)it).length() > 0;
            if (!bl13) continue;
            collection6.add(element$iv$iv3);
        }
        $this$filter$iv = (List)collection6;
        LinkedHashSet<String> linkedHashSet = protectedItemUuids;
        boolean $i$f$forEach4 = false;
        for (Object t : $this$forEach$iv) {
            p0 = (String)t;
            boolean bl14 = false;
            linkedHashSet.add((String)p0);
        }
    }

    public final boolean isLockedHotbarSlot(int slot) {
        return (Boolean)enabled.getValue() != false && (Boolean)lockHotbarSlots.getValue() != false && lockedSlots.contains(slot);
    }

    public final boolean isBlockedHotbarSlot(int slot) {
        return this.isLockedHotbarSlot(slot) || (Boolean)enabled.getValue() != false && (Boolean)lockBoundSlots.getValue() != false && this.isBoundHotbarSlot(slot);
    }

    public final boolean isProtectedItem(@Nullable class_1799 stack) {
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)protectItems.getValue()).booleanValue() || stack == null || stack.method_7960()) {
            return false;
        }
        String uuid = this.getSkyblockUuid(stack);
        return ((CharSequence)uuid).length() > 0 && protectedItemUuids.contains(uuid);
    }

    public final boolean shouldCancelSelectedItemDrop() {
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        return this.isBlockedHotbarSlot(player.method_31548().method_67532()) || this.isProtectedItem(player.method_6047());
    }

    public final boolean handleContainerKeyPressed(@NotNull class_1735 slot, @NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)slot, (String)"slot");
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return false;
        }
        if (this.matches(input, protectItemKey)) {
            class_1799 class_17992 = slot.method_7677();
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
            this.toggleProtectedItem(class_17992);
            return true;
        }
        if (this.matches(input, lockSlotKey) && this.isBindablePlayerSlot(slot)) {
            bindingStartSlot = slot.method_34266();
            return true;
        }
        return false;
    }

    public final boolean handleContainerKeyReleased(@Nullable class_1735 slot, @NotNull class_11908 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!((Boolean)enabled.getValue()).booleanValue() || !this.matches(input, lockSlotKey)) {
            return false;
        }
        Integer n = bindingStartSlot;
        if (n == null) {
            return false;
        }
        int startSlot = n;
        bindingStartSlot = null;
        if (slot == null || !this.isBindablePlayerSlot(slot)) {
            return true;
        }
        int endSlot = slot.method_34266();
        if (startSlot == endSlot) {
            this.toggleSlotAction(endSlot);
            return true;
        }
        if (this.isHotbarSlot(startSlot) != this.isHotbarSlot(endSlot)) {
            int inventorySlot;
            int hotbarSlot = this.isHotbarSlot(startSlot) ? startSlot : endSlot;
            int n2 = inventorySlot = this.isInventoryStorageSlot(startSlot) ? startSlot : endSlot;
            boolean bl = 9 <= inventorySlot ? inventorySlot < 36 : false;
            if (bl) {
                this.bindSlots(hotbarSlot, inventorySlot);
            }
            return true;
        }
        return true;
    }

    public final boolean shouldCancelContainerClick(@NotNull String title, @NotNull class_1703 menu, @Nullable class_1735 slot, int slotId, int button, @NotNull class_1713 clickType) {
        boolean lockedHotbarSlot;
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)menu, (String)"menu");
        Intrinsics.checkNotNullParameter((Object)clickType, (String)"clickType");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return false;
        }
        if (slotId == -999) {
            return this.isProtectedItem(menu.method_34255());
        }
        if (slot == null) {
            return false;
        }
        BoundSlotPair boundPair = this.getBoundSlotPair(slot);
        if (boundPair != null && clickType == class_1713.field_7794) {
            this.performBoundSlotSwap(menu, boundPair);
            return true;
        }
        class_1799 class_17992 = slot.method_7677();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
        class_1799 stack = class_17992;
        String titleText = this.sanitizeTitle(title);
        boolean bl = lockedHotbarSlot = this.isPlayerHotbarSlot(slot) && this.isLockedHotbarSlot(slot.method_34266());
        if (lockedHotbarSlot) {
            return true;
        }
        if (clickType == class_1713.field_7791) {
            boolean bl2 = 0 <= button ? button < 9 : false;
            if (bl2 && this.isBlockedHotbarSlot(button)) {
                return true;
            }
        }
        if (boundPair != null && ((Boolean)lockBoundSlots.getValue()).booleanValue()) {
            return true;
        }
        if (!this.isProtectedItem(stack)) {
            return false;
        }
        if (clickType == class_1713.field_7795) {
            return true;
        }
        if (Intrinsics.areEqual((Object)titleText, (Object)"Salvage Items")) {
            return true;
        }
        if (StringsKt.startsWith$default((String)titleText, (String)"You  ", (boolean)false, (int)2, null)) {
            return true;
        }
        if (StringsKt.endsWith$default((String)titleText, (String)"Auction House", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)titleText, (Object)"Create Auction") || Intrinsics.areEqual((Object)titleText, (Object)"Create BIN Auction")) {
            return true;
        }
        return this.isNpcSellScreen(menu) && slotId != 49;
    }

    public final void renderContainerSlotOverlay(@NotNull class_332 graphics, @NotNull class_1735 slot, int x, int y) {
        Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
        Intrinsics.checkNotNullParameter((Object)slot, (String)"slot");
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)renderInventoryOverlays.getValue()).booleanValue()) {
            return;
        }
        boolean locked = this.isPlayerHotbarSlot(slot) && this.isLockedHotbarSlot(slot.method_34266());
        boolean bound = this.isBoundSlot(slot);
        boolean bl = this.isProtectedItem(slot.method_7677());
        this.renderProtectionOutline(graphics, x, y, 16, locked, bound, bl);
    }

    public final void renderHotbarSlotOverlay(@NotNull class_332 graphics, int slot, int x, int y) {
        Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)renderHotbarOverlays.getValue()).booleanValue()) {
            return;
        }
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        class_1799 stack = class_7462 != null && (class_7462 = class_7462.method_31548()) != null ? class_7462.method_5438(slot) : null;
        boolean locked = this.isLockedHotbarSlot(slot);
        boolean bound = this.isBoundHotbarSlot(slot);
        boolean bl = this.isProtectedItem(stack);
        this.renderProtectionOutline(graphics, x, y, 20, locked, bound, bl);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!(ItemLockingModule.mc.field_1755 instanceof class_465)) {
            bindingStartSlot = null;
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (((KeyBind)lockSlotKey.getValue()).isPressed()) {
            this.toggleLockedSlot(player.method_31548().method_67532());
        }
        if (((KeyBind)protectItemKey.getValue()).isPressed()) {
            class_1799 class_17992 = player.method_6047();
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
            this.toggleProtectedItem(class_17992);
        }
    }

    @SubscribeEvent
    public final void onGuiRender(@NotNull GuiRenderEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)renderHotbarOverlays.getValue()).booleanValue() || ItemLockingModule.mc.field_1755 != null || HotbarOverlayModule.INSTANCE.isEnabled()) {
            return;
        }
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        int left = event.getGraphics().method_51421() / 2 - 91;
        int top = event.getGraphics().method_51443() - 22;
        for (int slot = 0; slot < 9; ++slot) {
            if (!this.isLockedHotbarSlot(slot) && !this.isBoundHotbarSlot(slot) && !this.isProtectedItem(player.method_31548().method_5438(slot))) continue;
            this.renderHotbarSlotOverlay(event.getGraphics(), slot, left + 1 + slot * 20, top + 1);
        }
    }

    private final void renderProtectionOutline(class_332 graphics, int x, int y, int size, boolean locked, boolean bound, boolean bl) {
        if (!(locked || bound || bl)) {
            return;
        }
        int inset = 0;
        if (locked) {
            this.renderInsetOutline(graphics, x, y, size, inset++, -11688961);
        }
        if (bound) {
            this.renderInsetOutline(graphics, x, y, size, inset++, -10299029);
        }
        if (bl) {
            this.renderInsetOutline(graphics, x, y, size, inset, -38294);
        }
    }

    private final void toggleLockedSlot(int slot) {
        boolean locked;
        block5: {
            block4: {
                if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)lockHotbarSlots.getValue()).booleanValue()) break block4;
                if (0 <= slot ? slot < 9 : false) break block5;
            }
            return;
        }
        boolean bl = locked = !lockedSlots.add(slot);
        if (locked) {
            lockedSlots.remove(slot);
            this.persistState();
            ChatUtils.sendMessage("Unlocked hotbar slot " + (slot + 1) + ".");
            return;
        }
        this.persistState();
        ChatUtils.sendMessage("Locked hotbar slot " + (slot + 1) + ".");
    }

    private final void toggleSlotAction(int slot) {
        if (this.clearBoundSlots(slot)) {
            return;
        }
        boolean bl = 0 <= slot ? slot < 9 : false;
        if (bl) {
            this.toggleLockedSlot(slot);
            return;
        }
        ChatUtils.sendMessage("Drag that slot to a hotbar slot to bind swapping.");
    }

    private final void bindSlots(int hotbarSlot, int inventorySlot) {
        block4: {
            block3: {
                boolean bl = 0 <= hotbarSlot ? hotbarSlot < 9 : false;
                if (!bl) break block3;
                if (9 <= inventorySlot ? inventorySlot < 36 : false) break block4;
            }
            return;
        }
        lockedSlots.remove(hotbarSlot);
        boundSlots.removeIf(arg_0 -> ItemLockingModule.bindSlots$lambda$1(arg_0 -> ItemLockingModule.bindSlots$lambda$0(hotbarSlot, inventorySlot, arg_0), arg_0));
        boundSlots.add(new BoundSlotPair(hotbarSlot, inventorySlot));
        this.persistState();
        ChatUtils.sendMessage("Bound " + this.describePlayerSlot(hotbarSlot) + " to " + this.describePlayerSlot(inventorySlot) + ".");
    }

    private final void toggleProtectedItem(class_1799 stack) {
        if (!((Boolean)enabled.getValue()).booleanValue() || !((Boolean)protectItems.getValue()).booleanValue()) {
            return;
        }
        if (stack.method_7960()) {
            ChatUtils.sendMessage("Hold or hover an item first.");
            return;
        }
        if (((CharSequence)SkyblockItemUtilsKt.getSkyblockId(stack)).length() == 0) {
            ChatUtils.sendMessage("That item is not a SkyBlock item.");
            return;
        }
        String uuid = this.getSkyblockUuid(stack);
        if (((CharSequence)uuid).length() == 0) {
            ChatUtils.sendMessage("That item has no SkyBlock UUID, so it cannot be protected.");
            return;
        }
        if (!protectedItemUuids.add(uuid)) {
            protectedItemUuids.remove(uuid);
            this.persistState();
            ChatUtils.sendMessage("Unprotected " + this.displayName(stack) + ".");
            return;
        }
        this.persistState();
        ChatUtils.sendMessage("Protected " + this.displayName(stack) + ".");
    }

    private final void persistState() {
        lockedSlotsData.setValue(CollectionsKt.joinToString$default((Iterable)lockedSlots, (CharSequence)",", null, null, (int)0, null, null, (int)62, null));
        protectedItemsData.setValue(CollectionsKt.joinToString$default((Iterable)protectedItemUuids, (CharSequence)",", null, null, (int)0, null, null, (int)62, null));
        Comparator comparator = new Comparator(){

            public final int compare(T a, T b) {
                BoundSlotPair it = (BoundSlotPair)a;
                boolean bl = false;
                Comparable comparable = Integer.valueOf(it.getHotbarSlot());
                it = (BoundSlotPair)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Integer.valueOf(it.getHotbarSlot()));
            }
        };
        boundSlotsData.setValue(CollectionsKt.joinToString$default((Iterable)CollectionsKt.sortedWith((Iterable)boundSlots, (Comparator)new Comparator(comparator){
            final /* synthetic */ Comparator $this_thenBy;
            {
                this.$this_thenBy = $receiver;
            }

            public final int compare(T a, T b) {
                int n;
                int previousCompare = this.$this_thenBy.compare(a, b);
                if (previousCompare != 0) {
                    n = previousCompare;
                } else {
                    BoundSlotPair it = (BoundSlotPair)a;
                    boolean bl = false;
                    Comparable comparable = Integer.valueOf(it.getInventorySlot());
                    it = (BoundSlotPair)b;
                    Comparable comparable2 = comparable;
                    bl = false;
                    n = ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Integer.valueOf(it.getInventorySlot()));
                }
                return n;
            }
        }), (CharSequence)",", null, null, (int)0, null, ItemLockingModule::persistState$lambda$2, (int)30, null));
        Config.INSTANCE.saveModulesConfig();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean isPlayerHotbarSlot(class_1735 slot) {
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        if (slot.field_7871 != player.method_31548()) return false;
        int n = slot.method_34266();
        if (0 > n) return false;
        if (n >= 9) return false;
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean isBindablePlayerSlot(class_1735 slot) {
        class_746 class_7462 = ItemLockingModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        if (slot.field_7871 != player.method_31548()) return false;
        int n = slot.method_34266();
        if (0 > n) return false;
        if (n >= 36) return false;
        return true;
    }

    private final boolean isBoundSlot(class_1735 slot) {
        return this.isBindablePlayerSlot(slot) && this.getBoundSlotPair(slot.method_34266()) != null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean isBoundHotbarSlot(int slot) {
        boolean bl;
        if ((Boolean)enabled.getValue() == false) return false;
        Iterable $this$any$iv = boundSlots;
        boolean $i$f$any = false;
        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
            return false;
        }
        Iterator iterator = $this$any$iv.iterator();
        do {
            if (!iterator.hasNext()) return false;
            Object element$iv = iterator.next();
            BoundSlotPair it = (BoundSlotPair)element$iv;
            boolean bl2 = false;
            if (it.getHotbarSlot() == slot) {
                return true;
            }
            bl = false;
        } while (!bl);
        return true;
    }

    private final BoundSlotPair getBoundSlotPair(class_1735 slot) {
        if (!this.isBindablePlayerSlot(slot)) {
            return null;
        }
        return this.getBoundSlotPair(slot.method_34266());
    }

    private final BoundSlotPair getBoundSlotPair(int slot) {
        Object v0;
        block2: {
            if (!((Boolean)enabled.getValue()).booleanValue()) {
                return null;
            }
            Iterable $this$firstOrNull$iv = boundSlots;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                BoundSlotPair it = (BoundSlotPair)element$iv;
                boolean bl = false;
                if (!(it.getHotbarSlot() == slot || it.getInventorySlot() == slot)) continue;
                v0 = element$iv;
                break block2;
            }
            v0 = null;
        }
        return v0;
    }

    private final void performBoundSlotSwap(class_1703 menu, BoundSlotPair pair) {
        block4: {
            Object v2;
            class_746 player;
            block3: {
                class_746 class_7462 = ItemLockingModule.mc.field_1724;
                if (class_7462 == null) {
                    return;
                }
                player = class_7462;
                class_2371 class_23712 = menu.field_7761;
                Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
                Iterable $this$firstOrNull$iv = (Iterable)class_23712;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    class_1735 slot = (class_1735)element$iv;
                    boolean bl = false;
                    if (!(slot.field_7871 == player.method_31548() && slot.method_34266() == pair.getInventorySlot())) continue;
                    v2 = element$iv;
                    break block3;
                }
                v2 = null;
            }
            class_1735 class_17352 = v2;
            if (class_17352 == null) {
                return;
            }
            class_1735 inventorySlot = class_17352;
            class_636 class_6362 = ItemLockingModule.mc.field_1761;
            if (class_6362 == null) break block4;
            class_6362.method_2906(menu.field_7763, inventorySlot.field_7874, pair.getHotbarSlot(), class_1713.field_7791, (class_1657)player);
        }
    }

    private final boolean clearBoundSlots(int slot) {
        if (!boundSlots.removeIf(arg_0 -> ItemLockingModule.clearBoundSlots$lambda$1(arg_0 -> ItemLockingModule.clearBoundSlots$lambda$0(slot, arg_0), arg_0))) {
            return false;
        }
        this.persistState();
        ChatUtils.sendMessage("Cleared slot bind from " + this.describePlayerSlot(slot) + ".");
        return true;
    }

    private final boolean isNpcSellScreen(class_1703 menu) {
        boolean bl;
        block6: {
            if (menu.field_7761.size() <= 49) {
                return false;
            }
            class_1799 class_17992 = ((class_1735)menu.field_7761.get(49)).method_7677();
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
            class_1799 sellStack = class_17992;
            if (sellStack.method_7960()) {
                return false;
            }
            String string = sellStack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String name = this.sanitizeTitle(string);
            if (Intrinsics.areEqual((Object)name, (Object)"Sell Item")) {
                return true;
            }
            Iterable $this$any$iv = ItemUtilsKt.getLoreLines(sellStack);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    class_2561 line = (class_2561)element$iv;
                    boolean bl2 = false;
                    String string2 = line.getString();
                    Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getString(...)");
                    if (!StringsKt.contains((CharSequence)string2, (CharSequence)"buyback", (boolean)true)) continue;
                    bl = true;
                    break block6;
                }
                bl = false;
            }
        }
        return bl;
    }

    private final boolean matches(class_11908 input, KeyBindSetting setting) {
        int keyCode = ((KeyBind)setting.getValue()).getKeyCode();
        return keyCode != -1 && input.comp_4795() == keyCode;
    }

    private final boolean isHotbarSlot(int slot) {
        return 0 <= slot ? slot < 9 : false;
    }

    private final boolean isInventoryStorageSlot(int slot) {
        return 9 <= slot ? slot < 36 : false;
    }

    private final void renderInsetOutline(class_332 graphics, int x, int y, int size, int inset, int color) {
        int innerSize = RangesKt.coerceAtLeast((int)(size - inset * 2), (int)2);
        graphics.method_73198(x + inset, y + inset, innerSize, innerSize, color);
    }

    private final String displayName(class_1799 stack) {
        CharSequence charSequence;
        String string = stack.method_7964().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        CharSequence charSequence2 = this.sanitizeTitle(string);
        if (charSequence2.length() == 0) {
            boolean bl = false;
            charSequence = "item";
        } else {
            charSequence = charSequence2;
        }
        return (String)charSequence;
    }

    private final String sanitizeTitle(String text) {
        String string = class_124.method_539((String)text);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        return string2;
    }

    private final String getSkyblockUuid(class_1799 $this$getSkyblockUuid) {
        class_2487 class_24872 = SkyblockItemUtilsKt.getSkyblockExtraAttributes($this$getSkyblockUuid);
        String string = class_24872 != null ? SkyblockItemUtilsKt.tagString(class_24872, "uuid") : null;
        if (string == null) {
            string = "";
        }
        return string;
    }

    private final BoundSlotPair parseBoundSlotPair(String entry) {
        int inventorySlot;
        int hotbarSlot;
        block8: {
            block7: {
                String trimmed = ((Object)StringsKt.trim((CharSequence)entry)).toString();
                if (((CharSequence)trimmed).length() == 0) {
                    return null;
                }
                int separator = StringsKt.indexOf$default((CharSequence)trimmed, (char)':', (int)0, (boolean)false, (int)6, null);
                if (separator <= 0 || separator == StringsKt.getLastIndex((CharSequence)trimmed)) {
                    return null;
                }
                String string = trimmed.substring(0, separator);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
                Integer n = StringsKt.toIntOrNull((String)string);
                if (n == null) {
                    return null;
                }
                hotbarSlot = n;
                String string2 = trimmed.substring(separator + 1);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
                Integer n2 = StringsKt.toIntOrNull((String)string2);
                if (n2 == null) {
                    return null;
                }
                inventorySlot = n2;
                boolean bl = 0 <= hotbarSlot ? hotbarSlot < 9 : false;
                if (!bl) break block7;
                if (9 <= inventorySlot ? inventorySlot < 36 : false) break block8;
            }
            return null;
        }
        return new BoundSlotPair(hotbarSlot, inventorySlot);
    }

    private final String describePlayerSlot(int slot) {
        return (0 <= slot ? slot < 9 : false) ? "hotbar slot " + (slot + 1) : ((9 <= slot ? slot < 36 : false) ? "inventory slot " + (slot - 9 + 1) : "slot " + (slot + 1));
    }

    private static final boolean bindSlots$lambda$0(int $hotbarSlot, int $inventorySlot, BoundSlotPair it) {
        return it.getHotbarSlot() == $hotbarSlot || it.getInventorySlot() == $inventorySlot;
    }

    private static final boolean bindSlots$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final CharSequence persistState$lambda$2(BoundSlotPair pair) {
        Intrinsics.checkNotNullParameter((Object)pair, (String)"pair");
        return pair.getHotbarSlot() + ":" + pair.getInventorySlot();
    }

    private static final boolean clearBoundSlots$lambda$0(int $slot, BoundSlotPair it) {
        return it.getHotbarSlot() == $slot || it.getInventorySlot() == $slot;
    }

    private static final boolean clearBoundSlots$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Enabled", "Enable item protection, hotbar locking, and Firmament-style slot swapping.", true), "General");
        protectItems = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Protect Items", "Prevent protected items from being dropped or used in risky SkyBlock menus.", true), "General");
        lockHotbarSlots = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Lock Hotbar Slots", "Prevent locked hotbar slots from being swapped out.", true), "General");
        lockBoundSlots = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Lock Bound Slots", "Treat bound slot pairs as protected unless they are shift-click swapped.", true), "General");
        renderInventoryOverlays = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Inventory Overlays", "Draw protected, locked, and bound outlines over inventory slots.", true), "Visuals");
        renderHotbarOverlays = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Hotbar Overlays", "Draw protected, locked, and bound outlines over the hotbar.", true), "Visuals");
        protectItemKey = (KeyBindSetting)SettingKt.inGroup((Setting)new KeyBindSetting("Protect Item Key", "Toggle protection on the held item, or the hovered item in a container screen.", new KeyBind(86)), "Keybinds");
        lockSlotKey = (KeyBindSetting)SettingKt.inGroup((Setting)new KeyBindSetting("Lock Slot Key", "Tap a hovered hotbar slot to lock it, or drag between hotbar and inventory slots to bind swapping.", new KeyBind(72)), "Keybinds");
        lockedSlotsData = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Locked Slots Data", "", ""), "__side__");
        protectedItemsData = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Protected Items Data", "", ""), "__side__");
        boundSlotsData = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Bound Slots Data", "", ""), "__side__");
        lockedSlots = new TreeSet();
        boundSlots = new LinkedHashSet();
        protectedItemUuids = new LinkedHashSet();
        Setting[] settingArray = new Setting[]{enabled, protectItems, lockHotbarSlots, lockBoundSlots, renderInventoryOverlays, renderHotbarOverlays, protectItemKey, lockSlotKey, lockedSlotsData, protectedItemsData, boundSlotsData};
        INSTANCE.addSetting(settingArray);
        INSTANCE.loadPersistedState();
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0010\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\bJ\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "", "", "hotbarSlot", "inventorySlot", "<init>", "(II)V", "component1", "()I", "component2", "copy", "(II)Lorg/cobalt/internal/qol/ItemLockingModule$BoundSlotPair;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getHotbarSlot", "getInventorySlot", "cobalt"})
    private static final class BoundSlotPair {
        private final int hotbarSlot;
        private final int inventorySlot;

        public BoundSlotPair(int hotbarSlot, int inventorySlot) {
            this.hotbarSlot = hotbarSlot;
            this.inventorySlot = inventorySlot;
        }

        public final int getHotbarSlot() {
            return this.hotbarSlot;
        }

        public final int getInventorySlot() {
            return this.inventorySlot;
        }

        public final int component1() {
            return this.hotbarSlot;
        }

        public final int component2() {
            return this.inventorySlot;
        }

        @NotNull
        public final BoundSlotPair copy(int hotbarSlot, int inventorySlot) {
            return new BoundSlotPair(hotbarSlot, inventorySlot);
        }

        public static /* synthetic */ BoundSlotPair copy$default(BoundSlotPair boundSlotPair, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = boundSlotPair.hotbarSlot;
            }
            if ((n3 & 2) != 0) {
                n2 = boundSlotPair.inventorySlot;
            }
            return boundSlotPair.copy(n, n2);
        }

        @NotNull
        public String toString() {
            return "BoundSlotPair(hotbarSlot=" + this.hotbarSlot + ", inventorySlot=" + this.inventorySlot + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.hotbarSlot);
            result = result * 31 + Integer.hashCode(this.inventorySlot);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BoundSlotPair)) {
                return false;
            }
            BoundSlotPair boundSlotPair = (BoundSlotPair)other;
            if (this.hotbarSlot != boundSlotPair.hotbarSlot) {
                return false;
            }
            return this.inventorySlot == boundSlotPair.inventorySlot;
        }
    }
}

