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
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.cobalt.internal.mining.MiningBlockRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010$\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001aB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J+\u0010\n\u001a\u00020\t2\b\u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001d\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u0010\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u001f\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R&\u0010\u0018\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/internal/mining/MiningBreakThresholds;", "", "<init>", "()V", "", "type", "", "miningSpeed", "fallbackHardness", "", "getOptimalTicks", "(Ljava/lang/String;DLjava/lang/Double;)I", "", "Lorg/cobalt/internal/mining/MiningBreakThresholds$ThresholdRow;", "getTable", "(Ljava/lang/String;)Ljava/util/List;", "hardness", "buildThresholdTable", "(D)Ljava/util/List;", "computeTicks", "(DD)I", "MAX_TABLE_SPEED", "I", "", "tables", "Ljava/util/Map;", "ThresholdRow", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningBreakThresholds.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningBreakThresholds.kt\norg/cobalt/internal/mining/MiningBreakThresholds\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,72:1\n1#2:73\n1#2:87\n296#3,2:74\n139#4,10:76\n221#4:86\n222#4:88\n149#4:89\n*S KotlinDebug\n*F\n+ 1 MiningBreakThresholds.kt\norg/cobalt/internal/mining/MiningBreakThresholds\n*L\n17#1:87\n28#1:74,2\n17#1:76,10\n17#1:86\n17#1:88\n17#1:89\n*E\n"})
public final class MiningBreakThresholds {
    @NotNull
    public static final MiningBreakThresholds INSTANCE;
    private static final int MAX_TABLE_SPEED = 100000;
    @NotNull
    private static final Map<String, List<ThresholdRow>> tables;

    private MiningBreakThresholds() {
    }

    public final int getOptimalTicks(@Nullable String type, double miningSpeed, @Nullable Double fallbackHardness) {
        double d;
        List<ThresholdRow> table;
        String normalizedType;
        String string;
        Object object;
        double speed = RangesKt.coerceAtLeast((double)miningSpeed, (double)0.0);
        if (speed <= 0.0) {
            return 0;
        }
        String string2 = type;
        if (string2 != null) {
            String string3 = string2;
            object = MiningBlockRegistry.INSTANCE;
            String p0 = string3;
            boolean bl = false;
            string = ((MiningBlockRegistry)object).normalizeType(p0);
        } else {
            string = null;
        }
        String string4 = normalizedType = string;
        if (string4 != null) {
            String it = string4;
            boolean bl = false;
            v3 = tables.get(it);
        } else {
            v3 = table = null;
        }
        if (table != null) {
            Object v4;
            block14: {
                Iterable $this$firstOrNull$iv = table;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    ThresholdRow it = (ThresholdRow)element$iv;
                    boolean bl = false;
                    if (!(speed <= (double)it.getMaxMiningSpeed())) continue;
                    v4 = element$iv;
                    break block14;
                }
                v4 = null;
            }
            object = v4;
            if (object != null) {
                Object it = object;
                boolean bl = false;
                return ((ThresholdRow)it).getTicks();
            }
        }
        Double d2 = fallbackHardness;
        if (d2 != null) {
            d = d2;
        } else {
            Double d3;
            String string5 = normalizedType;
            if (string5 != null) {
                String it = string5;
                boolean bl = false;
                d3 = MiningBlockRegistry.INSTANCE.getBLOCK_HARDNESS().get(it);
            } else {
                d3 = null;
            }
            if (d3 != null) {
                d = d3;
            } else {
                return 0;
            }
        }
        double hardness = d;
        return this.computeTicks(hardness, speed);
    }

    public static /* synthetic */ int getOptimalTicks$default(MiningBreakThresholds miningBreakThresholds, String string, double d, Double d2, int n, Object object) {
        if ((n & 4) != 0) {
            d2 = null;
        }
        return miningBreakThresholds.getOptimalTicks(string, d, d2);
    }

    @NotNull
    public final List<ThresholdRow> getTable(@NotNull String type) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        List list = tables.get(MiningBlockRegistry.INSTANCE.normalizeType(type));
        if (list == null) {
            list = CollectionsKt.emptyList();
        }
        return list;
    }

    private final List<ThresholdRow> buildThresholdTable(double hardness) {
        List rows = new ArrayList();
        double base = 30.0 * hardness;
        if (base <= 0.0) {
            return rows;
        }
        int minSpeed = 1;
        while (minSpeed <= 100000) {
            int ticks = this.computeTicks(hardness, minSpeed);
            int maxSpeedForTicks = ticks <= 1 ? 100000 : Math.min(100000, Math.max(minSpeed, (int)Math.ceil(base / (double)(ticks - 1)) - 1));
            ((Collection)rows).add(new ThresholdRow(maxSpeedForTicks, ticks));
            if (maxSpeedForTicks >= 100000) break;
            minSpeed = maxSpeedForTicks + 1;
        }
        return rows;
    }

    private final int computeTicks(double hardness, double miningSpeed) {
        if (hardness <= 0.0 || miningSpeed <= 0.0) {
            return 0;
        }
        return RangesKt.coerceAtLeast((int)((int)Math.ceil(30.0 * hardness / miningSpeed)), (int)1);
    }

    /*
     * WARNING - void declaration
     */
    static {
        void var3_3;
        void $this$mapNotNullTo$iv$iv;
        INSTANCE = new MiningBreakThresholds();
        Map $this$mapNotNull$iv = MiningBlockRegistry.INSTANCE.getBLOCK_HARDNESS();
        boolean $i$f$mapNotNull = false;
        Map map = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Pair pair;
            Double hardness;
            Map.Entry element$iv$iv$iv;
            Map.Entry element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            Map.Entry entry = element$iv$iv;
            boolean bl2 = false;
            String type = (String)entry.getKey();
            Double d = hardness = (Double)entry.getValue();
            if (d != null) {
                double it = ((Number)d).doubleValue();
                boolean bl3 = false;
                pair = TuplesKt.to((Object)MiningBlockRegistry.INSTANCE.normalizeType(type), INSTANCE.buildThresholdTable(it));
            } else {
                pair = null;
            }
            if (pair == null) continue;
            Pair it$iv$iv = pair;
            boolean bl4 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        tables = MapsKt.toMap((Iterable)((List)var3_3));
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0010\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\bJ\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/mining/MiningBreakThresholds$ThresholdRow;", "", "", "maxMiningSpeed", "ticks", "<init>", "(II)V", "component1", "()I", "component2", "copy", "(II)Lorg/cobalt/internal/mining/MiningBreakThresholds$ThresholdRow;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getMaxMiningSpeed", "getTicks", "cobalt"})
    public static final class ThresholdRow {
        private final int maxMiningSpeed;
        private final int ticks;

        public ThresholdRow(int maxMiningSpeed, int ticks) {
            this.maxMiningSpeed = maxMiningSpeed;
            this.ticks = ticks;
        }

        public final int getMaxMiningSpeed() {
            return this.maxMiningSpeed;
        }

        public final int getTicks() {
            return this.ticks;
        }

        public final int component1() {
            return this.maxMiningSpeed;
        }

        public final int component2() {
            return this.ticks;
        }

        @NotNull
        public final ThresholdRow copy(int maxMiningSpeed, int ticks) {
            return new ThresholdRow(maxMiningSpeed, ticks);
        }

        public static /* synthetic */ ThresholdRow copy$default(ThresholdRow thresholdRow, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = thresholdRow.maxMiningSpeed;
            }
            if ((n3 & 2) != 0) {
                n2 = thresholdRow.ticks;
            }
            return thresholdRow.copy(n, n2);
        }

        @NotNull
        public String toString() {
            return "ThresholdRow(maxMiningSpeed=" + this.maxMiningSpeed + ", ticks=" + this.ticks + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.maxMiningSpeed);
            result = result * 31 + Integer.hashCode(this.ticks);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ThresholdRow)) {
                return false;
            }
            ThresholdRow thresholdRow = (ThresholdRow)other;
            if (this.maxMiningSpeed != thresholdRow.maxMiningSpeed) {
                return false;
            }
            return this.ticks == thresholdRow.ticks;
        }
    }
}

