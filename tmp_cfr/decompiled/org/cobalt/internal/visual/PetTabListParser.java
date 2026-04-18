/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$IntRef
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_355
 *  net.minecraft.class_640
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.visual;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_355;
import net.minecraft.class_640;
import org.cobalt.internal.visual.PetData;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0019\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u001f\u0010\r\u001a\u00020\u0004*\u00060\u000bj\u0002`\f2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R(\u0010\u0015\u001a\u0004\u0018\u00010\b2\b\u0010\u0014\u001a\u0004\u0018\u00010\b8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001bR\u0014\u0010\u001e\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001bR\u0014\u0010\u001f\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u001bR\u0014\u0010 \u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010\u001bR\u0014\u0010!\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u001b\u00a8\u0006\""}, d2={"Lorg/cobalt/internal/visual/PetTabListParser;", "", "<init>", "()V", "", "update", "", "text", "Lorg/cobalt/internal/visual/PetData;", "parse", "(Ljava/lang/String;)Lorg/cobalt/internal/visual/PetData;", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "appendSanitizedLine", "(Ljava/lang/StringBuilder;Ljava/lang/String;)V", "stripFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "value", "current", "Lorg/cobalt/internal/visual/PetData;", "getCurrent", "()Lorg/cobalt/internal/visual/PetData;", "Lkotlin/text/Regex;", "petNameLevel", "Lkotlin/text/Regex;", "trailingSuffix", "petItem", "petXpNumbers", "petXpPercent", "petXpMax", "formattingCode", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPetTabListParser.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PetTabListParser.kt\norg/cobalt/internal/visual/PetTabListParser\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,137:1\n1#2:138\n1915#3,2:139\n*S KotlinDebug\n*F\n+ 1 PetTabListParser.kt\norg/cobalt/internal/visual/PetTabListParser\n*L\n58#1:139,2\n*E\n"})
public final class PetTabListParser {
    @NotNull
    public static final PetTabListParser INSTANCE = new PetTabListParser();
    @NotNull
    private static final class_310 mc;
    @Nullable
    private static PetData current;
    @NotNull
    private static final Regex petNameLevel;
    @NotNull
    private static final Regex trailingSuffix;
    @NotNull
    private static final Regex petItem;
    @NotNull
    private static final Regex petXpNumbers;
    @NotNull
    private static final Regex petXpPercent;
    @NotNull
    private static final Regex petXpMax;
    @NotNull
    private static final Regex formattingCode;

    private PetTabListParser() {
    }

    @Nullable
    public final PetData getCurrent() {
        return current;
    }

    public final void update() {
        String rawText;
        Object object;
        TabOverlayAccessor tabOverlayAccessor;
        TabOverlayAccessor it;
        StringBuilder stringBuilder;
        StringBuilder $this$update_u24lambda_u240 = stringBuilder = new StringBuilder();
        boolean bl = false;
        class_355 class_3552 = PetTabListParser.mc.field_1705.method_1750();
        TabOverlayAccessor tabOverlay = class_3552 instanceof TabOverlayAccessor ? (TabOverlayAccessor)class_3552 : null;
        TabOverlayAccessor tabOverlayAccessor2 = tabOverlay;
        if (tabOverlayAccessor2 != null && (tabOverlayAccessor2 = tabOverlayAccessor2.getHeader()) != null) {
            it = tabOverlayAccessor2;
            boolean bl2 = false;
            String string = it.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            INSTANCE.appendSanitizedLine($this$update_u24lambda_u240, string);
        }
        if ((tabOverlayAccessor = tabOverlay) != null && (tabOverlayAccessor = tabOverlayAccessor.getFooter()) != null) {
            it = tabOverlayAccessor;
            boolean bl3 = false;
            String string = it.getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            INSTANCE.appendSanitizedLine($this$update_u24lambda_u240, string);
        }
        if ((object = mc.method_1562()) != null && (object = object.method_45732()) != null) {
            Iterable $this$forEach$iv = (Iterable)object;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                class_2561 it2;
                class_640 info = (class_640)element$iv;
                boolean bl4 = false;
                if (info.method_2971() == null) continue;
                boolean bl5 = false;
                String string = it2.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                INSTANCE.appendSanitizedLine($this$update_u24lambda_u240, string);
            }
        }
        current = StringsKt.isBlank((CharSequence)(rawText = stringBuilder.toString())) ? null : this.parse(rawText);
    }

    private final PetData parse(String text) {
        String name = null;
        name = "";
        Ref.IntRef level2 = new Ref.IntRef();
        String heldItem = null;
        heldItem = "";
        long xpCurrent = 0L;
        long xpRequired = 0L;
        boolean xpLineFound = false;
        boolean xpIsMax = false;
        boolean xpIsPercentage = false;
        for (String line : StringsKt.lines((CharSequence)text)) {
            MatchResult match;
            String trimmed = ((Object)StringsKt.trim((CharSequence)line)).toString();
            if (((CharSequence)trimmed).length() == 0) continue;
            if (((CharSequence)name).length() == 0) {
                if (Regex.find$default((Regex)petNameLevel, (CharSequence)trimmed, (int)0, (int)2, null) != null) {
                    CharSequence charSequence;
                    CharSequence charSequence2;
                    MatchResult match2;
                    boolean bl = false;
                    Ref.IntRef intRef = level2;
                    CharSequence charSequence3 = (CharSequence)match2.getGroupValues().get(1);
                    if (charSequence3.length() == 0) {
                        Ref.IntRef intRef2 = intRef;
                        boolean bl2 = false;
                        charSequence2 = (String)match2.getGroupValues().get(4);
                        intRef = intRef2;
                    } else {
                        charSequence2 = charSequence3;
                    }
                    Integer n = StringsKt.toIntOrNull((String)((String)charSequence2));
                    intRef.element = n != null ? n : 0;
                    charSequence3 = (CharSequence)match2.getGroupValues().get(2);
                    if (charSequence3.length() == 0) {
                        boolean bl3 = false;
                        charSequence = (String)match2.getGroupValues().get(3);
                    } else {
                        charSequence = charSequence3;
                    }
                    String raw = ((Object)StringsKt.trim((CharSequence)((String)charSequence))).toString();
                    name = ((Object)StringsKt.trim((CharSequence)trailingSuffix.replace((CharSequence)raw, ""))).toString();
                }
            }
            if (((CharSequence)heldItem).length() == 0) {
                if (Regex.find$default((Regex)petItem, (CharSequence)trimmed, (int)0, (int)2, null) != null) {
                    MatchResult it;
                    boolean bl = false;
                    heldItem = ((Object)StringsKt.trim((CharSequence)((String)it.getGroupValues().get(1)))).toString();
                }
            }
            if (xpLineFound) continue;
            if (petXpNumbers.containsMatchIn((CharSequence)trimmed)) {
                Intrinsics.checkNotNull((Object)Regex.find$default((Regex)petXpNumbers, (CharSequence)trimmed, (int)0, (int)2, null));
                boolean bl = false;
                Long l = StringsKt.toLongOrNull((String)StringsKt.replace$default((String)((String)match.getGroupValues().get(1)), (String)",", (String)"", (boolean)false, (int)4, null));
                xpCurrent = l != null ? l : 0L;
                Long l2 = StringsKt.toLongOrNull((String)StringsKt.replace$default((String)((String)match.getGroupValues().get(2)), (String)",", (String)"", (boolean)false, (int)4, null));
                xpRequired = l2 != null ? l2 : 0L;
                xpLineFound = true;
                continue;
            }
            if (petXpPercent.containsMatchIn((CharSequence)trimmed)) {
                Intrinsics.checkNotNull((Object)Regex.find$default((Regex)petXpPercent, (CharSequence)trimmed, (int)0, (int)2, null));
                boolean bl = false;
                Float f = StringsKt.toFloatOrNull((String)((String)match.getGroupValues().get(1)));
                float pct = f != null ? f.floatValue() : 0.0f;
                xpCurrent = (long)(pct * (float)100);
                xpRequired = 10000L;
                xpLineFound = true;
                xpIsPercentage = true;
                continue;
            }
            if (!petXpMax.containsMatchIn((CharSequence)trimmed)) continue;
            xpLineFound = true;
            xpIsMax = true;
        }
        if (((CharSequence)name).length() == 0) {
            return null;
        }
        return new PetData(name, level2.element, heldItem, xpCurrent, xpRequired, xpIsMax, xpIsPercentage);
    }

    private final void appendSanitizedLine(StringBuilder $this$appendSanitizedLine, String text) {
        String stripped = this.stripFormatting(text);
        if (!StringsKt.isBlank((CharSequence)stripped)) {
            $this$appendSanitizedLine.append(stripped).append('\n');
        }
    }

    private final String stripFormatting(String text) {
        CharSequence charSequence = text;
        Regex regex = formattingCode;
        String string = "";
        return regex.replace(charSequence, string);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        petNameLevel = new Regex("(?:\\[(?:Lv|Lvl|Level)\\s*(\\d+)]\\s*(.+)|(.+?)\\s*\\[(?:Lv|Lvl|Level)\\s*(\\d+)])", RegexOption.IGNORE_CASE);
        trailingSuffix = new Regex("\\s*\\([^)]*\\)\\s*$");
        petItem = new Regex("(?:pet\\s+item|held\\s+item|item):\\s*(.+)", RegexOption.IGNORE_CASE);
        petXpNumbers = new Regex("(?:pet\\s+)?(?:xp|exp):\\s*([\\d,]+)\\s*/\\s*([\\d,]+)", RegexOption.IGNORE_CASE);
        petXpPercent = new Regex("(?:pet\\s+)?(?:xp|exp):\\s*([\\d.]+)%", RegexOption.IGNORE_CASE);
        petXpMax = new Regex("(?:pet\\s+)?(?:xp|exp):\\s*max(?:ed)?", RegexOption.IGNORE_CASE);
        formattingCode = new Regex("\\u00A7[0-9A-FK-ORa-fk-or]");
    }
}

