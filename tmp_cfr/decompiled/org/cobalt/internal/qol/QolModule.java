/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.qol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.CommandHotkeySetting;
import org.cobalt.api.module.setting.impl.CommandHotkeyValue;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.util.ChatUtils;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\u000b\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\r\u0010\u0003J\u000f\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0019\u001a\u00020\u00188\u0006\u00a2\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001e\u001a\u00020\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001fR\u0014\u0010!\u001a\u00020 8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\"\u00a8\u0006#"}, d2={"Lorg/cobalt/internal/qol/QolModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "", "raw", "runCommand", "(Ljava/lang/String;)V", "addCommandHotkey", "", "loadSavedHotkeyCount", "()I", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "Lorg/cobalt/api/module/setting/impl/CommandHotkeySetting;", "commandHotkeys", "Ljava/util/List;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "commandHotkeysInfo", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "addHotkey", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nQolModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QolModule.kt\norg/cobalt/internal/qol/QolModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,119:1\n1915#2,2:120\n1915#2:123\n1915#2:124\n1915#2,2:125\n1916#2:127\n1916#2:128\n1#3:122\n*S KotlinDebug\n*F\n+ 1 QolModule.kt\norg/cobalt/internal/qol/QolModule\n*L\n58#1:120,2\n101#1:123\n104#1:124\n108#1:125,2\n104#1:127\n101#1:128\n*E\n"})
public final class QolModule
extends Module {
    @NotNull
    public static final QolModule INSTANCE = new QolModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final List<CommandHotkeySetting> commandHotkeys;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting commandHotkeysInfo;
    @NotNull
    private static final ActionSetting addHotkey;

    private QolModule() {
        super("QoL");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        Iterable $this$forEach$iv = commandHotkeys;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            CommandHotkeySetting entry = (CommandHotkeySetting)element$iv;
            boolean bl = false;
            if (!((CommandHotkeyValue)entry.getValue()).getKeyBind().isPressed()) continue;
            INSTANCE.runCommand(((CommandHotkeyValue)entry.getValue()).getCommand());
        }
    }

    private final void runCommand(String raw) {
        block2: {
            class_746 class_7462 = QolModule.mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            String trimmed = ((Object)StringsKt.trim((CharSequence)raw)).toString();
            if (((CharSequence)trimmed).length() == 0) {
                return;
            }
            String command = StringsKt.removePrefix((String)trimmed, (CharSequence)"/");
            class_634 class_6342 = player.field_3944;
            if (class_6342 == null) break block2;
            class_6342.method_45730(command);
        }
    }

    private final void addCommandHotkey() {
        String last;
        if (!((Collection)commandHotkeys).isEmpty() && ((CharSequence)(last = ((Object)StringsKt.trim((CharSequence)((CommandHotkeyValue)((CommandHotkeySetting)CollectionsKt.last(commandHotkeys)).getValue()).getCommand())).toString())).length() == 0) {
            ChatUtils.sendMessage("Fill the last command before adding another.");
            return;
        }
        int nextIndex = commandHotkeys.size() + 1;
        CommandHotkeySetting setting = new CommandHotkeySetting("Command Hotkey " + nextIndex, "Keybind + command row " + nextIndex + ".", new CommandHotkeyValue(null, null, 3, null));
        commandHotkeys.add(setting);
        Setting[] settingArray = new Setting[]{setting};
        this.addSetting(settingArray);
    }

    private final int loadSavedHotkeyCount() {
        String text;
        Object $this$loadSavedHotkeyCount_u24lambda_u240;
        File file = QolModule.mc.field_1697;
        if (file == null) {
            return 1;
        }
        File dir = file;
        File file2 = new File(dir, "config/cobalt/addons.json");
        if (!file2.exists()) {
            return 1;
        }
        Object object = this;
        try {
            $this$loadSavedHotkeyCount_u24lambda_u240 = object;
            boolean bl = false;
            $this$loadSavedHotkeyCount_u24lambda_u240 = Result.constructor-impl((Object)FilesKt.readText$default((File)file2, null, (int)1, null));
        }
        catch (Throwable bl) {
            $this$loadSavedHotkeyCount_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$loadSavedHotkeyCount_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)object) ? null : object);
        String string2 = string != null ? ((Object)StringsKt.trim((CharSequence)string)).toString() : null;
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(text = string2)).length() == 0) {
            return 1;
        }
        Object object2 = this;
        try {
            QolModule $this$loadSavedHotkeyCount_u24lambda_u241 = object2;
            boolean bl = false;
            JsonArray json = JsonParser.parseString((String)text).getAsJsonArray();
            int maxIndex = 0;
            Intrinsics.checkNotNull((Object)json);
            Iterable $this$forEach$iv = (Iterable)json;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                JsonElement addonEl = (JsonElement)element$iv;
                boolean bl2 = false;
                JsonObject addonObj = addonEl.getAsJsonObject();
                JsonElement jsonElement = addonObj.get("addon");
                if (!Intrinsics.areEqual((Object)(jsonElement != null ? jsonElement.getAsString() : null), (Object)"cobalt")) continue;
                JsonArray jsonArray = addonObj.getAsJsonArray("modules");
                if (jsonArray == null) continue;
                Iterable $this$forEach$iv2 = (Iterable)jsonArray;
                boolean $i$f$forEach2 = false;
                for (Object element$iv2 : $this$forEach$iv2) {
                    JsonObject settingsObj;
                    JsonElement moduleEl = (JsonElement)element$iv2;
                    boolean bl3 = false;
                    JsonObject moduleObj = moduleEl.getAsJsonObject();
                    JsonElement jsonElement2 = moduleObj.get("name");
                    if (!Intrinsics.areEqual((Object)(jsonElement2 != null ? jsonElement2.getAsString() : null), (Object)"QoL") || moduleObj.getAsJsonObject("settings") == null) continue;
                    Set set = settingsObj.entrySet();
                    Intrinsics.checkNotNullExpressionValue((Object)set, (String)"entrySet(...)");
                    Iterable $this$forEach$iv3 = set;
                    boolean $i$f$forEach3 = false;
                    for (Object element$iv3 : $this$forEach$iv3) {
                        MatchResult match;
                        Map.Entry entry = (Map.Entry)element$iv3;
                        boolean bl4 = false;
                        Intrinsics.checkNotNull((Object)entry);
                        String key = (String)entry.getKey();
                        Regex regex = new Regex("^Command Hotkey (\\d+)$");
                        Intrinsics.checkNotNull((Object)key);
                        if (Regex.find$default((Regex)regex, (CharSequence)key, (int)0, (int)2, null) == null) continue;
                        Integer n = StringsKt.toIntOrNull((String)((String)match.getGroupValues().get(1)));
                        if (n == null) {
                            continue;
                        }
                        int idx = n;
                        if (idx <= maxIndex) continue;
                        maxIndex = idx;
                    }
                }
            }
            object = Result.constructor-impl((Object)RangesKt.coerceAtLeast((int)maxIndex, (int)1));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        object = 1;
        return ((Number)(Result.isFailure-impl((Object)object2) ? object : object2)).intValue();
    }

    private static final Unit addHotkey$lambda$0() {
        INSTANCE.addCommandHotkey();
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        commandHotkeys = new ArrayList();
        enabled = new CheckboxSetting("Enabled", "Enable QoL features.", false);
        commandHotkeysInfo = new InfoSetting("Command Hotkeys", "Bind keys to run chat commands.", InfoType.INFO);
        addHotkey = new ActionSetting("Add Hotkey", "Add a new command hotkey.", "+", null, QolModule::addHotkey$lambda$0, 8, null);
        Setting[] settingArray = new Setting[3];
        settingArray[0] = enabled;
        settingArray[1] = commandHotkeysInfo;
        settingArray[2] = addHotkey;
        INSTANCE.addSetting(settingArray);
        int initialCount = INSTANCE.loadSavedHotkeyCount();
        int n = 0;
        while (n < initialCount) {
            int it = n++;
            boolean bl = false;
            INSTANCE.addCommandHotkey();
        }
        EventBus.register(INSTANCE);
    }
}

