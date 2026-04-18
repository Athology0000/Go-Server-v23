/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.pathing.processing.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0016B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\t\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ/\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u0010\u0010\u0011R \u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u000b0\u00128\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/AvoidanceCache;", "", "<init>", "()V", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338;", "pos", "", "isAvoided", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "", "ttlTicks", "", "radius", "", "mark", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;JI)V", "Ljava/util/concurrent/ConcurrentHashMap;", "Lorg/cobalt/api/pathfinder/pathing/processing/impl/AvoidanceCache$Key;", "avoid", "Ljava/util/concurrent/ConcurrentHashMap;", "Key", "cobalt"})
public final class AvoidanceCache {
    @NotNull
    public static final AvoidanceCache INSTANCE = new AvoidanceCache();
    @NotNull
    private static final ConcurrentHashMap<Key, Long> avoid = new ConcurrentHashMap();

    private AvoidanceCache() {
    }

    public final boolean isAvoided(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        String string = level2.method_27983().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String dimension = string;
        Key key = new Key(dimension, pos.method_10063());
        Long l = avoid.get(key);
        if (l == null) {
            return false;
        }
        long expiry = l;
        long now = level2.method_75260();
        if (now > expiry) {
            avoid.remove(key);
            return false;
        }
        return true;
    }

    public final void mark(@NotNull class_1937 level2, @NotNull class_2338 pos, long ttlTicks, int radius) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        long now = level2.method_75260();
        long expiry = now + ttlTicks;
        String string = level2.method_27983().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String dimension = string;
        int dx = -radius;
        if (dx <= radius) {
            while (true) {
                int dy;
                if ((dy = -radius) <= radius) {
                    while (true) {
                        int dz;
                        if ((dz = -radius) <= radius) {
                            while (true) {
                                class_2338 p;
                                Intrinsics.checkNotNullExpressionValue((Object)pos.method_10069(dx, dy, dz), (String)"offset(...)");
                                ((Map)avoid).put(new Key(dimension, p.method_10063()), expiry);
                                if (dz == radius) break;
                                ++dz;
                            }
                        }
                        if (dy == radius) break;
                        ++dy;
                    }
                }
                if (dx == radius) break;
                ++dx;
            }
        }
    }

    public static /* synthetic */ void mark$default(AvoidanceCache avoidanceCache, class_1937 class_19372, class_2338 class_23382, long l, int n, int n2, Object object) {
        if ((n2 & 8) != 0) {
            n = 0;
        }
        avoidanceCache.mark(class_19372, class_23382, l, n);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\tR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/pathfinder/pathing/processing/impl/AvoidanceCache$Key;", "", "", "dimension", "", "pos", "<init>", "(Ljava/lang/String;J)V", "component1", "()Ljava/lang/String;", "component2", "()J", "copy", "(Ljava/lang/String;J)Lorg/cobalt/api/pathfinder/pathing/processing/impl/AvoidanceCache$Key;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getDimension", "J", "getPos", "cobalt"})
    private static final class Key {
        @NotNull
        private final String dimension;
        private final long pos;

        public Key(@NotNull String dimension, long pos) {
            Intrinsics.checkNotNullParameter((Object)dimension, (String)"dimension");
            this.dimension = dimension;
            this.pos = pos;
        }

        @NotNull
        public final String getDimension() {
            return this.dimension;
        }

        public final long getPos() {
            return this.pos;
        }

        @NotNull
        public final String component1() {
            return this.dimension;
        }

        public final long component2() {
            return this.pos;
        }

        @NotNull
        public final Key copy(@NotNull String dimension, long pos) {
            Intrinsics.checkNotNullParameter((Object)dimension, (String)"dimension");
            return new Key(dimension, pos);
        }

        public static /* synthetic */ Key copy$default(Key key, String string, long l, int n, Object object) {
            if ((n & 1) != 0) {
                string = key.dimension;
            }
            if ((n & 2) != 0) {
                l = key.pos;
            }
            return key.copy(string, l);
        }

        @NotNull
        public String toString() {
            return "Key(dimension=" + this.dimension + ", pos=" + this.pos + ")";
        }

        public int hashCode() {
            int result = this.dimension.hashCode();
            result = result * 31 + Long.hashCode(this.pos);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Key)) {
                return false;
            }
            Key key = (Key)other;
            if (!Intrinsics.areEqual((Object)this.dimension, (Object)key.dimension)) {
                return false;
            }
            return this.pos == key.pos;
        }
    }
}

