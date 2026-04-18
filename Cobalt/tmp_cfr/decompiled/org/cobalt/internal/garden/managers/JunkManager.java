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
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_2371
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.internal.garden.GardenConfig;
import org.cobalt.internal.garden.GardenWorkerThread;
import org.cobalt.mixin.client.GardenInventoryAccessor;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001b\u0010\u000b\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\t\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010R\"\u0010\u0011\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0011\u0010\b\"\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/garden/managers/JunkManager;", "", "<init>", "()V", "", "reset", "", "shouldDrop", "()Z", "Lkotlin/Function0;", "onComplete", "startDrop", "(Lkotlin/jvm/functions/Function0;)V", "", "", "junkList", "()Ljava/util/List;", "isHandling", "Z", "setHandling", "(Z)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nJunkManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JunkManager.kt\norg/cobalt/internal/garden/managers/JunkManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,51:1\n1807#2,3:52\n1586#2:55\n1661#2,3:56\n777#2:59\n873#2,2:60\n1807#2,3:62\n*S KotlinDebug\n*F\n+ 1 JunkManager.kt\norg/cobalt/internal/garden/managers/JunkManager\n*L\n20#1:52,3\n49#1:55\n49#1:56,3\n49#1:59\n49#1:60,2\n33#1:62,3\n*E\n"})
public final class JunkManager {
    @NotNull
    public static final JunkManager INSTANCE = new JunkManager();
    private static volatile boolean isHandling;

    private JunkManager() {
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean shouldDrop() {
        boolean bl;
        if (isHandling) {
            return false;
        }
        List<String> junkList = this.junkList();
        if (junkList.isEmpty()) {
            return false;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_746 class_7462 = mc.field_1724;
        class_1661 class_16612 = class_7462 != null ? class_7462.method_31548() : null;
        if (!(class_16612 instanceof GardenInventoryAccessor)) return false;
        class_2371<class_1799> class_23712 = (class_2371<class_1799>)class_16612;
        class_2371<class_1799> class_23713 = class_23712;
        if (class_23712 == null) return false;
        if ((class_23713 = class_23713.getItems()) == null) {
            return false;
        }
        class_2371<class_1799> inv = class_23713;
        Iterable $this$any$iv = (Iterable)inv;
        boolean $i$f$any = false;
        if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
            return false;
        }
        Iterator iterator = $this$any$iv.iterator();
        do {
            if (!iterator.hasNext()) return false;
            Object element$iv = iterator.next();
            class_1799 stack = (class_1799)element$iv;
            boolean bl2 = false;
            if (!stack.method_7960()) {
                boolean bl3;
                Iterable $this$any$iv2 = junkList;
                boolean $i$f$any2 = false;
                if ($this$any$iv2 instanceof Collection && ((Collection)$this$any$iv2).isEmpty()) {
                    bl3 = false;
                } else {
                    for (Object element$iv2 : $this$any$iv2) {
                        String junk = (String)element$iv2;
                        boolean bl4 = false;
                        String string = stack.method_7964().getString();
                        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                        String string2 = string.toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                        if (!StringsKt.contains$default((CharSequence)string2, (CharSequence)junk, (boolean)false, (int)2, null)) continue;
                        return true;
                    }
                    bl3 = false;
                }
                if (bl3) {
                    return true;
                }
            }
            bl = false;
        } while (!bl);
        return true;
    }

    public final void startDrop(@NotNull Function0<Unit> onComplete) {
        Intrinsics.checkNotNullParameter(onComplete, (String)"onComplete");
        isHandling = true;
        GardenWorkerThread.INSTANCE.submit("junk-drop", (Function0<Unit>)((Function0)() -> JunkManager.startDrop$lambda$0(onComplete)));
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> junkList() {
        void $this$filterTo$iv$iv;
        String it;
        Iterable $this$mapTo$iv$iv;
        String[] stringArray = new String[]{","};
        Iterable $this$map$iv = StringsKt.split$default((CharSequence)GardenConfig.INSTANCE.getJunkItems(), (String[])stringArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string2 = ((Object)StringsKt.trim((CharSequence)it)).toString().toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
            collection.add(string2);
        }
        Iterable $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$filter = false;
        $this$mapTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (String)element$iv$iv;
            boolean bl = false;
            boolean bl2 = !StringsKt.isBlank((CharSequence)it);
            if (!bl2) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private static final void startDrop$lambda$0$0(class_310 $mc, List $junk) {
        class_746 class_7462 = $mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        class_2371<class_1799> class_23712 = class_16612 instanceof GardenInventoryAccessor ? (class_2371<class_1799>)class_16612 : null;
        if (class_23712 == null || (class_23712 = class_23712.getItems()) == null) {
            return;
        }
        class_2371<class_1799> inv = class_23712;
        Iterator iterator = inv.iterator();
        Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
        Iterator iterator2 = iterator;
        while (iterator2.hasNext()) {
            boolean bl;
            class_1799 stack;
            block6: {
                stack = (class_1799)iterator2.next();
                if (stack.method_7960()) continue;
                Iterable $this$any$iv = $junk;
                boolean $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    bl = false;
                } else {
                    for (Object element$iv : $this$any$iv) {
                        String it = (String)element$iv;
                        boolean bl2 = false;
                        String string = stack.method_7964().getString();
                        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                        String string2 = string.toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                        if (!StringsKt.contains$default((CharSequence)string2, (CharSequence)it, (boolean)false, (int)2, null)) continue;
                        bl = true;
                        break block6;
                    }
                    bl = false;
                }
            }
            if (!bl) continue;
            player.method_7328(stack, false);
        }
    }

    private static final void startDrop$lambda$0$1(Function0 $onComplete) {
        $onComplete.invoke();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Unit startDrop$lambda$0(Function0 $onComplete) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        try {
            List<String> junk = INSTANCE.junkList();
            mc.execute(() -> JunkManager.startDrop$lambda$0$0(mc, junk));
            Thread.sleep(200L);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        finally {
            isHandling = false;
            mc.execute(() -> JunkManager.startDrop$lambda$0$1($onComplete));
        }
        return Unit.INSTANCE;
    }
}

