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
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.mixin.client.GardenInventoryAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001b\u0010\u000b\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010R\"\u0010\u0011\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0011\u0010\b\"\u0004\b\u0013\u0010\u0014R\"\u0010\u0016\u001a\u00020\u00158\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u00158\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u0017\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/garden/managers/GeorgeManager;", "", "<init>", "()V", "", "reset", "", "shouldSell", "()Z", "Lkotlin/Function0;", "onComplete", "startSell", "(Lkotlin/jvm/functions/Function0;)V", "", "Lnet/minecraft/class_1799;", "findTargetPets", "()Ljava/util/List;", "isHandling", "Z", "setHandling", "(Z)V", "", "lastSellTime", "J", "getLastSellTime", "()J", "setLastSellTime", "(J)V", "SELL_COOLDOWN_MS", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGeorgeManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GeorgeManager.kt\norg/cobalt/internal/garden/managers/GeorgeManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,69:1\n1586#2:70\n1661#2,3:71\n777#2:74\n873#2:75\n1807#2,3:76\n874#2:79\n296#2,2:80\n*S KotlinDebug\n*F\n+ 1 GeorgeManager.kt\norg/cobalt/internal/garden/managers/GeorgeManager\n*L\n61#1:70\n61#1:71,3\n62#1:74\n62#1:75\n65#1:76,3\n62#1:79\n38#1:80,2\n*E\n"})
public final class GeorgeManager {
    @NotNull
    public static final GeorgeManager INSTANCE = new GeorgeManager();
    private static volatile boolean isHandling;
    private static volatile long lastSellTime;
    private static final long SELL_COOLDOWN_MS = 60000L;

    private GeorgeManager() {
    }

    public final boolean isHandling() {
        return isHandling;
    }

    public final void setHandling(boolean bl) {
        isHandling = bl;
    }

    public final long getLastSellTime() {
        return lastSellTime;
    }

    public final void setLastSellTime(long l) {
        lastSellTime = l;
    }

    public final void reset() {
        isHandling = false;
        lastSellTime = 0L;
    }

    public final boolean shouldSell() {
        if (isHandling) {
            return false;
        }
        if (System.currentTimeMillis() - lastSellTime < 60000L) {
            return false;
        }
        return !((Collection)this.findTargetPets()).isEmpty();
    }

    public final void startSell(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        isHandling = true;
        GardenWorkerThread.INSTANCE.submit("george-sell", (Function0<Unit>)((Function0)() -> GeorgeManager.startSell$lambda$0(onComplete)));
    }

    /*
     * Unable to fully structure code
     */
    private final List<class_1799> findTargetPets() {
        v0 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getInstance(...)");
        mc = v0;
        v1 = mc.field_1724;
        var4_2 = v1 != null ? v1.method_31548() : null;
        v2 = var4_2 instanceof GardenInventoryAccessor != false ? (class_2371<class_1799>)var4_2 : null;
        if (v2 == null || (v2 = v2.getItems()) == null) {
            return CollectionsKt.emptyList();
        }
        inv = v2;
        var4_2 = new String[]{","};
        $this$map$iv = StringsKt.split$default((CharSequence)GardenConfig.INSTANCE.getGeorgeRarity(), (String[])var4_2, (boolean)false, (int)0, (int)6, null);
        $i$f$map = false;
        var6_5 = $this$map$iv;
        destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        $i$f$mapTo = false;
        for (T item$iv$iv : $this$mapTo$iv$iv) {
            var11_10 = (String)item$iv$iv;
            var20_12 = destination$iv$iv;
            $i$a$-map-GeorgeManager$findTargetPets$rarities$1 = false;
            v3 = StringsKt.trim((CharSequence)((CharSequence)it)).toString().toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"toUpperCase(...)");
            var20_12.add(v3);
        }
        rarities = (List)destination$iv$iv;
        $this$filter$iv = (Iterable)inv;
        $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList<E>();
        $i$f$filterTo = false;
        for (T element$iv$iv : $this$filterTo$iv$iv) {
            block10: {
                block8: {
                    block9: {
                        stack = (class_1799)element$iv$iv;
                        $i$a$-filter-GeorgeManager$findTargetPets$1 = false;
                        if (!stack.method_7960()) break block9;
                        v4 = false;
                        break block10;
                    }
                    Intrinsics.checkNotNullExpressionValue((Object)stack.method_7964().getString(), (String)"getString(...)");
                    if (!StringsKt.contains((CharSequence)name, (CharSequence)"pet", (boolean)true)) ** GOTO lbl-1000
                    $this$any$iv = rarities;
                    $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        v5 = false;
                    } else {
                        for (T element$iv : $this$any$iv) {
                            it = (String)element$iv;
                            $i$a$-any-GeorgeManager$findTargetPets$1$1 = false;
                            if (!StringsKt.contains((CharSequence)name, (CharSequence)it, (boolean)true)) continue;
                            v5 = true;
                            break block8;
                        }
                        v5 = false;
                    }
                }
                if (v5) {
                    v4 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v4 = false;
                }
            }
            if (!v4) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private static final void startSell$lambda$0$0(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null || (class_7462 = class_7462.field_3944) == null) break block0;
            class_7462.method_45730("george");
        }
    }

    private static final void startSell$lambda$0$1(class_310 $mc) {
        block4: {
            Object v3;
            class_465 screen;
            block3: {
                class_437 class_4372 = $mc.field_1755;
                class_465 class_4652 = class_4372 instanceof class_465 ? (class_465)class_4372 : null;
                if (class_4652 == null) {
                    return;
                }
                screen = class_4652;
                class_2371 class_23712 = screen.method_17577().field_7761;
                Intrinsics.checkNotNullExpressionValue((Object)class_23712, (String)"slots");
                Iterable $this$firstOrNull$iv = (Iterable)class_23712;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    class_1735 slot = (class_1735)element$iv;
                    boolean bl = false;
                    String string = slot.method_7677().method_7964().getString();
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                    if (!StringsKt.contains((CharSequence)string, (CharSequence)"sell", (boolean)true)) continue;
                    v3 = element$iv;
                    break block3;
                }
                v3 = null;
            }
            class_1735 class_17352 = v3;
            if (class_17352 == null) {
                return;
            }
            class_1735 sellSlot = class_17352;
            class_636 class_6362 = $mc.field_1761;
            if (class_6362 == null) break block4;
            int n = screen.method_17577().field_7763;
            int n2 = sellSlot.field_7874;
            class_746 class_7462 = $mc.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            class_6362.method_2906(n, n2, 0, class_1713.field_7790, (class_1657)class_7462);
        }
    }

    private static final void startSell$lambda$0$2(class_310 $mc) {
        block0: {
            class_746 class_7462 = $mc.field_1724;
            if (class_7462 == null) break block0;
            class_7462.method_7346();
        }
    }

    private static final void startSell$lambda$0$3(Function0 $onComplete) {
        $onComplete.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startSell$lambda$0(Function0 $onComplete) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            if (INSTANCE.findTargetPets().isEmpty()) {
                Unit unit = Unit.INSTANCE;
                return unit;
            }
            mc.execute(() -> GeorgeManager.startSell$lambda$0$0(mc));
            Thread.sleep(1500L);
            mc.execute(() -> GeorgeManager.startSell$lambda$0$1(mc));
            Thread.sleep(1000L);
            mc.execute(() -> GeorgeManager.startSell$lambda$0$2(mc));
            Thread.sleep(300L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isHandling = false;
            lastSellTime = System.currentTimeMillis();
            mc.execute(() -> GeorgeManager.startSell$lambda$0$3($onComplete));
        }
        return Unit.INSTANCE;
    }
}

