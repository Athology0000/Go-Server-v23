/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  kotlin.text.StringsKt
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.cobalt.mixin.client.TabOverlayAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0003R\"\u0010\b\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\b\u0010\t\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\"\u0010\u000e\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000e\u0010\t\u001a\u0004\b\u000f\u0010\u000b\"\u0004\b\u0010\u0010\rR\"\u0010\u0011\u001a\u00020\u00078\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\t\u001a\u0004\b\u0012\u0010\u000b\"\u0004\b\u0013\u0010\rR\u0014\u0010\u0015\u001a\u00020\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/garden/managers/PetXpTracker;", "", "<init>", "()V", "", "reset", "update", "", "currentXp", "J", "getCurrentXp", "()J", "setCurrentXp", "(J)V", "xpToNext", "getXpToNext", "setXpToNext", "totalGained", "getTotalGained", "setTotalGained", "Lkotlin/text/Regex;", "XP_REGEX", "Lkotlin/text/Regex;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPetXpTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PetXpTracker.kt\norg/cobalt/internal/garden/managers/PetXpTracker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,38:1\n1#2:39\n*E\n"})
public final class PetXpTracker {
    @NotNull
    public static final PetXpTracker INSTANCE = new PetXpTracker();
    private static volatile long currentXp;
    private static volatile long xpToNext;
    private static volatile long totalGained;
    @NotNull
    private static final Regex XP_REGEX;

    private PetXpTracker() {
    }

    public final long getCurrentXp() {
        return currentXp;
    }

    public final void setCurrentXp(long l) {
        currentXp = l;
    }

    public final long getXpToNext() {
        return xpToNext;
    }

    public final void setXpToNext(long l) {
        xpToNext = l;
    }

    public final long getTotalGained() {
        return totalGained;
    }

    public final void setTotalGained(long l) {
        totalGained = l;
    }

    public final void reset() {
        currentXp = 0L;
        xpToNext = 0L;
        totalGained = 0L;
    }

    public final void update() {
        block6: {
            String string;
            Regex regex;
            CharSequence charSequence;
            class_2561 it;
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            class_310 mc = class_3102;
            Object object = mc.field_1705.method_1750();
            TabOverlayAccessor tabOverlayAccessor = object instanceof TabOverlayAccessor ? (TabOverlayAccessor)object : null;
            if (tabOverlayAccessor == null) {
                return;
            }
            TabOverlayAccessor overlay = tabOverlayAccessor;
            Object $this$update_u24lambda_u240 = object = new StringBuilder();
            boolean bl = false;
            class_2561 class_25612 = overlay.getHeader();
            if (class_25612 != null) {
                it = class_25612;
                boolean bl2 = false;
                String string2 = it.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getString(...)");
                charSequence = string2;
                regex = new Regex("\u00a7[0-9a-fk-or]");
                string = "";
                ((StringBuilder)$this$update_u24lambda_u240).append(regex.replace(charSequence, string));
            }
            class_2561 class_25613 = overlay.getFooter();
            if (class_25613 != null) {
                it = class_25613;
                boolean bl3 = false;
                String string3 = it.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getString(...)");
                charSequence = string3;
                regex = new Regex("\u00a7[0-9a-fk-or]");
                string = "";
                ((StringBuilder)$this$update_u24lambda_u240).append(regex.replace(charSequence, string));
            }
            String text = ((StringBuilder)object).toString();
            MatchResult matchResult = Regex.find$default((Regex)XP_REGEX, (CharSequence)text, (int)0, (int)2, null);
            if (matchResult == null) break block6;
            MatchResult m = matchResult;
            boolean bl4 = false;
            Long l = StringsKt.toLongOrNull((String)StringsKt.replace$default((String)((String)m.getGroupValues().get(1)), (String)",", (String)"", (boolean)false, (int)4, null));
            if (l == null) {
                return;
            }
            long newXp = l;
            if (newXp > currentXp) {
                if (currentXp > 0L) {
                    totalGained += newXp - currentXp;
                }
            }
            currentXp = newXp;
            Long l2 = StringsKt.toLongOrNull((String)StringsKt.replace$default((String)((String)m.getGroupValues().get(2)), (String)",", (String)"", (boolean)false, (int)4, null));
            xpToNext = l2 != null ? l2 : 0L;
        }
    }

    static {
        XP_REGEX = new Regex("pet\\s+xp[:\\s]+([\\d,]+)\\s*/\\s*([\\d,]+)", RegexOption.IGNORE_CASE);
    }
}

