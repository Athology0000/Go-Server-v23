/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  net.minecraft.class_7923
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_7923;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningProfitTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001!B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0015\u001a\u00020\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001c\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR \u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e0\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 \u00a8\u0006\""}, d2={"Lorg/cobalt/internal/mining/MiningCoinPopupModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "event", "", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "onNvg", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "", "coins", "", "formatValue", "(D)Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "fontSize", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "", "Lorg/cobalt/internal/mining/MiningCoinPopupModule$CoinPopup;", "popups", "Ljava/util/List;", "", "lastNvgMs", "J", "", "BLOCK_TYPE_TO_BAZAAR_KEY", "Ljava/util/Map;", "CoinPopup", "cobalt"})
public final class MiningCoinPopupModule
extends Module {
    @NotNull
    public static final MiningCoinPopupModule INSTANCE = new MiningCoinPopupModule();
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Show floating +coins text when the macro mines a block.", false);
    @NotNull
    private static final SliderSetting fontSize = new SliderSetting("Font Size", "Size of the popup text.", 14.0, 12.0, 20.0, 0.0, 32, null);
    @NotNull
    private static final List<CoinPopup> popups;
    private static long lastNvgMs;
    @NotNull
    private static final Map<String, String> BLOCK_TYPE_TO_BAZAAR_KEY;

    private MiningCoinPopupModule() {
        super("Mining Coin Popups");
    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() || !MiningMacroModule.INSTANCE.isActive()) {
            return;
        }
        if (!event.getNewBlock().method_26215()) {
            return;
        }
        String string = class_7923.field_41175.method_10221((Object)event.getOldBlock().method_26204()).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String blockId = string;
        String string2 = MiningBlockRegistry.INSTANCE.getBLOCK_ID_TO_TYPE().get(blockId);
        if (string2 == null) {
            return;
        }
        String blockType = string2;
        String string3 = BLOCK_TYPE_TO_BAZAAR_KEY.get(blockType);
        if (string3 == null) {
            return;
        }
        String bazaarKey = string3;
        Double d = MiningProfitTracker.INSTANCE.getPriceForKey(bazaarKey);
        if (d == null) {
            return;
        }
        double price = d;
        if (price <= 0.0) {
            return;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        float x = (float)mc.method_22683().method_4480() / 2.0f + (Random.Default.nextFloat() * 120.0f - 60.0f);
        float y = (float)mc.method_22683().method_4507() * 0.8f;
        popups.add(new CoinPopup(this.formatValue(price), x, y, System.currentTimeMillis()));
    }

    @SubscribeEvent
    public final void onNvg(@NotNull NvgEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        long now = System.currentTimeMillis();
        float delta = lastNvgMs == 0L ? 0.0f : RangesKt.coerceAtMost((float)((float)(now - lastNvgMs) / 1000.0f), (float)0.1f);
        lastNvgMs = now;
        if (!((Boolean)enabled.getValue()).booleanValue() || popups.isEmpty()) {
            return;
        }
        class_1041 class_10412 = class_310.method_1551().method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float screenWidth = window.method_4480();
        float screenHeight = window.method_4507();
        NVGRenderer.INSTANCE.beginFrame(screenWidth, screenHeight);
        Iterator<CoinPopup> iter = popups.iterator();
        while (iter.hasNext()) {
            CoinPopup popup = iter.next();
            long elapsed = now - popup.getStartMs();
            if (elapsed >= 2000L) {
                iter.remove();
                continue;
            }
            popup.setY(popup.getY() - 40.0f * delta);
            int alpha = RangesKt.coerceIn((int)((int)((1.0f - (float)elapsed / 2000.0f) * (float)255)), (int)0, (int)255);
            int color = alpha << 24 | 0x4CFF72;
            NVGRenderer.text$default(popup.getLabel(), popup.getStartX(), popup.getY(), (float)((Number)fontSize.getValue()).doubleValue(), color, null, 32, null);
        }
        NVGRenderer.INSTANCE.endFrame();
    }

    private final String formatValue(double coins) {
        String string;
        if (coins >= 1000000.0) {
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{coins / 1000000.0};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            string = "+" + string3 + "M";
        } else if (coins >= 1000.0) {
            String string4 = "%.1f";
            Object[] objectArray = new Object[]{coins / 1000.0};
            String string5 = String.format(string4, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
            string = "+" + string5 + "K";
        } else {
            string = "+" + (long)coins;
        }
        return string;
    }

    static {
        Setting[] settingArray = new Setting[]{enabled, fontSize};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
        popups = new ArrayList();
        settingArray = new Pair[]{TuplesKt.to((Object)"Mithril (Gray)", (Object)"mithril ore"), TuplesKt.to((Object)"Mithril (Dark)", (Object)"mithril ore"), TuplesKt.to((Object)"Mithril (Hot)", (Object)"mithril ore"), TuplesKt.to((Object)"Titanium", (Object)"titanium ore"), TuplesKt.to((Object)"Ruby Gemstone", (Object)"flawed ruby gem"), TuplesKt.to((Object)"Amber Gemstone", (Object)"flawed amber gem"), TuplesKt.to((Object)"Amethyst Gemstone", (Object)"flawed amethyst gem"), TuplesKt.to((Object)"Jade Gemstone", (Object)"flawed jade gem"), TuplesKt.to((Object)"Sapphire Gemstone", (Object)"flawed sapphire gem"), TuplesKt.to((Object)"Opal Gemstone", (Object)"flawed opal gem"), TuplesKt.to((Object)"Topaz Gemstone", (Object)"flawed topaz gem"), TuplesKt.to((Object)"Jasper Gemstone", (Object)"flawed jasper gem"), TuplesKt.to((Object)"Onyx Gemstone", (Object)"flawed onyx gem"), TuplesKt.to((Object)"Aquamarine Gemstone", (Object)"flawed aquamarine gem"), TuplesKt.to((Object)"Citrine Gemstone", (Object)"flawed citrine gem"), TuplesKt.to((Object)"Peridot Gemstone", (Object)"flawed peridot gem"), TuplesKt.to((Object)"Umber", (Object)"umber"), TuplesKt.to((Object)"Tungsten", (Object)"tungsten"), TuplesKt.to((Object)"Glacite", (Object)"glacite")};
        BLOCK_TYPE_TO_BAZAAR_KEY = MapsKt.mapOf((Pair[])settingArray);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\r\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J8\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001b\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010\u000eR\"\u0010\u0006\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b \u0010\u000e\"\u0004\b!\u0010\"R\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010#\u001a\u0004\b$\u0010\u0011\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/mining/MiningCoinPopupModule$CoinPopup;", "", "", "label", "", "startX", "y", "", "startMs", "<init>", "(Ljava/lang/String;FFJ)V", "component1", "()Ljava/lang/String;", "component2", "()F", "component3", "component4", "()J", "copy", "(Ljava/lang/String;FFJ)Lorg/cobalt/internal/mining/MiningCoinPopupModule$CoinPopup;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getLabel", "F", "getStartX", "getY", "setY", "(F)V", "J", "getStartMs", "cobalt"})
    private static final class CoinPopup {
        @NotNull
        private final String label;
        private final float startX;
        private float y;
        private final long startMs;

        public CoinPopup(@NotNull String label, float startX, float y, long startMs) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            this.label = label;
            this.startX = startX;
            this.y = y;
            this.startMs = startMs;
        }

        @NotNull
        public final String getLabel() {
            return this.label;
        }

        public final float getStartX() {
            return this.startX;
        }

        public final float getY() {
            return this.y;
        }

        public final void setY(float f) {
            this.y = f;
        }

        public final long getStartMs() {
            return this.startMs;
        }

        @NotNull
        public final String component1() {
            return this.label;
        }

        public final float component2() {
            return this.startX;
        }

        public final float component3() {
            return this.y;
        }

        public final long component4() {
            return this.startMs;
        }

        @NotNull
        public final CoinPopup copy(@NotNull String label, float startX, float y, long startMs) {
            Intrinsics.checkNotNullParameter((Object)label, (String)"label");
            return new CoinPopup(label, startX, y, startMs);
        }

        public static /* synthetic */ CoinPopup copy$default(CoinPopup coinPopup, String string, float f, float f2, long l, int n, Object object) {
            if ((n & 1) != 0) {
                string = coinPopup.label;
            }
            if ((n & 2) != 0) {
                f = coinPopup.startX;
            }
            if ((n & 4) != 0) {
                f2 = coinPopup.y;
            }
            if ((n & 8) != 0) {
                l = coinPopup.startMs;
            }
            return coinPopup.copy(string, f, f2, l);
        }

        @NotNull
        public String toString() {
            return "CoinPopup(label=" + this.label + ", startX=" + this.startX + ", y=" + this.y + ", startMs=" + this.startMs + ")";
        }

        public int hashCode() {
            int result = this.label.hashCode();
            result = result * 31 + Float.hashCode(this.startX);
            result = result * 31 + Float.hashCode(this.y);
            result = result * 31 + Long.hashCode(this.startMs);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CoinPopup)) {
                return false;
            }
            CoinPopup coinPopup = (CoinPopup)other;
            if (!Intrinsics.areEqual((Object)this.label, (Object)coinPopup.label)) {
                return false;
            }
            if (Float.compare(this.startX, coinPopup.startX) != 0) {
                return false;
            }
            if (Float.compare(this.y, coinPopup.y) != 0) {
                return false;
            }
            return this.startMs == coinPopup.startMs;
        }
    }
}

