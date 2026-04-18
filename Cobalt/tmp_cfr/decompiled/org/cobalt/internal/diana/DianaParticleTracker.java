/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$ObjectRef
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1937
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2675
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.diana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1937;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2675;
import net.minecraft.class_2902;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001-B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\u0003J\u001d\u0010\r\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\n\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u000f\u0010\u0015\u001a\u0004\u0018\u00010\u0014\u00a2\u0006\u0004\b\u0015\u0010\u0016J%\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00140\u001a2\u0006\u0010\u0018\u001a\u00020\u00172\b\b\u0002\u0010\u0019\u001a\u00020\u0011\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0017\u0010\u001d\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0004\b\u001d\u0010\u001eR\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!R0\u0010%\u001a\u001e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020#0\"j\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020#`$8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0018\u0010'\u001a\u0004\u0018\u00010\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b'\u0010(R\u0016\u0010)\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b)\u0010*R\u0016\u0010+\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b+\u0010,\u00a8\u0006."}, d2={"Lorg/cobalt/internal/diana/DianaParticleTracker;", "", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "event", "", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "reset", "", "bx", "bz", "removeBurrow", "(II)V", "count", "()I", "", "getLastParticleMs", "()J", "Lnet/minecraft/class_243;", "getActivationPos", "()Lnet/minecraft/class_243;", "Lnet/minecraft/class_1937;", "level", "expireMs", "", "getBurrowPositions", "(Lnet/minecraft/class_1937;J)Ljava/util/List;", "getBurrowPos", "(Lnet/minecraft/class_1937;)Lnet/minecraft/class_243;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Ljava/util/HashMap;", "Lorg/cobalt/internal/diana/DianaParticleTracker$BurrowRecord;", "Lkotlin/collections/HashMap;", "seenBurrows", "Ljava/util/HashMap;", "activationPos", "Lnet/minecraft/class_243;", "lastParticleMs", "J", "packetCount", "I", "BurrowRecord", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDianaParticleTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DianaParticleTracker.kt\norg/cobalt/internal/diana/DianaParticleTracker\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,111:1\n1#2:112\n777#3:113\n873#3,2:114\n1080#3:116\n1586#3:117\n1661#3,3:118\n*S KotlinDebug\n*F\n+ 1 DianaParticleTracker.kt\norg/cobalt/internal/diana/DianaParticleTracker\n*L\n96#1:113\n96#1:114,2\n97#1:116\n100#1:117\n100#1:118,3\n*E\n"})
public final class DianaParticleTracker {
    @NotNull
    public static final DianaParticleTracker INSTANCE = new DianaParticleTracker();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final HashMap<Long, BurrowRecord> seenBurrows;
    @Nullable
    private static volatile class_243 activationPos;
    private static volatile long lastParticleMs;
    private static volatile int packetCount;

    private DianaParticleTracker() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Incoming event) {
        double dz;
        double dy;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_2596<?> class_25962 = event.getPacket();
        class_2675 class_26752 = class_25962 instanceof class_2675 ? (class_2675)class_25962 : null;
        if (class_26752 == null) {
            return;
        }
        class_2675 pkt = class_26752;
        if (!Intrinsics.areEqual((Object)pkt.method_11551().method_10295(), (Object)class_2398.field_11205)) {
            return;
        }
        class_746 class_7462 = DianaParticleTracker.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        double dx = pkt.method_11544() - player.method_23317();
        if (dx * dx + (dy = pkt.method_11547() - player.method_23318()) * dy + (dz = pkt.method_11546() - player.method_23321()) * dz < 9.0) {
            return;
        }
        if (activationPos == null) {
            activationPos = player.method_73189();
        }
        int bx = (int)Math.floor(pkt.method_11544());
        int bz = (int)Math.floor(pkt.method_11546());
        long key = (long)bx << 32 | (long)bz & 0xFFFFFFFFL;
        long now = System.currentTimeMillis();
        HashMap<Long, BurrowRecord> hashMap = seenBurrows;
        synchronized (hashMap) {
            BurrowRecord rec;
            boolean bl = false;
            BurrowRecord burrowRecord = rec = seenBurrows.get(key);
            if (burrowRecord != null) {
                burrowRecord.setLastSeenMs(now);
            } else {
                ((Map)seenBurrows).put(key, new BurrowRecord(bx, bz, now));
            }
            Unit unit = Unit.INSTANCE;
        }
        lastParticleMs = now;
        int n = packetCount;
        packetCount = n + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void reset() {
        HashMap<Long, BurrowRecord> hashMap = seenBurrows;
        synchronized (hashMap) {
            boolean bl = false;
            seenBurrows.clear();
            Unit unit = Unit.INSTANCE;
        }
        activationPos = null;
        lastParticleMs = 0L;
        packetCount = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void removeBurrow(int bx, int bz) {
        long key = (long)bx << 32 | (long)bz & 0xFFFFFFFFL;
        HashMap<Long, BurrowRecord> hashMap = seenBurrows;
        synchronized (hashMap) {
            boolean bl = false;
            BurrowRecord burrowRecord = seenBurrows.remove(key);
        }
    }

    public final int count() {
        return packetCount;
    }

    public final long getLastParticleMs() {
        return lastParticleMs;
    }

    @Nullable
    public final class_243 getActivationPos() {
        return activationPos;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @NotNull
    public final List<class_243> getBurrowPositions(@NotNull class_1937 level2, long expireMs) {
        void $this$mapTo$iv$iv;
        Object $i$a$-synchronized-DianaParticleTracker$getBurrowPositions$22;
        Object destination$iv$iv;
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        long now = System.currentTimeMillis();
        Ref.ObjectRef snapshot = new Ref.ObjectRef();
        HashMap<Long, BurrowRecord> hashMap = seenBurrows;
        synchronized (hashMap) {
            void $this$sortedByDescending$iv;
            void $this$filterTo$iv$iv;
            Iterable $this$filter$iv;
            boolean $i$a$-synchronized-DianaParticleTracker$getBurrowPositions$22 = false;
            Collection<BurrowRecord> collection = seenBurrows.values();
            Intrinsics.checkNotNullExpressionValue(collection, (String)"<get-values>(...)");
            Iterable iterable = collection;
            Ref.ObjectRef objectRef = snapshot;
            boolean $i$f$filter = false;
            void var13_15 = $this$filter$iv;
            destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                BurrowRecord it = (BurrowRecord)element$iv$iv;
                boolean bl = false;
                if (!(now - it.getLastSeenMs() <= expireMs)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filter$iv = (List)destination$iv$iv;
            boolean $i$f$sortedByDescending = false;
            objectRef.element = CollectionsKt.toList((Iterable)CollectionsKt.sortedWith((Iterable)$this$sortedByDescending$iv, (Comparator)new Comparator(){

                public final int compare(T a, T b) {
                    BurrowRecord it = (BurrowRecord)b;
                    boolean bl = false;
                    Comparable comparable = Long.valueOf(it.getLastSeenMs());
                    it = (BurrowRecord)a;
                    Comparable comparable2 = comparable;
                    bl = false;
                    return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Long.valueOf(it.getLastSeenMs()));
                }
            }));
            $i$a$-synchronized-DianaParticleTracker$getBurrowPositions$22 = Unit.INSTANCE;
        }
        Iterable $this$map$iv = (Iterable)snapshot.element;
        boolean $i$f$map = false;
        $i$a$-synchronized-DianaParticleTracker$getBurrowPositions$22 = $this$map$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void rec;
            destination$iv$iv = (BurrowRecord)item$iv$iv;
            Collection collection = destination$iv$iv2;
            boolean bl = false;
            int surfaceY = level2.method_8624(class_2902.class_2903.field_13197, rec.getBx(), rec.getBz()) - 1;
            collection.add(new class_243((double)rec.getBx() + 0.5, (double)surfaceY, (double)rec.getBz() + 0.5));
        }
        return (List)destination$iv$iv2;
    }

    public static /* synthetic */ List getBurrowPositions$default(DianaParticleTracker dianaParticleTracker, class_1937 class_19372, long l, int n, Object object) {
        if ((n & 2) != 0) {
            l = Long.MAX_VALUE;
        }
        return dianaParticleTracker.getBurrowPositions(class_19372, l);
    }

    @Nullable
    public final class_243 getBurrowPos(@NotNull class_1937 level2) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        return (class_243)CollectionsKt.firstOrNull((List)DianaParticleTracker.getBurrowPositions$default(this, level2, 0L, 2, null));
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        seenBurrows = new HashMap();
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\nJ\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0018\u001a\u0004\b\u001a\u0010\nR\"\u0010\u0006\u001a\u00020\u00058\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001c\u0010\r\"\u0004\b\u001d\u0010\u001e\u00a8\u0006\u001f"}, d2={"Lorg/cobalt/internal/diana/DianaParticleTracker$BurrowRecord;", "", "", "bx", "bz", "", "lastSeenMs", "<init>", "(IIJ)V", "component1", "()I", "component2", "component3", "()J", "copy", "(IIJ)Lorg/cobalt/internal/diana/DianaParticleTracker$BurrowRecord;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getBx", "getBz", "J", "getLastSeenMs", "setLastSeenMs", "(J)V", "cobalt"})
    public static final class BurrowRecord {
        private final int bx;
        private final int bz;
        private long lastSeenMs;

        public BurrowRecord(int bx, int bz, long lastSeenMs) {
            this.bx = bx;
            this.bz = bz;
            this.lastSeenMs = lastSeenMs;
        }

        public final int getBx() {
            return this.bx;
        }

        public final int getBz() {
            return this.bz;
        }

        public final long getLastSeenMs() {
            return this.lastSeenMs;
        }

        public final void setLastSeenMs(long l) {
            this.lastSeenMs = l;
        }

        public final int component1() {
            return this.bx;
        }

        public final int component2() {
            return this.bz;
        }

        public final long component3() {
            return this.lastSeenMs;
        }

        @NotNull
        public final BurrowRecord copy(int bx, int bz, long lastSeenMs) {
            return new BurrowRecord(bx, bz, lastSeenMs);
        }

        public static /* synthetic */ BurrowRecord copy$default(BurrowRecord burrowRecord, int n, int n2, long l, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = burrowRecord.bx;
            }
            if ((n3 & 2) != 0) {
                n2 = burrowRecord.bz;
            }
            if ((n3 & 4) != 0) {
                l = burrowRecord.lastSeenMs;
            }
            return burrowRecord.copy(n, n2, l);
        }

        @NotNull
        public String toString() {
            return "BurrowRecord(bx=" + this.bx + ", bz=" + this.bz + ", lastSeenMs=" + this.lastSeenMs + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.bx);
            result = result * 31 + Integer.hashCode(this.bz);
            result = result * 31 + Long.hashCode(this.lastSeenMs);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BurrowRecord)) {
                return false;
            }
            BurrowRecord burrowRecord = (BurrowRecord)other;
            if (this.bx != burrowRecord.bx) {
                return false;
            }
            if (this.bz != burrowRecord.bz) {
                return false;
            }
            return this.lastSeenMs == burrowRecord.lastSeenMs;
        }
    }
}

