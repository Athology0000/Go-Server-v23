/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1799
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.wardrobe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1799;
import net.minecraft.class_638;
import org.cobalt.internal.wardrobe.WardrobeFakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0014B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J-\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\u000e\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u000f\u0010\u0003R \u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00110\u00108\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeFakePlayerCache;", "", "<init>", "()V", "", "setId", "", "Lnet/minecraft/class_1799;", "armor", "Lnet/minecraft/class_638;", "level", "Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "get", "(ILjava/util/List;Lnet/minecraft/class_638;)Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "", "clear", "", "Lorg/cobalt/internal/wardrobe/WardrobeFakePlayerCache$Entry;", "cache", "Ljava/util/Map;", "Entry", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeFakePlayer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeFakePlayer.kt\norg/cobalt/internal/wardrobe/WardrobeFakePlayerCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"})
public final class WardrobeFakePlayerCache {
    @NotNull
    public static final WardrobeFakePlayerCache INSTANCE = new WardrobeFakePlayerCache();
    @NotNull
    private static final Map<Integer, Entry> cache = new LinkedHashMap();

    private WardrobeFakePlayerCache() {
    }

    @NotNull
    public final WardrobeFakePlayer get(int setId, @NotNull List<class_1799> armor, @NotNull class_638 level2) {
        WardrobeFakePlayer wardrobeFakePlayer;
        Intrinsics.checkNotNullParameter(armor, (String)"armor");
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        String fingerprint = CollectionsKt.joinToString$default((Iterable)armor, (CharSequence)"|", null, null, (int)0, null, WardrobeFakePlayerCache::get$lambda$0, (int)30, null);
        Entry entry = cache.get(setId);
        if (entry != null && Intrinsics.areEqual((Object)entry.getFingerprint(), (Object)fingerprint)) {
            return entry.getPlayer();
        }
        WardrobeFakePlayer it = wardrobeFakePlayer = new WardrobeFakePlayer(level2);
        boolean bl = false;
        it.applyArmor(armor);
        WardrobeFakePlayer player = wardrobeFakePlayer;
        cache.put(setId, new Entry(player, fingerprint));
        return player;
    }

    public final void clear() {
        cache.clear();
    }

    private static final CharSequence get$lambda$0(class_1799 it) {
        Object object = it;
        return object != null && (object = object.method_7909()) != null && (object = object.toString()) != null ? (CharSequence)object : (CharSequence)"null";
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u000bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeFakePlayerCache$Entry;", "", "Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "player", "", "fingerprint", "<init>", "(Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;Ljava/lang/String;)V", "component1", "()Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "component2", "()Ljava/lang/String;", "copy", "(Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;Ljava/lang/String;)Lorg/cobalt/internal/wardrobe/WardrobeFakePlayerCache$Entry;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "getPlayer", "Ljava/lang/String;", "getFingerprint", "cobalt"})
    private static final class Entry {
        @NotNull
        private final WardrobeFakePlayer player;
        @NotNull
        private final String fingerprint;

        public Entry(@NotNull WardrobeFakePlayer player, @NotNull String fingerprint) {
            Intrinsics.checkNotNullParameter((Object)((Object)player), (String)"player");
            Intrinsics.checkNotNullParameter((Object)fingerprint, (String)"fingerprint");
            this.player = player;
            this.fingerprint = fingerprint;
        }

        @NotNull
        public final WardrobeFakePlayer getPlayer() {
            return this.player;
        }

        @NotNull
        public final String getFingerprint() {
            return this.fingerprint;
        }

        @NotNull
        public final WardrobeFakePlayer component1() {
            return this.player;
        }

        @NotNull
        public final String component2() {
            return this.fingerprint;
        }

        @NotNull
        public final Entry copy(@NotNull WardrobeFakePlayer player, @NotNull String fingerprint) {
            Intrinsics.checkNotNullParameter((Object)((Object)player), (String)"player");
            Intrinsics.checkNotNullParameter((Object)fingerprint, (String)"fingerprint");
            return new Entry(player, fingerprint);
        }

        public static /* synthetic */ Entry copy$default(Entry entry, WardrobeFakePlayer wardrobeFakePlayer, String string, int n, Object object) {
            if ((n & 1) != 0) {
                wardrobeFakePlayer = entry.player;
            }
            if ((n & 2) != 0) {
                string = entry.fingerprint;
            }
            return entry.copy(wardrobeFakePlayer, string);
        }

        @NotNull
        public String toString() {
            return "Entry(player=" + this.player + ", fingerprint=" + this.fingerprint + ")";
        }

        public int hashCode() {
            int result = this.player.hashCode();
            result = result * 31 + this.fingerprint.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry)other;
            if (!Intrinsics.areEqual((Object)((Object)this.player), (Object)((Object)entry.player))) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.fingerprint, (Object)entry.fingerprint);
        }
    }
}

