/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  net.minecraft.class_3481
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.minecraft;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\b\u0003\n\u0002\b\u000b*\u0002.1\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u000389:B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\b\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u001f\u0010\u000b\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000b\u0010\tJ\u001d\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001d\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u001d\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u001d\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0011\u0010\u000eJ-\u0010\u0016\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\u0004\b\u0016\u0010\u0017J-\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\u0004\b\u0018\u0010\u0017J%\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0004\b\u0019\u0010\u001aJ-\u0010\u001d\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0014\u00a2\u0006\u0004\b\u001d\u0010\u001eJ/\u0010#\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u00142\u0006\u0010 \u001a\u00020\u00142\b\b\u0002\u0010\"\u001a\u00020!\u00a2\u0006\u0004\b#\u0010$J\u001f\u0010&\u001a\u00020%2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b&\u0010'R\u0014\u0010(\u001a\u00020!8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0014\u0010*\u001a\u00020\u00148\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b*\u0010+R\u0014\u0010,\u001a\u00020!8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b,\u0010)R\u0014\u0010-\u001a\u00020\u00148\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b-\u0010+R\u0014\u0010/\u001a\u00020.8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b/\u00100R\u0014\u00102\u001a\u0002018\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b2\u00103R\u0014\u00104\u001a\u00020\u00148\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b4\u0010+R\u0014\u00105\u001a\u00020\u00148\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b5\u0010+R\u0014\u00106\u001a\u00020\u00148\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b6\u0010+R\u0014\u00107\u001a\u00020\u00148\u0006X\u0086T\u00a2\u0006\u0006\n\u0004\b7\u0010+\u00a8\u0006;"}, d2={"Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules;", "", "<init>", "()V", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_2338;", "pos", "walkableAt", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2338;", "raw", "resolveTarget", "", "isWalkable", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "isPassable", "isStandable", "isClimbable", "Lnet/minecraft/class_2350;", "dir", "", "len", "gapClear", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;I)Z", "hasGapBelow", "hasRunway", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;)Z", "dx", "dz", "canMoveDiagonal", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;II)Z", "chunkX", "chunkZ", "", "ttlTicks", "isChunkCached", "(Lnet/minecraft/class_1937;IIJ)Z", "Lnet/minecraft/class_2680;", "getCachedState", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;", "STATE_CACHE_TTL_TICKS", "J", "STATE_CACHE_MAX", "I", "CHUNK_CACHE_TTL_TICKS", "CHUNK_CACHE_MAX", "org/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$stateCache$1", "stateCache", "Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$stateCache$1;", "org/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$chunkCache$1", "chunkCache", "Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$chunkCache$1;", "MAX_STEP_UP", "MAX_STEP_DOWN", "MAX_CLIMB_SCAN", "MAX_JUMP_LENGTH", "StateKey", "StateEntry", "ChunkKey", "cobalt"})
public final class MinecraftPathingRules {
    @NotNull
    public static final MinecraftPathingRules INSTANCE = new MinecraftPathingRules();
    private static final long STATE_CACHE_TTL_TICKS = 4L;
    private static final int STATE_CACHE_MAX = 100000;
    private static final long CHUNK_CACHE_TTL_TICKS = 200L;
    private static final int CHUNK_CACHE_MAX = 20000;
    @NotNull
    private static final stateCache.1 stateCache = new LinkedHashMap<StateKey, StateEntry>(){

        protected boolean removeEldestEntry(Map.Entry<StateKey, StateEntry> eldest) {
            Intrinsics.checkNotNullParameter(eldest, (String)"eldest");
            return this.size() > 100000;
        }
    };
    @NotNull
    private static final chunkCache.1 chunkCache = new LinkedHashMap<ChunkKey, Long>(){

        protected boolean removeEldestEntry(Map.Entry<ChunkKey, Long> eldest) {
            Intrinsics.checkNotNullParameter(eldest, (String)"eldest");
            return this.size() > 20000;
        }
    };
    public static final int MAX_STEP_UP = 1;
    public static final int MAX_STEP_DOWN = 3;
    public static final int MAX_CLIMB_SCAN = 10;
    public static final int MAX_JUMP_LENGTH = 3;

    private MinecraftPathingRules() {
    }

    @Nullable
    public final class_2338 walkableAt(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        int dy;
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        if (this.isWalkable(level2, pos)) {
            return pos;
        }
        for (dy = 1; dy < 2; ++dy) {
            class_2338 up;
            Intrinsics.checkNotNullExpressionValue((Object)pos.method_10086(dy), (String)"above(...)");
            if (!this.isWalkable(level2, up)) continue;
            return up;
        }
        for (dy = 1; dy < 4; ++dy) {
            class_2338 down;
            Intrinsics.checkNotNullExpressionValue((Object)pos.method_10087(dy), (String)"below(...)");
            if (!this.isWalkable(level2, down)) continue;
            return down;
        }
        return null;
    }

    @Nullable
    public final class_2338 resolveTarget(@NotNull class_1937 level2, @NotNull class_2338 raw) {
        int dy;
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)raw, (String)"raw");
        if (this.isWalkable(level2, raw)) {
            return raw;
        }
        for (dy = 1; dy < 11; ++dy) {
            class_2338 above;
            Intrinsics.checkNotNullExpressionValue((Object)raw.method_10086(dy), (String)"above(...)");
            if (!this.isWalkable(level2, above)) continue;
            return above;
        }
        for (dy = 1; dy < 4; ++dy) {
            class_2338 down;
            Intrinsics.checkNotNullExpressionValue((Object)raw.method_10087(dy), (String)"below(...)");
            if (!this.isWalkable(level2, down)) continue;
            return down;
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean isWalkable(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        if (this.isClimbable(level2, pos)) {
            if (!this.isPassable(level2, pos)) return false;
            class_2338 class_23382 = pos.method_10084();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
            if (!this.isPassable(level2, class_23382)) return false;
            return true;
        }
        if (!this.isPassable(level2, pos)) return false;
        class_2338 class_23383 = pos.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"above(...)");
        if (!this.isPassable(level2, class_23383)) return false;
        class_2338 class_23384 = pos.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"below(...)");
        if (!this.isStandable(level2, class_23384)) return false;
        return true;
    }

    public final boolean isPassable(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        class_2680 state = this.getCachedState(level2, pos);
        if (this.isClimbable(level2, pos)) {
            return true;
        }
        return state.method_26227().method_15769() && state.method_26220((class_1922)level2, pos).method_1110();
    }

    public final boolean isStandable(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        class_2680 state = this.getCachedState(level2, pos);
        return state.method_26227().method_15769() && !state.method_26220((class_1922)level2, pos).method_1110();
    }

    public final boolean isClimbable(@NotNull class_1937 level2, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        return this.getCachedState(level2, pos).method_26164(class_3481.field_22414);
    }

    public final boolean gapClear(@NotNull class_1937 level2, @NotNull class_2338 pos, @NotNull class_2350 dir, int len) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        for (int i = 1; i < len; ++i) {
            class_2338 step;
            Intrinsics.checkNotNullExpressionValue((Object)pos.method_10079(dir, i), (String)"relative(...)");
            if (!this.isPassable(level2, step)) {
                return false;
            }
            class_2338 class_23382 = step.method_10084();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
            if (this.isPassable(level2, class_23382)) continue;
            return false;
        }
        return true;
    }

    public final boolean hasGapBelow(@NotNull class_1937 level2, @NotNull class_2338 pos, @NotNull class_2350 dir, int len) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        for (int i = 1; i < len; ++i) {
            class_2338 step;
            Intrinsics.checkNotNullExpressionValue((Object)pos.method_10079(dir, i), (String)"relative(...)");
            class_2338 class_23382 = step.method_10074();
            Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"below(...)");
            if (this.isStandable(level2, class_23382)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean hasRunway(@NotNull class_1937 level2, @NotNull class_2338 pos, @NotNull class_2350 dir) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        class_2338 class_23382 = pos.method_10093(dir.method_10153());
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"relative(...)");
        class_2338 behind = class_23382;
        if (!this.isPassable(level2, pos)) return false;
        class_2338 class_23383 = pos.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"above(...)");
        if (!this.isPassable(level2, class_23383)) return false;
        class_2338 class_23384 = pos.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"below(...)");
        if (!this.isStandable(level2, class_23384)) return false;
        if (!this.isPassable(level2, behind)) return false;
        class_2338 class_23385 = behind.method_10084();
        Intrinsics.checkNotNullExpressionValue((Object)class_23385, (String)"above(...)");
        if (!this.isPassable(level2, class_23385)) return false;
        class_2338 class_23386 = behind.method_10074();
        Intrinsics.checkNotNullExpressionValue((Object)class_23386, (String)"below(...)");
        if (!this.isStandable(level2, class_23386)) return false;
        return true;
    }

    public final boolean canMoveDiagonal(@NotNull class_1937 level2, @NotNull class_2338 pos, int dx, int dz) {
        block7: {
            block6: {
                class_2338 sideZ;
                block5: {
                    block4: {
                        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
                        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
                        class_2338 class_23382 = pos.method_10069(dx, 0, 0);
                        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"offset(...)");
                        class_2338 sideX = class_23382;
                        class_2338 class_23383 = pos.method_10069(0, 0, dz);
                        Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"offset(...)");
                        sideZ = class_23383;
                        if (!this.isPassable(level2, sideX)) break block4;
                        class_2338 class_23384 = sideX.method_10084();
                        Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"above(...)");
                        if (this.isPassable(level2, class_23384)) break block5;
                    }
                    return false;
                }
                if (!this.isPassable(level2, sideZ)) break block6;
                class_2338 class_23385 = sideZ.method_10084();
                Intrinsics.checkNotNullExpressionValue((Object)class_23385, (String)"above(...)");
                if (this.isPassable(level2, class_23385)) break block7;
            }
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean isChunkCached(@NotNull class_1937 level2, int chunkX, int chunkZ, long ttlTicks) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        String string = level2.method_27983().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String dimension = string;
        ChunkKey key = new ChunkKey(dimension, chunkX, chunkZ);
        long now = level2.method_75260();
        chunkCache.1 var10_8 = chunkCache;
        synchronized (var10_8) {
            block6: {
                boolean bl = false;
                Long l = (Long)chunkCache.get((Object)key);
                if (l == null) {
                    boolean bl2 = false;
                    return bl2;
                }
                long l2 = l;
                long last = l2;
                if (now - last <= ttlTicks) break block6;
                chunkCache.remove((Object)key);
                boolean bl3 = false;
                return bl3;
            }
            boolean bl = true;
            return bl;
        }
    }

    public static /* synthetic */ boolean isChunkCached$default(MinecraftPathingRules minecraftPathingRules, class_1937 class_19372, int n, int n2, long l, int n3, Object object) {
        if ((n3 & 8) != 0) {
            l = 200L;
        }
        return minecraftPathingRules.isChunkCached(class_19372, n, n2, l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final class_2680 getCachedState(class_1937 level2, class_2338 pos) {
        String string = level2.method_27983().toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String dimension = string;
        StateKey key = new StateKey(dimension, pos.method_10063());
        long now = level2.method_75260();
        stateCache.1 var7_6 = stateCache;
        synchronized (var7_6) {
            block7: {
                boolean bl = false;
                StateEntry cached = (StateEntry)stateCache.get((Object)key);
                if (cached == null || now - cached.getTick() > 4L) break block7;
                class_2680 class_26802 = cached.getState();
                return class_26802;
            }
            class_2680 class_26803 = level2.method_8320(pos);
            Intrinsics.checkNotNullExpressionValue((Object)class_26803, (String)"getBlockState(...)");
            class_2680 state = class_26803;
            ((Map)stateCache).put(key, new StateEntry(state, now));
            chunkCache.1 var11_12 = chunkCache;
            synchronized (var11_12) {
                boolean bl = false;
                ChunkKey chunkKey = new ChunkKey(dimension, pos.method_10263() >> 4, pos.method_10260() >> 4);
                ((Map)chunkCache).put(chunkKey, now);
                Unit unit = Unit.INSTANCE;
            }
            class_2680 class_26804 = state;
            return class_26804;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\fJ.\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0012\u001a\u00020\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0011\u0010\u0014\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\fJ\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\nR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u001a\u0010\f\u00a8\u0006\u001b"}, d2={"Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$ChunkKey;", "", "", "dimension", "", "chunkX", "chunkZ", "<init>", "(Ljava/lang/String;II)V", "component1", "()Ljava/lang/String;", "component2", "()I", "component3", "copy", "(Ljava/lang/String;II)Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$ChunkKey;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getDimension", "I", "getChunkX", "getChunkZ", "cobalt"})
    private static final class ChunkKey {
        @NotNull
        private final String dimension;
        private final int chunkX;
        private final int chunkZ;

        public ChunkKey(@NotNull String dimension, int chunkX, int chunkZ) {
            Intrinsics.checkNotNullParameter((Object)dimension, (String)"dimension");
            this.dimension = dimension;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @NotNull
        public final String getDimension() {
            return this.dimension;
        }

        public final int getChunkX() {
            return this.chunkX;
        }

        public final int getChunkZ() {
            return this.chunkZ;
        }

        @NotNull
        public final String component1() {
            return this.dimension;
        }

        public final int component2() {
            return this.chunkX;
        }

        public final int component3() {
            return this.chunkZ;
        }

        @NotNull
        public final ChunkKey copy(@NotNull String dimension, int chunkX, int chunkZ) {
            Intrinsics.checkNotNullParameter((Object)dimension, (String)"dimension");
            return new ChunkKey(dimension, chunkX, chunkZ);
        }

        public static /* synthetic */ ChunkKey copy$default(ChunkKey chunkKey, String string, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                string = chunkKey.dimension;
            }
            if ((n3 & 2) != 0) {
                n = chunkKey.chunkX;
            }
            if ((n3 & 4) != 0) {
                n2 = chunkKey.chunkZ;
            }
            return chunkKey.copy(string, n, n2);
        }

        @NotNull
        public String toString() {
            return "ChunkKey(dimension=" + this.dimension + ", chunkX=" + this.chunkX + ", chunkZ=" + this.chunkZ + ")";
        }

        public int hashCode() {
            int result = this.dimension.hashCode();
            result = result * 31 + Integer.hashCode(this.chunkX);
            result = result * 31 + Integer.hashCode(this.chunkZ);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ChunkKey)) {
                return false;
            }
            ChunkKey chunkKey = (ChunkKey)other;
            if (!Intrinsics.areEqual((Object)this.dimension, (Object)chunkKey.dimension)) {
                return false;
            }
            if (this.chunkX != chunkKey.chunkX) {
                return false;
            }
            return this.chunkZ == chunkKey.chunkZ;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001b\u0010\u000b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$StateEntry;", "", "Lnet/minecraft/class_2680;", "state", "", "tick", "<init>", "(Lnet/minecraft/class_2680;J)V", "component1", "()Lnet/minecraft/class_2680;", "component2", "()J", "copy", "(Lnet/minecraft/class_2680;J)Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$StateEntry;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "Lnet/minecraft/class_2680;", "getState", "J", "getTick", "cobalt"})
    private static final class StateEntry {
        @NotNull
        private final class_2680 state;
        private final long tick;

        public StateEntry(@NotNull class_2680 state, long tick) {
            Intrinsics.checkNotNullParameter((Object)state, (String)"state");
            this.state = state;
            this.tick = tick;
        }

        @NotNull
        public final class_2680 getState() {
            return this.state;
        }

        public final long getTick() {
            return this.tick;
        }

        @NotNull
        public final class_2680 component1() {
            return this.state;
        }

        public final long component2() {
            return this.tick;
        }

        @NotNull
        public final StateEntry copy(@NotNull class_2680 state, long tick) {
            Intrinsics.checkNotNullParameter((Object)state, (String)"state");
            return new StateEntry(state, tick);
        }

        public static /* synthetic */ StateEntry copy$default(StateEntry stateEntry, class_2680 class_26802, long l, int n, Object object) {
            if ((n & 1) != 0) {
                class_26802 = stateEntry.state;
            }
            if ((n & 2) != 0) {
                l = stateEntry.tick;
            }
            return stateEntry.copy(class_26802, l);
        }

        @NotNull
        public String toString() {
            return "StateEntry(state=" + this.state + ", tick=" + this.tick + ")";
        }

        public int hashCode() {
            int result = this.state.hashCode();
            result = result * 31 + Long.hashCode(this.tick);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof StateEntry)) {
                return false;
            }
            StateEntry stateEntry = (StateEntry)other;
            if (!Intrinsics.areEqual((Object)this.state, (Object)stateEntry.state)) {
                return false;
            }
            return this.tick == stateEntry.tick;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\tR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$StateKey;", "", "", "dimension", "", "pos", "<init>", "(Ljava/lang/String;J)V", "component1", "()Ljava/lang/String;", "component2", "()J", "copy", "(Ljava/lang/String;J)Lorg/cobalt/api/pathfinder/minecraft/MinecraftPathingRules$StateKey;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getDimension", "J", "getPos", "cobalt"})
    private static final class StateKey {
        @NotNull
        private final String dimension;
        private final long pos;

        public StateKey(@NotNull String dimension, long pos) {
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
        public final StateKey copy(@NotNull String dimension, long pos) {
            Intrinsics.checkNotNullParameter((Object)dimension, (String)"dimension");
            return new StateKey(dimension, pos);
        }

        public static /* synthetic */ StateKey copy$default(StateKey stateKey, String string, long l, int n, Object object) {
            if ((n & 1) != 0) {
                string = stateKey.dimension;
            }
            if ((n & 2) != 0) {
                l = stateKey.pos;
            }
            return stateKey.copy(string, l);
        }

        @NotNull
        public String toString() {
            return "StateKey(dimension=" + this.dimension + ", pos=" + this.pos + ")";
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
            if (!(other instanceof StateKey)) {
                return false;
            }
            StateKey stateKey = (StateKey)other;
            if (!Intrinsics.areEqual((Object)this.dimension, (Object)stateKey.dimension)) {
                return false;
            }
            return this.pos == stateKey.pos;
        }
    }
}

