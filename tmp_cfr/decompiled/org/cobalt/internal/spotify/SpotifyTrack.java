/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.spotify;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u000e\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u0010\u0010\u0013\u001a\u00020\bH\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0011JL\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u001b\u0010\u0019\u001a\u00020\b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0011\u0010\u001e\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001e\u0010\u000eR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001f\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001f\u001a\u0004\b!\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\"\u001a\u0004\b#\u0010\u0011R\u0017\u0010\u0007\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\"\u001a\u0004\b$\u0010\u0011R\u0017\u0010\t\u001a\u00020\b8\u0006\u00a2\u0006\f\n\u0004\b\t\u0010%\u001a\u0004\b\t\u0010\u0014R\u0017\u0010\n\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\n\u0010\"\u001a\u0004\b&\u0010\u0011R\u0011\u0010(\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b'\u0010\u0011\u00a8\u0006)"}, d2={"Lorg/cobalt/internal/spotify/SpotifyTrack;", "", "", "name", "artist", "", "progressMs", "durationMs", "", "isPlaying", "snapshotMs", "<init>", "(Ljava/lang/String;Ljava/lang/String;JJZJ)V", "component1", "()Ljava/lang/String;", "component2", "component3", "()J", "component4", "component5", "()Z", "component6", "copy", "(Ljava/lang/String;Ljava/lang/String;JJZJ)Lorg/cobalt/internal/spotify/SpotifyTrack;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getName", "getArtist", "J", "getProgressMs", "getDurationMs", "Z", "getSnapshotMs", "getCurrentProgressMs", "currentProgressMs", "cobalt"})
public final class SpotifyTrack {
    @NotNull
    private final String name;
    @NotNull
    private final String artist;
    private final long progressMs;
    private final long durationMs;
    private final boolean isPlaying;
    private final long snapshotMs;

    public SpotifyTrack(@NotNull String name, @NotNull String artist, long progressMs, long durationMs, boolean isPlaying, long snapshotMs) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)artist, (String)"artist");
        this.name = name;
        this.artist = artist;
        this.progressMs = progressMs;
        this.durationMs = durationMs;
        this.isPlaying = isPlaying;
        this.snapshotMs = snapshotMs;
    }

    public /* synthetic */ SpotifyTrack(String string, String string2, long l, long l2, boolean bl, long l3, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 0x20) != 0) {
            l3 = System.currentTimeMillis();
        }
        this(string, string2, l, l2, bl, l3);
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getArtist() {
        return this.artist;
    }

    public final long getProgressMs() {
        return this.progressMs;
    }

    public final long getDurationMs() {
        return this.durationMs;
    }

    public final boolean isPlaying() {
        return this.isPlaying;
    }

    public final long getSnapshotMs() {
        return this.snapshotMs;
    }

    public final long getCurrentProgressMs() {
        return this.isPlaying ? RangesKt.coerceAtMost((long)(this.progressMs + (System.currentTimeMillis() - this.snapshotMs)), (long)this.durationMs) : this.progressMs;
    }

    @NotNull
    public final String component1() {
        return this.name;
    }

    @NotNull
    public final String component2() {
        return this.artist;
    }

    public final long component3() {
        return this.progressMs;
    }

    public final long component4() {
        return this.durationMs;
    }

    public final boolean component5() {
        return this.isPlaying;
    }

    public final long component6() {
        return this.snapshotMs;
    }

    @NotNull
    public final SpotifyTrack copy(@NotNull String name, @NotNull String artist, long progressMs, long durationMs, boolean isPlaying, long snapshotMs) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)artist, (String)"artist");
        return new SpotifyTrack(name, artist, progressMs, durationMs, isPlaying, snapshotMs);
    }

    public static /* synthetic */ SpotifyTrack copy$default(SpotifyTrack spotifyTrack, String string, String string2, long l, long l2, boolean bl, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            string = spotifyTrack.name;
        }
        if ((n & 2) != 0) {
            string2 = spotifyTrack.artist;
        }
        if ((n & 4) != 0) {
            l = spotifyTrack.progressMs;
        }
        if ((n & 8) != 0) {
            l2 = spotifyTrack.durationMs;
        }
        if ((n & 0x10) != 0) {
            bl = spotifyTrack.isPlaying;
        }
        if ((n & 0x20) != 0) {
            l3 = spotifyTrack.snapshotMs;
        }
        return spotifyTrack.copy(string, string2, l, l2, bl, l3);
    }

    @NotNull
    public String toString() {
        return "SpotifyTrack(name=" + this.name + ", artist=" + this.artist + ", progressMs=" + this.progressMs + ", durationMs=" + this.durationMs + ", isPlaying=" + this.isPlaying + ", snapshotMs=" + this.snapshotMs + ")";
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = result * 31 + this.artist.hashCode();
        result = result * 31 + Long.hashCode(this.progressMs);
        result = result * 31 + Long.hashCode(this.durationMs);
        result = result * 31 + Boolean.hashCode(this.isPlaying);
        result = result * 31 + Long.hashCode(this.snapshotMs);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpotifyTrack)) {
            return false;
        }
        SpotifyTrack spotifyTrack = (SpotifyTrack)other;
        if (!Intrinsics.areEqual((Object)this.name, (Object)spotifyTrack.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.artist, (Object)spotifyTrack.artist)) {
            return false;
        }
        if (this.progressMs != spotifyTrack.progressMs) {
            return false;
        }
        if (this.durationMs != spotifyTrack.durationMs) {
            return false;
        }
        if (this.isPlaying != spotifyTrack.isPlaying) {
            return false;
        }
        return this.snapshotMs == spotifyTrack.snapshotMs;
    }
}

