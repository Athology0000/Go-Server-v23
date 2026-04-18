/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001b\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\b2\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000eR7\u0010\u0012\u001a\"\u0012\u0004\u0012\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u000fj\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u0010`\u00118\u0006\u00a2\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u001d\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00168\u0006\u00a2\u0006\f\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR#\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u001b8\u0006\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001fR)\u0010 \u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\b0\u001b8\u0006\u00a2\u0006\f\n\u0004\b \u0010\u001d\u001a\u0004\b!\u0010\u001fR \u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010\u001dR\u001a\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00040\b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010$\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/mining/MiningBlockRegistry;", "", "<init>", "()V", "", "type", "normalizeType", "(Ljava/lang/String;)Ljava/lang/String;", "", "idsForType", "(Ljava/lang/String;)Ljava/util/Set;", "id", "", "isBlacklisted", "(Ljava/lang/String;)Z", "Ljava/util/LinkedHashMap;", "", "Lkotlin/collections/LinkedHashMap;", "BLOCK_HARDNESS", "Ljava/util/LinkedHashMap;", "getBLOCK_HARDNESS", "()Ljava/util/LinkedHashMap;", "", "BLOCK_TYPES", "[Ljava/lang/String;", "getBLOCK_TYPES", "()[Ljava/lang/String;", "", "BLOCK_ID_TO_TYPE", "Ljava/util/Map;", "getBLOCK_ID_TO_TYPE", "()Ljava/util/Map;", "TYPE_TO_BLOCK_IDS", "getTYPE_TO_BLOCK_IDS", "LEGACY_TYPE_ALIASES", "BLACKLISTED_BLOCK_IDS", "Ljava/util/Set;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningBlockRegistry.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningBlockRegistry.kt\norg/cobalt/internal/mining/MiningBlockRegistry\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,111:1\n862#2,2:112\n1525#2:116\n1557#2,3:117\n1560#2,3:127\n1266#2,4:132\n37#3,2:114\n383#4,7:120\n466#4:130\n415#4:131\n*S KotlinDebug\n*F\n+ 1 MiningBlockRegistry.kt\norg/cobalt/internal/mining/MiningBlockRegistry\n*L\n106#1:112,2\n90#1:116\n90#1:117,3\n90#1:127,3\n90#1:132,4\n37#1:114,2\n90#1:120,7\n90#1:130\n90#1:131\n*E\n"})
public final class MiningBlockRegistry {
    @NotNull
    public static final MiningBlockRegistry INSTANCE;
    @NotNull
    private static final LinkedHashMap<String, Double> BLOCK_HARDNESS;
    @NotNull
    private static final String[] BLOCK_TYPES;
    @NotNull
    private static final Map<String, String> BLOCK_ID_TO_TYPE;
    @NotNull
    private static final Map<String, Set<String>> TYPE_TO_BLOCK_IDS;
    @NotNull
    private static final Map<String, String> LEGACY_TYPE_ALIASES;
    @NotNull
    private static final Set<String> BLACKLISTED_BLOCK_IDS;

    private MiningBlockRegistry() {
    }

    @NotNull
    public final LinkedHashMap<String, Double> getBLOCK_HARDNESS() {
        return BLOCK_HARDNESS;
    }

    @NotNull
    public final String[] getBLOCK_TYPES() {
        return BLOCK_TYPES;
    }

    @NotNull
    public final Map<String, String> getBLOCK_ID_TO_TYPE() {
        return BLOCK_ID_TO_TYPE;
    }

    @NotNull
    public final Map<String, Set<String>> getTYPE_TO_BLOCK_IDS() {
        return TYPE_TO_BLOCK_IDS;
    }

    @NotNull
    public final String normalizeType(@NotNull String type) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        String string = LEGACY_TYPE_ALIASES.get(type);
        if (string == null) {
            string = type;
        }
        return string;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<String> idsForType(@NotNull String type) {
        void destination$iv;
        void $this$filterNotTo$iv;
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        String normalizedType = this.normalizeType(type);
        Set set = TYPE_TO_BLOCK_IDS.get(normalizedType);
        if (set == null) {
            set = SetsKt.emptySet();
        }
        Iterable iterable = set;
        Collection collection = new LinkedHashSet();
        boolean $i$f$filterNotTo = false;
        for (Object element$iv : $this$filterNotTo$iv) {
            String it = (String)element$iv;
            boolean bl = false;
            if (BLACKLISTED_BLOCK_IDS.contains(it)) continue;
            destination$iv.add(element$iv);
        }
        return (Set)destination$iv;
    }

    public final boolean isBlacklisted(@NotNull String id) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        return BLACKLISTED_BLOCK_IDS.contains(id);
    }

    /*
     * WARNING - void declaration
     */
    static {
        void $this$associateByTo$iv$iv$iv;
        void $this$mapValuesTo$iv$iv;
        Object object;
        Object value$iv$iv$iv;
        Map.Entry $this$getOrPut$iv$iv$iv;
        Map $this$groupByTo$iv$iv;
        INSTANCE = new MiningBlockRegistry();
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)"Custom", null), TuplesKt.to((Object)"Pure Coal", (Object)600.0), TuplesKt.to((Object)"Pure Iron", (Object)600.0), TuplesKt.to((Object)"Pure Gold", (Object)600.0), TuplesKt.to((Object)"Pure Lapis", (Object)600.0), TuplesKt.to((Object)"Pure Redstone", (Object)600.0), TuplesKt.to((Object)"Pure Emerald", (Object)600.0), TuplesKt.to((Object)"Pure Diamond", (Object)600.0), TuplesKt.to((Object)"Pure Quartz", (Object)600.0), TuplesKt.to((Object)"Mithril (Gray)", (Object)500.0), TuplesKt.to((Object)"Mithril (Dark)", (Object)800.0), TuplesKt.to((Object)"Mithril (Hot)", (Object)1500.0), TuplesKt.to((Object)"Titanium", (Object)2000.0), TuplesKt.to((Object)"Ruby Gemstone", (Object)2300.0), TuplesKt.to((Object)"Amber Gemstone", (Object)3000.0), TuplesKt.to((Object)"Amethyst Gemstone", (Object)3000.0), TuplesKt.to((Object)"Jade Gemstone", (Object)3000.0), TuplesKt.to((Object)"Sapphire Gemstone", (Object)3000.0), TuplesKt.to((Object)"Opal Gemstone", (Object)3000.0), TuplesKt.to((Object)"Topaz Gemstone", (Object)3800.0), TuplesKt.to((Object)"Jasper Gemstone", (Object)4800.0), TuplesKt.to((Object)"Onyx Gemstone", (Object)5200.0), TuplesKt.to((Object)"Aquamarine Gemstone", (Object)5200.0), TuplesKt.to((Object)"Citrine Gemstone", (Object)5200.0), TuplesKt.to((Object)"Peridot Gemstone", (Object)5200.0), TuplesKt.to((Object)"Umber", (Object)5600.0), TuplesKt.to((Object)"Tungsten", (Object)5600.0), TuplesKt.to((Object)"Glacite", (Object)6000.0), TuplesKt.to((Object)"Sulphur", (Object)500.0)};
        BLOCK_HARDNESS = MapsKt.linkedMapOf((Pair[])objectArray);
        Set<String> set = BLOCK_HARDNESS.keySet();
        Intrinsics.checkNotNullExpressionValue(set, (String)"<get-keys>(...)");
        Pair[] $this$toTypedArray$iv = (Pair[])set;
        boolean $i$f$toTypedArray = false;
        Object thisCollection$iv = $this$toTypedArray$iv;
        BLOCK_TYPES = thisCollection$iv.toArray(new String[0]);
        $this$toTypedArray$iv = new Pair[]{TuplesKt.to((Object)"minecraft:coal_ore", (Object)"Pure Coal"), TuplesKt.to((Object)"minecraft:deepslate_coal_ore", (Object)"Pure Coal"), TuplesKt.to((Object)"minecraft:coal_block", (Object)"Pure Coal"), TuplesKt.to((Object)"minecraft:iron_ore", (Object)"Pure Iron"), TuplesKt.to((Object)"minecraft:deepslate_iron_ore", (Object)"Pure Iron"), TuplesKt.to((Object)"minecraft:iron_block", (Object)"Pure Iron"), TuplesKt.to((Object)"minecraft:gold_ore", (Object)"Pure Gold"), TuplesKt.to((Object)"minecraft:deepslate_gold_ore", (Object)"Pure Gold"), TuplesKt.to((Object)"minecraft:gold_block", (Object)"Pure Gold"), TuplesKt.to((Object)"minecraft:lapis_ore", (Object)"Pure Lapis"), TuplesKt.to((Object)"minecraft:deepslate_lapis_ore", (Object)"Pure Lapis"), TuplesKt.to((Object)"minecraft:lapis_block", (Object)"Pure Lapis"), TuplesKt.to((Object)"minecraft:redstone_ore", (Object)"Pure Redstone"), TuplesKt.to((Object)"minecraft:deepslate_redstone_ore", (Object)"Pure Redstone"), TuplesKt.to((Object)"minecraft:redstone_block", (Object)"Pure Redstone"), TuplesKt.to((Object)"minecraft:emerald_ore", (Object)"Pure Emerald"), TuplesKt.to((Object)"minecraft:deepslate_emerald_ore", (Object)"Pure Emerald"), TuplesKt.to((Object)"minecraft:emerald_block", (Object)"Pure Emerald"), TuplesKt.to((Object)"minecraft:diamond_ore", (Object)"Pure Diamond"), TuplesKt.to((Object)"minecraft:deepslate_diamond_ore", (Object)"Pure Diamond"), TuplesKt.to((Object)"minecraft:diamond_block", (Object)"Pure Diamond"), TuplesKt.to((Object)"minecraft:nether_quartz_ore", (Object)"Pure Quartz"), TuplesKt.to((Object)"minecraft:quartz_block", (Object)"Pure Quartz"), TuplesKt.to((Object)"minecraft:gray_wool", (Object)"Mithril (Gray)"), TuplesKt.to((Object)"minecraft:cyan_terracotta", (Object)"Mithril (Gray)"), TuplesKt.to((Object)"minecraft:prismarine", (Object)"Mithril (Dark)"), TuplesKt.to((Object)"minecraft:prismarine_bricks", (Object)"Mithril (Dark)"), TuplesKt.to((Object)"minecraft:dark_prismarine", (Object)"Mithril (Dark)"), TuplesKt.to((Object)"minecraft:light_blue_wool", (Object)"Mithril (Hot)"), TuplesKt.to((Object)"minecraft:polished_diorite", (Object)"Titanium"), TuplesKt.to((Object)"minecraft:red_stained_glass", (Object)"Ruby Gemstone"), TuplesKt.to((Object)"minecraft:orange_stained_glass", (Object)"Amber Gemstone"), TuplesKt.to((Object)"minecraft:purple_stained_glass", (Object)"Amethyst Gemstone"), TuplesKt.to((Object)"minecraft:green_stained_glass", (Object)"Jade Gemstone"), TuplesKt.to((Object)"minecraft:light_blue_stained_glass", (Object)"Sapphire Gemstone"), TuplesKt.to((Object)"minecraft:white_stained_glass", (Object)"Opal Gemstone"), TuplesKt.to((Object)"minecraft:yellow_stained_glass", (Object)"Topaz Gemstone"), TuplesKt.to((Object)"minecraft:pink_stained_glass", (Object)"Jasper Gemstone"), TuplesKt.to((Object)"minecraft:black_stained_glass", (Object)"Onyx Gemstone"), TuplesKt.to((Object)"minecraft:cyan_stained_glass", (Object)"Aquamarine Gemstone"), TuplesKt.to((Object)"minecraft:lime_stained_glass", (Object)"Peridot Gemstone"), TuplesKt.to((Object)"minecraft:light_gray_stained_glass", (Object)"Citrine Gemstone"), TuplesKt.to((Object)"minecraft:packed_ice", (Object)"Glacite"), TuplesKt.to((Object)"minecraft:blue_ice", (Object)"Glacite"), TuplesKt.to((Object)"minecraft:brown_wool", (Object)"Umber"), TuplesKt.to((Object)"minecraft:yellow_wool", (Object)"Sulphur"), TuplesKt.to((Object)"minecraft:light_gray_terracotta", (Object)"Tungsten")};
        BLOCK_ID_TO_TYPE = MapsKt.mapOf((Pair[])$this$toTypedArray$iv);
        Iterable $this$groupBy$iv = BLOCK_ID_TO_TYPE.entrySet();
        boolean $i$f$groupBy = false;
        thisCollection$iv = $this$groupBy$iv;
        Map destination$iv$iv = new LinkedHashMap();
        boolean $i$f$groupByTo = false;
        Object object2 = $this$groupByTo$iv$iv.iterator();
        while (object2.hasNext()) {
            void it;
            Object object3;
            Object element$iv$iv = object2.next();
            Map.Entry it2 = (Map.Entry)element$iv$iv;
            boolean $i$a$-groupBy-MiningBlockRegistry$TYPE_TO_BLOCK_IDS$32 = false;
            String key$iv$iv = (String)it2.getValue();
            Map map = destination$iv$iv;
            String key$iv$iv$iv = key$iv$iv;
            boolean $i$f$getOrPut = false;
            value$iv$iv$iv = $this$getOrPut$iv$iv$iv.get(key$iv$iv$iv);
            if (value$iv$iv$iv == null) {
                boolean bl = false;
                List answer$iv$iv$iv = new ArrayList();
                $this$getOrPut$iv$iv$iv.put(key$iv$iv$iv, answer$iv$iv$iv);
                object3 = answer$iv$iv$iv;
            } else {
                object3 = value$iv$iv$iv;
            }
            List list$iv$iv = (List)object3;
            Map.Entry $i$a$-groupBy-MiningBlockRegistry$TYPE_TO_BLOCK_IDS$32 = (Map.Entry)element$iv$iv;
            object = list$iv$iv;
            boolean bl = false;
            object.add((String)it.getKey());
        }
        Map $this$mapValues$iv = destination$iv$iv;
        boolean $i$f$mapValues = false;
        $this$groupByTo$iv$iv = $this$mapValues$iv;
        destination$iv$iv = new LinkedHashMap(MapsKt.mapCapacity((int)$this$mapValues$iv.size()));
        boolean $i$f$mapValuesTo = false;
        object2 = $this$mapValuesTo$iv$iv.entrySet();
        Map destination$iv$iv$iv = destination$iv$iv;
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
            void it;
            void it$iv$iv;
            $this$getOrPut$iv$iv$iv = (Map.Entry)element$iv$iv$iv;
            Map map = destination$iv$iv$iv;
            boolean bl = false;
            value$iv$iv$iv = (Map.Entry)element$iv$iv$iv;
            Object k = it$iv$iv.getKey();
            object = map;
            boolean bl2 = false;
            Set set2 = CollectionsKt.toSet((Iterable)((Iterable)it.getValue()));
            object.put(k, set2);
        }
        TYPE_TO_BLOCK_IDS = destination$iv$iv$iv;
        objectArray = new Pair[]{TuplesKt.to((Object)"Mithril (Prismarine)", (Object)"Mithril (Dark)"), TuplesKt.to((Object)"Mithril (Blue Wool)", (Object)"Mithril (Hot)")};
        LEGACY_TYPE_ALIASES = MapsKt.mapOf((Pair[])objectArray);
        objectArray = new String[]{"minecraft:stone", "minecraft:light_gray_wool"};
        BLACKLISTED_BLOCK_IDS = SetsKt.setOf((Object[])objectArray);
    }
}

