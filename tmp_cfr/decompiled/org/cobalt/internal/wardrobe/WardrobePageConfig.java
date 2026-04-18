/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.wardrobe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001b\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ)\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\f2\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobePageConfig;", "", "<init>", "()V", "", "text", "", "", "parseSlots", "(Ljava/lang/String;)Ljava/util/Set;", "page1Text", "page2Text", "", "resolvePages", "(Ljava/lang/String;Ljava/lang/String;)Ljava/util/Map;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobePageConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobePageConfig.kt\norg/cobalt/internal/wardrobe/WardrobePageConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,36:1\n1642#2,10:37\n1915#2:47\n1916#2:49\n1652#2:50\n777#2:51\n873#2,2:52\n1300#2,2:54\n1315#2,4:56\n1#3:48\n*S KotlinDebug\n*F\n+ 1 WardrobePageConfig.kt\norg/cobalt/internal/wardrobe/WardrobePageConfig\n*L\n11#1:37,10\n11#1:47\n11#1:49\n11#1:50\n12#1:51\n12#1:52,2\n27#1:54,2\n27#1:56,4\n11#1:48\n*E\n"})
public final class WardrobePageConfig {
    @NotNull
    public static final WardrobePageConfig INSTANCE = new WardrobePageConfig();

    private WardrobePageConfig() {
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<Integer> parseSlots(@NotNull String text) {
        void $this$filterTo$iv$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)text, (String)"text");
        String[] stringArray = new String[]{","};
        Iterable $this$mapNotNull$iv = StringsKt.split$default((CharSequence)text, (String[])stringArray, (boolean)false, (int)0, (int)6, null);
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
        Iterable $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$filter = false;
        $this$mapNotNullTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            int it = ((Number)element$iv$iv).intValue();
            boolean bl = false;
            boolean bl4 = 1 <= it ? it < 28 : false;
            if (!bl4) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Map<Integer, Integer> resolvePages(@NotNull String page1Text, @NotNull String page2Text) {
        void $this$associateWithTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)page1Text, (String)"page1Text");
        Intrinsics.checkNotNullParameter((Object)page2Text, (String)"page2Text");
        Set<Integer> page1 = this.parseSlots(page1Text);
        Set<Integer> page2 = this.parseSlots(page2Text);
        Iterable $this$associateWith$iv = (Iterable)new IntRange(1, 27);
        boolean $i$f$associateWith = false;
        LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
        Iterable iterable = $this$associateWith$iv;
        Map destination$iv$iv = result$iv;
        boolean $i$f$associateWithTo = false;
        for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
            void id;
            int n = ((Number)element$iv$iv).intValue();
            Object t = element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            Integer n2 = page1.contains((int)id) ? 1 : (page2.contains((int)id) ? 2 : 3);
            map.put(t, n2);
        }
        return destination$iv$iv;
    }
}

