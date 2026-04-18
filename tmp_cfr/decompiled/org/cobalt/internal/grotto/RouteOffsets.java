/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0010\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0014B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0007\u001a\u0004\b\b\u0010\tR\u001d\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\n\u0010\u0007\u001a\u0004\b\u000b\u0010\tR\u001d\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\f\u0010\u0007\u001a\u0004\b\r\u0010\tR\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010\u0007\u001a\u0004\b\u000f\u0010\tR\u001d\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0007\u001a\u0004\b\u0011\u0010\tR\u001d\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0012\u0010\u0007\u001a\u0004\b\u0013\u0010\t\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/internal/grotto/RouteOffsets;", "", "<init>", "()V", "", "Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "MANSION", "[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "getMANSION", "()[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "OPTIMISED_MANSION", "getOPTIMISED_MANSION", "PALACE", "getPALACE", "OVERGROWN", "getOVERGROWN", "SHRINE", "getSHRINE", "WATERFALL", "getWATERFALL", "Offset", "cobalt"})
public final class RouteOffsets {
    @NotNull
    public static final RouteOffsets INSTANCE = new RouteOffsets();
    @NotNull
    private static final Offset[] MANSION;
    @NotNull
    private static final Offset[] OPTIMISED_MANSION;
    @NotNull
    private static final Offset[] PALACE;
    @NotNull
    private static final Offset[] OVERGROWN;
    @NotNull
    private static final Offset[] SHRINE;
    @NotNull
    private static final Offset[] WATERFALL;

    private RouteOffsets() {
    }

    @NotNull
    public final Offset[] getMANSION() {
        return MANSION;
    }

    @NotNull
    public final Offset[] getOPTIMISED_MANSION() {
        return OPTIMISED_MANSION;
    }

    @NotNull
    public final Offset[] getPALACE() {
        return PALACE;
    }

    @NotNull
    public final Offset[] getOVERGROWN() {
        return OVERGROWN;
    }

    @NotNull
    public final Offset[] getSHRINE() {
        return SHRINE;
    }

    @NotNull
    public final Offset[] getWATERFALL() {
        return WATERFALL;
    }

    static {
        Offset[] offsetArray = new Offset[]{new Offset(0, -1, 0), new Offset(-22, 0, -8), new Offset(-21, 0, -36), new Offset(-18, -7, -42), new Offset(1, -6, -35), new Offset(8, -6, -37), new Offset(19, -3, -41), new Offset(-3, -22, -12), new Offset(-5, -20, -26), new Offset(-8, -17, -40), new Offset(20, -23, -39), new Offset(16, -22, -19), new Offset(22, -23, -11), new Offset(22, -16, -2), new Offset(-8, -18, -2), new Offset(-13, -7, 4), new Offset(6, -7, 1)};
        MANSION = offsetArray;
        offsetArray = new Offset[]{new Offset(0, -1, 0), new Offset(-22, 0, -8), new Offset(-25, -1, -36), new Offset(-17, -8, -47), new Offset(1, -6, -35), new Offset(8, -6, -37), new Offset(19, -3, -41), new Offset(-3, -22, -12), new Offset(-5, -19, -26), new Offset(-13, -17, -40), new Offset(20, -23, -39), new Offset(12, -22, -19), new Offset(22, -23, -11), new Offset(25, -16, -3), new Offset(-8, -18, -2), new Offset(-13, -7, 4), new Offset(9, -7, 4)};
        OPTIMISED_MANSION = offsetArray;
        offsetArray = new Offset[]{new Offset(0, -1, 0), new Offset(12, -1, 0), new Offset(12, 21, 26), new Offset(-7, 21, 16), new Offset(11, 21, 0), new Offset(-16, 17, -9), new Offset(-28, 15, 18), new Offset(-21, 9, 15), new Offset(-17, 3, 34), new Offset(8, 5, 32), new Offset(-17, 2, 6), new Offset(1, 0, -10)};
        PALACE = offsetArray;
        offsetArray = new Offset[]{new Offset(-19, -1, -35), new Offset(3, 5, -33), new Offset(12, 3, -24), new Offset(2, -11, -34), new Offset(16, -11, -21), new Offset(16, -13, 0), new Offset(5, 2, 16), new Offset(16, 1, 3), new Offset(-5, 1, -5), new Offset(-22, -2, -6), new Offset(-22, -7, 15), new Offset(-4, -11, -4), new Offset(-21, -9, -4), new Offset(-34, -7, -21)};
        OVERGROWN = offsetArray;
        offsetArray = new Offset[]{new Offset(0, -18, -13), new Offset(17, -16, 1), new Offset(34, -18, -14), new Offset(32, -20, 15), new Offset(33, 3, 16), new Offset(29, 5, -16), new Offset(0, 2, -9), new Offset(-4, 0, 1), new Offset(4, -11, 22)};
        SHRINE = offsetArray;
        offsetArray = new Offset[]{new Offset(-5, -22, -7), new Offset(13, -21, -17), new Offset(18, -26, -5), new Offset(2, -25, 15), new Offset(22, -25, 6), new Offset(15, -8, 14), new Offset(-5, -11, 12), new Offset(3, -12, -6), new Offset(6, -1, -18)};
        WATERFALL = offsetArray;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0012\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\tJ\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0016\u001a\u0004\b\u0018\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0019\u0010\t\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "", "", "x", "y", "z", "<init>", "(III)V", "component1", "()I", "component2", "component3", "copy", "(III)Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getX", "getY", "getZ", "cobalt"})
    public static final class Offset {
        private final int x;
        private final int y;
        private final int z;

        public Offset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public final int getX() {
            return this.x;
        }

        public final int getY() {
            return this.y;
        }

        public final int getZ() {
            return this.z;
        }

        public final int component1() {
            return this.x;
        }

        public final int component2() {
            return this.y;
        }

        public final int component3() {
            return this.z;
        }

        @NotNull
        public final Offset copy(int x, int y, int z) {
            return new Offset(x, y, z);
        }

        public static /* synthetic */ Offset copy$default(Offset offset, int n, int n2, int n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                n = offset.x;
            }
            if ((n4 & 2) != 0) {
                n2 = offset.y;
            }
            if ((n4 & 4) != 0) {
                n3 = offset.z;
            }
            return offset.copy(n, n2, n3);
        }

        @NotNull
        public String toString() {
            return "Offset(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.x);
            result = result * 31 + Integer.hashCode(this.y);
            result = result * 31 + Integer.hashCode(this.z);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Offset)) {
                return false;
            }
            Offset offset = (Offset)other;
            if (this.x != offset.x) {
                return false;
            }
            if (this.y != offset.y) {
                return false;
            }
            return this.z == offset.z;
        }
    }
}

