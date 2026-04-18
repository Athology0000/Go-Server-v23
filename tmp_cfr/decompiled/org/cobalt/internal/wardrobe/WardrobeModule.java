/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.PropertyReference1
 *  kotlin.jvm.internal.PropertyReference1Impl
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.properties.ReadWriteProperty
 *  kotlin.reflect.KProperty
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2649
 *  net.minecraft.class_310
 *  net.minecraft.class_3944
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.wardrobe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2649;
import net.minecraft.class_310;
import net.minecraft.class_3944;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.render.GuiRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.internal.helper.Config;
import org.cobalt.internal.wardrobe.WardrobeFakePlayerCache;
import org.cobalt.internal.wardrobe.WardrobeRenderer;
import org.cobalt.internal.wardrobe.WardrobeSet;
import org.cobalt.internal.wardrobe.WardrobeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0005YZ[\\]B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u0018H\u0007\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0017\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u001bH\u0007\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0017\u0010 \u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b \u0010!J\u0017\u0010\"\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b\"\u0010!J\u0017\u0010$\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b$\u0010!J\u000f\u0010%\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b%\u0010\u0003R\u0014\u0010'\u001a\u00020&8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010(R\u001b\u0010,\u001a\u00020\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b)\u0010*\u001a\u0004\b+\u0010\bR\u001b\u00101\u001a\u00020-8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b.\u0010*\u001a\u0004\b/\u00100R\u001b\u00104\u001a\u00020-8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b2\u0010*\u001a\u0004\b3\u00100R\u0014\u00106\u001a\u0002058\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b6\u00107R!\u0010;\u001a\u00020-8BX\u0082\u0084\u0002\u00a2\u0006\u0012\n\u0004\b8\u0010*\u0012\u0004\b:\u0010\u0003\u001a\u0004\b9\u00100R$\u0010=\u001a\u00020\u001e2\u0006\u0010<\u001a\u00020\u001e8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b=\u0010>\u001a\u0004\b?\u0010@R\u0016\u0010B\u001a\u00020A8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bB\u0010CR\u0016\u0010D\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010>R\u0016\u0010E\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bE\u0010>R\u0018\u0010F\u001a\u0004\u0018\u00010\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010GR(\u0010I\u001a\b\u0012\u0004\u0012\u00020H0\t8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bI\u0010J\u001a\u0004\bK\u0010\f\"\u0004\bL\u0010MR(\u0010O\u001a\b\u0012\u0004\u0012\u00020N0\t8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bO\u0010J\u001a\u0004\bP\u0010\f\"\u0004\bQ\u0010MR(\u0010S\u001a\b\u0012\u0004\u0012\u00020R0\t8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bS\u0010J\u001a\u0004\bT\u0010\f\"\u0004\bU\u0010MR\u0014\u0010W\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010X\u00a8\u0006^"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "loadFavorites", "", "shouldSuppressVanillaRender", "()Z", "", "Lorg/cobalt/internal/wardrobe/WardrobeSet;", "setsOnCurrentCustomPage", "()Ljava/util/List;", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "event", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "Lnet/minecraft/class_3944;", "pkt", "handleOpenScreen", "(Lnet/minecraft/class_3944;)V", "Lnet/minecraft/class_2649;", "handleContainerContent", "(Lnet/minecraft/class_2649;)V", "Lorg/cobalt/api/event/impl/render/GuiRenderEvent;", "onGuiRender", "(Lorg/cobalt/api/event/impl/render/GuiRenderEvent;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;", "onLeftClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;)V", "", "setId", "clickSet", "(I)V", "clickFavorite", "slot", "clickVanillaSlot", "closeWardrobe", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "enabled$delegate", "Lkotlin/properties/ReadWriteProperty;", "getEnabled", "enabled", "", "page1Slots$delegate", "getPage1Slots", "()Ljava/lang/String;", "page1Slots", "page2Slots$delegate", "getPage2Slots", "page2Slots", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "_favSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "favoritesData$delegate", "getFavoritesData", "getFavoritesData$annotations", "favoritesData", "value", "currentCustomPage", "I", "getCurrentCustomPage", "()I", "Lorg/cobalt/internal/wardrobe/WardrobeModule$ScanState;", "scanState", "Lorg/cobalt/internal/wardrobe/WardrobeModule$ScanState;", "scanPagesReceived", "openContainerId", "pendingEquipSetId", "Ljava/lang/Integer;", "Lorg/cobalt/internal/wardrobe/WardrobeModule$SlotHitbox;", "slotHitboxes", "Ljava/util/List;", "getSlotHitboxes", "setSlotHitboxes", "(Ljava/util/List;)V", "Lorg/cobalt/internal/wardrobe/WardrobeModule$TabHitbox;", "tabHitboxes", "getTabHitboxes", "setTabHitboxes", "Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonHitbox;", "buttonHitboxes", "getButtonHitboxes", "setButtonHitboxes", "Lkotlin/text/Regex;", "WARDROBE_TITLE_REGEX", "Lkotlin/text/Regex;", "ScanState", "SlotHitbox", "TabHitbox", "ButtonHitbox", "ButtonType", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeModule.kt\norg/cobalt/internal/wardrobe/WardrobeModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,231:1\n1642#2,10:232\n1915#2:242\n1916#2:244\n1652#2:245\n1915#2,2:246\n777#2:248\n873#2,2:249\n777#2:251\n873#2,2:252\n296#2,2:254\n296#2,2:256\n296#2,2:258\n1#3:243\n1#3:260\n*S KotlinDebug\n*F\n+ 1 WardrobeModule.kt\norg/cobalt/internal/wardrobe/WardrobeModule\n*L\n57#1:232,10\n57#1:242\n57#1:244\n57#1:245\n58#1:246,2\n68#1:248\n68#1:249,2\n69#1:251\n69#1:252,2\n172#1:254,2\n183#1:256,2\n188#1:258,2\n57#1:243\n*E\n"})
public final class WardrobeModule
extends Module {
    @NotNull
    public static final WardrobeModule INSTANCE;
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final ReadWriteProperty enabled$delegate;
    @NotNull
    private static final ReadWriteProperty page1Slots$delegate;
    @NotNull
    private static final ReadWriteProperty page2Slots$delegate;
    @NotNull
    private static final TextSetting _favSetting;
    @NotNull
    private static final ReadWriteProperty favoritesData$delegate;
    private static int currentCustomPage;
    @NotNull
    private static ScanState scanState;
    private static int scanPagesReceived;
    private static int openContainerId;
    @Nullable
    private static Integer pendingEquipSetId;
    @NotNull
    private static List<SlotHitbox> slotHitboxes;
    @NotNull
    private static List<TabHitbox> tabHitboxes;
    @NotNull
    private static List<ButtonHitbox> buttonHitboxes;
    @NotNull
    private static final Regex WARDROBE_TITLE_REGEX;

    private WardrobeModule() {
        super("Wardrobe GUI");
    }

    public final boolean getEnabled() {
        return (Boolean)enabled$delegate.getValue((Object)this, $$delegatedProperties[0]);
    }

    @NotNull
    public final String getPage1Slots() {
        return (String)page1Slots$delegate.getValue((Object)this, $$delegatedProperties[1]);
    }

    @NotNull
    public final String getPage2Slots() {
        return (String)page2Slots$delegate.getValue((Object)this, $$delegatedProperties[2]);
    }

    private final String getFavoritesData() {
        return (String)favoritesData$delegate.getValue((Object)this, $$delegatedProperties[3]);
    }

    private static /* synthetic */ void getFavoritesData$annotations() {
    }

    public final int getCurrentCustomPage() {
        return currentCustomPage;
    }

    @NotNull
    public final List<SlotHitbox> getSlotHitboxes() {
        return slotHitboxes;
    }

    public final void setSlotHitboxes(@NotNull List<SlotHitbox> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        slotHitboxes = list;
    }

    @NotNull
    public final List<TabHitbox> getTabHitboxes() {
        return tabHitboxes;
    }

    public final void setTabHitboxes(@NotNull List<TabHitbox> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        tabHitboxes = list;
    }

    @NotNull
    public final List<ButtonHitbox> getButtonHitboxes() {
        return buttonHitboxes;
    }

    public final void setButtonHitboxes(@NotNull List<ButtonHitbox> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        buttonHitboxes = list;
    }

    /*
     * WARNING - void declaration
     */
    public final void loadFavorites() {
        void $this$mapNotNullTo$iv$iv;
        WardrobeState.INSTANCE.getFavorites().clear();
        String[] stringArray = new String[]{","};
        Iterable $this$mapNotNull$iv = StringsKt.split$default((CharSequence)((CharSequence)_favSetting.getValue()), (String[])stringArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            Integer it$iv$iv;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            String it = (String)element$iv$iv;
            boolean bl2 = false;
            if (StringsKt.toIntOrNull((String)((Object)StringsKt.trim((CharSequence)it)).toString()) == null) continue;
            boolean bl3 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        Iterable $this$forEach$iv = (List)destination$iv$iv;
        boolean $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv) {
            int it = ((Number)element$iv).intValue();
            boolean bl = false;
            WardrobeState.INSTANCE.getFavorites().add(it);
        }
    }

    public final boolean shouldSuppressVanillaRender() {
        return this.getEnabled() && WardrobeState.INSTANCE.isOpen() && scanState == ScanState.DONE;
    }

    @NotNull
    public final List<WardrobeSet> setsOnCurrentCustomPage() {
        WardrobeSet it;
        Iterable $this$filterTo$iv$iv;
        Integer n = WardrobeState.INSTANCE.getCurrentVanillaPage();
        if (n == null) {
            return CollectionsKt.emptyList();
        }
        int currentVanillaPage = n;
        Iterable $this$filter$iv = WardrobeState.INSTANCE.getSets();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (WardrobeSet)element$iv$iv;
            boolean bl = false;
            if (!(!it.isEmpty() && !it.getLocked())) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        $i$f$filter = false;
        $this$filterTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (WardrobeSet)element$iv$iv;
            boolean bl = false;
            if (!(it.getVanillaPage() == currentVanillaPage)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_2596<?> pkt = event.getPacket();
        if (pkt instanceof class_3944) {
            this.handleOpenScreen((class_3944)pkt);
        } else if (pkt instanceof class_2649 && WardrobeState.INSTANCE.isOpen()) {
            this.handleContainerContent((class_2649)pkt);
        }
    }

    private final void handleOpenScreen(class_3944 pkt) {
        String string = pkt.method_17594().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        CharSequence charSequence = string;
        Regex regex = new Regex("\u00a7[0-9a-fk-or]");
        String string2 = "";
        String title = regex.replace(charSequence, string2);
        MatchResult match = Regex.find$default((Regex)WARDROBE_TITLE_REGEX, (CharSequence)title, (int)0, (int)2, null);
        if (match == null) {
            if (WardrobeState.INSTANCE.isOpen()) {
                this.closeWardrobe();
            }
            return;
        }
        int page = Integer.parseInt((String)match.getGroupValues().get(1));
        WardrobeState.INSTANCE.setOpen(true);
        WardrobeState.INSTANCE.setCurrentVanillaPage(page);
        openContainerId = pkt.method_17592();
        if (scanState == ScanState.IDLE || scanState == ScanState.SCANNING) {
            scanState = ScanState.SCANNING;
            scanPagesReceived = 0;
            currentCustomPage = page;
        }
    }

    private final void handleContainerContent(class_2649 pkt) {
        WardrobeSet set;
        Integer pending;
        int slotIndex;
        if (pkt.comp_3837() != openContainerId) {
            return;
        }
        Integer n = WardrobeState.INSTANCE.getCurrentVanillaPage();
        if (n == null) {
            return;
        }
        int page = n;
        List list = pkt.comp_3839();
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"items(...)");
        List items = list;
        Map armorBySetId = new LinkedHashMap();
        Integer equippedId = null;
        Set lockedIds = new LinkedHashSet();
        for (slotIndex = 0; slotIndex < 9; ++slotIndex) {
            class_1799 selectorItem;
            int setId;
            block9: {
                block8: {
                    String string;
                    List list2;
                    String lore;
                    setId = (page - 1) * 9 + slotIndex + 1;
                    Map map = armorBySetId;
                    Integer n2 = setId;
                    class_1799[] class_1799Array = new class_1799[]{WardrobeModule.handleContainerContent$slot(items, slotIndex, 0), WardrobeModule.handleContainerContent$slot(items, slotIndex, 9), WardrobeModule.handleContainerContent$slot(items, slotIndex, 18), WardrobeModule.handleContainerContent$slot(items, slotIndex, 27)};
                    class_1799Array = CollectionsKt.listOf((Object[])class_1799Array);
                    map.put(n2, class_1799Array);
                    selectorItem = (class_1799)CollectionsKt.getOrNull((List)items, (int)(36 + slotIndex));
                    if (selectorItem == null || selectorItem.method_7960()) continue;
                    class_1799Array = (class_1799[])selectorItem.method_58694(class_9334.field_49632);
                    String string2 = lore = class_1799Array != null && (list2 = class_1799Array.comp_2400()) != null && (string = CollectionsKt.joinToString$default((Iterable)list2, (CharSequence)" ", null, null, (int)0, null, WardrobeModule::handleContainerContent$lambda$1, (int)30, null)) != null ? string : "";
                    if (StringsKt.contains((CharSequence)lore, (CharSequence)"Equipped", (boolean)true)) break block8;
                    String string3 = selectorItem.method_7964().getString();
                    Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getString(...)");
                    if (!StringsKt.contains((CharSequence)string3, (CharSequence)"Equipped", (boolean)true)) break block9;
                }
                equippedId = setId;
            }
            if (!selectorItem.method_31574(class_1802.field_8879)) continue;
            lockedIds.add(setId);
        }
        WardrobeState.INSTANCE.updatePage(page, armorBySetId, equippedId, lockedIds);
        slotIndex = scanPagesReceived;
        scanPagesReceived = slotIndex + 1;
        if (scanState == ScanState.SCANNING && scanPagesReceived >= 1) {
            scanState = ScanState.DONE;
        }
        if ((pending = pendingEquipSetId) != null && (set = (WardrobeSet)CollectionsKt.getOrNull(WardrobeState.INSTANCE.getSets(), (int)(pending - 1))) != null && set.getVanillaPage() == page) {
            pendingEquipSetId = null;
            mc.execute(() -> WardrobeModule.handleContainerContent$lambda$2(set));
        }
    }

    @SubscribeEvent
    public final void onGuiRender(@NotNull GuiRenderEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!this.shouldSuppressVanillaRender()) {
            return;
        }
        WardrobeRenderer.INSTANCE.render(event.getGraphics(), this);
    }

    @SubscribeEvent
    public final void onLeftClick(@NotNull MouseEvent.LeftClick event) {
        Object v2;
        Object object;
        block15: {
            Object v1;
            Object it;
            boolean $i$f$firstOrNull;
            Iterable $this$firstOrNull$iv;
            float my;
            float mx;
            block14: {
                Object v0;
                block13: {
                    Intrinsics.checkNotNullParameter((Object)event, (String)"event");
                    if (!this.shouldSuppressVanillaRender()) {
                        return;
                    }
                    mx = (float)WardrobeModule.mc.field_1729.method_1603();
                    my = (float)WardrobeModule.mc.field_1729.method_1604();
                    $this$firstOrNull$iv = slotHitboxes;
                    $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv) {
                        it = (SlotHitbox)element$iv;
                        boolean bl = false;
                        if (!WardrobeModule.onLeftClick$inBounds(mx, my, ((SlotHitbox)it).getX(), ((SlotHitbox)it).getY(), ((SlotHitbox)it).getW(), ((SlotHitbox)it).getH())) continue;
                        v0 = element$iv;
                        break block13;
                    }
                    v0 = null;
                }
                object = v0;
                if (object != null) {
                    float heartY;
                    SlotHitbox hit = object;
                    boolean bl = false;
                    float heartX = hit.getX() + hit.getW() - 20.0f;
                    if (WardrobeModule.onLeftClick$inBounds(mx, my, heartX, heartY = hit.getY(), 20.0f, 20.0f)) {
                        INSTANCE.clickFavorite(hit.getSetId());
                    } else {
                        INSTANCE.clickSet(hit.getSetId());
                    }
                    return;
                }
                $this$firstOrNull$iv = tabHitboxes;
                $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    it = (TabHitbox)element$iv;
                    boolean bl = false;
                    if (!WardrobeModule.onLeftClick$inBounds(mx, my, ((TabHitbox)it).getX(), ((TabHitbox)it).getY(), ((TabHitbox)it).getW(), ((TabHitbox)it).getH())) continue;
                    v1 = element$iv;
                    break block14;
                }
                v1 = null;
            }
            object = v1;
            if (object != null) {
                Object hit = object;
                boolean bl = false;
                currentCustomPage = ((TabHitbox)hit).getPage();
                return;
            }
            $this$firstOrNull$iv = buttonHitboxes;
            $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                it = (ButtonHitbox)element$iv;
                boolean bl = false;
                if (!WardrobeModule.onLeftClick$inBounds(mx, my, ((ButtonHitbox)it).getX(), ((ButtonHitbox)it).getY(), ((ButtonHitbox)it).getW(), ((ButtonHitbox)it).getH())) continue;
                v2 = element$iv;
                break block15;
            }
            v2 = null;
        }
        object = v2;
        if (object != null) {
            Object hit = object;
            boolean bl = false;
            switch (WhenMappings.$EnumSwitchMapping$0[((ButtonHitbox)hit).getType().ordinal()]) {
                case 1: {
                    INSTANCE.clickVanillaSlot(48);
                    break;
                }
                case 2: {
                    INSTANCE.clickVanillaSlot(49);
                    break;
                }
                default: {
                    throw new NoWhenBranchMatchedException();
                }
            }
            return;
        }
    }

    private final void clickSet(int setId) {
        WardrobeSet wardrobeSet = (WardrobeSet)CollectionsKt.getOrNull(WardrobeState.INSTANCE.getSets(), (int)(setId - 1));
        if (wardrobeSet == null) {
            return;
        }
        WardrobeSet set = wardrobeSet;
        if (set.isEmpty() || set.getLocked()) {
            return;
        }
        Integer n = WardrobeState.INSTANCE.getCurrentVanillaPage();
        if (n == null) {
            return;
        }
        int currentVanillaPage = n;
        if (set.getVanillaPage() == currentVanillaPage) {
            this.clickVanillaSlot(set.getInventorySlot());
        }
    }

    private final void clickFavorite(int setId) {
        if (!WardrobeState.INSTANCE.getFavorites().add(setId)) {
            WardrobeState.INSTANCE.getFavorites().remove(setId);
        }
        _favSetting.setValue(CollectionsKt.joinToString$default((Iterable)WardrobeState.INSTANCE.getFavorites(), (CharSequence)",", null, null, (int)0, null, null, (int)62, null));
        Config.INSTANCE.saveModulesConfig();
    }

    private final void clickVanillaSlot(int slot) {
        block1: {
            class_437 class_4372 = WardrobeModule.mc.field_1755;
            class_465 class_4652 = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
            if (class_4652 == null) {
                return;
            }
            class_465 screen = class_4652;
            class_636 class_6362 = WardrobeModule.mc.field_1761;
            if (class_6362 == null) break block1;
            int n = screen.method_17577().field_7763;
            class_746 class_7462 = WardrobeModule.mc.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            class_6362.method_2906(n, slot, 0, class_1713.field_7790, (class_1657)class_7462);
        }
    }

    private final void closeWardrobe() {
        WardrobeState.INSTANCE.reset();
        WardrobeFakePlayerCache.INSTANCE.clear();
        scanState = ScanState.IDLE;
        scanPagesReceived = 0;
        openContainerId = -1;
        pendingEquipSetId = null;
    }

    private static final class_1799 handleContainerContent$slot(List<class_1799> items, int slotIndex, int base) {
        Object object;
        class_1799 class_17992 = (class_1799)CollectionsKt.getOrNull(items, (int)(base + slotIndex));
        if (class_17992 != null) {
            class_1799 class_17993;
            class_1799 it = class_17993 = class_17992;
            boolean bl = false;
            object = !it.method_7960() ? class_17993 : null;
        } else {
            object = null;
        }
        return object;
    }

    private static final CharSequence handleContainerContent$lambda$1(class_2561 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        String string = it.getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        CharSequence charSequence = string;
        Regex regex = new Regex("\u00a7[0-9a-fk-or]");
        String string2 = "";
        return regex.replace(charSequence, string2);
    }

    private static final void handleContainerContent$lambda$2(WardrobeSet $set) {
        INSTANCE.clickVanillaSlot($set.getInventorySlot());
    }

    private static final boolean onLeftClick$inBounds(float mx, float my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    static {
        KProperty[] kPropertyArray = new KProperty[]{Reflection.property1((PropertyReference1)((PropertyReference1)new PropertyReference1Impl(WardrobeModule.class, "enabled", "getEnabled()Z", 0))), Reflection.property1((PropertyReference1)((PropertyReference1)new PropertyReference1Impl(WardrobeModule.class, "page1Slots", "getPage1Slots()Ljava/lang/String;", 0))), Reflection.property1((PropertyReference1)((PropertyReference1)new PropertyReference1Impl(WardrobeModule.class, "page2Slots", "getPage2Slots()Ljava/lang/String;", 0))), Reflection.property1((PropertyReference1)((PropertyReference1)new PropertyReference1Impl(WardrobeModule.class, "favoritesData", "getFavoritesData()Ljava/lang/String;", 0)))};
        $$delegatedProperties = kPropertyArray;
        INSTANCE = new WardrobeModule();
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled$delegate = new CheckboxSetting("Enabled", "Replace vanilla wardrobe with custom GUI", true).provideDelegate(INSTANCE, $$delegatedProperties[0]);
        page1Slots$delegate = new TextSetting("Page 1 Slots", "Comma-separated set numbers (1\u201327) for page 1", "1").provideDelegate(INSTANCE, $$delegatedProperties[1]);
        page2Slots$delegate = new TextSetting("Page 2 Slots", "Comma-separated set numbers (1\u201327) for page 2", "").provideDelegate(INSTANCE, $$delegatedProperties[2]);
        _favSetting = (TextSetting)SettingKt.inGroup((Setting)new TextSetting("Favorites Data", "", ""), "__side__");
        favoritesData$delegate = _favSetting.provideDelegate(INSTANCE, $$delegatedProperties[3]);
        currentCustomPage = 1;
        scanState = ScanState.IDLE;
        openContainerId = -1;
        slotHitboxes = CollectionsKt.emptyList();
        tabHitboxes = CollectionsKt.emptyList();
        buttonHitboxes = CollectionsKt.emptyList();
        EventBus.register(INSTANCE);
        WARDROBE_TITLE_REGEX = new Regex("Wardrobe \\((\\d+)/\\d+\\)");
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010 \u001a\u0004\b\"\u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010 \u001a\u0004\b#\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010 \u001a\u0004\b$\u0010\u000e\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonHitbox;", "", "Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;", "type", "", "x", "y", "w", "h", "<init>", "(Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;FFFF)V", "component1", "()Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;", "component2", "()F", "component3", "component4", "component5", "copy", "(Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;FFFF)Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonHitbox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;", "getType", "F", "getX", "getY", "getW", "getH", "cobalt"})
    public static final class ButtonHitbox {
        @NotNull
        private final ButtonType type;
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public ButtonHitbox(@NotNull ButtonType type, float x, float y, float w, float h) {
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            this.type = type;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @NotNull
        public final ButtonType getType() {
            return this.type;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getW() {
            return this.w;
        }

        public final float getH() {
            return this.h;
        }

        @NotNull
        public final ButtonType component1() {
            return this.type;
        }

        public final float component2() {
            return this.x;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.w;
        }

        public final float component5() {
            return this.h;
        }

        @NotNull
        public final ButtonHitbox copy(@NotNull ButtonType type, float x, float y, float w, float h) {
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            return new ButtonHitbox(type, x, y, w, h);
        }

        public static /* synthetic */ ButtonHitbox copy$default(ButtonHitbox buttonHitbox, ButtonType buttonType, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                buttonType = buttonHitbox.type;
            }
            if ((n & 2) != 0) {
                f = buttonHitbox.x;
            }
            if ((n & 4) != 0) {
                f2 = buttonHitbox.y;
            }
            if ((n & 8) != 0) {
                f3 = buttonHitbox.w;
            }
            if ((n & 0x10) != 0) {
                f4 = buttonHitbox.h;
            }
            return buttonHitbox.copy(buttonType, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "ButtonHitbox(type=" + this.type + ", x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = this.type.hashCode();
            result = result * 31 + Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ButtonHitbox)) {
                return false;
            }
            ButtonHitbox buttonHitbox = (ButtonHitbox)other;
            if (this.type != buttonHitbox.type) {
                return false;
            }
            if (Float.compare(this.x, buttonHitbox.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, buttonHitbox.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, buttonHitbox.w) != 0) {
                return false;
            }
            return Float.compare(this.h, buttonHitbox.h) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule$ButtonType;", "", "<init>", "(Ljava/lang/String;I)V", "BACK", "CLOSE", "cobalt"})
    public static final class ButtonType
    extends Enum<ButtonType> {
        public static final /* enum */ ButtonType BACK = new ButtonType();
        public static final /* enum */ ButtonType CLOSE = new ButtonType();
        private static final /* synthetic */ ButtonType[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static ButtonType[] values() {
            return (ButtonType[])$VALUES.clone();
        }

        public static ButtonType valueOf(String value) {
            return Enum.valueOf(ButtonType.class, value);
        }

        @NotNull
        public static EnumEntries<ButtonType> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = buttonTypeArray = new ButtonType[]{ButtonType.BACK, ButtonType.CLOSE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule$ScanState;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "SCANNING", "DONE", "cobalt"})
    private static final class ScanState
    extends Enum<ScanState> {
        public static final /* enum */ ScanState IDLE = new ScanState();
        public static final /* enum */ ScanState SCANNING = new ScanState();
        public static final /* enum */ ScanState DONE = new ScanState();
        private static final /* synthetic */ ScanState[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static ScanState[] values() {
            return (ScanState[])$VALUES.clone();
        }

        public static ScanState valueOf(String value) {
            return Enum.valueOf(ScanState.class, value);
        }

        @NotNull
        public static EnumEntries<ScanState> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = scanStateArray = new ScanState[]{ScanState.IDLE, ScanState.SCANNING, ScanState.DONE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\fJ\u0011\u0010\u001a\u001a\u00020\u0019H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000e\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule$SlotHitbox;", "", "", "setId", "", "x", "y", "w", "h", "<init>", "(IFFFF)V", "component1", "()I", "component2", "()F", "component3", "component4", "component5", "copy", "(IFFFF)Lorg/cobalt/internal/wardrobe/WardrobeModule$SlotHitbox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getSetId", "F", "getX", "getY", "getW", "getH", "cobalt"})
    public static final class SlotHitbox {
        private final int setId;
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public SlotHitbox(int setId, float x, float y, float w, float h) {
            this.setId = setId;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public final int getSetId() {
            return this.setId;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getW() {
            return this.w;
        }

        public final float getH() {
            return this.h;
        }

        public final int component1() {
            return this.setId;
        }

        public final float component2() {
            return this.x;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.w;
        }

        public final float component5() {
            return this.h;
        }

        @NotNull
        public final SlotHitbox copy(int setId, float x, float y, float w, float h) {
            return new SlotHitbox(setId, x, y, w, h);
        }

        public static /* synthetic */ SlotHitbox copy$default(SlotHitbox slotHitbox, int n, float f, float f2, float f3, float f4, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = slotHitbox.setId;
            }
            if ((n2 & 2) != 0) {
                f = slotHitbox.x;
            }
            if ((n2 & 4) != 0) {
                f2 = slotHitbox.y;
            }
            if ((n2 & 8) != 0) {
                f3 = slotHitbox.w;
            }
            if ((n2 & 0x10) != 0) {
                f4 = slotHitbox.h;
            }
            return slotHitbox.copy(n, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "SlotHitbox(setId=" + this.setId + ", x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.setId);
            result = result * 31 + Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SlotHitbox)) {
                return false;
            }
            SlotHitbox slotHitbox = (SlotHitbox)other;
            if (this.setId != slotHitbox.setId) {
                return false;
            }
            if (Float.compare(this.x, slotHitbox.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, slotHitbox.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, slotHitbox.w) != 0) {
                return false;
            }
            return Float.compare(this.h, slotHitbox.h) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\fJ\u0011\u0010\u001a\u001a\u00020\u0019H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001e\u001a\u0004\b!\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001e\u001a\u0004\b\"\u0010\u000e\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeModule$TabHitbox;", "", "", "page", "", "x", "y", "w", "h", "<init>", "(IFFFF)V", "component1", "()I", "component2", "()F", "component3", "component4", "component5", "copy", "(IFFFF)Lorg/cobalt/internal/wardrobe/WardrobeModule$TabHitbox;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getPage", "F", "getX", "getY", "getW", "getH", "cobalt"})
    public static final class TabHitbox {
        private final int page;
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public TabHitbox(int page, float x, float y, float w, float h) {
            this.page = page;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public final int getPage() {
            return this.page;
        }

        public final float getX() {
            return this.x;
        }

        public final float getY() {
            return this.y;
        }

        public final float getW() {
            return this.w;
        }

        public final float getH() {
            return this.h;
        }

        public final int component1() {
            return this.page;
        }

        public final float component2() {
            return this.x;
        }

        public final float component3() {
            return this.y;
        }

        public final float component4() {
            return this.w;
        }

        public final float component5() {
            return this.h;
        }

        @NotNull
        public final TabHitbox copy(int page, float x, float y, float w, float h) {
            return new TabHitbox(page, x, y, w, h);
        }

        public static /* synthetic */ TabHitbox copy$default(TabHitbox tabHitbox, int n, float f, float f2, float f3, float f4, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = tabHitbox.page;
            }
            if ((n2 & 2) != 0) {
                f = tabHitbox.x;
            }
            if ((n2 & 4) != 0) {
                f2 = tabHitbox.y;
            }
            if ((n2 & 8) != 0) {
                f3 = tabHitbox.w;
            }
            if ((n2 & 0x10) != 0) {
                f4 = tabHitbox.h;
            }
            return tabHitbox.copy(n, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "TabHitbox(page=" + this.page + ", x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.page);
            result = result * 31 + Float.hashCode(this.x);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Float.hashCode(this.w);
            result = result * 31 + Float.hashCode(this.h);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TabHitbox)) {
                return false;
            }
            TabHitbox tabHitbox = (TabHitbox)other;
            if (this.page != tabHitbox.page) {
                return false;
            }
            if (Float.compare(this.x, tabHitbox.x) != 0) {
                return false;
            }
            if (Float.compare(this.y, tabHitbox.y) != 0) {
                return false;
            }
            if (Float.compare(this.w, tabHitbox.w) != 0) {
                return false;
            }
            return Float.compare(this.h, tabHitbox.h) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[ButtonType.values().length];
            try {
                nArray[ButtonType.BACK.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ButtonType.CLOSE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

