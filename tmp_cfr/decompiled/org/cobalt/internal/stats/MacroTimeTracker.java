/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.hud.modules.CommissionMacroModule;
import org.cobalt.internal.combat.CombatMacroModule;
import org.cobalt.internal.diana.DianaMacroModule;
import org.cobalt.internal.farming.FarmingMacroModule;
import org.cobalt.internal.garden.GardenMacroModule;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.pig.PigMacroModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0003LMNB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\u000b\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J/\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0011\u0010\u001a\u001a\u0004\u0018\u00010\u0019H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0015\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00190\u001cH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ+\u0010\"\u001a\b\u0012\u0004\u0012\u00020!0\u001c2\u0014\u0010 \u001a\u0010\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u001fH\u0002\u00a2\u0006\u0004\b\"\u0010#J\u000f\u0010$\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0017\u0010(\u001a\u00020\u00042\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b(\u0010)J\u000f\u0010*\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b*\u0010\u0003J\u001f\u0010+\u001a\u00020&*\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110\u001fH\u0002\u00a2\u0006\u0004\b+\u0010,R\u0014\u0010.\u001a\u00020-8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b.\u0010/R\u001c\u00102\u001a\n 1*\u0004\u0018\u000100008\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b2\u00103R\u0014\u00105\u001a\u0002048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b5\u00106R\u0014\u00108\u001a\u0002078\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b8\u00109R\u001a\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00190\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b:\u0010;R\u0016\u0010<\u001a\u00020\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b<\u0010=R\u0016\u0010>\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b>\u0010?R\u0016\u0010@\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b@\u0010?R\u0018\u0010A\u001a\u0004\u0018\u00010\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bA\u0010BR\u0016\u0010C\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u0010?R0\u0010F\u001a\u001e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110Dj\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0011`E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u0010GR0\u0010H\u001a\u001e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110Dj\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0011`E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010GRh\u0010I\u001aV\u0012\u0004\u0012\u00020\u0014\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110Dj\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0011`E0Dj*\u0012\u0004\u0012\u00020\u0014\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110Dj\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0011`E`E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010GR\u0014\u0010J\u001a\u00020\u00118\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bJ\u0010?R\u0014\u0010K\u001a\u00020\u00118\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bK\u0010?\u00a8\u0006O"}, d2={"Lorg/cobalt/internal/stats/MacroTimeTracker;", "", "<init>", "()V", "", "load", "Lorg/cobalt/internal/stats/MacroTimeTracker$Snapshot;", "snapshot", "()Lorg/cobalt/internal/stats/MacroTimeTracker$Snapshot;", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "", "saveIfNeeded", "syncNow", "(Z)V", "", "startMs", "endMs", "", "macroId", "elapsedMs", "accumulateInterval", "(JJLjava/lang/String;J)V", "Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDefinition;", "resolvePrimaryMacro", "()Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDefinition;", "", "resolveActiveMacros", "()Ljava/util/List;", "", "raw", "Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDuration;", "buildMacroDurations", "(Ljava/util/Map;)Ljava/util/List;", "currentDateKey", "()Ljava/lang/String;", "Lcom/google/gson/JsonObject;", "root", "readFromJson", "(Lcom/google/gson/JsonObject;)V", "save", "toJsonObject", "(Ljava/util/Map;)Lcom/google/gson/JsonObject;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "Ljava/io/File;", "statsFile", "Ljava/io/File;", "Ljava/time/ZoneId;", "zoneId", "Ljava/time/ZoneId;", "macroDefinitions", "Ljava/util/List;", "loaded", "Z", "lastSampleMs", "J", "lastSaveMs", "lastPrimaryMacroId", "Ljava/lang/String;", "lifetimeTotalMs", "Ljava/util/LinkedHashMap;", "Lkotlin/collections/LinkedHashMap;", "lifetimeByMacroMs", "Ljava/util/LinkedHashMap;", "dailyTotalsMs", "dailyByMacroMs", "MAX_ACCUMULATION_MS", "SAVE_INTERVAL_MS", "Snapshot", "MacroDuration", "MacroDefinition", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMacroTimeTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MacroTimeTracker.kt\norg/cobalt/internal/stats/MacroTimeTracker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,242:1\n1#2:243\n1586#3:244\n1661#3,3:245\n777#3:255\n873#3,2:256\n1080#3:258\n1220#3,2:259\n1249#3,4:261\n777#3:265\n873#3,2:266\n1080#3:268\n1586#3:269\n1661#3,3:270\n1915#3,2:273\n1915#3,2:275\n1915#3:277\n1915#3,2:278\n1916#3:280\n383#4,7:248\n221#5,2:281\n221#5,2:283\n*S KotlinDebug\n*F\n+ 1 MacroTimeTracker.kt\norg/cobalt/internal/stats/MacroTimeTracker\n*L\n107#1:244\n107#1:245,3\n173#1:255\n173#1:256,2\n174#1:258\n180#1:259,2\n180#1:261,4\n182#1:265\n182#1:266,2\n183#1:268\n184#1:269\n184#1:270,3\n195#1:273,2\n200#1:275,2\n205#1:277\n208#1:278,2\n205#1:280\n160#1:248,7\n223#1:281,2\n238#1:283,2\n*E\n"})
public final class MacroTimeTracker {
    @NotNull
    public static final MacroTimeTracker INSTANCE = new MacroTimeTracker();
    @NotNull
    private static final class_310 mc;
    private static final Gson gson;
    @NotNull
    private static final File statsFile;
    @NotNull
    private static final ZoneId zoneId;
    @NotNull
    private static final List<MacroDefinition> macroDefinitions;
    private static boolean loaded;
    private static long lastSampleMs;
    private static long lastSaveMs;
    @Nullable
    private static String lastPrimaryMacroId;
    private static long lifetimeTotalMs;
    @NotNull
    private static final LinkedHashMap<String, Long> lifetimeByMacroMs;
    @NotNull
    private static final LinkedHashMap<String, Long> dailyTotalsMs;
    @NotNull
    private static final LinkedHashMap<String, LinkedHashMap<String, Long>> dailyByMacroMs;
    private static final long MAX_ACCUMULATION_MS = 5000L;
    private static final long SAVE_INTERVAL_MS = 30000L;

    private MacroTimeTracker() {
    }

    public final void load() {
        long now;
        Object object;
        if (loaded) {
            return;
        }
        File file = statsFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        if (!statsFile.exists()) {
            long now2;
            statsFile.createNewFile();
            loaded = true;
            lastSampleMs = now2 = System.currentTimeMillis();
            lastSaveMs = now2;
            MacroDefinition macroDefinition = this.resolvePrimaryMacro();
            lastPrimaryMacroId = macroDefinition != null ? macroDefinition.getId() : null;
            return;
        }
        Object object2 = this;
        try {
            MacroTimeTracker $this$load_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)FilesKt.readText$default((File)statsFile, null, (int)1, null));
        }
        catch (Throwable bl) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = object;
        object = "";
        String text = ((Object)StringsKt.trim((CharSequence)((String)(Result.isFailure-impl((Object)object2) ? object : object2)))).toString();
        if (((CharSequence)text).length() > 0) {
            Object $this$load_u24lambda_u241;
            object = this;
            try {
                $this$load_u24lambda_u241 = (MacroTimeTracker)object;
                boolean bl = false;
                $this$load_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonObject());
            }
            catch (Throwable bl) {
                $this$load_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object = $this$load_u24lambda_u241;
            JsonObject jsonObject = (JsonObject)(Result.isFailure-impl((Object)object) ? null : object);
            if (jsonObject != null) {
                JsonObject p0 = jsonObject;
                boolean bl = false;
                this.readFromJson(p0);
            }
        }
        loaded = true;
        lastSampleMs = now = System.currentTimeMillis();
        lastSaveMs = now;
        MacroDefinition macroDefinition = this.resolvePrimaryMacro();
        lastPrimaryMacroId = macroDefinition != null ? macroDefinition.getId() : null;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Snapshot snapshot() {
        Collection<String> collection;
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        this.syncNow(false);
        String todayKey = this.currentDateKey();
        List<MacroDefinition> activeMacros = this.resolveActiveMacros();
        Long l = dailyTotalsMs.get(todayKey);
        MacroDefinition macroDefinition = (MacroDefinition)CollectionsKt.firstOrNull(activeMacros);
        Iterable iterable = activeMacros;
        String string = macroDefinition != null ? macroDefinition.getDisplayName() : null;
        long l2 = lifetimeTotalMs;
        long l3 = l != null ? l : 0L;
        boolean $i$f$map = false;
        void var5_8 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            MacroDefinition macroDefinition2 = (MacroDefinition)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getDisplayName());
        }
        collection = (List)destination$iv$iv;
        List<MacroDuration> list = this.buildMacroDurations((Map<String, Long>)lifetimeByMacroMs);
        List<MacroDuration> list2 = this.buildMacroDurations((Map<String, Long>)dailyByMacroMs.get(todayKey));
        Collection<String> collection2 = collection;
        String string2 = string;
        long l4 = l2;
        long l5 = l3;
        return new Snapshot(l5, l4, string2, (List<String>)collection2, list2, list);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        this.syncNow(true);
    }

    private final void syncNow(boolean saveIfNeeded) {
        if (!loaded) {
            this.load();
        }
        long now = System.currentTimeMillis();
        if (lastSampleMs <= 0L) {
            lastSampleMs = now;
            lastSaveMs = now;
            MacroDefinition macroDefinition = this.resolvePrimaryMacro();
            lastPrimaryMacroId = macroDefinition != null ? macroDefinition.getId() : null;
            return;
        }
        long elapsedMs = RangesKt.coerceIn((long)(now - lastSampleMs), (long)0L, (long)5000L);
        if (elapsedMs > 0L && lastPrimaryMacroId != null) {
            String string = lastPrimaryMacroId;
            Intrinsics.checkNotNull((Object)string);
            this.accumulateInterval(lastSampleMs, now, string, elapsedMs);
        }
        MacroDefinition macroDefinition = this.resolvePrimaryMacro();
        String currentPrimaryMacroId = macroDefinition != null ? macroDefinition.getId() : null;
        boolean primaryChanged = !Intrinsics.areEqual((Object)currentPrimaryMacroId, (Object)lastPrimaryMacroId);
        lastSampleMs = now;
        lastPrimaryMacroId = currentPrimaryMacroId;
        if (saveIfNeeded && (primaryChanged || now - lastSaveMs >= 30000L)) {
            this.save();
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void accumulateInterval(long startMs, long endMs, String macroId, long elapsedMs) {
        long boundedEndMs = Math.min(endMs, startMs + elapsedMs);
        if (boundedEndMs <= startMs) {
            return;
        }
        long cursor = startMs;
        while (cursor < boundedEndMs) {
            Instant cursorInstant = Instant.ofEpochMilli(cursor);
            LocalDate currentDate = cursorInstant.atZone(zoneId).toLocalDate();
            long nextDayStartMs = currentDate.plusDays(1L).atStartOfDay(zoneId).toInstant().toEpochMilli();
            long segmentEnd = Math.min(boundedEndMs, nextDayStartMs);
            long segmentMs = segmentEnd - cursor;
            if (segmentMs > 0L) {
                Object object;
                void $this$getOrPut$iv;
                String dateKey;
                Intrinsics.checkNotNullExpressionValue((Object)currentDate.toString(), (String)"toString(...)");
                lifetimeTotalMs += segmentMs;
                Map map = lifetimeByMacroMs;
                Long l = lifetimeByMacroMs.get(macroId);
                Object object2 = (l != null ? l : 0L) + segmentMs;
                map.put(macroId, object2);
                map = dailyTotalsMs;
                Long l2 = dailyTotalsMs.get(dateKey);
                object2 = (l2 != null ? l2 : 0L) + segmentMs;
                map.put(dateKey, object2);
                object2 = dailyByMacroMs;
                String key$iv = dateKey;
                boolean $i$f$getOrPut = false;
                Object value$iv = $this$getOrPut$iv.get(key$iv);
                if (value$iv == null) {
                    boolean bl = false;
                    LinkedHashMap answer$iv = new LinkedHashMap();
                    $this$getOrPut$iv.put(key$iv, answer$iv);
                    object = answer$iv;
                } else {
                    object = value$iv;
                }
                LinkedHashMap dayMap = (LinkedHashMap)object;
                object2 = dayMap;
                Long l3 = (Long)dayMap.get(macroId);
                Long l4 = (l3 != null ? l3 : 0L) + segmentMs;
                object2.put(macroId, l4);
            }
            cursor = segmentEnd;
        }
    }

    private final MacroDefinition resolvePrimaryMacro() {
        return (MacroDefinition)CollectionsKt.firstOrNull(this.resolveActiveMacros());
    }

    /*
     * WARNING - void declaration
     */
    private final List<MacroDefinition> resolveActiveMacros() {
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv = macroDefinitions;
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Object object;
            MacroDefinition definition = (MacroDefinition)element$iv$iv;
            boolean bl = false;
            Object object2 = INSTANCE;
            try {
                MacroTimeTracker $this$resolveActiveMacros_u24lambda_u240_u240 = object2;
                boolean bl2 = false;
                object = Result.constructor-impl((Object)((Boolean)definition.isActive().invoke()));
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object2 = object;
            object = false;
            Object object3 = Result.isFailure-impl((Object)object2) ? object : object2;
            if (!((Boolean)object3).booleanValue()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        Iterable $this$sortedByDescending$iv = (List)destination$iv$iv;
        boolean $i$f$sortedByDescending = false;
        return CollectionsKt.sortedWith((Iterable)$this$sortedByDescending$iv, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                MacroDefinition it = (MacroDefinition)b;
                boolean bl = false;
                Comparable comparable = Integer.valueOf(it.getPriority());
                it = (MacroDefinition)a;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Integer.valueOf(it.getPriority()));
            }
        });
    }

    /*
     * WARNING - void declaration
     */
    private final List<MacroDuration> buildMacroDurations(Map<String, Long> raw) {
        void $this$mapTo$iv$iv;
        Iterable $this$filterTo$iv$iv;
        Object object;
        void $this$associateByTo$iv$iv;
        Map<String, Long> map = raw;
        if (map == null || map.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        Iterable $this$associateBy$iv = macroDefinitions;
        boolean $i$f$associateBy = false;
        int capacity$iv22 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv22);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            MacroDefinition macroDefinition = (MacroDefinition)element$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            object.put(it.getId(), element$iv$iv);
        }
        Map knownIds = destination$iv$iv;
        Iterable $this$filter$iv = raw.entrySet();
        boolean $i$f$filter = false;
        Iterable capacity$iv22 = $this$filter$iv;
        Collection destination$iv$iv2 = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Map.Entry it = (Map.Entry)element$iv$iv;
            boolean bl = false;
            if (!(((Number)it.getValue()).longValue() > 0L)) continue;
            destination$iv$iv2.add(element$iv$iv);
        }
        Iterable $this$sortedByDescending$iv = (List)destination$iv$iv2;
        boolean $i$f$sortedByDescending = false;
        Iterable $this$map$iv = CollectionsKt.sortedWith((Iterable)$this$sortedByDescending$iv, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                Map.Entry it = (Map.Entry)b;
                boolean bl = false;
                Comparable comparable = (Long)it.getValue();
                it = (Map.Entry)a;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)((Long)it.getValue()));
            }
        });
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            Map.Entry entry = (Map.Entry)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl = false;
            String id = (String)entry.getKey();
            long duration = ((Number)entry.getValue()).longValue();
            Object object2 = (MacroDefinition)knownIds.get(id);
            if (object2 == null || (object2 = ((MacroDefinition)object2).getDisplayName()) == null) {
                object2 = id;
            }
            object.add(new MacroDuration((String)object2, duration));
        }
        return (List)destination$iv$iv2;
    }

    private final String currentDateKey() {
        String string = LocalDate.now(zoneId).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    private final void readFromJson(JsonObject root) {
        block14: {
            Object object;
            Object object2;
            JsonElement value;
            String key;
            Map.Entry entry;
            boolean $i$f$forEach;
            Iterable $this$forEach$iv;
            JsonElement jsonElement = root.get("lifetimeTotalMs");
            lifetimeTotalMs = jsonElement != null ? jsonElement.getAsLong() : 0L;
            lifetimeByMacroMs.clear();
            Object object3 = root.getAsJsonObject("lifetimeByMacroMs");
            if (object3 != null && (object3 = object3.entrySet()) != null) {
                $this$forEach$iv = (Iterable)object3;
                $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    Object $this$readFromJson_u24lambda_u240_u240;
                    entry = (Map.Entry)element$iv;
                    boolean bl = false;
                    Intrinsics.checkNotNull((Object)entry);
                    key = (String)entry.getKey();
                    value = (JsonElement)entry.getValue();
                    object2 = lifetimeByMacroMs;
                    object = INSTANCE;
                    try {
                        $this$readFromJson_u24lambda_u240_u240 = object;
                        boolean bl2 = false;
                        $this$readFromJson_u24lambda_u240_u240 = Result.constructor-impl((Object)value.getAsLong());
                    }
                    catch (Throwable bl2) {
                        $this$readFromJson_u24lambda_u240_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl2));
                    }
                    object = $this$readFromJson_u24lambda_u240_u240;
                    $this$readFromJson_u24lambda_u240_u240 = 0L;
                    object = Result.isFailure-impl((Object)object) ? $this$readFromJson_u24lambda_u240_u240 : object;
                    object2.put(key, object);
                }
            }
            dailyTotalsMs.clear();
            Object object4 = root.getAsJsonObject("dailyTotalsMs");
            if (object4 != null && (object4 = object4.entrySet()) != null) {
                $this$forEach$iv = (Iterable)object4;
                $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    Object $this$readFromJson_u24lambda_u241_u240;
                    entry = (Map.Entry)element$iv;
                    boolean bl = false;
                    Intrinsics.checkNotNull((Object)entry);
                    key = (String)entry.getKey();
                    value = (JsonElement)entry.getValue();
                    object2 = dailyTotalsMs;
                    object = INSTANCE;
                    try {
                        $this$readFromJson_u24lambda_u241_u240 = object;
                        boolean bl3 = false;
                        $this$readFromJson_u24lambda_u241_u240 = Result.constructor-impl((Object)value.getAsLong());
                    }
                    catch (Throwable bl3) {
                        $this$readFromJson_u24lambda_u241_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl3));
                    }
                    object = $this$readFromJson_u24lambda_u241_u240;
                    $this$readFromJson_u24lambda_u241_u240 = 0L;
                    object = Result.isFailure-impl((Object)object) ? $this$readFromJson_u24lambda_u241_u240 : object;
                    object2.put(key, object);
                }
            }
            dailyByMacroMs.clear();
            Object object5 = root.getAsJsonObject("dailyByMacroMs");
            if (object5 == null || (object5 = object5.entrySet()) == null) break block14;
            $this$forEach$iv = (Iterable)object5;
            $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                JsonObject macroObj;
                Object $this$readFromJson_u24lambda_u242_u240;
                entry = (Map.Entry)element$iv;
                boolean bl = false;
                Intrinsics.checkNotNull((Object)entry);
                String dateKey = (String)entry.getKey();
                value = (JsonElement)entry.getValue();
                object2 = INSTANCE;
                try {
                    $this$readFromJson_u24lambda_u242_u240 = object2;
                    boolean bl4 = false;
                    $this$readFromJson_u24lambda_u242_u240 = Result.constructor-impl((Object)value.getAsJsonObject());
                }
                catch (Throwable throwable) {
                    $this$readFromJson_u24lambda_u242_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                }
                object2 = $this$readFromJson_u24lambda_u242_u240;
                if ((JsonObject)(Result.isFailure-impl((Object)object2) ? null : object2) == null) continue;
                LinkedHashMap dayMap = new LinkedHashMap();
                Set set = macroObj.entrySet();
                Intrinsics.checkNotNullExpressionValue((Object)set, (String)"entrySet(...)");
                Iterable $this$forEach$iv2 = set;
                boolean $i$f$forEach2 = false;
                for (Object element$iv2 : $this$forEach$iv2) {
                    Object object6;
                    Map.Entry entry2 = (Map.Entry)element$iv2;
                    boolean bl5 = false;
                    Intrinsics.checkNotNull((Object)entry2);
                    String macroId = (String)entry2.getKey();
                    JsonElement macroValue = (JsonElement)entry2.getValue();
                    Map map = dayMap;
                    Object object7 = INSTANCE;
                    try {
                        MacroTimeTracker $this$readFromJson_u24lambda_u242_u241_u240 = object7;
                        boolean bl6 = false;
                        object6 = Result.constructor-impl((Object)macroValue.getAsLong());
                    }
                    catch (Throwable throwable) {
                        object6 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                    }
                    object7 = object6;
                    object6 = 0L;
                    object7 = Result.isFailure-impl((Object)object7) ? object6 : object7;
                    map.put(macroId, object7);
                }
                ((Map)dailyByMacroMs).put(dateKey, dayMap);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void save() {
        Object object;
        JsonObject jsonObject;
        Object object2;
        if (!loaded) {
            return;
        }
        JsonObject $this$save_u24lambda_u240 = object2 = new JsonObject();
        boolean bl = false;
        $this$save_u24lambda_u240.addProperty("lifetimeTotalMs", (Number)lifetimeTotalMs);
        $this$save_u24lambda_u240.add("lifetimeByMacroMs", (JsonElement)INSTANCE.toJsonObject((Map<String, Long>)lifetimeByMacroMs));
        $this$save_u24lambda_u240.add("dailyTotalsMs", (JsonElement)INSTANCE.toJsonObject((Map<String, Long>)dailyTotalsMs));
        JsonObject jsonObject2 = jsonObject = new JsonObject();
        String string = "dailyByMacroMs";
        JsonObject jsonObject3 = $this$save_u24lambda_u240;
        boolean bl2 = false;
        Map $this$forEach$iv = dailyByMacroMs;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            void $this$save_u24lambda_u240_u240;
            Map.Entry element$iv;
            Map.Entry entry = element$iv = iterator.next();
            boolean bl3 = false;
            String dateKey = (String)entry.getKey();
            LinkedHashMap dayMap = (LinkedHashMap)entry.getValue();
            $this$save_u24lambda_u240_u240.add(dateKey, (JsonElement)INSTANCE.toJsonObject(dayMap));
        }
        Unit unit = Unit.INSTANCE;
        jsonObject3.add(string, (JsonElement)jsonObject);
        JsonObject root = object2;
        object2 = this;
        try {
            MacroTimeTracker $this$save_u24lambda_u241 = (MacroTimeTracker)object2;
            boolean bl4 = false;
            File file = statsFile.getParentFile();
            if (file != null) {
                file.mkdirs();
            }
            String string2 = gson.toJson((JsonElement)root);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toJson(...)");
            FilesKt.writeText$default((File)statsFile, (String)string2, null, (int)2, null);
            lastSaveMs = System.currentTimeMillis();
            object = Result.constructor-impl((Object)Unit.INSTANCE);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
    }

    private final JsonObject toJsonObject(Map<String, Long> $this$toJsonObject) {
        JsonObject jsonObject;
        JsonObject $this$toJsonObject_u24lambda_u240 = jsonObject = new JsonObject();
        boolean bl = false;
        Map<String, Long> $this$forEach$iv = $this$toJsonObject;
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<String, Long>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> element$iv;
            Map.Entry<String, Long> entry = element$iv = iterator.next();
            boolean bl2 = false;
            String key = entry.getKey();
            long value = ((Number)entry.getValue()).longValue();
            $this$toJsonObject_u24lambda_u240.addProperty(key, (Number)value);
        }
        return jsonObject;
    }

    private static final boolean macroDefinitions$lambda$0() {
        return CommissionMacroModule.INSTANCE.isRunning();
    }

    private static final boolean macroDefinitions$lambda$1() {
        return MiningMacroModule.INSTANCE.isActive();
    }

    private static final boolean macroDefinitions$lambda$2() {
        return CombatMacroModule.INSTANCE.isRunning();
    }

    private static final boolean macroDefinitions$lambda$3() {
        return GardenMacroModule.INSTANCE.isActive();
    }

    private static final boolean macroDefinitions$lambda$4() {
        return FarmingMacroModule.INSTANCE.isActive();
    }

    private static final boolean macroDefinitions$lambda$5() {
        return PigMacroModule.INSTANCE.isActive();
    }

    private static final boolean macroDefinitions$lambda$6() {
        return DianaMacroModule.INSTANCE.isActive();
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        gson = new GsonBuilder().setPrettyPrinting().create();
        statsFile = new File(MacroTimeTracker.mc.field_1697, "config/cobalt/macro_time_stats.json");
        ZoneId zoneId = ZoneId.systemDefault();
        Intrinsics.checkNotNullExpressionValue((Object)zoneId, (String)"systemDefault(...)");
        MacroTimeTracker.zoneId = zoneId;
        Object[] objectArray = new MacroDefinition[]{new MacroDefinition("commission", "Commission Macro", 100, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$0)), new MacroDefinition("mining", "Mining Macro", 90, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$1)), new MacroDefinition("combat", "Combat Macro", 80, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$2)), new MacroDefinition("garden", "Garden Macro", 70, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$3)), new MacroDefinition("farming", "Farming Macro", 60, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$4)), new MacroDefinition("pig", "Pig Macro", 50, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$5)), new MacroDefinition("diana", "Diana Macro", 40, (Function0<Boolean>)((Function0)MacroTimeTracker::macroDefinitions$lambda$6))};
        macroDefinitions = CollectionsKt.listOf((Object[])objectArray);
        lifetimeByMacroMs = new LinkedHashMap();
        dailyTotalsMs = new LinkedHashMap();
        dailyByMacroMs = new LinkedHashMap();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0018\b\u0082\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J>\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u001b\u0010\u0016\u001a\u00020\b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0018\u001a\u00020\u0005H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0010J\u0011\u0010\u0019\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\rR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001c\u0010\rR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001d\u001a\u0004\b\u001e\u0010\u0010R\u001d\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006\u00a2\u0006\f\n\u0004\b\t\u0010\u001f\u001a\u0004\b\t\u0010\u0012\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDefinition;", "", "", "id", "displayName", "", "priority", "Lkotlin/Function0;", "", "isActive", "<init>", "(Ljava/lang/String;Ljava/lang/String;ILkotlin/jvm/functions/Function0;)V", "component1", "()Ljava/lang/String;", "component2", "component3", "()I", "component4", "()Lkotlin/jvm/functions/Function0;", "copy", "(Ljava/lang/String;Ljava/lang/String;ILkotlin/jvm/functions/Function0;)Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDefinition;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getId", "getDisplayName", "I", "getPriority", "Lkotlin/jvm/functions/Function0;", "cobalt"})
    private static final class MacroDefinition {
        @NotNull
        private final String id;
        @NotNull
        private final String displayName;
        private final int priority;
        @NotNull
        private final Function0<Boolean> isActive;

        public MacroDefinition(@NotNull String id, @NotNull String displayName, int priority, @NotNull Function0<Boolean> isActive) {
            Intrinsics.checkNotNullParameter((Object)id, (String)"id");
            Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
            Intrinsics.checkNotNullParameter(isActive, (String)"isActive");
            this.id = id;
            this.displayName = displayName;
            this.priority = priority;
            this.isActive = isActive;
        }

        @NotNull
        public final String getId() {
            return this.id;
        }

        @NotNull
        public final String getDisplayName() {
            return this.displayName;
        }

        public final int getPriority() {
            return this.priority;
        }

        @NotNull
        public final Function0<Boolean> isActive() {
            return this.isActive;
        }

        @NotNull
        public final String component1() {
            return this.id;
        }

        @NotNull
        public final String component2() {
            return this.displayName;
        }

        public final int component3() {
            return this.priority;
        }

        @NotNull
        public final Function0<Boolean> component4() {
            return this.isActive;
        }

        @NotNull
        public final MacroDefinition copy(@NotNull String id, @NotNull String displayName, int priority, @NotNull Function0<Boolean> isActive) {
            Intrinsics.checkNotNullParameter((Object)id, (String)"id");
            Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
            Intrinsics.checkNotNullParameter(isActive, (String)"isActive");
            return new MacroDefinition(id, displayName, priority, isActive);
        }

        public static /* synthetic */ MacroDefinition copy$default(MacroDefinition macroDefinition, String string, String string2, int n, Function0 function0, int n2, Object object) {
            if ((n2 & 1) != 0) {
                string = macroDefinition.id;
            }
            if ((n2 & 2) != 0) {
                string2 = macroDefinition.displayName;
            }
            if ((n2 & 4) != 0) {
                n = macroDefinition.priority;
            }
            if ((n2 & 8) != 0) {
                function0 = macroDefinition.isActive;
            }
            return macroDefinition.copy(string, string2, n, function0);
        }

        @NotNull
        public String toString() {
            return "MacroDefinition(id=" + this.id + ", displayName=" + this.displayName + ", priority=" + this.priority + ", isActive=" + this.isActive + ")";
        }

        public int hashCode() {
            int result = this.id.hashCode();
            result = result * 31 + this.displayName.hashCode();
            result = result * 31 + Integer.hashCode(this.priority);
            result = result * 31 + this.isActive.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MacroDefinition)) {
                return false;
            }
            MacroDefinition macroDefinition = (MacroDefinition)other;
            if (!Intrinsics.areEqual((Object)this.id, (Object)macroDefinition.id)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.displayName, (Object)macroDefinition.displayName)) {
                return false;
            }
            if (this.priority != macroDefinition.priority) {
                return false;
            }
            return Intrinsics.areEqual(this.isActive, macroDefinition.isActive);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\tR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDuration;", "", "", "name", "", "durationMs", "<init>", "(Ljava/lang/String;J)V", "component1", "()Ljava/lang/String;", "component2", "()J", "copy", "(Ljava/lang/String;J)Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDuration;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getName", "J", "getDurationMs", "cobalt"})
    public static final class MacroDuration {
        @NotNull
        private final String name;
        private final long durationMs;

        public MacroDuration(@NotNull String name, long durationMs) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            this.name = name;
            this.durationMs = durationMs;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        public final long getDurationMs() {
            return this.durationMs;
        }

        @NotNull
        public final String component1() {
            return this.name;
        }

        public final long component2() {
            return this.durationMs;
        }

        @NotNull
        public final MacroDuration copy(@NotNull String name, long durationMs) {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            return new MacroDuration(name, durationMs);
        }

        public static /* synthetic */ MacroDuration copy$default(MacroDuration macroDuration, String string, long l, int n, Object object) {
            if ((n & 1) != 0) {
                string = macroDuration.name;
            }
            if ((n & 2) != 0) {
                l = macroDuration.durationMs;
            }
            return macroDuration.copy(string, l);
        }

        @NotNull
        public String toString() {
            return "MacroDuration(name=" + this.name + ", durationMs=" + this.durationMs + ")";
        }

        public int hashCode() {
            int result = this.name.hashCode();
            result = result * 31 + Long.hashCode(this.durationMs);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MacroDuration)) {
                return false;
            }
            MacroDuration macroDuration = (MacroDuration)other;
            if (!Intrinsics.areEqual((Object)this.name, (Object)macroDuration.name)) {
                return false;
            }
            return this.durationMs == macroDuration.durationMs;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\r\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u0007\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\u0007\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\t0\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0014J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0014J`\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00072\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u00072\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u001b\u0010\u001b\u001a\u00020\u001a2\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0011\u0010\u001e\u001a\u00020\u001dH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0011\u0010 \u001a\u00020\u0005H\u00d6\u0081\u0004\u00a2\u0006\u0004\b \u0010\u0012R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010!\u001a\u0004\b\"\u0010\u000fR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010!\u001a\u0004\b#\u0010\u000fR\u0019\u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010$\u001a\u0004\b%\u0010\u0012R\u001d\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010&\u001a\u0004\b'\u0010\u0014R\u001d\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u00078\u0006\u00a2\u0006\f\n\u0004\b\n\u0010&\u001a\u0004\b(\u0010\u0014R\u001d\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\u00078\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010&\u001a\u0004\b)\u0010\u0014\u00a8\u0006*"}, d2={"Lorg/cobalt/internal/stats/MacroTimeTracker$Snapshot;", "", "", "todayTotalMs", "lifetimeTotalMs", "", "primaryMacroName", "", "activeMacroNames", "Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDuration;", "todayByMacro", "lifetimeByMacro", "<init>", "(JJLjava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "component1", "()J", "component2", "component3", "()Ljava/lang/String;", "component4", "()Ljava/util/List;", "component5", "component6", "copy", "(JJLjava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;)Lorg/cobalt/internal/stats/MacroTimeTracker$Snapshot;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "J", "getTodayTotalMs", "getLifetimeTotalMs", "Ljava/lang/String;", "getPrimaryMacroName", "Ljava/util/List;", "getActiveMacroNames", "getTodayByMacro", "getLifetimeByMacro", "cobalt"})
    public static final class Snapshot {
        private final long todayTotalMs;
        private final long lifetimeTotalMs;
        @Nullable
        private final String primaryMacroName;
        @NotNull
        private final List<String> activeMacroNames;
        @NotNull
        private final List<MacroDuration> todayByMacro;
        @NotNull
        private final List<MacroDuration> lifetimeByMacro;

        public Snapshot(long todayTotalMs, long lifetimeTotalMs, @Nullable String primaryMacroName, @NotNull List<String> activeMacroNames, @NotNull List<MacroDuration> todayByMacro, @NotNull List<MacroDuration> lifetimeByMacro) {
            Intrinsics.checkNotNullParameter(activeMacroNames, (String)"activeMacroNames");
            Intrinsics.checkNotNullParameter(todayByMacro, (String)"todayByMacro");
            Intrinsics.checkNotNullParameter(lifetimeByMacro, (String)"lifetimeByMacro");
            this.todayTotalMs = todayTotalMs;
            this.lifetimeTotalMs = lifetimeTotalMs;
            this.primaryMacroName = primaryMacroName;
            this.activeMacroNames = activeMacroNames;
            this.todayByMacro = todayByMacro;
            this.lifetimeByMacro = lifetimeByMacro;
        }

        public final long getTodayTotalMs() {
            return this.todayTotalMs;
        }

        public final long getLifetimeTotalMs() {
            return this.lifetimeTotalMs;
        }

        @Nullable
        public final String getPrimaryMacroName() {
            return this.primaryMacroName;
        }

        @NotNull
        public final List<String> getActiveMacroNames() {
            return this.activeMacroNames;
        }

        @NotNull
        public final List<MacroDuration> getTodayByMacro() {
            return this.todayByMacro;
        }

        @NotNull
        public final List<MacroDuration> getLifetimeByMacro() {
            return this.lifetimeByMacro;
        }

        public final long component1() {
            return this.todayTotalMs;
        }

        public final long component2() {
            return this.lifetimeTotalMs;
        }

        @Nullable
        public final String component3() {
            return this.primaryMacroName;
        }

        @NotNull
        public final List<String> component4() {
            return this.activeMacroNames;
        }

        @NotNull
        public final List<MacroDuration> component5() {
            return this.todayByMacro;
        }

        @NotNull
        public final List<MacroDuration> component6() {
            return this.lifetimeByMacro;
        }

        @NotNull
        public final Snapshot copy(long todayTotalMs, long lifetimeTotalMs, @Nullable String primaryMacroName, @NotNull List<String> activeMacroNames, @NotNull List<MacroDuration> todayByMacro, @NotNull List<MacroDuration> lifetimeByMacro) {
            Intrinsics.checkNotNullParameter(activeMacroNames, (String)"activeMacroNames");
            Intrinsics.checkNotNullParameter(todayByMacro, (String)"todayByMacro");
            Intrinsics.checkNotNullParameter(lifetimeByMacro, (String)"lifetimeByMacro");
            return new Snapshot(todayTotalMs, lifetimeTotalMs, primaryMacroName, activeMacroNames, todayByMacro, lifetimeByMacro);
        }

        public static /* synthetic */ Snapshot copy$default(Snapshot snapshot, long l, long l2, String string, List list, List list2, List list3, int n, Object object) {
            if ((n & 1) != 0) {
                l = snapshot.todayTotalMs;
            }
            if ((n & 2) != 0) {
                l2 = snapshot.lifetimeTotalMs;
            }
            if ((n & 4) != 0) {
                string = snapshot.primaryMacroName;
            }
            if ((n & 8) != 0) {
                list = snapshot.activeMacroNames;
            }
            if ((n & 0x10) != 0) {
                list2 = snapshot.todayByMacro;
            }
            if ((n & 0x20) != 0) {
                list3 = snapshot.lifetimeByMacro;
            }
            return snapshot.copy(l, l2, string, list, list2, list3);
        }

        @NotNull
        public String toString() {
            return "Snapshot(todayTotalMs=" + this.todayTotalMs + ", lifetimeTotalMs=" + this.lifetimeTotalMs + ", primaryMacroName=" + this.primaryMacroName + ", activeMacroNames=" + this.activeMacroNames + ", todayByMacro=" + this.todayByMacro + ", lifetimeByMacro=" + this.lifetimeByMacro + ")";
        }

        public int hashCode() {
            int result = Long.hashCode(this.todayTotalMs);
            result = result * 31 + Long.hashCode(this.lifetimeTotalMs);
            result = result * 31 + (this.primaryMacroName == null ? 0 : this.primaryMacroName.hashCode());
            result = result * 31 + ((Object)this.activeMacroNames).hashCode();
            result = result * 31 + ((Object)this.todayByMacro).hashCode();
            result = result * 31 + ((Object)this.lifetimeByMacro).hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Snapshot)) {
                return false;
            }
            Snapshot snapshot = (Snapshot)other;
            if (this.todayTotalMs != snapshot.todayTotalMs) {
                return false;
            }
            if (this.lifetimeTotalMs != snapshot.lifetimeTotalMs) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.primaryMacroName, (Object)snapshot.primaryMacroName)) {
                return false;
            }
            if (!Intrinsics.areEqual(this.activeMacroNames, snapshot.activeMacroNames)) {
                return false;
            }
            if (!Intrinsics.areEqual(this.todayByMacro, snapshot.todayByMacro)) {
                return false;
            }
            return Intrinsics.areEqual(this.lifetimeByMacro, snapshot.lifetimeByMacro);
        }
    }
}

