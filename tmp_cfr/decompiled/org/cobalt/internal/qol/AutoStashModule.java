/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1657
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.qol;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_634;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.MouseClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001MB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\u0003J\u0015\u0010\r\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u000f\u0010\u0017\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0003J\u0017\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0016J\u0017\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0016J\u0017\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u0016J!\u0010\u001e\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u0014\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0019\u0010 \u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b \u0010!J\u0017\u0010\"\u001a\u00020\u001d2\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b\"\u0010#J\u0017\u0010%\u001a\u00020\u00042\u0006\u0010$\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010)\u001a\u00020\u00042\b\u0010(\u001a\u0004\u0018\u00010'\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010,\u001a\u00020\u00072\u0006\u0010+\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b,\u0010-J\u0017\u0010/\u001a\u00020\n2\u0006\u0010.\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b/\u00100J\u0017\u00102\u001a\u00020\n2\u0006\u00101\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b2\u0010\u000eR\u0014\u00104\u001a\u0002038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b4\u00105R\u0014\u00107\u001a\u0002068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b7\u00108R\u0014\u0010:\u001a\u0002098\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b:\u0010;R\u0014\u0010=\u001a\u00020<8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b=\u0010>R\u0014\u0010@\u001a\u00020?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0014\u0010B\u001a\u00020?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bB\u0010AR\u0014\u0010C\u001a\u00020?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bC\u0010AR\u0014\u0010D\u001a\u0002068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bD\u00108R\u0016\u0010F\u001a\u00020E8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0016\u0010H\u001a\u00020\u001d8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bH\u0010IR\u0016\u0010J\u001a\u00020\u001d8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bJ\u0010IR\u0016\u0010K\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bK\u0010L\u00a8\u0006N"}, d2={"Lorg/cobalt/internal/qol/AutoStashModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "isGuiEnabled", "()Z", "", "getGuiButtonLabel", "()Ljava/lang/String;", "", "toggleFromGui", "value", "setGuiEnabled", "(Z)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lnet/minecraft/class_746;", "player", "startRun", "(Lnet/minecraft/class_746;)V", "startOpenStashRun", "handleWaitOpen", "handleEmptying", "handleWaitPage", "Lnet/minecraft/class_1703;", "menu", "", "findMovableStashSlot", "(Lnet/minecraft/class_1703;Lnet/minecraft/class_746;)Ljava/lang/Integer;", "findNextPageSlot", "(Lnet/minecraft/class_1703;)Ljava/lang/Integer;", "getContainerSlotCount", "(Lnet/minecraft/class_1703;)I", "rawName", "isNavigationItem", "(Ljava/lang/String;)Z", "Lnet/minecraft/class_437;", "screen", "isStashScreen", "(Lnet/minecraft/class_437;)Z", "raw", "normalizeText", "(Ljava/lang/String;)Ljava/lang/String;", "message", "disableWithMessage", "(Ljava/lang/String;)V", "closeContainer", "resetState", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "openCommand", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "clickDelayTicks", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "openTimeoutTicks", "pageDelayTicks", "closeOnDone", "Lorg/cobalt/internal/qol/AutoStashModule$StashState;", "state", "Lorg/cobalt/internal/qol/AutoStashModule$StashState;", "actionCooldown", "I", "openTicks", "wasEnabled", "Z", "StashState", "cobalt"})
public final class AutoStashModule
extends Module {
    @NotNull
    public static final AutoStashModule INSTANCE = new AutoStashModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final TextSetting openCommand;
    @NotNull
    private static final SliderSetting clickDelayTicks;
    @NotNull
    private static final SliderSetting openTimeoutTicks;
    @NotNull
    private static final SliderSetting pageDelayTicks;
    @NotNull
    private static final CheckboxSetting closeOnDone;
    @NotNull
    private static StashState state;
    private static int actionCooldown;
    private static int openTicks;
    private static boolean wasEnabled;

    private AutoStashModule() {
        super("Auto Stash");
    }

    public final boolean isGuiEnabled() {
        return (Boolean)enabled.getValue();
    }

    @NotNull
    public final String getGuiButtonLabel() {
        return (Boolean)enabled.getValue() != false ? "Auto Empty: ON" : "Auto Empty: OFF";
    }

    public final void toggleFromGui() {
        this.setGuiEnabled((Boolean)enabled.getValue() == false);
    }

    public final void setGuiEnabled(boolean value) {
        if ((Boolean)enabled.getValue() == value) {
            return;
        }
        enabled.setValue(value);
        if (!value) {
            this.resetState(false);
            wasEnabled = false;
        }
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            if (wasEnabled) {
                this.resetState(false);
                wasEnabled = false;
            }
            return;
        }
        class_746 class_7462 = AutoStashModule.mc.field_1724;
        if (class_7462 == null) {
            AutoStashModule $this$onTick_u24lambda_u240 = this;
            boolean bl = false;
            $this$onTick_u24lambda_u240.disableWithMessage("Auto Stash: player unavailable.");
            return;
        }
        class_746 player = class_7462;
        if (!wasEnabled) {
            wasEnabled = true;
            if (this.isStashScreen(AutoStashModule.mc.field_1755)) {
                this.startOpenStashRun();
            } else {
                this.startRun(player);
            }
            return;
        }
        if (actionCooldown > 0) {
            int n = actionCooldown;
            actionCooldown = n + -1;
        }
        switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1: {
                this.handleWaitOpen(player);
                break;
            }
            case 2: {
                this.handleEmptying(player);
                break;
            }
            case 3: {
                this.handleWaitPage(player);
                break;
            }
        }
    }

    private final void startRun(class_746 player) {
        this.resetState(false);
        String command = StringsKt.removePrefix((String)((Object)StringsKt.trim((CharSequence)((String)openCommand.getValue()))).toString(), (CharSequence)"/");
        if (((CharSequence)command).length() == 0) {
            this.disableWithMessage("Auto Stash: open command is empty.");
            return;
        }
        class_634 class_6342 = player.field_3944;
        if (class_6342 != null) {
            class_6342.method_45730(command);
        }
        state = StashState.WAIT_OPEN;
        openTicks = 0;
        actionCooldown = RangesKt.coerceAtLeast((int)((int)((Number)clickDelayTicks.getValue()).doubleValue()), (int)1);
        ChatUtils.sendMessage("Auto Stash: opening stash.");
    }

    private final void startOpenStashRun() {
        this.resetState(false);
        state = StashState.EMPTYING;
        actionCooldown = RangesKt.coerceAtLeast((int)((int)((Number)clickDelayTicks.getValue()).doubleValue()), (int)1);
        openTicks = 0;
        ChatUtils.sendMessage("Auto Stash: emptying current stash.");
    }

    private final void handleWaitOpen(class_746 player) {
        if (this.isStashScreen(AutoStashModule.mc.field_1755)) {
            state = StashState.EMPTYING;
            actionCooldown = RangesKt.coerceAtLeast((int)((int)((Number)clickDelayTicks.getValue()).doubleValue()), (int)1);
            openTicks = 0;
            return;
        }
        int n = openTicks;
        if ((openTicks = n + 1) >= RangesKt.coerceAtLeast((int)((int)((Number)openTimeoutTicks.getValue()).doubleValue()), (int)10)) {
            this.disableWithMessage("Auto Stash: stash GUI did not open.");
        }
    }

    private final void handleEmptying(class_746 player) {
        if (!this.isStashScreen(AutoStashModule.mc.field_1755)) {
            this.disableWithMessage("Auto Stash: stash GUI closed.");
            return;
        }
        if (actionCooldown > 0) {
            return;
        }
        class_1703 class_17032 = player.field_7512;
        Intrinsics.checkNotNullExpressionValue((Object)class_17032, (String)"containerMenu");
        class_1703 menu = class_17032;
        Integer movableSlot = this.findMovableStashSlot(menu, player);
        if (movableSlot != null) {
            InventoryUtils.clickSlot(movableSlot, MouseClickType.LEFT, class_1713.field_7790);
            actionCooldown = RangesKt.coerceAtLeast((int)((int)((Number)clickDelayTicks.getValue()).doubleValue()), (int)1);
            return;
        }
        Integer nextPageSlot = this.findNextPageSlot(menu);
        if (nextPageSlot != null) {
            InventoryUtils.clickSlot$default(nextPageSlot, null, null, 6, null);
            state = StashState.WAIT_PAGE;
            actionCooldown = RangesKt.coerceAtLeast((int)((int)((Number)pageDelayTicks.getValue()).doubleValue()), (int)1);
            return;
        }
        if (((Boolean)closeOnDone.getValue()).booleanValue()) {
            player.method_7346();
        }
        this.disableWithMessage("Auto Stash: stash emptied.");
    }

    private final void handleWaitPage(class_746 player) {
        if (!this.isStashScreen(AutoStashModule.mc.field_1755)) {
            this.disableWithMessage("Auto Stash: stash GUI closed.");
            return;
        }
        if (actionCooldown > 0) {
            return;
        }
        state = StashState.EMPTYING;
    }

    private final Integer findMovableStashSlot(class_1703 menu, class_746 player) {
        int containerSlots = this.getContainerSlotCount(menu);
        for (int slotIndex = 0; slotIndex < containerSlots; ++slotIndex) {
            Object object = menu.field_7761.get(slotIndex);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
            class_1735 slot = (class_1735)object;
            if (!slot.method_7681() || !slot.method_7674((class_1657)player)) continue;
            String string = slot.method_7677().method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            if (this.isNavigationItem(string)) continue;
            return slotIndex;
        }
        return null;
    }

    private final Integer findNextPageSlot(class_1703 menu) {
        int containerSlots = this.getContainerSlotCount(menu);
        for (int slotIndex = 0; slotIndex < containerSlots; ++slotIndex) {
            Object object = menu.field_7761.get(slotIndex);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
            class_1735 slot = (class_1735)object;
            if (!slot.method_7681()) continue;
            String string = slot.method_7677().method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            String name = this.normalizeText(string);
            if (!StringsKt.contains$default((CharSequence)name, (CharSequence)"next page", (boolean)false, (int)2, null)) continue;
            return slotIndex;
        }
        return null;
    }

    private final int getContainerSlotCount(class_1703 menu) {
        return RangesKt.coerceAtLeast((int)(menu.field_7761.size() - 36), (int)0);
    }

    private final boolean isNavigationItem(String rawName) {
        String name = this.normalizeText(rawName);
        return StringsKt.contains$default((CharSequence)name, (CharSequence)"next page", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)"previous page", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)"go back", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)name, (CharSequence)"close", (boolean)false, (int)2, null);
    }

    public final boolean isStashScreen(@Nullable class_437 screen) {
        class_465 class_4652 = screen instanceof class_465 ? (class_465)screen : null;
        if (class_4652 == null) {
            return false;
        }
        class_465 containerScreen = class_4652;
        String string = containerScreen.method_25440().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String title = this.normalizeText(string);
        return StringsKt.contains$default((CharSequence)title, (CharSequence)"stash", (boolean)false, (int)2, null);
    }

    private final String normalizeText(String raw) {
        String string = class_124.method_539((String)raw);
        if (string == null) {
            string = raw;
        }
        String string2 = string.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        return ((Object)StringsKt.trim((CharSequence)string2)).toString();
    }

    private final void disableWithMessage(String message) {
        enabled.setValue(false);
        ChatUtils.sendMessage(message);
    }

    private final void resetState(boolean closeContainer) {
        if (closeContainer) {
            class_746 class_7462 = AutoStashModule.mc.field_1724;
            if (class_7462 != null) {
                class_7462.method_7346();
            }
        }
        state = StashState.IDLE;
        actionCooldown = 0;
        openTicks = 0;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = new CheckboxSetting("Enabled", "Open stash and left-click claim everything out, then disable itself.", false);
        info = new InfoSetting("Auto Stash", "Runs the stash command, opens the stash menu, and left-click claims it page by page.", InfoType.INFO);
        openCommand = new TextSetting("Open Command", "Command used to open the stash.", "pickupstash");
        clickDelayTicks = new SliderSetting("Click Delay", "Ticks to wait between stash actions.", 3.0, 1.0, 20.0, 1.0);
        openTimeoutTicks = new SliderSetting("Open Timeout", "Ticks to wait for the stash GUI to open.", 60.0, 10.0, 200.0, 1.0);
        pageDelayTicks = new SliderSetting("Page Delay", "Ticks to wait after clicking the next-page button.", 6.0, 1.0, 30.0, 1.0);
        closeOnDone = new CheckboxSetting("Close On Done", "Close the stash GUI after emptying it.", true);
        state = StashState.IDLE;
        Setting[] settingArray = new Setting[]{enabled, info, openCommand, clickDelayTicks, openTimeoutTicks, pageDelayTicks, closeOnDone};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/qol/AutoStashModule$StashState;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "WAIT_OPEN", "EMPTYING", "WAIT_PAGE", "cobalt"})
    private static final class StashState
    extends Enum<StashState> {
        public static final /* enum */ StashState IDLE = new StashState();
        public static final /* enum */ StashState WAIT_OPEN = new StashState();
        public static final /* enum */ StashState EMPTYING = new StashState();
        public static final /* enum */ StashState WAIT_PAGE = new StashState();
        private static final /* synthetic */ StashState[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static StashState[] values() {
            return (StashState[])$VALUES.clone();
        }

        public static StashState valueOf(String value) {
            return Enum.valueOf(StashState.class, value);
        }

        @NotNull
        public static EnumEntries<StashState> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = stashStateArray = new StashState[]{StashState.IDLE, StashState.WAIT_OPEN, StashState.EMPTYING, StashState.WAIT_PAGE};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[StashState.values().length];
            try {
                nArray[StashState.WAIT_OPEN.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[StashState.EMPTYING.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[StashState.WAIT_PAGE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

