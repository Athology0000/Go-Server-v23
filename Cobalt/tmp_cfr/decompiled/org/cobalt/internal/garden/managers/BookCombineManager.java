/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_310
 *  net.minecraft.class_465
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_310;
import net.minecraft.class_465;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.mixin.client.GardenInventoryAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001b\u0010\u000b\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\t\u00a2\u0006\u0004\b\u000b\u0010\fR\"\u0010\r\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\r\u0010\u000e\u001a\u0004\b\r\u0010\b\"\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0011"}, d2={"Lorg/cobalt/internal/garden/managers/BookCombineManager;", "", "<init>", "()V", "", "reset", "", "shouldCombine", "()Z", "Lkotlin/Function0;", "onComplete", "startCombine", "(Lkotlin/jvm/functions/Function0;)V", "isHandling", "Z", "setHandling", "(Z)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nBookCombineManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BookCombineManager.kt\norg/cobalt/internal/garden/managers/BookCombineManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,65:1\n1834#2,4:66\n777#2:70\n873#2,2:71\n1915#2,2:73\n*S KotlinDebug\n*F\n+ 1 BookCombineManager.kt\norg/cobalt/internal/garden/managers/BookCombineManager\n*L\n21#1:66,4\n40#1:70\n40#1:71,2\n46#1:73,2\n*E\n"})
public final class BookCombineManager {
    @NotNull
    public static final BookCombineManager INSTANCE = new BookCombineManager();
    private static volatile boolean isHandling;

    private BookCombineManager() {
    }

    public final boolean isHandling() {
        return isHandling;
    }

    public final void setHandling(boolean bl) {
        isHandling = bl;
    }

    public final void reset() {
        isHandling = false;
    }

    /*
     * Unable to fully structure code
     */
    public final boolean shouldCombine() {
        block6: {
            block5: {
                if (BookCombineManager.isHandling) {
                    return false;
                }
                v0 = class_310.method_1551();
                Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getInstance(...)");
                mc = v0;
                v1 = mc.field_1724;
                var4_2 = v1 != null ? v1.method_31548() : null;
                v2 = var4_2 instanceof GardenInventoryAccessor != false ? (class_2371<class_1799>)var4_2 : null;
                if (v2 == null || (v2 = v2.getItems()) == null) {
                    return false;
                }
                inv = v2;
                level = GardenConfig.INSTANCE.getBookCombineLevel();
                $this$count$iv = (Iterable)inv;
                $i$f$count = false;
                if (!($this$count$iv instanceof Collection) || !((Collection)$this$count$iv).isEmpty()) break block5;
                v3 = 0;
                break block6;
            }
            count$iv = 0;
            for (T element$iv : $this$count$iv) {
                stack = (class_1799)element$iv;
                $i$a$-count-BookCombineManager$shouldCombine$count$1 = false;
                if (stack.method_7960()) ** GOTO lbl-1000
                v4 = stack.method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"getString(...)");
                if (!StringsKt.contains((CharSequence)v4, (CharSequence)"enchanted book", (boolean)true)) ** GOTO lbl-1000
                v5 = stack.method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"getString(...)");
                if (StringsKt.contains((CharSequence)v5, (CharSequence)String.valueOf(level), (boolean)true)) {
                    v6 = true;
                } else lbl-1000:
                // 3 sources

                {
                    v6 = false;
                }
                if (!v6 || ++count$iv >= 0) continue;
                CollectionsKt.throwCountOverflow();
            }
            v3 = count$iv;
        }
        count = v3;
        return count >= 2;
    }

    public final void startCombine(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        isHandling = true;
        GardenWorkerThread.INSTANCE.submit("book-combine", (Function0<Unit>)((Function0)() -> BookCombineManager.startCombine$lambda$0(onComplete)));
    }

    private static final void startCombine$lambda$0$0(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45730("anvil");
        }
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private static final void startCombine$lambda$0$1(class_310 $mc) {
        var3_1 = $mc.field_1755;
        v0 = var3_1 instanceof class_465 != false ? (class_465)var3_1 : null;
        if (v0 == null) {
            return;
        }
        screen = v0;
        v1 = $mc.field_1724;
        var4_4 /* !! */  = v1 != null ? v1.method_31548() : null;
        v2 = var4_4 /* !! */  instanceof GardenInventoryAccessor != false ? (class_2371<class_1799>)var4_4 /* !! */  : null;
        if (v2 == null || (v2 = v2.getItems()) == null) {
            return;
        }
        inv = v2;
        level = GardenConfig.INSTANCE.getBookCombineLevel();
        $this$filter$iv = (Iterable)CollectionsKt.getIndices((Collection)((Collection)inv));
        $i$f$filter = false;
        var7_8 = $this$filter$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$filterTo = false;
        for (T element$iv$iv : $this$filterTo$iv$iv) {
            i = ((Number)element$iv$iv).intValue();
            $i$a$-filter-BookCombineManager$startCombine$1$2$bookSlots$1 = false;
            v3 = inv.get(i);
            Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"get(...)");
            s = (class_1799)v3;
            if (s.method_7960()) ** GOTO lbl-1000
            v4 = s.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"getString(...)");
            if (!StringsKt.contains((CharSequence)v4, (CharSequence)"enchanted book", (boolean)true)) ** GOTO lbl-1000
            v5 = s.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"getString(...)");
            if (StringsKt.contains((CharSequence)v5, (CharSequence)String.valueOf(level), (boolean)true)) {
                v6 = true;
            } else lbl-1000:
            // 3 sources

            {
                v6 = false;
            }
            if (!v6) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        bookSlots = CollectionsKt.take((Iterable)((List)destination$iv$iv), (int)2);
        $this$forEach$iv = bookSlots;
        $i$f$forEach = false;
        for (E element$iv : $this$forEach$iv) {
            invSlot = ((Number)element$iv).intValue();
            $i$a$-forEach-BookCombineManager$startCombine$1$2$1 = false;
            v7 = $mc.field_1761;
            if (v7 == null) continue;
            v8 = screen.method_17577().field_7763;
            v9 = screen.method_17577().field_7761.size() - 36 + invSlot;
            v10 = $mc.field_1724;
            Intrinsics.checkNotNull((Object)v10);
            v7.method_2906(v8, v9, 0, class_1713.field_7794, (class_1657)v10);
        }
    }

    private static final void startCombine$lambda$0$2(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) break block0;
            class_7462.method_7346();
        }
    }

    private static final void startCombine$lambda$0$3(Function0 $onComplete) {
        $onComplete.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startCombine$lambda$0(Function0 $onComplete) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            mc.execute(() -> BookCombineManager.startCombine$lambda$0$0(mc));
            Thread.sleep(1200L);
            mc.execute(() -> BookCombineManager.startCombine$lambda$0$1(mc));
            Thread.sleep(800L);
            mc.execute(() -> BookCombineManager.startCombine$lambda$0$2(mc));
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isHandling = false;
            mc.execute(() -> BookCombineManager.startCombine$lambda$0$3($onComplete));
        }
        return Unit.INSTANCE;
    }
}

