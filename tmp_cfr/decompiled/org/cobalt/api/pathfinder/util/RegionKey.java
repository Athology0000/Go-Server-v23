/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.util;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.pathfinder.wrapper.PathPosition;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ%\u0010\u0007\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\t\u00a2\u0006\u0004\b\u0007\u0010\rR\u0014\u0010\u000e\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u000fR\u0014\u0010\u0011\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0013\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0012\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/api/pathfinder/util/RegionKey;", "", "<init>", "()V", "Lorg/cobalt/api/pathfinder/wrapper/PathPosition;", "pos", "", "pack", "(Lorg/cobalt/api/pathfinder/wrapper/PathPosition;)J", "", "x", "y", "z", "(III)J", "MASK_Y", "J", "MASK_XZ", "SHIFT_Z", "I", "SHIFT_X", "cobalt"})
public final class RegionKey {
    @NotNull
    public static final RegionKey INSTANCE = new RegionKey();
    private static final long MASK_Y = 4095L;
    private static final long MASK_XZ = 0x3FFFFFFL;
    private static final int SHIFT_Z = 12;
    private static final int SHIFT_X = 38;

    private RegionKey() {
    }

    public final long pack(@NotNull PathPosition pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        return this.pack(pos.getFlooredX(), pos.getFlooredY(), pos.getFlooredZ());
    }

    public final long pack(int x, int y, int z) {
        return ((long)x & 0x3FFFFFFL) << 38 | ((long)z & 0x3FFFFFFL) << 12 | (long)y & 0xFFFL;
    }
}

