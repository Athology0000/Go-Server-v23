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
 *  kotlin.jvm.internal.Ref$FloatRef
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_355
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_355;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.garden.managers.PestManager;
import org.cobalt.internal.garden.managers.PestTabListParser;
import org.cobalt.internal.garden.managers.TabListData;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ\u000f\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\r\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0013\u001a\u00020\u00128\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0019\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001c\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0016\u0010\u001e\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001dR\u0016\u0010\u001f\u001a\u00020\u00128\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0014R\u0014\u0010 \u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0014\u0010\"\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\"\u0010!R\u0014\u0010#\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b#\u0010!R\u0014\u0010$\u001a\u00020\f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b$\u0010!R\u0017\u0010&\u001a\u00020%8\u0006\u00a2\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b(\u0010)\u00a8\u0006*"}, d2={"Lorg/cobalt/internal/garden/GardenHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "", "isInGarden", "()Z", "", "visibleRowCount", "()F", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "alivePests", "I", "", "", "infestedPlots", "Ljava/util/List;", "bonusActive", "Z", "", "cooldownDeadline", "J", "lastSpawnedAt", "prevAliveCount", "ROW_H", "F", "FONT_SZ", "PAD", "W", "Lorg/cobalt/api/hud/HudElement;", "pestHud", "Lorg/cobalt/api/hud/HudElement;", "getPestHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
public final class GardenHudModule
extends Module {
    @NotNull
    public static final GardenHudModule INSTANCE = new GardenHudModule();
    @NotNull
    private static final class_310 mc;
    private static int alivePests;
    @NotNull
    private static List<String> infestedPlots;
    private static boolean bonusActive;
    private static long cooldownDeadline;
    private static long lastSpawnedAt;
    private static int prevAliveCount;
    private static final float ROW_H = 15.0f;
    private static final float FONT_SZ = 10.0f;
    private static final float PAD = 8.0f;
    private static final float W = 190.0f;
    @NotNull
    private static final HudElement pestHud;

    private GardenHudModule() {
        super("Garden HUD");
    }

    @NotNull
    public final HudElement getPestHud() {
        return pestHud;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!pestHud.getEnabled()) {
            return;
        }
        if (!this.isInGarden()) {
            return;
        }
        TabListData data = PestTabListParser.INSTANCE.parse();
        if (data.getAlivePests() > prevAliveCount) {
            lastSpawnedAt = System.currentTimeMillis();
        }
        prevAliveCount = data.getAlivePests();
        alivePests = data.getAlivePests();
        infestedPlots = data.getInfestedPlots();
        bonusActive = data.getBonusActive();
        if (data.getCooldownSeconds() > 0) {
            long newDeadline = System.currentTimeMillis() + (long)data.getCooldownSeconds() * 1000L;
            if (newDeadline > cooldownDeadline) {
                cooldownDeadline = newDeadline;
            }
        } else {
            cooldownDeadline = 0L;
        }
    }

    private final boolean isInGarden() {
        String string;
        Object object;
        Object header;
        String string2;
        CharSequence charSequence;
        Object object2;
        class_355 class_3552 = GardenHudModule.mc.field_1705.method_1750();
        TabOverlayAccessor tabOverlayAccessor = class_3552 instanceof TabOverlayAccessor ? (TabOverlayAccessor)class_3552 : null;
        if (tabOverlayAccessor == null) {
            return false;
        }
        TabOverlayAccessor overlay = tabOverlayAccessor;
        Object object3 = overlay.getHeader();
        if (object3 == null || (object3 = object3.getString()) == null || (object3 = (object2 = new Regex("\u00a7[0-9a-fk-or]")).replace(charSequence = (CharSequence)object3, string2 = "")) == null) {
            object3 = header = "";
        }
        if ((object = overlay.getFooter()) == null || (object = object.getString()) == null || (object = (string2 = new Regex("\u00a7[0-9a-fk-or]")).replace((CharSequence)(object2 = (CharSequence)object), string = "")) == null) {
            object = "";
        }
        Object footer = object;
        return StringsKt.contains((CharSequence)((CharSequence)header), (CharSequence)"Garden", (boolean)true) || StringsKt.contains((CharSequence)((CharSequence)footer), (CharSequence)"Garden", (boolean)true);
    }

    private final float visibleRowCount() {
        float f;
        long now = System.currentTimeMillis();
        float count = 3.0f;
        if (!((Collection)infestedPlots).isEmpty()) {
            f = count;
            count = f + 1.0f;
        }
        if (lastSpawnedAt > 0L) {
            f = count;
            count = f + 1.0f;
        }
        if (PestManager.INSTANCE.getCleaningCooldownUntil() > now) {
            f = count;
            count = f + 1.0f;
        }
        return count;
    }

    private static final void pestHud$lambda$0$2$row(float $x, Ref.FloatRef rowY, String label, String value, int valueColor) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        NVGRenderer.text$default(label, $x + 8.0f, rowY.element, 10.0f, theme.getText(), null, 32, null);
        NVGRenderer.text$default(value, $x + 8.0f + 110.0f, rowY.element, 10.0f, valueColor, null, 32, null);
        rowY.element += 15.0f;
    }

    private static final float pestHud$lambda$0$0() {
        return 206.0f;
    }

    private static final float pestHud$lambda$0$1() {
        return 16.0f + 15.0f * INSTANCE.visibleRowCount();
    }

    private static final Unit pestHud$lambda$0$2(float x, float y, float f) {
        String string;
        Object[] objectArray;
        String string2;
        if (!INSTANCE.isInGarden()) {
            return Unit.INSTANCE;
        }
        long now = System.currentTimeMillis();
        float cdRemaining = RangesKt.coerceAtLeast((float)((float)(cooldownDeadline - now) / 1000.0f), (float)0.0f);
        float internalCdRemaining = RangesKt.coerceAtLeast((float)((float)(PestManager.INSTANCE.getCleaningCooldownUntil() - now) / 1000.0f), (float)0.0f);
        float lastSpawnedSec = lastSpawnedAt > 0L ? (float)(now - lastSpawnedAt) / 1000.0f : -1.0f;
        Ref.FloatRef rowY = new Ref.FloatRef();
        rowY.element = y + 8.0f;
        int green = -11141291;
        int yellow = -171;
        int red = -43691;
        int gray = -5592406;
        GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Curr Pest:", String.valueOf(alivePests), alivePests > 0 ? red : green);
        if (cdRemaining > 0.0f) {
            string2 = "%.0fs";
            objectArray = new Object[]{Float.valueOf(cdRemaining)};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        } else {
            string = "Ready";
        }
        GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Pest Cooldown:", string, cdRemaining > 0.0f ? yellow : green);
        if (!((Collection)infestedPlots).isEmpty()) {
            GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Infested Plots:", String.valueOf(infestedPlots.size()), red);
        }
        if (lastSpawnedSec >= 0.0f) {
            string2 = "%.0fs ago";
            objectArray = new Object[]{Float.valueOf(lastSpawnedSec)};
            String string4 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"format(...)");
            GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Last Spawned:", string4, gray);
        }
        GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Farming Bonus:", bonusActive ? "Active" : "Inactive", bonusActive ? green : gray);
        if (internalCdRemaining > 0.0f) {
            string2 = "%.0fs";
            objectArray = new Object[]{Float.valueOf(internalCdRemaining)};
            String string5 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
            GardenHudModule.pestHud$lambda$0$2$row(x, rowY, "Internal CD:", string5, yellow);
        }
        return Unit.INSTANCE;
    }

    private static final Unit pestHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        $this$hudElement.width((Function0<Float>)((Function0)GardenHudModule::pestHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)GardenHudModule::pestHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)GardenHudModule::pestHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        infestedPlots = CollectionsKt.emptyList();
        EventBus.register(INSTANCE);
        pestHud = HudModuleDSLKt.hudElement(INSTANCE, "garden-pest-hud", "Garden Pest HUD", "Shows pest cooldown and status in the Garden", (Function1<? super HudElementBuilder, Unit>)((Function1)GardenHudModule::pestHud$lambda$0));
    }
}

