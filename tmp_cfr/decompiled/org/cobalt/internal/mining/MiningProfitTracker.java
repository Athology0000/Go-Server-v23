/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_7923
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_7923;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.cobalt.internal.mining.MiningMacroModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010%\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000b\u0010\u0003J\r\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\f\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0017\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0010H\u0007\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u000f\u0010\u0014\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0003R$\u0010\u0016\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\f8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u000eR$\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\f8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u0019\u0010\u0017\u001a\u0004\b\u001a\u0010\u000eR \u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\b0\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0016\u0010\u001e\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0017R\u0014\u0010\u001f\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0017R \u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060 8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u001d\u00a8\u0006\""}, d2={"Lorg/cobalt/internal/mining/MiningProfitTracker;", "", "<init>", "()V", "", "ensureInitialized", "", "key", "", "getPriceForKey", "(Ljava/lang/String;)Ljava/lang/Double;", "resetSession", "", "runtimeMs", "()J", "coinsPerHour", "Lorg/cobalt/api/event/impl/client/BlockChangeEvent;", "event", "onBlockChange", "(Lorg/cobalt/api/event/impl/client/BlockChangeEvent;)V", "refreshBazaarIfNeeded", "value", "sessionCoins", "J", "getSessionCoins", "sessionStartTime", "getSessionStartTime", "", "bazaarPrices", "Ljava/util/Map;", "lastBazaarRefresh", "BAZAAR_REFRESH_INTERVAL_MS", "", "BLOCK_TYPE_TO_BAZAAR_KEY", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningProfitTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningProfitTracker.kt\norg/cobalt/internal/mining/MiningProfitTracker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,104:1\n1#2:105\n*E\n"})
public final class MiningProfitTracker {
    @NotNull
    public static final MiningProfitTracker INSTANCE = new MiningProfitTracker();
    private static volatile long sessionCoins;
    private static volatile long sessionStartTime;
    @NotNull
    private static final Map<String, Double> bazaarPrices;
    private static volatile long lastBazaarRefresh;
    private static final long BAZAAR_REFRESH_INTERVAL_MS;
    @NotNull
    private static final Map<String, String> BLOCK_TYPE_TO_BAZAAR_KEY;

    private MiningProfitTracker() {
    }

    public final long getSessionCoins() {
        return sessionCoins;
    }

    public final long getSessionStartTime() {
        return sessionStartTime;
    }

    public final void ensureInitialized() {
    }

    @Nullable
    public final Double getPriceForKey(@NotNull String key) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        return bazaarPrices.get(key);
    }

    public final void resetSession() {
        sessionCoins = 0L;
        sessionStartTime = System.currentTimeMillis();
        this.refreshBazaarIfNeeded();
    }

    public final long runtimeMs() {
        return sessionStartTime == 0L ? 0L : RangesKt.coerceAtLeast((long)(System.currentTimeMillis() - sessionStartTime), (long)0L);
    }

    public final long coinsPerHour() {
        double elapsedHours = (double)this.runtimeMs() / 3600000.0;
        if (elapsedHours < 0.001) {
            return 0L;
        }
        return (long)((double)sessionCoins / elapsedHours);
    }

    @SubscribeEvent
    public final void onBlockChange(@NotNull BlockChangeEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!event.getNewBlock().method_26215()) {
            return;
        }
        if (!MiningMacroModule.INSTANCE.isActive()) {
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
        Double d = bazaarPrices.get(bazaarKey);
        if (d == null) {
            return;
        }
        double price = d;
        if (price <= 0.0) {
            return;
        }
        sessionCoins += (long)price;
    }

    private final void refreshBazaarIfNeeded() {
        Thread thread;
        long now = System.currentTimeMillis();
        if (now - lastBazaarRefresh < BAZAAR_REFRESH_INTERVAL_MS) {
            return;
        }
        lastBazaarRefresh = now;
        Thread it = thread = new Thread(MiningProfitTracker::refreshBazaarIfNeeded$lambda$0, "mining-bazaar-refresh");
        boolean bl = false;
        it.setDaemon(true);
        thread.start();
    }

    private static final void refreshBazaarIfNeeded$lambda$0$0(Map $prices) {
        bazaarPrices.clear();
        bazaarPrices.putAll($prices);
    }

    private static final void refreshBazaarIfNeeded$lambda$0() {
        try {
            URL uRL = new URL("https://api.hypixel.net/v2/skyblock/bazaar");
            Charset charset = Charsets.UTF_8;
            byte[] byArray = TextStreamsKt.readBytes((URL)uRL);
            String json = new String(byArray, charset);
            JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
            JsonObject jsonObject = root.getAsJsonObject("products");
            if (jsonObject == null) {
                return;
            }
            JsonObject prods = jsonObject;
            Map prices = new LinkedHashMap();
            for (Map.Entry entry : prods.entrySet()) {
                JsonElement jsonElement;
                Intrinsics.checkNotNull((Object)entry);
                String name = (String)entry.getKey();
                JsonElement data = (JsonElement)entry.getValue();
                JsonObject jsonObject2 = data.getAsJsonObject().getAsJsonObject("quick_status");
                if (jsonObject2 == null || (jsonElement = jsonObject2.get("buyPrice")) == null) continue;
                double buy = jsonElement.getAsDouble();
                Intrinsics.checkNotNull((Object)name);
                String string = name.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                prices.put(StringsKt.replace$default((String)string, (String)"_", (String)" ", (boolean)false, (int)4, null), buy);
            }
            class_310.method_1551().execute(() -> MiningProfitTracker.refreshBazaarIfNeeded$lambda$0$0(prices));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static {
        bazaarPrices = new LinkedHashMap();
        BAZAAR_REFRESH_INTERVAL_MS = 300000L;
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"Mithril (Gray)", (Object)"mithril ore"), TuplesKt.to((Object)"Mithril (Dark)", (Object)"mithril ore"), TuplesKt.to((Object)"Mithril (Hot)", (Object)"mithril ore"), TuplesKt.to((Object)"Titanium", (Object)"titanium ore"), TuplesKt.to((Object)"Ruby Gemstone", (Object)"flawed ruby gem"), TuplesKt.to((Object)"Amber Gemstone", (Object)"flawed amber gem"), TuplesKt.to((Object)"Amethyst Gemstone", (Object)"flawed amethyst gem"), TuplesKt.to((Object)"Jade Gemstone", (Object)"flawed jade gem"), TuplesKt.to((Object)"Sapphire Gemstone", (Object)"flawed sapphire gem"), TuplesKt.to((Object)"Opal Gemstone", (Object)"flawed opal gem"), TuplesKt.to((Object)"Topaz Gemstone", (Object)"flawed topaz gem"), TuplesKt.to((Object)"Jasper Gemstone", (Object)"flawed jasper gem"), TuplesKt.to((Object)"Onyx Gemstone", (Object)"flawed onyx gem"), TuplesKt.to((Object)"Aquamarine Gemstone", (Object)"flawed aquamarine gem"), TuplesKt.to((Object)"Citrine Gemstone", (Object)"flawed citrine gem"), TuplesKt.to((Object)"Peridot Gemstone", (Object)"flawed peridot gem"), TuplesKt.to((Object)"Umber", (Object)"umber"), TuplesKt.to((Object)"Tungsten", (Object)"tungsten"), TuplesKt.to((Object)"Glacite", (Object)"glacite")};
        BLOCK_TYPE_TO_BAZAAR_KEY = MapsKt.mapOf((Pair[])pairArray);
        EventBus.register(INSTANCE);
        INSTANCE.refreshBazaarIfNeeded();
    }
}

