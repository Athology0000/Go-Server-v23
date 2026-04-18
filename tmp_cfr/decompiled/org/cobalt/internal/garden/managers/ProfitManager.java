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
 *  kotlin.Unit
 *  kotlin.collections.MapsKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Charsets
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

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
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010%\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u0015\u0010\t\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\r\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0003R\"\u0010\u0010\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\nR\"\u0010\u0015\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0015\u0010\u0011\u001a\u0004\b\u0016\u0010\u0013\"\u0004\b\u0017\u0010\nR\"\u0010\u0018\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\u0011\u001a\u0004\b\u0019\u0010\u0013\"\u0004\b\u001a\u0010\nR\"\u0010\u001b\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001b\u0010\u0011\u001a\u0004\b\u001c\u0010\u0013\"\u0004\b\u001d\u0010\nR \u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u001f0\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!R \u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u001f0\"8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010!\u00a8\u0006$"}, d2={"Lorg/cobalt/internal/garden/managers/ProfitManager;", "", "<init>", "()V", "", "reset", "fullReset", "", "amount", "addProfit", "(J)V", "", "message", "onChatMessage", "(Ljava/lang/String;)V", "refreshBazaarIfNeeded", "sessionProfit", "J", "getSessionProfit", "()J", "setSessionProfit", "dailyProfit", "getDailyProfit", "setDailyProfit", "lifetimeProfit", "getLifetimeProfit", "setLifetimeProfit", "lastBazaarRefresh", "getLastBazaarRefresh", "setLastBazaarRefresh", "", "", "bazaarPrices", "Ljava/util/Map;", "", "hardcodedPrices", "cobalt"})
public final class ProfitManager {
    @NotNull
    public static final ProfitManager INSTANCE = new ProfitManager();
    private static volatile long sessionProfit;
    private static volatile long dailyProfit;
    private static volatile long lifetimeProfit;
    private static volatile long lastBazaarRefresh;
    @NotNull
    private static final Map<String, Double> bazaarPrices;
    @NotNull
    private static final Map<String, Double> hardcodedPrices;

    private ProfitManager() {
    }

    public final long getSessionProfit() {
        return sessionProfit;
    }

    public final void setSessionProfit(long l) {
        sessionProfit = l;
    }

    public final long getDailyProfit() {
        return dailyProfit;
    }

    public final void setDailyProfit(long l) {
        dailyProfit = l;
    }

    public final long getLifetimeProfit() {
        return lifetimeProfit;
    }

    public final void setLifetimeProfit(long l) {
        lifetimeProfit = l;
    }

    public final long getLastBazaarRefresh() {
        return lastBazaarRefresh;
    }

    public final void setLastBazaarRefresh(long l) {
        lastBazaarRefresh = l;
    }

    public final void reset() {
        sessionProfit = 0L;
    }

    public final void fullReset() {
        sessionProfit = 0L;
        dailyProfit = 0L;
        lifetimeProfit = 0L;
    }

    public final void addProfit(long amount) {
        sessionProfit += amount;
        dailyProfit += amount;
        lifetimeProfit += amount;
    }

    public final void onChatMessage(@NotNull String message) {
        double d;
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        MatchResult matchResult = Regex.find$default((Regex)new Regex("\\+\\s*(\\d+)x?\\s+(.+)"), (CharSequence)message, (int)0, (int)2, null);
        if (matchResult == null) {
            return;
        }
        MatchResult m = matchResult;
        Long l = StringsKt.toLongOrNull((String)((String)m.getGroupValues().get(1)));
        if (l == null) {
            return;
        }
        long count = l;
        String string = ((Object)StringsKt.trim((CharSequence)((String)m.getGroupValues().get(2)))).toString().toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String item = string;
        Double d2 = bazaarPrices.get(item);
        if (d2 != null) {
            d = d2;
        } else {
            Double d3 = hardcodedPrices.get(item);
            if (d3 != null) {
                d = d3;
            } else {
                return;
            }
        }
        double price = d;
        this.addProfit((long)((double)count * price));
    }

    public final void refreshBazaarIfNeeded() {
        long interval = GardenConfig.INSTANCE.getBazaarRefreshSecs() * 1000L;
        if (System.currentTimeMillis() - lastBazaarRefresh < interval) {
            return;
        }
        lastBazaarRefresh = System.currentTimeMillis();
        GardenWorkerThread.INSTANCE.submit("bazaar-refresh", (Function0<Unit>)((Function0)ProfitManager::refreshBazaarIfNeeded$lambda$0));
    }

    private static final void refreshBazaarIfNeeded$lambda$0$0(Map $prices) {
        bazaarPrices.clear();
        bazaarPrices.putAll($prices);
    }

    private static final Unit refreshBazaarIfNeeded$lambda$0() {
        try {
            URL uRL = new URL("https://api.hypixel.net/v2/skyblock/bazaar");
            Charset charset = Charsets.UTF_8;
            byte[] byArray = TextStreamsKt.readBytes((URL)uRL);
            String json = new String(byArray, charset);
            JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
            JsonObject jsonObject = root.getAsJsonObject("products");
            if (jsonObject == null) {
                return Unit.INSTANCE;
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
            class_310.method_1551().execute(() -> ProfitManager.refreshBazaarIfNeeded$lambda$0$0(prices));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return Unit.INSTANCE;
    }

    static {
        bazaarPrices = new LinkedHashMap();
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"enchanted carrot", (Object)1000.0), TuplesKt.to((Object)"enchanted potato", (Object)1200.0), TuplesKt.to((Object)"enchanted wheat", (Object)800.0), TuplesKt.to((Object)"enchanted sugar cane", (Object)900.0), TuplesKt.to((Object)"enchanted cactus", (Object)1500.0)};
        hardcodedPrices = MapsKt.mapOf((Pair[])pairArray);
    }
}

