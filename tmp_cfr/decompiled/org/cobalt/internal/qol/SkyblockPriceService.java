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
 *  kotlin.collections.MapsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.qol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c0\u0002\u0018\u00002\u00020\u0001:\u0001*B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0012\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0011\u001a\u00020\t\u00a2\u0006\u0004\b\u0012\u0010\rJ\u0015\u0010\u0014\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0016\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0018\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0017J\u000f\u0010\u0019\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0017J\u0019\u0010\u001c\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u001a\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dR\"\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\"\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000e0\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010 R\"\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010 R\u0016\u0010#\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010%\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010$R\u0014\u0010'\u001a\u00020&8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010(R\u0014\u0010)\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b)\u0010$\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/qol/SkyblockPriceService;", "", "<init>", "()V", "", "marketRefreshSeconds", "", "refreshIfNeeded", "(J)V", "", "apiId", "", "getLowestBin", "(Ljava/lang/String;)Ljava/lang/Double;", "Lorg/cobalt/internal/qol/SkyblockPriceService$BazaarQuote;", "getBazaarQuote", "(Ljava/lang/String;)Lorg/cobalt/internal/qol/SkyblockPriceService$BazaarQuote;", "internalId", "getNpcSellPrice", "", "hasBazaarQuote", "(Ljava/lang/String;)Z", "refreshLowestBins", "()Z", "refreshBazaar", "refreshNpcSellPrices", "url", "Lcom/google/gson/JsonObject;", "fetchJsonObject", "(Ljava/lang/String;)Lcom/google/gson/JsonObject;", "", "lowestBinPrices", "Ljava/util/Map;", "bazaarQuotes", "npcSellPrices", "lastMarketRefreshMs", "J", "lastNpcRefreshMs", "Ljava/util/concurrent/atomic/AtomicBoolean;", "refreshInFlight", "Ljava/util/concurrent/atomic/AtomicBoolean;", "NPC_REFRESH_INTERVAL_MS", "BazaarQuote", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSkyblockPriceService.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SkyblockPriceService.kt\norg/cobalt/internal/qol/SkyblockPriceService\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,115:1\n1#2:116\n*E\n"})
public final class SkyblockPriceService {
    @NotNull
    public static final SkyblockPriceService INSTANCE = new SkyblockPriceService();
    @NotNull
    private static volatile Map<String, Double> lowestBinPrices = MapsKt.emptyMap();
    @NotNull
    private static volatile Map<String, BazaarQuote> bazaarQuotes = MapsKt.emptyMap();
    @NotNull
    private static volatile Map<String, Double> npcSellPrices = MapsKt.emptyMap();
    private static volatile long lastMarketRefreshMs;
    private static volatile long lastNpcRefreshMs;
    @NotNull
    private static final AtomicBoolean refreshInFlight;
    private static final long NPC_REFRESH_INTERVAL_MS = 21600000L;

    private SkyblockPriceService() {
    }

    public final void refreshIfNeeded(long marketRefreshSeconds) {
        Thread thread;
        long now = System.currentTimeMillis();
        long marketAge = now - lastMarketRefreshMs;
        long npcAge = now - lastNpcRefreshMs;
        long marketIntervalMs = RangesKt.coerceAtLeast((long)marketRefreshSeconds, (long)30L) * 1000L;
        long npcIntervalMs = 21600000L;
        if (marketAge < marketIntervalMs && npcAge < npcIntervalMs) {
            return;
        }
        if (!refreshInFlight.compareAndSet(false, true)) {
            return;
        }
        Thread it = thread = new Thread(() -> SkyblockPriceService.refreshIfNeeded$lambda$0(marketIntervalMs, npcIntervalMs), "Cobalt-PriceRefresh");
        boolean bl = false;
        it.setDaemon(true);
        it.start();
    }

    @Nullable
    public final Double getLowestBin(@NotNull String apiId) {
        Intrinsics.checkNotNullParameter((Object)apiId, (String)"apiId");
        return lowestBinPrices.get(apiId);
    }

    @Nullable
    public final BazaarQuote getBazaarQuote(@NotNull String apiId) {
        Intrinsics.checkNotNullParameter((Object)apiId, (String)"apiId");
        return bazaarQuotes.get(apiId);
    }

    @Nullable
    public final Double getNpcSellPrice(@NotNull String internalId) {
        Intrinsics.checkNotNullParameter((Object)internalId, (String)"internalId");
        return npcSellPrices.get(internalId);
    }

    public final boolean hasBazaarQuote(@NotNull String apiId) {
        Intrinsics.checkNotNullParameter((Object)apiId, (String)"apiId");
        return bazaarQuotes.containsKey(apiId);
    }

    private final boolean refreshLowestBins() {
        JsonObject jsonObject = this.fetchJsonObject("https://moulberry.codes/lowestbin.json");
        if (jsonObject == null) {
            return false;
        }
        JsonObject root = jsonObject;
        HashMap parsed = new HashMap(root.size());
        for (Map.Entry entry : root.entrySet()) {
            Intrinsics.checkNotNull((Object)entry);
            String key = (String)entry.getKey();
            JsonElement value = (JsonElement)entry.getValue();
            double price = value.getAsDouble();
            if (!(price >= 0.0)) continue;
            ((Map)parsed).put(key, price);
        }
        lowestBinPrices = parsed;
        return true;
    }

    private final boolean refreshBazaar() {
        JsonObject jsonObject = this.fetchJsonObject("https://api.hypixel.net/v2/skyblock/bazaar");
        if (jsonObject == null) {
            return false;
        }
        JsonObject root = jsonObject;
        JsonObject jsonObject2 = root.getAsJsonObject("products");
        if (jsonObject2 == null) {
            return false;
        }
        JsonObject products = jsonObject2;
        HashMap parsed = new HashMap(products.size());
        for (Map.Entry entry : products.entrySet()) {
            JsonObject quickStatus;
            Intrinsics.checkNotNull((Object)entry);
            String productId = (String)entry.getKey();
            JsonElement productValue = (JsonElement)entry.getValue();
            if (productValue.getAsJsonObject().getAsJsonObject("quick_status") == null) continue;
            JsonElement jsonElement = quickStatus.get("buyPrice");
            Double buyPrice = jsonElement != null ? Double.valueOf(jsonElement.getAsDouble()) : null;
            JsonElement jsonElement2 = quickStatus.get("sellPrice");
            if (jsonElement2 == null) {
                continue;
            }
            double sellPrice = jsonElement2.getAsDouble();
            ((Map)parsed).put(productId, new BazaarQuote(buyPrice, sellPrice));
        }
        bazaarQuotes = parsed;
        return true;
    }

    private final boolean refreshNpcSellPrices() {
        JsonObject jsonObject = this.fetchJsonObject("https://api.hypixel.net/v2/resources/skyblock/items");
        if (jsonObject == null) {
            return false;
        }
        JsonObject root = jsonObject;
        JsonArray jsonArray = root.getAsJsonArray("items");
        if (jsonArray == null) {
            return false;
        }
        JsonArray items = jsonArray;
        HashMap parsed = new HashMap(items.size());
        Iterator iterator = items.iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            JsonElement element = (JsonElement)iterator2.next();
            JsonObject item = element.getAsJsonObject();
            Object object = item.get("id");
            if (object == null || (object = object.getAsString()) == null) continue;
            Object id = object;
            JsonElement jsonElement = item.get("npc_sell_price");
            if (jsonElement == null) {
                continue;
            }
            double npcSellPrice = jsonElement.getAsDouble();
            ((Map)parsed).put(id, npcSellPrice);
        }
        npcSellPrices = parsed;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final JsonObject fetchJsonObject(String url) {
        Object object;
        Object object2 = this;
        try {
            SkyblockPriceService $this$fetchJsonObject_u24lambda_u240 = object2;
            boolean bl = false;
            URLConnection uRLConnection = new URI(url).toURL().openConnection();
            Intrinsics.checkNotNull((Object)uRLConnection, (String)"null cannot be cast to non-null type java.net.HttpURLConnection");
            HttpURLConnection connection = (HttpURLConnection)uRLConnection;
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Cobalt");
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(20000);
            InputStream inputStream = connection.getInputStream();
            Intrinsics.checkNotNullExpressionValue((Object)inputStream, (String)"getInputStream(...)");
            Closeable closeable = inputStream;
            Object object3 = Charsets.UTF_8;
            Reader reader = new InputStreamReader((InputStream)closeable, (Charset)object3);
            int n = 8192;
            closeable = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, n);
            object3 = null;
            try {
                BufferedReader reader2 = (BufferedReader)closeable;
                boolean bl2 = false;
                reader = JsonParser.parseReader((Reader)reader2).getAsJsonObject();
            }
            catch (Throwable throwable) {
                object3 = throwable;
                throw throwable;
            }
            finally {
                CloseableKt.closeFinally((Closeable)closeable, (Throwable)object3);
            }
            object = Result.constructor-impl((Object)reader);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (JsonObject)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void refreshIfNeeded$lambda$0(long $marketIntervalMs, long $npcIntervalMs) {
        try {
            long currentNow = System.currentTimeMillis();
            if (currentNow - lastMarketRefreshMs >= $marketIntervalMs) {
                boolean refreshedLowestBins = INSTANCE.refreshLowestBins();
                boolean refreshedBazaar = INSTANCE.refreshBazaar();
                if (refreshedLowestBins || refreshedBazaar) {
                    lastMarketRefreshMs = System.currentTimeMillis();
                }
            }
            if (currentNow - lastNpcRefreshMs >= $npcIntervalMs && INSTANCE.refreshNpcSellPrices()) {
                lastNpcRefreshMs = System.currentTimeMillis();
            }
        }
        finally {
            refreshInFlight.set(false);
        }
    }

    static {
        refreshInFlight = new AtomicBoolean(false);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ&\u0010\u000b\u001a\u00020\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000b\u0010\fJ\u001b\u0010\u000f\u001a\u00020\u000e2\b\u0010\r\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0015\u001a\u00020\u0014H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0019\u001a\u0004\b\u001a\u0010\n\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/qol/SkyblockPriceService$BazaarQuote;", "", "", "buyPrice", "sellPrice", "<init>", "(Ljava/lang/Double;D)V", "component1", "()Ljava/lang/Double;", "component2", "()D", "copy", "(Ljava/lang/Double;D)Lorg/cobalt/internal/qol/SkyblockPriceService$BazaarQuote;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Ljava/lang/Double;", "getBuyPrice", "D", "getSellPrice", "cobalt"})
    public static final class BazaarQuote {
        @Nullable
        private final Double buyPrice;
        private final double sellPrice;

        public BazaarQuote(@Nullable Double buyPrice, double sellPrice) {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        @Nullable
        public final Double getBuyPrice() {
            return this.buyPrice;
        }

        public final double getSellPrice() {
            return this.sellPrice;
        }

        @Nullable
        public final Double component1() {
            return this.buyPrice;
        }

        public final double component2() {
            return this.sellPrice;
        }

        @NotNull
        public final BazaarQuote copy(@Nullable Double buyPrice, double sellPrice) {
            return new BazaarQuote(buyPrice, sellPrice);
        }

        public static /* synthetic */ BazaarQuote copy$default(BazaarQuote bazaarQuote, Double d, double d2, int n, Object object) {
            if ((n & 1) != 0) {
                d = bazaarQuote.buyPrice;
            }
            if ((n & 2) != 0) {
                d2 = bazaarQuote.sellPrice;
            }
            return bazaarQuote.copy(d, d2);
        }

        @NotNull
        public String toString() {
            return "BazaarQuote(buyPrice=" + this.buyPrice + ", sellPrice=" + this.sellPrice + ")";
        }

        public int hashCode() {
            int result = this.buyPrice == null ? 0 : ((Object)this.buyPrice).hashCode();
            result = result * 31 + Double.hashCode(this.sellPrice);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BazaarQuote)) {
                return false;
            }
            BazaarQuote bazaarQuote = (BazaarQuote)other;
            if (!Intrinsics.areEqual((Object)this.buyPrice, (Object)bazaarQuote.buyPrice)) {
                return false;
            }
            return Double.compare(this.sellPrice, bazaarQuote.sellPrice) == 0;
        }
    }
}

