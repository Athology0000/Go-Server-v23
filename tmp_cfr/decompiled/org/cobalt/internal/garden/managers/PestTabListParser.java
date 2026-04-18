/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  net.minecraft.class_640
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import net.minecraft.class_640;
import org.cobalt.internal.garden.managers.TabListData;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\t\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u000b\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u000b\u0010\u0006R\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0014\u0010\u0010\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0011R\u0014\u0010\u0013\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0011R\u0014\u0010\u0014\u001a\u00020\u000f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0011\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/garden/managers/PestTabListParser;", "", "<init>", "()V", "Lorg/cobalt/internal/garden/managers/TabListData;", "parse", "()Lorg/cobalt/internal/garden/managers/TabListData;", "", "text", "strip", "(Ljava/lang/String;)Ljava/lang/String;", "empty", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lkotlin/text/Regex;", "ALIVE_REGEX", "Lkotlin/text/Regex;", "COOLDOWN_REGEX", "PLOT_REGEX", "BONUS_REGEX", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPestTabListParser.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PestTabListParser.kt\norg/cobalt/internal/garden/managers/PestTabListParser\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,54:1\n1#2:55\n1915#3,2:56\n296#3,2:58\n1586#3:60\n1661#3,3:61\n777#3:64\n873#3,2:65\n*S KotlinDebug\n*F\n+ 1 PestTabListParser.kt\norg/cobalt/internal/garden/managers/PestTabListParser\n*L\n34#1:56,2\n42#1:58,2\n45#1:60\n45#1:61,3\n45#1:64\n45#1:65,2\n*E\n"})
public final class PestTabListParser {
    @NotNull
    public static final PestTabListParser INSTANCE = new PestTabListParser();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Regex ALIVE_REGEX;
    @NotNull
    private static final Regex COOLDOWN_REGEX;
    @NotNull
    private static final Regex PLOT_REGEX;
    @NotNull
    private static final Regex BONUS_REGEX;

    private PestTabListParser() {
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @NotNull
    public final TabListData parse() {
        block12: {
            v0 = PestTabListParser.mc.field_1705;
            Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"gui");
            gui = v0;
            var4_2 = gui.method_1750();
            v1 = var4_2 instanceof TabOverlayAccessor != false ? (TabOverlayAccessor)var4_2 : null;
            if (v1 == null) {
                return this.empty();
            }
            overlay = v1;
            $this$parse_u24lambda_u240 = var4_2 = new StringBuilder();
            $i$a$-buildString-PestTabListParser$parse$raw$1 = false;
            v2 = overlay.getHeader();
            if (v2 != null) {
                it = v2;
                $i$a$-let-PestTabListParser$parse$raw$1$1 = false;
                v3 = it.getString();
                Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"getString(...)");
                $this$parse_u24lambda_u240.append(PestTabListParser.INSTANCE.strip(v3)).append("\n");
            }
            v4 = overlay.getFooter();
            if (v4 != null) {
                it = v4;
                $i$a$-let-PestTabListParser$parse$raw$1$2 = false;
                v5 = it.getString();
                Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"getString(...)");
                $this$parse_u24lambda_u240.append(PestTabListParser.INSTANCE.strip(v5)).append("\n");
            }
            if ((v6 /* !! */  = PestTabListParser.mc.method_1562()) != null && (v6 /* !! */  = v6 /* !! */ .method_45732()) != null) {
                $this$forEach$iv = (Iterable)v6 /* !! */ ;
                $i$f$forEach = false;
                var9_13 = $this$forEach$iv.iterator();
                while (var9_13.hasNext()) {
                    element$iv = var9_13.next();
                    info = (class_640)element$iv;
                    $i$a$-forEach-PestTabListParser$parse$raw$1$3 = false;
                    if (info.method_2971() == null) continue;
                    $i$a$-let-PestTabListParser$parse$raw$1$3$1 = false;
                    v7 = it.getString();
                    Intrinsics.checkNotNullExpressionValue((Object)v7, (String)"getString(...)");
                    $this$parse_u24lambda_u240.append(PestTabListParser.INSTANCE.strip(v7)).append("\n");
                }
            }
            v8 = alive = (v9 = Regex.find$default((Regex)PestTabListParser.ALIVE_REGEX, (CharSequence)(raw = var4_2.toString()), (int)0, (int)2, null)) != null && (v9 = v9.getGroupValues()) != null && (v9 = (String)v9.get(1)) != null && (v9 = StringsKt.toIntOrNull((String)v9)) != null ? v9.intValue() : 0;
            v10 = Regex.find$default((Regex)PestTabListParser.COOLDOWN_REGEX, (CharSequence)raw, (int)0, (int)2, null);
            if (v10 == null) ** GOTO lbl-1000
            m = v10;
            $i$a$-let-PestTabListParser$parse$cooldown$1 = false;
            $this$firstOrNull$iv = CollectionsKt.drop((Iterable)m.getGroupValues(), (int)1);
            $i$f$firstOrNull = false;
            for (E element$iv : $this$firstOrNull$iv) {
                it = (String)element$iv;
                $i$a$-firstOrNull-PestTabListParser$parse$cooldown$1$1 = false;
                v11 = ((CharSequence)it).length() > 0;
                if (!v11) continue;
                v12 = element$iv;
                break block12;
            }
            v12 = null;
        }
        v13 = v12;
        v14 = v13 != null ? StringsKt.toIntOrNull((String)v13) : null;
        v10 = v14;
        if (v14 != null) {
            v15 = v10.intValue();
        } else lbl-1000:
        // 2 sources

        {
            v15 = 0;
        }
        cooldown = v15;
        $this$forEach$iv = Regex.find$default((Regex)PestTabListParser.PLOT_REGEX, (CharSequence)raw, (int)0, (int)2, null);
        if ($this$forEach$iv != null && (var8_12 = $this$forEach$iv.getGroupValues()) != null && (var9_13 = (String)var8_12.get(1)) != null && (var10_16 = StringsKt.split$default((CharSequence)((CharSequence)var9_13), (String[])(var11_17 = new String[]{","}), (boolean)false, (int)0, (int)6, null)) != null) {
            $i$f$firstOrNull = var10_16;
            $i$f$map = false;
            element$iv = $this$map$iv;
            destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            $i$f$mapTo = false;
            for (T item$iv$iv : $this$mapTo$iv$iv) {
                var19_33 = (String)item$iv$iv;
                var22_36 = destination$iv$iv;
                $i$a$-map-PestTabListParser$parse$plots$1 = false;
                var22_36.add(StringsKt.trim((CharSequence)((CharSequence)it)).toString());
            }
            $i$f$map = (List)destination$iv$iv;
            $i$f$filter = false;
            destination$iv$iv = $this$filter$iv;
            destination$iv$iv = new ArrayList<E>();
            $i$f$filterTo = false;
            for (T element$iv$iv : $this$filterTo$iv$iv) {
                it = (String)element$iv$iv;
                $i$a$-filter-PestTabListParser$parse$plots$2 = false;
                v16 = !StringsKt.isBlank((CharSequence)it);
                if (!v16) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            v17 = (List)destination$iv$iv;
        } else {
            v17 = CollectionsKt.emptyList();
        }
        plots = v17;
        bonus = PestTabListParser.BONUS_REGEX.containsMatchIn((CharSequence)raw);
        return new TabListData(alive, cooldown, plots, bonus);
    }

    private final String strip(String text) {
        CharSequence charSequence = text;
        Regex regex = new Regex("\u00a7[0-9a-fk-or]");
        String string = "";
        return regex.replace(charSequence, string);
    }

    private final TabListData empty() {
        return new TabListData(0, 0, CollectionsKt.emptyList(), false);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        ALIVE_REGEX = new Regex("(?:alive|pest)[:\\s]+(\\d+)", RegexOption.IGNORE_CASE);
        COOLDOWN_REGEX = new Regex("(?:cooldown|pest\\s*cd|(?:next\\s+)?pest\\s+(?:spawn|in)|spawning\\s+in|infestation|pest\\s+timer)[:\\s]+(\\d+)s?|(?<!\\w)cd[:\\s]+(\\d+)s?", RegexOption.IGNORE_CASE);
        PLOT_REGEX = new Regex("plot[:\\s]+([a-zA-Z0-9_ ,]+)", RegexOption.IGNORE_CASE);
        BONUS_REGEX = new Regex("bonus[:\\s]+active", RegexOption.IGNORE_CASE);
    }
}

