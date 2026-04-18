/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1799
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.wardrobe;

import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_1799;
import org.cobalt.internal.wardrobe.WardrobeSet;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0003\u001a\u0011\u0010\u0002\u001a\u00020\u0001*\u00020\u0000\u00a2\u0006\u0004\b\u0002\u0010\u0003\" \u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeSet;", "", "displayName", "(Lorg/cobalt/internal/wardrobe/WardrobeSet;)Ljava/lang/String;", "", "REFORGE_LABELS", "Ljava/util/Map;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeState.kt\norg/cobalt/internal/wardrobe/WardrobeStateKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,88:1\n296#2,2:89\n*S KotlinDebug\n*F\n+ 1 WardrobeState.kt\norg/cobalt/internal/wardrobe/WardrobeStateKt\n*L\n79#1:89,2\n*E\n"})
public final class WardrobeStateKt {
    @NotNull
    private static final Map<String, String> REFORGE_LABELS;

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @NotNull
    public static final String displayName(@NotNull WardrobeSet $this$displayName) {
        block4: {
            Intrinsics.checkNotNullParameter((Object)$this$displayName, (String)"<this>");
            $this$firstOrNull$iv = CollectionsKt.filterNotNull((Iterable)$this$displayName.getArmor());
            $i$f$firstOrNull = false;
            var5_4 = $this$firstOrNull$iv.iterator();
            while (var5_4.hasNext()) {
                element$iv = var5_4.next();
                it = (class_1799)element$iv;
                $i$a$-firstOrNull-WardrobeStateKt$displayName$firstWord$1 = false;
                if (!(it.method_7960() == false)) continue;
                v0 = element$iv;
                break block4;
            }
            v0 = null;
        }
        var2_9 = v0;
        if (var2_9 == null || (var3_1 = var2_9.method_7964()) == null || (var4_3 = var3_1.getString()) == null || (var5_4 = (var7_6 /* !! */  = new Regex("\u00a7[0-9a-fk-or]")).replace(var6_5 = (CharSequence)var4_3, (String)(var8_8 = ""))) == null) ** GOTO lbl-1000
        var6_5 = StringsKt.trim((CharSequence)((CharSequence)var5_4)).toString();
        if (var6_5 != null && (var7_6 /* !! */  = StringsKt.split$default((CharSequence)var6_5, (String[])(var8_8 = new String[]{" "}), (boolean)false, (int)0, (int)6, null)) != null) {
            v1 = (String)CollectionsKt.firstOrNull((List)var7_6 /* !! */ );
        } else lbl-1000:
        // 2 sources

        {
            v1 = firstWord = null;
        }
        if ((v2 = WardrobeStateKt.REFORGE_LABELS.get(firstWord)) == null) {
            v2 = "Set " + $this$displayName.getId();
        }
        return v2;
    }

    static {
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"Mossy", (Object)"Farming"), TuplesKt.to((Object)"Mantid", (Object)"Pest"), TuplesKt.to((Object)"Jaded", (Object)"Mining")};
        REFORGE_LABELS = MapsKt.mapOf((Pair[])pairArray);
    }
}

