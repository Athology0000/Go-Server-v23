/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.pathfinder.jni;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\f\u001a\u00020\u000b2\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004\u00a2\u0006\u0004\b\f\u0010\rJ\u0011\u0010\u000e\u001a\u00020\u0004H\u0096\u0080\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u000fJ\u0010\u0010\u0013\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u000fJ\u0010\u0010\u0014\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u000fJ8\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0011\u0010\u0018\u001a\u00020\u0017H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\u0011R\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001c\u001a\u0004\b\u001d\u0010\u000fR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001c\u001a\u0004\b\u001e\u0010\u000fR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001c\u001a\u0004\b\u001f\u0010\u000f\u00a8\u0006 "}, d2={"Lorg/cobalt/api/pathfinder/jni/WorldBufferResult;", "", "", "buf", "", "bx", "by", "bz", "<init>", "([BIII)V", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "()I", "component1", "()[B", "component2", "component3", "component4", "copy", "([BIII)Lorg/cobalt/api/pathfinder/jni/WorldBufferResult;", "", "toString", "()Ljava/lang/String;", "[B", "getBuf", "I", "getBx", "getBy", "getBz", "cobalt"})
public final class WorldBufferResult {
    @NotNull
    private final byte[] buf;
    private final int bx;
    private final int by;
    private final int bz;

    public WorldBufferResult(@NotNull byte[] buf, int bx, int by, int bz) {
        Intrinsics.checkNotNullParameter((Object)buf, (String)"buf");
        this.buf = buf;
        this.bx = bx;
        this.by = by;
        this.bz = bz;
    }

    @NotNull
    public final byte[] getBuf() {
        return this.buf;
    }

    public final int getBx() {
        return this.bx;
    }

    public final int getBy() {
        return this.by;
    }

    public final int getBz() {
        return this.bz;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WorldBufferResult)) {
            return false;
        }
        return this.bx == ((WorldBufferResult)other).bx && this.by == ((WorldBufferResult)other).by && this.bz == ((WorldBufferResult)other).bz && Arrays.equals(this.buf, ((WorldBufferResult)other).buf);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.buf);
        result = 31 * result + this.bx;
        result = 31 * result + this.by;
        result = 31 * result + this.bz;
        return result;
    }

    @NotNull
    public final byte[] component1() {
        return this.buf;
    }

    public final int component2() {
        return this.bx;
    }

    public final int component3() {
        return this.by;
    }

    public final int component4() {
        return this.bz;
    }

    @NotNull
    public final WorldBufferResult copy(@NotNull byte[] buf, int bx, int by, int bz) {
        Intrinsics.checkNotNullParameter((Object)buf, (String)"buf");
        return new WorldBufferResult(buf, bx, by, bz);
    }

    public static /* synthetic */ WorldBufferResult copy$default(WorldBufferResult worldBufferResult, byte[] byArray, int n, int n2, int n3, int n4, Object object) {
        if ((n4 & 1) != 0) {
            byArray = worldBufferResult.buf;
        }
        if ((n4 & 2) != 0) {
            n = worldBufferResult.bx;
        }
        if ((n4 & 4) != 0) {
            n2 = worldBufferResult.by;
        }
        if ((n4 & 8) != 0) {
            n3 = worldBufferResult.bz;
        }
        return worldBufferResult.copy(byArray, n, n2, n3);
    }

    @NotNull
    public String toString() {
        return "WorldBufferResult(buf=" + Arrays.toString(this.buf) + ", bx=" + this.bx + ", by=" + this.by + ", bz=" + this.bz + ")";
    }
}

